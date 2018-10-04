package androidcustom.radiostation.twitter;

import androidcustom.radiostation.R;
import androidcustom.radiostation.global.Const;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebView;
import android.webkit.WebViewClient;

//==============================================================================
public class ActivityLoginTwitter extends Activity {

	//-------------------------------------------------------------------------------
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login_twitter);

		Intent intent = getIntent();
		String strUrl = intent.getStringExtra(Const.KEY_TWITTER_AUTHENTICATION_URL);

		WebView webView = (WebView) findViewById(R.id.ID_WEBVIEW_LOGIN_TWITTER);
		webView.getSettings().setJavaScriptEnabled(true);
		webView.setWebViewClient(new WebViewClientLoginToTwitter());

		webView.loadUrl(strUrl);
	}

	//==============================================================================
	private class WebViewClientLoginToTwitter extends WebViewClient {
		//-------------------------------------------------------------------------------
		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			Log.e("WebViewClient", url);
			if (url.contains(Const.TWITTER_CALLBACK_URL)) {
				Intent intent = new Intent();
				intent.putExtra(Const.KEY_TWITTER_CALLBACK_URL, url);
				setResult(Activity.RESULT_OK, intent);
				finish();
			}
			return false;
		}
	}
	//==============================================================================

}
//==============================================================================
