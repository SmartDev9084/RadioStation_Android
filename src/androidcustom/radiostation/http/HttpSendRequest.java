package androidcustom.radiostation.http;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import android.os.AsyncTask;

//==============================================================================
public class HttpSendRequest extends AsyncTask<Object, Object, String> {

	//==============================================================================
	public interface HttpSendRequestListener {
		public void OnSendRequestResult(String a_strResult);
	}

	//------------------------------------------------------------------------------
	private String						m_strUrl;
	private HttpSendRequestListener		m_httpRequestListener;
	private HttpRequestParameter		m_httpRequestParameter;
	private int							m_iStatus;

	//------------------------------------------------------------------------------
	public HttpSendRequest() {
		m_strUrl					= null;
		m_httpRequestParameter		= null;
		m_httpRequestListener		= null;
	}

	//------------------------------------------------------------------------------
	public HttpSendRequest(String a_strUrl, HttpRequestParameter a_httpRequestParameter, HttpSendRequestListener a_httpRequestListener) {
		m_strUrl					= a_strUrl;
		m_httpRequestParameter		= a_httpRequestParameter;
		m_httpRequestListener		= a_httpRequestListener;
	}

	//------------------------------------------------------------------------------
	public void SetRequestUrl(String a_strUrl) {
		m_strUrl = a_strUrl;
	}

	//------------------------------------------------------------------------------
	public void SetRequestParameter(HttpRequestParameter a_httpRequestParameter) {
		m_httpRequestParameter = a_httpRequestParameter;
	}

	//------------------------------------------------------------------------------
	public void SetRequestListener(HttpSendRequestListener a_httpRequestListener) {
		m_httpRequestListener = a_httpRequestListener;
	}

	//------------------------------------------------------------------------------
	@Override
	protected String doInBackground(Object... params) {
		URL					url = null;
		HttpURLConnection	httpUrlConnection = null;
		int					iLenRead	= 0;
		char[]				p_cBuffer	= new char[1024 * 8];
		String				strContent	= new String();
		InputStream			inputStream;
		InputStreamReader	inputStreamReader;

		if (m_httpRequestParameter.GetParameter().length() > 0)
			m_strUrl += "?";
		m_strUrl += m_httpRequestParameter.GetParameter();

		try {
			url = new URL(m_strUrl);

			httpUrlConnection = (HttpURLConnection)url.openConnection();
			httpUrlConnection.setRequestMethod("GET");

//			httpUrlConnection.setUseCaches(false);
//			httpUrlConnection.setDoInput(true);
//			httpUrlConnection.setDoOutput(true);
//			httpUrlConnection.setConnectTimeout(5000);
//			httpUrlConnection.setReadTimeout(5000);

			httpUrlConnection.connect();

			m_iStatus = httpUrlConnection.getResponseCode();
			if (m_iStatus != HttpURLConnection.HTTP_OK) {
				httpUrlConnection.disconnect();
				return null;
			}

			inputStream = httpUrlConnection.getInputStream();
			inputStreamReader = new InputStreamReader(inputStream);

			for (; true; ) {
				iLenRead = inputStreamReader.read(p_cBuffer, 0, p_cBuffer.length);
				String	strBuffer = new String(p_cBuffer);
				if (iLenRead <= 0)
					break;
				strContent += strBuffer;
			}
			inputStreamReader.close();

			httpUrlConnection.disconnect();
			httpUrlConnection = null;
/*
			try {
				HttpClient	httpClient	= new DefaultHttpClient();
				HttpGet		httpGet		= new HttpGet(m_strUrl);
				HttpResponse	httpResponse = httpClient.execute(httpGet);
				
				strContent = EntityUtils.toString(httpResponse.getEntity());
			}
			catch (ClientProtocolException e1)	{ e1.printStackTrace(); }
			catch (IOException e2)				{ e2.printStackTrace(); }
*/
			return strContent.trim();

		}
		catch (MalformedURLException e1)	{ e1.printStackTrace(); }
		catch (IOException e2)				{ e2.printStackTrace(); }
		return null;

	}

	//------------------------------------------------------------------------------
	@Override
	protected void onPostExecute(String result) {
		m_httpRequestListener.OnSendRequestResult(result);
	}
}

//==============================================================================
