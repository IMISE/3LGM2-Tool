package de.imise.template.ihe;

import static de.imise.tool3lgm.imexport.RDFDataImporter.NameCreationPatternStandardIndentifier.LABEL;

import java.io.File;
import java.net.MalformedURLException;

import de.imise.tool3lgm.Tool3lgmConstants;
import de.imise.tool3lgm.Tool3lgmModelType.ModelCategory;
import de.imise.tool3lgm.graphtools.metamodel.ModelConverter;
import de.imise.tool3lgm.graphtools.metamodel.ModelConverterDefinition;
import de.imise.tool3lgm.graphtools.model.GDCollection;
import de.imise.tool3lgm.graphtools.model.GDCollectionPrinter;
import de.imise.tool3lgm.imexport.ImportMetaModelDefinition;
import de.imise.tool3lgm.imexport.RDFDataImporter;
import de.imise.util.Sys;

/**
 * @author AXS (26 Jun 2019)
 */
public class IheRDFDataImporter extends RDFDataImporter {

    public static final File TEST_FILE = getRdfFile();
    //     /Users/astruebi/Projekte/eclipse/IMISE/tool-3l gm2/de.imise.tool3lgm/Templates/IHE/iheDomain_Ontology_straight-forward_v2.rdf

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
        return new File(templateDirName, "/IHE/iheDomain_Ontology_straight-forward_v2.rdf");
    }

    /**
     * Da ObjectProperties u.U. genauso heißen, wie Knoten-Klassen, sollte man mit diesem Namenszusatz bei 3LGM2-Kantenklassen für Eindeutigkeit
     * sorgen.
     */
    private static final String TLGM_EDGE_CLASS_NAME_POSTFIX = "_Edge";

    /**
     * Nur für Testzwecke
     *
     * @param args
     * @throws MalformedURLException
     */
    public static void main(final String[] args) throws MalformedURLException {

        IheRDFDataImporter importer = new IheRDFDataImporter(TEST_FILE);

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
}
