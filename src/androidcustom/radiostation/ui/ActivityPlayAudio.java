package androidcustom.radiostation.ui;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import androidcustom.radiostation.R;
import androidcustom.radiostation.db.DbApi;
import androidcustom.radiostation.global.Const;
import androidcustom.radiostation.global.EnvVariable;
import androidcustom.radiostation.http.HttpApi;
import androidcustom.radiostation.loader.LoaderImage;

//==============================================================================
public class ActivityPlayAudio extends Activity {

	private int			m_iIndex;
	private String		m_strTitle;
	private String		m_strArtist;
	private String		m_strUrl;
	
	private MediaPlayer		m_mediaPlayer;
	private	MediaPlayer.OnPreparedListener		m_listenerOnPrepared;
	private	MediaPlayer.OnCompletionListener	m_listenerOnCompletion;
	private	DialogProgress		m_dlgProgress;

	private int				m_iPlaybackPosition = 0;
	private boolean			m_bIsPaused;
	private	SeekBar			m_seekBarVolume;
	
	private	AdapterBase		m_adapterBase;

	//------------------------------------------------------------------------------
	public ActivityPlayAudio() {

		m_iIndex		= Const.INDEX_NONE;
		m_strTitle		= null;
		m_strArtist		= null;
		m_strUrl		= null;

		m_bIsPaused = false;
		m_mediaPlayer = null;
		m_dlgProgress = null;
	}

	//------------------------------------------------------------------------------
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_play_audio);
		setRequestedOrientation(getResources().getConfiguration().orientation);			// Won't Rotate

		m_iIndex		= getIntent().getIntExtra(Const.KEY_INDEX, -1);
		m_adapterBase	= EnvVariable.CurrentAdapter;
		EnvVariable.CurrentAdapter = null;

		m_strTitle	= m_adapterBase.GetMultimediaInfo(m_iIndex).GetTitle();
		m_strArtist	= m_adapterBase.GetMultimediaInfo(m_iIndex).GetArtist();
		m_strUrl	= m_adapterBase.GetMultimediaInfo(m_iIndex).GetPath1();

		SetLayout();

		m_listenerOnPrepared = new MediaPlayer.OnPreparedListener() {
			@Override
			public void onPrepared(MediaPlayer mp) {
				mp.start();
				m_dlgProgress.hide();
			}
		};

		m_listenerOnCompletion = new MediaPlayer.OnCompletionListener() {
			@Override
			public void onCompletion(MediaPlayer mp) {
				PlayNextAudio();
			}
		};

		m_dlgProgress = new DialogProgress(this);
		m_dlgProgress.setCanceledOnTouchOutside(true);
		m_dlgProgress.setCancelable(true);
		m_dlgProgress.setOnCancelListener(
			new OnCancelListener() {
				@Override
				public void onCancel(DialogInterface dialog) {
					ActivityPlayAudio.this.finish();
				}
			}
		);
		m_dlgProgress.show();
	}

	//------------------------------------------------------------------------------
	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		CountDownTimer		timer;
		super.onPostCreate(savedInstanceState);

	// {{ After some period, Start Playing
		timer = new CountDownTimer(Const.DELAY_INVALIDATE, 100) {
			@Override
			public void onFinish() {
				PlayAudio();
			}
			@Override
			public void onTick(long millisUntilFinished) {
			}
		};
		timer.start();
	// }} After some period, Start Playing
		
	}

	//------------------------------------------------------------------------------
	@Override
	protected void onDestroy() {
//		KillCurrentSound();
		super.onDestroy();
	}

	//------------------------------------------------------------------------------
	private void SetLayout() {
		ImageView		imageView;
		Button			button;
		TextView		textView;
		TextView		textViewTitle;
		TextView		textViewArtist;

	// {{ Title Bar
		button = (Button)findViewById(R.id.ID_BTN_TITLEBAR_PLAY_LEFT);
		button.setOnClickListener(
			new OnClickListener() {
				@Override
				public void onClick(View view) {
					ActivityPlayAudio.this.finish();
				}
			}
		);

		textView = (TextView)findViewById(R.id.ID_TXTVIEW_TITLEBAR_PLAY_TITLE);
		textView.setText("Media");
	// }} Title Bar
		
	// {{ Title TextView
		textViewTitle = (TextView)findViewById(R.id.ID_TXTVIEW_PLAY_AUDIO_TITLE);
		textViewTitle.setText(m_strTitle);
	// }} Title TextView

	// {{ Artist TextView
		textViewArtist = (TextView)findViewById(R.id.ID_TXTVIEW_PLAY_AUDIO_ARTIST);
		textViewArtist.setText(m_strArtist);
	// }} Artist TextView

	// {{ PlayBtn
		imageView = (ImageView)findViewById(R.id.ID_IMGVIEW_PLAY_AUDIO_BTN_PLAY);
		imageView.setOnClickListener(
			new OnClickListener() {
				@Override
				public void onClick(View v) {
					if (m_bIsPaused) {
						m_bIsPaused = false;
						((ImageView)v).setImageResource(R.drawable.img_btn_pause);
						if (m_mediaPlayer != null) {
							m_mediaPlayer.seekTo(m_iPlaybackPosition);
							m_mediaPlayer.start();
						}
					}
					else {
						m_bIsPaused = true;
						((ImageView)v).setImageResource(R.drawable.img_btn_play_white);
						if (m_mediaPlayer != null) {
							m_iPlaybackPosition = m_mediaPlayer.getCurrentPosition();
							m_mediaPlayer.pause();
						}
					}
				}
			}
		);
	// }} PlayBtn

	// {{ Next Prev Button
		button = (Button)findViewById(R.id.ID_BTN_PLAY_AUDIO_BTN_PREV);
		button.setOnClickListener(
			new OnClickListener() {
				@Override
				public void onClick(View v) {
					PlayPrevAudio();
				}
			}
		);

		button = (Button)findViewById(R.id.ID_BTN_PLAY_AUDIO_BTN_NEXT);
		button.setOnClickListener(
			new OnClickListener() {
				@Override
				public void onClick(View v) {
					PlayNextAudio();
				}
			}
		);
	// }} Next Prev Button

	// {{ SeekBar For Volume
		m_seekBarVolume = (SeekBar)findViewById(R.id.ID_SEEKBAR_PLAY_AUDIO_VOLUME);
		m_seekBarVolume.setMax(100);
		m_seekBarVolume.setProgress(100);
		m_seekBarVolume.setOnSeekBarChangeListener(
			new OnSeekBarChangeListener() {
				@Override
				public void onStopTrackingTouch(SeekBar seekBar) {
				}

				@Override
				public void onStartTrackingTouch(SeekBar seekBar) {
				}

				@Override
				public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
					if (m_mediaPlayer != null)
						m_mediaPlayer.setVolume((float)progress / 100, (float)progress / 100);
				}
			}
		);
	// }} SeekBar For Volume
	}

	//------------------------------------------------------------------------------
	private void PlayPrevAudio() {
		TextView	txtViewTitle;
		TextView	txtViewArtist;
		int			iType = Const.TYPE_NONE;
		boolean		bAvail = false;

		while (bAvail == false) {
			m_iIndex--;
			if (m_iIndex == -1)
				m_iIndex = m_adapterBase.getCount() - 1;
			iType = m_adapterBase.GetMultimediaInfo(m_iIndex).GetType();
			switch (iType) {
				case Const.TYPE_HOME_TREND:
				case Const.TYPE_HOME_TOP10:
				case Const.TYPE_HOME_STAFF:
				case Const.TYPE_HOME_CLASS:
				case Const.TYPE_PLAYLIST_GBEDU:
				case Const.TYPE_PLAYLIST_LOVE:
				case Const.TYPE_PLAYLIST_AFRO:
				case Const.TYPE_PLAYLIST_WORKOUT:
				case Const.TYPE_PLAYLIST_CHURCH:
				case Const.TYPE_PLAYLIST_OLD:
				case Const.TYPE_PLAYLIST_RAP:
				case Const.TYPE_PODCAST_1:
				case Const.TYPE_PODCAST_2:
				case Const.TYPE_PODCAST_3:
				case Const.TYPE_PODCAST_4:
				case Const.TYPE_PODCAST_5:
					bAvail = true;
					break;
				case Const.TYPE_VIDEO:
				case Const.TYPE_SUBCOMEDY:
				case Const.TYPE_SUBNOLLYWOOD:
					bAvail = false;
					break;
				default:
					bAvail = false;
					break;
			}
		}

		m_dlgProgress.show();

		m_strTitle	= m_adapterBase.GetMultimediaInfo(m_iIndex).GetTitle();
		m_strArtist	= m_adapterBase.GetMultimediaInfo(m_iIndex).GetArtist();
		m_strUrl	= m_adapterBase.GetMultimediaInfo(m_iIndex).GetPath1();
	// {{ Title TextView
		txtViewTitle = (TextView)findViewById(R.id.ID_TXTVIEW_PLAY_AUDIO_TITLE);
		txtViewTitle.setText(m_strTitle);
	// }} Title TextView

	// {{ Artist TextView
		txtViewArtist = (TextView)findViewById(R.id.ID_TXTVIEW_PLAY_AUDIO_ARTIST);
		txtViewArtist.setText(m_strArtist);
	// }} Artist TextView

		PlayAudio();
	}

	//------------------------------------------------------------------------------
	private void PlayNextAudio() {
		TextView	txtViewTitle;
		TextView	txtViewArtist;
		int			iType = Const.TYPE_NONE;
		boolean		bAvail = false;

		while (bAvail == false) {
			m_iIndex++;
			if (m_iIndex == m_adapterBase.getCount())
				m_iIndex = 0;
			iType = m_adapterBase.GetMultimediaInfo(m_iIndex).GetType();
			switch (iType) {
				case Const.TYPE_HOME_TREND:
				case Const.TYPE_HOME_TOP10:
				case Const.TYPE_HOME_STAFF:
				case Const.TYPE_HOME_CLASS:
				case Const.TYPE_PLAYLIST_GBEDU:
				case Const.TYPE_PLAYLIST_LOVE:
				case Const.TYPE_PLAYLIST_AFRO:
				case Const.TYPE_PLAYLIST_WORKOUT:
				case Const.TYPE_PLAYLIST_CHURCH:
				case Const.TYPE_PLAYLIST_OLD:
				case Const.TYPE_PLAYLIST_RAP:
				case Const.TYPE_PODCAST_1:
				case Const.TYPE_PODCAST_2:
				case Const.TYPE_PODCAST_3:
				case Const.TYPE_PODCAST_4:
				case Const.TYPE_PODCAST_5:
					bAvail = true;
					break;
				case Const.TYPE_VIDEO:
				case Const.TYPE_SUBCOMEDY:
				case Const.TYPE_SUBNOLLYWOOD:
					bAvail = false;
					break;
				default:
					bAvail = false;
					break;
			}
		}

		if (m_dlgProgress == null) {
			m_dlgProgress = new DialogProgress(this);
			m_dlgProgress.setCanceledOnTouchOutside(true);
			m_dlgProgress.setCancelable(true);
			m_dlgProgress.setOnCancelListener(
				new OnCancelListener() {
					@Override
					public void onCancel(DialogInterface dialog) {
						ActivityPlayAudio.this.finish();
					}
				}
			);
		}
		m_dlgProgress.show();

		m_strTitle	= m_adapterBase.GetMultimediaInfo(m_iIndex).GetTitle();
		m_strArtist	= m_adapterBase.GetMultimediaInfo(m_iIndex).GetArtist();
		m_strUrl	= m_adapterBase.GetMultimediaInfo(m_iIndex).GetPath1();
	// {{ Title TextView
		txtViewTitle = (TextView)findViewById(R.id.ID_TXTVIEW_PLAY_AUDIO_TITLE);
		txtViewTitle.setText(m_strTitle);
	// }} Title TextView

	// {{ Artist TextView
		txtViewArtist = (TextView)findViewById(R.id.ID_TXTVIEW_PLAY_AUDIO_ARTIST);
		txtViewArtist.setText(m_strArtist);
	// }} Artist TextView

		PlayAudio();
	}

	//------------------------------------------------------------------------------
	private void PlayAudio() {
		ImageView		imgView;
		HttpApi			httpApi = new HttpApi();

		EnvVariable.KillCurrentSound();
		m_mediaPlayer = null;

		DbApi.GetInstance().InsertMultimediaInfoToHistory(m_adapterBase.GetMultimediaInfo(m_iIndex));
		httpApi.IncreaseCountPlay(m_strTitle, m_strArtist);

	// {{ Refresh ListViews Periodically
		imgView = (ImageView)findViewById(R.id.ID_IMGVIEW_PLAY_AUDIO_BACKGROUND);

		if (LoaderImage.GetInstance() != null)
			LoaderImage.GetInstance().DisplayImage(
				imgView,
				m_adapterBase.GetMultimediaInfo(m_iIndex).GetPoster()
			);
	// }} Refresh ListViews Periodically

		if (m_mediaPlayer == null) {
			m_mediaPlayer = new MediaPlayer();
			m_mediaPlayer.setOnPreparedListener(m_listenerOnPrepared);
			m_mediaPlayer.setOnCompletionListener(m_listenerOnCompletion);
			EnvVariable.CurrentMediaPlayer = m_mediaPlayer;
		}

		try {
			m_mediaPlayer.setDataSource(m_strUrl);
			m_mediaPlayer.setVolume((float)m_seekBarVolume.getProgress() / 100,
									(float)m_seekBarVolume.getProgress() / 100);
			m_mediaPlayer.prepare();
		} catch (Exception e) {}
	}

}
//==============================================================================
