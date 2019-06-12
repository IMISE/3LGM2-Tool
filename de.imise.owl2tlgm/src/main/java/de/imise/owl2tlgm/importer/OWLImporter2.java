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
import org.apache.jena.util.iterator.ExtendedIterator;

import com.google.common.base.Strings;

import de.imise.owl2tlgm.importmetamodel.IheImportMetaModelDefinition;
import de.imise.tool3lgm.MetaModelContext;
import de.imise.tool3lgm.graphtools.metamodel.MetaModel;
import de.imise.tool3lgm.graphtools.metamodel.elements.Edge;
import de.imise.tool3lgm.graphtools.metamodel.elements.ModelElement;
import de.imise.tool3lgm.graphtools.metamodel.elements.Node;
import de.imise.tool3lgm.graphtools.model.GDCollection;
import de.imise.tool3lgm.graphtools.model.GDCollectionPrinter;
import de.imise.tool3lgm.graphtools.undoredo.TransactionManager;
import de.imise.tool3lgm.graphtools.view.container.NodeContainer;
import de.imise.tool3lgm.imexport.UrlSourceDataImporter;
import de.imise.util.StringUtils;
import de.imise.util.swing.dialog.ExtendedFileChooser;

/**
 * @author Dietmar (28 May 2019)
 */
public class OWLImporter2 extends UrlSourceDataImporter {

    private static final String TEST_FILE = "file:/Users/astruebi/ihaDomain_Ontology_straight-forward_v2_rdf.owl";

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
        //        importer.importData(TEST_FILE, gdcoll);
        importer.importData(gdcoll);
        GDCollectionPrinter.print(gdcoll);
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
            //System.err.println(url);

            boolean oldBulkMode = gdcoll.setBulkMode(true);
            OntModel ontModel = readOntModel(url);

            printOntModel(ontModel);

            Set<String> importableNodeClassNames = importNodes(ontModel, gdcoll);
            importEdges(ontModel, gdcoll, importableNodeClassNames);
            gdcoll.setBulkMode(oldBulkMode);
        }
    }

    /**
     * Gibt alle Objekte im übergebenen OntModel aus
     *
     * @param ontModel
     */
    private void printOntModel(final OntModel ontModel) {
        //print(ontModel.listObjects());

        //print(ontModel.listAllDifferent()); //---
        //print(ontModel.listAllOntProperties()); //hier kommen Unmengen zurück, aber eher ???
        //print(ontModel.listAnnotationProperties()); //http://purl.org/dc/terms/description, http://dbpedia.org/ontology/abbreviation, https://w3id.org/saref4ee#optionalSlot
        //print(ontModel.listClasses()); // +++ alle 5 Klassen!
        //print(ontModel.listComplementClasses()); //---
        //print(ontModel.listDataRanges()); //---
        //print(ontModel.listDatatypeProperties()); //???
        //            http://www.semanticweb.org/sippe/ontologies/2017/1/3lgm2ihe#status -> class org.apache.jena.ontology.impl.DatatypePropertyImpl
        //            http://www.semanticweb.org/sippe/ontologies/2017/1/3lgm2ihe#integrationProfile_dataProperty -> class org.apache.jena.ontology.impl.DatatypePropertyImpl
        //            http://www.semanticweb.org/sippe/ontologies/2017/1/3lgm2ihe#iheDomain_dataProperty -> class org.apache.jena.ontology.impl.DatatypePropertyImpl
        //            http://www.semanticweb.org/sippe/ontologies/2017/1/3lgm2ihe#helper -> class org.apache.jena.ontology.impl.DatatypePropertyImpl
        //            http://ns.nature.com/terms/PdfAsset -> class org.apache.jena.ontology.impl.DatatypePropertyImpl
        //print(ontModel.listEnumeratedClasses()); //---
        //print(ontModel.listFunctionalProperties()); // ???
        //            http://www.semanticweb.org/sippe/ontologies/2017/1/3lgm2ihe#invokes -> class org.apache.jena.ontology.impl.FunctionalPropertyImpl
        //            http://www.semanticweb.org/sippe/ontologies/2017/1/3lgm2ihe#iheIntegrationProfile_IheDomain_Edge_f -> class org.apache.jena.ontology.impl.FunctionalPropertyImpl
        //            http://www.semanticweb.org/sippe/ontologies/2017/1/3lgm2ihe#iheIntegrationProfile_IheActor_Edge_b -> class org.apache.jena.ontology.impl.FunctionalPropertyImpl
        //            http://www.semanticweb.org/sippe/ontologies/2017/1/3lgm2ihe#iheIntegrationProfile -> class org.apache.jena.ontology.impl.FunctionalPropertyImpl
        //            http://www.semanticweb.org/sippe/ontologies/2017/1/3lgm2ihe#iheDomain -> class org.apache.jena.ontology.impl.FunctionalPropertyImpl

        //print(ontModel.listHierarchyRootClasses()); //http://www.semanticweb.org/sippe/ontologies/2017/1/3lgm2ihe#IHEConcept
        //print(ontModel.listImportedModels()); //---
        //print(ontModel.listIndividuals()); ///+++
        //            http://www.semanticweb.org/sippe/ontologies/2017/1/3lgm2ihe#EUA -> class org.apache.jena.ontology.impl.IndividualImpl
        //            http://www.semanticweb.org/sippe/ontologies/2017/1/3lgm2ihe#XDR -> class org.apache.jena.ontology.impl.IndividualImpl
        //            http://www.semanticweb.org/sippe/ontologies/2017/1/3lgm2ihe#XDS-SD -> class org.apache.jena.ontology.impl.IndividualImpl
        //            http://www.semanticweb.org/sippe/ontologies/2017/1/3lgm2ihe#PIX_PAT_IDENTITY_SRC -> class org.apache.jena.ontology.impl.IndividualImpl
        //            http://www.semanticweb.org/sippe/ontologies/2017/1/3lgm2ihe#PIX_PAT_IDENTITY_CONSUMER -> class org.apache.jena.ontology.impl.IndividualImpl
        //            http://www.semanticweb.org/sippe/ontologies/2017/1/3lgm2ihe#BPPC -> class org.apache.jena.ontology.impl.IndividualImpl
        //            http://www.semanticweb.org/sippe/ontologies/2017/1/3lgm2ihe#PCC -> class org.apache.jena.ontology.impl.IndividualImpl
        //            http://www.semanticweb.org/sippe/ontologies/2017/1/3lgm2ihe#DSUB -> class org.apache.jena.ontology.impl.IndividualImpl
        //            http://www.semanticweb.org/sippe/ontologies/2017/1/3lgm2ihe#CARD -> class org.apache.jena.ontology.impl.IndividualImpl
        //            http://www.semanticweb.org/sippe/ontologies/2017/1/3lgm2ihe#PaLM -> class org.apache.jena.ontology.impl.IndividualImpl
        //            http://www.semanticweb.org/sippe/ontologies/2017/1/3lgm2ihe#DSG -> class org.apache.jena.ontology.impl.IndividualImpl
        //            http://www.semanticweb.org/sippe/ontologies/2017/1/3lgm2ihe#PIX_PAT_IDENTITY_X_REF_MGR -> class org.apache.jena.ontology.impl.IndividualImpl
        //            http://www.semanticweb.org/sippe/ontologies/2017/1/3lgm2ihe#PIX -> class org.apache.jena.ontology.impl.IndividualImpl
        //            http://www.semanticweb.org/sippe/ontologies/2017/1/3lgm2ihe#PSA -> class org.apache.jena.ontology.impl.IndividualImpl
        //            http://www.semanticweb.org/sippe/ontologies/2017/1/3lgm2ihe#XCPD -> class org.apache.jena.ontology.impl.IndividualImpl
        //            http://www.semanticweb.org/sippe/ontologies/2017/1/3lgm2ihe#XDSB_DOC_REGISTRY -> class org.apache.jena.ontology.impl.IndividualImpl
        //            http://www.semanticweb.org/sippe/ontologies/2017/1/3lgm2ihe#XUA -> class org.apache.jena.ontology.impl.IndividualImpl
        //            http://www.semanticweb.org/sippe/ontologies/2017/1/3lgm2ihe#RID -> class org.apache.jena.ontology.impl.IndividualImpl
        //            http://www.semanticweb.org/sippe/ontologies/2017/1/3lgm2ihe#XDSB_DOC_CONSUMER -> class org.apache.jena.ontology.impl.IndividualImpl
        //            http://www.semanticweb.org/sippe/ontologies/2017/1/3lgm2ihe#PDQV3 -> class org.apache.jena.ontology.impl.IndividualImpl
        //            http://www.semanticweb.org/sippe/ontologies/2017/1/3lgm2ihe#PWP -> class org.apache.jena.ontology.impl.IndividualImpl
        //            http://www.semanticweb.org/sippe/ontologies/2017/1/3lgm2ihe#XDSB_DOC_ADMINISTRATOR -> class org.apache.jena.ontology.impl.IndividualImpl
        //            http://www.semanticweb.org/sippe/ontologies/2017/1/3lgm2ihe#QRPH -> class org.apache.jena.ontology.impl.IndividualImpl
        //            http://www.semanticweb.org/sippe/ontologies/2017/1/3lgm2ihe#XDSB_DOC_SOURCE -> class org.apache.jena.ontology.impl.IndividualImpl
        //            http://www.semanticweb.org/sippe/ontologies/2017/1/3lgm2ihe#RAD -> class org.apache.jena.ontology.impl.IndividualImpl
        //            http://www.semanticweb.org/sippe/ontologies/2017/1/3lgm2ihe#MPQ -> class org.apache.jena.ontology.impl.IndividualImpl
        //            http://www.semanticweb.org/sippe/ontologies/2017/1/3lgm2ihe#XDSB_EMBED_REPOS -> class org.apache.jena.ontology.impl.IndividualImpl
        //            http://www.semanticweb.org/sippe/ontologies/2017/1/3lgm2ihe#PCD -> class org.apache.jena.ontology.impl.IndividualImpl
        //            http://www.semanticweb.org/sippe/ontologies/2017/1/3lgm2ihe#RFD -> class org.apache.jena.ontology.impl.IndividualImpl
        //            http://www.semanticweb.org/sippe/ontologies/2017/1/3lgm2ihe#SVS -> class org.apache.jena.ontology.impl.IndividualImpl
        //            http://www.semanticweb.org/sippe/ontologies/2017/1/3lgm2ihe#XDSB_PAT_IDENTITY_SRC -> class org.apache.jena.ontology.impl.IndividualImpl
        //            http://www.semanticweb.org/sippe/ontologies/2017/1/3lgm2ihe#ITI -> class org.apache.jena.ontology.impl.IndividualImpl
        //            http://www.semanticweb.org/sippe/ontologies/2017/1/3lgm2ihe#ATNA -> class org.apache.jena.ontology.impl.IndividualImpl
        //            http://www.semanticweb.org/sippe/ontologies/2017/1/3lgm2ihe#XCA -> class org.apache.jena.ontology.impl.IndividualImpl
        //            http://www.semanticweb.org/sippe/ontologies/2019/3/untitled-ontology-31#RO -> class org.apache.jena.ontology.impl.IndividualImpl
        //            http://www.semanticweb.org/sippe/ontologies/2017/1/3lgm2ihe#PDQ -> class org.apache.jena.ontology.impl.IndividualImpl
        //            http://www.semanticweb.org/sippe/ontologies/2017/1/3lgm2ihe#PAM -> class org.apache.jena.ontology.impl.IndividualImpl
        //            http://www.semanticweb.org/sippe/ontologies/2017/1/3lgm2ihe#XDSB_ON_DEMAND_DOC_SOURCE -> class org.apache.jena.ontology.impl.IndividualImpl
        //            http://www.semanticweb.org/sippe/ontologies/2017/1/3lgm2ihe#XDSB_DOC_REPOSITORY -> class org.apache.jena.ontology.impl.IndividualImpl
        //            http://www.semanticweb.org/sippe/ontologies/2017/1/3lgm2ihe#XDM -> class org.apache.jena.ontology.impl.IndividualImpl
        //            http://www.semanticweb.org/sippe/ontologies/2017/1/3lgm2ihe#PHARM -> class org.apache.jena.ontology.impl.IndividualImpl
        //            http://www.semanticweb.org/sippe/ontologies/2017/1/3lgm2ihe#PIXV3 -> class org.apache.jena.ontology.impl.IndividualImpl
        //            http://www.semanticweb.org/sippe/ontologies/2017/1/3lgm2ihe#CT -> class org.apache.jena.ontology.impl.IndividualImpl
        //            http://www.semanticweb.org/sippe/ontologies/2017/1/3lgm2ihe#XDS.b -> class org.apache.jena.ontology.impl.IndividualImpl

        //print(ontModel.listIntersectionClasses()); //---
        //print(ontModel.listInverseFunctionalProperties());
        //print(ontModel.listNamedClasses()); //+++ dasselbe wie listClasses() also alle 5 Klassen
        //print(ontModel.listObjectProperties()); // ???
        //            http://www.semanticweb.org/sippe/ontologies/2017/1/3lgm2ihe#iti-57 -> class org.apache.jena.ontology.impl.ObjectPropertyImpl
        //            http://www.semanticweb.org/sippe/ontologies/2017/1/3lgm2ihe#toDo -> class org.apache.jena.ontology.impl.ObjectPropertyImpl
        //            http://www.semanticweb.org/sippe/ontologies/2017/1/3lgm2ihe#iheIntegrationProfile_IheDomain_Edge_b -> class org.apache.jena.ontology.impl.ObjectPropertyImpl
        //            http://www.semanticweb.org/sippe/ontologies/2017/1/3lgm2ihe#iheIntegrationProfile_IheActor_Edge -> class org.apache.jena.ontology.impl.ObjectPropertyImpl
        //            http://www.semanticweb.org/sippe/ontologies/2017/1/3lgm2ihe#iheIntegrationProfile_iheDomain_Edge -> class org.apache.jena.ontology.impl.ObjectPropertyImpl
        //            http://www.semanticweb.org/sippe/ontologies/2017/1/3lgm2ihe#invokes -> class org.apache.jena.ontology.impl.ObjectPropertyImpl
        //            http://www.semanticweb.org/sippe/ontologies/2017/1/3lgm2ihe#iti-18 -> class org.apache.jena.ontology.impl.ObjectPropertyImpl
        //            http://www.semanticweb.org/sippe/ontologies/2017/1/3lgm2ihe#iheTransaction -> class org.apache.jena.ontology.impl.ObjectPropertyImpl
        //            http://www.semanticweb.org/sippe/ontologies/2017/1/3lgm2ihe#iti-42 -> class org.apache.jena.ontology.impl.ObjectPropertyImpl
        //            http://www.semanticweb.org/sippe/ontologies/2017/1/3lgm2ihe#iheIntegrationProfile_IheActor_Edge_b -> class org.apache.jena.ontology.impl.ObjectPropertyImpl
        //            http://www.semanticweb.org/sippe/ontologies/2017/1/3lgm2ihe#iti-61 -> class org.apache.jena.ontology.impl.ObjectPropertyImpl
        //            http://www.semanticweb.org/sippe/ontologies/2017/1/3lgm2ihe#iti-9 -> class org.apache.jena.ontology.impl.ObjectPropertyImpl
        //            http://www.semanticweb.org/sippe/ontologies/2017/1/3lgm2ihe#provides_optional -> class org.apache.jena.ontology.impl.ObjectPropertyImpl
        //            http://www.semanticweb.org/sippe/ontologies/2017/1/3lgm2ihe#provides -> class org.apache.jena.ontology.impl.ObjectPropertyImpl
        //            http://www.semanticweb.org/sippe/ontologies/2017/1/3lgm2ihe#iheDomain -> class org.apache.jena.ontology.impl.ObjectPropertyImpl
        //            http://www.semanticweb.org/sippe/ontologies/2017/1/3lgm2ihe#iti-41 -> class org.apache.jena.ontology.impl.ObjectPropertyImpl
        //            http://www.semanticweb.org/sippe/ontologies/2017/1/3lgm2ihe#iti-10 -> class org.apache.jena.ontology.impl.ObjectPropertyImpl
        //            http://www.semanticweb.org/sippe/ontologies/2017/1/3lgm2ihe#provides_mandatory -> class org.apache.jena.ontology.impl.ObjectPropertyImpl
        //            http://www.semanticweb.org/sippe/ontologies/2017/1/3lgm2ihe#iti-44 -> class org.apache.jena.ontology.impl.ObjectPropertyImpl
        //            http://www.semanticweb.org/sippe/ontologies/2017/1/3lgm2ihe#3LGM -> class org.apache.jena.ontology.impl.ObjectPropertyImpl
        //            http://www.semanticweb.org/sippe/ontologies/2017/1/3lgm2ihe#iti-30 -> class org.apache.jena.ontology.impl.ObjectPropertyImpl
        //            http://www.semanticweb.org/sippe/ontologies/2017/1/3lgm2ihe#iheIntegrationProfile -> class org.apache.jena.ontology.impl.ObjectPropertyImpl
        //            http://www.semanticweb.org/sippe/ontologies/2017/1/3lgm2ihe#iheIntegrationProfile_IheActor_Edge_f -> class org.apache.jena.ontology.impl.ObjectPropertyImpl
        //            http://www.semanticweb.org/sippe/ontologies/2017/1/3lgm2ihe#iti-43 -> class org.apache.jena.ontology.impl.ObjectPropertyImpl
        //            http://www.semanticweb.org/sippe/ontologies/2017/1/3lgm2ihe#iheIntegrationProfile_IheDomain_Edge_f -> class org.apache.jena.ontology.impl.ObjectPropertyImpl
        //            http://www.semanticweb.org/sippe/ontologies/2017/1/3lgm2ihe#iti-8 -> class org.apache.jena.ontology.impl.ObjectPropertyImpl

        //print(ontModel.listOntologies()); //+++ http://www.semanticweb.org/sippe/ontologies/2017/1/3lgm2ihe -> class org.apache.jena.ontology.impl.OntologyImpl
        //print(ontModel.listOntProperties()); //???
        //            http://www.w3.org/1999/02/22-rdf-syntax-ns#first -> class org.apache.jena.ontology.impl.OntPropertyImpl
        //            http://www.w3.org/1999/02/22-rdf-syntax-ns#object -> class org.apache.jena.ontology.impl.OntPropertyImpl
        //            http://www.w3.org/1999/02/22-rdf-syntax-ns#subject -> class org.apache.jena.ontology.impl.OntPropertyImpl
        //            http://www.w3.org/1999/02/22-rdf-syntax-ns#predicate -> class org.apache.jena.ontology.impl.OntPropertyImpl
        //            http://www.w3.org/1999/02/22-rdf-syntax-ns#rest -> class org.apache.jena.ontology.impl.OntPropertyImpl
        //            http://www.semanticweb.org/sippe/ontologies/2017/1/3lgm2ihe#iheTransaction -> class org.apache.jena.ontology.impl.OntPropertyImpl
        //            http://www.semanticweb.org/sippe/ontologies/2017/1/3lgm2ihe#iheIntegrationProfile_IheActor_Edge -> class org.apache.jena.ontology.impl.OntPropertyImpl
        //            http://www.semanticweb.org/sippe/ontologies/2017/1/3lgm2ihe#provides_optional -> class org.apache.jena.ontology.impl.OntPropertyImpl
        //            http://www.semanticweb.org/sippe/ontologies/2017/1/3lgm2ihe#helper -> class org.apache.jena.ontology.impl.OntPropertyImpl
        //            http://www.semanticweb.org/sippe/ontologies/2017/1/3lgm2ihe#toDo -> class org.apache.jena.ontology.impl.OntPropertyImpl
        //            http://www.semanticweb.org/sippe/ontologies/2017/1/3lgm2ihe#iheIntegrationProfile_iheDomain_Edge -> class org.apache.jena.ontology.impl.OntPropertyImpl
        //            http://www.w3.org/2002/07/owl#topObjectProperty -> class org.apache.jena.ontology.impl.OntPropertyImpl
        //            http://www.semanticweb.org/sippe/ontologies/2017/1/3lgm2ihe#3LGM -> class org.apache.jena.ontology.impl.OntPropertyImpl
        //            http://www.w3.org/2000/01/rdf-schema#isDefinedBy -> class org.apache.jena.ontology.impl.OntPropertyImpl
        //            http://www.w3.org/2000/01/rdf-schema#seeAlso -> class org.apache.jena.ontology.impl.OntPropertyImpl
        //            http://www.semanticweb.org/sippe/ontologies/2017/1/3lgm2ihe#provides -> class org.apache.jena.ontology.impl.OntPropertyImpl
        //            http://www.semanticweb.org/sippe/ontologies/2017/1/3lgm2ihe#invokes -> class org.apache.jena.ontology.impl.OntPropertyImpl
        //            http://www.semanticweb.org/sippe/ontologies/2017/1/3lgm2ihe#iheDomain_dataProperty -> class org.apache.jena.ontology.impl.OntPropertyImpl
        //            http://www.semanticweb.org/sippe/ontologies/2017/1/3lgm2ihe#iheIntegrationProfile_IheActor_Edge_b -> class org.apache.jena.ontology.impl.OntPropertyImpl
        //            http://www.semanticweb.org/sippe/ontologies/2017/1/3lgm2ihe#iti-43 -> class org.apache.jena.ontology.impl.OntPropertyImpl
        //            http://www.semanticweb.org/sippe/ontologies/2017/1/3lgm2ihe#provides_mandatory -> class org.apache.jena.ontology.impl.OntPropertyImpl
        //            http://www.semanticweb.org/sippe/ontologies/2017/1/3lgm2ihe#iti-41 -> class org.apache.jena.ontology.impl.OntPropertyImpl
        //            http://www.semanticweb.org/sippe/ontologies/2017/1/3lgm2ihe#integrationProfile_dataProperty -> class org.apache.jena.ontology.impl.OntPropertyImpl
        //            http://www.semanticweb.org/sippe/ontologies/2017/1/3lgm2ihe#iheIntegrationProfile -> class org.apache.jena.ontology.impl.OntPropertyImpl
        //            http://www.semanticweb.org/sippe/ontologies/2017/1/3lgm2ihe#iti-57 -> class org.apache.jena.ontology.impl.OntPropertyImpl
        //            http://www.semanticweb.org/sippe/ontologies/2017/1/3lgm2ihe#iti-9 -> class org.apache.jena.ontology.impl.OntPropertyImpl
        //            http://www.semanticweb.org/sippe/ontologies/2017/1/3lgm2ihe#iheIntegrationProfile_IheActor_Edge_f -> class org.apache.jena.ontology.impl.OntPropertyImpl
        //            http://www.semanticweb.org/sippe/ontologies/2017/1/3lgm2ihe#iheIntegrationProfile_IheDomain_Edge_f -> class org.apache.jena.ontology.impl.OntPropertyImpl
        //            http://www.semanticweb.org/sippe/ontologies/2017/1/3lgm2ihe#iti-61 -> class org.apache.jena.ontology.impl.OntPropertyImpl
        //            http://www.semanticweb.org/sippe/ontologies/2017/1/3lgm2ihe#iti-18 -> class org.apache.jena.ontology.impl.OntPropertyImpl
        //            http://www.semanticweb.org/sippe/ontologies/2017/1/3lgm2ihe#iheDomain -> class org.apache.jena.ontology.impl.OntPropertyImpl
        //            http://www.semanticweb.org/sippe/ontologies/2017/1/3lgm2ihe#iti-44 -> class org.apache.jena.ontology.impl.OntPropertyImpl
        //            http://www.semanticweb.org/sippe/ontologies/2017/1/3lgm2ihe#iti-10 -> class org.apache.jena.ontology.impl.OntPropertyImpl
        //            http://www.semanticweb.org/sippe/ontologies/2017/1/3lgm2ihe#iti-8 -> class org.apache.jena.ontology.impl.OntPropertyImpl
        //            http://www.semanticweb.org/sippe/ontologies/2017/1/3lgm2ihe#iti-42 -> class org.apache.jena.ontology.impl.OntPropertyImpl
        //            http://www.semanticweb.org/sippe/ontologies/2017/1/3lgm2ihe#iti-30 -> class org.apache.jena.ontology.impl.OntPropertyImpl
        //            http://www.semanticweb.org/sippe/ontologies/2017/1/3lgm2ihe#iheIntegrationProfile_IheDomain_Edge_b -> class org.apache.jena.ontology.impl.OntPropertyImpl
        //            http://www.w3.org/2000/01/rdf-schema#subClassOf -> class org.apache.jena.ontology.impl.OntPropertyImpl
        //            http://www.w3.org/2000/01/rdf-schema#domain -> class org.apache.jena.ontology.impl.OntPropertyImpl
        //            http://www.w3.org/2000/01/rdf-schema#subPropertyOf -> class org.apache.jena.ontology.impl.OntPropertyImpl
        //            http://www.w3.org/2000/01/rdf-schema#range -> class org.apache.jena.ontology.impl.OntPropertyImpl
        //            http://www.w3.org/2000/01/rdf-schema#comment -> class org.apache.jena.ontology.impl.OntPropertyImpl
        //            http://www.w3.org/1999/02/22-rdf-syntax-ns#type -> class org.apache.jena.ontology.impl.OntPropertyImpl
        //            http://www.w3.org/2000/01/rdf-schema#label -> class org.apache.jena.ontology.impl.OntPropertyImpl
        //            http://ns.nature.com/terms/PdfAsset -> class org.apache.jena.ontology.impl.OntPropertyImpl
        //            http://www.semanticweb.org/sippe/ontologies/2017/1/3lgm2ihe#status -> class org.apache.jena.ontology.impl.OntPropertyImpl
        //            https://w3id.org/saref4ee#optionalSlot -> class org.apache.jena.ontology.impl.AnnotationPropertyImpl
        //            http://www.semanticweb.org/sippe/ontologies/2017/1/3lgm2ihe#3lgm-classname-edge -> class org.apache.jena.ontology.impl.AnnotationPropertyImpl
        //            http://purl.org/dc/terms/description -> class org.apache.jena.ontology.impl.AnnotationPropertyImpl
        //            http://dbpedia.org/ontology/abbreviation -> class org.apache.jena.ontology.impl.AnnotationPropertyImpl

        //print(ontModel.listRestrictions()); //---
        //print(ontModel.listSubModels()); //---
        //print(ontModel.listSymmetricProperties()); //---
        //print(ontModel.listTransitiveProperties()); //---
        //print(ontModel.listUnionClasses()); //---
        //print(ontModel.listNameSpaces()); // ???
        //            http://www.w3.org/1999/02/22-rdf-syntax-ns# -> class java.lang.String
        //            http://purl.org/dc/terms/ -> class java.lang.String
        //            http://www.w3.org/2002/07/owl# -> class java.lang.String
        //            http://dbpedia.org/ontology/ -> class java.lang.String
        //            https://w3id.org/saref4ee# -> class java.lang.String
        //            http://www.w3.org/2000/01/rdf-schema# -> class java.lang.String
        //            http://www.semanticweb.org/sippe/ontologies/2017/1/3lgm2ihe#3 -> class java.lang.String
        //            http://www.semanticweb.org/sippe/ontologies/2017/1/3lgm2ihe# -> class java.lang.String

        //print(ontModel.listReifiedStatements()); //---
        print(ontModel.listStatements()); // +++ hier kommen alle Triple als StatementImpl zurück -> hieraus die Kanten ableiten
        //print(ontModel.listSubjects()); //hier kommen Unmengen zurück, aber eher ???
    }

    private void print(final ExtendedIterator<?> it) {
        System.out.println();
        while (it.hasNext()) {
            Object o = it.next();
            System.out.println(o + " -> " + o.getClass());
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
     * @return Set von Namen gültiger Knoten in der Ontologie (also Klassenknoten, die durch OntProperties verbunden sein können)
     */
    private Set<String> importNodes(final OntModel ontModel, final GDCollection gdcoll) {
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
                String hashString = null; //??? woher aus der Ontologie oder ID als extra Angabe setzten? Irgendwas muss eine eindeutige Referenz in die Ontologie sein
                NodeContainer nodeContainer = gdcoll.createKnotenWithContainer(nodeClass, name, description, hashString, TransactionManager.STANDARD_PID);
                ModelElement modelElement = nodeContainer.getElement();

                System.err.println(ontResource + "\t\t<-->\t\t" + modelElement);
                rdfSourceInstanceToModelElement.put(ontResource, modelElement);
            }
        }
        return simpleNodeClassNames;
    }

    /**
     * Fragt im Quellmodell alle ObjectProperties ab, deren Klassenname auch im Metamodell des Zielmodells als Kantenklassenname vorkommt. Dann werden
     * im Zielmodell die entsprechenden Elemente verlinkt, die die Objectproperty im Quellmodell verbindet.
     *
     * @param ontModel Quellmodell
     * @param gdcoll Zielmodell
     * @param importableObjectPropertyNames Namen aller importierbarer Knotenklassen, die als Domain oder Range der Kante in Frage kommen
     */
    private void importEdges(final OntModel ontModel, final GDCollection gdcoll, final Set<String> importableObjectPropertyNames) {
        MetaModel metaModel = gdcoll.getMetaModel();
        Set<Class<? extends Edge>> allEdgesSet = metaModel.allEdgesSet;
        Set<String> objectPropertyNames = getSimpleClassNames(allEdgesSet, TLGM_EDGE_CLASS_NAME_POSTFIX);
        ExtendedIterator<ObjectProperty> objectProperties = ontModel.listObjectProperties();
        while (objectProperties.hasNext()) {
            ObjectProperty objectProperty = objectProperties.next();
            String name = objectProperty.getLocalName();
            String superPropertyName = name;
            if (!objectPropertyNames.contains(name)) {
                superPropertyName = getImportableSuperObjectPropertyName(objectProperty, objectPropertyNames);
                if (Strings.isNullOrEmpty(superPropertyName)) {
                    continue;
                }
            }
            String edgeClassName = superPropertyName + TLGM_EDGE_CLASS_NAME_POSTFIX;
            edgeClassName = StringUtils.capitalizeFirstChar(edgeClassName); //Kantenklassennamen sind immer groß geschrieben - zugehörige, gleich heißende EdgeProperty evtl. klein -> Umwandeln

            String edgeHash = null; //evtl aus der Import-Quelle?
            //Das hier ist Quatsch, weil ich hier nach Klassen Frage (Ergebnis ist IheActor) aber Instanzen von Actor haben möchte -> hier muss nicht die Domain und der Range ermittelt werden, sondern
            //das, was im Protege bei Use von den ObjectProperties angezeigt wird und das bekommt man anscheinend nur über die ontModel.listStatements() und einige Mappings über die IRIs
            // hier rüber muss das eigentlich laufen https://jena.apache.org/tutorials/rdf_api.html#ch-Statements
            OntResource domainClass = getClassResource(objectProperty, importableObjectPropertyNames, true);
            OntResource rangeClass = getClassResource(objectProperty, importableObjectPropertyNames, false);
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
     * @return wird eine passende Super-Property gefeunden, kommt deren Name zurück, sonst <code>null</code>
     */
    private final String getImportableSuperObjectPropertyName(final OntProperty objectProperty, final Set<String> importableObjectPropertyNames) {
        ExtendedIterator<? extends OntProperty> superProperties = objectProperty.listSuperProperties();
        while (superProperties.hasNext()) {
            OntProperty superProperty = superProperties.next();
            if (superProperty instanceof OntProperty) {
                String name = superProperty.getLocalName();
                if (importableObjectPropertyNames.contains(name)) {
                    return name;
                }
                return getImportableSuperObjectPropertyName(superProperty, importableObjectPropertyNames);
            }
        }
        return null;
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
     * Liefert von einer ObjectProperty selbst oder von einer Oberklasse ein Domain oder Range.
     *
     * @param objectProperty die ObjectProperty, von der selbst oder von einer Oberklasse die Domain oder Range ermittelt werden soll
     * @param validLocalNames alle LocalNames gültiger Range oder Domain Objekte
     * @param domain wenn <code>true</code> wird die Domain ermittelt, sonst der Range
     * @return gefundene OntResoource der Domain oder des Ranges oder <code>null</code>, wenn nichts gefunden wird
     */
    public final OntResource getClassResource(final OntProperty objectProperty, final Set<String> validLocalNames, final boolean domain) {
        OntResource classResource = getClassResource(domain ? objectProperty.listDomain() : objectProperty.listRange(), validLocalNames);
        if (classResource == null) {
            ExtendedIterator<? extends OntProperty> superProperties = objectProperty.listSuperProperties();
            while (superProperties.hasNext()) {
                OntProperty superProperty = superProperties.next();
                classResource = getClassResource(superProperty, validLocalNames, domain);
                if (classResource != null) {
                    break;
                }
            }
        }
        return classResource;
    }

    /**
     * Holt aus dem übergebenen Iterator die erste {@link OntResource}, die bei {@link OntResource#isClass()}<code>==true</code> liefert und gibt
     * diese zurück, wenn der Name im übergebenen Set vorkommt.
     *
     * @param ontResources
     *            Iterator aller zu durchsuchenden {@link OntResource}
     * @param validLocalNames
     *            alle LocalNames von Objekten, die zurück kommen können
     * @return die erste {@link OntResource}, die eine Klasse mit einem der übergebenen namen ist oder <code>null</code>, wenn keine solche Resource
     *         gefunden wird
     */
    private final OntResource getClassResource(final ExtendedIterator<? extends OntResource> ontResources, final Set<String> validLocalNames) {
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
