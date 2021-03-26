package de.imise.tool3lgm.graphtools.consistency.error.checker;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

import de.imise.tool3lgm.graphtools.consistency.error.type.AbstractConsistencyError;
import de.imise.tool3lgm.graphtools.consistency.error.type.AbstractIDError;
import de.imise.tool3lgm.graphtools.consistency.error.type.IDEmptyError;
import de.imise.tool3lgm.graphtools.consistency.error.type.IDNotUniqueError;
import de.imise.tool3lgm.graphtools.metamodel.elements.ModelElement;
import de.imise.tool3lgm.graphtools.model.GDCollection;
import de.imise.tool3lgm.graphtools.model.GraphDocument;
import de.imise.tool3lgm.graphtools.userfield.definition.UserField;
import de.imise.tool3lgm.graphtools.userfield.definition.UserFieldDefinitions;
import de.imise.tool3lgm.graphtools.userfield.definition.UserFieldTarget;

/**
 * @author AXS (20.03.2016)
 */
public class UniqueIDChecker implements ConsistencyErrorChecker {

    @Override
    public Class<? extends AbstractConsistencyError> getErrorType() {
        return AbstractIDError.class;
    }

    @Override
    public Collection<AbstractConsistencyError> getErrors(final GDCollection gdcoll, final boolean checkOnly) {
        List<AbstractConsistencyError> idErrors = new ArrayList<>();
        UserFieldDefinitions ufd = gdcoll.getUserFieldDefinitions();
        GraphDocument doc = gdcoll.getMainDoc();
        //alle ID-UserFields aller Klassen
        Iterable<UserField> idUserFields = ufd.getIDUserFields();
        //die letzte TargetClass und die zuletzt rausgesuchte Menge aller Elemente dieser Klassen cachen
        Class<? extends UserFieldTarget> userFieldTargetClass = null;
        List<ModelElement> userFieldTargetClassElements = null;
        Multimap<String, ModelElement> idValueToElements = ArrayListMultimap.create();
        for (UserField idUserField : idUserFields) {
            Class<? extends UserFieldTarget> newUserFieldTargetClass = idUserField.getTargetClass();
            idValueToElements.clear();
            //den Cache ggf updaten
            if (newUserFieldTargetClass != userFieldTargetClass) {
                if (ModelElement.class.isAssignableFrom(newUserFieldTargetClass)) {
                    userFieldTargetClass = newUserFieldTargetClass;
                    userFieldTargetClassElements = doc.getModelItems(userFieldTargetClass.asSubclass(ModelElement.class));
                }
            }
            if (userFieldTargetClassElements != null) {
                //für alle Elemente mit dem UserField
                for (ModelElement me : userFieldTargetClassElements) {
                    String idValue = me.getUserFieldInputValue(idUserField);
                    if (UserField.EMPTY_STRING.equals(idValue)) {
                        AbstractIDError idError = new IDEmptyError(me, idUserField);
                        idErrors.add(idError);
                        if (checkOnly) {
                            return idErrors;
                        }
                    } else {
                        idValueToElements.put(idValue, me);
                    }
                }
                for (String idValue : idValueToElements.keySet()) {
                    Collection<ModelElement> elementsWithSameID = idValueToElements.get(idValue);
                    if (elementsWithSameID.size() > 1) {
                        for (ModelElement me : elementsWithSameID) {
                            Collection<ModelElement> allWithSameID = new ArrayList<>(elementsWithSameID);
                            AbstractIDError idError = new IDNotUniqueError(me, idUserField, allWithSameID);
                            idErrors.add(idError);
                            if (checkOnly) {
                                return idErrors;
                            }
                        }
                    }
                }
            }
        }
        return idErrors;
    }

    /**
     * Liefert eine Liste aller Elemente derselben Art, wie das übergebene
     * Modelelement, die bei demselben UserField denselben Wert haben. Das
     * Element selbst ist auch immer in der Liste, also ist die Rückgabeliste
     * immer mind. 1 groß, es sei denn das UserField passt gar nicht zum
     * Element. ACHTUNG: Das hier geht für beliebige UserFields - nicht nur für
     * ID-USerFields!
     *
     * @param userField
     * @param me
     * @param value
     * @return
     */
    public static List<ModelElement> getElementsWithSameValue(final ModelElement me, final UserField userField, final String value) {
        Class<? extends ModelElement> meClass = me.getClass();
        //das UserField ist nicht für das übergebene Element definiert -> leere Liste
        if (!userField.getTargetClass().isAssignableFrom(meClass)) {
            return new ArrayList<>();
        }
        GDCollection gdcoll = me.getCollection();
        GraphDocument mainDoc = gdcoll.getMainDoc();
        List<ModelElement> modelItems = mainDoc.getModelItems(meClass);
        for (int i = modelItems.size() - 1; i >= 0; i--) {
            ModelElement other = modelItems.get(i);
            if (other != me) {
                String otherValue = userField.getValue(other);
                boolean bothNull = value == null && value == otherValue;
                boolean sameValue = value != null && !value.equals(otherValue);
                if (!bothNull && !sameValue) {
                    modelItems.remove(i);
                }
            }
        }
        return modelItems;
    }

    //    /**
    //     * Fügt der übergebenen Error-Liste alle ID-Errors des übergebenen Elementes hinzu.
    //     *
    //     * @param me
    //     * @param returnList
    //     */
    //    public void _addIDErrors(final ModelElement me, final List<AbstractError> returnList) {
    //        List<IDNotUniqueError> idErrors = _getIDErrors(me);
    //        returnList.addAll(idErrors);
    //    }
    //
    //    /**
    //     * Prüft für ein ModelElement, ob es ID-UserFields besitzt deren Werte mit dem anderer Elemente
    //     * übereinstimmen.
    //     *
    //     * @param me
    //     * @return
    //     */
    //    public static final List<IDNotUniqueError> _getIDErrors(final ModelElement me) {
    //        ArrayList<IDNotUniqueError> errors = new ArrayList<>();
    //        GDCollection gdcoll = me.getCollection();
    //        UserFieldDefinitions ufd = gdcoll.getUserFieldDefinitions();
    //        Class<? extends ModelElement> elementClass = me.getClass();
    //        Iterable<UserField> userFields = ufd.getUserFields(elementClass);
    //        for (UserField userField : userFields) {
    //            List<IDNotUniqueError> newErrors = _getIDErrors(me, userField);
    //            errors.addAll(newErrors);
    //        }
    //        return errors;
    //    }
    //
    //    /**
    //     * Wenn das übergebene UserField ein ID-UserField ist, das zum übergebenen Element passt
    //     *
    //     * @param me
    //     * @param idUserField
    //     * @return
    //     */
    //    public static final List<IDNotUniqueError> _getIDErrors(final ModelElement me, final UserField idUserField) {
    //        ArrayList<IDNotUniqueError> errors = new ArrayList<>();
    //        if (idUserField.getStyle() != UserField.Style.ID || !idUserField.getTargetClass().isAssignableFrom(me.getClass())) {
    //            return errors;
    //        }
    //        GDCollection gdcoll = me.getCollection();
    //        GraphDocument doc = gdcoll.getMainGraphDocument();
    //        Class<? extends ModelElement> elementClass = me.getClass();
    //        ArrayList<ModelElement> otherWithSameID = null;
    //        String userFieldValue = me.getUserFieldInputValue(idUserField);
    //        //Alle anderen Elemente raussuchen, die dieselbe ID haben
    //        for (ModelElement otherMe : doc.getModelItems(elementClass)) {
    //            if (me != otherMe) {
    //                String otherUserFieldValue = otherMe.getUserFieldInputValue(idUserField);
    //                boolean sameID = otherUserFieldValue == userFieldValue; //tritt bei beide == null ein;
    //                sameID = sameID || otherUserFieldValue != null && otherUserFieldValue.equals(userFieldValue);
    //                if (sameID) {
    //                    if (otherWithSameID == null) {
    //                        otherWithSameID = new ArrayList<>();
    //                    }
    //                    otherWithSameID.add(otherMe);
    //                }
    //                IDNotUniqueError error = new IDNotUniqueError(me, gdcoll, idUserField, otherWithSameID);
    //                errors.add(error);
    //            }
    //        }
    //        return errors;
    //    }

}
