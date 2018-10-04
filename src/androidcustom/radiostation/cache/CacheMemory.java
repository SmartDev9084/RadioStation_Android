package androidcustom.radiostation.cache;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import android.graphics.Bitmap;

//==============================================================================
public class CacheMemory {

//	private	static final String TAG = "YTMemoryCache";
	// Last argument true for LRU ordering
	private	Map<String, Bitmap> m_mapCache = Collections.synchronizedMap(
		new LinkedHashMap<String, Bitmap>(10, 1.0f, false)
	);
	private	long	m_lSize = 0;						//current allocated size
	private	long	m_lLimit = 1024 * 1024;				//reInited below, max memory in bytes	// YINGZHI 141220	Prev 1000000

	//------------------------------------------------------------------------------
	public CacheMemory() {
		// use 25% of available heap size
		SetLimit(Runtime.getRuntime().maxMemory() / 4);		// YINGZHI	141220	Prev 1/4
	}
	
	//------------------------------------------------------------------------------
	public void SetLimit(long a_lLimit) {
		m_lLimit = a_lLimit;
	}

	//------------------------------------------------------------------------------
	public Bitmap Get(String a_strId) {
		try {
			if (!m_mapCache.containsKey(a_strId))
				return null;
			return m_mapCache.get(a_strId);
		}
		catch (NullPointerException ex) {
			ex.printStackTrace();
			return null;
		}
	}

	//------------------------------------------------------------------------------
	public void Put(String a_strId, Bitmap a_bitmap) {
		try {
			if (m_mapCache.containsKey(a_strId))
				return;
			if (m_lSize + GetSizeInBytes(a_bitmap) > m_lLimit) {
				Clear();
			}
			m_mapCache.put(a_strId, a_bitmap);
			m_lSize += GetSizeInBytes(a_bitmap);
//			CheckSize();
		} catch(Throwable th) {}
	}
	
	//------------------------------------------------------------------------------
	private void CheckSize() {
		if (m_lSize > m_lLimit) {
			Iterator<Entry<String, Bitmap>> iterator = m_mapCache.entrySet().iterator();
			while(iterator.hasNext()) {
				Entry<String, Bitmap>	entry = iterator.next();
				m_lSize -= GetSizeInBytes(entry.getValue());
				entry.getValue().recycle();
				m_mapCache.remove(entry.getKey());
//				iterator.remove();
				if(m_lSize <= m_lLimit)
					break;
			}
		}
	}

	//------------------------------------------------------------------------------
	public void Clear() {
		m_lSize = 0;
		Iterator<Entry<String, Bitmap>> iterator = m_mapCache.entrySet().iterator();
		while(iterator.hasNext()) {
			Entry<String, Bitmap>	entry = iterator.next();
			try { entry.getValue().recycle(); } catch (Exception e1) {}
			try { m_mapCache.remove(entry.getKey()); } catch (Exception e2) {}
//				iterator.remove();
		}
		try { m_mapCache.clear(); } catch(Exception e3) {}
		System.gc();
	}

	//------------------------------------------------------------------------------
	long GetSizeInBytes(Bitmap a_bitmap) {
		if (a_bitmap == null)
			return 0;
		return a_bitmap.getRowBytes() * a_bitmap.getHeight();
	}
}
//==============================================================================
