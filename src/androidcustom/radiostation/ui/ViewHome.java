package androidcustom.radiostation.ui;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.CountDownTimer;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidcustom.radiostation.R;
import androidcustom.radiostation.global.Const;
import androidcustom.radiostation.global.EnvVariable;
import androidcustom.radiostation.global.UtilDisplay;
import androidcustom.radiostation.http.HttpApi;
import androidcustom.radiostation.http.HttpApi.HttpApiListener;
import androidcustom.radiostation.loader.LoaderImage;
import androidcustom.radiostation.multimedia.MultimediaInfo;
import androidcustom.radiostation.ui.AdapterBaseList.ItemContentClickListener;

//==============================================================================
public class ViewHome {

	private Context			m_context;

	private	RelativeLayout		m_relLytContent;
	private	LinearLayout		m_linLytHeader;

	private	FrameLayout			m_frmLytTrend;
	private	FrameLayout			m_frmLytTop10;
	private	FrameLayout			m_frmLytStaff;
	private	FrameLayout			m_frmLytClass;

	private static String		TAG_TREND		= "Trending";
	private static String		TAG_TOP10		= "Top10";
	private static String		TAG_STAFF		= "StaffPick";
	private static String		TAG_CLASS		= "Classic";

	private	ImageView			m_imgViewTrend;
	private	ImageView			m_imgViewTop10;
	private	ImageView			m_imgViewStaff;
	private	ImageView			m_imgViewClass;

	private	TextView			m_txtViewTrend;
	private	TextView			m_txtViewTop10;
	private	TextView			m_txtViewStaff;
	private	TextView			m_txtViewClass;

	private	ListView		m_listViewTrend;
	private	ListView		m_listViewTop10;
	private	ListView		m_listViewStaff;
	private	ListView		m_listViewClass;
	
	private AdapterHome			m_adapterTrend;
	private	AdapterHome			m_adapterTop10;
	private	AdapterHome			m_adapterStaff;
	private	AdapterHome			m_adapterClass;

	private HttpApi				m_httpApiTrend;
	private HttpApi				m_httpApiTop10;
	private HttpApi				m_httpApiStaff;
	private HttpApi				m_httpApiClass;

	private int			m_iCurTabIndex;
	private	boolean		m_bIsShown;

	private	CountDownTimer 		m_timer;

	private	static	final	int		ID_LINLYT_HEADER	=	1;

	//------------------------------------------------------------------------------
	public ViewHome(Context a_context) {

		m_context = a_context;

		m_bIsShown		= false;
		m_iCurTabIndex	= 0;

		m_httpApiTrend = null;
		m_httpApiTop10 = null;
		m_httpApiStaff = null;
		m_httpApiClass = null;

		CreateLayout();

	// {{ ImageView Trend
		m_imgViewTrend.setOnClickListener(
			new OnClickListener() {
				@Override
				public void onClick(View v) {
					if (LoaderImage.GetInstance() != null)
						LoaderImage.GetInstance().ClearCache();
					m_iCurTabIndex = 0;
					m_imgViewTrend.setBackgroundResource(R.drawable.img_tab_home_clicked);
					m_imgViewTop10.setBackgroundResource(R.drawable.img_tab_home);
					m_imgViewStaff.setBackgroundResource(R.drawable.img_tab_home);
					m_imgViewClass.setBackgroundResource(R.drawable.img_tab_home);
					m_listViewTrend.setVisibility(View.VISIBLE);
					m_listViewTop10.setVisibility(View.INVISIBLE);
					m_listViewStaff.setVisibility(View.INVISIBLE);
					m_listViewClass.setVisibility(View.INVISIBLE);
					if (m_httpApiTrend == null) {
						m_httpApiTrend = new HttpApi();
						m_httpApiTrend.SetHttpApiListener(
							new HttpApiListener() {
								@Override
								public void OnHttpApiResult(String a_strResult, int a_iType) {
									AnalyzeJsonToAdapter(a_strResult, m_adapterTrend, a_iType);
								}
							}
						);
						m_httpApiTrend.SetApiType(Const.TYPE_HOME_TREND);
						m_httpApiTrend.StartGetHomeList(TAG_TREND);
					}
				}
			}
		);
	// }} ImageView Trend

	// {{ ImageView Top10
		m_imgViewTop10.setOnClickListener(
			new OnClickListener() {
				@Override
				public void onClick(View v) {
					if (LoaderImage.GetInstance() != null)
						LoaderImage.GetInstance().ClearCache();
					m_iCurTabIndex = 1;
					m_imgViewTrend.setBackgroundResource(R.drawable.img_tab_home);
					m_imgViewTop10.setBackgroundResource(R.drawable.img_tab_home_clicked);
					m_imgViewStaff.setBackgroundResource(R.drawable.img_tab_home);
					m_imgViewClass.setBackgroundResource(R.drawable.img_tab_home);
					m_listViewTrend.setVisibility(View.INVISIBLE);
					m_listViewTop10.setVisibility(View.VISIBLE);
					m_listViewStaff.setVisibility(View.INVISIBLE);
					m_listViewClass.setVisibility(View.INVISIBLE);
					if (m_httpApiTop10 == null) {
						m_httpApiTop10 = new HttpApi();
						m_httpApiTop10.SetHttpApiListener(
							new HttpApiListener() {
								@Override
								public void OnHttpApiResult(String a_strResult, int a_iType) {
									AnalyzeJsonToAdapter(a_strResult, m_adapterTop10, a_iType);
								}
							}
						);
						m_httpApiTop10.SetApiType(Const.TYPE_HOME_TOP10);
						m_httpApiTop10.StartGetHomeList(TAG_TOP10);
					}
				}
			}
		);
	// }} ImageView Top10

	// {{ ImageView Staff
		m_imgViewStaff.setOnClickListener(
			new OnClickListener() {
				@Override
				public void onClick(View v) {
					if (LoaderImage.GetInstance() != null)
						LoaderImage.GetInstance().ClearCache();
					m_iCurTabIndex = 2;
					m_imgViewTrend.setBackgroundResource(R.drawable.img_tab_home);
					m_imgViewTop10.setBackgroundResource(R.drawable.img_tab_home);
					m_imgViewStaff.setBackgroundResource(R.drawable.img_tab_home_clicked);
					m_imgViewClass.setBackgroundResource(R.drawable.img_tab_home);
					m_listViewTrend.setVisibility(View.INVISIBLE);
					m_listViewTop10.setVisibility(View.INVISIBLE);
					m_listViewStaff.setVisibility(View.VISIBLE);
					m_listViewClass.setVisibility(View.INVISIBLE);
					if (m_httpApiStaff == null) {
						m_httpApiStaff = new HttpApi();
						m_httpApiStaff.SetHttpApiListener(
							new HttpApiListener() {
								@Override
								public void OnHttpApiResult(String a_strResult, int a_iType) {
									AnalyzeJsonToAdapter(a_strResult, m_adapterStaff, a_iType);
								}
							}
						);
						m_httpApiStaff.SetApiType(Const.TYPE_HOME_STAFF);
						m_httpApiStaff.StartGetHomeList(TAG_STAFF);
					}
				}
			}
		);
	// }} ImageView Staff

	// {{ ImageView Class
		m_imgViewClass.setOnClickListener(
			new OnClickListener() {
				@Override
				public void onClick(View v) {
					if (LoaderImage.GetInstance() != null)
						LoaderImage.GetInstance().ClearCache();
					m_iCurTabIndex = 3;
					m_imgViewTrend.setBackgroundResource(R.drawable.img_tab_home);
					m_imgViewTop10.setBackgroundResource(R.drawable.img_tab_home);
					m_imgViewStaff.setBackgroundResource(R.drawable.img_tab_home);
					m_imgViewClass.setBackgroundResource(R.drawable.img_tab_home_clicked);
					m_listViewTrend.setVisibility(View.INVISIBLE);
					m_listViewTop10.setVisibility(View.INVISIBLE);
					m_listViewStaff.setVisibility(View.INVISIBLE);
					m_listViewClass.setVisibility(View.VISIBLE);
					if (m_httpApiClass == null) {
						m_httpApiClass = new HttpApi();
						m_httpApiClass.SetHttpApiListener(
							new HttpApiListener() {
								@Override
								public void OnHttpApiResult(String a_strResult, int a_iType) {
									AnalyzeJsonToAdapter(a_strResult, m_adapterClass, a_iType);
								}
							}
						);
						m_httpApiClass.SetApiType(Const.TYPE_HOME_CLASS);
						m_httpApiClass.StartGetHomeList(TAG_CLASS);
					}
				}
			}
		);
	// }} ImageView Class

	// {{ ListView Trend
		/*
		m_listViewTrend.setOnItemClickListener(
			new OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> adapter, View v, int position, long arg3) {
					ShowActivityPlayAudio(Const.TYPE_HOME_TREND, position);
				}
			}
		);
		*/
		m_adapterTrend = new AdapterHome(m_context, m_listViewTrend);
		m_listViewTrend.setAdapter(m_adapterTrend);

		m_adapterTrend.SetItemContentClickListener(
			new ItemContentClickListener() {
				@Override
				public void OnItemContentClicked(int a_iPosition) {
					ShowActivityPlayAudio(Const.TYPE_HOME_TREND, a_iPosition);
				}
			}
		);
	// }} ListView Trend

	// {{ ListView Top10
		/*
		m_listViewTop10.setOnItemClickListener(
			new OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> adapter, View v, int position, long arg3) {
					ShowActivityPlayAudio(Const.TYPE_HOME_TOP10, position);
				}
			}
		);
		*/
		m_adapterTop10 = new AdapterHome(m_context, m_listViewTop10);
		m_listViewTop10.setAdapter(m_adapterTop10);

		m_adapterTop10.SetItemContentClickListener(
			new ItemContentClickListener() {
				@Override
				public void OnItemContentClicked(int a_iPosition) {
					ShowActivityPlayAudio(Const.TYPE_HOME_TOP10, a_iPosition);
				}
			}
		);
	// {{ ListView Top10

	// {{ ListView Staff
		/*
		m_listViewStaff.setOnItemClickListener(
			new OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> adapter, View v, int position, long arg3) {
					ShowActivityPlayAudio(Const.TYPE_HOME_STAFF, position);
				}
			}
		);
		*/
		m_adapterStaff = new AdapterHome(m_context, m_listViewStaff);
		m_listViewStaff.setAdapter(m_adapterStaff);

		m_adapterStaff.SetItemContentClickListener(
			new ItemContentClickListener() {
				@Override
				public void OnItemContentClicked(int a_iPosition) {
					ShowActivityPlayAudio(Const.TYPE_HOME_STAFF, a_iPosition);
				}
			}
		);
	// {{ ListView Staff

	// {{ ListView Class
		/*
		m_listViewClass.setOnItemClickListener(
			new OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> adapter, View v, int position, long arg3) {
					ShowActivityPlayAudio(Const.TYPE_HOME_CLASS, position);
				}
			}
		);
		*/
		m_adapterClass = new AdapterHome(m_context, m_listViewClass);
		m_listViewClass.setAdapter(m_adapterClass);

		m_adapterClass.SetItemContentClickListener(
			new ItemContentClickListener() {
				@Override
				public void OnItemContentClicked(int a_iPosition) {
					ShowActivityPlayAudio(Const.TYPE_HOME_CLASS, a_iPosition);
				}
			}
		);
	// }} ListView Class

	// {{ Refresh ListViews Periodically
		m_timer = new CountDownTimer(Const.DELAY_INVALIDATE, 100) {
			@Override
			public void onFinish() {
				if (m_bIsShown && EnvVariable.CurrentMainItem == Const.ITEM_HOME) {
					switch (m_iCurTabIndex) {
					case 0:		m_listViewTrend.invalidateViews();		break;
					case 1:		m_listViewTop10.invalidateViews();		break;
					case 2:		m_listViewStaff.invalidateViews();		break;
					case 3:		m_listViewClass.invalidateViews();		break;
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
	private void CreateLayout() {
		RelativeLayout.LayoutParams		relLytParams;
		LinearLayout.LayoutParams		linLytParams;
		FrameLayout.LayoutParams		frmLytParams;

		int		iTabWidth		= UtilDisplay.GetScreenSize(m_context).x / 4;
		int		iTabHeight		= (int)(50 * m_context.getResources().getDisplayMetrics().density);
		float	fFontSize		= 20;

		m_relLytContent	= new RelativeLayout(m_context);
		m_linLytHeader	= new LinearLayout(m_context);
		m_linLytHeader.setId(ID_LINLYT_HEADER);

		m_imgViewTrend	= new ImageView(m_context);
		m_imgViewTop10	= new ImageView(m_context);
		m_imgViewStaff	= new ImageView(m_context);
		m_imgViewClass	= new ImageView(m_context);

		m_txtViewTrend	= new TextView(m_context);
		m_txtViewTop10	= new TextView(m_context);
		m_txtViewStaff	= new TextView(m_context);
		m_txtViewClass	= new TextView(m_context);

		m_listViewTrend	= new ListView(m_context);
		m_listViewTop10	= new ListView(m_context);
		m_listViewStaff	= new ListView(m_context);
		m_listViewClass	= new ListView(m_context);

		m_txtViewTrend.setTextSize(fFontSize);
		m_txtViewTop10.setTextSize(fFontSize);
		m_txtViewStaff.setTextSize(fFontSize);
		m_txtViewClass.setTextSize(fFontSize);

		m_txtViewTrend.setText("Trending");
		m_txtViewTop10.setText("Top 10");
		m_txtViewStaff.setText("Staff Pick");
		m_txtViewClass.setText("Classic");

		m_txtViewTrend.setTextColor(Color.WHITE);
		m_txtViewTop10.setTextColor(Color.WHITE);
		m_txtViewStaff.setTextColor(Color.WHITE);
		m_txtViewClass.setTextColor(Color.WHITE);

		m_txtViewTrend.setGravity(Gravity.CENTER);
		m_txtViewTop10.setGravity(Gravity.CENTER);
		m_txtViewStaff.setGravity(Gravity.CENTER);
		m_txtViewClass.setGravity(Gravity.CENTER);



	// {{ FrameLayout Trend
		m_frmLytTrend	= new FrameLayout(m_context);
		linLytParams	= new LinearLayout.LayoutParams(
			LinearLayout.LayoutParams.WRAP_CONTENT,
			LinearLayout.LayoutParams.WRAP_CONTENT
		);
		linLytParams.width	= iTabWidth;
		linLytParams.height	= iTabHeight;
		m_frmLytTrend.setLayoutParams(linLytParams);
	// }} FrameLayout Trend

	// {{ FrameLayout Top10
		m_frmLytTop10	= new FrameLayout(m_context);
		linLytParams	= new LinearLayout.LayoutParams(
			LinearLayout.LayoutParams.WRAP_CONTENT,
			LinearLayout.LayoutParams.WRAP_CONTENT
		);
		linLytParams.width	= iTabWidth;
		linLytParams.height	= iTabHeight;
		m_frmLytTop10.setLayoutParams(linLytParams);
	// }} FrameLayout Top10

	// {{ FrameLayout Staff
		m_frmLytStaff	= new FrameLayout(m_context);
		linLytParams	= new LinearLayout.LayoutParams(
			LinearLayout.LayoutParams.WRAP_CONTENT,
			LinearLayout.LayoutParams.WRAP_CONTENT
		);
		linLytParams.width	= iTabWidth;
		linLytParams.height	= iTabHeight;
		m_frmLytStaff.setLayoutParams(linLytParams);
	// }} FrameLayout Staff

	// {{ FrameLayout Class
		m_frmLytClass	= new FrameLayout(m_context);
		linLytParams	= new LinearLayout.LayoutParams(
			LinearLayout.LayoutParams.WRAP_CONTENT,
			LinearLayout.LayoutParams.WRAP_CONTENT
		);
		linLytParams.width	= iTabWidth;
		linLytParams.height	= iTabHeight;
		m_frmLytClass.setLayoutParams(linLytParams);
	// }} FrameLayout Class



	// {{ ImageView Trend
		frmLytParams	= new FrameLayout.LayoutParams(
			FrameLayout.LayoutParams.FILL_PARENT,
			FrameLayout.LayoutParams.FILL_PARENT
		);
		m_imgViewTrend.setLayoutParams(frmLytParams);
	// }} ImageView Trend

	// {{ ImageView Top10
		frmLytParams	= new FrameLayout.LayoutParams(
			FrameLayout.LayoutParams.FILL_PARENT,
			FrameLayout.LayoutParams.FILL_PARENT
		);
		m_imgViewTop10.setLayoutParams(frmLytParams);
	// }} ImageView Top10

	// {{ ImageView Staff
		frmLytParams	= new FrameLayout.LayoutParams(
			FrameLayout.LayoutParams.FILL_PARENT,
			FrameLayout.LayoutParams.FILL_PARENT
		);
		m_imgViewStaff.setLayoutParams(frmLytParams);
	// }} ImageView Staff

	// {{ ImageView Class
		frmLytParams	= new FrameLayout.LayoutParams(
			FrameLayout.LayoutParams.FILL_PARENT,
			FrameLayout.LayoutParams.FILL_PARENT
		);
		m_imgViewClass.setLayoutParams(frmLytParams);
	// }} ImageView Class



	// {{ TextView Trend
		frmLytParams	= new FrameLayout.LayoutParams(
			FrameLayout.LayoutParams.MATCH_PARENT,
			FrameLayout.LayoutParams.FILL_PARENT
		);
		m_txtViewTrend.setLayoutParams(frmLytParams);
	// }} TextView Trend

	// {{ TextView Top10
		frmLytParams	= new FrameLayout.LayoutParams(
			FrameLayout.LayoutParams.MATCH_PARENT,
			FrameLayout.LayoutParams.FILL_PARENT
		);
		m_txtViewTop10.setLayoutParams(frmLytParams);
	// }} TextView Top10

	// {{ TextView Staff
		frmLytParams	= new FrameLayout.LayoutParams(
			FrameLayout.LayoutParams.MATCH_PARENT,
			FrameLayout.LayoutParams.FILL_PARENT
		);
		m_txtViewStaff.setLayoutParams(frmLytParams);
	// }} TextView Staff

	// {{ TextView Class
		frmLytParams	= new FrameLayout.LayoutParams(
			FrameLayout.LayoutParams.MATCH_PARENT,
			FrameLayout.LayoutParams.FILL_PARENT
		);
		m_txtViewClass.setLayoutParams(frmLytParams);
	// }} TextView Class

		m_frmLytTrend.addView(m_imgViewTrend);
		m_frmLytTrend.addView(m_txtViewTrend);
		m_frmLytTop10.addView(m_imgViewTop10);
		m_frmLytTop10.addView(m_txtViewTop10);
		m_frmLytStaff.addView(m_imgViewStaff);
		m_frmLytStaff.addView(m_txtViewStaff);
		m_frmLytClass.addView(m_imgViewClass);
		m_frmLytClass.addView(m_txtViewClass);

		m_linLytHeader.addView(m_frmLytTrend);
		m_linLytHeader.addView(m_frmLytTop10);
		m_linLytHeader.addView(m_frmLytStaff);
		m_linLytHeader.addView(m_frmLytClass);

	// {{ LinearLayout Header
		relLytParams	= new RelativeLayout.LayoutParams(
			RelativeLayout.LayoutParams.FILL_PARENT,
			RelativeLayout.LayoutParams.WRAP_CONTENT
		);
		m_linLytHeader.setLayoutParams(relLytParams);
		m_relLytContent.addView(m_linLytHeader);
	// }} LinearLayout Header

	// {{ ListView Trend
		relLytParams	= new RelativeLayout.LayoutParams(
			RelativeLayout.LayoutParams.MATCH_PARENT,
			RelativeLayout.LayoutParams.MATCH_PARENT
		);
		relLytParams.addRule(RelativeLayout.BELOW, ID_LINLYT_HEADER);
		m_listViewTrend.setLayoutParams(relLytParams);
	// }} ListView Trend

	// {{ ListView Top10
		relLytParams	= new RelativeLayout.LayoutParams(
			RelativeLayout.LayoutParams.MATCH_PARENT,
			RelativeLayout.LayoutParams.MATCH_PARENT
		);
		relLytParams.addRule(RelativeLayout.BELOW, ID_LINLYT_HEADER);
		m_listViewTop10.setLayoutParams(relLytParams);
	// }} ListView Top10

	// {{ ListView Staff
		relLytParams	= new RelativeLayout.LayoutParams(
			RelativeLayout.LayoutParams.MATCH_PARENT,
			RelativeLayout.LayoutParams.MATCH_PARENT
		);
		relLytParams.addRule(RelativeLayout.BELOW, ID_LINLYT_HEADER);
		m_listViewStaff.setLayoutParams(relLytParams);
	// }} ListView Staff

	// {{ ListView Class
		relLytParams	= new RelativeLayout.LayoutParams(
			RelativeLayout.LayoutParams.MATCH_PARENT,
			RelativeLayout.LayoutParams.MATCH_PARENT
		);
		relLytParams.addRule(RelativeLayout.BELOW, ID_LINLYT_HEADER);
		m_listViewClass.setLayoutParams(relLytParams);
	// }} ListView Class

		m_relLytContent.addView(m_listViewTrend);
		m_relLytContent.addView(m_listViewTop10);
		m_relLytContent.addView(m_listViewStaff);
		m_relLytContent.addView(m_listViewClass);

		m_imgViewTrend.setBackgroundResource(R.drawable.img_tab_home);
		m_imgViewTop10.setBackgroundResource(R.drawable.img_tab_home);
		m_imgViewStaff.setBackgroundResource(R.drawable.img_tab_home);
		m_imgViewClass.setBackgroundResource(R.drawable.img_tab_home);

		m_listViewTrend.setVisibility(View.INVISIBLE);
		m_listViewTop10.setVisibility(View.INVISIBLE);
		m_listViewStaff.setVisibility(View.INVISIBLE);
		m_listViewClass.setVisibility(View.INVISIBLE);
	}

	//------------------------------------------------------------------------------
	public void SetLayout(FrameLayout a_frmlytParent) {
		FrameLayout.LayoutParams	frmLayoutParams;

		a_frmlytParent.addView(m_relLytContent);

		frmLayoutParams = new FrameLayout.LayoutParams(
			FrameLayout.LayoutParams.FILL_PARENT,
			FrameLayout.LayoutParams.FILL_PARENT
		);
		frmLayoutParams.setMargins((int)((1 +  Const.RATIO_WIDTH_MAINLIST) * EnvVariable.SizeDisplay.x), 0,
										(-1) * EnvVariable.SizeDisplay.x, 0);
		m_relLytContent.setLayoutParams(frmLayoutParams);
	}

	//------------------------------------------------------------------------------
	public View GetContentView() {
		return m_relLytContent;
	}

	//------------------------------------------------------------------------------
	public void Show() {
		FrameLayout.LayoutParams	frmLayoutParams;

		if (LoaderImage.GetInstance() != null)
			LoaderImage.GetInstance().ClearCache();

		frmLayoutParams = (FrameLayout.LayoutParams)m_relLytContent.getLayoutParams();
		frmLayoutParams.setMargins((int)(Const.RATIO_WIDTH_MAINLIST * EnvVariable.SizeDisplay.x), 0, 0, 0);
		m_relLytContent.setLayoutParams(frmLayoutParams);

		m_bIsShown = true;
		switch (m_iCurTabIndex) {
		case 0:		m_imgViewTrend.performClick();		break;
		case 1:		m_imgViewTop10.performClick();		break;
		case 2:		m_imgViewStaff.performClick();		break;
		case 3:		m_imgViewClass.performClick();		break;
		default:	break;
		}
		m_timer.start();
	}

	//------------------------------------------------------------------------------
	public void Hide() {
		FrameLayout.LayoutParams	frmLayoutParams;

		if (LoaderImage.GetInstance() != null)
			LoaderImage.GetInstance().ClearCache();

		frmLayoutParams = (FrameLayout.LayoutParams)m_relLytContent.getLayoutParams();
		frmLayoutParams.setMargins((int)((1 +  Const.RATIO_WIDTH_MAINLIST) * EnvVariable.SizeDisplay.x), 0,
									(-1) * EnvVariable.SizeDisplay.x, 0);
		m_relLytContent.setLayoutParams(frmLayoutParams);
		
		m_bIsShown = false;
		m_timer.cancel();
	}

	//------------------------------------------------------------------------------
	public void ShowActivityPlayAudio(int a_iType, int a_iIndex) {
		Intent		intentHome2Play;

		intentHome2Play = new Intent(m_context, ActivityPlayAudio.class);		// ActivityMain -> ActivityPlay
		intentHome2Play.putExtra(Const.KEY_INDEX,	a_iIndex);
		switch (a_iType) {
			case Const.TYPE_HOME_TREND:		EnvVariable.CurrentAdapter = m_adapterTrend;	break;
			case Const.TYPE_HOME_TOP10:		EnvVariable.CurrentAdapter = m_adapterTop10;	break;
			case Const.TYPE_HOME_STAFF:		EnvVariable.CurrentAdapter = m_adapterStaff;	break;
			case Const.TYPE_HOME_CLASS:		EnvVariable.CurrentAdapter = m_adapterClass;	break;
			default :	return;
		}
		m_context.startActivity(intentHome2Play);
	}

	//------------------------------------------------------------------------------
	public void SetFindText(String a_strFindText) {
		switch (m_iCurTabIndex) {
		case 0:		m_adapterTrend.SetFindText(a_strFindText);		m_listViewTrend.invalidateViews();	break;
		case 1:		m_adapterTop10.SetFindText(a_strFindText);		m_listViewTop10.invalidateViews();	break;
		case 2:		m_adapterStaff.SetFindText(a_strFindText);		m_listViewStaff.invalidateViews();	break;
		case 3:		m_adapterClass.SetFindText(a_strFindText);		m_listViewClass.invalidateViews();	break;
		default:	break;
		}
	}

	//------------------------------------------------------------------------------
	public void Refresh() {
		switch (m_iCurTabIndex) {
		case 0:		m_adapterTrend.SetFindText("");		m_listViewTrend.invalidateViews();	break;
		case 1:		m_adapterTop10.SetFindText("");		m_listViewTop10.invalidateViews();	break;
		case 2:		m_adapterStaff.SetFindText("");		m_listViewStaff.invalidateViews();	break;
		case 3:		m_adapterClass.SetFindText("");		m_listViewClass.invalidateViews();	break;
		default:	break;
		}
	}

	//------------------------------------------------------------------------------
	public void AnalyzeJsonToAdapter(String a_strJson, AdapterHome a_adapter, int a_iType) {
		JSONObject			jsonObj;
		JSONArray			jsonArrSongList;
		int					iIndex;

		if (a_strJson == null)
			return;

		if (a_strJson.length() == 0)
			return;

		try {
			jsonObj = new JSONObject(a_strJson);
			jsonArrSongList = jsonObj.getJSONArray("songlist");
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

				a_adapter.AddMultimediaInfo(multimediaInfo);
			}
		}
		catch (JSONException e1) {}
		catch (Exception e) {}
	}

	//------------------------------------------------------------------------------
}

//==============================================================================
