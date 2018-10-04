package androidcustom.radiostation.ui;

import java.io.File;

import com.arellomobile.android.push.BasePushMessageReceiver;
import com.arellomobile.android.push.PushManager;
import com.arellomobile.android.push.utils.RegisterBroadcastReceiver;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.widget.Toast;
import androidcustom.radiostation.R;
import androidcustom.radiostation.db.DbApi;
import androidcustom.radiostation.global.Const;
import androidcustom.radiostation.global.EnvVariable;
import androidcustom.radiostation.global.UtilDisplay;
import androidcustom.radiostation.http.HttpApi;
import androidcustom.radiostation.http.HttpApi.HttpApiListener;
import androidcustom.radiostation.loader.LoaderImage;
import androidcustom.radiostation.login.LoginInfo;

//==============================================================================
public class ActivitySplash extends Activity {

	PushManager		m_pushManager;
	LoginInfo		m_loginInfo;
	boolean			m_bAutoLoggedIn;

	//------------------------------------------------------------------------------
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash);

		setRequestedOrientation(getResources().getConfiguration().orientation);			// Won't Rotate
		
		DbApi.Create(getApplicationContext());
		LoaderImage.Create(getApplicationContext());

	// {{ Set EnvVariable
		EnvVariable.CurrentMainItem			= Const.ITEM_NONE;
		EnvVariable.CurrentSubPlaylistType	= Const.TYPE_NONE;
		EnvVariable.CurrentSubPodcastType	= Const.TYPE_NONE;
		EnvVariable.SizeDisplay				= UtilDisplay.GetScreenSize(this);
	// }} Set EnvVariable

	// {{ PushWoosh
		RegisterReceivers();									// Register receivers for push notifications
		m_pushManager = PushManager.getInstance(this);			// Create and start push manager
		PushManager.startTrackingGeoPushes(this);
		// Start push manager, this will count app open for Pushwoosh stats as well
		try {
			m_pushManager.onStartup(this);
		}
		catch(Exception e) {
			// push notifications are not available or AndroidManifest.xml is not configured properly
		}

		// Register for push!
		m_pushManager.registerForPushNotifications();
		CheckMessage(getIntent());
	// }} PushWoosh

		m_bAutoLoggedIn = false;

		CreateDirectory();
		CheckLogin();
	}

	//------------------------------------------------------------------------------
	private void CreateDirectory() {
		File fileDirectory;
		fileDirectory = new File(Const.APP_DIR);
		if (fileDirectory.exists())
			fileDirectory.delete();
		fileDirectory.mkdir();
	}

	//------------------------------------------------------------------------------
	private void CheckLogin() {
		if (DbApi.GetInstance() == null) {
			ShowActivityLogin();
			return;
		}
		if (DbApi.GetInstance().HasLoginInfo() == false) {
			ShowActivityLogin();
			return;
		}

		m_loginInfo = DbApi.GetInstance().GetLoginInfo();
		HttpApi		httpApi = new HttpApi();

		httpApi.SetApiType(Const.TYPE_LOGIN);
		httpApi.SetHttpApiListener(
			new HttpApiListener() {
				@Override
				public void OnHttpApiResult(String a_strResult, int a_iType) {
					if (a_strResult == null)
						ShowActivityLogin();
					else {
						if (a_strResult.contains("<respond>1</respond>")) {
							ShowActivityMain();
						}
					}
				}
			}
		);
		httpApi.StartLogin(m_loginInfo.GetUserName(), m_loginInfo.GetPassword());
	}

	//------------------------------------------------------------------------------
	public void ShowActivityLogin() {
		Intent		intentSplash2Login;
		intentSplash2Login = new Intent(ActivitySplash.this, ActivityLogin.class);
		startActivity(intentSplash2Login);
		finish();
	}

	//------------------------------------------------------------------------------
	public void ShowActivityMain() {
		Intent		intentSplash2Main;
		Bundle		bundle;
		intentSplash2Main = new Intent(ActivitySplash.this, ActivityMain.class);
		bundle = new Bundle();
		bundle.putInt(Const.KEY_LOGINTYPE,		Const.TYPE_LOGIN_NORMAL);
		bundle.putString(Const.KEY_USERID,		Const.ID_USER_NONE);
		bundle.putString(Const.KEY_USERNAME,	m_loginInfo.GetUserName());
		bundle.putString(Const.KEY_AVATAR,		null);
		intentSplash2Main.putExtras(bundle);

		startActivity(intentSplash2Main);
		finish();
	}

// {{ PushWoosh

	//------------------------------------------------------------------------------
	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		setIntent(intent);
		CheckMessage(intent);
	}

	//------------------------------------------------------------------------------
	@Override
	public void onResume() {
		super.onResume();
	}
	 
	//------------------------------------------------------------------------------
	@Override
	public void onPause() {
		super.onPause();

		// Unregister receivers on pause
		UnregisterReceivers();
	}

	//------------------------------------------------------------------------------
	// Registration receiver
	BroadcastReceiver mBroadcastReceiver = new RegisterBroadcastReceiver() {
		@Override
		public void onRegisterActionReceive(Context context, Intent intent) {
			CheckMessage(intent);
		}
	};

	//------------------------------------------------------------------------------
	// Push message receiver
	private BroadcastReceiver mReceiver = new BasePushMessageReceiver() {
		@Override
		protected void onMessageReceive(Intent intent) {
			//JSON_DATA_KEY contains JSON payload of push notification.
			ShowMessage("push message is " + intent.getExtras().getString(JSON_DATA_KEY));
		}
	};

	//------------------------------------------------------------------------------
	// Registration of the receivers
	public void RegisterReceivers() {
		IntentFilter intentFilter = new IntentFilter(getPackageName() + ".action.PUSH_MESSAGE_RECEIVE");
		registerReceiver(mReceiver, intentFilter);
		registerReceiver(mBroadcastReceiver, new IntentFilter(getPackageName() + "." + PushManager.REGISTER_BROAD_CAST_ACTION));	   
	}

	//------------------------------------------------------------------------------
	public void UnregisterReceivers() {
		// Unregister receivers on pause
		try { unregisterReceiver(mReceiver); } catch (Exception e) { /* pass */ }
		try { unregisterReceiver(mBroadcastReceiver); } catch (Exception e) { /* pass through */ }
	}

	//------------------------------------------------------------------------------
	private void CheckMessage(Intent intent) {
		if (null != intent) {
			if (intent.hasExtra(PushManager.PUSH_RECEIVE_EVENT)) {
				ShowMessage("push message is " + intent.getExtras().getString(PushManager.PUSH_RECEIVE_EVENT));
			}
			else if (intent.hasExtra(PushManager.REGISTER_EVENT)) {
//				ShowMessage("register");
			}
			else if (intent.hasExtra(PushManager.UNREGISTER_EVENT)) {
				ShowMessage("unregister");
			}
			else if (intent.hasExtra(PushManager.REGISTER_ERROR_EVENT)) {
				ShowMessage("register error");
			}
			else if (intent.hasExtra(PushManager.UNREGISTER_ERROR_EVENT)) {
				ShowMessage("unregister error");
			}

			ResetIntentValues();
		}
	}

	//------------------------------------------------------------------------------
	/**
	 * Will check main Activity intent and if it contains any PushWoosh data, will clear it
	 */
	private void ResetIntentValues() {
		Intent mainAppIntent = getIntent();

		if (mainAppIntent.hasExtra(PushManager.PUSH_RECEIVE_EVENT)) {
			mainAppIntent.removeExtra(PushManager.PUSH_RECEIVE_EVENT);
		}
		else if (mainAppIntent.hasExtra(PushManager.REGISTER_EVENT)) {
			mainAppIntent.removeExtra(PushManager.REGISTER_EVENT);
		}
		else if (mainAppIntent.hasExtra(PushManager.UNREGISTER_EVENT)) {
			mainAppIntent.removeExtra(PushManager.UNREGISTER_EVENT);
		}
		else if (mainAppIntent.hasExtra(PushManager.REGISTER_ERROR_EVENT)) {
			mainAppIntent.removeExtra(PushManager.REGISTER_ERROR_EVENT);
		}
		else if (mainAppIntent.hasExtra(PushManager.UNREGISTER_ERROR_EVENT)) {
			mainAppIntent.removeExtra(PushManager.UNREGISTER_ERROR_EVENT);
		}

		setIntent(mainAppIntent);
	}

	//------------------------------------------------------------------------------
	private void ShowMessage(String message) {
		Toast.makeText(this, message, Toast.LENGTH_LONG).show();
	}

// }} PushWoosh
}
//==============================================================================
