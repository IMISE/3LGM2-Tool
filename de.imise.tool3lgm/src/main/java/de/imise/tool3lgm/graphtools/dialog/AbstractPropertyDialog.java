/*
 * Created on 30.10.2007
 */
package de.imise.tool3lgm.graphtools.dialog;

import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.JButton;
import javax.swing.JPanel;

import de.imise.tool3lgm.Help;
import de.imise.tool3lgm.Static;
import de.imise.tool3lgm.Tool3lgm;
import de.imise.tool3lgm.Tool3lgmConstants;
import de.imise.tool3lgm.graphtools.GDCollection;
import de.imise.tool3lgm.graphtools.GraphDocument;
import de.imise.tool3lgm.graphtools.LGMGraphDocument;
import de.imise.tool3lgm.graphtools.userfield.UserFieldDefinitions;
import de.imise.util.swing.dialog.AbstractSizeAndPositionRestoringDialog;

/**
 * Allgemeiner Eigenschaftendialog.<br>
 * In dieser Form kann er als Eigenschaftendialog für ein Modell genutzt werden. Der
 * Eigenschaftendialog von Modellelementen ist eine spezielle Unterklasse dieser Dialogklasse.
 *
 * @author AXS
 * @created 30.10.2007
 */
public abstract class AbstractPropertyDialog extends AbstractSizeAndPositionRestoringDialog implements WindowListener {

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
     * Panel, das <code>applyButton</code>,<code>okButton</code> und <code>cancelButton</code> beinhaltet
     */
    protected JPanel controlPanel;

    /**
     * @param graphDocument Document in dessen Kontext der Dialog steht
     */
    public AbstractPropertyDialog(final GDCollection gdcoll) {
        this(Static.getMainFrame(), gdcoll);
    }

    /**
     * @param owner
     * @param gdcoll Modell, in dessen Kontext der Dialog steht
     */
    public AbstractPropertyDialog(final Frame owner, final GDCollection gdcoll) {
        super(owner, "", false);
        init(gdcoll);
    }

    /**
     * @param owner
     * @param graphDocument Document in dessen Kontext der Dialog steht
     */
    public AbstractPropertyDialog(final Dialog owner, final GDCollection gdcoll) {
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
        setSameButtonSize(okButton, cancelButton, applyButton);
        String helpID = Help.getHelp().getHelpID(this);
        if (helpID != null && helpID.length() > 0) {
            helpButton = new JButton(Tool3lgmConstants.getResString("hilfeButtonText"));
            helpButton.setMnemonic('H');
            Help.getHelp().enableHelpOnButton(helpButton, helpID);
        }
        createNewTransactionID();
    }

    private static final void setSameButtonSize(final JButton... buttons) {
        if (buttons == null || buttons.length == 0) {
            return;
        }
        Dimension dim = buttons[0].getPreferredSize();
        for (int i = 1; i < buttons.length; i++) {
            Dimension otherDim = buttons[i].getPreferredSize();
            dim.width = Math.max(dim.width, otherDim.width);
            dim.height = Math.max(dim.height, otherDim.height);
        }
        for (int i = 0; i < buttons.length; i++) {
            buttons[i].setPreferredSize(dim);
        }
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
     * @return <code>doc</code>
     */
    public LGMGraphDocument getGraphDocument() {
        return doc;
    }

    /**
     * @return
     */
    public UserFieldDefinitions getUserFieldDefinitions() {
        return gdcoll.getUserFieldDefinitions();
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
    public void windowOpened(final WindowEvent e) {
    }

    @Override
    public void windowClosing(final WindowEvent e) {
    }

    @Override
    public void windowClosed(final WindowEvent e) {
    }

    @Override
    public void windowIconified(final WindowEvent e) {
    }

    @Override
    public void windowDeiconified(final WindowEvent e) {
    }

    @Override
    public void windowDeactivated(final WindowEvent e) {
    }

}
