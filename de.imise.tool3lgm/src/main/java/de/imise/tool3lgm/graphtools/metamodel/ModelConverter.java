package de.imise.tool3lgm.graphtools.metamodel;

import static de.imise.tool3lgm.graphtools.undoredo.TransactionManager.STANDARD_PID;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import de.imise.tool3lgm.MetaModelContext;
import de.imise.tool3lgm.Tool3lgmModelType;
import de.imise.tool3lgm.Tool3lgmModelType.ModelCategory;
import de.imise.tool3lgm.graphtools.metamodel.ModelConverterDefinition.TargetMetaPathsCreationDefinition;
import de.imise.tool3lgm.graphtools.metamodel.elements.Edge;
import de.imise.tool3lgm.graphtools.metamodel.elements.ModelElement;
import de.imise.tool3lgm.graphtools.metamodel.elements.Node;
import de.imise.tool3lgm.graphtools.model.GDCollection;
import de.imise.tool3lgm.graphtools.model.GraphDocument;
import de.imise.tool3lgm.graphtools.model.Szenario;
import de.imise.tool3lgm.graphtools.path.meta.SimpleMetaPath;
import de.imise.tool3lgm.graphtools.path.pathmodel.ElementaryPath;
import de.imise.tool3lgm.graphtools.path.pathmodel.SimplePath;
import de.imise.tool3lgm.graphtools.view.container.ElementContainer;
import de.imise.tool3lgm.graphtools.view.container.NodeContainer;

/**
 * Diese Klasse übersetzt ein Modell mit einem Metamodell in ein Modell mit einem anderen Metamodell.
 *
 * @author AXS (7 Jun 2019)
 */
public class ModelConverter {

    /** Transformationsdefinition zur Umwandlung eines Modells in das andere */
    private final ModelConverterDefinition modelConverterDefinition;

    /** Quellmodell */
    private final GDCollection sourceModel;

    /** Zielmodell */
    private final GDCollection targetModel;

    /** Mappt vom Teilmodell aus dem SourceModell auf das entsprechende Teilmodell im TagetModell */
    private final Map<Szenario, Szenario> sourceSzenToTargetSzen = new HashMap<>();

    /**
     * @param modelConverterDefinition
     *            Transformationsdefinition zur Umwandlung eines Modells in das andere
     * @param sourceModel
     *            Quellmodell
     * @param targetModel
     *            Zielmodell
     */
    private ModelConverter(final ModelConverterDefinition modelConverterDefinition, final GDCollection sourceModel, final GDCollection targetModel) {
        this.modelConverterDefinition = modelConverterDefinition;
        this.sourceModel = sourceModel;
        this.targetModel = targetModel;
    }

    /**
     * Konvertiert das übergebene Modell anhand der übergebenen Transformationsdefinition in ein Modell mit anderem MetaModell.
     *
     * @param modelConverterDefinition
     * @param sourceModel
     */
    public static GDCollection convert(final ModelConverterDefinition modelConverterDefinition, final GDCollection sourceModel) {
        MetaModelContext targetMetaModelContext = modelConverterDefinition.getTargetMetaModelContext();
        Tool3lgmModelType sourceModelType = sourceModel.getModelType();
        ModelCategory modelCategory = sourceModelType.getModelCategory();
        Tool3lgmModelType targetModelType = new Tool3lgmModelType(targetMetaModelContext, modelCategory); //das hier muss nicht immer richtig sein, aber beim Umstellen auf die ModelTypes statt nur MetaModelContexts war es erstmal richtig und zu aufwendig den TargetModelType auch noch parametrierbar zu machen. Daher werden hier jetzt erstmal nur gelcihartige Modelle ineinander umgewandelt.
        GDCollection targetModel = new GDCollection(targetModelType);
        targetModel.setBulkMode(true);
        convert(modelConverterDefinition, sourceModel, targetModel);
        targetModel.setBulkMode(false);
        return targetModel;
    }

    /**
     * Konvertiert das übergebene Source-Modell anhand der übergebenen Transformationsdefinition in Elemente des Target-Modells, das ein anderes
     * Metamodell hat als das Source-Modell.
     *
     * @param modelConverterDefinition
     * @param sourceModel
     * @param targetModel
     */
    private static void convert(final ModelConverterDefinition modelConverterDefinition, final GDCollection sourceModel, final GDCollection targetModel) {
        ModelConverter modelConverter = new ModelConverter(modelConverterDefinition, sourceModel, targetModel);
        modelConverter.prepareTargetModel();
        modelConverter.convertDirectMappingNodes(); //Fall 1
        modelConverter.convertDirectMappingEdges(false); //Fall 2
        modelConverter.convertDirectMappingEdges(true); //Fall 3
        modelConverter.convertEdgesMappingMetaPaths(); //Fall 4
        modelConverter.convertMetaPathsMappingEdges(); //Fall 5
        modelConverter.convertMetaPathsMappingMetaPaths(); //Fall 6
        modelConverter.transform(); //Fall 7
    }

    /**
     * Legt im Target-Model alle Teilmodelle an, die es auch im SourceModel gibt. Diese werden in der Map {@link #sourceSzenToTargetSzen} gespeichert.
     */
    public void prepareTargetModel() {
        boolean oldBulkMode = targetModel.setBulkMode(true);
        for (Szenario sourceSzen : sourceModel.getSzenarios()) {
            Szenario targetSzen = targetModel.createSzenario(sourceSzen.getTitle(), false, sourceSzen.getDescription(), sourceSzen.getHashString(), false);
            sourceSzenToTargetSzen.put(sourceSzen, targetSzen);
        }
        targetModel.setBulkMode(oldBulkMode);
    }

    /**
     * Überträgt aus dem Ausgangsmodell alle per {@link #modelConverterDefinition} angegebenen direkt abbildbaren Knoten ins Zielmodell.
     */
    private void convertDirectMappingNodes() {
        //Map der direkt aufeinander abbildbaren Knotenklassen holen
        Map<Class<? extends Node>, Class<? extends Node>> directMappingNodeClasses = modelConverterDefinition.getSourceNodeClassesToTargetNodeClasses();
        //Set aller Knotenklassen holen, die in die Zielklassen umgewandelt werden sollen
        Set<Class<? extends Node>> sourceNodeClasses = directMappingNodeClasses.keySet();
        //Hauptdokument des umzuwandelnden Modells (Ausgangsmodell)
        GraphDocument sourceMainDoc = sourceModel.getMainGraphDocument();
        //Hauptdokument des Zielmodells
        GraphDocument targetMainDoc = targetModel.getMainGraphDocument();
        //für jede umzuwandelnde Knotenart
        for (Class<? extends Node> sourceNodeClass : sourceNodeClasses) {
            //hole die Knotenklasse, in die sie im Zielmodell umgewandelt werden soll
            Class<? extends Node> targetNodeClass = directMappingNodeClasses.get(sourceNodeClass);
            //hole aus dem Ausgangsmodell alle Knoten der umzuwandelnden Art
            List<ModelElement> sourceNodes = sourceMainDoc.getModelItems(sourceNodeClass, true);
            //für jeden dieser umzuwandelnden Knoten
            for (ModelElement sourceNode : sourceNodes) {
                //lege im Hauptdokument des Zielmodells einen Knoten der neuen Art an mit gleichem Namen, gleicher Beschreibung und gleichem HashString, wie der entsprechende Knoten im Ausgangsmodell hatte
                NodeContainer targetMainDocContainer = targetMainDoc.createNodeAndContainer(targetNodeClass, sourceNode.getName(), sourceNode.getDescription(), sourceNode.getHashString(), STANDARD_PID);
                //für alle Teilmodelle, in denen der umzuwandelnde Knoten im Ausgangsmodell vorkommt
                for (GraphDocument sourceDoc : sourceNode.getMySzenarios()) {
                    //wenn es nicht das Hauptdokument ist
                    if (sourceDoc instanceof Szenario) {
                        //hole das entsprechende Teilmodell im Zielmodell
                        Szenario targetSzen = sourceSzenToTargetSzen.get(sourceDoc);
                        //füge im dem Ziel-Teilmodell einen eigenen Container des umgewandelten Elementes ein
                        ElementContainer targetContainer = targetSzen.addContainerCopy(targetMainDocContainer);
                        //hole den Urspungscontainer aus dem zugehörigen Teilmodell im Ausgangsmodell
                        ElementContainer sourceContainer = sourceNode.getContainer(sourceDoc);
                        //übertrage das Layout auf den Zielcontainer
                        sourceContainer.adaptLayout(targetContainer);
                    }
                }
            }
        }
    }

    private void convertDirectMappingEdges(final boolean switchDirection) {
        //Map der direkt aufeinander abbildbaren Kantenklassen holen
        Map<Class<? extends Edge>, Class<? extends Edge>> directMappingEdgeClasses = !switchDirection ? modelConverterDefinition.getSourceEdgeClassesToTargetEdgeClasses() : modelConverterDefinition.getSourceEdgeClassesToSwitchedTargetEdgeClasses();
        //Set aller Kantenklassen holen, die in die Zielklassen umgewandelt werden sollen
        Set<Class<? extends Edge>> sourceEdgeClasses = directMappingEdgeClasses.keySet();
        //Hauptdokument des umzuwandelnden Modells (Ausgangsmodell)
        GraphDocument sourceMainDoc = sourceModel.getMainGraphDocument();
        //Hauptdokument des Zielmodells
        GraphDocument targetMainDoc = targetModel.getMainGraphDocument();
        //für jede umzuwandelnde Kantenart
        for (Class<? extends Edge> sourceEdgeClass : sourceEdgeClasses) {
            //hole die Kantenklasse, in die sie im Zielmodell umgewandelt werden soll
            Class<? extends Edge> targetEdgeClass = directMappingEdgeClasses.get(sourceEdgeClass);
            //hole aus dem Ausgangsmodell alle Kanten der umzuwandelnden Art
            List<ModelElement> sourceEdges = sourceMainDoc.getModelItems(sourceEdgeClass, true);
            //für jeden dieser umzuwandelnden Kanten
            for (ModelElement sourceEdgeElement : sourceEdges) {
                Edge sourceEdge = (Edge) sourceEdgeElement;
                ModelElement sourceEdgeStartElement = sourceEdge.getStart();
                ModelElement sourceEdgeEndElement = sourceEdge.getEnd();
                String sourceStartElementHash = sourceEdgeStartElement.getHashString();
                String sourceEndElementHash = sourceEdgeEndElement.getHashString();
                ModelElement targetStartElement = targetMainDoc.findNodeCoded(sourceStartElementHash);
                ModelElement targetEndElement = targetMainDoc.findNodeCoded(sourceEndElementHash);
                int startElementEdgeIndex = sourceEdgeStartElement.getEdgeIndex(sourceEdge);
                int endElementEdgeIndex = sourceEdgeEndElement.getEdgeIndex(sourceEdge);
                String sourceEdgeHash = sourceEdge.getHashString();
                String targetEdgeClassName = targetEdgeClass.getName();
                //linke die entsprechende Kante im Target-Modell mit demselben Hash und zwischen den Elementen mit demselben Hash
                if (!switchDirection) {
                    targetModel.link(targetEdgeClassName, sourceEdgeHash, targetStartElement, targetEndElement, startElementEdgeIndex, endElementEdgeIndex, true, STANDARD_PID);
                } else {
                    targetModel.link(targetEdgeClassName, sourceEdgeHash, targetEndElement, targetStartElement, endElementEdgeIndex, startElementEdgeIndex, true, STANDARD_PID);
                }
            }
        }
    }

    private void convertEdgesMappingMetaPaths() {
        //Map der Kantenklassen, die auf Pfade gemappt werden holen
        Map<Class<? extends Edge>, TargetMetaPathsCreationDefinition> edgesMappingMetaPaths = modelConverterDefinition.getSourceEdgeClassesToTargetMetaPaths();
        //Set aller Kantenklassen holen, die in die Metapfade umgewandelt werden sollen
        Set<Class<? extends Edge>> sourceEdgeClasses = edgesMappingMetaPaths.keySet();
        //Hauptdokument des umzuwandelnden Modells (Ausgangsmodell)
        GraphDocument sourceMainDoc = sourceModel.getMainGraphDocument();
        //Hauptdokument des Zielmodells
        GraphDocument targetMainDoc = targetModel.getMainGraphDocument();
        //für jede umzuwandelnde Kantenart
        for (Class<? extends Edge> sourceEdgeClass : sourceEdgeClasses) {
            //hole aus dem Ausgangsmodell alle Kanten der umzuwandelnden Art
            List<ModelElement> sourceEdges = sourceMainDoc.getModelItems(sourceEdgeClass, true);
            //hole den MetaPfad der im Zielmodell für die Kante angelet werden soll
            TargetMetaPathsCreationDefinition edgesMappingMetaPathsCreationDefinition = edgesMappingMetaPaths.get(sourceEdgeClass);
            SimpleMetaPath targetMetaPath = edgesMappingMetaPathsCreationDefinition.getSimpleMetaPath2Create();
            //Set, in das alle Pfadzwischenelemente kommen, die umbenannt wurden. Diese werden pauschal als identisch betrachtet, wenn sie denselben Namen haben. Diese Elemente werden dann zusammengeführt.
            Set<ModelElement> generatedRenamedElements = new HashSet<>();
            //für jeden dieser umzuwandelnden Kanten
            for (ModelElement sourceEdgeElement : sourceEdges) {
                Edge sourceEdge = (Edge) sourceEdgeElement;
                ModelElement sourceEdgeStartElement = sourceEdge.getStart();
                ModelElement sourceEdgeEndElement = sourceEdge.getEnd();
                String sourceEdgeStartElementHash = sourceEdgeStartElement.getHashString();
                String sourceEdgeEndElementHash = sourceEdgeEndElement.getHashString();
                String sourceEdgeHash = sourceEdgeElement.getHashString();
                //                System.err.println(sourceEdgeStartElement + " (" + sourceEdgeStartElement.getHashString() + ") " + sourceEdge.getClass().getSimpleName() + " (" + sourceEdge.getHashString() + ") " + " " + sourceEdgeEndElement + " ("
                //                        + sourceEdgeEndElement.getHashString() + ") ");
                ModelElement targetStartElement = targetMainDoc.findNodeCoded(sourceEdgeStartElementHash);
                ModelElement targetEndElement = targetMainDoc.findNodeCoded(sourceEdgeEndElementHash);
                //lege den MetaPfad im Zielmodell an
                SimplePath createdPath = targetMainDoc.createPath(targetStartElement, targetEndElement, targetMetaPath, STANDARD_PID);
                //replace the generated 3LGM-hashStrings by derived hashStrings from the source edge
                replaceGeneratedHashStrings(createdPath, sourceEdgeHash);
                //nach der Definiton der Umbenennungen die Namen der Elemente in Abhängigkeit von der Source-Edge umbenennen
                generatedRenamedElements = edgesMappingMetaPathsCreationDefinition.renameAndJoinEqualNamedElements(createdPath, sourceEdge, generatedRenamedElements);
            }
        }
    }

    /**
     * Replaces the automatic generated hashStrings in a generated path by a given hashString. The element in the
     * middle gets the original given hashString. All the others get the same with an unique number appended.
     * This algorithm is deterministic, so converting the same model again will generate the same hashStrings.
     * The hashStrings of the start- and endElement of the path will not be changed.
     *
     * @param path
     * @param hashString the generated element in the middle of the path gets this hash
     */
    private void replaceGeneratedHashStrings(final SimplePath path, final String hashString) {
        ModelElement middleElement = getMiddleElement(path);
        middleElement.setHashString(hashString);
        int pathLength = path.length();
        int counter = 1;
        for (int i = 0; i < pathLength; i++) {
            ElementaryPath pathStep = path.getPathStep(i);
            //edge
            Edge edge = pathStep.getEdge();
            counter = setHashString(edge, hashString, counter);
            if (i < pathLength - 1) { // endElement
                ModelElement endElement = pathStep.getEndElement();
                counter = setHashString(endElement, hashString, counter);
            }
        }
    }

    /**
     * @param me
     * @param hashString
     * @param counter
     * @return
     */
    private int setHashString(final ModelElement me, final String hashString, final int counter) {
        //ignore the element which already has the correct hashString
        if (me.getHashString().equals(hashString)) {
            return counter;
        }
        String fullHashString = hashString + "_" + counter;
        me.setHashString(fullHashString);
        return counter + 1;
    }

    private ModelElement getMiddleElement(final SimplePath path) {
        int pathLength = path.length();
        int middlePathStep = pathLength / 2;
        ElementaryPath pathStep = path.getPathStep(middlePathStep);
        //even path step count -> node in the middle; odd pathStepCount -> edge in the middle
        ModelElement middleElement = pathLength % 2 == 0 ? pathStep.getStartElement() : pathStep.getEdge();
        return middleElement;
    }

    private void convertMetaPathsMappingEdges() {
        //TODO: implementieren
    }

    private void convertMetaPathsMappingMetaPaths() {
        //TODO: implementieren

    }

    private void transform() {
        modelConverterDefinition.transform(sourceModel, targetModel);
    }

}
