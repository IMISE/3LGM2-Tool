/*
 * Created on 22.02.2005
 */
package de.imise.tool3lgm.graphtools.view.browser;

import static de.imise.tool3lgm.userproperties.UserProperties.BooleanProperty.OPTION_SHOW_MODELS_IN_SEPARATE_BROWSER;

import java.awt.Color;
import java.awt.Component;
import java.awt.GridLayout;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JPanel;

import de.imise.tool3lgm.graphtools.model.GDCollection;
import de.imise.tool3lgm.graphtools.model.GraphDocument;
import de.imise.tool3lgm.graphtools.model.LGMGraphDocument;
import de.imise.tool3lgm.graphtools.model.Szenario;
import de.imise.tool3lgm.userproperties.UserProperties;

/**
 * @author Rudi, AXS
 */
public final class ModelBrowserPanel extends JPanel implements PropertyChangeListener {

    /** show ModelBrowsers sidy by side or all models in one browser */
    protected boolean showModelsInSeparateBrowser = false;

    /** xmlText color for tab with model of active frame */
    protected final static Color activeColor = Color.BLUE;

    /** xmlText color for tabs with models of non-active frames */
    protected static Color inactiveColor;

    /**
     *
     */
    public ModelBrowserPanel() {
        super();
        setLayout(new GridLayout(1, 1, 0, 0));
        showModelsInSeparateBrowser = OPTION_SHOW_MODELS_IN_SEPARATE_BROWSER.is();
        inactiveColor = new ModelBrowser().getForeground();
        UserProperties.addPropertyChangeListener(this);
    }

    /**
     * @param gdcoll
     * @return
     */
    public final void addCollection(final GDCollection gdcoll) {
        ModelBrowser modelBrowser;
        if (showModelsInSeparateBrowser) {
            modelBrowser = new ModelBrowser();
            modelBrowser.addCollection(gdcoll);
            ((GridLayout) getLayout()).setColumns(((GridLayout) getLayout()).getColumns() + 1);
            add(modelBrowser);
        } else {
            modelBrowser = getFirstBrowser();
            if (modelBrowser == null) {
                modelBrowser = new ModelBrowser();
                add(modelBrowser);
            }
            modelBrowser.addCollection(gdcoll);
        }
        addGraphDocuments(modelBrowser, gdcoll);
    }

    /**
     * Liefert den ersten ModelBrowser.
     *
     * @return
     */
    private ModelBrowser getFirstBrowser() {
        if (getComponentCount() == 0) {
            return null;
        }
        return (ModelBrowser) getComponent(0);
    }

    /**
     * @param gdcoll
     * @return
     */
    private void addGraphDocuments(final ModelBrowser modelBrowser, final GDCollection gdcoll) {
        LGMGraphDocument mainDoc = gdcoll.getMainDoc();
        modelBrowser.addGraphDocument(mainDoc);
        for (Szenario szen : gdcoll.getSzenarios()) {
            modelBrowser.addGraphDocument(szen);
        }
    }

    /**
     * @param doc
     */
    public void addGraphDocumentAndSetSelected(final GraphDocument doc) {
        GDCollection gdcoll = doc.getCollection();
        ModelBrowser modelBrowser = getModelBrowser(gdcoll);
        modelBrowser.addGraphDocument(doc);
        modelBrowser.setCurrentDoc(doc);
    }

    /**
     * Entfernt ein Teil-Modell aus dem dazugehörigen Browser. Wenn das letzte Teilmodell eines Modells entfernt wird, dann wird das ganze Modell
     * entfernt.
     *
     * @param doc
     */
    public void removeGraphDocument(final GraphDocument doc) {
        GDCollection gdcoll = doc.getCollection();
        ModelBrowser modelBrowser = getModelBrowser(gdcoll);
        modelBrowser.removeGraphDocument(doc);
        if (showModelsInSeparateBrowser) {
            if (modelBrowser.getTabCount() == 0) {
                remove(modelBrowser);
                GridLayout layout = (GridLayout) getLayout();
                int columns = layout.getColumns();
                columns = Math.max(columns - 1, 1);
                layout.setColumns(columns);
            }
        }
    }

    /**
     * @param gdcoll
     * @return
     */
    private ModelBrowser getModelBrowser(final GDCollection gdcoll) {
        if (showModelsInSeparateBrowser) {
            for (int i = 0; i < getComponentCount(); i++) {
                Component comp = getComponent(i);
                if (comp instanceof ModelBrowser) {
                    ModelBrowser browser = (ModelBrowser) comp;
                    if (browser.hasCollection(gdcoll)) {
                        return browser;
                    }
                }
            }
            return null;
        }
        return getFirstBrowser();
    }

    /**
     * Aktiviert die Baumansicht für das übergebene <code>GraphDocument</code>
     */
    public void setCurrentDoc(final GraphDocument doc) {
        GDCollection gdcoll = doc.getCollection();
        ModelBrowser modelBrowser = getModelBrowser(gdcoll);
        if (modelBrowser != null) {
            modelBrowser.setCurrentDoc(doc);
        }
    }

    /**
     * @param value
     */
    private void updateShowModelsInSeparateBrowser() {
        //wenn nichts zu tun ist -> raus
        boolean showModelsInSeparateBrowser = OPTION_SHOW_MODELS_IN_SEPARATE_BROWSER.is();
        if (this.showModelsInSeparateBrowser == showModelsInSeparateBrowser) {
            return;
        }
        //erst jetzt den neuen Wert setzen, weil getSelectedDoc() nur das Richtige tut, wenn
        //noch der alte Wert gesetzt ist
        this.showModelsInSeparateBrowser = showModelsInSeparateBrowser;
        ModelBrowser firstBrowser = getFirstBrowser();
        if (showModelsInSeparateBrowser) {
            setLayout(new GridLayout(1, Math.max(firstBrowser.getTabCount(), 1), 0, 0));
            while (firstBrowser.getTabCount() > 1) {
                ModelBrowser modelBrowser = new ModelBrowser();
                Component tab = firstBrowser.getComponentAt(1);
                String title = firstBrowser.getTitleAt(1);
                firstBrowser.remove(1);
                modelBrowser.addTab(title, tab);
                add(modelBrowser);
            }
        } else {
            while (getComponentCount() > 1) {
                ModelBrowser secondBrowser = (ModelBrowser) getComponent(1);
                remove(secondBrowser);
                firstBrowser.addTab(secondBrowser.getTitleAt(0), secondBrowser.getComponentAt(0));
            }
            setLayout(new GridLayout(1, 1, 0, 0));
        }
        revalidate();
    }

    @Override
    public void propertyChange(final PropertyChangeEvent evt) {
        if (OPTION_SHOW_MODELS_IN_SEPARATE_BROWSER.isChanged(evt)) {
            updateShowModelsInSeparateBrowser();
        }
    }

    /**
     * Ruft update für alle SubModelBrowser auf, die alle Bäume komplett neu aufbauen
     */
    public void updateModelBrowsers() {
        for (int i = 0; i < getComponentCount(); i++) {
            Component comp = getComponent(i);
            if (comp instanceof ModelBrowser) {
                ModelBrowser browser = (ModelBrowser) comp;
                browser.updateAllSubModelBrowsers();
            }
        }
    }

}
