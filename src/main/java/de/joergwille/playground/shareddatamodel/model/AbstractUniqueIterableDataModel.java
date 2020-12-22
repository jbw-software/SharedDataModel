package de.joergwille.playground.shareddatamodel.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import javax.swing.AbstractListModel;

/**
 * List data model which can be iterated.<br>
 * Compared to the standard list model it uses a LinkedHashSet instead of a
 * Vector and is iterable, an enabled flag can be set to false to avoid unwanted
 * events while changing the datamodel.
 *
 * @param <E> the type of the elements of this model
 *
 *
 * @author willejoerg
 */
public class AbstractUniqueIterableDataModel<E> extends AbstractListModel<E> implements Iterable<E> {

    private static final long serialVersionUID = -3206515347886083915L;
    private final Set<E> objects;
    private boolean enabled;
    private int sizeBeforeDisabled;

    /**
     * Construct a new empty {@link AbstractIterableListModel}
     */
    public AbstractUniqueIterableDataModel() {
        this.objects = new LinkedHashSet<>();
        this.enabled = true;
    }

    /**
     * Construct a new {@link AbstractIterableListModel}
     *
     * @param initial the collection to initialize the model with. May be null.
     */
    public AbstractUniqueIterableDataModel(Collection<? extends E> initial) {
        int defaultInitialCapacity = Math.max(16, initial.size());
        int initialCapacity = AbstractUniqueIterableDataModel.nearestPowerOfTwo(defaultInitialCapacity);
        this.objects = new LinkedHashSet<>(initialCapacity);
        this.objects.addAll(initial);
        this.enabled = true;
    }

    private static int nearestPowerOfTwo(int value) {
        return (int) Math.pow(2, (Integer.SIZE - Integer.numberOfLeadingZeros(value - 1)));
    }

    private int indexOf(E element) {
        int index = -1;
        Iterator<E> iterator = this.objects.iterator();
        int currentIndex = 0;
        while (iterator.hasNext()) {
            final E nextElement = iterator.next();
            if (nextElement.equals(element)) {
                index = currentIndex;
                break;
            }
            currentIndex++;
        }
        return index;
    }

    /**
     * Returns the element at the specified index. Throws an
     * <code>ArrayIndexOutOfBoundsException</code> if the index is negative or
     * not less than the size of the list. <blockquote> <b>Note:</b> Although
     * this method is not deprecated, the preferred method to use is
     * <code>get(int)</code>, which implements the <code>List</code> interface
     * defined in the 1.2 Collections framework. </blockquote>
     *
     * @param index an index into this list
     * @return the element at the specified index
     * @see #get(int)
     * @see List#get(int)
     */
    private E elementAt(int index) {
        E element = null;
        Iterator<E> iterator = this.objects.iterator();
        int currentIndex = 0;
        while (iterator.hasNext()) {
            element = iterator.next();
            if (currentIndex == index) {
                break;
            }
            currentIndex++;
        }
        return element;
    }

    /**
     * Returns the number of elements in this list.
     *
     * @return the number of elements in this list
     * @see List#size()
     */
    public int size() {
        return this.objects.size();
    }

    /**
     * Returns the number of elements in this list.
     * <p>
     * This method is identical to <code>size</code>, which implements the
     * <code>List</code> interface defined in the 1.2 Collections framework.
     * This method exists in conjunction with <code>setSize</code> so that
     * <code>size</code> is identifiable as a JavaBean property.
     *
     * @return the number of elements in this list
     * @see #size()
     */
    @Override
    public int getSize() {
        return this.size();
    }

    /**
     * Returns the element at the specified index. <blockquote> <b>Note:</b>
     * Although this method is not deprecated, the preferred method to use is
     * <code>get(int)</code>, which implements the <code>List</code> interface
     * defined in the 1.2 Collections framework. </blockquote>
     *
     * @param index an index into this list
     * @return the element at the specified index
     * @exception ArrayIndexOutOfBoundsException if the <code>index</code> is
     * negative or greater than the current size of this list
     * @see #get(int)
     */
    @Override
    public E getElementAt(int index) {
        return index >= 0 && index < size() ? this.elementAt(index) : null;
    }

    /**
     * Returns the element at the specified position in this list.
     * <p>
     * Throws an <code>ArrayIndexOutOfBoundsException</code> if the index is out
     * of range (<code>index &lt; 0 || index &gt;= size()</code>).
     *
     * @param index index of element to return
     * @return the element for the index
     */
    public E get(int index) {
        return index >= 0 && index < size() ? this.elementAt(index) : null;
    }

    /**
     * Tests whether this list has any elements.
     *
     * @return <code>true</code> if and only if this list has no elements, that
     * is, its size is zero; <code>false</code> otherwise
     * @see List#isEmpty()
     */
    public boolean isEmpty() {
        return this.objects.isEmpty();
    }

    /**
     * Returns an Iterator of the elements of this list.
     *
     * @return an Iterator of the elements of this list
     * @see List#iterator()
     */
    @Override
    public Iterator<E> iterator() {
        return this.objects.iterator();
    }

    /**
     * Returns an Iterator of the elements of this list.
     *
     * @return an Iterator of the elements of this list
     * @see List#iterator()
     */
    public Iterator<E> elements() {
        return this.objects.iterator();
    }

    /**
     * Tests whether the specified object is a element in this list.
     *
     * @param elem an object
     * @return <code>true</code> if the specified object is the same as a
     * element in this list
     * @see List#contains(Object)
     */
    public boolean contains(Object elem) {
        return this.objects.contains(elem);
    }

    /**
     * Searches for the first occurrence of <code>elem</code>.
     *
     * @param elem an object
     * @return the index of the first occurrence of the argument in this list;
     * returns <code>-1</code> if the object is not found
     * @see List#indexOf(Object)
     */
    @SuppressWarnings("unchecked")
    public int getIndexOf(Object elem) {
        return this.indexOf((E) elem);
    }

    /**
     * Removes all of the elements from this list. The list will be empty after
     * this call returns (unless it throws an exception).
     */
    public void clear() {
        int index = objects.size() - 1;
        if (index >= 0) {
            objects.clear();
            int remaining = size();
            fireIntervalRemoved(this, remaining, remaining + index);
        }
    }

    /**
     * Adds the specified element to the end of this set.
     *
     * @param element the element to be added
     * @return true if this set did not already contain the specified element
     */
    public boolean add(E element) {
        int index = size();
        boolean added = objects.add(element);
        if (added) {
            fireIntervalAdded(this, index, index);
        }
        return added;
    }

    public boolean addAll(Collection<? extends E> items) {
        int fromIndex = size();
        boolean changed = objects.addAll(items);
        int toIndex = size() - 1;
        if (changed) {
            fireIntervalAdded(this, fromIndex, toIndex);
        }
        return changed;
    }

    /**
     * Inserts the specified element at the specified position in this list.
     * <p>
     * Throws an <code>ArrayIndexOutOfBoundsException</code> if the index is out
     * of range (<code>index &lt; 0 || index &gt; size()</code>).
     *
     * @param index index at which the specified element is to be inserted
     * @param element element to be inserted
     */
    public void insertElementAt(E element, int index) {
        if (index < 0 || index >= size()) {
            return;
        }
        if (this.objects.contains(element)) {
            return;
        }
        int initialCapacity = AbstractUniqueIterableDataModel.nearestPowerOfTwo(size() + 1);
        final Set<E> aNewSet = new LinkedHashSet<>(initialCapacity);
        
        // First copy all existing items before the new element, then add the new element
        // and then complete with the rest of the existing elements.
        Iterator<E> iterator = this.objects.iterator();
        int currentIndex = 0;
        while (iterator.hasNext()) {
            aNewSet.add(iterator.next());
            if (currentIndex == index) {
                aNewSet.add(element);
            }
            currentIndex++;
        }
        fireIntervalAdded(this, index, index);
    }

    /**
     * Removes the first (lowest-indexed) occurrence of the argument from this
     * list.
     *
     * @param obj the element to be removed
     * @return <code>true</code> if the argument was a element of this list;
     * <code>false</code> otherwise
     */
    public boolean remove(Object obj) {
        int index = getIndexOf(obj);
        if (index < 0) {
            return false;
        }
        int totalIndex = index;
        boolean rv = objects.remove(obj);
        if (index >= 0) {
            fireIntervalRemoved(this, totalIndex, totalIndex);
        }
        return rv;
    }

    /**
     * Removes all elements from this list and sets its size to zero.
     * <blockquote>
     * <b>Note:</b> Although this method is not deprecated, the preferred method
     * to use is <code>clear</code>, which implements the <code>List</code>
     * interface defined in the 1.2 Collections framework. </blockquote>
     *
     * @see #clear()
     */
    public void removeAllElements() {
        clear();
    }

    /**
     * Removes the element at the specified position in this list. Returns the
     * element that was removed from the list.
     * <p>
     * Nothing will happen, if the index is out of range
     * (<code>index &lt; 0 || index &gt;= size()</code>).
     *
     * @param index the index of the element to removed
     */
    public void removeElementAt(int index) {
        if (index < 0 || index >= size()) {
            return;
        }
        E elem = this.elementAt(index);
        remove(elem);
    }

    /**
     * Returns a string that displays and identifies this object's properties.
     *
     * @return a String representation of this object
     */
    @Override
    public String toString() {
        return this.objects.toString();
    }

    /**
     * Returns a new <code>ArrayList</code> containing all of the elements in
     * correct order as added to the <code>Set</code>.
     *
     * @return a <code>ArrayList</code> containing the elements of the <code>Set</code>
     */
    public List<E> toList() {
        return new ArrayList<>(this.objects);
    }

    /**
     * Returns a new array containing all of the elements in
     * correct order as added to the <code>Set</code>.
     *
     * @return an array containing the elements of the <code>Set</code>
     */
    public Object[] toArray() {
        return toList().toArray();
    }

    @Override
    protected void fireContentsChanged(Object source, int index0, int index1) {
        if (!isEnabled()) {
            return;
        }
        super.fireContentsChanged(source, index0, index1);
    }

    @Override
    protected void fireIntervalAdded(Object source, int index0, int index1) {
        if (!isEnabled()) {
            return;
        }
        super.fireIntervalAdded(source, index0, index1);
    }

    @Override
    protected void fireIntervalRemoved(Object source, int index0, int index1) {
        if (!isEnabled()) {
            return;
        }
        super.fireIntervalRemoved(source, index0, index1);
    }

    /**
     * @return the enabled
     */
    public boolean isEnabled() {
        return enabled;
    }

    /**
     * @param enabled the enabled to set
     */
    public void setEnabled(boolean enabled) {
        if (this.enabled != enabled) {
            this.enabled = enabled;
            if (enabled) {
                int size = size();
                if (size > sizeBeforeDisabled) {
                    fireIntervalAdded(this, sizeBeforeDisabled, size - 1);
                } else if (sizeBeforeDisabled > size) {
                    fireIntervalRemoved(this, size, sizeBeforeDisabled - 1);
                }
            } else {
                sizeBeforeDisabled = size();
            }
        }
    }
}
