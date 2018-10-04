package androidcustom.radiostation.http;

import androidcustom.radiostation.global.Const;
import androidcustom.radiostation.http.HttpSendRequest.HttpSendRequestListener;

//==============================================================================
public class HttpApi {

	//==============================================================================
	public static interface HttpApiListener {
		public void OnHttpApiResult(String a_strResult, int a_iType);
	}

	//------------------------------------------------------------------------------
	private	static	String	URL_REQUEST_LOGIN		= "http://1triberadio.com/Sorikodo/login";
	private	static	String	URL_REQUEST_REGISTER	= "http://1triberadio.com/Sorikodo/signup";
	private static	String	URL_REQUEST_LOSTPSWD	= "http://1triberadio.com/Sorikodo/lostpwd";
	private	static	String	URL_REQUEST_MAIN		= "http://1triberadio.com/wp-content/uploads/index.php";
	private	static	String	URL_REQUEST_COMEDY		= "http://gdata.youtube.com/feeds/api/videos";
	private	static	String	URL_REQUEST_NOLLYWOOD	= "http://gdata.youtube.com/feeds/api/videos";
	
	private int		m_iType;

	private HttpSendRequest				m_httpSendRequest;
	private HttpSendRequestListener		m_httpSendRequestListener;
	private HttpRequestParameter	m_httpRequestParameter;
	private HttpApiListener			m_httpApiListener;

	//------------------------------------------------------------------------------
	public HttpApi() {

		m_httpSendRequestListener = new HttpSendRequestListener() {
			@Override
			public void OnSendRequestResult(String a_strResult) {
				if (m_httpApiListener != null)
					m_httpApiListener.OnHttpApiResult(a_strResult, m_iType);
			}
		};

		m_iType = Const.TYPE_NONE;
		m_httpSendRequest = new HttpSendRequest();
		m_httpSendRequest.SetRequestListener(m_httpSendRequestListener);
		m_httpRequestParameter = new HttpRequestParameter();
		m_httpApiListener = null;
	}
	
	//------------------------------------------------------------------------------
	public void SetApiType(int a_iType) {
		m_iType = a_iType;
	}

	//------------------------------------------------------------------------------
	public void SetHttpApiListener(HttpApiListener a_httpApiListener) {
		m_httpApiListener = a_httpApiListener;
	}

	//------------------------------------------------------------------------------
	public void StartLogin(String a_strUserName, String a_strPassword) {
		if (a_strUserName == null)				return;
		if (a_strPassword == null)				return;
		if (a_strUserName.length() == 0)		return;
		if (a_strPassword.length() == 0)		return;
		m_httpRequestParameter.Clear();
		m_httpRequestParameter.AddParameter("username", a_strUserName);
		m_httpRequestParameter.AddParameter("password", a_strPassword);
		m_httpSendRequest.SetRequestUrl(URL_REQUEST_LOGIN);
		m_httpSendRequest.SetRequestParameter(m_httpRequestParameter);
		m_httpSendRequest.execute();
	}

	//------------------------------------------------------------------------------
	public void StartRegister(String a_strUserName, String a_strPassword, String a_strEmail) {
		if (a_strUserName	== null)			return;
		if (a_strPassword	== null)			return;
		if (a_strEmail		== null)			return;
		if (a_strUserName.length()	== 0)		return;
		if (a_strPassword.length()	== 0)		return;
		if (a_strEmail.length()		== 0)		return;
		m_httpRequestParameter.Clear();
		m_httpRequestParameter.AddParameter("username",	a_strUserName);
		m_httpRequestParameter.AddParameter("password",	a_strPassword);
		m_httpRequestParameter.AddParameter("email",	a_strEmail);
		m_httpSendRequest.SetRequestUrl(URL_REQUEST_REGISTER);
		m_httpSendRequest.SetRequestParameter(m_httpRequestParameter);
		m_httpSendRequest.execute();
	}

	//------------------------------------------------------------------------------
	public void StartGetPswd(String a_strUserName) {
		if (a_strUserName	== null)			return;
		if (a_strUserName.length()	== 0)		return;
		m_httpRequestParameter.Clear();
		m_httpRequestParameter.AddParameter("username",	a_strUserName);
		m_httpSendRequest.SetRequestUrl(URL_REQUEST_LOSTPSWD);
		m_httpSendRequest.SetRequestParameter(m_httpRequestParameter);
		m_httpSendRequest.execute();
	}

	//------------------------------------------------------------------------------
	public void StartGetHomeList(String a_strCategory) {
		if (a_strCategory == null)				return;
		if (a_strCategory.length() == 0)		return;
		m_httpRequestParameter.Clear();
		m_httpRequestParameter.AddParameter("method", "songlist");
		m_httpRequestParameter.AddParameter("category", a_strCategory);
		m_httpSendRequest.SetRequestUrl(URL_REQUEST_MAIN);
		m_httpSendRequest.SetRequestParameter(m_httpRequestParameter);
		m_httpSendRequest.execute();
	}

	//------------------------------------------------------------------------------
	public void StartGetPlaylist() {
		m_httpRequestParameter.Clear();
		m_httpRequestParameter.AddParameter("method", "count");
		m_httpSendRequest.SetRequestUrl(URL_REQUEST_MAIN);
		m_httpSendRequest.SetRequestParameter(m_httpRequestParameter);
		m_httpSendRequest.execute();
	}

	//------------------------------------------------------------------------------
	public void StartGetSubPlaylist(String a_strType) {
		if (a_strType == null)				return;
		if (a_strType.length() == 0)		return;
		m_httpRequestParameter.Clear();
		m_httpRequestParameter.AddParameter("method", "playlist");
		m_httpRequestParameter.AddParameter("type", a_strType);
		m_httpSendRequest.SetRequestUrl(URL_REQUEST_MAIN);
		m_httpSendRequest.SetRequestParameter(m_httpRequestParameter);
		m_httpSendRequest.execute();
	}

	//------------------------------------------------------------------------------
	public void StartGetPodcast() {
		m_httpRequestParameter.Clear();
		m_httpRequestParameter.AddParameter("method", "podcastcount");
		m_httpSendRequest.SetRequestUrl(URL_REQUEST_MAIN);
		m_httpSendRequest.SetRequestParameter(m_httpRequestParameter);
		m_httpSendRequest.execute();
	}

	//------------------------------------------------------------------------------
	public void StartGetSubPodcast(String a_strType) {
		if (a_strType == null)				return;
		if (a_strType.length() == 0)		return;
		m_httpRequestParameter.Clear();
		m_httpRequestParameter.AddParameter("method", "playlist");
		m_httpRequestParameter.AddParameter("type", a_strType);
		m_httpSendRequest.SetRequestUrl(URL_REQUEST_MAIN);
		m_httpSendRequest.SetRequestParameter(m_httpRequestParameter);
		m_httpSendRequest.execute();
	}

	//------------------------------------------------------------------------------
	public void StartGetVideo() {
		m_httpRequestParameter.Clear();
		m_httpRequestParameter.AddParameter("method", "videolist");
		m_httpSendRequest.SetRequestUrl(URL_REQUEST_MAIN);
		m_httpSendRequest.SetRequestParameter(m_httpRequestParameter);
		m_httpSendRequest.execute();
	}
	
	//------------------------------------------------------------------------------
	public void StartGetComedy() {
		m_httpRequestParameter.Clear();
		m_httpRequestParameter.AddParameter("method", "comedylist");
		m_httpSendRequest.SetRequestUrl(URL_REQUEST_MAIN);
		m_httpSendRequest.SetRequestParameter(m_httpRequestParameter);
		m_httpSendRequest.execute();
	}
	
	//------------------------------------------------------------------------------
	public void StartGetSubComedy(String a_strTerm, int a_iIndex) {
		m_httpRequestParameter.Clear();

		m_httpRequestParameter.AddParameter("alt",			"json");
		m_httpRequestParameter.AddParameter("v",			"2");
		m_httpRequestParameter.AddParameter("format",		"1");
		m_httpRequestParameter.AddParameter("safeSearch",	"none");
		m_httpRequestParameter.AddParameter("max-results",	"1");
		m_httpRequestParameter.AddParameter("q",			a_strTerm);
		m_httpRequestParameter.AddParameter("start-index",	String.valueOf(a_iIndex + 1));
		m_httpRequestParameter.AddParameter("orderby",		"relevance");
//		m_httpRequestParameter.AddParameter("key",			"AIzaSyAY5wyhp7vnjxHnmMG7iYFvytVfsf6M3Dc");
		m_httpRequestParameter.AddParameter("key",			"");
		m_httpSendRequest.SetRequestUrl(URL_REQUEST_COMEDY);
		m_httpSendRequest.SetRequestParameter(m_httpRequestParameter);
		try { m_httpSendRequest.execute(); } catch (Exception e) {}
	}

	//------------------------------------------------------------------------------
	public void StartGetNollywood() {
		m_httpRequestParameter.Clear();
		m_httpRequestParameter.AddParameter("method", "nollywoodlist");
		m_httpSendRequest.SetRequestUrl(URL_REQUEST_MAIN);
		m_httpSendRequest.SetRequestParameter(m_httpRequestParameter);
		m_httpSendRequest.execute();
	}
	
	//------------------------------------------------------------------------------
	public void StartGetSubNollywood(String a_strTerm, int a_iIndex) {
		m_httpRequestParameter.Clear();

		m_httpRequestParameter.AddParameter("alt",			"json");
		m_httpRequestParameter.AddParameter("v",			"2");
		m_httpRequestParameter.AddParameter("format",		"1");
		m_httpRequestParameter.AddParameter("safeSearch",	"none");
		m_httpRequestParameter.AddParameter("max-results",	"1");
		m_httpRequestParameter.AddParameter("q",			a_strTerm);
		m_httpRequestParameter.AddParameter("start-index",	String.valueOf(a_iIndex + 1));
		m_httpRequestParameter.AddParameter("orderby",		"relevance");
//		m_httpRequestParameter.AddParameter("key",			"AIzaSyAY5wyhp7vnjxHnmMG7iYFvytVfsf6M3Dc");
		m_httpRequestParameter.AddParameter("key",			"");
		m_httpSendRequest.SetRequestUrl(URL_REQUEST_NOLLYWOOD);
		m_httpSendRequest.SetRequestParameter(m_httpRequestParameter);
		try { m_httpSendRequest.execute(); } catch (Exception e) {}
	}

	//------------------------------------------------------------------------------
	public void StartGetPhoto() {
		m_httpRequestParameter.Clear();
		m_httpRequestParameter.AddParameter("method", "imagelist");
		m_httpSendRequest.SetRequestUrl(URL_REQUEST_MAIN);
		m_httpSendRequest.SetRequestParameter(m_httpRequestParameter);
		m_httpSendRequest.execute();
	}

	//------------------------------------------------------------------------------
	public void IncreaseCountLike(String a_strSongName, String a_strArtist) {
		m_httpRequestParameter.Clear();
		m_httpRequestParameter.AddParameter("method",		"save");
		m_httpRequestParameter.AddParameter("songname",		a_strSongName);
		m_httpRequestParameter.AddParameter("artist",		a_strArtist);
		m_httpRequestParameter.AddParameter("likecount",	"1");
		m_httpSendRequest.SetRequestUrl(URL_REQUEST_MAIN);
		m_httpSendRequest.SetRequestParameter(m_httpRequestParameter);
		try { m_httpSendRequest.execute(); } catch (Exception e) {}
	}

	//------------------------------------------------------------------------------
	public void IncreaseCountPlay(String a_strSongName, String a_strArtist) {
		m_httpRequestParameter.Clear();
		m_httpRequestParameter.AddParameter("method",		"save");
		m_httpRequestParameter.AddParameter("songname",		a_strSongName);
		m_httpRequestParameter.AddParameter("artist",		a_strArtist);
		m_httpRequestParameter.AddParameter("playcount",	"1");
		m_httpSendRequest.SetRequestUrl(URL_REQUEST_MAIN);
		m_httpSendRequest.SetRequestParameter(m_httpRequestParameter);
		try { m_httpSendRequest.execute(); } catch (Exception e) {}
	}
}

//==============================================================================
