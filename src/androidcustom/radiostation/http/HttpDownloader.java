package androidcustom.radiostation.http;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import android.os.AsyncTask;
import androidcustom.radiostation.global.Const;

//==============================================================================
public class HttpDownloader extends AsyncTask<Object, Object, String> {

	//{{============================================================================
	public interface HttpDownloaderListener {
		public void OnDownloaderResult(int a_iType, int a_iIndex, String a_strPath);
	}
	//}}============================================================================

	//------------------------------------------------------------------------------
	private int							m_iType;
	private int							m_iIndex;
	private String						m_strUrl;
	private String						m_strFilePath;
	private String						m_strFileName;
	private boolean						m_bForceRenew;
	private HttpDownloaderListener		m_httpDownloaderListener;

	//------------------------------------------------------------------------------
	public HttpDownloader() {
		m_iType						= Const.TYPE_NONE;
		m_iIndex					= Const.INDEX_NONE;
		m_strUrl					= null;
		m_strFilePath				= null;
		m_strFileName				= null;
		m_bForceRenew				= false;
		m_httpDownloaderListener	= null;
	}

	//------------------------------------------------------------------------------
	public void SetType(int a_iType) {
		m_iType = a_iType;
	}

	//------------------------------------------------------------------------------
	public void SetIndex(int a_iIndex) {
		m_iIndex = a_iIndex;
	}

	//------------------------------------------------------------------------------
	public void SetUrl(String a_strUrl) {
		m_strUrl = a_strUrl;
	}

	//------------------------------------------------------------------------------
	public void SetFilePath(String a_strFilePath) {
		m_strFilePath = a_strFilePath;
	}

	//------------------------------------------------------------------------------
	public void SetFileName(String a_strFileName) {
		m_strFileName = a_strFileName;
	}

	//------------------------------------------------------------------------------
	public void SetForceRenew(boolean a_bForceRenew) {
		m_bForceRenew = a_bForceRenew;
	}

	//------------------------------------------------------------------------------
	public void SetDownloaderListener(HttpDownloaderListener a_httpDownloaderListener) {
		m_httpDownloaderListener = a_httpDownloaderListener;
	}

	//------------------------------------------------------------------------------
	@Override
	protected String doInBackground(Object... params) {
		URL					url = null;
		HttpURLConnection	httpUrlConnection = null;
		int					iLenRead		= 0;
		byte[]				p_cBuffer	= new byte[1024 * 8];
		InputStream			inputStream;
		File				fileOutput = new File(m_strFilePath, m_strFileName);
		FileOutputStream	fileOutputStream;

		if (fileOutput.exists()) {
			if (m_bForceRenew)
				fileOutput.delete();
			else
				return fileOutput.getAbsolutePath();
		}
		try { fileOutput.createNewFile(); } catch (Exception e) { return null; }

		try { fileOutputStream = new FileOutputStream(fileOutput); }
		catch (Exception e) { return null; }

		try { url = new URL(m_strUrl); }
		catch (Exception e) {
			try { fileOutputStream.close(); } catch (Exception ex) { return null; }
			return null;
		}

		try {
			httpUrlConnection = (HttpURLConnection)url.openConnection();
			httpUrlConnection.setRequestMethod("GET");
			httpUrlConnection.setDoInput(true);
			httpUrlConnection.connect();

			inputStream = httpUrlConnection.getInputStream();

			while((iLenRead = inputStream.read(p_cBuffer)) > 0) {
				if (iLenRead <= 0) 		break;
				try {
					fileOutputStream.write(p_cBuffer, 0, iLenRead);
				} catch (IOException e) { break; };
			}
			inputStream.close();
			fileOutputStream.close();

			httpUrlConnection.disconnect();
			httpUrlConnection = null;
			return fileOutput.getAbsolutePath();
		}
		catch (Exception e) {
			if (httpUrlConnection != null)
				httpUrlConnection.disconnect();
		}

		return null;
	}

	//------------------------------------------------------------------------------
	@Override
	protected void onPostExecute(String result) {
		m_httpDownloaderListener.OnDownloaderResult(m_iType, m_iIndex, result);
	}

}

//==============================================================================
