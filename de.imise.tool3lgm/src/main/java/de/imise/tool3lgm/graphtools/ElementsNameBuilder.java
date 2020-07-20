package de.imise.tool3lgm.graphtools;

import static de.imise.tool3lgm.graphtools.metamodel.elements.Edge.getEndClass;
import static de.imise.tool3lgm.graphtools.metamodel.elements.Edge.getStartClass;
import static de.imise.tool3lgm.graphtools.metamodel.elements.Edge.Direction.BACKWARD;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.MissingResourceException;

import com.google.common.base.Strings;

import de.imise.tool3lgm.MetaModelContext;
import de.imise.tool3lgm.Tool3lgmConstants;
import de.imise.tool3lgm.graphtools.metamodel.MetaModelSpecificAdapter;
import de.imise.tool3lgm.graphtools.metamodel.ModelConstants;
import de.imise.tool3lgm.graphtools.metamodel.elements.DoubleMeaningEdge.ConnectionState;
import de.imise.tool3lgm.graphtools.metamodel.elements.Edge;
import de.imise.tool3lgm.graphtools.metamodel.elements.Edge.Direction;
import de.imise.tool3lgm.graphtools.metamodel.elements.InstanciationEdge;
import de.imise.tool3lgm.graphtools.metamodel.elements.ModelElement;
import de.imise.util.Alphabetical;
import de.imise.util.StringUtils;

/**
 * @author AXS (25.10.2018)
 */
public final class ElementsNameBuilder extends MetaModelSpecificAdapter {

    public static final String STANDARD_DOUBLE_MEANING_EDGE_DELIMITER = " / ";

    /**
     * Mappt von den Knotenklassen auf den zugehörigen Short-Name für die HashString der Elemente. Diese 3-Buchstabigen Klassenkürzel sind nicht
     * zwangsläufig eindeutig und diesen lediglich der besseren Lesbarkeit von Hash-Strings, denen sie immer
     * Vorangestellt werden.
     */
    private static HashMap<String, String> elementClassSimpleNameToHashShortName = null;

    /**
     * @param metaModelContext
     */
    public ElementsNameBuilder(final MetaModelContext metaModelContext) {
        super(metaModelContext);
    }

    // braucht keine eigene equals() und hashCode(), weil es derselbe Builder wird, wenn die selbe MetaModelDefintion-Klasse zurgunde liegt
    //    @Override
    //    public int hashCode() {
    //        return super.hashCode();
    //    }
    //
    //    @Override
    //    public boolean equals(final Object obj) {
    //        return super.equals(obj);
    //    }
    //
    /**
     * Gibt den anzeigbaren Namen der Klassen aus den Resoucen zurück.<br>
     *
     * @param plural wenn true, wird der Pluralname zurück gegeben, sonst der Singular
     * @param elementClass Klasse für die der anzeigbare Name geliefert werden soll
     * @return String aus dem geladenen ResourcenBundle
     */
    public final String getDisplayableName(final boolean plural, final Class<? extends ModelElement> elementClass) {
        return getDisplayableName(elementClass, plural, false);
    }

    /**
     * Gibt den anzeigbaren Namen der Klassen aus den Resoucen zurück.<br>
     * Im Unterschied zu {@link #getDisplayableName(boolean, Class...)} wird hier bei Elementen, die der Master
     * einer {@link InstanciationEdge} sind noch "(Template)" angehängt und bei den Instanzen dieser "(Realisierung)".
     *
     * @param plural wenn true, wird der Pluralname zurück gegeben, sonst der Singular
     * @param elementClass Klasse für die der anzeigbare Name geliefert werden soll
     * @return String aus dem geladenen ResourcenBundle
     */
    public final String getDisplayableFullName(final boolean plural, final Class<? extends ModelElement> elementClass) {
        return getDisplayableName(elementClass, plural, true);
    }

    /**
     * Gibt den anzeigbaren Namen der Klassen aus den Resoucen zurück.<br>
     *
     * @param plural wenn true, wird der Pluralname zurück gegeben, sonst der Singular
     * @param classes Klassen für die der anzeigbare Name geliefert werden soll
     * @return String aus dem geladenen ResourcenBundle
     */
    @SafeVarargs
    public final String getDisplayableName(final boolean plural, final Class<? extends ModelElement>... classes) {
        if (classes == null || classes.length == 0) {
            return "";
        }
        return getDisplayableName(plural, Arrays.asList(classes), true, false);
    }

    /**
     * Gibt den anzeigbaren Namen der Klassen aus den Resoucen zurück.<br>
     * Im Unterschied zu {@link #getDisplayableName(boolean, Class...)} wird hier bei Elementen, die der Master
     * einer {@link InstanciationEdge} sind noch "(Template)" angehängt und bei den Instanzen dieser "(Realisierung)".
     *
     * @param plural wenn true, wird der Pluralname zurück gegeben, sonst der Singular
     * @param classes Klassen für die der anzeigbare Name geliefert werden soll
     * @return String aus dem geladenen ResourcenBundle
     */
    @SafeVarargs
    public final String getDisplayableFullName(final boolean plural, final Class<? extends ModelElement>... classes) {
        if (classes == null || classes.length == 0) {
            return "";
        }
        return getDisplayableName(plural, Arrays.asList(classes), true, true);
    }

    /**
     * @param plural
     * @param classes
     * @param alphabetical
     * @param appendRealizationOrTemplatePostfix If <code>true</code> and the element class has a {@link InstanciationEdge}
     *            then the name gets an appendix. If the element is the instanciation master, then the appendix is "(Template)".
     *            If it is the instanciation slave element (= the instane)then the appendix is "(Realization)".
     * @return
     */
    private final String getDisplayableName(final boolean plural, final Collection<Class<? extends ModelElement>> classes, final boolean alphabetical, final boolean appendRealizationOrTemplatePostfix) {
        if (classes == null) {
            return "";
        }
        List<String> names = new ArrayList<>();
        for (Iterator<Class<? extends ModelElement>> classesIt = classes.iterator(); classesIt.hasNext();) {
            Class<? extends ModelElement> elementClass = classesIt.next();
            String name = getDisplayableName(elementClass, plural, appendRealizationOrTemplatePostfix);
            if (alphabetical) {
                Alphabetical.insert(names, name);
            } else {
                names.add(name);
            }
        }
        return StringUtils.createCollectionString(names);
    }

    /**
     * @param name
     * @return the name with the appendix "(Template)"
     */
    public static final String appendTemplatePostfix(String name) {
        name += " (" + Tool3lgmConstants.getResString("instanciaton_template") + ")";
        return name;
    }

    /**
     * @param elementClass
     * @param plural
     * @param appendTemplatePostfix If <code>true</code> and the element class has a {@link InstanciationEdge}
     *            then the name gets an appendix. If the element is the instanciation master, then the appendix is "(Template)".
     * @return
     */
    private String getDisplayableName(Class<? extends ModelElement> elementClass, final boolean plural, final boolean appendTemplatePostfix) {
        MetaModelContext metaModelContext = getMetaModelContext();
        while (ModelElement.class.isAssignableFrom(elementClass)) {
            try {
                String resKey = elementClass.getSimpleName();
                if (plural) {
                    resKey += ModelConstants.PLURAL_NAME_RES_KEY_SUFFIX;
                }
                String name = metaModelContext.getResString(resKey);
                if (appendTemplatePostfix) {
                    //If the elementClass is a master type of an InstanciationEdge (start class of the edge type) then
                    //check the dispalyable name of the instance type (end class of the edge type). If they have the
                    //same displayable name, so append " (Template)" to the name of the master. This ensures that they
                    //can be distinguished.
                    List<Class<InstanciationEdge>> instanciationEdgeTypesAsMaster = metaModel.getInstanciationEdgeTypesAsMaster(elementClass);
                    for (Class<InstanciationEdge> instanciationEdgeType : instanciationEdgeTypesAsMaster) {
                        Class<? extends ModelElement> instanciationInstanceType = InstanciationEdge.getInstanciationInstance(instanciationEdgeType);
                        String instanceTypeDisplayableName = getDisplayableName(instanciationInstanceType, plural, false);
                        if (instanceTypeDisplayableName.equals(name)) {
                            name = appendTemplatePostfix(name);
                            break;
                        }
                    }
                }
                return name;
            } catch (MissingResourceException mre) {
                elementClass = elementClass.getSuperclass().asSubclass(ModelElement.class);
            } catch (NullPointerException npe) {
                break;
            }
        }
        return metaModelContext.getResString(ModelElement.class.getSimpleName());
    }

    /**
     * Gibt den anzeigbaren Namen einer Klasse in der Mehrzahl (Plural) aus den Resoucen zurück.<br>
     * Wird <code>null</code> übergeben, wird der Name für ein Modell (im Deutschen also "Modell" zurück gegeben.)
     *
     * @param classes Klassen für die der anzeigbare Name geliefert werden soll
     * @return String aus dem geladenen ResourcenBundle
     */
    @SafeVarargs
    public final String getDisplayablePluralName(final Class<? extends ModelElement>... classes) {
        return getDisplayableName(true, classes);
    }

    /**
     * Gibt den anzeigbaren Namen einer Klasse in der Mehrzahl (Plural) aus den Resoucen zurück.<br>
     * Wird <code>null</code> übergeben, wird der Name für ein Modell (im Deutschen also "Modell" zurück gegeben.)
     * Im Unterschied zu {@link #getDisplayablePluralName(Class...)} wird hier bei Elementen, die der Master
     * einer {@link InstanciationEdge} sind noch "(Template)" angehängt und bei den Instanzen dieser "(Realisierung)".
     *
     * @param classes Klassen für die der anzeigbare Name geliefert werden soll
     * @return String aus dem geladenen ResourcenBundle
     */
    @SafeVarargs
    public final String getDisplayablePluralFullName(final Class<? extends ModelElement>... classes) {
        return getDisplayableFullName(true, classes);
    }

    /**
     * Gibt den anzeigbaren Namen einer Klasse in der Mehrzahl (Plural) aus den Resoucen zurück.<br>
     * Wird <code>null</code> übergeben, wird der Name für ein Modell (im Deutschen also "Modell" zurück gegeben.)
     *
     * @param classes Klassen für die der anzeigbare Name geliefert werden soll
     * @return String aus dem geladenen ResourcenBundle
     */
    public final String getDisplayablePluralName(final Collection<Class<? extends ModelElement>> classes) {
        return getDisplayableName(true, classes, false, false);
    }

    /**
     * Gibt den anzeigbaren Namen einer Klasse in der Mehrzahl (Plural) aus den Resoucen zurück.<br>
     * Wird <code>null</code> übergeben, wird der Name für ein Modell (im Deutschen also "Modell" zurück gegeben.)
     * Im Unterschied zu {@link #getDisplayableName(Collection)} wird hier bei Elementen, die der Master
     * einer {@link InstanciationEdge} sind noch "(Template)" angehängt und bei den Instanzen dieser "(Realisierung)".
     *
     * @param classes Klassen für die der anzeigbare Name geliefert werden soll
     * @return String aus dem geladenen ResourcenBundle
     */
    public final String getDisplayablePluralFullName(final Collection<Class<? extends ModelElement>> classes) {
        return getDisplayableName(true, classes, false, true);
    }

    /**
     * Gibt den anzeigbaren Namen einer Klasse aus den Resoucen zurück.<br>
     * Wird <code>null</code> übergeben, wird der Name für ein Modell (im Deutschen also "Modell" zurück gegeben.)
     *
     * @param elementClass Klasse für die der anzeigbare Name geliefert werden soll
     * @return String aus dem geladenen ResourcenBundle
     */
    public final String getDisplayableName(final Class<? extends ModelElement> elementClass) {
        return getDisplayableName(false, elementClass);
    }

    /**
     * Gibt den anzeigbaren Namen einer Klasse aus den Resoucen zurück.<br>
     * Wird <code>null</code> übergeben, wird der Name für ein Modell (im Deutschen also "Modell" zurück gegeben.)
     * Im Unterschied zu {@link #getDisplayableName(Class)} wird hier bei Elementen, die der Master
     * einer {@link InstanciationEdge} sind noch "(Template)" angehängt und bei den Instanzen dieser "(Realisierung)".
     *
     * @param elementClass Klasse für die der anzeigbare Name geliefert werden soll
     * @return String aus dem geladenen ResourcenBundle
     */
    public final String getDisplayableFullName(final Class<? extends ModelElement> elementClass) {
        return getDisplayableFullName(false, elementClass);
    }

    /**
     * Gibt den anzeigbaren Namen einer Klasse aus den Resoucen zurück.<br>
     * Wird <code>null</code> übergeben, wird der Name für ein Modell (im Deutschen also "Modell" zurück gegeben.)
     *
     * @param classes Klasse für die der anzeigbare Name geliefert werden soll
     * @return String aus dem geladenen ResourcenBundle
     */
    @SafeVarargs
    public final String getDisplayableName(final Class<? extends ModelElement>... classes) {
        return getDisplayableName(false, classes);
    }

    /**
     * Gibt den anzeigbaren Namen einer Klasse aus den Resoucen zurück.<br>
     * Wird <code>null</code> übergeben, wird der Name für ein Modell (im Deutschen also "Modell" zurück gegeben.)
     * Im Unterschied zu {@link #getDisplayableName(Class...)} wird hier bei Elementen, die der Master
     * einer {@link InstanciationEdge} sind noch "(Template)" angehängt und bei den Instanzen dieser "(Realisierung)".
     *
     * @param classes Klasse für die der anzeigbare Name geliefert werden soll
     * @return String aus dem geladenen ResourcenBundle
     */
    @SafeVarargs
    public final String getDisplayableFullName(final Class<? extends ModelElement>... classes) {
        return getDisplayableFullName(false, classes);
    }

    /**
     * Gibt den anzeigbaren Namen einer Klasse aus den Resoucen zurück.<br>
     * Wird <code>null</code> übergeben, wird der Name für ein Modell (im Deutschen also "Modell" zurück gegeben.)
     *
     * @param classes Klassen für die der anzeigbare Name geliefert werden soll
     * @return String aus dem geladenen ResourcenBundle
     */
    public final String getDisplayableName(final Collection<Class<? extends ModelElement>> classes) {
        return getDisplayableName(false, classes, false, false);
    }

    /**
     * Gibt den anzeigbaren Namen einer Klasse aus den Resoucen zurück.<br>
     * Wird <code>null</code> übergeben, wird der Name für ein Modell (im Deutschen also "Modell" zurück gegeben.)
     * Im Unterschied zu {@link #getDisplayableName(Collection)} wird hier bei Elementen, die der Master
     * einer {@link InstanciationEdge} sind noch "(Template)" angehängt und bei den Instanzen dieser "(Realisierung)".
     *
     * @param classes Klassen für die der anzeigbare Name geliefert werden soll
     * @return String aus dem geladenen ResourcenBundle
     */
    public final String getDisplayableFullName(final Collection<Class<? extends ModelElement>> classes) {
        return getDisplayableName(false, classes, false, true);
    }

    /**
     * Liefert den Standardanzeigenamen für ein Modelelement der übergebenen Art
     *
     * @param me
     * @return
     * @see getDisplayableName
     */
    public final String getDisplayableName(final ModelElement me) {
        return getDisplayableName(me.getClass());
    }

    /**
     * @param edgeClass
     * @return
     * @see ElementsNameBuilder#getMetaAssociationName(Class, boolean, ModelConstants.ConnectionState, boolean, boolean, String)
     */
    public String getForwardMetaAssociationName(final Class<? extends Edge> edgeClass) {
        return getForwardMetaAssociationName(edgeClass, false, false);
    }

    /**
     * Liefert den Meta-Namen der Kanteklasse für die Vorwärtsrichtung ohne die Elementartnamen, die die Edge verbindet.
     *
     * @param edgeClass
     * @return
     * @see ElementsNameBuilder#getMetaAssociationName(Class, boolean, ModelConstants.ConnectionState, boolean, boolean, String)
     */
    public String getFullForwardMetaAssociationName(final Class<? extends Edge> edgeClass) {
        return getForwardMetaAssociationName(edgeClass, true, true);
    }

    /**
     * @param edgeClass
     * @param appendPrefixClass
     * @param appendPostfixClass
     * @return
     * @see ElementsNameBuilder#getMetaAssociationName(Class, boolean, ModelConstants.ConnectionState, boolean, boolean)
     */
    public String getForwardMetaAssociationName(final Class<? extends Edge> edgeClass, final boolean appendPrefixClass, final boolean appendPostfixClass) {
        return getForwardMetaAssociationName(edgeClass, ConnectionState.DOUBLE, appendPrefixClass, appendPostfixClass);
    }

    /**
     * @param edgeClass
     * @param connectionState
     * @param appendPrefixClass
     * @param appendPostfixClass
     * @return
     * @see ElementsNameBuilder#getMetaAssociationName(Class, boolean, ModelConstants.ConnectionState, boolean, boolean)
     */
    public String getForwardMetaAssociationName(final Class<? extends Edge> edgeClass, final ConnectionState connectionState, final boolean appendPrefixClass, final boolean appendPostfixClass) {
        return getMetaAssociationName(edgeClass, Direction.FORWARD, connectionState, appendPrefixClass, appendPostfixClass);
    }

    /**
     * @param edgeClass
     * @return
     * @see ElementsNameBuilder#getMetaAssociationName(Class, boolean, ModelConstants.ConnectionState, boolean, boolean, String)
     */
    public String getBackwardMetaAssociationName(final Class<? extends Edge> edgeClass) {
        return getBackwardMetaAssociationName(edgeClass, false, false);
    }

    /**
     * Liefert den Meta-Namen der Kanteklasse für die Rückwärtsrichtung mit den Elementartnamen, die die Edge verbindet.
     *
     * @param edgeClass
     * @return
     * @see ElementsNameBuilder#getMetaAssociationName(Class, boolean, ModelConstants.ConnectionState, boolean, boolean)
     */
    public String getFullBackwardMetaAssociationName(final Class<? extends Edge> edgeClass) {
        return getBackwardMetaAssociationName(edgeClass, true, true);
    }

    /**
     * @param edgeClass
     * @param appendPrefixClass
     * @param appendPostfixClass
     * @return
     * @see ElementsNameBuilder#getMetaAssociationName(Class, boolean, ModelConstants.ConnectionState, boolean, boolean)
     */
    public String getBackwardMetaAssociationName(final Class<? extends Edge> edgeClass, final boolean appendPrefixClass, final boolean appendPostfixClass) {
        return getBackwardMetaAssociationName(edgeClass, ConnectionState.DOUBLE, appendPrefixClass, appendPostfixClass);
    }

    /**
     * @param edgeClass
     * @param connectionState
     * @param appendPrefixClass
     * @param appendPostfixClass
     * @return
     * @see ElementsNameBuilder#getMetaAssociationName(Class, boolean, ModelConstants.ConnectionState, boolean, boolean)
     */
    public String getBackwardMetaAssociationName(final Class<? extends Edge> edgeClass, final ConnectionState connectionState, final boolean appendPrefixClass, final boolean appendPostfixClass) {
        return getMetaAssociationName(edgeClass, Direction.BACKWARD, connectionState, appendPrefixClass, appendPostfixClass);
    }

    /**
     * @param edgeClass
     * @param direction
     * @param connectionState
     * @return
     * @see ElementsNameBuilder#getMetaAssociationName(Class, boolean, ModelConstants.ConnectionState, boolean, boolean)
     */
    public String getFullMetaAssociationName(final Class<? extends Edge> edgeClass, final Direction direction, final ConnectionState connectionState) {
        return getMetaAssociationName(edgeClass, direction, connectionState, true, true);
    }

    /**
     * Bedeutungen zurück.
     * Liefert in Abhängigkeit von der Richtung den Meta-Namen der Kanteklasse. Bei Kanten mit doppelter Bedeutung kommt hier der Name mit beiden
     *
     * @param edgeClass
     * @param direction gibt an, ob die Bedeutung der Edge von der Startklasse zur Endklasse (Direction.FORWARD) oder von der Endklasse
     *            zur Startklasse (Direction.BACKWARD) zurück gegeben werden soll. Mit Start- und Endklasse sind hier die
     *            beiden Elementklasse gemeint, die in der Kantenklasse in dieser Reihenfolge definiert sind
     * @return
     */
    public String getMetaAssociationName(final Class<? extends Edge> edgeClass, final Direction direction) {
        return getMetaAssociationName(edgeClass, direction, ConnectionState.DOUBLE, false, false);
    }

    /**
     * Liefert in Abhängigkeit von der Richtung den Meta-Namen der Kanteklasse
     *
     * @param edgeClass
     * @param direction gibt an, ob die Bedeutung der Edge von der Startklasse zur Endklasse (Direction.FORWARD) oder von der Endklasse
     *            zur Startklasse (Direction.BACKWARD) zurück gegeben werden soll. Mit Start- und Endklasse sind hier die
     *            beiden Elementklasse gemeint, die in der Kantenklasse in dieser Reihenfolge definiert sind
     * @param connectionState MeaningDirection.FORWARD, MeaningDirection.BACKWARD oder MeaningDirection.DOUBLE - Bei allen
     *            Assoziationen, die in jede Richtung nur eine Bedeutung haben, ist dieser Parameter egal. Bei Assoziationen, die mehr als eine
     *            Bedeutung haben, kann hier die Richtung angegeben werden für die die bedeutung zurück gegeben werden soll.<br>
     *            Alle Assoziationen im aktuellen Metamodell haben maximal 2 Bedeutungen. Das ist bei allen Assoziationen der Fall, die eigentlich 2
     *            Assoziationen sind, aber aus allerlei Gründen in eine gepackt wurden.<br>
     *            Beispiel 1: AufObjVerbindung = Assoziation zw. Startklasse Aufgabe und Endklasse Objekttyp.<br>
     *            <ul>
     *            <li>
     *            <code>switchDefinedDirection == false</code>, <code>direction == {@link Doppelkante}.FORWARD</code> heißt
     *            "Aufgabe bearbeitet Objekttyp"</li>
     *            <li>
     *            <code>switchDefinedDirection == false</code>, <code>direction == {@link Doppelkante}.BACKWARD</code> heißt
     *            "Aufgabe interpretiert Objekttyp"</li>
     *            <li>
     *            <code>switchDefinedDirection == true</code>, <code>direction == {@link Doppelkante}.FORWARD</code> heißt
     *            "Objekttyp wird interpretiert von Aufgabe "</li>
     *            <li>
     *            <code>switchDefinedDirection == true</code>, <code>direction == {@link Doppelkante}.BACKWARD</code> heißt
     *            "Objekttyp wird bearbeitet von Aufgabe"</li>
     *            </ul>
     * @param doubleMeaningEdgeDelimiter String der bei Kanten mit doppelter Bedeutung, bei denen beide Bedeutungen gleichzeitig ausgegeben werden
     *            sollen zwischen die beiden Bedeutungen geschrieben wird.
     * @return
     */
    public String getMetaAssociationName(final Class<? extends Edge> edgeClass, final Direction direction, final ConnectionState connectionState) {
        //alle Kantenamen müssen mit SimplerKantenklassenName_f oder SimplerKantenklassenName_b angegeben sein oder bei Kanten mit doppelter Bedeutung SimplerKantenklassenName_f_f,
        //SimplerKantenklassenName_f_b, SimplerKantenklassenName_b_f und SimplerKantenklassenName_b_b
        String edgeClassName = edgeClass.getSimpleName();
        String metaAssociationName = getDirectedName(edgeClassName, direction, connectionState);
        if (metaAssociationName != null) {
            return metaAssociationName;
        }
        //wenn für den aktuellen Klassennamen kein Name gefunden wurde -> nimm die Oberklasse -> irgendwann kommt man bei Edge.class an, für die auf jeden Fall ein Namen ex.
        return getMetaAssociationName(edgeClass.getSuperclass().asSubclass(Edge.class), direction, connectionState);
    }

    /**
     * @param edgeClass
     * @param direction
     * @param connectionState
     * @param appendPrefixClass
     * @param appendPostfixClass
     * @return
     * @see getMetaAssociationName
     */
    public String getMetaAssociationName(final Class<? extends Edge> edgeClass, final Direction direction, final ConnectionState connectionState, final boolean appendPrefixClass, final boolean appendPostfixClass) {
        boolean forward = direction != Direction.BACKWARD;
        Class<? extends ModelElement> prefixClass = !appendPrefixClass ? null : forward ? getStartClass(edgeClass) : getEndClass(edgeClass);
        Class<? extends ModelElement> postfixClass = !appendPostfixClass ? null : forward ? getEndClass(edgeClass) : getStartClass(edgeClass);
        return getMetaAssociationName(edgeClass, direction, connectionState, prefixClass, postfixClass);
    }

    /**
     * @param edgeClass
     * @param switchDefinedDirection
     * @param connectionState
     * @param prefixClass
     * @param postfixClass
     * @return
     * @see getMetaAssociationName
     */
    public String getMetaAssociationName(final Class<? extends Edge> edgeClass, final Direction direction, final ConnectionState connectionState, final Class<? extends ModelElement> prefixClass, final Class<? extends ModelElement> postfixClass) {
        return getMetaAssociationName(edgeClass, direction, connectionState, prefixClass == null ? null : getDisplayableFullName(prefixClass), postfixClass == null ? null : getDisplayableFullName(postfixClass));
    }

    /**
     * @param edgeClass
     * @param direction
     * @param doubleMeaningEdgeMeaningDirection
     * @param prefix
     * @param postfix
     * @return
     */
    public String getMetaAssociationName(final Class<? extends Edge> edgeClass, final Direction direction, final ConnectionState connectionState, final String prefix, final String postfix) {
        boolean emptyPrefix = Strings.isNullOrEmpty(prefix);
        boolean emptyPostfix = Strings.isNullOrEmpty(postfix);
        if (emptyPrefix && emptyPostfix) {
            return getMetaAssociationName(edgeClass, direction, connectionState);
        }
        StringBuilder sb = new StringBuilder();
        if (!emptyPrefix) {
            sb.append(prefix);
            sb.append(" ");
        }
        sb.append(getMetaAssociationName(edgeClass, direction, connectionState));
        if (!emptyPostfix) {
            sb.append(" ");
            sb.append(postfix);
        }
        return sb.toString();
    }

    public final String getDirectedName(final String baseResKey, final Direction direction) {
        return getDirectedName(baseResKey, direction, null);
    }

    /**
     * Setzt aus dem übergebenen String und den beiden anderen Parametern einen Gesamt-ResourceKey zusammen. Ist der connectionState null oder leer,
     * wird er nicht mit angehängt.
     *
     * @param baseResKey
     * @param direction
     * @param connectionState
     * @return
     */
    public final String getDirectedName(final String baseResKey, final Direction direction, final ConnectionState connectionState) {
        String directionPostfix = direction == BACKWARD ? "_b" : "_f"; //direction == null oder FORWARD werden als forward interpretiert
        String directedName = getDirectedName(baseResKey, directionPostfix);//bei einfachen Kanten kommt hier nicht null zurück
        if (directedName != null) {
            return directedName;
        }
        //bei einem ResourceKey mit doppelter Bedeutung suchen
        if (connectionState != null) {
            String connectionStatePostfix = connectionState == ConnectionState.FORWARD ? "_f" : connectionState == ConnectionState.BACKWARD ? "_b" : "_d";
            directedName = getDirectedName(baseResKey, directionPostfix, connectionStatePostfix); //falls in den Resourcen ein Name für den ConnectionState.DOUBLE ("_d") angegeben sein sollte, kommt der hier zurück! Falls nicht, wird unten einer zusammengebaut
            if (directedName != null) {
                return directedName;
            }

        }
        if (connectionState == ConnectionState.DOUBLE || connectionState == null) {
            directedName = getDirectedName(baseResKey, directionPostfix, "_f"); //hier braucht man immer beide Namen -> hole den Vorwärtsnamen
            if (directedName != null) {
                MetaModelContext metaModelContext = getMetaModelContext();
                String doubleMeaningEdgeDelimiter = connectionState == null ? metaModelContext.getResString("oder") : metaModelContext.getResString("und");
                //wenn es einen Vorwärtsnamen gibt, dann muss auch ein Rückwärtsname angegeben sein!
                return directedName + " " + doubleMeaningEdgeDelimiter + " " + getDirectedName(baseResKey, directionPostfix, "_b");
            }
        }
        return null;
    }

    /**
     * Erzeugt aus dem übergbenen <code>baseResKey</code> und dem 2. String einen Gesamt-ResourceKey, dessen String geladen wird.
     *
     * @param baseResKey
     * @param direction
     * @return
     */
    private final String getDirectedName(final String baseResKey, final String direction) {
        return getDirectedName(baseResKey, direction, null);
    }

    /**
     * Hängt die übergebenen Strings hintereinander und versucht den daraus enstandenen ResourceKey zu laden. Ist der connectionState null oder leer,
     * wird er nicht mit angehängt.
     *
     * @param baseResKey
     * @param direction
     * @param connectionState
     * @return
     */
    private final String getDirectedName(final String baseResKey, final String direction, final String connectionState) {
        try {
            MetaModelContext metaModelContext = getMetaModelContext();
            if (Strings.isNullOrEmpty(connectionState)) {
                return metaModelContext.getResString(baseResKey + direction);
            }
            return metaModelContext.getResString(baseResKey + direction + connectionState);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Erzeugt einen String, in dem die Namen der Elementklassen kommasepariert in aplhabetischer Reihenfolge zurück kommen.
     *
     * @param classes
     * @return
     */
    public String getDisplayableClassesNames(final Collection<Class<? extends ModelElement>> classes) {
        List<String> names = new ArrayList<>();
        for (Class<? extends ModelElement> c : classes) {
            String name = getDisplayableName(c);
            Alphabetical.insert(names, name);
        }
        String s = names.toString(); //ArrayList erzeugt einen String mit eckigen Klammern wie [name1, name2]
        s = s.substring(1, s.length() - 1);//eckige Klammern weglassen
        return s;
    }

    //################################################################################################################################################

    //    /**
    //     * Gibt Namenskuerzel einer Elementklasse zurueck. Diese Namenskürzel garantieren nicht, dass man von ihnen auf die Klasse zurückschließen kann.
    //     * Sie dienen lediglich dazu, die Hash-Strings der Modellelemente im Baukasten und der XML-Datei etwas
    //     * lesbarer zu gestalten.
    //     *
    //     * @param elementClass Elementklasse für die das Kürzel zurück gegeben werden soll.
    //     * @return String mit Namenskuerzel
    //     */
    //    public static final String getShortName(final Class<? extends ModelElement> elementClass) {
    //
    //        //HashMap mit den ShortNames der Klassen initialisieren (einmal statisch)
    //        if (elementClassToHashShortName == null) {
    //            elementClassToHashShortName = new HashMap<>();
    //            //Set in das alle bisher gefundenen ShortNames eingetragen werden, um zu prüfen, ob ein shortName bereits existiert
    //            Set<String> allShortNames = new HashSet<>();
    //            loop1: for (Class<? extends ModelElement> nodeClass : ModelConstants.ALL_NODES_SET) {
    //                String s = nodeClass.getSimpleName();
    //                //wenn der Klassenname aus weniger als 4 Zeichen besteht
    //                if (s.length() <= 3) {
    //                    elementClassToHashShortName.put(nodeClass, s.toUpperCase());
    //                    continue;
    //                }
    //                //mehr als 3 Zeichen
    //                StringBuilder shortName = new StringBuilder(3);
    //                for (int j = 0; j < s.length(); j++) {
    //                    //suche Großbuchstaben -> sie werden bevorzugt in den Shortname aufgenommen
    //                    String character = s.substring(j, j + 1);
    //                    if (character.toUpperCase().equals(character)) {
    //                        shortName.append(character);
    //                        //wenn 3 Großbuchstaben gefunden wurden
    //                        if (shortName.length() == 3) {
    //                            String sn = shortName.toString();
    //                            //wenn es den ShortName noch nicht gibt
    //                            if (!allShortNames.contains(sn)) {
    //                                allShortNames.add(sn);
    //                                elementClassToHashShortName.put(nodeClass, sn);
    //                                continue loop1;
    //                            }
    //                            //es gibt den ShortName bereits -> letztes Zeichen löschen und weiter nach Großbuchstanben suchen
    //                            shortName.deleteCharAt(2);
    //                        }
    //                    }
    //                }
    //                //hier kommt er nur hin, wenn keine 3 Großbuchstaben gefunden wurden
    //                //short name hat 0 bis 2 Zeichen
    //
    //                //wenn genau 2 Großbuchstaben gefunden wurden
    //                if (shortName.length() == 2) {
    //                    int lastUpperCharInClassName = 0;
    //                    for (int j = 0; j < shortName.length(); j++) {
    //                        char shortNameChar = shortName.charAt(j);
    //                        for (; lastUpperCharInClassName < s.length(); lastUpperCharInClassName++) {
    //                            if (s.charAt(lastUpperCharInClassName) == shortNameChar) {
    //                                break;
    //                            }
    //                        }
    //                    }
    //                    //lastUpperCharInClassName hat jetzt den Index des letzten Großbuchstaben in shortName
    //
    //                    //solange hinter dem letzten Großbuchstaben noch Zeichen kommen, einfach solange diese Zeichen anhängen,
    //                    //bis ein eindeutiger 3-Zeichen-shortName gefunden wurde
    //                    while (++lastUpperCharInClassName < s.length()) {
    //                        shortName.append(s.charAt(lastUpperCharInClassName));
    //                        String sn = shortName.toString().toUpperCase();
    //                        //wenn es den ShortName noch nicht gibt
    //                        if (!allShortNames.contains(sn)) {
    //                            allShortNames.add(sn);
    //                            elementClassToHashShortName.put(nodeClass, sn);
    //                            continue loop1;
    //                        }
    //                        //es gibt den ShortName bereits -> letztes Zeichen löschen und weiter suchen
    //                        shortName.deleteCharAt(2);
    //                    }
    //                }
    //
    //                //es wurden keine 3 eindutigen Buchstaben nach Großbuchstaben gefunden -> Nimm einfach die ersten beiden
    //                //Buchstaben und suche einen Folgebuchstaben bis 3 eindeutige Zeichen gefunden werden (das geht immer gut,
    //                //wenn die Klassennamen eindeutig sind (was immer der Fall ist, wenn sie im selben package liegen) und hier
    //                ///unten fest steht, dass der Name mind. 4 Zeichen lang ist)
    //                shortName.setLength(0);
    //                shortName.append(s.charAt(0));
    //                shortName.append(s.charAt(1));
    //                for (int j = 2; j < s.length(); j++) {
    //                    shortName.append(s.charAt(j));
    //                    String sn = shortName.toString().toUpperCase();
    //                    // wenn es den ShortName noch nicht gibt
    //                    if (!allShortNames.contains(sn)) {
    //                        allShortNames.add(sn);
    //                        elementClassToHashShortName.put(nodeClass, sn);
    //                        continue loop1;
    //                    }
    //                    //es gibt den ShortName bereits -> letztes Zeichen löschen und weiter suchen
    //                    shortName.deleteCharAt(2);
    //                }
    //
    //                //wenn auch das nicht gekalppt hat (der Fall dürfte nicht eintreten, wenn die Klassen alle im gleichen Package liegen,
    //                //da sie dann alle etwas eindeutiges bei s = class.getShortName() geliefert haben)
    //                //-> nimm einfach die ersten 3 Zeichen ohne noch einmal irgendwelche Eindeutigkeit zu prüfen;
    //                String sn = s.substring(0, 3).toUpperCase();
    //                allShortNames.add(sn); //kann man sich wahrscheinlich sparen, weil auch diese Kombination schon oben durchprobiert wurde, aber sicher ist sicher
    //                elementClassToHashShortName.put(nodeClass, sn);
    //            }
    //        }
    //
    //        //Node
    //        if (Node.class.isAssignableFrom(elementClass)) {
    //            Object o = elementClassToHashShortName.get(elementClass);
    //            //ist null bei Layerknoten. Die brauchen aber auch keinen lesbaren Hash
    //            if (o == null) {
    //                return ModelConstants.NO_MODEL_ELEMENT_SHORT_NAME;
    //            }
    //            return elementClassToHashShortName.get(elementClass).toString();
    //            //Kanten
    //        } else if (Edge.class.isAssignableFrom(elementClass)) {
    //            return ModelConstants.EDGE_SHORT_NAME;
    //        }
    //        return ModelConstants.NO_MODEL_ELEMENT_SHORT_NAME;
    //    }

    /**
     * Gibt Namenskuerzel einer Elementklasse zurueck. Diese Namenskürzel garantieren nicht, dass man von ihnen auf die Klasse zurückschließen kann.
     * Sie dienen lediglich dazu, die Hash-Strings der Modellelemente im Baukasten und der XML-Datei etwas lesbarer zu gestalten. 2 verschiedene
     * Klassen können den selben Shortname haben. So wird in beiden Fällen aus AufObjVerbindung und AufOrgVerbindung der Short-Name AOV.
     *
     * @param elementClass Elementklasse für die das Kürzel zurück gegeben werden soll.
     * @return String mit Namenskuerzel
     */
    public final String getShortName(final Class<? extends ModelElement> elementClass) {
        if (Edge.class.isAssignableFrom(elementClass)) {
            return ModelConstants.EDGE_SHORT_NAME;
        }
        //HashMap mit den ShortNames der Klassen initialisieren (einmal statisch)
        if (elementClassSimpleNameToHashShortName == null) {
            elementClassSimpleNameToHashShortName = new HashMap<>();
        }
        String simpleClassName = elementClass.getSimpleName();
        String shortName = elementClassSimpleNameToHashShortName.get(simpleClassName);
        if (shortName == null) {
            if (simpleClassName.length() <= 3) {
                shortName = simpleClassName;
            } else {
                StringBuilder shortNameBuilder = new StringBuilder(simpleClassName.charAt(0));
                for (int j = 1; j < simpleClassName.length(); j++) {
                    //suche Großbuchstaben -> sie werden bevorzugt in den Shortname aufgenommen
                    String character = simpleClassName.substring(j, j + 1);
                    if (character.toUpperCase().equals(character)) {
                        shortNameBuilder.append(character);
                        //wenn 3 Großbuchstaben gefunden wurden
                        if (shortNameBuilder.length() == 3) {
                            break;
                        }
                    }
                }
                //kein oder nur ein einziger Großbuchstabe am Anfang
                if (shortNameBuilder.length() == 1) {
                    shortName = simpleClassName.substring(0, 3);
                } else if (shortNameBuilder.length() == 2) { // 2 Großbuchstaben gefunden
                    int lastUpperChar = simpleClassName.lastIndexOf(shortNameBuilder.charAt(1));
                    if (lastUpperChar == simpleClassName.length() - 1) { // der 2. Großbuchstabe steht ganz am Ende?
                        char secondCharInName = simpleClassName.charAt(1);
                        shortNameBuilder.insert(1, secondCharInName); //füge den 2. Buchstaben des Gesamtwortes noch in der Mitte ein
                    } else {
                        char charAfterlastUpperChar = simpleClassName.charAt(lastUpperChar + 1);
                        shortNameBuilder.append(charAfterlastUpperChar); //hänge den Buchstaben direkt nach dem 2. Großbuchstaben ans Ende an
                    }
                }
                shortName = shortNameBuilder.toString();
            }
            elementClassSimpleNameToHashShortName.put(simpleClassName, shortName.toUpperCase());
        }
        return shortName;
    }

}
