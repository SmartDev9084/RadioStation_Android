package androidcustom.radiostation.global;

import android.content.Context;
import android.net.wifi.WifiManager;

//==============================================================================
public class UtilDevice {

	//------------------------------------------------------------------------------
	public static String GetMacAddress(Context context) {
		WifiManager		wifiManager = (WifiManager)context.getSystemService(Context.WIFI_SERVICE);
		String			strMacAddress = wifiManager.getConnectionInfo().getMacAddress();
		if (strMacAddress == null) {
			strMacAddress = "Device don't have mac address or Wi-Fi is disabled";
		}
		return strMacAddress;
	}

}

//==============================================================================
