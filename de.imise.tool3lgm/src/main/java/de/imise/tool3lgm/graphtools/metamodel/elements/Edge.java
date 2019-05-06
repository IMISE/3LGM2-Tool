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
     * Name der <code>Class&lt;? extends ModelElement&gt;</code>, mit der jede Kantenklasse seine Startklasse beschreibt. Über diesen Namen wird die
     * jeweilige Startklasse per Reflection ermittelt.
     */
    public static final String START_CLASS_FIELD_NAME = "STCL";

    /**
     * Name der <code>Class&lt;? extends ModelElement&gt;</code>, mit dem jede Kantenklasse seine Endklasse beschreibt. Über diesen Namen wird die
     * jeweilige Endklasse per Reflection ermittelt.
     */
    public static final String END_CLASS_FIELD_NAME = "ETCL";

    /**
     * Name des Feldes mit der {@link EdgeCardinality}, mit der jede Kantenklasse die Kardinalitäten beschreibt, mit denen Elemente der Startklasse
     * für Elemente der Endklasse vorhanden sein müssen.<br />
     * Über diesen Namen werden die Kardinalitäten per Reflection ermittelt.
     */
    public static final String START_CARDINALITY_FIELD_NAME = "SCARD";

    /**
     * Name des Feldes mit der {@link EdgeCardinality}, mit der jede Kantenklasse die Kardinalitäten beschreibt, mit denen Elemente der Endklasse für
     * Elemente der Startklasse vorhanden sein müssen.<br />
     * Über diesen Namen werden die Kardinalitäten per Reflection ermittelt.
     */
    public static final String END_CARDINALITY_FIELD_NAME = "ECARD";

    /**
     * Auch für Kanten muss angegeben, welche Elementarten sie verbinden können, damit die Vererbung bei der Definition der MetaPfade funktioniert,
     * die getStartElementClass() und getEndElementClass() aufruft.
     */
    public static final Class<? extends ModelElement> STCL = ModelElement.class;

    /**
     * Auch für Kanten muss angegeben, welche Elementarten sie verbinden können, damit die Vererbung bei der Definition der MetaPfade funktioniert,
     * die getStartElementClass() und getEndElementClass() aufruft.
     */
    public static final Class<? extends ModelElement> ETCL = ModelElement.class;

    /**
     * The two elements wchich are connected by this edge.
     */
    protected ModelElement startElement, endElement;

    /**
     * Hash-Strings of the start
     */
    private String start_hash, end_hash;

    public enum Direction {
        FORWARD,
        BACKWARD;
        //ACHTUNG: toString() darf nicht überschreiben werden und muss dasselbe wie name() zurück liefern, weil das in den UNDO-REDO-Kommandos genutzt wird
    }

    @Override
    public final Edge clone() {
        Edge retVal = (Edge) super.clone();
        retVal.start_hash = "";
        retVal.end_hash = "";
        return retVal;
    }

    @Override
    public final int layerFor() {
        int layer = super.layerFor();
        if (layer == ModelConstants.NO_LAYER) {
            layer = ModelConstants.getEdgeLayer(startElement.getClass(), endElement.getClass());
        }
        return layer;
    }

    /**
     * liefert true, wenn beide Node, die die Edge verbindet identisch sind und die Edge von derselben Art ist (Richtung ist egal)
     *
     * @param edge
     * @return
     */
    public final boolean isEqualTo(final Edge edge) {
        return (startElement == edge.getStart() && endElement == edge.getEnd() || endElement == edge.getStart() && startElement == edge.getEnd()) && getClass() == edge.getClass();
    }

    /**
     * @param startElement
     * @param endElement
     */
    public final void setNodes(final ModelElement startElement, final ModelElement endElement) {
        setNodes(startElement, endElement, true);
    }

    /**
     * @param startElement
     * @param endElement
     * @param registerInKnots
     */
    public void setNodes(final ModelElement startElement, final ModelElement endElement, final boolean registerInKnots) {
        this.startElement = startElement;
        this.endElement = endElement;
        if (registerInKnots) {
            if (startElement != null) {
                startElement.addEdge(this);
            }
            if (endElement != null) {
                endElement.addEdge(this);
            }
        }
        //Validität der Edge prüfen und dabei wenn nötig umdrehen (bis Version 3.2
        //ist teilweise die Reihenfolge der Start- und Endelemente von Kanten andersherum gewesen,
        //als sie in der Kantenklasse festgelegt sind. Das wird hier grade gebogen
        checkValidity();
        //		if (!checkValidity());
        //			System.err.println(getClass().getSimpleName() + " : Edge with Node1 = "  + startElement.getClearName() + " and Node2 " + endElement.getClearName() + " is not valid. ");
        //

    }

    /**
     * Setzt fuer die Edge das Start- und Endelement und fügt die Edge beim StartElement an Position startElementEdgePos und bei endElement an
     * Position endElementEdgePos ein.
     *
     * @param startElement
     *            Startelement der Edge
     * @param startElementEdgePos
     *            Postion der Edge in der Kantenliste des StartElementes. Wenn der Wert größer oder kleiner als die aktuelle Liste ist, dann wird die
     *            Edge hinten angefügt.
     * @param endElement
     *            Endelement der Edge
     * @param endElementEdgePos
     *            Postion der Edge in der Kantenliste von EndElementes. Wenn der Wert größer oder kleiner als die aktuelle Liste ist, dann wird die
     *            Edge hinten angefügt.
     */
    public void setNodesAndInsert(final ModelElement startElement, final int startElementEdgePos, final ModelElement endElement, final int endElementEdgePos) {
        this.startElement = startElement;
        this.endElement = endElement;
        //wenn HasPartEdges im Kreis modelliert wurden, wird die falsche Beziehung gleich wieder entfernt
        //und ihre alten Start- und Endelemente gesetzt, die bei einer neuen Edge immer null waren -> null hier abfangen
        if (startElement != null) {
            startElement.insertEdge(this, startElementEdgePos);
        }
        if (endElement != null) {
            endElement.insertEdge(this, endElementEdgePos);
        }
    }

    /**
     * @param start
     */
    public void setStartAndInsert(final ModelElement start) {
        startElement = start;
        if (startElement != null) {
            startElement.addEdge(this);
        }
    }

    /**
     * @param end
     */
    public void setEndAndInsert(final ModelElement end) {
        endElement = end;
        if (endElement != null) {
            endElement.addEdge(this);
        }
    }

    /**
     * @return
     */
    public final ModelElement getStart() {
        return startElement;
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
        return endElement;
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
    public boolean putXMLFieldString(final String field, final String value) {
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
        return super.putXMLFieldString(field, value);
    }

    /**
     * @param doc
     * @return
     */
    public final boolean decodeHashStrings(final GraphDocument doc) {
        ModelElement startElement = null, endElement = null;
        if (start_hash == null || end_hash == null) {
            return false;
        }
        startElement = doc.findElementCoded(start_hash);
        if (startElement == null) {
            System.out.println("Error decoding start element hash \"" + start_hash + "\"" + getClass());
        }

        endElement = doc.findElementCoded(end_hash);
        if (endElement == null) {
            System.out.println("Error decoding end element hash \"" + end_hash + "\"" + getClass());
        }

        if (startElement == null || endElement == null) {
            return false;
        }
        setNodes(startElement, endElement);
        return true;
    }

    public boolean isDirecting(final ModelElement me1, final ModelElement me2) {
        return isDirectingForward(me1, me2);
    }

    /**
     * @param me1
     * @param me2
     * @return
     */
    protected final boolean isDirectingForward(final ModelElement me1, final ModelElement me2) {
        return startElement == me1 && endElement == me2;
    }

    /**
     * @param me1
     * @param me2
     * @return
     */
    public final boolean isConnecting(final ModelElement me1, final ModelElement me2) {
        return startElement == me1 && endElement == me2 || startElement == me2 && endElement == me1;
    }

    /**
     * @return
     */
    public final String _toString() {
        return " - ";
    }

    @Override
    public final String getDebugString() {
        String startName = startElement == null ? "null" : startElement.getName();
        String endName = endElement == null ? "null" : endElement.getName();
        String startHash = start_hash != null ? start_hash : startElement == null ? "null" : startElement.getHashString();
        String endHash = end_hash != null ? end_hash : endElement == null ? "null" : endElement.getHashString();
        return startHash + " <-> " + endHash + " " + getClass().getSimpleName() + ": " + getName() + " " + startName + " <-> " + endName;
    }

    /**
     * Prüft die Validität der Kanten und stellt sie wenn möglich her. Die Prüfung betrifft die Art der Kantenelemente
     *
     * @return <code>true</code>, wenn die Edge vollständig richtig ist
     */
    public boolean checkValidity() {
        boolean startClassOk = false, endClassOk = false;
        boolean switchStart = false, switchEnd = false;
        if (startElement != null && endElement != null) {
            Class<? extends ModelElement> clazz = startElement.getClass();
            //prüfen, ob das StartElement von einer der Startklassen ist
            if (!isStartClass(clazz)) {
                //wenn nicht
                switchStart = isEndClass(clazz);
            } else {
                startClassOk = true;
            }
            clazz = endElement.getClass();
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
            if (!isStartClass(endElement.getClass())) {
                return false;
            }
            switchClasses = true;
        } else if (switchEnd) {
            if (!isEndClass(startElement.getClass())) {
                return false;
            }
            switchClasses = true;
        }
        if (switchClasses) {
            ModelElement dummy = startElement;
            startElement = endElement;
            endElement = dummy;
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
        Object field = ReflectionUtils.getField(edgeClass, ModelElement.class, fieldName);
        Class<?> startOrEndClass = (Class<?>) field;
        Class<? extends ModelElement> startOrEndElementClass = startOrEndClass.asSubclass(ModelElement.class);
        return startOrEndElementClass;
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
        return startClass.isAssignableFrom(elementClass);
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
        return endClass.isAssignableFrom(elementClass);
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
    public static boolean isRecursive(final Class<? extends Edge> edgeClass) {
        Class<? extends ModelElement> startClass = getStartClass(edgeClass);
        Class<? extends ModelElement> endClass = getEndClass(edgeClass);
        return startClass.isAssignableFrom(endClass);
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
        if (startElement == null || endElement == null) {
            return false;
        }

        startElement = coll.getMainGraphDocument().findElementCoded(startElement.getHashString());
        endElement = coll.getMainGraphDocument().findElementCoded(endElement.getHashString());

        if (startElement == null || endElement == null || coll.getMainGraphDocument().findElementCoded(getHashString()) == null) {
            return false;
        }

        boolean reconnectFirst = startElement.addEdge(this);
        boolean reconnectSecond = endElement.addEdge(this);

        return reconnectFirst || reconnectSecond;
    }

    @Override
    public final boolean join(final ModelElement other, final boolean overwriteHashstringAndExtIDs) {
        if (super.join(other, overwriteHashstringAndExtIDs)) {
            startElement = ((Edge) other).startElement;
            endElement = ((Edge) other).endElement;
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
        if (startElement != null && endElement != null) {
            return startElement.isUnique() || endElement.isUnique();
        }
        return ModelConstants.isUnique(getClass());
    }

    @Override
    public final boolean isPaintable() {
        if (startElement != null && endElement != null) {
            return startElement.isPaintable() && endElement.isPaintable();
        }
        return false;
    }

    @Override
    protected final int getMaxContainerCount() {
        return isUnique() ? 1 : Integer.MAX_VALUE;
    }

}