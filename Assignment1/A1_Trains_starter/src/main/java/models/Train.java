package models;

public class Train {
    private final String origin;
    private final String destination;
    private final Locomotive engine;
    private Wagon firstWagon;

    /* Representation invariants:
        firstWagon == null || firstWagon.previousWagon == null
        engine != null
     */

    public Train(Locomotive engine, String origin, String destination) {
        this.engine = engine;
        this.destination = destination;
        this.origin = origin;
    }

    /**
     * Indicates whether the train has at least one connected Wagon.
     * @return  true if the train has wagons, else false
     */
    public boolean hasWagons() {
        return firstWagon != null;
    }

    /**
     * A train is a passenger train when its first wagon is a PassengerWagon
     * (we do not worry about the posibility of mixed compositions here)
     * @return  true if the train is a passenger train, else false
     */
    public boolean isPassengerTrain() {
        return firstWagon instanceof PassengerWagon;
    }

    /**
     * A train is a freight train when its first wagon is a FreightWagon
     * (we do not worry about the posibility of mixed compositions here)
     * @return  true if the train is a passenger train, else false.
     */
    public boolean isFreightTrain() {
        return firstWagon instanceof FreightWagon;
    }

    public Locomotive getEngine() {
        return engine;
    }

    public Wagon getFirstWagon() {
        return firstWagon;
    }

    /**
     * Replaces the current sequence of wagons (if any) in the train
     * by the given new sequence of wagons (if any)
     * @param wagon the first wagon of a sequence of wagons to be attached (can be null)
     */
    public void setFirstWagon(Wagon wagon) {
        firstWagon = wagon;
    }

    /**
     * @return  the number of Wagons connected to the train
     */
    public int getNumberOfWagons() {
        if (!hasWagons()) {
            return 0;
        }

        int wagonCounter = 1;
        Wagon currentWagon = firstWagon;

        while (currentWagon.hasNextWagon()) {
            wagonCounter++;
            currentWagon = currentWagon.getNextWagon();
        }

        return wagonCounter;
    }

    /**
     * @return  the last wagon attached to the train
     */
    public Wagon getLastWagonAttached() {
        if (!hasWagons()) {
            return null;
        }

        Wagon currentWagon = firstWagon;

        while (currentWagon.hasNextWagon()) {
            currentWagon = currentWagon.getNextWagon();
        }

        return currentWagon;
    }

    /**
     * @return  the total number of seats on a passenger train
     *          (return 0 for a freight train)
     */
    public int getTotalNumberOfSeats() {
        if (isFreightTrain() || !hasWagons()) {
            return 0;
        }

        PassengerWagon currentWagon = (PassengerWagon) firstWagon;
        int totalSeats = currentWagon.getNumberOfSeats();

        while (currentWagon.hasNextWagon()) {
            currentWagon = (PassengerWagon) currentWagon.getNextWagon();
            totalSeats += currentWagon.getNumberOfSeats();
        }

        return totalSeats;
    }

    /**
     * calculates the total maximum weight of a freight train
     * @return  the total maximum weight of a freight train
     *          (return 0 for a passenger train)
     */
    public int getTotalMaxWeight() {
        if (isPassengerTrain() || !hasWagons()) {
            return 0;
        }

        FreightWagon currentWagon = (FreightWagon) firstWagon;
        int totalMaxWeight = currentWagon.getMaxWeight();

        while (currentWagon.hasNextWagon()) {
            currentWagon = (FreightWagon) currentWagon.getNextWagon();
            totalMaxWeight += currentWagon.getMaxWeight();
        }

        return totalMaxWeight;
    }

     /**
     * Finds the wagon at the given position (starting at 0 for the first wagon of the train)
     * @param   position  the index of the wagon in the train
     * @return            the wagon found at the given position
     */
    public Wagon findWagonAtPosition(int position) {
        if (!hasWagons() || position < 0) {
            return null;
        }

        int currentPosition = 0;
        Wagon currentWagon = firstWagon;

        while (currentWagon != null) {
            if (position == currentPosition) {
                return currentWagon;
            }

            currentWagon = currentWagon.getNextWagon();
            currentPosition++;
        }

        return null;
    }

    /**
     * Finds the wagon with a given wagonId
     * @param  wagonId  the id of the wagon to be found
     * @return          the wagon found
     *                  (return null if no wagon was found with the given wagonId)
     */
    public Wagon findWagonById(int wagonId) {
        if (!hasWagons()) {
            return null;
        }

        Wagon currentWagon = firstWagon;
        while (currentWagon != null) {
            if (currentWagon.getId() == wagonId) {
                return currentWagon;
            }

            currentWagon = currentWagon.getNextWagon();
        }

        return null;
    }

    /**
     * Determines if the given sequence of wagons can be attached to this train
     * Verifies if the type of wagons match the type of train (Passenger or Freight)
     * Verifies that the capacity of the engine is sufficient to also pull the additional wagons
     * Verifies that the wagon is not part of the train already
     * Ignores the predecessors before the head wagon, if any
     * @param wagon the head wagon of a sequence of wagons to consider for attachment
     * @return whether type and capacity of this train can accommodate attachment of the sequence
     */
    public boolean canAttach(Wagon wagon) {
        if ((isPassengerTrain() && wagon instanceof FreightWagon) // If the wagon type is incorrect.
             || isFreightTrain() && wagon instanceof PassengerWagon) {
            return false;
        } else if (getNumberOfWagons() + wagon.getSequenceLength() > engine.getMaxWagons()) {
            return false; // If the number of wagons exceeds the max.
        }

        return findWagonById(wagon.getId()) == null; // If the wagon isn't already present.
    }

    /**
     * Tries to attach the given sequence of wagons to the rear of the train
     * No change is made if the attachment cannot be made.
     * (when the sequence is not compatible or the engine has insufficient capacity)
     * if attachment is possible, the head wagon is first detached from its predecessors, if any
     * @param wagon the head wagon of a sequence of wagons to be attached
     * @return  whether the attachment could be completed successfully
     */
    public boolean attachToRear(Wagon wagon) {
        if (!canAttach(wagon)) {
            return false;
        }

        if (!hasWagons()) {
            firstWagon = wagon;
        } else {
            this.getLastWagonAttached().attachTail(wagon);
        }

        return true;
    }

    /**
     * Tries to insert the given sequence of wagons at the front of the train
     * (the front is at position one, before the current first wagon, if any)
     * No change is made if the insertion cannot be made.
     * (when the sequence is not compatible or the engine has insufficient capacity)
     * if insertion is possible, the head wagon is first detached from its predecessors, if any
     * @param wagon the head wagon of a sequence of wagons to be inserted
     * @return  whether the insertion could be completed successfully
     */
    public boolean insertAtFront(Wagon wagon) {



        return false;
    }

    /**
     * Tries to insert the given sequence of wagons at/before the given position in the train.
     * (The current wagon at given position including all its successors shall then be reattached
     *    after the last wagon of the given sequence.)
     * No change is made if the insertion cannot be made.
     * (when the sequence is not compatible or the engine has insufficient capacity
     *    or the given position is not valid for insertion into this train)
     * if insertion is possible, the head wagon of the sequence is first detached from its predecessors, if any
     * @param position the position where the head wagon and its successors shall be inserted
     *                 0 <= position <= numWagons
     *                 (i.e. insertion immediately after the last wagon is also possible)
     * @param wagon the head wagon of a sequence of wagons to be inserted
     * @return  whether the insertion could be completed successfully
     */
    public boolean insertAtPosition(int position, Wagon wagon) {
        // TODO

        return false;   // replace by proper outcome
    }

    /**
     * Tries to remove one Wagon with the given wagonId from this train
     * and attach it at the rear of the given toTrain
     * No change is made if the removal or attachment cannot be made
     * (when the wagon cannot be found, or the trains are not compatible
     * or the engine of toTrain has insufficient capacity)
     * @param wagonId   the id of the wagon to be removed
     * @param toTrain   the train to which the wagon shall be attached
     *                  toTrain shall be different from this train
     * @return  whether the move could be completed successfully
     */
    public boolean moveOneWagon(int wagonId, Train toTrain) {
        // TODO

        return false;   // replace by proper outcome
     }

    /**
     * Tries to split this train before the wagon at given position and move the complete sequence
     * of wagons from the given position to the rear of toTrain.
     * No change is made if the split or re-attachment cannot be made
     * (when the position is not valid for this train, or the trains are not compatible
     * or the engine of toTrain has insufficient capacity)
     * @param position  0 <= position < numWagons
     * @param toTrain   the train to which the split sequence shall be attached
     *                  toTrain shall be different from this train
     * @return  whether the move could be completed successfully
     */
    public boolean splitAtPosition(int position, Train toTrain) {
        if (!hasWagons()) {
            return false;
        }

//        System.out.println(this);
        Wagon splitWagon = findWagonAtPosition(position);
//        System.out.println(splitWagon);

        if (!toTrain.canAttach(splitWagon) || splitWagon == null) {
            return false;
        }

        toTrain.getLastWagonAttached().attachTail(splitWagon.detachTail());

//        System.out.println(splitWagon);

        return true;   // replace by proper outcome
    }

    /**
     * Reverses the sequence of wagons in this train (if any)
     * i.e. the last wagon becomes the first wagon
     *      the previous wagon of the last wagon becomes the second wagon
     *      etc.
     * (No change if the train has no wagons or only one wagon)
     */
    public void reverse() {
        // TODO

    }

    @Override
    public String toString() {
        StringBuilder trainStr = new StringBuilder(engine.toString());
        Wagon currentWagon = firstWagon;

        while (currentWagon != null) {
            trainStr.append(currentWagon);
            currentWagon = currentWagon.getNextWagon();
        }

        return String.format("%s with %d %s from %s to %s",
                trainStr, getNumberOfWagons(), getNumberOfWagons() == 1 ? "wagon" : "wagons", origin, destination);
    }
}
