package de.imise.tool3lgm.graphtools.model;

import java.util.Collection;
import java.util.List;

import de.imise.tool3lgm.graphtools.metamodel.MetaModel;
import de.imise.tool3lgm.graphtools.metamodel.ModelConstants;
import de.imise.tool3lgm.graphtools.metamodel.elements.Edge;
import de.imise.tool3lgm.graphtools.metamodel.elements.ModelElement;
import de.imise.tool3lgm.graphtools.view.container.EdgeContainer;
import de.imise.tool3lgm.graphtools.view.container.ElementContainer;
import de.imise.tool3lgm.graphtools.view.container.LayerContainer;
import de.imise.tool3lgm.graphtools.view.container.NodeContainer;
import de.imise.util.Sys;

public class GDCollectionPrinter {

    private final GDCollection gdcoll;

    private final StringBuilder sb = new StringBuilder();

    private int indent = 0;

    public GDCollectionPrinter(final GDCollection gdcoll) {
        this(gdcoll, false);
    }

    public GDCollectionPrinter(final GDCollection gdcoll, final boolean appendMainModelInformation) {
        this.gdcoll = gdcoll;
        appendln("Model name=", gdcoll.getName() + " (" + gdcoll.getModelCategory().name() + ")");
        if (appendMainModelInformation) {
            appendMainModelInformation();
        }
    }

    private StringBuilder appendMainModelInformation() {
        increaseIndent();
        appendGraphDocument(gdcoll.getMainDoc());
        for (Szenario szen : gdcoll.getSzenarios()) {
            appendGraphDocument(szen);
        }
        resetIndent();
        appendln();
        return sb;
    }

    private StringBuilder appendGraphDocument(final GraphDocument doc) {
        append(doc.getClass().getSimpleName()).append(": ").append(doc);
        appendln();
        for (int layerIndex : ModelConstants.LAYERS) {
            LayerContainer lc = doc.getLayer(layerIndex);
            appendln("Layer ", layerIndex, "    Knoten: ", lc.getNodeContainerCount(), "    Kanten: ", lc.getEdgeContainerCount(), "    Knickpunkte: ", lc.getBendpointContainerCount());
            increaseIndent();
            appendln("Knoten");
            increaseIndent();
            for (NodeContainer nc : lc.getNodeContainersAlphabetical()) {
                appendElementContainer(nc);
            }
            decreaseIndent();
            appendln("Kanten");
            increaseIndent();
            for (EdgeContainer ec : lc.getEdgeContainers()) {
                appendElementContainer(ec);
            }
            decreaseIndent();
            decreaseIndent();
        }
        resetIndent();
        return sb;
    }

    private StringBuilder appendElementContainer(final ElementContainer ec) {
        ModelElement me = ec.getElement();
        appendln(me.getClass().getSimpleName(), ": ", me);
        increaseIndent();
        appendIndent();
        append("ID=", me.getID(), "    idMe=", System.identityHashCode(me), "\tidCont=", System.identityHashCode(ec));
        if (me instanceof Edge) {
            Edge edge = (Edge) me;
            ModelElement start = edge.getStart();
            String startID = start == null ? "null" : start.getID();
            ModelElement end = edge.getEnd();
            String endID = end == null ? "null" : end.getID();
            append("\tstartID=", startID, "\tendID=", endID);
        }
        appendln();
        appendEdgesOfElement(me);
        decreaseIndent();
        return sb;
    }

    private StringBuilder appendEdgesOfElement(final ModelElement me) {
        for (Edge edge : me.getEdges()) {
            appendln("--> ", edge.getClass().getSimpleName(), ": ", edge);
            increaseIndent();
            //appendIndent();
            ModelElement start = edge.getStart();
            String startID = start == null ? "null" : start.getID();
            ModelElement end = edge.getEnd();
            String endID = end == null ? "null" : end.getID();
            appendln("ID=", edge.getID(), "    idEdge=", System.identityHashCode(edge), "\tstartHash=", startID, "\tendHash=", endID);
            decreaseIndent();
        }
        return sb;
    }

    private StringBuilder appendln() {
        sb.append("\n");
        return sb;
    }

    private StringBuilder append(final Object... objects) {
        for (Object o : objects) {
            sb.append(o);
        }
        return sb;
    }

    private StringBuilder appendln(final Object... objects) {
        appendIndent();
        append(objects);
        appendln();
        return sb;
    }

    private StringBuilder appendIndent() {
        for (int i = 0; i < indent; i++) {
            sb.append("\t");
        }
        return sb;
    }

    private int increaseIndent() {
        indent++;
        return indent;
    }

    private int decreaseIndent() {
        indent--;
        return indent;
    }

    private int resetIndent() {
        indent = 0;
        return indent;
    }

    private void removeNewLine() {
        int length = sb.length();
        if (length > 0) {
            if (sb.charAt(length - 1) == '\n') {
                sb.setLength(length - 1);
            }
        }
    }

    @Override
    public String toString() {
        return sb.toString();
    }

    public static final void print(final GDCollection gdcoll) {
        Sys.outm(1, 1, new GDCollectionPrinter(gdcoll, true));
    }

    public static final void print(final GDCollectionOwner gdcollOwner) {
        GDCollection gdcoll = gdcollOwner.getCollection();
        Sys.outn(2, new GDCollectionPrinter(gdcoll, true));
    }

    @SafeVarargs
    public static void printElements(final GDCollection gdcoll, final Class<? extends ModelElement>... elementClasses) {
        String[] simpleElementClassNames = new String[elementClasses.length];
        for (int i = 0; i < simpleElementClassNames.length; i++) {
            simpleElementClassNames[i] = elementClasses[i].getSimpleName();
        }
        GDCollectionPrinter printer = getPrinter(gdcoll.getMainDoc(), simpleElementClassNames);
        Sys.outn(2, printer);
    }

    /**
     * @param gdcoll
     * @param simpleElementClassNames
     */
    public static void printElements(final GDCollection gdcoll, final String... simpleElementClassNames) {
        GDCollectionPrinter printer = getPrinter(gdcoll.getMainDoc(), simpleElementClassNames);
        Sys.outn(2, printer);
    }

    /**
     * @param doc
     * @param simpleElementClassNames
     */
    public static void printElements(final GraphDocument doc, final String... simpleElementClassNames) {
        GDCollectionPrinter printer = getPrinter(doc, simpleElementClassNames);
        Sys.outn(2, printer);
    }

    /**
     * @param doc
     * @param simpleElementClassNames
     * @return
     */
    public static GDCollectionPrinter getPrinter(final GraphDocument doc, final String... simpleElementClassNames) {
        MetaModel metaModel = doc.getMetaModel();
        GDCollection gdcoll = doc.getCollection();
        GDCollectionPrinter printer = new GDCollectionPrinter(gdcoll);
        printer.removeNewLine();
        printer.appendln(" (Sub-)Model: " + doc);
        for (String elementClassName : simpleElementClassNames) {
            for (Class<? extends ModelElement> elementClass : metaModel.allModelElementClassesWithSuperClasses) {
                String simpleClassName = elementClass.getSimpleName();
                if (simpleClassName.equals(elementClassName)) {
                    Collection<Class<? extends ModelElement>> instanciableElementClasses = metaModel.getInstanciableAssignableClasses(elementClass);
                    for (Class<? extends ModelElement> instanciableElementClass : instanciableElementClasses) {
                        List<ElementContainer> elements = doc.getElementContainers(instanciableElementClass);
                        for (ElementContainer ec : elements) {
                            printer.appendElementContainer(ec);
                        }
                    }
                }
            }
        }
        return printer;
    }

}
