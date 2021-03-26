package de.imise.tool3lgm.graphtools.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import de.imise.tool3lgm.graphtools.metamodel.MetaModel;
import de.imise.tool3lgm.graphtools.metamodel.elements.Bendpoint;
import de.imise.tool3lgm.graphtools.metamodel.elements.Edge;
import de.imise.tool3lgm.graphtools.metamodel.elements.InferenceEdge;
import de.imise.tool3lgm.graphtools.metamodel.elements.ModelElement;
import de.imise.tool3lgm.graphtools.metamodel.elements.Node;
import de.imise.tool3lgm.graphtools.path.metapaths.ElementaryMetaPath;
import de.imise.tool3lgm.graphtools.userfield.definition.UserField;
import de.imise.tool3lgm.graphtools.userfield.definition.UserFieldNumberFormat;
import de.imise.tool3lgm.graphtools.view.container.BendpointContainer;
import de.imise.tool3lgm.graphtools.view.container.EdgeContainer;
import de.imise.tool3lgm.graphtools.view.container.ElementContainer;
import de.imise.tool3lgm.graphtools.view.container.LayerContainer;
import de.imise.tool3lgm.graphtools.view.container.NodeContainer;

/**
 * @author AXS (30.03.2020)
 */
public class CopyDependencyResolver {

    /**
     * @author AXS (17.03.2021)
     */
    public static class CopyDependencyResolverResultSimple {

        /**  */
        public final List<ModelElement> elements;

        /**  */
        public final Set<UserField> userFields;

        /**  */
        public final Set<UserFieldNumberFormat> userFieldNumberFormats;

        /**  */
        public CopyDependencyResolverResultSimple() {
            elements = new ArrayList<>();
            userFields = new HashSet<>();
            userFieldNumberFormats = new HashSet<>();
        }

        /**
         * @param me
         * @return
         */
        public boolean add(final ModelElement me) {
            if (contains(me)) {
                return false;
            }
            Set<UserField> elementUserFields = me.getUserFieldInputValueKeys();
            userFields.addAll(elementUserFields);
            for (UserField userField : elementUserFields) {
                UserFieldNumberFormat numberFormat = userField.getNumberFormat();
                if (numberFormat != null) {
                    userFieldNumberFormats.add(numberFormat);
                }
            }
            return elements.add(me);
        }

        /**
         * @param me
         * @return
         */
        public boolean contains(final ModelElement me) {
            return elements.contains(me);
        }

        /**
         * @return the count of all elements
         */
        public int size() {
            return elements.size();
        }
    }

    /**
     * @author AXS (17.03.2021)
     */
    public static class CopyDependencyResolverResultFull extends CopyDependencyResolverResultSimple {

        /**  */
        public final List<? extends GraphDocument> export;

        /**  */
        public final Set<String> iconIDs;

        /**
         * @param export
         */
        public CopyDependencyResolverResultFull(final List<? extends GraphDocument> export) {
            this.export = export;
            iconIDs = new HashSet<>();
        }

        /**
         * @param nc
         * @return
         */
        public boolean add(final NodeContainer nc) {
            if (!add((ElementContainer) nc)) {
                return false;
            }
            String iconName = nc.getIconID();
            if (iconName != null) {
                iconIDs.add(iconName);
            }
            return true;
        }

        /**
         * @param ec
         * @return
         */
        public boolean add(final ElementContainer ec) {
            if (ec == null) {
                return false;
            }
            ModelElement me = ec.getElement();
            return add(me);
        }

    }

    /**
     * Sucht alle Element und Icons, die kopiert werden müssen
     *
     * @param export Array von Szenarios, die zu kopieren sind
     * @param elements Set, in welches die zu kopierenden Element geschrieben
     *            werden
     * @param bitmaps Set, in welches die IDs der zu kopierenden Icons
     *            geschrieben werden
     * @param userFields Set, in welches die zu kopierenden benutzdefinierten
     *            Eigenschaftsfelder geschrieben werden
     */
    public static CopyDependencyResolverResultFull resolveCopyDependencies(final List<? extends GraphDocument> export) {
        Set<GDCollection> models2Export = new HashSet<>(); //usually the GDCollection of all GraphDocuments in export is the same, but this is the general way
        for (GraphDocument doc : export) {
            models2Export.add(doc.getCollection());
        }
        CopyDependencyResolverResultFull result = new CopyDependencyResolverResultFull(export);
        for (GDCollection gdcoll : models2Export) {
            //alle übergebenen Szenarios durchgehen und copyDependcies auflösen
            GraphDocument mainDoc = gdcoll.getMainDoc();
            for (LayerContainer lc : mainDoc.getLayers()) {
                for (NodeContainer nc : lc.getGraphNodeContainers()) {
                    Node node = nc.getNode();
                    for (GraphDocument doc : export) {
                        NodeContainer nodeContainer = node.getContainer(doc);
                        if (result.add(nodeContainer)) {
                            resolveCopyDependencies(node, null, result);
                        }
                    }
                }
                for (EdgeContainer ec : lc.getEdgeContainers()) {
                    Edge edge = ec.getEdge();
                    for (GraphDocument doc : export) {
                        ElementContainer edgeContainer = edge.getContainer(doc);
                        if (result.add(edgeContainer)) {
                            resolveCopyDependencies(edge, null, result);
                        }
                    }
                }
                for (BendpointContainer bc : lc.getBendpointContainers()) {
                    Bendpoint bendpoint = bc.getBendpoint();
                    for (GraphDocument doc : export) {
                        NodeContainer bendpointContainer = bendpoint.getContainer(doc);
                        result.add(bendpointContainer);
                    }
                }
            }
        }
        return result;
    }

    /**
     * @param elements ArrayList with ElementContainer
     * @param result ArrayList with hastStrings
     * @param userFields
     */
    public static CopyDependencyResolverResultSimple resolveCopyDependencies(final Collection<ElementContainer> elements) {
        CopyDependencyResolverResultSimple result = new CopyDependencyResolverResultSimple();
        for (ElementContainer ec : elements) {
            ModelElement me = ec.getElement();
            resolveCopyDependencies(me, null, result);
        }
        return result;
    }

    /**
     * sucht alle Element, die beim kopieren eines Knotens ebenfalls kopiert
     * werden sollen (rekursiv, auch für die gefundenen Element)
     *
     * @param me Element dessen abhängige Elemente gefunden werden sollen
     * @param ignoreClass elements with this class or with a subclass of this
     *            class will be ignored
     * @param elements the recursive filled retuern list with all elements. At
     *            least the given modelelement is contained in this list.
     * @param userFields all userfields of all elements in the filled elements
     *            list
     */
    private static void resolveCopyDependencies(final ModelElement me, final Class<? extends ModelElement> ignoreClass, CopyDependencyResolverResultSimple result) {
        if (me instanceof Bendpoint || me instanceof InferenceEdge) {
            return;
        }
        if (result == null) {
            result = new CopyDependencyResolverResultSimple();
        }
        if (!result.add(me)) {
            return;
        }
        GDCollection gdcoll = me.getCollection();
        LGMGraphDocument mainDoc = gdcoll.getMainDoc();
        Class<? extends ModelElement> elementClass = me.getClass();
        if (me instanceof Edge) {
            int layer = me.layerFor();
            LayerContainer lc = mainDoc.getLayer(layer);
            Iterable<BendpointContainer> bendpointContainers = lc.getBendpointContainers();
            for (BendpointContainer bpc : bendpointContainers) {
                Bendpoint bendpoint = bpc.getBendpoint();
                String edgeID = bendpoint.getEdgeID();
                if (edgeID != null) {
                    String meID = me.getID();
                    if (edgeID.equals(meID)) {
                        result.add(bendpoint);
                    }
                }
            }
            Edge edge = (Edge) me;
            ModelElement start = edge.getStart();
            resolveCopyDependencies(start, elementClass, result);
            ModelElement end = edge.getEnd();
            resolveCopyDependencies(end, elementClass, result);
        }
        MetaModel metaModel = gdcoll.getMetaModel();
        Collection<ElementaryMetaPath> copyDependencies = metaModel.getCopyDependencies(elementClass);
        for (ElementaryMetaPath copyDependentElementaryMetaPath : copyDependencies) {
            Class<? extends ModelElement> copyDependentClass = copyDependentElementaryMetaPath.getEndClass();
            boolean resolveCopyDependenciesOfConnected = ignoreClass == null;
            resolveCopyDependenciesOfConnected = resolveCopyDependenciesOfConnected || copyDependentElementaryMetaPath.canBeRecursive();
            resolveCopyDependenciesOfConnected = resolveCopyDependenciesOfConnected || !copyDependentClass.isAssignableFrom(ignoreClass);
            if (resolveCopyDependenciesOfConnected) {
                Collection<ElementContainer> connectedContainers = copyDependentElementaryMetaPath.getConnectedContainer(me, mainDoc);
                for (ElementContainer ec : connectedContainers) {
                    ModelElement connected = ec.getElement();
                    resolveCopyDependencies(connected, elementClass, result);
                    List<Edge> edgesWith = me.getEdgesWith(connected);
                    for (Edge edge : edgesWith) {
                        resolveCopyDependencies(edge, elementClass, result);
                    }
                }
            }
        }
        //elements wird in der Schleife vergrößert -> nicht über Iterable gehen
        for (int i = 0; i < result.elements.size(); i++) {
            ModelElement element = result.elements.get(i);
            List<Edge> edgesWith = me.getEdgesWith(element);
            for (Edge edge : edgesWith) {
                resolveCopyDependencies(edge, elementClass, result);
            }
        }
    }

}
