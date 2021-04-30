package de.imise.tool3lgm.graphtools.view.graph;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import de.imise.tool3lgm.graphtools.metamodel.elements.ModelElement;
import de.imise.tool3lgm.graphtools.view.container.ElementContainer;
import de.imise.util.Alphabetical;

public class DefaultElementsLayoutDefinition {

    /**
     * Mappt von der Elementklasse auf das zugehörige
     * Standard-<code>GraphElementLayout</code>
     */
    private Map<Class<? extends ModelElement>, GraphElementLayout> elementClassToStandardLayoutMap = new HashMap<>();

    /**
     * Standardelementlayout. Initial entspricht es dem Standardlayout aus
     * <code>GraphElementLayout</code>
     */
    private GraphElementLayout defaultElementLayout = createStandardElementLayout();

    /**
     * @return
     */
    private GraphElementLayout createStandardElementLayout() {
        GraphElementLayout layout = new GraphElementLayout();
        layout.fg_color = GraphElementLayout.STANDARD_FONT_COLOR;
        layout.bg_color = GraphElementLayout.STANDARD_NODE_COLOR;
        layout.border_color = GraphElementLayout.STANDARD_BORDER_COLOR;
        layout.setFont(GraphElementLayout.STANDARD_FONT);
        layout.setIconID(null);
        layout.form = GraphElementLayout.STANDARD_FORM;
        layout.textPositionHorizontal = GraphElementLayout.STANDARD_TEXT_POSITION_HORIZONTAL;
        layout.textPositionVertical = GraphElementLayout.STANDARD_TEXT_POSITION_VERTICAL;
        layout.textAlignmentHTML = GraphElementLayout.STANDARD_TEXT_ALIGNMENT_HTML;
        layout.width = GraphElementLayout.STANDARD_WIDTH;
        return layout;
    }

    /**
     * @param loadDefaults
     */
    public DefaultElementsLayoutDefinition(final DefaultElementsLayoutDefinition defaultElementsLayout) {
        if (defaultElementsLayout != null) {
            adapt(defaultElementsLayout);
        }
    }

    /**
     * Setzt für diese Map alle Werte der übergebenen. Alle
     * <code>GraphElementLayout</code>s der übergebenen Map werden geclont.
     *
     * @param layout2Clone
     */
    public final void adapt(final DefaultElementsLayoutDefinition layout2Clone) {
        defaultElementLayout = (GraphElementLayout) layout2Clone.defaultElementLayout.clone();
        Set<Class<? extends ModelElement>> keySet = layout2Clone.elementClassToStandardLayoutMap.keySet();
        elementClassToStandardLayoutMap = new HashMap<>(keySet.size());
        for (Class<? extends ModelElement> c : keySet) {
            elementClassToStandardLayoutMap.put(c, (GraphElementLayout) layout2Clone.elementClassToStandardLayoutMap.get(c).clone());
        }
    }

    /**
     * Liefert einen Iterable über alle Elementklassen, für die ein
     * Standardlayout festgelegt wurde
     *
     * @return
     */
    public Iterable<Class<? extends ModelElement>> getElementClassesWithStandardLayout() {
        return elementClassToStandardLayoutMap.keySet();
    }

    /**
     * Gibt das allgemeine Standardlayout für alle Elementklassen zurück, die
     * kein eigenes Layout besitzen.
     *
     * @return Returns the standardElementLayout.
     */
    public GraphElementLayout getStandardElementLayout() {
        return defaultElementLayout;
    }

    /**
     * Gibt das Standardlayout für ModellElemente der übergebenen Art
     * zurcük.<br>
     * Existiert in der Map mit den Layouts für alle Elemente kein eigener
     * Eintrag für diese Elementart, wird das StandardLayout zurück gegeben.<br>
     * Will man das Layout für eine spezielle Elementart setzen, muss man das
     * Layout übder die Funktion
     * <code>getElementClassSpecificLayout(Class)</code> holen.
     *
     * @param ec Container, für dessen Element das StandardLayout ermittelt
     *            werden soll
     * @see #getElementClassSpecificLayout(Class)
     * @return StandardLayout für Elemente der übergebenen Art
     */
    private GraphElementLayout getStandardElementLayout(final ElementContainer ec) {
        return getStandardElementLayout(ec.getElement().getClass());
    }

    /**
     * @param elementClass Elementklasse, für das das StandardLayout ermitelt
     *            werden soll
     * @return StandardLayout für Elemente der übergebenen Art
     */
    public GraphElementLayout getStandardElementLayout(final Class<? extends ModelElement> elementClass) {
        GraphElementLayout layout = elementClassToStandardLayoutMap.get(elementClass);
        if (layout == null) {
            layout = defaultElementLayout;
        }
        return layout;
    }

    /**
     * Gibt für die übergebene Elementklasse das eigene Standardlayout zurück.
     * Sollte in der Map mit allen Layouts bisher kein eigenes für diese
     * Elementart vorhanden sein, wird das Standardlayout geclont, in die Map
     * eingetragen und zurückgegeben.<br>
     * Über diese Funktion sollte das Layout einer Elementart immer ermittelt
     * werden, wenn man einen Wert setzen möchte. Bei reinen Abfragen kann man
     * das immer über <code>getStandardGraphElementLayout(Class)</code> tun.
     *
     * @param elementClass
     * @see #getStandardGraphElementLayout(Class)
     * @return
     */
    private GraphElementLayout getElementClassSpecificLayout(final Class<? extends ModelElement> elementClass) {
        GraphElementLayout gel = getStandardElementLayout(elementClass);
        if (gel == defaultElementLayout) {
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
            return defaultElementLayout.bg_color;
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
     * @return
     */
    public final int getStandardHeight(final Class<? extends ModelElement> elementClass) {
        return getStandardElementLayout(elementClass).height;
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
    public final Shape getStandardForm(final ElementContainer ec) {
        return getStandardElementLayout(ec).form;
    }

    /**
     * @param elementClass
     * @param form
     */
    public final void setStandardForm(final Class<? extends ModelElement> elementClass, final Shape form) {
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

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        List<Class<? extends ModelElement>> elementClasses = new ArrayList<>(elementClassToStandardLayoutMap.keySet());
        Alphabetical.sort(elementClasses);
        for (Class<? extends ModelElement> elementClass : elementClasses) {
            sb.append(elementClass.getSimpleName());
            sb.append(" ");
            sb.append(elementClassToStandardLayoutMap.get(elementClass));
            sb.append("\n");
        }
        return sb.toString();
    }

}