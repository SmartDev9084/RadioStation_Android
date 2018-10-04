package androidcustom.radiostation.ui;

import android.graphics.drawable.Drawable;

//==============================================================================
public class ItemDataMain {
	private Drawable	m_drawable;		// ID of Image
	private String		m_strText;
	
	//------------------------------------------------------------------------------
	public ItemDataMain(Drawable a_drawable, String a_strText) {
		m_drawable = a_drawable;
		m_strText = a_strText;
	}

	//------------------------------------------------------------------------------
	public String GetText() {
		return m_strText;
	}

	//------------------------------------------------------------------------------
	public Drawable GetImage() {
		return m_drawable;
	}
}

//==============================================================================
