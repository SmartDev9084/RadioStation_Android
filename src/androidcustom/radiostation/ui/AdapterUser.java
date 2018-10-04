package androidcustom.radiostation.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import androidcustom.radiostation.R;
import androidcustom.radiostation.global.Const;
import androidcustom.radiostation.loader.LoaderImage;

//==============================================================================
public class AdapterUser extends AdapterBase {

	private		LayoutInflater		m_inflater;
	private		Context				m_context;
	private		ListView			m_listView;

	private		final String		URL_IMAGE_PREFIX = "http://i4.ytimg.com/vi/";

	//------------------------------------------------------------------------------
	public AdapterUser(Context a_context, ListView a_listView) {
		super(a_context);
		m_context = a_context;
		m_listView = a_listView;
		m_inflater = (LayoutInflater)m_context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	//------------------------------------------------------------------------------
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ImageView		imgViewItem;
		ImageView		imgViewItemBack;
		TextView		txtViewItem;
		TextView		txtViewItemSubText;
		String			strPath		= null;
		String			strId		= null;
		String			strUrlImage	= null;

		if (position >= m_arrItemIndex.size())
			return null;

		if (convertView == null)
			convertView = m_inflater.inflate(R.layout.listitem_user, parent, false);

		imgViewItem			= (ImageView)convertView.findViewById(R.id.ID_IMGVIEW_ITEM_USER);
		imgViewItemBack		= (ImageView)convertView.findViewById(R.id.ID_IMGVIEW_ITEM_USER_BACK);
		txtViewItem			= (TextView)convertView.findViewById(R.id.ID_TXTVIEW_ITEM_USER);
		txtViewItemSubText	= (TextView)convertView.findViewById(R.id.ID_TXTVIEW_ITEM_USER_SUBTEXT);

		imgViewItem.setImageResource(R.drawable.img_subitem_song);
	// {{ imgViewItemBack
		if (	(position >= m_listView.getFirstVisiblePosition())
				&&	(position <= (m_listView.getLastVisiblePosition() + 1))) {
			switch (m_arrMultimediaInfo.get(m_arrItemIndex.get(position)).GetType()) {
				case Const.TYPE_HOME_TREND:
				case Const.TYPE_HOME_TOP10:
				case Const.TYPE_HOME_STAFF:
				case Const.TYPE_HOME_CLASS:
				case Const.TYPE_PLAYLIST_GBEDU:
				case Const.TYPE_PLAYLIST_LOVE:
				case Const.TYPE_PLAYLIST_AFRO:
				case Const.TYPE_PLAYLIST_WORKOUT:
				case Const.TYPE_PLAYLIST_CHURCH:
				case Const.TYPE_PLAYLIST_OLD:
				case Const.TYPE_PLAYLIST_RAP:
				case Const.TYPE_PODCAST_1:
				case Const.TYPE_PODCAST_2:
				case Const.TYPE_PODCAST_3:
				case Const.TYPE_PODCAST_4:
				case Const.TYPE_PODCAST_5:
					strUrlImage	= m_arrMultimediaInfo.get(m_arrItemIndex.get(position)).GetPoster();
					break;
				case Const.TYPE_VIDEO:
				case Const.TYPE_SUBCOMEDY:
				case Const.TYPE_SUBNOLLYWOOD:
					strPath		= m_arrMultimediaInfo.get(m_arrItemIndex.get(position)).GetPath1();
					strId		= strPath.substring(31);
					strUrlImage	= URL_IMAGE_PREFIX + (strId) + "/default.jpg";
					break;
				default:
					break;
			}

			if ((strUrlImage != null) && (LoaderImage.GetInstance() != null))
				LoaderImage.GetInstance().DisplayImage(imgViewItemBack, strUrlImage);
		}
	// }} imgViewItemBack
		txtViewItem.setText(m_arrMultimediaInfo.get(m_arrItemIndex.get(position)).GetTitle());
		txtViewItemSubText.setText(String.valueOf(m_arrMultimediaInfo.get(m_arrItemIndex.get(position)).GetArtist()));

		return convertView;
	}

	//------------------------------------------------------------------------------
}

//==============================================================================
