package de.imise.tool3lgm.graphtools.metamodel.elements;

import static de.imise.tool3lgm.graphtools.metamodel.ModelConstants.STANDARD_ERROR_INT_VALUE;

import de.imise.tool3lgm.graphtools.metamodel.EdgeCardinality;
import de.imise.tool3lgm.graphtools.metamodel.ModelConstants;
import de.imise.tool3lgm.graphtools.model.GDCollection;
import de.imise.tool3lgm.graphtools.model.GraphDocument;
import de.imise.tool3lgm.graphtools.view.container.EdgeContainer;
import de.imise.tool3lgm.graphtools.view.container.ElementContainer;
import de.imise.util.ReflectionUtils;

/**
 * @author N.N.
 */
public abstract class Edge extends ModelElement {

    /**
     * Name des <code>Class&lt;? extends ModelElement&gt;[]</code>-Arrays, mit dem jede Kantenklasse alle seine Startklassen beschreibt. Über diesen
     * Namen werden die Startklassen per Reflection ermittelt.
     */
    public static final String START_CLASS_FIELD_NAME = "stcl";

    /**
     * Name des <code>Class&lt;? extends ModelElement&gt;[]</code>-Arrays, mit dem jede Kantenklasse alle seine Endklassen beschreibt. Über diesen
     * Namen werden die Endklassen per Reflection ermittelt.
     */
    public static final String END_CLASS_FIELD_NAME = "etcl";

    /**
     * Name des <code>int[]</code>-Arrays, mit dem jede Kantenklasse die Kardinalitäten beschreibt, mit denen Elemente der Startklasse für Elemente
     * der Endklasse vorhanden sein müssen.<br />
     * Über diesen Namen werden die Kardinalitäten per Reflection ermittelt. Das Array sollte immer nur 2 int-Werte für Minimum und Maximum enthalten.
     */
    public static final String START_CARDINALITY_FIELD_NAME = "scard";

    /**
     * Name des <code>int[]</code>-Arrays, mit dem jede Kantenklasse die Kardinalitäten beschreibt, mit denen Elemente der Endklasse für Elemente der
     * Startklasse vorhanden sein müssen.<br />
     * Über diesen Namen werden die Kardinalitäten per Reflection ermittelt. Das Array sollte immer nur 2 int-Werte für Minimum und Maximum enthalten.
     */
    public static final String END_CARDINALITY_FIELD_NAME = "ecard";

    /**
     * Auch für Kanten muss angegeben, welche Elementarten sie verbinden können, damit die Vererbung bei der Definition der MetaPfade funktioniert,
     * die getStartElementClass() und getEndElementClass() aufruft.
     */
    public static final Class<? extends ModelElement> stcl = ModelElement.class;

    /**
     * Auch für Kanten muss angegeben, welche Elementarten sie verbinden können, damit die Vererbung bei der Definition der MetaPfade funktioniert,
     * die getStartElementClass() und getEndElementClass() aufruft.
     */
    public static final Class<? extends ModelElement> etcl = ModelElement.class;

    /**
     * The two elements wchich are connected by this edge. <code>k1</code> is the start element and <code>k2</code> the end element of this edge.
     */
    protected ModelElement k1, k2;

    /**
     * Hash-Strings of the start
     */
    private String start_hash, end_hash;

    ///////////////////////////////////////////
    // Richtungskram aus ehemals Doppelkante //
    ///////////////////////////////////////////
    public static final int FORWARD = 1;

    public static final int BACKWARD = 2;

    public static final int DOUBLE = 0;

    public static final int ANY = -2;

    public static final int NOTCONNECTED = -1;

    protected int direction = FORWARD;

    public static final int[] DIRECTION = {
            NOTCONNECTED,
            DOUBLE,
            FORWARD,
            BACKWARD
    };

    public static final String[] DIRECTION_STR = {
            "NOTCONNECTED",
            "DOUBLE",
            "FORWARD",
            "BACKWARD"
    };

    @Override
    public final Edge clone() {
        Edge retVal = (Edge) super.clone();
        retVal.start_hash = "";
        retVal.end_hash = "";
        return retVal;
    }

    @Override
    public int layerFor() {
        int layer = super.layerFor();
        if (layer == ModelConstants.NO_LAYER) {
            layer = ModelConstants.getEdgeLayer(k1.getClass(), k2.getClass());
        }
        return layer;
    }

    /**
     * liefert true, wenn beide Node, die die Edge verbindet identisch sind und die Edge von derselben Art ist (Richtung ist egal)
     *
     * @param kante
     * @return
     */
    public final boolean isEqualTo(final Edge kante) {
        return (k1 == kante.getStart() && k2 == kante.getEnd() || k2 == kante.getStart() && k1 == kante.getEnd()) && getClass() == kante.getClass();
        //		if ((k1 == kante.getStart() && k2 == kante.getEnd()) || (k2 == kante.getStart() && k1 == kante.getEnd()))
        //			return true;
        //		return false;
    }

    /**
     * @param _k1
     * @param _k2
     */
    public final void setKnots(final ModelElement _k1, final ModelElement _k2) {
        setKnots(_k1, _k2, true);
    }

    /**
     * @param _k1
     * @param _k2
     * @param registerInKnots
     */
    public void setKnots(final ModelElement _k1, final ModelElement _k2, final boolean registerInKnots) {
        k1 = _k1;
        k2 = _k2;
        if (registerInKnots) {
            if (_k1 != null) {
                _k1.addEdge(this);
            }
            if (_k2 != null) {
                _k2.addEdge(this);
            }
        }
        //Validität der Edge prüfen und dabei wenn nötig umdrehen (bis Version 3.2
        //ist teilweise die Reihenfolge der Start- und Endelemente von Kanten andersherum gewesen,
        //als sie in der Kantenklasse festgelegt sind. Das wird hier grade gebogen
        checkValidity();
        //		if (!checkValidity());
        //			System.err.println(getClass().getSimpleName() + " : Edge with Node1 = "  + k1.getClearName() + " and Node2 " + k2.getClearName() + " is not valid. ");
        //

    }

    /**
     * Setzt fuer die Edge die Node Anfang=_k1 und Ende=_k2 und fügt die Edge bei _k1 an Position _k1EdgePos und bei _k2 an Position _k2EdgePos
     * ein.
     *
     * @param _k1 Node
     * @param pos int Position
     * @param _k2 Node
     */
    /**
     * @param _k1 Startelement der Edge
     * @param _k1EdgePos Postion der Edge in der Kantenliste von _k1. Wenn der Wert größer oder kleiner als die aktuelle Liste ist, dann wird die
     *            Edge hinten angefügt.
     * @param _k2 Endelement der Edge
     * @param _k2EdgePos Postion der Edge in der Kantenliste von _k2. Wenn der Wert größer oder kleiner als die aktuelle Liste ist, dann wird die
     *            Edge hinten angefügt.
     */
    public void setKnotsAndInsert(final ModelElement _k1, final int _k1EdgePos, final ModelElement _k2, final int _k2EdgePos) {
        k1 = _k1;
        k2 = _k2;
        //wenn IsPartOfEdgeen im Kreis modelliert wurden, wird die falsche Beziehung gleich wieder entfernt
        //und ihre alten Start- und Endelemente gesetzt, die bei einer neuen Edge immer null waren -> null hier abfangen
        if (_k1 != null) {
            _k1.insertEdge(this, _k1EdgePos);
        }
        if (_k2 != null) {
            _k2.insertEdge(this, _k2EdgePos);
        }
    }

    /**
     * @param start
     */
    public void setStartAndInsert(final ModelElement start) {
        k1 = start;
        if (k1 != null) {
            k1.addEdge(this);
        }
    }

    /**
     * @param end
     */
    public void setEndAndInsert(final ModelElement end) {
        k2 = end;
        if (k2 != null) {
            k2.addEdge(this);
        }
    }

    /**
     * @return
     */
    public final ModelElement getStart() {
        return k1;
    }

    /**
     * @param me
     * @return
     */
    public final boolean isStart(final ModelElement me) {
        return getStart() == me;
    }

    /**
     * @return
     */
    public final ModelElement getEnd() {
        return k2;
    }

    /**
     * @param me
     * @return
     */
    public final boolean isEnd(final ModelElement me) {
        return getEnd() == me;
    }

    /**
     * Wenn das übergebene Element durch diese Edge mit einem anderen Element verbunden ist, kommt das andere Element der Edge zurück, sons
     * <code>null</code>.
     *
     * @param me
     * @return
     */
    public final ModelElement getOther(final ModelElement me) {
        if (isStart(me)) {
            return getEnd();
        }
        if (isEnd(me)) {
            return getStart();
        }
        return null;
    }

    /**
     * Wenn die übergebene Elementklasse durch eine Edge der angegebenen Art mit anderen Elementen verbunden sein kann, dann wird die Elementklasse
     * dieser anderen Elemente zurück gegeben. Passen Edge und Elementklasse nicht zusammen, kommt <code>null</code> zurück.
     *
     * @param edgeClass Kantanklasse, von der die andere verbundene Elementklasse zurück gegeben werden soll
     * @param meClass Elementklasse der Edge, deren Gegenelementklasse zurück gegeben werden soll
     * @return die andere Elementklasse der Edge, als die übergebene Klasse oder <code>null</code>, wenn die Klasse gar nicht passt
     */
    public static final Class<? extends ModelElement> getOther(final Class<? extends Edge> edgeClass, final Class<? extends ModelElement> meClass) {
        if (isStartClass(edgeClass, meClass)) {
            return getEndClass(edgeClass);
        }
        if (isEndClass(edgeClass, meClass)) {
            return getStartClass(edgeClass);
        }
        return null;
    }

    @Override
    public final boolean putXMLFieldString(final String field, final String value) {
        if (field.equals("start")) {
            start_hash = value;
            return true;
        }
        if (field.equals("end")) {
            end_hash = value;
            return true;
        }
        if (field.equals("direction")) {
            return true;
        }
        if (field.equals("master_slave")) {
            return true;
        }
        if (field.equals("state")) {
            for (int i = 0; i < DIRECTION_STR.length; i++) {
                if (value.equals(DIRECTION_STR[i])) {
                    setDirection(i - 1);
                    return true;
                }
            }
            return false;
        }
        return super.putXMLFieldString(field, value);
    }

    /**
     * @param doc
     * @return
     */
    public final boolean decodeHashStrings(final GraphDocument doc) {
        ModelElement _k1 = null, _k2 = null;
        if (start_hash == null || end_hash == null) {
            return false;
        }
        _k1 = doc.findElementCoded(start_hash);
        if (_k1 == null) {
            System.out.println("Error decoding start element hash \"" + start_hash + "\"" + getClass());
        }

        _k2 = doc.findElementCoded(end_hash);
        if (_k2 == null) {
            System.out.println("Error decoding end element hash \"" + end_hash + "\"" + getClass());
        }

        if (_k1 == null || _k2 == null) {
            return false;
        }
        setKnots(_k1, _k2);

        return true;
    }

    /**
     * @param _k1
     * @param _k2
     * @return
     */
    private final boolean isDirectingForward(final ModelElement _k1, final ModelElement _k2) {
        return k1 == _k1 && k2 == _k2;
    }

    /**
     * @param _k1
     * @param _k2
     * @return
     */
    public final boolean isConnecting(final ModelElement _k1, final ModelElement _k2) {
        return k1 == _k1 && k2 == _k2 || k1 == _k2 && k2 == _k1;
    }

    /**
     * @return
     */
    public final String _toString() {
        return " - ";
    }

    /**
     * Prüft die Validität der Kanten und stellt sie wenn möglich her. Die Prüfung betrifft die Art der Kantenelemente
     *
     * @return <code>true</code>, wenn die Edge vollständig richtig ist
     */
    public boolean checkValidity() {
        boolean startClassOk = false, endClassOk = false;
        boolean switchStart = false, switchEnd = false;
        if (k1 != null && k2 != null) {
            Class<? extends ModelElement> clazz = k1.getClass();
            //prüfen, ob das StartElement von einer der Startklassen ist
            if (!isStartClass(clazz)) {
                //wenn nicht
                switchStart = isEndClass(clazz);
            } else {
                startClassOk = true;
            }
            clazz = k2.getClass();
            //prüfen, ob das EndElement von einer der Endklassen ist
            if (!isEndClass(clazz)) {
                //wenn nicht
                switchEnd = isStartClass(clazz);
            } else {
                endClassOk = true;
            }
        }
        boolean switchClasses = false;
        //wenn sich die Konsitenz herstellen lässt indem man beide Elemente vertaucht -> vertauschen
        if (switchStart && switchEnd) {
            switchClasses = true;
        } else if (switchStart) {
            if (!isStartClass(k2.getClass())) {
                return false;
            }
            switchClasses = true;
        } else if (switchEnd) {
            if (!isEndClass(k1.getClass())) {
                return false;
            }
            switchClasses = true;
        }
        //bei allen Kanten, bei denen die Richtung egal ist, wird sie immer auf DOUBLE gesetzt (das macht die GDCollection in link auch!)
        if (!ModelConstants.isDirectedEdge(getClass())) {
            direction = DOUBLE;
        }
        if (switchClasses) {
            ModelElement dummy = k1;
            k1 = k2;
            k2 = dummy;
            if (direction == FORWARD) {
                direction = BACKWARD;
            } else if (direction == BACKWARD) {
                direction = FORWARD;
            }
            return true;
        }
        //Es musste nichts vertauscht werden -> hier kommt nur true zurück, wenn die Klassen
        //der Start- und Endelemente mit den Metaklassen übereinstimmen.
        return startClassOk && endClassOk;
    }

    @Override
    public final ElementContainer createContainer(final GraphDocument doc) {
        return new EdgeContainer(this, doc);
    }

    //////////////////////////
    // Start- und Endklasse //
    //////////////////////////

    /**
     * @param edgeClass
     * @param start
     * @return
     */
    private static final Class<? extends ModelElement> getStartOrEndClass(final Class<? extends Edge> edgeClass, final boolean start) {
        String fieldName = start ? START_CLASS_FIELD_NAME : END_CLASS_FIELD_NAME;
        return ((Class<?>) ReflectionUtils.getField(edgeClass, ModelElement.class, fieldName)).asSubclass(ModelElement.class);
    }

    /**
     * @param edgeClass
     * @return
     */
    public static final Class<? extends ModelElement> getStartClass(final Class<? extends Edge> edgeClass) {
        return getStartOrEndClass(edgeClass, true);
    }

    /**
     * @return
     */
    public final Class<? extends ModelElement> getStartClass() {
        return getStartClass(getClass());
    }

    /**
     * @param edgeClass
     * @return
     */
    public final static Class<? extends ModelElement> getEndClass(final Class<? extends Edge> edgeClass) {
        return getStartOrEndClass(edgeClass, false);
    }

    /**
     * @return
     */
    public final Class<? extends ModelElement> getEndClass() {
        return getEndClass(getClass());
    }

    /**
     * Liefert <code>true</code>, wenn die übergebene Klasse die Startklasse der Edge oder eine Ober- oder Unterklasse davon ist.
     *
     * @param edgeClass
     * @param elementClass
     * @return
     */
    public static final boolean isStartClass(final Class<? extends Edge> edgeClass, final Class<? extends ModelElement> elementClass) {
        Class<? extends ModelElement> startClass = getStartClass(edgeClass);
        return startClass.isAssignableFrom(elementClass) || elementClass.isAssignableFrom(startClass);
    }

    /**
     * Liefert <code>true</code>, wenn die übergebene Klasse die Startklasse der Edge oder eine Ober- oder Unterklasse davon ist.
     *
     * @param elementClass
     * @return
     */
    public final boolean isStartClass(final Class<? extends ModelElement> elementClass) {
        return isStartClass(getClass(), elementClass);
    }

    /**
     * Liefert <code>true</code>, wenn die übergebene Klasse die Endklasse der Edge oder eine Ober- oder Unterklasse davon ist.
     *
     * @param edgeClass
     * @param elementClass
     * @return
     */
    public static final boolean isEndClass(final Class<? extends Edge> edgeClass, final Class<? extends ModelElement> elementClass) {
        Class<? extends ModelElement> endClass = getEndClass(edgeClass);
        return endClass.isAssignableFrom(elementClass) || elementClass.isAssignableFrom(endClass);
    }

    /**
     * Liefert <code>true</code>, wenn die übergebene Klasse die Endklasse der Edge oder eine Ober- oder Unterklasse davon ist.
     *
     * @param elementClass
     * @return
     */
    public final boolean isEndClass(final Class<? extends ModelElement> elementClass) {
        return isEndClass(getClass(), elementClass);
    }

    /**
     * Liefert <code>true</code>, wenn die übergebene Klasse die Start- oder Endklasse der Edge oder eine Ober- oder Unterklasse davon ist.
     *
     * @param edgeClass
     * @param elementClass
     * @return
     */
    public static final boolean isStartOrEndClass(final Class<? extends Edge> edgeClass, final Class<? extends ModelElement> elementClass) {
        return isStartClass(edgeClass, elementClass) || isEndClass(edgeClass, elementClass);
    }

    /**
     * Liefert <code>true</code>, wenn die übergebene Kantenklasse Elemente der angegebenen Arten miteinander verbindet.
     *
     * @param edgeClass
     * @param elementClass1
     * @param elementClass2
     * @return
     */
    public static final boolean isConnecting(final Class<? extends Edge> edgeClass, final Class<? extends ModelElement> elementClass1, final Class<? extends ModelElement> elementClass2) {
        return isConnectingForward(edgeClass, elementClass1, elementClass2) || isConnectingForward(edgeClass, elementClass2, elementClass1);
    }

    /**
     * Liefert <code>true</code>, wenn die übergebene Kantenklasse Elemente der angegebenen Arten in Vorwärtsrichtung miteinander verbindet. Also
     * <code>startElementClass</code> die Startklasse der Kantenklasse oder eine Unterklasse davon ist und <code>endElementClass</code> die Endklasse
     * der Kantenklasse oder eine Unterklasse davon ist.
     *
     * @param edgeClass
     * @param startElementClass
     * @param endElementClass
     * @return
     */
    public static final boolean isConnectingForward(final Class<? extends Edge> edgeClass, final Class<? extends ModelElement> startElementClass, final Class<? extends ModelElement> endElementClass) {
        return isStartClass(edgeClass, startElementClass) && isEndClass(edgeClass, endElementClass);
    }

    /**
     * Liefert <code>true</code>, wenn die Start- und Endelemente von derselben Klasse sein können, d.h. wenn die beiden Klassen
     * gleich sind oder eine eine Oberklasse der anderen ist.
     *
     * @param edgeClass
     * @return
     */
    public static boolean isConnectingSameElementClasses(final Class<? extends Edge> edgeClass) {
        Class<? extends ModelElement> startClass = getStartClass(edgeClass);
        Class<? extends ModelElement> endClass = getEndClass(edgeClass);
        return startClass.isAssignableFrom(endClass) || endClass.isAssignableFrom(startClass);
    }

    ////////////////////
    // Kardinalitäten //
    ////////////////////

    /**
     * @param edgeClass
     * @param backward
     * @return
     */
    private static final EdgeCardinality getCardinality(final Class<? extends Edge> edgeClass, final boolean backward) {
        String fieldName = backward ? START_CARDINALITY_FIELD_NAME : END_CARDINALITY_FIELD_NAME;
        return (EdgeCardinality) ReflectionUtils.getField(edgeClass, ModelElement.class, fieldName);
    }

    /**
     * Liefert die Kardinalität für Kanten der übergebenen Art, die ein Element der übergebenen Art zu anderen Elementen hat.
     *
     * @param edgeClass
     * @param elementClass
     * @return
     */
    public static final EdgeCardinality getCardinality(final Class<? extends ModelElement> elementClass, final Class<? extends Edge> edgeClass) {
        if (isStartClass(edgeClass, elementClass)) {
            return getCardinality(edgeClass, false);
        }
        if (isEndClass(edgeClass, elementClass)) {
            return getCardinality(edgeClass, true);
        }
        return null;
    }

    /**
     * Liefert die minimale Anzahl von Kanten der übergebenen Art, die ein Element der übergebenen Art zu anderen Elementen haben muss.
     *
     * @param edgeClass
     * @param elementClass
     * @return
     */
    public static final int getMinCardinality(final Class<? extends ModelElement> elementClass, final Class<? extends Edge> edgeClass) {
        EdgeCardinality cardinality = getCardinality(elementClass, edgeClass);
        return cardinality != null ? cardinality.min() : STANDARD_ERROR_INT_VALUE;
    }

    /**
     * Liefert die maximale Anzahl von Kanten der übergebenen Art, die ein Element der übergebenen Art zu anderen Elementen haben kann.
     *
     * @param edgeClass
     * @param elementClass
     * @return
     */
    public static final int getMaxCardinality(final Class<? extends ModelElement> elementClass, final Class<? extends Edge> edgeClass) {
        EdgeCardinality cardinality = getCardinality(elementClass, edgeClass);
        return cardinality != null ? cardinality.max() : STANDARD_ERROR_INT_VALUE;
    }

    /**
     * @param edgeClass
     * @return
     */
    public static final EdgeCardinality getForwardCardinality(final Class<? extends Edge> edgeClass) {
        return getCardinality(edgeClass, false);
    }

    /**
     * @param edgeClass
     * @return
     */
    public static final EdgeCardinality getBackwardCardinality(final Class<? extends Edge> edgeClass) {
        return getCardinality(edgeClass, true);
    }

    /**
     * @param edgeClass
     * @return
     */
    public static final int getMinForwardCardinality(final Class<? extends Edge> edgeClass) {
        return getCardinality(edgeClass, false).min();
    }

    /**
     * @return
     */
    public final int getMinForwardCardinality() {
        return getMinForwardCardinality(getClass());
    }

    /**
     * @param edgeClass
     * @return
     */
    public static final int getMaxForwardCardinality(final Class<? extends Edge> edgeClass) {
        return getCardinality(edgeClass, false).max();
    }

    /**
     * @return
     */
    public final int getMaxForwardCardinality() {
        return getMaxForwardCardinality(getClass());
    }

    /**
     * @param edgeClass
     * @return
     */
    public static final int getMinBackwardCardinality(final Class<? extends Edge> edgeClass) {
        return getCardinality(edgeClass, true).min();
    }

    /**
     * @return
     */
    public final int getMinBackwardCardinality() {
        return getMinBackwardCardinality(getClass());
    }

    /**
     * @param edgeClass
     * @return
     */
    public static final int getMaxBackwardCardinality(final Class<? extends Edge> edgeClass) {
        return getCardinality(edgeClass, true).max();
    }

    /**
     * @return
     */
    public final int getMaxBackwardCardinality() {
        return getMaxBackwardCardinality(getClass());
    }

    /**
     * @return
     */
    public final String getStartHash() {
        return start_hash;
    }

    /**
     * @return
     */
    public final String getEndHash() {
        return end_hash;
    }

    /**
     * @param coll
     * @return
     */
    public final boolean reconnect(final GDCollection coll) {
        if (k1 == null || k2 == null) {
            return false;
        }

        k1 = coll.getMainGraphDocument().findElementCoded(k1.getHashString());
        k2 = coll.getMainGraphDocument().findElementCoded(k2.getHashString());

        if (k1 == null || k2 == null || coll.getMainGraphDocument().findElementCoded(getHashString()) == null) {
            return false;
        }

        boolean reconnectFirst = k1.addEdge(this);
        boolean reconnectSecond = k2.addEdge(this);

        return reconnectFirst || reconnectSecond;
    }

    @Override
    public final boolean join(final ModelElement other, final boolean overwriteHashstringAndExtIDs) {
        if (super.join(other, overwriteHashstringAndExtIDs)) {
            k1 = ((Edge) other).k1;
            k2 = ((Edge) other).k2;
            return true;
        }
        return false;
    }

    @Override
    public final boolean isUnique() {
        //zuerst über die eventuelle schon gesetzten
        //Start- und Endelemente gehen. Wenn die aber
        //noch nicht gesetzt sind, dann über Reflection
        //direkt über die ModelConstants.
        if (k1 != null && k2 != null) {
            return k1.isUnique() || k2.isUnique();
        }
        return ModelConstants.isUnique(getClass());
    }

    @Override
    public final boolean isUnpaintable() {
        if (k1 != null && k2 != null) {
            return k1.isUnpaintable() && k2.isUnpaintable();
        }
        return false;
    }

    ///////////////////////////////////////////
    // Richtungskram aus ehemals Doppelkante //
    ///////////////////////////////////////////
    /**
     * @return
     */
    public int getDirection() {
        return direction;
    }

    /**
     * @param dir
     */
    public void setDirection(final int dir) {
        direction = dir;
    }

    public final String getDirectionName() {
        return DIRECTION_STR[direction + 1];
    }

    public final boolean isDirecting(final ModelElement _k1, final ModelElement _k2) {
        switch (direction) {
        case DOUBLE:
            return isConnecting(_k1, _k2);
        case FORWARD:
            return isDirectingForward(_k1, _k2);
        case BACKWARD:
            return isDirectingForward(_k2, _k1);
        default:
            return false;
        }
    }

    @Override
    protected final int getMaxContainerCount() {
        return isUnique() ? 1 : Integer.MAX_VALUE;
    }

}