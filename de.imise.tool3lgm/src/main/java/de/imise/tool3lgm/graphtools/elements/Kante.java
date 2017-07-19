package de.imise.tool3lgm.graphtools.elements;

import java.lang.reflect.Field;

import de.imise.tool3lgm.Tool3lgmConstants;
import de.imise.tool3lgm.graphtools.GDCollection;
import de.imise.tool3lgm.graphtools.GraphDocument;
import de.imise.tool3lgm.graphtools.view.container.EdgeContainer;
import de.imise.tool3lgm.graphtools.view.container.ElementContainer;
import de.imise.tool3lgm.log.Log;

/**
 * @author N.N.
 */
public abstract class Kante extends ModelElement {

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

    /**
     *
     */
    public Kante() {
    }

    /**
     * @param knot1
     * @param knot2
     */
    public Kante(final ModelElement knot1, final ModelElement knot2) {
        this(knot1, knot2, true);
    }

    /**
     * @param knot1
     * @param knot2
     * @param registerInKnots
     */
    public Kante(final ModelElement knot1, final ModelElement knot2, final boolean registerInKnots) {
        super();
        setKnots(knot1, knot2, registerInKnots);
    }

    @Override
    public Object clone() {
        Kante retVal;
        try {
            retVal = (Kante) super.clone();
        } catch (Exception e) {
            Log.show(Log.ERROR, Tool3lgmConstants.getErrString("FehlerAllgemein"), e);
            return null;
        }
        retVal.k1 = k1;
        retVal.k2 = k2;

        retVal.start_hash = "";
        retVal.end_hash = "";

        return retVal;
    }

    /**
     * liefert true, wenn beide Knoten, die die Kante verbindet identisch sind und die Kante von derselben Art ist (Richtung ist egal)
     *
     * @param kante
     * @return
     */
    public boolean isEqualTo(final Kante kante) {
        return (k1 == kante.getStart() && k2 == kante.getEnd() || k2 == kante.getStart() && k1 == kante.getEnd()) && getClass() == kante.getClass();
        //		if ((k1 == kante.getStart() && k2 == kante.getEnd()) || (k2 == kante.getStart() && k1 == kante.getEnd()))
        //			return true;
        //		return false;
    }

    /**
     * @param _k1
     * @param _k2
     */
    public void setKnots(final ModelElement _k1, final ModelElement _k2) {
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
        //Validität der Kante prüfen und dabei wenn nötig umdrehen (bis Version 3.2
        //ist teilweise die Reihenfolge der Start- und Endelemente von Kanten andersherum gewesen,
        //als sie in der Kantenklasse festgelegt sind. Das wird hier grade gebogen
        checkValidity();
        //		if (!checkValidity());
        //			System.err.println(getClass().getSimpleName() + " : Edge with Node1 = "  + k1.getClearName() + " and Node2 " + k2.getClearName() + " is not valid. ");
        //

    }

    /**
     * Setzt fuer die Kante die Knoten Anfang=_k1 und Ende=_k2 und fügt die Kante bei _k1 an Position _k1EdgePos und bei _k2 an Position _k2EdgePos
     * ein.
     *
     * @param _k1 Knoten
     * @param pos int Position
     * @param _k2 Knoten
     */
    /**
     * @param _k1 Startelement der Kante
     * @param _k1EdgePos Postion der Kante in der Kantenliste von _k1. Wenn der Wert größer oder kleiner als die aktuelle Liste ist, dann wird die
     *            Kante hinten angefügt.
     * @param _k2 Endelement der Kante
     * @param _k2EdgePos Postion der Kante in der Kantenliste von _k2. Wenn der Wert größer oder kleiner als die aktuelle Liste ist, dann wird die
     *            Kante hinten angefügt.
     */
    public void setKnotsAndInsert(final ModelElement _k1, final int _k1EdgePos, final ModelElement _k2, final int _k2EdgePos) {
        k1 = _k1;
        k2 = _k2;
        //wenn PartOfBeziheungen im Kreis modelliert wurden, wird die falsche Beziehung glpeich wieder entfernt
        //und ihre alten Start- und Endelemente gesetzt, die bei einer neuen Kante immer null waren -> null hier abfangen
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
    public boolean isStart(final ModelElement me) {
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
    public boolean isEnd(final ModelElement me) {
        return getEnd() == me;
    }

    /**
     * Wenn das übergebene Element durch diese Kante mit einem anderen Element verbunden ist, kommt das andere Element der Kante zurück, sons
     * <code>null</code>.
     *
     * @param me
     * @return
     */
    public ModelElement getOther(final ModelElement me) {
        if (isStart(me)) {
            return getEnd();
        }
        if (isEnd(me)) {
            return getStart();
        }
        return null;
    }

    /**
     * Wenn die übergebene Elementklasse durch eine Kante der angegebenen Art mit anderen Elementen verbunden sein kann, dann wird die Elementklasse
     * dieser anderen Elemente zurück gegeben. Passen Kante und Elementklasse nicht zusammen, kommt <code>null</code> zurück.
     *
     * @param edgeClass Kantanklasse, von der die andere verbundene Elementklasse zurück gegeben werden soll
     * @param meClass Elementklasse der Kante, deren Gegenelementklasse zurück gegeben werden soll
     * @return die andere Elementklasse der Kante, als die übergebene Klasse oder <code>null</code>, wenn die Klasse gar nicht passt
     */
    public static Class<? extends ModelElement> getOther(final Class<? extends Kante> edgeClass, final Class<? extends ModelElement> meClass) {
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
    public boolean isDirecting(final ModelElement _k1, final ModelElement _k2) {
        return k1 == _k1 && k2 == _k2;
    }

    /**
     * @param _k1
     * @param _k2
     * @return
     */
    public boolean isConnecting(final ModelElement _k1, final ModelElement _k2) {
        return k1 == _k1 && k2 == _k2 || k1 == _k2 && k2 == _k1;
    }

    /**
     * @return
     */
    public String _toString() {
        return " - ";
    }

    @Override
    public final String toXMLString() {
        if (k1 == null || k2 == null) {
            return "";
        }
        return super.toXMLString();
    }

    @Override
    public StringBuilder getXMLEntities() {
        if (!checkValidity()) {
            System.out.println("Start- bzw. Endknoten entspricht nicht den Kardinalitaeten! hash: " + getHashString());
            System.out.println(getClass().getSimpleName() + "\t" + k1 + "\t" + k2);

        }
        return super.getXMLEntities().append("<field name=\"start\">" + k1.getHashString() + "</field>" + "<field name=\"end\">" + k2.getHashString() + "</field>");
    }

    /**
     * Prüft die Validität der Kanten und stellt sie wenn möglich her. Die Prüfung betrifft die Art der Kantenelemente
     *
     * @return <code>true</code>, wenn die Kante vollständig richtig ist
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
        if (switchClasses) {
            ModelElement dummy = k1;
            k1 = k2;
            k2 = dummy;
            return true;
        }
        //Es musste nichts vertauscht werden -> hier kommt nur true zurück, wenn die Klassen
        //der Start- und Endelemente mit den Metaklassen üerbeinstimmen.
        return startClassOk && endClassOk;
    }

    @Override
    public ElementContainer createContainer(final GraphDocument doc) {
        return new EdgeContainer(this, doc);
    }

    /**
     * @param start
     * @return
     */
    private static final Class<? extends ModelElement> getStartEndClass(Class<? extends Kante> edgeClass, final boolean start) {
        String fieldName = start ? START_CLASS_FIELD_NAME : END_CLASS_FIELD_NAME;
        try {
            while (Kante.class.isAssignableFrom(edgeClass)) {
                for (Field fld : edgeClass.getDeclaredFields()) {
                    if (fld.getName().toLowerCase().equals(fieldName)) {
                        return (Class<? extends ModelElement>) fld.get(fld);
                    }
                }
                edgeClass = edgeClass.getSuperclass().asSubclass(Kante.class);
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * @param start
     * @return
     */
    protected static final int[] getStartEndCardinality(final Class<? extends Kante> edgeClass, final boolean start) {
        String fieldName = start ? START_CARDINALITY_FIELD_NAME : END_CARDINALITY_FIELD_NAME;
        Class<?> elementClass = edgeClass;
        try {
            while (elementClass != ModelElement.class) {
                for (Field fld : elementClass.getDeclaredFields()) {
                    if (fld.getName().toLowerCase().equals(fieldName)) {
                        return (int[]) fld.get(fld);
                    }
                }
                elementClass = elementClass.getSuperclass();
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
        //		System.err.println(getClass().getSimpleName() + ": " +fieldName + " (" + elementClass.getSimpleName()+")");
        return null;
    }

    /**
     * @param edgeClass
     * @return
     */
    public static final Class<? extends ModelElement> getStartClass(final Class<? extends Kante> edgeClass) {
        return getStartEndClass(edgeClass, true);
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
    public final static Class<? extends ModelElement> getEndClass(final Class<? extends Kante> edgeClass) {
        return getStartEndClass(edgeClass, false);
    }

    /**
     * @return
     */
    public final Class<? extends ModelElement> getEndClass() {
        return getEndClass(getClass());
    }

    /**
     * Liefert <code>true</code>, wenn die übergebene Klasse die Startklasse der Kante oder eine Ober- oder Unterklasse davon ist.
     *
     * @param edgeClass
     * @param elementClass
     * @return
     */
    public static final boolean isStartClass(final Class<? extends Kante> edgeClass, final Class<? extends ModelElement> elementClass) {
        Class<? extends ModelElement> startClass = getStartClass(edgeClass);
        return startClass.isAssignableFrom(elementClass) || elementClass.isAssignableFrom(startClass);
    }

    /**
     * Liefert <code>true</code>, wenn die übergebene Klasse die Startklasse der Kante oder eine Ober- oder Unterklasse davon ist.
     *
     * @param elementClass
     * @return
     */
    public final boolean isStartClass(final Class<? extends ModelElement> elementClass) {
        return isStartClass(getClass(), elementClass);
    }

    /**
     * Liefert <code>true</code>, wenn die übergebene Klasse die Endklasse der Kante oder eine Ober- oder Unterklasse davon ist.
     *
     * @param edgeClass
     * @param elementClass
     * @return
     */
    public static final boolean isEndClass(final Class<? extends Kante> edgeClass, final Class<? extends ModelElement> elementClass) {
        Class<? extends ModelElement> endClass = getEndClass(edgeClass);
        return endClass.isAssignableFrom(elementClass) || elementClass.isAssignableFrom(endClass);
    }

    /**
     * Liefert <code>true</code>, wenn die übergebene Klasse die Endklasse der Kante oder eine Ober- oder Unterklasse davon ist.
     *
     * @param elementClass
     * @return
     */
    public final boolean isEndClass(final Class<? extends ModelElement> elementClass) {
        return isEndClass(getClass(), elementClass);
    }

    /**
     * Liefert <code>true</code>, wenn die übergebene Klasse die Start- oder Endklasse der Kante oder eine Ober- oder Unterklasse davon ist.
     *
     * @param edgeClass
     * @param elementClass
     * @return
     */
    public static final boolean isStartOrEndClass(final Class<? extends Kante> edgeClass, final Class<? extends ModelElement> elementClass) {
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
    public static final boolean isConnecting(final Class<? extends Kante> edgeClass, final Class<? extends ModelElement> elementClass1, final Class<? extends ModelElement> elementClass2) {
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
    public static final boolean isConnectingForward(final Class<? extends Kante> edgeClass, final Class<? extends ModelElement> startElementClass, final Class<? extends ModelElement> endElementClass) {
        return Kante.isStartClass(edgeClass, startElementClass) && Kante.isEndClass(edgeClass, endElementClass);
    }

    /**
     * Liefert <code>true</code>, wenn die Start- und Endelemente von derselben Klasse sein können, d.h. wenn die beiden Klassen
     * gleich sind oder eine eine Oberklasse der anderen ist.
     *
     * @param edgeClass
     * @return
     */
    public static boolean isConnectingSameElementClasses(final Class<? extends Kante> edgeClass) {
        Class<? extends ModelElement> startClass = getStartClass(edgeClass);
        Class<? extends ModelElement> endClass = getEndClass(edgeClass);
        return startClass.isAssignableFrom(endClass) || endClass.isAssignableFrom(startClass);
    }

    /**
     * Liefert die minimale Anzahl von Kanten der übergebenen Art, die ein Element der übergebenen Art zu anderen Elementen haben muss.
     *
     * @param edgeClass
     * @param elementClass
     * @return
     */
    public static final int getMinCardinality(final Class<? extends ModelElement> elementClass, final Class<? extends Kante> edgeClass) {
        if (Kante.isStartClass(edgeClass, elementClass)) {
            return getMinStartToEndCardinality(edgeClass);
        }
        if (isEndClass(edgeClass, elementClass)) {
            return getMinEndToStartCardinality(edgeClass);
        }
        return ModelConstants.STANDARD_ERROR_INT_VALUE;
    }

    /**
     * Liefert die maximale Anzahl von Kanten der übergebenen Art, die ein Element der übergebenen Art zu anderen Elementen haben kann.
     *
     * @param edgeClass
     * @param elementClass
     * @return
     */
    public static final int getMaxCardinality(final Class<? extends ModelElement> elementClass, final Class<? extends Kante> edgeClass) {
        if (Kante.isStartClass(edgeClass, elementClass)) {
            return getMaxStartToEndCardinality(edgeClass);
        }
        if (isEndClass(edgeClass, elementClass)) {
            return getMaxEndToStartCardinality(edgeClass);
        }
        return ModelConstants.STANDARD_ERROR_INT_VALUE;
    }

    /**
     * @param edgeClass
     * @return
     */
    public static final int getMinStartToEndCardinality(final Class<? extends Kante> edgeClass) {
        return getStartEndCardinality(edgeClass, false)[0];
    }

    /**
     * @return
     */
    public final int getMinStartToEndCardinality() {
        return getMinStartToEndCardinality(getClass());
    }

    /**
     * @param edgeClass
     * @return
     */
    public static final int getMaxStartToEndCardinality(final Class<? extends Kante> edgeClass) {
        return getStartEndCardinality(edgeClass, false)[1];
    }

    /**
     * @return
     */
    public final int getMaxStartToEndCardinality() {
        return getMaxStartToEndCardinality(getClass());
    }

    /**
     * @param edgeClass
     * @return
     */
    public static final int getMinEndToStartCardinality(final Class<? extends Kante> edgeClass) {
        return getStartEndCardinality(edgeClass, true)[0];
    }

    /**
     * @return
     */
    public final int getMinEndToStartCardinality() {
        return getMinEndToStartCardinality(getClass());
    }

    /**
     * @param edgeClass
     * @return
     */
    public static final int getMaxEndToStartCardinality(final Class<? extends Kante> edgeClass) {
        return getStartEndCardinality(edgeClass, true)[1];
    }

    /**
     * @return
     */
    public final int getMaxEndToStartCardinality() {
        return getMaxEndToStartCardinality(getClass());
    }

    /**
     * @return
     */
    public String getStartHash() {
        return start_hash;
    }

    /**
     * @return
     */
    public String getEndHash() {
        return end_hash;
    }

    @Override
    public final boolean hasSortedKanten() {
        return false;
    }

    /**
     * @param coll
     * @return
     */
    public boolean reconnect(final GDCollection coll) {
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
    public boolean avoidDuplicates() {
        if (k1 == null || k2 == null) {
            return false;
        }
        return k1.avoidDuplicates() || k2.avoidDuplicates();
    }

    @Override
    public boolean join(final ModelElement other, final boolean overwriteHashstringAndExtIDs) {
        if (super.join(other, overwriteHashstringAndExtIDs)) {
            k1 = ((Kante) other).k1;
            k2 = ((Kante) other).k2;
            return true;
        }
        return false;
    }

    @Override
    public boolean isUnique() {
        if (k1 != null && k2 != null) {
            return k1.isUnique() || k2.isUnique();
        }
        return false;
    }

    @Override
    public final boolean isUnpaintable() {
        if (k1 != null && k2 != null) {
            return k1.isUnpaintable() && k2.isUnpaintable();
        }
        return false;
    }

}