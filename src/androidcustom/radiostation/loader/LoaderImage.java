package androidcustom.radiostation.loader;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.ImageView;
import androidcustom.radiostation.R;
import androidcustom.radiostation.cache.CacheFile;
import androidcustom.radiostation.cache.CacheMemory;
import androidcustom.radiostation.global.UtilStream;

//==============================================================================
public class LoaderImage {

	private	static	LoaderImage			m_instance;

	private static CacheMemory			m_cacheMemory;
	private static CacheFile			m_cacheFile;
	private static ExecutorService		m_executorService;
	private	static Map<ImageView, String> m_mapImageViews = Collections.synchronizedMap(new WeakHashMap<ImageView, String>());

//{{============================================================================
	class PhotoDisplayer implements Runnable {
		ImageView		m_imageView;
		String			m_strUrl;
		
		PhotoDisplayer(ImageView a_imageView, String a_strUrl) {
			m_imageView	= a_imageView;
			m_strUrl	= a_strUrl;
		}
		
		@Override
		public void run() {
			Bitmap				bitmap = null;
			BitmapDisplayer		bitmapDisplayer;
			Activity			activity;

			try {
				bitmap = GetBitmap(m_strUrl);
				m_cacheMemory.Put(m_strUrl, bitmap);
				bitmapDisplayer = new BitmapDisplayer(m_imageView, bitmap);
				activity		= (Activity)m_imageView.getContext();
				activity.runOnUiThread(bitmapDisplayer);
			} catch (Exception e) { e.printStackTrace(); }
		}
	}
//}}============================================================================

//{{============================================================================
	class BitmapDisplayer implements Runnable {
		ImageView		m_imageView;
		Bitmap			m_bitmap;

		//------------------------------------------------------------------------------
		public BitmapDisplayer(ImageView a_imageView, Bitmap a_bitmap) {
			m_imageView		= a_imageView;
			m_bitmap		= a_bitmap;
		}

		//------------------------------------------------------------------------------
		@Override
		public void run() {
			try {
				if (m_bitmap == null)
					return;
				m_imageView.setImageBitmap(m_bitmap);
			} catch (Exception e) {}
		}
	}
//}}============================================================================

	//------------------------------------------------------------------------------
	public LoaderImage(Context a_context) {
		m_cacheMemory	= new CacheMemory();
		m_cacheFile		= new CacheFile(a_context);
		m_executorService = Executors.newFixedThreadPool(1);
	}

	//------------------------------------------------------------------------------
	public	static	LoaderImage GetInstance() {
		return m_instance;
	}

	//------------------------------------------------------------------------------
	public static void Create(Context a_context) {
		if (m_instance == null)
			m_instance = new LoaderImage(a_context);
	}

	//------------------------------------------------------------------------------
	public void DisplayImage(ImageView a_imageView, String a_strUrl) {
		try {
			m_mapImageViews.put(a_imageView, a_strUrl);
			Bitmap		bitmap = m_cacheMemory.Get(a_strUrl);
			if (bitmap != null)
				a_imageView.setImageBitmap(bitmap);
			else {
				a_imageView.setImageResource(R.drawable.img_empty);
				QueueDisplayer(a_imageView, a_strUrl);
			}
		} catch (Exception e) { m_mapImageViews.remove(a_imageView); }
	}

	//------------------------------------------------------------------------------
	private void QueueDisplayer(ImageView a_imageView, String a_strUrl) {
		if (m_executorService.isShutdown())
			m_executorService = Executors.newFixedThreadPool(1);
		m_executorService.submit(new PhotoDisplayer(a_imageView, a_strUrl));
	}

	//------------------------------------------------------------------------------
	private Bitmap GetBitmap(String a_strUrl) {
		File	file;
		Bitmap	bitmap	=  null;

		try { file	= m_cacheFile.GetFile(a_strUrl); } catch (Exception e) { return null; }

		if (file == null)
			return null;

		bitmap = DecodeFile(file);				// from SD cache
		if (bitmap != null)
			return bitmap;

		// from web
		try {
			URL		urlImage = new URL(a_strUrl);

			HttpURLConnection conn = (HttpURLConnection)urlImage.openConnection();
			conn.setConnectTimeout(30000);
			conn.setReadTimeout(30000);
			conn.setInstanceFollowRedirects(true);
			InputStream		is = conn.getInputStream();
			OutputStream	os = new FileOutputStream(file);
			UtilStream.CopyStream(is, os);
			os.close();
			bitmap = DecodeFile(file);
			return bitmap;
		} catch (Exception e) {}
		return null;
	}

	//------------------------------------------------------------------------------
	private Bitmap DecodeFile(File a_file) {
		try {
			// decode image size
			BitmapFactory.Options	option1 = new BitmapFactory.Options();
			option1.inJustDecodeBounds = true;
			FileInputStream			stream1 = new FileInputStream(a_file);
			BitmapFactory.decodeStream(stream1, null, option1);

			final int REQUIRED_SIZE = 1024;		// YINGZHI 141220, Prev 70
			int		iWidth	= option1.outWidth;
			int		iHeight	= option1.outHeight;
			int		iScale	= 1;
			while (true) {
				if ((iWidth < REQUIRED_SIZE) || (iHeight < REQUIRED_SIZE))
					break;
				iWidth	/= 2;
				iHeight	/= 2;
				iScale	*= 2;
			}

			BitmapFactory.Options	option2 = new BitmapFactory.Options();
			option2.inSampleSize = iScale;
			FileInputStream			stream2	= new FileInputStream(a_file);
			Bitmap					bitmap2	= BitmapFactory.decodeStream(stream2, null, option2);
			stream2.close();
			return bitmap2;
		}
		catch (OutOfMemoryError e1) {}
		catch (FileNotFoundException e2) {}
		catch (IOException e3) {}

		return null;
	}

	//------------------------------------------------------------------------------
	private boolean IsImageViewReused(ImageView a_imageView, String a_strUrl) {
		String strTag = m_mapImageViews.get(a_imageView);
		if (strTag != null && strTag.equals(a_strUrl))
			return true;
		return false;
	}

	//------------------------------------------------------------------------------
	public void ClearCache() {
		try {
			m_cacheMemory.Clear();
			m_cacheFile.Clear();
			m_mapImageViews.clear();
			m_executorService.shutdownNow();
		} catch (Exception e) {}
		System.gc();
	}

}
//==============================================================================
