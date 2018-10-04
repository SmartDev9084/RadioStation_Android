package androidcustom.radiostation.ui;


import android.app.Activity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Animation.AnimationListener;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidcustom.radiostation.R;
import androidcustom.radiostation.global.Const;
import androidcustom.radiostation.global.EnvVariable;
import androidcustom.radiostation.global.UtilDisplay;
import androidcustom.radiostation.loader.LoaderImage;

//==============================================================================
public class ActivityPlayPhoto extends Activity {

	private int			m_iIndex;
	private String		m_strTitle;
	private	AdapterBase		m_adapterBase;
   
	private	float		m_fTouchDownX;

	private	int			m_iIndexImageViewL;		// Index of ImageView
	private	int			m_iIndexImageViewC;		// Index of ImageView
	private	int			m_iIndexImageViewR;		// Index of ImageView

	private	ImageView	m_imgView1;
	private	ImageView	m_imgView2;
	private	ImageView	m_imgView3;

	private	FrameLayout.LayoutParams	m_frmLytParamsL;
	private	FrameLayout.LayoutParams	m_frmLytParamsC;
	private	FrameLayout.LayoutParams	m_frmLytParamsR;

	private	FrameLayout		m_frmLytMain;

	private boolean						m_bIsAnimationing;
	private int							m_iAnimationId;
	private AnimationListener			m_listenerAnimation;

	//------------------------------------------------------------------------------
	public ActivityPlayPhoto() {
		m_iIndex		= Const.INDEX_NONE;
		m_strTitle		= null;
		m_fTouchDownX	= 0;
		m_iIndexImageViewL	= 1;	// ImageView1
		m_iIndexImageViewC	= 2;	// ImageView2
		m_iIndexImageViewR	= 3;	// ImageView3
		
		m_bIsAnimationing = false;
		m_iAnimationId = 0;
	}

	//------------------------------------------------------------------------------
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_play_photo);
//		setRequestedOrientation(getResources().getConfiguration().orientation);			// Won't Rotate

		m_iIndex	= getIntent().getIntExtra(Const.KEY_INDEX, -1);
		m_adapterBase	= EnvVariable.CurrentAdapter;
		EnvVariable.CurrentAdapter = null;
		m_strTitle	= m_adapterBase.GetMultimediaInfo(m_iIndex).GetTitle();

		SetLayout();

		UpdateImage();
		
	// {{ AnimationListener
		m_listenerAnimation = new AnimationListener() {
			@Override
			public void onAnimationStart(Animation animation) {
			}

			@Override
			public void onAnimationRepeat(Animation animation) {
			}

			@Override
			public void onAnimationEnd(Animation animation) {
				if (m_bIsAnimationing == false)
					return;
				m_bIsAnimationing = false;
				m_frmLytMain.clearAnimation();
				if (m_iAnimationId == R.animator.animation_photo_left) {
					m_iIndex++;
					if (m_iIndex > m_adapterBase.getCount() - 1)
						m_iIndex = 0;
					UpdateImageLayout();
					UpdateImageRight();
				}
				if (m_iAnimationId == R.animator.animation_photo_right) {
					m_iIndex--;
					if (m_iIndex < 0)
						m_iIndex = m_adapterBase.getCount() - 1;
					UpdateImageLayout();
					UpdateImageLeft();
				}
			}
		};
	// }} AnimationListener
	}

	//------------------------------------------------------------------------------
	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	//------------------------------------------------------------------------------
	private void SetLayout() {
		Button			button;
		TextView		textView;
		RelativeLayout.LayoutParams		relLytParams;

	// {{ Title Bar
		button = (Button)findViewById(R.id.ID_BTN_TITLEBAR_PLAY_LEFT);
		button.setOnClickListener(
			new OnClickListener() {
				@Override
				public void onClick(View view) {
					ActivityPlayPhoto.this.finish();
				}
			}
		);

		textView = (TextView)findViewById(R.id.ID_TXTVIEW_TITLEBAR_PLAY_TITLE);
		textView.setText(m_strTitle);
	// }} Title Bar

	// {{ Next Prev Button
		button = (Button)findViewById(R.id.ID_BTN_PLAY_PHOTO_BTN_PREV);
		button.setOnClickListener(
			new OnClickListener() {
				@Override
				public void onClick(View v) {
					PlayPrevPhoto();
				}
			}
		);

		button = (Button)findViewById(R.id.ID_BTN_PLAY_PHOTO_BTN_NEXT);
		button.setOnClickListener(
			new OnClickListener() {
				@Override
				public void onClick(View v) {
					PlayNextPhoto();
				}
			}
		);
	// }} Next Prev Button

	// {{ FrameLayout Main
		m_frmLytMain = (FrameLayout)findViewById(R.id.ID_FRMLYT_PLAY_PHOTO_MAIN);
		relLytParams = (RelativeLayout.LayoutParams)m_frmLytMain.getLayoutParams();
		relLytParams.setMargins(
			(-1) * UtilDisplay.GetScreenSize(this).x,
			relLytParams.topMargin,
			(-1) * UtilDisplay.GetScreenSize(this).x,
			relLytParams.bottomMargin
		);
		m_frmLytMain.setLayoutParams(relLytParams);
	// }} FrameLayout Main

		m_imgView1 = (ImageView)findViewById(R.id.ID_IMGVIEW_PLAY_PHOTO_1);
		m_imgView2 = (ImageView)findViewById(R.id.ID_IMGVIEW_PLAY_PHOTO_2);
		m_imgView3 = (ImageView)findViewById(R.id.ID_IMGVIEW_PLAY_PHOTO_3);

	// {{ ImageView Layout for Left
		m_frmLytParamsL = (FrameLayout.LayoutParams)m_imgView1.getLayoutParams();
		m_frmLytParamsL.setMargins(
			0,
			m_frmLytParamsL.topMargin,
			2 * UtilDisplay.GetScreenSize(this).x,
			m_frmLytParamsL.bottomMargin
		);
		m_imgView1.setLayoutParams(m_frmLytParamsL);
	// }} ImageView Layout for Left

	// {{ ImageView Layout for Left
		m_frmLytParamsC = (FrameLayout.LayoutParams)m_imgView2.getLayoutParams();
		m_frmLytParamsC.setMargins(
			UtilDisplay.GetScreenSize(this).x,
			m_frmLytParamsC.topMargin,
			UtilDisplay.GetScreenSize(this).x,
			m_frmLytParamsC.bottomMargin
		);
		m_imgView2.setLayoutParams(m_frmLytParamsC);
	// }} ImageView Layout for Left

	// {{ ImageView Layout for Right
		m_frmLytParamsR = (FrameLayout.LayoutParams)m_imgView3.getLayoutParams();
		m_frmLytParamsR.setMargins(
			2 * UtilDisplay.GetScreenSize(this).x,
			m_frmLytParamsR.topMargin,
			0,
			m_frmLytParamsR.bottomMargin
		);
		m_imgView3.setLayoutParams(m_frmLytParamsR);
	// }} ImageView Layout for Right

	// {{ ImageView1
		m_imgView1.setOnTouchListener(
			new OnTouchListener() {
				@Override
				public boolean onTouch(View v, MotionEvent event) {
					float	fTouchUpX;
					switch (event.getAction()) {
						case MotionEvent.ACTION_DOWN:
							m_fTouchDownX = event.getX();
							break;
						case MotionEvent.ACTION_UP:
							fTouchUpX = event.getX();
							if (fTouchUpX - m_fTouchDownX > 150)
								SwipeRight();
							if (m_fTouchDownX - fTouchUpX > 150)
								SwipeLeft();
							break;
					}
					return true;
				}
			}
		);
	// }} ImageView1

	// {{ ImageView2
		m_imgView2.setOnTouchListener(
			new OnTouchListener() {
				@Override
				public boolean onTouch(View v, MotionEvent event) {
					float	fTouchUpX;
					switch (event.getAction()) {
						case MotionEvent.ACTION_DOWN:
							m_fTouchDownX = event.getX();
							break;
						case MotionEvent.ACTION_UP:
							fTouchUpX = event.getX();
							if (fTouchUpX - m_fTouchDownX > 150)
								SwipeRight();
							if (m_fTouchDownX - fTouchUpX > 150)
								SwipeLeft();
							break;
					}
					return true;
				}
			}
		);
	// }} ImageView2

	// {{ ImageView3
		m_imgView3.setOnTouchListener(
			new OnTouchListener() {
				@Override
				public boolean onTouch(View v, MotionEvent event) {
					float	fTouchUpX;
					switch (event.getAction()) {
						case MotionEvent.ACTION_DOWN:
							m_fTouchDownX = event.getX();
							break;
						case MotionEvent.ACTION_UP:
							fTouchUpX = event.getX();
							if (fTouchUpX - m_fTouchDownX > 150)
								SwipeRight();
							if (m_fTouchDownX - fTouchUpX > 150)
								SwipeLeft();
							break;
					}
					return true;
				}
			}
		);
	// }} ImageView4
	}

	//------------------------------------------------------------------------------
	private void UpdateImage() {
		if (m_iIndex == Const.INDEX_NONE)
			return;

		switch (m_iIndexImageViewC) {
			case 1:
				if (LoaderImage.GetInstance() != null)
					LoaderImage.GetInstance().DisplayImage(
						m_imgView1,
						m_adapterBase.GetMultimediaInfo(m_iIndex).GetLink()
					);
				break;
			case 2:
				if (LoaderImage.GetInstance() != null)
					LoaderImage.GetInstance().DisplayImage(
						m_imgView2,
						m_adapterBase.GetMultimediaInfo(m_iIndex).GetLink()
					);
				break;
			default:
				if (LoaderImage.GetInstance() != null)
					LoaderImage.GetInstance().DisplayImage(
						m_imgView3,
						m_adapterBase.GetMultimediaInfo(m_iIndex).GetLink()
					);
				break;
		}
		
		UpdateImageRight();
		UpdateImageLeft();
	}

	//------------------------------------------------------------------------------
	private void UpdateImageRight() {
		int iTemp;

	// {{ For Right ImageView
		iTemp = m_iIndex + 1;
		if (iTemp >= m_adapterBase.getCount())
			iTemp = 0;

		switch (m_iIndexImageViewR) {
			case 1:
				if (LoaderImage.GetInstance() != null)
					LoaderImage.GetInstance().DisplayImage(
						m_imgView1,
						m_adapterBase.GetMultimediaInfo(iTemp).GetLink()
					);
				break;
			case 2:
				if (LoaderImage.GetInstance() != null)
					LoaderImage.GetInstance().DisplayImage(
						m_imgView2,
						m_adapterBase.GetMultimediaInfo(iTemp).GetLink()
					);
				break;
			default:
				if (LoaderImage.GetInstance() != null)
					LoaderImage.GetInstance().DisplayImage(
						m_imgView3,
						m_adapterBase.GetMultimediaInfo(iTemp).GetLink()
					);
				break;
		}
	// }} For Right ImageView
	}

	//------------------------------------------------------------------------------
	private void UpdateImageLeft() {
		int iTemp;
	// {{ For Left ImageView
		iTemp = m_iIndex - 1;
		if (iTemp == -1)
			iTemp = m_adapterBase.getCount() - 1;

		switch (m_iIndexImageViewL) {
			case 1:
				if (LoaderImage.GetInstance() != null)
					LoaderImage.GetInstance().DisplayImage(
						m_imgView1,
						m_adapterBase.GetMultimediaInfo(iTemp).GetLink()
					);
				break;
			case 2:
				if (LoaderImage.GetInstance() != null)
					LoaderImage.GetInstance().DisplayImage(
						m_imgView2,
						m_adapterBase.GetMultimediaInfo(iTemp).GetLink()
					);
				break;
			default:
				if (LoaderImage.GetInstance() != null)
					LoaderImage.GetInstance().DisplayImage(
						m_imgView3,
						m_adapterBase.GetMultimediaInfo(iTemp).GetLink()
					);
				break;
		}
	// }} For Left ImageView
	}

	//------------------------------------------------------------------------------
	private void UpdateImageLayout() {
		switch (m_iIndexImageViewL) {
			case 1:		m_imgView1.setLayoutParams(m_frmLytParamsL);		break;
			case 2:		m_imgView2.setLayoutParams(m_frmLytParamsL);		break;
			default:	m_imgView3.setLayoutParams(m_frmLytParamsL);		break;
		}
		switch (m_iIndexImageViewC) {
			case 1:		m_imgView1.setLayoutParams(m_frmLytParamsC);		break;
			case 2:		m_imgView2.setLayoutParams(m_frmLytParamsC);		break;
			default:	m_imgView3.setLayoutParams(m_frmLytParamsC);		break;
		}
		switch (m_iIndexImageViewR) {
			case 1:		m_imgView1.setLayoutParams(m_frmLytParamsR);		break;
			case 2:		m_imgView2.setLayoutParams(m_frmLytParamsR);		break;
			default:	m_imgView3.setLayoutParams(m_frmLytParamsR);		break;
		}
	}

	//------------------------------------------------------------------------------
	private void PlayPrevPhoto() {
		m_iIndex--;
		if (m_iIndex == -1)
			m_iIndex = m_adapterBase.getCount() - 1;
		UpdateImage();
	}

	//------------------------------------------------------------------------------
	private void PlayNextPhoto() {
		m_iIndex++;
		if (m_iIndex == m_adapterBase.getCount())
			m_iIndex = 0;
		UpdateImage();
	}

	//------------------------------------------------------------------------------
	private void SwipeRight() {
		int		iTemp;
		Animation		animation;

		if (m_bIsAnimationing)		return;

		iTemp = m_iIndexImageViewR;
		m_iIndexImageViewR = m_iIndexImageViewC;
		m_iIndexImageViewC = m_iIndexImageViewL;
		m_iIndexImageViewL = iTemp;

		animation = AnimationUtils.loadAnimation(this, R.animator.animation_photo_right);
		animation.setAnimationListener(m_listenerAnimation);

		m_iAnimationId		= R.animator.animation_photo_right;
		m_bIsAnimationing	= true;
		m_frmLytMain.startAnimation(animation);
	}

	//------------------------------------------------------------------------------
	private void SwipeLeft() {
		int		iTemp;
		Animation		animation;

		if (m_bIsAnimationing)		return;

		iTemp = m_iIndexImageViewL;
		m_iIndexImageViewL = m_iIndexImageViewC;
		m_iIndexImageViewC = m_iIndexImageViewR;
		m_iIndexImageViewR = iTemp;

		animation = AnimationUtils.loadAnimation(this, R.animator.animation_photo_left);
		animation.setAnimationListener(m_listenerAnimation);

		m_iAnimationId		= R.animator.animation_photo_left;
		m_bIsAnimationing	= true;
		m_frmLytMain.startAnimation(animation);
	}

}

//==============================================================================
