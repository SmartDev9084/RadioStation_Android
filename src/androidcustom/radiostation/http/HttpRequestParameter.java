package androidcustom.radiostation.http;

import java.net.URLEncoder;

//==============================================================================
public class HttpRequestParameter {
	private		StringBuilder		m_strBuilder;

	//------------------------------------------------------------------------------
	public HttpRequestParameter() {
		m_strBuilder = new StringBuilder();
	}

	//------------------------------------------------------------------------------
	public String GetParameter() {
		return m_strBuilder.toString();
	}

	//------------------------------------------------------------------------------
	public void AddParameter(String a_strField, String a_strValue) {
		if (m_strBuilder.length() > 0)
			m_strBuilder.append("&");
		m_strBuilder.append(String.format("%s=%s", a_strField, URLEncoder.encode(a_strValue)));
	}

	//------------------------------------------------------------------------------
	public void Clear() {
		if (m_strBuilder.length() > 0)
			m_strBuilder.delete(0, m_strBuilder.length());
	}
}

//==============================================================================
