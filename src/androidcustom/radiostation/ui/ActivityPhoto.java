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
import android.widget.GridView;
import android.widget.TextView;
import androidcustom.radiostation.R;
import androidcustom.radiostation.global.Const;
import androidcustom.radiostation.global.EnvVariable;
import androidcustom.radiostation.http.HttpApi;
import androidcustom.radiostation.http.HttpApi.HttpApiListener;
import androidcustom.radiostation.loader.LoaderImage;
import androidcustom.radiostation.multimedia.MultimediaInfo;

//==============================================================================
public class ActivityPhoto extends Activity {

	private GridView		m_gridView;
	private HttpApi			m_httpApi;
	private AdapterPhoto	m_adapterPhoto;
	private	CountDownTimer	m_timer;

	//------------------------------------------------------------------------------
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_photo);
		setRequestedOrientation(getResources().getConfiguration().orientation);		// Won't Rotate

		if (LoaderImage.GetInstance() != null)
			LoaderImage.GetInstance().ClearCache();

		m_gridView = (GridView)findViewById(R.id.ID_GRIDVIEW_PHOTO);
		m_gridView.setHorizontalFadingEdgeEnabled(true);
		m_gridView.setVerticalFadingEdgeEnabled(true);

		SetLayout();

		m_httpApi = new HttpApi();
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
							m_adapterPhoto.AddMultimediaInfo(multimediaInfo);
						}
					}
					catch (JSONException e1) {}
					catch (Exception e2) {}
				}
			}
		);
		m_httpApi.StartGetPhoto();
		
		m_adapterPhoto = new AdapterPhoto(this, m_gridView);
		m_gridView.setAdapter(m_adapterPhoto);
		
		m_gridView.setOnItemClickListener(
			new OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
					ShowActivityPlayPhoto(position);
				}
			}
		);
		
		// {{ Refresh ListViews Periodically
			m_timer = new CountDownTimer(Const.DELAY_INVALIDATE, 100) {
				@Override
				public void onFinish() {
					m_gridView.invalidateViews();
					start();
				}
				@Override
				public void onTick(long millisUntilFinished) {
				}
			};
			m_timer.start();
		// }} Refresh ListViews Periodically
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
					ActivityPhoto.this.finish();
				}
			}
		);

		textView = (TextView)findViewById(R.id.ID_TXTVIEW_TITLEBAR_PLAY_TITLE);
		textView.setText("Photos");
	// }} Title Bar
	}

	//------------------------------------------------------------------------------
	public void ShowActivityPlayPhoto(int a_iIndex) {
		Intent		intentPhoto2Play;

		intentPhoto2Play = new Intent(this, ActivityPlayPhoto.class);		// ActivityPhoto -> ActivityPlayPhoto
		intentPhoto2Play.putExtra(Const.KEY_INDEX,	a_iIndex);
		EnvVariable.CurrentAdapter = m_adapterPhoto;
		startActivity(intentPhoto2Play);
	}

}
//==============================================================================
