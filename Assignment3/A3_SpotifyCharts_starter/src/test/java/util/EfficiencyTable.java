package util;

import java.util.function.BinaryOperator;

/**
 * Table to display execution time in relation to the amount of executions
 * Based on the table used in the week 7 exercise names sorter
 * @param <E> the object class used for the table
 */
public class EfficiencyTable<E> {
    E[][] items;
    BinaryOperator<E> adder;
    BinaryOperator<E> multiplier;

    public EfficiencyTable(int nX, int nY, BinaryOperator<E> adder, BinaryOperator<E> multiplier) {
        this.items = (E[][]) new Object[nX][nY];
        this.adder = adder;
        this.multiplier = multiplier;
    }

    public E get(int x, int y) {
        return this.items[x][y];
    }

    public E set(int x, int y, E value) {
        E oldValue = this.items[x][y];
        this.items[x][y] = value;
        return oldValue;
    }

    public E add(int x, int y, E value) {
        if (this.items[x][y]==null)
            return this.set(x,y, value);
        this.items[x][y] = this.adder.apply(this.get(x,y), value);
        return this.items[x][y];
    }

    public E multiply(int x, int y, E value) {
        if (this.items[x][y]==null)
            return null;
        this.items[x][y] = this.multiplier.apply(this.get(x,y), value);
        return this.items[x][y];
    }

    public void multiplyAll(E value) {
        for (int x = 0; x < this.items.length; x++)
            for (int y = 0; y < this.items[0].length; y++)
                this.multiply(x,y, value);
    }

    public String rowCSV(int y, String itemFormat) {
        String separator = "";
        String row = "";
        for (int x = 0; x < this.items.length; x++) {
            row += separator;
            row += this.items[x][y] != null ? String.format(itemFormat, this.items[x][y]) : "";
            separator = ";";
        }
        return row;
    }



    public String csv(String itemFormat) {
        String csv = "";
        for (int y = 0; y < this.items[0].length; y++)
            csv += this.rowCSV(y, itemFormat) + "\n";
        return csv;
    }
}
