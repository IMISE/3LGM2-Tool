package de.imise.tool3lgm.graphtools.model;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import de.imise.tool3lgm.graphtools.metamodel.MetaModel;
import de.imise.tool3lgm.graphtools.metamodel.elements.Bendpoint;
import de.imise.tool3lgm.graphtools.metamodel.elements.Edge;
import de.imise.tool3lgm.graphtools.metamodel.elements.InferenceEdge;
import de.imise.tool3lgm.graphtools.metamodel.elements.ModelElement;
import de.imise.tool3lgm.graphtools.metamodel.elements.Node;
import de.imise.tool3lgm.graphtools.path.PathFunctions;
import de.imise.tool3lgm.graphtools.path.metapaths.ElementaryMetaPath;
import de.imise.tool3lgm.graphtools.userfield.UserField;
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
     *
     */
    final GDCollection gdcoll;

    /**
     * @param gdcoll
     */
    public CopyDependencyResolver(final GDCollection gdcoll) {
        this.gdcoll = gdcoll;
    }

    /**
     * Sucht alle Element und Icons, die kopiert werden müssen
     *
     * @param export
     *            Array von Szenarios, die zu kopieren sind
     * @param elements
     *            Set, in welches die zu kopierenden Element geschrieben werden
     * @param bitmaps
     *            Set, in welches die HashStrings der zu kopierenden Icons geschrieben werden
     * @param userFields
     *            Set, in welches die zu kopierenden benutzdefinierten Eigenschaftsfelder geschrieben werden
     */
    public void resolveCopyDependencies(final List<? extends GraphDocument> export, final List<ModelElement> elements, final Set<String> bitmaps, final Set<UserField> userFields) {
        /* alle übergebenen Szenarios durchgehen und copyDependcies auflösen */
        GraphDocument mainDoc = gdcoll.getMainDoc();
        for (LayerContainer lc : mainDoc.getLayers()) {
            for (NodeContainer nc : lc.getGraphNodeContainers()) {
                Node node = nc.getNode();
                for (GraphDocument doc : export) {
                    ElementContainer container = node.getContainer(doc);
                    if (container != null && !elements.contains(node)) {
                        elements.add(node);
                        String iconName = ((NodeContainer) container).getIconString();
                        if (iconName != null) {
                            bitmaps.add(iconName);
                        }
                        resolveCopyDependencies(node, null, elements, userFields);
                    }
                }
            }
            for (EdgeContainer ec : lc.getEdgeContainers()) {
                Edge edge = ec.getEdge();
                for (GraphDocument doc : export) {
                    ElementContainer container = edge.getContainer(doc);
                    if (container != null && !elements.contains(edge)) {
                        elements.add(edge);
                        resolveCopyDependencies(edge, null, elements, userFields);
                    }
                }
            }
            for (BendpointContainer bc : lc.getBendpointContainers()) {
                Bendpoint bendpoint = bc.getBendpoint();
                for (GraphDocument doc : export) {
                    ElementContainer container = bendpoint.getContainer(doc);
                    if (container != null && !elements.contains(bendpoint)) {
                        elements.add(bendpoint);
                    }
                }
            }
        }
    }

    /**
     * @param elements ArrayList with ElementContainer
     * @param result ArrayList with hastStrings
     * @param userFields
     */
    public void resolveCopyDependencies(final Collection<ElementContainer> elements, final List<ModelElement> result, final Set<UserField> userFields) {
        for (ElementContainer ec : elements) {
            ModelElement me = ec.getElement();
            resolveCopyDependencies(me, null, result, userFields);
        }
    }

    /**
     * sucht alle Element, die beim kopieren eines Knotens ebenfalls kopiert werden sollen (rekursiv, auch für die gefundenen Element)
     *
     * @param knoten der dessen abhängige Element gefunden werden sollen
     * @return HashSet mit den HashStrings der gefundenen Elementen
     */
    /**
     * @param me Element dessen abhängige Elemente gefunden werden sollen
     * @p
     * @param elements
     * @param userFields
     */
    private void resolveCopyDependencies(final ModelElement me, final Class<? extends ModelElement> ignoreClass, final List<ModelElement> elements, final Set<UserField> userFields) {
        if (me instanceof Bendpoint || me instanceof InferenceEdge || elements.contains(me)) {
            return;
        }
        elements.add(me);

        for (UserField userField : me.getUserFieldInputValueKeys()) {
            userFields.add(userField);
        }
        LGMGraphDocument mainDoc = gdcoll.getMainDoc();
        Class<? extends ModelElement> elementClass = me.getClass();
        if (me instanceof Edge) {
            int layer = me.layerFor();
            LayerContainer lc = mainDoc.getLayer(layer);
            Iterable<BendpointContainer> bendpointContainers = lc.getBendpointContainers();
            for (BendpointContainer bpc : bendpointContainers) {
                Bendpoint bendpoint = bpc.getBendpoint();
                String edgeHash = bendpoint.getEdgeHash();
                if (edgeHash != null) {
                    String meHash = me.getHashString();
                    if (edgeHash.equals(meHash)) {
                        if (!elements.contains(bendpoint)) {
                            elements.add(bendpoint);
                        }
                    }
                }
            }
            Edge edge = (Edge) me;
            ModelElement start = edge.getStart();
            resolveCopyDependencies(start, elementClass, elements, userFields);
            ModelElement end = edge.getEnd();
            resolveCopyDependencies(end, elementClass, elements, userFields);
        }
        MetaModel metaModel = gdcoll.getMetaModel();
        Collection<ElementaryMetaPath> copyDependencies = metaModel.getCopyDependencies(elementClass);
        for (ElementaryMetaPath copyDependentElementaryMetaPath : copyDependencies) {
            Class<? extends ModelElement> copyDependentClass = copyDependentElementaryMetaPath.getEndClass();
            boolean resolveCopyDependenciesOfConnected = ignoreClass == null;
            resolveCopyDependenciesOfConnected = resolveCopyDependenciesOfConnected || copyDependentElementaryMetaPath.canBeRecursive();
            resolveCopyDependenciesOfConnected = resolveCopyDependenciesOfConnected || !copyDependentClass.isAssignableFrom(ignoreClass);
            if (resolveCopyDependenciesOfConnected) {
                Collection<ElementContainer> connectedContainers = PathFunctions.getConnectedContainer(me, mainDoc, copyDependentElementaryMetaPath);
                for (ElementContainer ec : connectedContainers) {
                    ModelElement connected = ec.getElement();
                    resolveCopyDependencies(connected, elementClass, elements, userFields);
                    List<Edge> edgesWith = me.getEdgesWith(connected);
                    for (Edge edge : edgesWith) {
                        resolveCopyDependencies(edge, elementClass, elements, userFields);
                    }
                }
            }
        }
        //elements wird in der Schleife vergrößert -> nicht über Iterable gehen
        for (int i = 0; i < elements.size(); i++) {
            ModelElement element = elements.get(i);
            List<Edge> edgesWith = me.getEdgesWith(element);
            for (Edge edge : edgesWith) {
                resolveCopyDependencies(edge, elementClass, elements, userFields);
            }
        }
    }

}
