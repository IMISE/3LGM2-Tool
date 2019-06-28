package de.imise.owl2tlgm;

import de.imise.tool3lgm.graphtools.metamodel.ModelConverter;
import de.imise.tool3lgm.graphtools.metamodel.ModelConverterDefinition;
import de.imise.tool3lgm.graphtools.model.GDCollection;
import de.imise.tool3lgm.graphtools.model.GDCollectionPrinter;
import de.imise.tool3lgm.imexport.ImportMetaModelDefinition;
import de.imise.tool3lgm.imexport.RDFDataImporter;

/**
 * @author AXS (26 Jun 2019)
 */
public class IheRDFDataImporter extends RDFDataImporter {

    public static final String TEST_FILE = "file:///Users/astruebi/Projekte/eclipse/IMISE/tool-3lgm2/de.imise.owl2tlgm/src/main/resources/de/imise/owl2tlgm/iheDomain_Ontology_straight-forward_v2.rdf";

    /**
     * Da ObjectProperties u.U. genauso heißen, wie Knoten-Klassen, sollte man mit diesem Namenszusatz bei 3LGM2-Kantenklassen für Eindeutigkeit
     * sorgen.
     */
    private static final String TLGM_EDGE_CLASS_NAME_POSTFIX = "_Edge";

    /**
     * Nur für Testzwecke
     *
     * @param args
     */
    public static void main(final String[] args) {
        RDFDataImporter importer = new IheRDFDataImporter(TEST_FILE);
        importer.startImport();
        if (importer.isDebug()) {
            importer.printModel();
        }
        ModelConverterDefinition converterDefinition = new IheModelConverterDefinition();
        GDCollection targetModel = ModelConverter.convert(converterDefinition, importer.getCollection());
        targetModel.setName("Imported OWL Target Model");
        if (importer.isDebug()) {
            GDCollectionPrinter.print(targetModel);
        }
    }

    @Override
    public boolean isDebug() {
        return true;
    }

    @Override
    public Class<? extends ImportMetaModelDefinition> getImportMetaModelDefinitionClass() {
        return IheImportMetaModelDefinition.class;
    }

    /**
     *
     */
    public IheRDFDataImporter() {
        super(TEST_FILE);
    }

    /**
     * @param urlString
     */
    public IheRDFDataImporter(final String urlString) {
        super(urlString);
    }

    @Override
    public String getEdgeClassNamePostfix() {
        return TLGM_EDGE_CLASS_NAME_POSTFIX;
    }

}
