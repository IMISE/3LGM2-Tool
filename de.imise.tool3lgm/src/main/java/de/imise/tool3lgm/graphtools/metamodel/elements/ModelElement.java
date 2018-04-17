package de.imise.tool3lgm.graphtools.metamodel.elements;

import static de.imise.tool3lgm.Tool3lgmConstants.getResString;
import static de.imise.tool3lgm.graphtools.metamodel.elements.Edge.ANY;
import static de.imise.tool3lgm.graphtools.metamodel.elements.Edge.BACKWARD;
import static de.imise.tool3lgm.graphtools.metamodel.elements.Edge.DOUBLE;
import static de.imise.tool3lgm.graphtools.metamodel.elements.Edge.FORWARD;
import static de.imise.tool3lgm.graphtools.metamodel.elements.Edge.getMaxBackwardCardinality;
import static de.imise.tool3lgm.graphtools.metamodel.elements.Edge.getMaxForwardCardinality;
import static de.imise.tool3lgm.graphtools.metamodel.elements.Edge.getMinBackwardCardinality;
import static de.imise.tool3lgm.graphtools.metamodel.elements.Edge.getMinForwardCardinality;
import static de.imise.tool3lgm.graphtools.metamodel.elements.Edge.getOther;
import static de.imise.tool3lgm.graphtools.metamodel.elements.Edge.isConnectingForward;
import static de.imise.tool3lgm.graphtools.metamodel.elements.Edge.isEndClass;
import static de.imise.tool3lgm.graphtools.metamodel.elements.Edge.isStartClass;
import static de.imise.tool3lgm.graphtools.userfield.UserField.EMPTY_STRING;

import java.awt.Container;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections4.map.Flat3Map;

import com.google.common.collect.ImmutableList;

import de.imise.tool3lgm.graphtools.dialog.ElementPropertyDialog;
import de.imise.tool3lgm.graphtools.metamodel.ModelConstants;
import de.imise.tool3lgm.graphtools.model.GDCollection;
import de.imise.tool3lgm.graphtools.model.GDCommands;
import de.imise.tool3lgm.graphtools.model.GraphDocument;
import de.imise.tool3lgm.graphtools.path.MetaPath;
import de.imise.tool3lgm.graphtools.path.PathFinder;
import de.imise.tool3lgm.graphtools.userfield.UserField;
import de.imise.tool3lgm.graphtools.userfield.UserFieldTarget;
import de.imise.tool3lgm.graphtools.view.container.ElementContainer;
import de.imise.tool3lgm.graphtools.view.container.LayerContainer;
import de.imise.tool3lgm.graphtools.view.container.NodeContainer;
import de.imise.util.Alphabetical;
import de.imise.util.HashStringGenerator;
import de.imise.util.htmlxml.HTMLConverter;

public abstract class ModelElement extends UserFieldTarget {

    /**
     * Die Ebene auf der sich dieses Element befindet
     */
    protected int layer = -1;

    /**
     * Name, Anzeigename in der Grafik und Beschreibung
     */
    private String name = "", htmlName = "", descr = "";

    /**
     * HashString
     */
    protected String hashstring;

    /**
     * Name des Elementes mit allen Namen der Teilmodelle in eckigen Klammern dahinter.
     */
    private String nameWithSzens = null;

    /**
     * Table, der von einem <code>GraphDocument</code> auf den Container des Elemtentes in diesem <code>GraphDocument</code> mappt, wenn es darin
     * vorkommt.
     */
    private Map<GraphDocument, ElementContainer> containerTable;

    /**
     * Liste aller Assoziationen zu anderen Elementen. Solange keine Elemente in dieser Liste sind,
     * wird sie <code>null</code> gehalten, so dass bei Elementen, die sowieso niemals Kanten
     * haben (Kanten selbst und Knickpunkte), auch keine unnöige Liste angelegt wird.
     */
    private List<Edge> edges = null;

    /**
     * Ein StringBuilder, der gebraucht wird, um die Namen der Elemente zusammen zu bauen. Er ist statisch, damit man ihn nicht ständig neu anlegen
     * muss.
     */
    private static final StringBuilder toStringBuffer = new StringBuilder(40);

    private static final StringBuilder nameBuffer = new StringBuilder(40);

    private String toStringName = null;

    private static final StringBuilder suffixBuf = new StringBuilder("");

    private static final StringBuilder textBuf = new StringBuilder("");

    /**
     * HashString des Teilmodells, mit dem das Element verknüpft ist. Diese Verknüpfung sagt einfach nur aus, dass das Element in dem Teilmodell näher
     * berschrieben wird (z.B. duch seine Teile). Es kann, aber muss selbst nicht in diesem Teilmodell
     * vorkommen.
     */
    private String associatedSzenHashString = null;

    /**
     * Erzeut ein neues Modellelement mit einem HashString.
     */
    public ModelElement() {
        hashstring = getNewHashString(this);
        initContainerTable(this);
    }

    private static void initContainerTable(final ModelElement me) {
        //bei allen Elementen, die sowieso nie mehr als 3 Container haben können (uniques und Knickpunkte) wird
        //eine optimierte Map für die Container initialisiert
        me.containerTable = me.getMaxContainerCount() > 3 ? new HashMap<>(3, 1) : new Flat3Map<>();
    }

    public void printContainer() {
        System.err.println(containerTable.size() + " " + containerTable.getClass().getSimpleName() + " " + this.getClass().getSimpleName());
    }

    @Override
    public ModelElement clone() {
        ModelElement retVal = (ModelElement) super.clone();
        retVal.hashstring = getNewHashString(this);
        initContainerTable(retVal);
        retVal.edges = null;
        return retVal;
    }

    /**
     * Liefert den Index der Ebene, auf dem das Element liegt. Diese Funktion wird von den konkreten Elementen überschrieben. Das braucht man neben
     * den der Möglichkeit das für eine Klasse über die {@link ModelConstants} zu sagen, weil es nicht bei allen
     * Klassen der Layer feststeht (Knickpunkte)
     *
     * @return
     */
    public int layerFor() {
        int layer = ModelConstants.layerFor(getClass());
        if (layer == ModelConstants.NO_LAYER) {
            for (ElementContainer ec : containerTable.values()) {
                Container parent = ec.getParent();
                if (parent instanceof LayerContainer) {
                    layer = ((LayerContainer) parent).getLayerNumber();
                    break;
                }
            }
        }
        return layer;
    }

    /**
     * @param me
     * @return
     */
    private static final String getNewHashString(final ModelElement me) {
        return HashStringGenerator.getHash(ModelConstants.getShortName(me.getClass()));
    }

    /**
     * @param gd
     * @param ec
     */
    public final void setContainer(final GraphDocument gd, final ElementContainer ec) {
        if (gd != null) {
            containerTable.put(gd, ec);
        }
        nameWithSzens = null;
    }

    /**
     * @param gd
     */
    public final void removeContainer(final GraphDocument gd) {
        containerTable.remove(gd);
        nameWithSzens = null;
    }

    /**
     * @return
     */
    public final Set<GraphDocument> getMySzenarios() {
        return new HashSet<>(containerTable.keySet());
    }

    protected abstract int getMaxContainerCount();

    /**
     *
     */
    public final void removeAllContainer() {
        containerTable.clear();
        nameWithSzens = null;
    }

    /**
     * @param gd
     * @return
     */
    public final ElementContainer getContainer(final GraphDocument gd) {
        if (gd == null) {
            return null;
        }
        return containerTable.get(gd);
    }

    /**
     * @return
     */
    public final int getContainerCount() {
        return containerTable.size();
    }

    /**
     * @param _hashstring
     */
    public final void setHashString(final String _hashstring) {
        if (_hashstring == null) {
            return;
        }
        if (_hashstring.equals("") || _hashstring.equals("null")) {
            return;
        }
        hashstring = _hashstring;
    }

    /**
     * @return
     */
    public final String getHashString() {
        return hashstring;
    }

    /** Gibt den Namen des Objektes zurueck */
    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        if (toStringName == null) {
            toStringBuffer.setLength(0);
            toStringBuffer.append(name);
            for (int i = toStringBuffer.length(); i > 1;) {
                if (name.charAt(--i) == '-' && name.charAt(--i) == '\\') {
                    toStringBuffer.delete(i, i + 2);
                }
            }
            toStringName = toStringBuffer.toString();
        }
        return toStringName;
    }

    /**
     * @return
     */
    public final String getClearName() {
        nameBuffer.setLength(0);
        nameBuffer.append(toString());
        if (nameBuffer.length() == 0) {
            return "";
        }
        while (nameBuffer.charAt(0) == '\n' || nameBuffer.charAt(0) == ' ' || nameBuffer.charAt(0) == '\t') {
            nameBuffer.deleteCharAt(0);
            //Diese zusätzliche Abfrage verhindert die Exception, die flog, wenn man den Eigenschaftendialog öffnete und die Bedingungen aus Bug-Meldung vom <08.11.06 LI> galt.
            if (nameBuffer.length() == 0) {
                return "";
            }
        }
        while (true) {
            int pos = nameBuffer.length() - 1;
            if (nameBuffer.charAt(pos) == '\n' || nameBuffer.charAt(pos) == ' ' || nameBuffer.charAt(pos) == '\t') {
                nameBuffer.deleteCharAt(pos);
            } else {
                break;
            }
        }
        for (int i = 1; i < nameBuffer.length(); i++) {
            if (nameBuffer.charAt(i) == '\n') {
                if (nameBuffer.charAt(i - 1) == ' ') {
                    nameBuffer.deleteCharAt(i--);
                } else {
                    nameBuffer.setCharAt(i, ' ');
                }
            }
        }
        return nameBuffer.toString();
    }

    /**
     * @param doc
     * @return
     */
    private final void updateNameWithSzens() {
        if (name == null) {
            return;
        }
        if (isUnique()) {
            nameWithSzens = toString();
            return;
        }

        nameBuffer.setLength(0);
        nameBuffer.append(toString());

        Set<GraphDocument> mySzenarios = getMySzenarios();
        //Beim Laden eines Modells liefert getCollection() null -> abfangen
        GDCollection gdcoll = getCollection();
        if (gdcoll == null) {
            return;
        }
        mySzenarios.remove(gdcoll.getMainGraphDocument());
        List<GraphDocument> mySortedSzenarios = new ArrayList<>(mySzenarios);
        Alphabetical.sort(mySortedSzenarios);
        nameBuffer.append("      ");
        nameBuffer.append(mySortedSzenarios);
        nameWithSzens = nameBuffer.toString();
        return;
    }

    /**
     *
     */
    public final void invalidateNameWithSzens() {
        nameWithSzens = null;
    }

    /**
     * @return
     */
    public String getNameWithSzens() {
        if (nameWithSzens == null || ModelConstants.isGenerateName(getClass())) {
            updateNameWithSzens();
        }
        return nameWithSzens;
    }

    /** Gibt den Namen des Objektes im HTML-Formatzurueck */
    public String getHTMLName() {
        return htmlName;
    }

    /**
     * Setzt den Namen des Objektes und sortiert die Liste der {@link NodeContainer} im LayerContainer
     *
     * @param name
     */
    public void setName(final String name) {
        setName(name, true);
    }

    /**
     * Name der Funtkion, die den MetaPfad zu verbundenen Elementen angibt, von denen der Name
     * zusätzlich zum eigenen Elementnamen in der Grafik angezeigt werden soll.
     */
    public static final String GET_NAME_EXTENSION_METHOD_NAME = "getNameExtension";

    /**
     * Liefert für alle Elementklassen, bei denen der Name verbundendener Elemente in der Grafik in Klammern
     * unter der eigentlichen Elementart angezeigt werden soll, den MetaPfad zu den anzuzeigenden verbundenen
     * Elementen.
     * Diese Funktion darf nicht einfach refactored werden und wenn doch, dann muss das Feld GET_NAME_EXTENSION_METHOD_NAME
     * ebenfalls umbenannt werden.
     */
    protected MetaPath getNameExtensionPath() {
        return null;
    }

    public void updateNameExtensions() {
        if (getNameExtensionPath() != null) {
            setName(name);
        }
    }

    /**
     * Setzt den Namen des Objektes und sortiert die Liste der {@link NodeContainer} im LayerContainer, wenn sort==true
     *
     * @param name
     * @param sort
     */
    public void setName(final String name, final boolean sort) {
        toStringName = null;
        if (name == null) {
            return;
        }
        this.name = name.equalsIgnoreCase("null") ? "" : name;
        updateNameWithSzens();

        //Node der Layer neu sortieren
        if (sort && this instanceof Node) {
            for (ElementContainer ec : containerTable.values()) {
                NodeContainer kc = (NodeContainer) ec;
                LayerContainer lc = kc.getMyLayerContainer();
                if (lc != null) {
                    lc.resetPositionOf(kc);
                }
            }
        }
        if (isUnpaintable()) {
            return;
        }
        updateHTMLName();
        for (ElementContainer ec : containerTable.values()) {
            ec.refreshText();
        }
    }

    public String getNameExtension() {
        updateHTMLNameSuffixBuffer();
        return suffixBuf.toString();
    }

    private void updateHTMLNameSuffixBuffer() {
        suffixBuf.setLength(0);
        MetaPath nameExtension = getNameExtensionPath();
        if (nameExtension != null) {
            Collection<ModelElement> directConnectedElements = PathFinder.getDirectConnectedElements(this, nameExtension);
            //Kein Element, dessen Namen in Klammern angezeigt werden soll verbunden -> weiter
            if (!directConnectedElements.isEmpty()) {
                //genau ein Element verbunden, das denselben Namen hat wie dieses Element -> weiter (damit in der Grafik nicht
                //2 mal dasselbe steht)
                ModelElement firstConnected = directConnectedElements.iterator().next();
                if (directConnectedElements.size() == 1) {
                    if (!firstConnected.getClearName().equals(getClearName())) {
                        suffixBuf.append("(").append(firstConnected.getName()).append(")");
                    }
                } else {
                    suffixBuf.append("(");
                    //in allen anderen Fällen kommen die verbundenen Elemente in Klammmern hinter den Elementnamen
                    for (ModelElement swp : directConnectedElements) {
                        suffixBuf.append(swp.getName());
                        suffixBuf.append(", ");
                    }
                    //das letzte Komma wieder löschen
                    suffixBuf.setLength(suffixBuf.length() - 2);
                    suffixBuf.append(")");
                }
            }
        }
    }

    private void updateHTMLName() {
        updateHTMLNameSuffixBuffer();
        textBuf.setLength(0);
        textBuf.append("<HTML><CENTER>");
        if (isHyperlink()) {
            textBuf.append("<U>");
        }
        HTMLConverter.appendDecimalEncodedHTMLString(textBuf, name);
        if (isHyperlink()) {
            textBuf.append("</U>");
        }
        textBuf.append(suffixBuf.length() > 0 ? "<BR>" : "");
        if (suffixBuf.length() > 0) {
            HTMLConverter.appendDecimalEncodedHTMLString(textBuf, suffixBuf.toString());
        }
        textBuf.append("</CENTER></HTML>");
        htmlName = textBuf.toString();
    }

    /**
     *
     */
    public void refreshText() {
        for (ElementContainer ec : containerTable.values()) {
            ec.refreshText();
        }
    }

    /** Gibt die Beschreibung des Objektes zurueck */
    public final String getDescription() {
        return descr;
    }

    /** Setzt die Beschreibung des Objektes */
    public final void setDescription(final String descr) {
        if (descr != null) {
            if (descr.equalsIgnoreCase("null")) {
                this.descr = "";
            } else {
                this.descr = descr;
            }
        }
        hyperlink = null;
        if (this.descr != null) {
            String descrLow = descr.toLowerCase();
            int i1 = descrLow.indexOf("hyperlink:");
            if (i1 == -1) {
            } else {
                i1 += 10;
                int i2 = this.descr.indexOf('\n', i1);
                if (i2 == -1) {
                    i2 = this.descr.length();
                }
                if (i2 > i1) {
                    hyperlink = this.descr.substring(i1, i2).trim();
                }
            }
        }
    }

    /**
     * setzt die UserField(Felder) zu einem Objekt
     *
     * @param field Bezeichnung des Feldes
     * @param value Wert des Feldes (als unkodierte String)
     * @return true, wenn das Feld existiert und der Wert gesetzt werden konnte
     * @author Thomas Rudert
     */
    public boolean putXMLFieldString(final String field, final String value) {
        if (field.equals("name")) {
            setName(value);
            return true;
        }
        if (field.equals("description") || field.equals("requirement") || field.equals("note")) {
            if (!"".equals(value)) {
                String descrip = getDescription();
                setDescription(!descrip.equals("") ? descrip + "\n" + value : value);
            }
            return true;

        }
        if (field.equals("assoc_szen")) {
            setAssociatedDoc(value);
            return true;
        }

        return false;
    }

    /**
     * set a property / field of the ModelElement possible fieldNames: name, description
     *
     * @author Thomas Rudert
     * @param fieldName String with the name of the field
     * @param value String with the value for this field
     * @return boolean with true, if property / field was set successful
     */
    public boolean setField(final String fieldName, final String value) {
        if (fieldName.equals("name")) {
            if (value == null) {
                setName("");
            } else {
                setName(value);
            }

            return true;
        } else if (fieldName.equals("description")) {
            if (value == null) {
                setDescription("");
            } else {
                setDescription(value);
            }

            return true;
        }

        return false;
    }

    public abstract ElementContainer createContainer(GraphDocument doc);

    //ehemals in Node.java

    /* --- Funktionen im Netzwerk --- Anfang --- */

    /** Fuegt diesem Node eine Edge zu. */
    public boolean addEdge(final Edge edge) {
        int pos = edges == null ? 0 : edges.size() - 1;
        return insertEdge(edge, pos);
    }

    /**
     * Fügt diesem Node in der List connections an der Position pos die Edge kante hinzu.
     */
    public boolean insertEdge(final Edge kante, int pos) {
        if (kante == null || edges != null && edges.contains(kante)) {
            return false;
        }
        if (edges == null) {
            edges = new ArrayList<>(3); // die meisten Elemente, die überhaupt Kanten haben, haben fast nie mehr als 3
        }
        if (pos < edges.size() || pos > edges.size()) {
            pos = edges.size();
        }
        edges.add(pos, kante);
        return true;
    }

    public boolean swapEdges(final int pos1, final int pos2) {
        if (!isValidEdgeIndex(pos1) || !isValidEdgeIndex(pos2) || pos1 == pos2) {
            return false;
        }
        Edge egde = edges.get(pos1);
        edges.set(pos1, edges.get(pos2));
        edges.set(pos2, egde);
        return true;
    }

    /**
     * Entfernt die angegebene Edge vom Node.
     *
     * @return index, an dem sich die entfernte Kante befand
     */
    public final int removeEdge(final Edge edge) {
        if (edges != null) {
            int edgeIndex = edges.indexOf(edge);
            if (edgeIndex >= 0) {
                edges.remove(edge);
                if (edges.size() == 0) {
                    edges = null;
                }
            }
        }
        return -1;
    }

    /* --- Funktionen im Netzwerk --- Ende --- */

    /** Ermittelt, ob der Node an eine Edge gebunden ist oder nicht. */
    public final boolean hasEdges() {
        return getEdgesCount() > 0;
    }

    /** Gibt die Zahl der Kanten zurueck, an die der Node gebunden ist. */
    public final int getEdgesCount() {
        return edges == null ? 0 : edges.size();
    }

    private static final Iterable<Edge> emtpyEdgeIterable = ImmutableList.of();

    /** Gibt den Vektor der Verbindungen zurueck */
    public final Iterable<Edge> getEdges() {
        return edges == null ? emtpyEdgeIterable : edges;
    }

    /** Gibt die Verbindung Nummer <i>index </i> zurueck */
    public final Edge getEdge(final int index) {
        return isValidEdgeIndex(index) ? edges.get(index) : null;
    }

    public int getEdgeIndex(final Edge edge) {
        return edges == null ? -1 : edges.indexOf(edge);
    }

    public boolean isValidEdgeIndex(final int index) {
        return index >= 0 && edges != null && index < edges.size();
    }

    //###############################################################################

    /**
     * @param index
     * @return Node ueber die Verbindung Nummer <i>index </i> zurueck
     */
    public final ModelElement getConnectedElement(final int index) {
        Edge k = getEdge(index);
        if (k == null) {
            return null;
        }
        ModelElement start = k.getStart();
        if (this == start) {
            return k.getEnd();
        }
        return k.getStart();
    }

    /**
     * @param edge
     * @return <code>true</code>, wenn Edge <i>k</i> an diesem Node ansetzt, sonst <code>false</code>
     */
    public final boolean hasConnection(final Edge edge) {
        return getEdgeIndex(edge) >= 0;
    }

    /**
     * @param modelElement
     * @return <code>true</code>, wenn eine Edge zwischen diesem und dem übergebenen Element besteht
     */
    public final boolean isConnectedWith(final ModelElement modelElement) {
        for (Edge edge : getEdges()) {
            if (edge.isConnecting(this, modelElement)) {
                return true;
            }
        }
        return false;
    }

    /**
     * @param modelElement
     * @return <code>true</code>, wenn eine Edge von diesem zu dem übergebenen Element besteht
     */
    public final boolean isConnectedTo(final ModelElement modelElement) {
        for (Edge edge : getEdges()) {
            if (edge.isDirecting(this, modelElement)) {
                return true;
            }
        }
        return false;
    }

    /**
     * @param modelElement
     * @return <code>true</code>, wenn eine Edge vom übergebenen zu diesem Element besteht
     */
    public final boolean isConnectedFrom(final ModelElement modelElement) {
        for (Edge edge : getEdges()) {
            if (edge.isDirecting(modelElement, this)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Prüft, ob zwischen diesem und dem übergebenen Element eine Edge der angegebenen Art existiert. Die Richtung ist dabei egal.
     *
     * @param modelElement Element zu dem die Existenz einer Verbindung geprüft werden soll
     * @param edgeClass Art der Edge, die gesucht werden soll
     * @return <code>true</code>, wenn eine Edge zwischen diesem und dem übergebenen Element besteht
     */
    public final boolean isConnectedWith(final ModelElement modelElement, final Class<? extends Edge> edgeClass) {
        for (Edge edge : getEdges()) {
            if (edgeClass.isAssignableFrom(edge.getClass()) && edge.isConnecting(this, modelElement)) {
                return true;
            }
        }
        return false;
    }

    public final boolean isConnectedTo(final ModelElement k, final Class<? extends Edge> edgeClass) {
        for (Edge edge : getEdges()) {
            if (edgeClass.isAssignableFrom(edge.getClass()) && edge.isDirecting(this, k)) {
                return true;
            }
        }
        return false;
    }

    public final boolean isConnectedFrom(final ModelElement k, final Class<? extends Edge> edgeClass) {
        for (Edge edge : getEdges()) {
            if (edgeClass.isAssignableFrom(edge.getClass()) && edge.isDirecting(k, this)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Gibt die erste Edge vom Typ <code>edgeClass</code> zurück, die von diesem <code>ModelElement</code> zum <code>ModelElement k</code> geht.
     * Führt <code>getConnectionTo(ModelElement, Class, int)</code> für alle Positionen aus.
     *
     * @param modelElement
     * @param edgeClasses
     * @return
     */
    public final Edge getEdgeTo(final ModelElement modelElement, final Class<? extends Edge> edgeClass) {
        return getEdgeTo(this, modelElement, edgeClass);
    }

    /**
     * Gibt die erste Edge vom Typ <code>edgeClass</code> zurück, die von diesem <code>ModelElement</code> zum <code>ModelElement k</code> geht.
     * Führt <code>getConnectionTo(ModelElement, Class, int)</code> für alle Positionen aus.
     *
     * @param modelElement
     * @param edgeClasses
     * @return
     */
    private static final Edge getEdgeTo(final ModelElement start, final ModelElement end, final Class<? extends Edge> edgeClass) {
        for (Edge edge : start.getEdges()) {
            if (isEdgeTo(edge, edgeClass, start, end)) {
                return edge;
            }
        }
        return null;
    }

    /**
     * @param modelElement
     * @param edgeClass
     * @param position
     * @return
     */
    public final Edge getEdgeTo(final ModelElement modelElement, final Class<? extends Edge> edgeClass, final int position) {
        if (isValidEdgeIndex(position)) {
            Edge edge = edges.get(position);
            if (isEdgeTo(edge, edgeClass, this, modelElement)) {
                return edge;
            }
        } else if (position == GDCommands.INVALID_EDGE_INDEX) {
            return getEdgeTo(this, modelElement, edgeClass);
        }
        return null;
    }

    /**
     * @param modelElement
     * @param edgeClass
     * @param position
     * @return
     */
    public final Edge getEdgeFrom(final ModelElement modelElement, final Class<? extends Edge> edgeClass, final int position) {
        if (isValidEdgeIndex(position)) {
            Edge edge = edges.get(position);
            if (isEdgeTo(edge, edgeClass, modelElement, this)) {
                return edge;
            }
        } else if (position == GDCommands.INVALID_EDGE_INDEX) {
            return getEdgeTo(modelElement, this, edgeClass);
        }
        return null;
    }

    /**
     * Prüft, ob die übergebene Kante der übergebenen die zuweisungskompatibel zur übergebenen Kantenklasse ist (wenn die Kantenklasse
     * <code>null</code> ist, dann ist sie egal bzw. ganauso als würde man ModelElement.class übergeben) und ob sie this mit dem
     * übergebenen ModelElement in Richtung FORWAD verbindet.
     *
     * @param edge
     * @param edgeClass
     * @param start
     * @param end
     * @return
     */
    private static boolean isEdgeTo(final Edge edge, final Class<? extends Edge> edgeClass, final ModelElement start, final ModelElement end) {
        return edgeHasClass(edge, edgeClass) && edge.isDirecting(start, end);
    }

    /**
     * Prüft, ob die übergebene Kante der übergebenen die zuweisungskompatibel zur übergebenen Kantenklasse ist (wenn die Kantenklasse
     * <code>null</code> ist, dann ist sie egal bzw. ganauso als würde man ModelElement.class übergeben).
     *
     * @param edge
     * @param edgeClass
     * @return
     */
    private static boolean edgeHasClass(final Edge edge, final Class<? extends Edge> edgeClass) {
        return edgeClass == null || edgeClass.isAssignableFrom(edge.getClass());
    }

    /**
     * @param modelElement
     * @return
     */
    public final List<Edge> getEdgesWith(final ModelElement modelElement) {
        return getEdgesWith(modelElement, null);
    }

    /**
     * @param modelElement
     * @param edgeClass
     * @return
     */
    public final List<Edge> getEdgesWith(final ModelElement modelElement, final Class<? extends Edge> edgeClass) {
        return getEdgesWith(modelElement, edgeClass, GDCommands.INVALID_EDGE_INDEX);
    }

    /**
     * @param modelElement
     * @param edgeClass
     * @param position
     * @return
     */
    public final List<Edge> getEdgesWith(final ModelElement modelElement, final Class<? extends Edge> edgeClass, final int position) {
        return getEdgesWith(modelElement, edgeClass, position, ANY);
    }

    /**
     * @param modelElement
     * @param edgeClass
     * @return
     */
    public final List<Edge> getEdgesTo(final ModelElement modelElement, final Class<? extends Edge> edgeClass) {
        return getEdgesWith(modelElement, edgeClass, GDCommands.INVALID_EDGE_INDEX, FORWARD);
    }

    /**
     * @param modelElement
     * @param edgeClass
     * @param position
     * @return
     */
    public final List<Edge> getEdgesTo(final ModelElement modelElement, final Class<? extends Edge> edgeClass, final int position) {
        return getEdgesWith(modelElement, edgeClass, position, FORWARD);
    }

    /**
     * @param modelElement
     * @param edgeClass
     * @param edgeIndex
     * @param direction
     * @return
     */
    private final List<Edge> getEdgesWith(final ModelElement modelElement, final Class<? extends Edge> edgeClass, final int edgeIndex, final int direction) {
        List<Edge> retVal = new ArrayList<>();
        if (modelElement == null || edges == null) {
            return retVal;
        }
        int startIndex = 0;
        int endIndex = edges.size();
        if (isValidEdgeIndex(edgeIndex)) {
            startIndex = edgeIndex;
            endIndex = edgeIndex + 1;
        }
        for (int i = startIndex; i < endIndex; i++) {
            Edge edge = getEdge(i);
            if (edgeClass != null && edgeClass != edge.getClass()) {
                continue;
            }
            boolean add = false;
            if (direction == FORWARD) {
                add = edge.isDirecting(this, modelElement);
            } else if (direction == BACKWARD) {
                add = edge.isDirecting(modelElement, this);
            } else {
                add = edge.isConnecting(this, modelElement);
            }
            if (add) {
                retVal.add(edge);
            }
        }
        return retVal;
    }

    /**
     * Sucht alle Kanten, die diesen Node mit Node des angegebenen Typs verbinden.
     *
     * @param elementClass Klasse der verbundenen Node
     * @return List mit allen gefundenen Kanten
     */
    public final List<Edge> getEdgesWith(final Class<? extends ModelElement> elementClass) {
        return getEdgesWith(elementClass, null);
    }

    /**
     * Sucht alle Kanten des angegebenen Typs, die diesen Node mit Node des angegebenen Typs verbinden. Wird als <code>edgeClass</code>
     * <code>null</code> übergeben, werden alle Kantenarten zurückgegeben.
     *
     * @param elementClass Klasse der verbundenen Node
     * @param edgeClass Kanteklasse nach der gesucht werden soll
     * @return List mit allen gefundenen Kanten
     */
    public final List<Edge> getEdgesWith(final Class<? extends ModelElement> elementClass, final Class<? extends Edge> edgeClass) {
        return getEdgesWith(elementClass, edgeClass, ANY);
    }

    /**
     * Sucht alle Kanten des angegebenen Typs, die diesen Node in Vorwärtsrichtung mit Node des angegebenen Typs verbinden. Wird als
     * <code>edgeClass</code> <code>null</code> übergeben, werden alle Kantenarten zurückgegeben.
     *
     * @param elementClass Klasse der verbundenen Node
     * @param edgeClass Kanteklasse nach der gesucht werden soll
     * @return List mit allen gefundenen Kanten
     */
    public final List<Edge> getEdgesTo(final Class<? extends ModelElement> elementClass, final Class<? extends Edge> edgeClass) {
        return getEdgesWith(elementClass, edgeClass, FORWARD);
    }

    /**
     * Sucht alle Kanten des angegebenen Typs, die diesen Node in Rückwärtsrichtung mit Node des angegebenen Typs verbinden. Wird als
     * <code>edgeClass</code> <code>null</code> übergeben, werden alle Kantenarten zurückgegeben.
     *
     * @param elementClass Klasse der verbundenen Node
     * @param edgeClass Kanteklasse nach der gesucht werden soll
     * @return List mit allen gefundenen Kanten
     */
    public final List<Edge> getEdgesFrom(final Class<? extends ModelElement> elementClass, final Class<? extends Edge> edgeClass) {
        return getEdgesWith(elementClass, edgeClass, BACKWARD);
    }

    /**
     * Sucht alle Kanten des angegebenen Typs, die diesen Node mit Node des angegebenen Typs verbinden. Wird als <code>edgeClass</code>
     * <code>null</code> übergeben, werden alle Kanten zurückgegeben.
     *
     * @param elementClass Klasse der verbundenen Node
     * @param edgeClass Kanteklasse nach der gesucht werden soll
     * @param Richtung der Edge nach der gesucht werden soll (<code>ANY</code>, <code>FORWARD</code> oder
     *            <code>BACKWARD</code>)
     * @return List mit allen gefundenen Kanten
     */
    public final List<Edge> getEdgesWith(final Class<? extends ModelElement> elementClass, final Class<? extends Edge> edgeClass, final int direction) {
        List<Edge> l_connections = new ArrayList<>();
        for (Edge o_kante : getEdges()) {
            if (edgeClass != null && !edgeClass.isAssignableFrom(o_kante.getClass())) {
                continue;
            }
            if (direction == FORWARD) {
                if (o_kante.getStart() == this && elementClass.isAssignableFrom(o_kante.getEnd().getClass())) {
                    l_connections.add(o_kante);
                }
            } else if (direction == BACKWARD) {
                if (o_kante.getEnd() == this && elementClass.isAssignableFrom(o_kante.getStart().getClass())) {
                    l_connections.add(o_kante);
                }
            } else {
                if (o_kante.getStart() == this && elementClass.isAssignableFrom(o_kante.getEnd().getClass()) || o_kante.getEnd() == this && elementClass.isAssignableFrom(o_kante.getStart().getClass())) {
                    l_connections.add(o_kante);
                }
            }
        }
        return l_connections;
    }

    /**
     * Gibt eine alphabetisch sortierte Liste aller ElementContainer der mit diesem Node verbundenen Node der Klasse
     * <code>searchElementClass</code>, die in doc enthalten, sind zurueck
     *
     * @param searchElementClass Art der verbundenen Elemente, deren Container geliefert werden sollen
     * @param doc Node aus diesem Dokument
     * @return List mit ElementContainer der gefundenen Node
     */
    public final List<ElementContainer> getConnectedContainer(final Class<? extends ModelElement> searchElementClass, final GraphDocument doc) {
        return getConnectedContainer(searchElementClass, doc, null, ANY, true);
    }

    /**
     * Gibt eine alphabetisch sortierte Liste aller ElementContainer zurück, die mit diesem Node übder die angegebene Kantenart verbundenen sind.
     *
     * @param doc Node aus diesem Dokument
     * @param searchEdgeClass Art der zu suchenden verbindungen
     * @return List mit ElementContainer der gefundenen Node
     */
    public final List<ElementContainer> getConnectedContainer(final GraphDocument doc, final Class<? extends Edge> searchEdgeClass) {
        return getConnectedContainer(ModelElement.class, doc, searchEdgeClass);
    }

    /**
     * @param searchElementClass
     * @param doc
     * @param edgeClass
     * @return
     */
    public final List<ElementContainer> getConnectedContainer(final Class<? extends ModelElement> searchElementClass, final GraphDocument doc, final Class<? extends Edge> edgeClass) {
        return getConnectedContainer(searchElementClass, doc, edgeClass, ANY, true);
    }

    /**
     * @param searchElementClass
     * @param doc
     * @param edgeClass
     * @param direction
     * @return
     */
    public final List<ElementContainer> getConnectedContainer(final Class<? extends ModelElement> searchElementClass, final GraphDocument doc, final Class<? extends Edge> edgeClass, final int direction) {
        return getConnectedContainer(searchElementClass, doc, edgeClass, direction, true);
    }

    /**
     * Gibt eine alphabetisch sotrierte Liste aller ElementContainer der mit diesem Node verbundenen Node des Klasse searchElementClass, die in
     * doc enthalten, sind zurueck
     *
     * @param searchElementClass Elementklasse deren Objekte zurück gegeben werden sollen
     * @param doc Node aus diesem Dokument
     * @param start true = Verbindungen beginnen nicht bei diesem Node
     * @param end true = Verbindungen enden nicht bei diesem Node
     * @return List mit ElementContainer der gefundenen Node
     */
    @SuppressWarnings("unchecked")
    public final List<ElementContainer> getConnectedContainer(final Class<? extends ModelElement> searchElementClass, final GraphDocument doc, final Class<? extends Edge> edgeClass, final int direction, final boolean alphabetical) {
        return (List<ElementContainer>) getConnected(searchElementClass, doc, edgeClass, direction, true, alphabetical);
    }

    ////////////////////
    // Beginn Part-Of //
    ////////////////////

    /**
     * Liefert eine Liste aller Elemente der angegebenen Art, die über irgendeine Kantenart mit den direkten und indirekten Teilelementen dieses
     * Elementes verbunden sind. Es wird nur in dem angegebenen <code>GraphDocument</code> gesucht:
     *
     * @param searchElementClass Elementart nach der gesucht werden soll
     * @param doc <code>GraphDocument</code>, in dem nach verbundenen Elementen gesucht wird
     * @return Liste mit verbundenen <code>ModelElement</code>s
     * @see #getPartConnectedContainer(Class, GraphDocument, Class, int)
     */
    public final List<ElementContainer> getPartConnectedContainer(final Class<? extends ModelElement> searchElementClass, final GraphDocument doc) {
        return getPartConnectedContainer(searchElementClass, doc, null, ANY);
    }

    /**
     * Liefert eine Liste aller Elemente die über die übergebene Kantenart mit den direkten und indirekten Teilelementen dieses Elementes verbunden
     * sind. Es wird nur in dem angegebenen <code>GraphDocument</code> gesucht:
     *
     * @param doc <code>GraphDocument</code>, in dem nach verbundenen Elementen gesucht wird
     * @param searchEdgeClass Kantenart nach der gesucht werden soll
     * @return Liste mit verbundenen <code>ModelElement</code>s
     * @see #getPartConnectedContainer(Class, GraphDocument, Class, int)
     */
    public final List<ElementContainer> getPartConnectedContainer(final GraphDocument doc, final Class<? extends Edge> searchEdgeClass) {
        return getPartConnectedContainer(ModelElement.class, doc, searchEdgeClass, ANY);
    }

    /**
     * Liefert eine Liste aller Elemente, die über die angegebene Kantenart mit den direkten und indirekten Teilelementen dieses Elementes verbunden
     * sind. Es wird nur in dem angegebenen <code>GraphDocument</code> gesucht und nur in der angegebenen
     * Richtung der Edge.
     *
     * @param searchElementClass Elementart nach der gesucht werden soll
     * @param doc <code>GraphDocument</code>, in dem nach verbundenen Elementen gesucht wird
     * @param edgeClass Art der Edge, über die Elemente mit den Teilen dieses Elementes verbunden sein sollen
     * @param direction Richtung, die die Kanten haben sollen, über die die verbundenen Elemente gesucht werden
     * @return Liste mit verbundenen <code>ModelElement</code>s
     */
    public final List<ElementContainer> getPartConnectedContainer(final Class<? extends ModelElement> searchElementClass, final GraphDocument doc, final Class<? extends Edge> edgeClass, final int direction) {
        //Rückgabeliste
        List<ElementContainer> connected = new ArrayList<>();
        //Liste aller Teile holen (direkte und indirekte)
        for (ModelElement me : getPartElements(false)) {
            //füge zur Rückgabeliste alle über die angegebene Art verbundenen Node hinzu
            connected.addAll(me.getConnectedContainer(searchElementClass, doc, edgeClass, direction));
        }
        return connected;
    }

    /**
     * Liefert eine Liste aller Elemente der angegebenen Art, die über irgendeine Kantenart mit den direkten und indirekten Oberelementen dieses
     * Elementes verbunden sind. Es wird nur in dem angegebenen <code>GraphDocument</code> gesucht.
     *
     * @param searchElementClass Elementart nach der gesucht werden soll
     * @param doc <code>GraphDocument</code>, in dem nach verbundenen Elementen gesucht wird
     * @return Liste mit verbundenen <code>ModelElement</code>s
     * @see #getParentConnectedContainer(Class, GraphDocument, Class, int)
     */
    public final List<ElementContainer> getParentConnectedContainer(final Class<? extends ModelElement> searchElementClass, final GraphDocument doc) {
        return getParentConnectedContainer(searchElementClass, doc, null, ANY);
    }

    /**
     * Liefert eine Liste aller Elemente, die über die angegebene Kantenart mit den direkten und indirekten Oberelementen dieses Elementes verbunden
     * sind. Es wird nur in dem angegebenen <code>GraphDocument</code> gesucht und nur in der angegebenen
     * Richtung der Edge.
     *
     * @param searchElementClass Elementart nach der gesucht werden soll
     * @param doc <code>GraphDocument</code>, in dem nach verbundenen Elementen gesucht wird
     * @param edgeClass Art der Edge, über die Elemente mit den Teilen dieses Elementes verbunden sein sollen
     * @param direction Richtung, die die Kanten haben sollen, über die die verbundenen Elemente gesucht werden
     * @return Liste mit verbundenen <code>ModelElement</code>s
     */
    public final List<ElementContainer> getParentConnectedContainer(final Class<? extends ModelElement> searchElementClass, final GraphDocument doc, final Class<? extends Edge> edgeClass, final int direction) {
        //Rückgabeliste
        List<ElementContainer> connected = new ArrayList<>();
        //für alle Oberelemente
        List<ModelElement> al = getParentElements(false);

        for (ModelElement me : al) {
            //füge zur Rückgabeliste alle über die angegebene Art verbundenen Node hinzu
            connected.addAll(me.getConnectedContainer(searchElementClass, doc, edgeClass, direction));
        }
        return connected;
    }

    /**
     * Liefert eine Liste mit den ElementContainer der direkten Teilelemente im aktuellen <code>GraphDocument</code>
     *
     * @param doc
     * @return Liste mit den Containern der direkten Teilelemente
     */
    public List<ElementContainer> getDirectPartContainer(final GraphDocument doc) {
        Class<? extends HasPartEdge>[] hasPartsEdgeClasses = ModelConstants.getHasPartsEdgeClasses(getClass());
        List<ElementContainer> returnList = new ArrayList<>();
        for (Class<? extends HasPartEdge> c : hasPartsEdgeClasses) {
            returnList.addAll(getConnectedContainer(ModelElement.class, doc, c, Edge.FORWARD));
        }
        return returnList;
    }

    /**
     * Liefert eine Liste mit den ElementContainer der direkten Oberelemente im aktuellen <code>GraphDocument</code>
     *
     * @param doc
     * @return Liste mit den Containern der direkten oberelemente
     */
    public List<ElementContainer> getDirectParentContainer(final GraphDocument doc) {
        Class<? extends HasPartEdge>[] isPartEdgeClasses = ModelConstants.getIsPartOfEdgeClasses(getClass());
        List<ElementContainer> returnList = new ArrayList<>();
        for (Class<? extends HasPartEdge> c : isPartEdgeClasses) {
            returnList.addAll(getConnectedContainer(ModelElement.class, doc, c, Edge.BACKWARD));
        }
        return returnList;
    }

    /**
     * Prüft, ob im übergebenen GraphDocument der Container eines direkt verbundenen Parent-Elementes von <code>this</code> existiert. Wenn ja, kommt
     * <code>true</code> zurück.
     *
     * @param doc
     * @return
     */
    public final boolean hasDirectParentContainer(final GraphDocument doc) {
        return getDirectParentContainer(doc).size() > 0;
    }

    /**
     * Prüft, ob im übergebenen GraphDocument ein Container eines Part-Elementes von <code>this</code> existiert. Wenn ja, kommt <code>true</code>
     * zurück.
     *
     * @param doc
     * @return
     */
    public boolean hasDirectPartContainer(final GraphDocument doc) {
        return getDirectPartContainer(doc).size() > 0;
    }

    /**
     * Liefert ein <code>List</code> aller <code>ElementContainer</code>, deren Elemente Teil dieses Elementes sind, aber selbst keine Teile
     * besitzen. <br>
     *
     * @return Liste mit <code>ElementContainer</code>n, die die absoluten Kindelemente sind
     */
    public final List<ElementContainer> getAbsolutePartContainer(final GraphDocument doc) {
        List<ElementContainer> parts = getDirectPartContainer(doc);
        for (int i = 0; i < parts.size(); i++) {
            ModelElement part = parts.get(i).getElement();
            List<ElementContainer> partParts = part.getDirectPartContainer(doc);
            if (partParts.size() > 0) {
                parts.remove(i--);
                parts.addAll(partParts);
            }
        }
        return parts;
    }

    /**
     * Füllt die übergebene Liste <code>returnList</code> mit allen hierarschich verbundenen Elementen.
     *
     * @param returnList Liste mit <code>ElementContainer</code>n
     * @param doc (Teil-)Modell in dem gesucht werden soll
     * @param parts Wenn <code>true</code> wird nach allen Teilen gesucht, sonst nach allen Oberelementen
     * @param testonly Wenn <code>true</code> wird beim ersten gefundenen Element abgerochen und <code>true</code> zurück gegeben
     * @return <code>true</code>, wenn mind. ein Element gefunden wurde, das in die Rückgabeliste gehört
     */
    private final boolean getPartOrParentContainer(final List<ElementContainer> returnList, GraphDocument doc, final boolean parts, final boolean testonly) {
        if (returnList == null || returnList.size() == 0 || returnList.get(0) == null) {
            return false;
        }
        doc = isUnique() ? doc.getCollection().getMainGraphDocument() : doc;
        List<ElementContainer> partsOrParents = null;
        ModelElement lastResultElement = returnList.get(returnList.size() - 1).getElement();
        partsOrParents = parts ? lastResultElement.getDirectPartContainer(doc) : lastResultElement.getDirectParentContainer(doc);
        for (int i = 0; i < partsOrParents.size(); i++) {
            boolean found = false;
            ElementContainer pp = partsOrParents.get(i);
            for (int j = 0; j < returnList.size(); j++) {
                if (pp == returnList.get(j)) {
                    found = true;
                    if (testonly) {
                        return true;
                    }
                    break;
                }
            }
            if (!found) {
                returnList.add(pp);
                getPartOrParentContainer(returnList, doc, parts, testonly);
            }
        }
        return false;
    }

    /**
     * Liefert alle Eltern, Kinder und Geschwister dieses Elementes und das Element selbst. Es werden also alle Elemente gesucht, die mit diesem
     * Element über eine beliebigen Pfad von PartOfVerbindungen zusammenhängen.
     *
     * @return Liste mit <code>ModelElement</code>en
     */
    public final List<ModelElement> getPartAndParentElements() {
        Set<ModelElement> returnSet = new HashSet<>();
        for (ModelElement parent : getParentElements()) {
            returnSet.addAll(parent.getPartElements());
        }
        if (returnSet.size() == 0) {
            returnSet.addAll(getPartElements());
            returnSet.add(this);
        }
        return new ArrayList<>(returnSet);
    }

    /**
     * Gibt die Parts in Form von <code>ElementContainer</code> zurück.
     *
     * @param doc
     * @return Eine <code>List</code> gefüllt mit <code>ElementContainer</code>n.
     */
    public final List<ElementContainer> getPartContainer(final GraphDocument doc) {
        List<ElementContainer> al = new ArrayList<>();
        ElementContainer ec = getContainer(doc);
        if (ec != null) {
            al.add(ec);
        }
        getPartOrParentContainer(al, doc, true, false);
        return al;
    }

    /**
     * Gibt die Parts in Form von <code>ModelElement</code> zurück.
     *
     * @param addMeAsFirst
     * @return Eine <code>List</code> gefüllt mit <code>ModelElement</code>s.
     */
    public final List<ModelElement> getPartElements(final boolean addMeAsFirst) {
        if (!addMeAsFirst) {
            return new ArrayList<>(getPartElements());
        }
        Set<ModelElement> s = getPartElements();
        List<ModelElement> returnList = new ArrayList<>(s.size() + 1);
        returnList.add(this);
        returnList.addAll(s);
        return returnList;
    }

    /** rekursiv über alle ist-Teil-von-Beziehungen */
    public final Set<ModelElement> getParentElements() {
        return getSubOrSuperElements(HasPartEdge.class, false);
    }

    /** rekursiv über alle ist-Teil-von-Beziehungen */
    public final Set<ModelElement> getPartElements() {
        return getSubOrSuperElements(HasPartEdge.class, true);
    }

    public boolean isSubElementOf(final ModelElement me) {
        return isSubElementOf(me, SubordinationEdge.class);
    }

    public boolean isSuperElementOf(final ModelElement me) {
        return isSuperElementOf(me, SubordinationEdge.class);
    }

    public boolean isSubElementOf(final ModelElement me, final Class<? extends SubordinationEdge> subordinationEdgeClass) {
        return me.getSubElements(subordinationEdgeClass).contains(this);
    }

    public boolean isSuperElementOf(final ModelElement me, final Class<? extends SubordinationEdge> subordinationEdgeClass) {
        return getSubElements(subordinationEdgeClass).contains(me);
    }

    public final Set<ModelElement> getSubElements(final Class<? extends SubordinationEdge> subordinationEdgeClass) {
        return getSubOrSuperElements(subordinationEdgeClass, true);
    }

    public final Set<ModelElement> getSuperElements(final Class<? extends SubordinationEdge> subordinationEdgeClass) {
        return getSubOrSuperElements(subordinationEdgeClass, false);
    }

    /**
     * @param subordinationEdgeClass
     * @param subElements
     * @return
     */
    private final Set<ModelElement> getSubOrSuperElements(final Class<? extends SubordinationEdge> subordinationEdgeClass, final boolean subElements) {
        Set<ModelElement> recursiveConnected = new HashSet<>();
        getSubOrSuperElementsRecursive(recursiveConnected, subordinationEdgeClass, subElements);
        return recursiveConnected;
    }

    /**
     * @param result
     * @param edgeClass
     * @param subElements
     */
    private final void getSubOrSuperElementsRecursive(final Set<ModelElement> result, final Class<? extends SubordinationEdge> edgeClass, final boolean subElements) {
        for (Edge edge : getEdges()) {
            if (!edgeClass.isAssignableFrom(edge.getClass())) {
                continue;
            }
            ModelElement connected = subElements ? ((SubordinationEdge) edge).getSubElement() : ((SubordinationEdge) edge).getSuperElement();
            if (this != connected && result.add(connected)) {
                connected.getSubOrSuperElementsRecursive(result, edgeClass, subElements);
            }
        }
    }

    /**
     * Liefert die direkten Teilelemente dieses Elements
     *
     * @return
     */
    public final List<ModelElement> getDirectPartElements() {
        Class<? extends HasPartEdge>[] hasPartEdgeClasses = ModelConstants.getHasPartsEdgeClasses(getClass());
        List<ModelElement> returnList = new ArrayList<>();
        for (Class<? extends HasPartEdge> c : hasPartEdgeClasses) {
            returnList.addAll(getConnectedElements(ModelElement.class, c, FORWARD));
        }
        return returnList;
    }

    /**
     * Liefert die direkten Oberelemente dieses Elements
     *
     * @return
     */
    public final List<ModelElement> getDirectParentElements() {
        Class<? extends HasPartEdge>[] isPartEdgeClasses = ModelConstants.getIsPartOfEdgeClasses(getClass());
        List<ModelElement> returnList = new ArrayList<>();
        for (Class<? extends HasPartEdge> c : isPartEdgeClasses) {
            returnList.addAll(getConnectedElements(ModelElement.class, c, BACKWARD));
        }
        return returnList;
    }

    /**
     * Gibt die Parents in Form von <code>ModelElement</code> zurück.
     *
     * @param doc
     * @param addMeAsFirst
     * @return Eine <code>List</code> gefüllt mit <code>ModelElement</code>s.
     */
    public final List<ModelElement> getParentElements(final boolean addMeAsFirst) {
        if (!addMeAsFirst) {
            return new ArrayList<>(getParentElements());
        }
        Set<ModelElement> s = getParentElements();
        List<ModelElement> returnList = new ArrayList<>(s.size() + 1);
        returnList.add(this);
        returnList.addAll(s);
        return returnList;
    }

    /**
     * Gibt alle absoluten Teilelemente zurück
     *
     * @return
     */
    public final Set<ModelElement> getAbsolutePartElements() {
        Set<ModelElement> returnSet = new HashSet<>(1);
        Set<ModelElement> parts = new HashSet<>();
        parts.add(this);
        while (parts.size() > 0) {
            Set<ModelElement> partParts = new HashSet<>();
            for (ModelElement part : parts) {
                List<ModelElement> pParts = part.getDirectPartElements();
                if (pParts.size() == 0) {
                    returnSet.add(part);
                } else {
                    partParts.addAll(pParts);
                }
            }
            parts = partParts;
        }
        return returnSet;
    }

    /**
     * @param doc
     * @return
     */
    public final boolean isInPartOfCyle(GraphDocument doc) {
        doc = isUnique() ? doc.getCollection().getMainGraphDocument() : doc;
        List<ElementContainer> al = new ArrayList<>();
        al.add(getContainer(doc));
        return getPartOrParentContainer(al, doc, true, true);
    }

    /** rekursiv über alle ist-Teil-von-Beziehungen */
    public final boolean isParentOf(final ModelElement me) {
        return getPartElements().contains(me);
    }

    /** rekursiv über alle ist-Teil-von-Beziehungen */
    public final boolean isPartOf(final ModelElement me) {
        return me.getPartElements().contains(this);
    }

    /**
     * Liefert <code>true</code>, wenn <code>this</code> und <code>me</code> direkt über eine <code>IsPartOfEdge</code> verbunden sind und
     * <code>this</code> ein Teil von <code>me</code> ist.
     *
     * @param me
     * @return
     */
    public final boolean isDirectPartOf(final ModelElement me) {
        return me.isDirectParentOf(this);
    }

    /**
     * Liefert <code>true</code>, wenn <code>me</code> und <code>this</code> direkt über eine <code>IsPartOfEdge</code> verbunden sind und
     * <code>me</code> ein Teil von <code>this</code> ist.
     *
     * @param me
     * @return
     */
    public final boolean isDirectParentOf(final ModelElement me) {
        for (Edge edge : getEdges()) {
            if (!(edge instanceof HasPartEdge)) {
                continue;
            }
            if (((HasPartEdge) edge).getPart() == me) {
                return true;
            }
        }
        return false;
    }

    /**
     * Gibt wieder, ob dieses Modelelement Parent eines anderen ist.
     *
     * @return
     */
    public final boolean hasPart() {
        return getDirectPartElements().size() > 0;
    }

    /**
     * Gibt wieder, ob dieses Modelelement Part eines anderen ist.
     *
     * @return
     */
    public final boolean hasParent() {
        return getDirectParentElements().size() > 0;
    }

    //////////////////
    // Ende Part-Of //
    //////////////////

    ////////////////////////
    // Beginn Composition //
    ////////////////////////

    /**
     * Liefert eine Liste aller Container der Slaveelemente dieses Elementes, also aller Elemente, die mit diesem Element über eine
     * {@link CompositionEdge} verbunden sind, wobei das verbundene Element diesem Element übergeordnet ist.
     *
     * @param doc {@link GraphDocument} in dem die Container liegen sollen
     * @return
     */
    public final List<ElementContainer> getDirectCompositionSlaveContainer(final GraphDocument doc) {
        List<ElementContainer> retVal = new ArrayList<>();
        for (Edge edge : getEdges()) {
            if (edge instanceof CompositionEdge) {
                CompositionEdge comp = (CompositionEdge) edge;
                if (comp.getMaster() == this) {
                    ModelElement slave = comp.getSlave();
                    ElementContainer slaveContainer = slave.getContainer(doc);
                    if (slaveContainer != null) {
                        retVal.add(slaveContainer);
                    }
                }
            }
        }
        return retVal;
    }

    //////////////////////
    // Ende Composition //
    //////////////////////

    /**
     * Gibt alle mit diesem <code>ModelElement</code> verbundenen <code>ModelElement</code>s des Klasse <code>searchElementOrTraceClass</code>
     * zurueck.
     *
     * @param searchElementClass
     * @return
     */
    public final List<ModelElement> getConnectedElements(final Class<? extends ModelElement> searchElementClass) {
        return getConnectedElements(searchElementClass, (Class<? extends Edge>) null);
    }

    /**
     * Gibt alle mit diesem <code>ModelElement</code> verbundenen <code>ModelElement</code>s des Klasse <code>searchElementClass</code> zurueck.
     *
     * @param searchElementClass
     * @param edgeClass
     * @return
     */
    public final List<ModelElement> getConnectedElements(final Class<? extends ModelElement> searchElementClass, final Class<? extends Edge> edgeClass) {
        return getConnectedElements(searchElementClass, edgeClass, ANY);
    }

    /**
     * Gibt alle mit diesem <code>ModelElement</code> verbundenen <code>ModelElement</code>s des Klasse <code>searchElementClass</code> zurueck.
     *
     * @param searchElementClass
     * @param edgeClass
     * @param direction
     * @return
     */
    public final List<ModelElement> getConnectedElements(final Class<? extends ModelElement> searchElementClass, final Class<? extends Edge> edgeClass, final int direction) {
        return getConnectedElements(searchElementClass, edgeClass, direction, false);
    }

    /**
     * Gibt alle mit diesem <code>ModelElement</code> verbundenen <code>ModelElement</code>s des Klasse <code>searchElementClass</code> zurueck.
     *
     * @param searchElementClass
     * @param edgeClass
     * @param direction
     * @param alphabetical
     * @return List mit allen verbundenen Node
     */
    public final List<ModelElement> getConnectedElements(final Class<? extends ModelElement> searchElementClass, final Class<? extends Edge> edgeClass, final int direction, final boolean alphabetical) {
        return getConnectedElements(searchElementClass, null, edgeClass, direction, alphabetical);
    }

    /**
     * Gibt alle mit diesem <code>ModelElement</code> verbundenen <code>ModelElement</code>s der Klasse <code>searchElementClass</code> zurueck.
     *
     * @param searchElementClass
     * @param doc <code>GraphDocument</code> in dem verbundene Elemente gesucht werden sollen. Wird <code>null</code> übergeben, werden alle
     *            verbundenen Elemente zurück gegeben, was der Suche im Hauptmodell entspricht.
     * @param edgeClass
     * @param direction
     * @param alphabetical
     * @return List mit allen verbundenen <code>ModelElement</code>s oder <code>ElementContainer</code>n
     */
    @SuppressWarnings("unchecked")
    public final List<ModelElement> getConnectedElements(final Class<? extends ModelElement> searchElementClass, final GraphDocument doc, final Class<? extends Edge> edgeClass, final int direction, final boolean alphabetical) {
        return (List<ModelElement>) getConnected(searchElementClass, doc, edgeClass, direction, false, alphabetical);
    }

    /**
     * Gibt alle mit diesem <code>ModelElement</code> verbundenen <code>ModelElement</code>s der Klasse <code>searchElementClass</code> zurueck oder
     * deren <code>ElementContainer</code>.
     *
     * @param searchElementClass
     * @param doc <code>GraphDocument</code> in dem verbundene Elemente gesucht werden sollen. Wird <code>null</code> übergeben, werden alle
     *            verbundenen Elemente zurück gegeben, was der Suche im Hauptmodell entspricht. Will man aber <code>ElementContainer</code> aus dem
     *            Hauptmodell haben, muss man ein gültiges Haupt- <code>GraphDocument</code> übergeben.
     * @param edgeClass
     * @param direction
     * @param container wenn <code>true</code>, werden die <code>ElementContainer</code> der gefundenen Elemente zurück gegeben; sonst die Elemente
     *            selbst
     * @param alphabetical
     * @return List mit allen verbundenen <code>ModelElement</code>s oder <code>ElementContainer</code>n
     */
    private final List<?> getConnected(final Class<? extends ModelElement> searchElementClass, final GraphDocument doc, final Class<? extends Edge> edgeClass, final int direction, final boolean container, final boolean alphabetical) {
        List<Object> knoten = new ArrayList<>(getEdgesCount());

        if (doc == null && container) {
            System.err.println("Can't find ElementContainer with an null-GraphDocument");
            return knoten;
        }

        for (Edge edge : getEdges()) {
            if (edgeClass != null && !edgeClass.isAssignableFrom(edge.getClass())) {
                continue;
            }

            ModelElement knot = null;
            switch (direction) {
            case ANY:
                knot = edge.getEnd() == this ? edge.getStart() : edge.getEnd();
                break;
            case FORWARD:
                switch (edge.getDirection()) {
                case FORWARD:
                    knot = edge.getEnd() == this ? null : edge.getEnd();
                    break;
                case BACKWARD:
                    knot = edge.getEnd() == this ? edge.getStart() : null;
                    break;
                case DOUBLE:
                    knot = edge.getEnd() == this ? edge.getStart() : edge.getEnd();
                    break;
                }
                break;
            case BACKWARD:
                switch (edge.getDirection()) {
                case FORWARD:
                    knot = edge.getEnd() == this ? edge.getStart() : null;
                    break;
                case BACKWARD:
                    knot = edge.getEnd() == this ? null : edge.getEnd();
                    break;
                case DOUBLE:
                    knot = edge.getEnd() == this ? edge.getStart() : edge.getEnd();
                    break;
                }
                break;
            case DOUBLE:
                if (edge.getDirection() == DOUBLE) {
                    knot = edge.getEnd() == this ? edge.getStart() : edge.getEnd();
                }
                break;
            }

            if (knot == null) {
                continue;
            }
            if (!searchElementClass.isAssignableFrom(knot.getClass())) {
                continue;
            }
            if (container) {
                ElementContainer ec = knot.getContainer(knot.isUnique() ? doc.getCollection().getMainGraphDocument() : doc);
                if (ec != null) {
                    if (alphabetical) {
                        Alphabetical.insert(knoten, ec);
                    } else {
                        knoten.add(ec);
                    }
                }
            } else {
                if (doc != null && knot.getContainer(knot.isUnique() ? doc.getCollection().getMainGraphDocument() : doc) == null) {
                    continue;
                }
                if (alphabetical) {
                    Alphabetical.insert(knoten, knot);
                } else {
                    knoten.add(knot);
                }
            }
        }
        return knoten;
    }

    /**
     * Gibt alle <code>ModelElement</code>s zurück, die mit diesem <code>ModelElement</code> im angegebenen <code>GraphDocument</code> verbunden sind.
     *
     * @param edgeClass alle Kanten des Types
     * @return List mit den verbundenen ModelElementen
     */
    public List<ModelElement> getConnectedElementsByEdge(final Class<? extends Edge> edgeClass) {
        return getConnectedElementsByEdge(getOther(edgeClass, getClass()), edgeClass);
    }

    /**
     * Gibt alle <code>ModelElement</code>s zurück, die mit diesem <code>ModelElement</code> im angegebenen <code>GraphDocument</code> verbunden sind.
     *
     * @param edgeClass alle Kanten des Types
     * @param targetElementClass Klasse, von der die Zielelemente sein sollen. Diese muss nicht mit der letzten Elementklasse des Metapfades
     *            übereinstimmen, sondern kann eine spezielle Unterklasse sein.
     * @return List mit den verbundenen ModelElementen
     */
    public List<ModelElement> getConnectedElementsByEdge(final Class<? extends ModelElement> targetElementClass, final Class<? extends Edge> edgeClass) {
        List<ModelElement> retVal = new ArrayList<>(getEdgesCount());
        for (Edge edge : getEdges()) {
            if (edgeClass.isAssignableFrom(edge.getClass())) {
                ModelElement me = edge.getOther(this);
                if (targetElementClass.isAssignableFrom(me.getClass())) {
                    retVal.add(me);
                }
            }
        }
        return retVal;
    }

    /**
     * Liefert einen Eigenschafts-Dialog für dieses Element. Wenn bereits einer existiert, wird dieser zurück gegeben, sonst wird ein neuer Dialog
     * angelegt. Der Dialog wird sofort angezeigt oder wenn er bereits angezeigt wird in den Vordergrund gebracht.
     *
     * @param gdcoll GDCollection, in der sich das Element befinden sollte
     * @return
     */
    public ElementPropertyDialog getPropertyDialog() {
        ElementPropertyDialog prop = ModelConstants.hasObjektDialog(this);
        if (prop == null) {
            prop = createPropertyDialog();
            ModelConstants.dialogs.add(prop);
        }
        return prop;
    }

    protected ElementPropertyDialog createPropertyDialog() {
        GDCollection gdcoll = getCollection();
        if (gdcoll == null) {
            return null;
        }
        return new ElementPropertyDialog(this, gdcoll);
    }

    /**
     * Gibt eine Liste aller Verbindungen der angegebenen Art zurück.<br>
     * Die übergebene Klasse muss gleich der zurückzugebenen Kantenklassen oder eine Oberklasse davon sein.
     *
     * @param edgeClass Klasse der zu suchenden Kanten
     * @return
     */
    public final List<Edge> getEdges(final Class<? extends Edge> edgeClass) {
        List<Edge> returnList = new ArrayList<>(getEdgesCount());
        for (Edge edge : getEdges()) {
            if (edgeHasClass(edge, edgeClass)) {
                returnList.add(edge);
            }
        }
        return returnList;
    }

    /**
     * Counts the edges
     *
     * @param edgeClass Type of edges to count
     * @return Number of edges with the specified type
     */
    public final int countConnections(final Class<? extends Edge> edgeClass) {
        int retVal = 0;
        for (Edge edge : getEdges()) {
            if (edgeHasClass(edge, edgeClass)) {
                retVal++;
            }
        }
        return retVal;
    }

    /**
     * @return <code>true</code>, wenn das Elemente alle Kanten in ausreichender Anzahl hat, die es haben muss
     *         (= Kanten, bei denen die minimale Kardinalität > 0 ist)
     */
    public boolean isConsistent() {
        Class<? extends ModelElement> meClass = getClass();
        Class<? extends Edge>[] edgeTypes = ModelConstants.getEdgeTypes(meClass);
        //für alle Kantenarten dieser ModelElement-Klasse
        for (Class<? extends Edge> edgeType : edgeTypes) {
            //minimale Kardinalität wird erst einmal als 0 angenommen
            int minCardinality = 0;
            //wenn diese Elementart Startklasse der aktuellen Kantenart ist
            if (isStartClass(edgeType, meClass)) {
                //minimale Kardinalität zur Endklasse holen
                minCardinality = getMinForwardCardinality(edgeType);
                //wenn diese minimale Kardinalität > 0 ist, aber dieses Element weniger Kanten zu anderen Elementen hat, als nötig
                if (minCardinality > 0 && getEdgesTo(ModelElement.class, edgeType).size() < minCardinality) {
                    //nicht konsistent
                    return false;
                }
            }
            //OHNE ELSE-IF! Wenn diese Elementart Endklasse der aktuellen Kantenart ist
            if (isEndClass(edgeType, meClass)) {
                //minimale Kardinalität zur Startklasse holen
                minCardinality = getMinBackwardCardinality(edgeType);
                //wenn diese minimale Kardinalität > 0 ist, aber dieses Element weniger Kanten von anderen Elementen zu sich hat, als nötig
                if (minCardinality > 0 && getEdgesFrom(ModelElement.class, edgeType).size() < minCardinality) {
                    //nicht konsistent
                    return false;
                }
            }
        }
        //alle notwendigen Kanten in ausreichender Anzahl vorhanden
        return true;
    }

    /**
     * Ueberprueft, ob der Node zur Menge der UNIQUE_KNOTS gehoert, ob er also ein Node ist, der im gesamten Modell nur einmal vorkommt
     */
    public boolean isUnique() {
        return ModelConstants.isUnique(getClass());
    }

    /**
     * Ueberprueft, ob der Node ein untergeodnetes Element ist.
     *
     * @return
     */
    public boolean isSlave() {
        return ModelConstants.isSlaveType(getClass());
    }

    /**
     * @return
     */
    public boolean isUnpaintable() {
        return !isPaintable();
    }

    /**
     * @return
     */
    public final boolean isPaintable() {
        return ModelConstants.getGraphViewDefinition().isPaintable(getClass());
    }

    public final boolean hasLayout() {
        return ModelConstants.getGraphViewDefinition().hasLayout(getClass());
    }

    /**
     * @return <code>true</code>, wenn die Elementart eine IsPartOfEdge hat
     */
    public final boolean canHaveParts() {
        return ModelConstants.canHaveParts(getClass());
    }

    /**
     * @return <code>true</code>, wenn die Elementart eine IsPartOfEdge hat
     */
    public final boolean canHaveParents() {
        return ModelConstants.canHaveParents(getClass());
    }

    /**
     * Testet, ob das Modelelement this in der Richtung direction zu dem ModelElement eine Edge haben kann
     *
     * @param me
     * @param direction {@link .FORWARD, {@link .BACKWARD
     * @param testCardinality wenn <code>true</code>, wird auch noch getestet, ob die maximale Kardinalität der Verbindungen bereits erreicht ist
     * @return
     */
    public final boolean isForwardLinkable(final ModelElement me, final Class<? extends Edge> edgeClass, final boolean testCardinality) {
        //wenn die Edge die beiden Elemente nicht in Vorwärtsrichtung verbinden kann
        if (!isConnectingForward(edgeClass, getClass(), me.getClass())) {
            return false;
        }
        //Wenn es sich bei dieser Kantenart nicht um eine mehrfach zwischend denselben Elementen anlgebare Edge handelt
        if (!ModelConstants.isMultipleEdgeClass(edgeClass)) {
            //wenn schon eine solche Edge zwischen den beiden Elementen existiert
            List<Edge> edges = getEdgesTo(me, edgeClass);
            if (edges != null && edges.size() > 0) {
                return false;
            }
        }
        //wenn es eine PartOfVerbindung ist -> this darf kein Teil von me sein
        if (HasPartEdge.class.isAssignableFrom(edgeClass)) {
            //wenn die beiden Elemente bereits in einer PartOfVerbindung stehen, die der jetzt zu verknüpfenden Richtung widerspricht
            if (me.isPartOf(this)) {
                return false;
            }
        }
        //wenn das Überschreiten der Kardinalität geprüft werden soll
        if (testCardinality) {
            //für das Startelement ist die maximale Verbindungsanzahl bereits erreicht?
            if (getMaxForwardCardinality(edgeClass) <= countConnections(edgeClass)) {
                return false;
            }
            //für das Endelement ist die maximale Verbindungsanzahl bereits erreicht?
            if (getMaxBackwardCardinality(edgeClass) <= me.countConnections(edgeClass)) {
                return false;
            }
        }
        return true;

    }

    /**
     * @return {@link HashMap} aller Container dieses Elementes
     */
    public Map<GraphDocument, ElementContainer> getContainerTable() {
        return containerTable;
    }

    /**
     * Liefert das Modell, in dem dieses Element vorkommt oder <code>null</code>.
     *
     * @return
     */
    public final GDCollection getCollection() {
        //gibt vom erstbesten doc die gdcoll zurück
        for (GraphDocument doc : getContainerTable().keySet()) {
            return doc.getCollection();
        }
        return null;
    }

    /**
     * join Element properties without connections this.hashstring = other.hashstring other will be not changed
     */
    public boolean join(final ModelElement other, final boolean overwriteHashstring) {
        if (other.getClass() != this.getClass() || this == other) {
            return false;
        }

        if (overwriteHashstring) {
            hashstring = other.getHashString();
        }

        String joined = getResString("joined");
        if (!name.trim().equals(other.name.trim())) {
            name = name.concat("\n-" + joined + "-\n" + other.name);
        }
        //es gibt keine Beschreibung bei this
        if (descr.trim().equals("")) {
            //egal, was in der Beschreibung für other steht -> setze sie bei this
            descr = other.descr;
            //es gibt eine Beschreibung bei this
        } else {
            //wenn es auch eine Beschreibung für other gibt, die sich von der von this unterscheidet -> hänge sie zusammen
            if (!other.descr.trim().equals("") || !descr.trim().equals(other.descr.trim())) {
                descr = descr.concat("\n-" + joined + "-\n" + other.descr);
            }
        }

        htmlName = HTMLConverter.getDecimalEncodedHTMLString(name);
        refreshText();

        //UserFields zusammenführen. Bei allen UserFields, bei denen nur ein Element einen Wert hat oder sich die Werte nicht
        //unterscheiden, nimm nur einen gültigen Wert. Haben beide einen unterschiedlichen Wert, führe sie String-technisch zusammen
        Set<UserField> allElementUserFields = new HashSet<>(getUserFieldInputValueKeys());
        allElementUserFields.addAll(other.getUserFieldInputValueKeys());
        for (UserField userField : allElementUserFields) {
            String value = getUserFieldInputValue(userField);
            String otherValue = other.getUserFieldInputValue(userField);
            if (otherValue != EMPTY_STRING) {
                if (value == EMPTY_STRING) {
                    setUserFieldInputValue(userField, otherValue);
                    //wenn es einen otherValue gibt, der sich vom value unterscheidet -> füge sie zusammen
                } else if (!value.equals(otherValue)) {
                    //Bei Kennzahlen bleibt es einfach der Wert des ersten Elements
                    if (!userField.isClassificationUserField()) {
                        setUserFieldInputValue(userField, value.toString().concat(" -" + joined + "- ").concat(otherValue.toString()));
                    }
                }
            }
        }
        return true;
    }

    /**
     * @return
     */
    public String getAssociatedDoc() {
        return associatedSzenHashString;
    }

    /**
     * @param document
     */
    public void setAssociatedDoc(final String document) {
        associatedSzenHashString = document;
    }

    /**
     * COMMENTME
     */
    protected String hyperlink = null;

    /**
     * @return
     */
    public boolean isHyperlink() {
        return hyperlink != null;
    }

    /**
     * @return
     */
    public String getHyperlink() {
        return hyperlink;
    }

}