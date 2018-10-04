package androidcustom.radiostation.ui;

import android.content.Context;
import android.util.Log;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import androidcustom.radiostation.global.Const;
import androidcustom.radiostation.global.EnvVariable;
import androidcustom.radiostation.loader.LoaderImage;

//==============================================================================
public class ViewFacebook {

	private Context			m_context;
	private WebView			m_webViewContent;
	private	boolean			m_bFirstShow;

	//------------------------------------------------------------------------------
	public ViewFacebook(Context a_context) {

		m_context = a_context;
		m_bFirstShow = false;
		m_webViewContent	= new WebView(m_context);
		m_webViewContent.getSettings().setJavaScriptEnabled(true);
		m_webViewContent.getSettings().setDomStorageEnabled(true);
	}

	//------------------------------------------------------------------------------
	public void SetLayout(FrameLayout a_frmlytParent) {
		FrameLayout.LayoutParams	frmLayoutParams;

		a_frmlytParent.addView(m_webViewContent);

	// {{ m_webviewContent
		frmLayoutParams = new FrameLayout.LayoutParams(
				FrameLayout.LayoutParams.FILL_PARENT,
				FrameLayout.LayoutParams.FILL_PARENT);
		frmLayoutParams.setMargins((int)((1 +  Const.RATIO_WIDTH_MAINLIST) * EnvVariable.SizeDisplay.x), 0,
										(-1) * EnvVariable.SizeDisplay.x, 0);
		m_webViewContent.setLayoutParams(frmLayoutParams);
	// }} m_webviewContent
	}

	//------------------------------------------------------------------------------
	public WebView GetContentView() {
		return m_webViewContent;
	}

	//------------------------------------------------------------------------------
	public void Show() {
		FrameLayout.LayoutParams	frmLayoutParams;

		if (LoaderImage.GetInstance() != null)
			LoaderImage.GetInstance().ClearCache();

		frmLayoutParams = (FrameLayout.LayoutParams)m_webViewContent.getLayoutParams();
		frmLayoutParams.setMargins((int)(Const.RATIO_WIDTH_MAINLIST * EnvVariable.SizeDisplay.x), 0, 0, 0);
		m_webViewContent.setLayoutParams(frmLayoutParams);

		if (!m_bFirstShow) {
			m_bFirstShow = true;
			m_webViewContent.setWebViewClient(new WebViewClient());
			Log.e("VIEW", "Should Override FACEBOOK");
			m_webViewContent.loadUrl("https://facebook.com");
		}
	}

	//------------------------------------------------------------------------------
	public void Hide() {
		FrameLayout.LayoutParams	frmLayoutParams;
		if (LoaderImage.GetInstance() != null)
			LoaderImage.GetInstance().ClearCache();

		frmLayoutParams = (FrameLayout.LayoutParams)m_webViewContent.getLayoutParams();
		frmLayoutParams.setMargins((int)((1 +  Const.RATIO_WIDTH_MAINLIST) * EnvVariable.SizeDisplay.x), 0,
										(-1) * EnvVariable.SizeDisplay.x, 0);
		m_webViewContent.setLayoutParams(frmLayoutParams);
	}

	//------------------------------------------------------------------------------
}

//==============================================================================
