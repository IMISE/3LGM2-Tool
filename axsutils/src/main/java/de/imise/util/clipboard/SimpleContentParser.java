package de.imise.util.clipboard;

import java.io.File;
import java.util.Collection;

import de.imise.util.collections.CollectionUtils;
import de.imise.util.io.FileHandler;



/**
 * Klasse zur Verarbeitung von Inhalten der System-Zwischenablage.
 * <p>
 * Bekannte Fehler:
 * 	<li>leere Zellen am rechten und unteren Rand einer Selektion in einer Excel-Tabelle werden nicht übernommen
 * 
 * @see
 * @author Frank
 *
 */
public class SimpleContentParser implements ClipboardConstants,ContentParser {

	/* ******************************* Beginn: Initialisierung ***************************** */
	/**
	 * Konstruktor
	 */
	protected SimpleContentParser() {
		super();
	}
	/* ******************************* Ende: Initialisierung ***************************** */


	/* *************************** Beginn: Parsing extern->intern ************************* */

	/**
	 * Wandelt, wenn möglich, <code>externalContent</code> in ein ObjectArray um.<br>
	 * Falls eine Umwandlung nicht möglich ist, wird eine {@link IllegalContentException} geworfen.
	 * <p>
	 * 
	 * Gültige Formate für <code>externalContent</code> sind:
	 * 	<li> Tabulator/Zeilenumbruch-separierte {@link String}s
	 * 	<li> {@link File}s, die solche {@link String}s enthalten
	 * 
	 * @see ContentParser#toInternalContent(Object)
	 * @throws IllegalContentException
	 * @param externalContent
	 * @return
	 */
	@Override
	public String[][] toInternalContent(Object externalContent) throws IllegalContentException{
		String[][] internalContent;
		try {
			internalContent = _toInternalContent(externalContent);
		} catch (Exception e) {
			throw new IllegalContentException(externalContent);
		}
		if(internalContent == null)
			throw new IllegalContentException(externalContent);
		return internalContent;
	}

		
	/**
	 * Wandelt, wenn möglich, <code>externalContent</code> in ein ObjectArray um.<br>
	 * Falls eine Umwandlung nicht möglich ist, wird <code>null</code> zurückgegeben.
	 * 
	 * @see SimpleContentParser#toInternalContent(Object)
	 * @param externalContent
	 * @return
	 */
	private static final String[][] _toInternalContent(Object externalContent) {
		
//		Fehler nach paste bei öffnen der Zelle
		
		if(externalContent == null)
			return null;

		String contentString;

		if(externalContent instanceof File)
			contentString = FileHandler.readFile((File)externalContent);
		else
			contentString = externalContent.toString();

		if(contentString.equals(""))
			return new String[][]{{""}};

		String[] rows = (getRows(contentString));

		if(rows == null)
			return null;

		int rowCount = rows.length;

		if(rowCount == 0)
			return null;

		int columnCount = getColumnCount(rows[0]);

		if(columnCount == 0)
			return null;

		String[][] internalContent = new String[rowCount][columnCount];

		int y=0;
		StringBuffer sb = new StringBuffer();

		for (int i=0; i<rowCount; i++) {
			String row = rows[i];
			for (int j=0; j<row.length(); j++) {
				if (row.charAt(j) == ROW_TAG) // gehe in nächste Reihe
					break;
				if(row.charAt(j) == COLUMN_TAG) { // gehe in nächste Spalte
					internalContent[i][y] = sb.toString();
					y++;
					sb = new StringBuffer();
					continue;
				}
				sb.append(row.charAt(j)); // füge Inhalt hinzu
			}
			internalContent[i][y] = sb.toString();
			y=0;
			sb = new StringBuffer();
		}
		return replaceNull(internalContent);
	}

	/**
	 * Gibt die Zeilen in <code>contentString</code> wieder
	 * @param contentString
	 * @return
	 */
	private static final String[] getRows(String contentString) {
		return contentString.split(String.valueOf(ROW_TAG));
	}

	/**
	 * Gibt die Anzahl der Spalten in <code>rowString</code> wieder
	 * @param rowString
	 * @return
	 */
	private static final int getColumnCount(String rowString) {
		char[] chars = rowString.toCharArray();
		int columnCount = 0;
		for (char c : chars) {
			if(c == COLUMN_TAG)
				columnCount++;
		}
		return (columnCount+1);
	}
	
	/**
	 * Ersetzt jedes <code>null</code> in <code>internalContent</code> durch den leeren String.
	 * @param internalContent
	 * @return
	 */
	private static String[][] replaceNull(String[][] internalContent) {
		for (int i = 0; i < internalContent.length; i++) {
			for (int j = 0; j < internalContent[0].length; j++) {
				if (internalContent[i][j] == null)
					internalContent[i][j] = "";
			}
		}
		return internalContent;
	}

	/* *************************** Ende: Parsing extern->intern ************************* */


	/* *************************** Beginn: Parsing intern->extern ************************* */

	/**
	 * Wandelt, wenn möglich, <code>internalContent</code> in einen <code>String</code> um.<br>
	 * Falls eine Umwandlung nicht möglich ist, wird eine {@link IllegalContentException} geworfen.
	 * <p>
	 * 
	 * Gültige Formate für <code>internalContent</code> sind:
	 * 	<li> <code>Object[][]</code>
	 * 	<li> <code>String[][]</code>
	 * 	<li> gültige <code>Collection</code>s (<b>siehe auch:</b> {@link CollectionUtils#toArray(Collection)})
	 * 
	 * @throws IllegalContentException
	 * @see ContentParser#toExternalContent(Object)
	 */
	@Override
	public String toExternalContent(final Object internalContent) throws IllegalContentException {
		String externalContent = null;
		try {
			externalContent = _toExternalContent(internalContent);
		} catch (Exception e) {
			throw new IllegalContentException(internalContent,e);
		}
		
		if (externalContent == null)
			throw new IllegalContentException(internalContent);
		return externalContent;
	}
	
	/**
	 * Wandelt, wenn möglich, <code>internalContent</code> in einen <code>String</code> um.<br>
	 * Falls eine Umwandlung nicht möglich ist, wird <code>null</code> zurückgegeben.
	 * 
	 * @throws ArrayIndexOutOfBoundsException
	 * @see SimpleContentParser#toExternalContent(Object)
	 * @param internalContent
	 * @return
	 */
	private static final String _toExternalContent(Object internalContent) {
		
		String[][] internalContentArray = null;
		StringBuffer externalContent = new StringBuffer();

		// Typ von internalContent ermitteln und auf String[][] parsen
		if(internalContent instanceof String[][])
			internalContentArray = (String[][])internalContent;
		else if (internalContent instanceof Object[][])
			internalContentArray = CollectionUtils.toStringArray((Object[][]) internalContent);
		else if (internalContent instanceof Collection<?>)
			internalContentArray = toStringArray((Collection<?>)internalContent);
		
		if(internalContentArray == null)
			return null;
		
		// externalContent füllen
		String s;
		int m = internalContentArray.length;
		int n = internalContentArray[0].length;
		for(int i=0; i<m; i++) {
			for (int j=0; j<n; j++) {
				s = internalContentArray[i][j];
				if(s != null)
					externalContent.append(s);
				if(j < n-1)
					externalContent.append(COLUMN_TAG);
			}
			if(i < m-1)
				externalContent.append(ROW_TAG);
		}
		return externalContent.toString();
	}
	

	/**
	 * Wandelt <code>rows</code> in ein <code>String[][]</code> um, falls es sich um eine gültige
	 * <code>Collection</code> handelt.<br>
	 * Sonst wird <code>null</code> zurückgegeben.
	 * 
	 * @see CollectionUtils#toArray(Collection)
	 * @param rows
	 * @return
	 */
	private static final String[][] toStringArray(Collection<?> rows) {
		@SuppressWarnings("unchecked")
		Object[][] objectArray = CollectionUtils.toMatrixArray((Collection<Collection<?>>) rows);
		if(objectArray == null)
			return null;
		return CollectionUtils.toStringArray(objectArray);
	}
	
	/* *************************** Ende: Parsing intern->extern ************************* */



	/* *************************** Beginn: statische Methoden ************************* */

	/**
	 * Gibt den Standard-Parser für diese Plattform wieder.
	 * @return
	 */
	public static SimpleContentParser getDefaultParser() {
		return new SimpleContentParser();
	}

	/* *************************** Ende: statische Methoden ************************* */



}
