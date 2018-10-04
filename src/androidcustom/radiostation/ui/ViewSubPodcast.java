package androidcustom.radiostation.ui;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.FrameLayout;
import android.widget.ListView;
import androidcustom.radiostation.global.Const;
import androidcustom.radiostation.global.EnvVariable;
import androidcustom.radiostation.http.HttpApi;
import androidcustom.radiostation.http.HttpApi.HttpApiListener;
import androidcustom.radiostation.loader.LoaderImage;
import androidcustom.radiostation.multimedia.MultimediaInfo;
import androidcustom.radiostation.ui.AdapterBaseList.ItemContentClickListener;

//==============================================================================
public class ViewSubPodcast {

	private Context			m_context;
	private ListView		m_listViewContent;
	private	int				m_iType;

	private HttpApi			m_httpApi;

	private AdapterSubPodcast			m_adapterSubPodcast;

	private	CountDownTimer		m_timer;


	//------------------------------------------------------------------------------
	public ViewSubPodcast(Context a_context) {
		m_context = a_context;

		m_httpApi = null;

		m_listViewContent		= new ListView(m_context);
		m_adapterSubPodcast	= new AdapterSubPodcast(m_context, m_listViewContent);
		m_listViewContent.setAdapter(m_adapterSubPodcast);

		m_iType = Const.TYPE_NONE;

	// {{ Refresh ListViews Periodically
		m_timer = new CountDownTimer(Const.DELAY_INVALIDATE, 100) {
			@Override
			public void onFinish() {
				if ((EnvVariable.CurrentMainItem == Const.ITEM_PODCAST) && (EnvVariable.CurrentSubPodcastType == m_iType))
					m_listViewContent.invalidateViews();
				start();
			}
			@Override
			public void onTick(long millisUntilFinished) {
			}
		};
	// }} Refresh ListViews Periodically

	// {{ ListView Item Click Listener
		/*
		m_listViewContent.setOnItemClickListener(
			new OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> adapter, View v, int position, long arg3) {
					ShowActivityPlayAudio(m_iType, position);
				}
			}
		);
		*/
		m_adapterSubPodcast.SetItemContentClickListener(
			new ItemContentClickListener() {
				@Override
				public void OnItemContentClicked(int a_iPosition) {
					ShowActivityPlayAudio(m_iType, a_iPosition);
				}
			}
		);
	// }} ListView Item Click Listener
	}

	//------------------------------------------------------------------------------
	public void SetType(int a_iType) {
		m_iType = a_iType;
	}

	//------------------------------------------------------------------------------
	public void SetLayout(FrameLayout a_frmlytParent) {
		FrameLayout.LayoutParams	frmLayoutParams;

		a_frmlytParent.addView(m_listViewContent);

	// {{ m_lstviewContent
		frmLayoutParams = new FrameLayout.LayoutParams(
				FrameLayout.LayoutParams.FILL_PARENT,
				FrameLayout.LayoutParams.FILL_PARENT);
		frmLayoutParams.setMargins((int)((1 +  Const.RATIO_WIDTH_MAINLIST) * EnvVariable.SizeDisplay.x), 0,
										(-1) * EnvVariable.SizeDisplay.x, 0);
		m_listViewContent.setLayoutParams(frmLayoutParams);
	// }} m_lstviewContent
	}

	//------------------------------------------------------------------------------
	public ListView GetContentView() {
		return m_listViewContent;
	}

	//------------------------------------------------------------------------------
	public void Show() {
		FrameLayout.LayoutParams	frmLayoutParams;

		if (LoaderImage.GetInstance() != null)
			LoaderImage.GetInstance().ClearCache();

		frmLayoutParams = (FrameLayout.LayoutParams)m_listViewContent.getLayoutParams();
		frmLayoutParams.setMargins((int)(Const.RATIO_WIDTH_MAINLIST * EnvVariable.SizeDisplay.x), 0, 0, 0);
		m_listViewContent.setLayoutParams(frmLayoutParams);

		if ((m_httpApi == null) && (m_iType != Const.TYPE_NONE)) {
			m_httpApi = new HttpApi();
			m_httpApi.SetHttpApiListener(
				new HttpApiListener() {
					@Override
					public void OnHttpApiResult(String a_strResult, int a_iType) {
						JSONObject			jsonObj;
						JSONArray			jsonArrSongList;
						int					iIndex;

						if (a_strResult == null)			return;
						if (a_strResult.length() == 0)		return;

						try {
							jsonObj = new JSONObject(a_strResult);
							jsonArrSongList = jsonObj.getJSONArray("playlist");
							for (iIndex = 0; iIndex < jsonArrSongList.length(); iIndex++) {
								if (iIndex == Const.MAX_AUDIO_COUNT)
									break;
								JSONObject			jsonPathData;
								String				strPath;
								MultimediaInfo		multimediaInfo = new MultimediaInfo();
								JSONObject			jsonMultimediaData = jsonArrSongList.getJSONObject(iIndex);
								try {
									multimediaInfo.SetIndex(iIndex);
									multimediaInfo.SetType(a_iType);
									multimediaInfo.SetTitle(jsonMultimediaData.getString("name"));
									multimediaInfo.SetArtist(jsonMultimediaData.getString("artist"));
									strPath = jsonMultimediaData.getString("path");
									jsonPathData = new JSONObject(strPath);
									if (strPath.contains("mp3"))
										multimediaInfo.SetPath1(jsonPathData.getString("mp3"));
									if (strPath.contains("webmv"))
										multimediaInfo.SetPath2(jsonPathData.getString("webmv"));
									multimediaInfo.SetPoster(jsonMultimediaData.getString("poster"));
									multimediaInfo.SetLikeCount(jsonMultimediaData.getInt("likecount"));
								} catch (Exception e) {}

								m_adapterSubPodcast.AddMultimediaInfo(multimediaInfo);
							}
						}
						catch (JSONException e1) {}
						catch (Exception e2) {}
					}
				}
			);
			m_httpApi.SetApiType(m_iType);
			switch (m_iType)
			{
				case Const.TYPE_PODCAST_1:		m_httpApi.StartGetSubPodcast("Podcast_1");	break;
				case Const.TYPE_PODCAST_2:		m_httpApi.StartGetSubPodcast("Podcast_2");	break;
				case Const.TYPE_PODCAST_3:		m_httpApi.StartGetSubPodcast("Podcast_3");	break;
				case Const.TYPE_PODCAST_4:		m_httpApi.StartGetSubPodcast("Podcast_4");	break;
				case Const.TYPE_PODCAST_5:		m_httpApi.StartGetSubPodcast("Podcast_5");	break;
				default:	break;
			}
		}
		m_timer.start();
	}

	//------------------------------------------------------------------------------
	public void Hide() {
		FrameLayout.LayoutParams	frmLayoutParams;

		if (LoaderImage.GetInstance() != null)
			LoaderImage.GetInstance().ClearCache();

		frmLayoutParams = (FrameLayout.LayoutParams)m_listViewContent.getLayoutParams();
		frmLayoutParams.setMargins((int)((1 +  Const.RATIO_WIDTH_MAINLIST) * EnvVariable.SizeDisplay.x), 0,
										(-1) * EnvVariable.SizeDisplay.x, 0);
		m_listViewContent.setLayoutParams(frmLayoutParams);
		m_timer.cancel();
	}

	//------------------------------------------------------------------------------
	public void ShowActivityPlayAudio(int a_iType, int a_iIndex) {
		Intent		intentSubPodcast2Play;

		intentSubPodcast2Play = new Intent(m_context, ActivityPlayAudio.class);		// ActivityMain -> ActivityPlay
		intentSubPodcast2Play.putExtra(Const.KEY_INDEX,		a_iIndex);
		EnvVariable.CurrentAdapter = m_adapterSubPodcast;
		m_context.startActivity(intentSubPodcast2Play);
	}

	//------------------------------------------------------------------------------
	public void SetFindText(String a_strFindText) {
		m_adapterSubPodcast.SetFindText(a_strFindText);
		m_listViewContent.invalidateViews();
	}

	//------------------------------------------------------------------------------
}

//==============================================================================
