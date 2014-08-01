/*
 * Created on 19.07.2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package de.imise.tool3lgm.xslt;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;


/**
 * @author Thomas Rudert
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class XSLTScript {

	/**
	 * COMMENTME
	 */
	private String source;
	
	/**
	 * COMMENTME
	 */
	private String name = null;
	
	/**
	 * COMMENTME
	 */
	private String description = null;
	
	/**
	 * COMMENTME
	 */
	private String type = null;
	
	/**
	 * COMMENTME
	 */
	private String author = null;

	/**
	 * COMMENTME
	 */
	private URL url = null;
	
	/**
	 * COMMENTME
	 */
	private File file = null;

	/**
	 * 
	 */
	public XSLTScript(URL url) throws IOException {
//TODO:AXS: testen, ob der Kommentar unten noch stimmt	    
	    this.url = url;
		//Aus irgendeinem Grund kommt die URL mit "%5c" und "/" als Dateitrenner gemischt rein
		//und das obwohl File.separator einen Backslash ("\") liefert. Deswegen wird hier einfach
		//jede Möglichkeit getestet, um an den letzten Namen innerhalb des Dateipfades zu kommen
		int index = url.toString().lastIndexOf("%5c");
		int offset = 0;
		if (index < 0)
			index = url.toString().lastIndexOf(File.separator);
		else
			offset = "%5c".length();
		if (offset==0) {
			if (index<0)
				index = url.toString().lastIndexOf("/");
			else
				offset = 1;
		}
		if (offset==0 && index>=0)
			offset = 1;
		source = url.toString().substring(index < 0 ? 0 : index + offset);

		BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));
		String line;
		while ((line = reader.readLine()) != null) {
			index = line.indexOf("-->");
			if (line.startsWith("<!--name: ")) {	
				name = line.substring("<!--name: ".length(), index < 0 ? line.length() : index);
			} else if (line.startsWith("<!--description: ")) {
				description = line.substring("<!--description: ".length(), index < 0 ? line.length() : index);
			} else if (line.startsWith("<!--type: ")) {
				type = line.substring("<!--type: ".length(), index < 0 ? line.length() : index);
			} else if (line.startsWith("<!--author: ")) {
				author = line.substring("<!--author: ".length(), index < 0 ? line.length() : index);
			}
			if (name != null && description != null && type != null && author != null)
				break;
		}
	}
	
	public XSLTScript(File file) {
		this.file = file;
		source = file.toString();
		String[] content = XSLTFileHandler.checkContent(file);
		name = content[1];
		type = content[2];
		description = content[3];
		author = content[4];		
	}
	
	public String getSource() {
		return source;
	}

	public String getName() {
		return name;
	}
	
	public String getDescription() {
		return description;
	}
	
	public String getType() {
		return type;
	}
	
	public String getAuthor() {
		return author;
	}
	
	public InputStream openStream() throws IOException {
		if (file != null)
			return new FileInputStream(file);
		else if (url != null)
			return url.openStream();
		else
			return null;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
//	    assert false : "hashCode not designed";
	    return 42; // any arbitrary constant will do 
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		return (obj instanceof XSLTScript && ((XSLTScript)obj).getSource().equals(getSource()));
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return getName();
	}
	
	/**
	 * @return
	 */
	public File getFile() {
		return file;
	}

	/**
	 * @return
	 */
	public boolean isReadOnly() {
		return (file == null && url != null);
	}
	
}