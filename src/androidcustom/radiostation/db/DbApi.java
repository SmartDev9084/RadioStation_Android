package androidcustom.radiostation.db;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import androidcustom.radiostation.login.LoginInfo;
import androidcustom.radiostation.multimedia.MultimediaInfo;

//==============================================================================
public class DbApi extends SQLiteOpenHelper {

	private	static	DbApi				m_instance;

	private			SQLiteDatabase		m_sqlDatabase;

	private	static	final	String		DATABASE_NAME		= "radiostationdb.db";
	private	static	final	String		TABLE_LOGIN			= "tbl_login";
	private	static	final	String		TABLE_FAVORITE		= "tbl_favorite";
	private	static	final	String		TABLE_PLAYLIST		= "tbl_playlist";
	private	static	final	String		TABLE_HISTORY		= "tbl_history";

	private	static	final	int		DATABASE_VERSION	= 1;

	private	final	String	FIELD_ID		= "id";

// {{ For Login
	private	final	String	FIELD_USERNAME	= "username";
	private	final	String	FIELD_PASSWORD	= "password";
// }} For Login

// {{ For MultimediaInfo
	private	final	String	FIELD_TYPE			=	"type";
	private	final	String	FIELD_MEDIAID		=	"mediaid";
	private	final	String	FIELD_TITLE			=	"title";
	private	final	String	FIELD_ARTIST		=	"artist";
	private	final	String	FIELD_PATH1			=	"path1";
	private	final	String	FIELD_PATH2			=	"path2";
	private	final	String	FIELD_LINK			=	"link";
	private	final	String	FIELD_THUMBNAIL		=	"thumbnail";
	private	final	String	FIELD_POSTER		=	"poster";
	private	final	String	FIELD_DURATION		=	"duration";
	private	final	String	FIELD_LIKECOUNT		=	"likecount";
// }} For MultimediaInfo

	private	final	String	COMMAND_CREATE_TABLE_LOGIN =
			"CREATE TABLE " + TABLE_LOGIN +
			" ( " +
				FIELD_ID		+ " INTEGER PRIMARY KEY AUTOINCREMENT"	+ ", " +
				FIELD_USERNAME	+ " TEXT"	+ ", " +
				FIELD_PASSWORD	+ " TEXT"	+
			" );";

	private	final	String	COMMAND_CREATE_TABLE_FAVORITE =
			"CREATE TABLE " + TABLE_FAVORITE +
			" ( " +
				FIELD_ID			+ " INTEGER PRIMARY KEY AUTOINCREMENT"	+ ", " +
				FIELD_TYPE			+ " INTEGER"	+ ", " +
				FIELD_MEDIAID		+ " TEXT"		+ ", " +
				FIELD_TITLE			+ " TEXT"		+ ", " +
				FIELD_ARTIST		+ " TEXT"		+ ", " +
				FIELD_PATH1			+ " TEXT"		+ ", " +
				FIELD_PATH2			+ " TEXT"		+ ", " +
				FIELD_LINK			+ " TEXT"		+ ", " +
				FIELD_THUMBNAIL		+ " TEXT"		+ ", " +
				FIELD_POSTER		+ " TEXT"		+ ", " +
				FIELD_DURATION		+ " TEXT"		+ ", " +
				FIELD_LIKECOUNT		+ " INTEGER"	+
			" );";

	private	final	String	COMMAND_CREATE_TABLE_PLAYLIST =
			"CREATE TABLE " + TABLE_PLAYLIST +
			" ( " +
				FIELD_ID			+ " INTEGER PRIMARY KEY AUTOINCREMENT"	+ ", " +
				FIELD_TYPE			+ " INTEGER"	+ ", " +
				FIELD_MEDIAID		+ " TEXT"		+ ", " +
				FIELD_TITLE			+ " TEXT"		+ ", " +
				FIELD_ARTIST		+ " TEXT"		+ ", " +
				FIELD_PATH1			+ " TEXT"		+ ", " +
				FIELD_PATH2			+ " TEXT"		+ ", " +
				FIELD_LINK			+ " TEXT"		+ ", " +
				FIELD_THUMBNAIL		+ " TEXT"		+ ", " +
				FIELD_POSTER		+ " TEXT"		+ ", " +
				FIELD_DURATION		+ " TEXT"		+ ", " +
				FIELD_LIKECOUNT		+ " INTEGER"	+
			" );";

	private	final	String	COMMAND_CREATE_TABLE_HISTORY =
			"CREATE TABLE " + TABLE_HISTORY +
			" ( " +
				FIELD_ID			+ " INTEGER PRIMARY KEY AUTOINCREMENT"	+ ", " +
				FIELD_TYPE			+ " INTEGER"	+ ", " +
				FIELD_MEDIAID		+ " TEXT"		+ ", " +
				FIELD_TITLE			+ " TEXT"		+ ", " +
				FIELD_ARTIST		+ " TEXT"		+ ", " +
				FIELD_PATH1			+ " TEXT"		+ ", " +
				FIELD_PATH2			+ " TEXT"		+ ", " +
				FIELD_LINK			+ " TEXT"		+ ", " +
				FIELD_THUMBNAIL		+ " TEXT"		+ ", " +
				FIELD_POSTER		+ " TEXT"		+ ", " +
				FIELD_DURATION		+ " TEXT"		+ ", " +
				FIELD_LIKECOUNT		+ " INTEGER"	+
			" );";

	//------------------------------------------------------------------------------
	public	static	DbApi GetInstance() {
		return m_instance;
	}

	//------------------------------------------------------------------------------
	public static void Create(Context a_context) {
		if (m_instance == null)
			m_instance = new DbApi(a_context);
	}

	//------------------------------------------------------------------------------
	public DbApi(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		Open();
	}

	//------------------------------------------------------------------------------
	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(COMMAND_CREATE_TABLE_FAVORITE);
		db.execSQL(COMMAND_CREATE_TABLE_LOGIN);
		db.execSQL(COMMAND_CREATE_TABLE_PLAYLIST);
		db.execSQL(COMMAND_CREATE_TABLE_HISTORY);
	}

	//------------------------------------------------------------------------------
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		m_sqlDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_LOGIN		+ ";");
		m_sqlDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_FAVORITE	+ ";");
		m_sqlDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_PLAYLIST	+ ";");
		m_sqlDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_HISTORY	+ ";");
		onCreate(m_sqlDatabase);
	}

	//------------------------------------------------------------------------------
	public DbApi Open() {
		try {
			m_sqlDatabase = getWritableDatabase();
		}
		catch (SQLiteException ex) {
			m_sqlDatabase = getReadableDatabase();
		}
		return this;
	}

	//------------------------------------------------------------------------------
	@Override
	public void close() {
		super.close();
		m_sqlDatabase.close();
	}

	//------------------------------------------------------------------------------
	public void InsertLoginInfo(LoginInfo a_loginInfo) {
		ContentValues contentValues = new ContentValues();
		contentValues.put(FIELD_USERNAME, a_loginInfo.GetUserName());
		contentValues.put(FIELD_PASSWORD, a_loginInfo.GetPassword());
	
		m_sqlDatabase.insert(TABLE_LOGIN, null, contentValues);

	}

	//------------------------------------------------------------------------------
	public boolean HasLoginInfo() {
		String	strQuery = "SELECT count(*) FROM " + TABLE_LOGIN;
		Cursor	cursor = m_sqlDatabase.rawQuery(strQuery, null);
		cursor.moveToFirst();
		int		iCount = cursor.getInt(0);
		cursor.close();
		return (iCount > 0);
	}

	//------------------------------------------------------------------------------
	public LoginInfo GetLoginInfo() {
		Cursor cursor =  m_sqlDatabase.query(TABLE_LOGIN, null, null, null, null, null, null);
		if (cursor.getCount() == 0 || !cursor.moveToFirst()) {
			return null;
		}
		LoginInfo loginInfo = new LoginInfo();
		while (!cursor.isAfterLast()) {
			String strUserName = cursor.getString(cursor.getColumnIndex(FIELD_USERNAME));
			String strPassword = cursor.getString(cursor.getColumnIndex(FIELD_PASSWORD));
			
			loginInfo.SetUserName(strUserName);
			loginInfo.SetPassword(strPassword);
			cursor.moveToNext();
		}
		return loginInfo;
	}

	//------------------------------------------------------------------------------
	public boolean InsertMultimediaInfoToFavorite(MultimediaInfo a_multimediaInfo) {
		ContentValues contentValues = new ContentValues();

	// {{ Count Duplicate
		String	strQuery = "SELECT count(*) FROM " + TABLE_FAVORITE
							+ " WHERE "
							+ FIELD_MEDIAID + " LIKE " + "'" + a_multimediaInfo.GetId()		+ "'" + " AND "
							+ FIELD_PATH1	+ " LIKE " + "'" + a_multimediaInfo.GetPath1()	+ "'";
		Cursor	cursor = m_sqlDatabase.rawQuery(strQuery, null);
		cursor.moveToFirst();
		int		iCount = cursor.getInt(0);
		cursor.close();
		if (iCount > 0)
			return false;
	// }} Count Duplicate

		contentValues.put(FIELD_TYPE,		a_multimediaInfo.GetType());
		contentValues.put(FIELD_MEDIAID,	a_multimediaInfo.GetId());
		contentValues.put(FIELD_TITLE,		a_multimediaInfo.GetTitle());
		contentValues.put(FIELD_ARTIST,		a_multimediaInfo.GetArtist());
		contentValues.put(FIELD_PATH1,		a_multimediaInfo.GetPath1());
		contentValues.put(FIELD_PATH2,		a_multimediaInfo.GetPath2());
		contentValues.put(FIELD_LINK,		a_multimediaInfo.GetLink());
		contentValues.put(FIELD_THUMBNAIL,	a_multimediaInfo.GetThumb());
		contentValues.put(FIELD_POSTER,		a_multimediaInfo.GetPoster());
		contentValues.put(FIELD_DURATION,	a_multimediaInfo.GetDuration());
		contentValues.put(FIELD_LIKECOUNT,	a_multimediaInfo.GetLikeCount() + 1);

		m_sqlDatabase.insert(TABLE_FAVORITE, null, contentValues);
		return true;
	}

	//------------------------------------------------------------------------------
	public void InsertMultimediaInfoToPlaylist(MultimediaInfo a_multimediaInfo) {
		ContentValues contentValues = new ContentValues();

	// {{ Count Duplicate
		String	strQuery = "SELECT count(*) FROM " + TABLE_PLAYLIST
							+ " WHERE "
							+ FIELD_MEDIAID + " LIKE " + "'" + a_multimediaInfo.GetId()		+ "'" + " AND "
							+ FIELD_PATH1	+ " LIKE " + "'" + a_multimediaInfo.GetPath1()	+ "'";
		Cursor	cursor = m_sqlDatabase.rawQuery(strQuery, null);
		cursor.moveToFirst();
		int		iCount = cursor.getInt(0);
		cursor.close();
		if (iCount > 0)
			return;
	// }} Count Duplicate

		contentValues.put(FIELD_TYPE,		a_multimediaInfo.GetType());
		contentValues.put(FIELD_MEDIAID,	a_multimediaInfo.GetId());
		contentValues.put(FIELD_TITLE,		a_multimediaInfo.GetTitle());
		contentValues.put(FIELD_ARTIST,		a_multimediaInfo.GetArtist());
		contentValues.put(FIELD_PATH1,		a_multimediaInfo.GetPath1());
		contentValues.put(FIELD_PATH2,		a_multimediaInfo.GetPath2());
		contentValues.put(FIELD_LINK,		a_multimediaInfo.GetLink());
		contentValues.put(FIELD_THUMBNAIL,	a_multimediaInfo.GetThumb());
		contentValues.put(FIELD_POSTER,		a_multimediaInfo.GetPoster());
		contentValues.put(FIELD_DURATION,	a_multimediaInfo.GetDuration());
		contentValues.put(FIELD_LIKECOUNT,	a_multimediaInfo.GetLikeCount());

		m_sqlDatabase.insert(TABLE_PLAYLIST, null, contentValues);
	}

	//------------------------------------------------------------------------------
	public void InsertMultimediaInfoToHistory(MultimediaInfo a_multimediaInfo) {
		ContentValues	contentValues = new ContentValues();
		int		iCount = 0;

		String	strQuery = "SELECT count(*) FROM " + TABLE_HISTORY
							+ " WHERE "
							+ FIELD_MEDIAID + " LIKE " + "'" + a_multimediaInfo.GetId()		+ "'" + " AND "
							+ FIELD_PATH1	+ " LIKE " + "'" + a_multimediaInfo.GetPath1()	+ "'";
		Cursor	cursor = m_sqlDatabase.rawQuery(strQuery, null);
		cursor.moveToFirst();
		iCount = cursor.getInt(0);
		cursor.close();
		if (iCount > 0)
			return;

		contentValues.put(FIELD_TYPE,		a_multimediaInfo.GetType());
		contentValues.put(FIELD_MEDIAID,	a_multimediaInfo.GetId());
		contentValues.put(FIELD_TITLE,		a_multimediaInfo.GetTitle());
		contentValues.put(FIELD_ARTIST,		a_multimediaInfo.GetArtist());
		contentValues.put(FIELD_PATH1,		a_multimediaInfo.GetPath1());
		contentValues.put(FIELD_PATH2,		a_multimediaInfo.GetPath2());
		contentValues.put(FIELD_LINK,		a_multimediaInfo.GetLink());
		contentValues.put(FIELD_THUMBNAIL,	a_multimediaInfo.GetThumb());
		contentValues.put(FIELD_POSTER,		a_multimediaInfo.GetPoster());
		contentValues.put(FIELD_DURATION,	a_multimediaInfo.GetDuration());
		contentValues.put(FIELD_LIKECOUNT,	a_multimediaInfo.GetLikeCount());

		m_sqlDatabase.insert(TABLE_HISTORY, null, contentValues);
	}

	//------------------------------------------------------------------------------
	public ArrayList<MultimediaInfo> GetMultimediaInfoFromFavorite() {
		Cursor						cursor;
		ArrayList<MultimediaInfo>	arrMultimediaInfo;
		MultimediaInfo				multimediaInfo;
		arrMultimediaInfo = new ArrayList<MultimediaInfo>();
		try {
			cursor =  m_sqlDatabase.query(TABLE_FAVORITE, null, null, null, null, null, null);
		}
		catch (Exception e) {
			return arrMultimediaInfo;
		}

		if (cursor.getCount() == 0 || !cursor.moveToFirst()) {
			return arrMultimediaInfo;
		}

		while (!cursor.isAfterLast()) {
			int		iType			= cursor.getInt(cursor.getColumnIndex(FIELD_TYPE));
			String	strMediaId		= cursor.getString(cursor.getColumnIndex(FIELD_MEDIAID));
			String	strTitle		= cursor.getString(cursor.getColumnIndex(FIELD_TITLE));
			String	strArtist		= cursor.getString(cursor.getColumnIndex(FIELD_ARTIST));
			String	strPath1		= cursor.getString(cursor.getColumnIndex(FIELD_PATH1));
			String	strPath2		= cursor.getString(cursor.getColumnIndex(FIELD_PATH2));
			String	strLink			= cursor.getString(cursor.getColumnIndex(FIELD_LINK));
			String	strThumbnail	= cursor.getString(cursor.getColumnIndex(FIELD_THUMBNAIL));
			String	strPoster		= cursor.getString(cursor.getColumnIndex(FIELD_POSTER));
			String	strDuration		= cursor.getString(cursor.getColumnIndex(FIELD_DURATION));
			int		iLikeCount		= cursor.getInt(cursor.getColumnIndex(FIELD_LIKECOUNT));

			multimediaInfo = new MultimediaInfo();

			multimediaInfo.SetType(iType);
			multimediaInfo.SetId(strMediaId);
			multimediaInfo.SetTitle(strTitle);
			multimediaInfo.SetArtist(strArtist);
			multimediaInfo.SetPath1(strPath1);
			multimediaInfo.SetPath2(strPath2);
			multimediaInfo.SetLink(strLink);
			multimediaInfo.SetThumb(strThumbnail);
			multimediaInfo.SetPoster(strPoster);
			multimediaInfo.SetDuration(strDuration);
			multimediaInfo.SetLikeCount(iLikeCount);

			arrMultimediaInfo.add(multimediaInfo);

			cursor.moveToNext();
		}
		return arrMultimediaInfo;
	}

	//------------------------------------------------------------------------------
	public ArrayList<MultimediaInfo> GetMultimediaInfoFromPlaylist() {
		Cursor						cursor;
		ArrayList<MultimediaInfo>	arrMultimediaInfo;
		MultimediaInfo				multimediaInfo;
		arrMultimediaInfo = new ArrayList<MultimediaInfo>();

		try {
			cursor =  m_sqlDatabase.query(TABLE_PLAYLIST, null, null, null, null, null, null);
		}
		catch (Exception e) {
			return arrMultimediaInfo;
		}

		if (cursor.getCount() == 0 || !cursor.moveToFirst()) {
			return arrMultimediaInfo;
		}

		while (!cursor.isAfterLast()) {
			int		iType			= cursor.getInt(cursor.getColumnIndex(FIELD_TYPE));
			String	strMediaId		= cursor.getString(cursor.getColumnIndex(FIELD_MEDIAID));
			String	strTitle		= cursor.getString(cursor.getColumnIndex(FIELD_TITLE));
			String	strArtist		= cursor.getString(cursor.getColumnIndex(FIELD_ARTIST));
			String	strPath1		= cursor.getString(cursor.getColumnIndex(FIELD_PATH1));
			String	strPath2		= cursor.getString(cursor.getColumnIndex(FIELD_PATH2));
			String	strLink			= cursor.getString(cursor.getColumnIndex(FIELD_LINK));
			String	strThumbnail	= cursor.getString(cursor.getColumnIndex(FIELD_THUMBNAIL));
			String	strPoster		= cursor.getString(cursor.getColumnIndex(FIELD_POSTER));
			String	strDuration		= cursor.getString(cursor.getColumnIndex(FIELD_DURATION));
			int		iLikeCount		= cursor.getInt(cursor.getColumnIndex(FIELD_LIKECOUNT));

			multimediaInfo = new MultimediaInfo();

			multimediaInfo.SetType(iType);
			multimediaInfo.SetId(strMediaId);
			multimediaInfo.SetTitle(strTitle);
			multimediaInfo.SetArtist(strArtist);
			multimediaInfo.SetPath1(strPath1);
			multimediaInfo.SetPath2(strPath2);
			multimediaInfo.SetLink(strLink);
			multimediaInfo.SetThumb(strThumbnail);
			multimediaInfo.SetPoster(strPoster);
			multimediaInfo.SetDuration(strDuration);
			multimediaInfo.SetLikeCount(iLikeCount);

			arrMultimediaInfo.add(multimediaInfo);

			cursor.moveToNext();
		}
		return arrMultimediaInfo;
	}

	//------------------------------------------------------------------------------
	public ArrayList<MultimediaInfo> GetMultimediaInfoFromHistory() {
		Cursor						cursor;
		ArrayList<MultimediaInfo>	arrMultimediaInfo;
		MultimediaInfo				multimediaInfo;
		arrMultimediaInfo = new ArrayList<MultimediaInfo>();

		try {
			cursor =  m_sqlDatabase.query(TABLE_HISTORY, null, null, null, null, null, null);
		}
		catch (Exception e) {
			return arrMultimediaInfo;
		}

		if (cursor.getCount() == 0 || !cursor.moveToFirst()) {
			return arrMultimediaInfo;
		}

		while (!cursor.isAfterLast()) {
			int		iType			= cursor.getInt(cursor.getColumnIndex(FIELD_TYPE));
			String	strMediaId		= cursor.getString(cursor.getColumnIndex(FIELD_MEDIAID));
			String	strTitle		= cursor.getString(cursor.getColumnIndex(FIELD_TITLE));
			String	strArtist		= cursor.getString(cursor.getColumnIndex(FIELD_ARTIST));
			String	strPath1		= cursor.getString(cursor.getColumnIndex(FIELD_PATH1));
			String	strPath2		= cursor.getString(cursor.getColumnIndex(FIELD_PATH2));
			String	strLink			= cursor.getString(cursor.getColumnIndex(FIELD_LINK));
			String	strThumbnail	= cursor.getString(cursor.getColumnIndex(FIELD_THUMBNAIL));
			String	strPoster		= cursor.getString(cursor.getColumnIndex(FIELD_POSTER));
			String	strDuration		= cursor.getString(cursor.getColumnIndex(FIELD_DURATION));
			int		iLikeCount		= cursor.getInt(cursor.getColumnIndex(FIELD_LIKECOUNT));

			multimediaInfo = new MultimediaInfo();

			multimediaInfo.SetType(iType);
			multimediaInfo.SetId(strMediaId);
			multimediaInfo.SetTitle(strTitle);
			multimediaInfo.SetArtist(strArtist);
			multimediaInfo.SetPath1(strPath1);
			multimediaInfo.SetPath2(strPath2);
			multimediaInfo.SetLink(strLink);
			multimediaInfo.SetThumb(strThumbnail);
			multimediaInfo.SetPoster(strPoster);
			multimediaInfo.SetDuration(strDuration);
			multimediaInfo.SetLikeCount(iLikeCount);

			arrMultimediaInfo.add(multimediaInfo);

			cursor.moveToNext();
		}
		return arrMultimediaInfo;
	}
}

//==============================================================================
