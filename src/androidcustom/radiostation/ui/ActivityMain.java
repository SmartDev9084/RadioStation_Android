package androidcustom.radiostation.ui;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.entity.BufferedHttpEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import com.un4seen.bass.BASS;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.SearchView.OnQueryTextListener;
import android.widget.TextView;
import androidcustom.radiostation.R;
import androidcustom.radiostation.chat.ui.ActivityChatLogin;
import androidcustom.radiostation.global.Const;
import androidcustom.radiostation.global.EnvVariable;
import androidcustom.radiostation.global.UtilDisplay;
import androidcustom.radiostation.loader.LoaderImage;

//==============================================================================
public class ActivityMain extends Activity {
	private	FrameLayout					m_frmLytContent;
	private	ListView					m_lstViewMain;
	private	ImageView					m_imgViewMain;
	private	AdapterMain					m_adapterListMain;
	private	ArrayList<ItemDataMain>		m_arrItemData;

	private	SearchView					m_searchView;

	private int					m_iLoginType;
	private	String				m_strUserId;
	private	String				m_strUserName;
	private	String				m_strAvatarUrl;

	private	ViewUser					m_viewUser;
	private	ViewHome					m_viewHome;
	private	ViewPlaylist				m_viewPlaylist;
	private	ViewPodcast					m_viewPodcast;
	private	ViewComedy					m_viewComedy;
	private	ViewNollywood				m_viewNollywood;
	private	ViewFacebook				m_viewFacebook;
	private	ViewTwitter					m_viewTwitter;

	private boolean						m_bIsAnimationing;
	private int							m_iAnimationId;

	private boolean						m_bShowSocialItem;

	private CountDownTimer				m_timer;
	private	int							m_iCountTime;
	
	private	DialogProgress				m_dlgProgress;

	private AnimationListener			m_listenerAnimation;
	private OnItemClickListener			m_listenerOnItemClick;
	private OnScrollListener			m_listenerOnScroll;

	private static final int	DELAY_CATCH		= 1000;
	private static final int	DELAY_SUBVIEW	= 5000;
	private static final int	DELAY_READY		= 10000;

	//------------------------------------------------------------------------------
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		setRequestedOrientation(getResources().getConfiguration().orientation);		// Won't Rotate

		m_iLoginType	= getIntent().getExtras().getInt(Const.KEY_LOGINTYPE);
		m_strUserId		= getIntent().getExtras().getString(Const.KEY_USERID);
		m_strUserName	= getIntent().getExtras().getString(Const.KEY_USERNAME);
		m_strAvatarUrl	= getIntent().getExtras().getString(Const.KEY_AVATAR);

		m_iCountTime	= 0;

	// {{ SSL Communication
		System.setProperty("twitter4j.http.useSSL", "true");
	// }} SSL Communication

	// {{ DialogProgress
		if (m_dlgProgress == null) {
			m_dlgProgress = new DialogProgress(this);
			m_dlgProgress.setCanceledOnTouchOutside(false);
			m_dlgProgress.setCancelable(false);
		}
		m_dlgProgress.show();
	// }} DialogProgress

	// {{ Views
		m_frmLytContent	= (FrameLayout)findViewById(R.id.ID_FRMLYT_MAIN);
		m_lstViewMain	= (ListView)findViewById(R.id.ID_LSTVIEW_MAIN);
		m_viewUser			= new ViewUser(this);
		m_viewHome			= new ViewHome(this);
		m_viewPlaylist		= new ViewPlaylist(this);
		m_viewPodcast		= new ViewPodcast(this);
		m_viewComedy		= new ViewComedy(this);
		m_viewNollywood		= new ViewNollywood(this);
		m_viewFacebook		= new ViewFacebook(this);
		m_viewTwitter		= new ViewTwitter(this);
	// }} Views

	// {{ OnItemClickListener For Main List
		m_listenerOnItemClick = new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> adapter, View v, int position, long arg3) {
				m_iCountTime = 0;
				switch (position) {
					case Const.ITEM_USER:
					case Const.ITEM_HOME:
					case Const.ITEM_PLAYLIST:
					case Const.ITEM_PODCAST:
					case Const.ITEM_COMEDY:
					case Const.ITEM_NOLLYWOOD:
					case Const.ITEM_FACEBOOK:
					case Const.ITEM_TWITTER:	ShowSubView(position);		break;
					case Const.ITEM_SOCIAL:		ShowHideSocialItem();		break;
					case Const.ITEM_CHAT:		HideCurrentSubView();	ShowActivityChat();			break;
					case Const.ITEM_RADIO:		HideCurrentSubView();	ShowActivityRadio();		break;
					case Const.ITEM_VIDEO:		HideCurrentSubView();	ShowActivityVideo();		break;
					case Const.ITEM_PHOTO:		HideCurrentSubView();	ShowActivityPhoto();		break;
					case Const.ITEM_EXIT:
						if (LoaderImage.GetInstance() != null)
							LoaderImage.GetInstance().ClearCache();

						EnvVariable.KillCurrentSound();

						finish();
						break;
					default:	break;
				}
			}
		};
		m_lstViewMain.setOnItemClickListener(m_listenerOnItemClick);
	// }} OnItemClickListener For Main List

	// {{ OnScroll Listener For Main List
		m_listenerOnScroll = new OnScrollListener() {
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				m_iCountTime = 0;
			}
			@Override
			public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
				m_iCountTime = 0;
			}
		};
		m_lstViewMain.setOnScrollListener(m_listenerOnScroll);
	// }} OnScroll Listener For Main List

	// {{ AnimationListener
		m_listenerAnimation = new AnimationListener() {
			@Override
			public void onAnimationStart(Animation animation) {
			}

			@Override
			public void onAnimationRepeat(Animation animation) {
			}

			@Override
			public void onAnimationEnd(Animation animation) {
				FrameLayout.LayoutParams	frmLayoutParams;
				m_iCountTime = 0;
				m_bIsAnimationing = false;
				m_frmLytContent.clearAnimation();
				if (m_iAnimationId == R.animator.animation_half_left) {
					frmLayoutParams = (FrameLayout.LayoutParams)m_frmLytContent.getLayoutParams();
					frmLayoutParams.setMargins(
						(int)((-Const.RATIO_WIDTH_MAINLIST) * EnvVariable.SizeDisplay.x),
						frmLayoutParams.topMargin,
						0,
						0
					);
					m_frmLytContent.setLayoutParams(frmLayoutParams);
				}
				if (m_iAnimationId == R.animator.animation_half_right) {
					frmLayoutParams = (FrameLayout.LayoutParams)m_frmLytContent.getLayoutParams();
					frmLayoutParams.setMargins(
						0,
						frmLayoutParams.topMargin,
						(int)((-Const.RATIO_WIDTH_MAINLIST) * EnvVariable.SizeDisplay.x),
						0
					);
					m_frmLytContent.setLayoutParams(frmLayoutParams);
				}
			}
		};
	// }} AnimationListener

	// {{ Timer
		m_timer = new CountDownTimer(DELAY_CATCH, 500) {
			@Override
			public void onFinish() {
				m_iCountTime += DELAY_CATCH;
				if (EnvVariable.CurrentMainItem == Const.ITEM_VIDEO)	m_iCountTime = 0;
				if (EnvVariable.CurrentMainItem == Const.ITEM_PHOTO)	m_iCountTime = 0;
				if (EnvVariable.CurrentMainItem == Const.ITEM_RADIO)	m_iCountTime = 0;
				if (EnvVariable.CurrentMainItem == Const.ITEM_CHAT)		m_iCountTime = 0;
				if (EnvVariable.CurrentMainItem == Const.ITEM_SOCIAL)	m_iCountTime = 0;
				if (m_bIsAnimationing)									m_iCountTime = 0;
				if (m_iCountTime >= DELAY_SUBVIEW) {
					m_iCountTime = 0;
					if (m_iAnimationId == R.animator.animation_half_right)
						StartAnimationLeft();
				}
				
				start();
			}
			@Override
			public void onTick(long millisUntilFinished) {
			}
		};
	// }} Timer

		EnvVariable.CurrentMainItem				= Const.ITEM_USER;
		EnvVariable.CurrentMainItemWithView		= Const.ITEM_USER;
		m_iAnimationId = R.animator.animation_half_right;
		m_bIsAnimationing		= false;

		SetData();
		SetLayout();
		
		m_timer.start();
	}

	//------------------------------------------------------------------------------
	private void SetMiscMainItemData() {
		ItemDataMain		itemData;
		Drawable			drawable;

		drawable = getResources().getDrawable(R.drawable.img_item_home);
		itemData = new ItemDataMain(drawable,		"Home");				m_arrItemData.add(itemData);
		drawable = getResources().getDrawable(R.drawable.img_item_radio);
		itemData = new ItemDataMain(drawable,		"Listen Now");			m_arrItemData.add(itemData);
		drawable = getResources().getDrawable(R.drawable.img_item_playlist);
		itemData = new ItemDataMain(drawable,		"Playlists");			m_arrItemData.add(itemData);
		drawable = getResources().getDrawable(R.drawable.img_item_podcast);
		itemData = new ItemDataMain(drawable,		"Podcast");				m_arrItemData.add(itemData);
		drawable = getResources().getDrawable(R.drawable.img_item_video);
		itemData = new ItemDataMain(drawable,		"Videos");				m_arrItemData.add(itemData);
		drawable = getResources().getDrawable(R.drawable.img_item_comedy);
		itemData = new ItemDataMain(drawable,		"Comedy");				m_arrItemData.add(itemData);
		drawable = getResources().getDrawable(R.drawable.img_item_nollywood);
		itemData = new ItemDataMain(drawable,		"Nollywood");			m_arrItemData.add(itemData);
		drawable = getResources().getDrawable(R.drawable.img_item_photo);
		itemData = new ItemDataMain(drawable,		"Photos");				m_arrItemData.add(itemData);
		drawable = getResources().getDrawable(R.drawable.img_item_social);
		itemData = new ItemDataMain(drawable,		"Social");				m_arrItemData.add(itemData);
		drawable = getResources().getDrawable(R.drawable.img_item_facebook);
		itemData = new ItemDataMain(drawable,		"Facebook");			m_arrItemData.add(itemData);
		drawable = getResources().getDrawable(R.drawable.img_item_twitter);
		itemData = new ItemDataMain(drawable,		"Twitter");				m_arrItemData.add(itemData);
		drawable = getResources().getDrawable(R.drawable.img_item_chat);
		itemData = new ItemDataMain(drawable,		"Chat");				m_arrItemData.add(itemData);
		drawable = getResources().getDrawable(R.drawable.img_item_exit);
		itemData = new ItemDataMain(drawable,		"Exit App");			m_arrItemData.add(itemData);

		m_adapterListMain = new AdapterMain(this, m_arrItemData);
		m_lstViewMain.setAdapter(m_adapterListMain);
	}

	//------------------------------------------------------------------------------
	private synchronized void SetMainItemDataWithFacebook() {
		AsyncTask<Void, Void, Drawable> task = new AsyncTask<Void, Void, Drawable>() {
			@Override
			public Drawable doInBackground(Void... params) {
//				URL			urlImage	= null;
				Drawable	drawable	= null;
				InputStream		inputStream = null;
				try {
/*
					urlImage	= new URL("http://graph.facebook.com/" + m_strUserId + "/picture");
					inputStream = urlImage.openConnection().getInputStream();
					drawable	= Drawable.createFromStream(inputStream, null);
*/
					HttpGet		httpRequest = null;
					try {
						httpRequest = new HttpGet(new URL("http://graph.facebook.com/" + m_strUserId + "/picture").toURI());
					} catch (Exception e) {}
					HttpClient			httpClient		= new DefaultHttpClient();
					HttpResponse		response		= (HttpResponse) httpClient.execute(httpRequest);
					HttpEntity			entity			= response.getEntity();
					BufferedHttpEntity	bufHttpEntity	= new BufferedHttpEntity(entity);
					inputStream = bufHttpEntity.getContent();
					drawable	= Drawable.createFromStream(inputStream, null);
				}
				catch (Exception e) {
					drawable = getResources().getDrawable(R.drawable.img_defaultprofile);
				}
				return drawable;
			}
			@Override
			protected void onPostExecute(Drawable result) {
				ItemDataMain		itemData;
				itemData = new ItemDataMain(result, m_strUserName);
				m_arrItemData.add(itemData);
				SetMiscMainItemData();
				m_bShowSocialItem = false;
				m_adapterListMain.SetVisibleSocialItem(m_bShowSocialItem);
				m_dlgProgress.hide();
			}
		};
		task.execute();
	}

	//------------------------------------------------------------------------------
	private synchronized void SetMainItemDataWithGoogle() {
		AsyncTask<Void, Void, Bitmap> task = new AsyncTask<Void, Void, Bitmap>() {
			@Override
			public Bitmap doInBackground(Void... params) {
				String	strUrlDisplay = m_strAvatarUrl;
				Bitmap	bitmapAvatar = null;
				try {
					InputStream		inputStream = new java.net.URL(strUrlDisplay).openStream();
					bitmapAvatar = BitmapFactory.decodeStream(inputStream);
				}
				catch (Exception e) {}
				return bitmapAvatar;
			}
			@Override
			protected void onPostExecute(Bitmap result) {
				ItemDataMain		itemData;
				Drawable	drawable = new BitmapDrawable(result);
				itemData = new ItemDataMain(drawable, m_strUserName);
				m_arrItemData.add(itemData);
				SetMiscMainItemData();
				m_bShowSocialItem = false;
				m_adapterListMain.SetVisibleSocialItem(m_bShowSocialItem);
				m_dlgProgress.hide();
			}
		};
		task.execute();
	}

	//------------------------------------------------------------------------------
	private synchronized void SetMainItemDataWithTwitter() {
		AsyncTask<Void, Void, Bitmap> task = new AsyncTask<Void, Void, Bitmap>() {
			@Override
			public Bitmap doInBackground(Void... params) {
				String	strUrlDisplay = m_strAvatarUrl;
				Bitmap	bitmapAvatar = null;
				try {
					InputStream		inputStream = new java.net.URL(strUrlDisplay).openStream();
					bitmapAvatar = BitmapFactory.decodeStream(inputStream);
				}
				catch (Exception e) {}
				return bitmapAvatar;
			}
			@Override
			protected void onPostExecute(Bitmap result) {
				ItemDataMain		itemData;
				Drawable	drawable = new BitmapDrawable(result);
				itemData = new ItemDataMain(drawable, m_strUserName);
				m_arrItemData.add(itemData);
				SetMiscMainItemData();
				m_bShowSocialItem = false;
				m_adapterListMain.SetVisibleSocialItem(m_bShowSocialItem);
				m_dlgProgress.hide();
			}
		};
		task.execute();
	}

	//------------------------------------------------------------------------------
	private void SetData() {
		ItemDataMain		itemData;
		Drawable			drawable;

		m_arrItemData	= new ArrayList<ItemDataMain>();

	// {{ Adapter For Main List
		switch (m_iLoginType) {
			case Const.TYPE_LOGIN_FACEBOOK:
				SetMainItemDataWithFacebook();
				break;
			case Const.TYPE_LOGIN_TWITTER:
				SetMainItemDataWithTwitter();
				break;
			case Const.TYPE_LOGIN_GOOGLE:
				SetMainItemDataWithGoogle();
				break;
			default:
				drawable = getResources().getDrawable(R.drawable.img_defaultprofile);
				itemData = new ItemDataMain(drawable,	m_strUserName);			m_arrItemData.add(itemData);
				SetMiscMainItemData();
				m_bShowSocialItem = false;
				m_adapterListMain.SetVisibleSocialItem(m_bShowSocialItem);
				m_dlgProgress.hide();
				break;
		}
	// }} Adapter For Main List
	}

	//------------------------------------------------------------------------------
	private void SetLayout() {
		FrameLayout.LayoutParams	frmLayoutParams;
		Button						button;
		TextView					textView;
		int							iTextViewId;

	// {{ Title Bar
		// {{ Left Button
			button = (Button)findViewById(R.id.ID_BTN_TITLEBAR_MAIN_LEFT);
			button.setOnClickListener(
				new OnClickListener() {
					@Override
					public void onClick(View view) {
						if (m_bIsAnimationing)
							return;
						if (m_iAnimationId == R.animator.animation_half_left) {
							switch (EnvVariable.CurrentMainItem) {
								case Const.ITEM_PLAYLIST:
									if (m_viewPlaylist.IsShownSubPlaylist()) {
										m_viewPlaylist.HideSubPlaylist();
										break;
									}
								case Const.ITEM_PODCAST:
									if (m_viewPodcast.IsShownSubPodcast()) {
										m_viewPodcast.HideSubPodcast();
										break;
									}
								default:
									StartAnimationRight();		break;
							}
						}
						if (m_iAnimationId == R.animator.animation_half_right) {
							StartAnimationLeft();
						}
					}
				}
			);
		// }} Left Button

		// {{ Right Button - Refresh
			button = (Button)findViewById(R.id.ID_BTN_TITLEBAR_MAIN_RIGHT);
			button.setOnClickListener(
				new OnClickListener() {
					@Override
					public void onClick(View view) {
						if (EnvVariable.CurrentMainItem == EnvVariable.CurrentMainItemWithView) {
							m_searchView.setQuery("", false);
							switch (EnvVariable.CurrentMainItemWithView) {
								case Const.ITEM_USER:		m_viewUser.Refresh();		break;
								case Const.ITEM_HOME:		m_viewHome.Refresh();		break;
								case Const.ITEM_PLAYLIST:	m_viewPlaylist.Refresh();	break;
								case Const.ITEM_PODCAST:	m_viewPodcast.Refresh();	break;
								case Const.ITEM_COMEDY:			break;
								case Const.ITEM_NOLLYWOOD:		break;
								case Const.ITEM_SOCIAL:			break;
								case Const.ITEM_FACEBOOK:		break;
								case Const.ITEM_TWITTER:		break;
								case Const.ITEM_CHAT:			break;
								case Const.ITEM_RADIO:			break;
								case Const.ITEM_VIDEO:			break;
								case Const.ITEM_PHOTO:			break;
								case Const.ITEM_EXIT:			break;
								default:						break;
							}
						}
					}
				}
			);
		// }} Right Button - Refresh

		// {{ SearchView
			m_searchView = (SearchView)findViewById(R.id.ID_SEARCHVIEW_TITLEBAR_MAIN);
			m_searchView.setOnQueryTextListener(
				new OnQueryTextListener() {
					@Override
					public boolean onQueryTextSubmit(String query) {
						if (query.isEmpty())
							return true;
						if (EnvVariable.CurrentMainItem == EnvVariable.CurrentMainItemWithView) {
							switch (EnvVariable.CurrentMainItemWithView) {
								case Const.ITEM_USER:		m_viewUser.SetFindText(query);		break;
								case Const.ITEM_HOME:		m_viewHome.SetFindText(query);		break;
								case Const.ITEM_PLAYLIST:	m_viewPlaylist.SetFindText(query);	break;
								case Const.ITEM_PODCAST:	m_viewPodcast.SetFindText(query);	break;
								case Const.ITEM_COMEDY:			break;
								case Const.ITEM_NOLLYWOOD:		break;
								case Const.ITEM_SOCIAL:			break;
								case Const.ITEM_FACEBOOK:		break;
								case Const.ITEM_TWITTER:		break;
								case Const.ITEM_CHAT:			break;
								case Const.ITEM_RADIO:			break;
								case Const.ITEM_VIDEO:			break;
								case Const.ITEM_PHOTO:			break;
								case Const.ITEM_EXIT:			break;
								default:						break;
							}
						}
						return true;
					}
					@Override
					public boolean onQueryTextChange(String newText) {
						return true;
					}
				}
			);
			iTextViewId = m_searchView.getContext().getResources().getIdentifier("android:id/search_src_text",  null,  null);
			textView = (TextView)m_searchView.findViewById(iTextViewId);
			textView.setTextColor(Color.WHITE);
		// }} SearchView
	// }} Title Bar

		if (EnvVariable.SizeDisplay == null)
			EnvVariable.SizeDisplay = UtilDisplay.GetScreenSize(this);

	// {{ Main ListView
		frmLayoutParams = (FrameLayout.LayoutParams)m_frmLytContent.getLayoutParams();
		if (frmLayoutParams != null) {
			frmLayoutParams.setMargins(0, frmLayoutParams.topMargin, (int)((-Const.RATIO_WIDTH_MAINLIST) * EnvVariable.SizeDisplay.x), 0);			// Width of m_frmlytContent = (1 + Const.RATIO_WIDTH_MAINLIST) * Display.width
			m_frmLytContent.setLayoutParams(frmLayoutParams);
			frmLayoutParams = (FrameLayout.LayoutParams)m_lstViewMain.getLayoutParams();
			frmLayoutParams.setMargins(0, 0, (int)(EnvVariable.SizeDisplay.x), 0);					// Width of m_lstviewMain =  Display.width * Const.RATIO_WIDTH_MAINLIST
			m_lstViewMain.setLayoutParams(frmLayoutParams);
		}
	// }} Main ListView

	// {{ Main ImageView Background Behind View
		if (m_imgViewMain == null) {
			m_imgViewMain = new ImageView(this);
			m_imgViewMain.setBackgroundResource(R.drawable.img_bkg_main);
			frmLayoutParams = new FrameLayout.LayoutParams(
					FrameLayout.LayoutParams.FILL_PARENT,
					FrameLayout.LayoutParams.FILL_PARENT);
			frmLayoutParams.setMargins((int)(Const.RATIO_WIDTH_MAINLIST * EnvVariable.SizeDisplay.x), 0, 0, 0);
			m_imgViewMain.setLayoutParams(frmLayoutParams);
			m_frmLytContent.addView(m_imgViewMain);
		}
	// }} Main ImageView Background Behind View

	// {{ Views
		m_viewUser.SetLayout(m_frmLytContent);
		m_viewHome.SetLayout(m_frmLytContent);
		m_viewPlaylist.SetLayout(m_frmLytContent);
		m_viewPodcast.SetLayout(m_frmLytContent);
		m_viewComedy.SetLayout(m_frmLytContent);
		m_viewNollywood.SetLayout(m_frmLytContent);
		m_viewFacebook.SetLayout(m_frmLytContent);
		m_viewTwitter.SetLayout(m_frmLytContent);
		m_viewUser.Show();
	// }} Views
	}

	//------------------------------------------------------------------------------
	@Override
	public void onDestroy() {
		m_timer.cancel();
		super.onDestroy();
	}

	//------------------------------------------------------------------------------
	private void HideCurrentSubView() {
		switch (EnvVariable.CurrentMainItemWithView) {
			case Const.ITEM_USER:			m_viewUser.Hide();		break;
			case Const.ITEM_HOME:			m_viewHome.Hide();		break;
			case Const.ITEM_PLAYLIST:		m_viewPlaylist.Hide();	break;
			case Const.ITEM_PODCAST:		m_viewPodcast.Hide();	break;
			case Const.ITEM_VIDEO:									break;
			case Const.ITEM_COMEDY:			m_viewComedy.Hide();	break;
			case Const.ITEM_NOLLYWOOD:		m_viewNollywood.Hide();	break;
			case Const.ITEM_PHOTO:									break;
			case Const.ITEM_SOCIAL:									break;
			case Const.ITEM_CHAT:									break;
			default:	break;
		}
	}

	//------------------------------------------------------------------------------
	private void ShowSubView(int a_iPosition) {
		if (EnvVariable.CurrentMainItemWithView == a_iPosition)
			return;

		switch (EnvVariable.CurrentMainItemWithView) {
			case Const.ITEM_USER:			m_viewUser.Hide();		break;
			case Const.ITEM_HOME:			m_viewHome.Hide();		break;
			case Const.ITEM_PLAYLIST:		m_viewPlaylist.Hide();	break;
			case Const.ITEM_PODCAST:		m_viewPodcast.Hide();	break;
			case Const.ITEM_VIDEO:									break;
			case Const.ITEM_COMEDY:			m_viewComedy.Hide();	break;
			case Const.ITEM_NOLLYWOOD:		m_viewNollywood.Hide();	break;
			case Const.ITEM_PHOTO:									break;
			case Const.ITEM_SOCIAL:									break;
			case Const.ITEM_FACEBOOK:		m_viewFacebook.Hide();	break;
			case Const.ITEM_TWITTER:		m_viewTwitter.Hide();	break;
			case Const.ITEM_CHAT:									break;
			default:	break;
		}

		switch (a_iPosition) {
			case Const.ITEM_USER:			m_viewUser.Show();	break;
			case Const.ITEM_HOME:			m_viewHome.Show();		break;
			case Const.ITEM_PLAYLIST:		m_viewPlaylist.Show();	break;
			case Const.ITEM_PODCAST:		m_viewPodcast.Show();	break;
			case Const.ITEM_VIDEO:									break;
			case Const.ITEM_COMEDY:			m_viewComedy.Show();	break;
			case Const.ITEM_NOLLYWOOD:		m_viewNollywood.Show();	break;
			case Const.ITEM_PHOTO:									break;
			case Const.ITEM_SOCIAL:									break;
			case Const.ITEM_FACEBOOK:		m_viewFacebook.Show();	break;
			case Const.ITEM_TWITTER:		m_viewTwitter.Show();	break;
			case Const.ITEM_CHAT:									break;
			case Const.ITEM_EXIT:									break;
			default:	break;
		}

		EnvVariable.CurrentMainItem				= a_iPosition;
		EnvVariable.CurrentMainItemWithView		= a_iPosition;
		m_lstViewMain.invalidateViews();
	}

	//------------------------------------------------------------------------------
	private void StartAnimationLeft() {
		Animation		animation;

		if (m_bIsAnimationing)		return;

		animation = AnimationUtils.loadAnimation(ActivityMain.this, R.animator.animation_half_left);
		animation.setAnimationListener(m_listenerAnimation);

		m_iCountTime		= (-1) * DELAY_READY;
		m_iAnimationId		= R.animator.animation_half_left;
		m_bIsAnimationing	= true;
		m_frmLytContent.startAnimation(animation);
	}

	//------------------------------------------------------------------------------
	private void StartAnimationRight() {
		Animation		animation;

		if (m_bIsAnimationing)		return;

		animation = AnimationUtils.loadAnimation(ActivityMain.this, R.animator.animation_half_right);
		animation.setAnimationListener(m_listenerAnimation);

		m_iCountTime		= (-1) * DELAY_READY;
		m_iAnimationId		= R.animator.animation_half_right;
		m_bIsAnimationing	= true;
		m_frmLytContent.startAnimation(animation);
	}

	//------------------------------------------------------------------------------
	public void ShowActivityRadio() {
		Intent		intentMain2Radio;
		Bundle		bundle;
		if (m_bIsAnimationing)		return;
		EnvVariable.CurrentMainItem = Const.ITEM_RADIO;
		m_lstViewMain.invalidateViews();
		intentMain2Radio = new Intent(ActivityMain.this, ActivityRadio.class);
		bundle = new Bundle();
		bundle.putInt(Const.KEY_LOGINTYPE,	Const.TYPE_LOGIN_NORMAL);
		intentMain2Radio.putExtras(bundle);
		startActivity(intentMain2Radio);
	}

	//------------------------------------------------------------------------------
	public void ShowActivityVideo() {
		Intent		intentMain2Video;
		if (m_bIsAnimationing)		return;
		EnvVariable.CurrentMainItem = Const.ITEM_VIDEO;
		m_lstViewMain.invalidateViews();
		intentMain2Video = new Intent(ActivityMain.this, ActivityVideo.class);
		startActivity(intentMain2Video);
	}

	//------------------------------------------------------------------------------
	public void ShowActivityPhoto() {
		Intent		intentMain2Photo;
		if (m_bIsAnimationing)		return;
		EnvVariable.CurrentMainItem = Const.ITEM_PHOTO;
		m_lstViewMain.invalidateViews();
		intentMain2Photo = new Intent(ActivityMain.this, ActivityPhoto.class);
		startActivity(intentMain2Photo);
	}

	//------------------------------------------------------------------------------
	public void ShowActivityChat() {
		Intent		intentMain2Chat;
		EnvVariable.CurrentMainItem = Const.ITEM_CHAT;
		m_lstViewMain.invalidateViews();
		intentMain2Chat = new Intent(ActivityMain.this, ActivityChatLogin.class);
		startActivity(intentMain2Chat);
	}

	//------------------------------------------------------------------------------
	public void ShowHideSocialItem() {
		EnvVariable.CurrentMainItem = Const.ITEM_SOCIAL;
		if (m_bShowSocialItem)		m_bShowSocialItem = false;
		else						m_bShowSocialItem = true;
		m_adapterListMain.SetVisibleSocialItem(m_bShowSocialItem);
		m_lstViewMain.invalidateViews();
	}

	//------------------------------------------------------------------------------
}

//==============================================================================
