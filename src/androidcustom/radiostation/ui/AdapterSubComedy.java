package androidcustom.radiostation.ui;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import androidcustom.radiostation.R;
import androidcustom.radiostation.loader.LoaderImage;

//==============================================================================
public class AdapterSubComedy extends AdapterBaseList {

	private	final	String		URL_IMAGE_PREFIX = "http://i4.ytimg.com/vi/";

	//------------------------------------------------------------------------------
	public AdapterSubComedy(Context a_context, ListView a_listView) {
		super(a_context, a_listView);
	}

	//------------------------------------------------------------------------------
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		convertView = GetInflateView(position, convertView, parent, R.layout.listitem_subcomedy);

		ImageView		imgviewItem;
		ImageView		imgviewItemBack;
		TextView		txtviewItem;
		TextView		txtviewItemSubText;
		TextView		txtviewItemDuration;
		String			strId;
		String			strUrlImage;

		imgviewItem			= (ImageView)convertView.findViewById(R.id.ID_IMGVIEW_ITEM_SUBCOMEDY);
		imgviewItemBack		= (ImageView)convertView.findViewById(R.id.ID_IMGVIEW_ITEM_SUBCOMEDY_BACK);
		txtviewItem			= (TextView)convertView.findViewById(R.id.ID_TXTVIEW_ITEM_SUBCOMEDY);
		txtviewItemSubText	= (TextView)convertView.findViewById(R.id.ID_TXTVIEW_ITEM_SUBCOMEDY_SUBTEXT);
		txtviewItemDuration	= (TextView)convertView.findViewById(R.id.ID_TXTVIEW_ITEM_SUBCOMEDY_DURATION);

		imgviewItem.setImageResource(R.drawable.img_subitem_video);
	// {{ imgviewItemBack
		if (	(position >= m_listView.getFirstVisiblePosition())
			&&	(position <= (m_listView.getLastVisiblePosition() + 1))) {
			strId		= m_arrMultimediaInfo.get(m_arrItemIndex.get(position)).GetId();
			m_arrMultimediaInfo.get(m_arrItemIndex.get(position)).SetPath1("http://www.youtube.com/watch?v=" + strId);
			strUrlImage	= URL_IMAGE_PREFIX + (strId) + "/default.jpg";

			if (LoaderImage.GetInstance() != null)
				LoaderImage.GetInstance().DisplayImage(imgviewItemBack, strUrlImage);
		}
	// }} imgviewItemBack
		txtviewItem.setText(m_arrMultimediaInfo.get(m_arrItemIndex.get(position)).GetTitle());
		txtviewItemSubText.setText(m_arrMultimediaInfo.get(m_arrItemIndex.get(position)).GetArtist());
		txtviewItemDuration.setText(m_arrMultimediaInfo.get(m_arrItemIndex.get(position)).GetDuration());
		return convertView;
	}

	//------------------------------------------------------------------------------
}

//==============================================================================
