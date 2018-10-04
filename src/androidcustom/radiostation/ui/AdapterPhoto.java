package androidcustom.radiostation.ui;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import androidcustom.radiostation.loader.LoaderImage;

//==============================================================================
public class AdapterPhoto extends AdapterBase {

	private		GridView		m_gridView;

	//------------------------------------------------------------------------------
	public AdapterPhoto(Context a_context, GridView a_gridView) {
		super(a_context);
		m_gridView = a_gridView;
	}

	//------------------------------------------------------------------------------
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		int			iCellSize;
		GridView.LayoutParams		gridLayoutParams;

		iCellSize = m_gridView.getMeasuredWidth() / m_gridView.getNumColumns() - 10;

		if (position >= m_arrItemIndex.size())
			return null;
		if (convertView == null) {
			convertView = new ImageView(m_context);
			gridLayoutParams = new GridView.LayoutParams(iCellSize, iCellSize);
			gridLayoutParams.width		= iCellSize;
			gridLayoutParams.height		= iCellSize;
			convertView.setLayoutParams(gridLayoutParams);
			((ImageView)convertView).setScaleType(ScaleType.FIT_XY);
		}
	// {{ imgviewItemBack
		if (	(position >= m_gridView.getFirstVisiblePosition())
			&&	(position <= (m_gridView.getLastVisiblePosition() + 1))) {
			if (LoaderImage.GetInstance() != null)
				LoaderImage.GetInstance().DisplayImage(
					(ImageView)convertView,
					m_arrMultimediaInfo.get(m_arrItemIndex.get(position)).GetLink()
				);
		}
	// }} imgviewItemBack


		return convertView;
	}

	//------------------------------------------------------------------------------
}

//==============================================================================
