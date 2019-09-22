/*
 * Created on 14.06.2004
 */
package de.imise.tool3lgm.graphtools.analyse.context;

import java.io.IOException;
import java.io.StringReader;
import java.util.List;
import java.util.StringTokenizer;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import de.imise.tool3lgm.MetaModelContext;
import de.imise.tool3lgm.graphtools.metamodel.MetaModel;
import de.imise.tool3lgm.graphtools.metamodel.elements.ModelElement;
import de.imise.tool3lgm.graphtools.model.GraphDocument;
import de.imise.tool3lgm.graphtools.view.container.ElementContainer;
import de.imise.tool3lgm.log.Log;

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
    private XMLAnalysis(final MetaModelContext metaModelContext) {
        super(metaModelContext);
    }

    /**
     * Erzeugt eine neue XMLAnalyse mit den übergebenen Parametern.
     *
     * @param metaModelContext der Context des Metamodels, für das die Analyse anwendbar ist
     * @param analyseText die XMLAnalyse als XML-Text.
     * @return wenn kein Fehler beim erzeugen der XMLAnalyse auftrat, wird eine neue XMLAnalyse mit
     *         den übergebenen Parametern zurück gegeben.
     * @throws SAXException wenn ein Fehler beim parsen des Analysetextes auftritt.
     */
    public static XMLAnalysis createAnalysis(final MetaModelContext metaModelContext, final String analyseText) throws SAXException {
        XMLAnalysis xMLAnalysis = new XMLAnalysis(metaModelContext);
        xMLAnalysis.setXMLText(analyseText);
        return xMLAnalysis;
    }

    /**
     * Erzeugt eine neue XMLAnalyse mit den übergebenen Parametern.
     *
     * @param metaModelContext der Context des Metamodels, für das die Analyse anwendbar ist
     * @param name Name der XMLAnalyse.
     * @param analyseText die XMLAnalyse als XML-Text.
     * @return wenn kein Fehler beim erzeugen der XMLAnalyse auftrat, wird eine neue XMLAnalyse mit
     *         den übergebenen Parametern zurück gegeben.
     * @throws SAXException wenn ein Fehler beim parsen des Analysetextes auftritt.
     */
    public static XMLAnalysis createAnalysis(final MetaModelContext metaModelContext, final String name, final String analyseText) throws SAXException {
        XMLAnalysis xMLAnalysis = new XMLAnalysis(metaModelContext);
        xMLAnalysis.setName(name);
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
     * @throws SAXException wenn ein Fehler beim parsen des Analysetextes auftritt.
     */
    public void setXMLText(String xmlText) throws SAXException {
        if (xmlText == null || xmlText.trim().length() == 0) {
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
        @Override
        public void startElement(final String namespaceURI, final String localName, final String qName, final Attributes atts) {
            if (qName.equals("startknoten")) {
                StringTokenizer st = new StringTokenizer(atts.getValue("name"), ",");
                while (st.hasMoreTokens()) {
                    addStartClass(st.nextToken().trim());
                }
            }
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
     * @throws SAXException wenn ein Fehler beim parsen des Analysetextes auftritt.
     */
    public void parseAnalysis(final String analysisText) throws SAXException {
        SAXParser parser = null;
        SAXParserFactory factory = SAXParserFactory.newInstance();

        try {
            parser = factory.newSAXParser();
        } catch (Exception e) {
            Log.show(Log.ERROR, getResString("FehlerParser") + "\n" + e.getMessage(), e);
        }

        if (parser != null) {
            try {
                parser.parse(new InputSource(new StringReader(analysisText)), new AnalysisParser());
            } catch (IOException | IllegalArgumentException ex) {
                Log.show(Log.ERROR, getResString("FehlerParser") + "\n" + ex.getMessage(), ex);
            }
        }
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
}
