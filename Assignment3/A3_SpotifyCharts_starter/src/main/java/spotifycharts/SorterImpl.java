package spotifycharts;

import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class SorterImpl<E> implements Sorter<E> {

    /**
     * Sorts all items by selection or insertion sort using the provided comparator
     * for deciding relative ordening of two items
     * Items are sorted 'in place' without use of an auxiliary list or array
     * @param items       the items to be sorted
     * @param comparator  the comparator to decide relative ordening
     * @return  the items sorted in place
     */
    public List<E> selInsBubSort(List<E> items, Comparator<E> comparator) {
        int n = items.size();

        // Chose selection sort because it is the most efficient of the three.
        for (int i = 1; i < n; i++) {
            E key = items.get(i);
            int j = i - 1; // Index of the item before the key.

            // Move items of arr[0..i-1], that are greater than key, to one position
            // ahead of their current position.
            while (j >= 0 && comparator.compare(items.get(j), key) > 0) {
                items.set(j + 1, items.get(j)); // Move the item one position ahead.
                j = j - 1; // Move the index one position back.
            }

            items.set(j + 1, key); // Insert the key at the correct position in the sorted array.
        }

        return items;
    }

    /**
     * Sorts all items by quick sort using the provided comparator
     * for deciding relative ordening of two items
     * Items are sorted 'in place' without use of an auxiliary list or array
     * @param items
     * @param comparator
     * @return  the items sorted in place
     */
    public List<E> quickSort(List<E> items, Comparator<E> comparator) {
        quickSort(items, comparator, 0, items.size() - 1);
        return items;   // replace as you find appropriate
    }

    /**
     * Private helper function for the quickSort sorting algorithm
     *
     * @param items a list of items
     * @param comparator a comparator to determine how the list should be sorted
     * @param low the lowest index of the subarray which is partitioned
     * @param high the highest index of the subarray which is partitioned
     *
     * based on <a href="https://www.programiz.com/dsa/quick-sort#google_vignette">quicksort pseudocode</a>
     * made some adjustments to the implementation to randomize the pivot each time
     * this reduces the change of quicksort running in its worst-possible scenario
     */
    private void quickSort(List<E> items, Comparator<E> comparator, int low, int high) {
        if (low < high) {
            int partition =  partition(items, comparator, low, high);

            //sort left subarray
            quickSort(items, comparator, low, partition -1);
            //sort right subarray
            quickSort(items, comparator, partition + 1, high);
        }
    }

    /**
     * Set one item of the list as the pivot. All items lower than the pivot are set to the left
     * and all items higher than the pivot are set to the right of the pivot.
     * Making sure the pivot is at the correct spot in the list
     * @param items a list of items
     * @param comparator a comparator to determine how the list should be sorted
     * @param low the lowest index of the subarray which is partitioned
     * @param high the highest index of the subarray which is partitioned
     * @return the correct index of the pivot in the sorted array
     *
     * this function is protected, so it can be checked in a unit test.
     */
    protected int partition(List<E> items, Comparator<E> comparator, int low, int high) {
        long seed = 121247;
        //get random pivot
        Random random = new Random(seed);
        int randomPivot = random.nextInt(high - low + 1) + low;
        //swap random pivot with high
        swap(items, randomPivot, high);

        //get new pivot
        E pivot = items.get(high);
        int i = low; //location of elements smaller then pivot

        /*
            loop-invariant: All items in the subarray [low: i -1] are smaller than the pivot and all items in the subarray
            [i: j -1] are greater than the pivot.
         */
        for (int j = low; j < high; j++) {
            if (comparator.compare(items.get(j), pivot) <= 0) {
                //swap current item to the left of the pivot
                swap(items, i, j);
                i++;
            }
        }
        //set the pivot to the correct spot
        swap(items, i, high);
        return i;
    }

    /**
     * Swap to items in a list
     * @param items a list of items
     * @param index1 the index of the first item
     * @param index2 the index of the second
     */
    private void swap(List<E> items, int index1, int index2) {
        E item1 = items.get(index1);
        E item2 = items.get(index2);
        items.set(index1, item2);
        items.set(index2, item1);
    }

    /**
     * Identifies the lead collection of numTops items according to the ordening criteria of comparator
     * and organizes and sorts this lead collection into the first numTops positions of the list
     * with use of (zero-based) heapSwim and heapSink operations.
     * The remaining items are kept in the tail of the list, in arbitrary order.
     * Items are sorted 'in place' without use of an auxiliary list or array or other positions in items
     * @param numTops       the size of the lead collection of items to be found and sorted
     * @param items         the items to be sorted
     * @param comparator    the comparator to decide relative ordening
     * @return              the items list with its first numTops items sorted according to comparator
     *                      all other items >= any item in the lead collection
     */
    public List<E> topsHeapSort(int numTops, List<E> items, Comparator<E> comparator) {

        // Create a reverse comparator to build a max-heap.
        Comparator<E> reverseComparator = comparator.reversed();

        // Build a max-heap of the first numTops items.
        for (int heapSize = 2; heapSize <= numTops; heapSize++) {
            // Repair the heap condition of items[0..heapSize-2] to include new item items[heapSize-1]
            heapSwim(items, heapSize, reverseComparator);
        }

        // Insert remaining items into the lead collection as appropriate.
        for (int i = numTops; i < items.size(); i++) {
            // Loop-invariant: items[0..numTops-1] represents the current lead collection in a heap data structure.
            // The root of the heap is the currently trailing item in the lead collection,
            // which will lose its membership if a better item is found from position i onwards
            E item = items.get(i);
            E worstLeadItem = items.get(0);
            if (comparator.compare(item, worstLeadItem) < 0) {
                // Item < worstLeadItem, so shall be included in the lead collection.
                items.set(0, item);
                // Demote worstLeadItem back to the tail collection, at the original position of item.
                items.set(i, worstLeadItem);
                // Repair the heap condition of the lead collection.
                heapSink(items, numTops, reverseComparator);
            }
        }

        // The first numTops positions of the list now contain the lead collection.
        // The reverseComparator heap condition applies to this lead collection.
        // Now use heapSort to realise full ordening of this collection.
        for (int i = numTops-1; i > 0; i--) {
            // Loop-invariant: items[i+1..numTops-1] contains the tail part of the sorted lead collection.
            // Position 0 holds the root item of a heap of size i+1 organised by reverseComparator.
            // This root item is the worst item of the remaining front part of the lead collection.

            // Swap root with the last item.
            swap(items, 0, i);

            // Repair the heap condition of the remaining items.
            heapSink(items, i, reverseComparator);
        }

        return items;
    }

    /**
     * Repairs the zero-based heap condition for items[heapSize-1] on the basis of the comparator
     * all items[0..heapSize-2] are assumed to satisfy the heap condition
     * The zero-bases heap condition says:
     *                      all items[i] <= items[2*i+1] and items[i] <= items[2*i+2], if any
     * or equivalently:     all items[i] >= items[(i-1)/2]
     * @param items       the items to be sorted
     * @param heapSize    the size of the heap
     * @param comparator  the comparator to decide relative ordening
     */
    protected void heapSwim(List<E> items, int heapSize, Comparator<E> comparator) {
        int i = heapSize - 1; // Index of the item that is being swum up the heap.

        // Move items of arr[0..i-1], that are greater than key, to one position ahead of their current position.
        while (i > 0 && comparator.compare(items.get((i - 1) / 2), items.get(i)) > 0) {
            swap(items, i, (i - 1) / 2); // Move the item one position ahead.
            i = (i - 1) / 2; // Move the index one position back.
        }
    }
    /**
     * Repairs the zero-based heap condition for its root items[0] on the basis of the comparator
     * all items[1..heapSize-1] are assumed to satisfy the heap condition
     * The zero-bases heap condition says:
     *                      all items[i] <= items[2*i+1] and items[i] <= items[2*i+2], if any
     * or equivalently:     all items[i] >= items[(i-1)/2]
     * @param items       the items to be sorted
     * @param heapSize    the size of the heap
     * @param comparator  the comparator to decide relative ordening
     */
    protected void heapSink(List<E> items, int heapSize, Comparator<E> comparator) {
        int i = 0; // Index of the item that is being sunk down the heap.

        // Move items of arr[0..i-1], that are greater than key, to one position ahead of their current position.
        while (2 * i + 1 < heapSize) {
            int j = 2 * i + 1; // Index of the left child of i.
            if (j < heapSize - 1 && comparator.compare(items.get(j), items.get(j + 1)) > 0) {
                j++; // Index of the right child of i.
            }

            if (comparator.compare(items.get(i), items.get(j)) <= 0) {
                break; // The heap condition is satisfied.
            }

            swap(items, i, j); // Swap the item with its smallest child.
            i = j; // Move the index one position down.
        }
    }
}
