package androidcustom.radiostation.ui;

import org.json.JSONArray;
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
import androidcustom.radiostation.multimedia.MultimediaInfo;
import androidcustom.radiostation.ui.AdapterBaseList.ItemContentClickListener;

//==============================================================================
public class ActivitySubComedy extends Activity {
	
	private	ListView			m_listView;
	private AdapterSubComedy	m_adapterSubComedy;
	private	String				m_strTerm;
	private	int					m_iIndexVideo;
	private	boolean				m_bAvailDownload;	// Indicate Downloading one video data is finished or not

	//------------------------------------------------------------------------------
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		CountDownTimer		timer;

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_subcomedy);

		setRequestedOrientation(getResources().getConfiguration().orientation);			// Won't Rotate
		
		m_strTerm = getIntent().getExtras().getString(Const.KEY_TERM);

		m_listView = new ListView(this); 
		m_adapterSubComedy = new AdapterSubComedy(this, m_listView);
		m_listView.setAdapter(m_adapterSubComedy);

		FrameLayout	frmLayout = (FrameLayout)findViewById(R.id.ID_FRMLYT_SUBCOMEDY);
		frmLayout.addView(m_listView);

	// {{ Refresh ListViews Periodically
		timer = new CountDownTimer(Const.DELAY_INVALIDATE, 500) {
			@Override
			public void onFinish() {
				m_listView.invalidateViews();
				if (m_bAvailDownload && (m_iIndexVideo < Const.MAX_VIDEO_COUNT))
					SetData();
				start();
			}
			@Override
			public void onTick(long millisUntilFinished) {
			}
		};
		timer.start();
	// }} Refresh ListViews Periodically

	// {{ ListView Item Click Listener
		/*
		m_listView.setOnItemClickListener(
			new OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> adapter, View v, int position, long arg3) {
					switch (position) {
						default:	break;
					}
					ShowActivityPlay(position);
				}
			}
		);
		*/
		m_adapterSubComedy.SetItemContentClickListener(
			new ItemContentClickListener() {
				@Override
				public void OnItemContentClicked(int a_iPosition) {
					ShowActivityPlay(a_iPosition);
				}
			}
		);
	// }} ListView Item Click Listener

		m_iIndexVideo	= 0;

		SetLayout();
		SetData();
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
					ActivitySubComedy.this.finish();
				}
			}
		);
		
		textView = (TextView)findViewById(R.id.ID_TXTVIEW_TITLEBAR_PLAY_TITLE);
		textView.setText(Const.STRING_COMEDY);
	// }} Title Bar
	}

	//------------------------------------------------------------------------------
	private void SetData() {
		HttpApi				httpApi;
		HttpApiListener		httpApiListener;

		m_bAvailDownload = false;

		httpApi = new HttpApi();
		httpApiListener = new HttpApiListener() {
			@Override
			public void OnHttpApiResult(String a_strResult, int a_iType) {
				JSONObject		jsonObj;
				JSONObject		jsonObjFeed;
				JSONArray		jsonArrEntry;
				JSONObject		jsonObjData;
				JSONObject		jsonObjGroup;
				JSONObject		jsonObjId;
				JSONArray		jsonArrThumb;
				JSONObject		jsonObjThumb;
				JSONObject		jsonObjTitle;
				JSONArray		jsonArrCredit;
				JSONObject		jsonObjCredit;
				JSONObject		jsonObjCount;
				int				iIndex;

				if (a_strResult == null)		return;
				if (a_strResult.length() == 0)	return;

				try {
					jsonObj			= new JSONObject(a_strResult);
					jsonObjFeed		= jsonObj.getJSONObject("feed");
					jsonArrEntry	= jsonObjFeed.getJSONArray("entry");
					
					for (iIndex = 0; iIndex < jsonArrEntry.length(); iIndex++) {
						if (iIndex == Const.MAX_VIDEO_COUNT)
							break;
						MultimediaInfo		multimediaInfo = new MultimediaInfo();
						multimediaInfo.SetIndex(iIndex);
						multimediaInfo.SetType(a_iType);

						try {
							jsonObjData		= jsonArrEntry.getJSONObject(iIndex);
							jsonObjGroup	= jsonObjData.getJSONObject("media$group");
							jsonObjId		= jsonObjGroup.getJSONObject("yt$videoid");		multimediaInfo.SetId(jsonObjId.getString("$t"));
							jsonArrThumb	= jsonObjGroup.getJSONArray("media$thumbnail");
							jsonObjThumb	= jsonArrThumb.getJSONObject(0);				multimediaInfo.SetThumb(jsonObjThumb.getString("url"));
							jsonObjTitle	= jsonObjData.getJSONObject("title");			multimediaInfo.SetTitle(jsonObjTitle.getString("$t"));
							jsonArrCredit	= jsonObjGroup.getJSONArray("media$credit");
							jsonObjCredit	= jsonArrCredit.getJSONObject(0);				multimediaInfo.SetArtist(jsonObjCredit.getString("$t"));
							jsonObjCount	= jsonObjData.getJSONObject("yt$statistics");	multimediaInfo.SetLikeCount(Integer.parseInt(jsonObjCount.getString("viewCount")));

							m_adapterSubComedy.AddMultimediaInfo(multimediaInfo);
							m_iIndexVideo++;
						} catch (Exception e) {}
					}
				} catch (Exception e1)	{}
				m_bAvailDownload = true; 
			}
		};
		httpApi.SetApiType(Const.TYPE_SUBCOMEDY);
		httpApi.SetHttpApiListener(httpApiListener);
		httpApi.StartGetSubComedy(m_strTerm, m_iIndexVideo);
	}

	//------------------------------------------------------------------------------
	public void ShowActivityPlay(int a_iIndex) {
		Intent		intentSubComedy2Play;

		intentSubComedy2Play = new Intent(ActivitySubComedy.this, ActivityPlayVideo.class);		// ActivitySubComedy -> ActivityPlayComedy
		intentSubComedy2Play.putExtra(Const.KEY_INDEX,	a_iIndex);
		EnvVariable.CurrentAdapter = m_adapterSubComedy;
		startActivity(intentSubComedy2Play);
	}

}

//==============================================================================
