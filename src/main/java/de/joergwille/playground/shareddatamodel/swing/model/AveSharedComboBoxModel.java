package de.joergwille.playground.shareddatamodel.swing.model;

import java.io.Serializable;
import java.util.EventListener;
import java.util.List;
import java.util.Objects;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.JComboBox;
import javax.swing.ListModel;
import javax.swing.MutableComboBoxModel;
import javax.swing.event.EventListenerList;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

/**
 * AveSharedComboBoxModel is a replacement for {@link DefaultComboBoxModel} as
 * data model for <code>JComboBox</code>. It uses {@link AveSharedDataModel} as
 * the actual container for the data and extends from
 * {@link AveUpdatableSelection} for storing the selected item of a
 * <code>JComboBox</code>. It implements {@link UpdateListener} to react on
 * changes on the data.
 *
 * @author willejoerg
 * @param <E>
 */
public class AveSharedComboBoxModel<E> extends AveUpdatableSelection<E>
        implements MutableComboBoxModel<E>, ListModel<E>, ListDataListener,
        UpdateListener<E>, Serializable {

    private static final long serialVersionUID = -3894432749074287792L;
    final protected EventListenerList listenerList; // copied from javax.swing.AbstractListModel
    private JComboBox<E> associatedComboBox;
    private boolean allowOneMutation;
    private boolean forceDeselectionOnIndexChange;
    private int selectedIndexBackup;

    /**
     *
     * @param sharedModel The {@link AveSharedDataModel} which actually holds
     * items.
     */
    public AveSharedComboBoxModel(final AveSharedDataModel<E> sharedModel) {
        this(sharedModel, null);
    }

    public AveSharedComboBoxModel(final AveSharedDataModel<E> sharedModel, final JComboBox<E> associatedComboBox) {
        this(sharedModel, associatedComboBox, false);
    }

    public AveSharedComboBoxModel(AveSharedDataModel<E> sharedModel, final JComboBox<E> associatedComboBox,
            boolean allowOneMutation) {
        this(sharedModel, associatedComboBox, false, true);
    }

    public AveSharedComboBoxModel(final AveSharedDataModel<E> sharedModel, final JComboBox<E> associatedComboBox,
            boolean allowOneMutation, boolean forceDeselectionOnIndexChange) {
        super(sharedModel);
        this.associatedComboBox = associatedComboBox;
        this.allowOneMutation = allowOneMutation;
        this.forceDeselectionOnIndexChange = forceDeselectionOnIndexChange;
        this.listenerList = new EventListenerList();
        this.setPrototypeDisplayValue(sharedModel.getPrototypeDisplayValue());
     }

    private boolean isOnlyOneMutation(List<E> newItems, List<E> currentItems) {
        // If the size has changed, it cannot be a one item mutatation.
        if (newItems.size() != currentItems.size()) {
            return false;
        }
        int changes = 0;
        for (int i = 0; i < newItems.size(); i++) {
            if (!newItems.get(i).equals(currentItems.get(i))) {
                changes++;
            }
        }
        return (changes <= 1);
    }

    /**
     * Returns the value of the boolean {@code allowOneMutation}. If
     * {@code true} the selected index is retained when exactly one element
     * mutates. This enables the use case, that an element stays selected when
     * it is being renamed. Default value is {@code false}
     *
     * @return allowOneMutation
     */
    public boolean isAllowOneMutation() {
        return allowOneMutation;
    }

    /**
     * Sets the value of the boolean {@code allowOneMutation}. If {@code true}
     * the selected index is retained when exactly one element mutates. This
     * enables the use case, that an element stays selected when it is being
     * renamed. Default value is {@code false}
     *
     * @param allowOneMutation
     */
    public void setAllowOneMutation(boolean allowOneMutation) {
        this.allowOneMutation = allowOneMutation;
    }

    /**
     * Returns the value of the boolean {@code forceDeselectionOnIndexChange}.
     * If {@code true} and {@code matchSelectionByString} is {@code true} an
     * extra deselection of the selected element is being forced when the data
     * model changes and the selection remains constant but its index changed.
     * Without the deselection no ItemChangeEvent would be fired. Default value
     * is {@code true}.
     *
     * @return forceDeselectionOnIndexChange
     */
    public boolean isForceDeselectionOnIndexChange() {
        return forceDeselectionOnIndexChange;
    }

    /**
     * Sets the value of the boolean {@code forceDeselectionOnIndexChange}. If
     * {@code true} and {@code matchSelectionByString} is {@code true} an extra
     * deselection of the selected element is being forced when the data model
     * changes and the selection remains constant but its index changed. Without
     * the deselection no ItemChangeEvent would be fired. Default value is
     * {@code true}.
     *
     * @param forceDeselectionOnIndexChange
     */
    public void setForceDeselectionOnIndexChange(boolean forceDeselectionOnIndexChange) {
        this.forceDeselectionOnIndexChange = forceDeselectionOnIndexChange;
    }
    
    public JComboBox<E> getAssociatedComboBox() {
        return associatedComboBox;
    }

    public void setAssociatedComboBox(JComboBox<E> associatedComboBox) {
        this.associatedComboBox = associatedComboBox;
        
        // update associatedComboBox with previously set prototypeDisplayValue.
        this.setPrototypeDisplayValue(super.getPrototypeDisplayValue());
    }
    
    @Override
    public void setPrototypeDisplayValue(E prototypeDisplayValue) {
        super.setPrototypeDisplayValue(prototypeDisplayValue);
        if ((this.associatedComboBox != null)) {
            this.associatedComboBox.setPrototypeDisplayValue(prototypeDisplayValue);
        }
    }

    @Override
    public void addElement(E anObject) {
        super.sharedModel.addElement(anObject);
    }

    @Override
    public void removeElement(Object anObject) {
        super.sharedModel.removeElement(anObject);
    }

    public boolean remove(Object obj) {
        boolean result = super.sharedModel.remove(obj);
        Object newSelected = Objects.equals(obj, super.getSelectedItem())
                ? null : super.getSelectedItem();
        setSelectedItem(newSelected);
        return result;
    }

    public void clear() {
        super.sharedModel.clear();
        super.setSelectedItem(null);
    }

    @Override
    public void insertElementAt(E anObject, int index) {
        super.sharedModel.insertElementAt(anObject, index);
        super.setSelectedItem(super.getSelectedItem());
    }

    @Override
    public void removeElementAt(int index) {
        super.sharedModel.removeElementAt(index);
    }

    /**
     * @param index the index of the object
     * @return the object at the given index from the delegate <code>Set</code>
     */
    // implements javax.swing.ListModel
    @Override
    public E getElementAt(int index) {
        return this.sharedModel.getElementAt(index);
    }

    /**
     * @return the size of the delegate <code>Set</code>
     */
    // implements javax.swing.ListModel
    @Override
    public int getSize() {
        return this.sharedModel.getSize();
    }

    // implements javax.swing.ListModel
    @Override
    public void addListDataListener(ListDataListener l) {
        if (getListDataListeners().length == 0) {
            super.sharedModel.addListDataListener(this);
        }
        listenerList.add(ListDataListener.class, l);
    }

    // implements javax.swing.ListModel
    @Override
    public void removeListDataListener(ListDataListener l) {
        listenerList.remove(ListDataListener.class, l);
        if (getListDataListeners().length == 0) {
            super.sharedModel.removeListDataListener(this);
        }
    }

    @Override
    public void updating(State state, List<E> newItems, List<E> currentItems, E prototypeDisplayValue) {

        if (UpdateListener.State.BEFORE_UPDATE.equals(state)) {
            int selectedObjIndex = -1;
            Object selectedObj = this.getSelectedItem();

            if (selectedObj != null) {
                // If only the currently selected item changed, then this is a
                // "rename case" and the new item with the same index gets selected.
                // This is a wanted behaviour and be enabled with allowOneMutation == true.
                if (this.allowOneMutation) {
                    selectedObjIndex = isOnlyOneMutation(newItems, currentItems)
                            ? currentItems.indexOf(selectedObj) : -1;
                }

                // Force a deselection of the item since the index has changed but
                // the selected item remained constant.
                // Without the deselection no ItemChangeEvent would be fired.
                if (this.forceDeselectionOnIndexChange == true
                        && super.isMatchSelectionByString() == true
                        && newItems.indexOf(selectedObj) > 0
                        && newItems.indexOf(selectedObj) != selectedObjIndex) {
                    setSelectedItem(null);
                }
            }

            if (super.isMatchSelectionByString() == true) {
                // If item exists also in updated items use its index
                int indexInNewItems = newItems.indexOf(selectedObj);
                // else fallback using the index of the selected item in the old list,
                // if the size of both lists are equal, else give up.
                selectedObjIndex = indexInNewItems > 0 ? indexInNewItems : selectedObjIndex;
            }
            selectedIndexBackup = selectedObjIndex;

        } else { // UpdateListener.State.AFTER_UPDATE
            this.setPrototypeDisplayValue(prototypeDisplayValue);
            
            if (selectedIndexBackup >= 0 && selectedIndexBackup < this.getSize()) {
                setSelectedItem(newItems.get(selectedIndexBackup));
            } else {
                setSelectedItem(null);
            }
        }
    }

    @Override
    public void intervalAdded(ListDataEvent e) {
        this.fireIntervalAdded(this, e.getIndex0(), e.getIndex1());
    }

    @Override
    public void intervalRemoved(ListDataEvent e) {
        int index = super.sharedModel.getIndexOf(super.getSelectedItem());
        if (index >= 0 && index >= e.getIndex0() && index <= e.getIndex1()) {
            setSelectedItem(getSelectedItem());
        } else if (index < 0 && super.getSelectedItem() != null) {
            setSelectedItem(null);
        }
        this.fireIntervalRemoved(this, e.getIndex0(), e.getIndex1());
    }

    @Override
    public void contentsChanged(ListDataEvent e) {
        this.fireContentsChanged(this, e.getIndex0(), e.getIndex1());
    }

    /**
     * Returns an array of all the list data listeners registered on this
     * <code>AbstractListModel</code>.
     *
     * @return all of this model's <code>ListDataListener</code>s, or an empty
     * array if no list data listeners are currently registered
     *
     * @see #addListDataListener
     * @see #removeListDataListener
     */
    public ListDataListener[] getListDataListeners() {
        return listenerList.getListeners(ListDataListener.class);
    }

    /**
     * <code>AbstractListModel</code> subclasses must call this method
     * <b>after</b>
     * one or more elements of the list change. The changed elements are
     * specified by the closed interval index0, index1 -- the endpoints are
     * included. Note that index0 need not be less than or equal to index1.
     *
     * @param source the <code>ListModel</code> that changed, typically "this"
     * @param index0 one end of the new interval
     * @param index1 the other end of the new interval
     * @see EventListenerList
     * @see DefaultListModel
     */
    protected void fireContentsChanged(Object source, int index0, int index1) {
        Object[] listeners = listenerList.getListenerList();
        ListDataEvent e = null;

        for (int i = listeners.length - 2; i >= 0; i -= 2) {
            if (listeners[i] == ListDataListener.class) {
                if (e == null) {
                    e = new ListDataEvent(source, ListDataEvent.CONTENTS_CHANGED, index0, index1);
                }
                ((ListDataListener) listeners[i + 1]).contentsChanged(e);
            }
        }
    }

    /**
     * <code>AbstractListModel</code> subclasses must call this method
     * <b>after</b>
     * one or more elements are added to the model. The new elements are
     * specified by a closed interval index0, index1 -- the enpoints are
     * included. Note that index0 need not be less than or equal to index1.
     *
     * @param source the <code>ListModel</code> that changed, typically "this"
     * @param index0 one end of the new interval
     * @param index1 the other end of the new interval
     * @see EventListenerList
     * @see DefaultListModel
     */
    protected void fireIntervalAdded(Object source, int index0, int index1) {
        Object[] listeners = listenerList.getListenerList();
        ListDataEvent e = null;

        for (int i = listeners.length - 2; i >= 0; i -= 2) {
            if (listeners[i] == ListDataListener.class) {
                if (e == null) {
                    e = new ListDataEvent(source, ListDataEvent.INTERVAL_ADDED, index0, index1);
                }
                ((ListDataListener) listeners[i + 1]).intervalAdded(e);
            }
        }
    }

    /**
     * <code>AbstractListModel</code> subclasses must call this method
     * <b>after</b> one or more elements are removed from the model.
     * <code>index0</code> and <code>index1</code> are the end points of the
     * interval that's been removed. Note that <code>index0</code> need not be
     * less than or equal to <code>index1</code>.
     *
     * @param source the <code>ListModel</code> that changed, typically "this"
     * @param index0 one end of the removed interval, including
     * <code>index0</code>
     * @param index1 the other end of the removed interval, including
     * <code>index1</code>
     * @see EventListenerList
     * @see DefaultListModel
     */
    protected void fireIntervalRemoved(Object source, int index0, int index1) {
        Object[] listeners = listenerList.getListenerList();
        ListDataEvent e = null;

        for (int i = listeners.length - 2; i >= 0; i -= 2) {
            if (listeners[i] == ListDataListener.class) {
                if (e == null) {
                    e = new ListDataEvent(source, ListDataEvent.INTERVAL_REMOVED, index0, index1);
                }
                ((ListDataListener) listeners[i + 1]).intervalRemoved(e);
            }
        }
    }

    /**
     * Returns an array of all the objects currently registered as
     * <code><em>Foo</em>Listener</code>s upon this model.
     * <code><em>Foo</em>Listener</code>s are registered using the
     * <code>add<em>Foo</em>Listener</code> method.
     * <p>
     * You can specify the <code>listenerType</code> argument with a class
     * literal, such as <code><em>Foo</em>Listener.class</code>. For example,
     * you can query a list model <code>m</code> for its list data listeners
     * with the following code:
     *
     * <pre>ListDataListener[] ldls = (ListDataListener[])(m.getListeners(ListDataListener.class));</pre>
     *
     * If no such listeners exist, this method returns an empty array.
     *
     * @param <T> the type of {@code EventListener} class being requested
     * @param listenerType the type of listeners requested; this parameter
     * should specify an interface that descends from
     * <code>java.util.EventListener</code>
     * @return an array of all objects registered as
     * <code><em>Foo</em>Listener</code>s on this model, or an empty array if no
     * such listeners have been added
     * @exception ClassCastException if <code>listenerType</code> doesn't
     * specify a class or interface that implements
     * <code>java.util.EventListener</code>
     *
     * @see #getListDataListeners
     */
    public <T extends EventListener> T[] getListeners(Class<T> listenerType) {
        return listenerList.getListeners(listenerType);
    }

}
