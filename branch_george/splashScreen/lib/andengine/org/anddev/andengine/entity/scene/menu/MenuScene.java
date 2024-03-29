package org.anddev.andengine.entity.scene.menu;

import java.util.ArrayList;

import org.anddev.andengine.engine.camera.Camera;
import org.anddev.andengine.entity.scene.CameraScene;
import org.anddev.andengine.entity.scene.Scene;
import org.anddev.andengine.entity.scene.Scene.IOnAreaTouchListener;
import org.anddev.andengine.entity.scene.Scene.IOnSceneTouchListener;
import org.anddev.andengine.entity.scene.menu.animator.IMenuAnimator;
import org.anddev.andengine.entity.scene.menu.item.IMenuItem;
import org.anddev.andengine.input.touch.TouchEvent;

import android.view.MotionEvent;

/**
 * @author Nicolas Gramlich
 * @since 20:06:51 - 01.04.2010
 */
public class MenuScene extends CameraScene implements IOnAreaTouchListener, IOnSceneTouchListener {
	// ===========================================================
	// Constants
	// ===========================================================

	// ===========================================================
	// Fields
	// ===========================================================

	private final ArrayList<IMenuItem> mMenuItems = new ArrayList<IMenuItem>();

	private IOnMenuItemClickListener mOnMenuItemClickListener;

	private IMenuAnimator mMenuAnimator = IMenuAnimator.DEFAULT;

	private IMenuItem mSelectedMenuItem;

	// ===========================================================
	// Constructors
	// ===========================================================

	public MenuScene() {
		this(null, null);
	}

	public MenuScene(final IOnMenuItemClickListener pOnMenuItemClickListener) {
		this(null, pOnMenuItemClickListener);
	}

	public MenuScene(final Camera pCamera) {
		this(pCamera, null);
	}

	public MenuScene(final Camera pCamera, final IOnMenuItemClickListener pOnMenuItemClickListener) {
		super(1, pCamera);
		this.mOnMenuItemClickListener = pOnMenuItemClickListener;
		this.setOnSceneTouchListener(this);
		this.setOnAreaTouchListener(this);
	}

	// ===========================================================
	// Getter & Setter
	// ===========================================================

	public IOnMenuItemClickListener getOnMenuItemClickListener() {
		return this.mOnMenuItemClickListener;
	}

	public void setOnMenuItemClickListener(final IOnMenuItemClickListener pOnMenuItemClickListener) {
		this.mOnMenuItemClickListener = pOnMenuItemClickListener;
	}

	public int getMenuItemCount() {
		return this.mMenuItems.size();
	}

	public void addMenuItem(final IMenuItem pMenuItem) {
		this.mMenuItems.add(pMenuItem);
		this.getBottomLayer().addEntity(pMenuItem);
		this.registerTouchArea(pMenuItem);
	}

	
	public MenuScene getChildScene() {
		return (MenuScene)super.getChildScene();
	}

	
	public void setChildScene(final Scene pChildScene, final boolean pModalDraw, final boolean pModalUpdate, final boolean pModalTouch) throws IllegalArgumentException {
		if(pChildScene instanceof MenuScene) {
			super.setChildScene(pChildScene, pModalDraw, pModalUpdate, pModalTouch);
		} else {
			throw new IllegalArgumentException("MenuScene accepts only MenuScenes as a ChildScene.");
		}
	}

	
	public void clearChildScene() {
		if(this.getChildScene() != null) {
			this.getChildScene().reset();
			super.clearChildScene();
		}
	}

	public void setMenuAnimator(final IMenuAnimator pMenuAnimator) {
		this.mMenuAnimator = pMenuAnimator;
	}

	// ===========================================================
	// Methods for/from SuperClass/Interfaces
	// ===========================================================

	
	public boolean onAreaTouched(final TouchEvent pSceneTouchEvent, final ITouchArea pTouchArea, final float pTouchAreaLocalX, final float pTouchAreaLocalY) {
		final IMenuItem menuItem = ((IMenuItem)pTouchArea);

		switch(pSceneTouchEvent.getAction()) {
			case MotionEvent.ACTION_DOWN:
			case MotionEvent.ACTION_MOVE:
				if(this.mSelectedMenuItem != null && this.mSelectedMenuItem != menuItem) {
					this.mSelectedMenuItem.onUnselected();
				}
				this.mSelectedMenuItem = menuItem;
				this.mSelectedMenuItem.onSelected();
				break;
			case MotionEvent.ACTION_UP:
				if(this.mOnMenuItemClickListener != null) {
					menuItem.onUnselected();
					this.mSelectedMenuItem = null;
					return this.mOnMenuItemClickListener.onMenuItemClicked(this, menuItem, pTouchAreaLocalX, pTouchAreaLocalY);
				}
				break;
			case MotionEvent.ACTION_CANCEL:
				menuItem.onUnselected();
				this.mSelectedMenuItem = null;
				break;
		}
		return true;
	}

	
	public boolean onSceneTouchEvent(final Scene pScene, final TouchEvent pSceneTouchEvent) {
		if(this.mSelectedMenuItem != null) {
			this.mSelectedMenuItem.onUnselected();
			this.mSelectedMenuItem = null;
		}
		return false;
	}

	
	public void back() {
		super.back();

		this.reset();
	}

	
	public void reset() {
		super.reset();

		final ArrayList<IMenuItem> menuItems = this.mMenuItems;
		for(int i = menuItems.size() - 1; i >= 0; i--) {
			menuItems.get(i).reset();
		}

		this.prepareAnimations();
	}

	// ===========================================================
	// Methods
	// ===========================================================
	
	public void closeMenuScene() {
		this.back();
	}

	public void buildAnimations() {
		this.prepareAnimations();

		final float cameraHeight = this.mCamera.getHeight();
		final float cameraWidth = this.mCamera.getWidth();
		this.mMenuAnimator.buildAnimations(this.mMenuItems, cameraWidth, cameraHeight);
	}

	public void prepareAnimations() {
		final float cameraHeight = this.mCamera.getHeight();
		final float cameraWidth = this.mCamera.getWidth();
		this.mMenuAnimator.prepareAnimations(this.mMenuItems, cameraWidth, cameraHeight);
	}

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================

	public static interface IOnMenuItemClickListener {
		// ===========================================================
		// Final Fields
		// ===========================================================

		// ===========================================================
		// Methods
		// ===========================================================

		public boolean onMenuItemClicked(final MenuScene pMenuScene, final IMenuItem pMenuItem, final float pMenuItemLocalX, final float pMenuItemLocalY);
	}
}
