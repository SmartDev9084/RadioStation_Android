package androidcustom.radiostation.ui;

//import oauth.signpost.commonshttp.CommonsHttpOAuthConsumer;
//import oauth.signpost.commonshttp.CommonsHttpOAuthProvider;


import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.json.JSONObject;

import com.parse.FindCallback;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import android.app.ActionBar;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;
import androidcustom.radiostation.R;
import androidcustom.radiostation.facebook.FacebookLoginUtil;
import androidcustom.radiostation.facebook.FacebookLoginUtil.FacebookLoginFinishedListener;
import androidcustom.radiostation.global.Const;
import androidcustom.radiostation.global.EnvVariable;
import androidcustom.radiostation.google.ActivityLoginGoogle;
import androidcustom.radiostation.loader.LoaderImage;
import androidcustom.radiostation.twitter.TwitterLoginUtil;
import androidcustom.radiostation.twitter.TwitterLoginUtil.TwitterLoginFinishedListener;

//==============================================================================
public class ActivityLogin extends Activity {

	private		Button			m_btnFaceBook;
	private		Button			m_btnViewGoogle;
	private		Button			m_btnViewTwitter;

// {{ Facebook
	private		FacebookLoginUtil	m_facebookLoginUtil;
	private		String				m_strFacebookId;
	private		String				m_strFacebookName;
// }} Facebook

// {{ Twitter
	private	TwitterLoginUtil		m_twitterLoginUtil;	
	private	String					m_strTwitterName;
	private	String					m_strTwitterAvatarUrl;
// }} Twitter

	DialogProgress			m_dlgProgress;

	DialogLogin				m_dlgLogin;

	//------------------------------------------------------------------------------
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		TextView	txtViewGuest;
		TextView	txtViewRegister;
		TextView	txtViewLostPswd;

		if (LoaderImage.GetInstance() != null)
			LoaderImage.GetInstance().ClearCache();

		setContentView(R.layout.activity_login);
		setRequestedOrientation(getResources().getConfiguration().orientation);			// Won't Rotate

	// {{ MemberVariable Initiate
		m_facebookLoginUtil		= null;
		m_strFacebookId			= null;
		m_strFacebookName		= null;
		m_twitterLoginUtil		= null;	
		m_strTwitterName		= null;
		m_strTwitterAvatarUrl	= null;
		m_dlgLogin				= null;
	// }} MemberVariable Initiate

	// {{ TextView For Guest
		txtViewGuest	= (TextView)findViewById(R.id.ID_TXTVIEW_LOGIN_GUEST);
		txtViewGuest.setOnClickListener(
			new OnClickListener() {
				@Override
				public void onClick(View v) {
					ShowActivityRadio();
				}
			}
		);
	// }} TextView For Guest

	// {{ TextView For Register
		txtViewRegister	= (TextView)findViewById(R.id.ID_TXTVIEW_LOGIN_REGISTER);
		txtViewRegister.setOnClickListener(
			new OnClickListener() {
				@Override
				public void onClick(View v) {
					DialogRegister	dlgRegister = new DialogRegister(ActivityLogin.this);
					dlgRegister.setOnCancelListener(
						new OnCancelListener() {
							@Override
							public void onCancel(DialogInterface dialog) {
								if (((DialogRegister)dialog).IsRegistered())
									ShowActivityMain(Const.TYPE_LOGIN_NORMAL, Const.ID_USER_NONE, ((DialogRegister)dialog).GetUserName(), null);
								m_dlgLogin.Show();
							}
						}
					);
					m_dlgLogin.Hide();
					dlgRegister.show();
				}
			}
		);
	// }} TextView For Register
	
	// {{ TextView For Lost Password
		txtViewLostPswd	= (TextView)findViewById(R.id.ID_TXTVIEW_LOGIN_LOSTPSWD);
		txtViewLostPswd.setOnClickListener(
			new OnClickListener() {
				@Override
				public void onClick(View v) {
					DialogGetPswd		dlgGetPswd;
					dlgGetPswd = new DialogGetPswd(ActivityLogin.this);
					dlgGetPswd.setOnCancelListener(
						new OnCancelListener() {
							@Override
							public void onCancel(DialogInterface dialog) {
								m_dlgLogin.Show();
							}
						}
					);
					m_dlgLogin.Hide();
					dlgGetPswd.show();
				}
			}
		);
	// }} TextView For Lost Password

	// {{ Facebook Login Button
		m_btnFaceBook = (Button)findViewById(R.id.ID_BTN_LOGIN_FACEBOOK);
		m_btnFaceBook.setOnClickListener(
			new OnClickListener() {
				@Override
				public void onClick(View v) {
					if (m_facebookLoginUtil == null)
						m_facebookLoginUtil = new FacebookLoginUtil(ActivityLogin.this);
					m_facebookLoginUtil.SetLoginFinishedListener(
						new FacebookLoginFinishedListener() {
							@Override
							public void OnFacebookLoginFinished(boolean a_result) {
								if (a_result) {
									m_strFacebookId		= m_facebookLoginUtil.GetFacebookId();
									m_strFacebookName	= m_facebookLoginUtil.GetFacebookName();
									ShowActivityMain(Const.TYPE_LOGIN_FACEBOOK, m_strFacebookId, m_strFacebookName, null);
								}
							}
						}
					);
					try {
						m_facebookLoginUtil.SetFacebookConnection();
						if (m_facebookLoginUtil.IsFacebookSessionValid()) {
							m_strFacebookId		= m_facebookLoginUtil.GetFacebookId();
							m_strFacebookName	= m_facebookLoginUtil.GetFacebookName();
							if ((m_strFacebookId != null) && (m_strFacebookName != null)) {
								ShowActivityMain(Const.TYPE_LOGIN_FACEBOOK, m_strFacebookId, m_strFacebookName, null);
								return;
							}
						}
						m_facebookLoginUtil.StartAuthorizeFacebook();
					} catch (Exception e) { e.printStackTrace(); }
					/*
					if (m_dlgProgress == null)
						m_dlgProgress = new DialogProgress(ActivityLogin.this);
					m_dlgProgress.setCanceledOnTouchOutside(false);
//					m_dlgProgress.setCancelable(false);
					m_dlgProgress.show();
					*/
					/*
					List<String>	lstStrPermissions = Arrays.asList(
						"public_profile",
						"email"
					);
					*/
					/*
					try {
						ParseFacebookUtils.logIn(
//							lstStrPermissions,
							ActivityLogin.this,
							new LogInCallback() {
								@Override
								public void done(ParseUser a_parseUser, ParseException a_parseErr) {
									try {
										ActivityLogin.this.m_dlgProgress.dismiss();
										if (a_parseUser == null) {
											a_parseErr.printStackTrace();
										}
										else {
											ParseQuery<ParseUser> parseQuery = a_parseUser.getQuery();
											parseQuery.whereExists("username");
											parseQuery.findInBackground(
												new FindCallback<ParseUser>() {
													public void done(List<ParseUser>objects, ParseException e) {
														if (e == null) {
															int i;
															for (i = 0; i < objects.size(); i++) {
																m_strFacebookName = objects.get(i).getUsername();
															}
															ShowActivityMain(Const.TYPE_LOGIN_FACEBOOK, m_strFacebookId, m_strFacebookName, null);
														}
													}
												}
											);
										}
									} catch (Exception e) { e.printStackTrace(); }
								}
							}
						);
					} catch (Exception e) { e.printStackTrace(); }
					*/
				}
			}
		);
	// }} Facebook Login Button

	// {{ Google Login Button
		m_btnViewGoogle = (Button)findViewById(R.id.ID_BTN_LOGIN_GOOGLE);
		m_btnViewGoogle.setOnClickListener(
			new OnClickListener() {
				@Override
				public void onClick(View v) {
					Intent		intentLogin2Google;
					intentLogin2Google = new Intent(ActivityLogin.this, ActivityLoginGoogle.class);
					startActivity(intentLogin2Google);
				}
			}
		);
	// }} Google Login Button

	// {{ Twitter Login Button
		m_btnViewTwitter = (Button)findViewById(R.id.ID_BTN_LOGIN_TWITTER);
		m_btnViewTwitter.setOnClickListener(
			new OnClickListener() {
				@Override
				public void onClick(View v) {
					if (m_twitterLoginUtil == null)
						m_twitterLoginUtil = new TwitterLoginUtil(ActivityLogin.this);
					m_twitterLoginUtil.SetLoginFinishedListener(
						new TwitterLoginFinishedListener() {
							@Override
							public void OnTwitterLoginFinished() {
								m_strTwitterName		= m_twitterLoginUtil.GetUserName();
								m_strTwitterAvatarUrl	= m_twitterLoginUtil.GetAvatarUrl();
								ShowActivityMain(Const.TYPE_LOGIN_TWITTER, m_strTwitterName, m_strTwitterName, m_strTwitterAvatarUrl);
							}
						}
					);
					m_twitterLoginUtil.LoginTwitter();
				}
			}
		);
	// }} Twitter Login Button
	
	// {{ DialogLogin
		m_dlgLogin = new DialogLogin(this);
		m_dlgLogin.SetCommitListener(
			new DialogLogin.CommitListener() {
				@Override
				public void OnCommit() {
					if (m_dlgLogin.IsLogged())
						ShowActivityMain(Const.TYPE_LOGIN_NORMAL, Const.ID_USER_NONE, m_dlgLogin.GetUserName(), null);
				}
			}
		);
		m_dlgLogin.SetParentView((FrameLayout)findViewById(R.id.ID_FRMLYT_LOGIN));
		m_dlgLogin.Show();
	// }} DialogLogin
	}

	//------------------------------------------------------------------------------
	@Override
	protected void onResume()  {
		super.onResume();
		EnvVariable.KillCurrentSound();
	}

	//------------------------------------------------------------------------------
	@Override
	public void onBackPressed() {
		try { m_dlgProgress.dismiss(); } catch (Exception e) {}
		super.onBackPressed();
	}

	//------------------------------------------------------------------------------
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		try { m_dlgProgress.dismiss(); } catch (Exception e) {}
		ParseFacebookUtils.finishAuthentication(requestCode, resultCode, data);
		switch (requestCode) {
			case Const.REQUESTCODE_LOGIN_TWITTER :
				if (resultCode == Activity.RESULT_OK) {
					if (m_twitterLoginUtil != null)
					m_twitterLoginUtil.GetAccessToken(data.getStringExtra(Const.KEY_TWITTER_CALLBACK_URL));
				}
				break;
			case Const.REQUESTCODE_LOGIN_FACEBOOK :
				if (m_facebookLoginUtil != null) {
					m_facebookLoginUtil.AuthorizeCallback(requestCode, resultCode, data);
				}
				break;
			default:
				break;
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	//------------------------------------------------------------------------------
	public void ShowActivityMain(int a_iLoginType, String a_strUserId, String a_strUserName, String a_strAvatarUrl) {
		Intent		intentLogin2Main;
		Bundle		bundle;
		intentLogin2Main = new Intent(ActivityLogin.this, ActivityMain.class);
		bundle = new Bundle();
		bundle.putInt(Const.KEY_LOGINTYPE,		a_iLoginType);
		bundle.putString(Const.KEY_USERID,		a_strUserId);
		bundle.putString(Const.KEY_USERNAME,	a_strUserName);
		bundle.putString(Const.KEY_AVATAR,		a_strAvatarUrl);
		intentLogin2Main.putExtras(bundle);

		startActivity(intentLogin2Main);
	}

	//------------------------------------------------------------------------------
	public void ShowActivityRadio() {
		Intent		intentLogin2Radio;
		Bundle		bundle;
		intentLogin2Radio = new Intent(ActivityLogin.this, ActivityRadio.class);
		bundle = new Bundle();
		bundle.putInt(Const.KEY_LOGINTYPE,		Const.TYPE_LOGIN_GUEST);
		intentLogin2Radio.putExtras(bundle);

		startActivity(intentLogin2Radio);
	}
	//------------------------------------------------------------------------------

}
//==============================================================================
