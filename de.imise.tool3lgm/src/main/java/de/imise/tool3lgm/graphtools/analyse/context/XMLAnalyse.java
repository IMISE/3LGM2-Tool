/*
 * Created on 14.06.2004
 */
package de.imise.tool3lgm.graphtools.analyse.context;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import de.imise.tool3lgm.Tool3lgmConstants;
import de.imise.tool3lgm.graphtools.GraphDocument;
import de.imise.tool3lgm.graphtools.elements.ModelConstants;
import de.imise.tool3lgm.graphtools.elements.ModelElement;
import de.imise.tool3lgm.graphtools.view.container.ElementContainer;
import de.imise.tool3lgm.log.Log;

/**
 * Diese Klasse repräsentiert eine einzelne XMLAnalyse.
 *
 * @author Sebastian Weber
 */
public class XMLAnalyse extends AbstractAnalyse {

    /** der Text der die Analyse beschreibt. */
    protected String xmlText;

    /**
     * Erzeugt eine neue XMLAnalyse mit den übergebenen Parametern.
     *
     * @param analyseText die XMLAnalyse als XML-Text.
     * @return wenn kein Fehler beim erzeugen der XMLAnalyse auftrat, wird eine neue XMLAnalyse mit
     *         den übergebenen Parametern zurück gegeben.
     * @throws SAXException wenn ein Fehler beim parsen des Analysetextes auftritt.
     */
    public static XMLAnalyse createAnalyse(final String analyseText) throws SAXException {
        XMLAnalyse xMLAnalyse = new XMLAnalyse();
        xMLAnalyse.setXMLText(analyseText);
        return xMLAnalyse;
    }

    /**
     * Erzeugt eine neue XMLAnalyse mit den übergebenen Parametern.
     *
     * @param name Name der XMLAnalyse.
     * @param analyseText die XMLAnalyse als XML-Text.
     * @return wenn kein Fehler beim erzeugen der XMLAnalyse auftrat, wird eine neue XMLAnalyse mit
     *         den übergebenen Parametern zurück gegeben.
     * @throws SAXException wenn ein Fehler beim parsen des Analysetextes auftritt.
     */
    public static XMLAnalyse createAnalyse(final String name, final String analyseText) throws SAXException {
        XMLAnalyse xMLAnalyse = new XMLAnalyse();
        xMLAnalyse.setName(name);
        xMLAnalyse.setXMLText(analyseText);
        return xMLAnalyse;
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
        ArrayList<Class<? extends ModelElement>> alteStartknoten = getStartknoten();
        deleteStartknoten();

        try {
            parseAnalyse(xmlText);
        } catch (SAXException e) {
            // alte Werte wieder herstellen
            startknoten = alteStartknoten;
            throw e;
        }
        this.xmlText = xmlText;
    }

    /**
     * Fügt einen neuen Startknoten hinzu.
     *
     * @param startknoten der neue Name des Startknotens.
     */
    private void addStartknoten(final String startknoten) {
        Class<? extends ModelElement> startClass = ModelConstants.getClassForName(startknoten);
        if (startClass != null) {
            this.startknoten.add(startClass);
        }
    }

    /**
     * Löscht die Liste der Startknoten.
     */
    protected void deleteStartknoten() {
        if (startknoten != null) {
            startknoten.clear();
        }
    }

    /**
     * DocumentHandler für den XML-Analysetext.
     *
     * @author Sebastian Weber
     */
    private class AnalyseParser extends DefaultHandler {
        @Override
        public void startElement(final String namespaceURI, final String localName, final String qName, final Attributes atts) {
            if (qName.equals("startknoten")) {
                StringTokenizer st = new StringTokenizer(atts.getValue("name"), ",");
                while (st.hasMoreTokens()) {
                    addStartknoten(st.nextToken().trim());
                }
            }
        }
    }

    @Override
    public final List<ElementContainer> getResult(final GraphDocument doc) {
        return AnalyseXMLParser.analyze(xmlText, doc);
    }

    /**
     * Parst einen Analysetext.
     *
     * @param analyseText der Analysetext.
     * @throws SAXException wenn ein Fehler beim parsen des Analysetextes auftritt.
     */
    public void parseAnalyse(final String analyseText) throws SAXException {
        SAXParser parser = null;
        SAXParserFactory factory = SAXParserFactory.newInstance();

        try {
            parser = factory.newSAXParser();
        } catch (Exception e) {
            Log.show(Log.ERROR, Tool3lgmConstants.getErrString("FehlerParser") + "\n" + e.getMessage(), e);
        }

        if (parser != null) {
            try {
                parser.parse(new InputSource(new StringReader(analyseText)), new AnalyseParser());
            } catch (IOException ex) {
                Log.show(Log.ERROR, Tool3lgmConstants.getErrString("FehlerParser") + "\n" + ex.getMessage(), ex);
            } catch (IllegalArgumentException ex) {
                Log.show(Log.ERROR, Tool3lgmConstants.getErrString("FehlerParser") + "\n" + ex.getMessage(), ex);
            }
        }
    }

    @Override
    public String toString() {
        // wird gebraucht, um die Analysen in der Tabelle des AnalyseRepositoryDialoges richtig
        // zu sortieren (das ganze is ne echte Krücke!)
        return getStartknotenString();
        // return startknoten.toString();
    }

    @Override
    public int hashCode() {
        // assert false : "hashCode not designed";
        return 42; // any arbitrary constant will do
    }

    @Override
    public boolean equals(final Object obj) {
        if (super.equals(obj)) {
            return true;
        }
        if (!(obj instanceof XMLAnalyse)) {
            return false;
        }
        XMLAnalyse a = (XMLAnalyse) obj;
        if (!name.equals(a.name)) {
            return false;
        }
        if (!startknoten.equals(a.startknoten)) {
            return false;
        }
        if (!xmlText.equals(a.xmlText)) {
            return false;
        }
        return true;
    }
}
