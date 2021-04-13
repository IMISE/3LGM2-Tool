package de.imise.tool3lgm.graphtools.userfield.definition;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import javax.swing.JOptionPane;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

import de.imise.tool3lgm.Static;
import de.imise.tool3lgm.Tool3lgmConstants;
import de.imise.tool3lgm.graphtools.ElementsNameBuilder;
import de.imise.tool3lgm.graphtools.metamodel.elements.Edge;
import de.imise.tool3lgm.graphtools.metamodel.elements.ModelElement;
import de.imise.tool3lgm.graphtools.model.CopyDependencyResolver.CopyDependencyResolverResultSimple;
import de.imise.tool3lgm.graphtools.model.GDCollection;
import de.imise.tool3lgm.graphtools.model.GraphDocument;
import de.imise.tool3lgm.graphtools.userfield.UserFieldDefinitionsAnalyzer;
import de.imise.tool3lgm.graphtools.userfield.WeightReplacer;
import de.imise.tool3lgm.graphtools.userfield.calculator.Calculator;
import de.imise.tool3lgm.graphtools.userfield.calculator.PartValueSumFunction;
import de.imise.tool3lgm.graphtools.userfield.calculator.PartValueSumFunction.TWSumArguments;
import de.imise.tool3lgm.graphtools.userfield.calculator.PartValueSumSinglePartResults;
import de.imise.tool3lgm.graphtools.userfield.definition.UserField.Style;
import de.imise.tool3lgm.graphtools.userfield.definition.definitiontree.DefinitionGroupNode;
import de.imise.tool3lgm.graphtools.userfield.definition.definitiontree.DefinitionStructureNode;
import de.imise.tool3lgm.graphtools.userfield.definition.definitiontree.DefinitionSubTypeNode;
import de.imise.tool3lgm.graphtools.userfield.definition.definitiontree.DefinitionTabNode;
import de.imise.tool3lgm.graphtools.userfield.definition.definitiontree.DefinitionUserFieldNode;
import de.imise.tool3lgm.graphtools.userfield.definition.definitiontree.DefinitionUserFieldTargetClassNode;
import de.imise.tool3lgm.graphtools.userfield.event.UserFieldDefinitionChangeHandler;
import de.imise.tool3lgm.graphtools.view.tree.node.IconifiedTreeNode;
import de.imise.tool3lgm.graphtools.view.tree.node.LGMTreeNode;
import de.imise.util.Alphabetical;
import de.imise.util.collections.CollectionUtils;
import de.imise.util.collections.ExtendedMap;
import de.imise.util.swing.dialog.MultipleOptionPane;

/**
 * Beinhaltet alle <code>UserField</code>s in einer <code>HashMap</code>, die
 * für Node, Kanten und das Modell deklariert und definiert wurden.
 *
 * @author Thomas Rudert
 */
public final class UserFieldDefinitions extends UserFieldDefinitionChangeHandler implements Cloneable, Iterable<UserField> {

    /**
     * We only need this node for iterating over all UserFields of all
     * {@link DefinitionUserFieldTargetClassNode}s. This node will be not shown.
     */
    private DefinitionStructureNode definitionRoot = new DefinitionStructureNode("definitionRoot", "This node is only for having easy access to the Iterable<UserField> of all DefinitionUserFieldTargetClassNodes", "") {
    };

    /**
     * Maps from an element class to the tree node which describes the subtype,
     * tab, userfield group and userfield structure of the element class.
     */
    private Map<Class<? extends UserFieldTarget>, DefinitionUserFieldTargetClassNode> classToStructureNodeMap = new HashMap<>();

    /** Maps from the ID of a format to the format */
    private final Map<String, UserFieldNumberFormat> formatIdToFormat = new HashMap<>();

    /** Mappt von den IDs der UserFields auf das UserField */
    private final Map<String, UserField> idToUserFieldMap = new HashMap<>();

    /** Berechnet für diese Defnition alle Kennzahlen der konkreten Elemente */
    private Calculator calculator;

    /** Enthält für alle einfachen Teilwertsummen alle Zwischenergebnisse **/
    private final PartValueSumSinglePartResults partValueSumSinglePartResults;

    /**
     * Liste aller UserFields, die Formeln darstellen. Die Reihenfolge ist
     * relevant für die Konsistenz der Definition. Wenn Formeln andere Formeln
     * referenzieren, dann müssen die referenzierten Formeln in der Liste immer
     * vor den Formeln stehen, durch die sie referenziert werden.
     */
    private List<UserField> formulaUserFieldList = new ArrayList<>();

    /**
     * guava-Table für die Speicherung, bei welchem ModelElement welches
     * Verteilungsgeweichtg in Verrechnungsfunktionen, die dieses
     * Verteilungsgewicht nutzen, durch ein anderes ersetzt werden soll.
     */
    private final WeightReplacer weightReplacer;

    /**
     * Analyzer, über den der Zustand dieser {@link UserFieldDefinitions}
     * abgefragt werden kann
     */
    private UserFieldDefinitionsAnalyzer definitionsAnalyzer;

    /**
     * Klasse, über die die sogenannten Modellvariablen identifiziert werden,
     * also Variablen, die nicht für ein spezielles Element sondern für das
     * Gesamtmodell gelten und zur Verfügung stehen.
     */
    public static final Class<? extends UserFieldTarget> GLOBAL_USERFIELD_IDENTIFIER_CLASS = GDCollection.class;

    /**
     * Konstante um für <code>firstInconsistentUserFieldFormulaIndex</code>
     * anzugeben, dass alle darin befindlichen Formeln berechnet werden können.
     */
    private static final int NO_INCONSISTENCE_INDEX_FOUND = -1;

    /**
     * Maximale Anzahl der abhängigen <code>UserField</code>s, die im
     * Warnungsdialog vor dem Löschen eines <code>UserField</code>s angezeigt
     * werden.
     */
    private static final int MAX_USED_USERFIELD_DELETE_NUMBER = 10;

    /**
     * Konstante um für <code>firstInconsistentUserFieldFormulaIndex</code>
     * anzugeben, dass die Liste <code>formulaUserFieldList</code> neu sortiert
     * werden müsste, um festzustellen, ob sich alle Formeln berechnen lassen
     * bzw. welche inkonsitent sind.
     */
    private static final int FORMULA_INCONSITENCE_INDEX_UNKNOWN = -2;

    /**
     * Die Liste <code>formulaUserFieldList</code> wird (wenn keine
     * Kreisreferenzen in den Formeln vorkommen) so sortiert, dass sich jede
     * Formel in der Liste berechnen lässt, wenn alle Formeln berechnet wurden,
     * die sich in der Liste davor befinden. Sollte doch mind. eine
     * Kreisreferenz vorliegen, wird in dieser Variable hier der Index des
     * ersten <code>UserField</code>s in <code>formulaUserFieldList</code>
     * gespeichert, der sich nicht mehr berechnen lässt. Lassen sich alle
     * Formeln berechnen, dann ist der Index -1.
     */
    private int firstInconsistentUserFieldFormulaIndex = FORMULA_INCONSITENCE_INDEX_UNKNOWN;

    /**
     * Erzeugt eine neue UserFielddefinition für das übergebene Modell
     *
     * @param gdcoll
     */
    public UserFieldDefinitions(final GDCollection gdcoll) {
        super(gdcoll);
        calculator = new Calculator(this);
        partValueSumSinglePartResults = new PartValueSumSinglePartResults();
        weightReplacer = new WeightReplacer();
        definitionsAnalyzer = new UserFieldDefinitionsAnalyzer(this);
    }

    /**
     * @return Analyzer, über den der Zustand dieser
     *         {@link UserFieldDefinitions} abgefragt werden kann
     */
    public UserFieldDefinitionsAnalyzer getAnalyzer() {
        return definitionsAnalyzer;
    }

    /**
     * @param format
     */
    public void addNumberFormat(final UserFieldNumberFormat format) {
        String id = format.getID();
        formatIdToFormat.put(id, format);
    }

    ///////////////
    // ClassNode //
    ///////////////

    /**
     * @param targetClass
     * @return
     */
    public DefinitionUserFieldTargetClassNode getUserFieldTargetClassNode(final Class<? extends UserFieldTarget> targetClass) {
        return getOrCreateUserFieldTargetClassNode(targetClass);
    }

    /**
     * @param targetClass
     * @return
     */
    private DefinitionUserFieldTargetClassNode getOrCreateUserFieldTargetClassNode(final Class<? extends UserFieldTarget> targetClass) {
        DefinitionUserFieldTargetClassNode classNode = classToStructureNodeMap.get(targetClass);
        if (classNode == null) {
            String displayableName;
            if (ModelElement.class.isAssignableFrom(targetClass)) {
                ElementsNameBuilder elementsNameBuilder = getElementsNameBuilder();
                Class<? extends ModelElement> elementClass = targetClass.asSubclass(ModelElement.class);
                displayableName = elementsNameBuilder.getDisplayableName(elementClass);
            } else {
                displayableName = getResString("userFieldEditor_global");
            }
            classNode = new DefinitionUserFieldTargetClassNode(targetClass, displayableName);
            classToStructureNodeMap.put(targetClass, classNode);
            definitionRoot.add(classNode);
        }
        return classNode;
    }

    /////////////
    // SubType //
    /////////////

    /**
     * @param elementClass
     * @param name
     * @param description
     * @param id
     * @return
     */
    public DefinitionSubTypeNode addSubType(final Class<? extends ModelElement> elementClass, final String name, final String description, final String id) {
        DefinitionUserFieldTargetClassNode classNode = getOrCreateUserFieldTargetClassNode(elementClass);
        DefinitionSubTypeNode subTypeNode = new DefinitionSubTypeNode(name, description, id);
        classNode.add(subTypeNode);
        return subTypeNode;
    }

    //////////
    // Tabs //
    //////////

    /**
     * @param elementClass
     * @return
     */
    private DefinitionTabNode getOrCreateDefaultTab(final Class<? extends UserFieldTarget> elementClass) {
        DefinitionUserFieldTargetClassNode classNode = getOrCreateUserFieldTargetClassNode(elementClass);
        for (int i = 0; i < classNode.getChildCount(); i++) {
            LGMTreeNode<?> child = classNode.getChildAt(i);
            if (child instanceof DefinitionTabNode) {
                return (DefinitionTabNode) child;
            }
        }
        return addTab(classNode, Tool3lgmConstants.getResString("userfields"), null, null);
    }

    /**
     * @param elementClass
     * @param name
     * @param description
     * @param id
     * @return
     */
    public DefinitionTabNode addTab(final Class<? extends ModelElement> elementClass, final String name, final String description, final String id) {
        DefinitionUserFieldTargetClassNode classNode = getOrCreateUserFieldTargetClassNode(elementClass);
        return addTab(classNode, name, description, id);
    }

    /**
     * @param parent
     * @param name
     * @param description
     * @param id
     * @return
     */
    public DefinitionTabNode addTab(final DefinitionUserFieldTargetClassNode parent, final String name, final String description, final String id) {
        return addTab((IconifiedTreeNode<?>) parent, name, description, id);
    }

    /**
     * @param parent
     * @param name
     * @param description
     * @param id
     * @return
     */
    public DefinitionTabNode addTab(final DefinitionSubTypeNode parent, final String name, final String description, final String id) {
        return addTab((IconifiedTreeNode<?>) parent, name, description, id);
    }

    /**
     * @param parent
     * @param name
     * @param description
     * @param id
     * @return
     */
    private DefinitionTabNode addTab(final IconifiedTreeNode<?> parent, final String name, final String description, final String id) {
        DefinitionTabNode tabNode = new DefinitionTabNode(name, description, id);
        parent.add(tabNode);
        return tabNode;
    }

    ////////////////////////
    // UserFieldGroupNode //
    ////////////////////////

    private DefinitionGroupNode getOrCreateDefaultGroup(final Class<? extends UserFieldTarget> elementClass) {
        DefinitionTabNode defaultTabNode = getOrCreateDefaultTab(elementClass);
        if (defaultTabNode.getChildCount() > 0) {
            return defaultTabNode.getChildAt(0);
        }
        DefinitionGroupNode defaultGroupNode = addGroup(defaultTabNode, "", "", "");
        defaultGroupNode.setShowGroupAsTitledBorderOnTab(false);
        return defaultGroupNode;
    }

    public DefinitionGroupNode addGroup(final DefinitionTabNode tabNode) {
        DefinitionGroupNode groupNode = addGroup(tabNode, "", "", "");
        groupNode.setShowGroupAsTitledBorderOnTab(false);
        return groupNode;
    }

    /**
     * @param tabNode
     * @param name
     * @param description
     * @param id
     * @return
     */
    public DefinitionGroupNode addGroup(final DefinitionTabNode tabNode, final String name, final String description, final String id) {
        DefinitionGroupNode groupNode = new DefinitionGroupNode(name, description, id);
        tabNode.add(groupNode);
        return groupNode;
    }

    ///////////////
    // UserField //
    ///////////////

    /**
     * Hängt der zuletzt benutzen Liste ein neues Element an. Die Methode
     * erwartet beim Aufruf ein <code>UserField</code>. Das
     * <code>UserField</code> wird an eine Liste, die sich in der
     * <code>classToUserFieldListMap</code>- HashMap befindet, angehangen.
     * Methode wird beim Laden des Modells aufgerufen.
     *
     * @param userField
     */
    public DefinitionUserFieldNode addUserField(final UserField userField) {
        Class<? extends UserFieldTarget> targetClass = userField.getTargetClass();
        DefinitionGroupNode defaultGroupNode = getOrCreateDefaultGroup(targetClass);
        return addUserField(defaultGroupNode, userField);
    }

    /**
     * @param parent shoul be a {@link DefinitionGroupNode} or
     *            {@link DefinitionTabNode}
     * @param userField
     * @return
     */
    public DefinitionUserFieldNode addUserField(final DefinitionGroupNode parent, final UserField userField) {
        return addUserField(parent, userField, -1);
    }

    /**
     * @param parent shoul be a {@link DefinitionGroupNode} or
     *            {@link DefinitionTabNode}
     * @param userField
     * @param index
     * @return
     */
    public DefinitionUserFieldNode addUserField(final DefinitionGroupNode parent, final UserField userField, final int index) {
        UserField clone = userField.clone();
        DefinitionUserFieldNode userFieldNode = new DefinitionUserFieldNode(clone);
        if (index < 0 || index >= parent.getChildCount()) {
            parent.add(userFieldNode);
        } else {
            parent.insert(userFieldNode, index);
        }
        String id = clone.getID();
        idToUserFieldMap.put(id, clone);
        UserFieldNumberFormat numberFormat = clone.getNumberFormat();
        if (numberFormat != null) {
            addNumberFormat(numberFormat);
        }
        //Formeln extra merken
        if (clone.hasStyle(UserField.Style.FORMULA)) {
            formulaUserFieldList.add(clone);
            setConsistencyUnknown();
        }
        return userFieldNode;
    }

    /**
     * @param sibling
     * @param userField
     * @return
     */
    public DefinitionUserFieldNode addUserFieldAfter(final DefinitionUserFieldNode sibling, final UserField userField) {
        DefinitionGroupNode parent = sibling.getParent();
        int index = parent.getIndex(sibling);
        return addUserField(parent, userField, index);
    }

    /**
     * Fügt der zuletzt benutzen Liste ein neues Element ein. Positioniert am
     * übergebenen Index.
     *
     * @param parent shoul be a {@link DefinitionGroupNode} or
     *            {@link DefinitionTabNode}
     * @param userField
     * @param index
     */
    public void moveToPosition(final DefinitionStructureNode parent, final DefinitionUserFieldNode userFieldNode, final int index) {
        DefinitionStructureNode oldParent = userFieldNode.getParent();
        if (oldParent != null) {
            oldParent.remove(userFieldNode);
        }
        if (index >= 0) {
            parent.insert(userFieldNode, index);
        } else {
            parent.add(userFieldNode);
        }
    }

    ////////////
    // Remove //
    ////////////

    /**
     * @param userFieldNode
     */
    public void remove(final DefinitionUserFieldNode userFieldNode) {
        UserField userField = userFieldNode.getUserObject();
        remove(userField);
    }

    /**
     * @param structureNode
     */
    public void remove(final DefinitionStructureNode structureNode) {
        for (DefinitionUserFieldNode userFieldNode : structureNode.getUserFieldNodes()) {
            remove(userFieldNode);
        }
        structureNode.removeFromParent();
    }

    /**
     * Liefert <code>true</code>, wenn das übergebene <code>UserField</code> von
     * anderen <code>UserField</code>s benutzt wird.<br>
     * Das trifft bei Kennzahlformeln, Knenzahlen und Verteilungsgewichten zu,
     * die in anderen Kennzahlformeln verwendet werden. Außerdem wird für zu
     * löschende Format-UserFields geprüft, ob sie mind. einem anderen UserField
     * als Format zugewiesen sind.
     *
     * @param userField
     * @return
     */
    public final boolean isInUse(final UserField userField) {
        //wenn es nichts mit Kennzahlen zu tun hat -> false
        if (!userField.isNumberUserField()) {
            return false;
        }
        for (UserField formula : formulaUserFieldList) {
            if (formula.uses(userField)) {
                return true;
            }
        }
        return false;
    }

    /**
     * @param numberFormat
     */
    public void removeNumberFormat(final UserFieldNumberFormat numberFormat) {
        for (UserField userField : this) {
            if (userField.hasNumberFormat(numberFormat)) {
                userField.removeNumberFormat();
            }
        }
        String formatID = numberFormat.getID();
        formatIdToFormat.remove(formatID);
    }

    /**
     * @param userField
     * @return Liste aller gelöschten <code>UserField</code>s
     */
    public List<UserField> remove(final UserField userField) {
        ArrayList<UserField> deleted = new ArrayList<>();
        //wenn das userField irgendwo anders noch benutzt wird -> lösche die Referenzen ebenfalls
        //wenn das zu löschende UserField ein UserField ist, das bei einem anderen in der Formel vorkommen kann (Kennzahl, Kennzahlformel, Verteilungsgewicht)
        if (userField.isNumberUserField()) {
            ArrayList<UserField> userFieldsToDelete = new ArrayList<>();
            userFieldsToDelete.add(userField);
            for (int i = 0; i < userFieldsToDelete.size(); i++) {
                UserField uncheckedField = userFieldsToDelete.get(i);
                for (int j = 0; j < formulaUserFieldList.size(); j++) {
                    UserField formulaUserField = formulaUserFieldList.get(j);
                    if (userFieldsToDelete.contains(formulaUserField)) {
                        continue;
                    }
                    if (formulaUserField.uses(uncheckedField)) {
                        userFieldsToDelete.add(formulaUserField);
                    }
                }
            }
            //wenn mehr als ein Feld gelöscht werden soll, warnen
            if (userFieldsToDelete.size() > 1) {
                ArrayList<UserField> tmpList = new ArrayList<>(userFieldsToDelete);
                if (userFieldsToDelete.size() > MAX_USED_USERFIELD_DELETE_NUMBER) {
                    tmpList.clear();
                    for (int i = 0; i < 10; i++) {
                        tmpList.add(userFieldsToDelete.get(i));
                    }
                }
                int answer = MultipleOptionPane.showConfirmDialog(Static.getMainFrame(), getResString("warnung"), getResString("userfield_still_in_use") + "\n" + getResString("insgesamt") + ": " + userFieldsToDelete.size() + "\n" + tmpList + " ... ",
                        JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
                if (answer != JOptionPane.YES_OPTION) {
                    return deleted;
                }
            }
            if (formulaUserFieldList.removeAll(userFieldsToDelete)) {
                setConsistencyUnknown();
            }

            for (UserField field : userFieldsToDelete) {
                removeUserFieldInternal(field);
            }
            deleted.addAll(userFieldsToDelete);
            //bei allem, was nicht mit Kennzahlen zu tun hat -> einfach löschen
        } else {
            removeUserFieldInternal(userField);
            deleted.add(userField);
        }
        return deleted;
    }

    /**
     * @param userField
     */
    private void removeUserFieldInternal(final UserField userField) {
        String id = userField.getID();
        idToUserFieldMap.remove(id);
        if (userField.hasStyle(Style.FORMULA)) {
            formulaUserFieldList.remove(userField);
        }
        Class<? extends UserFieldTarget> targetClass = userField.getTargetClass();
        DefinitionUserFieldTargetClassNode classNode = classToStructureNodeMap.get(targetClass);
        classNode.removeChildNodesWith(userField);
    }

    @Override
    public UserFieldDefinitions clone() {
        UserFieldDefinitions clone = null;
        try {
            clone = (UserFieldDefinitions) super.clone();
            //this deep clones all tree nodes and all userFields in the tree nodes
            clone.definitionRoot = (DefinitionStructureNode) clone.definitionRoot.clone();
            //set the deep clones in the main map
            clone.classToStructureNodeMap = new HashMap<>();
            for (int i = 0; i < clone.definitionRoot.getChildCount(); i++) {
                DefinitionUserFieldTargetClassNode cloneTargetClassNode = (DefinitionUserFieldTargetClassNode) clone.definitionRoot.getChildAt(i);
                Class<? extends UserFieldTarget> targetClass = cloneTargetClassNode.getUserObject();
                clone.classToStructureNodeMap.put(targetClass, cloneTargetClassNode);
            }
            //deep clone all formats
            for (String formatId : formatIdToFormat.keySet()) {
                UserFieldNumberFormat format = formatIdToFormat.get(formatId);
                format = format.clone();
                clone.formatIdToFormat.put(formatId, format);
            }
            //replace all userFields in the ID-to-UserField-map by the clones
            for (UserField clonedUserField : clone) {
                String id = clonedUserField.getID();
                idToUserFieldMap.put(id, clonedUserField);
            }
            //replace all original formula UserFields by the clones in the formula list in the same order like in the original list
            for (int i = 0; i < formulaUserFieldList.size(); i++) {
                UserField formula = formulaUserFieldList.get(i);
                String formulaID = formula.getID();
                UserField clonedFormula = idToUserFieldMap.get(formulaID);
                formulaUserFieldList.set(i, clonedFormula);
            }
            //initialize the Calculator for the cloned definitions
            clone.calculator = new Calculator(clone);
            //initialize the Analyzer for the cloned definitions
            clone.definitionsAnalyzer = new UserFieldDefinitionsAnalyzer(clone);
            return clone;

        } catch (CloneNotSupportedException e) {
            //this should never happen since we are cloneable
            throw new InternalError(e);
        }
    }

    /**
     * @param otherDef
     */
    public void _addAll(final UserFieldDefinitions otherDef) {
        //TODO: #382: prüfen, ob man das hier überhaupt braucht, es wird nirgends genutzt und müsste erst berichtigt werden
        //        Iterable<UserField> elementClassUserFields = otherDef.getElementClassUserFields();
        //        Iterable<UserFieldNumberFormat> numberFormats = otherDef.getNumberFormats();
        //        addAll(numberFormats, elementClassUserFields);
        //        for (Class<? extends UserFieldTarget> clazz : otherDef.classToUserFieldListMap.keySet()) {
        //            for (UserField uf : otherDef.classToUserFieldListMap.get(clazz)) {
        //                addUserField(uf);
        //            }
        //        }
    }

    /**
     * @param otherDef
     */
    public void addAll(final CopyDependencyResolverResultSimple resolvedCopyDependencies) {
        //TODO: #382: das hier fügt die Benutzerdefinierten Eigenschaften immer auf dem Default-Tab ein. Das CopyDependencyResolverResultSimple müsste statt der UserFields die DefinitionUserFieldNode übernehmen und so die Tab-Position beachten
        addAll(resolvedCopyDependencies.userFieldNumberFormats, resolvedCopyDependencies.userFields);
    }

    /**
     * @param numberFormats
     * @param userFields
     */
    private void addAll(final Iterable<UserFieldNumberFormat> numberFormats, final Iterable<UserField> userFields) {
        for (UserFieldNumberFormat numberFormat : numberFormats) {
            addNumberFormat(numberFormat);
        }
        for (UserField userField : userFields) {
            addUserField(userField);
        }
    }

    /**
     * Liefert ein Benutzerfeld, das anhand des Namens herausgesucht wird.
     * ACHTUNG: Es wird immer nur das erste mit dem übergebenen Namen gefunden.
     * Gebraucht werden sollte diese Funktion nur beim Import von Daten, da der
     * Name kein eindeutiges Kriterium ist.
     *
     * @param userFieldTargetClass
     * @param name
     * @return das erstebeste UserField mit dem übergebenen Namen oder
     *         <code>null</code>, wenn keins gefunden wurde
     */
    public UserField getUserField(final Class<? extends UserFieldTarget> userFieldTargetClass, final String name) {
        DefinitionUserFieldTargetClassNode targetClassNode = classToStructureNodeMap.get(userFieldTargetClass);
        if (targetClassNode != null) {
            for (UserField userField : targetClassNode) {
                String userFieldName = userField.getName();
                if (userFieldName.equals(name)) {
                    return userField;
                }
            }
        }
        return null;
    }

    /**
     * @param userFieldTargetClass
     * @return <code>true</code> if there is at least one {@link UserField}
     *         defined for the userFieldTargetClass
     */
    public boolean hasUserFields(final Class<? extends UserFieldTarget> userFieldTargetClass) {
        return getUserFields(userFieldTargetClass).iterator().hasNext();
    }

    /**
     * @param userFieldTargetClass
     * @return
     */
    public Iterable<UserField> getUserFields(final Class<? extends UserFieldTarget> userFieldTargetClass) {
        DefinitionUserFieldTargetClassNode targetClassNode = classToStructureNodeMap.get(userFieldTargetClass);
        if (targetClassNode == null) {
            return ImmutableList.of();
        }
        return targetClassNode;
    }

    /**
     * @param userFieldTargetClass
     * @return
     */
    public List<UserField> getUserFields(final Class<? extends UserFieldTarget> userFieldTargetClass, final UserField.Style style) {
        return getUserFields(userFieldTargetClass, ImmutableSet.of(style));
    }

    /**
     * @param userFieldTargetClass
     * @return
     */
    public List<UserField> getUserFields(final Class<? extends UserFieldTarget> userFieldTargetClass, Set<UserField.Style> styles) {
        ArrayList<UserField> returnList = new ArrayList<>();
        if (styles != null && styles.isEmpty()) {
            styles = null;
        }
        DefinitionUserFieldTargetClassNode targetClassNode = classToStructureNodeMap.get(userFieldTargetClass);
        for (UserField userField : targetClassNode) {
            if (styles == null || styles.contains(userField.getStyle())) {
                returnList.add(userField);
            }
        }
        return returnList;
    }

    /**
     * Liefert eine Liste aller UserFields mit einfachen Teilwertsummenformeln,
     * die für die übergebene Element- und Kantenklasse definiert sind.
     *
     * @param me Elementklasse, für die das UserField mit der einfachen
     *            Teilwertsummenformel definiert ist
     * @param edgeClass Kantenklasse über die die einfache Teilwertsummenformel
     *            rechnnetr
     * @return
     */
    public List<UserField> getFractionValueSumUserFields(final Class<? extends ModelElement> elementClass, final Class<? extends Edge> edgeClass) {
        List<UserField> userFieldList = getUserFields(elementClass, UserField.Style.FORMULA);
        for (int i = userFieldList.size() - 1; i >= 0; i--) {
            UserField userField = userFieldList.get(i);
            if (!userField.isSimplePartValueSumFormula()) {
                userFieldList.remove(i);
            } else {
                TWSumArguments arguments = PartValueSumFunction.getTWSumArguments(this, userField);
                if (!edgeClass.isAssignableFrom(arguments.edgeClass)) {
                    userFieldList.remove(i);
                }
            }
        }
        return userFieldList;
    }

    /**
     * @return
     */
    public Iterable<UserField> getGlobalUserFields() {
        return getUserFields(GLOBAL_USERFIELD_IDENTIFIER_CLASS);
    }

    /**
     * @return
     */
    public Iterable<UserFieldNumberFormat> getNumberFormats() {
        return formatIdToFormat.values();
    }

    /**
     * @param formatId
     * @return
     */
    public UserFieldNumberFormat getNumberFormat(final String formatId) {
        return formatIdToFormat.get(formatId);
    }

    /**
     * @return
     */
    public Iterable<UserField> getElementClassUserFields() {
        ImmutableList.Builder<Iterable<UserField>> iterables = new ImmutableList.Builder<>();
        for (int i = 0; i < definitionRoot.getChildCount(); i++) {
            DefinitionUserFieldTargetClassNode targetClassNode = (DefinitionUserFieldTargetClassNode) definitionRoot.getChildAt(i);
            Class<? extends UserFieldTarget> targetClass = targetClassNode.getUserObject();
            if (targetClass != GLOBAL_USERFIELD_IDENTIFIER_CLASS) {
                iterables.add(targetClassNode);
            }
        }
        ImmutableList<Iterable<UserField>> build = iterables.build();
        return CollectionUtils.getCommonIterable(build);
    }

    /**
     * @return all id userfields for all target classes
     */
    public List<UserField> getIDUserFields() {
        ArrayList<UserField> idUserFields = new ArrayList<>();
        for (UserField userField : this) {
            if (userField.getStyle() == UserField.Style.ID) {
                idUserFields.add(userField);
            }
        }
        return idUserFields;
    }

    /**
     * @return the weightReplacer
     */
    public WeightReplacer getWeightReplacer() {
        return weightReplacer;
    }

    /**
     * Die Methode <code>get</code> gibt unter Angabe der zugehörigen Klasse und
     * des entsprechenden Indices ein <code>UserField</code> zurück. Es wird aus
     * der HashMap die zur übergebenen Klasse gehörende ArrayList geladen, falls
     * sie nicht schon geladen ist, und das Element an der Stelle
     * <code>index</code> zurückgegeben.
     *
     * @param userFieldTargetClass
     * @param index
     * @return UserField / public UserField get(Class<?> userFieldTargetClass,
     *         int index) { UserFieldList ufl =
     *         classToUserFieldListMap.get(userFieldTargetClass); if (ufl !=
     *         null) return ufl.get(index); return null; } /** Liefert das
     *         globale {@link UserField} mit dem entsprechenden Index (oder
     *         <code>null</code> wenn es kein solches gibt.
     * @param index
     * @return
     * @see #get(Class, int) / public UserField getGlobal(int index) { return
     *      get(GLOBAL_USERFIELD_IDENTIFIER_CLASS, index); } /** Gibt
     *      <code>UserField</code> zurück, für das die ID angegeben wurde.
     * @param id
     * @return <code>UserField</code>
     */
    public UserField getUserField(final String id) {
        return idToUserFieldMap.get(id);
    }

    /**
     * Prüft für ein übergebenes <code>UserField</code>, ob es zu den
     * berechenbaren Formel-UserFields gehört.
     *
     * @param formulaUserField
     * @return <code>true</code>, wenn es ein Formel-UserField ist, das
     *         berechnet werden kann, sonst <code>false</code>
     */
    public boolean isCalculatable(final UserField formulaUserField) {
        int index = formulaUserFieldList.indexOf(formulaUserField);
        if (index >= 0 && index < firstInconsistentUserFieldFormulaIndex) {
            return false;
        }
        return true;
    }

    /**
     * Prüft, ob die Formeldefinitionen konsistent sind - also ob sich die
     * Formeln nicht im Kreis referenzieren.
     *
     * @return <code>false</code>, wenn alle Formeln berechnet werden können,
     *         sonst <code>true</code>
     */
    public boolean hasCrossReferences() {
        //wenn irgendwas an den Kennzahlformeldefinitionen geändert wurde -> prüfe die Kreisreferenzen in den Formeln
        if (firstInconsistentUserFieldFormulaIndex == FORMULA_INCONSITENCE_INDEX_UNKNOWN) {
            ArrayList<UserField> inconsistentUserFields = makeFormulaUserFieldListConsistent();
            if (inconsistentUserFields == null) {
                firstInconsistentUserFieldFormulaIndex = NO_INCONSISTENCE_INDEX_FOUND;
                return false;
            }
            //den Index des ersten nicht konsistenten UserFields in der Formel-UserField-Liste merken
            firstInconsistentUserFieldFormulaIndex = formulaUserFieldList.size() - inconsistentUserFields.size();
            StringBuilder sb = new StringBuilder(getResString("circuit_reference"));
            for (int i = 0; i < inconsistentUserFields.size(); i++) {
                sb.append("\n");
                sb.append(inconsistentUserFields.get(i).getName());
            }
            MultipleOptionPane.showInformationMessageDialog(Static.getMainFrame(), getResString("fehler"), sb.toString());
            return true;
        }
        return false;
    }

    /**
     * Prüft, ob sich ein FormeluserField in seiner Formel außerhalb einer
     * verrechnungsfunktion selbst referenziert.
     *
     * @param userField
     * @return
     */
    public static boolean hasSimpleCrossReferences(final UserField userField) {
        String formula = userField.getFormula();

        if (formula == null) {
            return false;
            // UserFields dürfen sich nur in PartOf-Beziehungen in
            // Verrechnungsfunktionen selbst referenzieren,
            // da dann ja immer nur das UserField der verbundenen Elemente abgefragt
            // wird.
            // Es darf aber nicht außerhalb einer Verrechnungfunktion in der Formel
            // auftauchen, da es sich dann
            // für ein und dasselbe Element (für das es berechnet werden soll) auf
            // sich selbst bezieht
        }

        // Prüfen kann man das, in dem man darauf hin prüft, ob vor dem
        // userFieldID, der Kennzahl, die überprüft werden soll, ein "|" (
        // Calculator.OPERAND_DELIMITER ) steht. Dann und nur dann, liegt keine
        // Kreisreferenz vor.
        // Das ist nämlich genau dann der Fall, wenn das userField in einer
        // Verrechnungsfunktion vorkommt und in diesem Fall, ist es gestattet.

        StringTokenizer st = new StringTokenizer(formula);

        String firstString = "";
        String secondString = "";

        if (st.hasMoreTokens()) {
            firstString = st.nextToken();
        }
        // Hier wird geprüft, ob immer vor dem userField der Delimiter steht.
        // Wenn das nicht der Fall ist, ist dieFormel nicht korrekt und es liegt
        // eine Kreisreferenz vor.
        while (st.hasMoreTokens()) {
            secondString = st.nextToken();

            if (secondString.equals(userField.getID())) {
                if (!firstString.equals(Calculator.OPERAND_DELIMITER)) {
                    return true;
                }
            }
            firstString = secondString;
        }

        // Prüfen, ob in der einen Formel mind. zwei Verrechnungsfunktionen
        // angegeben sind, die aus unterschiedlichen Richtungen rechnen wollen.

        // Das Vorgehen:
        // In einer Schleife:
        // 1) Durchsuche den Stringtokenizer nach userFieldIDs, die gleich dem eigenen userfield sind.
        // 2) Wenn so einer gefunden wurde, handelt es sich um eine interne Verrechnung.
        //    Suche die Richtung: Prüfe, ob nach der userFieldID erst ein Verteilungsgewicht angegeben wurde.
        //    (Das ist auch eine userFieldID)
        // 3) Wenn die Richtung gefunden wurde, prüfe, ob die Variable direction noch leer ist,
        //      wenn ja, setze die Variable mit den Richtungswert.
        //      Wenn nein, dann prüfe, ob der Richtungswert gleich dem der Variable ist.
        //          Wenn ja, in der Schelife weitermachen
        //          Wenn nein: es liegt eine Kreisrefenz vor. Ausstieg aus der Schleife mit return true (true = es liegt eine Kreis.-ref. vor).
        //
        //  Durch das einmalige Setzen der Richtung, weiß man, welche Richtungen alle folgenden internen Verrechnungsfunktionen haben müssen.
        //  Diese wird also als Referenzrichtung genutzt.

        st = new StringTokenizer(formula);

        firstString = "";
        secondString = "";
        String direction = "";

        while (st.hasMoreTokens()) {
            firstString = st.nextToken();

            if (firstString.equals(userField.getID())) {

                // Das nächtse Zeichen holen, wenn es ein Delimiter ist, steht
                // dahinter entweder die Richtung oder im Falle einer TWSUM kann
                // auch erst noch das zu nuzende VG stehen und dann erst die
                // Richtung.
                // Diese muss gemerkt werden.
                if (st.hasMoreTokens()) {
                    secondString = st.nextToken();
                }
                if (secondString.equals(Calculator.OPERAND_DELIMITER)) {
                    if (st.hasMoreTokens()) {
                        secondString = st.nextToken();
                    }
                    // Wenn wieder auf das eigene UserField gestoßen wird, muss
                    // geprüft werden, ob erst ein Verteilungsgewicht angegeben
                    // wurde.
                    if (secondString.startsWith(UserField.USERFIELD_ID_PREFIX)) {
                        // Wenn erst ein Verteilungsgewicht angegeben wurde,
                        // muss als nächstes der Delimiter und als nächstes die
                        // Richtung geholt werden
                        if (st.hasMoreTokens()) {
                            secondString = st.nextToken();
                        }
                        if (secondString.equals(Calculator.OPERAND_DELIMITER)) {
                            if (st.hasMoreTokens()) {
                                // spätestens an dieser Stelle kommt auf jeden
                                // Fall die Richtung
                                secondString = st.nextToken();
                            }
                        }

                        if (secondString.equals(UserField.DIRECTION_FROM_PART_TO_WHOLE) || secondString.equals(UserField.DIRECTION_FROM_WHOLE_TO_PART)) {
                            direction = secondString;
                        }
                    } else
                    // Wenn es kein Verteilungsgewicht ist, gehts gleich hier weiter.
                    if (direction.equals("")) {
                        //Die erste Richtung, die gefunden wurde, wird als die Vergleichsrichtung angesegen.
                        //Alle anderen Richtungen in Verrechnungsfuntionen, die sich selbst verrechnen, müssen die selbe Richtung haben.
                        direction = secondString;
                    } else if (!direction.equals(secondString)) {
                        return true;
                    }

                }
            }
        }
        return false;
    }

    /**
     * @return Iterator aller Schlüsselwerte der Map, die von den Klassen auf
     *         die für sie definierten UserFields mappt
     */
    public Set<Class<? extends UserFieldTarget>> getClassToUserFieldKeys() {
        return classToStructureNodeMap.keySet();
    }

    /////////////////////////////////////////////////////////////////////////////
    // Util-Funktionen zur Sicherung der Konsistenz der UserFields mit Formeln //
    /////////////////////////////////////////////////////////////////////////////

    /**
     * Sortiert die Liste <code>formulaUserFieldList</code> so, dass sie
     * konsistent ist. Siehe Kommentar zur Variable
     * <code>formulaUserFieldList</code>.
     *
     * @return Liste von <code>UserField</code>s, die sich im Kreus
     *         referenzieren oder von solchen Elementen abhängig sind bzw.
     *         <code>null</code>, wenn es keine Kreisreferenzen gibt
     */
    private ArrayList<UserField> makeFormulaUserFieldListConsistent() {
        ArrayList<UserField> calculateableFormulaList = new ArrayList<>(formulaUserFieldList.size());
        while (true) {
            //Größe der Liste der berechenbaren USerFields merken -> nur wenn sie in jedem
            //Durchlauf wächst, sind die Formeln konsistent
            int lastSortedListSize = calculateableFormulaList.size();
            for (int i = 0; i < formulaUserFieldList.size(); i++) {
                UserField u = formulaUserFieldList.get(i);

                if (hasSimpleCrossReferences(u)) {
                    continue;
                }

                boolean allDependingAreCalculateable = true;
                String formula = u.getFormula();
                if (formula == null) {
                    return null;
                }
                StringTokenizer st = new StringTokenizer(formula, Calculator.ALL_IN_FUNCTION_SIGNS);
                while (st.hasMoreElements()) {
                    String token = st.nextToken();
                    if (token.startsWith(UserField.USERFIELD_ID_PREFIX)) {
                        //hole das UserField von dem die aktuelle Formel abhängig ist
                        UserField dependingUserField = getUserField(token);
                        //wenn jetzt die Formel sich selbst enthält, dann ist das zulässig (oben wurden alle
                        //unzulässigen Selbstreferenzen von UserFilds schon geprüft)
                        if (u.equals(dependingUserField)) {
                            continue;
                        }
                        //wenn das auch eine Formel ist
                        if (dependingUserField != null && dependingUserField.hasStyle(UserField.Style.FORMULA)) {
                            //wenn die abhängige Formel noch nicht in der Liste der berechenbaren Formeln vorkommt
                            if (!calculateableFormulaList.contains(dependingUserField)) {
                                allDependingAreCalculateable = false;
                                break;
                            }
                        }
                    }
                }
                //wenn alle UserFields in der aktuellen Formel bereits in der Liste der berechenbaren vorkommen
                if (allDependingAreCalculateable) {
                    //füge diese Formel zur Liste der berechenbaren Formeln hinzu
                    calculateableFormulaList.add(u);
                    //das UserField aus der Urprungsliste entfernen und den Index dementsprechend verringern
                    formulaUserFieldList.remove(i--);
                    //prüfe das nächste UserField aus der unsortierten Liste
                    break;
                }

            }
            //wenn alle Formeln in die Liste der berechenbaren einsortiert werden konnten
            if (formulaUserFieldList.size() == 0) {
                //setzte die glovlae Liste auf die nun sortierte Liste
                formulaUserFieldList = calculateableFormulaList;
                return null;
            }
            //wenn die ganze Liste durchlaufen wurde, aber keine Kennzahlformel mehr zu den berechenbaren hinzugefügt
            //werden konnte, obwohl noch welche in der unsortierten Liste sind -> die noch in der Liste formulaUserFieldList
            //enthaltenen UserFields refrenzieren sich an mindesten einer Stelle in ihren Formeln gegenseitig (es reicht
            //schon, dass sich 2 Formeln gegenseitig referenzieren, von denen dann der ganze Rest abhängt)
            if (lastSortedListSize == calculateableFormulaList.size()) {
                ArrayList<UserField> inconsistentUserFields = new ArrayList<>(formulaUserFieldList);
                //füge zur globalen Liste wieder alle entfernten Elemente hinzu
                calculateableFormulaList.addAll(formulaUserFieldList);
                formulaUserFieldList = calculateableFormulaList;
                //gib die Elemente zurück, in denen sich mind. ein Kreis befindet
                return inconsistentUserFields;
            }
        }
    }

    //    /**
    //     * Diese Funktion ist nötig, da beim Einlesen der Style eines Format-UserFields noch nicht erkannt
    //     * wird und diese Format-UserFields erstmal als globale UserFields angelegt werden. Erst wenn der
    //     * Style.FORMAT eingelesen wird kann man wissen, dass man diese UserFields in exhte FormatUserFields
    //     * umwandeln muss. Das geschieht hier.
    //     * Voraussetzung ist, dass vor dem Aufrufen dieser Funktion bereits die TargetClass des UserFields
    //     * auf GLOBAL_FORMAT_IDENTIFIER_CLASS gesetzt wurde und das UserField bereits als globale Variable
    //     * in den Definitions steht (also als UserField für die TargetClass GLOBAL_USERFIELD_IDENTIFIER_CLASS)
    //     *
    //     * @param userField
    //     */
    //    void MakeGlobalUserFieldToFormat(final UserField userField) {
    //        if (userField.getTargetClass() == GLOBAL_FORMAT_IDENTIFIER_CLASS) {
    //            classToUserFieldListMap.get(GLOBAL_USERFIELD_IDENTIFIER_CLASS).remove(userField);
    //            add(userField);
    //        }
    //    }
    //
    //////////////////////////////////////////////////////////////////////////////////////////////////////
    // Funktionen zur Berechnung der Kennzahlformeln (das eigentliche Berechnen passiert im Calculator) //
    //////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    protected void clearCalculatedUserFieldValues() {
        //für alle Elementklassen alle berechneten Werte zurück setzen - also löschen

        partValueSumSinglePartResults.clear();

        //Menge alle Elementklassen, für die bereits alle UserFields gelöscht wurden (alle
        //FormelUserFields werden für alle Elemente einer Art immer komplett gelöscht, sobald
        //in der Liste aller Formel-UserFields (formulaUserFieldList) ein UserField für die
        //betreffende Elementart gefunden wurde. Beim nächsten UserField für diese Elementart
        //braucht man nicht noch einmal alle UserFields zu löschen)
        Set<Class<?>> resetedElementClasses = new HashSet<>(15);

        //Das Hauptdokument der GDCollection holen (UserField-Änderungen gelten immer für alle
        //Elemente, also immer im Hauptdokument arbeiten)
        GraphDocument doc = gdcoll.getMainDoc();

        //Für alle Kennzahlformel-UserFields
        for (UserField userField : formulaUserFieldList) {
            Class<? extends UserFieldTarget> userFieldTargetClass = userField.getTargetClass();
            //Wenn bereits alle Formel-UserFields der Elementart des aktuellen UserFields gelöscht wurden
            if (resetedElementClasses.contains(userFieldTargetClass)) {
                //nächstes Formel-UserField prüfen
                continue;
            }
            //Elementklasse des UserFields als bereits zurück gesetzt merken
            resetedElementClasses.add(userFieldTargetClass);
            //alle berechneten Modellvariablen zurück setzen
            if (userField.isGlobal()) {
                gdcoll.resetCalculatedUserFieldMap();
                continue;
            }
            //alle berechnten Elementvariablen zurück setzen (das kann hier nur noch eine Unterklasse von ModelElement sein)
            for (UserFieldTarget userFieldTarget : doc.getModelItems(userFieldTargetClass.asSubclass(ModelElement.class))) {
                userFieldTarget.resetCalculatedUserFieldMap();
            }
        }
    }

    /**
     * Berechnet den Wert des übergebenen <code>UserField</code>s für das
     * übergebene <code>UserFieldTarget</code>.
     *
     * @param userField
     * @param target
     * @return <code>UserField.ERROR_OBJECT_VALUE</code>, wenn die Berechnung
     *         nicht durchgeführt werden konnte oder den
     *         <code>Double</code>-Wert als <code>String</code>
     */
    protected final String calculate(final UserField userField, final UserFieldTarget target) {
        if (!isCalculatable(userField)) {
            return UserField.ERROR_CROSS_REFERENCE_IN_FORMULA_DEFINITION;
        }

        //      if (count==0){
        //          count++;
        //          GraphDocument doc = gdcoll.getGraphDocument();
        //          ArrayList elems = doc.getAllModelElements(RechAnwendungsbaustein.class, true);
        //          for (int i=0; i<getUserFieldCount(RechAnwendungsbaustein.class); i++){
        //              UserField userField = get(RechAnwendungsbaustein.class, i);
        //              if (userField.getStyle()==Style.NUMBER){
        //                  for (int j=0; j<elems.size(); j++){
        //                      UserFieldTarget uft = (UserFieldTarget)elems.get(j);
        //                      if (uft.getUserFieldInputValue(userField).equals(UserField.EMPTY_STRING))
        //                          uft.setUserFieldInputValue(userField, "0");
        //                  }
        //              }
        //          }
        //      }

        return calculator.calculate(userField, target);
    }

    /**
     * Immer wenn sich was an den Definitionen ändert muss diese Funktion
     * aufgerufen werden, damit die Definition auf CrossReferences geprüft wird.
     * Beim Hinzufügen oder Entfernen von UserFields macht die Definition das
     * allein, aber wenn von einer Kennzahlformel der FormelString geändert
     * wird, muss das der Ändernde machen.
     */
    public void setConsistencyUnknown() {
        firstInconsistentUserFieldFormulaIndex = FORMULA_INCONSITENCE_INDEX_UNKNOWN;
        initReset();
    }

    /**
     * @param numberFormat Format, für das alle Kennzahlen zurück gegeben werden
     *            sollen, die es benutzen
     * @return Liste aller UserFields, die das übergebene Format benutzen
     */
    public List<UserField> getUserFieldsWithNumberFormat(final UserFieldNumberFormat numberFormat) {
        List<UserField> returnList = new ArrayList<>();
        for (Class<? extends UserFieldTarget> elementClass : getClassToUserFieldKeys()) {
            for (UserField userField : getUserFields(elementClass)) {
                if (userField.hasNumberFormat(numberFormat)) {
                    returnList.add(userField);
                }
            }
        }
        return returnList;
    }

    /**
     * @return the partValueSumSinglePartResults
     */
    public PartValueSumSinglePartResults getPartValueSumSinglePartResults() {
        return partValueSumSinglePartResults;
    }

    /**
     * @return
     */
    public Set<Class<? extends UserFieldTarget>> getUserFieldTargets() {
        return classToStructureNodeMap.keySet();
    }

    /**
     * @return
     */
    public String getDebugString() {
        StringBuilder sb = new StringBuilder();
        List<Class<? extends UserFieldTarget>> sortedKeys = Alphabetical.getSorted(getClassToUserFieldKeys());
        sb.append("classToUserFieldListMap");
        sb.append("\n-----------------------\n");
        for (Class<?> keyClass : sortedKeys) {
            sb.append(keyClass.getSimpleName());
            sb.append("\n");
            for (Object o : classToStructureNodeMap.get(keyClass)) {
                UserField uf = (UserField) o;
                if (uf == null) {
                    sb.append("null#############\n");
                    continue;
                }
                sb.append("\t");
                sb.append(uf.getID());
                sb.append("\n\t\t");
                sb.append(uf.getName());
                sb.append("\n");
            }
        }
        sb.append("\nformatIdToFormat");
        sb.append("\n----------------\n");
        sb.append(ExtendedMap.toString(formatIdToFormat));
        sb.append("idToUserFieldMap)");
        sb.append("\n-----------------\n");
        sb.append(ExtendedMap.toString(idToUserFieldMap));
        return sb.toString();
    }

    @Override
    public Iterator<UserField> iterator() {
        return definitionRoot.iterator();
    }

}
