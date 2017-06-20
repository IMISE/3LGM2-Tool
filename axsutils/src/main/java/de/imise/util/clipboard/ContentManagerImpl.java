package de.imise.util.clipboard;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Klasse zum Austausch von Daten zwischen dieser Software und anderen Desktop-Anwendungen über
 * die System-Zwischenablage.
 * 
 * @author Frank
 *
 */
public class ContentManagerImpl implements ClipboardConstants, ContentManager{

	/** Der aktuelle {@link ContentManagerImpl} */
	private static ContentManagerImpl currentManager;


	/** Die System-Zwischenablage */
	private Clipboard clipboard;

	/**
	 * Konstruktor
	 */
	protected ContentManagerImpl() {
		super();
	}

	/**
	 * Gibt die System-Zwischenablage wieder.
	 * @see de.imise.util.clipboard.ContentManager#getClipboard()
	 */
	@Override
	public Clipboard getClipboard() {
		return clipboard;
	}

	/**
	 * Gibt den Inhalt der Zwischenablage wieder.
	 * @see de.imise.util.clipboard.ContentManager#getClipboardContents()
	 */
	@Override
	public Transferable getClipboardContents() {
		return getClipboard().getContents(null);
	}

	/**
	 * Gibt alle <code>String</code> - Inhalte der Zwischenablage wieder.
	 * @return
	 */
	public String[] getClipboardStringContents() {

		Transferable t = getClipboardContents();

		ArrayList<String> l = new ArrayList<String>(5);

		for (DataFlavor dataFlavor : t.getTransferDataFlavors()) {
			Object content;
			try {
				content = t.getTransferData(dataFlavor);

				if (content instanceof String) {
					l.add((String)content);
				}

			} catch (UnsupportedFlavorException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return l.toArray(new String[l.size()]);

	}

	/**
	 * Gibt den String zurück der sich an der Position {@link ClipboardConstants#CONTENT_STRING_POSITION}
	 * der <code>String</code> - Inhalte der System-Zwischenablage befindet.<br>
	 * Falls keine <code>String</code> - Inhalte existieren, wird <code>null</code> zurückgegeben.
	 * @return
	 * @see de.imise.util.clipboard.ContentManager#getClipboardContent()
	 */
	@Override
	public String getClipboardContent() {
		try {
			return getClipboardStringContents()[CONTENT_STRING_POSITION];
		} catch (ArrayIndexOutOfBoundsException e) {
			return null;
		}
	}

	/**
	 * Setzt <code>contents</code> als den Inhalt der System-Zwischenablage.
	 * @param contents
	 * @see de.imise.util.clipboard.ContentManager#setClipboardContent(java.lang.Object)
	 */
	@Override
	public void setClipboardContent(Object contents) {
		InternalClipboardContent icc = new InternalClipboardContent(contents);
		clipboard.setContents(icc, this);


		/*
		wenn Table auf seine in die Zwischenablage geschobenen Inhalte
		zurückgreift, kommt es zu einem Fehler. Das könnte daran liegen,
		dass sich an der Position 1 der Zwischenablage nichts mehr befindet.
		--> Eventuell ein Transferable aus zwei StringSelections erstellen
			und als content übergeben.
		 */

	}

	/**
	 * Methode holt sich die aktuelle System-Zwischenablage wieder und setzt {@link #clipboard}.
	 * @see java.awt.datatransfer.ClipboardOwner#lostOwnership(java.awt.datatransfer.Clipboard, java.awt.datatransfer.Transferable)
	 */
	@Override
	public void lostOwnership(Clipboard clipboard, Transferable contents) {
	}

	/**
	 * Gibt den aktuellen {@link ContentManagerImpl} zurück.
	 * @return {@link currentManager}
	 */
	public static ContentManagerImpl getCurrentManager() {

		if(currentManager == null) { // aktueller ContentManager wurde noch nicht erstellt
			currentManager = new ContentManagerImpl();
			currentManager.clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
		}
		return currentManager;
	}

	/** 
	 * Repräsentation des im Konstruktor übergebenen <code>data</code> als {@link Transferable}.<br>
	 * Dabei wird immer <code>data.toString()</code> durch {@link #getTransferData(DataFlavor)}
	 * zurückgegeben.
	 * 
	 * @author fstephan
	 *
	 */
	private static class InternalClipboardContent implements Transferable {

		private String data;

		public InternalClipboardContent(Object data) {
			this.data = data.toString();
		}

		@Override
		public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
			if (flavor.equals(DataFlavor.stringFlavor))
				return data;
			throw new UnsupportedFlavorException(flavor);
		}

		@Override
		public DataFlavor[] getTransferDataFlavors() {
			return new DataFlavor[] {DataFlavor.stringFlavor, DataFlavor.stringFlavor};

		}

		@Override
		public boolean isDataFlavorSupported(DataFlavor flavor) {
			return (flavor.equals(DataFlavor.stringFlavor));
		}

		@Override
		public String toString() {
			StringBuffer sb = new StringBuffer();
			sb.append(super.toString());
			sb.append("\n[");
			sb.append(data);
			sb.append("]");
			return sb.toString();
		}


	}








}
