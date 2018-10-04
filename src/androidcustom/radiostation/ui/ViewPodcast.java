package androidcustom.radiostation.ui;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
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

//==============================================================================
public class ViewPodcast {

	private Context				m_context;
	private ListView			m_listViewContent;
	private AdapterPodcast		m_adapterPodcast;

	private boolean				m_bIsShownSubPodcast;
	private	int			m_iCurSubPodcastIndex;
	
	private HttpApi				m_httpApi;

	private ArrayList<ViewSubPodcast>		m_arrViewSubPodcast;

	private	CountDownTimer 		m_timer;

	//------------------------------------------------------------------------------
	public ViewPodcast(Context a_context) {
		ViewSubPodcast		viewSubPodcast;

		m_context = a_context;
		
		m_httpApi = null;
		m_iCurSubPodcastIndex = 0;

		m_listViewContent	= new ListView(m_context);
		m_adapterPodcast	= new AdapterPodcast(m_context);
		m_listViewContent.setAdapter(m_adapterPodcast);
		
	// {{ SubPodcast
		m_arrViewSubPodcast = new ArrayList<ViewSubPodcast>();

		viewSubPodcast		= new ViewSubPodcast(m_context);
		viewSubPodcast.SetType(Const.TYPE_PODCAST_1);
		m_arrViewSubPodcast.add(viewSubPodcast);

		viewSubPodcast		= new ViewSubPodcast(m_context);
		viewSubPodcast.SetType(Const.TYPE_PODCAST_2);
		m_arrViewSubPodcast.add(viewSubPodcast);

		viewSubPodcast		= new ViewSubPodcast(m_context);
		viewSubPodcast.SetType(Const.TYPE_PODCAST_3);
		m_arrViewSubPodcast.add(viewSubPodcast);

		viewSubPodcast		= new ViewSubPodcast(m_context);
		viewSubPodcast.SetType(Const.TYPE_PODCAST_4);
		m_arrViewSubPodcast.add(viewSubPodcast);

		viewSubPodcast		= new ViewSubPodcast(m_context);
		viewSubPodcast.SetType(Const.TYPE_PODCAST_5);
		m_arrViewSubPodcast.add(viewSubPodcast);
	// }} SubPodcast

		m_listViewContent.setOnItemClickListener(
			new OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> adapter, View v, int position, long arg3) {
					switch (position) {
						default:	break;
					}
					ShowSubPodcast(position);
				}
			}
		);

		m_bIsShownSubPodcast = false;
		
	// {{ Timer to Invalidate ListView
		m_timer = new CountDownTimer(Const.DELAY_INVALIDATE, 100) {
			@Override
			public void onFinish() {
				if (EnvVariable.CurrentMainItem == Const.ITEM_PODCAST)
					m_listViewContent.invalidateViews();
				start();
			}
			@Override
			public void onTick(long millisUntilFinished) {
			}
		};
	// }} Timer to Invalidate ListView
	}

	//------------------------------------------------------------------------------
	public void SetLayout(FrameLayout a_frmlytParent) {
		FrameLayout.LayoutParams	frmLayoutParams;
		int							iIndex;

		a_frmlytParent.addView(m_listViewContent);

	// {{ m_lstviewContent
		frmLayoutParams = new FrameLayout.LayoutParams(
				FrameLayout.LayoutParams.FILL_PARENT,
				FrameLayout.LayoutParams.FILL_PARENT);
		frmLayoutParams.setMargins((int)((1 +  Const.RATIO_WIDTH_MAINLIST) * EnvVariable.SizeDisplay.x), 0,
										(-1) * EnvVariable.SizeDisplay.x, 0);
		m_listViewContent.setLayoutParams(frmLayoutParams);
	// }} m_lstviewContent

	// {{ m_arrViewSubPodcast
		for (iIndex = 0; iIndex < m_arrViewSubPodcast.size(); iIndex++)
			m_arrViewSubPodcast.get(iIndex).SetLayout(a_frmlytParent);
	// }} m_arrViewSubPodcast
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

		if (m_httpApi == null) {
			m_httpApi = new HttpApi();
			m_httpApi.SetHttpApiListener(
				 new HttpApiListener() {
					@Override
					public void OnHttpApiResult(String a_strResult, int a_iType) {
						JSONObject		jsonObj;
						JSONArray		jsonArrSongList;
						int				iIndex;
						int				iCount;

						if (a_strResult == null)		return;
						if (a_strResult.length() == 0)	return;

						try {
							jsonObj = new JSONObject(a_strResult);
							jsonArrSongList = jsonObj.getJSONArray("songcount");
							for (iIndex = 0; iIndex < jsonArrSongList.length(); iIndex++) {
								JSONObject		jsonCountData = jsonArrSongList.getJSONObject(iIndex);
								iCount = jsonCountData.getInt("count");
								m_adapterPodcast.SetCountInfo(iIndex, iCount);
							}
						}
						catch (JSONException e1) {}
						catch (Exception e2) {}
					}
				}
			);
			m_httpApi.SetApiType(Const.TYPE_PODCAST);
			m_httpApi.StartGetPodcast();
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
	private void ShowSubPodcast(int a_iIndex) {
		Hide();
		m_iCurSubPodcastIndex = a_iIndex;
		m_arrViewSubPodcast.get(m_iCurSubPodcastIndex).Show();
		m_bIsShownSubPodcast = true;

		switch (a_iIndex) {
			case 0:	EnvVariable.CurrentSubPodcastType = Const.TYPE_PODCAST_1;		break;
			case 1:	EnvVariable.CurrentSubPodcastType = Const.TYPE_PODCAST_2;		break;
			case 2:	EnvVariable.CurrentSubPodcastType = Const.TYPE_PODCAST_3;		break;
			case 3:	EnvVariable.CurrentSubPodcastType = Const.TYPE_PODCAST_4;		break;
			case 4:	EnvVariable.CurrentSubPodcastType = Const.TYPE_PODCAST_5;		break;
			default:	EnvVariable.CurrentSubPodcastType = Const.TYPE_NONE;		break;
		}
	}

	//------------------------------------------------------------------------------
	public void HideSubPodcast() {
		int		iIndex;
		Show();
		for (iIndex = 0; iIndex < m_arrViewSubPodcast.size(); iIndex++)
			m_arrViewSubPodcast.get(iIndex).Hide();
		m_bIsShownSubPodcast = false;
	}

	//------------------------------------------------------------------------------
	public boolean IsShownSubPodcast() {
		return m_bIsShownSubPodcast;
	}

	//------------------------------------------------------------------------------
	public void SetFindText(String a_strFindText) {
		if (m_bIsShownSubPodcast)
			m_arrViewSubPodcast.get(m_iCurSubPodcastIndex).SetFindText(a_strFindText);
	}

	//------------------------------------------------------------------------------
	public void Refresh() {
		if (m_bIsShownSubPodcast)
			m_arrViewSubPodcast.get(m_iCurSubPodcastIndex).SetFindText("");
	}

	//------------------------------------------------------------------------------
}

//==============================================================================
