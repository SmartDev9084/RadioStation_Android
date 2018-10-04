package androidcustom.radiostation.ui;

import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;
import androidcustom.radiostation.R;
import androidcustom.radiostation.db.DbApi;
import androidcustom.radiostation.global.Const;
import androidcustom.radiostation.global.EnvVariable;
import androidcustom.radiostation.global.UtilDisplay;
import androidcustom.radiostation.loader.LoaderImage;
import androidcustom.radiostation.multimedia.MultimediaInfo;

//==============================================================================
public class ViewUser {

	private Context				m_context;
	private View				m_viewContent;

	private	ListView			m_listViewFavorite;
	private	ListView			m_listViewPlaylist;
	private	ListView			m_listViewHistory;

	private	AdapterUser			m_adapterFavorite;
	private	AdapterUser			m_adapterPlaylist;
	private	AdapterUser			m_adapterHistory;

	private	ImageView			m_imgViewFavorite;
	private	ImageView			m_imgViewPlaylist;
	private	ImageView			m_imgViewHistory;
	
	private	CountDownTimer 		m_timer;

	private	boolean		m_bIsShown;
	private int			m_iCurTabIndex;

	//------------------------------------------------------------------------------
	public ViewUser(Context a_context) {
		LayoutInflater		inflater;
		LayoutParams		layoutParams;

		m_context = a_context;

		m_bIsShown		= false;
		m_iCurTabIndex	= 0;

		inflater = (LayoutInflater)m_context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		m_viewContent = inflater.inflate(R.layout.view_user, null);

	// {{ ImageView Favorite
		m_imgViewFavorite = (ImageView)m_viewContent.findViewById(R.id.ID_IMGVIEW_USER_FAVORITE);
		layoutParams = m_imgViewFavorite.getLayoutParams();
		layoutParams.width = UtilDisplay.GetScreenSize(m_context).x / 3;
		m_imgViewFavorite.setLayoutParams(layoutParams);
		m_imgViewFavorite.setOnClickListener(
			new OnClickListener() {
				@Override
				public void onClick(View v) {
					ArrayList<MultimediaInfo>		arrMultimediaInfo = null;

					if (LoaderImage.GetInstance() != null)
						LoaderImage.GetInstance().ClearCache();
					m_iCurTabIndex = 0;

					m_imgViewFavorite.setBackgroundResource(R.drawable.img_tab_user_clicked);
					m_imgViewPlaylist.setBackgroundResource(R.drawable.img_tab_user);
					m_imgViewHistory.setBackgroundResource(R.drawable.img_tab_user);

					m_listViewFavorite.setVisibility(View.VISIBLE);
					m_listViewPlaylist.setVisibility(View.INVISIBLE);
					m_listViewHistory.setVisibility(View.INVISIBLE);

					if (DbApi.GetInstance() != null)
						arrMultimediaInfo = DbApi.GetInstance().GetMultimediaInfoFromFavorite();
					m_adapterFavorite.SetMultimediaInfoArray(arrMultimediaInfo);
					m_listViewFavorite.invalidateViews();
				}
			}
		);
	// }} ImageView Favorite
	// {{ ImageView Playlist
		m_imgViewPlaylist = (ImageView)m_viewContent.findViewById(R.id.ID_IMGVIEW_USER_PLAYLIST);
		layoutParams = m_imgViewFavorite.getLayoutParams();
		layoutParams.width = UtilDisplay.GetScreenSize(m_context).x / 3;
		m_imgViewPlaylist.setLayoutParams(layoutParams);
		m_imgViewPlaylist.setOnClickListener(
			new OnClickListener() {
				@Override
				public void onClick(View v) {
					ArrayList<MultimediaInfo>		arrMultimediaInfo = null;

					if (LoaderImage.GetInstance() != null)
						LoaderImage.GetInstance().ClearCache();
					m_iCurTabIndex = 1;

					m_imgViewFavorite.setBackgroundResource(R.drawable.img_tab_user);
					m_imgViewPlaylist.setBackgroundResource(R.drawable.img_tab_user_clicked);
					m_imgViewHistory.setBackgroundResource(R.drawable.img_tab_user);

					m_listViewFavorite.setVisibility(View.INVISIBLE);
					m_listViewPlaylist.setVisibility(View.VISIBLE);
					m_listViewHistory.setVisibility(View.INVISIBLE);
					if (DbApi.GetInstance() != null)
						arrMultimediaInfo = DbApi.GetInstance().GetMultimediaInfoFromPlaylist();
					m_adapterPlaylist.SetMultimediaInfoArray(arrMultimediaInfo);
					m_listViewPlaylist.invalidateViews();
				}
			}
		);
	// }} ImageView Playlist
	// {{ ImageView History
		m_imgViewHistory = (ImageView)m_viewContent.findViewById(R.id.ID_IMGVIEW_USER_HISTORY);
		layoutParams = m_imgViewFavorite.getLayoutParams();
		layoutParams.width = UtilDisplay.GetScreenSize(m_context).x / 3;
		m_imgViewHistory.setLayoutParams(layoutParams);
		m_imgViewHistory.setOnClickListener(
			new OnClickListener() {
				@Override
				public void onClick(View v) {
					ArrayList<MultimediaInfo>		arrMultimediaInfo = null;

					if (LoaderImage.GetInstance() != null)
						LoaderImage.GetInstance().ClearCache();
					m_iCurTabIndex = 2;

					m_imgViewFavorite.setBackgroundResource(R.drawable.img_tab_user);
					m_imgViewPlaylist.setBackgroundResource(R.drawable.img_tab_user);
					m_imgViewHistory.setBackgroundResource(R.drawable.img_tab_user_clicked);

					m_listViewFavorite.setVisibility(View.INVISIBLE);
					m_listViewPlaylist.setVisibility(View.INVISIBLE);
					m_listViewHistory.setVisibility(View.VISIBLE);
					if (DbApi.GetInstance() != null)
						arrMultimediaInfo = DbApi.GetInstance().GetMultimediaInfoFromHistory();
					m_adapterHistory.SetMultimediaInfoArray(arrMultimediaInfo);
					m_listViewHistory.invalidateViews();
				}
			}
		);
	// }} ImageView History

	// {{ Favorite ListView
		m_listViewFavorite = (ListView)m_viewContent.findViewById(R.id.ID_LISTVIEW_USER_FAVORITE);
		m_listViewFavorite.setOnItemClickListener(
			new OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> adapter, View v, int position, long arg3) {
					int			iType = Const.TYPE_NONE;
					MultimediaInfo	multimediaInfo;
					multimediaInfo = (MultimediaInfo)m_adapterFavorite.getItem(position);
					iType = multimediaInfo.GetType();
					switch (iType) {
						case Const.TYPE_HOME_TREND:
						case Const.TYPE_HOME_TOP10:
						case Const.TYPE_HOME_STAFF:
						case Const.TYPE_HOME_CLASS:
						case Const.TYPE_PLAYLIST_GBEDU:
						case Const.TYPE_PLAYLIST_LOVE:
						case Const.TYPE_PLAYLIST_AFRO:
						case Const.TYPE_PLAYLIST_WORKOUT:
						case Const.TYPE_PLAYLIST_CHURCH:
						case Const.TYPE_PLAYLIST_OLD:
						case Const.TYPE_PLAYLIST_RAP:
						case Const.TYPE_PODCAST_1:
						case Const.TYPE_PODCAST_2:
						case Const.TYPE_PODCAST_3:
						case Const.TYPE_PODCAST_4:
						case Const.TYPE_PODCAST_5:
							ShowActivityPlayAudio(0, position);
							break;
						case Const.TYPE_VIDEO:
						case Const.TYPE_SUBCOMEDY:
						case Const.TYPE_SUBNOLLYWOOD:
							ShowActivityPlayVideo(0, position);
					default:
						break;
					}
				}
			}
		);
		m_adapterFavorite = new AdapterUser(m_context, m_listViewFavorite);
		m_listViewFavorite.setAdapter(m_adapterFavorite);
	// }} Favorite ListView

	// {{ Playlist ListView
		m_listViewPlaylist = (ListView)m_viewContent.findViewById(R.id.ID_LISTVIEW_USER_PLAYLIST);
		m_listViewPlaylist.setOnItemClickListener(
			new OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> adapter, View v, int position, long arg3) {
					int			iType = Const.TYPE_NONE;
					MultimediaInfo	multimediaInfo;
					multimediaInfo = (MultimediaInfo)m_adapterPlaylist.getItem(position);
					iType = multimediaInfo.GetType();
					switch (iType) {
						case Const.TYPE_HOME_TREND:
						case Const.TYPE_HOME_TOP10:
						case Const.TYPE_HOME_STAFF:
						case Const.TYPE_HOME_CLASS:
						case Const.TYPE_PLAYLIST_GBEDU:
						case Const.TYPE_PLAYLIST_LOVE:
						case Const.TYPE_PLAYLIST_AFRO:
						case Const.TYPE_PLAYLIST_WORKOUT:
						case Const.TYPE_PLAYLIST_CHURCH:
						case Const.TYPE_PLAYLIST_OLD:
						case Const.TYPE_PLAYLIST_RAP:
						case Const.TYPE_PODCAST_1:
						case Const.TYPE_PODCAST_2:
						case Const.TYPE_PODCAST_3:
						case Const.TYPE_PODCAST_4:
						case Const.TYPE_PODCAST_5:
							ShowActivityPlayAudio(1, position);
							break;
						case Const.TYPE_VIDEO:
						case Const.TYPE_SUBCOMEDY:
						case Const.TYPE_SUBNOLLYWOOD:
							ShowActivityPlayVideo(1, position);
					default:
						break;
					}
				}
			}
		);
		m_adapterPlaylist = new AdapterUser(m_context, m_listViewPlaylist);
		m_listViewPlaylist.setAdapter(m_adapterPlaylist);
	// }} Playlist ListView

	// {{ History ListView
		m_listViewHistory = (ListView)m_viewContent.findViewById(R.id.ID_LISTVIEW_USER_HISTORY);
		m_listViewHistory.setOnItemClickListener(
			new OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> adapter, View v, int position, long arg3) {
					int			iType = Const.TYPE_NONE;
					MultimediaInfo	multimediaInfo;
					multimediaInfo = (MultimediaInfo)m_adapterHistory.getItem(position);
					iType = multimediaInfo.GetType();
					switch (iType) {
						case Const.TYPE_HOME_TREND:
						case Const.TYPE_HOME_TOP10:
						case Const.TYPE_HOME_STAFF:
						case Const.TYPE_HOME_CLASS:
						case Const.TYPE_PLAYLIST_GBEDU:
						case Const.TYPE_PLAYLIST_LOVE:
						case Const.TYPE_PLAYLIST_AFRO:
						case Const.TYPE_PLAYLIST_WORKOUT:
						case Const.TYPE_PLAYLIST_CHURCH:
						case Const.TYPE_PLAYLIST_OLD:
						case Const.TYPE_PLAYLIST_RAP:
						case Const.TYPE_PODCAST_1:
						case Const.TYPE_PODCAST_2:
						case Const.TYPE_PODCAST_3:
						case Const.TYPE_PODCAST_4:
						case Const.TYPE_PODCAST_5:
							ShowActivityPlayAudio(2, position);
							break;
						case Const.TYPE_VIDEO:
						case Const.TYPE_SUBCOMEDY:
						case Const.TYPE_SUBNOLLYWOOD:
							ShowActivityPlayVideo(2, position);
					default:
						break;
					}
				}
			}
		);
		m_adapterHistory = new AdapterUser(m_context, m_listViewHistory);
		m_listViewHistory.setAdapter(m_adapterHistory);
	// }} History ListView

	// {{ Refresh ListViews Periodically
		m_timer = new CountDownTimer(Const.DELAY_INVALIDATE, 100) {
			@Override
			public void onFinish() {
				if (m_bIsShown && EnvVariable.CurrentMainItem == Const.ITEM_USER) {
					switch (m_iCurTabIndex) {
						case 0:		m_listViewFavorite.invalidateViews();	break;
						case 1:		m_listViewPlaylist.invalidateViews();	break;
						case 2:		m_listViewPlaylist.invalidateViews();	break;
						default:	break;
					}
				}
				start();
			}
			@Override
			public void onTick(long millisUntilFinished) {
			}
		};
	// }} Refresh ListViews Periodically

	}

	//------------------------------------------------------------------------------
	public void SetLayout(FrameLayout a_frmLytParent) {
		FrameLayout.LayoutParams	frmLayoutParams;

		a_frmLytParent.addView(m_viewContent);

		frmLayoutParams = new FrameLayout.LayoutParams(
				FrameLayout.LayoutParams.FILL_PARENT,
				FrameLayout.LayoutParams.FILL_PARENT);
		frmLayoutParams.setMargins(	(int)((1 + Const.RATIO_WIDTH_MAINLIST) * EnvVariable.SizeDisplay.x),
									0,
									(-1) * EnvVariable.SizeDisplay.x,
									0);
		m_viewContent.setLayoutParams(frmLayoutParams);
	}

	//------------------------------------------------------------------------------
	public View GetContentView() {
		return m_viewContent;
	}

	//------------------------------------------------------------------------------
	public void Show() {
		FrameLayout.LayoutParams	frmLayoutParams;

		if (LoaderImage.GetInstance() != null)
			LoaderImage.GetInstance().ClearCache();

		frmLayoutParams = (FrameLayout.LayoutParams)m_viewContent.getLayoutParams();
		frmLayoutParams.setMargins((int)(Const.RATIO_WIDTH_MAINLIST * EnvVariable.SizeDisplay.x), 0, 0, 0);
		m_viewContent.setLayoutParams(frmLayoutParams);

		m_timer.start();
		m_bIsShown = true;
		switch (m_iCurTabIndex) {
			case 0:		m_imgViewFavorite.performClick();	break;
			case 1:		m_imgViewPlaylist.performClick();	break;
			case 2:		m_imgViewHistory.performClick();	break;
			default:	break;
		}
	}

	//------------------------------------------------------------------------------
	public void Hide() {
		FrameLayout.LayoutParams	frmLayoutParams;

		if (LoaderImage.GetInstance() != null)
			LoaderImage.GetInstance().ClearCache();

		frmLayoutParams = (FrameLayout.LayoutParams)m_viewContent.getLayoutParams();
		frmLayoutParams.setMargins((int)((1 + Const.RATIO_WIDTH_MAINLIST) * EnvVariable.SizeDisplay.x), 0,
										(-1) * EnvVariable.SizeDisplay.x, 0);
		m_viewContent.setLayoutParams(frmLayoutParams);

		m_timer.cancel();
		m_bIsShown = false;
	}

	//------------------------------------------------------------------------------
	public void SetFindText(String a_strFindText) {
		switch (m_iCurTabIndex) {
			case 0:		m_adapterFavorite.SetFindText(a_strFindText);	m_listViewFavorite.invalidateViews();	break;
			case 1:		m_adapterPlaylist.SetFindText(a_strFindText);	m_listViewPlaylist.invalidateViews();	break;
			case 2:		m_adapterHistory.SetFindText(a_strFindText);	m_listViewHistory.invalidateViews();	break;
			default:	break;
		}
	}

	//------------------------------------------------------------------------------
	public void ShowActivityPlayAudio(int a_iAdapterIndex, int a_iIndex) {
		Intent		intentHome2Play;

		intentHome2Play = new Intent(m_context, ActivityPlayAudio.class);		// ActivityMain -> ActivityPlay
		intentHome2Play.putExtra(Const.KEY_INDEX,	a_iIndex);
		switch (a_iAdapterIndex) {
			case 0:		EnvVariable.CurrentAdapter = m_adapterFavorite;	break;
			case 1:		EnvVariable.CurrentAdapter = m_adapterPlaylist;	break;
			case 2:		EnvVariable.CurrentAdapter = m_adapterHistory;	break;
			default:	return;
		}
		m_context.startActivity(intentHome2Play);
	}

	//------------------------------------------------------------------------------
	public void ShowActivityPlayVideo(int a_iAdapterIndex, int a_iIndex) {
		Intent		intentVideo2Play;

		intentVideo2Play = new Intent(m_context, ActivityPlayVideo.class);		// ActivityMain -> ActivityPlayVideo
		intentVideo2Play.putExtra(Const.KEY_INDEX,	a_iIndex);
		switch (a_iAdapterIndex) {
			case 0:		EnvVariable.CurrentAdapter = m_adapterFavorite;	break;
			case 1:		EnvVariable.CurrentAdapter = m_adapterPlaylist;	break;
			case 2:		EnvVariable.CurrentAdapter = m_adapterHistory;	break;
			default:	return;
		}
		m_context.startActivity(intentVideo2Play);
	}

	//------------------------------------------------------------------------------
	public void Refresh() {
		switch (m_iCurTabIndex) {
			case 0:		m_adapterFavorite.SetFindText("");		m_listViewFavorite.invalidateViews();	break;
			case 1:		m_adapterPlaylist.SetFindText("");		m_listViewPlaylist.invalidateViews();	break;
			case 2:		m_adapterHistory.SetFindText("");		m_listViewHistory.invalidateViews();	break;
			default:	break;
		}
	}
}

//==============================================================================
