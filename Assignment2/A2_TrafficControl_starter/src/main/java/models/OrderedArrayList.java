package models;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.function.BinaryOperator;
import java.util.function.Function;

public class OrderedArrayList<E>
        extends ArrayList<E>
        implements OrderedList<E> {

    protected Comparator<? super E> sortOrder;   // the comparator that has been used with the latest sort
    protected int nSorted;                       // the number of sorted items in the first section of the list
    // representation-invariant
    //      all items at index positions 0 <= index < nSorted have been ordered by the given sortOrder comparator
    //      other items at index position nSorted <= index < size() can be in any order amongst themselves
    //              and also relative to the sorted section

    public OrderedArrayList() {
        this(null);
    }

    public OrderedArrayList(Comparator<? super E> sortOrder) {
        super();
        this.sortOrder = sortOrder;
        this.nSorted = 0;
    }

    public Comparator<? super E> getSortOrder() {
        return this.sortOrder;
    }

    @Override
    public void clear() {
        super.clear();
        this.nSorted = 0;
    }

    @Override
    public void sort(Comparator<? super E> c) {
        super.sort(c);
        this.sortOrder = c;
        this.nSorted = this.size();
    }

    @Override
    public void add(int index, E element) {
        super.add(index, element);
        //set nSorted to the current index, because only the items before the newly added element are guaranteed to be sorted
        // the element of index itself could be of any kind, so after the insertion it is not certain that the list is sorted
        // Therefore nSorted should be re-instantiated
        if (index < nSorted) {
            nSorted = index;
        }
    }

    /**
     * Remove the item at the current index, and if the removed item is in the sorted part of the array
     * decrement the nSorted variable.
     *
     * @param index the index of the element to be removed
     * @return
     */
    @Override
    public E remove(int index) {

        E toRemove = super.remove(index);
        //decrement after super.remove is invoked if remove throws an exception nSorted is not decremented
        if (index < nSorted) {
            nSorted--;
        }
        return toRemove;
    }

    @Override
    public boolean remove(Object o) {
        int index = indexOf(o);

        //if the index remove is successful, then the nSorted should be decremented.
        try {
           this.remove(index);
           return true;
        } catch (IndexOutOfBoundsException ex) {
            return false;
        }
    }

    @Override
    public void sort() {
        if (this.nSorted < this.size()) {
            this.sort(this.sortOrder);
        }
    }

    @Override
    public int indexOf(Object item) {
        // efficient search can be done only if you have provided an sortOrder for the list
        if (this.getSortOrder() != null) {
            return indexOfByIterativeBinarySearch((E) item);
        } else {
            return super.indexOf(item);
        }
    }

    @Override
    public int indexOfByBinarySearch(E searchItem) {
        if (searchItem != null) {
            // some arbitrary choice to use the iterative or the recursive version
            return indexOfByRecursiveBinarySearch(searchItem);
        } else {
            return -1;
        }
    }

    /**
     * finds the position of the searchItem by an iterative binary search algorithm in the
     * sorted section of the arrayList, using the this.sortOrder comparator for comparison and equality test.
     * If the item is not found in the sorted section, the unsorted section of the arrayList shall be searched by linear search.
     * The found item shall yield a 0 result from the this.sortOrder comparator, and that need not to be in agreement with the .equals test.
     * Here we follow the comparator for sorting items and for deciding on equality.
     *
     * @param searchItem the item to be searched on the basis of comparison by this.sortOrder
     * @return the position index of the found item in the arrayList, or -1 if no item matches the search item.
     */
    public int indexOfByIterativeBinarySearch(E searchItem) {
        int left = 0;
        int right = nSorted - 1;
        int mid = 0;
        while (left <= right) {
            mid = (int) Math.floor((left + right) / 2.0);

            //if item is in the middle return mid
            if (this.sortOrder.compare(this.get(mid), searchItem) == 0) {
                return mid;
            }

            /*
             * If the mid is less than the item that means that search item is positioned to the right of the current mid
             */
            if (this.sortOrder.compare(this.get(mid), searchItem) < 0) {
                left = mid + 1;
            } else {
                // If the mid is more than the item that means that search item is positioned to the left of the current mid
                right = mid - 1;
            }

        }

        // Use linear search to find the item in the unsorted section of the arrayList.
        return linearSearch(searchItem);
    }

    /**
     * Finds the position of the searchItem in the unsorted section of the arrayList by linear search.
     *
     * @param searchItem The item to be searched on the basis of comparison by this.sortOrder.
     * @return The position index of the found item in the arrayList, or -1 if no item matches the search item.
     */
    public int linearSearch(E searchItem) {
        for (int i = 0; i < this.size(); i++) {
            // If the item is found, return the index.
            if (this.sortOrder.compare(this.get(i), searchItem) == 0) {
                return i;
            }
        }

        return -1; // When nothing is found.
    }

    /**
     * finds the position of the searchItem by a recursive binary search algorithm in the
     * sorted section of the arrayList, using the this.sortOrder comparator for comparison and equality test.
     * If the item is not found in the sorted section, the unsorted section of the arrayList shall be searched by linear search.
     * The found item shall yield a 0 result from the this.sortOrder comparator, and that need not to be in agreement with the .equals test.
     * Here we follow the comparator for sorting items and for deciding on equality.
     *
     * @param searchItem the item to be searched on the basis of comparison by this.sortOrder
     * @return the position index of the found item in the arrayList, or -1 if no item matches the search item.
     */
    public int indexOfByRecursiveBinarySearch(E searchItem) {
        int index = recursiveBinarySearch(searchItem, 0, nSorted - 1);

        // In the event that the item is not found in the sorted section,
        // the unsorted section of the arrayList shall be searched by linear search.
        if (index == -1) {
            return linearSearch(searchItem);
        }

        return index;
    }

    /**
     * Finds the position of the searchItem by a recursive binary search algorithm.
     *
     * @param searchItem The item to be searched on the basis of comparison by this.sortOrder
     * @param left       The left index of the array.
     * @param right      The right index of the array.
     * @return Position index of the found item in the arrayList, or -1 if no item matches the search item.
     */
    public int recursiveBinarySearch(E searchItem, int left, int right) {
        if (left > right) { // if left is greater than right, then the search item is not found.
            return -1;
        }

        // Select middle element of array.
        int mid = (left + right) / 2;

        // If the mid is equal to the item that means that search item is found.
        if (this.sortOrder.compare(this.get(mid), searchItem) == 0) {
            return mid;
        }

        // If the mid is less than the item that means that search item is positioned to the right of the current mid.
        if (this.sortOrder.compare(this.get(mid), searchItem) < 0) {
            return recursiveBinarySearch(searchItem, mid + 1, right);
        } else {
            // If the mid is more than the item that means that search item is positioned to the left of the current mid.
            return recursiveBinarySearch(searchItem, left, mid - 1);
        }
    }

    /**
     * finds a match of newItem in the list and applies the merger operator with the newItem to that match
     * i.e. the found match is replaced by the outcome of the merge between the match and the newItem
     * If no match is found in the list, the newItem is added to the list.
     *
     * @param newItem the item to be merged into the list.
     * @param merger  a function that takes two items and returns an item that contains the merged content of
     *                the two items according to some merging rule.
     *                e.g. a merger could add the value of attribute X of the second item
     *                to attribute X of the first item and then return the first item
     * @return true if the newItem was added to the list, false if a match was found and the match was
     * replaced by the outcome of the merge.
     */
    @Override
    public boolean merge(E newItem, BinaryOperator<E> merger) {
        if (newItem == null) return false;
        int matchedItemIndex = this.indexOfByRecursiveBinarySearch(newItem);

        // If no match is found in the list, the newItem is added to the list.
        if (matchedItemIndex < 0) {
            this.add(newItem);
            return true;
        } else {
            // If a match is found, the matched item is replaced by the outcome of the merge between the match and the newItem.
            E matchedItem = this.get(matchedItemIndex);
            this.set(matchedItemIndex, merger.apply(matchedItem, newItem));

            return false;
        }
    }

    /**
     * calculates the total sum of contributions of all items in the list
     *
     * @param mapper a function that calculates the contribution of a single item
     * @return the total sum of all contributions
     */
    @Override
    public double aggregate(Function<E, Double> mapper) {
        double sum = 0.0;

        for (E item : this) {
            sum += mapper.apply(item);
        }
        return sum;
    }
}
