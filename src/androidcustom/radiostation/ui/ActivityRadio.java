package androidcustom.radiostation.ui;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidcustom.radiostation.R;
import androidcustom.radiostation.facebook.Facebook;
import androidcustom.radiostation.global.Const;
import androidcustom.radiostation.global.EnvVariable;
import androidcustom.radiostation.http.HttpApi;
import androidcustom.radiostation.http.HttpApi.HttpApiListener;
import androidcustom.radiostation.loader.LoaderImage;
import androidcustom.radiostation.multimedia.MultimediaInfo;
import androidcustom.radiostation.twitter.TwitterUtil;

import com.un4seen.bass.BASS;

//==============================================================================
public class ActivityRadio extends Activity {

	private	final	String		URL_RADIO = "http://174.142.97.228:9000";
	private			int			m_iLoginType;			// Login Type, It would be TYPE_LOGIN_GUEST
	private			int			m_iRequestCount;		// request number/counter
	private			int			m_iChannel;				// stream handle
	
	private	ImageView		m_imageViewBack;

	private			Runnable	m_runTimer;
	private			Object		m_objLock		= new Object();
	private			Thread		m_thread		= null;

	private		boolean		m_bIsPlaying;
	private		boolean		m_bIsSound;
	private		ImageView	m_imageViewStatus;
	private		ImageView	m_imageViewSound;
	private		TextView	m_textViewTitle;
//	private		DialogProgress		m_dlgProgress;

	private		String	m_strTitle;
	private		String	m_strArtist;

	private		ArrayList<MultimediaInfo>		m_arrMultimediaInfo;	// For Background Image
	private		int								m_iCurBackImgIndex;		// Index in m_arrMultimediaInfo;

	private CountDownTimer				m_timer;
	private	int							m_iCountTimeForGuest;		// Play Time When Guest Login
	private	int							m_iCountTimeForBack;		// Back Ground Change
	private	final	int		DELAY_REFRESH_BACKGROUND	= 60 * 1000;			// BackGround Refresh
	private	final	int		DELAY_GUEST_PLAY			= 60 * 60 * 1000;		// Guest Play TimeOut

	private SharedPreferences sharedPrefs;			// For Sharing text to twitter

	//------------------------------------------------------------------------------
	BASS.SYNCPROC MetaSync = new BASS.SYNCPROC() {
		public void SYNCPROC(int handle, int channel, int data, Object user) {
			runOnUiThread(
				new Runnable() {
					public void run() {
						DoMeta();
					}
				}
			);
		}
	};

	//------------------------------------------------------------------------------
	BASS.SYNCPROC EndSync = new BASS.SYNCPROC() {
		public void SYNCPROC(int handle, int channel, int data, Object user) {
			runOnUiThread(
				new Runnable() {
					public void run() {
					}
				}
			);
		}
	};

	//------------------------------------------------------------------------------
	BASS.DOWNLOADPROC StatusProc = new BASS.DOWNLOADPROC() {
		public void DOWNLOADPROC(ByteBuffer buffer, int length, Object user) {
			if (buffer != null && length == 0 && (Integer)user == m_iRequestCount) {
				// Got HTTP/ICY tags, and this is still the current request
				String[] arrStr;
				try {
					CharsetDecoder	charDecoder	= Charset.forName("ISO-8859-1").newDecoder();
					ByteBuffer		bufferTemp	= ByteBuffer.allocate(buffer.limit());
					// CharsetDecoder doesn't like a direct buffer?
					bufferTemp.put(buffer);
					bufferTemp.position(0);
					// Convert buffer to string array
					arrStr = charDecoder.decode(bufferTemp).toString().split("\0");
				} catch (Exception e) { return; }

				runOnUiThread(
					new RunnableParam(arrStr[0]) {
						// First string is status
						public void run() {
//							((TextView)findViewById(R.id.music_title)).setText(radioName);
						}
					}
				);
			}
		}
	};

// {{===========================================================================
	class RunnableParam implements Runnable {
		Object	m_objParam;

		public RunnableParam(Object a_objParam) {
			m_objParam = a_objParam;
		}

		public void run() {
		}
	}
// }}===========================================================================

// {{===========================================================================
	public class OpenURL implements Runnable {

		private String m_strUrl;
		Handler		m_handler = new Handler();

		//------------------------------------------------------------------------------
		public OpenURL(String a_strUrl) {
			m_strUrl = a_strUrl;
		}

		//------------------------------------------------------------------------------
		public void run() {
			int iRequestCount;
			synchronized(m_objLock) {
				// Make sure only 1 thread at a time can do the following
				// Increment the request counter for this request
				iRequestCount = ++m_iRequestCount;
			}

			BASS.BASS_StreamFree(m_iChannel); // close old stream
			runOnUiThread(
				new Runnable() {
					public void run() {
//						((TextView)findViewById(R.id.music_title)).setText("A ligar...");
					}
				}
			);

			// Open URL
			int iChannel = BASS.BASS_StreamCreateURL(
				m_strUrl,
				0,
				BASS.BASS_STREAM_BLOCK | BASS.BASS_STREAM_STATUS | BASS.BASS_STREAM_AUTOFREE,
				StatusProc,
				iRequestCount
			);

			synchronized(m_objLock) {
				if (iRequestCount != m_iRequestCount) {
					// There is a newer request, discard this stream
					if (iChannel != 0)
						BASS.BASS_StreamFree(iChannel);
					return;
				}
				m_iChannel = iChannel;		// Current stream
			}

			if (m_iChannel == 0) {
				// Failed to open
				runOnUiThread(
					new Runnable() {
						public void run() {
//							((TextView)findViewById(R.id.music_title)).setText("Ave fm a sua radio!");
						}
					}
				);
				Error("Ocurreu um erro ao reproduzir.");
			}
			else
				m_handler.postDelayed(m_runTimer, 50);		// Start prebuffer monitoring
		}
		//------------------------------------------------------------------------------
	}
// }}===========================================================================

	//------------------------------------------------------------------------------
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Runnable		runGetBackImage;
		Thread			threadGetBackImage;

		setContentView(R.layout.activity_radio);
		setRequestedOrientation(getResources().getConfiguration().orientation);			// Won't Rotate

		if (LoaderImage.GetInstance() != null)
			LoaderImage.GetInstance().ClearCache();

		m_iLoginType	= getIntent().getExtras().getInt(Const.KEY_LOGINTYPE);

		m_bIsPlaying	= true;
		m_bIsSound		= true;
		m_thread		= null;
		m_arrMultimediaInfo = new ArrayList<MultimediaInfo>();
		m_iCurBackImgIndex = -1;

		SetLayout();

//		m_dlgProgress = new DialogProgress(this);
//		m_dlgProgress.setCanceledOnTouchOutside(false);
//		m_dlgProgress.setCancelable(false);
		m_imageViewBack = (ImageView)findViewById(R.id.ID_IMGVIEW_RADIO_BACKGROUND);

	// {{ Init for Radio
		// Initialize output device
		if (!BASS.BASS_Init(-1, 44100, 0)) {
			Error("Erro ao inicializar a aplication.");
			return;
		}

		BASS.BASS_SetConfig(BASS.BASS_CONFIG_NET_PLAYLIST,	1);		// enable playlist processing
		BASS.BASS_SetConfig(BASS.BASS_CONFIG_NET_PREBUF,	1);		// minimize automatic pre-buffering, so we can do it (and display it) instead

		m_runTimer = new Runnable() {
			Handler		m_handler = new Handler();
			public void run() {
				// Monitor prebuffering progress
				long lProgress = BASS.BASS_StreamGetFilePosition(m_iChannel, BASS.BASS_FILEPOS_BUFFER)*100/BASS.BASS_StreamGetFilePosition(m_iChannel, BASS.BASS_FILEPOS_END); // percentage of buffer filled
				if ((lProgress > 75) || (BASS.BASS_StreamGetFilePosition(m_iChannel, BASS.BASS_FILEPOS_CONNECTED) == 0)) {
//					m_dlgProgress.hide();
					// Over 75% full (or end of download)
					// Get the broadcast name and URL
					String[] arrStrIcy = (String[])BASS.BASS_ChannelGetTags(m_iChannel, BASS.BASS_TAG_ICY);
					if (arrStrIcy == null)		// No ICY tags, try HTTP
						arrStrIcy = (String[])BASS.BASS_ChannelGetTags(m_iChannel, BASS.BASS_TAG_HTTP);
					if (arrStrIcy != null) {
						for (String str: arrStrIcy) {
							if (str.regionMatches(true, 0, "icy-name:", 0, 9))
								m_textViewTitle.setText(str.substring(9));
							else if (str.regionMatches(true, 0, "icy-url:", 0, 8))
								m_textViewTitle.setText(str.substring(8));
						}
					}
					else
						m_textViewTitle.setText("");
					// get the stream title and set sync for subsequent titles
					DoMeta();
					BASS.BASS_ChannelSetSync(m_iChannel, BASS.BASS_SYNC_META,		0, MetaSync,	0);		// Shoutcast
					BASS.BASS_ChannelSetSync(m_iChannel, BASS.BASS_SYNC_OGG_CHANGE,	0, MetaSync,	0);		// Icecast/OGG
					BASS.BASS_ChannelSetSync(m_iChannel, BASS.BASS_SYNC_END,		0, EndSync,		0);		// Set sync for end of stream
					BASS.BASS_ChannelPlay(m_iChannel, false);												// Play
				}
				else {
					m_textViewTitle.setText(String.format("buffering... %d%%", lProgress));
					m_handler.postDelayed(this, 50);
				}
			}
		};
	// }} Init for Radio

	// {{ Get Background Image
		runGetBackImage = new Runnable() {
			public void run() {
				HttpApi		m_httpApi = new HttpApi();
				m_httpApi.SetApiType(Const.TYPE_PHOTO);
				m_httpApi.SetHttpApiListener(
					new HttpApiListener() {
						@Override
						public void OnHttpApiResult(String a_strResult, int a_iType) {
							JSONObject			jsonObj;
							JSONArray			jsonArrSongList;
							int					iIndex;
		
							if (a_strResult == null)		return;
							if (a_strResult.length() == 0)	return;
		
							try {
								jsonObj = new JSONObject(a_strResult);
								jsonArrSongList = jsonObj.getJSONArray("imagelist");
								for (iIndex = 0; iIndex < jsonArrSongList.length(); iIndex++) {
									MultimediaInfo		multimediaInfo = new MultimediaInfo();
									JSONObject			jsonMultimediaData = jsonArrSongList.getJSONObject(iIndex);
									try {
										multimediaInfo.SetIndex(iIndex);
										multimediaInfo.SetType(a_iType);
										multimediaInfo.SetTitle(jsonMultimediaData.getString("title"));
										multimediaInfo.SetLink(jsonMultimediaData.getString("link"));
									} catch (Exception e) {}
									m_arrMultimediaInfo.add(multimediaInfo);
								}
							}
							catch (JSONException e1) {}
							catch (Exception e2) {}
						}
					}
				);
				m_httpApi.StartGetPhoto();
			}
		};
		threadGetBackImage = new Thread(runGetBackImage);
		threadGetBackImage.start();
	// }} Get Background image

	// {{ Timer
		m_iCountTimeForGuest	= 0;
		m_iCountTimeForBack		= 0;
		m_timer = new CountDownTimer(10 * 1000, 10 * 1000) {
			@Override
			public void onFinish() {
				m_iCountTimeForGuest	+= 10 * 1000;
				m_iCountTimeForBack		+= 10 * 1000;
				if (m_iCountTimeForGuest >= DELAY_GUEST_PLAY) {
					if (m_iLoginType == Const.TYPE_LOGIN_GUEST)
//						ActivityRadio.this.finish();
					m_iCountTimeForGuest = 0;
				}
				if (m_iCountTimeForBack >= DELAY_REFRESH_BACKGROUND) {
					m_iCountTimeForBack = 0;
					m_iCurBackImgIndex++;
					if (m_iCurBackImgIndex >= m_arrMultimediaInfo.size())
						m_iCurBackImgIndex = 0;
					if (m_arrMultimediaInfo.size() > 0) {
						if (LoaderImage.GetInstance() != null)
							LoaderImage.GetInstance().DisplayImage(
								m_imageViewBack,
								m_arrMultimediaInfo.get(m_iCurBackImgIndex).GetLink()
							);
					}
				}
				start();
			}
			@Override
			public void onTick(long millisUntilFinished) {
			}
		};
		m_timer.start();
	// }} Timer

		PlayRadio();
	}

	//------------------------------------------------------------------------------
	@Override
	public void onDestroy() {
//		BASS.BASS_Free();
//		try { m_thread.stop(); } catch (Exception e) {}
		if (m_timer != null)
			m_timer.cancel();
		if (LoaderImage.GetInstance() != null)
			LoaderImage.GetInstance().ClearCache();
		super.onDestroy();
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
					ActivityRadio.this.finish();
				}
			}
		);

		textView = (TextView)findViewById(R.id.ID_TXTVIEW_TITLEBAR_PLAY_TITLE);
		textView.setText("Radio");
	// }} Title Bar

	// {{ ImageView Status
		m_imageViewStatus = (ImageView)findViewById(R.id.ID_IMGVIEW_RADIO_BTN_STATUS);
		m_imageViewStatus.setOnClickListener(
			new View.OnClickListener() {
				public void onClick(View v) {
					Drawable	drawable;
					if (m_bIsPlaying) {
						drawable = ActivityRadio.this.getResources().getDrawable(R.drawable.img_btn_radio_play);
						m_imageViewStatus.setImageDrawable(drawable);
						StopRadio();
					}
					else {
						drawable = ActivityRadio.this.getResources().getDrawable(R.drawable.img_btn_radio_stop);
						m_imageViewStatus.setImageDrawable(drawable);
						PlayRadio();
					}
					m_bIsPlaying = !m_bIsPlaying;
				}
			}
		);
	// }} ImageView Status

	// {{ ImageView Sound
		m_imageViewSound = (ImageView)findViewById(R.id.ID_IMGVIEW_RADIO_BTN_SOUND);
		m_imageViewSound.setOnClickListener(
			new View.OnClickListener() {
				public void onClick(View v) {
					Drawable	drawable;
					if (m_bIsSound) {
						drawable = ActivityRadio.this.getResources().getDrawable(R.drawable.img_btn_sound_off);
						m_imageViewSound.setImageDrawable(drawable);
						TurnOffSound();
					}
					else {
						drawable = ActivityRadio.this.getResources().getDrawable(R.drawable.img_btn_sound_on);
						m_imageViewSound.setImageDrawable(drawable);
						TurnOnSound();
					}
					m_bIsSound = !m_bIsSound;
				}
			}
		);
	// }} ImageView Sound

	// {{ TextView Radio Title
		m_textViewTitle = (TextView)findViewById(R.id.ID_TXTVIEW_RADIO_TITLE);
		m_textViewTitle.setText("");
	// }} TextView Radio Title

	// {{ Button Share Facebook
		button = (Button)findViewById(R.id.ID_BTN_RADIO_FACEBOOK);
		button.setOnClickListener(
			new OnClickListener() {
				@Override
				public void onClick(View view) {
				// {{
					try {
						Intent intentShare = new Intent(android.content.Intent.ACTION_SEND);
						intentShare.setType("image/*");
						intentShare.setPackage("com.facebook.katana");
						intentShare.putExtra(Intent.EXTRA_TEXT,		"#NP '" + m_strTitle + "' on 1triberadio app #1triberadio @1triberadio");
						intentShare.putExtra(Intent.EXTRA_TITLE,	"#NP '" + m_strTitle + "' on 1triberadio app #1triberadio @1triberadio");
						intentShare.putExtra(Intent.EXTRA_SUBJECT,	"#NP '" + m_strTitle + "' on 1triberadio app #1triberadio @1triberadio");
						startActivity(Intent.createChooser(intentShare, "SHARE"));
					}
					catch (Exception e) {
						String	strShareUrl = "https://www.facebook.com/sharer/sharer.php?u=" + "#NP '" + m_strTitle + "' on 1triberadio app #1triberadio @1triberadio";
						Intent	intentShare = new Intent(Intent.ACTION_VIEW, Uri.parse(strShareUrl));
						startActivity(intentShare);
					}
				// }}

				// {{
//					ShareTextToFacebook("#NP '" + m_strTitle + "' on 1triberadio app #1triberadio @1triberadio");
				// }}

				// {{
					/*
					Bundle params = new Bundle();
					params.putString("caption", "Share Radio");
					params.putString("message", "#NP '" + m_strTitle + "' on 1triberadio app #1triberadio @1triberadio");

					Request request = new Request(Session.getActiveSession(), "me/feed", params, HttpMethod.POST);
					request.setCallback(
						new Request.Callback() {
						    @Override
						    public void onCompleted(Response response) {
						        if (response.getError() == null) {
									Toast.makeText(ActivityRadio.this, "This radio is shared to Facebook!", Toast.LENGTH_SHORT).show();
						        }
						        else {
									Toast.makeText(ActivityRadio.this, "Failed to post to wall!", Toast.LENGTH_SHORT).show();
						        }
						    }
						}
					);
					request.executeAsync();
					*/
				// }}
				}
			}
		);
	// }} Button Share Facebooksssss

	// {{ Button Share Twitter
		button = (Button)findViewById(R.id.ID_BTN_RADIO_TWITTER);
		button.setOnClickListener(
			new OnClickListener() {
				@Override
				public void onClick(View view) {
				// {{
					Intent	intentShare = new Intent(android.content.Intent.ACTION_SEND);
					intentShare.setType("text/*");
					intentShare.setPackage("com.twitter.android");
					intentShare.putExtra(Intent.EXTRA_TEXT, "#NP '" + m_strTitle + "' on 1triberadio app #1triberadio @1triberadio");
					startActivity(Intent.createChooser(intentShare, "SHARE"));
				// }}

				// {{
//					ShareTextToTwitter("#NP '" + m_strTitle + "' on 1triberadio app #1triberadio @1triberadio");
				// }}
				}
			}
		);
	// }} Button Share Twitter
	}

	//------------------------------------------------------------------------------
	// Display error messages
	void Error(String a_strErr) {
		// get error code in current thread for display in UI thread
		try {
			String str = String.format("%s\n(Erro: %d)", a_strErr, BASS.BASS_ErrorGetCode());
			runOnUiThread(
				new RunnableParam(str) {
					public void run() {
						new AlertDialog.Builder(ActivityRadio.this)
							.setMessage((String)m_objParam)
							.setPositiveButton("OK", null)
							.show();
					}
				}
			);
		} catch (Exception e) {}
	}

	//------------------------------------------------------------------------------
	// Update stream title from metadata
	void DoMeta() {
		String	strMeta = (String)BASS.BASS_ChannelGetTags(m_iChannel, BASS.BASS_TAG_META);
		if (strMeta != null) {
			// Got Shoutcast metadata
			int iTitleIndex = strMeta.indexOf("StreamTitle='");
			if (iTitleIndex >= 0) {
				m_strTitle = strMeta.substring(iTitleIndex + 13, strMeta.indexOf("'", iTitleIndex + 13));
				m_textViewTitle.setText(m_strTitle);
			}
		}
		else {
			String[] arrStrOgg = (String[])BASS.BASS_ChannelGetTags(m_iChannel, BASS.BASS_TAG_OGG);
			if (arrStrOgg != null) {
				// Got Icecast/OGG tags
				m_strArtist	= null;
				m_strTitle	= null;
				for (String str : arrStrOgg) {
					if (str.regionMatches(true, 0, "artist=", 0, 7))
						m_strArtist = str.substring(7);
					else if (str.regionMatches(true, 0, "title=", 0, 6))
						m_strTitle = str.substring(6);
				}
				m_textViewTitle.setText(m_strTitle + "-" + m_strArtist);
			}
		}
	}

	//------------------------------------------------------------------------------
	public void PlayRadio() {
//		m_dlgProgress.show();
		EnvVariable.KillCurrentSound();
		BASS.BASS_SetConfigPtr(BASS.BASS_CONFIG_NET_PROXY, null);		// Set proxy server
		m_thread = new Thread(new OpenURL(URL_RADIO));
		m_thread.start();
		EnvVariable.CurrentRadioThread = m_thread;
	}

	//------------------------------------------------------------------------------
	public void StopRadio() {
		try { BASS.BASS_ChannelStop(m_iChannel); } catch (Exception e) {}
		m_iChannel = 0;
		m_thread = null;
		EnvVariable.CurrentRadioThread = m_thread;
	}

	//------------------------------------------------------------------------------
	public void TurnOnSound() {
		AudioManager	audioManager;
		audioManager = (AudioManager)getSystemService(AUDIO_SERVICE);
		audioManager.setStreamMute(AudioManager.STREAM_SYSTEM, false);
		audioManager.setStreamMute(AudioManager.STREAM_NOTIFICATION, false);
		audioManager.setStreamMute(AudioManager.STREAM_RING, false);
		audioManager.setStreamMute(AudioManager.STREAM_MUSIC, false);
	}

	//------------------------------------------------------------------------------
	public void TurnOffSound() {
		AudioManager	audioManager;
		audioManager = (AudioManager)getSystemService(AUDIO_SERVICE);
		audioManager.setStreamMute(AudioManager.STREAM_SYSTEM, true);
		audioManager.setStreamMute(AudioManager.STREAM_NOTIFICATION, true);
		audioManager.setStreamMute(AudioManager.STREAM_RING, true);
		audioManager.setStreamMute(AudioManager.STREAM_MUSIC, true);
	}

	//------------------------------------------------------------------------------
	public void ShareTextToFacebook(String a_strMessage) {
		Facebook			facebook;
		SharedPreferences	sharedPreferences;
		String				strToken;
		Bundle				parameters;
		String				strResponse;

		facebook			= new Facebook(getResources().getString(R.string.FACEBOOK_APP_ID));
		sharedPreferences	= getSharedPreferences(Facebook.STR_CREDENTIAL, Context.MODE_PRIVATE);
		strToken			= sharedPreferences.getString(Facebook.STR_ACCESS_TOKEN, null);

		parameters = new Bundle();
		parameters.putString("message",		a_strMessage);
		parameters.putString("description",	"topic share");
		if ((strToken != null) && (strToken.length() > 0)) {
			parameters.putString(Facebook.STR_ACCESS_TOKEN, "" + strToken);
		}
		try {
			facebook.Request("me");
			strResponse = facebook.Request("me/feed", parameters, "POST");
			Log.d("Tests--->*************", "got response: " + strResponse);

			if (	strResponse == null
				||	strResponse.equals("")
				||	strResponse.equals("false")
				||	strResponse.equalsIgnoreCase("{\"error\":{\"message\":\"An active access token must be used to query information about the current user.\",\"type\":\"OAuthException\",\"code\":2500}}")) {
			// {{ Clear Facebook Data
				Toast.makeText(this, "Blank response. please login again facebook", Toast.LENGTH_SHORT).show();
				SharedPreferences sharedPreferences1 = PreferenceManager.getDefaultSharedPreferences(this);
				Editor editor = sharedPreferences1.edit();
				editor.remove(Facebook.STR_ACCESS_TOKEN);
				editor.remove(Facebook.STR_EXPIRES_IN);
				editor.commit();
			// }} Clear Facebook Data
			}
			else {
				Toast.makeText(this, "Message posted to your facebook wall!", Toast.LENGTH_SHORT).show();
			}
		}
		catch (Exception e) {
			Toast.makeText(this, "Failed to post to wall!", Toast.LENGTH_SHORT).show();
			e.printStackTrace();
		}
	}

// {{==============================================================================
	class TaskShareTwitter extends AsyncTask<String, Void, Boolean> {
		String	m_strMessage;

	    protected Boolean doInBackground(String... a_strParams) {
	    	m_strMessage = a_strParams[0];
	        try {
	    		sharedPrefs = PreferenceManager.getDefaultSharedPreferences(ActivityRadio.this);

	    		if (!TwitterUtil.IsAuthenticated(sharedPrefs))
	    		{
	    			Toast.makeText(ActivityRadio.this, "Twitter is not authenticated!", Toast.LENGTH_SHORT).show();
	    			return Boolean.FALSE;
	    		}
	        } catch (Exception e) { return Boolean.FALSE; }
	        return Boolean.TRUE;
	    }

	    protected void onPostExecute(Boolean a_bResult) {
	    	if (a_bResult.equals(Boolean.FALSE))
				Toast.makeText(ActivityRadio.this, "Failed to post to wall!", Toast.LENGTH_SHORT).show();
	    	else {
				Thread thread = new Thread() {
					public void run() {
						try {
							TwitterUtil.SendTweet(sharedPrefs, m_strMessage);
							Toast.makeText(ActivityRadio.this, "This radio is shared to Twitter!", Toast.LENGTH_SHORT).show();
						}
						catch (Exception e) {
							Toast.makeText(ActivityRadio.this, "Failed to post to wall!", Toast.LENGTH_SHORT).show();
							e.printStackTrace();
							Log.d("dhaval-->send tweet:", e.getMessage().toString());
						}
					}
	
				};
				thread.start();
	    	}
	    }
	}
// }}==============================================================================

	//------------------------------------------------------------------------------
	public void ShareTextToTwitter(String a_strMessage) {
		/*
		final String			strMessage = a_strMessage;

		sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);

		if (!TwitterUtil.IsAuthenticated(sharedPrefs))
		{
			Toast.makeText(this, "Twitter is not authenticated!", Toast.LENGTH_SHORT).show();
			return;
		}
		Thread thread = new Thread() {
			public void run() {
				try {
					TwitterUtil.SendTweet(sharedPrefs, strMessage);
					mTwitterHandler.post(mUpdateTwitterNotification);
				}
				catch (Exception e) {
					Toast.makeText(ActivityRadio.this, "Failed to post to wall!", Toast.LENGTH_SHORT).show();
					e.printStackTrace();
					Log.d("dhaval-->send tweet:", e.getMessage().toString());
				}
			}

		};
		thread.start();
		*/
		new TaskShareTwitter().execute(a_strMessage);
	}

	//------------------------------------------------------------------------------
}

//==============================================================================
