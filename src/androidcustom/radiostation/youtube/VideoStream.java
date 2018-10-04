package androidcustom.radiostation.youtube;

import java.util.HashMap;
import java.util.Map;

//==============================================================================
/**
 * Represents a video stream
 */
public class VideoStream {

	protected String m_strUrl;

	//------------------------------------------------------------------------------
	/**
	 * Construct a video stream from one of the strings obtained
	 * from the "url_encoded_fmt_stream_map" parameter if the video_info
	 *
	 * @param a_strStream - one of the strings from "url_encoded_fmt_stream_map"
	 */
	public VideoStream(String a_strStream) {
		String[] arrStrArg = a_strStream.split("&");
		Map<String, String> mapArg = new HashMap<String, String>();
		for (int i = 0; i < arrStrArg.length; i++) {
			String[] arrStrArgVal = arrStrArg[i].split("=");
			if (arrStrArgVal != null) {
				if (arrStrArgVal.length >= 2)
					mapArg.put(arrStrArgVal[0], arrStrArgVal[1]);
			}
		}
		m_strUrl = mapArg.get("url") + "&signature=" + mapArg.get("sig");
	}

	//------------------------------------------------------------------------------
	public String GetUrl() {
		return m_strUrl;
	}
}

//==============================================================================
