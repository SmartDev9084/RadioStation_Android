package androidcustom.radiostation.multimedia;

import androidcustom.radiostation.global.Const;

//==============================================================================
public class MultimediaInfo {
	private		int			m_iIndex;
	private		int			m_iType;
	private		String		m_strId;
	private		String		m_strTitle;
	private		String		m_strArtist;
	private		String		m_strPath1;			// For Tag : mp3 in audio
	private		String		m_strPath2;			// For Tag : webmv in audio
	private		String		m_strLink;
	private		String		m_strThumbnail;
	private		String		m_strPoster;
	private		String		m_strDuration;
	private		int			m_iLikeCount;

	//------------------------------------------------------------------------------
	public MultimediaInfo() {
		m_iIndex		= Const.INDEX_NONE;
		m_iType			= Const.TYPE_NONE;
		m_strId			= "";
		m_strTitle		= "";
		m_strArtist		= "";
		m_strPath1		= "";
		m_strPath2		= "";
		m_strPoster		= "";
		m_strDuration	= "";
		m_iLikeCount	= 0;
	}

	//------------------------------------------------------------------------------
	public void SetId(String a_strId) {
		if (a_strId == null)
			m_strId = "";
		else
			m_strId = a_strId.replace("'", "");
	}

	//------------------------------------------------------------------------------
	public void SetIndex(int a_iIndex) {
		m_iIndex = a_iIndex;
	}

	//------------------------------------------------------------------------------
	public void SetType(int a_iType) {
		m_iType = a_iType;
	}

	//------------------------------------------------------------------------------
	public void SetTitle(String a_strTitle) {
		if (a_strTitle == null)
			m_strTitle = "";
		else
			m_strTitle = a_strTitle.replace("'", "");
	}

	//------------------------------------------------------------------------------
	public void SetArtist(String a_strArtist) {
		if (a_strArtist == null)
			m_strArtist = "";
		else
			m_strArtist = a_strArtist.replace("'", "");
	}

	//------------------------------------------------------------------------------
	public void SetPath1(String a_strPath1) {
		if (a_strPath1 == null)
			m_strPath1 = "";
		else
			m_strPath1 = a_strPath1.replace("'", "");
	}

	//------------------------------------------------------------------------------
	public void SetPath2(String a_strPath2) {
		if (a_strPath2 == null)
			m_strPath2 = "";
		else
			m_strPath2 = a_strPath2.replace("'", "");
	}

	//------------------------------------------------------------------------------
	public void SetLink(String a_strLink) {
		if (a_strLink == null)
			m_strLink = "";
		else
			m_strLink = a_strLink.replace("'", "");
	}

	//------------------------------------------------------------------------------
	public void SetThumb(String a_strThumbnail) {
		if (a_strThumbnail == null)
			m_strThumbnail = "";
		else
			m_strThumbnail = a_strThumbnail.replace("'", "");
	}

	//------------------------------------------------------------------------------
	public void SetPoster(String a_strPoster) {
		if (a_strPoster == null)
			m_strPoster = "";
		else
			m_strPoster = a_strPoster.replace("'", "");
	}

	//------------------------------------------------------------------------------
	public void SetDuration(String a_strDuration) {
		if (a_strDuration == null)
			m_strDuration = "";
		else
			m_strDuration = a_strDuration.replace("'", "");		// For Db Insert
	}

	//------------------------------------------------------------------------------
	public void SetLikeCount(int a_iLikeCount) {
		m_iLikeCount = a_iLikeCount;
	}

	//------------------------------------------------------------------------------
	public String GetId() {
		return m_strId;
	}

	//------------------------------------------------------------------------------
	public int GetIndex() {
		return m_iIndex;
	}

	//------------------------------------------------------------------------------
	public int GetType() {
		return m_iType;
	}

	//------------------------------------------------------------------------------
	public String GetTitle() {
		return m_strTitle;
	}

	//------------------------------------------------------------------------------
	public String GetArtist() {
		return m_strArtist;
	}

	//------------------------------------------------------------------------------
	public String GetPath1() {
		return m_strPath1;
	}

	//------------------------------------------------------------------------------
	public String GetPath2() {
		return m_strPath2;
	}

	//------------------------------------------------------------------------------
	public String GetLink() {
		return m_strLink;
	}

	//------------------------------------------------------------------------------
	public String GetThumb() {
		return m_strThumbnail;
	}

	//------------------------------------------------------------------------------
	public String GetPoster() {
		return m_strPoster;
	}

	//------------------------------------------------------------------------------
	public String GetDuration() {
		return m_strDuration;
	}

	//------------------------------------------------------------------------------
	public int GetLikeCount() {
		return m_iLikeCount;
	}

	//------------------------------------------------------------------------------
	public boolean ContainText(String a_strFindText) {
		if (a_strFindText.isEmpty())
			return true;
		if (m_strTitle != null)
		if ((!m_strTitle.isEmpty()) && (m_strTitle.contains(a_strFindText)))
			return true;
		if (m_strArtist != null)
		if ((!m_strArtist.isEmpty()) && (m_strArtist.contains(a_strFindText)))
			return true;
		return false;
	}

	//------------------------------------------------------------------------------
}

//==============================================================================
