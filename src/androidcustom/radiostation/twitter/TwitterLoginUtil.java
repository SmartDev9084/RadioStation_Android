package androidcustom.radiostation.twitter;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.User;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.StrictMode;
import android.util.Log;
import androidcustom.radiostation.global.Const;
import androidcustom.radiostation.ui.DialogProgress;

//==============================================================================
public class TwitterLoginUtil {

	private static	Twitter		m_twitter;
	private	Context		m_context;
	
	private	String		m_strUserName;
	private	String		m_strAvatarUrl;

	private	DialogProgress	m_dlgProgress;

	private	TwitterLoginFinishedListener	m_listenerLoginFinished;

	//==============================================================================
	public static interface TwitterLoginFinishedListener {
		public void OnTwitterLoginFinished();
	}
	//==============================================================================

	//------------------------------------------------------------------------------
	public TwitterLoginUtil(Context a_context) {
		m_context		= a_context;
		m_dlgProgress	= null;

		m_strUserName	= null;
		m_strAvatarUrl	= null;
		m_listenerLoginFinished	= null;

		if (android.os.Build.VERSION.SDK_INT > 8) {
			StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
			StrictMode.setThreadPolicy(policy);
		}
		System.setProperty("twitter4j.http.useSSL", "true");
	}

	//------------------------------------------------------------------------------
	public void SetLoginFinishedListener(TwitterLoginFinishedListener a_listener) {
		m_listenerLoginFinished = a_listener;
	}

	//------------------------------------------------------------------------------
	public void LoginTwitter() {

		if (m_dlgProgress == null) {
			m_dlgProgress = new DialogProgress(m_context);
			m_dlgProgress.setCanceledOnTouchOutside(false);
			m_dlgProgress.setCancelable(false);
		}
		m_dlgProgress.show();

		TaskGetRequestToken taskGetRequestToken = new TaskGetRequestToken();
		taskGetRequestToken.execute();
	}

	//------------------------------------------------------------------------------
	public String GetUserName() {
		return m_strUserName;
	}

	//------------------------------------------------------------------------------
	public String GetAvatarUrl() {
		return m_strAvatarUrl;
	}

	//------------------------------------------------------------------------------
	private void LaunchLoginWebView(RequestToken a_requestToken) {
		if ((m_dlgProgress != null) && (m_dlgProgress.isShowing()))
			m_dlgProgress.dismiss();
		Intent intent = new Intent(m_context, ActivityLoginTwitter.class);
		intent.putExtra(Const.KEY_TWITTER_AUTHENTICATION_URL, a_requestToken.getAuthenticationURL());
		((Activity)m_context).startActivityForResult(intent, Const.REQUESTCODE_LOGIN_TWITTER);
	}

	//------------------------------------------------------------------------------
	public void GetAccessToken(String a_strCallbackUrl) {
		Uri		uri = Uri.parse(a_strCallbackUrl);
		String	strVerifier = null;
		if (strVerifier == null)	strVerifier = uri.getQueryParameter("oauth_verifier");
		if (strVerifier == null)	strVerifier = uri.getQueryParameter("oauth_token");
		if (strVerifier == null)	return;

		TaskGetAccessToken taskGetAccessToken = new TaskGetAccessToken();
		taskGetAccessToken.execute(strVerifier);
	}

	//==============================================================================
	private class TaskGetRequestToken extends AsyncTask<Void, Void, Void> {

		@Override
		protected Void doInBackground(Void... voids) {
			ConfigurationBuilder	configBuilder = new ConfigurationBuilder();
			configBuilder.setDebugEnabled(true)
						.setOAuthConsumerKey(Const.TWITTER_CONSUMER_KEY)
						.setOAuthConsumerSecret(Const.TWITTER_CONSUMER_SECRET);
			Configuration	configuration	= configBuilder.build();
			TwitterFactory	twitterFactory	= new TwitterFactory(configuration);
			m_twitter = twitterFactory.getInstance();

			try {
				RequestToken requestToken = m_twitter.getOAuthRequestToken(Const.TWITTER_CALLBACK_URL);
				LaunchLoginWebView(requestToken);
			}
			catch (TwitterException e) {
				Log.e("Twitter", e.getMessage());
				if ((m_dlgProgress != null) && (m_dlgProgress.isShowing()))
					m_dlgProgress.dismiss();
			}
			return null;
		}
	}

	//==============================================================================
	private class TaskGetAccessToken extends AsyncTask<String, Void, Boolean> {

		@Override
		protected Boolean doInBackground(String... strings) {
			String strVerifier = strings[0];
			try {
				AccessToken accessToken		= m_twitter.getOAuthAccessToken(strVerifier);
				User		user			= m_twitter.showUser(m_twitter.getId());
				m_strUserName	= accessToken.getScreenName();
				m_strAvatarUrl	= user.getProfileImageURL();
			} catch (Exception e) { Log.e("TOKEN", e.getMessage()); return false; }
			return true;
		}

		@Override
		protected void onPostExecute(Boolean result) {
			if (result == Boolean.TRUE)
				m_listenerLoginFinished.OnTwitterLoginFinished();
		}
	}

	//==============================================================================

	//------------------------------------------------------------------------------
}

//==============================================================================
