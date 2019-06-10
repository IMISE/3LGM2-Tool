package de.imise.tool3lgm.imexport;

import de.imise.tool3lgm.graphtools.model.GDCollection;

/**
 * Ein {@link DataImporter}, der eine Url als Quelle übergeben bekommt.
 *
 * @author AXS (10 Jun 2019)
 */
public abstract class UrlSourceDataImporter extends DataImporter {

    @Override
    protected final void importData(final GDCollection gdcoll) {
        importData(null, gdcoll);
    }

    /**
     * @param url URL zu den Daten. Kann <code>null</code> sein und muss dann durch einen validen Wert ersetzt werden .
     * @param gdcoll Zielmodell des Imports
     */
    protected abstract void importData(final String url, final GDCollection gdcoll);

}
