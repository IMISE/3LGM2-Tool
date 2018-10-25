package de.imise.tool3lgm.graphtools;

import static de.imise.tool3lgm.Tool3lgmConstants.getResString;
import static de.imise.tool3lgm.graphtools.metamodel.elements.Edge.getEndClass;
import static de.imise.tool3lgm.graphtools.metamodel.elements.Edge.getStartClass;

import java.util.HashMap;
import java.util.HashSet;
import java.util.MissingResourceException;

import de.imise.tool3lgm.graphtools.metamodel.ModelConstants;
import de.imise.tool3lgm.graphtools.metamodel.elements.DoubleMeaningEdge;
import de.imise.tool3lgm.graphtools.metamodel.elements.Edge;
import de.imise.tool3lgm.graphtools.metamodel.elements.ModelElement;
import de.imise.tool3lgm.graphtools.metamodel.elements.Node;

/**
 * @author AXS (25.10.2018)
 */
public class ElementsNameBuilder {

    /**
     * Mappt von den Knotenklassen auf den zugehörigen Short-Name für die HashString der Elemente. Diese 3-Buchstabigen Klassenkürzel sind nicht
     * zwangsläufig eindeutig und diesen lediglich der besseren Lesbarkeit von Hash-Strings, denen sie immer
     * Vorangestellt werden.
     */
    private static HashMap<Class<? extends ModelElement>, String> elementClassToHashShortName = null;

    /**
     * Gibt den anzeigbaren Namen einer Klasse aus den Resoucen zurück.<br>
     * Wird <code>null</code> übergeben, wird der Name für ein Modell (im Deutschen also "Modell" zurück gegeben.)
     *
     * @param clazz Klasse für die der anzeigbare Name geliefert werden soll
     * @param plural wenn true, wird der Pluralname zurück gegeben, sonst der Singular
     * @return String aus dem geladenen ResourcenBundle
     */
    private static final String getDisplayableName(Class<? extends ModelElement> clazz, final boolean plural) {
        if (clazz == null) {
            return null;
        }
        while (ModelElement.class.isAssignableFrom(clazz)) {
            try {
                String resKey = clazz.getSimpleName();
                if (plural) {
                    resKey += ModelConstants.PLURAL_NAME_RES_KEY_SUFFIX;
                }
                return getResString(resKey);
            } catch (MissingResourceException mre) {
                clazz = clazz.getSuperclass().asSubclass(ModelElement.class);
            }
        }
        return null;
    }

    /**
     * Gibt den anzeigbaren Namen einer Klasse in der Mehrzahl (Plural) aus den Resoucen zurück.<br>
     * Wird <code>null</code> übergeben, wird der Name für ein Modell (im Deutschen also "Modell" zurück gegeben.)
     *
     * @param clazz Klasse für die der anzeigbare Name geliefert werden soll
     * @return String aus dem geladenen ResourcenBundle
     */
    public static final String getDisplayablePluralName(final Class<? extends ModelElement> clazz) {
        return getDisplayableName(clazz, true);
    }

    /**
     * Gibt den anzeigbaren Namen einer Klasse aus den Resoucen zurück.<br>
     * Wird <code>null</code> übergeben, wird der Name für ein Modell (im Deutschen also "Modell" zurück gegeben.)
     *
     * @param clazz Klasse für die der anzeigbare Name geliefert werden soll
     * @return String aus dem geladenen ResourcenBundle
     */
    public static final String getDisplayableName(final Class<? extends ModelElement> clazz) {
        return getDisplayableName(clazz, false);
    }

    /**
     * Liefert den Standardanzeigenamen für ein Modelelement der übergebenen Art
     *
     * @param me
     * @return
     * @see getDisplayableName
     */
    public static final String getDisplayableName(final ModelElement me) {
        return getDisplayableName(me.getClass());
    }

    /**
     * @param edgeClass
     * @return
     * @see ElementsNameBuilder#getMetaAssociationName(Class, boolean, ModelConstants.ConnectionState, boolean, boolean, String)
     */
    public static String getForwardMetaAssociationName(final Class<? extends Edge> edgeClass) {
        return ElementsNameBuilder.getForwardMetaAssociationName(edgeClass, false, false);
    }

    /**
     * Liefert den Meta-Namen der Kanteklasse für die Vorwärtsrichtung ohne die Elementartnamen, die die Edge verbindet.
     *
     * @param edgeClass
     * @return
     * @see ElementsNameBuilder#getMetaAssociationName(Class, boolean, ModelConstants.ConnectionState, boolean, boolean, String)
     */
    public static String getFullForwardMetaAssociationName(final Class<? extends Edge> edgeClass) {
        return ElementsNameBuilder.getForwardMetaAssociationName(edgeClass, true, true);
    }

    /**
     * @param edgeClass
     * @param appendPrefixClass
     * @param appendPostfixClass
     * @return
     * @see ElementsNameBuilder#getMetaAssociationName(Class, boolean, ModelConstants.ConnectionState, boolean, boolean, String)
     */
    public static String getForwardMetaAssociationName(final Class<? extends Edge> edgeClass, final boolean appendPrefixClass, final boolean appendPostfixClass) {
        return ElementsNameBuilder.getForwardMetaAssociationName(edgeClass, ModelConstants.ConnectionState.DOUBLE, appendPrefixClass, appendPostfixClass);
    }

    /**
     * @param edgeClass
     * @param doubleMeaningEdgeMeaningDirection
     * @param appendPrefixClass
     * @param appendPostfixClass
     * @return
     * @see ElementsNameBuilder#getMetaAssociationName(Class, boolean, ModelConstants.ConnectionState, boolean, boolean, String)
     */
    public static String getForwardMetaAssociationName(final Class<? extends Edge> edgeClass, final ModelConstants.ConnectionState doubleMeaningEdgeMeaningDirection, final boolean appendPrefixClass, final boolean appendPostfixClass) {
        return ElementsNameBuilder.getMetaAssociationName(edgeClass, false, doubleMeaningEdgeMeaningDirection, appendPrefixClass, appendPostfixClass);
    }

    /**
     * @param edgeClass
     * @return
     * @see ElementsNameBuilder#getMetaAssociationName(Class, boolean, ModelConstants.ConnectionState, boolean, boolean, String)
     */
    public static String getBackwardMetaAssociationName(final Class<? extends Edge> edgeClass) {
        return ElementsNameBuilder.getBackwardMetaAssociationName(edgeClass, false, false);
    }

    /**
     * Liefert den Meta-Namen der Kanteklasse für die Rückwärtsrichtung mit den Elementartnamen, die die Edge verbindet.
     *
     * @param edgeClass
     * @return
     * @see ElementsNameBuilder#getMetaAssociationName(Class, boolean, ModelConstants.ConnectionState, boolean, boolean, String)
     */
    public static String getFullBackwardMetaAssociationName(final Class<? extends Edge> edgeClass) {
        return ElementsNameBuilder.getBackwardMetaAssociationName(edgeClass, true, true);
    }

    /**
     * @param edgeClass
     * @param appendPrefixClass
     * @param appendPostfixClass
     * @return
     * @see ElementsNameBuilder#getMetaAssociationName(Class, boolean, ModelConstants.ConnectionState, boolean, boolean, String)
     */
    public static String getBackwardMetaAssociationName(final Class<? extends Edge> edgeClass, final boolean appendPrefixClass, final boolean appendPostfixClass) {
        return ElementsNameBuilder.getBackwardMetaAssociationName(edgeClass, ModelConstants.ConnectionState.DOUBLE, appendPrefixClass, appendPostfixClass);
    }

    /**
     * @param edgeClass
     * @param doubleMeaningEdgeMeaningDirection
     * @param appendPrefixClass
     * @param appendPostfixClass
     * @return
     * @see ElementsNameBuilder#getMetaAssociationName(Class, boolean, ModelConstants.ConnectionState, boolean, boolean, String)
     */
    public static String getBackwardMetaAssociationName(final Class<? extends Edge> edgeClass, final ModelConstants.ConnectionState doubleMeaningEdgeMeaningDirection, final boolean appendPrefixClass, final boolean appendPostfixClass) {
        return ElementsNameBuilder.getMetaAssociationName(edgeClass, true, doubleMeaningEdgeMeaningDirection, appendPrefixClass, appendPostfixClass);
    }

    /**
     * @param edgeClass
     * @param switchDefinedDirection
     * @param doubleMeaningEdgeMeaningDirection
     * @return
     * @see ElementsNameBuilder#getMetaAssociationName(Class, boolean, ModelConstants.ConnectionState, boolean, boolean, String)
     */
    public static String getFullMetaAssociationName(final Class<? extends Edge> edgeClass, final boolean switchDefinedDirection, final ModelConstants.ConnectionState doubleMeaningEdgeMeaningDirection) {
        return ElementsNameBuilder.getMetaAssociationName(edgeClass, switchDefinedDirection, doubleMeaningEdgeMeaningDirection, true, true);
    }

    /**
     * Liefert in Abhängigkeit von der Richtung den Meta-Namen der Kanteklasse. Bei Kanten mit doppelter Bedeutung kommt hier der Name mit beiden
     * Bedeutungen zurück.
     *
     * @param edgeClass
     * @param switchDefinedDirection gibt an, ob die Bedeutung der Edge von der Startklasse zur Endklasse (<code>false</code>) oder von der Endklasse
     *            zur Startklasse (<code>true</code>) zurück gegeben werden soll. Mit Start- und Endklasse sind hier die
     *            beiden Elementklasse gemeint, die in der Kantenklasse in dieser Reihenfolge definiert sind
     * @return
     */
    public static String getMetaAssociationName(final Class<? extends Edge> edgeClass, final boolean switchDefinedDirection) {
        return ElementsNameBuilder.getMetaAssociationName(edgeClass, switchDefinedDirection, ModelConstants.ConnectionState.DOUBLE, false, false);
    }

    /**
     * Liefert in Abhängigkeit von der Richtung den Meta-Namen der Kanteklasse
     *
     * @param edgeClass
     * @param switchDefinedDirection gibt an, ob die Bedeutung der Edge von der Startklasse zur Endklasse (<code>false</code>) oder von der Endklasse
     *            zur Startklasse (<code>true</code>) zurück gegeben werden soll. Mit Start- und Endklasse sind hier die
     *            beiden Elementklasse gemeint, die in der Kantenklasse in dieser Reihenfolge definiert sind
     * @param doubleMeaningEdgeMeaningDirection Doppelkante.FORWARD, Doppelkante.BACKWARD oder Doppelkante.DOUBLE - Bei allen Assoziationen, die in
     *            jede Richtung nur eine Bedeutung haben, ist dieser Parameter egal. Bei Assoziationen, die mehr als eine Bedeutung haben, kann hier
     *            die Richtung angegeben werden für die die bedeutung zurück gegeben werden soll.<br>
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
     * @return
     */
    public static String getMetaAssociationName(final Class<? extends Edge> edgeClass, final boolean switchDefinedDirection, final ModelConstants.ConnectionState doubleMeaningEdgeMeaningDirection) {
        return ElementsNameBuilder.getMetaAssociationName(edgeClass, switchDefinedDirection, doubleMeaningEdgeMeaningDirection, false, false);
    }

    /**
     * Liefert in Abhängigkeit von der Richtung den Meta-Namen der Kanteklasse
     *
     * @param edgeClass
     * @param switchDefinedDirection gibt an, ob die Bedeutung der Edge von der Startklasse zur Endklasse (<code>false</code>) oder von der Endklasse
     *            zur Startklasse (<code>true</code>) zurück gegeben werden soll. Mit Start- und Endklasse sind hier die
     *            beiden Elementklasse gemeint, die in der Kantenklasse in dieser Reihenfolge definiert sind
     * @param doubleMeaningEdgeMeaningDirection MeaningDirection.FORWARD, MeaningDirection.BACKWARD oder MeaningDirection.DOUBLE - Bei allen
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
    public static String getMetaAssociationName(final Class<? extends Edge> edgeClass, final boolean switchDefinedDirection, final ModelConstants.ConnectionState doubleMeaningEdgeMeaningDirection, final String doubleMeaningEdgeDelimiter) {
        //alle Kantenamen müssen mit SimplerKantenklassenName_f oder SimplerKantenklassenName_b angegeben sein oder bei Kanten mit doppelter Bedeutung SimplerKantenklassenName_f_f,
        //SimplerKantenklassenName_f_b, SimplerKantenklassenName_b_f und SimplerKantenklassenName_b_b
        String edgeClassName = edgeClass.getSimpleName();
        final String mainEdgeDirection = !switchDefinedDirection ? "_f" : "_b";
        String edgeName = ElementsNameBuilder.getEdgeName(edgeClassName, mainEdgeDirection);
        if (edgeName != null) {
            return edgeName;
        }
        //bei Kanten mit doppelter Bedeutung
        if (DoubleMeaningEdge.class.isAssignableFrom(edgeClass)) {
            if (edgeName == null) {
                String interpretedDirection = doubleMeaningEdgeMeaningDirection == ModelConstants.ConnectionState.FORWARD ? "_f" : doubleMeaningEdgeMeaningDirection == ModelConstants.ConnectionState.BACKWARD ? "_b" : null;
                if (interpretedDirection != null) {
                    edgeName = ElementsNameBuilder.getEdgeName(edgeClassName, mainEdgeDirection, interpretedDirection);
                    if (edgeName != null) {
                        return edgeName;
                    }
                } else {
                    edgeName = ElementsNameBuilder.getEdgeName(edgeClassName, mainEdgeDirection, "_f");
                    if (edgeName != null) {
                        //wenn es einen Vorwärtsnamen gibt, dann muss auch ein Rückwärtsname angegeben sein!
                        return edgeName + doubleMeaningEdgeDelimiter + ElementsNameBuilder.getEdgeName(edgeClassName, mainEdgeDirection, "_b");
                    }
                }
            }
        }
        //wenn für den aktuellen Klassennamen kein Name gefunden wurde -> nimm die Oberklasse -> irgendwann kommt man bei Edge.class an, für die auf jeden Fall ein Namen ex.
        return getMetaAssociationName(edgeClass.getSuperclass().asSubclass(Edge.class), switchDefinedDirection, doubleMeaningEdgeMeaningDirection, doubleMeaningEdgeDelimiter);
    }

    private static final String getEdgeName(final String simpleEdgeClassName, final String mainEdgeDirection) {
        return ElementsNameBuilder.getEdgeName(simpleEdgeClassName, mainEdgeDirection, null);
    }

    private static final String getEdgeName(final String simpleEdgeClassName, final String mainEdgeDirection, final String interpretedDirection) {
        try {
            if (interpretedDirection == null) {
                return getResString(simpleEdgeClassName + mainEdgeDirection);
            }
            return getResString(simpleEdgeClassName + mainEdgeDirection + interpretedDirection);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * @param edgeClass
     * @param switchDefinedDirection
     * @param doubleMeaningEdgeMeaningDirection
     * @param appendPrefixClass
     * @param appendPostfixClass
     * @return
     * @see getMetaAssociationName
     */
    public static String getMetaAssociationName(final Class<? extends Edge> edgeClass, final boolean switchDefinedDirection, final ModelConstants.ConnectionState doubleMeaningEdgeMeaningDirection, final boolean appendPrefixClass,
            final boolean appendPostfixClass) {
        return ElementsNameBuilder.getMetaAssociationName(edgeClass, switchDefinedDirection, doubleMeaningEdgeMeaningDirection, appendPostfixClass, appendPrefixClass, " / ");
    }

    /**
     * @param edgeClass
     * @param switchDefinedDirection
     * @param doubleMeaningEdgeMeaningDirection
     * @param appendPrefixClass
     * @param appendPostfixClass
     * @param doubleMeaningEdgeDelimiter String der bei Kanten mit doppelter Bedeutung, bei denen beide Bedeutungen gleichzeitig ausgegeben werden
     *            sollen zwischen die beiden Bedeutungen geschrieben wird.
     * @return
     * @see getMetaAssociationName
     */
    public static String getMetaAssociationName(final Class<? extends Edge> edgeClass, final boolean switchDefinedDirection, final ModelConstants.ConnectionState doubleMeaningEdgeMeaningDirection, final boolean appendPrefixClass,
            final boolean appendPostfixClass, final String doubleMeaningEdgeDelimiter) {
        if (!appendPrefixClass && !appendPostfixClass) {
            return getMetaAssociationName(edgeClass, switchDefinedDirection, doubleMeaningEdgeMeaningDirection, doubleMeaningEdgeDelimiter);
        }
        StringBuilder sb = new StringBuilder();
        if (appendPrefixClass) {
            sb.append(getDisplayableName(!switchDefinedDirection ? getStartClass(edgeClass) : getEndClass(edgeClass)));
            sb.append(" ");
        }
        sb.append(getMetaAssociationName(edgeClass, switchDefinedDirection, doubleMeaningEdgeMeaningDirection, doubleMeaningEdgeDelimiter));
        if (appendPostfixClass) {
            sb.append(" ");
            sb.append(getDisplayableName(!switchDefinedDirection ? getEndClass(edgeClass) : getStartClass(edgeClass)));
        }
        return sb.toString();
    }

    /**
     * Gibt Namenskuerzel einer Elementklasse zurueck. Diese Namenskürzel garantieren nicht, dass man von ihnen auf die Klasse zurückschließen kann.
     * Sie dienen lediglich dazu, die Hash-Strings der Modellelemente im Baukasten und der XML-Datei etwas
     * lesbarer zu gestalten.
     *
     * @param elementClass Elementklasse für die das Kürzel zurück gegeben werden soll.
     * @return String mit Namenskuerzel
     */
    public static final String getShortName(final Class<? extends ModelElement> elementClass) {

        //HashMap mit den ShortNames der Klassen initialisieren (einmal statisch)
        if (ElementsNameBuilder.elementClassToHashShortName == null) {
            ElementsNameBuilder.elementClassToHashShortName = new HashMap<>();
            //Set in das alle bisher gefundenen ShortNames eingetragen werden, um zu prüfen, ob ein shortName bereits existiert
            HashSet<String> allShortNames = new HashSet<>();
            loop1: for (int i = 0; i < ModelConstants.ALL_NODES.length; i++) {
                String s = ModelConstants.ALL_NODES[i].getSimpleName();
                //wenn der Klassenname aus weniger als 4 Zeichen besteht
                if (s.length() <= 3) {
                    ElementsNameBuilder.elementClassToHashShortName.put(ModelConstants.ALL_NODES[i], s.toUpperCase());
                    continue;
                }
                //mehr als 3 Zeichen
                StringBuilder shortName = new StringBuilder(3);
                for (int j = 0; j < s.length(); j++) {
                    //suche Großbuchstaben -> sie werden bevorzugt in den Shortname aufgenommen
                    String character = s.substring(j, j + 1);
                    if (character.toUpperCase().equals(character)) {
                        shortName.append(character);
                        //wenn 3 Großbuchstaben gefunden wurden
                        if (shortName.length() == 3) {
                            String sn = shortName.toString();
                            //wenn es den ShortName noch nicht gibt
                            if (!allShortNames.contains(sn)) {
                                allShortNames.add(sn);
                                ElementsNameBuilder.elementClassToHashShortName.put(ModelConstants.ALL_NODES[i], sn);
                                continue loop1;
                            }
                            //es gibt den ShortName bereits -> letztes Zeichen löschen und weiter nach Großbuchstanben suchen
                            shortName.deleteCharAt(2);
                        }
                    }
                }
                //hier kommt er nur hin, wenn keine 3 Großbuchstaben gefunden wurden
                //short name hat 0 bis 2 Zeichen

                //wenn genau 2 Großbuchstaben gefunden wurden
                if (shortName.length() == 2) {
                    int lastUpperCharInClassName = 0;
                    for (int j = 0; j < shortName.length(); j++) {
                        char shortNameChar = shortName.charAt(j);
                        for (; lastUpperCharInClassName < s.length(); lastUpperCharInClassName++) {
                            if (s.charAt(lastUpperCharInClassName) == shortNameChar) {
                                break;
                            }
                        }
                    }
                    //lastUpperCharInClassName hat jetzt den Index des letzten Großbuchstaben in shortName

                    //solange hinter dem letzten Großbuchstaben noch Zeichen kommen, einfach solange diese Zeichen anhängen,
                    //bis ein eindeutiger 3-Zeichen-shortName gefunden wurde
                    while (++lastUpperCharInClassName < s.length()) {
                        shortName.append(s.charAt(lastUpperCharInClassName));
                        String sn = shortName.toString().toUpperCase();
                        //wenn es den ShortName noch nicht gibt
                        if (!allShortNames.contains(sn)) {
                            allShortNames.add(sn);
                            ElementsNameBuilder.elementClassToHashShortName.put(ModelConstants.ALL_NODES[i], sn);
                            continue loop1;
                        }
                        //es gibt den ShortName bereits -> letztes Zeichen löschen und weiter suchen
                        shortName.deleteCharAt(2);
                    }
                }

                //es wurden keine 3 eindutigen Buchstaben nach Großbuchstaben gefunden -> Nimm einfach die ersten beiden
                //Buchstaben und suche einen Folgebuchstaben bis 3 eindeutige Zeichen gefunden werden (das geht immer gut,
                //wenn die Klassennamen eindeutig sind (was immer der Fall ist, wenn sie im selben package liegen) und hier
                ///unten fest steht, dass der Name mind. 4 Zeichen lang ist)
                shortName.setLength(0);
                shortName.append(s.charAt(0));
                shortName.append(s.charAt(1));
                for (int j = 2; j < s.length(); j++) {
                    shortName.append(s.charAt(j));
                    String sn = shortName.toString().toUpperCase();
                    // wenn es den ShortName noch nicht gibt
                    if (!allShortNames.contains(sn)) {
                        allShortNames.add(sn);
                        ElementsNameBuilder.elementClassToHashShortName.put(ModelConstants.ALL_NODES[i], sn);
                        continue loop1;
                    }
                    //es gibt den ShortName bereits -> letztes Zeichen löschen und weiter suchen
                    shortName.deleteCharAt(2);
                }

                //wenn auch das nicht gekalppt hat (der Fall dürfte nicht eintreten, wenn die Klassen alle im gleichen Package liegen,
                //da sie dann alle etwas eindeutiges bei s = class.getShortName() geliefert haben)
                //-> nimm einfach die ersten 3 Zeichen ohne noch einmal irgendwelche Eindeutigkeit zu prüfen;
                String sn = s.substring(0, 3).toUpperCase();
                allShortNames.add(sn); //kann man sich wahrscheinlich sparen, weil auch diese Kombination schon oben durchprobiert wurde, aber sicher ist sicher
                ElementsNameBuilder.elementClassToHashShortName.put(ModelConstants.ALL_NODES[i], sn);
            }
        }

        //Node
        if (Node.class.isAssignableFrom(elementClass)) {
            Object o = ElementsNameBuilder.elementClassToHashShortName.get(elementClass);
            //ist null bei Layerknoten. Die brauchen aber auch keinen lesbaren Hash
            if (o == null) {
                return ModelConstants.NO_MODEL_ELEMENT_SHORT_NAME;
            }
            return ElementsNameBuilder.elementClassToHashShortName.get(elementClass).toString();
            //Kanten
        } else if (Edge.class.isAssignableFrom(elementClass)) {
            return ModelConstants.EDGE_SHORT_NAME;
        }
        return ModelConstants.NO_MODEL_ELEMENT_SHORT_NAME;
    }

}
