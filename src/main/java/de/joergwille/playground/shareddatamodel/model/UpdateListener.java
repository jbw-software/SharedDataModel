package de.joergwille.playground.shareddatamodel.model;

import java.util.List;

/**
 *
 * @author joerg
 * @param <E>
 */
public interface UpdateListener<E> {

    enum State {
        BEFORE_UPDATE,
        AFTER_UPDATE
    }

    /**
     * Before and after the shared DataModel of
     * {@code AveDefaultIterableComboBoxModel} gets updated, it informs all
     * bound {@code AveSelectableDefaultIterableComboBoxModel}.
     *
     * @param state
     * @param newItems
     * @param currentItems
     * @param prototypeDisplayValue  A PrototypeDisplayValue to be used for optimizing rendering.
     *                               Might be <i>null<i/>.
     */
    public void updating(State state, List<E> newItems, List<E> currentItems, E prototypeDisplayValue);
}
