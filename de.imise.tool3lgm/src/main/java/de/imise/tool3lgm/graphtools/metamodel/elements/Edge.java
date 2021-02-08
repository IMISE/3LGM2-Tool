package de.imise.tool3lgm.graphtools.metamodel.elements;

import static de.imise.tool3lgm.graphtools.metamodel.EdgeCardinality.ZERO_UNLIMITED;

import de.imise.tool3lgm.graphtools.metamodel.CoreMetaModel;
import de.imise.tool3lgm.graphtools.metamodel.EdgeCardinality;
import de.imise.tool3lgm.graphtools.metamodel.ModelConstants;
import de.imise.tool3lgm.graphtools.model.GDCollection;
import de.imise.tool3lgm.graphtools.model.GraphDocument;
import de.imise.tool3lgm.graphtools.view.container.EdgeContainer;
import de.imise.tool3lgm.graphtools.view.container.ElementContainer;
import de.imise.util.ReflectionUtils;
import de.imise.util.Sys;

/**
 * @author N.N.
 */
public abstract class Edge extends ModelElement {

    /**
     * Name der <code>Class&lt;? extends ModelElement&gt;</code>, mit der jede
     * Kantenklasse seine Startklasse beschreibt. Über diesen Namen wird die
     * jeweilige Startklasse per Reflection ermittelt.
     */
    public static final String START_CLASS_FIELD_NAME = "STCL";

    /**
     * Name der <code>Class&lt;? extends ModelElement&gt;</code>, mit dem jede
     * Kantenklasse seine Endklasse beschreibt. Über diesen Namen wird die
     * jeweilige Endklasse per Reflection ermittelt.
     */
    public static final String END_CLASS_FIELD_NAME = "ETCL";

    /**
     * Name des Feldes mit der {@link EdgeCardinality}, mit der jede
     * Kantenklasse die Kardinalitäten beschreibt, mit denen Elemente der
     * Startklasse für Elemente der Endklasse vorhanden sein müssen.<br />
     * Über diesen Namen werden die Kardinalitäten per Reflection ermittelt.
     */
    public static final String START_CARDINALITY_FIELD_NAME = "SCARD";

    /**
     * Name des Feldes mit der {@link EdgeCardinality}, mit der jede
     * Kantenklasse die Kardinalitäten beschreibt, mit denen Elemente der
     * Endklasse für Elemente der Startklasse vorhanden sein müssen.<br />
     * Über diesen Namen werden die Kardinalitäten per Reflection ermittelt.
     */
    public static final String END_CARDINALITY_FIELD_NAME = "ECARD";

    /**
     * Auch für Kanten muss angegeben, welche Elementarten sie verbinden können,
     * damit die Vererbung bei der Definition der MetaPfade funktioniert, die
     * getStartElementClass() und getEndElementClass() aufruft. Unterklassen
     * können eine eigene Konstante derselben Form defnieren. Da diese über
     * Reflection geholt werden, funktioniert das mit diesen statischen Feldern
     * genauso, als würde man eine Instanzfunktion überschreiben (trotz dass sie
     * final sind).
     */
    public static final Class<? extends ModelElement> STCL = ModelElement.class;

    /**
     * Kardinalität der Endklasse zur Startklasse. Sie gibt also immer an wie
     * viele der Startelemente das EndElement braucht. Verwendung in
     * Unterklassen identisch zu STCL.
     */
    public static final EdgeCardinality SCARD = ZERO_UNLIMITED;

    /**
     * Kardinalität der Startklasse zur Endklasse. Sie gibt also immer an wie
     * viele der Startelemente das EndElement braucht. Verwendung in
     * Unterklassen identisch zu STCL.
     */
    public static final EdgeCardinality ECARD = ZERO_UNLIMITED;

    /**
     * Auch für Kanten muss angegeben, welche Elementarten sie verbinden können,
     * damit die Vererbung bei der Definition der MetaPfade funktioniert, die
     * getStartElementClass() und getEndElementClass() aufruft. Verwendung in
     * Unterklassen identisch zu STCL.
     */
    public static final Class<? extends ModelElement> ETCL = ModelElement.class;

    /**
     * The two elements wchich are connected by this edge.
     */
    protected ModelElement startElement, endElement;

    /** ID of the start */
    private String startID;

    private String endID;

    public enum Direction {
        FORWARD,
        BACKWARD;
        //ACHTUNG: toString() darf nicht überschrieben werden und muss dasselbe wie name() zurück liefern, weil das in den UNDO-REDO-Kommandos genutzt wird
        ;

        /**
         * @return
         */
        public Direction getOther() {
            return this == FORWARD ? BACKWARD : FORWARD;
        }
    }

    @Override
    public final Edge clone() {
        Edge retVal = (Edge) super.clone();
        retVal.startID = "";
        retVal.endID = "";
        return retVal;
    }

    @Override
    public final int layerFor() {
        int layer = super.layerFor();
        if (layer == ModelConstants.NO_LAYER) {
            layer = getMetaModel().getEdgeLayer(startElement.getClass(), endElement.getClass());
        }
        return layer;
    }

    /**
     * liefert true, wenn beide Node, die die Edge verbindet identisch sind und
     * die Edge von derselben Art ist (Richtung ist egal)
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
     * @param registerInNodes
     */
    public void setNodes(final ModelElement startElement, final ModelElement endElement, final boolean registerInNodes) {
        this.startElement = startElement;
        this.endElement = endElement;
        if (registerInNodes) {
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
     * Setzt fuer die Edge das Start- und Endelement und fügt die Edge beim
     * StartElement an Position startElementEdgePos und bei endElement an
     * Position endElementEdgePos ein.
     *
     * @param startElement Startelement der Edge
     * @param startElementEdgePos Postion der Edge in der Kantenliste des
     *            StartElementes. Wenn der Wert größer oder kleiner als die
     *            aktuelle Liste ist, dann wird die Edge hinten angefügt.
     * @param endElement Endelement der Edge
     * @param endElementEdgePos Postion der Edge in der Kantenliste von
     *            EndElementes. Wenn der Wert größer oder kleiner als die
     *            aktuelle Liste ist, dann wird die Edge hinten angefügt.
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
     * Wenn das übergebene Element durch diese Edge mit einem anderen Element
     * verbunden ist, kommt das andere Element der Edge zurück, sons
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

    @Override
    public boolean putXMLFieldString(final String field, final String value) {
        if (field.equals("start")) {
            startID = value;
            return true;
        }
        if (field.equals("end")) {
            endID = value;
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
    public final boolean decodeIDs(final GraphDocument doc) {
        ModelElement startElement = null, endElement = null;
        if (startID == null || endID == null) {
            return false;
        }
        startElement = doc.findElementCoded(startID);
        if (startElement == null) {
            System.out.println("Error decoding start element ID \"" + startID + "\"" + getClass());
        }

        endElement = doc.findElementCoded(endID);
        if (endElement == null) {
            System.out.println("Error decoding end element ID \"" + endID + "\"" + getClass());
        }

        if (startElement == null || endElement == null) {
            return false;
        }
        setNodes(startElement, endElement);
        return true;
    }

    /**
     * @param me1
     * @param me2
     * @return
     */
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
        String startID = this.startID != null ? this.startID : startElement == null ? "null" : startElement.getID();
        String endID = this.endID != null ? this.endID : endElement == null ? "null" : endElement.getID();
        return startID + " <-> " + endID + " " + getClass().getSimpleName() + ": " + getName() + " " + startName + " <-> " + endName;
    }

    /**
     * Prüft die Validität der Kanten und stellt sie wenn möglich her. Die
     * Prüfung betrifft die Art der Kantenelemente
     *
     * @return <code>true</code>, wenn die Edge vollständig richtig ist
     */
    public boolean checkValidity() {
        boolean startClassOk = false, endClassOk = false;
        boolean switchStart = false, switchEnd = false;
        if (startElement != null && endElement != null) {
            Class<? extends ModelElement> elementClass = startElement.getClass();
            Class<? extends Edge> edgeClass = getClass();
            //prüfen, ob das StartElement von einer der Startklassen ist
            if (!CoreMetaModel.isStartClass(edgeClass, elementClass)) {
                //wenn nicht
                switchStart = isEndClass(elementClass);
            } else {
                startClassOk = true;
            }
            elementClass = endElement.getClass();
            //prüfen, ob das EndElement von einer der Endklassen ist
            if (!isEndClass(elementClass)) {
                //wenn nicht
                switchEnd = isStartClass(elementClass);
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
        //this here may and should not occur at all, nevertheless there were exceptions
        //here once, which arise however probably only, because before already some
        //exception flew -> simply output the error
        if (field == null) {
            Sys.err1(edgeClass.getSimpleName() + ": " + (start ? "(start class)" : "(end class)") + "is null");
        }
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
        Class<? extends Edge> edgeClass = getClass();
        return getEndClass(edgeClass);
    }

    /**
     * Liefert <code>true</code>, wenn die übergebene Klasse die Startklasse der
     * Edge oder eine Ober- oder Unterklasse davon ist.
     *
     * @param elementClass
     * @return
     */
    public final boolean isStartClass(final Class<? extends ModelElement> elementClass) {
        Class<? extends Edge> edgeClass = getClass();
        return CoreMetaModel.isStartClass(edgeClass, elementClass);
    }

    /**
     * Liefert <code>true</code>, wenn die übergebene Klasse die Endklasse der
     * Edge oder eine Ober- oder Unterklasse davon ist.
     *
     * @param elementClass
     * @return
     */
    public final boolean isEndClass(final Class<? extends ModelElement> elementClass) {
        Class<? extends Edge> edgeClass = getClass();
        return CoreMetaModel.isEndClass(edgeClass, elementClass);
    }

    /**
     * @return
     */
    public final int getMinForwardCardinality() {
        Class<? extends Edge> edgeClass = getClass();
        return CoreMetaModel.getMinForwardCardinality(edgeClass);
    }

    /**
     * @return
     */
    public final int getMaxForwardCardinality() {
        Class<? extends Edge> edgeClass = getClass();
        return CoreMetaModel.getMaxForwardCardinality(edgeClass);
    }

    /**
     * @return
     */
    public final int getMinBackwardCardinality() {
        Class<? extends Edge> edgeClass = getClass();
        return CoreMetaModel.getMinBackwardCardinality(edgeClass);
    }

    /**
     * @return
     */
    public final int getMaxBackwardCardinality() {
        Class<? extends Edge> edgeClass = getClass();
        return CoreMetaModel.getMaxBackwardCardinality(edgeClass);
    }

    /**
     * @return
     */
    public final String getStartID() {
        return startID;
    }

    /**
     * @return
     */
    public final String getEndID() {
        return endID;
    }

    /**
     * @param coll
     * @return
     */
    public final boolean reconnect(final GDCollection coll) {
        if (startElement == null || endElement == null) {
            return false;
        }

        startElement = coll.getMainDoc().findElementCoded(startElement.getID());
        endElement = coll.getMainDoc().findElementCoded(endElement.getID());

        if (startElement == null || endElement == null || coll.getMainDoc().findElementCoded(getID()) == null) {
            return false;
        }

        boolean reconnectFirst = startElement.addEdge(this);
        boolean reconnectSecond = endElement.addEdge(this);

        return reconnectFirst || reconnectSecond;
    }

    @Override
    public final ModelElement join(final ModelElement other, final boolean overwriteIDsAndExtIDs, final boolean joinNameDescriptionAndUserfields) {
        ModelElement joinedEdge = super.join(other, overwriteIDsAndExtIDs, joinNameDescriptionAndUserfields);
        if (joinedEdge != null) {
            startElement = ((Edge) other).startElement;
            endElement = ((Edge) other).endElement;
        }
        return joinedEdge;
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
        return super.isUnique();
    }

    @Override
    public final boolean isPaintable() {
        if (startElement != null && endElement != null) {
            return startElement.isPaintable() && endElement.isPaintable();
        }
        return false;
    }

}