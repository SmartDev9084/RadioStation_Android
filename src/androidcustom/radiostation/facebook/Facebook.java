/*
 * Copyright 2010 Facebook, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *	http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package androidcustom.radiostation.facebook;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;

import org.json.JSONException;
import org.json.JSONObject;

import android.Manifest;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.content.pm.Signature;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.text.TextUtils;
import android.webkit.CookieSyncManager;

/**
 * Main Facebook object for interacting with the Facebook developer API.
 * Provides methods to log in and log out a user, make requests using the REST
 * and Graph APIs, and start user interface interactions with the API (such as
 * pop-ups promoting for credentials, permissions, stream posts, etc.)
 *
 * @author  Jim Brusstar (jimbru@facebook.com),
 *		  Yariv Sadan (yariv@facebook.com),
 *		  Luke Shepard (lshepard@facebook.com)
 */
//==============================================================================
public class Facebook {

	// Strings used in the authorization flow
	public	static	final	String STR_URI_REDIRECT		= "fbconnect://success";
	public	static	final	String STR_URI_CANCEL		= "fbconnect://cancel";
	public	static	final	String STR_ACCESS_TOKEN		= "access_token";
	public	static	final	String STR_EXPIRES_IN		= "expires_in";
	public	static	final	String STR_CREDENTIAL		= "facebook-credentials";
	public	static	final	String STR_SERVICE_DISABLED = "service_disabled";

	public	static	final	Uri		URI_ATTRIBUTION_ID_CONTENT = Uri.parse("content://com.facebook.katana.provider.AttributionIdProvider");
	public	static	final	String	STR_ATTRIBUTION_ID_COLUMN_NAME = "aid";

	private	static	final	String	STR_ATTRIBUTION_PREFERENCES	= "com.facebook.sdk.attributionTracking";
	private	static	final	String	STR_PUBLISH_ACTIVITY_PATH	= "%s/activities";
	private	static	final	String	STR_MOBILE_INSTALL_EVENT	= "MOBILE_APP_INSTALL";
	private	static	final	String	STR_SUPPORTS_ATTRIBUTION	= "supports_attribution";
	private	static	final	String	STR_APPLICATION_FIELDS		= "fields";
	private	static	final	String	STR_ANALYTICS_EVENT			= "event";
	private	static	final	String	STR_ATTRIBUTION_KEY			= "attribution";
	private	static	final	String	STR_OAUTH = "oauth";

	public	static final int	FORCE_DIALOG_AUTH = -1;


	// Used as default activityCode by authorize(). See authorize() below.
	private static final int DEFAULT_AUTH_ACTIVITY_CODE = 32665;

	// Facebook server endpoints: may be modified in a subclass for testing
	protected static String STR_URL_BASE_DIALOG	= "https://m.facebook.com/dialog/";
	protected static String STR_URL_BASE_GRAPH	= "https://graph.facebook.com/";
	protected static String STR_URL_RESTSERVER	= "https://api.facebook.com/restserver.php";

	private String	m_strAppId;
	private String	m_strAccessToken		= null;
	private long	m_lLastAccessUpdate		= 0;
	private long	m_lAccessExpires		= 0;

	private Activity	m_activityAuth;
	private String[]	m_arrStrAuthPermission;
	private int			m_iAuthActivityCode;
	private FacebookDialogListener		m_listenerAuthDialog;
	
	// If the last time we extended the access token was more than 24 hours ago
	// we try to refresh the access token again.
	final private long REFRESH_TOKEN_BARRIER = 24L * 60L * 60L * 1000L;

	private boolean		m_bShouldAutoPublishInstall		= true;
	private AutoPublishAsyncTask m_asyncTaskAutoPublish	= null;

	/**
	 * Constructor for Facebook object.
	 *
	 * @param appId
	 *		  Your Facebook application ID. Found at
	 *		  www.facebook.com/developers/apps.php.
	 */
	//------------------------------------------------------------------------------
	public Facebook(String appId) {
		if (appId == null) {
			throw new IllegalArgumentException(
					"You must specify your application ID when instantiating " +
					"a Facebook object. See README for details."
			);
		}
		m_strAppId = appId;
	}

	/**
	 * Default authorize method. Grants only basic permissions.
	 *
	 * See authorize() below for @params.
	 */
	//------------------------------------------------------------------------------
	public void Authorize(Activity activity, final FacebookDialogListener listener) {
		try { LogOut(activity); } catch (Exception e) {}
		Authorize(activity, new String[] {}, DEFAULT_AUTH_ACTIVITY_CODE, listener);
	}

	/**
	 * Authorize method that grants custom permissions.
	 *
	 * See authorize() below for @params.
	 */
	//------------------------------------------------------------------------------
	public void Authorize(Activity activity, String[] permissions, final FacebookDialogListener listener) {
		try { LogOut(activity); } catch (Exception e) {}
		Authorize(activity, permissions, DEFAULT_AUTH_ACTIVITY_CODE, listener);
	}

	/**
	 * Full authorize method.
	 *
	 * Starts either an Activity or a dialog which prompts the user to log in to
	 * Facebook and grant the requested permissions to the given application.
	 *
	 * This method will, when possible, use Facebook's single sign-on for
	 * Android to obtain an access token. This involves proxying a call through
	 * the Facebook for Android stand-alone application, which will handle the
	 * authentication flow, and return an OAuth access token for making API
	 * calls.
	 *
	 * Because this process will not be available for all users, if single
	 * sign-on is not possible, this method will automatically fall back to the
	 * OAuth 2.0 User-Agent flow. In this flow, the user credentials are handled
	 * by Facebook in an embedded WebView, not by the client application. As
	 * such, the dialog makes a network request and renders HTML content rather
	 * than a native UI. The access token is retrieved from a redirect to a
	 * special URL that the WebView handles.
	 *
	 * Note that User credentials could be handled natively using the OAuth 2.0
	 * Username and Password Flow, but this is not supported by this SDK.
	 *
	 * See http://developers.facebook.com/docs/authentication/ and
	 * http://wiki.oauth.net/OAuth-2 for more details.
	 *
	 * Note that this method is asynchronous and the callback will be invoked in
	 * the original calling thread (not in a background thread).
	 *
	 * Also note that requests may be made to the API without calling authorize
	 * first, in which case only public information is returned.
	 *
	 * IMPORTANT: Note that single sign-on authentication will not function
	 * correctly if you do not include a call to the authorizeCallback() method
	 * in your onActivityResult() function! Please see below for more
	 * information. single sign-on may be disabled by passing FORCE_DIALOG_AUTH
	 * as the activityCode parameter in your call to authorize().
	 *
	 * @param activity
	 *			The Android activity in which we want to display the
	 *			authorization dialog.
	 * @param applicationId
	 *			The Facebook application identifier e.g. "350685531728"
	 * @param permissions
	 *			A list of permissions required for this application: e.g.
	 *			"read_stream", "publish_stream", "offline_access", etc. see
	 *			http://developers.facebook.com/docs/authentication/permissions
	 *			This parameter should not be null -- if you do not require any
	 *			permissions, then pass in an empty String array.
	 * @param activityCode
	 *			Single sign-on requires an activity result to be called back
	 *			to the client application -- if you are waiting on other
	 *			activities to return data, pass a custom activity code here to
	 *			avoid collisions. If you would like to force the use of legacy
	 *			dialog-based authorization, pass FORCE_DIALOG_AUTH for this
	 *			parameter. Otherwise just omit this parameter and Facebook
	 *			will use a suitable default. See
	 *			http://developer.android.com/reference/android/
	 *			  app/Activity.html for more information.
	 * @param listener
	 *			Callback interface for notifying the calling application when
	 *			the authentication dialog has completed, failed, or been
	 *			canceled.
	 */
	//------------------------------------------------------------------------------
	public void Authorize(Activity activity, String[] permissions, int activityCode, final FacebookDialogListener listener) {

		boolean singleSignOnStarted = false;		// For Authorize Every Login, this must be true.

		m_listenerAuthDialog = listener;

		// fire off an auto-attribution publish if appropriate.
		AutoPublishAsync(activity.getApplicationContext());

		// Prefer single sign-on, where available.
		if (activityCode >= 0) {
			singleSignOnStarted = StartSingleSignOn(activity, m_strAppId, permissions, activityCode);
		}
		// Otherwise fall back to traditional dialog.
		if (!singleSignOnStarted) {
			StartDialogAuth(activity, permissions);
		}
	}

	/**
	 * Internal method to handle single sign-on backend for authorize().
	 *
	 * @param activity
	 *			The Android Activity that will parent the ProxyAuth Activity.
	 * @param applicationId
	 *			The Facebook application identifier.
	 * @param permissions
	 *			A list of permissions required for this application. If you do
	 *			not require any permissions, pass an empty String array.
	 * @param activityCode
	 *			Activity code to uniquely identify the result Intent in the
	 *			callback.
	 */
	//------------------------------------------------------------------------------
	private boolean StartSingleSignOn(Activity activity, String applicationId, String[] permissions, int activityCode) {
		boolean didSucceed = true;
		Intent intent = new Intent();

		intent.setClassName("com.facebook.katana", "com.facebook.katana.ProxyAuth");
		intent.putExtra("client_id", applicationId);
		if (permissions.length > 0) {
			intent.putExtra("scope", TextUtils.join(",", permissions));
		}

		// Verify that the application whose package name is
		// com.facebook.katana.ProxyAuth
		// has the expected FB app signature.
		if (!ValidateActivityIntent(activity, intent)) {
			return false;
		}

		m_activityAuth = activity;
		m_arrStrAuthPermission = permissions;
		m_iAuthActivityCode = activityCode;
		try {
			activity.startActivityForResult(intent, activityCode);
		}
		catch (ActivityNotFoundException e) {
			didSucceed = false;
		}

		return didSucceed;
	}

	/**
	 * Helper to validate an activity intent by resolving and checking the
	 * provider's package signature.
	 *
	 * @param context
	 * @param intent
	 * @return true if the service intent resolution happens successfully and the
	 * 	signatures match.
	 */
	//------------------------------------------------------------------------------
	private boolean ValidateActivityIntent(Context context, Intent intent) {
		ResolveInfo resolveInfo = context.getPackageManager().resolveActivity(intent, 0);
		if (resolveInfo == null)
			return false;

		return ValidateAppSignatureForPackage(context, resolveInfo.activityInfo.packageName);
	}

	/**
	 * Helper to validate a service intent by resolving and checking the
	 * provider's package signature.
	 *
	 * @param context
	 * @param intent
	 * @return true if the service intent resolution happens successfully and the
	 * 	signatures match.
	 */
	//------------------------------------------------------------------------------
	private boolean ValidateServiceIntent(Context context, Intent intent) {
		ResolveInfo resolveInfo = context.getPackageManager().resolveService(intent, 0);
		if (resolveInfo == null)
			return false;

		return ValidateAppSignatureForPackage(context, resolveInfo.serviceInfo.packageName);
	}

	/**
	 * Query the signature for the application that would be invoked by the
	 * given intent and verify that it matches the FB application's signature.
	 *
	 * @param context
	 * @param packageName
	 * @return true if the app's signature matches the expected signature.
	 */
	//------------------------------------------------------------------------------
	private boolean ValidateAppSignatureForPackage(Context context, String packageName) {

		PackageInfo packageInfo;
		try {
			packageInfo = context.getPackageManager().getPackageInfo(
					packageName, PackageManager.GET_SIGNATURES);
		} catch (NameNotFoundException e) { return false; }

		for (Signature signature : packageInfo.signatures) {
			if (signature.toCharsString().equals(FB_APP_SIGNATURE))
				return true;
		}
		return false;
	}

	/**
	 * Internal method to handle dialog-based authentication backend for
	 * authorize().
	 *
	 * @param activity
	 *			The Android Activity that will parent the auth dialog.
	 * @param applicationId
	 *			The Facebook application identifier.
	 * @param permissions
	 *			A list of permissions required for this application. If you do
	 *			not require any permissions, pass an empty String array.
	 */
	//------------------------------------------------------------------------------
	private void StartDialogAuth(Activity activity, String[] permissions) {
		Bundle params = new Bundle();
		if (permissions.length > 0)
			params.putString("scope", TextUtils.join(",", permissions));
		CookieSyncManager.createInstance(activity);
		ShowDialog(
			activity,
			STR_OAUTH,
			params,
			new FacebookDialogListener() {
				public void OnComplete(Bundle values) {
					// ensure any cookies set by the dialog are saved
					CookieSyncManager.getInstance().sync();
					SetAccessToken(values.getString(STR_ACCESS_TOKEN));
					SetAccessExpiresIn(values.getString(STR_EXPIRES_IN));
					if (IsSessionValid()) {
						FacebookMisc.Logd("Facebook-authorize", "Login Success! access_token="
								+ GetAccessToken() + " expires="
								+ GetAccessExpires());
						m_listenerAuthDialog.OnComplete(values);
					}
					else {
						m_listenerAuthDialog.OnFacebookError(
							new FacebookError("Failed to receive access token."));
					}
				}
	
				public void OnError(DialogError error) {
					FacebookMisc.Logd("Facebook-authorize", "Login failed: " + error);
					m_listenerAuthDialog.OnError(error);
				}
	
				public void OnFacebookError(FacebookError error) {
					FacebookMisc.Logd("Facebook-authorize", "Login failed: " + error);
					m_listenerAuthDialog.OnFacebookError(error);
				}
	
				public void OnCancel() {
					FacebookMisc.Logd("Facebook-authorize", "Login canceled");
					m_listenerAuthDialog.OnCancel();
				}
			}
		);
	}

	/**
	 * IMPORTANT: This method must be invoked at the top of the calling
	 * activity's onActivityResult() function or Facebook authentication will
	 * not function properly!
	 *
	 * If your calling activity does not currently implement onActivityResult(),
	 * you must implement it and include a call to this method if you intend to
	 * use the authorize() method in this SDK.
	 *
	 * For more information, see
	 * http://developer.android.com/reference/android/app/
	 *   Activity.html#onActivityResult(int, int, android.content.Intent)
	 */
	//------------------------------------------------------------------------------
	public void AuthorizeCallback(int requestCode, int resultCode, Intent data) {
		if (requestCode == m_iAuthActivityCode) {

			// Successfully redirected.
			if (resultCode == Activity.RESULT_OK) {

				// Check OAuth 2.0/2.10 error code.
				String error = data.getStringExtra("error");
				if (error == null)
					error = data.getStringExtra("error_type");

				// A Facebook error occurred.
				if (error != null) {
					if (error.equals(STR_SERVICE_DISABLED)
							|| error.equals("AndroidAuthKillSwitchException")) {
						FacebookMisc.Logd("Facebook-authorize", "Hosted auth currently "
							+ "disabled. Retrying dialog auth...");
						StartDialogAuth(m_activityAuth, m_arrStrAuthPermission);
					}
					else if (error.equals("access_denied")
							|| error.equals("OAuthAccessDeniedException")) {
						FacebookMisc.Logd("Facebook-authorize", "Login canceled by user.");
						m_listenerAuthDialog.OnCancel();
					}
					else {
						String description = data.getStringExtra("error_description");
						if (description != null)
							error = error + ":" + description;
						FacebookMisc.Logd("Facebook-authorize", "Login failed: " + error);
						m_listenerAuthDialog.OnFacebookError(new FacebookError(error));
					}

				// No errors.
				}
				else {
					SetAccessToken(data.getStringExtra(STR_ACCESS_TOKEN));
					SetAccessExpiresIn(data.getStringExtra(STR_EXPIRES_IN));
					if (IsSessionValid()) {
						FacebookMisc.Logd("Facebook-authorize",
								"Login Success! access_token="
										+ GetAccessToken() + " expires="
										+ GetAccessExpires());
						m_listenerAuthDialog.OnComplete(data.getExtras());
					}
					else {
						m_listenerAuthDialog.OnFacebookError(
								new FacebookError("Failed to receive access token."));
					}
				}
			// An error occurred before we could be redirected.
			}
			else if (resultCode == Activity.RESULT_CANCELED) {
				// An Android error occured.
				if (data != null) {
					FacebookMisc.Logd("Facebook-authorize",
							"Login failed: " + data.getStringExtra("error"));
					m_listenerAuthDialog.OnError(
							new DialogError(
									data.getStringExtra("error"),
									data.getIntExtra("error_code", -1),
									data.getStringExtra("failing_url")));
				// User pressed the 'back' button.
				}
				else {
					FacebookMisc.Logd("Facebook-authorize", "Login canceled by user.");
					m_listenerAuthDialog.OnCancel();
				}
			}
		}
	}

	/**
	 * Refresh OAuth access token method. Binds to Facebook for Android
	 * stand-alone application application to refresh the access token. This
	 * method tries to connect to the Facebook App which will handle the
	 * authentication flow, and return a new OAuth access token. This method
	 * will automatically replace the old token with a new one. Note that this
	 * method is asynchronous and the callback will be invoked in the original
	 * calling thread (not in a background thread).
	 * 
	 * @param context
	 *			The Android Context that will be used to bind to the Facebook
	 *			RefreshToken Service
	 * @param serviceListener
	 *			Callback interface for notifying the calling application when
	 *			the refresh request has completed or failed (can be null). In
	 *			case of a success a new token can be found inside the result
	 *			Bundle under Facebook.ACCESS_TOKEN key.
	 * @return true if the binding to the RefreshToken Service was created
	 */
	//------------------------------------------------------------------------------
	public boolean ExtendAccessToken(Context context, ServiceListener serviceListener) {
		Intent intent = new Intent();

		intent.setClassName("com.facebook.katana",
				"com.facebook.katana.platform.TokenRefreshService");

		// Verify that the application whose package name is
		// com.facebook.katana
		// has the expected FB app signature.
		if (!ValidateServiceIntent(context, intent))
			return false;

		return context.bindService(	intent,
									new TokenRefreshServiceConnection(context, serviceListener),
									Context.BIND_AUTO_CREATE);
	}
	
	/**
	* Calls extendAccessToken if shouldExtendAccessToken returns true.
	* 
	* @return the same value as extendAccessToken if the the token requires
	*		   refreshing, true otherwise
	*/
	//------------------------------------------------------------------------------
	public boolean ExtendAccessTokenIfNeeded(Context context, ServiceListener serviceListener) {
		if (ShouldExtendAccessToken()) {
			return ExtendAccessToken(context, serviceListener);
		}
		return true;
	}
	
	/**
	 * Check if the access token requires refreshing. 
	 * 
	 * @return true if the last time a new token was obtained was over 24 hours ago.
	 */
	//------------------------------------------------------------------------------
	public boolean ShouldExtendAccessToken() {
		return (IsSessionValid() && (System.currentTimeMillis() - m_lLastAccessUpdate >= REFRESH_TOKEN_BARRIER));
	}
	
	/**
	 * Handles connection to the token refresh service (this service is a part
	 * of Facebook App).
	 */
	//==============================================================================
	private class TokenRefreshServiceConnection implements ServiceConnection {

		final Messenger messageReceiver = new Messenger(
			new Handler() {
				@Override
				public void handleMessage(Message msg) {
					String token = msg.getData().getString(STR_ACCESS_TOKEN);
					long expiresAt = msg.getData().getLong(STR_EXPIRES_IN) * 1000L;
	
					// To avoid confusion we should return the expiration time in
					// the same format as the getAccessExpires() function - that
					// is in milliseconds.
					Bundle resultBundle = (Bundle) msg.getData().clone();
					resultBundle.putLong(STR_EXPIRES_IN, expiresAt);
	
					if (token != null) {
						SetAccessToken(token);
						SetAccessExpires(expiresAt);
						if (serviceListener != null)
							serviceListener.OnComplete(resultBundle);
					}
					else if (serviceListener != null) { // extract errors only if client wants them
						String error = msg.getData().getString("error");
						if (msg.getData().containsKey("error_code")) {
							int errorCode = msg.getData().getInt("error_code");
							serviceListener.OnFacebookError(new FacebookError(error, null, errorCode));
						}
						else {
							serviceListener.onError(
								new Error(error != null ? error : "Unknown service error"));
						}
					}
	
					// The refreshToken function should be called rarely,
					// so there is no point in keeping the binding open.
					applicationsContext.unbindService(TokenRefreshServiceConnection.this);
				}
			}
		);

		final ServiceListener serviceListener;
		final Context applicationsContext;

		Messenger messageSender = null;

		//------------------------------------------------------------------------------
		public TokenRefreshServiceConnection(Context applicationsContext,
				ServiceListener serviceListener) {
			this.applicationsContext = applicationsContext;
			this.serviceListener = serviceListener;
		}

		//------------------------------------------------------------------------------
		@Override
		public void onServiceConnected(ComponentName className, IBinder service) {
			messageSender = new Messenger(service);
			RefreshToken();
		}

		//------------------------------------------------------------------------------
		@Override
		public void onServiceDisconnected(ComponentName arg) {
			serviceListener.onError(new Error("Service disconnected"));
			// We returned an error so there's no point in
			// keeping the binding open.
			applicationsContext.unbindService(TokenRefreshServiceConnection.this);
		}

		//------------------------------------------------------------------------------
		private void RefreshToken() {
			Bundle requestData = new Bundle();
			requestData.putString(STR_ACCESS_TOKEN, m_strAccessToken);

			Message request = Message.obtain();
			request.setData(requestData);
			request.replyTo = messageReceiver;

			try {
				messageSender.send(request);
			}
			catch (RemoteException e) {
				serviceListener.onError(new Error("Service connection error"));
			}
		}
	};	
	//==============================================================================

	/**
	 * Invalidate the current user session by removing the access token in
	 * memory, clearing the browser cookie, and calling auth.expireSession
	 * through the API.
	 *
	 * Note that this method blocks waiting for a network response, so do not
	 * call it in a UI thread.
	 *
	 * @param context
	 *			The Android context in which the logout should be called: it
	 *			should be the same context in which the login occurred in
	 *			order to clear any stored cookies
	 * @throws IOException
	 * @throws MalformedURLException
	 * @return JSON string representation of the auth.expireSession response
	 *			("true" if successful)
	 */
	//------------------------------------------------------------------------------
	public String LogOut(Context context)
			throws MalformedURLException, IOException {
		FacebookMisc.ClearCookies(context);
		Bundle b = new Bundle();
		b.putString("method", "auth.expireSession");
		String response = Request(b);
		SetAccessToken(null);
		SetAccessExpires(0);
		return response;
	}

	/**
	 * Make a request to Facebook's old (pre-graph) API with the given
	 * parameters. One of the parameter keys must be "method" and its value
	 * should be a valid REST server API method.
	 *
	 * See http://developers.facebook.com/docs/reference/rest/
	 *
	 * Note that this method blocks waiting for a network response, so do not
	 * call it in a UI thread.
	 *
	 * Example:
	 * <code>
	 *  Bundle parameters = new Bundle();
	 *  parameters.putString("method", "auth.expireSession");
	 *  String response = request(parameters);
	 * </code>
	 *
	 * @param parameters
	 *			Key-value pairs of parameters to the request. Refer to the
	 *			documentation: one of the parameters must be "method".
	 * @throws IOException
	 *			if a network error occurs
	 * @throws MalformedURLException
	 *			if accessing an invalid endpoint
	 * @throws IllegalArgumentException
	 *			if one of the parameters is not "method"
	 * @return JSON string representation of the response
	 */
	//------------------------------------------------------------------------------
	public String Request(Bundle parameters)
			throws MalformedURLException, IOException {
		if (!parameters.containsKey("method")) {
			throw new IllegalArgumentException("API method must be specified. "
					+ "(parameters must contain key \"method\" and value). See"
					+ " http://developers.facebook.com/docs/reference/rest/");
		}
		return Request(null, parameters, "GET");
	}

	/**
	 * Make a request to the Facebook Graph API without any parameters.
	 *
	 * See http://developers.facebook.com/docs/api
	 *
	 * Note that this method blocks waiting for a network response, so do not
	 * call it in a UI thread.
	 *
	 * @param graphPath
	 *			Path to resource in the Facebook graph, e.g., to fetch data
	 *			about the currently logged authenticated user, provide "me",
	 *			which will fetch http://graph.facebook.com/me
	 * @throws IOException
	 * @throws MalformedURLException
	 * @return JSON string representation of the response
	 */
	//------------------------------------------------------------------------------
	public String Request(String graphPath)
			throws MalformedURLException, IOException {
		return Request(graphPath, new Bundle(), "GET");
	}

	/**
	 * Make a request to the Facebook Graph API with the given string parameters
	 * using an HTTP GET (default method).
	 *
	 * See http://developers.facebook.com/docs/api
	 *
	 * Note that this method blocks waiting for a network response, so do not
	 * call it in a UI thread.
	 *
	 * @param graphPath
	 *			Path to resource in the Facebook graph, e.g., to fetch data
	 *			about the currently logged authenticated user, provide "me",
	 *			which will fetch http://graph.facebook.com/me
	 * @param parameters
	 *			key-value string parameters, e.g. the path "search" with
	 *			parameters "q" : "facebook" would produce a query for the
	 *			following graph resource:
	 *			https://graph.facebook.com/search?q=facebook
	 * @throws IOException
	 * @throws MalformedURLException
	 * @return JSON string representation of the response
	 */
	//------------------------------------------------------------------------------
	public String Request(String graphPath, Bundle parameters)
			throws MalformedURLException, IOException {
		return Request(graphPath, parameters, "GET");
	}

	/**
	 * Synchronously make a request to the Facebook Graph API with the given
	 * HTTP method and string parameters. Note that binary data parameters
	 * (e.g. pictures) are not yet supported by this helper function.
	 *
	 * See http://developers.facebook.com/docs/api
	 *
	 * Note that this method blocks waiting for a network response, so do not
	 * call it in a UI thread.
	 *
	 * @param graphPath
	 *			Path to resource in the Facebook graph, e.g., to fetch data
	 *			about the currently logged authenticated user, provide "me",
	 *			which will fetch http://graph.facebook.com/me
	 * @param params
	 *			Key-value string parameters, e.g. the path "search" with
	 *			parameters {"q" : "facebook"} would produce a query for the
	 *			following graph resource:
	 *			https://graph.facebook.com/search?q=facebook
	 * @param httpMethod
	 *			http verb, e.g. "GET", "POST", "DELETE"
	 * @throws IOException
	 * @throws MalformedURLException
	 * @return JSON string representation of the response
	 */
	//------------------------------------------------------------------------------
	public String Request(String graphPath, Bundle params, String httpMethod)
			throws FileNotFoundException, MalformedURLException, IOException {
		params.putString("format", "json");
		if (IsSessionValid())
			params.putString(STR_ACCESS_TOKEN, GetAccessToken());

		String url = (graphPath != null) ? STR_URL_BASE_GRAPH + graphPath : STR_URL_RESTSERVER;
		return FacebookMisc.openUrl(url, httpMethod, params);
	}

	/**
	 * Generate a UI dialog for the request action in the given Android context.
	 *
	 * Note that this method is asynchronous and the callback will be invoked in
	 * the original calling thread (not in a background thread).
	 *
	 * @param context
	 *			The Android context in which we will generate this dialog.
	 * @param action
	 *			String representation of the desired method: e.g. "login",
	 *			"stream.publish", ...
	 * @param listener
	 *			Callback interface to notify the application when the dialog
	 *			has completed.
	 */
	//------------------------------------------------------------------------------
	public void ShowDialog(Context context, String action, FacebookDialogListener listener) {
		ShowDialog(context, action, new Bundle(), listener);
	}

	/**
	 * Generate a UI dialog for the request action in the given Android context
	 * with the provided parameters.
	 *
	 * Note that this method is asynchronous and the callback will be invoked in
	 * the original calling thread (not in a background thread).
	 *
	 * @param context
	 *			The Android context in which we will generate this dialog.
	 * @param action
	 *			String representation of the desired method: e.g. "feed" ...
	 * @param parameters
	 *			String key-value pairs to be passed as URL parameters.
	 * @param listener
	 *			Callback interface to notify the application when the dialog
	 *			has completed.
	 */
	//------------------------------------------------------------------------------
	public void ShowDialog(Context context, String action, Bundle parameters,
			final FacebookDialogListener listener) {

		String endpoint = STR_URL_BASE_DIALOG + action;
		parameters.putString("display", "touch");
		parameters.putString("redirect_uri", STR_URI_REDIRECT);

		if (action.equals(STR_OAUTH)) {
			parameters.putString("type", "user_agent");
			parameters.putString("client_id", m_strAppId);
		}
		else
			parameters.putString("app_id", m_strAppId);

		if (IsSessionValid())
			parameters.putString(STR_ACCESS_TOKEN, GetAccessToken());
		String url = endpoint + "?" + FacebookMisc.encodeUrl(parameters);
		if (context.checkCallingOrSelfPermission(Manifest.permission.INTERNET)
			!= PackageManager.PERMISSION_GRANTED) {
			FacebookMisc.ShowAlert(context, "Error", "Application requires permission to access the Internet");
		}
		else {
			new FacebookDialog(context, url, listener).show();
		}
	}

	/**
	 * @return boolean - whether this object has an non-expired session token
	 */
	//------------------------------------------------------------------------------
	public boolean IsSessionValid() {
		return ((GetAccessToken() != null)
				&& ((GetAccessExpires() == 0)
				|| (System.currentTimeMillis() < GetAccessExpires())));
	}

	/**
	 * Retrieve the OAuth 2.0 access token for API access: treat with care.
	 * Returns null if no session exists.
	 *
	 * @return String - access token
	 */
	//------------------------------------------------------------------------------
	public String GetAccessToken() {
		return m_strAccessToken;
	}

	/**
	 * Retrieve the current session's expiration time (in milliseconds since
	 * Unix epoch), or 0 if the session doesn't expire or doesn't exist.
	 *
	 * @return long - session expiration time
	 */
	//------------------------------------------------------------------------------
	public long GetAccessExpires() {
		return m_lAccessExpires;
	}

	/**
	 * Retrieve the last time the token was updated (in milliseconds since
	 * the Unix epoch), or 0 if the token has not been set.
	 *
	 * @return long - timestamp of the last token update.
	 */
	//------------------------------------------------------------------------------
	public long GetLastAccessUpdate() {
		return m_lLastAccessUpdate;
	}

	/**
	 * Restore the token, expiration time, and last update time from cached values.
	 * These should be values obtained from getAccessToken(), getAccessExpires, and
	 * getLastAccessUpdate() respectively.
	 *
	 * @param accessToken - access token
	 * @param accessExpires - access token expiration time
	 * @param lastAccessUpdate - timestamp of the last token update
	 */
	//------------------------------------------------------------------------------
	public void SetTokenFromCache(String accessToken, long accessExpires, long lastAccessUpdate) {
		m_strAccessToken = accessToken;
		m_lAccessExpires = accessExpires;
		m_lLastAccessUpdate = lastAccessUpdate;
	}

	/**
	 * Set the OAuth 2.0 access token for API access.
	 *
	 * @param token - access token
	 */
	//------------------------------------------------------------------------------
	public void SetAccessToken(String token) {
		m_strAccessToken = token;
		m_lLastAccessUpdate = System.currentTimeMillis();
	}

	/**
	 * Set the current session's expiration time (in milliseconds since Unix
	 * epoch), or 0 if the session doesn't expire.
	 *
	 * @param time - timestamp in milliseconds
	 */
	//------------------------------------------------------------------------------
	public void SetAccessExpires(long time) {
		m_lAccessExpires = time;
	}

	/**
	 * Set the current session's duration (in seconds since Unix epoch), or "0"
	 * if session doesn't expire.
	 *
	 * @param expiresIn
	 *			- duration in seconds (or 0 if the session doesn't expire)
	 */
	//------------------------------------------------------------------------------
	public void SetAccessExpiresIn(String expiresIn) {
		if (expiresIn != null) {
			long expires = expiresIn.equals("0") ?
					0 : System.currentTimeMillis() + Long.parseLong(expiresIn) * 1000L;
			SetAccessExpires(expires);
		}
	}

	//------------------------------------------------------------------------------
	public String GetAppId() {
		return m_strAppId;
	}

	//------------------------------------------------------------------------------
	public void SetAppId(String appId) {
		m_strAppId = appId;
	}

	/**
	 * Get Attribution ID for app install conversion tracking.
	 * @param contentResolver
	 * @return Attribution ID that will be used for conversion tracking. It will be null only if
	 *		 the user has not installed or logged in to the Facebook app.
	 */
	//------------------------------------------------------------------------------
	public static String getAttributionId(ContentResolver contentResolver) {
		String [] projection = {STR_ATTRIBUTION_ID_COLUMN_NAME};
		Cursor c = contentResolver.query(URI_ATTRIBUTION_ID_CONTENT, projection, null, null, null);
		if (c == null || !c.moveToFirst())
			return null;

		String attributionId = c.getString(c.getColumnIndex(STR_ATTRIBUTION_ID_COLUMN_NAME));

		return attributionId;
	}

	/**
	 * Get the auto install publish setting.  If true, an install event will be published during authorize(), unless
	 * it has occurred previously or the app does not have install attribution enabled on the application's developer
	 * config page.
	 * @return
	 */
	//------------------------------------------------------------------------------
	public boolean GetShouldAutoPublishInstall() {
		return m_bShouldAutoPublishInstall;
	}

	/**
	 * Sets whether auto publishing of installs will occur.
	 * @param value
	 */
	//------------------------------------------------------------------------------
	public void SetShouldAutoPublishInstall(boolean value) {
		m_bShouldAutoPublishInstall = value;
	}

	/**
	 * Manually publish install attribution to the facebook graph.  Internally handles tracking repeat calls to prevent
	 * multiple installs being published to the graph.
	 * @param context
	 * @return returns false on error.  Applications should retry until true is returned.  Safe to call again after
	 * true is returned.
	 */
	//------------------------------------------------------------------------------
	public boolean PublishInstall(final Context context) {
		try {
			// copy the application id to guarantee thread safety..
			String applicationId = m_strAppId;
			if (applicationId != null) {
				PublishInstall(this, applicationId, context);
				return true;
			}
		}
		catch (Exception e) {
			// if there was an error, fall through to the failure case.
			FacebookMisc.Logd("Facebook-publish", e.getMessage());
		}
		return false;
	}

	/**
	 * This function does the heavy lifting of publishing an install.
	 * @param fb
	 * @param applicationId
	 * @param context
	 * @throws Exception
	 */
	//------------------------------------------------------------------------------
	private static void PublishInstall(final Facebook fb, final String applicationId, final Context context)
			throws JSONException, FacebookError, MalformedURLException, IOException {

		String attributionId = Facebook.getAttributionId(context.getContentResolver());
		SharedPreferences preferences = context.getSharedPreferences(STR_ATTRIBUTION_PREFERENCES, Context.MODE_PRIVATE);
		String pingKey = applicationId+"ping";
		long lastPing = preferences.getLong(pingKey, 0);
		if (lastPing == 0 && attributionId != null) {
			Bundle supportsAttributionParams = new Bundle();
			supportsAttributionParams.putString(STR_APPLICATION_FIELDS, STR_SUPPORTS_ATTRIBUTION);
			JSONObject supportResponse = FacebookMisc.ParseJson(fb.Request(applicationId, supportsAttributionParams));
			Object doesSupportAttribution = (Boolean)supportResponse.get(STR_SUPPORTS_ATTRIBUTION);

			if (!(doesSupportAttribution instanceof Boolean)) {
				throw new JSONException(String.format(
					"%s contains %s instead of a Boolean", STR_SUPPORTS_ATTRIBUTION, doesSupportAttribution));
			}

			if ((Boolean)doesSupportAttribution) {
				Bundle publishParams = new Bundle();
				publishParams.putString(STR_ANALYTICS_EVENT, STR_MOBILE_INSTALL_EVENT);
				publishParams.putString(STR_ATTRIBUTION_KEY, attributionId);

				String publishUrl = String.format(STR_PUBLISH_ACTIVITY_PATH, applicationId);

				fb.Request(publishUrl, publishParams, "POST");

				// denote success since no error threw from the post.
				SharedPreferences.Editor editor = preferences.edit();
				editor.putLong(pingKey, System.currentTimeMillis());
				editor.commit();
			}
		}
	}

	//------------------------------------------------------------------------------
	void AutoPublishAsync(final Context context) {
		AutoPublishAsyncTask asyncTask = null;
		synchronized (this) {
			if (m_asyncTaskAutoPublish == null && GetShouldAutoPublishInstall()) {
				// copy the application id to guarantee thread safety against our container.
				String applicationId = Facebook.this.m_strAppId;

				// skip publish if we don't have an application id.
				if (applicationId != null)
					asyncTask = m_asyncTaskAutoPublish = new AutoPublishAsyncTask(applicationId, context);
			}
		}

		if (asyncTask != null)
			asyncTask.execute();
	}

	/**
	 * Async implementation to allow auto publishing to not block the ui thread.
	 */
	//==============================================================================
	private class AutoPublishAsyncTask extends AsyncTask<Void, Void, Void> {
		private final String	mApplicationId;
		private final Context	mApplicationContext;

		public AutoPublishAsyncTask(String applicationId, Context context) {
			mApplicationId = applicationId;
			mApplicationContext = context.getApplicationContext();
		}

		@Override
		protected Void doInBackground(Void... voids) {
			try {
				Facebook.PublishInstall(Facebook.this, mApplicationId, mApplicationContext);
			}
			catch (Exception e) {
				FacebookMisc.Logd("Facebook-publish", e.getMessage());
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			// always clear out the publisher to allow other invocations.
			synchronized (Facebook.this) {
				m_asyncTaskAutoPublish = null;
			}
		}
	}

	/**
	 * Callback interface for dialog requests.
	 *
	 */
	//==============================================================================
	public static interface FacebookDialogListener {

		/**
		 * Called when a dialog completes.
		 *
		 * Executed by the thread that initiated the dialog.
		 *
		 * @param values
		 *			Key-value string pairs extracted from the response.
		 */
		public void OnComplete(Bundle values);

		/**
		 * Called when a Facebook responds to a dialog with an error.
		 *
		 * Executed by the thread that initiated the dialog.
		 *
		 */
		public void OnFacebookError(FacebookError e);

		/**
		 * Called when a dialog has an error.
		 *
		 * Executed by the thread that initiated the dialog.
		 *
		 */
		public void OnError(DialogError e);

		/**
		 * Called when a dialog is canceled by the user.
		 *
		 * Executed by the thread that initiated the dialog.
		 *
		 */
		public void OnCancel();

	}
	
	/**
	 * Callback interface for service requests.
	 */
	//==============================================================================
	public static interface ServiceListener {

		/**
		 * Called when a service request completes.
		 * 
		 * @param values
		 *			Key-value string pairs extracted from the response.
		 */
		public void OnComplete(Bundle values);

		/**
		 * Called when a Facebook server responds to the request with an error.
		 */
		public void OnFacebookError(FacebookError e);

		/**
		 * Called when a Facebook Service responds to the request with an error.
		 */
		public void onError(Error e);

	}
	//==============================================================================

	public static final String FB_APP_SIGNATURE =
			"30820268308201d102044a9c4610300d06092a864886f70d0101040500307a310"
		+	"b3009060355040613025553310b30090603550408130243413112301006035504"
		+	"07130950616c6f20416c746f31183016060355040a130f46616365626f6f6b204"
		+	"d6f62696c653111300f060355040b130846616365626f6f6b311d301b06035504"
		+	"03131446616365626f6f6b20436f72706f726174696f6e3020170d30393038333"
		+	"13231353231365a180f32303530303932353231353231365a307a310b30090603"
		+	"55040613025553310b30090603550408130243413112301006035504071309506"
		+	"16c6f20416c746f31183016060355040a130f46616365626f6f6b204d6f62696c"
		+	"653111300f060355040b130846616365626f6f6b311d301b06035504031314466"
		+	"16365626f6f6b20436f72706f726174696f6e30819f300d06092a864886f70d01"
		+	"0101050003818d0030818902818100c207d51df8eb8c97d93ba0c8c1002c928fa"
		+	"b00dc1b42fca5e66e99cc3023ed2d214d822bc59e8e35ddcf5f44c7ae8ade50d7"
		+	"e0c434f500e6c131f4a2834f987fc46406115de2018ebbb0d5a3c261bd97581cc"
		+	"fef76afc7135a6d59e8855ecd7eacc8f8737e794c60a761c536b72b11fac8e603"
		+	"f5da1a2d54aa103b8a13c0dbc10203010001300d06092a864886f70d010104050"
		+	"0038181005ee9be8bcbb250648d3b741290a82a1c9dc2e76a0af2f2228f1d9f9c"
		+	"4007529c446a70175c5a900d5141812866db46be6559e2141616483998211f4a6"
		+	"73149fb2232a10d247663b26a9031e15f84bc1c74d141ff98a02d76f85b2c8ab2"
		+	"571b6469b232d8e768a7f7ca04f7abe4a775615916c07940656b58717457b42bd"
		+	"928a2";

}

//==============================================================================
