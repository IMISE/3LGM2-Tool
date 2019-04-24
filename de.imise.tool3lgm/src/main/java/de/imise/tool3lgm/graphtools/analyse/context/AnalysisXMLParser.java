package de.imise.tool3lgm.graphtools.analyse.context;

import static de.imise.tool3lgm.Tool3lgmConstants.getResString;

import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.helpers.DefaultHandler;

import de.imise.tool3lgm.graphtools.model.GraphDocument;
import de.imise.tool3lgm.graphtools.view.container.ElementContainer;
import de.imise.tool3lgm.log.Log;
import de.imise.tool3lgm.userproperties.UserProperties;
import de.imise.tool3lgm.userproperties.UserProperties.BooleanProperty;

public class AnalysisXMLParser extends DefaultHandler {

    // public final static String parserClass = "javax.xml.parsers.SAXParser";

    public static final int START = 0;
    public static final int IN_TYP = 1;
    public static final int IN_VERBUNDENE = 2;
    public static final int IN_NAME = 3;
    public static final int IN_BESCHREIBUNG = 4;
    public static final int IN_VERBUNDENSTATE = 5;
    public static final int IN_EINTRAG = 6;
    public static final int IN_ANALYSE = 7;
    public static final int IN_SUCHE = 8;
    public static final int UNKNOWN = 9;
    public static final int END = 10;

    /**
     *
     */
    public static List<ElementContainer> analyze(final String str, final GraphDocument _doc) {
        AnalysisXMLParser p = new AnalysisXMLParser();
        return p.process(new StringReader(str), _doc);
    }

    /**
     * @param str
     * @return
     */
    public static boolean isXMLString(final String str) {
        return str.startsWith("<?xml") || str.startsWith("<analyse>");
    }

    private final StringBuilder elementTextBuf = new StringBuilder();
    SAXParser parser = null;
    private int state, file_state;
    private int unknown_depth, suchcnt;
    private boolean verbundenstate;
    private List<String> typ, connectedNames;
    private final List<ElementContainer> result;

    private List<ElementContainer> resulttmp;

    private GraphDocument doc;

    /**
     *
     */
    public AnalysisXMLParser() {
        unknown_depth = 0;
        file_state = START;
        state = START;
        result = new ArrayList<>(100);
        resulttmp = new ArrayList<>(100);
        parser = null;
        suchcnt = 0;
        SAXParserFactory factory = SAXParserFactory.newInstance();
        try {
            parser = factory.newSAXParser();
        } catch (Exception e) {
            Log.show(Log.ERROR, getResString("FehlerParser"), e);
        }
    }

    @Override
    public void characters(final char[] ch, final int start, final int length) {
        elementTextBuf.append(ch, start, length);
    }

    @Override
    public void endElement(final String namespaceURI, final String localName, final String rawName) {
        /*
         * System.out.println("endElement:"); System.out.println("namespaceURI: "+namespaceURI);
         * System.out.println("localName: "+localName); System.out.println("rawName: "+rawName);
         */
        if (state == UNKNOWN) {
            System.out.println("Ignoring unknown sub-elements.");
            if (unknown_depth > 1) {
                unknown_depth--;
            } else {
                state = file_state;
                file_state = START;
                unknown_depth = 0;
            }
            return;
        }

        if (rawName.equals("analyse")) {
            if (state != IN_ANALYSE) {
                System.out.println("XML Error: Not in analyse when finishing analyse.");
                return;
            }
            state = END;
            return;
        }

        if (rawName.equals("suche")) {
            if (state != IN_SUCHE) {
                System.out.println("XML Error: Not in suche when finishing suche.");
                return;
            }
            state = IN_ANALYSE;
            boolean searchParts = UserProperties.is(BooleanProperty.OPTION_ELEMENTS_RECEIVE_PROPERTIES_FROM_PARTS);
            boolean searchParents = UserProperties.is(BooleanProperty.OPTION_ELEMENTS_RECEIVE_PROPERTIES_FROM_PARENTS);
            if (suchcnt == 0) {
                if (doc.getSelectionSize() > 0) {
                    Collection<ElementContainer> selection = doc.getSelectedContainer();
                    List<ElementContainer> connected = GraphAnalyse.searchWithinConnected(doc, resulttmp, new ArrayList<>(resulttmp), typ, verbundenstate, connectedNames, searchParts, searchParents);
                    resulttmp = new ArrayList<>(selection.size() + connected.size());
                    resulttmp.addAll(selection);
                    resulttmp.addAll(connected);
                } else {
                    resulttmp = GraphAnalyse.performSearch(doc, typ, verbundenstate, connectedNames);
                }
            } else {
                resulttmp = GraphAnalyse.searchWithinConnected(doc, resulttmp, new ArrayList<>(resulttmp), typ, verbundenstate, connectedNames, searchParts, searchParents);
            }
            result.addAll(resulttmp);
            suchcnt++;
            return;
        }

        if (rawName.equals("typ")) {
            if (state != IN_TYP) {
                System.out.println("XML Error: Not in typ when finishing typ.");
                return;
            }
            state = IN_SUCHE;
            file_state = IN_ANALYSE;
            return;
        }

        if (rawName.equals("verbundene")) {
            if (state != IN_VERBUNDENE) {
                System.out.println("XML Error: Not in verbundene when finishing verbundene.");
                return;
            }
            state = IN_SUCHE;
            file_state = IN_ANALYSE;
            return;
        }

        if (rawName.equals("name")) {
            return;
        }

        if (rawName.equals("beschreibung")) {
            return;
        }

        if (rawName.equals("verbundenstate")) {
            if (state != IN_VERBUNDENSTATE) {
                System.out.println("XML Error: Not in verbundenstate when finishing verbundenstate.");
                return;
            }
            verbundenstate = elementTextBuf.toString().equals("falsch") ? false : true;
            elementTextBuf.setLength(0);
            state = IN_SUCHE;
            return;
        }
        if (rawName.equals("eintrag")) {
            if (state != IN_EINTRAG) {
                System.out.println("XML Error: Not in entry when finishing entry.");
                return;
            }
            if (elementTextBuf.toString() == null) { // Value
                System.out.println("XML Error: Field has no name!");
                return;
            }
            if (file_state == IN_TYP) {
                typ.add(elementTextBuf.toString());
            }
            if (file_state == IN_VERBUNDENE) {
                connectedNames.add(elementTextBuf.toString());
            }
            elementTextBuf.setLength(0);
            state = file_state;
            file_state = IN_SUCHE;
            return;
        }
    }

    /**
     * @param input
     * @param _doc
     * @return
     */
    public List<ElementContainer> process(final Reader input, final GraphDocument _doc) {
        if (input == null || _doc == null) {
            return new ArrayList<>();
        }
        doc = _doc;

        try {
            parser.parse(new InputSource(input), this);
        } catch (Exception e) {
            Log.show(Log.ERROR, getResString("FehlerAllgemein"), e);
        }

        return result;
    }

    @Override
    public void startElement(final String namespaceURI, final String localName, final String rawName, final Attributes atts) {

        if (state == UNKNOWN) {
            unknown_depth++;
            return;
        }

        elementTextBuf.setLength(0);

        if (rawName.equals("analyse")) {
            if (state != START) {
                System.out.println("XML Error: Not in start when analyse begins.");
            }
            state = IN_ANALYSE;
            return;
        }

        if (rawName.equals("suche")) {
            if (state != IN_ANALYSE) {
                System.out.println("XML Error: Not in analyse when suche begins.");
            }
            state = IN_SUCHE;
            typ = new ArrayList<>();
            connectedNames = new ArrayList<>();
            verbundenstate = true;
            return;
        }

        if (rawName.equals("typ")) {
            if (state != IN_SUCHE) {
                if (state != IN_SUCHE && state != IN_SUCHE) {
                    System.out.println("XML Error: Not in suche or layer when verbundenstate begins.");
                }
                return;
            }
            state = IN_TYP;
            return;
        }

        if (rawName.equals("verbundene")) {
            if (state != IN_SUCHE) {
                if (state != IN_SUCHE && state != IN_SUCHE) {
                    System.out.println("XML Error: Not in suche or layer when verbundenstate begins.");
                }
                return;
            }
            state = IN_VERBUNDENE;
            return;
        }

        if (rawName.equals("name")) {
            return;
        }

        if (rawName.equals("beschreibung")) {
            return;
        }

        if (rawName.equals("verbundenstate")) {
            if (state != IN_SUCHE && state != IN_SUCHE) {
                System.out.println("XML Error: Not in suche or layer when verbundenstate begins.");
            } else {
                file_state = state;
                state = IN_VERBUNDENSTATE;
            }
            return;
        }

        if (rawName.equals("eintrag")) {
            if (state != IN_TYP && state != IN_VERBUNDENE) {
                System.out.println("XML Error: Not in element or layer when field begins.");
                return;
            }
            file_state = state;
            state = IN_EINTRAG;
            return;
        }

        if (rawName.equals("startknoten")) {
            return;
        }

        // now we have a new unknown element
        System.out.println("Unknown Element: " + rawName);
        file_state = state;
        state = UNKNOWN;
        unknown_depth = 1;
        return;
    }

}
