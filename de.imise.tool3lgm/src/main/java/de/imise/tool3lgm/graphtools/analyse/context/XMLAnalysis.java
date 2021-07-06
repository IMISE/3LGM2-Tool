/*
 * Created on 14.06.2004
 */
package de.imise.tool3lgm.graphtools.analyse.context;

import static org.apache.commons.lang3.StringUtils.isBlank;

import java.io.IOException;
import java.io.StringReader;
import java.util.List;
import java.util.Locale;
import java.util.StringTokenizer;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.commons.lang3.StringUtils;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import com.google.common.base.Objects;

import de.imise.tool3lgm.MetaModelContext;
import de.imise.tool3lgm.graphtools.IDSource;
import de.imise.tool3lgm.graphtools.metamodel.MetaModel;
import de.imise.tool3lgm.graphtools.metamodel.elements.ModelElement;
import de.imise.tool3lgm.graphtools.model.GraphDocument;
import de.imise.tool3lgm.graphtools.view.container.ElementContainer;
import de.imise.tool3lgm.log.Log;
import de.imise.tool3lgm.userproperties.UserProperties;

/**
 * Diese Klasse repräsentiert eine einzelne XMLAnalyse.
 *
 * @author Sebastian Weber
 */
public final class XMLAnalysis extends AbstractAnalysis {

    /** der Text der die Analyse beschreibt. */
    private String xmlText;

    /**
     * @param metaModelContext
     */
    private XMLAnalysis(final MetaModelContext metaModelContext, final String id) {
        super(metaModelContext, id);
    }

    /**
     * @param metaModelContext
     * @return
     * @throws SAXException
     */
    public static XMLAnalysis createAnalysis(final MetaModelContext metaModelContext) throws SAXException {
        return createAnalysis(metaModelContext, "", IDSource.createIDString("ANA"));
    }

    /**
     * Erzeugt eine neue XMLAnalyse mit den übergebenen Parametern.
     *
     * @param metaModelContext der Context des Metamodels, für das die Analyse
     *            anwendbar ist
     * @param name Name der XMLAnalyse.
     * @param analyseText die XMLAnalyse als XML-Text.
     * @return wenn kein Fehler beim erzeugen der XMLAnalyse auftrat, wird eine
     *         neue XMLAnalyse mit den übergebenen Parametern zurück gegeben.
     * @throws SAXException wenn ein Fehler beim parsen des Analysetextes
     *             auftritt.
     */
    public static XMLAnalysis createAnalysis(final MetaModelContext metaModelContext, final String analyseText, final String id) throws SAXException {
        XMLAnalysis xMLAnalysis = new XMLAnalysis(metaModelContext, id);
        xMLAnalysis.setXMLText(analyseText);
        return xMLAnalysis;
    }

    /**
     * Gibt den Analysetext zurück.
     *
     * @return der Text der die XMLAnalyse beschreibt.
     */
    public String getXMLText() {
        return xmlText;
    }

    /**
     * Setzt eine neue Beschreibung für diese XMLAnalyse.
     *
     * @param xmlText der Text der die XMLAnalyse beschreibt.
     * @throws SAXException wenn ein Fehler beim parsen des Analysetextes
     *             auftritt.
     */
    public void setXMLText(String xmlText) throws SAXException {
        if (isBlank(xmlText)) {
            xmlText = "<analyse>\n\n</analyse>";
        }

        // alte Startknoten speichern
        List<Class<? extends ModelElement>> oldStartNodes = getStartClasses();
        clearStartClasses();

        try {
            parseAnalysis(xmlText);
        } catch (SAXException e) {
            // alte Werte wieder herstellen
            startClasses = oldStartNodes;
            throw e;
        }
        this.xmlText = xmlText;
    }

    /**
     * Fügt einen Startklasse hinzu.
     *
     * @param startClassName der neue Name des Startknotens.
     */
    private void addStartClass(final String startClassName) {
        MetaModel metaModel = getMetaModel();
        Class<? extends ModelElement> startClass = metaModel.getClassForName(startClassName);
        if (startClass != null) {
            startClasses.add(startClass);
        }
    }

    /**
     * Löscht die Liste der Startklassen.
     */
    private final void clearStartClasses() {
        if (startClasses != null) {
            startClasses.clear();
        }
    }

    /**
     * DocumentHandler für den XML-Analysetext.
     *
     * @author Sebastian Weber
     */
    private class AnalysisParser extends DefaultHandler {

        private boolean inAnalysisNameTag = false;

        private final StringBuilder valueBuilder = new StringBuilder();

        @Override
        public void startElement(final String namespaceURI, final String localName, final String qName, final Attributes atts) {
            if (qName.equalsIgnoreCase("startknoten")) {
                String startClassNames = atts.getValue("name");
                StringTokenizer st = new StringTokenizer(startClassNames, ",");
                while (st.hasMoreTokens()) {
                    String startClassName = st.nextToken();
                    startClassName = startClassName.trim();
                    addStartClass(startClassName);
                }
            } else if (qName.equalsIgnoreCase("name")) {
                inAnalysisNameTag = true;
            }
        }

        @Override
        public void endElement(final String uri, final String localName, final String qName) throws SAXException {
            if (inAnalysisNameTag) {
                if (qName.equals("name")) {
                    inAnalysisNameTag = false;
                } else if (StringUtils.isBlank(name)) {
                    name = valueBuilder.toString();
                } else {
                    Locale locale = UserProperties.getLocale();
                    String toolLanguage = locale.getLanguage();
                    Locale xmlLocale = new Locale(qName);
                    String xmlLanguage = xmlLocale.getLanguage();
                    if (toolLanguage.equals(xmlLanguage)) {
                        name = valueBuilder.toString();
                    }
                }
            }
            valueBuilder.setLength(0);
        }

        @Override
        public void characters(final char[] arg0, final int arg1, final int arg2) throws SAXException {
            valueBuilder.append(String.valueOf(arg0, arg1, arg2));
        }

    }

    @Override
    public final List<ElementContainer> getResult(final GraphDocument doc) {
        return AnalysisXMLParser.analyze(xmlText, doc);
    }

    /**
     * Parst einen Analysetext.
     *
     * @param analysisText der Analysetext.
     * @throws SAXException wenn ein Fehler beim parsen des Analysetextes
     *             auftritt.
     */
    public void parseAnalysis(final String analysisText) throws SAXException {
        SAXParser parser = null;
        SAXParserFactory factory = SAXParserFactory.newInstance();

        try {
            parser = factory.newSAXParser();
        } catch (Exception e) {
            logException(e);
        }

        if (parser != null) {
            try {
                InputSource inputSource = new InputSource(new StringReader(analysisText));
                AnalysisParser analysisParser = new AnalysisParser();
                parser.parse(inputSource, analysisParser);
            } catch (IOException | IllegalArgumentException ex) {
                logException(ex);
            }
        }
    }

    private void logException(final Exception ex) {
        Log.show(Log.ERROR, getResString("FehlerParser") + "\n" + ex.getMessage(), ex);
    }

    @Override
    public String toString() {
        // wird gebraucht, um die Analysen in der Tabelle des AnalysesRepositoryDialoges richtig
        // zu sortieren (das ganze is ne echte Krücke!)
        return getStartClassesDisplayNames();
        // return startknoten.toString();
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + (xmlText == null ? 0 : xmlText.hashCode());
        return result;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (!super.equals(obj)) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        XMLAnalysis other = (XMLAnalysis) obj;
        if (xmlText == null) {
            if (other.xmlText != null) {
                return false;
            }
        } else if (!xmlText.equals(other.xmlText)) {
            return false;
        }
        return true;
    }

    @Override
    public boolean hasEqualsContent(final AbstractAnalysis other, final boolean checkNameEquality) {
        if (!super.hasEqualsContent(other, checkNameEquality)) {
            return false;
        }
        if (!(other instanceof XMLAnalysis)) {
            return false;
        }
        return Objects.equal(xmlText, ((XMLAnalysis) other).xmlText);
    }

}
