package androidcustom.radiostation.youtube;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.xml.parsers.FactoryConfigurationError;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.Uri;
import android.preference.PreferenceManager;

//==============================================================================
public class UtilYouTube {

	public	static final String URL_YOUTUBE_VIDEO_INFORMATION	= "http://www.youtube.com/get_video_info?&video_id=";
	public	static final String URL_YOUTUBE_PLAYLIST_ATOM_FEED	= "http://gdata.youtube.com/feeds/api/playlists/";

	//------------------------------------------------------------------------------
	/**
	 * Retrieve the latest video in the specified playlist.
	 *
	 * @param a_playlistId the id of the playlist for which to retrieve the latest video id
	 * @return the video id of the latest video, null if something goes wrong
	 * @throws IOException
	 * @throws ClientProtocolException
	 * @throws FactoryConfigurationError
	 */
	public static String QueryLatestPlaylistVideo(PlaylistId a_playlistId)
			throws IOException, ClientProtocolException, FactoryConfigurationError {

		String			strVideoId = null;
		HttpClient		httpClient = new DefaultHttpClient();
		HttpGet			httpGet = new HttpGet(URL_YOUTUBE_PLAYLIST_ATOM_FEED + a_playlistId.GetId() + "?v=2&max-results=50&alt=json");
		HttpResponse	httpResponse = httpClient.execute(httpGet);
		ByteArrayOutputStream	outputStream = new ByteArrayOutputStream();
		String		strInfo = null;
		JSONObject	jsonObjYouTubeResponse = null;

		try {
			httpResponse.getEntity().writeTo(outputStream);
			strInfo = outputStream.toString("UTF-8");
			jsonObjYouTubeResponse = new JSONObject(strInfo);

			JSONArray lEntryArr = jsonObjYouTubeResponse.getJSONObject("feed").getJSONArray("entry");
			JSONArray lLinkArr = lEntryArr.getJSONObject(lEntryArr.length() - 1).getJSONArray("link");
			for (int i = 0; i < lLinkArr.length(); i++) {
				JSONObject	jsonObjLink = lLinkArr.getJSONObject(i);
				String		strRelVal = jsonObjLink.optString("rel", null);
				if (strRelVal != null && strRelVal.equals("alternate")) {
					String	strUri = jsonObjLink.optString("href", null);
					Uri		uriVideo = Uri.parse(strUri);
					strVideoId = uriVideo.getQueryParameter("v");
					break;
				}
			}
		}
		catch (IllegalStateException e) {}
		catch (IOException e1) {}
		catch (JSONException e2) {}

		return strVideoId;
	}

	//------------------------------------------------------------------------------
	/**
	 * Calculate the YouTube URL to load the video.  Includes retrieving a token that YouTube
	 * requires to play the video.
	 *
	 * @param a_strYouTubeFmtQuality quality of the video.  17=low, 18=high
	 * @param bFallback		  whether to fallback to lower quality in case the supplied quality is not available
	 * @param a_strYouTubeVideoId	the id of the video
	 * @return the url string that will retrieve the video
	 * @throws IOException
	 * @throws ClientProtocolException
	 * @throws UnsupportedEncodingException
	 */
	public static String CalculateYouTubeUrl(String a_strYouTubeFmtQuality, boolean a_bFallback, String a_strYouTubeVideoId)
			throws IOException, ClientProtocolException, UnsupportedEncodingException {
		String			strUri = null;
		HttpClient		httpClient = new DefaultHttpClient();
		HttpGet			httpGet = new HttpGet(URL_YOUTUBE_VIDEO_INFORMATION + a_strYouTubeVideoId);
		HttpResponse	httpResponse = httpClient.execute(httpGet);
		ByteArrayOutputStream	outputStream = new ByteArrayOutputStream();
		String			strInfo = null;

		httpResponse.getEntity().writeTo(outputStream);
		strInfo = new String(outputStream.toString("UTF-8"));

		String[] arrStrArg = strInfo.split("&");
		Map<String, String> mapArg = new HashMap<String, String>();
		for (int i = 0; i < arrStrArg.length; i++) {
			String[] arrStrVal = arrStrArg[i].split("=");
			if (arrStrVal != null) {
				if (arrStrVal.length >= 2)
					mapArg.put(arrStrVal[0], URLDecoder.decode(arrStrVal[1]));
			}
		}

		//Find out the URI string from the parameters
		//Populate the list of formats for the video
		String	strFmtList = URLDecoder.decode(mapArg.get("fmt_list"));
		ArrayList<Format>	arrFormat = new ArrayList<Format>();
		if (null != strFmtList) {
			String arrStrFormat[] = strFmtList.split(",");

			for (String strFormat : arrStrFormat) {
				Format format = new Format(strFormat);
				arrFormat.add(format);
			}
		}

		//Populate the list of streams for the video
		String strStreamList = mapArg.get("url_encoded_fmt_stream_map");
		if (null != strStreamList) {
			String arrStrStream[] = strStreamList.split(",");
			ArrayList<VideoStream> arrVideoStream = new ArrayList<VideoStream>();
			for (String strStream : arrStrStream) {
				VideoStream videoStream = new VideoStream(strStream);
				arrVideoStream.add(videoStream);
			}

			//Search for the given format in the list of video formats
			// if it is there, select the corresponding stream
			// otherwise if fallback is requested, check for next lower format
			int iFormatId = Integer.parseInt(a_strYouTubeFmtQuality);

			Format formatSearch = new Format(iFormatId);
			while (!arrFormat.contains(formatSearch) && a_bFallback) {
				int iOldId = formatSearch.GetId();
				int iNewId = GetSupportedFallbackId(iOldId);

				if (iOldId == iNewId)
					break;
				formatSearch = new Format(iNewId);
			}

			int iIndex = arrFormat.indexOf(formatSearch);
			if (iIndex >= 0) {
				VideoStream videoStream = arrVideoStream.get(iIndex);
				strUri = videoStream.GetUrl();
			}
		}
		//Return the URI string. It may be null if the format (or a fallback format if enabled)
		// is not found in the list of formats for the video
		return strUri;
	}

	//------------------------------------------------------------------------------
	public static boolean HasVideoBeenViewed(Context a_context, String a_strVideoId) {
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(a_context);

		String strViewedVideoIds = preferences.getString("com.keyes.screebl.lastViewedVideoIds", null);

		if (strViewedVideoIds == null) {
			return false;
		}

		String[] arrStrSplitIds = strViewedVideoIds.split(";");
		if (arrStrSplitIds == null || arrStrSplitIds.length == 0)
			return false;

		for (int i = 0; i < arrStrSplitIds.length; i++) {
			if (arrStrSplitIds[i] != null && arrStrSplitIds[i].equals(a_strVideoId)) {
				return true;
			}
		}

		return false;

	}

	//------------------------------------------------------------------------------
	public static void MarkVideoAsViewed(Context a_context, String a_strVideoId) {

		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(a_context);
		if (a_strVideoId == null)
			return;

		String strViewedVideoIds = preferences.getString("com.keyes.screebl.lastViewedVideoIds", null);

		if (strViewedVideoIds == null)
			strViewedVideoIds = "";

		String[] arrStrSplitIds = strViewedVideoIds.split(";");
		if (arrStrSplitIds == null)
			arrStrSplitIds = new String[]{};

		// make a hash table of the ids to deal with duplicates
		Map<String, String> mapId = new HashMap<String, String>();
		for (int i = 0; i < arrStrSplitIds.length; i++)
			mapId.put(arrStrSplitIds[i], arrStrSplitIds[i]);

		// recreate the viewed list
		String				strNewIdList = "";
		Set<String>			setStrKeys = mapId.keySet();
		Iterator<String>	iteratorStr = setStrKeys.iterator();
		while (iteratorStr.hasNext()) {
			String strId = iteratorStr.next();
			if (!strId.trim().equals(""))
				strNewIdList += strId + ";";
		}

		// add the new video id
		strNewIdList += a_strVideoId + ";";

		Editor	editorPreferences = preferences.edit();
		editorPreferences.putString("com.keyes.screebl.lastViewedVideoIds", strNewIdList);
		editorPreferences.commit();
	}

	//------------------------------------------------------------------------------
	public static int GetSupportedFallbackId(int a_iOldId) {
		final int arrIntSupportedFormatId[] = {
				13,		//3GPP (MPEG-4 encoded) Low quality
				17,		//3GPP (MPEG-4 encoded) Medium quality
				18,		//MP4  (H.264 encoded) Normal quality
				22,		//MP4  (H.264 encoded) High quality
				37		//MP4  (H.264 encoded) High quality
		};
		int iFallbackId = a_iOldId;
		for (int i = arrIntSupportedFormatId.length - 1; i >= 0; i--) {
			if (a_iOldId == arrIntSupportedFormatId[i] && i > 0)
				iFallbackId = arrIntSupportedFormatId[i - 1];
		}
		return iFallbackId;
	}

	//==============================================================================
	public abstract class YouTubeId {
		protected String m_strId;

		public YouTubeId(String a_strId) {
			m_strId = a_strId;
		}

		public String GetId() {
			return m_strId;
		}
	}

	//==============================================================================
	public class VideoId extends YouTubeId {
		public VideoId(String a_strId) {
			super(a_strId);
		}
	}

	//==============================================================================
	public class FileId extends YouTubeId {
		public FileId(String a_strId) {
			super(a_strId);
		}
	}

	//==============================================================================
	public class PlaylistId extends YouTubeId {
		public PlaylistId(String a_strId) {
			super(a_strId);
		}
	}
	//==============================================================================
}

//==============================================================================
