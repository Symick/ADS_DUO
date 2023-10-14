package models;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.*;
import java.util.function.Function;

public class TrafficTracker {
    private final String TRAFFIC_FILE_EXTENSION = ".txt";
    private final String TRAFFIC_FILE_PATTERN = ".+\\" + TRAFFIC_FILE_EXTENSION;

    private OrderedList<Car> cars;                  // the reference list of all known Cars registered by the RDW
    private OrderedList<Violation> violations;      // the accumulation of all offences by car and by city

    public TrafficTracker() {
        cars = new OrderedArrayList<>(Car::compareTo);
        violations = new OrderedArrayList<>(Violation::compareByLicensePlateAndCity);
    }

    /**
     * imports all registered cars from a resource file that has been provided by the RDW
     * @param resourceName
     */
    public void importCarsFromVault(String resourceName) {
        this.cars.clear();

        // load all cars from the text file
        int numberOfLines = importItemsFromFile(this.cars,
                createFileFromURL(TrafficTracker.class.getResource(resourceName)),
                Car::fromLine);

        // sort the cars for efficient later retrieval
        this.cars.sort();

        System.out.printf("Imported %d cars from %d lines in %s.\n", this.cars.size(), numberOfLines, resourceName);
    }

    /**
     * imports and merges all raw detection data of all entry gates of all cities from the hierarchical file structure of the vault
     * accumulates any offences against purple rules into this.violations
     * @param resourceName
     */
    public void importDetectionsFromVault(String resourceName) {
        this.violations.clear();

        int totalNumberOfOffences =
            this.mergeDetectionsFromVaultRecursively(
                    createFileFromURL(TrafficTracker.class.getResource(resourceName)));

        System.out.printf("Found %d offences among detections imported from files in %s.\n",
                totalNumberOfOffences, resourceName);
    }

    /**
     * traverses the detections vault recursively and processes every data file that it finds
     * @param file
     */
    private int mergeDetectionsFromVaultRecursively(File file) {
        int totalNumberOfOffences = 0;

        if (file.isDirectory()) {
            // the file is a folder (a.k.a. directory)
            //  retrieve a list of all files and sub folders in this directory
            File[] filesInDirectory = Objects.requireNonNullElse(file.listFiles(), new File[0]);

            // Process all files and sub folders from the filesInDirectory list.
            for (File fileInDirectory : filesInDirectory) {
                totalNumberOfOffences += this.mergeDetectionsFromVaultRecursively(fileInDirectory);
            }

        } else if (file.getName().matches(TRAFFIC_FILE_PATTERN)) {
            // the file is a regular file that matches the target pattern for raw detection files
            // process the content of this file and merge the offences found into this.violations
            totalNumberOfOffences += this.mergeDetectionsFromFile(file);
        }

        return totalNumberOfOffences;
    }

    /**
     * imports another batch detection data from the filePath text file
     * and merges the offences into the earlier imported and accumulated violations
     * @param file
     */
    private int mergeDetectionsFromFile(File file) {
        // Re-sort the accumulated violations for efficient searching and merging.
        this.violations.sort();

        // Use a regular ArrayList to load the raw detection info from the file.
        List<Detection> newDetections = new ArrayList<>();

        // Import all detections from the specified file into the newDetections list.
        importItemsFromFile(newDetections, file, textLine -> Detection.fromLine(textLine, this.cars));

        System.out.printf("Imported %d detections from %s.\n", newDetections.size(), file.getPath());

        int totalNumberOfOffences = 0; // tracks the number of offences that emerges from the data in this file

        // Validate all detections against the purple criteria.
        for (Detection detection : newDetections) {
            Violation violation = detection.validatePurple();

            // Skip any detections that do not violate purple rules.
            if (violation == null) {
                continue;
            }

            // Merge any resulting offences into this.violations, accumulating offences per car and per city.
            this.violations.merge(violation, Violation::combineOffencesCounts);

            // Keep track of the totalNumberOfOffences for reporting.
            totalNumberOfOffences += violation.getOffencesCount();
        }

        return totalNumberOfOffences;
    }

    /**
     * calculates the total revenue of fines from all violations,
     * Trucks pay €25 per offence, Coaches €35 per offence
     * @return      the total amount of money recovered from all violations
     */
    public double calculateTotalFines() {
        final double TRUCK_FINE = 25.00;
        final double COACH_FINE = 35.00;

        return this.violations.aggregate(
                (violation) -> {
                    if (violation.getCar().getCarType() == Car.CarType.Truck) {
                        return TRUCK_FINE * violation.getOffencesCount();
                    } else {
                        return COACH_FINE * violation.getOffencesCount();
                    }
                }
        );
    }

    /**
     * Calculates the top violations by the specified keyExtractor.
     *
     * @param topNumber                   the requested top number of violations in the result list
     * @param keyExtractor                a function that extracts a key from a violation
     * @throws IndexOutOfBoundsException  if the topNumber is larger than the number of violations
     * @throws IllegalArgumentException   if the topNumber is negative
     * @return                            a list of topNum items that provides the top aggregated violations
     */
    public List<Violation> calculateTopViolations(int topNumber, Function<Violation, String> keyExtractor)
            throws IndexOutOfBoundsException, IllegalArgumentException {
        // Return an empty list if there are no violations.
        if (this.violations.isEmpty()) {
            return new ArrayList<>();
        }

        if (topNumber == 0) {
            return new ArrayList<>();
        } else if (topNumber < 0) {
            throw new IllegalArgumentException("The topNumber cannot be negative.");
        }

        // Create a new list of violations that aggregates the offencesCount by the specified keyExtractor.
        OrderedList<Violation> topViolations = new OrderedArrayList<>(Comparator.comparing(keyExtractor));

        // Merge all violations into the new list.
        for (Violation violation : this.violations) {
            topViolations.merge(violation, Violation::combineOffencesCounts);
        }

        // If the topNumber is larger than the number of violations.
        if (topNumber > topViolations.size()) {
            throw new IndexOutOfBoundsException("The topNumber is larger than the number of violations.");
        }

        // Sort the new list by decreasing offencesCount.
        topViolations.sort((v1, v2) -> v2.getOffencesCount() - v1.getOffencesCount());

        return topViolations.subList(0, topNumber); // Return the topNumber of violations from the new list.
    }

    /**
     * Prepares a list of topNumber of violations that show the highest offencesCount
     * when this.violations are aggregated by car across all cities.
     * @param topNumber     the requested top number of violations in the result list
     * @return              a list of topNum items that provides the top aggregated violations
     */
    public List<Violation> topViolationsByCar(int topNumber) {
         return calculateTopViolations(topNumber, violation -> violation.getCar().getLicensePlate());
    }

    /**
     * Prepares a list of topNumber of violations that show the highest offencesCount
     * when this.violations are aggregated by city across all cars.
     * @param topNumber     the requested top number of violations in the result list
     * @return              a list of topNum items that provides the top aggregated violations
     */
    public List<Violation> topViolationsByCity(int topNumber) {
        return calculateTopViolations(topNumber, Violation::getCity);
    }

    /**
     * imports a collection of items from a text file which provides one line for each item
     * @param items         the list to which imported items shall be added
     * @param file          the source text file
     * @param converter     a function that can convert a text line into a new item instance
     * @param <E>           the (generic) type of each item
     */
    public static <E> int importItemsFromFile(List<E> items, File file, Function<String,E> converter) {
        int numberOfLines = 0;

        Scanner scanner = createFileScanner(file);

        // read all source lines from the scanner,
        // convert each line to an item of type E
        // and add each successfully converted item into the list
        while (scanner.hasNext()) {
            // input another line with author information
            String line = scanner.nextLine();
            numberOfLines++;

            E item = converter.apply(line);

            items.add(item);
        }

        return numberOfLines;
    }

    /**
     * helper method to create a scanner on a file and handle the exception
     * @param file
     * @return
     */
    private static Scanner createFileScanner(File file) {
        try {
            return new Scanner(file);
        } catch (FileNotFoundException e) {
            throw new RuntimeException("FileNotFound exception on path: " + file.getPath());
        }
    }
    private static File createFileFromURL(URL url) {
        try {
            return new File(url.toURI().getPath());
        } catch (URISyntaxException e) {
            throw new RuntimeException("URI syntax error found on URL: " + url.getPath());
        }
    }

    public OrderedList<Car> getCars() {
        return this.cars;
    }

    public OrderedList<Violation> getViolations() {
        return this.violations;
    }
}
