package androidcustom.radiostation.ui;

//==============================================================================
public class ItemDataNollywood {

	private String		m_strName;
	private String		m_strTerm;
	
	//------------------------------------------------------------------------------
	public ItemDataNollywood() {
		m_strName = null;
		m_strTerm = null;
	}

	//------------------------------------------------------------------------------
	public ItemDataNollywood(String a_strName, String a_strData) {
		m_strName = a_strName;
		m_strTerm = a_strData;
	}

	//------------------------------------------------------------------------------
	public void SetName(String a_strName) {
		m_strName = a_strName;
	}

	//------------------------------------------------------------------------------
	public void SetTerm(String a_strData) {
		m_strTerm = a_strData;
	}
	
	//------------------------------------------------------------------------------
	public String GetName() {
		return m_strName;
	}

	//------------------------------------------------------------------------------
	public String GetTerm() {
		return m_strTerm;
	}
	//------------------------------------------------------------------------------
}

//==============================================================================
