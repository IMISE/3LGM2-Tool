package de.imise.tool3lgm.graphtools.elements;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.google.common.collect.Lists;

import de.imise.tool3lgm.Tool3lgmConstants;
import de.imise.tool3lgm.graphtools.GDCollection;
import de.imise.tool3lgm.graphtools.GDCommands;
import de.imise.tool3lgm.graphtools.GraphDocument;
import de.imise.tool3lgm.graphtools.dialog.ElementPropertyDialog;
import de.imise.tool3lgm.graphtools.elements.node.Anwendungsprogramm;
import de.imise.tool3lgm.graphtools.elements.node.RechAnwendungsbaustein;
import de.imise.tool3lgm.graphtools.elements.node.Softwareprodukt;
import de.imise.tool3lgm.graphtools.userfield.UserField;
import de.imise.tool3lgm.graphtools.userfield.UserFieldTarget;
import de.imise.tool3lgm.graphtools.view.container.ElementContainer;
import de.imise.tool3lgm.graphtools.view.container.LayerContainer;
import de.imise.tool3lgm.graphtools.view.container.NodeContainer;
import de.imise.tool3lgm.graphtools.view.graph.GraphViewConstants;
import de.imise.tool3lgm.log.Log;
import de.imise.tool3lgm.xml.XMLCharacterCoder;
import de.imise.util.Alphabetical;
import de.imise.util.HTMLConverter;

public abstract class ModelElement extends UserFieldTarget {

    /**
     * Anzahl bereits neu angelegter Elemente
     */
    private static int elementCounter = 0;

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
    private Hashtable<GraphDocument, ElementContainer> containerTable = new Hashtable<GraphDocument, ElementContainer>(3, 1);

    /**
     * Liste aller Assoziationen zu anderen Elementen
     */
    private List<Kante> edges = Lists.newArrayList();

    /**
     * Ein StringBuilder, der gebraucht wird, um die Namen der Elemente zusammen zu bauen. Er ist statisch, damit man ihn nicht ständig neu anlegen
     * muss.
     */
    private static final StringBuilder toStringBuffer = new StringBuilder(40);
    private static final StringBuilder nameBuffer = new StringBuilder(40);
    private String toStringName = null;
    private static final StringBuilder suffixBuf = new StringBuilder("");
    private static final StringBuilder textBuf = new StringBuilder("");

    /** Trenner für die einzelnen Sektionen des HashStrings */
    public static final String HASH_STRING_DELIMITER = "_";

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
    }

    @Override
    public Object clone() {
        ModelElement retVal;
        try {
            retVal = (ModelElement) super.clone();
        } catch (Exception e) {
            Log.show(Log.ERROR, Tool3lgmConstants.getErrString("FehlerAllgemein"), e);
            return null;
        }

        retVal.name = name;
        retVal.htmlName = htmlName;
        retVal.descr = descr;
        retVal.hashstring = getNewHashString(this);
        retVal.containerTable = new Hashtable<GraphDocument, ElementContainer>(3, 1);

        retVal.edges = new ArrayList<Kante>(3);

        for (UserField key : getUserFieldInputValueKeys()) {
            retVal.setUserFieldInputValue(key, getUserFieldInputValue(key).toString());
        }

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
        return layer;
    }

    /**
     * does nothing because layer ist defined by 3lgm-specifications <br/>
     * overwrite method in classes with no definite layer, like oldTrace
     *
     * @param layer
     */
    public final void setLayer(final int layer) {
        this.layer = layer;
    }

    /**
     * @param me
     * @return
     */
    public static final String getNewHashString(final ModelElement me) {
        return getNewHashString(me.getClass());
    }

    /**
     * @param elementClass
     * @return
     */
    public static final String getNewHashString(final Class<? extends ModelElement> elementClass) {
        elementCounter++;
        return ModelConstants.getShortName(elementClass) + HASH_STRING_DELIMITER + new Date().getTime() + HASH_STRING_DELIMITER + elementCounter;
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
        return new HashSet<GraphDocument>(containerTable.keySet());
    }

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

    /** 01.01.1970 als Date */
    public static final Date STANDARD_CREATION_DATE = new Date(0);

    /**
     * Berechnet aus dem HashString das Datum, an dem das Element erstellt wurde. Lässt sich das Datum aus irgendwelchen Gründen nicht berechnen kommt
     * new STANDARD_CREATION_DATE = new Date(0) zurück.
     *
     * @return
     */
    public Date getCreationDate() {
        try {
            String h = getHashString();
            long l = Long.parseLong(h.substring(h.indexOf(HASH_STRING_DELIMITER) + 1, h.lastIndexOf(HASH_STRING_DELIMITER)));
            return new Date(l);
        } catch (Exception e) {
            return STANDARD_CREATION_DATE;
        }
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
        ArrayList<GraphDocument> mySortedSzenarios = new ArrayList<GraphDocument>(mySzenarios);
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
        if (name.equalsIgnoreCase("null")) {
            this.name = "";
        } else {
            this.name = name;
        }

        updateNameWithSzens();

        //Knoten der Layer neu sortieren
        if (sort && this instanceof Knoten) {
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

        suffixBuf.setLength(0);
        if (this instanceof RechAnwendungsbaustein) {
            for (ModelElement awp : getConnectedElements(Anwendungsprogramm.class)) {
                ArrayList<ModelElement> connectedSwp = awp.getConnectedElements(Softwareprodukt.class);
                //Kein Softwareprodukt verbunden -> weiter
                if (connectedSwp.size() == 0) {
                    continue;
                }
                //genau ein Softwareprodukt verbunden, das denselben Namen hat wie dieser Anwendungsbaustein -> weiter (damit in der Grafik nicht
                //2 mal dasselbe steht, wenn der Baustein genau wie das SWP genannt wurde
                if (connectedSwp.size() == 1 && connectedSwp.get(0).getClearName().equals(getClearName())) {
                    continue;
                }
                //in allen anderen Fällen kommt das SWP in Klammmern hinter den Bausteinnamen
                for (ModelElement swp : connectedSwp) {
                    suffixBuf.append("(");
                    suffixBuf.append(swp.getName());
                    suffixBuf.append(")");
                }
            }
        }
        textBuf.delete(0, textBuf.length());
        textBuf.append("<HTML><CENTER>");
        if (isHyperlink()) {
            textBuf.append("<U>");
        }
        textBuf.append(HTMLConverter.getHTMLString(name).replaceAll("&#10;", "<BR>").replaceAll("\\\\&#45;", "-<BR>"));
        if (isHyperlink()) {
            textBuf.append("</U>");
        }
        textBuf.append(suffixBuf.length() > 0 ? "<BR>" : "");
        textBuf.append(suffixBuf.length() > 0 ? HTMLConverter.getHTMLString(suffixBuf.toString()) : "");
        textBuf.append("</CENTER></HTML>");
        htmlName = textBuf.toString();

        for (ElementContainer ec : containerTable.values()) {
            ec.refreshText();
        }

    }

    //	/**
    //	 * Alte Variante. Hier könnte es ein Problem geben, wenn der zuerst im Iterator gefundene Container
    //	 * kleiner ist, als 35x30, dann bekommen alle weiteren Container unabhängig von der Größe
    //	 * auch einen leeren String als Anzeigetext
    //	 */
    //	public void refreshText() {
    //		Iterator<ElementContainer> it = containerTable.values().iterator();
    //		if (it.hasNext()) {
    //			ElementContainer ec = it.next();
    //			ec.refreshText();
    //			String text = ec.getText();
    //			if (this instanceof Bausteinschnittstelle)
    //				//TODO:AXS 15.02.2012: wieder entfernen
    ////				System.err.println("ModelElement.refreshText() " + this + " -> " + text);
    //			while (it.hasNext()) {
    //				ElementContainer ec2 = it.next();
    //				if ((ec2.getWidth() < 35) && (ec2.getHeight() < 30))
    //					ec2.setText("");
    //				else
    //					ec2.setText(text);
    ////				System.err.println("ModelElement.refreshText() " + this + " -> " + text + );
    //			}
    //		}
    //	}

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
     * if you overwrite this methode in a sub-class nevertheless please always call this methode by super.getXMLEntities()
     *
     * @return xml-tags for name, description and userFields
     */
    protected StringBuilder getXMLEntities() {
        if (name == null) {
            name = "";
        }
        if (descr == null) {
            descr = "";
        }
        StringBuilder retVal = new StringBuilder();
        retVal.append("<field name=\"layer\">" + layer + "</field>");
        retVal.append("<field name=\"name\">" + XMLCharacterCoder.encodeString(name) + "</field>");
        retVal.append("<field name=\"description\">" + XMLCharacterCoder.encodeString(descr) + "</field>");
        retVal.append(associatedSzenHashString != null && associatedSzenHashString != "" ? "<field name=\"assoc_szen\">" + associatedSzenHashString + "</field>" : "");

        appendUserFieldXMLString(retVal);
        return retVal;
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
        if (field.equals("layer")) {
            int layer = Integer.parseInt(value);
            if (layer < 0 || layer > ModelConstants.LAYERS.length) {
                layer = ModelConstants.layerFor(getClass());
            }
            setLayer(layer);
            return true;
        }
        if (field.equals("assoc_szen")) {
            setAssociatedDoc(value);
            return true;
        }

        return false;
    }

    /**
     * @author Thomas Rudert
     * @return String der vollstaendige XML-Tag zu diesem Objekt
     */
    public String toXMLString() {
        StringBuilder sb = new StringBuilder("<element class=\"");
        sb.append(getClass().getSimpleName());
        sb.append("\" hash=\"");
        sb.append(hashstring);
        sb.append("\">");
        sb.append(getXMLEntities());
        sb.append("</element>");
        return sb.toString();
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

    //ehemals in Knoten.java

    /* --- Funktionen im Netzwerk --- Anfang --- */

    /** Fuegt diesem Knoten eine Kante zu. */
    public boolean addEdge(final Kante kante) {
        if (kante == null || edges.contains(kante)) {
            return false;
        }
        edges.add(kante);
        return true;
    }

    /**
     * Fuegt diesem Knoten in der ArrayList connections an der Position pos die Kante kante hinzu.
     */
    public boolean insertEdge(final Kante kante, int pos) {
        if (kante == null || edges.contains(kante)) {
            return false;
        }
        if (pos < edges.size() || pos > edges.size()) {
            pos = edges.size();
        }
        edges.add(null);
        for (int i = edges.size() - 1; i > pos; i--) {
            edges.set(i, edges.get(i - 1));
        }
        edges.set(pos, kante);
        return true;
    }

    public void setEdges(final ArrayList<Kante> associations) {
        edges = associations;
    }

    /** Setzt in connections die Kante an Position pos auf kante. */
    public boolean setEdge(final int pos, final Kante kante) {
        if (pos < 0 || pos >= edges.size()) {
            return false;
        }
        edges.set(pos, kante);
        return true;
    }

    /** Entfernt die angegebene Kante vom Knoten. */
    public final void removeEdge(final Kante kante) {
        edges.remove(kante);
    }

    /** Entfernt alle Kanten vom Knoten. */
    public final void removeEdges() {
        edges.clear();
    }

    /* --- Funktionen im Netzwerk --- Ende --- */

    /** Ermittelt, ob der Knoten an eine Kante gebunden ist oder nicht. */
    public final boolean hasEdges() {
        if (edges.size() > 0) {
            return true;
        }
        return false;
    }

    /** Gibt die Zahl der Kanten zurueck, an die der Knoten gebunden ist. */
    public final int getEdgesCount() {
        return edges.size();
    }

    /** Gibt den Vektor der Verbindungen zurueck */
    public final Iterable<Kante> getEdges() {
        return edges;
    }

    /** Gibt die Verbindung Nummer <i>index </i> zurueck */
    public final Kante getEdge(final int index) {
        if (index >= edges.size() || index < 0) {
            return null;
        }
        return edges.get(index);
    }

    public int getEdgeIndex(final Kante edge) {
        return edges.indexOf(edge);
    }

    //###############################################################################

    /**
     * @param index
     * @return Knoten ueber die Verbindung Nummer <i>index </i> zurueck
     */
    public final ModelElement getConnectedElement(final int index) {
        Kante k = getEdge(index);
        if (k == null) {
            return null;
        }
        if (this == k.getStart()) {
            return k.getEnd();
        }
        return k.getStart();
    }

    /**
     * @param edge
     * @return <code>true</code>, wenn Kante <i>k</i> an diesem Knoten ansetzt, sonst <code>false</code>
     */
    public final boolean hasConnection(final Kante edge) {
        if (edges.indexOf(edge) >= 0) {
            return true;
        }
        return false;
    }

    /**
     * @param modelElement
     * @return <code>true</code>, wenn eine Kante zwischen diesem und dem übergebenen Element besteht
     */
    public final boolean isConnectedWith(final ModelElement modelElement) {
        for (int c = 0; c < edges.size(); c++) {
            if (getEdge(c).isConnecting(this, modelElement)) {
                return true;
            }
        }
        return false;
    }

    /**
     * @param modelElement
     * @return <code>true</code>, wenn eine Kante von diesem zu dem übergebenen Element besteht
     */
    public final boolean isConnectedTo(final ModelElement modelElement) {
        for (int c = 0; c < edges.size(); c++) {
            if (getEdge(c).isDirecting(this, modelElement)) {
                return true;
            }
        }
        return false;
    }

    /**
     * @param modelElement
     * @return <code>true</code>, wenn eine Kante vom übergebenen yu diesem Element besteht
     */
    public final boolean isConnectedFrom(final ModelElement modelElement) {
        for (int c = 0; c < edges.size(); c++) {
            if (getEdge(c).isDirecting(modelElement, this)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Prüft, ob zwischen diesem und dem übergebenen Element eine Kante der angegebenen Art existiert. Die Richtung ist dabei egal.
     *
     * @param modelElement Element zu dem die Existenz einer Verbindung geprüft werden soll
     * @param edgeClass Art der Kante, die gesucht werden soll
     * @return <code>true</code>, wenn eine Kante zwischen diesem und dem übergebenen Element besteht
     */
    public final boolean isConnectedWith(final ModelElement modelElement, final Class<? extends Kante> edgeClass) {
        for (int c = 0; c < edges.size(); c++) {
            if (edgeClass.isAssignableFrom(edges.get(c).getClass()) && getEdge(c).isConnecting(this, modelElement)) {
                return true;
            }
        }
        return false;
    }

    public final boolean isConnectedTo(final ModelElement k, final Class<? extends Kante> edgeClass) {
        for (int c = 0; c < edges.size(); c++) {
            if (edgeClass.isAssignableFrom(edges.get(c).getClass()) && getEdge(c).isDirecting(this, k)) {
                return true;
            }
        }
        return false;
    }

    public final boolean isConnectedFrom(final ModelElement k, final Class<? extends Kante> edgeClass) {
        for (int c = 0; c < edges.size(); c++) {
            if (edgeClass.isAssignableFrom(edges.get(c).getClass()) && getEdge(c).isDirecting(k, this)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Gibt die erste Kante vom Typ <code>edgeClass</code> zurück, die von diesem <code>ModelElement</code> zum <code>ModelElement k</code> geht.
     * Führt <code>getConnectionTo(ModelElement, Class, int)</code> für alle Positionen aus.
     *
     * @param modelElement
     * @param edgeClasses
     * @return
     */
    public final Kante getEdgeTo(final ModelElement modelElement, final Class<? extends Kante> edgeClass) {
        for (int i = 0; i < edges.size(); i++) {
            Kante kante = getEdgeTo(modelElement, edgeClass, i);
            if (kante != null) {
                return kante;
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
    public final Kante getEdgeTo(final ModelElement modelElement, final Class<? extends Kante> edgeClass, final int position) {
        for (int c = 0; c < edges.size(); c++) {
            if (position != GDCommands.INVALID_EDGE_INDEX && position != c) {
                continue;
            }
            Kante ka = getEdge(c);
            if (edgeClass != null && edgeClass != ka.getClass()) {
                continue;
            }
            if (ka.isDirecting(this, modelElement)) {
                return ka;
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
    public final Kante getEdgeFrom(final ModelElement modelElement, final Class<? extends Kante> edgeClass, final int position) {
        for (int c = 0; c < edges.size(); c++) {
            if (position != GDCommands.INVALID_EDGE_INDEX && position != c) {
                continue;
            }
            Kante ka = getEdge(c);
            if (edgeClass != null && edgeClass != ka.getClass()) {
                continue;
            }
            if (ka.isDirecting(modelElement, this)) {
                return ka;
            }
        }
        return null;
    }

    /**
     * @param elemClass
     * @param edgeClass
     * @param direction
     * @return
     */
    public ArrayList<Kante> getEdge(final Class<? extends ModelElement> elemClass, final Class<? extends Kante> edgeClass, final int direction) {
        ArrayList<Kante> kanten = null;
        if (Doppelkante.FORWARD == direction) {
            kanten = getEdgesTo(elemClass, edgeClass);
        } else if (Doppelkante.BACKWARD == direction) {
            kanten = getEdgesFrom(elemClass, edgeClass);
        } else {
            kanten = getEdgesWith(elemClass, edgeClass);
        }
        return kanten;
    }

    /**
     * @param modelElement
     * @return
     */
    public final ArrayList<Kante> getEdgesWith(final ModelElement modelElement) {
        return getEdgesWith(modelElement, null);
    }

    /**
     * @param modelElement
     * @param edgeClass
     * @return
     */
    public final ArrayList<Kante> getEdgesWith(final ModelElement modelElement, final Class<? extends Kante> edgeClass) {
        return getEdgesWith(modelElement, edgeClass, GDCommands.INVALID_EDGE_INDEX);
    }

    /**
     * @param modelElement
     * @param edgeClass
     * @param position
     * @return
     */
    public final ArrayList<Kante> getEdgesWith(final ModelElement modelElement, final Class<? extends Kante> edgeClass, final int position) {
        return getEdgesWith(modelElement, edgeClass, position, Doppelkante.ANY);
    }

    ///*	public final ArrayList<Kante> getEdgesWith(ModelElement modelElement, Class<? extends Kante> edgeClass, int position) {
    //		ArrayList<Kante> retVal = new ArrayList<Kante>();
    //		if (modelElement == null)
    //			return retVal;
    //		for (int c = 0; c < edges.size(); c++) {
    //			if ((position != GDCommands.INVALID_EDGE_INDEX) && (position != c))
    //				continue;
    //			Kante ka = getEdge(c);
    //			if ((edgeClass != null) && (edgeClass != ka.getClass()))
    //				continue;
    //			if (ka.isConnecting(this, modelElement))
    //				retVal.add(ka);
    //		}
    //		return retVal;
    //	}
    //*/

    /**
     * @param modelElement
     * @param edgeClass
     * @return
     */
    public final ArrayList<Kante> getEdgesTo(final ModelElement modelElement, final Class<? extends Kante> edgeClass) {
        return getEdgesWith(modelElement, edgeClass, GDCommands.INVALID_EDGE_INDEX, Doppelkante.FORWARD);
    }

    /**
     * @param modelElement
     * @param edgeClass
     * @param position
     * @return
     */
    public final ArrayList<Kante> getEdgesTo(final ModelElement modelElement, final Class<? extends Kante> edgeClass, final int position) {
        return getEdgesWith(modelElement, edgeClass, position, Doppelkante.FORWARD);
    }

    /**
     * @param modelElement
     * @param edgeClass
     * @param position
     * @param direction
     * @return
     */
    public final ArrayList<Kante> getEdgesWith(final ModelElement modelElement, final Class<? extends Kante> edgeClass, final int position, final int direction) {
        ArrayList<Kante> retVal = new ArrayList<Kante>();
        if (modelElement == null) {
            return retVal;
        }
        for (int c = 0; c < edges.size(); c++) {
            if (position != GDCommands.INVALID_EDGE_INDEX && position != c) {
                continue;
            }
            Kante ka = getEdge(c);
            if (edgeClass != null && edgeClass != ka.getClass()) {
                continue;
            }
            boolean add = false;
            if (direction == Doppelkante.FORWARD) {
                add = ka.isDirecting(this, modelElement);
            } else if (direction == Doppelkante.BACKWARD) {
                add = ka.isDirecting(modelElement, this);
            } else {
                add = ka.isConnecting(this, modelElement);
            }
            if (add) {
                retVal.add(ka);
            }
        }
        return retVal;
    }

    /**
     * Sucht alle Kanten, die diesen Knoten mit Knoten des angegebenen Typs verbinden.
     *
     * @param elementClass Klasse der verbundenen Knoten
     * @return ArrayList mit allen gefundenen Kanten
     */
    public final ArrayList<Kante> getEdgesWith(final Class<? extends ModelElement> elementClass) {
        return getEdgesWith(elementClass, null);
    }

    /**
     * Sucht alle Kanten des angegebenen Typs, die diesen Knoten mit Knoten des angegebenen Typs verbinden. Wird als <code>edgeClass</code>
     * <code>null</code> übergeben, werden alle Kantenarten zurückgegeben.
     *
     * @param elementClass Klasse der verbundenen Knoten
     * @param edgeClass Kanteklasse nach der gesucht werden soll
     * @return ArrayList mit allen gefundenen Kanten
     */
    public final ArrayList<Kante> getEdgesWith(final Class<? extends ModelElement> elementClass, final Class<? extends Kante> edgeClass) {
        return getEdgesWith(elementClass, edgeClass, Doppelkante.ANY);
    }

    /**
     * Sucht alle Kanten des angegebenen Typs, die diesen Knoten in Vorwärtsrichtung mit Knoten des angegebenen Typs verbinden. Wird als
     * <code>edgeClass</code> <code>null</code> übergeben, werden alle Kantenarten zurückgegeben.
     *
     * @param elementClass Klasse der verbundenen Knoten
     * @param edgeClass Kanteklasse nach der gesucht werden soll
     * @return ArrayList mit allen gefundenen Kanten
     */
    public final ArrayList<Kante> getEdgesTo(final Class<? extends ModelElement> elementClass, final Class<? extends Kante> edgeClass) {
        return getEdgesWith(elementClass, edgeClass, Doppelkante.FORWARD);
    }

    /**
     * Sucht alle Kanten des angegebenen Typs, die diesen Knoten in Rückwärtsrichtung mit Knoten des angegebenen Typs verbinden. Wird als
     * <code>edgeClass</code> <code>null</code> übergeben, werden alle Kantenarten zurückgegeben.
     *
     * @param elementClass Klasse der verbundenen Knoten
     * @param edgeClass Kanteklasse nach der gesucht werden soll
     * @return ArrayList mit allen gefundenen Kanten
     */
    public final ArrayList<Kante> getEdgesFrom(final Class<? extends ModelElement> elementClass, final Class<? extends Kante> edgeClass) {
        return getEdgesWith(elementClass, edgeClass, Doppelkante.BACKWARD);
    }

    /**
     * Sucht alle Kanten des angegebenen Typs, die diesen Knoten mit Knoten des angegebenen Typs verbinden. Wird als <code>edgeClass</code>
     * <code>null</code> übergeben, werden alle Kanten zurückgegeben.
     *
     * @param elementClass Klasse der verbundenen Knoten
     * @param edgeClass Kanteklasse nach der gesucht werden soll
     * @param Richtung der Kante nach der gesucht werden soll (<code>Doppelkante.ANY</code>, <code>Doppelkante.FORWARD</code> oder
     *            <code>Doppelkante.BACKWARD</code>)
     * @return ArrayList mit allen gefundenen Kanten
     */
    public final ArrayList<Kante> getEdgesWith(final Class<? extends ModelElement> elementClass, final Class<? extends Kante> edgeClass, final int direction) {
        ArrayList<Kante> l_connections = new ArrayList<Kante>();
        for (Kante o_kante : edges) {
            if (edgeClass != null && !edgeClass.isAssignableFrom(o_kante.getClass())) {
                continue;
            }
            if (direction == Doppelkante.FORWARD) {
                if (o_kante.getStart() == this && elementClass.isAssignableFrom(o_kante.getEnd().getClass())) {
                    l_connections.add(o_kante);
                }
            } else if (direction == Doppelkante.BACKWARD) {
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
     * Gibt eine alphabetisch sortierte Liste aller ElementContainer der mit diesem Knoten verbundenen Knoten der Klasse
     * <code>searchElementClass</code>, die in doc enthalten, sind zurueck
     *
     * @param searchElementClass Art der verbundenen Elemente, deren Container geliefert werden sollen
     * @param doc Knoten aus diesem Dokument
     * @return ArrayList mit ElementContainer der gefundenen Knoten
     */
    public final ArrayList<ElementContainer> getConnectedContainer(final Class<? extends ModelElement> searchElementClass, final GraphDocument doc) {
        return getConnectedContainer(searchElementClass, doc, null, Doppelkante.ANY, true);
    }

    /**
     * Gibt eine alphabetisch sortierte Liste aller ElementContainer zurück, die mit diesem Knoten übder die angegebene Kantenart verbundenen sind.
     *
     * @param doc Knoten aus diesem Dokument
     * @param searchEdgeClass Art der zu suchenden verbindungen
     * @return ArrayList mit ElementContainer der gefundenen Knoten
     */
    public final ArrayList<ElementContainer> getConnectedContainer(final GraphDocument doc, final Class<? extends Kante> searchEdgeClass) {
        return getConnectedContainer(ModelElement.class, doc, searchEdgeClass);
    }

    /**
     * @param searchElementClass
     * @param doc
     * @param edgeClass
     * @return
     */
    public final ArrayList<ElementContainer> getConnectedContainer(final Class<? extends ModelElement> searchElementClass, final GraphDocument doc, final Class<? extends Kante> edgeClass) {
        return getConnectedContainer(searchElementClass, doc, edgeClass, Doppelkante.ANY, true);
    }

    /**
     * @param searchElementClass
     * @param doc
     * @param edgeClass
     * @param direction
     * @return
     */
    public final ArrayList<ElementContainer> getConnectedContainer(final Class<? extends ModelElement> searchElementClass, final GraphDocument doc, final Class<? extends Kante> edgeClass, final int direction) {
        return getConnectedContainer(searchElementClass, doc, edgeClass, direction, true);
    }

    /**
     * Gibt eine alphabetisch sotrierte Liste aller ElementContainer der mit diesem Knoten verbundenen Knoten des Klasse searchElementClass, die in
     * doc enthalten, sind zurueck
     *
     * @param searchElementClass Elementklasse deren Objekte zurück gegeben werden sollen
     * @param doc Knoten aus diesem Dokument
     * @param start true = Verbindungen beginnen nicht bei diesem Knoten
     * @param end true = Verbindungen enden nicht bei diesem Knoten
     * @return ArrayList mit ElementContainer der gefundenen Knoten
     */
    @SuppressWarnings("unchecked")
    public final ArrayList<ElementContainer> getConnectedContainer(final Class<? extends ModelElement> searchElementClass, final GraphDocument doc, final Class<? extends Kante> edgeClass, final int direction, final boolean alphabetical) {
        return (ArrayList<ElementContainer>) getConnected(searchElementClass, doc, edgeClass, direction, true, alphabetical);
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
    public final ArrayList<ElementContainer> getPartConnectedContainer(final Class<? extends ModelElement> searchElementClass, final GraphDocument doc) {
        return getPartConnectedContainer(searchElementClass, doc, null, Doppelkante.ANY);
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
    public final ArrayList<ElementContainer> getPartConnectedContainer(final GraphDocument doc, final Class<? extends Kante> searchEdgeClass) {
        return getPartConnectedContainer(ModelElement.class, doc, searchEdgeClass, Doppelkante.ANY);
    }

    /**
     * Liefert eine Liste aller Elemente, die über die angegebene Kantenart mit den direkten und indirekten Teilelementen dieses Elementes verbunden
     * sind. Es wird nur in dem angegebenen <code>GraphDocument</code> gesucht und nur in der angegebenen
     * Richtung der Kante.
     *
     * @param searchElementClass Elementart nach der gesucht werden soll
     * @param doc <code>GraphDocument</code>, in dem nach verbundenen Elementen gesucht wird
     * @param edgeClass Art der Kante, über die Elemente mit den Teilen dieses Elementes verbunden sein sollen
     * @param direction Richtung, die die Kanten haben sollen, über die die verbundenen Elemente gesucht werden
     * @return Liste mit verbundenen <code>ModelElement</code>s
     */
    public final ArrayList<ElementContainer> getPartConnectedContainer(final Class<? extends ModelElement> searchElementClass, final GraphDocument doc, final Class<? extends Kante> edgeClass, final int direction) {
        //Rückgabeliste
        ArrayList<ElementContainer> connected = new ArrayList<ElementContainer>();
        //Liste aller Teile holen (direkte und indirekte)
        for (ModelElement me : getPartElements(false)) {
            //füge zur Rückgabeliste alle über die angegebene Art verbundenen Knoten hinzu
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
    public final ArrayList<ElementContainer> getParentConnectedContainer(final Class<? extends ModelElement> searchElementClass, final GraphDocument doc) {
        return getParentConnectedContainer(searchElementClass, doc, null, Doppelkante.ANY);
    }

    /**
     * Liefert eine Liste aller Elemente, die über die angegebene Kantenart mit den direkten und indirekten Oberelementen dieses Elementes verbunden
     * sind. Es wird nur in dem angegebenen <code>GraphDocument</code> gesucht und nur in der angegebenen
     * Richtung der Kante.
     *
     * @param searchElementClass Elementart nach der gesucht werden soll
     * @param doc <code>GraphDocument</code>, in dem nach verbundenen Elementen gesucht wird
     * @param edgeClass Art der Kante, über die Elemente mit den Teilen dieses Elementes verbunden sein sollen
     * @param direction Richtung, die die Kanten haben sollen, über die die verbundenen Elemente gesucht werden
     * @return Liste mit verbundenen <code>ModelElement</code>s
     */
    public final ArrayList<ElementContainer> getParentConnectedContainer(final Class<? extends ModelElement> searchElementClass, final GraphDocument doc, final Class<? extends Kante> edgeClass, final int direction) {
        //Rückgabeliste
        ArrayList<ElementContainer> connected = new ArrayList<ElementContainer>();
        //für alle Oberelemente
        ArrayList<ModelElement> al = getParentElements(false);

        for (ModelElement me : al) {
            //füge zur Rückgabeliste alle über die angegebene Art verbundenen Knoten hinzu
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
    public ArrayList<ElementContainer> getDirectPartContainer(final GraphDocument doc) {
        Class<? extends PartOfBeziehung>[] hasPartsEdgeClasses = ModelConstants.getHasPartsEdgeClasses(getClass());
        ArrayList<ElementContainer> returnList = new ArrayList<ElementContainer>();
        for (Class<? extends PartOfBeziehung> c : hasPartsEdgeClasses) {
            returnList.addAll(getConnectedContainer(ModelElement.class, doc, c, PartOfBeziehung.PARENT_TO_PART_DIRECTION));
        }
        return returnList;
    }

    /**
     * Liefert eine Liste mit den ElementContainer der direkten Oberelemente im aktuellen <code>GraphDocument</code>
     *
     * @param doc
     * @return Liste mit den Containern der direkten oberelemente
     */
    public ArrayList<ElementContainer> getDirectParentContainer(final GraphDocument doc) {
        Class<? extends PartOfBeziehung>[] isPartEdgeClasses = ModelConstants.getIsPartOfEdgeClasses(getClass());
        ArrayList<ElementContainer> returnList = new ArrayList<ElementContainer>();
        for (Class<? extends PartOfBeziehung> c : isPartEdgeClasses) {
            returnList.addAll(getConnectedContainer(ModelElement.class, doc, c, PartOfBeziehung.PART_TO_PARENT_DIRECTION));
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
     * Liefert ein <code>ArrayList</code> aller <code>ElementContainer</code>, deren Elemente Teil dieses Elementes sind, aber selbst keine Teile
     * besitzen. <br>
     *
     * @return Liste mit <code>ElementContainer</code>n, die die absoluten Kindelemente sind
     */
    public final ArrayList<ElementContainer> getAbsolutePartContainer(final GraphDocument doc) {
        ArrayList<ElementContainer> parts = getDirectPartContainer(doc);
        for (int i = 0; i < parts.size(); i++) {
            ModelElement part = parts.get(i).getElement();
            ArrayList<ElementContainer> partParts = part.getDirectPartContainer(doc);
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
    private final boolean getPartOrParentContainer(final ArrayList<ElementContainer> returnList, GraphDocument doc, final boolean parts, final boolean testonly) {
        if (returnList == null || returnList.size() == 0 || returnList.get(0) == null) {
            return false;
        }
        doc = isUnique() ? doc.getCollection().getMainGraphDocument() : doc;
        ArrayList<ElementContainer> partsOrParents = null;
        if (parts) {
            partsOrParents = returnList.get(returnList.size() - 1).getElement().getDirectPartContainer(doc);
        } else {
            partsOrParents = returnList.get(returnList.size() - 1).getElement().getDirectParentContainer(doc);
        }
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
    public final ArrayList<ModelElement> getPartAndParentElements() {
        HashSet<ModelElement> returnSet = new HashSet<ModelElement>();
        for (ModelElement parent : getParentElements()) {
            returnSet.addAll(parent.getPartElements());
        }
        if (returnSet.size() == 0) {
            returnSet.addAll(getPartElements());
            returnSet.add(this);
        }
        return new ArrayList<ModelElement>(returnSet);
    }

    /**
     * Liefert alle Eltern, Kinder und Geschwister dieses Elementes und das Element selbst. Es werden also alle Elemente gesucht, die mit diesem
     * Element über eine beliebigen Pfad von PartOfVerbindungen zusammenhängen.
     *
     * @param doc <code>GraphDocument</code> in dem gesucht werden soll
     * @return Liste mit <code>ElementContainer</code>n
     */
    public final ArrayList<ElementContainer> getPartAndParentContainer(final GraphDocument doc) {
        ArrayList<ModelElement> partsAndParents = getPartAndParentElements();
        ArrayList<ElementContainer> returnList = new ArrayList<ElementContainer>(partsAndParents.size());
        for (ModelElement me : partsAndParents) {
            ElementContainer ec = me.getContainer(doc);
            if (ec != null) {
                returnList.add(ec);
            }
        }
        return returnList;
    }

    /**
     * @param doc
     * @param addMeAsFirst
     * @return
     */
    private final ArrayList<ElementContainer> getPartOrParentContainer(GraphDocument doc, final boolean addMeAsFirst, final boolean parts) {
        doc = isUnique() ? doc.getCollection().getMainGraphDocument() : doc;
        ArrayList<ElementContainer> al = new ArrayList<ElementContainer>();
        al.add(getContainer(doc));
        getPartOrParentContainer(al, doc, parts, false);
        if (!addMeAsFirst) {
            al.remove(0);
        }
        return al;
    }

    /**
     * Gibt die Parts in Form von <code>ElementContainer</code> zurück.
     *
     * @param doc
     * @param addMeAsFirst
     * @return Eine <code>ArrayList</code> gefüllt mit <code>ElementContainer</code>n.
     */
    public final ArrayList<ElementContainer> getPartContainer(final GraphDocument doc, final boolean addMeAsFirst) {
        return getPartOrParentContainer(doc, addMeAsFirst, true);
    }

    /**
     * Gibt die Parts in Form von <code>ModelElement</code> zurück.
     *
     * @param addMeAsFirst
     * @return Eine <code>ArrayList</code> gefüllt mit <code>ModelElement</code>s.
     */
    public final ArrayList<ModelElement> getPartElements(final boolean addMeAsFirst) {
        if (!addMeAsFirst) {
            return new ArrayList<ModelElement>(getPartElements());
        }
        Set<ModelElement> s = getPartElements();
        ArrayList<ModelElement> returnList = new ArrayList<ModelElement>(s.size() + 1);
        returnList.add(this);
        returnList.addAll(s);
        return returnList;
    }

    /** rekursiv über alle ist-Teil-von-Beziehungen */
    public final HashSet<ModelElement> getParentElements() {
        HashSet<ModelElement> list = new HashSet<ModelElement>();
        getParentElementsRecursive(list);
        return list;
    }

    /**
     * @param result
     */
    private final void getParentElementsRecursive(final Set<ModelElement> result) {
        for (Kante edge : edges) {
            if (!(edge instanceof PartOfBeziehung)) {
                continue;
            }
            ModelElement parent = ((PartOfBeziehung) edge).getParent();
            if (this != parent && result.add(parent)) {
                parent.getParentElementsRecursive(result);
            }
        }
    }

    /** rekursiv über alle ist-Teil-von-Beziehungen */
    public final HashSet<ModelElement> getPartElements() {
        HashSet<ModelElement> list = new HashSet<ModelElement>();
        getPartElementsRecursive(list);
        return list;
    }

    /**
     * @param result
     */
    private final void getPartElementsRecursive(final Set<ModelElement> result) {
        for (Kante edge : edges) {
            if (!(edge instanceof PartOfBeziehung)) {
                continue;
            }
            ModelElement part = ((PartOfBeziehung) edge).getPart();
            if (this != part && result.add(part)) {
                part.getPartElementsRecursive(result);
            }
        }
    }

    /**
     * Liefert die direkten Teilelemente dieses Elements
     *
     * @return
     */
    public final ArrayList<ModelElement> getDirectPartElements() {
        Class<? extends PartOfBeziehung>[] hasPartEdgeClasses = ModelConstants.getHasPartsEdgeClasses(getClass());
        ArrayList<ModelElement> returnList = new ArrayList<ModelElement>();
        for (Class<? extends PartOfBeziehung> c : hasPartEdgeClasses) {
            returnList.addAll(getConnectedElements(ModelElement.class, c, Doppelkante.BACKWARD));
        }
        return returnList;
    }

    /**
     * Liefert die direkten Oberelemente dieses Elements
     *
     * @return
     */
    public final ArrayList<ModelElement> getDirectParentElements() {
        Class<? extends PartOfBeziehung>[] isPartEdgeClasses = ModelConstants.getIsPartOfEdgeClasses(getClass());
        ArrayList<ModelElement> returnList = new ArrayList<ModelElement>();
        for (Class<? extends PartOfBeziehung> c : isPartEdgeClasses) {
            returnList.addAll(getConnectedElements(ModelElement.class, c, Doppelkante.FORWARD));
        }
        return returnList;
    }

    /**
     * Gibt die Parents in Form von <code>ElementContainer</code> zurück.
     *
     * @param doc
     * @param addMeAsFirst
     * @return Eine <code>ArrayList</code> gefüllt mit <code>ElementContainer</code>n.
     */
    public final ArrayList<ElementContainer> getParentContainer(final GraphDocument doc, final boolean addMeAsFirst) {
        return getPartOrParentContainer(doc, addMeAsFirst, false);
    }

    /**
     * Gibt die Parents in Form von <code>ModelElement</code> zurück.
     *
     * @param doc
     * @param addMeAsFirst
     * @return Eine <code>ArrayList</code> gefüllt mit <code>ModelElement</code>s.
     */
    public final ArrayList<ModelElement> getParentElements(final boolean addMeAsFirst) {
        if (!addMeAsFirst) {
            return new ArrayList<ModelElement>(getParentElements());
        }
        Set<ModelElement> s = getParentElements();
        ArrayList<ModelElement> returnList = new ArrayList<ModelElement>(s.size() + 1);
        returnList.add(this);
        returnList.addAll(s);
        return returnList;
    }

    /**
     * Gibt alle absoluten Teilelemente zurück
     *
     * @return
     */
    public final HashSet<ModelElement> getAbsolutePartElements() {
        HashSet<ModelElement> returnSet = new HashSet<ModelElement>(1);
        HashSet<ModelElement> parts = new HashSet<ModelElement>();
        parts.add(this);
        while (parts.size() > 0) {
            HashSet<ModelElement> partParts = new HashSet<ModelElement>();
            for (ModelElement part : parts) {
                ArrayList<ModelElement> pParts = part.getDirectPartElements();
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
        ArrayList<ElementContainer> al = new ArrayList<ElementContainer>();
        al.add(getContainer(doc));
        return getPartOrParentContainer(al, doc, true, true);
    }

    /** rekursiv über alle ist-Teil-von-Beziehungen */
    public final boolean isParentOf(final ModelElement me) {
        return this.getPartElements().contains(me);
    }

    /** rekursiv über alle ist-Teil-von-Beziehungen */
    public final boolean isPartOf(final ModelElement me) {
        return me.getPartElements().contains(this);
    }

    /**
     * Liefert <code>true</code>, wenn <code>this</code> und <code>me</code> direkt über eine <code>PartOfBeziehung</code> verbunden sind und
     * <code>this</code> ein Teil von <code>me</code> ist.
     *
     * @param me
     * @return
     */
    public final boolean isDirectPartOf(final ModelElement me) {
        return me.isDirectParentOf(this);
    }

    /**
     * Liefert <code>true</code>, wenn <code>me</code> und <code>this</code> direkt über eine <code>PartOfBeziehung</code> verbunden sind und
     * <code>me</code> ein Teil von <code>this</code> ist.
     *
     * @param me
     * @return
     */
    public final boolean isDirectParentOf(final ModelElement me) {
        for (Kante edge : edges) {
            if (!(edge instanceof PartOfBeziehung)) {
                continue;
            }
            if (((PartOfBeziehung) edge).getPart() == me) {
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
     * Füllt die übergebene Liste <code>returnList</code> mit allen hierarschich verbundenen Elementen.
     *
     * @param returnList Liste mit <code>ElementContainer</code>n
     * @param doc (Teil-)Modell in dem gesucht werden soll
     * @param parts Wenn <code>true</code> wird nach allen Teilen gesucht, sonst nach allen Oberelementen
     * @param testonly Wenn <code>true</code> wird beim ersten gefundenen Element abgerochen und <code>true</code> zurück gegeben
     * @return <code>true</code>, wenn mind. ein Element gefunden wurde, das in die Rückgabeliste gehört / Braucht man vielleicht und sollte in
     *         Analogie zu den Part-OfBeziehungen (oder irgendwie über dieselbe Funktion gemacht werden private final boolean
     *         getCompositionMasterOrSlaveContainer(ArrayList<ElementContainer> returnList, GraphDocument doc, boolean slave, boolean testonly) { if
     *         ((returnList == null) || (returnList.size() == 0) || (returnList.get(0) == null)) return false; doc =
     *         (isUnique() ? doc.getCollection().getGraphDocument() : doc); ArrayList<ElementContainer> masterOrSlaves = null; if (slave)
     *         masterOrSlaves = returnList.get(returnList.size()-1).getElement().getDirectCompositionSlaveContainer(doc); else
     *         masterOrSlaves = returnList.get(returnList.size()-1).getElement().getDirectCompositionMasterContainer(doc); for (int i = 0; i <
     *         masterOrSlaves.size(); i++) { boolean found = false; ElementContainer ms = masterOrSlaves.get(i); if
     *         (returnList.contains(ms)){ found = true; if (testonly) return true; break; } if (!found) { returnList.add(ms);
     *         getPartOrParentContainer(returnList, doc, parts, testonly); } } return false; }
     */

    /**
     * COMMENTME
     *
     * @return
     */
    public final ArrayList<? extends ModelElement> getDirectCompositionSlaveElements() {
        ArrayList<ModelElement> retVal = new ArrayList<ModelElement>();
        for (Kante edge : getEdges()) {
            if (edge instanceof Composition) {
                Composition comp = (Composition) edge;
                if (comp.getMaster() == this) {
                    retVal.add(comp.getSlave());
                }
            }
        }
        return retVal;
    }

    /**
     * Liefert eine Liste aller Masterelemente dieses Elementes, also aller Elemente, die mit diesem Element über eine {@link Composition} verbunden
     * sind, wobei das verbundene Element diesem Element übergeordnet ist.
     *
     * @return
     */
    public final ArrayList<? extends ModelElement> getDirectCompositionMasterElements() {
        //meistens ist es genau ein Master
        ArrayList<ModelElement> retVal = new ArrayList<ModelElement>(1);
        for (Kante edge : getEdges()) {
            if (edge instanceof Composition) {
                Composition comp = (Composition) edge;
                if (comp.getSlave() == this) {
                    retVal.add(comp.getMaster());
                }
            }
        }
        return retVal;
    }

    /**
     * Liefert eine Liste aller Container der Slaveelemente dieses Elementes, also aller Elemente, die mit diesem Element über eine
     * {@link Composition} verbunden sind, wobei das verbundene Element diesem Element übergeordnet ist.
     *
     * @param doc {@link GraphDocument} in dem die Container liegen sollen
     * @return
     */
    public final ArrayList<ElementContainer> getDirectCompositionSlaveContainer(final GraphDocument doc) {
        return getContainer(getDirectCompositionSlaveElements(), doc);
    }

    /**
     * Liefert eine Liste aller Container der Masterelemente dieses Elementes, also aller Elemente, die mit diesem Element über eine
     * {@link Composition} verbunden sind, wobei das verbundene Element diesem Element übergeordnet ist.
     *
     * @param doc {@link GraphDocument} in dem die Container liegen sollen
     * @return
     */
    public final ArrayList<ElementContainer> getDirectCompositionMasterContainer(final GraphDocument doc) {
        return getContainer(getDirectCompositionMasterElements(), doc);
    }

    /**
     * Liefert eine Liste mit allen Containern, die die übergebenen Elemente im übergebenen {@link GraphDocument} haben. Wenn ein Element der
     * übergebenen Collection keinen Container im {@link GraphDocument} hat, dann wird auch kein Eintrag in der
     * Rückgabeliste hinzugefügt. Die Rückgabeliste kann also kleiner sein, als die übergebene Liste.
     *
     * @param elements
     * @param doc
     * @return
     */
    public static final ArrayList<ElementContainer> getContainer(final ArrayList<? extends ModelElement> elements, final GraphDocument doc) {
        ArrayList<ElementContainer> container = new ArrayList<ElementContainer>(elements.size());
        for (ModelElement me : elements) {
            container.add(me.getContainer(doc));
        }
        return container;
    }

    //	public final ArrayList<? extends ModelElement> getCompositionSlaveElements(){
    //		return null;
    //	}
    //
    //	public final ArrayList<? extends ModelElement> getCompositionMasterElements(){
    //		return null;
    //	}
    //
    //	public final ArrayList<ElementContainer> getCompositionSlaveContainer(GraphDocument doc){
    //		return null;
    //	}
    //
    //	public final ArrayList<ElementContainer> getCompositionMasterContainer(GraphDocument doc){
    //		return null;
    //	}

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
    public final ArrayList<ModelElement> getConnectedElements(final Class<? extends ModelElement> searchElementClass) {
        return getConnectedElements(searchElementClass, (Class<? extends Kante>) null);
    }

    /**
     * Gibt alle mit diesem <code>ModelElement</code> verbundenen <code>ModelElement</code>s des Klasse <code>searchElementClass</code> zurueck.
     *
     * @param searchElementClass
     * @param edgeClass
     * @return
     */
    public final ArrayList<ModelElement> getConnectedElements(final Class<? extends ModelElement> searchElementClass, final Class<? extends Kante> edgeClass) {
        return getConnectedElements(searchElementClass, edgeClass, Doppelkante.ANY);
    }

    /**
     * Gibt alle mit diesem <code>ModelElement</code> verbundenen <code>ModelElement</code>s des Klasse <code>searchElementClass</code> zurueck.
     *
     * @param searchElementClass
     * @param edgeClass
     * @param direction
     * @return
     */
    public final ArrayList<ModelElement> getConnectedElements(final Class<? extends ModelElement> searchElementClass, final Class<? extends Kante> edgeClass, final int direction) {
        return getConnectedElements(searchElementClass, edgeClass, direction, false);
    }

    /**
     * Gibt alle mit diesem <code>ModelElement</code> verbundenen <code>ModelElement</code>s des Klasse <code>searchElementClass</code> zurueck.
     *
     * @param searchElementClass
     * @param edgeClass
     * @param direction
     * @param alphabetical
     * @return ArrayList mit allen verbundenen Knoten
     */
    public final ArrayList<ModelElement> getConnectedElements(final Class<? extends ModelElement> searchElementClass, final Class<? extends Kante> edgeClass, final int direction, final boolean alphabetical) {
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
     * @return ArrayList mit allen verbundenen <code>ModelElement</code>s oder <code>ElementContainer</code>n
     */
    @SuppressWarnings("unchecked")
    public final ArrayList<ModelElement> getConnectedElements(final Class<? extends ModelElement> searchElementClass, final GraphDocument doc, final Class<? extends Kante> edgeClass, final int direction, final boolean alphabetical) {
        return (ArrayList<ModelElement>) getConnected(searchElementClass, doc, edgeClass, direction, false, alphabetical);
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
     * @return ArrayList mit allen verbundenen <code>ModelElement</code>s oder <code>ElementContainer</code>n
     */
    private final ArrayList<?> getConnected(final Class<? extends ModelElement> searchElementClass, final GraphDocument doc, final Class<? extends Kante> edgeClass, final int direction, final boolean container, final boolean alphabetical) {
        ArrayList<Object> knoten = new ArrayList<Object>(edges.size());

        if (doc == null && container) {
            System.err.println("Can't find ElementContainer with an null-GraphDocument");
            return knoten;
        }

        for (Kante t : edges) {
            if (!(t instanceof Doppelkante)) {
                Log.show(Log.ERROR, Tool3lgmConstants.getErrString("inkons_modelldaten_bei") + " " + t.getHashString());
                continue;
            }
            Doppelkante edge = (Doppelkante) t;

            if (edgeClass != null && !edgeClass.isAssignableFrom(edge.getClass())) {
                continue;
            }

            ModelElement knot = null;
            switch (direction) {
            case Doppelkante.ANY:
                knot = edge.getEnd() == this ? edge.getStart() : edge.getEnd();
                break;
            case Doppelkante.FORWARD:
                switch (edge.getDirection()) {
                case Doppelkante.FORWARD:
                    knot = edge.getEnd() == this ? null : edge.getEnd();
                    break;
                case Doppelkante.BACKWARD:
                    knot = edge.getEnd() == this ? edge.getStart() : null;
                    break;
                case Doppelkante.DOUBLE:
                    knot = edge.getEnd() == this ? edge.getStart() : edge.getEnd();
                    break;
                }
                break;
            case Doppelkante.BACKWARD:
                switch (edge.getDirection()) {
                case Doppelkante.FORWARD:
                    knot = edge.getEnd() == this ? edge.getStart() : null;
                    break;
                case Doppelkante.BACKWARD:
                    knot = edge.getEnd() == this ? null : edge.getEnd();
                    break;
                case Doppelkante.DOUBLE:
                    knot = edge.getEnd() == this ? edge.getStart() : edge.getEnd();
                    break;
                }
                break;
            case Doppelkante.DOUBLE:
                if (edge.getDirection() == Doppelkante.DOUBLE) {
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
     * @return ArrayList mit den verbundenen ModelElementen
     */
    public ArrayList<ModelElement> getConnectedElementsByEdge(final Class<? extends Kante> edgeClass) {
        return getConnectedElementsByEdge(Kante.getOther(edgeClass, getClass()), edgeClass);
    }

    /**
     * Gibt alle <code>ModelElement</code>s zurück, die mit diesem <code>ModelElement</code> im angegebenen <code>GraphDocument</code> verbunden sind.
     *
     * @param edgeClass alle Kanten des Types
     * @param targetElementClass Klasse, von der die Zielelemente sein sollen. Diese muss nicht mit der letzten Elementklasse des Metapfades
     *            übereinstimmen, sondern kann eine spezielle Unterklasse sein.
     * @return ArrayList mit den verbundenen ModelElementen
     */
    public ArrayList<ModelElement> getConnectedElementsByEdge(final Class<? extends ModelElement> targetElementClass, final Class<? extends Kante> edgeClass) {
        ArrayList<ModelElement> retVal = new ArrayList<ModelElement>(edges.size());
        for (Kante edge : edges) {
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
    public final ArrayList<Kante> getEdges(final Class<? extends Kante> edgeClass) {
        ArrayList<Kante> returnList = new ArrayList<Kante>(edges.size());
        for (Kante edge : edges) {
            if (edgeClass.isAssignableFrom(edge.getClass())) {
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
    public final int countConnections(final Class<? extends Kante> edgeClass) {
        return countConnections(edgeClass, Doppelkante.ANY);
    }

    /**
     * Counts the edges
     *
     * @param edgeClass Type of edges to count
     * @param direction Direction of the edge in relation to this
     * @return Number of edges with the specified type
     */
    private final int countConnections(final Class<? extends Kante> edgeClass, final int direction) {
        int retVal = 0;
        for (Kante edge : edges) {
            if (edgeClass.isAssignableFrom(edge.getClass())) {
                if (direction == Doppelkante.FORWARD) {
                    if (edge.isStart(this)) {
                        retVal++;
                    }
                } else if (direction == Doppelkante.BACKWARD) {
                    if (edge.isEnd(this)) {
                        retVal++;
                    }
                } else {
                    retVal++;
                }
            }
        }
        return retVal;
    }

    /**
     * Counts the edges starting by this
     *
     * @param edgeClass Type of edges to count
     * @return Number of edges with the specified type
     */
    public final int countConnectionsFromThis(final Class<? extends Kante> edgeClass) {
        return countConnections(edgeClass, Doppelkante.FORWARD);
    }

    /**
     * Counts the edges ending by this
     *
     * @param edgeClass Type of edges to count
     * @return Number of edges with the specified type
     */
    public final int countConnectionsToThis(final Class<? extends Kante> edgeClass) {
        return countConnections(edgeClass, Doppelkante.BACKWARD);
    }

    /**
     * Reicht die selbe Methode die von <code>Tool3lgmConstants</code> einfach nur durch.
     *
     * @param key
     * @return
     */
    protected String getResString(final String key) {
        return Tool3lgmConstants.getResString(key);
    }

    /**
     * COMMENTME
     */
    @SuppressWarnings("unchecked")
    protected static final Class<ModelElement>[] EMPTY_CLASS_ARRAY = new Class[0];

    /**
     * Gibt zurück, welche Knoten zusätzlich mit einem Knoten zu kopieren sind
     */
    public Class<? extends ModelElement>[] getCopyDependencies() {
        return EMPTY_CLASS_ARRAY;
    }

    /**
     * @return <code>true</code>, wenn das Elemente alle Kanten in ausreichender Anzahl hat, die es haben muss
     *         (= Kanten, bei denen die minimale Kardinalität > 0 ist)
     */
    public boolean isConsistent() {
        Class<? extends ModelElement> meClass = getClass();
        Class<? extends Kante>[] edgeTypes = ModelConstants.getEdgeTypes(meClass);
        //für alle Kantenarten dieser ModelElement-Klasse
        for (Class<? extends Kante> edgeType : edgeTypes) {
            //minimale Kardinalität wird erst einmal als 0 angenommen
            int minCardinality = 0;
            //wenn diese Elementart Startklasse der aktuellen Kantenart ist
            if (Kante.isStartClass(edgeType, meClass)) {
                //minimale Kardinalität zur Endklasse holen
                minCardinality = Kante.getMinStartToEndCardinality(edgeType);
                //wenn diese minimale Kardinalität > 0 ist, aber dieses Element weniger Kanten zu anderen Elementen hat, als nötig
                if (minCardinality > 0 && getEdgesTo(ModelElement.class, edgeType).size() < minCardinality) {
                    //nicht konsistent
                    return false;
                }
            }
            //OHNE ELSE-IF! Wenn diese Elementart Endklasse der aktuellen Kantenart ist
            if (Kante.isEndClass(edgeType, meClass)) {
                //minimale Kardinalität zur Startklasse holen
                minCardinality = Kante.getMinEndToStartCardinality(edgeType);
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
     * Ueberprueft, ob der Knoten zur Menge der UNIQUE_KNOTS gehoert, ob er also ein Knoten ist, der im gesamten Modell nur einmal vorkommt
     */
    public boolean isUnique() {
        return ModelConstants.isUnique(getClass());
    }

    /**
     * Ueberprueft, ob der Knoten ein untergeodnetes Element ist.
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
        return GraphViewConstants.isUnpaintable(getClass());
    }

    /**
     * @return
     */
    public final boolean isPaintable() {
        return !isUnpaintable();
    }

    /**
     * @return
     */
    public abstract boolean hasLayout();

    /**
     * @return
     */
    public abstract boolean hasSortedKanten();

    /**
     * @return <code>true</code>, wenn die Elementart eine PartOfBeziehung hat
     */
    public final boolean canHaveParts() {
        return ModelConstants.getHasPartsEdgeClasses(getClass()).length > 0;
    }

    /**
     * @return <code>true</code>, wenn die Elementart eine PartOfBeziehung hat
     */
    public final boolean canHaveParents() {
        return ModelConstants.getIsPartOfEdgeClasses(getClass()).length > 0;
    }

    /**
     * Testet, ob das Modelelement this in der Richtung direction zu dem ModelElement eine Kante haben kann
     *
     * @param me
     * @param direction {@link Doppelkante}.FORWARD, {@link Doppelkante}.BACKWARD
     * @param testCardinality wenn <code>true</code>, wird auch noch getestet, ob die maximale Kardinalität der Verbindungen bereits erreicht ist
     * @return
     */
    public final boolean isForwardLinkable(final ModelElement me, final Class<? extends Kante> edgeClass, final boolean testCardinality) {
        //wenn die Kante die beiden Elemente nicht in Vorwärtsrichtung verbinden kann
        if (!Kante.isConnectingForward(edgeClass, getClass(), me.getClass())) {
            return false;
        }
        //Wenn es sich bei dieser Kantenart nicht um eine mehrfach zwischend denselben Elementen anlgebare Kante handelt
        if (!ModelConstants.isMultipleEdgeClass(edgeClass)) {
            //wenn schon eine solche Kante zwischen den beiden Elementen existiert
            ArrayList<Kante> edges = getEdgesTo(me, edgeClass);
            if (edges != null && edges.size() > 0) {
                return false;
            }
        }
        //wenn es eine PartOfVerbindung ist -> this darf kein Teil von me sein
        if (PartOfBeziehung.class.isAssignableFrom(edgeClass)) {
            //wenn die beiden Elemente bereits in einer PartOfVerbindung stehen, die der jetzt zu verknüpfenden Richtung widerspricht
            if (me.isPartOf(this)) {
                return false;
            }
        }
        //wenn das Überschreiten der Kardinalität geprüft werden soll
        if (testCardinality) {
            //für das Startelement ist die maximale Verbindungsanzahl bereits erreicht?
            if (Kante.getMaxStartToEndCardinality(edgeClass) <= countConnections(edgeClass)) {
                return false;
            }
            //für das Endelement ist die maximale Verbindungsanzahl bereits erreicht?
            if (Kante.getMaxEndToStartCardinality(edgeClass) <= me.countConnections(edgeClass)) {
                return false;
            }
        }
        return true;

    }

    //	/**
    //	 * Testet, ob das Modelelement this in der Richtung direction zu dem
    //	 * ModelElement eine Kante haben kann
    //	 *
    //     * @param me
    //     * @param direction
    //     * 		{@link Doppelkante}.FORWARD, {@link Doppelkante}.BACKWARD
    //     * @return
    //     */
    //    public final boolean isLinkable(ModelElement me, int direction) {
    //		Class<? extends Kante>[] edgeClasses = ModelConstants.getEdgeTypes(getClass(), me.getClass());
    //
    //		if (edgeClasses == null)
    //			return false;
    //
    //		if (edgeClasses.length < 1)
    //			return false;
    //
    //		if (edgeClasses.length > 1)
    //			return true;
    //
    //		Doppelkante edge;
    //		for (int i = 0; i < getEdgesCount(); i++) {
    //			edge = (Doppelkante) getEdge(i);
    //			if ((direction != Doppelkante.BACKWARD) && edge.isDirecting(this, me))
    //				return false;
    //			if ((direction != Doppelkante.FORWARD) && edge.isDirecting(me, this))
    //				return false;
    //		}
    //
    //		try {
    //			edge = (Doppelkante) edgeClasses[0].newInstance();
    //			if (edge instanceof PartOfBeziehung) {
    //				if (direction == Doppelkante.FORWARD && isParentOf(me))
    //					return false;
    //				if (direction == Doppelkante.BACKWARD && isPartOf(me))
    //					return false;
    //
    //				if (edge.isStartClass(getClass()) && edge.getMaxStartToEndCardinality()<=countConnections(edge.getClass()))
    //					return false;
    //				if (edge.isEndClass(me.getClass()) && edge.getMaxEndToStartCardinality()<=me.countConnections(edge.getClass()))
    //					return false;
    //			}
    //		} catch (Exception exp) {
    //			Log.show(Log.ERROR, Tool3lgmConstants.getErrString("FehlerAllgemein"), exp);
    //			return false;
    //		}
    //		return true;
    //	}

    /**
     * @return {@link Hashtable} aller Container dieses Elementes
     */
    public Hashtable<GraphDocument, ElementContainer> getContainerTable() {
        return containerTable;
    }

    /**
     * Liefert das Modell, in dem dieses Element vorkommt oder <code>null</code>.
     *
     * @return
     */
    public final GDCollection getCollection() {
        Iterator<GraphDocument> docIt = getContainerTable().keySet().iterator();
        if (!docIt.hasNext()) {
            return null;
        }
        return docIt.next().getCollection();
    }

    /**
     * @author Thomas Rudert sollte auf true gesetzt werden, wenn beim Kopieren kein Duplikat erstellte werden soll, falls das Modelelement nur durch
     *         aufgeloeste copyDependencies mitkopiert wird <br>
     *         (Bsp: kopieren von PhyDvBausteinen soll der Standort nicht doppelt vorhanden sein)
     */
    public boolean avoidDuplicates() {
        return false;
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

        String joined = Tool3lgmConstants.getResString("joined");
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
        htmlName = HTMLConverter.getHTMLString(name).replaceAll("&#10;", "<BR>").replaceAll("\\\\&#45;", "-<BR>");
        refreshText();

        //UserFields zusammenführen. Bei allen UserFIelds, bei denen nur ein Element einen Wert hat oder sich die Werte nicht
        //unterscheiden, nimm nur einen gültigen Wert. Haben beide einen unterschiedlichen Wert, führe sie String-technisch zusammen
        HashSet<UserField> allKeys = new HashSet<UserField>(getUserFieldInputValueKeys());
        allKeys.addAll(other.getUserFieldInputValueKeys());

        for (UserField keyUserField : allKeys) {
            String value = getUserFieldInputValue(keyUserField);
            String otherValue = other.getUserFieldInputValue(keyUserField);
            if (otherValue != UserField.EMPTY_STRING) {
                if (value == UserField.EMPTY_STRING) {
                    setUserFieldInputValue(keyUserField, otherValue);
                    //wenn es einen otherValue gibt, der sich vom value unterscheidet -> füge sie zusammen
                } else if (!value.equals(otherValue)) {
                    //Bei Kennzahlen bleibt es einfach der Wert des ersten Elements
                    if (!keyUserField.isClassificationUserField()) {
                        setUserFieldInputValue(keyUserField, value.toString().concat(" -" + joined + "- ").concat(otherValue.toString()));
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