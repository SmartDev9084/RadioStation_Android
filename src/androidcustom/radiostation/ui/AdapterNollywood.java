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

//==============================================================================
public class AdapterNollywood extends BaseAdapter {

	private LayoutInflater				m_inflater;
	private Context						m_context;
	private ArrayList<ItemDataNollywood>	m_arrItemData;

	//------------------------------------------------------------------------------
	public AdapterNollywood(Context a_context) {
		super();
		m_context = a_context;
		m_arrItemData = new ArrayList<ItemDataNollywood>();
		m_inflater = (LayoutInflater)m_context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	//------------------------------------------------------------------------------
	@Override
	public int getCount() {
		return m_arrItemData.size();
	}

	//------------------------------------------------------------------------------
	@Override
	public Object getItem(int position) {
		return m_arrItemData.get(position).GetName();
	}

	//------------------------------------------------------------------------------
	@Override
	public long getItemId(int position) {
		return position;
	}

	//------------------------------------------------------------------------------
	public void AddItemData(ItemDataNollywood a_itemData) {
		m_arrItemData.add(a_itemData);
	}
	
	//------------------------------------------------------------------------------
	public ItemDataNollywood GetItemData(int a_iIndex) {
		if (a_iIndex < m_arrItemData.size())
			return m_arrItemData.get(a_iIndex);
		return null;
	}

	//------------------------------------------------------------------------------
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ImageView		imgviewItem;
		ImageView		imgviewItemShowList;
		TextView		txtviewItem;

		if (convertView == null) {
			convertView = m_inflater.inflate(R.layout.listitem_nollywood, parent, false);
		}
		imgviewItem				= (ImageView)convertView.findViewById(R.id.ID_IMGVIEW_ITEM_NOLLYWOOD);
		imgviewItemShowList		= (ImageView)convertView.findViewById(R.id.ID_IMGVIEW_ITEM_NOLLYWOOD_SHOWLIST);
		txtviewItem				= (TextView)convertView.findViewById(R.id.ID_TXTVIEW_ITEM_NOLLYWOOD);

		imgviewItem.setImageResource(R.drawable.img_item_nollywood);
		imgviewItemShowList.setImageResource(R.drawable.img_btn_showlist);
		txtviewItem.setText(m_arrItemData.get(position).GetName());
		return convertView;
	}

	//------------------------------------------------------------------------------
}

//==============================================================================
