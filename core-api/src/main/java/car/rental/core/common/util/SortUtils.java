package car.rental.core.common.util;

import io.quarkus.panache.common.Sort;

public class SortUtils {

    /**
     * Creates a Sort object from a sort parameter string.
     * Supports syntax: field (ascending), +field (ascending), -field (descending)
     * Multiple fields can be separated by commas: field1,-field2,+field3
     *
     * @param sortParam the sort parameter string
     * @return Sort object or null if sortParam is null or empty
     */
    public static Sort createSort(String sortParam) {
        if (sortParam == null || sortParam.trim().isEmpty()) {
            return null;
        }

        String[] sortFields = sortParam.split(",");
        Sort sort = null;

        for (String sortField : sortFields) {
            sortField = sortField.trim();
            if (sortField.startsWith("-")) {
                // Descending order
                String fieldName = sortField.substring(1);
                if (sort == null) {
                    sort = Sort.by(fieldName, Sort.Direction.Descending);
                } else {
                    sort.and(fieldName, Sort.Direction.Descending);
                }
            } else if (sortField.startsWith("+")) {
                // Ascending order (explicit)
                String fieldName = sortField.substring(1);
                if (sort == null) {
                    sort = Sort.by(fieldName, Sort.Direction.Ascending);
                } else {
                    sort.and(fieldName, Sort.Direction.Ascending);
                }
            } else {
                // Default ascending order
                if (sort == null) {
                    sort = Sort.by(sortField, Sort.Direction.Ascending);
                } else {
                    sort.and(sortField, Sort.Direction.Ascending);
                }
            }
        }

        return sort;
    }
}
