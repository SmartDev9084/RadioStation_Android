package androidcustom.radiostation.ui;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import androidcustom.radiostation.R;
import androidcustom.radiostation.global.Const;
import androidcustom.radiostation.http.HttpApi;
import androidcustom.radiostation.http.HttpApi.HttpApiListener;

//==============================================================================
public class DialogGetPswd extends Dialog implements OnClickListener {
	private Context				m_context;
	private LinearLayout		m_linLytContent;
	private EditText			m_edtTextName;
	private Button				m_btnGetPswd;

	private	boolean				m_bGetPswdClicked;

	private	static	final	int		ID_BTN_GETPSWD		= 0x01;

	//------------------------------------------------------------------------------
	public DialogGetPswd(Context context) {
		super(context);
		m_context = context;
		m_bGetPswdClicked = false;
	}

	//------------------------------------------------------------------------------
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		m_linLytContent		= new LinearLayout(getContext());
		m_edtTextName		= new EditText(getContext());
		m_btnGetPswd		= new Button(getContext());

		m_edtTextName.setTextSize(16);

		m_btnGetPswd.setId(ID_BTN_GETPSWD);
		m_btnGetPswd.setOnClickListener(this);

		SetLayout();
	}

	//------------------------------------------------------------------------------
	private void SetLayout() {
//		Drawable						drawable;
		LinearLayout.LayoutParams		linLayoutParams;

	// {{ m_edttextName
		m_edtTextName.setLines(1);
		m_edtTextName.setHint(R.string.str_hint_name);
		linLayoutParams = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.MATCH_PARENT,
				LinearLayout.LayoutParams.MATCH_PARENT);
		linLayoutParams.setMargins(5, 20, 5, 0);
		m_edtTextName.setLayoutParams(linLayoutParams);

		// {{ Left Image For m_edtTextName
		/*
		drawable = getContext().getResources().getDrawable(R.drawable.img_txt_user);
		drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
		m_edtTextName.setCompoundDrawables(drawable, null, null, null);
		*/
		// }} Left Image For m_edtTextName
	// }} m_edttextName

	// {{ m_imgViewGetPswd
		m_btnGetPswd.setBackgroundDrawable(getContext().getResources().getDrawable(R.drawable.selector_btn_getpswd));
		linLayoutParams = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.MATCH_PARENT,
				(int)(40.0 * m_context.getResources().getDisplayMetrics().density)
		);
		linLayoutParams.setMargins(30, 20, 30, 20);
		m_btnGetPswd.setLayoutParams(linLayoutParams);
   	// }} m_imgViewGetPswd

	// {{ Add Controls
		m_linLytContent.setOrientation(LinearLayout.VERTICAL);
		m_linLytContent.addView(m_edtTextName);
		m_linLytContent.addView(m_btnGetPswd);
	// }} Add Controls

		setContentView(m_linLytContent);
	}

	//------------------------------------------------------------------------------
	public void onClick(View view) {
		switch (view.getId()) {
			case ID_BTN_GETPSWD:
				if (m_bGetPswdClicked == false) {
					if (m_edtTextName.getText().length() == 0)	return;

					HttpApi		httpApi = new HttpApi();
					httpApi.SetApiType(Const.TYPE_LOGIN);
					httpApi.SetHttpApiListener(
						new HttpApiListener() {
							@Override
							public void OnHttpApiResult(String a_strResult, int a_iType) {
								if (a_strResult != null) {
									if (a_strResult.contains("<respond>1</respond>")) {
										cancel();		// Exit Dialog
									}
								}
								m_bGetPswdClicked = false;
								m_edtTextName.setCursorVisible(true);		m_edtTextName.setClickable(true);
							}
						}
					);
					httpApi.StartGetPswd(m_edtTextName.getText().toString());
					m_bGetPswdClicked = true;
					m_edtTextName.setCursorVisible(false);		m_edtTextName.setClickable(false);
				}
				break;
			default:
				break;
		}
	}
}
