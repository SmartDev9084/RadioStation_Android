package androidcustom.radiostation.global;


import com.un4seen.bass.BASS;
import android.graphics.Point;
import android.media.MediaPlayer;
import androidcustom.radiostation.ui.AdapterBase;

//==============================================================================
public class EnvVariable {
	public	static	int		CurrentMainItem;
	public	static	int		CurrentMainItemWithView;
	public	static	int		CurrentSubPlaylistType;
	public	static	int		CurrentSubPodcastType;
	public	static	Point	SizeDisplay;
	public	static	AdapterBase		CurrentAdapter;
	public	static	MediaPlayer		CurrentMediaPlayer;
	public	static	Thread			CurrentRadioThread;

	//------------------------------------------------------------------------------
	public EnvVariable() {
		EnvVariable.CurrentMediaPlayer = null;
		EnvVariable.CurrentRadioThread = null;
	}

	//------------------------------------------------------------------------------
	public static void KillCurrentSound() {
		if (EnvVariable.CurrentMediaPlayer != null) {
			try { EnvVariable.CurrentMediaPlayer.release(); } catch(Exception e) {}
			EnvVariable.CurrentMediaPlayer = null;
		}
		if (EnvVariable.CurrentRadioThread != null) {
			try { BASS.BASS_Free(); }							catch (Exception e) {}
			try { EnvVariable.CurrentRadioThread.stop(); }		catch (Exception e) {}
			try { EnvVariable.CurrentRadioThread = null; }		catch (Exception e) {}
		}
	}

	//------------------------------------------------------------------------------
}

//==============================================================================
