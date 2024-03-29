package org.anddev.andengine.entity.layer;

import java.util.Comparator;

import javax.microedition.khronos.opengles.GL10;

import org.anddev.andengine.engine.camera.Camera;
import org.anddev.andengine.entity.IEntity;
import org.anddev.andengine.util.IEntityMatcher;


/**
 * @author Nicolas Gramlich
 * @since 12:47:43 - 08.03.2010
 */
public class FixedCapacityLayer extends BaseLayer {
	// ===========================================================
	// Constants
	// ===========================================================

	// ===========================================================
	// Fields
	// ===========================================================

	private final IEntity[] mEntities;
	private final int mCapacity;
	private int mEntityCount;

	// ===========================================================
	// Constructors
	// ===========================================================

	public FixedCapacityLayer(final int pCapacity) {
		this.mCapacity = pCapacity;
		this.mEntities = new IEntity[pCapacity];
		this.mEntityCount = 0;
	}

	// ===========================================================
	// Getter & Setter
	// ===========================================================

	// ===========================================================
	// Methods for/from SuperClass/Interfaces
	// ===========================================================

	
	public int getEntityCount() {
		return this.mEntityCount;
	}

	
	protected void onManagedDraw(final GL10 pGL, final Camera pCamera) {
		final IEntity[] entities = this.mEntities;
		final int entityCount = this.mEntityCount;
		for(int i = 0; i < entityCount; i++) {
			entities[i].onDraw(pGL, pCamera);
		}
	}

	
	protected void onManagedUpdate(final float pSecondsElapsed) {
		final IEntity[] entities = this.mEntities;
		final int entityCount = this.mEntityCount;
		for(int i = 0; i < entityCount; i++) {
			entities[i].onUpdate(pSecondsElapsed);
		}
	}

	
	public void reset() {
		super.reset();

		final IEntity[] entities = this.mEntities;
		for(int i = this.mEntityCount - 1; i >= 0; i--) {
			entities[i].reset();
		}
	}

	
	public IEntity getEntity(final int pIndex) {
		this.checkIndex(pIndex);

		return this.mEntities[pIndex];
	}

	
	public void clear() {
		final IEntity[] entities = this.mEntities;
		for(int i = this.mEntityCount - 1; i >= 0; i--) {
			entities[i] = null;
		}
		this.mEntityCount = 0;
	}

	
	public void addEntity(final IEntity pEntity) {
		if(this.mEntityCount < this.mCapacity) {
			this.mEntities[this.mEntityCount] = pEntity;
			this.mEntityCount++;
		}
	}

	
	public boolean removeEntity(final IEntity pEntity) {
		return this.removeEntity(this.indexOfEntity(pEntity)) != null;
	}

	
	public IEntity removeEntity(final int pIndex) {
		this.checkIndex(pIndex);

		final IEntity[] entities = this.mEntities;
		final IEntity out = entities[pIndex];
		
		final int lastIndex = this.mEntityCount - 1;
		if(pIndex == lastIndex) {
			this.mEntities[lastIndex] = null;
		} else {
			entities[pIndex] = entities[lastIndex];
			entities[this.mEntityCount] = null;
		}
		this.mEntityCount = lastIndex;
		
		return out;
	}

	
	public boolean removeEntity(final IEntityMatcher pEntityMatcher) {
		final IEntity[] entities = this.mEntities;
		for(int i = entities.length - 1; i >= 0; i--) {
			if(pEntityMatcher.matches(entities[i])) {
				this.removeEntity(i);
				return true;
			}
		}
		return false;
	}

	
	public IEntity findEntity(final IEntityMatcher pEntityMatcher) {
		final IEntity[] entities = this.mEntities;
		for(int i = entities.length - 1; i >= 0; i--) {
			final IEntity entity = entities[i];
			if(pEntityMatcher.matches(entity)) {
				return entity;
			}
		}
		return null;
	}

	private int indexOfEntity(final IEntity pEntity) {
		final IEntity[] entities = this.mEntities;
		for(int i = entities.length - 1; i >= 0; i--) {
			final IEntity entity = entities[i];
			if(entity == pEntity) {
				return i;
			}
		}
		return -1;
	}

	
	public void sortEntities() {
		ZIndexSorter.getInstance().sort(this.mEntities, 0, this.mEntityCount);
	}

	
	public void sortEntities(final Comparator<IEntity> pEntityComparator) {
		ZIndexSorter.getInstance().sort(this.mEntities, 0, this.mEntityCount, pEntityComparator);
	}

	
	public IEntity replaceEntity(final int pIndex, final IEntity pEntity) {
		this.checkIndex(pIndex);

		final IEntity[] entities = this.mEntities;
		final IEntity oldEntity = entities[pIndex];
		entities[pIndex] = pEntity;
		return oldEntity;
	}

	
	public void setEntity(final int pIndex, final IEntity pEntity) {
		this.checkIndex(pIndex);

		if(pIndex == this.mEntityCount) {
			this.addEntity(pEntity);
		} else if(pIndex < this.mEntityCount) {
			this.mEntities[pIndex] = pEntity;
		}
	}

	
	public void swapEntities(final int pEntityIndexA, final int pEntityIndexB) {
		if(pEntityIndexA > this.mEntityCount) {
			throw new IndexOutOfBoundsException("pEntityIndexA was bigger than the EntityCount.");
		}
		if(pEntityIndexA > this.mEntityCount) {
			throw new IndexOutOfBoundsException("pEntityIndexB was bigger than the EntityCount.");
		}
		final IEntity[] entities = this.mEntities;
		final IEntity tmp = entities[pEntityIndexA];
		entities[pEntityIndexA] = entities[pEntityIndexB];
		entities[pEntityIndexB] = tmp;
	}

	// ===========================================================
	// Methods
	// ===========================================================

	private void checkIndex(final int pIndex) {
		if(pIndex < 0 || pIndex >= this.mEntityCount) {
			throw new IndexOutOfBoundsException("Invalid index: " + pIndex + " (Size: " + this.mEntityCount + " | Capacity: " + this.mCapacity + ")");
		}
	}

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
}
