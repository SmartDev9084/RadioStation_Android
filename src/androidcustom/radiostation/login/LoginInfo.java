package androidcustom.radiostation.login;

//==============================================================================
public class LoginInfo {

	private String	m_strUserName;
	private String	m_strPassword;

	//------------------------------------------------------------------------------
	public LoginInfo() {
		m_strUserName = null;
		m_strPassword = null;
	}

	//------------------------------------------------------------------------------
	public LoginInfo(String a_strUserName, String a_strPassword) {
		m_strUserName = a_strUserName;
		m_strPassword = a_strPassword;
	}
	
	//------------------------------------------------------------------------------
	public void SetUserName(String a_strUserName) {
		m_strUserName = a_strUserName;
	}

	//------------------------------------------------------------------------------
	public void SetPassword(String a_strPassword) {
		m_strPassword= a_strPassword;
	}
	
	//------------------------------------------------------------------------------
	public String GetUserName() {
		return m_strUserName;
	}

	//------------------------------------------------------------------------------
	public String GetPassword() {
		return m_strPassword;
	}

}

//==============================================================================
