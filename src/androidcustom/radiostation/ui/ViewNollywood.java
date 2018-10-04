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
public class ViewNollywood {

	private Context				m_context;
	private ListView			m_listViewContent;
	private AdapterNollywood		m_adapterNollywood;

	private	HttpApi				m_httpApi;

	private	CountDownTimer 		m_timer;

	//------------------------------------------------------------------------------
	public ViewNollywood(Context a_context) {
		m_context = a_context;

		m_httpApi = null;

		m_listViewContent	= new ListView(m_context);
		m_adapterNollywood		= new AdapterNollywood(m_context);
		m_listViewContent.setAdapter(m_adapterNollywood);

		m_listViewContent.setOnItemClickListener(
			new OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> adapter, View v, int position, long arg3) {
					ShowActivityNollywood(position);
				}
			}
		);
		
	// {{ Timer to Invalidate ListView
		m_timer = new CountDownTimer(Const.DELAY_INVALIDATE, 100) {
			@Override
			public void onFinish() {
				if (EnvVariable.CurrentMainItem == Const.ITEM_NOLLYWOOD)
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
						JSONArray			jsonArrNollywoodList;
						int					iIndex;

						if (a_strResult == null)		return;
						if (a_strResult.length() == 0)	return;

						try {
							jsonObj = new JSONObject(a_strResult);
							jsonArrNollywoodList = jsonObj.getJSONArray("nollywoodlist");
							for (iIndex = 0; iIndex < jsonArrNollywoodList.length(); iIndex++) {
								ItemDataNollywood		itemDataNollywood = new ItemDataNollywood();
								JSONObject			jsonNollywoodData = jsonArrNollywoodList.getJSONObject(iIndex);
								try {
									itemDataNollywood.SetName(jsonNollywoodData.getString("videoname"));
									itemDataNollywood.SetTerm(jsonNollywoodData.getString("videoterm"));
								} catch (Exception e) {}

								m_adapterNollywood.AddItemData(itemDataNollywood);
							}
						}
						catch (JSONException e1) {}
						catch (Exception e2) {}
					}
				}
			);
			m_httpApi.SetApiType(Const.TYPE_NOLLYWOOD);
			m_httpApi.StartGetNollywood();
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
	private void ShowActivityNollywood(int a_iIndex) {
		Intent		intentNollywood2SubNollywood;
		Bundle		bundle;
		ItemDataNollywood		itemDataNollywood;
		
		itemDataNollywood = m_adapterNollywood.GetItemData(a_iIndex);

		intentNollywood2SubNollywood = new Intent(m_context, ActivitySubNollywood.class);		// ActivityMain -> ActivitySubNollywood
		bundle = new Bundle();
		bundle.putCharSequence(Const.KEY_TERM,		itemDataNollywood.GetTerm());
		intentNollywood2SubNollywood.putExtras(bundle);
		m_context.startActivity(intentNollywood2SubNollywood);
	}

	//------------------------------------------------------------------------------
}

//==============================================================================
