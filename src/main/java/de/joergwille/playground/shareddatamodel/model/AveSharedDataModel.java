package de.joergwille.playground.shareddatamodel.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * AveSharedDataModel is the data model which stores the values being used in
 * <code>JComboBox</code>. It is meant to be used as a shared model together
 * with multiple {@link AveSharedComboBoxModel}, which themselves are used as
 * model in {@link JComboBox}. If updates occur to
 * <code>AveSharedDataModel</code> all instances of <code>JComboBox</code> get
 * updated.
 *
 * @author willejoerg
 * @param <E>
 */
public class AveSharedDataModel<E> extends AbstractUniqueIterableDataModel<E>
        implements Serializable, UpdateObservable<E> {

    private static final long serialVersionUID = -3479145342764218640L;
    private List<UpdateListener<E>> updateListeners;

    public AveSharedDataModel() {
        super();
        this.updateListeners = new ArrayList<>();
    }

    public AveSharedDataModel(final E[] initial) {
        this(Arrays.asList(initial));
    }

    public AveSharedDataModel(Collection<E> initial) {
        super(initial);
        this.updateListeners = new ArrayList<>();
    }

    public void addElement(E item) {
        super.add(item);
    }

    public void removeElement(Object obj) {
        remove(obj);
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

        this.updateListeners.forEach((updateListener) -> {
            updateListener.updating(UpdateListener.State.BEFORE_UPDATE, newItems, currentItems);
        });

        boolean isEnabled = super.isEnabled();
        super.setEnabled(false);
        super.clear();
        super.addAll(newItems);
        super.setEnabled(isEnabled);

        this.updateListeners.forEach((updateListener) -> {
            updateListener.updating(UpdateListener.State.AFTER_UPDATE, newItems, currentItems);
        });

        return true;
    }

    @Override
    public void insertElementAt(E item, int index) {
        super.insertElementAt(item, index);
    }

    @Override
    public void removeElementAt(int index) {
        super.remove(index);
    }

    @Override
    public void addUpdateListener(UpdateListener<E> listener) {
        updateListeners.add(listener);
    }

    @Override
    public void removeUpdateListener(UpdateListener<E> listener) {
        updateListeners.remove(listener);
    }

}
