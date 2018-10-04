package androidcustom.radiostation.ui;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebChromeClient;
import android.webkit.WebViewClient;
import android.webkit.WebSettings.PluginState;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.TextView;
import androidcustom.radiostation.R;
import androidcustom.radiostation.db.DbApi;
import androidcustom.radiostation.global.Const;
import androidcustom.radiostation.global.EnvVariable;
import androidcustom.radiostation.youtube.UtilYouTube;

//==============================================================================
public class ActivityPlayVideo extends Activity {

	private	int			m_iIndex;
	private String		m_strTitle;
	private String		m_strArtist;
	private	String		m_strViews;
	private String		m_strUrl;
	private	Uri			m_uriVideo;

	private	AdapterBase		m_adapterBase;

	private	WebView				m_webView;
	private	WebChromeClient		m_webChromeClient;
	private	WebViewClient		m_webViewClient;

	// YouTube
	private	String				m_strVideoId = null;

	//------------------------------------------------------------------------------
	public ActivityPlayVideo() {
		m_strTitle		= null;
		m_strArtist		= null;
		m_strViews		= null;
		m_strUrl		= null;
	}

	//------------------------------------------------------------------------------
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_play_video);
		setRequestedOrientation(getResources().getConfiguration().orientation);			// Won't Rotate

		m_iIndex	= getIntent().getIntExtra(Const.KEY_INDEX, -1);
		m_adapterBase	= EnvVariable.CurrentAdapter;
		EnvVariable.CurrentAdapter = null;

		m_strTitle	= m_adapterBase.GetMultimediaInfo(m_iIndex).GetTitle();
		m_strArtist	= m_adapterBase.GetMultimediaInfo(m_iIndex).GetArtist();
		if (m_adapterBase.GetMultimediaInfo(m_iIndex).GetLikeCount() != 0)
			m_strViews	= String.valueOf(m_adapterBase.GetMultimediaInfo(m_iIndex).GetLikeCount()) + " views";
		else
			m_strViews = "";
		m_strUrl	= m_adapterBase.GetMultimediaInfo(m_iIndex).GetPath1();

		SetLayout();

		PlayVideo();
	}

	//------------------------------------------------------------------------------
	private void SetLayout() {
		Button			button;
		TextView		textView;

	// {{ Title Bar
		button = (Button)findViewById(R.id.ID_BTN_TITLEBAR_PLAY_LEFT);
		button.setOnClickListener(
			new OnClickListener() {
				@Override
				public void onClick(View view) {
					ActivityPlayVideo.this.finish();
				}			
			}
		);
		textView = (TextView)findViewById(R.id.ID_TXTVIEW_TITLEBAR_PLAY_TITLE);
		textView.setText("Videos");
	// }} Title Bar
		
	// {{ Title TextView
		textView = (TextView)findViewById(R.id.ID_TXTVIEW_PLAY_VIDEO_TITLE);
		textView.setText(m_strTitle);
	// }} Title TextView

	// {{ Artist TextView
		textView = (TextView)findViewById(R.id.ID_TXTVIEW_PLAY_VIDEO_ARTIST);
		textView.setText(m_strArtist);
	// }} Artist TextView

	// {{ Review TextView
		textView = (TextView)findViewById(R.id.ID_TXTVIEW_PLAY_VIDEO_VIEWS);
		textView.setText(m_strViews);
	// }} Review TextView

	// {{ Share Button
		button = (Button)findViewById(R.id.ID_BTN_PLAY_VIDEO_SHARE);
		button.setOnClickListener(
			new OnClickListener() {
				@Override
				public void onClick(View v) {
					Intent intentShare = new Intent(android.content.Intent.ACTION_SEND);
					intentShare.setType("text/*");
					intentShare.putExtra(Intent.EXTRA_TEXT, m_strUrl);
					startActivity(Intent.createChooser(intentShare, "SHARE"));
				}
			}
		);
	// }} Share Button

	// {{ WebView
		m_webView			= (WebView)findViewById(R.id.ID_WEBVIEW_PLAY_VIDEO);
		m_webChromeClient	= new WebChromeClient();
		m_webViewClient		= new WebViewClient();
		m_webView.getSettings().setJavaScriptEnabled(true);
		m_webView.setWebChromeClient(m_webChromeClient);
		m_webView.setWebViewClient(m_webViewClient);
		m_webView.getSettings().setAllowFileAccess(true);
		m_webView.getSettings().setPluginState(PluginState.ON);
	// }} WebView
	}

	//------------------------------------------------------------------------------
	private void PlayVideo() {
		// YouTube
		String	strVideoId;
		
		EnvVariable.KillCurrentSound();

		DbApi.GetInstance().InsertMultimediaInfoToHistory(m_adapterBase.GetMultimediaInfo(m_iIndex));

		strVideoId = GetYouTubeID(m_strUrl);

		if (strVideoId.length() == 0)
			finish();

		m_uriVideo		= Uri.parse("ytv://" + strVideoId);
		m_strVideoId	= m_uriVideo.getEncodedSchemeSpecificPart();

		if (m_strVideoId.startsWith("//")) {
			if (m_strVideoId.length() > 2)
				m_strVideoId = m_strVideoId.substring(2);
			else
				finish();
		}
		
		m_webView.loadUrl("http://www.youtube.com/embed/" + m_strVideoId + "?fs=1");
/*
		m_webView.loadData(
			GetHtml(
				m_strVideoId,
				UtilDisplay.GetScreenSize(this).x,
				UtilDisplay.GetScreenSize(this).y
			),
			"text/html",
			null
		);   //WebView¿¡ data¼³Á¤
*/
	}

	//------------------------------------------------------------------------------
	public String GetHtml(String a_strVideoId, int a_iWidth, int a_iHeight) {
		String	strEmbedHtml	= "";
		String	strPath			= "";
		strEmbedHtml += "<html><body>";
		strEmbedHtml += "<iframe class=\"youtube-player\" width=\"%d\" height=\"%d\" id=\"ytplayer\" type=\"text/html\" src=\"http://www.youtube.com/embed/%s?fs=1\" frameborder=\"0\" autoplay=1 allowfullscreen> </iframe>";
		strEmbedHtml += "</body></html>";
		strPath = String.format(strEmbedHtml, a_iWidth, a_iHeight, a_strVideoId);
		return strPath;
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
	@Override
	protected void onDestroy() {

		UtilYouTube.MarkVideoAsViewed(this, m_strVideoId);
		m_webView.stopLoading();
		m_webView.clearView();
		m_webView.destroy();
		super.onDestroy();
	}

	//------------------------------------------------------------------------------
}

//==============================================================================
