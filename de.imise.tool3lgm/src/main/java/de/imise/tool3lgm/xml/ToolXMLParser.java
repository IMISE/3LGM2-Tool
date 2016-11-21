package de.imise.tool3lgm.xml;

import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import de.imise.tool3lgm.Tool3lgm;
import de.imise.tool3lgm.Tool3lgmConstants;
import de.imise.tool3lgm.graphtools.GDCollection;
import de.imise.tool3lgm.graphtools.GraphDocument;
import de.imise.tool3lgm.graphtools.consistency.ModelCleaner;
import de.imise.tool3lgm.graphtools.view.container.LayerContainer;

/**
 * @author Thomas Rudert
 */
public class ToolXMLParser {

    //	/**
    //	 * Set der Hashes aller Elemente, die nicht länger unterstützt werden. Beispielsweise werden
    //	 * keine gemischten Anwendungsbausteine mehr unterstützt. Die zugehörige Klasse "Anwendungsbaustein"
    //	 * ist jetzt abstract. Da diese nicht mehr unterstützten Elemente aber Assoziationen zu anderen
    //	 * Elementen aber auch untergeordnete Elemente haben können, müssen diese auch entfernt werden. Um diese
    //	 * Assoziationen und Elemente zu finden, muss man sich alle Hashes der nicht mehr unterstützten Elemente
    //	 * merken.
    //	 * 
    //	 * TODO:implementieren
    //	 */
    //	private static HashSet<String> _deprecatedElementHashes;

    /**
     * Die Collection, die dieser Parser einliest
     */
    private final GDCollection gdcoll;

    /** unterstützte XML und Datei Versionen (aktuellste Version steht im Array ganz hinten, also mit Index = length-1) */
    private static String[] supportedXMLVersions = {
            "<?xml version='1.0' encoding='utf-8'?>", "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
    };
    private static String[] supportedFileVersions = {
            "<!--Tool3lgmFile version='1.0'-->", "<!--Tool3lgmFile version='1.1'-->", "<!--Tool3lgmFile version='1.2'-->", "<!--Tool3lgmFile version='2.0'-->", "<!--Tool3lgmFile version='3.0'-->", "<!--Tool3lgmFile version='3.1'-->",
            "<!--Tool3lgmFile version='3.2'-->", "<!--Tool3lgmFile version='3.3'-->", "<!--Tool3lgmFile version='3.4'-->",
    };

    private final SAXParser parser;

    private int[] version = {
            -1, -1
    };

    private final InputStream parseStream;

    /**
	 * 
	 */
    public ToolXMLParser(final GDCollection collection, final InputStream inputStream, final boolean paste) throws SAXException, ParserConfigurationException, FileNotFoundException, IOException, LGMVersionException, XMLVersionException {

        parseStream = inputStream;
        gdcoll = collection;

        SAXParserFactory factory = SAXParserFactory.newInstance();

        parser = factory.newSAXParser();
        parser.getXMLReader().setDTDHandler(new ToolDTDHandler());

        version = getVersion(parseStream);

        /* XML Version */
        if (version[0] < 0) {
            throw new SAXException("angegebenes Dateiformat wird nicht unterstützt");
        }

        /* Tool3lgm2-Datei-Version */
        switch (version[1]) {
        case 0:
            parser.getXMLReader().setContentHandler(new ToolContentHandlerV1_0(collection));
            break;

        case 1:
            parser.getXMLReader().setContentHandler(new ToolContentHandlerV1_1(collection));
            break;

        case 2:
            parser.getXMLReader().setContentHandler(new ToolContentHandlerV1_2(collection));
            break;

        case 3:
            parser.getXMLReader().setContentHandler(new ToolContentHandlerV2_0(collection));
            break;

        case 4:
            parser.getXMLReader().setContentHandler(new ToolContentHandlerV3_0(collection));
            break;

        case 5:
            parser.getXMLReader().setContentHandler(new ToolContentHandlerV3_1(collection));
            break;

        case 6:
            parser.getXMLReader().setContentHandler(new ToolContentHandlerV3_2(collection, paste));
            break;

        case 7:
            parser.getXMLReader().setContentHandler(new ToolContentHandlerV3_2(collection, paste));
            break;

        case 8:
            parser.getXMLReader().setContentHandler(new ToolContentHandlerV3_4(collection, paste));
            break;

        default:
            throw new SAXException("angegebenes Dateiformat wird nicht unterstützt");

            //			if (version[1]<8)
            //TODO:+++				//drehe alle KommBezETNTVBerbindungen! ACHTUNG: beim Modellieren muss das auch gedreht werden
            //				ModelCleaner.correctModel();

        }
    }

    public void parseDocument() throws SAXException, IOException {
        //		deprecatedElementHashes = new HashSet();
        de.imise.tool3lgm.Tool3lgm.tool.setProgressDialogStatusLabel(Tool3lgmConstants.getResString("labelReadFile"));

        XMLReader reader = parser.getXMLReader();
        reader.parse(new InputSource(parseStream));

        //alle Assoziationen und untergeordneten Elemente von nicht mehr unterstützten
        //Elementen entfernen
        // 		GraphDocument mainDoc = gdcoll.getGraphDocument();

        ////TODO:AXS: sauberes Entfernen nicht mehr unterstützter Elemente beenden		
        ///*
        // 		//HashSet, in das die HashStrings aller Elemente kommen, die gelöscht werden müssen,
        // 		//weil sie von einem anderen Element abhängig sind (untergeordnete Elemente), 
        //
        // 		HashSet alreadyRemovedHashes = new HashSet();
        // 		System.err.println(deprecatedElementHashes);
        // 		
        // 		//für alle HashWerte von ModellElementen, die nicht mehr unterstützt werden
        // 		for (Iterator it=deprecatedElementHashes.iterator(); it.hasNext();){
        // 			String deprecatedElementHash = it.next().toString();
        // 			//wenn er schon aufgrund eines anderen nicht mehr unterstützten Elementes gelöscht
        // 			//wurde -> nimm den nächsten Hash
        // 			if (alreadyRemovedHashes.contains(deprecatedElementHash))
        // 				continue;
        // 			//für alle Kantenarten
        // 			for (int i=0; i<ModelConstants.ALL_TRACES.length; i++){
        // 	 			//hole alle Kanten der i-ten Art
        // 				ArrayList connections = mainDoc.getAllModelElements(ModelConstants.ALL_TRACES[i]);
        // 	 			//für jede dieser Kanten
        // 				for (Iterator tracesIt=connections.iterator(); tracesIt.hasNext();){
        // 	 				Kante trace = (Kante)tracesIt.next();
        // 	 				String elemToDeleteHash = null;
        // 					Object[][] typesAndCardinality = null;
        // 					ModelElement me = null;
        // 	 				//wenn der Hashstring des nicht mehr unterstützten Elementes dem StartHash
        // 					//der Kante entspricht
        // 					if (trace.getStartHash().equals(deprecatedElementHash)){
        // 	 					//hole das Endelement der Kante und die Kardinalitäten
        // 						me = mainDoc.findElementCoded(trace.getEndHash());
        // 	 					typesAndCardinality = trace.getStartTypesAndCardinality();
        // 	 	 				//wenn der Hashstring des nicht mehr unterstützten Elementes dem EndHash
        // 	 					//der Kante entspricht
        // 	 				}else if (trace.getEndHash().equals(deprecatedElementHash)){
        // 	 					//hole das Startelement der Kante und die Kardinalitäten
        // 	 					me = mainDoc.findElementCoded(trace.getStartHash());
        // 	 					typesAndCardinality = trace.getEndTypesAndCardinality();
        // 	 				}
        // 	 				//entferne die Kante
        // 					mainDoc.removeTrace(trace, TransactionManager.STANDARD_PID);
        // 					//wenn das andere Element der Kante auch nicht richtig instanziiert war -> nächste Kante
        // 					if (me==null)
        // 						continue;
        // 					//wenn beim anderen Element der Kante die minimale Kardinalität größer 0 ist (wenn
        // 					//es also dem nicht mehr unterstützten Element untergeordnet ist (z.B. Datenbanken
        // 					//waren gemischten Anwendungsbausteinen untergeordnet -> gemischte Anwendungsbausteine
        // 					//gibt es nicht mehr -> Datenbanken von solchen nicht mehr untertsützten gemischten
        // 					//Anwendungsbausteinen müssen auch gelöscht werden))
        // 					Class elementClass = me.getClass();
        // 					for (int o=0; o<typesAndCardinality.length; o++){
        //	 	 				if (((Class)typesAndCardinality[o][0]).isAssignableFrom(elementClass))
        // 	 	 					if (((Integer)typesAndCardinality[o][1]).intValue()>0)
        // 	 	 	 	 				elemToDeleteHash = trace.getEndHash();
        // 	 				}
        // 	 				//das andere Element der Kante muss auch gelöscht werden
        // 					if (elemToDeleteHash!=null){
        // 	 					mainDoc.remove(elemToDeleteHash, TransactionManager.STANDARD_PID);
        // 	 					//merke es als bereits gelöscht, falls es selbst auch ein nicht mehr
        // 	 					//unterstütztes Element ist
        // 	 					alreadyRemovedHashes.add(elemToDeleteHash);
        // 	 				}
        // 	 			}
        // 			}
        // 		}
        //*/ 		
        ///* 		deprecatedElementHashes.clear();
        // 		deprecatedElementHashes = null;
        //*/		
        // 		//Bis zur version[1] == 7 (= FileVersion V3_4)haben die x- und Y-Koordinaten der Elemente den Mittelpunkt 
        // 		//beschrieben, danach den oberen linken Eckpunkt. Das hier war nur nötig, weil die alte version auch Modelle
        // 		//aus dem 3LGM²-Reporter einlesen können sollte, die mit der neuen Version gechrieben worden waren
        //// 		System.err.println(version[1]);
        ///* 		if (version[1] >= 9) {
        // 			for (Szenario szen : gdcoll.getSzenarios()) {
        // 				int layerMiddleX = szen.getPageWidth() / 2;
        // 				int layerMiddleY = szen.getPageHeight() / 2;
        // 				for(int i = 0; i < ModelConstants.LAYERS.length; i++) {
        // 					LayerContainer lc = szen.getLayer(i);
        // 					for (NodeContainer nc : lc.getKnoten()) {
        // 						nc.setLocation((nc.getX() + nc.getWidth() / 2) - layerMiddleX, (nc.getY() + nc.getHeight() / 2) - layerMiddleY);
        // 					}
        // 					for (NodeContainer nc : lc.getKnickpunkte()) {
        // 						//Bei Knickpunkten darf es nicht der Mittelpunkt sein, sondern es muss wirklich der alte X- und Y-Wert bleiben
        // 						nc.setLocation(nc.getX() - layerMiddleX, nc.getY() - layerMiddleY);
        // 					}
        // 					for (EdgeContainer ec : lc.getKanten()) {
        // 						ec.computeBorderPoints();
        // 					}
        // 				}
        // 			}
        // 		}
        // */		

        Tool3lgm.tool.setProgressDialogStatusLabel(Tool3lgmConstants.getResString("labelCleanModel"));
        new ModelCleaner(gdcoll).cleanModel();

        //beim Einlesen werden Elemente mit generierten Namen evtl. nicht richtig eingelesen, weil
        //die Elemente, aus denen ihr Name generiert wird evtl. nach ihnen eingelesen werden ->
        //einfach nach dem kompletten Einlesen nochmal sortieren
        ArrayList<GraphDocument> docs = new ArrayList<GraphDocument>(gdcoll.getSzenarios());
        docs.add(gdcoll.getMainGraphDocument());
        for (GraphDocument d : docs) {
            for (LayerContainer lc : d.getLayers()) {
                lc.refreshAlpahbetical();
            }
        }
        Tool3lgm.tool.setProgressDialogStatusLabel(Tool3lgmConstants.getResString("labelReady"));

        ///*		AlphabeticalSet<Class<? extends ModelElement>> allUsedElementClasses = new AlphabeticalSet<Class<? extends ModelElement>>();
        //		for (ModelElement me : gdcoll.getGraphDocument().getModelItems(ModelElement.class, true))
        //			allUsedElementClasses.add(me.getClass());
        //		for (Class<? extends ModelElement> elementClass : allUsedElementClasses)
        //			System.err.println(elementClass.getName());
        //*/
    }

    /**
     * @param file
     * @return
     * @throws IOException, FileNotFoundException
     */
    public static boolean isParseAbleFileVersion(final FileInputStream fileStream) throws FileNotFoundException, IOException, LGMVersionException, XMLVersionException {
        fileStream.getChannel().position(0);
        int[] version = getVersion(fileStream);
        return !(version[0] < 0 || version[1] < 0);
    }

    /**
     * @param file
     * @return int[0] = xmlVersion, int[1] = fileVersion
     * @throws IOException, FileNotFoundException
     */
    @SuppressWarnings("deprecation")
    private static int[] getVersion(final InputStream inputStream) throws FileNotFoundException, IOException, LGMVersionException, XMLVersionException {
        String line;
        int[] version = {
                -1, -1
        };

        ///*		
        //		byte[] byteBuffer = new byte[30];
        //		inputStream.read(byteBuffer);
        //		String s = new String(byteBuffer);
        //		System.err.println(s);
        //*/		
        DataInputStream dataStream = new DataInputStream(inputStream) {
            @Override
            public void close() {
            }
        };

        ///*Das hier geht aus irgend einem Grund nicht. Der Stream haut beim Parser nicht mehr hin. Wahrscheinlich wird 
        // * der Zeiger innerhalb der Datei nicht mehr korrekt weitergesetzt.
        //		InputStreamReader is = new InputStreamReader(inputStream) {public void close() {}};
        //		BufferedReader dataStream = new BufferedReader(is) {public void close() {}};
        //*/
        line = dataStream.readLine();
        for (int i = 0; i < supportedXMLVersions.length; i++) {
            if (line.toLowerCase().equals(supportedXMLVersions[i].toLowerCase())) {
                version[0] = i;
                break;
            }
        }
        if (version[0] == -1) {
            throw new XMLVersionException(Tool3lgmConstants.getResString("xmlversionsfehler"));
        }

        line = dataStream.readLine();
        if (!line.startsWith("<!--Tool3lgmFile version=")) {
            return version;
        }
        for (int i = 0; i < supportedFileVersions.length; i++) {
            if (line.toLowerCase().equals(supportedFileVersions[i].toLowerCase())) {
                version[1] = i;
                break;
            }
        }
        if (version[1] == -1) {
            throw new LGMVersionException(Tool3lgmConstants.getResString("lgmversionsfehler"));
        }

        return version;
    }

    public static String getCurrentVersionString() {
        return supportedXMLVersions[supportedXMLVersions.length - 1] + "\n" + supportedFileVersions[supportedFileVersions.length - 1] + "\n";
    }

    //	/**
    //	 * Fügt zu den Hashes der Elemente, die nicht mehr unterstützt werden den übergebenen hinzu.
    //	 * 
    //	 * @param hash
    //	 */
    ///*	public static void addDeprecatedElementHashes(String hash) {
    //		deprecatedElementHashes.add(hash);
    //	}
    //*/	
}