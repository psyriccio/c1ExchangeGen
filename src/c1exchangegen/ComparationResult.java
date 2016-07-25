/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package c1exchangegen;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author psyriccio
 */
public class ComparationResult {

    public enum Status {
        ERROR, NO_MAP, DIFF_CLASSES, DIFF, EQU
    }

    public enum DiffKind {
        PROPERTY, TYPE
    }

    private final Status status;
    private final List<ComparationResultsItem> resultItems;

    public ComparationResult(Status status, List<ComparationResultsItem> resultItems) {
        this.status = status;
        this.resultItems = resultItems;
    }

    public ComparationResult(ComparationResult base, Status status, List<ComparationResultsItem> resuItems) {
        this.status = Status.values()[Math.min(base.getStatus().ordinal(), status.ordinal())];
        this.resultItems = new ArrayList<>(base.getResultItems());
        if (resuItems != null && this.resultItems != null) {
            this.resultItems.addAll(resuItems);
        }
    }

    public Status getStatus() {
        return status;
    }

    public List<ComparationResultsItem> getResultItems() {
        return resultItems;
    }

    @Override
    public String toString() {
        return status.name()
                + ": {"
                + (resultItems.isEmpty() ? " }"
                : resultItems.stream()
                .map((i) -> i.toString())
                        .reduce("", (ac, itm) -> ac.isEmpty() ? ac + itm : ac + "; " + itm));
    }

}
