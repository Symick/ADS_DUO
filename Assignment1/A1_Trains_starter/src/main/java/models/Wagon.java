package models;

public abstract class Wagon {
    protected int id;               // some unique ID of a Wagon
    private Wagon nextWagon;        // another wagon that is appended at the tail of this wagon
    // a.k.a. the successor of this wagon in a sequence
    // set to null if no successor is connected
    private Wagon previousWagon;    // another wagon that is prepended at the front of this wagon
    // a.k.a. the predecessor of this wagon in a sequence
    // set to null if no predecessor is connected


    // representation invariant propositions:
    // tail-connection-invariant:   wagon.nextWagon == null or wagon == wagon.nextWagon.previousWagon
    // front-connection-invariant:  wagon.previousWagon == null or wagon = wagon.previousWagon.nextWagon

    public Wagon(int wagonId) {
        this.id = wagonId;
    }

    public int getId() {
        return id;
    }

    public Wagon getNextWagon() {
        return nextWagon;
    }

    private void setNextWagon(Wagon nextWagon) {
        this.nextWagon = nextWagon;
    }

    public Wagon getPreviousWagon() {
        return previousWagon;
    }

    private void setPreviousWagon(Wagon previousWagon) {
        this.previousWagon = previousWagon;
    }

    /**
     * @return whether this wagon has a wagon appended at the tail
     */
    public boolean hasNextWagon() {
        return nextWagon != null;
    }

    /**
     * @return whether this wagon has a wagon prepended at the front
     */
    public boolean hasPreviousWagon() {
        return previousWagon != null;
    }

    /**
     * Returns the last wagon attached to it,
     * if there are no wagons attached to it then this wagon is the last wagon.
     *
     * @return the last wagon
     */
    public Wagon getLastWagonAttached() {
        Wagon current = this;
        while (current.hasNextWagon()) {
            current = current.nextWagon;
        }
        return current;
    }

    /**
     * @return the length of the sequence of wagons towards the end of its tail
     * including this wagon itself.
     */
    public int getSequenceLength() {
        Wagon current = this;

        //current wagon is also part of the sequence so start at one
        int sequence = 1;
        while (current.hasNextWagon()) {
            current = current.getNextWagon();
            sequence++;
        }
        return sequence;
    }

    /**
     * Attaches the tail wagon and its connected successors behind this wagon,
     * if and only if this wagon has no wagon attached at its tail
     * and if the tail wagon has no wagon attached in front of it.
     *
     * @param tail the wagon to attach behind this wagon.
     * @throws IllegalStateException if this wagon already has a wagon appended to it.
     * @throws IllegalStateException if tail is already attached to a wagon in front of it.
     *                               The exception should include a message that reports the conflicting connection,
     *                               e.g.: "%s is already pulling %s"
     *                               or:   "%s has already been attached to %s"
     */
    public void attachTail(Wagon tail) throws IllegalStateException {
        // this already has a successor so a tail can't be attached
        if (this.hasNextWagon()) {
            throw new IllegalStateException(String.format("%s is already pulling %s", this, this.nextWagon));
        }

        //the wagon which needs to become the tail already has a predecessor
        if (tail.hasPreviousWagon()) {
            throw new IllegalStateException(String.format("%s has already been attached to %s", tail, tail.previousWagon));
        }

        this.setNextWagon(tail);
        tail.setPreviousWagon(this);
    }

    /**
     * Detaches the tail from this wagon and returns the first wagon of this tail.
     *
     * @return the first wagon of the tail that has been detached
     * or <code>null</code> if it had no wagons attached to its tail.
     */
    public Wagon detachTail() {
        if (!this.hasNextWagon()) {
            return null;
        }
        //makes the next wagon of this wagon become the front of its own sequence
        Wagon firstOfTail = this.getNextWagon();
        firstOfTail.setPreviousWagon(null);

        //remove the reference to the old tail. Make this wagon the tail of the sequence
        this.setNextWagon(null);
        return firstOfTail;
    }

    /**
     * Detaches this wagon from the wagon in front of it.
     * No action if this wagon has no previous wagon attached.
     *
     * @return the former previousWagon that has been detached from,
     * or <code>null</code> if it had no previousWagon.
     */
    public Wagon detachFront() {
        if (!this.hasPreviousWagon()) {
            return null;
        }
        //remove reference from wagon in front of this wagon
        Wagon prev = this.getPreviousWagon();
        prev.setNextWagon(null);
        //this wagon becomes front, and doesn't have a previous anymore
        this.setPreviousWagon(null);
        return prev;


    }

    /**
     * Replaces the tail of the <code>front</code> wagon by this wagon and its connected successors
     * Before such reconfiguration can be made,
     * the method first disconnects this wagon form its predecessor,
     * and the <code>front</code> wagon from its current tail.
     *
     * @param front the wagon to which this wagon must be attached to.
     */
    public void reAttachTo(Wagon front) {
        //disconnect front from successor
        front.detachTail();
        //disconnect this wagon from its predecessor
        this.detachFront();
        //Attach this wagon to its new predecessor front (sustaining the invariant propositions).
        front.setNextWagon(this);
        this.setPreviousWagon(front);
    }

    /**
     * Removes this wagon from the sequence that it is part of,
     * and reconnects its tail to the wagon in front of it, if any.
     */
    public void removeFromSequence() {
        //if Wagon is not in a sequence
        if (!this.hasPreviousWagon() && !this.hasNextWagon()) {
            return;
        }
        //remove from front
        if (!this.hasPreviousWagon()) {
            this.detachTail();
            return;
        }

        //remove from end
        if (!this.hasNextWagon()) {
            this.detachFront();
            return;
        }

        //remove from middle
        Wagon predecessor = this.detachFront();
        Wagon successor = this.detachTail();
        successor.reAttachTo(predecessor);
    }


    /**
     * Reverses the order in the sequence of wagons from this Wagon until its final successor.
     * The reversed sequence is attached again to the wagon in front of this Wagon, if any.
     * No action if this Wagon has no succeeding next wagon attached.
     *
     * @return the new start Wagon of the reversed sequence (with is the former last Wagon of the original sequence)
     */
    public Wagon reverseSequence() {
        if (!this.hasNextWagon()) {
            return this;
        }
        Wagon attachAfterReverse = this.detachFront();
        Wagon reverse = null;
        Wagon current = this;
        while (current != null) {
            Wagon next = current.getNextWagon();
            //remove tail, this also has a side effect that the next wagon doesn't have a front anymore
            current.detachTail();

            //Reverse is the first Wagon of the reversed sequence
            if (reverse == null) {
                //add the current to the reversed sequence, which is empty at the start
                reverse = current;
            } else {
                //make the current the new starting Wagon of the reversed sequence
                current.attachTail(reverse);
                reverse = current;
            }
            current = next;
        }


        //attach the reversed sequence back to the front if it exists
        if (attachAfterReverse != null) {
            reverse.reAttachTo(attachAfterReverse);
        }
        return reverse;
    }

    @Override
    public String toString() {
        return String.format("[Wagon-%d]", id);
    }
}
