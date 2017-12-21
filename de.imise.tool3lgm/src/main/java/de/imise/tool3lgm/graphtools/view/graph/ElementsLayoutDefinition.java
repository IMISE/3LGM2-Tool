package de.imise.tool3lgm.graphtools.view.graph;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.util.HashMap;
import java.util.Set;

import de.imise.tool3lgm.graphtools.metamodel.ModelConstants;
import de.imise.tool3lgm.graphtools.metamodel.ModelElement;
import de.imise.tool3lgm.graphtools.view.container.ElementContainer;

public class ElementsLayoutDefinition {

    /**
     * Mappt von der Elementklasse auf das zugehörige Standard-<code>GraphElementLayout</code>
     */
    private HashMap<Class<? extends ModelElement>, GraphElementLayout> elementClassToStandardLayoutMap = new HashMap<>();

    /**
     * Standardelementlayout. Initial entspricht es dem Standardlayout aus <code>GraphElementLayout</code>
     */
    private GraphElementLayout standardElementLayout = (GraphElementLayout) GraphElementLayout.STANDARD_ELEMENT_LAYOUT.clone();

    /**
     *
     */
    public ElementsLayoutDefinition(final boolean loadDefaults) {
        super();
        if (loadDefaults) {
            loadDefaults();
        }
    }

    /**
     * Setzt für diese Map alle Werte der übergebenen. Alle <code>GraphElementLayout</code>s der übergebenen
     * Map werden geclont.
     *
     * @param map
     */
    public final void adapt(final ElementsLayoutDefinition map) {
        standardElementLayout = (GraphElementLayout) map.standardElementLayout.clone();
        Set<Class<? extends ModelElement>> keySet = map.elementClassToStandardLayoutMap.keySet();
        elementClassToStandardLayoutMap = new HashMap<>(keySet.size());
        for (Class<? extends ModelElement> c : keySet) {
            elementClassToStandardLayoutMap.put(c, (GraphElementLayout) map.elementClassToStandardLayoutMap.get(c).clone());
        }
    }

    /**
     * Liefert einen Iterable über alle Elementklassen, für die ein Standardlayout festgelegt wurde
     *
     * @return
     */
    public Iterable<Class<? extends ModelElement>> getElementClassesWithStandardLayout() {
        return elementClassToStandardLayoutMap.keySet();
    }

    /**
     * Gibt das allgemeine Standardlayout für alle Elementklassen zurück, die kein eigenes Layout besitzen.
     *
     * @return Returns the standardElementLayout.
     */
    public GraphElementLayout getStandardElementLayout() {
        return standardElementLayout;
    }

    /**
     * Gibt das Standardlayout für ModellElemente der übergebenen Art zurcük.<br>
     * Existiert in der HashMap mit den Layouts für alle Elemente kein eigener
     * Eintrag für diese Elementart, wird das StandardLayout zurück gegeben.<br>
     * Will man das Layout für eine spezielle Elementart setzen, muss man das
     * Layout übder die Funktion <code>getElementClassSpecificLayout(Class)</code> holen.
     *
     * @param ec Container, für dessen Element das StandardLayout ermittelt werden soll
     * @see #getElementClassSpecificLayout(Class)
     * @return StandardLayout für Elemente der übergebenen Art
     */
    private GraphElementLayout getStandardElementLayout(final ElementContainer ec) {
        return getStandardElementLayout(ec.getElement().getClass());
    }

    /**
     * @param elementClass Elementklasse, für das das StandardLayout ermitelt werden soll
     * @return StandardLayout für Elemente der übergebenen Art
     */
    public GraphElementLayout getStandardElementLayout(final Class<? extends ModelElement> elementClass) {
        GraphElementLayout layout = elementClassToStandardLayoutMap.get(elementClass);
        if (layout == null) {
            layout = standardElementLayout;
        }
        return layout;
    }

    /**
     * Gibt für die übergebene Elementklasse das eigene Standardlayout zurück. Sollte in der HashMap mit allen
     * Layouts bisher kein eigenes für diese Elementart vorhanden sein, wird das Standardlayout geclont, in die
     * HashMap eingetragen und zurückgegeben.<br>
     * Über diese Funktion sollte das Layout einer Elementart immer ermittelt werden, wenn man einen Wert setzen
     * möchte. Bei reinen Abfragen kann man das immer über <code>getStandardGraphElementLayout(Class)</code> tun.
     *
     * @param elementClass
     * @see #getStandardGraphElementLayout(Class)
     * @return
     */
    private GraphElementLayout getElementClassSpecificLayout(final Class<? extends ModelElement> elementClass) {
        GraphElementLayout gel = getStandardElementLayout(elementClass);
        if (gel == standardElementLayout) {
            gel = (GraphElementLayout) gel.clone();
            elementClassToStandardLayoutMap.put(elementClass, gel);
        }
        return gel;
    }

    /**
     * @param ec
     * @return
     */
    public final Color getStandardBackGroundColor(final ElementContainer ec) {
        GraphElementLayout gel = getStandardElementLayout(ec);
        if (gel.bg_color == null) {
            return standardElementLayout.bg_color;
        }
        return gel.bg_color;
    }

    /**
     * @param elementClass
     * @param color
     */
    public final void setStandardBackGroundColor(final Class<? extends ModelElement> elementClass, final Color color) {
        getElementClassSpecificLayout(elementClass).bg_color = color;
    }

    /**
     * @param elementClass
     * @return
     */
    public final Dimension getStandardSize(final Class<? extends ModelElement> elementClass) {
        return new Dimension(getStandardWidth(elementClass), getStandardHeight(elementClass));
    }

    /**
     * @param elementClass
     * @return
     */
    public final int getStandardWidth(final Class<? extends ModelElement> elementClass) {
        return getStandardElementLayout(elementClass).width;
    }

    /**
     * @param elementClass
     * @param width
     */
    public final void setStandardWidth(final Class<? extends ModelElement> elementClass, final int width) {
        getElementClassSpecificLayout(elementClass).width = width;
    }

    /**
     * @param elementClass
     * @return
     */
    public final int getStandardHeight(final Class<? extends ModelElement> elementClass) {
        return getStandardElementLayout(elementClass).height;
    }

    /**
     * @param elementClass
     * @param height
     */
    public final void setStandardHeight(final Class<? extends ModelElement> elementClass, final int height) {
        getElementClassSpecificLayout(elementClass).height = height;
    }

    /**
     * @param elementClass
     * @param width
     * @param height
     */
    public final void setStandardSize(final Class<? extends ModelElement> elementClass, final int width, final int height) {
        GraphElementLayout gel = getElementClassSpecificLayout(elementClass);
        gel.width = width;
        gel.height = height;
    }

    /**
     * @param ec
     * @return
     */
    public final GraphElementLayout.SHAPE getStandardForm(final ElementContainer ec) {
        return getStandardElementLayout(ec).form;
    }

    /**
     * @param elementClass
     * @param form
     */
    public final void setStandardForm(final Class<? extends ModelElement> elementClass, final GraphElementLayout.SHAPE form) {
        getElementClassSpecificLayout(elementClass).form = form;
    }

    /**
     * @param ec
     * @return
     */
    public final Font getStandardFont(final ElementContainer ec) {
        return getStandardElementLayout(ec).getFont();
    }

    /**
     * @param elementClass
     * @param font
     */
    public final void setStandardFont(final Class<? extends ModelElement> elementClass, final Font font) {
        getElementClassSpecificLayout(elementClass).setFont(font);
    }

    public void loadDefaults() {
        ElementsLayoutDefinition defaultElementsLayout = ModelConstants.getGraphViewDefinition().getDefaultElementsLayout();
        adapt(defaultElementsLayout);
    }

}