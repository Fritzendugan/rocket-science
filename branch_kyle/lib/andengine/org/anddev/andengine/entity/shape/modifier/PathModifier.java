package org.anddev.andengine.entity.shape.modifier;

import org.anddev.andengine.entity.shape.IShape;
import org.anddev.andengine.entity.shape.modifier.ease.IEaseFunction;
import org.anddev.andengine.util.Path;
import org.anddev.andengine.util.modifier.IModifier;
import org.anddev.andengine.util.modifier.SequenceModifier;
import org.anddev.andengine.util.modifier.SequenceModifier.ISubSequenceModifierListener;

/**
 * @author Nicolas Gramlich
 * @since 16:50:02 - 16.06.2010
 */
public class PathModifier extends ShapeModifier {
	// ===========================================================
	// Constants
	// ===========================================================

	// ===========================================================
	// Fields
	// ===========================================================

	private final SequenceModifier<IShape> mSequenceModifier;

	private IPathModifierListener mPathModifierListener;

	private final Path mPath;

	// ===========================================================
	// Constructors
	// ===========================================================

	public PathModifier(final float pDuration, final Path pPath) {
		this(pDuration, pPath, null, IEaseFunction.DEFAULT);
	}

	public PathModifier(final float pDuration, final Path pPath, final IEaseFunction pEaseFunction) {
		this(pDuration, pPath, null, pEaseFunction);
	}

	public PathModifier(final float pDuration, final Path pPath, final IShapeModifierListener pShapeModiferListener) {
		this(pDuration, pPath, pShapeModiferListener, null, IEaseFunction.DEFAULT);
	}

	public PathModifier(final float pDuration, final Path pPath, final IShapeModifierListener pShapeModiferListener, final IEaseFunction pEaseFunction) {
		this(pDuration, pPath, pShapeModiferListener, null, pEaseFunction);
	}

	public PathModifier(final float pDuration, final Path pPath, final IShapeModifierListener pShapeModiferListener, final IPathModifierListener pPathModifierListener) throws IllegalArgumentException {
		this(pDuration, pPath, pShapeModiferListener, pPathModifierListener, IEaseFunction.DEFAULT);
	}

	public PathModifier(final float pDuration, final Path pPath, final IShapeModifierListener pShapeModiferListener, final IPathModifierListener pPathModifierListener, final IEaseFunction pEaseFunction) throws IllegalArgumentException {
		final int pathSize = pPath.getSize();

		if (pathSize < 2) {
			throw new IllegalArgumentException("Path needs at least 2 waypoints!");
		}

		this.mPath = pPath;
		this.mModifierListener = pShapeModiferListener;
		this.mPathModifierListener = pPathModifierListener;

		final MoveModifier[] moveModifiers = new MoveModifier[pathSize - 1];

		final float[] coordinatesX = pPath.getCoordinatesX();
		final float[] coordinatesY = pPath.getCoordinatesY();

		final float velocity = pPath.getLength() / pDuration;

		final int modifierCount = moveModifiers.length;
		for(int i = 0; i < modifierCount; i++) {
			final float duration = pPath.getSegmentLength(i) / velocity;

			if(i == 0) {
				/* When the first modifier is initialized, we have to
				 * fire onWaypointPassed of mPathModifierListener. */
				moveModifiers[i] = new MoveModifier(duration, coordinatesX[i], coordinatesX[i + 1], coordinatesY[i], coordinatesY[i + 1], null, pEaseFunction){
					
					protected void onManagedInitialize(final IShape pShape) {
						super.onManagedInitialize(pShape);
						if(PathModifier.this.mPathModifierListener != null) {
							PathModifier.this.mPathModifierListener.onWaypointPassed(PathModifier.this, pShape, 0);
						}
					}
				};
			} else {
				moveModifiers[i] = new MoveModifier(duration, coordinatesX[i], coordinatesX[i + 1], coordinatesY[i], coordinatesY[i + 1], null, pEaseFunction);
			}
		}


		/* Create a new SequenceModifier and register the listeners that
		 * call through to mShapeModifierListener and mPathModifierListener. */
		this.mSequenceModifier = new SequenceModifier<IShape>(
				new IShapeModifierListener() {
					
					public void onModifierFinished(final IModifier<IShape> pShapeModifier, final IShape pShape) {
						if(PathModifier.this.mPathModifierListener != null) {
							PathModifier.this.mPathModifierListener.onWaypointPassed(PathModifier.this, pShape, modifierCount);
						}
						if(PathModifier.this.mModifierListener != null) {
							PathModifier.this.mModifierListener.onModifierFinished(PathModifier.this, pShape);
						}
					}
				},
				new ISubSequenceModifierListener<IShape>() {
					
					public void onSubSequenceFinished(final IModifier<IShape> pShapeModifier, final IShape pShape, final int pIndex) {
						if(PathModifier.this.mPathModifierListener != null) {
							PathModifier.this.mPathModifierListener.onWaypointPassed(PathModifier.this, pShape, pIndex);
						}
					}
				},
				moveModifiers
		);
	}

	protected PathModifier(final PathModifier pPathModifier) {
		this.mPath = pPathModifier.mPath.clone();
		this.mSequenceModifier = pPathModifier.mSequenceModifier.clone();
	}

	
	public PathModifier clone() {
		return new PathModifier(this);
	}

	// ===========================================================
	// Getter & Setter
	// ===========================================================

	public Path getPath() {
		return this.mPath;
	}

	// ===========================================================
	// Methods for/from SuperClass/Interfaces
	// ===========================================================

	
	public boolean isFinished() {
		return this.mSequenceModifier.isFinished();
	}

	
	public float getDuration() {
		return this.mSequenceModifier.getDuration();
	}

	public IPathModifierListener getPathModifierListener() {
		return this.mPathModifierListener;
	}

	public void setPathModifierListener(final IPathModifierListener pPathModifierListener) {
		this.mPathModifierListener = pPathModifierListener;
	}

	
	public void reset() {
		this.mSequenceModifier.reset();
	}

	
	public void onUpdate(final float pSecondsElapsed, final IShape pShape) {
		this.mSequenceModifier.onUpdate(pSecondsElapsed, pShape);
	}

	// ===========================================================
	// Methods
	// ===========================================================

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================

	public static interface IPathModifierListener {
		// ===========================================================
		// Constants
		// ===========================================================

		// ===========================================================
		// Fields
		// ===========================================================

		public void onWaypointPassed(final PathModifier pPathModifier, final IShape pShape, final int pWaypointIndex);
	}
}
