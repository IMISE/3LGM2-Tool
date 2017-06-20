package de.imise.util.clipboard;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.HashMap;

import de.imise.util.ReflectionUtils;
import de.imise.util.io.FileHandler;

/**
 * TODO:AXS:20.11.2012 entfernen?
 * 
 * FUNKTIONIERT NICHT!
 * <p>
 * Klasse zur Verarbeitung von Inhalten der System-Zwischenablage.
 * <p>
 * Probleme:<br>
 * Der in der Zwischenablage befindliche, durch Programme wie etwa Excel erzeugte html-String
 * ist nicht xml-konform. Damit ist das Erstellen eines {@link Document}s mittels des 
 * {@link SAXBuilder}s nicht möglich. Demnach gibt es auch keine Möglichkeit eine xsl-
 * Transformation auf diesem html-Inhalt durchzuführen. <br>
 * Programme wie "HTMLCleaner" oder "CyberNeko" schaffen es auch nicht, die Inhalte
 * xml-konform zu machen.
 * <p>
 * Aus diesem Grund sollte vorerst die Klasse {@link SimpleContentParser} zum Verarbeiten von 
 * Inhalten der System-Zwischenablage verwendet werden.
 * 
 * <p>
 * Lösungsvorschläge:
 *   <li>eventuell {@link CleanerProperties} besser konfigurieren
 *   <li>das {@link Document} manuell erstellen
 *   <li>einen eigenen Wandler von html in xml schreiben
 *  	
 * @author Frank
 */
class XMLContentParser {

	
	/** 
	 * Datei, die die Bezeichnung der Programme(z.b. excel, word, 3lgm.uerfieldtable, ...), und die 
	 * Pfade der XSL-Dateien enthält, die für die Transformation der durch die Programme 
	 * erzeugten Inhalte in der System-Zwischenablage zuständig sind.
	 */ 
//	private static final File PARSER_MAP = new File("src/clipboard/XSL/ParserMap.txt");
	@SuppressWarnings("unused")
	private static final File PARSER_MAP = new File(ReflectionUtils.getClassFile(XMLContentParser.class).getParent()+File.separator + "XSL" + File.separator + "ParserMap.txt");
	
	private static final String TEMP_FILE_PREFIX = "tmp";
	private static final String TEMP_FILE_SUFFIX_CSV = "csv";
	private static final String TEMP_FILE_SUFFIX_CCS = "ccs";
	
	/** Der Standard-Parser für diese Plattform */
	private static XMLContentParser defaultParser;
	
	/**
	 * Temporäre Datei, die das Ergebnis der Umwandlung von
	 * <code>String</code> - Inhalten der Zwischenablage in CSV<code>String</code>s
	 * enthält. Es wird nur diese Datei für CSV-Inhalte verwendet um den 
	 * Speicherplatzbedarf, der bei permanentem Erzeugen neuer temporärer Dateien entsteht,
	 * zu vermeiden.
	 */
	private File csvTempFile = FileHandler.createTempFile(TEMP_FILE_PREFIX,TEMP_FILE_SUFFIX_CSV);

	/**
	 * Temporäre Datei, in der die <code>String</code>-Inhalte der Zwischenablage
	 * gespeichert werden. Es wird nur diese Datei für die Speicherung verwendet, um den 
	 * Speicherplatzbedarf, der bei permanentem Erzeugen neuer temporärer Dateien entsteht,
	 * zu vermeiden.
	 */
	private File ccsTempFile = FileHandler.createTempFile(TEMP_FILE_PREFIX,TEMP_FILE_SUFFIX_CCS);
	
	/**
	 * Zuweisung von Programmen und xsl's
	 * @see #PARSER_MAP 
	 */
	private HashMap<String, File> parsers;
	
	/**
	 * Konstruktor
	 */
	private XMLContentParser() {
		super();
	}
	
	/**
	 * Wandelt <code>contentString</code>s der System-Zwischenablage in <code>String[][]</code> um.
	 * @param contentString
	 * @return
	 */
	public String[][] toStringArray(String contentString) throws NullPointerException {
		return null;
	}

	/**
	 * Wandelt <code>contentString</code>s der System-Zwischenablage in 
	 * komma-separierte <code>String</code>s um.
	 * @param contentString
	 * @return
	 */
	public String toCSVString(String contentString) throws NullPointerException {
		setCCSTempFile(contentString);
		ccsTOcsv();
		return toString(csvTempFile);
	}

	/**
	 * Liest den Inhalt von <code>file</code> in einen String aus und gibt diesen wieder.
	 * @param file
	 * @return
	 */
	private static final String toString(File file) {
		
		FileReader fr = null;
		File[] files = null;
		String csvString = "";
		
		files = splitFile(file);
		
		for (File f : files) {
			try {
				fr = new FileReader(f);
				char[] chars = new char[(int)f.length()];
				fr.read(chars);
				fr.close();
				csvString = csvString.concat(String.copyValueOf(chars));
			} catch (Exception e) {
				e.printStackTrace();
			} 
		}
		
		return csvString;
		
	}
	
	/**
	 * Schreibt <code>contentString</code> in {@link #ccsTempFile}.
	 * @param contentString
	 */
	private void setCCSTempFile(String contentString) {

		FileWriter fw = null;
		try {
			fw = new FileWriter(ccsTempFile);
			fw.write(contentString);
			fw.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Wandelt den Inhalt von {@link #ccsTempFile} in CSV-Format um und speichert das
	 * Ergebnis in {@link #csvTempFile}
	 */
	private void ccsTOcsv() {
		
		//TODO: korrekte Transformation
		// //////////////////////////////////////////////
		// Document korrekt erstellen                  // 
		// passende xsl-Datei mittels parsers finden   //
		// Transformation anwenden                     //
		// Ergebnis in csvTempFile speichern           //
		/////////////////////////////////////////////////
		
		//test1();
		//System.err.println(correct(toCSVString(csvTempFile)));
		/*
			HtmlCleaner cleaner = new HtmlCleaner(); 
			String s =(toString(ccsTempFile));
			s = s.replace(':', ' ');
			TagNode tn = cleaner.clean(s);
			CleanerProperties cp = new CleanerProperties();
			cp.setTreatUnknownTagsAsContent(true);
			JDomSerializer seri = new JDomSerializer(cp);
			Document doc = seri.createJDom(tn);
			System.err.println(doc.getContentSize());

			//Document doc = b.build(new File("src/clipboard/XSL/SampleXMLExcel.xml"));
			//XSLTransformer trans = new XSLTransformer(parsers.get("excel"));
			//Document doc2 = trans.transform(doc);
			//XMLOutputter xmlOut = new XMLOutputter(Format.getPrettyFormat());
			//xmlOut.output(doc2, System.out);
			 * */
	
	}
	
	/** testet korrektes Lesen und Schreiben beider tmp-Dateien */
    @SuppressWarnings("unused")
	private void test1() {
		try {
			FileWriter fw = new FileWriter(csvTempFile);
			FileReader fr = new FileReader(ccsTempFile);
			char[] chars = new char[(int)ccsTempFile.length()];
			fr.read(chars);
			fw.write(chars);
			fw.close();
			fr.close();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}


	/**
	 * Noch nicht korrekt implementiert!
	 * <p>
	 * Gibt einfach ein Array wieder, das ausschließlich aus <code>file</code> besteht,
	 * falls die zulässige Zeichenanzahl nicht überschritten wird.
	 * <br>
	 * Sonst wird eine Exception geworfen.
	 * <p>
	 * Ziel:
	 * Zerlegt <code>file</code> in einzelne Dateien, sodass die Anzahl ihrer Zeichen kleiner
	 * als {@link Integer#MAX_VALUE} ist. Damit können dann alle Zeichen der einzelnen Dateien
	 * in jeweils einem <code>charArray</code> erfasst werden.
	 * @param file
	 * @return
	 */
	private static final File[] splitFile(File file) {
		// kein splitten notwendig
		if(file.length() <= Integer.MAX_VALUE) 
			return new File[] {file};
		throw new IllegalArgumentException("Die Datei: " + file.getName() + " überschreitet die zulässige Größe");
	}
	
	/**
	 * Setzt fehlende Anführungszeichen in den contentString und gibt das Ergebnis wieder.
	 * @param contentString
	 * @return
	 */
	@SuppressWarnings("unused")
	private static final String correct(String contentString) {
		int lastPos = 0;
		int pos1;
		int pos2;
		
		StringBuffer sb = new StringBuffer();
		
		while(true) {
			pos1 = contentString.indexOf('=', lastPos);
			
			if(pos1==-1) {
				sb.append(contentString.substring(lastPos, contentString.length()));
				break;
			}
			
			// Anfügen inkl. '='
			pos1++;
			sb.append(contentString.substring(lastPos, pos1));
			
			if (contentString.charAt(pos1) != '"') {
				sb.append('"');
				pos2 = contentString.indexOf(' ',pos1);
				if (contentString.indexOf('>',pos1)<pos2)
					pos2 = (contentString.indexOf('>',pos1));
				if (contentString.indexOf('\n',pos1)<pos2)
					pos2 = (contentString.indexOf('\n',pos1));
				
				// Anfügen ohne ' '
				sb.append(contentString.substring(pos1, pos2));
				sb.append('"');
			}
			else {
				pos2=contentString.indexOf('"', pos1+1);
				pos2++;
				sb.append(contentString.substring(pos1, pos2));
			}
			
			lastPos = pos2;
		}
		
		return sb.toString();
		
	}
	
	/**
	 * Erzeugt den Standard-Parser für diese Plattform 
	 * @return
	 */
	public static XMLContentParser getDefaultParser() {
		
		if (defaultParser == null) { // defaultParser noch nicht erzeugt
			defaultParser = new XMLContentParser();
			defaultParser.parsers = new  HashMap<String, File>(1,1);
			defaultParser.parsers.put("excel", new File("src/clipboard/XSL/ExcelToCSV.xsl"));
		}
		
		return defaultParser;
		
		
		
	}
	
}
