package androidcustom.radiostation.facebook;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import androidcustom.radiostation.R;
import androidcustom.radiostation.facebook.Facebook.FacebookDialogListener;
import androidcustom.radiostation.facebook.FacebookAsyncRunner.FacebookRequestListener;
import androidcustom.radiostation.global.Const;

//==============================================================================
public class FacebookLoginUtil {

	private	Context		m_context;
	private	Facebook	m_facebook;

	private	FacebookAsyncRunner		m_asyncFacebookRunner;
	
	private	static	final	String[]	m_arrStrPermissions = new String[] {
		"read_stream",
		"email"
	};
	
	private		String			m_strFacebookId;
	private		String			m_strFacebookName;

	private SharedPreferences		m_sharedPreferences;
	
	private	FacebookLoginFinishedListener	m_listenerLoginFinished;

	//==============================================================================
	public static interface FacebookLoginFinishedListener {
		public void OnFacebookLoginFinished(boolean a_result);
	}
	//==============================================================================

	//------------------------------------------------------------------------------
	public FacebookLoginUtil(Context a_context) {
		m_context = a_context;
		m_facebook	= null;
	}

	//------------------------------------------------------------------------------
	public String GetFacebookId() {
		return m_strFacebookId;
	}
	
	//------------------------------------------------------------------------------
	public String GetFacebookName() {
		return m_strFacebookName;
	}

	//------------------------------------------------------------------------------
	public void SetLoginFinishedListener(FacebookLoginFinishedListener a_listener) {
		m_listenerLoginFinished = a_listener;
	}

	//------------------------------------------------------------------------------
	public void SetFacebookConnection() {
		m_facebook = new Facebook(m_context.getResources().getString(R.string.FACEBOOK_APP_ID));
		m_asyncFacebookRunner = new FacebookAsyncRunner(m_facebook);
	}

	//------------------------------------------------------------------------------
	public void StartAuthorizeFacebook() {
		// For Authorizing at Every Login, close below if

		if (IsFacebookSessionValid()) {
			m_asyncFacebookRunner.Request("me", new MyFacebookRequestListener());
		}
		else {
			// no logged in, so relogin
			m_facebook.Authorize((Activity)m_context, m_arrStrPermissions, Const.REQUESTCODE_LOGIN_FACEBOOK, new FacebookLoginDialogListener());

		}

	}

	//------------------------------------------------------------------------------
	public boolean IsFacebookSessionValid() {
		m_sharedPreferences = PreferenceManager.getDefaultSharedPreferences(m_context);
		String access_token = m_sharedPreferences.getString("access_token", "x");
		Long expires = m_sharedPreferences.getLong("access_expires", -1);

		if (access_token != null && expires != -1) {
			m_facebook.SetAccessToken(access_token);
			m_facebook.SetAccessExpires(expires);
		}
		return m_facebook.IsSessionValid();
	}

	//------------------------------------------------------------------------------
	public void AuthorizeCallback(int requestCode, int resultCode, Intent data) {
		m_facebook.AuthorizeCallback(requestCode, resultCode, data);
	}

	//==============================================================================
	private class FacebookLoginDialogListener implements FacebookDialogListener {
		@Override
		public void OnComplete(Bundle values) {
			String token = m_facebook.GetAccessToken();
			long token_expires = m_facebook.GetAccessExpires();
			m_sharedPreferences = PreferenceManager.getDefaultSharedPreferences(m_context);
			m_sharedPreferences.edit().putLong("access_expires", token_expires).commit();
			m_sharedPreferences.edit().putString("access_token", token).commit();
			m_asyncFacebookRunner.Request("me", new MyFacebookRequestListener());
		}

		@Override
		public void OnFacebookError(FacebookError e) {
			m_listenerLoginFinished.OnFacebookLoginFinished(false);
		}

		@Override
		public void OnError(DialogError e) {
			m_listenerLoginFinished.OnFacebookLoginFinished(false);
		}

		@Override
		public void OnCancel() {
			m_listenerLoginFinished.OnFacebookLoginFinished(false);
		}
	}

	//==============================================================================
	private class MyFacebookRequestListener implements FacebookRequestListener {
		@Override
		public void OnComplete(String response, Object state) {
			try {
				JSONObject	json	= FacebookMisc.ParseJson(response);
				m_strFacebookId		= json.getString("id");
				m_strFacebookName	= json.getString("name");
				m_listenerLoginFinished.OnFacebookLoginFinished(true);
			}
			catch (JSONException e) {}
			catch (FacebookError e1) {}
		}

		@Override
		public void OnIoException(IOException e, Object state) {
			m_listenerLoginFinished.OnFacebookLoginFinished(false);
		}

		@Override
		public void OnFileNotFoundException(FileNotFoundException e, Object state) {
			m_listenerLoginFinished.OnFacebookLoginFinished(false);
		}

		@Override
		public void OnMalformedUrlException(MalformedURLException e, Object state) {
			m_listenerLoginFinished.OnFacebookLoginFinished(false);
		}

		@Override
		public void OnFacebookError(FacebookError e, Object state) {
			m_listenerLoginFinished.OnFacebookLoginFinished(false);
		}
	}
	//==============================================================================
}
//==============================================================================
