package androidcustom.radiostation.ui;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import androidcustom.radiostation.R;
import androidcustom.radiostation.global.Const;
import androidcustom.radiostation.global.EnvVariable;

//==============================================================================
public class AdapterMain extends BaseAdapter {
	private	LayoutInflater			m_inflater;
	private	Context					m_context;
	private	ArrayList<ItemDataMain> m_arrItemData;
	private	boolean					m_bVisibleSocialItemBefore;
	private	boolean					m_bVisibleSocialItem;

	//------------------------------------------------------------------------------
	public AdapterMain(Context a_context, ArrayList<ItemDataMain> a_arrItemData) {
		super();
		m_context = a_context;
		m_arrItemData = a_arrItemData;
		m_inflater = (LayoutInflater)m_context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		m_bVisibleSocialItemBefore	= false;
		m_bVisibleSocialItem		= false;
	}

	//------------------------------------------------------------------------------
	@Override
	public int getCount() {
		return m_arrItemData.size();
	}

	//------------------------------------------------------------------------------
	@Override
	public Object getItem(int position) {
		return m_arrItemData.get(position).GetText();
	}

	//------------------------------------------------------------------------------
	@Override
	public long getItemId(int position) {
		return position;
	}

	//------------------------------------------------------------------------------
	public void SetVisibleSocialItem(boolean a_bVisibleSocialItem) {
		m_bVisibleSocialItemBefore	= m_bVisibleSocialItem;
		m_bVisibleSocialItem		= a_bVisibleSocialItem;
	}

	//------------------------------------------------------------------------------
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ImageView		imgViewBack;
		ImageView		imgViewItem;
		TextView		txtViewItem;

		if ((position == Const.ITEM_FACEBOOK) || (position == Const.ITEM_TWITTER)) {
			if (m_bVisibleSocialItem == true) {
				if ((convertView == null) || (m_bVisibleSocialItem != m_bVisibleSocialItemBefore))
					convertView = m_inflater.inflate(R.layout.listitem_main, parent, false);
			}
			else {
				if ((convertView == null) || (m_bVisibleSocialItem != m_bVisibleSocialItemBefore))
				convertView = m_inflater.inflate(R.layout.listitem_main_hide, parent, false);
			}
		}
		else {
			if (convertView == null)
				convertView = m_inflater.inflate(R.layout.listitem_main, parent, false);
		}
		imgViewBack = (ImageView)convertView.findViewById(R.id.ID_IMGVIEW_ITEM_MAIN_BACK);
		imgViewItem = (ImageView)convertView.findViewById(R.id.ID_IMGVIEW_ITEM_MAIN);
		txtViewItem = (TextView)convertView.findViewById(R.id.ID_TXTVIEW_ITEM_MAIN);
		
		if (position == EnvVariable.CurrentMainItem)
			imgViewBack.setImageResource(R.drawable.img_itm_main_selected);
		else {
			switch (position) {
			case Const.ITEM_HOME:
			case Const.ITEM_PLAYLIST:
			case Const.ITEM_VIDEO:
			case Const.ITEM_NOLLYWOOD:
			case Const.ITEM_SOCIAL:
			case Const.ITEM_FACEBOOK:
			case Const.ITEM_TWITTER:
			case Const.ITEM_EXIT:		imgViewBack.setImageResource(R.drawable.img_itm_main_odd);		break;
			default:					imgViewBack.setImageResource(R.drawable.img_itm_main_even);		break;
			}
		}
		imgViewItem.setImageDrawable(m_arrItemData.get(position).GetImage());
		txtViewItem.setText(m_arrItemData.get(position).GetText());
		
		return convertView;
	}

}

//==============================================================================
