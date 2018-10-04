package androidcustom.radiostation.global;

import java.io.InputStream;
import java.io.OutputStream;

//==============================================================================
public class UtilStream {

	//------------------------------------------------------------------------------
	public static void CopyStream(InputStream a_streamInput, OutputStream a_streamOutput)
	{
		final int iSizeBuffer = 1024;
		try {
			byte[] arrByte = new byte[iSizeBuffer];
			for(;;) {
				int iCount = a_streamInput.read(arrByte, 0, iSizeBuffer);
				if (iCount == -1)
					break;
				a_streamOutput.write(arrByte, 0, iCount);
			}
		} catch (Exception ex) {}
	}
}
//==============================================================================
