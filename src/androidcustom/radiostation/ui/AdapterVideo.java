package androidcustom.radiostation.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import androidcustom.radiostation.R;
import androidcustom.radiostation.loader.LoaderImage;

//==============================================================================
public class AdapterVideo extends AdapterBase {

	private		final String		URL_IMAGE_PREFIX = "http://i4.ytimg.com/vi/";
	private		ListView			m_listView;

	//------------------------------------------------------------------------------
	public AdapterVideo(Context a_context, ListView a_listView) {
		super(a_context);
		m_listView = a_listView;
	}

	//------------------------------------------------------------------------------
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater		inflater;
		inflater = (LayoutInflater)m_context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		if (position >= m_arrItemIndex.size())
			return null;

		if (convertView == null)
			convertView = inflater.inflate(R.layout.listitem_video, parent, false);

		ImageView		imgViewItem;
		ImageView		imgViewItemBack;
		TextView		txtViewItem;
		TextView		txtViewItemSubText;
		TextView		txtViewItemDuration;
		String			strPath;
		String			strId;
		String			strUrlImage;

		imgViewItem			= (ImageView)convertView.findViewById(R.id.ID_IMGVIEW_ITEM_VIDEO);
		imgViewItemBack		= (ImageView)convertView.findViewById(R.id.ID_IMGVIEW_ITEM_VIDEO_BACK);
		txtViewItem			= (TextView)convertView.findViewById(R.id.ID_TXTVIEW_ITEM_VIDEO);
		txtViewItemSubText	= (TextView)convertView.findViewById(R.id.ID_TXTVIEW_ITEM_VIDEO_SUBTEXT);
		txtViewItemDuration	= (TextView)convertView.findViewById(R.id.ID_TXTVIEW_ITEM_VIDEO_DURATION);

		imgViewItem.setImageResource(R.drawable.img_subitem_video);
	// {{ imgviewItemBack
		if (	(position >= m_listView.getFirstVisiblePosition())
			&&	(position <= (m_listView.getLastVisiblePosition() + 1))) {
			try {
				strPath		= m_arrMultimediaInfo.get(m_arrItemIndex.get(position)).GetLink();
				strId		= strPath.substring(16);
				m_arrMultimediaInfo.get(m_arrItemIndex.get(position)).SetPath1("http://www.youtube.com/watch?v=" + strId);
				strUrlImage	= URL_IMAGE_PREFIX + (strId) + "/default.jpg";
	
				if (LoaderImage.GetInstance() != null)
					LoaderImage.GetInstance().DisplayImage(imgViewItemBack, strUrlImage);
			} catch (Exception e) {}
		}
	// }} imgviewItemBack
		txtViewItem.setText(m_arrMultimediaInfo.get(m_arrItemIndex.get(position)).GetTitle());
		txtViewItemSubText.setText(m_arrMultimediaInfo.get(m_arrItemIndex.get(position)).GetArtist());
		txtViewItemDuration.setText(m_arrMultimediaInfo.get(m_arrItemIndex.get(position)).GetDuration());
		return convertView;
	}

	//------------------------------------------------------------------------------
}

//==============================================================================
