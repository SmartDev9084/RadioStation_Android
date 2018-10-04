package androidcustom.radiostation.global;

import java.io.File;

import android.os.Environment;

//==============================================================================
public class Const {

	//------------------------------------------------------------------------------
	public	static	final	int		DELAY_SPLASH			= 2000;
	public	static	final	int		DELAY_INVALIDATE		= 1000;
	public	static	final	int		DELAY_TASKGAP			= 2000;

	//------------------------------------------------------------------------------
	public	static	final	double	RATIO_WIDTH_MAINLIST	= 0.625;	// Matched with delta in animation_left.xml, animation.right.xml
	
	//------------------------------------------------------------------------------
	public	static	final	String	RESULT_OK					= "OK";
	public	static	final	int		RESULT_SUCCESS				= 1;
	public	static	final	int		RESULT_FAIL					= 0;

	public	static	final	int		RESULT_ERR_SQL				= 2;
	public	static	final	int		RESULT_ERR_INVALID_FIELD	= 3;
	public	static	final	int		RESULT_ERR_NODATA			= 4;

	//------------------------------------------------------------------------------
	public	static	final	int		INDEX_NONE					= -1;

	//------------------------------------------------------------------------------
	// For Intent
	public	static	final	int		REQUESTCODE_LOGIN_GOOGLE	= 1;
	public	static	final	int		REQUESTCODE_LOGIN_FACEBOOK	= 2;
	public	static	final	int		REQUESTCODE_LOGIN_TWITTER	= 3;

	//------------------------------------------------------------------------------
	// For Animation
	public	static	final	int		ANIMATION_NONE		= 0;
	public	static	final	int		ANIMATION_RIGHT		= 1;
	public	static	final	int		ANIMATION_LEFT		= 2;

	//------------------------------------------------------------------------------
	// For Video
	public	static	final	int		MAX_VIDEO_COUNT		= 25;
	public	static	final	int		MAX_AUDIO_COUNT		= 50;

	//------------------------------------------------------------------------------
	// Login with Facebook, Twitter, Google
	public	static	final	String	ID_USER_NONE			= "-1";
	public	static	final	int		TYPE_LOGIN_NORMAL		= 0;
	public	static	final	int		TYPE_LOGIN_FACEBOOK		= 1;
	public	static	final	int		TYPE_LOGIN_TWITTER		= 2;
	public	static	final	int		TYPE_LOGIN_GOOGLE		= 3;
	public	static	final	int		TYPE_LOGIN_GUEST		= 4;

	//------------------------------------------------------------------------------
	// Twitter
	public	static	final	String	TWITTER_CONSUMER_KEY		=		"QSYCQGI6rjjkaFFHgoApUYpof";
	public	static	final	String	TWITTER_CONSUMER_SECRET		=		"9q8lCg5hkSHhnV8Quq9HtHOyKSoPS0jxRpphQU8z3ZKo3X6vsF";
	public	static	final	String	TWITTER_CALLBACK_URL		=		"oauth://twitter_callback";

	//------------------------------------------------------------------------------
	// Twitter to Main
	public	static	final	String	KEY_TWITTER_CALLBACK_URL		= "URL_CALLBACK";
	public	static	final	String	KEY_TWITTER_AUTHENTICATION_URL	= "URL_AUTHENTICATION";

	// Login to Main Intent
	public	static	final	String	KEY_LOGINTYPE	= "TYPELOGIN";
	public	static	final	String	KEY_USERID		= "USERID";
	public	static	final	String	KEY_USERNAME	= "USERNAME";
	public	static	final	String	KEY_AVATAR		= "AVATAR";

	// Main to Play Intent
	public	static	final	String	KEY_INDEX		= "INDEX";

	// Main to SubComedy Intent
	public	static	final	String	KEY_TERM		= "TERM";

	// Photo to Play Intent
	public	static	final	String	KEY_TITLE		= "TITLE";
	
	//------------------------------------------------------------------------------
	// Main ListView Item Index
	public	static	final	int		ITEM_NONE		= -1;
	public	static	final	int		ITEM_USER		= 0;
	public	static	final	int		ITEM_HOME		= 1;
	public	static	final	int		ITEM_RADIO		= 2;
	public	static	final	int		ITEM_PLAYLIST	= 3;
	public	static	final	int		ITEM_PODCAST	= 4;
	public	static	final	int		ITEM_VIDEO		= 5;
	public	static	final	int		ITEM_COMEDY		= 6;
	public	static	final	int		ITEM_NOLLYWOOD	= 7;
	public	static	final	int		ITEM_PHOTO		= 8;
	public	static	final	int		ITEM_SOCIAL		= 9;
	public	static	final	int		ITEM_FACEBOOK	= 10;
	public	static	final	int		ITEM_TWITTER	= 11;
	public	static	final	int		ITEM_CHAT		= 12;
	public	static	final	int		ITEM_EXIT		= 13;

	//------------------------------------------------------------------------------
	// API TYPE
	public	static	final	int		TYPE_NONE					= 0;
	public	static	final	int		TYPE_USER					= 1;
	public	static	final	int		TYPE_HOME					= 2;
	public	static	final	int		TYPE_HOME_TREND				= 21;
	public	static	final	int		TYPE_HOME_TREND_IMAGE		= 211;
	public	static	final	int		TYPE_HOME_TREND_VIDEO		= 212;
	public	static	final	int		TYPE_HOME_TOP10				= 22;
	public	static	final	int		TYPE_HOME_TOP10_IMAGE		= 221;
	public	static	final	int		TYPE_HOME_TOP10_VIDEO		= 222;
	public	static	final	int		TYPE_HOME_STAFF				= 23;
	public	static	final	int		TYPE_HOME_STAFF_IMAGE		= 231;
	public	static	final	int		TYPE_HOME_STAFF_VIDEO		= 232;
	public	static	final	int		TYPE_HOME_CLASS				= 24;
	public	static	final	int		TYPE_HOME_CLASS_IMAGE		= 241;
	public	static	final	int		TYPE_HOME_CLASS_VIDEO		= 242;
	public	static	final	int		TYPE_PLAYLIST				= 3;
	public	static	final	int		TYPE_PLAYLIST_GBEDU			= 31;
	public	static	final	int		TYPE_PLAYLIST_GBEDU_IMAGE	= 311;
	public	static	final	int		TYPE_PLAYLIST_GBEDU_VIDEO	= 312;
	public	static	final	int		TYPE_PLAYLIST_LOVE			= 32;
	public	static	final	int		TYPE_PLAYLIST_LOVE_IMAGE	= 321;
	public	static	final	int		TYPE_PLAYLIST_LOVE_VIDEO	= 322;
	public	static	final	int		TYPE_PLAYLIST_AFRO			= 33;
	public	static	final	int		TYPE_PLAYLIST_AFRO_IMAGE	= 331;
	public	static	final	int		TYPE_PLAYLIST_AFRO_VIDEO	= 332;
	public	static	final	int		TYPE_PLAYLIST_WORKOUT		= 34;
	public	static	final	int		TYPE_PLAYLIST_WORKOUT_IMAGE	= 341;
	public	static	final	int		TYPE_PLAYLIST_WORKOUT_VIDEO	= 342;
	public	static	final	int		TYPE_PLAYLIST_CHURCH		= 35;
	public	static	final	int		TYPE_PLAYLIST_CHURCH_IMAGE	= 351;
	public	static	final	int		TYPE_PLAYLIST_CHURCH_VIDEO	= 352;
	public	static	final	int		TYPE_PLAYLIST_OLD			= 36;
	public	static	final	int		TYPE_PLAYLIST_OLD_IMAGE		= 361;
	public	static	final	int		TYPE_PLAYLIST_OLD_VIDEO		= 362;
	public	static	final	int		TYPE_PLAYLIST_RAP			= 37;
	public	static	final	int		TYPE_PLAYLIST_RAP_IMAGE		= 371;
	public	static	final	int		TYPE_PLAYLIST_RAP_VIDEO		= 372;
	public	static	final	int		TYPE_PODCAST				= 4;
	public	static	final	int		TYPE_PODCAST_1				= 41;
	public	static	final	int		TYPE_PODCAST_1_IMAGE		= 411;
	public	static	final	int		TYPE_PODCAST_1_VIDEO		= 412;
	public	static	final	int		TYPE_PODCAST_2				= 42;
	public	static	final	int		TYPE_PODCAST_2_IMAGE		= 421;
	public	static	final	int		TYPE_PODCAST_2_VIDEO		= 422;
	public	static	final	int		TYPE_PODCAST_3				= 43;
	public	static	final	int		TYPE_PODCAST_3_IMAGE		= 431;
	public	static	final	int		TYPE_PODCAST_3_VIDEO		= 432;
	public	static	final	int		TYPE_PODCAST_4				= 44;
	public	static	final	int		TYPE_PODCAST_4_IMAGE		= 441;
	public	static	final	int		TYPE_PODCAST_4_VIDEO		= 442;
	public	static	final	int		TYPE_PODCAST_5				= 45;
	public	static	final	int		TYPE_PODCAST_5_IMAGE		= 451;
	public	static	final	int		TYPE_PODCAST_5_VIDEO		= 452;
	public	static	final	int		TYPE_VIDEO					= 5;
	public	static	final	int		TYPE_COMEDY					= 6;
	public	static	final	int		TYPE_SUBCOMEDY				= 61;
	public	static	final	int		TYPE_NOLLYWOOD				= 7;
	public	static	final	int		TYPE_SUBNOLLYWOOD			= 71;
	public	static	final	int		TYPE_PHOTO					= 8;
	public	static	final	int		TYPE_LOGIN					= 9;
	public	static	final	int		TYPE_REGISTER				= 10;

	//------------------------------------------------------------------------------
	public	static	final	String	APP_DIR = Environment.getExternalStorageDirectory().getAbsolutePath()
											+ File.separator + "RadioStation";
	
	//------------------------------------------------------------------------------
	// String For Home
	public	static	final	String	STRING_TREND = "TREND";
	public	static	final	String	STRING_TOP10 = "TOP10";
	public	static	final	String	STRING_STAFF = "STAFF";
	public	static	final	String	STRING_CLASS = "CLASS";
	
	// String For Playlist
	public	static	final	String	STRING_GBEDU	= "GBEDU";
	public	static	final	String	STRING_LOVE		= "LOVE";
	public	static	final	String	STRING_AFRO		= "AFRO";
	public	static	final	String	STRING_WORKOUT	= "WORKOUT";
	public	static	final	String	STRING_CHURCH	= "CHURCH";
	public	static	final	String	STRING_OLD		= "OLD";
	public	static	final	String	STRING_RAP		= "RAP";

	// String For Podcast
	public	static	final	String	STRING_PODCAST_1	= "PODCAST1";
	public	static	final	String	STRING_PODCAST_2	= "PODCAST2";
	public	static	final	String	STRING_PODCAST_3	= "PODCAST3";
	public	static	final	String	STRING_PODCAST_4	= "PODCAST4";
	public	static	final	String	STRING_PODCAST_5	= "PODCAST5";
	
	// String For Video, Photo, Comedy, NollyWood
	public	static	final	String	STRING_VIDEO		= "VIDEO";
	public	static	final	String	STRING_PHOTO		= "PHOTO";
	public	static	final	String	STRING_COMEDY		= "COMEDY";
	public	static	final	String	STRING_NOLLYWOOD	= "NOLLYWOOD";

	// String for Cache
	public	static	final	String	STRING_CACHE		= "CACHE";
	
	//------------------------------------------------------------------------------
	// For Chat
	public	static	final	String EXTRA_DATA			= "extraData";

	//------------------------------------------------------------------------------
}

//==============================================================================
