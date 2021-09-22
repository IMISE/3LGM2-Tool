package de.imise.tool3lgm.graphtools.metamodel;

import static de.imise.tool3lgm.graphtools.undoredo.TransactionManager.STANDARD_PID;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.Multimap;

import de.imise.tool3lgm.MetaModelContext;
import de.imise.tool3lgm.Tool3lgmModelType;
import de.imise.tool3lgm.Tool3lgmModelType.ModelCategory;
import de.imise.tool3lgm.graphtools.metamodel.ModelConverterDefinition.TargetPathsCreationDefinition;
import de.imise.tool3lgm.graphtools.metamodel.elements.Edge;
import de.imise.tool3lgm.graphtools.metamodel.elements.ModelElement;
import de.imise.tool3lgm.graphtools.metamodel.elements.Node;
import de.imise.tool3lgm.graphtools.model.GDCollection;
import de.imise.tool3lgm.graphtools.model.GraphDocument;
import de.imise.tool3lgm.graphtools.model.LGMGraphDocument;
import de.imise.tool3lgm.graphtools.model.Szenario;
import de.imise.tool3lgm.graphtools.path.metapaths.SimpleMetaPath;
import de.imise.tool3lgm.graphtools.path.paths.SimplePath;
import de.imise.tool3lgm.graphtools.view.container.ElementContainer;
import de.imise.tool3lgm.graphtools.view.container.NodeContainer;

/**
 * Diese Klasse übersetzt ein Modell mit einem Metamodell in ein Modell mit
 * einem anderen Metamodell.
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

    /**
     * Mappt vom Teilmodell aus dem SourceModell auf das entsprechende
     * Teilmodell im TagetModell
     */
    private final Map<Szenario, Szenario> sourceSzenToTargetSzen = new HashMap<>();

    /**
     * @param modelConverterDefinition Transformationsdefinition zur Umwandlung
     *            eines Modells in das andere
     * @param sourceModel Quellmodell
     * @param targetModel Zielmodell
     */
    private ModelConverter(final ModelConverterDefinition modelConverterDefinition, final GDCollection sourceModel, final GDCollection targetModel) {
        this.modelConverterDefinition = modelConverterDefinition;
        this.sourceModel = sourceModel;
        this.targetModel = targetModel;
    }

    /**
     * Konvertiert das übergebene Modell anhand der übergebenen
     * Transformationsdefinition in ein Modell mit anderem MetaModell.
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
        targetModel.setName(sourceModel.getName());
        convert(modelConverterDefinition, sourceModel, targetModel);
        targetModel.updateInferenceEdges(STANDARD_PID);
        targetModel.setBulkMode(false);
        return targetModel;
    }

    /**
     * Konvertiert das übergebene Source-Modell anhand der übergebenen
     * Transformationsdefinition in Elemente des Target-Modells, das ein anderes
     * Metamodell hat als das Source-Modell.
     *
     * @param modelConverterDefinition
     * @param sourceModel
     * @param targetModel
     */
    private static void convert(final ModelConverterDefinition modelConverterDefinition, final GDCollection sourceModel, final GDCollection targetModel) {
        ModelConverter modelConverter = new ModelConverter(modelConverterDefinition, sourceModel, targetModel);
        modelConverter.prepareTargetModel();
        modelConverter.beforeTransform(); // Fall 0
        modelConverter.convertDirectMappingNodes(); //Fall 1
        modelConverter.convertDirectMappingEdges(false); //Fall 2
        modelConverter.convertDirectMappingEdges(true); //Fall 3
        modelConverter.convertEdgesMappingMetaPaths(); //Fall 4
        modelConverter.convertMetaPathsMappingEdges(); //Fall 5
        modelConverter.convertMetaPathsMappingMetaPaths(); //Fall 6
        modelConverter.afterTransform(); //Fall 7
    }

    /**
     * Legt im Target-Model alle Teilmodelle an, die es auch im SourceModel
     * gibt. Diese werden in der Map {@link #sourceSzenToTargetSzen}
     * gespeichert.
     */
    private void prepareTargetModel() {
        boolean oldBulkMode = targetModel.setBulkMode(true);
        for (Szenario sourceSzen : sourceModel.getSzenarios()) {
            Szenario targetSzen = targetModel.createSzenario(sourceSzen.getName(), false, sourceSzen.getDescription(), sourceSzen.getID(), false);
            sourceSzenToTargetSzen.put(sourceSzen, targetSzen);
        }
        targetModel.setBulkMode(oldBulkMode);
    }

    /**
     *
     */
    private void beforeTransform() {
        modelConverterDefinition.beforeTransform(sourceModel, targetModel);
    }

    /**
     * Überträgt aus dem Ausgangsmodell alle per
     * {@link #modelConverterDefinition} angegebenen direkt abbildbaren Knoten
     * ins Zielmodell.
     */
    private void convertDirectMappingNodes() {
        //Map der direkt aufeinander abbildbaren Knotenklassen holen
        Map<Class<? extends Node>, Class<? extends Node>> directMappingNodeClasses = modelConverterDefinition.getSourceNodeClassesToTargetNodeClasses();
        //Set aller Knotenklassen holen, die in die Zielklassen umgewandelt werden sollen
        Set<Class<? extends Node>> sourceNodeClasses = directMappingNodeClasses.keySet();
        //Hauptdokument des umzuwandelnden Modells (Ausgangsmodell)
        GraphDocument sourceMainDoc = sourceModel.getMainDoc();
        //Hauptdokument des Zielmodells
        GraphDocument targetMainDoc = targetModel.getMainDoc();
        //für jede umzuwandelnde Knotenart
        for (Class<? extends Node> sourceNodeClass : sourceNodeClasses) {
            //hole die Knotenklasse, in die sie im Zielmodell umgewandelt werden soll
            Class<? extends Node> targetNodeClass = directMappingNodeClasses.get(sourceNodeClass);
            //hole aus dem Ausgangsmodell alle Knoten der umzuwandelnden Art
            List<ModelElement> sourceNodes = sourceMainDoc.getModelItems(sourceNodeClass, true);
            //für jeden dieser umzuwandelnden Knoten
            for (ModelElement sourceNode : sourceNodes) {
                //lege im Hauptdokument des Zielmodells einen Knoten der neuen Art an mit gleichem Namen, gleicher Beschreibung und gleicher ID, wie der entsprechende Knoten im Ausgangsmodell hatte
                NodeContainer targetMainDocContainer = targetMainDoc.createNodeAndContainer(targetNodeClass, sourceNode.getName(), sourceNode.getDescription(), sourceNode.getID(), STANDARD_PID);
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

    /**
     * @param switchDirection
     */
    private void convertDirectMappingEdges(final boolean switchDirection) {
        //Map der direkt aufeinander abbildbaren Kantenklassen holen
        Map<Class<? extends Edge>, Class<? extends Edge>> directMappingEdgeClasses = !switchDirection ? modelConverterDefinition.getSourceEdgeClassesToTargetEdgeClasses() : modelConverterDefinition.getSourceEdgeClassesToSwitchedTargetEdgeClasses();
        //Set aller Kantenklassen holen, die in die Zielklassen umgewandelt werden sollen
        Set<Class<? extends Edge>> sourceEdgeClasses = directMappingEdgeClasses.keySet();
        //Hauptdokument des umzuwandelnden Modells (Ausgangsmodell)
        GraphDocument sourceMainDoc = sourceModel.getMainDoc();
        //Hauptdokument des Zielmodells
        GraphDocument targetMainDoc = targetModel.getMainDoc();
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
                String sourceStartElementID = sourceEdgeStartElement.getID();
                String sourceEndElementID = sourceEdgeEndElement.getID();
                ModelElement targetStartElement = targetMainDoc.findNodeCoded(sourceStartElementID);
                ModelElement targetEndElement = targetMainDoc.findNodeCoded(sourceEndElementID);
                int startElementEdgeIndex = sourceEdgeStartElement.getEdgeIndex(sourceEdge);
                int endElementEdgeIndex = sourceEdgeEndElement.getEdgeIndex(sourceEdge);
                String sourceEdgeID = sourceEdge.getID();
                String targetEdgeClassName = targetEdgeClass.getName();
                //linke die entsprechende Kante im Target-Modell mit derselben ID und zwischen den Elementen mit dermselben ID
                if (!switchDirection) {
                    targetModel.link(targetEdgeClassName, sourceEdgeID, targetStartElement, targetEndElement, startElementEdgeIndex, endElementEdgeIndex, true, STANDARD_PID);
                } else {
                    targetModel.link(targetEdgeClassName, sourceEdgeID, targetEndElement, targetStartElement, endElementEdgeIndex, startElementEdgeIndex, true, STANDARD_PID);
                }
            }
        }
    }

    /**
     *
     */
    private void convertEdgesMappingMetaPaths() {
        //Map der Kantenklassen, die auf Pfade gemappt werden holen
        Multimap<Class<? extends Edge>, TargetPathsCreationDefinition> edgesMappingMetaPaths = modelConverterDefinition.getSourceEdgeClassesToTargetMetaPaths();
        //Set aller Kantenklassen holen, die in die Metapfade umgewandelt werden sollen
        Set<Class<? extends Edge>> sourceEdgeClasses = edgesMappingMetaPaths.keySet();
        //Hauptdokument des umzuwandelnden Modells (Ausgangsmodell)
        LGMGraphDocument sourceMainDoc = sourceModel.getMainDoc();
        //Hauptdokument des Zielmodells
        LGMGraphDocument targetMainDoc = targetModel.getMainDoc();
        //für jede umzuwandelnde Kantenart
        for (Class<? extends Edge> sourceEdgeClass : sourceEdgeClasses) {
            //hole aus dem Ausgangsmodell alle Kanten der umzuwandelnden Art
            List<ModelElement> sourceEdges = sourceMainDoc.getModelItems(sourceEdgeClass, true);
            //hole den MetaPfad der im Zielmodell für die Kante angelegt werden soll
            Collection<TargetPathsCreationDefinition> edgesMappingMetaPathsCreationDefinitions = edgesMappingMetaPaths.get(sourceEdgeClass);
            for (TargetPathsCreationDefinition edgesMappingMetaPathsCreationDefinition : edgesMappingMetaPathsCreationDefinitions) {
                SimpleMetaPath targetMetaPath = edgesMappingMetaPathsCreationDefinition.getSimpleMetaPath2Create();
                //Set, in das alle Pfadzwischenelemente kommen, die umbenannt wurden. Diese werden pauschal als identisch betrachtet, wenn sie denselben Namen haben. Diese Elemente werden dann zusammengeführt.
                Set<ModelElement> generatedRenamedElements = new HashSet<>();
                //für jeden dieser umzuwandelnden Kanten
                for (ModelElement sourceEdgeElement : sourceEdges) {
                    Edge sourceEdge = (Edge) sourceEdgeElement;
                    ModelElement sourceEdgeStartElement = sourceEdge.getStart();
                    ModelElement sourceEdgeEndElement = sourceEdge.getEnd();
                    String sourceEdgeStartElementID = sourceEdgeStartElement.getID();
                    String sourceEdgeEndElementID = sourceEdgeEndElement.getID();
                    String sourceEdgeID = sourceEdgeElement.getID();
                    //System.err.println(sourceEdgeStartElement + " (" + sourceEdgeStartElement.getID() + ") " + sourceEdge.getClass().getSimpleName() + " (" + sourceEdge.getID() + ") " + " " + sourceEdgeEndElement + " ("
                    //+ sourceEdgeEndElement.getID() + ") ");
                    ModelElement targetStartElement = targetMainDoc.findNodeCoded(sourceEdgeStartElementID);
                    ModelElement targetEndElement = targetMainDoc.findNodeCoded(sourceEdgeEndElementID);
                    //lege den MetaPfad im Zielmodell an
                    SimplePath createdPath = targetMainDoc.createSimplePath(targetStartElement, targetEndElement, targetMetaPath, false, STANDARD_PID);
                    // replace the generated 3LGM-IDs by derived
                    // IDs from the source edge or join created
                    // elements if the same element (same type with
                    // same IDprefix) already exists
                    ModelConverterUtils.replaceGeneratedIDAndJoinEqualsElements(createdPath, sourceEdgeID);
                    // nach der Definiton der Umbenennungen die Namen der
                    // Elemente in Abhängigkeit von der Source-Edge umbenennen
                    //und ggf. zusammenführen, wenn sie identisch heißen
                    generatedRenamedElements = ModelConverterUtils.renameAndJoinEqualNamedElements(edgesMappingMetaPathsCreationDefinition, createdPath, sourceEdge, generatedRenamedElements);
                    // am Ende alle untergeordneten Elemente, die nach dem obigen
                    // join dieselben Elemente verbinden, auch joinen, wenn die
                    // Definition das vorgibt
                    if (edgesMappingMetaPathsCreationDefinition.joinSubordinatedBetweenEqualsElements) {
                        ModelConverterUtils.joinSubordinatedBetweenEqualsElements(createdPath);
                    }
                }
            }
        }
    }

    /**
     *
     */
    private void convertMetaPathsMappingEdges() {
        //TODO: implementieren
    }

    /**
     *
     */
    private void convertMetaPathsMappingMetaPaths() {
        //TODO: implementieren

    }

    /**
     *
     */
    private void afterTransform() {
        modelConverterDefinition.afterTransform(sourceModel, targetModel);
    }

}
