package de.imise.tool3lgm.graphtools.userfield.definition;

import static de.imise.tool3lgm.Tool3lgmConstants.getResString;

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
import de.imise.tool3lgm.graphtools.userfield.event.UserFieldDefinitionChangeHandler;
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
public final class UserFieldDefinitions extends UserFieldDefinitionChangeHandler implements Cloneable {

    /**
     * Klasse, über die die sogenannten Modellvariablen identifiziert werden,
     * also Variablen, die nicht für ein spezielles Element sondern für das
     * Gesamtmodell gelten und zur Verfügung stehen.
     */
    public static final Class<? extends UserFieldTarget> GLOBAL_USERFIELD_IDENTIFIER_CLASS = GDCollection.class;

    /**
     * Mappt von der Elementklasse auf die dafür definierte Liste von
     * <code>UserField</code>s
     */
    private Map<Class<? extends UserFieldTarget>, UserFieldList> classToUserFieldTargetSpecificListMap = new HashMap<>();

    /** Maps from the ID of a format to the format */
    private final Map<String, UserFieldNumberFormat> formatIdToFormat = new HashMap<>();

    /** Mappt von den IDs der UserFields auf das UserField */
    private Map<String, UserField> idToUserFieldMap = new HashMap<>();

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
    private List<UserField> formulaUserFieldTargetSpecificList = new ArrayList<>();

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
     * anzugeben, dass die Liste <code>formulaUserFieldTargetSpecificList</code>
     * neu sortiert werden müsste, um festzustellen, ob sich alle Formeln
     * berechnen lassen bzw. welche inkonsitent sind.
     */
    private static final int FORMULA_INCONSITENCE_INDEX_UNKNOWN = -2;

    /**
     * Die Liste <code>formulaUserFieldTargetSpecificList</code> wird (wenn
     * keine Kreisreferenzen in den Formeln vorkommen) so sortiert, dass sich
     * jede Formel in der Liste berechnen lässt, wenn alle Formeln berechnet
     * wurden, die sich in der Liste davor befinden. Sollte doch mind. eine
     * Kreisreferenz vorliegen, wird in dieser Variable hier der Index des
     * ersten <code>UserField</code>s in
     * <code>formulaUserFieldTargetSpecificList</code> gespeichert, der sich
     * nicht mehr berechnen lässt. Lassen sich alle Formeln berechnen, dann ist
     * der Index -1.
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
    public void add(final UserFieldNumberFormat format) {
        String id = format.getID();
        formatIdToFormat.put(id, format);
    }

    /**
     * Hängt der zuletzt benutzen Liste ein neues Element an. Die Methode
     * erwartet beim Aufruf ein <code>UserField</code>. Das
     * <code>UserField</code> wird an eine Liste, die sich in der
     * <code>classToUserFieldTargetSpecificListMap</code>- HashMap befindet,
     * angehangen. Methode wird beim Laden des Modells aufgerufen.
     *
     * @param userField
     */
    public void add(final UserField userField) {
        UserField clone = userField.clone();
        Class<? extends UserFieldTarget> targetClass = clone.getTargetClass();
        UserFieldList ufl = classToUserFieldTargetSpecificListMap.get(targetClass);
        if (ufl == null) {
            ufl = new UserFieldList(targetClass);
            classToUserFieldTargetSpecificListMap.put(targetClass, ufl);
        }
        ufl.add(clone);
        String id = clone.getID();
        idToUserFieldMap.put(id, clone);
        UserFieldNumberFormat numberFormat = clone.getNumberFormat();
        if (numberFormat != null) {
            add(numberFormat);
        }
        //Formeln extra merken
        if (clone.hasStyle(UserField.Style.FORMULA)) {
            formulaUserFieldTargetSpecificList.add(clone);
            setConsistencyUnknown();
        }
    }

    /**
     * Fügt der zuletzt benutzen Liste ein neues Element ein. Positioniert am
     * übergebenen Index.
     *
     * @param userField
     * @param index
     */
    public void insert(final UserField userField, final int index) {
        UserFieldList ufl = classToUserFieldTargetSpecificListMap.get(userField.getTargetClass());
        if (ufl == null) {
            return;
        }
        ufl.insert(userField, index);
        idToUserFieldMap.put(userField.getID(), userField);
        //Formeln extra merken
        if (userField.hasStyle(UserField.Style.FORMULA)) {
            formulaUserFieldTargetSpecificList.add(userField);
            setConsistencyUnknown();
        }
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
        for (Class<? extends UserFieldTarget> c : getClassToUserFieldKeys()) {
            for (UserField uf : classToUserFieldTargetSpecificListMap.get(c)) {
                if (uf.uses(userField)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * @param numberFormat
     */
    public void removeNumberFormat(final UserFieldNumberFormat numberFormat) {
        for (Class<? extends UserFieldTarget> userFieldTargetClass : getClassToUserFieldKeys()) {
            for (UserField userField : classToUserFieldTargetSpecificListMap.get(userFieldTargetClass)) {
                if (userField.hasNumberFormat(numberFormat)) {
                    userField.removeNumberFormat();
                }
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
        UserFieldList ufl = classToUserFieldTargetSpecificListMap.get(userField.getTargetClass());
        if (ufl == null) {
            return deleted;
        }
        //wenn das userField irgendwo anders noch benutzt wird -> lösche die Referenzen ebenfalls
        //wenn das zu löschende UserField ein UserField ist, das bei einem anderen in der Formel vorkommen kann (Kennzahl, Kennzahlformel, Verteilungsgewicht)
        if (userField.isNumberUserField()) {
            ArrayList<UserField> userFieldsToDelete = new ArrayList<>();
            userFieldsToDelete.add(userField);
            for (int i = 0; i < userFieldsToDelete.size(); i++) {
                UserField uncheckedField = userFieldsToDelete.get(i);
                for (int j = 0; j < formulaUserFieldTargetSpecificList.size(); j++) {
                    UserField formulaUserField = formulaUserFieldTargetSpecificList.get(j);
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
            if (formulaUserFieldTargetSpecificList.removeAll(userFieldsToDelete)) {
                setConsistencyUnknown();
            }

            for (UserField field : userFieldsToDelete) {
                idToUserFieldMap.remove(field.getID());
                ufl = classToUserFieldTargetSpecificListMap.get(field.getTargetClass());
                ufl.remove(field);
            }
            deleted.addAll(userFieldsToDelete);
            //bei allem, was nicht mit Kennzahlen zu tun hat -> einfach löschen
        } else {
            idToUserFieldMap.remove(userField.getID());
            ufl.remove(userField);
            deleted.add(userField);
        }
        return deleted;
    }

    @Override
    public UserFieldDefinitions clone() {
        UserFieldDefinitions def = null;
        try {
            def = (UserFieldDefinitions) super.clone();
        } catch (CloneNotSupportedException e) {
            //this should never happen since we are cloneable
            throw new InternalError(e);
        }
        //Alle Eigenschaften clonen, die ein anderes Object sein müssen, als beim Original
        //die Map mit allen Userfields, die von ihren IDs auf das UserField mappt clonen
        def.idToUserFieldMap = new HashMap<>(idToUserFieldMap);
        //jedes einzelne UserField clonen (erstmal nur in dieser Map)
        for (String key : idToUserFieldMap.keySet()) {
            UserField userFieldClone = def.idToUserFieldMap.get(key).clone();
            def.idToUserFieldMap.put(key, userFieldClone);
        }
        //die Map, die von den UserFieldTargetClasses auf die Liste der dafür defnierten
        //UserFields mappt auch clonen und alle darin enthaltenen UserFields durch die
        //oben erzeugten Clone ersetzen
        def.classToUserFieldTargetSpecificListMap = new HashMap<>(classToUserFieldTargetSpecificListMap);
        for (Class<? extends UserFieldTarget> targetClass : def.classToUserFieldTargetSpecificListMap.keySet()) {
            UserFieldList userFieldList = def.classToUserFieldTargetSpecificListMap.get(targetClass);
            //die Listen in der Map selbst müssen auch geclont werden
            userFieldList = userFieldList.clone();
            //Listen mit ihren clones ersetzen
            def.classToUserFieldTargetSpecificListMap.put(targetClass, userFieldList);
            //in der geclonten Listen die UserFields mit den clones ersetzen
            replaceWithClones(userFieldList, def.idToUserFieldMap);
        }
        //in der Liste mit allen Formel-UserFields auch die Original durch die clone ersetzen
        def.formulaUserFieldTargetSpecificList = new ArrayList<>(formulaUserFieldTargetSpecificList);
        replaceWithClones(def.formulaUserFieldTargetSpecificList, def.idToUserFieldMap);

        //eigenen Calculator für den clone initialisieren
        def.calculator = new Calculator(def);
        //den Analyzer ersetzen
        def.definitionsAnalyzer = new UserFieldDefinitionsAnalyzer(def);
        return def;
    }

    /**
     * Ersetzt die UserFields in der übergebenen Liste durch die aus der
     * übergebenen Map mit derselben ID. Weil UserFieldTargetSpecificList nicht
     * das Interface {@link List} implementiert muss man im Grunde dieselbe
     * Funktion hier 2 mal schreiben. Die UserFieldTargetSpecificList soll aber
     * nicht List implementieren, weil es zu aufwändig wäre, sie für alle darin
     * enthaltenen Funktionen konsitent zu halten
     *
     * @param userFieldList
     * @param idToClonedUserFieldMap
     */
    private static void replaceWithClones(final UserFieldList userFieldList, final Map<String, UserField> idToClonedUserFieldMap) {
        for (int i = 0; i < userFieldList.size(); i++) {
            UserField orgUserField = userFieldList.get(i);
            String userFieldID = orgUserField.getID();
            UserField cloneUserField = idToClonedUserFieldMap.get(userFieldID);
            userFieldList.set(i, cloneUserField);
        }
    }

    /**
     * Ersetzt die UserFields in der übergebenen Liste durch die aus der
     * übergebenen Map mit derselben ID.
     *
     * @param userFieldList
     * @param idToClonedUserFieldMap
     */
    private static void replaceWithClones(final List<UserField> userFieldList, final Map<String, UserField> idToClonedUserFieldMap) {
        for (int i = 0; i < userFieldList.size(); i++) {
            UserField orgUserField = userFieldList.get(i);
            String userFieldID = orgUserField.getID();
            UserField cloneUserField = idToClonedUserFieldMap.get(userFieldID);
            userFieldList.set(i, cloneUserField);
        }
    }

    /**
     * @param otherDef
     */
    public void addAll(final UserFieldDefinitions otherDef) {
        Iterable<UserField> elementClassUserFields = otherDef.getElementClassUserFields();
        Iterable<UserFieldNumberFormat> numberFormats = otherDef.getNumberFormats();
        addAll(numberFormats, elementClassUserFields);
        for (Class<? extends UserFieldTarget> clazz : otherDef.classToUserFieldTargetSpecificListMap.keySet()) {
            for (UserField uf : otherDef.classToUserFieldTargetSpecificListMap.get(clazz)) {
                add(uf);
            }
        }
    }

    /**
     * @param otherDef
     */
    public void addAll(final CopyDependencyResolverResultSimple resolvedCopyDependencies) {
        addAll(resolvedCopyDependencies.userFieldNumberFormats, resolvedCopyDependencies.userFields);
    }

    /**
     * @param numberFormats
     * @param userFields
     */
    public void addAll(final Iterable<UserFieldNumberFormat> numberFormats, final Iterable<UserField> userFields) {
        for (UserFieldNumberFormat numberFormat : numberFormats) {
            add(numberFormat);
        }
        for (UserField userField : userFields) {
            add(userField);
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
        for (Class<?> clazz : classToUserFieldTargetSpecificListMap.keySet()) {
            UserFieldList userFields = classToUserFieldTargetSpecificListMap.get(clazz);
            if (userFields == null) {
                continue;
            }
            for (UserField uf : userFields) {
                if (uf.getTargetClass().equals(userFieldTargetClass) && uf.getName().equals(name)) {
                    return uf;
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
        final UserFieldList fieldList = classToUserFieldTargetSpecificListMap.get(userFieldTargetClass);
        if (fieldList == null) {
            return ImmutableList.of();
        }
        final Iterator<UserField> userFieldsIterator = fieldList.iterator();
        Iterable<UserField> userFieldsIterable = () -> new Iterator<UserField>() {

            @Override
            public boolean hasNext() {
                return userFieldsIterator.hasNext();
            }

            @Override
            public UserField next() {
                return userFieldsIterator.next();
            }

            @Override
            public void remove() {
                userFieldsIterator.remove();
            }
        };
        return userFieldsIterable;
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
    public List<UserField> getUserFields(final Class<? extends UserFieldTarget> userFieldTargetClass, final Set<UserField.Style> styles) {
        if (styles == null || styles.isEmpty()) {
            UserFieldList fieldList = classToUserFieldTargetSpecificListMap.get(userFieldTargetClass);
            return fieldList != null ? fieldList.getData() : new ArrayList<>(0);
        }
        ArrayList<UserField> returnList = new ArrayList<>();
        for (UserField uf : getUserFields(userFieldTargetClass)) {
            if (styles.contains(uf.getStyle())) {
                returnList.add(uf);
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
        Set<Class<? extends UserFieldTarget>> keys = classToUserFieldTargetSpecificListMap.keySet();
        ImmutableList.Builder<Iterable<UserField>> iterables = new ImmutableList.Builder<>();
        for (Class<? extends UserFieldTarget> key : keys) {
            if (key != GLOBAL_USERFIELD_IDENTIFIER_CLASS) {
                iterables.add(classToUserFieldTargetSpecificListMap.get(key));
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
        for (Class<? extends UserFieldTarget> userFieldTargetClass : classToUserFieldTargetSpecificListMap.keySet()) {
            UserFieldList userFields = classToUserFieldTargetSpecificListMap.get(userFieldTargetClass);
            for (UserField userField : userFields) {
                if (userField.getStyle() == UserField.Style.ID) {
                    idUserFields.add(userField);
                }
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
     *         int index) { UserFieldTargetSpecificList ufl =
     *         classToUserFieldTargetSpecificListMap.get(userFieldTargetClass);
     *         if (ufl != null) return ufl.get(index); return null; } /**
     *         Liefert das globale {@link UserField} mit dem entsprechenden
     *         Index (oder <code>null</code> wenn es kein solches gibt.
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
        int index = formulaUserFieldTargetSpecificList.indexOf(formulaUserField);
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
            ArrayList<UserField> inconsistentUserFields = makeFormulaUserFieldTargetSpecificListConsistent();
            if (inconsistentUserFields == null) {
                firstInconsistentUserFieldFormulaIndex = NO_INCONSISTENCE_INDEX_FOUND;
                return false;
            }
            //den Index des ersten nicht konsistenten UserFields in der Formel-UserField-Liste merken
            firstInconsistentUserFieldFormulaIndex = formulaUserFieldTargetSpecificList.size() - inconsistentUserFields.size();
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
        return classToUserFieldTargetSpecificListMap.keySet();
    }

    /////////////////////////////////////////////////////////////////////////////
    // Util-Funktionen zur Sicherung der Konsistenz der UserFields mit Formeln //
    /////////////////////////////////////////////////////////////////////////////

    /**
     * Sortiert die Liste <code>formulaUserFieldTargetSpecificList</code> so,
     * dass sie konsistent ist. Siehe Kommentar zur Variable
     * <code>formulaUserFieldTargetSpecificList</code>.
     *
     * @return Liste von <code>UserField</code>s, die sich im Kreus
     *         referenzieren oder von solchen Elementen abhängig sind bzw.
     *         <code>null</code>, wenn es keine Kreisreferenzen gibt
     */
    private ArrayList<UserField> makeFormulaUserFieldTargetSpecificListConsistent() {
        ArrayList<UserField> calculateableFormulaList = new ArrayList<>(formulaUserFieldTargetSpecificList.size());
        while (true) {
            //Größe der Liste der berechenbaren USerFields merken -> nur wenn sie in jedem
            //Durchlauf wächst, sind die Formeln konsistent
            int lastSortedListSize = calculateableFormulaList.size();
            for (int i = 0; i < formulaUserFieldTargetSpecificList.size(); i++) {
                UserField u = formulaUserFieldTargetSpecificList.get(i);

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
                    formulaUserFieldTargetSpecificList.remove(i--);
                    //prüfe das nächste UserField aus der unsortierten Liste
                    break;
                }

            }
            //wenn alle Formeln in die Liste der berechenbaren einsortiert werden konnten
            if (formulaUserFieldTargetSpecificList.size() == 0) {
                //setzte die glovlae Liste auf die nun sortierte Liste
                formulaUserFieldTargetSpecificList = calculateableFormulaList;
                return null;
            }
            //wenn die ganze Liste durchlaufen wurde, aber keine Kennzahlformel mehr zu den berechenbaren hinzugefügt
            //werden konnte, obwohl noch welche in der unsortierten Liste sind -> die noch in der Liste formulaUserFieldTargetSpecificList
            //enthaltenen UserFields refrenzieren sich an mindesten einer Stelle in ihren Formeln gegenseitig (es reicht
            //schon, dass sich 2 Formeln gegenseitig referenzieren, von denen dann der ganze Rest abhängt)
            if (lastSortedListSize == calculateableFormulaList.size()) {
                ArrayList<UserField> inconsistentUserFields = new ArrayList<>(formulaUserFieldTargetSpecificList);
                //füge zur globalen Liste wieder alle entfernten Elemente hinzu
                calculateableFormulaList.addAll(formulaUserFieldTargetSpecificList);
                formulaUserFieldTargetSpecificList = calculateableFormulaList;
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
    //            classToUserFieldTargetSpecificListMap.get(GLOBAL_USERFIELD_IDENTIFIER_CLASS).remove(userField);
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
        //in der Liste aller Formel-UserFields (formulaUserFieldTargetSpecificList) ein UserField für die
        //betreffende Elementart gefunden wurde. Beim nächsten UserField für diese Elementart
        //braucht man nicht noch einmal alle UserFields zu löschen)
        Set<Class<?>> resetedElementClasses = new HashSet<>(15);

        //Das Hauptdokument der GDCollection holen (UserField-Änderungen gelten immer für alle
        //Elemente, also immer im Hauptdokument arbeiten)
        GraphDocument doc = gdcoll.getMainDoc();

        //Für alle Kennzahlformel-UserFields
        for (UserField userField : formulaUserFieldTargetSpecificList) {
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
    public ArrayList<UserField> getUserFieldsWithNumberFormat(final UserFieldNumberFormat numberFormat) {
        ArrayList<UserField> returnList = new ArrayList<>();
        for (Class<? extends UserFieldTarget> elementClass : getClassToUserFieldKeys()) {
            for (UserField uf : getUserFields(elementClass)) {
                if (uf.hasNumberFormat(numberFormat)) {
                    returnList.add(uf);
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
        return classToUserFieldTargetSpecificListMap.keySet();
    }

    /**
     * @return
     */
    public String getDebugString() {
        StringBuilder sb = new StringBuilder();
        List<Class<? extends UserFieldTarget>> sortedKeys = Alphabetical.getSorted(getClassToUserFieldKeys());
        sb.append("classToUserFieldTargetSpecificListMap");
        sb.append("\n-----------------------\n");
        for (Class<?> keyClass : sortedKeys) {
            sb.append(keyClass.getSimpleName());
            sb.append("\n");
            for (Object o : classToUserFieldTargetSpecificListMap.get(keyClass)) {
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

}
