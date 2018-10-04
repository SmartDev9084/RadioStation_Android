package androidcustom.radiostation.ui;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.FrameLayout;
import android.widget.ListView;
import androidcustom.radiostation.global.Const;
import androidcustom.radiostation.global.EnvVariable;
import androidcustom.radiostation.http.HttpApi;
import androidcustom.radiostation.http.HttpApi.HttpApiListener;
import androidcustom.radiostation.loader.LoaderImage;

//==============================================================================
public class ViewComedy {

	private Context				m_context;
	private ListView			m_listViewContent;
	private AdapterComedy		m_adapterComedy;

	private	HttpApi				m_httpApi;

	private	CountDownTimer 		m_timer;

	//------------------------------------------------------------------------------
	public ViewComedy(Context a_context) {
		m_context = a_context;

		m_httpApi = null;

		m_listViewContent	= new ListView(m_context);
		m_adapterComedy		= new AdapterComedy(m_context);
		m_listViewContent.setAdapter(m_adapterComedy);

		m_listViewContent.setOnItemClickListener(
			new OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> adapter, View v, int position, long arg3) {
					ShowActivityComedy(position);
				}
			}
		);
		
	// {{ Timer to Invalidate ListView
		m_timer = new CountDownTimer(Const.DELAY_INVALIDATE, 100) {
			@Override
			public void onFinish() {
				if (EnvVariable.CurrentMainItem == Const.ITEM_COMEDY)
					m_listViewContent.invalidateViews();
				start();
			}
			@Override
			public void onTick(long millisUntilFinished) {
			}
		};
	// }} Timer to Invalidate ListView
	}

	//------------------------------------------------------------------------------
	public void SetLayout(FrameLayout a_frmlytParent) {
		FrameLayout.LayoutParams	frmLayoutParams;

		a_frmlytParent.addView(m_listViewContent);

	// {{ m_lstviewContent
		frmLayoutParams = new FrameLayout.LayoutParams(
				FrameLayout.LayoutParams.FILL_PARENT,
				FrameLayout.LayoutParams.FILL_PARENT);
		frmLayoutParams.setMargins((int)((1 +  Const.RATIO_WIDTH_MAINLIST) * EnvVariable.SizeDisplay.x), 0,
										(-1) * EnvVariable.SizeDisplay.x, 0);
		m_listViewContent.setLayoutParams(frmLayoutParams);
	// }} m_lstviewContent
	}

	//------------------------------------------------------------------------------
	public ListView GetContentView() {
		return m_listViewContent;
	}

	//------------------------------------------------------------------------------
	public void Show() {
		FrameLayout.LayoutParams	frmLayoutParams;

		if (LoaderImage.GetInstance() != null)
			LoaderImage.GetInstance().ClearCache();

		frmLayoutParams = (FrameLayout.LayoutParams)m_listViewContent.getLayoutParams();
		frmLayoutParams.setMargins((int)(Const.RATIO_WIDTH_MAINLIST * EnvVariable.SizeDisplay.x), 0, 0, 0);
		m_listViewContent.setLayoutParams(frmLayoutParams);

		if (m_httpApi == null) {
			m_httpApi = new HttpApi();
			m_httpApi.SetHttpApiListener(
				new HttpApiListener() {
					@Override
					public void OnHttpApiResult(String a_strResult, int a_iType) {
						JSONObject			jsonObj;
						JSONArray			jsonArrComedyList;
						int					iIndex;

						if (a_strResult == null)		return;
						if (a_strResult.length() == 0)	return;

						try {
							jsonObj = new JSONObject(a_strResult);
							jsonArrComedyList = jsonObj.getJSONArray("comedylist");
							for (iIndex = 0; iIndex < jsonArrComedyList.length(); iIndex++) {
								ItemDataComedy		itemDataComedy = new ItemDataComedy();
								JSONObject			jsonComedyData = jsonArrComedyList.getJSONObject(iIndex);
								try {
									itemDataComedy.SetName(jsonComedyData.getString("comedyname"));
									itemDataComedy.SetTerm(jsonComedyData.getString("comedyterm"));
								} catch (Exception e) {}

								m_adapterComedy.AddItemData(itemDataComedy);
							}
						}
						catch (JSONException e1) {}
						catch (Exception e2) {}
					}
				}
			);
			m_httpApi.SetApiType(Const.TYPE_COMEDY);
			m_httpApi.StartGetComedy();
		}
		m_timer.start();
	}

	//------------------------------------------------------------------------------
	public void Hide() {
		FrameLayout.LayoutParams	frmLayoutParams;

		if (LoaderImage.GetInstance() != null)
			LoaderImage.GetInstance().ClearCache();

		frmLayoutParams = (FrameLayout.LayoutParams)m_listViewContent.getLayoutParams();
		frmLayoutParams.setMargins((int)((1 +  Const.RATIO_WIDTH_MAINLIST) * EnvVariable.SizeDisplay.x), 0,
										(-1) * EnvVariable.SizeDisplay.x, 0);
		m_listViewContent.setLayoutParams(frmLayoutParams);
		m_timer.cancel();
	}

	//------------------------------------------------------------------------------
	private void ShowActivityComedy(int a_iIndex) {
		Intent		intentComedy2SubComedy;
		Bundle		bundle;
		ItemDataComedy		itemDataComedy;
		
		itemDataComedy = m_adapterComedy.GetItemData(a_iIndex);

		intentComedy2SubComedy = new Intent(m_context, ActivitySubComedy.class);		// ActivityMain -> ActivitySubComedy
		bundle = new Bundle();
		bundle.putCharSequence(Const.KEY_TERM,		itemDataComedy.GetTerm());
		intentComedy2SubComedy.putExtras(bundle);
		m_context.startActivity(intentComedy2SubComedy);
	}

	//------------------------------------------------------------------------------
}

//==============================================================================
