package androidcustom.radiostation.youtube;

//==============================================================================
/**
 * Represents a format in the "fmt_list" parameter
 * Currently, only id is used
 */
public class Format {

	protected int m_iId;

	//------------------------------------------------------------------------------
	/**
	 * Construct this object from one of the strings in the "fmt_list" parameter
	 *
	 * @param a_strFormat one of the comma separated strings in the "fmt_list" parameter
	 */
	public Format(String a_strFormat) {
		String arrStrFormatVar[] = a_strFormat.split("/");
		m_iId = Integer.parseInt(arrStrFormatVar[0]);
	}

	//------------------------------------------------------------------------------
	/**
	 * Construct this object using a format id
	 *
	 * @param a_iId id of this format
	 */
	public Format(int a_iId) {
		this.m_iId = a_iId;
	}

	//------------------------------------------------------------------------------
	/**
	 * Retrieve the id of this format
	 *
	 * @return the id
	 */
	public int GetId() {
		return m_iId;
	}

	//------------------------------------------------------------------------------
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object o) {
		if (!(o instanceof Format)) {
			return false;
		}
		return ((Format)o).m_iId == m_iId;
	}
}

//==============================================================================
