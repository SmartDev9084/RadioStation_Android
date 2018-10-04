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
public class AdapterPlaylist extends BaseAdapter {

	LayoutInflater			m_inflater;
	Context					m_context;
	ArrayList<ItemDataPlaylist> m_arrItemData;

	//------------------------------------------------------------------------------
	public AdapterPlaylist(Context a_context) {
		super();
		ItemDataPlaylist itemData;
		m_context = a_context;
		m_arrItemData = new ArrayList<ItemDataPlaylist>();
		m_inflater = (LayoutInflater)m_context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		
		itemData = new ItemDataPlaylist("Gbedu",	0);		m_arrItemData.add(itemData);
		itemData = new ItemDataPlaylist("Love",		0);		m_arrItemData.add(itemData);
		itemData = new ItemDataPlaylist("Afro",		0);		m_arrItemData.add(itemData);
		itemData = new ItemDataPlaylist("Workout",	0);		m_arrItemData.add(itemData);
		itemData = new ItemDataPlaylist("Church",	0);		m_arrItemData.add(itemData);
		itemData = new ItemDataPlaylist("Old",		0);		m_arrItemData.add(itemData);
		itemData = new ItemDataPlaylist("Rap",		0);		m_arrItemData.add(itemData);

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
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ImageView		imgviewItem;
		ImageView		imgviewItemShowList;
		TextView		txtviewItem;
		TextView		txtviewItemCount;

		if (convertView == null) {
			convertView = m_inflater.inflate(R.layout.listitem_playlist, parent, false);
		}
		imgviewItem				= (ImageView)convertView.findViewById(R.id.ID_IMGVIEW_ITEM_PLAYLIST);
		imgviewItemShowList		= (ImageView)convertView.findViewById(R.id.ID_IMGVIEW_ITEM_PLAYLIST_SHOWLIST);
		txtviewItem				= (TextView)convertView.findViewById(R.id.ID_TXTVIEW_ITEM_PLAYLIST);
		txtviewItemCount		= (TextView)convertView.findViewById(R.id.ID_TXTVIEW_ITEM_PLAYLIST_COUNT);

		imgviewItem.setImageResource(R.drawable.img_item_playlist);
		imgviewItemShowList.setImageResource(R.drawable.img_btn_showlist);
		txtviewItem.setText(m_arrItemData.get(position).GetText());
		txtviewItemCount.setText(String.valueOf(m_arrItemData.get(position).GetCount()));
		return convertView;
	}

	//------------------------------------------------------------------------------
	public void SetCountInfo(int a_iIndex, int a_iCount) {
		if (a_iIndex < m_arrItemData.size())
			m_arrItemData.get(a_iIndex).SetCount(a_iCount);
	}

	//------------------------------------------------------------------------------
}

//==============================================================================
