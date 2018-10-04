package androidcustom.radiostation.twitter;

import oauth.signpost.OAuth;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import android.content.SharedPreferences;
import android.util.Log;
import androidcustom.radiostation.global.Const;

//==============================================================================
public class TwitterUtil {

	//------------------------------------------------------------------------------
	public static boolean IsAuthenticated(SharedPreferences a_sharedPrefs) {

		String strToken		= a_sharedPrefs.getString(OAuth.OAUTH_TOKEN, "");
		String strSecret	= a_sharedPrefs.getString(OAuth.OAUTH_TOKEN_SECRET, "");

		AccessToken accessToken = new AccessToken(strToken, strSecret);
		Twitter twitter = new TwitterFactory().getInstance();
		twitter.setOAuthConsumer(Const.TWITTER_CONSUMER_KEY, Const.TWITTER_CONSUMER_SECRET);
		twitter.setOAuthAccessToken(accessToken);

		try {
			twitter.getAccountSettings();
			return true;
		} catch (TwitterException e) {}
		return false;
	}

	//------------------------------------------------------------------------------
	public static void SendTweet(SharedPreferences a_sharedPrefs, String a_strMsg) throws Exception {
		String strToken = a_sharedPrefs.getString(OAuth.OAUTH_TOKEN, "");
		String strSecret = a_sharedPrefs.getString(OAuth.OAUTH_TOKEN_SECRET, "");
		Log.d("dhaval-> token",		strToken);
		Log.d("dhaval-> secret",	strSecret);
		Log.d("dhaval-> msg",		a_strMsg);

		try {
			AccessToken a = new AccessToken(strToken, strSecret);
			Log.d("acc_token :", "" + a);
			Twitter twitter = new TwitterFactory().getInstance();
			twitter.setOAuthConsumer(Const.TWITTER_CONSUMER_KEY, Const.TWITTER_CONSUMER_SECRET);
			twitter.setOAuthAccessToken(a);
			twitter.updateStatus("" + a_strMsg);
		}
		catch (Exception e) {
			Log.d("error dj-->", e.getMessage().toString());
		}
	}

	//------------------------------------------------------------------------------
}

//==============================================================================
