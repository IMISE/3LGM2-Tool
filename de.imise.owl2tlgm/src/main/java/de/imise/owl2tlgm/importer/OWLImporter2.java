package de.imise.owl2tlgm.importer;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.swing.JDialog;
import javax.swing.JFileChooser;

import org.apache.jena.ontology.ObjectProperty;
import org.apache.jena.ontology.OntClass;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.ontology.OntProperty;
import org.apache.jena.ontology.OntResource;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.NodeIterator;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.util.iterator.ExtendedIterator;

import com.google.common.base.Strings;

import de.imise.owl2tlgm.importmetamodel.IheImportMetaModelDefinition;
import de.imise.tool3lgm.MetaModelContext;
import de.imise.tool3lgm.graphtools.metamodel.MetaModel;
import de.imise.tool3lgm.graphtools.metamodel.elements.Edge;
import de.imise.tool3lgm.graphtools.metamodel.elements.ModelElement;
import de.imise.tool3lgm.graphtools.metamodel.elements.Node;
import de.imise.tool3lgm.graphtools.model.GDCollection;
import de.imise.tool3lgm.graphtools.undoredo.TransactionManager;
import de.imise.tool3lgm.graphtools.view.container.NodeContainer;
import de.imise.tool3lgm.imexport.UrlSourceDataImporter;
import de.imise.util.StringUtils;
import de.imise.util.swing.dialog.ExtendedFileChooser;

/**
 * @author Dietmar (28 May 2019)
 */
public class OWLImporter2 extends UrlSourceDataImporter {

    /**
     * Da ObjectProperties u.U. genauso heißen, wie Knoten-Klassen, sollte man mit diesem Namenszusatz bei 3LGM2-Kantenklassen für Eindeutigkeit
     * sorgen.
     */
    private static final String TLGM_EDGE_CLASS_NAME_POSTFIX = "_Edge";

    /**
     * Mappt von einem eine Knotenklassen-Instanz repräsentierenden RDF-Objekt auf das im 3LGM2-Import-Modell erzeugte korrespondierende ModelElement
     */
    private final Map<OntResource, ModelElement> rdfSourceInstanceToModelElement = new HashMap<>();

    /**
     *
     */
    public OWLImporter2() {
    }

    /**
     * @param args
     */
    public static void main(final String[] args) {
        MetaModelContext metaModelContext = new MetaModelContext(IheImportMetaModelDefinition.class);
        GDCollection gdcoll = new GDCollection(metaModelContext);
        OWLImporter2 importer = new OWLImporter2();
        importer.importData(gdcoll);
    }

    @Override
    protected void importData(String url, final GDCollection gdcoll) {
        if (Strings.isNullOrEmpty(url)) {
            File file = getFile(null);
            if (file != null) {
                try {
                    URI uri = file.toURI();
                    URL url2 = uri.toURL();
                    url = url2.toString();
                } catch (MalformedURLException e) {
                }
            }
        }
        if (!Strings.isNullOrEmpty(url)) {
            boolean oldBulkMode = gdcoll.setBulkMode(true);
            OntModel ontModel = readOntModel(url);

            printOntModel(ontModel);

            importNodes(ontModel, gdcoll);
            importEdges(ontModel, gdcoll);
            gdcoll.setBulkMode(oldBulkMode);
        }
    }

    /**
     * Gibt alle Objekte im übergebenen OntModel aus
     *
     * @param ontModel
     */
    private void printOntModel(final OntModel ontModel) {
        NodeIterator allObjects = ontModel.listObjects();
        while (allObjects.hasNext()) {
            RDFNode o = allObjects.next();
            System.out.println(o);
        }
    }

    /**
     * Auswahl einer Datei über einen FileChooser
     *
     * @return
     */
    public final File getFile(final JDialog dialogOwner) {
        ExtendedFileChooser fileChooser = new ExtendedFileChooser(getClass());
        fileChooser.setMultiSelectionEnabled(false);
        if (fileChooser.showOpenDialog(dialogOwner) != JFileChooser.APPROVE_OPTION) {
            return null;
        }
        File file = fileChooser.getSelectedFile();
        return file;
    }

    /**
     * Fragt in der Ontologie alle Class-Knoten ab, deren Klasse denselben Namen hat, wie eine Klasse aus dem Metamodell des zu füllenden
     * 3LGM2-Modells und legt in diesem 3LGM2-Modell dafür einen Knoten an. Außerdem werden die Class-Knoten und der korrespondierende 3LGM2-Knoten in
     * der Map {@link #rdfSourceInstanceToModelElement} gespeichert.
     *
     * @param ontModel Quellmodell
     * @param gdcoll Zielmodell
     */
    private void importNodes(final OntModel ontModel, final GDCollection gdcoll) {
        MetaModel metaModel = gdcoll.getMetaModel();
        Set<Class<? extends ModelElement>> allNodesSet = metaModel.allNodesSet;
        Set<String> simpleNodeClassNames = getSimpleClassNames(allNodesSet, "");

        ExtendedIterator<OntClass> ontClasses = ontModel.listClasses();
        while (ontClasses.hasNext()) {
            OntClass ontClass = ontClasses.next();
            String ontClassName = ontClass.getLocalName(); //ist das der Klassenname oder ein Instanzname? Hier gehe ich vom Klassennamen aus
            if (!simpleNodeClassNames.contains(ontClassName)) {
                continue;
            }
            Class<? extends ModelElement> lgmClass = metaModel.getClassForName(ontClassName);
            Class<? extends Node> nodeClass = lgmClass.asSubclass(Node.class);
            ExtendedIterator<? extends OntResource> ontClassInstances = ontClass.listInstances();
            while (ontClassInstances.hasNext()) {
                OntResource ontResource = ontClassInstances.next();
                String name = ontResource.getLocalName();
                String description = ""; //??? woher aus der Ontologie
                String hashString = null; //??? woher aus der Ontologie oder ID als extra Angabe setzten? Irgendwas muss eine eindeutige Referenz ins die Ontologie sein
                NodeContainer nodeContainer = gdcoll.createKnotenWithContainer(nodeClass, name, description, hashString, TransactionManager.STANDARD_PID);
                ModelElement modelElement = nodeContainer.getElement();
                rdfSourceInstanceToModelElement.put(ontResource, modelElement);
            }
        }
    }

    /**
     * Fragt im Quellmodell alle ObjectProperties ab, deren Klassenname auch im Metamodell des Zielmodells als Kantenklassenname vorkommt. Dann werden
     * im Zielmodell die entsprechenden Elemente verlinkt, die die Objectproperty im Quellmodell verbindet.
     *
     * @param ontModel Quellmodell
     * @param gdcoll Zielmodell
     */
    private void importEdges(final OntModel ontModel, final GDCollection gdcoll) {
        MetaModel metaModel = gdcoll.getMetaModel();
        Set<Class<? extends Edge>> allEdgesSet = metaModel.allEdgesSet;
        Set<String> objectPropertyNames = getSimpleClassNames(allEdgesSet, TLGM_EDGE_CLASS_NAME_POSTFIX);
        ExtendedIterator<ObjectProperty> objectProperties = ontModel.listObjectProperties();
        while (objectProperties.hasNext()) {
            ObjectProperty objectProperty = objectProperties.next();
            String name = objectProperty.getLocalName();
            if (!objectPropertyNames.contains(name) && !isSubPropertyOfImportableObjectProperty(objectProperty, objectPropertyNames)) {
                continue;
            }
            String edgeClassName = name + TLGM_EDGE_CLASS_NAME_POSTFIX;
            edgeClassName = StringUtils.capitalizeFirstChar(edgeClassName); //Kantenklassennamen sind immer groß geschrieben - zugehörige, gleich heißende EdgeProperty evtl. klein -> Umwandeln

            String edgeHash = null; //evtl aus der Import-Quelle?
            OntResource domainClass = getClassResource(objectProperty.listDomain());
            OntResource rangeClass = getClassResource(objectProperty.listRange());
            ModelElement startElement = rdfSourceInstanceToModelElement.get(domainClass);
            ModelElement endElement = rdfSourceInstanceToModelElement.get(rangeClass);
            gdcoll.link(edgeClassName, edgeHash, startElement, endElement, -1, -1, false, TransactionManager.STANDARD_PID);
        }

    }

    /**
     * Prüft rekursiv, ob die übergebene ObjectProperty eine Super-ObjectProperty besitzt, deren Name zu den zu importierenden ObjectProperties
     * gehört.
     *
     * @param objectProperty
     * @param importableObjectPropertyNames
     * @return
     */
    private final boolean isSubPropertyOfImportableObjectProperty(final ObjectProperty objectProperty, final Set<String> importableObjectPropertyNames) {
        ExtendedIterator<? extends OntProperty> superProperties = objectProperty.listSuperProperties();
        while (superProperties.hasNext()) {
            OntProperty superProperty = superProperties.next();
            if (superProperty.isObjectProperty()) {
                String name = superProperty.getLocalName();
                if (importableObjectPropertyNames.contains(name)) {
                    return true;
                }
                if (isSubPropertyOfImportableObjectProperty((ObjectProperty) superProperty, importableObjectPropertyNames)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Liefert aus der übergebenen Menge von Klassen ein Set der darin enthaltenen simplen Klassennamen. Wird ein postfix-String übergeben, dann wird
     * dieser von allen Klassennamen abgezogen, in denen er vorkommt.
     *
     * @param classes
     * @param postfix
     * @return
     */
    private static final <T> Set<String> getSimpleClassNames(final Iterable<Class<? extends T>> classes, final String postfix) {
        Set<String> returnSet = new HashSet<>();
        int postFixLength = postfix.length();
        for (Class<? extends T> clazz : classes) {
            String simpleClassName = clazz.getSimpleName();
            if (postFixLength > 0 && simpleClassName.endsWith(postfix)) {
                simpleClassName = simpleClassName.substring(0, simpleClassName.length() - postFixLength);
            }
            returnSet.add(simpleClassName);
            //Wenn der SimpleClassName mit einem Großbuchstaben anfing, dann auch den SimpleClassName mit Kleinbuchstaben ins Set schreiben. Wenn er mit einem Kleinbuchstaben los geht, dann Groß machen.
            //Das ist nätig, weil due ObjectProperties in OWL laut Konvention mit einem Kleinbuchstaben beginnen, die korrespondierende Java-Klasse, aber immer groß beginnt, aber sonst gleich heißt (bis auf einen fixen Namenszusatz wie "_Edge")
            char firstChar = simpleClassName.charAt(0);
            if (Character.isUpperCase(firstChar)) {
                firstChar = Character.toLowerCase(firstChar);
            } else {
                firstChar = Character.toUpperCase(firstChar);
            }
            simpleClassName = firstChar + simpleClassName.substring(1);
            returnSet.add(simpleClassName);
        }
        return returnSet;
    }

    /**
     * Holt aus dem übergebenen Iterator die erste {@link OntResource}, die bei {@link OntResource#isClass()}<code>==true</code> liefert und gibt
     * diese zurück.
     *
     * @param ontResources
     *            Iterator aller zu durchsuchenden {@link OntResource}
     * @return die erste {@link OntResource}, die eine Klasse ist oder <code>null</code>, wenn keine solche Resource gefunden wird
     */
    private final OntResource getClassResource(final ExtendedIterator<? extends OntResource> ontResources) {
        OntResource ontResourceClass = null;
        while (ontResources.hasNext()) {
            OntResource ontResource = ontResources.next();
            if (ontResource.isClass()) {
                ontResourceClass = ontResource;
                break;
            }
        }
        return ontResourceClass;
    }

    /**
     * List aus der übergebenen URL ein OntModel ein und gibt dieses zurück.
     *
     * @param url
     * @return
     */
    private final OntModel readOntModel(final String url) {
        // Create an ontology model from an URL.
        OntModel ontModel = ModelFactory.createOntologyModel();
        ontModel.read(url);
        return ontModel;
    }

}
