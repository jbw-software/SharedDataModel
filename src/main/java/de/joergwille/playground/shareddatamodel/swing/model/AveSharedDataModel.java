package de.joergwille.playground.shareddatamodel.swing.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.swing.JLabel;

/**
 * AveSharedDataModel is the data model which stores the values being used in
 * <i>JComboBox</i>. It is meant to be used as a shared model together
 * with multiple {@link AveSharedComboBoxModel}, which themselves are used as
 * model in {@link JComboBox}. If updates occur to
 * <i>AveSharedDataModel</i> all instances of <i>JComboBox</i> get
 * updated.
 *
 * @author willejoerg
 * @param <E>
 */
public class AveSharedDataModel<E> extends AbstractUniqueIterableDataModel<E>
        implements Serializable, UpdateObservable<E> {

    private static final long serialVersionUID = 1L;
    private List<UpdateListener<E>> updateListeners;
    private boolean autoSetPrototypeDisplayValue;
    private E prototypeDisplayValue;

    public AveSharedDataModel() {
        this((List<E>) null);
    }

    public AveSharedDataModel(boolean autoSetPrototypeDisplayValue) {
        this((List<E>) null, autoSetPrototypeDisplayValue);
    }

    public AveSharedDataModel(final E[] initial) {
        this(initial, false);
    }

    public AveSharedDataModel(final E[] initial, boolean autoSetPrototypeDisplayValue) {
        this(Arrays.asList(initial), autoSetPrototypeDisplayValue);
    }

    public AveSharedDataModel(final List<E> initial) {
        this(initial, false);
    }

    public AveSharedDataModel(final List<E> initial, boolean autoSetPrototypeDisplayValue) {
        super(initial);
        this.updateListeners = new ArrayList<>();
        this.autoSetPrototypeDisplayValue = autoSetPrototypeDisplayValue;
        this.setPrototypeDisplayValue((this.autoSetPrototypeDisplayValue) ? this.autoSetPrototypeDisplayValue(initial) : null);
    }

    public void addElement(E item) {
        super.add(item);
        this.setPrototypeDisplayValue((this.autoSetPrototypeDisplayValue) ? this.autoSetPrototypeDisplayValue(super.toList()) : null);
    }

    public void removeElement(Object obj) {
        super.remove(obj);
        this.setPrototypeDisplayValue((this.autoSetPrototypeDisplayValue) ? this.autoSetPrototypeDisplayValue(super.toList()) : null);
    }

    @SuppressWarnings("unchecked")
    public boolean update(List<E> newItems) {
        // get a copy of the current elements in the set as an ArrayList
        final List<E> currentItems = super.toList();

        // check if updated list is different from original list or no listeners are registered.
        if (this.updateListeners.isEmpty() || (newItems.size() == currentItems.size() && newItems.containsAll(currentItems) && currentItems.containsAll(newItems))) {
            return false;
        }
        if (newItems.isEmpty()) {
            clear();
            return true;
        }
        
        // get the PrototypeDisplayValue for optimizing drawing in BasicComboBoxUI
        this.setPrototypeDisplayValue((this.autoSetPrototypeDisplayValue) ? this.autoSetPrototypeDisplayValue(newItems) : null);

        this.updateListeners.forEach((updateListener) -> {
            updateListener.updating(UpdateListener.State.BEFORE_UPDATE, newItems, currentItems, this.getPrototypeDisplayValue());
        });

        boolean isEnabled = super.isEnabled();
        super.setEnabled(false);
        super.clear();
        super.addAll(newItems);
        super.setEnabled(isEnabled);

        this.updateListeners.forEach((updateListener) -> {
            updateListener.updating(UpdateListener.State.AFTER_UPDATE, newItems, currentItems, prototypeDisplayValue);
        });

        return true;
    }

    @Override
    public void insertElementAt(E item, int index) {
        throw new UnsupportedOperationException("'insertElementAt' is not yet implemented inn 'AveSharedDataModel'.");
        //super.insertElementAt(item, index);
    }

    @Override
    public void removeElementAt(int index) {
        throw new UnsupportedOperationException("'insertElementAt' is not yet implemented inn 'AveSharedDataModel'.");
        //super.remove(index);
    }

    @Override
    public void addUpdateListener(UpdateListener<E> listener) {
        updateListeners.add(listener);
    }

    @Override
    public void removeUpdateListener(UpdateListener<E> listener) {
        updateListeners.remove(listener);
    }

    public E getPrototypeDisplayValue() {
        return prototypeDisplayValue;
    }

    public void setPrototypeDisplayValue(E prototypeDisplayValue) {
        this.prototypeDisplayValue = prototypeDisplayValue;
    }

    public boolean isAutoSetPrototypeDisplayValue() {
        return autoSetPrototypeDisplayValue;
    }

    public void setAutoSetPrototypeDisplayValue(boolean autoSetPrototypeDisplayValue) {
        this.autoSetPrototypeDisplayValue = autoSetPrototypeDisplayValue;
    }
    
    private static String getWidestOfStrings(List<String> strings) {
        final JLabel label = new JLabel();
        int maxWidth = 0;
        String maxString = null;
        for (final String aString : strings) {
            label.setText(aString);
            final int width = label.getPreferredSize().width;
            if (width > maxWidth) {
                maxString = aString;
                maxWidth = width;
            }
        }
        return maxString;
    }
    
    @SuppressWarnings("unchecked")
    private E autoSetPrototypeDisplayValue(List<E> items) {
        if (items == null || items.isEmpty()) {
            return null;
        }
        if (items.size() ==  1) {
            return items.get(0);
        }
        if (items.get(0) instanceof String) {
            return (E) getWidestOfStrings((List<String>) items);
        }
        return null;
    }

}
