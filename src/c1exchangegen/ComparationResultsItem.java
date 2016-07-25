/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package c1exchangegen;

import com.google.common.collect.Lists;
import java.util.List;

/**
 *
 * @author psyriccio
 */
public class ComparationResultsItem {

    public static ComparationResultsItem makeOne(Object objIn, Object objOut, ComparationResult.DiffKind diffKind, String description) {
        return new ComparationResultsItem(
                (objIn != null && objOut != null) ? PlaceKind.PLACE_BOTH
                        : (objIn != null ? PlaceKind.PLACE_IN : PlaceKind.PLACE_OUT),
                objIn, objOut, diffKind, description);
    
    }

    public static List<ComparationResultsItem> makeOneAsList(Object objIn, Object objOut, ComparationResult.DiffKind diffKind, String description) {
        return Lists.newArrayList(
                makeOne(objIn, objOut, diffKind, description)
        );
    }

    private final Object objectIn;
    private final Object objectOut;
    private final PlaceKind where;
    private final ComparationResult.DiffKind diffKind;
    private final String description;

    public ComparationResultsItem(PlaceKind where, Object objectIn, Object objectOut, ComparationResult.DiffKind diffKind, String description) {
        this.objectIn = objectIn;
        this.objectOut = objectOut;
        this.where = where;
        this.diffKind = diffKind;
        this.description = description;
    }

    public ComparationResultsItem(Object[] objects, ComparationResult.DiffKind diffKind, String description) {
        this.objectIn = objects[0];
        this.objectOut = objects[1];
        this.where = PlaceKind.PLACE_BOTH;
        this.diffKind = diffKind;
        this.description = description;
    }

    public Object getObjectIn() {
        return objectIn;
    }

    public Object getObjectOut() {
        return objectOut;
    }

    public PlaceKind getWhere() {
        return where;
    }

    public ComparationResult.DiffKind getDiffKind() {
        return diffKind;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        return c1exchangegen.ObjectIndex.getFullDescription(this.getObjectIn()) + " <> "
                + c1exchangegen.ObjectIndex.getFullDescription(this.getObjectOut()) + " @ "
                + this.getDiffKind().name() + " / "
                + this.getWhere().name() + " : "
                + this.getDescription() + " ] ";
    }

}
