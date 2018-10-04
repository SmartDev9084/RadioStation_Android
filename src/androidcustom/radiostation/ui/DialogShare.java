package androidcustom.radiostation.ui;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Window;
import androidcustom.radiostation.R;

//==============================================================================
public class DialogShare extends Dialog {
	private Context				m_context;
	
	//------------------------------------------------------------------------------
	public DialogShare(Context context) {
		super(context);
		m_context = context;
	}

	//------------------------------------------------------------------------------
	protected void onCreate(Bundle bundle) {
		super.onCreate(bundle);

		setContentView(R.layout.dialog_share);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		SetLayout();
	}

	//------------------------------------------------------------------------------
	private void SetLayout() {

	}

}

//==============================================================================
