package de.joergwille.playground.shareddatamodel.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 *
 * @author willejoerg
 * @param <E>
 */
public class AveSharedSelectionModel<E> extends AbstractUniqueIterableDataModel<E>
        implements Serializable, UpdateObservable<E> {

    private static final long serialVersionUID = -3479145342764218640L;
    private List<UpdateListener<E>> updateListeners;

    public AveSharedSelectionModel() {
        super();
        this.updateListeners = new ArrayList<>();
    }

    public AveSharedSelectionModel(final E[] initial) {
        this(Arrays.asList(initial));
    }

    public AveSharedSelectionModel(Collection<E> initial) {
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
        if (this.updateListeners.isEmpty() || (newItems.size() == currentItems.size() && newItems.containsAll(currentItems) && currentItems.containsAll(newItems) )) {
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
