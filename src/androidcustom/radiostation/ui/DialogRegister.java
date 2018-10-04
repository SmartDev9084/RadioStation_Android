package androidcustom.radiostation.ui;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.InputType;
import android.text.method.PasswordTransformationMethod;
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
public class DialogRegister extends Dialog implements OnClickListener {
	private Context				m_context;
	private LinearLayout		m_linLytContent;
	private EditText			m_edtTextName;
	private EditText			m_edtTextMail;
	private EditText			m_edtTextPswd;
	private EditText			m_edtTextCfwd;
	private Button				m_btnRegister;
	
	private	boolean				m_bRegistered;
	private	boolean				m_bRegisterClicked;

	private	DialogProgress		m_dlgProgress;

	private	static	final	int		ID_BTN_REGISTER		= 0x01;

	//------------------------------------------------------------------------------
	public DialogRegister(Context context) {
		super(context);
		m_context = context;
		m_bRegistered		= false;
		m_bRegisterClicked	= false;
	}

	//------------------------------------------------------------------------------
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		m_linLytContent			= new LinearLayout(getContext());
		m_edtTextName			= new EditText(getContext());
		m_edtTextMail			= new EditText(getContext());
		m_edtTextPswd			= new EditText(getContext());
		m_edtTextCfwd			= new EditText(getContext());
		m_btnRegister			= new Button(getContext());

		m_btnRegister.setId(ID_BTN_REGISTER);
		m_btnRegister.setOnClickListener(this);

		SetLayout();
	}

	//------------------------------------------------------------------------------
	private void SetLayout() {
		LinearLayout.LayoutParams		linLayoutParams;

	// {{ m_edttextName, m_edttextMail, m_edttextPswd, m_edttextCfwd
		m_edtTextName.setLines(1);
		m_edtTextMail.setLines(1);
		m_edtTextPswd.setLines(1);
		m_edtTextCfwd.setLines(1);

		m_edtTextName.setTextSize(16);
		m_edtTextMail.setTextSize(16);
		m_edtTextPswd.setTextSize(16);
		m_edtTextCfwd.setTextSize(16);

		m_edtTextMail.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
		m_edtTextPswd.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
		m_edtTextCfwd.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
		m_edtTextPswd.setTransformationMethod(PasswordTransformationMethod.getInstance());
		m_edtTextCfwd.setTransformationMethod(PasswordTransformationMethod.getInstance());
		m_edtTextName.setHint(R.string.str_hint_name);
		m_edtTextMail.setHint(R.string.str_hint_mail);
		m_edtTextPswd.setHint(R.string.str_hint_pswd);
		m_edtTextCfwd.setHint(R.string.str_hint_cfwd);

		linLayoutParams = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.MATCH_PARENT,
				LinearLayout.LayoutParams.MATCH_PARENT);
		linLayoutParams.setMargins(5, 20, 5, 0);
		m_edtTextName.setLayoutParams(linLayoutParams);

		linLayoutParams = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.MATCH_PARENT,
				LinearLayout.LayoutParams.MATCH_PARENT);
		linLayoutParams.setMargins(5, 10, 5, 0);
		m_edtTextMail.setLayoutParams(linLayoutParams);
		m_edtTextPswd.setLayoutParams(linLayoutParams);
		m_edtTextCfwd.setLayoutParams(linLayoutParams);

		// {{ Left Image For m_edtTextName
			/*
			drawable = getContext().getResources().getDrawable(R.drawable.img_txt_user);
			drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
			m_edtTextName.setCompoundDrawables(drawable, null, null, null);
			*/
		// }} Left Image For m_edtTextName
	
		// {{ Left Image For m_edtTextMail
			/*
			drawable = getContext().getResources().getDrawable(R.drawable.img_txt_mail);
			drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
			m_edtTextMail.setCompoundDrawables(drawable, null, null, null);
			*/
		// }} Left Image For m_edtTextMail

		// {{ Left Image For m_edtTextPswd
			/*
			drawable = getContext().getResources().getDrawable(R.drawable.img_txt_pswd);
			drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
			m_edtTextPswd.setCompoundDrawables(drawable, null, null, null);
			*/
		// }} Left Image For m_edtTextName

		// {{ Left Image For m_edtTextCfwd
			/*
			drawable = getContext().getResources().getDrawable(R.drawable.img_txt_pswd);
			drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
			m_edtTextCfwd.setCompoundDrawables(drawable, null, null, null);
			*/
		// }} Left Image For m_edtTextName
	// }} m_edttextName, m_edttextMail, m_edttextPswd, m_edttextCfwd

	// {{ m_imgviewRegister
		m_btnRegister.setBackgroundDrawable(getContext().getResources().getDrawable(R.drawable.selector_btn_register));
		linLayoutParams = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.MATCH_PARENT,
				(int)(40.0 * m_context.getResources().getDisplayMetrics().density)
		);
		linLayoutParams.setMargins(30, 20, 30, 20);
		m_btnRegister.setLayoutParams(linLayoutParams);
   	// }} m_imgviewRegister

	// {{ Add Controls
		m_linLytContent.setOrientation(LinearLayout.VERTICAL);
		m_linLytContent.addView(m_edtTextName);
		m_linLytContent.addView(m_edtTextMail);
		m_linLytContent.addView(m_edtTextPswd);
		m_linLytContent.addView(m_edtTextCfwd);
		m_linLytContent.addView(m_btnRegister);
	// }} Add Controls

		setContentView(m_linLytContent);
	}	

	//------------------------------------------------------------------------------
	public void onClick(View view) {
		switch (view.getId()) {
			case ID_BTN_REGISTER:
				if (m_bRegisterClicked == false) {
					if (m_edtTextName.getText().length() == 0)	return;
					if (m_edtTextMail.getText().length() == 0)	return;
					if (m_edtTextPswd.getText().length() == 0)	return;
					if (m_edtTextCfwd.getText().length() == 0)	return;
					if (!(m_edtTextPswd.getText().toString().equals(m_edtTextCfwd.getText().toString())))
						return;

					if (m_dlgProgress == null)
						m_dlgProgress = new DialogProgress(m_context);
					m_dlgProgress.setCanceledOnTouchOutside(false);
					m_dlgProgress.setCancelable(false);
					m_dlgProgress.show();

					HttpApi		httpApi = new HttpApi();
					httpApi.SetApiType(Const.TYPE_REGISTER);
					httpApi.SetHttpApiListener(
						new HttpApiListener() {
							@Override
							public void OnHttpApiResult(String a_strResult, int a_iType) {
								m_dlgProgress.cancel();
								if (a_strResult != null) {
									if (a_strResult.contains("<respond>1</respond>")) {
										m_bRegistered = true;
										cancel();		// Exit Dialog
									}
								}

								m_bRegisterClicked = false;
								m_edtTextName.setCursorVisible(true);		m_edtTextName.setClickable(true);
								m_edtTextPswd.setCursorVisible(true);		m_edtTextPswd.setClickable(true);
								m_edtTextCfwd.setCursorVisible(true);		m_edtTextCfwd.setClickable(true);
								m_edtTextMail.setCursorVisible(true);		m_edtTextMail.setClickable(true);
							}
						}
					);
					httpApi.StartRegister(m_edtTextName.getText().toString(),
										m_edtTextPswd.getText().toString(),
										m_edtTextMail.getText().toString());
					m_bRegisterClicked = true;
					m_edtTextName.setCursorVisible(false);		m_edtTextName.setClickable(false);
					m_edtTextMail.setCursorVisible(false);		m_edtTextMail.setClickable(false);
					m_edtTextPswd.setCursorVisible(false);		m_edtTextPswd.setClickable(false);
					m_edtTextCfwd.setCursorVisible(false);		m_edtTextCfwd.setClickable(false);
				}
				break;
			default:
				break;
		}
	}

	//------------------------------------------------------------------------------
	public boolean IsRegistered() {
		return m_bRegistered;
	}
	
	//------------------------------------------------------------------------------
	public String GetUserName() {
		return m_edtTextName.getText().toString();
	}

}

//==============================================================================
