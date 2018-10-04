package androidcustom.radiostation.ui;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.text.Html;
import android.text.InputType;
import android.text.method.PasswordTransformationMethod;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidcustom.radiostation.R;
import androidcustom.radiostation.db.DbApi;
import androidcustom.radiostation.global.Const;
import androidcustom.radiostation.http.HttpApi;
import androidcustom.radiostation.http.HttpApi.HttpApiListener;
import androidcustom.radiostation.login.LoginInfo;

//==============================================================================
public class DialogLogin {
	
	//==============================================================================
	public static interface CommitListener {
		public void OnCommit();
	}
	//==============================================================================

	private Context				m_context;

	private	CommitListener		m_listenerCommit;

	private	FrameLayout			m_frmLytParent;
	private FrameLayout			m_frmLytContent;
	private LinearLayout		m_linLytContent;

	private RelativeLayout		m_relLytRemember;
	private EditText			m_edtTextName;
	private EditText			m_edtTextPswd;
	private ImageView			m_imgViewRememberCheck;
	private TextView			m_txtViewRemember;
	private Button				m_btnLogin;

	private	Boolean				m_bRemember;
	private Boolean				m_bLogged;
	private boolean				m_bLoginClicked;

	private	DialogProgress		m_dlgProgress;

	private static final int	ID_TXTVIEW_REMEMBER		= 0x01;
	private static final int	ID_BTN_LOGIN			= 0x02;

	//------------------------------------------------------------------------------
	public DialogLogin(Context context) {
		m_context = context;

		m_bRemember			= true;
		m_bLogged			= false;
		m_bLoginClicked		= false;
		m_frmLytParent		= null;

		m_frmLytContent				= new FrameLayout(m_context);
		m_linLytContent				= new LinearLayout(m_context);
		m_edtTextName				= new EditText(m_context);
		m_edtTextPswd				= new EditText(m_context);
		m_relLytRemember			= new RelativeLayout(m_context);
		m_txtViewRemember			= new TextView(m_context);
		m_imgViewRememberCheck		= new ImageView(m_context);
		m_btnLogin					= new Button(m_context);

		m_txtViewRemember.setId(ID_TXTVIEW_REMEMBER);
		m_btnLogin.setId(ID_BTN_LOGIN);
		
		SetLayout();

	// {{ DbApi
		if (DbApi.GetInstance() != null) {
			if (DbApi.GetInstance().HasLoginInfo()) {
				LoginInfo loginInfo = DbApi.GetInstance().GetLoginInfo();
				m_edtTextName.setText(loginInfo.GetUserName());
				m_edtTextPswd.setText(loginInfo.GetPassword());
			}
		}
	// }} DbApi

	}

	//------------------------------------------------------------------------------
	private void SetLayout() {
		Drawable						drawable;
		FrameLayout.LayoutParams		frmLayoutParams;
		LinearLayout.LayoutParams		linLayoutParams;
		RelativeLayout.LayoutParams		relLayoutParams;

	// {{ m_edtTextName, m_edtTextPswd
		m_edtTextName.setLines(1);
		m_edtTextPswd.setLines(1);
		m_edtTextName.setMaxLines(1);
		m_edtTextPswd.setMaxLines(1);
		m_edtTextPswd.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
		m_edtTextPswd.setTransformationMethod(PasswordTransformationMethod.getInstance());
		m_edtTextName.setTextSize(16);
		m_edtTextPswd.setTextSize(16);

		m_edtTextName.setHint(
			Html.fromHtml(
				"<small><small>" +
				m_context.getResources().getString(R.string.str_hint_name) +
				"</small></small>"
			)
		);
		m_edtTextPswd.setHint(
			Html.fromHtml(
				"<small><small>" +
				m_context.getResources().getString(R.string.str_hint_pswd) +
				"</small></small>"
			)
		);

	// {{ EditText Name
		linLayoutParams = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.MATCH_PARENT,
				LinearLayout.LayoutParams.WRAP_CONTENT
		);
		linLayoutParams.setMargins(10, 20, 10, 0);
		m_edtTextName.setLayoutParams(linLayoutParams);
	// }} EditText Name

	// {{ EditText Pswd
		linLayoutParams = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.MATCH_PARENT,
				LinearLayout.LayoutParams.WRAP_CONTENT
		);
		linLayoutParams.setMargins(10, 10, 10, 10);
		m_edtTextPswd.setLayoutParams(linLayoutParams);
	// }} EditText Pswd

	// {{ m_txtviewRemember
		m_txtViewRemember.setText("Remember me");
		relLayoutParams = new RelativeLayout.LayoutParams(
				RelativeLayout.LayoutParams.WRAP_CONTENT,
				RelativeLayout.LayoutParams.WRAP_CONTENT
		);
		relLayoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
		m_txtViewRemember.setLayoutParams(relLayoutParams);
   	// }} m_txtviewRemember

	// {{ m_imgviewRememberCheck
		drawable = m_context.getResources().getDrawable(R.drawable.img_btn_check_on);
		m_imgViewRememberCheck.setImageDrawable(drawable);
		m_imgViewRememberCheck.setOnClickListener(
			new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					Drawable drawable1;
					if (m_bRemember) {
						m_bRemember = false;
						drawable1 = m_context.getResources().getDrawable(R.drawable.img_btn_check_off);
						m_imgViewRememberCheck.setImageDrawable(drawable1);
					}
					else {
						m_bRemember = true;
						drawable1 = m_context.getResources().getDrawable(R.drawable.img_btn_check_on);
						m_imgViewRememberCheck.setImageDrawable(drawable1);
					}
				}
			}
		);

		relLayoutParams = new RelativeLayout.LayoutParams(
				(int)(30.0 * m_context.getResources().getDisplayMetrics().density),
				(int)(30.0 * m_context.getResources().getDisplayMetrics().density)
		);
		relLayoutParams.setMargins(0, 0, 20, 0);
		relLayoutParams.addRule(RelativeLayout.LEFT_OF, ID_TXTVIEW_REMEMBER);
		m_imgViewRememberCheck.setLayoutParams(relLayoutParams);
	// }} m_imgviewRememberCheck

	// {{ m_imgviewLogin
		m_btnLogin.setBackgroundDrawable(m_context.getResources().getDrawable(R.drawable.selector_btn_login));
		linLayoutParams = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.MATCH_PARENT,
				(int)(40.0 * m_context.getResources().getDisplayMetrics().density)
		);
		linLayoutParams.setMargins(20, 20, 20, 20);
		m_btnLogin.setLayoutParams(linLayoutParams);
	// }} m_imgviewLogin

	// {{ Add Controls
		m_relLytRemember.addView(m_imgViewRememberCheck);
		m_relLytRemember.addView(m_txtViewRemember);

		m_linLytContent.setOrientation(LinearLayout.VERTICAL);
		m_linLytContent.addView(m_edtTextName);
		m_linLytContent.addView(m_edtTextPswd);
		m_linLytContent.addView(m_relLytRemember);
		m_linLytContent.addView(m_btnLogin);
	// }} Add Controls
	
	// {{ LinearLayout Content
		frmLayoutParams = new FrameLayout.LayoutParams(
			FrameLayout.LayoutParams.MATCH_PARENT,
			FrameLayout.LayoutParams.MATCH_PARENT
		);
		m_linLytContent.setLayoutParams(frmLayoutParams);
	// }} LinearLayout Content

		m_frmLytContent.setBackgroundColor(Color.WHITE);

		m_frmLytContent.addView(m_linLytContent);
		
	// {{ Button Login
		m_btnLogin.setOnClickListener(
			new OnClickListener() {
				@Override
				public void onClick(View v) {
					if (m_bLoginClicked == false) {
						if (m_edtTextName.getText().length() == 0)	return;
						if (m_edtTextPswd.getText().length() == 0)	return;

						if (m_dlgProgress == null)
							m_dlgProgress = new DialogProgress(m_context);
						m_dlgProgress.setCanceledOnTouchOutside(false);
						m_dlgProgress.setCancelable(false);
						m_dlgProgress.show();

						HttpApi		httpApi = new HttpApi();
						httpApi.SetApiType(Const.TYPE_LOGIN);
						httpApi.SetHttpApiListener(
							new HttpApiListener() {
								@Override
								public void OnHttpApiResult(String a_strResult, int a_iType) {
									m_dlgProgress.cancel();
									if (a_strResult != null) {
										if (a_strResult.contains("<respond>1</respond>")) {
											m_bLogged = true;
											if (m_bRemember) {
												LoginInfo		loginInfo = new LoginInfo();
												loginInfo.SetUserName(m_edtTextName.getText().toString());
												loginInfo.SetPassword(m_edtTextPswd.getText().toString());
												if (DbApi.GetInstance() != null)
													DbApi.GetInstance().InsertLoginInfo(loginInfo);
											}
											m_listenerCommit.OnCommit();
										}
									}
									m_bLoginClicked = false;
									m_edtTextName.setCursorVisible(true);		m_edtTextName.setClickable(true);
									m_edtTextPswd.setCursorVisible(true);		m_edtTextPswd.setClickable(true);
								}
							}
						);
						httpApi.StartLogin(m_edtTextName.getText().toString(), m_edtTextPswd.getText().toString());
						m_bLoginClicked = true;
						m_edtTextName.setCursorVisible(false);		m_edtTextName.setClickable(false);
						m_edtTextPswd.setCursorVisible(false);		m_edtTextPswd.setClickable(false);
					}
				}
			}
		);
	// }} Button Login

	}

	//------------------------------------------------------------------------------
	public void SetCommitListener(CommitListener a_listenerCommit) {
		m_listenerCommit = a_listenerCommit;
	}
	
	//------------------------------------------------------------------------------
	public Boolean IsLogged() {
		return m_bLogged;
	}

	//------------------------------------------------------------------------------
	public String GetUserName() {
		return m_edtTextName.getText().toString();
	}

	//------------------------------------------------------------------------------
	public void SetParentView(FrameLayout a_frmLytParent) {
		m_frmLytParent = a_frmLytParent;
	}

	//------------------------------------------------------------------------------
	public void Show() {
		LayoutParams	layoutParams;

		if (m_frmLytParent == null)
			return;

		if (m_frmLytContent.getParent() == null) {
			layoutParams = new FrameLayout.LayoutParams(
					FrameLayout.LayoutParams.WRAP_CONTENT,
					FrameLayout.LayoutParams.WRAP_CONTENT,
					Gravity.CENTER
			);
			m_frmLytParent.addView(m_frmLytContent, layoutParams);
		}
		m_frmLytContent.setVisibility(View.VISIBLE);
	}

	//------------------------------------------------------------------------------
	public void Hide() {
		m_frmLytContent.setVisibility(View.INVISIBLE);
	}
}

//==============================================================================
