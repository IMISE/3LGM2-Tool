package de.imise.tool3lgm.imexport;

import de.imise.tool3lgm.MetaModelContext;
import de.imise.tool3lgm.graphtools.model.GDCollection;

/**
 * Oberklasse für Importer, die in ein Modell importieren
 *
 * @author AXS (7 Jun 2019)
 */
public abstract class DataImporter {

    /**
     * Importiert alle Daten aus einer Datenquelle in das übergebene Modell.
     *
     * @param gdcoll Zielmodell des Imports
     */
    protected abstract void importData(GDCollection gdcoll);

    /**
     * @param metaModelContext MetaModelContext mit dem Metamodell, mit dem das zurückgeleiferte Modell initialisiert wird
     * @return ein neues Modell mit dem übergebenen Metamodell, in das importiert wurde
     */
    public GDCollection importData(final MetaModelContext metaModelContext) {
        GDCollection gdcoll = new GDCollection(metaModelContext);
        importData(gdcoll);
        return gdcoll;
    }

}
