package androidcustom.radiostation.loader;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;

//==============================================================================
public class LoaderVideo {

	private Context		m_context;
	private String		m_strUrl;			// YouTube VideoUrl

	private SharedPreferences	m_preferences		= null;
	private CharSequence		m_chrSeqQuality		= null;

	private	LoaderVideoListener	m_loaderListener	= null;

	//{{============================================================================
	public interface LoaderVideoListener {
		public void OnLoadResult(String a_strUrl);
	}
	//}}============================================================================

	//------------------------------------------------------------------------------
	public LoaderVideo(Context a_context) {
		
		m_context = a_context;
		m_strUrl = null;
		m_loaderListener = null;

	// {{ Quality
		m_preferences = PreferenceManager.getDefaultSharedPreferences(m_context);
		ConnectivityManager		connectManager = (ConnectivityManager)m_context.getSystemService(Context.CONNECTIVITY_SERVICE);

		NetworkInfo[]	arrNetInfo = connectManager.getAllNetworkInfo();
		for (NetworkInfo netInfo : arrNetInfo) {
			if (netInfo.getTypeName().equalsIgnoreCase("WIFI")){
//				m_chrSeqQuality = m_preferences.getString("quality", "HighQuality");
				m_chrSeqQuality = m_preferences.getString("quality", "MediumQuality"); // Modify real device, Virtual device
			}
			if (netInfo.getTypeName().equalsIgnoreCase("MOBILE")) {
				m_chrSeqQuality = m_preferences.getString("quality", "MediumQuality");
			}
		}
	// }} Quality
	}

	//------------------------------------------------------------------------------
	public void SetLoaderListener(LoaderVideoListener a_loaderListener) {
		m_loaderListener = a_loaderListener;
	}

	//------------------------------------------------------------------------------
	public void SetVideoUrl(String a_strUrl) {
		m_strUrl = a_strUrl;
	}

	//------------------------------------------------------------------------------
	public void StartLoad() {
		m_strUrl = m_strUrl.replaceAll("\r?\n", " ");
		String	strId = GetYouTubeID(m_strUrl);

		if (strId.length() == 0)
			return;
		GetYouTubeVideoLink(strId);
	}

	//------------------------------------------------------------------------------
	private String GetYouTubeID(String a_strUrl) {
		String		strPattern	= ".*youtu.*[?|&]v=([^&?]*).*";
		Boolean		bFail		= Boolean.FALSE;
		String		strId		= a_strUrl.replaceAll(strPattern, "$1");
		
		a_strUrl = a_strUrl.replace("/embed/","/?v=");

		if (strId.equals(a_strUrl))
			bFail = Boolean.TRUE;

		if (bFail) {
			bFail = Boolean.FALSE;
			strPattern = ".*plus.google.*&ytl=([^&]*).*";
			strId = a_strUrl.replaceAll(strPattern, "$1");
			if (strId.equals(a_strUrl))
				bFail = Boolean.TRUE;
		}

		if (bFail)
			return "";

		return strId;
	}

	//------------------------------------------------------------------------------
	private void GetYouTubeVideoLink(String id) {
		try { id = URLEncoder.encode(id, "UTF-8"); }
		catch (UnsupportedEncodingException e) { return; }
		YT_TryGetMeta(id, -1);
	}

	//------------------------------------------------------------------------------
	private void YT_TryGetMeta(String a_strId, Integer a_iNum) {
		a_iNum += 1;
		String[] type_page = {"spec", "&el=detailpage", "&el=vevo", "&el=embedded", ""};

		if (a_iNum >= type_page.length)
			return;

		if (a_iNum == 0) {
			String strUrl = "";
			try {
				strUrl = "http://www.youtube.com/get_video_info?video_id=" + a_strId + "&el=embedded&gl=US&hl=en&eurl=" + URLEncoder.encode("https://youtube.googleapis.com/v/" + a_strId, "UTF-8") + "&asv=3&sts=1588";
			}
			catch (java.io.UnsupportedEncodingException e) {
				YT_TryGetMeta(a_strId, a_iNum);
				return;
			}
			YT_GetMeta(strUrl, a_strId, a_iNum);
		}
		else {
			String strUrl = "http://www.youtube.com/get_video_info?&video_id=" + a_strId + type_page[a_iNum] + "&ps=default&eurl=&gl=US&hl=en";
			YT_GetMeta(strUrl, a_strId, a_iNum);
		}
	}

	//------------------------------------------------------------------------------
	private void YT_GetMeta(final String a_strUrl, final String a_strId, final Integer a_iNum) {
		Thread thread = new Thread(
			new Runnable() {
				@Override
				public void run() {
					HttpClient		httpClient		= new DefaultHttpClient();
					HttpGet			httpGet			= new HttpGet(a_strUrl);
					HttpResponse	httpResponse	= null;

					try { httpResponse = httpClient.execute(httpGet); }
					catch (IOException e) {
						YT_TryGetMeta(a_strId, a_iNum);
						return;
					}

					HttpEntity	httpEntity = httpResponse.getEntity();
					String		strLine = "";
					try { strLine = EntityUtils.toString(httpEntity, "UTF-8"); }
					catch (IOException e) {
						YT_TryGetMeta(a_strId, a_iNum);
						return;
					}

					if (!YT_ReadCode(strLine))
						YT_TryGetMeta(a_strId, a_iNum);
				}
			}
		);

		thread.start();
	}

	//------------------------------------------------------------------------------
	private Boolean YT_ReadCode(String a_strCode) {
		String[]	arrStrCode = a_strCode.replaceAll("[?&]?([^&]*)", "&$1").split("&");
		JSONObject	jsonObj = new JSONObject();

		try {
			jsonObj.put("content", YT_ReadInfo(arrStrCode));
			jsonObj = jsonObj.getJSONObject("content");
		} catch (JSONException e) {}

		if (!jsonObj.has("token"))
			return Boolean.FALSE;
		if (jsonObj.has("ypc_video_rental_bar_text") && !jsonObj.has("author"))
			return Boolean.FALSE;

		try {
			String	strVideos = "";
			if (jsonObj.has("url_encoded_fmt_stream_map"))
				strVideos += jsonObj.getString("url_encoded_fmt_stream_map").trim();

			if (jsonObj.has("adaptive_fmts")) {
				if (strVideos.length() != 0)
					strVideos += ",";
				strVideos += jsonObj.getString("adaptive_fmts").trim();
			}
			String[]	arrStrVideo		= strVideos.split(",");
			JSONObject	jsonObjVideo	= new JSONObject();

			jsonObjVideo.put("itag",	new JSONArray());
			jsonObjVideo.put("url",		new JSONArray());

			for (String strItem : arrStrVideo) {
				String[]	arrStrNew	= strItem.replaceAll("[?&]?([^&]*)", "&$1").split("&");
				JSONObject	jsonObjNew	= new JSONObject();
				jsonObjNew.put("content", YT_ReadInfo(arrStrNew));
				jsonObjNew = jsonObjNew.getJSONObject("content");

				if (!jsonObjNew.has("itag") || !jsonObjNew.has("url"))	continue;
				if (jsonObjNew.has("s"))								continue;

				String strUrl = jsonObjNew.getString("url").trim();

				if (jsonObjNew.has("sig"))
					strUrl += "&signature=" + jsonObjNew.getString("sig").trim();

				if (!strUrl.contains("signature="))
					continue;

				jsonObjNew.put("url", strUrl);

				JSONArray	jsonArrNew = jsonObjVideo.getJSONArray("itag");
				jsonArrNew.put(jsonObjNew.getString("itag"));
				jsonObjVideo.put("itag", jsonArrNew);

				JSONArray	jsonArrUrl = jsonObjVideo.getJSONArray("url");
				jsonArrUrl.put(jsonObjNew.getString("url"));
				jsonObjVideo.put("url", jsonArrUrl);
			}
			jsonObj.put("url_encoded_fmt_stream_map", jsonObjVideo);
		} catch (JSONException e) {}

		JSONArray	jsonArrLinkList = new JSONArray();

		try {
			if (jsonObj.has("url_encoded_fmt_stream_map")) {
				JSONObject	jsonObjItem = jsonObj.getJSONObject("url_encoded_fmt_stream_map");
				JSONArray	jsonArrUrlList = new JSONArray();
				JSONArray	jsonArrItag = new JSONArray();

				if (jsonObjItem.getString("url").trim().substring(0, 1).equals("[") == Boolean.FALSE)
					jsonArrUrlList.put(jsonObjItem.getString("url").trim());
				else
					jsonArrUrlList = jsonObjItem.getJSONArray("url");

				if (jsonObjItem.getString("itag").trim().substring(0, 1).equals("[") == Boolean.FALSE)
					jsonArrItag.put(jsonObjItem.getString("itag").trim());
				else
					jsonArrItag = jsonObjItem.getJSONArray("itag");

				for (Integer iIndex = 0; iIndex < jsonArrUrlList.length(); iIndex++) {
					try {
						JSONObject	jsonObjVideo = new JSONObject();
						String		strUrl = jsonArrUrlList.getString(iIndex).trim();

						if (!strUrl.contains("ratebypass"))
							strUrl += "&ratebypass=yes";

						jsonObjVideo.put("url", strUrl);
						jsonObjVideo.put("itag", jsonArrItag.getString(iIndex));
						jsonArrLinkList.put(jsonObjVideo);
					} catch (JSONException e) {}
				}
			}
		} catch (JSONException e) {}

		Boolean bIsLower = Boolean.FALSE;

		try {
			for (Integer iIndex = 0; iIndex < jsonArrLinkList.length(); iIndex++) {
				JSONObject jsonObjItem = jsonArrLinkList.getJSONObject(iIndex);
			}
		} catch (JSONException e) {}

		if (m_chrSeqQuality.equals("HighQuality")) {
			String[] itags = {"22", "136", "102", "45"};
			try {
				for (Integer iIndex = 0; iIndex < jsonArrLinkList.length(); iIndex++) {
					JSONObject jsonObjItem = jsonArrLinkList.getJSONObject(iIndex);
					for (String sub_item : itags) {
						if (sub_item.equals(jsonObjItem.getString("itag"))) {
							OnLoadUrl(jsonObjItem.getString("url"));
							return Boolean.TRUE;
						}
					}
				}
			} catch (JSONException e) {}
			bIsLower = Boolean.TRUE;
		}

		if (m_chrSeqQuality.equals("MediumQuality") || bIsLower) {
			String[]	arrStrItag = {"43", "18", "36", "17", "134", "135"};
			try {
				for (Integer iIndex = 0; iIndex < jsonArrLinkList.length(); iIndex++) {
					JSONObject	jsonObjItem = jsonArrLinkList.getJSONObject(iIndex);
					for (String strSubItem : arrStrItag) {
						if (strSubItem.equals(jsonObjItem.getString("itag"))) {
							OnLoadUrl(jsonObjItem.getString("url"));
							return Boolean.TRUE;
						}
					}
				}
			} catch (JSONException e) {}
		}
		if (m_chrSeqQuality.equals("Audio")) {
			String[] arrStrItag = {"141", "140", "139", "171", "172"};
			try {
				for (Integer iIndex = 0; iIndex < jsonArrLinkList.length(); iIndex++) {
					JSONObject jsonObjItem = jsonArrLinkList.getJSONObject(iIndex);
					for (String strSubItem : arrStrItag) {
						if (strSubItem.equals(jsonObjItem.getString("itag"))) {
							OnLoadUrl(jsonObjItem.getString("url"));
							return Boolean.TRUE;
						}
					}
				}
			} catch (JSONException e) {}
		}

		return Boolean.FALSE;
	}

	//------------------------------------------------------------------------------
	private Object YT_ReadInfo(String[] a_arrStrInfo) {
		JSONObject		jsonObj		= new JSONObject();
		List<String>	lstStrKeys	= new ArrayList<String>();
		List<String>	lstStrKeys2	= new ArrayList<String>();

		for (String strItem : a_arrStrInfo) {
			if (strItem.length() == 0)
				continue;
			Integer		iPos		= strItem.indexOf("=");
			String		strKey		= null;
			String		strValue	= null;
			Boolean		bIsObj		= Boolean.TRUE;

			if (iPos == -1 && a_arrStrInfo.length == 1)
				return strItem;

			if (iPos >= 0)
				strKey = strItem.substring(0, iPos);

			if (lstStrKeys.indexOf(strKey) == -1)
				lstStrKeys.add(strKey);
			else {
				if (lstStrKeys2.indexOf(strKey) == -1) {
					try {
						JSONArray	jsonArrVal = new JSONArray();
						jsonArrVal.put(jsonObj.getString(strKey));
						jsonObj.put(strKey, jsonArrVal);
						lstStrKeys2.add(strKey);
					} catch (JSONException e) {}
				}
				bIsObj = Boolean.FALSE;
			}

			strValue = strItem.substring(iPos + 1);
/*
			if (strValue.length() > 0) {
				try {
					strValue = URLDecoder.decode(strValue, "UTF-8");
				} catch (UnsupportedEncodingException e) {}
			}
*/
			try {
				if (bIsObj)
					jsonObj.put(strKey, strValue);
				else {
					JSONArray	jsonArrVal = jsonObj.getJSONArray(strKey);
					jsonArrVal.put(strValue);
					jsonObj.put(strKey, jsonArrVal);
				}
			} catch (JSONException e) {}
		}
		return jsonObj;
	}

	//------------------------------------------------------------------------------
	private void OnLoadUrl(String a_strUrl) {
		m_loaderListener.OnLoadResult(a_strUrl);
	}
}

//==============================================================================
