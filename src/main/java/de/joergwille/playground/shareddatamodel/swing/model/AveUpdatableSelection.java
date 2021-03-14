package de.joergwille.playground.shareddatamodel.swing.model;

import java.io.Serializable;
import java.util.List;

/**
 * AveUpdatableSelection is the smallest common denominators for storing a
 * selection of a <code>JComboBox</code>. {@link AveSharedComboBoxModel} extends
 * from it to individually store the selection for each instance. It also is
 * being used as the selection model for a <code>JComboBox</code> within a
 * <code>JTabel</code>. It has a reference to {@link AveSharedDataModel}. It
 * implements {@link UpdateListener} to react on changes for the
 * <code>selectedItem</code>.
 *
 * @author willejoerg
 * @param <E>
 */
@SuppressWarnings("serial")
public class AveUpdatableSelection<E> implements UpdateListener<E>, Serializable {

    protected final AveSharedDataModel<E> sharedModel;
    private Object selectedItem;
    private boolean allowEmptySelection;
    private boolean matchSelectionByString;
    private E prototypeDisplayValue;
    private boolean selectionUpdated;

    /**
     *
     * @param sharedModel A reference to {@link AveSharedDataModel} of which the
     * <code>selectedItem</code> is a member.
     */
    public AveUpdatableSelection(AveSharedDataModel<E> sharedModel) {
        this(sharedModel, (E) null);
    }

    public AveUpdatableSelection(AveSharedDataModel<E> sharedModel, final E selected) {
        this(sharedModel, selected, true);
    }

    public AveUpdatableSelection(AveSharedDataModel<E> sharedModel, final E selected, boolean allowEmptySelection) {
        this(sharedModel, selected, allowEmptySelection, false);
    }

    public AveUpdatableSelection(AveSharedDataModel<E> sharedModel, final E selected, boolean allowEmptySelection,
            boolean matchSelectionByString) {
        this.sharedModel = sharedModel;
        this.allowEmptySelection = allowEmptySelection;
        this.matchSelectionByString = matchSelectionByString;
        this.setSelected(selected);
        this.setPrototypeDisplayValue(sharedModel.getPrototypeDisplayValue());
        this.addListener();
    }

    private void addListener() {
        this.sharedModel.addUpdateListener(this);
    }

    private void removeListener() {
        this.sharedModel.removeUpdateListener(this);
    }

    private void setSelected(E anItem) {
        if (anItem == null && !allowEmptySelection && !this.sharedModel.isEmpty()) {
            anItem = this.sharedModel.get(0);
        }
        if ((this.sharedModel.contains(anItem) && this.selectedItem != null && !this.selectedItem.equals(anItem))
                || (this.sharedModel.contains(anItem) && this.selectedItem == null && anItem != null)) {
            this.selectedItem = anItem;
            this.sharedModel.fireContentsChanged(this, -1, -1);
        }
    }
    
    private boolean crossedRenamingWithSelectedItem(List<E> newItems, List<E> currentItems, int newSelectedIndex, int currentSelectedIndex) {
        // Define that for cross renaming the list must be of equal lenght, have equal items and only the order differs.
        if (newItems.size() != currentItems.size() || !newItems.containsAll(currentItems) || !currentItems.containsAll(newItems)) {
            return false;
        }
        if (!newItems.get(currentSelectedIndex).equals(currentItems.get(newSelectedIndex))
                || !newItems.get(newSelectedIndex).equals(currentItems.get(currentSelectedIndex))) {
            return false;
        }
        return true;
    }

    private boolean onlySelectedItemIsRenamed(List<E> newItems, List<E> currentItems, int currentSelectedIndex) {
        // Renaming only makes sense for E == String
        if (newItems.get(0) instanceof String == false) {
            return false;
        }
        // In a "rename case" the size does not change
        if (newItems.size() != currentItems.size()) {
            return false;
        }
        // In a "rename case" the size does not change, except for the special case that the new list has exactly 1 additional item... 
//        if (newItems.size() != currentItems.size() && newItems.size() - 1 != currentItems.size()) {
//            return false;
//        }
        // and this item has the same name as the selected item in the current list.
        // (This fullfills the use case that a new item was added and the original item renamed.)
//        else if (newItems.size() - 1 == currentItems.size()) {
//            final E addedItem = newItems.get(newItems.size() - 1);
//            final E lastSelectedItem = currentItems.get(currentSelectedIndex);
//            if (!addedItem.equals(lastSelectedItem)) {
//                return false;
//            }
//        }
        
        for (int i = 0; i < currentItems.size(); i++) {
            if (!newItems.get(i).equals(currentItems.get(i)) && i != currentSelectedIndex) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void updating(State state, List<E> newItems, List<E> currentItems, E prototypeDisplayValue) {
        if (UpdateListener.State.AFTER_UPDATE.equals(state)) {
            
            // use PrototypeDisplayValue from AveSharedDataModel
            this.setPrototypeDisplayValue(prototypeDisplayValue);
            this.selectionUpdated = true;

            // Update selection.
            Object newSelection = null;
            // Check if current selected item still exists and has the same index in new list and if so return without changing selection.
            final int indexInCurrentItems = currentItems.indexOf(this.selectedItem);
            final int indexInNewItems = newItems.indexOf(this.selectedItem);
            if (indexInCurrentItems == indexInNewItems) {
                return;
            }
            // Check if in the new list there exists an item with the same name as the current selected item and if so mark this item with that name for selection. 
            if (this.matchSelectionByString && indexInNewItems >= 0) {
                newSelection = newItems.get(indexInNewItems);
            } else
            // Check if only the selected item changed.
            if (this.onlySelectedItemIsRenamed(newItems, currentItems, indexInCurrentItems)) {
                newSelection = newItems.get(indexInCurrentItems);
            }

//            if (this.crossedRenamingWithSelectedItem(newItems, currentItems, indexInNewItems, indexInCurrentItems)) {
//                newSelection = newItems.get(indexInCurrentItems);
//            }

            this.setSelectedItem(newSelection);
        }
    }

    /**
     * Notifies this object that it is no longer being used.
     * This method should be called from the components removeNotify() method which uses this object as data model.
     */
    public void removeNotify() {
        this.removeListener();
    }

    /**
     * Returns the PrototypeDisplayValue which can be uses in a <code>JComboBox</code>.
     * 
     * @return the PrototypeDisplayValue.
     */
    public E getPrototypeDisplayValue() {
        return this.prototypeDisplayValue;
    }
    
    /**
     * Sets the PrototypeDisplayValue which can be uses in a <code>JComboBox</code>.
     *
     * @param prototypeDisplayValue the String with the most charcaters to be used as the PrototypeDisplayValue in
     * a <code>JComboBox</code>.
     */
    public void setPrototypeDisplayValue(E prototypeDisplayValue) {
        this.prototypeDisplayValue = prototypeDisplayValue;
    }

    /**
     * Get the value of the selected item. The selected item may be null if
     * {@code allowEmptySelection} is {@code true}.
     *
     * @return The selected value or null.
     */
    // implements javax.swing.ComboBoxModel
    public Object getSelectedItem() {
        return this.selectedItem;
    }
    
    /**
     * Set the value of the selected item. The selected item may be null.
     *
     * @param anObject The selected value or null for no selection.
     */
    // implements javax.swing.ComboBoxModel
    @SuppressWarnings("unchecked")
    public void setSelectedItem(Object anObject) {
        this.setSelected((E) anObject);
    }    
    
    /**
     * Get the value of the selected item with defined type.
     * The selected item may be null if {@code allowEmptySelection} is {@code true}.
     *
     * @return The selected value or null.
     */
    public E getSelectedTypedItem() {
        Object obj = this.getSelectedItem();
        int index = this.sharedModel.getIndexOf(obj);
        if (index < 0) {
            return null;
        }
        return this.sharedModel.get(index);
    }

    /**
     * Returns <i>true</i> if these selection has been updated by a <code>UpdateListener</code> event.
     * The internal flag is being resetted in this method, therfore the request is only valid once before an update occurs.
     * 
     * @return <i>true</i> if these selection has been updated.
     */
    public boolean isSelectionUpdated() {
        // Reset selectionUpdated to false.
        if (selectionUpdated) {
            selectionUpdated = false;
            return true;
        }
        return false;
    }

    /**
     * Returns the value of the boolean {@code allowEmptySelection}. If
     * {@code false} and List is not empty, there will always be a selected
     * element. The default item is the first item in the list. Default value is
     * {@code true}
     *
     * @return allowEmptySelection
     */
    public boolean isAllowEmptySelection() {
        return this.allowEmptySelection;
    }

    /**
     * Sets the value of the boolean {@code allowEmptySelection}. If
     * {@code false} and List is not empty, there will always be a selected
     * element. The default item is the first item in the list. Default value is
     * {@code true}
     *
     * @param allowEmptySelection
     */
    public void setAllowEmptySelection(boolean allowEmptySelection) {
        this.allowEmptySelection = allowEmptySelection;
        // if not allowEmptySelection but selection is null, try setting a non-null value (in method setSelected).
        if (allowEmptySelection == false && this.selectedItem == null) {
            this.setSelected(null);
        }
    }

    /**
     * Returns the value of the boolean {@code matchSelectionByString}. If
     * {@code true} the selection of an element is retained if arbitrary
     * elements change (mutation or modification of elements) and if there
     * exists an equal element after the change. The indices of these elements
     * may vary. This enables the use case, that an element stays selected when
     * the data model changes as long as the selected element exists. Default
     * value is {@code false}
     *
     * @return matchSelectionByString
     */
    public boolean isMatchSelectionByString() {
        return matchSelectionByString;
    }

    /**
     * Sets the value of the boolean {@code matchSelectionByString}. If
     * {@code true} the selection of an element is retained if arbitrary
     * elements change (mutation or modification of elements) and if there
     * exists an equal element after the change.The indices of these elements
     * may vary. This enables the use case, that an element stays selected when
     * the data model changes as long as the selected element exists. Default
     * value is {@code false}
     *
     * @param matchSelectionByString
     */
    public void setMatchSelectionByString(boolean matchSelectionByString) {
        this.matchSelectionByString = matchSelectionByString;
    }

    public AveSharedDataModel<E> getDataSelectionModel() {
        return this.sharedModel;
    }

    /**
     * Returns a new <code>ArrayList</code> containing all of the elements in
     * correct order as added to the delegate <code>Set</code>.
     *
     * @return a <code>ArrayList</code> containing the elements of the delegate
     * <code>Set</code>
     */
    public List<E> toList() {
        return this.sharedModel.toList();
    }

    /**
     * Returns a new array containing all of the elements in correct order as
     * added to the delegate <code>Set</code>.
     *
     * @return an array containing the elements of the delegate <code>Set</code>
     */
    public Object[] toArray() {
        return this.sharedModel.toArray();
    }

}
