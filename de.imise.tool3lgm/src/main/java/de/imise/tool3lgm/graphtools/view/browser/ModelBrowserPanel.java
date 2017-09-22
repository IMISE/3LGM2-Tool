/*
 * Created on 22.02.2005
 */
package de.imise.tool3lgm.graphtools.view.browser;

import java.awt.Color;
import java.awt.Component;
import java.awt.GridLayout;
import java.awt.Image;

import javax.swing.ImageIcon;
import javax.swing.JPanel;

import de.imise.tool3lgm.Static;
import de.imise.tool3lgm.Tool3lgmConstants;
import de.imise.tool3lgm.graphtools.model.GDCollection;
import de.imise.tool3lgm.graphtools.model.GraphDocument;
import de.imise.tool3lgm.userproperties.UserProperties;

/**
 * @author Rudi, AXS
 */
public final class ModelBrowserPanel extends JPanel {

    /** show ModelBrowsers sidy by side or all models in one browser */
    protected boolean showModelsInSeparateBrowser = false;

    /** icon for tap with model of active frame */
    protected final static ImageIcon activeIcon = new ImageIcon(Tool3lgmConstants.getIcon("toolIcon.gif").getImage().getScaledInstance(13, 13, Image.SCALE_FAST));

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
        showModelsInSeparateBrowser = UserProperties.isShowModelsInSeparateBrowser();
        inactiveColor = new ModelBrowser().getForeground();
    }

    /**
     * @param gdcoll
     * @return
     */
    public final void addCollection(final GDCollection gdcoll) {
        if (showModelsInSeparateBrowser) {
            ModelBrowser modelBrowser = new ModelBrowser();
            modelBrowser.addCollection(gdcoll);
            ((GridLayout) getLayout()).setColumns(((GridLayout) getLayout()).getColumns() + 1);
            add(modelBrowser);
        } else {
            ModelBrowser firstBrowser = getFirstBrowser();
            if (firstBrowser == null) {
                firstBrowser = new ModelBrowser();
                add(firstBrowser);
            }
            firstBrowser.addCollection(gdcoll);
        }
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
     * @param doc
     * @return
     */
    public void addGraphDocument(final GraphDocument doc) {
        getModelBrowser(doc.getCollection()).addGraphDocument(doc);
    }

    /**
     * Entfernt ein Teil-Modell aus dem dazugehörigen Browser. Wenn das letzte Teilmodell eines Modells entfernt wird, dann wird das ganze Modell
     * entfernt.
     *
     * @param doc
     */
    public void removeGraphDocument(final GraphDocument doc) {
        if (showModelsInSeparateBrowser) {
            ModelBrowser modelBrowser = getModelBrowser(doc.getCollection());
            modelBrowser.removeGraphDocument(doc);
            if (modelBrowser.getTabCount() == 0) {
                remove(modelBrowser);
                GridLayout layout = (GridLayout) getLayout();
                layout.setColumns(Math.max(layout.getColumns() - 1, 1));
            }
        } else {
            getFirstBrowser().removeGraphDocument(doc);
        }
    }

    /**
     * @param doc
     */
    public void updateTitle(final GraphDocument doc) {
        getModelBrowser(doc.getCollection()).updateTitle(doc);
    }

    /**
     * @param gdcoll
     */
    public void updateTitle(final GDCollection gdcoll) {
        getModelBrowser(gdcoll).updateTitle(gdcoll);
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
    public void setSelectedDoc(final GraphDocument doc) {
        ModelBrowser modelBrowser = getModelBrowser(doc.getCollection());
        if (modelBrowser != null) {
            modelBrowser.setSelectedDoc(doc);
        }
    }

    /**
     * @return
     */
    public static final GraphDocument getSelectedDoc() {
        return ModelBrowser.lastActiveBrowser != null ? ModelBrowser.lastActiveBrowser.getSelectedDoc() : null;
    }

    /**
     * @param value
     */
    public void updateShowModelsInSeparateBrowser() {

        //wenn nichts zu tun ist -> raus
        if (showModelsInSeparateBrowser == UserProperties.isShowModelsInSeparateBrowser()) {
            return;
        }
        //Zuerst das aktuell selektierte GraphDocument holen. Das muss noch mit dem alten globalen Wert
        //von showModelsInSeparateBrowser passieren, sonst kommt hier null zurück
        GraphDocument activeDoc = getSelectedDoc();

        //erst jetzt den neuen Wert setzen, weil getSelectedDoc() nur das Richtige tut, wenn
        //noch der alte Wert gesetzt ist
        showModelsInSeparateBrowser = UserProperties.isShowModelsInSeparateBrowser();

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

        Static.setSelectedDoc(activeDoc, true);

        revalidate();
    }
}
