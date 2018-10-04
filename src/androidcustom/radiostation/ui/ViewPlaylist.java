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
public class ViewPlaylist {

	private Context				m_context;
	private ListView			m_listViewContent;
	private AdapterPlaylist		m_adapterPlaylist;

	private boolean				m_bIsShownSubPlaylist;

	private ArrayList<ViewSubPlaylist>		m_arrViewSubPlaylist;
	private	int			m_iCurSubPlaylistIndex;

	private	HttpApi				m_httpApi;
	private	CountDownTimer 		m_timer;

	//------------------------------------------------------------------------------
	public ViewPlaylist(Context a_context) {
		ViewSubPlaylist		viewSubPlaylist;

		m_context = a_context;

		m_httpApi = null;
		m_iCurSubPlaylistIndex = 0;

		m_listViewContent	= new ListView(m_context);
		m_adapterPlaylist	= new AdapterPlaylist(m_context);
		m_listViewContent.setAdapter(m_adapterPlaylist);
		
	// {{ SubPlayList
		m_arrViewSubPlaylist = new ArrayList<ViewSubPlaylist>();

		viewSubPlaylist		= new ViewSubPlaylist(m_context);
		viewSubPlaylist.SetType(Const.TYPE_PLAYLIST_GBEDU);
		m_arrViewSubPlaylist.add(viewSubPlaylist);

		viewSubPlaylist		= new ViewSubPlaylist(m_context);
		viewSubPlaylist.SetType(Const.TYPE_PLAYLIST_LOVE);
		m_arrViewSubPlaylist.add(viewSubPlaylist);

		viewSubPlaylist		= new ViewSubPlaylist(m_context);
		viewSubPlaylist.SetType(Const.TYPE_PLAYLIST_AFRO);
		m_arrViewSubPlaylist.add(viewSubPlaylist);

		viewSubPlaylist		= new ViewSubPlaylist(m_context);
		viewSubPlaylist.SetType(Const.TYPE_PLAYLIST_WORKOUT);
		m_arrViewSubPlaylist.add(viewSubPlaylist);

		viewSubPlaylist		= new ViewSubPlaylist(m_context);
		viewSubPlaylist.SetType(Const.TYPE_PLAYLIST_CHURCH);
		m_arrViewSubPlaylist.add(viewSubPlaylist);

		viewSubPlaylist		= new ViewSubPlaylist(m_context);
		viewSubPlaylist.SetType(Const.TYPE_PLAYLIST_OLD);
		m_arrViewSubPlaylist.add(viewSubPlaylist);

		viewSubPlaylist		= new ViewSubPlaylist(m_context);
		viewSubPlaylist.SetType(Const.TYPE_PLAYLIST_RAP);
		m_arrViewSubPlaylist.add(viewSubPlaylist);
	// }} SubPlayList

		m_listViewContent.setOnItemClickListener(
			new OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> adapter, View v, int position, long arg3) {
					switch (position) {
						default:	break;
					}
					ShowSubPlaylist(position);
				}
			}
		);

		m_bIsShownSubPlaylist = false;
		
	// {{ Timer to Invalidate ListView
		m_timer = new CountDownTimer(Const.DELAY_INVALIDATE, 100) {
			@Override
			public void onFinish() {
				if (EnvVariable.CurrentMainItem == Const.ITEM_PLAYLIST)
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

	// {{ m_arrViewSubPlaylist
		for (iIndex = 0; iIndex < m_arrViewSubPlaylist.size(); iIndex++)
			m_arrViewSubPlaylist.get(iIndex).SetLayout(a_frmlytParent);
	// }} m_arrViewSubPlaylist
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
								m_adapterPlaylist.SetCountInfo(iIndex, iCount);
							}
						}
						catch (JSONException e1) {}
						catch (Exception e2) {}
					}
				}
			);
			m_httpApi.SetApiType(Const.TYPE_PLAYLIST);
			m_httpApi.StartGetPlaylist();
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
	private void ShowSubPlaylist(int a_iIndex) {
		Hide();
		m_iCurSubPlaylistIndex = a_iIndex;
		m_arrViewSubPlaylist.get(m_iCurSubPlaylistIndex).Show();
		m_bIsShownSubPlaylist = true;
		switch (a_iIndex) {
			case 0:		EnvVariable.CurrentSubPlaylistType = Const.TYPE_PLAYLIST_GBEDU;		break;
			case 1:		EnvVariable.CurrentSubPlaylistType = Const.TYPE_PLAYLIST_LOVE;		break;
			case 2:		EnvVariable.CurrentSubPlaylistType = Const.TYPE_PLAYLIST_AFRO;		break;
			case 3:		EnvVariable.CurrentSubPlaylistType = Const.TYPE_PLAYLIST_WORKOUT;	break;
			case 4:		EnvVariable.CurrentSubPlaylistType = Const.TYPE_PLAYLIST_CHURCH;	break;
			case 5:		EnvVariable.CurrentSubPlaylistType = Const.TYPE_PLAYLIST_OLD;		break;
			case 6:		EnvVariable.CurrentSubPlaylistType = Const.TYPE_PLAYLIST_RAP;		break;
			default:	EnvVariable.CurrentSubPlaylistType = Const.TYPE_NONE;				break;
		}
	}

	//------------------------------------------------------------------------------
	public void HideSubPlaylist() {
		int		iIndex;
		Show();
		for (iIndex = 0; iIndex < m_arrViewSubPlaylist.size(); iIndex++)
			m_arrViewSubPlaylist.get(iIndex).Hide();
		m_bIsShownSubPlaylist = false;
	}

	//------------------------------------------------------------------------------
	public boolean IsShownSubPlaylist() {
		return m_bIsShownSubPlaylist;
	}

	//------------------------------------------------------------------------------
	public void SetFindText(String a_strFindText) {
		if (m_bIsShownSubPlaylist)
			m_arrViewSubPlaylist.get(m_iCurSubPlaylistIndex).SetFindText(a_strFindText);
	}

	//------------------------------------------------------------------------------
	public void Refresh() {
		if (m_bIsShownSubPlaylist)
			m_arrViewSubPlaylist.get(m_iCurSubPlaylistIndex).SetFindText("");
	}

	//------------------------------------------------------------------------------
}

//==============================================================================
