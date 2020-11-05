package de.imise.tool3lgm.imexport;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

import com.google.common.base.Strings;

/**
 * Ein {@link DataImporter}, der eine Url als Quelle übergeben bekommt.
 *
 * @author AXS (10 Jun 2019)
 */
public abstract class UrlSourceDataImporter<T> extends DataImporter<T> {

    /** Url zur Datenquelle */
    protected URL url;

    /**
     * @param url Url zur Datenquelle
     */
    public UrlSourceDataImporter(final URL url) {
        //TODO: wenn hier null oder ein leerer String übergeben wird, könnte man festlegen, dass die Datenquell-URL aus einer Property-Datei mit demselben Namen wie die Importer-Klasse (this.getCLass().getsimpleName()) und im selben Verzeichnis wie das Jar-File des Importers liegen muss. Dann könnte man das sehr einfach von außen steuern.
        this.url = url;
    }

    /**
     * @param file File zur Datenquelle
     * @throws MalformedURLException
     */
    public UrlSourceDataImporter(final File file) throws MalformedURLException {
        this.url = file.toURI().toURL();
    }

    @Override
    protected final boolean importData() {
        boolean imported = importData(url);
        if (imported) {
            String name = gdcoll.getName();
            if (Strings.isNullOrEmpty(name)) {
                name = url.toString();
                gdcoll.setName(name);
            }
        }
        return imported;
    }

    /**
     * @param url Url zur Datenquelle. Kann <code>null</code> sein und muss dann
     *            durch einen validen Wert ersetzt werden .
     * @return <code>true</code>, wenn der Import erfolgreich war
     */
    protected abstract boolean importData(final URL url);

}
