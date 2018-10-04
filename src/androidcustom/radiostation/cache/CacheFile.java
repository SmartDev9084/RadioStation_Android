package androidcustom.radiostation.cache;

import java.io.File;

import android.content.Context;
import androidcustom.radiostation.global.Const;

//==============================================================================
public class CacheFile {

	private File m_fileCacheDir;

	//------------------------------------------------------------------------------
	public CacheFile(Context a_context){
		if (android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED))
			m_fileCacheDir = new File(Const.APP_DIR, Const.STRING_CACHE);
		else
			m_fileCacheDir = a_context.getCacheDir();
		if (m_fileCacheDir.exists())
			m_fileCacheDir.delete();
		m_fileCacheDir.mkdirs();
	}
	
	//------------------------------------------------------------------------------
	public File GetFile(String a_strUrl){
		String	strFilename	= String.valueOf(a_strUrl.hashCode());
		File	file		= new File(m_fileCacheDir, strFilename);
		return file;
		
	}

	//------------------------------------------------------------------------------
	public void Clear() {
		File[] arrFile = m_fileCacheDir.listFiles();
		if (arrFile == null)
			return;
		for (File file : arrFile)
			file.delete();
	}

}
//==============================================================================
