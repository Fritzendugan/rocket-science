package org.anddev.andengine.util.sort;

import java.util.Comparator;
import java.util.List;

/**
 * @author Nicolas Gramlich
 * @since 14:14:31 - 06.08.2010
 * @param <T>
 */
public class InsertionSorter<T> extends Sorter<T> {
	// ===========================================================
	// Constants
	// ===========================================================

	// ===========================================================
	// Fields
	// ===========================================================

	// ===========================================================
	// Constructors
	// ===========================================================

	// ===========================================================
	// Getter & Setter
	// ===========================================================

	// ===========================================================
	// Methods for/from SuperClass/Interfaces
	// ===========================================================

	
	public void sort(final T[] pArray, final int pStart, final int pEnd, final Comparator<T> pComparator) {
		for(int i = pStart + 1; i < pEnd; i++) {
			T current = pArray[i];
			T prev = pArray[i - 1];
			if(pComparator.compare(current, prev) < 0) {
				int j = i;
				do {
					pArray[j--] = prev;
				} while(j > pStart && pComparator.compare(current, prev = pArray[j - 1]) < 0);
				pArray[j] = current;
			}
		}
		return;
	}
	
	
	public void sort(final List<T> pList, final int pStart, final int pEnd, final Comparator<T> pComparator) {
		for(int i = pStart + 1; i < pEnd; i++) {
			T current = pList.get(i);
			T prev = pList.get(i - 1);
			if(pComparator.compare(current, prev) < 0) {
				int j = i;
				do {
					pList.set(j--, prev);
				} while(j > pStart && pComparator.compare(current, prev = pList.get(j - 1)) < 0);
				pList.set(j, current);
			}
		}
		return;
	}

	// ===========================================================
	// Methods
	// ===========================================================

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
}