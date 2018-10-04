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
public class AdapterHome extends AdapterBaseList {

	//------------------------------------------------------------------------------
	public AdapterHome(Context a_context, ListView a_listView) {
		super(a_context, a_listView);
	}

	//------------------------------------------------------------------------------
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		convertView = GetInflateView(position, convertView, parent, R.layout.listitem_home);

		ImageView		imgViewItem;
		ImageView		imgViewItemBack;
		ImageView		imgViewItemMark;
		TextView		txtViewItem;
		TextView		txtViewItemDegree;

		imgViewItem			= (ImageView)convertView.findViewById(R.id.ID_IMGVIEW_ITEM_HOME);
		imgViewItemBack		= (ImageView)convertView.findViewById(R.id.ID_IMGVIEW_ITEM_HOME_BACK);
		imgViewItemMark		= (ImageView)convertView.findViewById(R.id.ID_IMGVIEW_ITEM_HOME_MARK);
		txtViewItem			= (TextView)convertView.findViewById(R.id.ID_TXTVIEW_ITEM_HOME);
		txtViewItemDegree	= (TextView)convertView.findViewById(R.id.ID_TXTVIEW_ITEM_HOME_DEGREE);

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
		imgViewItemMark.setImageResource(R.drawable.img_item_home);
		txtViewItem.setText(m_arrMultimediaInfo.get(m_arrItemIndex.get(position)).GetTitle());
		txtViewItemDegree.setText(String.valueOf(m_arrMultimediaInfo.get(m_arrItemIndex.get(position)).GetLikeCount()));

		return convertView;
	}

	//------------------------------------------------------------------------------
}

//==============================================================================
