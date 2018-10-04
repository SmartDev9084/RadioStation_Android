package androidcustom.radiostation.global;

import android.content.Context;
import android.graphics.Point;
import android.util.DisplayMetrics;
import android.view.WindowManager;

//==============================================================================
public class UtilDisplay {

	//------------------------------------------------------------------------------
	public static Point GetScreenSize(Context a_context) {
		Point size = new Point();
		
		WindowManager		windowManager = (WindowManager)a_context.getSystemService(Context.WINDOW_SERVICE);
		DisplayMetrics		displayMetrics= new DisplayMetrics();

		windowManager.getDefaultDisplay().getMetrics(displayMetrics);

		size.x = displayMetrics.widthPixels;
		size.y = displayMetrics.heightPixels;

		return size;
	}
}

//==============================================================================
