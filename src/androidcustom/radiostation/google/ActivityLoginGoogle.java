package androidcustom.radiostation.google;

import java.io.InputStream;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender.SendIntentException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidcustom.radiostation.R;
import androidcustom.radiostation.global.Const;
import androidcustom.radiostation.ui.ActivityMain;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;

//==============================================================================
public class ActivityLoginGoogle extends Activity
	implements	OnClickListener,
				ConnectionCallbacks,
				OnConnectionFailedListener {

	private static final int RC_SIGN_IN = 0;

	// Profile pic image size in pixels
	private static final int PROFILE_PIC_SIZE = 400;

	// Google client to interact with Google API
	private GoogleApiClient mGoogleApiClient;

	/**
	 * A flag indicating that a PendingIntent is in progress and prevents us
	 * from starting further intents.
	 */
	private boolean mIntentInProgress;

	private boolean mSignInClicked;

	private ConnectionResult mConnectionResult;

	private SignInButton btnSignIn;
	private Button btnSignOut, btnRevokeAccess;
	private ImageView imgProfilePic;
	private TextView txtName, txtEmail;
	private LinearLayout llProfileLayout;

	private	String		m_strUserName;
	private	String		m_strAvatarUrl;

	//------------------------------------------------------------------------------
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login_google);

		m_strUserName	= null;
		m_strAvatarUrl	= null;
		btnSignIn = (SignInButton) findViewById(R.id.btn_sign_in);
		btnSignOut = (Button) findViewById(R.id.btn_sign_out);
		btnRevokeAccess = (Button) findViewById(R.id.btn_revoke_access);
		imgProfilePic = (ImageView) findViewById(R.id.imgProfilePic);
		txtName = (TextView) findViewById(R.id.txtName);
		txtEmail = (TextView) findViewById(R.id.txtEmail);
		llProfileLayout = (LinearLayout) findViewById(R.id.llProfile);

		// Button click listeners
		btnSignIn.setOnClickListener(this);
		btnSignOut.setOnClickListener(this);
		btnRevokeAccess.setOnClickListener(this);

		mGoogleApiClient = new GoogleApiClient
				.Builder(this)
				.addConnectionCallbacks(this)
				.addOnConnectionFailedListener(this)
				.addApi(Plus.API)
				.addScope(Plus.SCOPE_PLUS_LOGIN).build();
	}

	//------------------------------------------------------------------------------
	@Override
	protected void onStart() {
		super.onStart();
		mGoogleApiClient.connect();
	}

	//------------------------------------------------------------------------------
	@Override
	protected void onStop() {
		super.onStop();
		if (mGoogleApiClient.isConnected()) {
			mGoogleApiClient.disconnect();
		}
	}

	//------------------------------------------------------------------------------
	private void ResolveSignInError() {
		if (mConnectionResult.hasResolution()) {
			try {
				mIntentInProgress = true;
				mConnectionResult.startResolutionForResult(this, RC_SIGN_IN);
			}
			catch (SendIntentException e) {
				mIntentInProgress = false;
				mGoogleApiClient.connect();
			}
		}
	}

	//------------------------------------------------------------------------------
	@Override
	public void onConnectionFailed(ConnectionResult result) {
		if (!result.hasResolution()) {
			GooglePlayServicesUtil.getErrorDialog(result.getErrorCode(), this, 0).show();
			return;
		}

		if (!mIntentInProgress) {
			// Store the ConnectionResult for later usage
			mConnectionResult = result;

			if (mSignInClicked) {
				// The user has already clicked 'sign-in' so we attempt to
				// resolve all
				// errors until the user is signed in, or they cancel.
				ResolveSignInError();
			}
		}

	}

	//------------------------------------------------------------------------------
	@Override
	protected void onActivityResult(int requestCode, int responseCode,
			Intent intent) {
		if (requestCode == RC_SIGN_IN) {
			if (responseCode != RESULT_OK) {
				mSignInClicked = false;
			}

			mIntentInProgress = false;

			if (!mGoogleApiClient.isConnecting()) {
				mGoogleApiClient.connect();
			}
		}
	}

	//------------------------------------------------------------------------------
	@Override
	public void onConnected(Bundle arg0) {
		mSignInClicked = false;
		Toast.makeText(this, "User is connected!", Toast.LENGTH_LONG).show();

		// Get user's information
		GetProfileInformation();

		// Update the UI after signin
		UpdateUi(true);

	}

	//------------------------------------------------------------------------------
	private void UpdateUi(boolean isSignedIn) {
		if (isSignedIn) {
//			btnSignIn.setVisibility(View.GONE);
//			btnSignOut.setVisibility(View.VISIBLE);
//			btnRevokeAccess.setVisibility(View.VISIBLE);
//			llProfileLayout.setVisibility(View.VISIBLE);
			Intent		intentGoogle2Main;
			Bundle		bundle;
			intentGoogle2Main = new Intent(ActivityLoginGoogle.this, ActivityMain.class);
			bundle = new Bundle();
			bundle.putInt(Const.KEY_LOGINTYPE, Const.TYPE_LOGIN_GOOGLE);
			bundle.putString(Const.KEY_USERID,		m_strUserName);
			bundle.putString(Const.KEY_USERNAME,	m_strUserName);
			bundle.putString(Const.KEY_AVATAR,		m_strAvatarUrl);
			intentGoogle2Main.putExtras(bundle);
			startActivity(intentGoogle2Main);
			finish();
		}
		else {
			btnSignIn.setVisibility(View.VISIBLE);
			btnSignOut.setVisibility(View.GONE);
			btnRevokeAccess.setVisibility(View.GONE);
			llProfileLayout.setVisibility(View.GONE);
		}
	}

	//------------------------------------------------------------------------------
	private void GetProfileInformation() {
		try {
			if (Plus.PeopleApi.getCurrentPerson(mGoogleApiClient) != null) {
				Person currentPerson = Plus.PeopleApi.getCurrentPerson(mGoogleApiClient);
				m_strUserName	= currentPerson.getDisplayName();
				m_strAvatarUrl	= currentPerson.getImage().getUrl();
				String personGooglePlusProfile = currentPerson.getUrl();
				String email = Plus.AccountApi.getAccountName(mGoogleApiClient);

				txtName.setText(m_strUserName);
				txtEmail.setText(email);

				// by default the profile url gives 50x50 px image only
				// we can replace the value with whatever dimension we want by
				// replacing sz=X
				m_strAvatarUrl = m_strAvatarUrl.substring(0, m_strAvatarUrl.length() - 2) + PROFILE_PIC_SIZE;

				new LoadProfileImage(imgProfilePic).execute(m_strAvatarUrl);

			}
			else {
				Toast.makeText(
					getApplicationContext(),
					"Person information is null", Toast.LENGTH_LONG
				).show();
			}
		} catch (Exception e) {}
	}

	//------------------------------------------------------------------------------
	@Override
	public void onConnectionSuspended(int arg0) {
		mGoogleApiClient.connect();
		UpdateUi(false);
	}

	//------------------------------------------------------------------------------
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
//		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	//------------------------------------------------------------------------------
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_sign_in:
			// Signin button clicked
			SignInWithGplus();
			break;
		case R.id.btn_sign_out:
			// Signout button clicked
			SignOutFromGplus();
			break;
		case R.id.btn_revoke_access:
			// Revoke access button clicked
			RevokeGplusAccess();
			break;
		}
	}

	//------------------------------------------------------------------------------
	private void SignInWithGplus() {
		if (!mGoogleApiClient.isConnecting()) {
			mSignInClicked = true;
			ResolveSignInError();
		}
	}

	//------------------------------------------------------------------------------
	private void SignOutFromGplus() {
		if (mGoogleApiClient.isConnected()) {
			Plus.AccountApi.clearDefaultAccount(mGoogleApiClient);
			mGoogleApiClient.disconnect();
			mGoogleApiClient.connect();
			UpdateUi(false);
		}
	}

	//------------------------------------------------------------------------------
	private void RevokeGplusAccess() {
		if (mGoogleApiClient.isConnected()) {
			Plus.AccountApi.clearDefaultAccount(mGoogleApiClient);
			Plus.AccountApi.revokeAccessAndDisconnect(mGoogleApiClient).setResultCallback(
				new ResultCallback<Status>() {
					@Override
					public void onResult(Status arg0) {
						mGoogleApiClient.connect();
						UpdateUi(false);
					}
				}
			);
		}
	}

	//==============================================================================
	private class LoadProfileImage extends AsyncTask<String, Void, Bitmap> {
		ImageView bmImage;

		public LoadProfileImage(ImageView bmImage) {
			this.bmImage = bmImage;
		}
		@Override
		protected Bitmap doInBackground(String... urls) {
			String	strUrlDisplay = urls[0];
			Bitmap	mIcon11 = null;
			try {
				InputStream in = new java.net.URL(strUrlDisplay).openStream();
				mIcon11 = BitmapFactory.decodeStream(in);
			}
			catch (Exception e) {}
			return mIcon11;
		}
		@Override
		protected void onPostExecute(Bitmap result) {
			bmImage.setImageBitmap(result);
		}
	}

	//==============================================================================

}

//==============================================================================
