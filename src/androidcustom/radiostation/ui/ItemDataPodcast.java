package androidcustom.radiostation.ui;

//==============================================================================
public class ItemDataPodcast {

	private String		m_strText;
	private int			m_iCount;			// Song Count
	
	//------------------------------------------------------------------------------
	public ItemDataPodcast(String a_strText, int a_iCount) {
		m_strText	= a_strText;
		m_iCount	= a_iCount;
	}

	//------------------------------------------------------------------------------
	public String GetText() {
		return m_strText;
	}

	//------------------------------------------------------------------------------
	public int GetCount() {
		return m_iCount;
	}

	//------------------------------------------------------------------------------
	public void SetCount(int a_iCount) {
		m_iCount = a_iCount;
	}

	//------------------------------------------------------------------------------
}

//==============================================================================
