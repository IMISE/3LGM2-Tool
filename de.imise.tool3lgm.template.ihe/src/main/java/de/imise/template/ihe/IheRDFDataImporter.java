package de.imise.template.ihe;

import static de.imise.tool3lgm.imexport.RDFDataImporter.NameCreationPatternStandardIndentifier.LABEL;

import java.io.File;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.jena.ontology.Individual;
import org.apache.jena.ontology.OntClass;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.ontology.OntResource;
import org.apache.jena.rdf.model.ModelFactory;

import com.google.common.collect.ArrayListMultimap;

import de.imise.tool3lgm.Tool3lgmConstants;
import de.imise.tool3lgm.Tool3lgmModelType.ModelCategory;
import de.imise.tool3lgm.graphtools.metamodel.ModelConverter;
import de.imise.tool3lgm.graphtools.metamodel.ModelConverterDefinition;
import de.imise.tool3lgm.graphtools.model.GDCollection;
import de.imise.tool3lgm.graphtools.model.GDCollectionPrinter;
import de.imise.tool3lgm.imexport.ImportMetaModelDefinition;
import de.imise.tool3lgm.imexport.RDFDataImporter;
import de.imise.util.Alphabetical;
import de.imise.util.Sys;

/**
 * @author AXS (26 Jun 2019)
 */
public class IheRDFDataImporter extends RDFDataImporter {

    public static final File TEST_FILE = getRdfFile();
    //     /Users/astruebi/Projekte/eclipse/IMISE/tool-3l gm2/de.imise.tool3lgm/Templates/IHE/iheDomain_Ontology_straight-forward_v2.rdf

    public static final String IHE_RDF_FILE_NAME = "/IHE/iheDomain_Ontology.rdf";

    static File getRdfFile() {
        File templateDir = Tool3lgmConstants.TEMPLATE_DIR;
        String templateDirName = templateDir.getAbsolutePath();
        String thisProjectNameSuffix = ".template.ihe";
        int replaceStart = templateDirName.lastIndexOf(thisProjectNameSuffix);
        if (replaceStart > 0) {
            StringBuilder sb = new StringBuilder(templateDirName);
            sb.replace(replaceStart, replaceStart + thisProjectNameSuffix.length(), "");
            templateDirName = sb.toString();
        }
        return new File(templateDirName, IHE_RDF_FILE_NAME);
    }

    /**
     * Da ObjectProperties u.U. genauso heißen, wie Knoten-Klassen, sollte man
     * mit diesem Namenszusatz bei 3LGM2-Kantenklassen für Eindeutigkeit sorgen.
     */
    private static final String TLGM_EDGE_CLASS_NAME_POSTFIX = "_Edge";

    @Override
    protected String getImportModelDefaultName() {
        return IHE_RDF_FILE_NAME;
    }

    @Override
    public Class<? extends ImportMetaModelDefinition> getImportMetaModelDefinitionClass() {
        return IheImportMetaModelDefinition.class;
    }

    /**
     * @throws MalformedURLException
     */
    public IheRDFDataImporter() throws MalformedURLException {
        this(TEST_FILE);
    }

    /**
     * @param urlString
     * @throws MalformedURLException
     */
    public IheRDFDataImporter(final File file) throws MalformedURLException {
        super(file.toURI().toURL());
        init();
    }

    /**
     *
     */
    private void init() {
        //name von TransaktionsEdges soll OntProperty-Abkürzung (z.B. "ITI-8") + Leerzeichen + Label (z.B. "Patient Identity Feed") sein -> "ITI-8 Patient Identity Feed"
        addNamePattern(IheImportMetaModelDefinition.IheTransaction_Edge.class, new OntPropertyName("abbreviation"), " ", LABEL);
        addNamePattern(IheImportMetaModelDefinition.IntegrationProfile.class, LABEL, " (", new OntPropertyName("abbreviation"), ")");
        addNamePattern(IheImportMetaModelDefinition.Domain.class, LABEL, " (", new OntPropertyName("abbreviation"), ")");
    }

    @Override
    public String getEdgeClassNamePostfix() {
        return TLGM_EDGE_CLASS_NAME_POSTFIX;
    }

    @Override
    public String getDescriptionPropertyUriOrLocalName() {
        return "description";
    }

    ////////////////////////////////////
    //Test the RDF File Quality ////////
    ////////////////////////////////////

    private static void testLoadRDFModelAndImport3LGM2Model() throws MalformedURLException {
        IheRDFDataImporter importer = new IheRDFDataImporter(TEST_FILE);
        importer.logDebug = true;
        final int SINGLE_IMPORTS = 1; //300;
        final int SINGLE_IMPORT_FULL_ROUNDS = 1;//30;

        long start = System.currentTimeMillis();
        for (int i = 0; i < SINGLE_IMPORT_FULL_ROUNDS; i++) {
            long start2 = System.currentTimeMillis();
            for (int j = 0; j < SINGLE_IMPORTS; j++) {
                importer.startImport(ModelCategory.REGULAR);
            }
            long end2 = System.currentTimeMillis();
            System.err.println(end2 - start2);
        }
        long end = System.currentTimeMillis();
        Sys.err(end - start);

        importer.printModel();
        ModelConverterDefinition converterDefinition = new IheModelConverterDefinition();
        GDCollection targetModel = ModelConverter.convert(converterDefinition, importer.getCollection());
        targetModel.setName("Imported OWL Target Model");
        GDCollectionPrinter.print(targetModel);
    }

    private static void testLoadRDFModelAndCheckQuality() throws MalformedURLException {
        try {
            OntModel ontModel = ModelFactory.createOntologyModel();
            String urlString = TEST_FILE.toURI().toURL().toString();
            ontModel.read(urlString);
            ArrayListMultimap<OntClass, Individual> ontClassToIndividuals = ArrayListMultimap.create();

            System.out.println();
            System.out.println("#########################################################################");
            System.out.println("# Check instances of classes against the class returned by the instance #");
            System.out.println("#########################################################################");
            System.out.println();
            for (Iterator<OntClass> ontClasses = ontModel.listNamedClasses(); ontClasses.hasNext();) {
                OntClass ontClass = ontClasses.next();
                String ontClassName = ontClass.getLocalName();
                String ontClassURI = ontClass.getURI();
                System.out.println(">>>>>>> " + ontClassName + " (" + ontClassURI + ")");
                ArrayList<String> messages = new ArrayList<>();
                int i = 1;
                for (Iterator<? extends OntResource> ontNodes = ontClass.listInstances(true); ontNodes.hasNext();) {
                    OntResource ontNode = ontNodes.next();
                    Individual individual = ontNode.asIndividual();
                    ontClassToIndividuals.put(ontClass, individual);
                    OntClass individualOntClass = individual.getOntClass();
                    String individualOntClassName = individualOntClass.getLocalName();
                    String name = getLabel(individual);
                    String id = ontNode.getURI(); //originale URI übernehmen
                    String message = ontClassName + " -> " + name + " (" + id + ")";
                    if (!individualOntClass.equals(ontClass)) {
                        messages.add(message + " WRONG ONT_CLASS: Individual returns class'" + individualOntClassName + "' (" + individualOntClass + ")'");
                    } else {
                        messages.add(message);
                    }
                }
                Alphabetical.sort(messages);
                for (String message : messages) {
                    if (message.endsWith("'")) {
                        message = i++ + " ###\t" + message;
                        System.out.println(message);
                    } else {
                        message = i++ + "\t" + message;
                        System.out.println(message);
                    }
                }
            }
            System.out.println();
            System.out.println("############################################################################################");
            System.out.println("# Check instances of classes that are contained in more than one instances list of a class #");
            System.out.println("############################################################################################");
            System.out.println();
            int i = 1;
            Set<Individual> individualsWithTwoClasses = new HashSet<>();
            for (OntClass ontClass1 : ontClassToIndividuals.keySet()) {
                List<Individual> individualsList1 = ontClassToIndividuals.get(ontClass1);
                for (OntClass ontClass2 : ontClassToIndividuals.keySet()) {
                    if (ontClass1 != ontClass2) {
                        List<Individual> individualsList2 = ontClassToIndividuals.get(ontClass2);
                        for (Individual individual : individualsList1) {
                            if (individualsList2.contains(individual)) {
                                if (individualsWithTwoClasses.add(individual)) {
                                    String ontClass1Name = ontClass1.getLocalName();
                                    String ontClass2Name = ontClass2.getLocalName();
                                    String name = getLabel(individual);
                                    String id = individual.getURI(); //originale URI übernehmen
                                    String message = i++ + "\t" + name + " (" + id + ") is conatined in list of classes '" + ontClass1Name + "' and '" + ontClass2Name + "'";
                                    System.err.println(message);
                                }
                            }
                        }
                    }

                }
            }

        } catch (Exception e) {
            // show a message that a/the template could not be loaded
            e.printStackTrace();
        }
    }

    /**
     * Nur für Testzwecke
     *
     * @param args
     * @throws MalformedURLException
     */
    public static void main(final String[] args) throws MalformedURLException {
        //testLoadRDFModelAndImport3LGM2Model();
        testLoadRDFModelAndCheckQuality();
    }

}
