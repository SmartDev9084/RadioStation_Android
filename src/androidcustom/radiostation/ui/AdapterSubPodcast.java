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
public class AdapterSubPodcast extends AdapterBaseList {

	//------------------------------------------------------------------------------
	public AdapterSubPodcast(Context a_context, ListView a_listView) {
		super(a_context, a_listView);
	}

	//------------------------------------------------------------------------------
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		convertView = GetInflateView(position, convertView, parent, R.layout.listitem_subpodcast);

		ImageView		imgViewItem;
		ImageView		imgViewItemBack;
		TextView		txtViewItem;
		TextView		txtViewItemSubText;

		imgViewItem			= (ImageView)convertView.findViewById(R.id.ID_IMGVIEW_ITEM_SUBPODCAST);
		imgViewItemBack		= (ImageView)convertView.findViewById(R.id.ID_IMGVIEW_ITEM_SUBPODCAST_BACK);
		txtViewItem			= (TextView)convertView.findViewById(R.id.ID_TXTVIEW_ITEM_SUBPODCAST);
		txtViewItemSubText	= (TextView)convertView.findViewById(R.id.ID_TXTVIEW_ITEM_SUBPODCAST_SUBTEXT);

		imgViewItem.setImageResource(R.drawable.img_subitem_song);
	// {{ imgviewItemBack
		if (	(position >= m_listView.getFirstVisiblePosition())
			&&	(position <= (m_listView.getLastVisiblePosition() + 1))) {
			if (LoaderImage.GetInstance() != null)
				LoaderImage.GetInstance().DisplayImage(
					imgViewItemBack,
					m_arrMultimediaInfo.get(m_arrItemIndex.get(position)).GetPoster()
				);
		}
	// }} imgviewItemBack
		txtViewItem.setText(m_arrMultimediaInfo.get(m_arrItemIndex.get(position)).GetTitle());
		txtViewItemSubText.setText(String.valueOf(m_arrMultimediaInfo.get(m_arrItemIndex.get(position)).GetLikeCount()));
		return convertView;
	}

	//------------------------------------------------------------------------------
}

//==============================================================================
