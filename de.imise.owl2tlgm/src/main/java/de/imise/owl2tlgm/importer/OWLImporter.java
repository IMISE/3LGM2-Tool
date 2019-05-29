package de.imise.owl2tlgm.importer;

import java.util.Properties;

import org.apache.jena.ontology.ObjectProperty;
import org.apache.jena.ontology.OntClass;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.ontology.OntResource;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.util.iterator.ExtendedIterator;
import org.apache.log4j.PropertyConfigurator;

import de.imise.owl2tlgm.graph.Edge;
import de.imise.owl2tlgm.graph.Graph;
import de.imise.owl2tlgm.graph.Node;

/**
 * @author Dietmar (28 May 2019)
 */
public class OWLImporter {

    // some definitions

    private static final String TEST_OWL_URL = "file:///Users/astruebi/Projekte/eclipse/IMISE/owl_test/owl2tlgm/src/main/resources/test-onto.owl";
    //    private static final String TEST_OWL_URL = "file:///home/lippold/pizza.owl";

    private static final String ROOT_LOGGER = "log4j.rootLogger";

    private static final String DEFAULT_LOG_LEVEL = "ERROR";

    private static final boolean DEBUG = false;

    /** Der OWL-Graph, in den die Daten importiert werden */
    private final Graph graph = new Graph();

    /**
     * @param owlFileURI
     */
    public OWLImporter(final String owlFileURI) {
        OntModel ontModel = readOntModelFromFile(owlFileURI);
        if (DEBUG) {
            printOntModelClasses(ontModel);
        }

        ExtendedIterator<ObjectProperty> objectProperties = ontModel.listObjectProperties();
        while (objectProperties.hasNext()) {
            // List and output object properties.
            ObjectProperty objectProperty = objectProperties.next();
            if (DEBUG) {
                System.out.println("Object Property: " + objectProperty);
            }
            OntResource domainClass = getClassResource(objectProperty.listDomain(), "  Domain class: ");
            OntResource rangeClass = getClassResource(objectProperty.listRange(), "  Range class: ");
            Node domainNode = createNode(domainClass);
            Node rangeNode = createNode(rangeClass);
            if (domainNode != null && rangeNode != null) {
                Edge edge = new Edge(objectProperty.getLocalName(), domainNode, rangeNode);
                domainNode.addDomainEdge(edge);
                rangeNode.addRangeEdge(edge);
                graph.addEdge(edge);
            }
        }
    }

    /**
     * List aus der übergebenen URI ein OntModel ein und gibt dieses zurück.
     *
     * @param uri
     * @return
     */
    private final OntModel readOntModelFromFile(final String uri) {
        // Create an ontology model from an URI.
        OntModel ontModel = ModelFactory.createOntologyModel();
        ontModel.read(uri);
        return ontModel;
    }

    /**
     * Gibt alle im übergebenen OntModel vorhandenen OntClass-Objekte aus
     *
     * @param ontModel
     */
    private final void printOntModelClasses(final OntModel ontModel) {
        // List and output classes.
        ExtendedIterator<OntClass> ontClasses = ontModel.listClasses();
        while (ontClasses.hasNext()) {
            OntClass ontClass = ontClasses.next();
            System.out.println("Class: " + ontClass.toString() + " Local name: " + ontClass.getLocalName());
        }
    }

    /**
     * Holt aus dem übergebenen Iterator die erste {@link OntResource}, die bei {@link OntResource#isClass()}<code>==true</code> liefert und gibt
     * diese zurück.
     *
     * @param ontResources
     *            Iterator aller zu durchsuchenden {@link OntResource}
     * @param debugPrefix
     *            Prefix, der vor die Debug-Ausgabe geschrieben werden soll
     * @return die erste {@link OntResource}, die eine Klasse ist oder <code>null</code>, wenn keine solche Resource gefunden wird
     */
    private final OntResource getClassResource(final ExtendedIterator<? extends OntResource> ontResources, final String debugPrefix) {
        OntResource ontResourceClass = null;
        while (ontResources.hasNext()) {
            OntResource ontResource = ontResources.next();
            if (ontResource.isClass()) {
                ontResourceClass = ontResource;
                if (DEBUG) {
                    System.out.println(debugPrefix + ontResource);
                }
                break;
            }
        }
        return ontResourceClass;
    }

    /**
     * Erzeugt einen neuen Knoten der übergebenen Art. Ist er bereits im Graph vorhanden, wird kein neuer Knoten angelegt, sondern der vorhandene
     * zurück gegeben.
     *
     * @param classOntResource
     * @return
     */
    private Node createNode(final OntResource classOntResource) {
        Node node = null;
        if (classOntResource != null) {
            String domainNodeName = classOntResource.getLocalName();
            if (graph.containsNode(domainNodeName)) {
                node = graph.getNode(domainNodeName);
            } else {
                node = new Node(domainNodeName);
            }
        }
        return node;
    }

    /**
     * @param args
     */
    public static void main(final String[] args) {
        initLogging();
        new OWLImporter(TEST_OWL_URL);
    }

    /**
     *
     */
    private static final void initLogging() {
        // Initialize logging.
        Properties properties = new Properties();
        properties.setProperty(ROOT_LOGGER, DEFAULT_LOG_LEVEL);
        PropertyConfigurator.configure(properties);
    }

}
