package de.imise.tool3lgm.imexport;

/**
 * Ein {@link DataImporter}, der eine Url als Quelle übergeben bekommt.
 *
 * @author AXS (10 Jun 2019)
 */
public abstract class UrlSourceDataImporter<T> extends DataImporter<T> {

    /** Url zur Datenquelle als String */
    protected String urlString;

    /**
     * @param urlString Url zur Datenquelle als String
     */
    public UrlSourceDataImporter(final String urlString) {
        //TODO: wenn hier null oder ein leerer String übergeben wird, könnte man festlegen, dass die Datenquell-URL aus einer Property-Datei mit demselben Namen wie die Importer-Klasse (this.getCLass().getsimpleName()) und im selben Verzeichnis wie das Jar-File des Importers liegen muss. Dann könnte man das sehr einfach von außen steuern.
        this.urlString = urlString;
    }

    @Override
    protected final boolean importData() {
        boolean imported = importData(urlString);
        if (imported) {
            gdcoll.setName(urlString);
        }
        return imported;
    }

    /**
     * @param urlString Url zur Datenquelle als String. Kann <code>null</code> sein und muss dann durch einen validen Wert ersetzt werden .
     * @return <code>true</code>, wenn der Import erfolgreich war
     */
    protected abstract boolean importData(final String urlString);

}
