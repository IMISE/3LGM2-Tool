/*
 * Created on 30.10.2007
 */
package de.imise.tool3lgm.graphtools.dialog;

import java.awt.Component;
import java.awt.Dialog;
import java.awt.Frame;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;

import de.imise.tool3lgm.Help;
import de.imise.tool3lgm.Tool3lgm;
import de.imise.tool3lgm.Tool3lgmConstants;
import de.imise.tool3lgm.graphtools.GDCollection;
import de.imise.tool3lgm.graphtools.GraphDocument;
import de.imise.tool3lgm.graphtools.LGMGraphDocument;
import de.imise.util.swing.component.TabbedPane;

/**
 * Allgemeiner Eigenschaftendialog.<br>
 * In dieser Form kann er als Eigenschaftendialog für ein Modell genutzt werden. Der
 * Eigenschaftendialog von Modellelementen ist eine spezielle Unterklasse dieser Dialogklasse.
 * 
 * @author AXS
 * @created 30.10.2007
 */
public class PropertyDialog extends JDialog implements WindowListener {

    /** TabbedPane in das alle Panels kommen */
    protected TabbedPane tab;

    /**
     * Ok-Knopf für die Dialoge
     */
    protected JButton okButton;

    /**
     * Abbrechen-Knopf für die Dialoge
     */
    protected JButton cancelButton;

    /**
     * Übernehmen-Knopf für die Dialoge
     */
    protected JButton applyButton;

    /**
     * Hilfe-Knopf für die Dialoge
     */
    protected JButton helpButton;

    /** Modell in dessen Kontext der Dialog steht */
    protected GDCollection gdcoll;

    /** Hauptmodell der GDCollection */
    protected LGMGraphDocument doc;

    /** ID des Dialoges mit der alle Transaktionen durchgeführt werden */
    protected int transactionID;

    /**
     * Panel, das <code>applyButton</code>,<code>okButton</code> und <code>cancelButton</code>
     * beinhaltet
     */
    protected JPanel controlPanel;

    /**
     * @param graphDocument Document in dessen Kontext der Dialog steht
     */
    public PropertyDialog(final GDCollection gdcoll) {
        this(Tool3lgm.tool, gdcoll);
    }

    /**
     * @param owner
     * @param gdcoll Modell, in dessen Kontext der Dialog steht
     */
    public PropertyDialog(final Frame owner, final GDCollection gdcoll) {
        super(owner, "", false);
        init(gdcoll);
    }

    /**
     * @param owner
     * @param graphDocument Document in dessen Kontext der Dialog steht
     */
    public PropertyDialog(final Dialog owner, final GDCollection gdcoll) {
        super(owner, "", false);
        init(gdcoll);
    }

    /**
     * @param gdcoll
     */
    private void init(final GDCollection gdcoll) {
        this.gdcoll = gdcoll;
        addWindowListener(this);
        doc = gdcoll.getMainGraphDocument();
        okButton = new JButton(Tool3lgmConstants.getResString("ok"));
        cancelButton = new JButton(Tool3lgmConstants.getResString("cancel"));
        applyButton = new JButton(Tool3lgmConstants.getResString("apply"));
        String helpID = Help.getHelp().getHelpID(this);
        if (helpID != null && helpID.length() > 0) {
            helpButton = new JButton(Tool3lgmConstants.getResString("hilfeButtonText"));
            helpButton.setMnemonic('H');
            Help.getHelp().enableHelpOnButton(helpButton, helpID);
        }
        createNewTransactionID();
    }

    /**
     * @return Returns the transactionID.
     */
    public int getTransactionID() {
        return transactionID;
    }

    /**
     * @return
     */
    protected int createNewTransactionID() {
        transactionID = GraphDocument.createTransactionId();
        return transactionID;
    }

    /**
     * Fügt dem Dialog einen neuen Tab mit dem übergebenen Titel und der Komponente hinzu.
     * 
     * @param title
     * @param component
     */
    public void addTab(final String title, final Component component) {
        tab.addTab(title, component);
    }

    /**
     * Fügt dem Dialog einen neuen Tab mit dem übergebenen Titel, Icon und der Komponente hinzu.
     * 
     * @param title
     * @param icon
     * @param component
     */
    public void addTab(final String title, final Icon icon, final Component component) {
        tab.addTab(title, icon, component);
    }

    /**
     * Bringt den Tab mit dem angegebenen Titel in den Vordergrund, wenn zusätzlich noch die
     * übergebene Klasse mit der Klasse der Componente in dem Tab zuweisungskompatibel ist. Die
     * Klasse der im Tabpanel enthaltenen Componente muss die gleiche oder eine Unterklasse der
     * übergebenen Klasse sein.
     * 
     * @param title Titel des zu selektierenden Tabs
     * @param tabComponentClass Oberklasse der Komponente in dem zu selektierenden Tab
     * @return <code>true</code>, wenn ein Tab der angegebenen Art gefunden und in den Vordergund
     *         geracht werden konnte
     */
    public boolean selectTab(final String title, final Class<? extends Component> tabComponentClass) {
        for (int i = 0; i < tab.getComponentCount(); i++) {
            Component comp = tab.getComponent(i);
            if (!tab.getTitleAt(i).equals(title)) {
                continue;
            }
            if (!tabComponentClass.isAssignableFrom(comp.getClass())) {
                continue;
            }
            tab.setSelectedIndex(i);
            return true;
        }
        return false;
    }

    /**
     * @return <code>doc</code>
     */
    public LGMGraphDocument getGraphDocument() {
        return doc;
    }

    /**
     * Performs a click on the Ok-Button
     */
    public void performOK() {
        okButton.doClick();
    }

    // /////////////////////////////////////////////////////////////////////////////////////////////////////
    // WindowListener -> bei Aktivierung im Contextgenerator auf das GraphDocument des Dialoges
    // wechseln //
    // /////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void windowActivated(final WindowEvent e) {
        Tool3lgm.getContextGenerator().changeContext(doc);
    }

    @Override
    public void windowClosed(final WindowEvent e) {
    }

    @Override
    public void windowClosing(final WindowEvent e) {
    }

    @Override
    public void windowDeactivated(final WindowEvent e) {
    }

    @Override
    public void windowDeiconified(final WindowEvent e) {
    }

    @Override
    public void windowIconified(final WindowEvent e) {
    }

    @Override
    public void windowOpened(final WindowEvent e) {
    }
}
