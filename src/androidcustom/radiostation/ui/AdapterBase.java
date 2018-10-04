package androidcustom.radiostation.ui;

import java.util.ArrayList;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import androidcustom.radiostation.multimedia.MultimediaInfo;

//==============================================================================
public class AdapterBase extends BaseAdapter {

	protected	Context						m_context;
	private		String						m_strFindText;
	protected	ArrayList<Integer>			m_arrItemIndex;
	protected	ArrayList<MultimediaInfo>	m_arrMultimediaInfo;
	protected	int							m_iPosCurrAnim;

	//------------------------------------------------------------------------------
	public AdapterBase(Context a_context) {
		super();
		m_context = a_context;
		m_strFindText		= new String("");
		m_arrItemIndex		= new ArrayList<Integer>();
		m_arrMultimediaInfo	= new ArrayList<MultimediaInfo>();
		m_iPosCurrAnim		= -1;

	}

	//------------------------------------------------------------------------------
	@Override
	public int getCount() {
		return m_arrItemIndex.size();
	}

	//------------------------------------------------------------------------------
	@Override
	public Object getItem(int position) {
		return m_arrMultimediaInfo.get(m_arrItemIndex.get(position));
	}

	//------------------------------------------------------------------------------
	@Override
	public long getItemId(int position) {
		return m_arrItemIndex.get(position);
	}

	//------------------------------------------------------------------------------
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		return convertView;
	}

	//------------------------------------------------------------------------------
	public void SetFindText(String a_strFindText) {
		int			iIndex;

		m_strFindText = a_strFindText;

		m_arrItemIndex.clear();
		for (iIndex = 0; iIndex < m_arrMultimediaInfo.size(); iIndex++) {
			if (m_arrMultimediaInfo.get(iIndex).ContainText(m_strFindText))
				m_arrItemIndex.add(iIndex);
		}
	}

	//------------------------------------------------------------------------------
	public void AddMultimediaInfo(MultimediaInfo a_multimediaInfo) {
		m_arrMultimediaInfo.add(a_multimediaInfo);
		if (m_strFindText.isEmpty())
			m_arrItemIndex.add(m_arrMultimediaInfo.size() - 1);
		else if (a_multimediaInfo.ContainText(m_strFindText))
			m_arrItemIndex.add(m_arrMultimediaInfo.size() - 1);
	}

	//------------------------------------------------------------------------------
	public MultimediaInfo GetMultimediaInfo(int a_iPosition) {
		if (a_iPosition < m_arrItemIndex.size())
			return m_arrMultimediaInfo.get(m_arrItemIndex.get(a_iPosition));
		return null;
	}

	//------------------------------------------------------------------------------
	public void SetMultimediaInfoArray(ArrayList<MultimediaInfo> a_arrMultimediaInfo) {
		int				iIndex;
		MultimediaInfo	multimediaInfo;

		if (a_arrMultimediaInfo == null)
			return;

		m_arrMultimediaInfo.clear();
		m_arrItemIndex.clear();

		m_arrMultimediaInfo = a_arrMultimediaInfo;
		
		for (iIndex = 0; iIndex < m_arrMultimediaInfo.size(); iIndex++) {
			multimediaInfo = m_arrMultimediaInfo.get(iIndex);
			if (m_strFindText.isEmpty())
				m_arrItemIndex.add(iIndex);
			else if (multimediaInfo.ContainText(m_strFindText))
				m_arrItemIndex.add(iIndex);
		}
	}

	//------------------------------------------------------------------------------
	public ArrayList<MultimediaInfo> GetMultimediaInfoArray() {
		return m_arrMultimediaInfo;
	}

	//------------------------------------------------------------------------------
	public ArrayList<Integer> GetItemIndexArray() {
		return m_arrItemIndex;
	}

	//------------------------------------------------------------------------------
}

//==============================================================================
