package androidcustom.radiostation.ui;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.BounceInterpolator;
import android.view.animation.TranslateAnimation;
import android.view.animation.Animation.AnimationListener;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ListView;
import androidcustom.radiostation.R;
import androidcustom.radiostation.db.DbApi;
import androidcustom.radiostation.global.Const;
import androidcustom.radiostation.http.HttpApi;
import androidcustom.radiostation.multimedia.MultimediaInfo;

//==============================================================================
public class AdapterBaseList extends AdapterBase {

	//==============================================================================
	public static interface ItemContentClickListener {
		public void OnItemContentClicked(int a_iPosition);
	}
	//==============================================================================

	private		LayoutInflater		m_inflater;
	protected	ListView			m_listView;

	protected	OnClickListener			m_listenerAddFavorite;
	protected	OnClickListener			m_listenerAddPlaylist;
	protected	OnClickListener			m_listenerShare;
	protected	OnClickListener			m_listenerSlide;

	private		float		m_fTouchDownX;
	private		float		m_fTouchDownY;
	private		boolean		m_bIsAnimationing;
	private		int			m_iAnimationType;

	private		View		m_viewCurrAnim;
	private		View		m_viewMainContent;
	
	private		OnTouchListener					m_listenerTouch;
	private		ItemContentClickListener		m_listenerItemContentClick;

	//------------------------------------------------------------------------------
	public AdapterBaseList(Context a_context, ListView a_listView) {
		super(a_context);

		m_listView = a_listView;
		m_inflater = (LayoutInflater)m_context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		
		m_iAnimationType	= Const.ANIMATION_NONE;
		m_bIsAnimationing	= false;

	// {{ Touch Listener
		m_listenerTouch = new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				float	fTouchUpX;
				float	fTouchUpY;
				int		iPosition;
				iPosition = m_listView.getPositionForView(v);
				switch (event.getAction()) {
					case MotionEvent.ACTION_DOWN:
						m_fTouchDownX = event.getX();
						m_fTouchDownY = event.getY();
						return true;
					case MotionEvent.ACTION_UP:
						fTouchUpX = event.getX();
						fTouchUpY = event.getY();
						if (Math.abs(fTouchUpY - m_fTouchDownY) < 50) {
							if (fTouchUpX - m_fTouchDownX > 3) {
								if (m_iPosCurrAnim != iPosition) {
									m_iPosCurrAnim = iPosition;
									StartAnimRight();
								}
							}
							else if (m_fTouchDownX - fTouchUpX > 3) {
								if (m_iPosCurrAnim == iPosition)
									StartAnimLeft();
							}
							else
								m_listenerItemContentClick.OnItemContentClicked(iPosition);
							return true;
						}
					default:
						break;
				}
				return false;
			}
		};
	// }} Touch Listener

	// {{ Click Listener Favorite
		m_listenerAddFavorite = new OnClickListener() {
			@Override
			public void onClick(View v) {
				DialogProgress	dlgProgress = new DialogProgress(m_context);
				HttpApi			httpApi = new HttpApi();
				boolean			bIsNew = false;

				dlgProgress.setCanceledOnTouchOutside(false);
				dlgProgress.setCancelable(false);
				dlgProgress.show();

				MultimediaInfo	multimediaInfo = m_arrMultimediaInfo.get(m_arrItemIndex.get(m_iPosCurrAnim));
				bIsNew = DbApi.GetInstance().InsertMultimediaInfoToFavorite(multimediaInfo);		// Like Count is increased in this function
				if (bIsNew)
				{
					multimediaInfo.SetLikeCount(multimediaInfo.GetLikeCount() + 1);
					httpApi = new HttpApi();
					httpApi.IncreaseCountLike(multimediaInfo.GetTitle(), multimediaInfo.GetArtist());
				}
				dlgProgress.cancel();
			}
		};
	// }} Click Listener Favorite

	// {{ Click Listener Playlist
		m_listenerAddPlaylist = new OnClickListener() {
			@Override
			public void onClick(View v) {
				DialogProgress	dlgProgress = new DialogProgress(m_context);

				dlgProgress.setCanceledOnTouchOutside(false);
				dlgProgress.setCancelable(false);
				dlgProgress.show();

				MultimediaInfo	multimediaInfo = m_arrMultimediaInfo.get(m_arrItemIndex.get(m_iPosCurrAnim));
				DbApi.GetInstance().InsertMultimediaInfoToPlaylist(multimediaInfo);
				
				dlgProgress.cancel();
			}
		};
	// }} Click Listener Playlist

	// {{ Click Listener Share
		m_listenerShare = new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intentShare = new Intent(android.content.Intent.ACTION_SEND);
				intentShare.setType("text/*");
				MultimediaInfo	multimediaInfo = m_arrMultimediaInfo.get(m_arrItemIndex.get(m_iPosCurrAnim));
				intentShare.putExtra(
					Intent.EXTRA_TEXT,
					"#Playing now "								+
					"'" +	multimediaInfo.GetTitle()	+ "' "	+ "by " +
					"'" +	multimediaInfo.GetArtist()	+ "' "	+
					"on 1triberadio app #1triberadio @1triberadio"
				);
				m_context.startActivity(Intent.createChooser(intentShare, "SHARE"));
			}
		};
	// }} Click Listener Share

	// {{ Click Listener Slide
		m_listenerSlide = new OnClickListener() {
			@Override
			public void onClick(View v) {
				m_iPosCurrAnim = m_listView.getPositionForView(v);
				StartAnimRight();
			}
		};
	// }} Click Listener Slide
	}

	//------------------------------------------------------------------------------
	public void SetItemContentClickListener(ItemContentClickListener a_listenerItemContentClick) {
		m_listenerItemContentClick = a_listenerItemContentClick;
	}

	//------------------------------------------------------------------------------
	protected View GetInflateView(int a_iPosition, View a_convertView, ViewGroup a_parent, int a_iListItemId) {
		Button		btnAddFavorite;
		Button		btnAddPlaylist;
		Button		btnShare;
		Button		btnSlide;

		if (a_iPosition >= m_arrItemIndex.size())
			return null;

		if (a_convertView == null)
			a_convertView = m_inflater.inflate(a_iListItemId, a_parent, false);

		FrameLayout.LayoutParams	frmLayoutParams;
		View						viewMainContent;
		viewMainContent	= a_convertView.findViewById(R.id.ID_RELLYT_ITEM_MAIN_CONTENT);
		frmLayoutParams	= (FrameLayout.LayoutParams)viewMainContent.getLayoutParams();

		btnAddFavorite	= (Button)a_convertView.findViewById(R.id.ID_BTN_ADD_FAVORITE);
		btnAddPlaylist	= (Button)a_convertView.findViewById(R.id.ID_BTN_ADD_PLAYLIST);
		btnShare		= (Button)a_convertView.findViewById(R.id.ID_BTN_SHARE);
		btnSlide		= (Button)a_convertView.findViewById(R.id.ID_BTN_SLIDE);

		if (a_iPosition == m_iPosCurrAnim) {
			if (!m_bIsAnimationing) {
				if (m_iAnimationType == Const.ANIMATION_RIGHT) {
					viewMainContent.clearAnimation();
					frmLayoutParams.setMargins(
						(int)(150 * m_context.getResources().getDisplayMetrics().density),
						0,
						(int)(-150 * m_context.getResources().getDisplayMetrics().density),
						0
					);
					viewMainContent.setLayoutParams(frmLayoutParams);
					btnAddFavorite.setVisibility(View.VISIBLE);
					btnAddPlaylist.setVisibility(View.VISIBLE);
					btnShare.setVisibility(View.VISIBLE);
				}
				else {
					viewMainContent.clearAnimation();
					frmLayoutParams.setMargins(0, 0, 0, 0);
					viewMainContent.setLayoutParams(frmLayoutParams);
				}
			}
		}
		else {
			btnAddFavorite.setVisibility(View.INVISIBLE);
			btnAddPlaylist.setVisibility(View.INVISIBLE);
			btnShare.setVisibility(View.INVISIBLE);

			viewMainContent.clearAnimation();
			frmLayoutParams.setMargins(0, 0, 0, 0);
			viewMainContent.setLayoutParams(frmLayoutParams);
		}

		btnAddFavorite.setOnClickListener(m_listenerAddFavorite);
		btnAddPlaylist.setOnClickListener(m_listenerAddPlaylist);
		btnShare.setOnClickListener(m_listenerShare);
		btnSlide.setOnClickListener(m_listenerSlide);

	// {{ TouchListener
		a_convertView.setOnTouchListener(m_listenerTouch);
	// }} TouchListener

		return a_convertView;
	}

	//------------------------------------------------------------------------------
	private void StartAnimRight() {
		AnimationSet		animationSet = new AnimationSet(true);
		TranslateAnimation	translateAnimation = new TranslateAnimation(
			0,
			(int)(150 * m_context.getResources().getDisplayMetrics().density),
			0,
			0
		);
		animationSet.setFillAfter(true);
		animationSet.setDuration(500);
//		animationSet.setInterpolator(new BounceInterpolator());
		animationSet.addAnimation(translateAnimation);
		animationSet.setAnimationListener(
			new AnimationListener() {
				@Override
				public void onAnimationStart(Animation animation) {
				}
				@Override
				public void onAnimationRepeat(Animation animation) {
				}
				@Override
				public void onAnimationEnd(Animation animation) {
					/* This function is called TWICE */
					m_bIsAnimationing = false;
				}
			}
		);
		m_viewCurrAnim = m_listView.getChildAt(m_iPosCurrAnim - m_listView.getFirstVisiblePosition());
		if (m_viewCurrAnim != null) {
			m_bIsAnimationing	= true;
			m_iAnimationType	= Const.ANIMATION_RIGHT;
			m_viewMainContent = m_viewCurrAnim.findViewById(R.id.ID_RELLYT_ITEM_MAIN_CONTENT);
			m_viewMainContent.startAnimation(animationSet);
		}
	}

	//------------------------------------------------------------------------------
	private void StartAnimLeft() {
		AnimationSet		animationSet = new AnimationSet(true);
		TranslateAnimation	translateAnimation = new TranslateAnimation(
			0,
			(int)(-150 * m_context.getResources().getDisplayMetrics().density),
			0,
			0
		);
		animationSet.setFillAfter(true);
		animationSet.setDuration(500);
//		animationSet.setInterpolator(new BounceInterpolator());
		animationSet.addAnimation(translateAnimation);
		animationSet.setAnimationListener(
			new AnimationListener() {
				@Override
				public void onAnimationStart(Animation animation) {
				}
				@Override
				public void onAnimationRepeat(Animation animation) {
				}
				@Override
				public void onAnimationEnd(Animation animation) {
					/* This function is called TWICE */
					m_bIsAnimationing	= false;
					m_iPosCurrAnim = -1;
				}
			}
		);
		if (m_viewCurrAnim != null) {
			Button	btnAddFavorite	= (Button)m_viewCurrAnim.findViewById(R.id.ID_BTN_ADD_FAVORITE);
			Button	btnAddPlaylist	= (Button)m_viewCurrAnim.findViewById(R.id.ID_BTN_ADD_PLAYLIST);
			Button	btnShare		= (Button)m_viewCurrAnim.findViewById(R.id.ID_BTN_SHARE);
			btnAddFavorite.setVisibility(View.INVISIBLE);
			btnAddPlaylist.setVisibility(View.INVISIBLE);
			btnShare.setVisibility(View.INVISIBLE);
			
			m_bIsAnimationing	= true;
			m_iAnimationType	= Const.ANIMATION_LEFT;
			m_viewMainContent = m_viewCurrAnim.findViewById(R.id.ID_RELLYT_ITEM_MAIN_CONTENT);
			m_viewMainContent.startAnimation(animationSet);
		}
	}

	//------------------------------------------------------------------------------

}

//==============================================================================
