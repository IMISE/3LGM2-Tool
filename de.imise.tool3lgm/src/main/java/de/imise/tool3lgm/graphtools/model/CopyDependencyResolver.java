package de.imise.tool3lgm.graphtools.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import de.imise.tool3lgm.graphtools.metamodel.MetaModel;
import de.imise.tool3lgm.graphtools.metamodel.elements.Bendpoint;
import de.imise.tool3lgm.graphtools.metamodel.elements.Edge;
import de.imise.tool3lgm.graphtools.metamodel.elements.HasPartEdge;
import de.imise.tool3lgm.graphtools.metamodel.elements.InferenceEdge;
import de.imise.tool3lgm.graphtools.metamodel.elements.ModelElement;
import de.imise.tool3lgm.graphtools.metamodel.elements.Node;
import de.imise.tool3lgm.graphtools.path.metapaths.ElementaryMetaPath;
import de.imise.tool3lgm.graphtools.userfield.definition.SubType;
import de.imise.tool3lgm.graphtools.userfield.definition.UserField;
import de.imise.tool3lgm.graphtools.userfield.definition.UserFieldDefinitions;
import de.imise.tool3lgm.graphtools.userfield.definition.UserFieldNumberFormat;
import de.imise.tool3lgm.graphtools.userfield.definition.UserFieldTarget;
import de.imise.tool3lgm.graphtools.view.container.BendpointContainer;
import de.imise.tool3lgm.graphtools.view.container.EdgeContainer;
import de.imise.tool3lgm.graphtools.view.container.ElementContainer;
import de.imise.tool3lgm.graphtools.view.container.LayerContainer;
import de.imise.tool3lgm.graphtools.view.container.NodeContainer;
import de.imise.util.Alphabetical;
import de.imise.util.collections.ListSet;

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
        public final Set<Edge> additionalEdges;

        /**
         * This definitions contains all userFields and numberformats that must
         * be copied too
         */
        public UserFieldDefinitions userFieldDefinitions;

        /**  */
        protected final Set<UserField> usedUserFields;

        /**
         * This set should contain all element classes which should be copied
         * and will be used to identify all default userfields which mus be
         * copied too
         */
        public final Set<Class<? extends UserFieldTarget>> userFieldTargetClasses;

        /**
         * This set should contain all subtype userfields which should be copied
         * with its subuserfields
         */
        public final Set<SubType> subTypes;

        /**
         * @param sourceCollection
         */
        public CopyDependencyResolverResultSimple(final GDCollection sourceCollection) {
            elements = new ArrayList<>();
            additionalEdges = new ListSet<>();
            userFieldDefinitions = new UserFieldDefinitions(sourceCollection);
            usedUserFields = new HashSet<>();
            userFieldTargetClasses = new HashSet<>();
            subTypes = new HashSet<>();
        }

        /**
         * @param me
         * @return
         */
        protected boolean add(final ModelElement me) {
            if (me instanceof Bendpoint || contains(me)) {
                return false;
            }
            Set<UserField> elementUserFields = me.getUserFieldInputValueKeys();
            if (!elementUserFields.isEmpty()) {
                usedUserFields.addAll(elementUserFields);
                for (UserField userField : elementUserFields) {
                    UserFieldNumberFormat numberFormat = userField.getNumberFormat();
                    if (numberFormat != null) {
                        userFieldDefinitions.add(numberFormat);
                    }
                }
            }
            userFieldTargetClasses.add(me.getClass());
            SubType subType = me.getSubType();
            if (subType != null) {
                subTypes.add(subType);
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

        /**
         * @return
         */
        public UserFieldDefinitions getUserFieldDefinitions() {
            return userFieldDefinitions;
        }

        @Override
        public String toString() {
            ArrayList<Edge> edges = new ArrayList<>(additionalEdges);
            Alphabetical.sort(edges);
            return getClass().getSimpleName() + ": \n\telements=" + elements + "\n\tadditionalEdges=" + edges + "\n\tusedUserFields=" + usedUserFields + "\n\tuserFieldDefinitions=" + userFieldDefinitions;
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
            super(export.isEmpty() ? null : export.get(0).getCollection());
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
     * @param usedUserFields Set, in welches die zu kopierenden
     *            benutzdefinierten Eigenschaftsfelder geschrieben werden
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
                            resolveCopyDependencies(node, null, result, null);
                        }
                    }
                }
                for (EdgeContainer ec : lc.getEdgeContainers()) {
                    Edge edge = ec.getEdge();
                    for (GraphDocument doc : export) {
                        ElementContainer edgeContainer = edge.getContainer(doc);
                        if (result.add(edgeContainer)) {
                            resolveCopyDependencies(edge, null, result, null);
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
        //TODO: the UserFieldDefinitions is never set here
        addNotSelectedEdgesOfSelectedElements(result);
        return result;
    }

    /**
     * @param sourceElements ArrayList with ElementContainer
     * @param targetCollection
     * @return
     */
    public static CopyDependencyResolverResultSimple resolveCopyDependencies(final Collection<ElementContainer> sourceElements, GDCollection targetCollection) {
        ElementContainer firstEc = sourceElements.isEmpty() ? null : sourceElements.iterator().next();
        ModelElement firstMe = firstEc == null ? null : firstEc.getElement();
        GDCollection gdcoll = firstMe == null ? null : firstMe.getCollection();
        CopyDependencyResolverResultSimple result = new CopyDependencyResolverResultSimple(gdcoll);
        for (ElementContainer ec : sourceElements) {
            ModelElement me = ec.getElement();
            resolveCopyDependencies(me, null, result, sourceElements);
        }
        addNotSelectedEdgesOfSelectedElements(result);

        UserFieldDefinitions sourceUserFieldDefinitions = gdcoll.getUserFieldDefinitions();
        UserFieldDefinitions clonedSourceUserFieldDefinitions = sourceUserFieldDefinitions.cloneForTargetCollection(targetCollection);
        clonedSourceUserFieldDefinitions.retain(result);
        result.userFieldDefinitions = clonedSourceUserFieldDefinitions;
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
     * @param usedUserFields all userfields of all elements in the filled
     *            elements list
     * @param initialSelectedElements
     */
    private static CopyDependencyResolverResultSimple resolveCopyDependencies(final ModelElement me, final Class<? extends ModelElement> ignoreClass, CopyDependencyResolverResultSimple result, final Collection<ElementContainer> initialSelectedElements) {
        if (result == null) {
            GDCollection gdcoll = me.getCollection();
            result = new CopyDependencyResolverResultSimple(gdcoll);
        }
        if (me instanceof Bendpoint || me instanceof InferenceEdge) {
            return result;
        }
        if (!result.add(me)) {
            return result;
        }
        GDCollection gdcoll = me.getCollection();
        LGMGraphDocument mainDoc = gdcoll.getMainDoc();
        Class<? extends ModelElement> elementClass = me.getClass();
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
                    resolveCopyDependencies(connected, elementClass, result, initialSelectedElements);
                    List<Edge> edgesWith = me.getEdgesWith(connected);
                    for (Edge edge : edgesWith) {
                        resolveCopyDependencies(edge, elementClass, result, initialSelectedElements);
                    }
                }
            }
        }
        //elements wird in der Schleife vergrößert -> nicht über Iterable gehen
        for (int i = 0; i < result.elements.size(); i++) {
            ModelElement element = result.elements.get(i);
            List<Edge> edgesWith = me.getEdgesWith(element);
            for (Edge edge : edgesWith) {
                resolveCopyDependencies(edge, elementClass, result, initialSelectedElements);
            }
        }
        return result;
    }

    /**
     * Adds all edges of selected elements to the set additionalEdges in the
     * given result which are not selected itself.<br>
     * After testing the functionality: PartOf-Edges to subordinated elements
     * are not added, because the copy is placed on top in graph and will always
     * hide its parts which are parts of the original element too. And if you
     * move the copied element the hidden parts will move too.
     *
     * @param result
     */
    private static void addNotSelectedEdgesOfSelectedElements(CopyDependencyResolverResultSimple result) {
        for (ModelElement element : result.elements) {
            for (Edge edge : element.getEdges()) {
                if (!(edge instanceof HasPartEdge) || ((HasPartEdge) edge).getPart() == element) {
                    if (!result.elements.contains(edge)) {
                        result.additionalEdges.add(edge);
                    }
                }
            }
        }
    }

}
