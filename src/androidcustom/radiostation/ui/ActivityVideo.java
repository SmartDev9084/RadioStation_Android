package androidcustom.radiostation.ui;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;
import androidcustom.radiostation.R;
import androidcustom.radiostation.global.Const;
import androidcustom.radiostation.global.EnvVariable;
import androidcustom.radiostation.http.HttpApi;
import androidcustom.radiostation.http.HttpApi.HttpApiListener;
import androidcustom.radiostation.loader.LoaderImage;
import androidcustom.radiostation.multimedia.MultimediaInfo;

//==============================================================================
public class ActivityVideo extends Activity {
	
	private	ListView			m_listView;
	private AdapterVideo		m_adapterVideo;
	private	CountDownTimer		m_timer;

	//------------------------------------------------------------------------------
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_video);
		setRequestedOrientation(getResources().getConfiguration().orientation);			// Won't Rotate

		if (LoaderImage.GetInstance() != null)
			LoaderImage.GetInstance().ClearCache();

		m_listView = new ListView(this); 
		m_adapterVideo = new AdapterVideo(this, m_listView);
		m_listView.setAdapter(m_adapterVideo);

		FrameLayout	frmLayout = (FrameLayout)findViewById(R.id.ID_FRMLYT_VIDEO);
		frmLayout.addView(m_listView);

	// {{ Refresh ListViews Periodically
		m_timer = new CountDownTimer(Const.DELAY_INVALIDATE, 100) {
			@Override
			public void onFinish() {
				m_listView.invalidateViews();
				start();
			}
			@Override
			public void onTick(long millisUntilFinished) {
			}
		};
		m_timer.start();
	// }} Refresh ListViews Periodically

		m_listView.setOnItemClickListener(
			new OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> adapter, View v, int position, long arg3) {
					ShowActivityPlayVideo(position);
				}
			}
		);

		SetLayout();
		SetData();
	}

	//------------------------------------------------------------------------------
	@Override
	protected void onDestroy() {
		if (LoaderImage.GetInstance() != null)
			LoaderImage.GetInstance().ClearCache();
		m_timer.cancel();
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
					ActivityVideo.this.finish();
				}
			}
		);
		
		textView = (TextView)findViewById(R.id.ID_TXTVIEW_TITLEBAR_PLAY_TITLE);
		textView.setText("Videos");
	// }} Title Bar
	}

	//------------------------------------------------------------------------------
	private void SetData() {
		HttpApi		httpApi;
	
		httpApi = new HttpApi();
		httpApi.SetHttpApiListener(
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
						jsonArrSongList = jsonObj.getJSONArray("videolist");
						for (iIndex = 0; iIndex < jsonArrSongList.length(); iIndex++) {
							if (iIndex == Const.MAX_VIDEO_COUNT)
								break;
							MultimediaInfo		multimediaInfo = new MultimediaInfo();
							JSONObject			jsonMultimediaData = jsonArrSongList.getJSONObject(iIndex);
							try {
								multimediaInfo.SetIndex(iIndex);
								multimediaInfo.SetType(a_iType);
								multimediaInfo.SetTitle(jsonMultimediaData.getString("title"));
								multimediaInfo.SetArtist(jsonMultimediaData.getString("artist"));
								multimediaInfo.SetLink(jsonMultimediaData.getString("link"));
								multimediaInfo.SetDuration(jsonMultimediaData.getString("duration"));
							} catch (Exception e) {}

							m_adapterVideo.AddMultimediaInfo(multimediaInfo);
						}
					}
					catch (JSONException e1) {}
					catch (Exception e2) {}
				}
			}
		);
		httpApi.SetApiType(Const.TYPE_VIDEO);
		httpApi.StartGetVideo();
	}

	//------------------------------------------------------------------------------
	public void ShowActivityPlayVideo(int a_iIndex) {
		Intent		intentVideo2Play;

		intentVideo2Play = new Intent(ActivityVideo.this, ActivityPlayVideo.class);		// ActivityVideo -> ActivityPlayVideo
		intentVideo2Play.putExtra(Const.KEY_INDEX,		a_iIndex);
		EnvVariable.CurrentAdapter = m_adapterVideo;
		startActivity(intentVideo2Play);
	}

}

//==============================================================================
