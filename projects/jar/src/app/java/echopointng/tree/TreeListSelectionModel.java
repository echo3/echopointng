package echopointng.tree;
/* 
 * This file is part of the Echo Point Project.  This project is a collection
 * of Components that have extended the Echo Web Application Framework.
 *
 * Version: MPL 1.1/GPL 2.0/LGPL 2.1
 *
 * The contents of this file are subject to the Mozilla Public License Version
 * 1.1 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * http://www.mozilla.org/MPL/
 *
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License
 * for the specific language governing rights and limitations under the
 * License.
 *
 * Alternatively, the contents of this file may be used under the terms of
 * either the GNU General Public License Version 2 or later (the "GPL"), or
 * the GNU Lesser General Public License Version 2.1 or later (the "LGPL"),
 * in which case the provisions of the GPL or the LGPL are applicable instead
 * of those above. If you wish to allow use of your version of this file only
 * under the terms of either the GPL or the LGPL, and not to allow others to
 * use your version of this file under the terms of the MPL, indicate your
 * decision by deleting the provisions above and replace them with the notice
 * and other provisions required by the GPL or the LGPL. If you do not delete
 * the provisions above, a recipient may use your version of this file under
 * the terms of any one of the MPL, the GPL or the LGPL.
 */

 /*
 * The design paradigm and class name used within have been taken directly from
 * the java.swing package has been retro-fitted to work with the NextApp Echo web framework.
 *
 * This file was made part of the EchoPoint project on the 25/07/2002.
 *
 */

import java.util.BitSet;

import javax.swing.event.ListSelectionListener;

import nextapp.echo2.app.event.ChangeEvent;
import nextapp.echo2.app.event.ChangeListener;
import nextapp.echo2.app.event.EventListenerList;
import nextapp.echo2.app.list.ListSelectionModel;
;

/**
 * Default data model for tree list selections. 
 * <p>
 */

public class TreeListSelectionModel implements ListSelectionModel, java.io.Serializable {

	public static final int SINGLE_SELECTION 			= 1;
	public static final int SINGLE_INTERVAL_SELECTION 	= 2;
	public static final int MULTIPLE_INTERVAL_SELECTION = 4;
	
    private static final int MIN = -1;
    private static final int MAX = Integer.MAX_VALUE;
    private int selectionMode = MULTIPLE_INTERVAL_SELECTION;
    private int minIndex = MAX;
    private int maxIndex = MIN;
    private int anchorIndex = -1;
    private int leadIndex = -1;
    private int firstAdjustedIndex = MAX;
    private int lastAdjustedIndex = MIN;
    private boolean isAdjusting = false;

    private int firstChangedIndex = MAX;
    private int lastChangedIndex = MIN;

    private BitSet value = new BitSet(32);
    protected EventListenerList listenerList = new EventListenerList();

    protected boolean leadAnchorNotificationEnabled = true;

    /**
	 * @see nextapp.echo2.app.list.ListSelectionModel#addChangeListener(nextapp.echo2.app.event.ChangeListener)
	 */
	public void addChangeListener(ChangeListener l) {
        listenerList.addListener(ChangeListener.class, l);
	}
	
     public void addSelectionInterval(int index0, int index1) {
        if (index0 == -1 || index1 == -1) {
            return;
        }

        if (getSelectionMode() != MULTIPLE_INTERVAL_SELECTION) {
            setSelectionInterval(index0, index1);
            return;
        }

        updateLeadAnchorIndices(index0, index1);

        int clearMin = MAX;
        int clearMax = MIN;
        int setMin = Math.min(index0, index1);
        int setMax = Math.max(index0, index1);
        changeSelection(clearMin, clearMax, setMin, setMax);
    }
    /*   Change the selection with the effect of first clearing the values
    	*   in the inclusive range [clearMin, clearMax] then setting the values
    	*   in the inclusive range [setMin, setMax]. Do this in one pass so
    	*   that no values are cleared if they would later be set.
    	*/
    private void changeSelection(int clearMin, int clearMax, int setMin, int setMax) {
        changeSelection(clearMin, clearMax, setMin, setMax, true);
    }
    private void changeSelection(int clearMin, int clearMax, int setMin, int setMax, boolean clearFirst) {
        for (int i = Math.min(setMin, clearMin); i <= Math.max(setMax, clearMax); i++) {

            boolean shouldClear = contains(clearMin, clearMax, i);
            boolean shouldSet = contains(setMin, setMax, i);

            if (shouldSet && shouldClear) {
                if (clearFirst) {
                    shouldClear = false;
                } else {
                    shouldSet = false;
                }
            }

            if (shouldSet) {
                set(i);
            }
            if (shouldClear) {
                clear(i);
            }
        }
        fireValueChanged();
    }
    // Clear the state at this index and update all relevant state.
    private void clear(int r) {
        if (!value.get(r)) {
            return;
        }
        value.clear(r);
        markAsDirty(r);

        // Update minimum and maximum indices
        /*
           If (r > minIndex) the minimum has not changed.
           The case (r < minIndex) is not possible because r'th value was set.
           We only need to check for the case when lowest entry has been cleared,
           and in this case we need to search for the first value set above it.
        */
        if (r == minIndex) {
            for (minIndex = minIndex + 1; minIndex <= maxIndex; minIndex++) {
                if (value.get(minIndex)) {
                    break;
                }
            }
        }
        /*
           If (r < maxIndex) the maximum has not changed.
           The case (r > maxIndex) is not possible because r'th value was set.
           We only need to check for the case when highest entry has been cleared,
           and in this case we need to search for the first value set below it.
        */
        if (r == maxIndex) {
            for (maxIndex = maxIndex - 1; minIndex <= maxIndex; maxIndex--) {
                if (value.get(maxIndex)) {
                    break;
                }
            }
        }
        /* Performance note: This method is called from inside a loop in
           changeSelection() but we will only iterate in the loops
           above on the basis of one iteration per deselected cell - in total.
           Ie. the next time this method is called the work of the previous
           deselection will not be repeated.
        
           We also don't need to worry about the case when the min and max
           values are in their unassigned states. This cannot happen because
           this method's initial check ensures that the selection was not empty
           and therefore that the minIndex and maxIndex had 'real' values.
        
           If we have cleared the whole selection, set the minIndex and maxIndex
           to their cannonical values so that the next set command always works
           just by using Math.min and Math.max.
        */
        if (isSelectionEmpty()) {
            minIndex = MAX;
            maxIndex = MIN;
        }
    }
    // implements javax.swing.ListSelectionModel
    public void clearSelection() {
        removeSelectionInterval(minIndex, maxIndex);
    }
    private boolean contains(int a, int b, int i) {
        return (i >= a) && (i <= b);
    }
    private void fireValueChanged() {
        if (lastAdjustedIndex == MIN) {
            return;
        }
        /* If getValueAdjusting() is true, (eg. during a drag opereration) 
         * record the bounds of the changes so that, when the drag finishes (and 
         * setValueAdjusting(false) is called) we can post a single event 
         * with bounds covering all of these individual adjustments.  
         */
        if (getValueIsAdjusting()) {
            firstChangedIndex = Math.min(firstChangedIndex, firstAdjustedIndex);
            lastChangedIndex = Math.max(lastChangedIndex, lastAdjustedIndex);
        }
        /* Change the values before sending the event to the
         * listeners in case the event causes a listener to make
         * another change to the selection.
         */
        int oldFirstAdjustedIndex = firstAdjustedIndex;
        int oldLastAdjustedIndex = lastAdjustedIndex;
        firstAdjustedIndex = MAX;
        lastAdjustedIndex = MIN;

        fireValueChanged(oldFirstAdjustedIndex, oldLastAdjustedIndex);
    }
    /**
     * Notify ListSelectionListeners that the value of the selection,
     * in the closed interval firstIndex,lastIndex, has changed.
     */
    protected void fireValueChanged(int firstIndex, int lastIndex) {
        fireValueChanged(firstIndex, lastIndex, getValueIsAdjusting());
    }
    /**
     * @param firstIndex The first index in the interval.
     * @param lastIndex The last index in the interval.
     * @param isAdjusting True if this is the final change in a series of them.
     */
    protected void fireValueChanged(int firstIndex, int lastIndex, boolean isAdjusting) {
    	ChangeEvent e = null;
		Object[] listeners = listenerList.getListeners(ListSelectionListener.class);
	    for (int i = 0; i < listeners.length; i++){
			if (e == null) {
				e = new ChangeEvent(this);
       		}
			((ChangeListener) listeners[i]).stateChanged(e);
	    }
    }
    /**
     * Notify listeners that we have ended a series of adjustments. 
     */
    protected void fireValueChanged(boolean isAdjusting) {
        if (lastChangedIndex == MIN) {
            return;
        }
        /* Change the values before sending the event to the
         * listeners in case the event causes a listener to make
         * another change to the selection.
         */
        int oldFirstChangedIndex = firstChangedIndex;
        int oldLastChangedIndex = lastChangedIndex;
        firstChangedIndex = MAX;
        lastChangedIndex = MIN;
        fireValueChanged(oldFirstChangedIndex, oldLastChangedIndex, isAdjusting);
    }
    public int getAnchorSelectionIndex() {
        return anchorIndex;
    }
    public int getLeadSelectionIndex() {
        return leadIndex;
    }
    public int getMaxSelectedIndex() {
        return maxIndex;
    }
    public int getMinSelectedIndex() {
        return isSelectionEmpty() ? -1 : minIndex;
    }
    public int getSelectionMode() {
        return selectionMode;
    }
    public boolean getValueIsAdjusting() {
        return isAdjusting;
    }
    /**
     * Insert length indices beginning before/after index. If the value 
     * at index is itself selected, set all of the newly inserted 
     * items, otherwise leave them unselected. This method is typically
     * called to sync the selection model with a corresponding change
     * in the data model.
     */
    public void insertIndexInterval(int index, int length, boolean before) {
        /* The first new index will appear at insMinIndex and the last
         * one will appear at insMaxIndex
         */
        int insMinIndex = (before) ? index : index + 1;
        int insMaxIndex = (insMinIndex + length) - 1;

        /* Right shift the entire bitset by length, beginning with
         * index-1 if before is true, index+1 if it's false (i.e. with
         * insMinIndex).
         */
        for (int i = maxIndex; i >= insMinIndex; i--) {
            setState(i + length, value.get(i));
        }

        /* Initialize the newly inserted indices.
         */
        boolean setInsertedValues = value.get(index);
        for (int i = insMinIndex; i <= insMaxIndex; i++) {
            setState(i, setInsertedValues);
        }
    }
    /**
     * Returns the value of the leadAnchorNotificationEnabled flag.
     * When leadAnchorNotificationEnabled is true the model
     * generates notification events with bounds that cover all the changes to
     * the selection plus the changes to the lead and anchor indices.
     * Setting the flag to false causes a norrowing of the event's bounds to
     * include only the elements that have been selected or deselected since
     * the last change. Either way, the model continues to maintain the lead
     * and anchor variables internally. The default is true.
     */
    public boolean isLeadAnchorNotificationEnabled() {
        return leadAnchorNotificationEnabled;
    }
    public boolean isSelectedIndex(int index) {
        return ((index < minIndex) || (index > maxIndex)) ? false : value.get(index);
    }
    public boolean isSelectionEmpty() {
        return (minIndex > maxIndex);
    }
    // Update first and last change indices
    private void markAsDirty(int r) {
        firstAdjustedIndex = Math.min(firstAdjustedIndex, r);
        lastAdjustedIndex = Math.max(lastAdjustedIndex, r);
    }
    /**
     * Remove the indices in the interval index0,index1 (inclusive) from
     * the selection model.  This is typically called to sync the selection
     * model width a corresponding change in the data model.  Note
     * that (as always) index0 need not be <= index1.
     */
    public void removeIndexInterval(int index0, int index1) {
        int rmMinIndex = Math.min(index0, index1);
        int rmMaxIndex = Math.max(index0, index1);
        int gapLength = (rmMaxIndex - rmMinIndex) + 1;

        /* Shift the entire bitset to the left to close the index0, index1
         * gap.
         */
        for (int i = rmMinIndex; i <= maxIndex; i++) {
            setState(i, value.get(i + gapLength));
        }
    }
     /**
	 * @see nextapp.echo2.app.list.ListSelectionModel#removeChangeListener(nextapp.echo2.app.event.ChangeListener)
	 */
	public void removeChangeListener(ChangeListener l) {
        listenerList.removeListener(ChangeListener.class, l);
	}
    public void removeSelectionInterval(int index0, int index1) {
        if (index0 == -1 || index1 == -1) {
            return;
        }

        updateLeadAnchorIndices(index0, index1);

        int clearMin = Math.min(index0, index1);
        int clearMax = Math.max(index0, index1);
        int setMin = MAX;
        int setMax = MIN;

        // If the removal would produce to two disjoint selections in a mode 
        // that only allows one, extend the removal to the end of the selection. 
        if (getSelectionMode() != MULTIPLE_INTERVAL_SELECTION && clearMin > minIndex && clearMax < maxIndex) {
            clearMax = maxIndex;
        }

        changeSelection(clearMin, clearMax, setMin, setMax);
    }
    // Set the state at this index and update all relevant state.
    private void set(int r) {
        if (value.get(r)) {
            return;
        }
        value.set(r);
        markAsDirty(r);

        // Update minimum and maximum indices
        minIndex = Math.min(minIndex, r);
        maxIndex = Math.max(maxIndex, r);
    }
    /**
     * Set the anchor selection index, leaving all selection values unchanged. 
     * If leadAnchorNotificationEnabled is true, send a notification covering 
     * the old and new anchor cells. 
     *
     */
    public void setAnchorSelectionIndex(int anchorIndex) {
        updateLeadAnchorIndices(anchorIndex, this.leadIndex);
        this.anchorIndex = anchorIndex;
        fireValueChanged();
    }
    /**
     * Sets the value of the leadAnchorNotificationEnabled flag.
     */
    public void setLeadAnchorNotificationEnabled(boolean flag) {
        leadAnchorNotificationEnabled = flag;
    }
    /**
     * Set the lead selection index, ensuring that values between the 
     * anchor and the new lead are either all selected or all deselected. 
     * If the value at the anchor index is selected, first clear all the 
     * values in the range [anchor, oldLeadIndex], then select all the values 
     * values in the range [anchor, newLeadIndex], where oldLeadIndex is the old 
     * leadIndex and newLeadIndex is the new one. 
     * <p> 
     * If the value at the anchor index is not selected, do the same thing in reverse, 
     * selecting values in the old range and deslecting values in the new one. 
     * <p>
     * Generate a single event for this change and notify all listeners. 
     * For the purposes of generating minimal bounds in this event, do the 
     * operation in a single pass; that way the first and last index inside the 
     * ListSelectionEvent that is broadcast will refer to cells that actually 
     * changed value because of this method. If, instead, this operation were 
     * done in two steps the effect on the selection state would be the same 
     * but two events would be generated and the bounds around the changed values 
     * would be wider, including cells that had been first cleared only 
     * to later be set. 
     * <p>
     * This method can be used in the mouseDragged() method of a UI class 
     * to extend a selection.  
     *
     */
    public void setLeadSelectionIndex(int leadIndex) {
        int anchorIndex = this.anchorIndex;

        if ((anchorIndex == -1) || (leadIndex == -1)) {
            return;
        }

        if (this.leadIndex == -1) {
            this.leadIndex = leadIndex;
        }

        if (getSelectionMode() == SINGLE_SELECTION) {
            anchorIndex = leadIndex;
        }

        int oldMin = Math.min(this.anchorIndex, this.leadIndex);
        int oldMax = Math.max(this.anchorIndex, this.leadIndex);
        int newMin = Math.min(anchorIndex, leadIndex);
        int newMax = Math.max(anchorIndex, leadIndex);
        if (value.get(this.anchorIndex)) {
            changeSelection(oldMin, oldMax, newMin, newMax);
        } else {
            changeSelection(newMin, newMax, oldMin, oldMax, false);
        }
        this.anchorIndex = anchorIndex;
        this.leadIndex = leadIndex;
    }
/**
 */
public void setSelectedIndex(int index, boolean selected) {
	setSelectionInterval(index,index);
}
    public void setSelectionInterval(int index0, int index1) {
        if (index0 == -1 || index1 == -1) {
            return;
        }

        if (getSelectionMode() == SINGLE_SELECTION) {
            index0 = index1;
        }

        updateLeadAnchorIndices(index0, index1);

        int clearMin = minIndex;
        int clearMax = maxIndex;
        int setMin = Math.min(index0, index1);
        int setMax = Math.max(index0, index1);
        changeSelection(clearMin, clearMax, setMin, setMax);
    }
    public void setSelectionMode(int selectionMode) {
        switch (selectionMode) {
            case SINGLE_SELECTION :
            case SINGLE_INTERVAL_SELECTION :
            case MULTIPLE_INTERVAL_SELECTION :
                this.selectionMode = selectionMode;
                break;
            default :
                throw new IllegalArgumentException("invalid selectionMode");
        }
    }
    private void setState(int index, boolean state) {
        if (state) {
            set(index);
        } else {
            clear(index);
        }
    }
    public void setValueIsAdjusting(boolean isAdjusting) {
        if (isAdjusting != this.isAdjusting) {
            this.isAdjusting = isAdjusting;
            this.fireValueChanged(isAdjusting);
        }
    }
    /**
     * Returns a string that displays and identifies this
     * object's properties.
     *
     * @return a String representation of this object
     */
    public String toString() {
        String s = ((getValueIsAdjusting()) ? "~" : "=") + value.toString();
        return getClass().getName() + " " + Integer.toString(hashCode()) + " " + s;
    }
    private void updateLeadAnchorIndices(int anchorIndex, int leadIndex) {
        if (leadAnchorNotificationEnabled) {
            if (this.anchorIndex != anchorIndex) {
                if (this.anchorIndex != -1) { // The unassigned state.
                    markAsDirty(this.anchorIndex);
                }
                markAsDirty(anchorIndex);
            }

            if (this.leadIndex != leadIndex) {
                if (this.leadIndex != -1) { // The unassigned state.
                    markAsDirty(this.leadIndex);
                }
                markAsDirty(leadIndex);
            }
        }
        this.anchorIndex = anchorIndex;
        this.leadIndex = leadIndex;
    }
}
