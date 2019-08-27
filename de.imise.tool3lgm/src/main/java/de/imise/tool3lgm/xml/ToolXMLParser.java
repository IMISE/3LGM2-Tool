package de.imise.tool3lgm.xml;

import static de.imise.tool3lgm.Tool3lgmConstants.getResString;

import java.io.DataInputStream;
import java.io.File;
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

import de.imise.tool3lgm.MetaModelContext;
import de.imise.tool3lgm.Static;
import de.imise.tool3lgm.Tool3lgmMetaModelContext;
import de.imise.tool3lgm.Tool3lgmModelType;
import de.imise.tool3lgm.Tool3lgmModelType.ModelCategory;
import de.imise.tool3lgm.graphtools.consistency.ModelCleaner;
import de.imise.tool3lgm.graphtools.model.GDCollection;
import de.imise.tool3lgm.graphtools.model.GraphDocument;
import de.imise.tool3lgm.graphtools.model.Szenario;
import de.imise.tool3lgm.graphtools.view.container.LayerContainer;
import de.imise.tool3lgm.log.Log;

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
    //	private static Set<String> _deprecatedElementHashes;

    /**
     * Die Collection, die dieser Parser einliest
     */
    private final GDCollection gdcoll;

    /** unterstützte XML und Datei Versionen (aktuellste Version steht im Array ganz hinten, also mit Index = length-1) */
    private static final String[] SUPPORTED_XML_VERSIONS = {
            "<?xml version='1.0' encoding='utf-8'?>", "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
    };

    private static final String FILE_VERSION_LINE_START = "<!--Tool3lgmFile ";

    /** Präfix vor der Angabe der Metamodell-Klasse in der Zeile der FileVersion */
    private static final String FILE_VERSION_NUMBER_PREFIX = " version='";

    /** Alle unterstützten FileVersions */
    private static final String[] FILE_VERSION_SUPPORTED_NUMBERS = {
            "1.0", //"<!--Tool3lgmFile version='1.0'-->", //0
            "1.1", //"<!--Tool3lgmFile version='1.1'-->", //1
            "1.2", //"<!--Tool3lgmFile version='1.2'-->", //2
            "2.0", //"<!--Tool3lgmFile version='2.0'-->", //3
            "3.0", //"<!--Tool3lgmFile version='3.0'-->", //4
            "3.1", //"<!--Tool3lgmFile version='3.1'-->", //5
            "3.2", //"<!--Tool3lgmFile version='3.2'-->", //6
            "3.3", //"<!--Tool3lgmFile version='3.3'-->", //7
            "3.4", //"<!--Tool3lgmFile version='3.4'-->", //8
            "3.5", //"<!--Tool3lgmFile version='3.5'-->", //9
            "3.6", //"<!--Tool3lgmFile version='3.6'-->", //10
            "3.7", //"<!--Tool3lgmFile version='3.7'-->", //11 -> nach Tool-Version 3.4.0.2 (Beta)
    };

    /**
     * String der in den supportedFileVersions-String gebaut wird, um das Metamodell der Dateiversion zu kennzeichnen. Hinter das Hochkomma
     * am Ende kommt der SimpleClassName der Metamodel-Klasse und danach noch ein Hochkomma.
     */
    private static final String FILE_VERSION_METAMODEL_CLASS_PREFIX = " metamodel='";

    private static final String FILE_VERSION_MODEL_CATEGORY_PREFIX = " category='";

    private final SAXParser parser;

    private FileVersion version = new FileVersion();

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

        version = extractVersionAndMetaModel(parseStream);
        Tool3lgmModelType modelType = new Tool3lgmModelType(version.metaModelContext, version.modelCategory);
        gdcoll.setModelType(modelType);

        /* XML Version */
        if (version.xmlVersionIndex < 0) {
            throw new SAXException("angegebenes Dateiformat wird nicht unterstützt");
        }

        /* Metamodell passt nicht */ // das hier tritt nun nicht mehr auf. Es sollte schon vorher fesgestellt werden, dass das Metamodell des Files nicht im Plugins-Ordner gefunden wurde
        //        if (version.metaModelContext == Tool3lgmMetaModelContext.getMetaModelClass()) {
        //            String fileMetaModelName = Tool3lgmMetaModelContext.getMetaModelDisplayableName(version.metaModelClass);
        //            String toolMetaModelName = Tool3lgmMetaModelContext.getMetaModelDisplayableName(Tool3lgmMetaModelContext.getMetaModelClass());
        //            JOptionPane.showMessageDialog(Static.getTool(), getResString("wrong_metamodel_open_warning", fileMetaModelName, toolMetaModelName), getResString("warnung"), JOptionPane.WARNING_MESSAGE);
        //        }

        /* Tool3lgm2-Datei-Version */
        switch (version.lgmVersionIndex) {
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
        case 7:
        case 8:
        case 9:
        case 10: //Version 3.6 -> nur noch die DoubleMeaningEdges haben einen State und alle anderen nicht mehr
            //3.1 bis 3.6 haben denselben Parser, aber alte Baukastenversionen können neuere
            //Modelldateien nicht lesen, weil Elementklassen umbenannt wurden (was aber den Parser
            //nicht kümmert)
        case 11: //Version 3.7 -> OptionalEdge.isOptional() wird gespeichert
            parser.getXMLReader().setContentHandler(new ToolContentHandlerV3_1(collection, paste));
            break;

        default:
            throw new SAXException("angegebenes Dateiformat wird nicht unterstützt");
        }

    }

    public void parseDocument() throws SAXException, IOException {
        Log.showErrorDialog = true;
        //		deprecatedElementHashes = new HashSet();
        Static.setProgressDialogStatusLabel("labelReadFile");

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
        // 	 				Edge trace = (Edge)tracesIt.next();
        // 	 				String elemToDeleteHash = null;
        // 					Object[][] typesAndCardinality = null;
        // 					ModelElement me = null;
        // 	 				//wenn der Hashstring des nicht mehr unterstützten Elementes dem StartHash
        // 					//der Edge entspricht
        // 					if (trace.getStartHash().equals(deprecatedElementHash)){
        // 	 					//hole das Endelement der Edge und die Kardinalitäten
        // 						me = mainDoc.findElementCoded(trace.getEndHash());
        // 	 					typesAndCardinality = trace.getStartTypesAndCardinality();
        // 	 	 				//wenn der Hashstring des nicht mehr unterstützten Elementes dem EndHash
        // 	 					//der Edge entspricht
        // 	 				}else if (trace.getEndHash().equals(deprecatedElementHash)){
        // 	 					//hole das Startelement der Edge und die Kardinalitäten
        // 	 					me = mainDoc.findElementCoded(trace.getStartHash());
        // 	 					typesAndCardinality = trace.getEndTypesAndCardinality();
        // 	 				}
        // 	 				//entferne die Edge
        // 					mainDoc.removeTrace(trace, TransactionManager.STANDARD_PID);
        // 					//wenn das andere Element der Edge auch nicht richtig instanziiert war -> nächste Edge
        // 					if (me==null)
        // 						continue;
        // 					//wenn beim anderen Element der Edge die minimale Kardinalität größer 0 ist (wenn
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
        // 	 				//das andere Element der Edge muss auch gelöscht werden
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

        Static.setProgressDialogStatusLabel("labelCleanModel");

        //in Verison 3.5 wurden die IsPartOfEdges zu HasPartEdges gedreht, damit sie sich genauso verhalten wie die
        //CompositionEgdes, bei denen auch das Oberelement Start und das Unterlement EndElement ist.
        if (version.lgmVersionIndex < 9) {
            ModelCleaner.switchIsEdgesToHasPartEdges(gdcoll);
        }

        new ModelCleaner(gdcoll).cleanModel();

        //beim Einlesen werden Elemente mit generierten Namen evtl. nicht richtig eingelesen, weil
        //die Elemente, aus denen ihr Name generiert wird evtl. nach ihnen eingelesen werden ->
        //einfach nach dem kompletten Einlesen nochmal sortieren
        ArrayList<GraphDocument> docs = new ArrayList<>(gdcoll.getSzenarioCount() + 1);
        for (Szenario szen : gdcoll.getSzenarios()) {
            docs.add(szen);
        }
        docs.add(gdcoll.getMainGraphDocument());
        for (GraphDocument d : docs) {
            for (LayerContainer lc : d.getLayers()) {
                lc.refreshAlpahbetical();
            }
        }
        Static.setProgressDialogStatusLabel("labelReady");

        ///*		AlphabeticalSet<Class<? extends ModelElement>> allUsedElementClasses = new AlphabeticalSet<Class<? extends ModelElement>>();
        //		for (ModelElement me : gdcoll.getGraphDocument().getModelItems(ModelElement.class, true))
        //			allUsedElementClasses.add(me.getClass());
        //		for (Class<? extends ModelElement> elementClass : allUsedElementClasses)
        //			System.err.println(elementClass.getName());
        //*/
        Log.showErrorDialog = false;
    }

    /**
     * @param fileStream
     * @return
     */
    public static boolean isParsableXMLFile(final FileInputStream fileStream) {
        String line = "";
        try {
            fileStream.getChannel().position(0);
            byte[] chars = new byte["<graph>".length()];
            fileStream.read(chars);
            line = new String(chars);
            if (!isParseableFileVersion(fileStream)) {

            }
        } catch (Exception e) {
            Log.show(Log.ERROR, getResString("FehlerAllgemein"), e);
            return false;
        }
        if (line.startsWith("<?xml") || line.startsWith("<graph>")) {
            return true;
        }
        return false;
    }

    /**
     * @param file
     * @return
     * @throws IOException, FileNotFoundException
     */
    private static boolean isParseableFileVersion(final FileInputStream fileStream) throws FileNotFoundException, IOException, LGMVersionException, XMLVersionException {
        fileStream.getChannel().position(0);
        FileVersion version = extractVersionAndMetaModel(fileStream);
        return version.xmlVersionIndex >= 0 && version.lgmVersionIndex >= 0 && version.metaModelContext != null;
    }

    public static class FileVersion {
        public int xmlVersionIndex = -1;
        public int lgmVersionIndex = -1;
        public MetaModelContext metaModelContext = null;
        public ModelCategory modelCategory = null;
        @Override
        public String toString() {
            return "MetaModel=" + metaModelContext + " xmlVersion='" + SUPPORTED_XML_VERSIONS[xmlVersionIndex] + "' lgmVersion='" + FILE_VERSION_SUPPORTED_NUMBERS[lgmVersionIndex] + "'";
        }
    }

    public static FileVersion extractVersionAndMetaModel(final File file) throws FileNotFoundException, IOException, LGMVersionException, XMLVersionException {
        FileInputStream fileInputStream = new FileInputStream(file);
        FileVersion fileVersion = extractVersionAndMetaModel(fileInputStream);
        fileInputStream.close();
        return fileVersion;
    }

    /**
     * @param file
     * @return int[0] = xmlVersion, int[1] = fileVersion
     * @throws IOException, FileNotFoundException
     */
    @SuppressWarnings("deprecation")
    public static FileVersion extractVersionAndMetaModel(final InputStream inputStream) throws FileNotFoundException, IOException, LGMVersionException, XMLVersionException {
        DataInputStream dataStream = new DataInputStream(inputStream);
        //first line should be the xml version ("<?xml version='1.0' encoding='utf-8'?>" or "<?xml version=\"1.0\" encoding=\"UTF-8\"?>")
        FileVersion result = new FileVersion();
        String line = dataStream.readLine();
        for (int i = 0; i < SUPPORTED_XML_VERSIONS.length; i++) {
            if (line.toLowerCase().equals(SUPPORTED_XML_VERSIONS[i].toLowerCase())) {
                result.xmlVersionIndex = i;
                break;
            }
        }
        if (result.xmlVersionIndex == -1) {
            throw new XMLVersionException(getResString("xmlversionsfehler"));
        }

        //FILE_VERSION_SUPPORTED_NUMBERS
        //next line should start with "<!--Tool3lgmFile " and contains information abaout the file version, metamodel and the model category
        line = dataStream.readLine().toLowerCase();
        if (line.startsWith(FILE_VERSION_LINE_START.toLowerCase())) { //"<!--Tool3lgmFile "
            //supported file version (e.g: <!--Tool3lgmFile version='3.7')
            int versionStartIndex = line.indexOf(FILE_VERSION_NUMBER_PREFIX) + 1; //" version='"
            int versionEndIndex = line.indexOf('\'', versionStartIndex);
            if (versionEndIndex > versionStartIndex) {
                String versionNumer = line.substring(versionStartIndex, versionEndIndex);
                for (int i = 0; i < FILE_VERSION_SUPPORTED_NUMBERS.length; i++) {
                    if (FILE_VERSION_SUPPORTED_NUMBERS[i].equals(versionNumer)) {
                        result.lgmVersionIndex = i;
                        break;
                    }
                }
            }
            if (result.lgmVersionIndex == -1) {
                throw new LGMVersionException(getResString("lgmversionsfehler"));
            }

            //Metamodell is next statement in this line
            result.metaModelContext = Tool3lgmMetaModelContext.getDefaultMetaModelContext();
            int metaModelNameStartIndex = line.indexOf(FILE_VERSION_METAMODEL_CLASS_PREFIX); //" metamodel='"
            int metaModelNameEndIndex = line.indexOf('\'', metaModelNameStartIndex);
            if (metaModelNameEndIndex > metaModelNameStartIndex) {
                String metaModelName = line.substring(metaModelNameStartIndex, metaModelNameEndIndex);
                result.metaModelContext = Tool3lgmMetaModelContext.getMetaModelContextForID(metaModelName);
            }
            if (result.metaModelContext == null) {
                throw new LGMVersionException(getResString("lgmversionsfehler"));
            }

            //Model Category (REGULAR or TEMPLATE)
            result.modelCategory = ModelCategory.REGULAR;
            int modelCategoryStartIndex = line.indexOf(FILE_VERSION_MODEL_CATEGORY_PREFIX); //" category='"
            int modelCategoryEndIndex = line.indexOf('\'', modelCategoryStartIndex);
            if (modelCategoryEndIndex > modelCategoryStartIndex) {
                String modelCategoryName = line.substring(modelCategoryStartIndex, modelCategoryEndIndex).toUpperCase();
                result.modelCategory = Enum.valueOf(ModelCategory.class, modelCategoryName);
            }
            if (result.modelCategory == null) {
                throw new LGMVersionException(getResString("lgmversionsfehler"));
            }

        }
        return result;
    }

    //scheint im Moment ungenutzt, wird aber eigentlich beim DataImportModule gebraucht
    public static String getCurrentVersionString(final GDCollection gdcoll) {
        return SUPPORTED_XML_VERSIONS[SUPPORTED_XML_VERSIONS.length - 1] + "\n" + getCurrentFileVersion(gdcoll) + "\n";
    }

    private static final String getCurrentFileVersion(final GDCollection gdcoll) {
        //"<!--Tool3lgmFile"
        StringBuilder sb = new StringBuilder(FILE_VERSION_LINE_START.trim()); //trim() removes the last whitespace
        //"<!--Tool3lgmFile version='"
        sb.append(FILE_VERSION_NUMBER_PREFIX);
        //"<!--Tool3lgmFile version='3.7"
        sb.append(FILE_VERSION_SUPPORTED_NUMBERS[FILE_VERSION_SUPPORTED_NUMBERS.length - 1]);
        //"<!--Tool3lgmFile version='3.7'"
        sb.append("'");
        //"<!--Tool3lgmFile version='3.7' metamodel='"
        sb.append(FILE_VERSION_METAMODEL_CLASS_PREFIX);
        //"<!--Tool3lgmFile version='3.7' metamodel='TLGMServiceMetaModel@2388259974838049670"
        sb.append(gdcoll.getMetaModelContext().getMetaModelID());
        //"<!--Tool3lgmFile version='3.7' metamodel='TLGMServiceMetaModel@2388259974838049670'"
        sb.append("'");
        //"<!--Tool3lgmFile version='3.7' metamodel='TLGMServiceMetaModel@2388259974838049670' category='"
        sb.append(FILE_VERSION_MODEL_CATEGORY_PREFIX);
        //"<!--Tool3lgmFile version='3.7' metamodel='TLGMServiceMetaModel@2388259974838049670' category='REGULAR"
        sb.append(gdcoll.getModelCategory().name());
        //"<!--Tool3lgmFile version='3.7' metamodel='TLGMServiceMetaModel@2388259974838049670' category='REGULAR'-->"
        sb.append("'-->");
        return sb.toString();
    }

    public static final String getCurrentFileVersionBare(final GDCollection gdcoll) {
        String fileVersion = getCurrentFileVersion(gdcoll);
        if (fileVersion.startsWith("<!--")) {
            fileVersion = fileVersion.substring("<!--".length(), fileVersion.length() - "-->".length());
        }
        return fileVersion;
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