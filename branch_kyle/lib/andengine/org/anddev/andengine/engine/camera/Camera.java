package org.anddev.andengine.engine.camera;

import static org.anddev.andengine.util.constants.Constants.VERTEX_INDEX_X;
import static org.anddev.andengine.util.constants.Constants.VERTEX_INDEX_Y;

import javax.microedition.khronos.opengles.GL10;

import org.anddev.andengine.collision.BaseCollisionChecker;
import org.anddev.andengine.engine.camera.hud.HUD;
import org.anddev.andengine.engine.handler.IUpdateHandler;
import org.anddev.andengine.entity.shape.IShape;
import org.anddev.andengine.entity.shape.RectangularShape;
import org.anddev.andengine.entity.shape.Shape;
import org.anddev.andengine.input.touch.TouchEvent;
import org.anddev.andengine.opengl.util.GLHelper;
import org.anddev.andengine.util.MathUtils;

import android.opengl.GLU;

/**
 * @author Nicolas Gramlich
 * @since 10:24:18 - 25.03.2010
 */
public class Camera implements IUpdateHandler {
	// ===========================================================
	// Constants
	// ===========================================================

	protected static final float[] VERTICES_TOUCH_TMP = new float[2];

	// ===========================================================
	// Fields
	// ===========================================================

	private float mMinX;
	private float mMaxX;
	private float mMinY;
	private float mMaxY;

	private HUD mHUD;

	private IShape mChaseShape;

	protected float mRotation = 0;
	protected float mCameraSceneRotation = 0;

	// ===========================================================
	// Constructors
	// ===========================================================

	public Camera(final float pX, final float pY, final float pWidth, final float pHeight) {
		this.mMinX = pX;
		this.mMaxX = pX + pWidth;
		this.mMinY = pY;
		this.mMaxY = pY + pHeight;
	}

	// ===========================================================
	// Getter & Setter
	// ===========================================================

	public float getMinX() {
		return this.mMinX;
	}

	public float getMaxX() {
		return this.mMaxX;
	}

	public float getMinY() {
		return this.mMinY;
	}

	public float getMaxY() {
		return this.mMaxY;
	}

	public float getWidth() {
		return this.mMaxX - this.mMinX;
	}

	public float getHeight() {
		return this.mMaxY - this.mMinY;
	}

	public float getCenterX() {
		final float minX = this.mMinX;
		return minX + (this.mMaxX - minX) * 0.5f;
	}

	public float getCenterY() {
		final float minY = this.mMinY;
		return minY + (this.mMaxY - minY) * 0.5f;
	}

	public void setCenter(final float pCenterX, final float pCenterY) {
		final float dX = pCenterX - this.getCenterX();
		final float dY = pCenterY - this.getCenterY();

		this.mMinX += dX;
		this.mMaxX += dX;
		this.mMinY += dY;
		this.mMaxY += dY;
	}

	public void offsetCenter(final float pX, final float pY) {
		this.setCenter(this.getCenterX() + pX, this.getCenterY() + pY);
	}

	public HUD getHUD() {
		return this.mHUD;
	}

	public void setHUD(final HUD pHUD) {
		this.mHUD = pHUD;
		pHUD.setCamera(this);
	}

	public boolean hasHUD() {
		return this.mHUD != null;
	}

	public void setChaseShape(final IShape pChaseShape) {
		this.mChaseShape = pChaseShape;
	}

	public float getRotation() {
		return this.mRotation;
	}

	public void setRotation(final float pRotation) {
		this.mRotation = pRotation;
	}

	public float getCameraSceneRotation() {
		return this.mCameraSceneRotation;
	}

	public void setCameraSceneRotation(final float pCameraSceneRotation) {
		this.mCameraSceneRotation = pCameraSceneRotation;
	}

	// ===========================================================
	// Methods for/from SuperClass/Interfaces
	// ===========================================================

	
	public void onUpdate(final float pSecondsElapsed) {
		if(this.mHUD != null) {
			this.mHUD.onUpdate(pSecondsElapsed);
		}

		if(this.mChaseShape != null) {
			final float[] centerCoordinates = this.mChaseShape.getSceneCenterCoordinates();
			this.setCenter(centerCoordinates[VERTEX_INDEX_X], centerCoordinates[VERTEX_INDEX_Y]);
		}
	}

	
	public void reset() {

	}

	// ===========================================================
	// Methods
	// ===========================================================

	public void centerShapeInCamera(final Shape pShape) {
		pShape.setPosition((this.getWidth() - pShape.getWidth()) * 0.5f, (this.getHeight() - pShape.getHeight()) * 0.5f);
	}

	public void centerShapeInCameraHorizontally(final Shape pShape) {
		pShape.setPosition((this.getWidth() - pShape.getWidth()) * 0.5f, pShape.getY());
	}

	public void centerShapeInCameraVertically(final Shape pShape) {
		pShape.setPosition(pShape.getX(), (this.getHeight() - pShape.getHeight()) * 0.5f);
	}

	public void onDrawHUD(final GL10 pGL) {
		if(this.mHUD != null) {
			this.mHUD.onDraw(pGL, this);
		}
	}

	public boolean isRectangularShapeVisible(final RectangularShape pRectangularShape) {
		final float otherLeft = pRectangularShape.getX();
		final float otherTop = pRectangularShape.getY();
		final float otherRight = pRectangularShape.getWidthScaled() + otherLeft;
		final float otherBottom = pRectangularShape.getHeightScaled() + otherTop;

		// TODO Should also use RectangularShapeCollisionChecker
		return BaseCollisionChecker.checkAxisAlignedRectangleCollision(this.getMinX(), this.getMinY(), this.getMaxX(), this.getMaxY(), otherLeft, otherTop, otherRight, otherBottom);
	}

	public void onApplyMatrix(final GL10 pGL) {
		GLHelper.setProjectionIdentityMatrix(pGL);

		GLU.gluOrtho2D(pGL, this.getMinX(), this.getMaxX(), this.getMaxY(), this.getMinY());

		final float rotation = this.mRotation;
		if(rotation != 0) {
			this.applyRotation(pGL, this.getCenterX(), this.getCenterY(), rotation);
		}
	}

	public void onApplyPositionIndependentMatrix(final GL10 pGL) {
		GLHelper.setProjectionIdentityMatrix(pGL);

		final float width = this.mMaxX - this.mMinX;
		final float height = this.mMaxY - this.mMinY;

		GLU.gluOrtho2D(pGL, 0, width, height, 0);

		final float rotation = this.mRotation;
		if(rotation != 0) {
			this.applyRotation(pGL, width * 0.5f, height * 0.5f, rotation);
		}
	}

	public void onApplyCameraSceneMatrix(final GL10 pGL) {
		GLHelper.setProjectionIdentityMatrix(pGL);

		final float width = this.mMaxX - this.mMinX;
		final float height = this.mMaxY - this.mMinY;

		GLU.gluOrtho2D(pGL, 0, width, height, 0);

		final float cameraSceneRotation = this.mCameraSceneRotation;
		if(cameraSceneRotation != 0) {
			this.applyRotation(pGL, width * 0.5f, height * 0.5f, cameraSceneRotation);
		}
	}

	private void applyRotation(final GL10 pGL, final float pRotationCenterX, final float pRotationCenterY, final float pAngle) {
		pGL.glTranslatef(pRotationCenterX, pRotationCenterY, 0);
		pGL.glRotatef(pAngle, 0, 0, 1);
		pGL.glTranslatef(-pRotationCenterX, -pRotationCenterY, 0);
	}

	public void convertSceneToCameraSceneTouchEvent(final TouchEvent pSceneTouchEvent) {
		this.unapplySceneRotation(pSceneTouchEvent);

		this.applySceneToCameraSceneOffset(pSceneTouchEvent);

		this.applyCameraSceneRotation(pSceneTouchEvent);
	}

	public void convertCameraSceneToSceneTouchEvent(final TouchEvent pCameraSceneTouchEvent) {
		this.unapplyCameraSceneRotation(pCameraSceneTouchEvent);

		this.unapplySceneToCameraSceneOffset(pCameraSceneTouchEvent);

		this.applySceneRotation(pCameraSceneTouchEvent);
	}

	protected void applySceneToCameraSceneOffset(final TouchEvent pSceneTouchEvent) {
		pSceneTouchEvent.offset(-this.mMinX, -this.mMinY);
	}

	protected void unapplySceneToCameraSceneOffset(final TouchEvent pCameraSceneTouchEvent) {
		pCameraSceneTouchEvent.offset(this.mMinX, this.mMinY);
	}

	private void applySceneRotation(final TouchEvent pCameraSceneTouchEvent) {
		final float rotation = -this.mRotation;
		if(rotation != 0) {
			VERTICES_TOUCH_TMP[0] = pCameraSceneTouchEvent.getX();
			VERTICES_TOUCH_TMP[0 + VERTEX_INDEX_Y] = pCameraSceneTouchEvent.getY();

			MathUtils.rotateAroundCenter(VERTICES_TOUCH_TMP, rotation, this.getCenterX(), this.getCenterY());

			pCameraSceneTouchEvent.set(VERTICES_TOUCH_TMP[0], VERTICES_TOUCH_TMP[0 + VERTEX_INDEX_Y]);
		}
	}

	private void unapplySceneRotation(final TouchEvent pSceneTouchEvent) {
		final float rotation = this.mRotation;

		if(rotation != 0) {
			VERTICES_TOUCH_TMP[0] = pSceneTouchEvent.getX();
			VERTICES_TOUCH_TMP[0 + VERTEX_INDEX_Y] = pSceneTouchEvent.getY();

			MathUtils.revertRotateAroundCenter(VERTICES_TOUCH_TMP, rotation, this.getCenterX(), this.getCenterY());

			pSceneTouchEvent.set(VERTICES_TOUCH_TMP[0], VERTICES_TOUCH_TMP[0 + VERTEX_INDEX_Y]);
		}
	}

	private void applyCameraSceneRotation(final TouchEvent pSceneTouchEvent) {
		final float cameraSceneRotation = -this.mCameraSceneRotation;

		if(cameraSceneRotation != 0) {
			VERTICES_TOUCH_TMP[0] = pSceneTouchEvent.getX();
			VERTICES_TOUCH_TMP[0 + VERTEX_INDEX_Y] = pSceneTouchEvent.getY();

			MathUtils.rotateAroundCenter(VERTICES_TOUCH_TMP, cameraSceneRotation, (this.mMaxX - this.mMinX) * 0.5f, (this.mMaxY - this.mMinY) * 0.5f);

			pSceneTouchEvent.set(VERTICES_TOUCH_TMP[0], VERTICES_TOUCH_TMP[0 + VERTEX_INDEX_Y]);
		}
	}

	private void unapplyCameraSceneRotation(final TouchEvent pCameraSceneTouchEvent) {
		final float cameraSceneRotation = -this.mCameraSceneRotation;

		if(cameraSceneRotation != 0) {
			VERTICES_TOUCH_TMP[0] = pCameraSceneTouchEvent.getX();
			VERTICES_TOUCH_TMP[0 + VERTEX_INDEX_Y] = pCameraSceneTouchEvent.getY();

			MathUtils.revertRotateAroundCenter(VERTICES_TOUCH_TMP, cameraSceneRotation, (this.mMaxX - this.mMinX) * 0.5f, (this.mMaxY - this.mMinY) * 0.5f);

			pCameraSceneTouchEvent.set(VERTICES_TOUCH_TMP[0], VERTICES_TOUCH_TMP[0 + VERTEX_INDEX_Y]);
		}
	}

	public void convertSurfaceToSceneTouchEvent(final TouchEvent pSurfaceTouchEvent, final int pSurfaceWidth, final int pSurfaceHeight) {
		final float relativeX;
		final float relativeY;

		final float rotation = this.mRotation;
		if(rotation == 0) {
			relativeX = pSurfaceTouchEvent.getX() / pSurfaceWidth;
			relativeY = pSurfaceTouchEvent.getY() / pSurfaceHeight;
		} else if(rotation == 180) {
			relativeX = 1 - (pSurfaceTouchEvent.getX() / pSurfaceWidth);
			relativeY = 1 - (pSurfaceTouchEvent.getY() / pSurfaceHeight);
		} else {
			VERTICES_TOUCH_TMP[0] = pSurfaceTouchEvent.getX();
			VERTICES_TOUCH_TMP[0 + VERTEX_INDEX_Y] = pSurfaceTouchEvent.getY();

			MathUtils.rotateAroundCenter(VERTICES_TOUCH_TMP, rotation, pSurfaceWidth / 2, pSurfaceHeight / 2);

			relativeX = VERTICES_TOUCH_TMP[0] / pSurfaceWidth;
			relativeY = VERTICES_TOUCH_TMP[0 + VERTEX_INDEX_Y] / pSurfaceHeight;
		}

		this.convertAxisAlignedSurfaceToSceneTouchEvent(pSurfaceTouchEvent, relativeX, relativeY);
	}

	private void convertAxisAlignedSurfaceToSceneTouchEvent(final TouchEvent pSurfaceTouchEvent, final float pRelativeX, final float pRelativeY) {
		final float minX = this.getMinX();
		final float maxX = this.getMaxX();
		final float minY = this.getMinY();
		final float maxY = this.getMaxY();

		final float x = minX + pRelativeX * (maxX - minX);
		final float y = minY + pRelativeY * (maxY - minY);

		pSurfaceTouchEvent.set(x, y);
	}

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
}
