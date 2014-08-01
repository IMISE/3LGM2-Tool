/*
 * Created on 14.06.2004
 */
package de.imise.tool3lgm.graphtools.analyse.context;

import java.awt.BorderLayout;
import java.io.File;
import java.util.List;

import javax.swing.AbstractButton;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;

import de.imise.util.Alphabetical;

import de.imise.tool3lgm.Tool3lgm;
import de.imise.tool3lgm.Tool3lgmConstants;

/**
 * @author Thomas, Sebastian Weber, AXS
 * 
 */
public class AnalyseRepositoryFrame extends JFrame{

	
	/**
	 * Tabelle, in der die Analysen angezeigt werden
	 */
	static AnalyseRepositoryFrameTable table;
	
	private static AbstractButton[] buttons = {
		new JButton(AnalyseRepositoryFrameActions.ACTION_START_ANALYSIS),
		new JButton(AnalyseRepositoryFrameActions.ACTION_RESET_ANALYSIS_RESULT),
		new JButton(AnalyseRepositoryFrameActions.ACTION_CLOSE_DIALOG),
	};
	
	/**
	 * Lokale Kopie der Analysen aus dem Repository. Die Liste wird für die Tabelle, die die 
	 * selbe Liste nutzt, alphabetisch sortiert.
	 */
	static List<XMLAnalyse> analysen;

	/**
	 * Speichert die Analysendatei auf der der Benutzer grade arbeitet, wenn er eine Analysedatei
	 * nicht über das Repository geöffnet oder gespeichert hat. Solange man sich nur vom Repository
	 * die Analysen geben lässt, bleibt diese Variable null.<br>
	 * Diese Variable wird nur gebraucht, um immer in das zuletzt vom Benutzer ausgewählte Verzeichnis
	 * wechseln zu können, damit er es nicht immer wieder neu asuwählen muss.
	 */
	static File analyseFile = null;
	
	/** Instanz dieser Klasse. */
	static AnalyseRepositoryFrame dialog = new AnalyseRepositoryFrame();

	/**
	 * Wenn sich die Analysen geändert haben, muss beim Schließen des Frames gefragt werden,
	 * ob sie als Repository gespeichert werden sollen.
	 */
	static boolean analysisChanged = false;
	
	/**
	 * Konstruktor. Zugriff auf diese Klasse ist über die Methode showDialog möglich.
	 * @param t	die Tool3lgm Klasse, in der dieser Dialog angezeigt wird.
	 */
	private AnalyseRepositoryFrame () {
		super(Tool3lgmConstants.getResString("repository"));
		setIconImage(Tool3lgm.tool.getIconImage());

		JMenu menuFile = new JMenu(Tool3lgmConstants.getResString("file"));
		menuFile.add(new JMenuItem(AnalyseRepositoryFrameActions.ACTION_LOAD_STANDARD_REPOSITORY));
		menuFile.add(new JMenuItem(AnalyseRepositoryFrameActions.ACTION_SAVE_REPOSITORY));
		menuFile.add(new JSeparator());
		menuFile.add(new JMenuItem(AnalyseRepositoryFrameActions.ACTION_IMPORT_ANALYSIS));
		menuFile.add(new JMenuItem(AnalyseRepositoryFrameActions.ACTION_EXPORT_ANALYSIS));
		menuFile.add(new JSeparator());
		menuFile.add(new JMenuItem(AnalyseRepositoryFrameActions.ACTION_CLOSE_DIALOG));
		JMenu menuAnalysis = new JMenu(Tool3lgmConstants.getResString("analysis"));
		menuAnalysis.add(new JMenuItem(AnalyseRepositoryFrameActions.ACTION_NEW_ANALYSIS));
		menuAnalysis.add(new JMenuItem(AnalyseRepositoryFrameActions.ACTION_DELETE_ANALYSIS));
		menuAnalysis.add(new JSeparator());
		menuAnalysis.add(new JMenuItem(AnalyseRepositoryFrameActions.ACTION_ANALYSIS_EDITOR));
		
		JMenuBar menuBar = new JMenuBar();
		menuBar.add(menuFile);
		menuBar.add(menuAnalysis);
		setJMenuBar(menuBar);
		
		setAnalysen(AnalyseRepository.getXMLAnalysen());
		table = new AnalyseRepositoryFrameTable();
		JScrollPane tableScrollPane = new JScrollPane(table);
		
		//das Buttonpanel zusammenbauen
		JPanel buttonPanel = new JPanel();
		for (int i=0; i<buttons.length; i++)
			buttonPanel.add(buttons[i]);

		//Tabelle und Buttonpanel ins ContentPane einfügen
		JPanel mainPanel = new JPanel(new BorderLayout());
		mainPanel.add(buttonPanel, BorderLayout.SOUTH);
		mainPanel.add(tableScrollPane, BorderLayout.CENTER);
		getContentPane().add(mainPanel);
		pack();
		
	}
	
	/**
	 * Setzt die übergeben ArrayList als die Analysenliste dieses Dialoges und sortiert 
	 * sie für die Tabelle. 
	 */
	static void setAnalysen(List<XMLAnalyse> analysen) {
		Alphabetical.sort(analysen);
		AnalyseRepositoryFrame.analysen = analysen;
	}
	
	/**
	 * Fügt die übergebene XMLAnalyse in die Liste der Analysen ein, wenn sie nicht <code>null</code> ist
	 * und noch nicht in der Liste vorkommt.
	 * @param toadd
	 * @param ignoreDuplicates wenn <code>true</code> werden identische Analysen auch mehrfach eingefügt, sonst nicht
	 * @return
	 */
	static boolean addAnalyse(XMLAnalyse toadd, boolean ignoreDuplicates) {
		if (toadd==null || (!ignoreDuplicates && analysen.contains(toadd)))
			return false;
		Alphabetical.insert(analysen, toadd);
		return true;
	}
	
	/**
	 * Zeigt den AnalyseRepositoryFrame an.
	 */
	public static void showDialog() {
		if (table!=null)
			table.update();
		dialog.setVisible(true);
	}

	/**
	 * Prüft den enabled-Status aller Buttons
	 */
	public static final void refreshActionStates() {
		for (int i=0; i<buttons.length; i++)
			buttons[i].setEnabled(buttons[i].getAction().isEnabled());
		//Das hier funktioniert nur solange richtig, wie es in Menüs keine Untermenüs mit zu aktualisierenden
		//Aktionen gibt
		JMenuBar menuBar = AnalyseRepositoryFrame.dialog.getJMenuBar();
		for (int i=0; i<menuBar.getComponentCount(); i++) {
			JMenu menu = (JMenu) menuBar.getComponent(i);
			if (menu.getAction()!=null)
				menu.setEnabled(menu.getAction().isEnabled());
			for (int j=0; j<menu.getItemCount(); j++) {
				//Separatoren liefern hier null
				if (menu.getItem(j)==null)
					continue;
				JMenuItem item = menu.getItem(j);
				if (item.getAction()!=null)
					item.setEnabled(item.getAction().isEnabled());
			}
		}
	}
	
	/* (non-Javadoc)
	 * @see java.awt.Window#dispose()
	 */
	@Override
	public void dispose() {
		super.dispose();
		//beim Schließen immer die Analysen wieder auf die des Repositories setzen
		setAnalysen(AnalyseRepository.getXMLAnalysen());
		analysisChanged = false;
	}
}
