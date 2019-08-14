package de.imise.tool3lgm.graphtools.view.browser;

import static de.imise.tool3lgm.userproperties.UserProperties.BooleanProperty.OPTION_SHOW_MODELS_IN_SEPARATE_BROWSER;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import javax.swing.ImageIcon;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import de.imise.tool3lgm.Static;
import de.imise.tool3lgm.Tool3lgmConstants;
import de.imise.tool3lgm.graphtools.model.GDCollection;
import de.imise.tool3lgm.graphtools.model.GraphDocument;
import de.imise.util.swing.component.TabbedPane;

/** Erzeugt ModelBrowser für 3lgm */
public final class ModelBrowser extends TabbedPane implements ChangeListener, FocusListener {

    /** icon for tap with model of active frame */
    private final static ImageIcon activeIcon = new ImageIcon(Tool3lgmConstants.getIcon("toolIcon.gif").getImage().getScaledInstance(13, 13, Image.SCALE_FAST));

    /** xmlText color for tab with model of active frame */
    private final static Color activeColor = Color.BLUE;

    /** xmlText color for tabs with models of non-active frames */
    private static Color inactiveColor;

    /** Der zuletzt aktive Browser */
    protected static ModelBrowser lastActiveBrowser = null;

    /**
     * Ein neuer Browser
     */
    protected ModelBrowser() {
        super(TabbedPane.TOP, TabbedPane.SCROLL_TAB_LAYOUT);
        setMinimumSize(new Dimension(10, 10));
        addChangeListener(this);
        addFocusListener(this);
        inactiveColor = getForeground();
    }

    /**
     * Tab für eine {@link GDCollection} wird hinzugefügt
     *
     * @param gdcoll
     * @return
     */
    protected void addCollection(final GDCollection gdcoll) {
        SubModelsBrowser subModelsBrowser = new SubModelsBrowser(gdcoll);
        addTab(subModelsBrowser.getTitle(), subModelsBrowser);
        setSelectedComponent(subModelsBrowser);
    }

    /**
     * @param name
     * @param doc
     * @return
     */
    protected void addGraphDocument(final GraphDocument doc) {
        SubModelsBrowser subModelsBrowser = getCollectionPane(doc.getCollection());
        if (subModelsBrowser != null) {
            subModelsBrowser.addGraphDocument(doc);
        }
    }

    /**
     * @param doc
     */
    protected void removeGraphDocument(final GraphDocument doc) {
        SubModelsBrowser subModelsBrowser = getCollectionPane(doc.getCollection());
        if (subModelsBrowser != null) {
            subModelsBrowser.removeGraphDocument(doc);
        }
        if (subModelsBrowser.getDocCount() == 0) {
            remove(subModelsBrowser);
        }
    }

    /**
     * @return <code>GraphDocument</code> of the selected <code>ModelBrowser</code> -tree
     */
    private GraphDocument getCurrentDoc() {
        SubModelsBrowser subModelsBrowser = (SubModelsBrowser) getSelectedComponent();
        if (subModelsBrowser == null) {
            return null;
        }
        return subModelsBrowser.getCurrentDoc();
    }

    /**
     * @param doc
     * @return tab-index of document/model-pane or -1
     */
    protected final void setCurrentDoc(final GraphDocument doc) {
        if (doc != null) {
            SubModelsBrowser subModelsBrowser = getCollectionPane(doc.getCollection());
            setSelectedComponent(subModelsBrowser);
            if (subModelsBrowser != null) {
                subModelsBrowser.setCurrentDoc(doc);
            }
            updateActiveBrowserTab();
        }
    }

    /**
     * @param coll
     * @return
     */
    private SubModelsBrowser getCollectionPane(final GDCollection coll) {
        for (int i = 0; i < getTabCount(); i++) {
            SubModelsBrowser subModelsBrowser = (SubModelsBrowser) getComponentAt(i);
            if (subModelsBrowser.getCollection() == coll) {
                return subModelsBrowser;
            }
        }
        return null;
    }

    /**
     * @param index
     * @return
     */
    protected GDCollection getCollectionAt(final int index) {
        return getTabCount() > index ? ((SubModelsBrowser) getComponentAt(index)).getCollection() : null;
    }

    /**
     * @return
     */
    protected int getCollectionIndex(final GDCollection gdcoll) {
        for (int i = 0; i < getTabCount(); i++) {
            if (getCollectionAt(i) == gdcoll) {
                return i;
            }
        }
        return -1;
    }

    /**
     * @return
     */
    protected boolean hasCollection(final GDCollection gdcoll) {
        return getCollectionIndex(gdcoll) > -1;
    }

    /**
     * Hebt den aktiven Tab hervor und löscht die Hervorhebung beim letzten aktiven Tab.
     */
    private void updateActiveBrowserTab() {
        boolean isShowModelsInSeparateBrowser = OPTION_SHOW_MODELS_IN_SEPARATE_BROWSER.is();
        if (isShowModelsInSeparateBrowser && lastActiveBrowser == this) {
            return;
        }
        int index = -1;
        if (lastActiveBrowser != null) {
            if (isShowModelsInSeparateBrowser) {
                index = lastActiveBrowser.getSelectedIndex();
            } else {
                for (int i = 0; i < lastActiveBrowser.getTabCount(); i++) {
                    if (lastActiveBrowser.getForegroundAt(i) == activeColor) {
                        index = i;
                        break;
                    }
                }
            }
        }
        if (index >= 0) {
            lastActiveBrowser.setIconAt(index, null);
            lastActiveBrowser.setForegroundAt(index, inactiveColor);
        }
        index = getSelectedIndex();
        if (index >= 0) {
            setIconAt(index, activeIcon);
            setForegroundAt(index, activeColor);
        }

        lastActiveBrowser = this;
    }

    public void updateAllSubModelBrowsers() {
        for (int i = 0; i < getTabCount(); i++) {
            SubModelsBrowser subModelsBrowser = (SubModelsBrowser) getComponentAt(i);
            setTitleAt(i, subModelsBrowser.getTitle());
            subModelsBrowser.update();
        }
    }

    @Override
    public void stateChanged(final ChangeEvent e) {
        updateActiveBrowserTab();
        GraphDocument doc = getCurrentDoc();
        Static.setSelectedDoc(doc);
    }

    @Override
    public void focusGained(final FocusEvent e) {
        fireStateChanged();
    }

    @Override
    public void focusLost(final FocusEvent e) {
    }

}
