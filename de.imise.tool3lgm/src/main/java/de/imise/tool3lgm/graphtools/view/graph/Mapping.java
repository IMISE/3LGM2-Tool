package de.imise.tool3lgm.graphtools.view.graph;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.util.HashMap;
import java.util.Set;

import de.imise.tool3lgm.graphtools.metamodel.Knickpunkt;
import de.imise.tool3lgm.graphtools.metamodel.ModelElement;
import de.imise.tool3lgm.graphtools.metamodel.TextfeldFach;
import de.imise.tool3lgm.graphtools.metamodel.TextfeldLog;
import de.imise.tool3lgm.graphtools.metamodel.TextfeldPhy;
import de.imise.tool3lgm.graphtools.view.container.ElementContainer;
import de.imise.tool3lgm.metamodel.tlgm_v3_0.node.Anwendungsbaustein;
import de.imise.tool3lgm.metamodel.tlgm_v3_0.node.Aufgabe;
import de.imise.tool3lgm.metamodel.tlgm_v3_0.node.Bausteinschnittstelle;
import de.imise.tool3lgm.metamodel.tlgm_v3_0.node.Benutzungsschnittstelle;
import de.imise.tool3lgm.metamodel.tlgm_v3_0.node.Datenbanksystem;
import de.imise.tool3lgm.metamodel.tlgm_v3_0.node.Dokumentensammlung;
import de.imise.tool3lgm.metamodel.tlgm_v3_0.node.KonAnwendungsbaustein;
import de.imise.tool3lgm.metamodel.tlgm_v3_0.node.Objekttyp;
import de.imise.tool3lgm.metamodel.tlgm_v3_0.node.Organisationsplan;
import de.imise.tool3lgm.metamodel.tlgm_v3_0.node.PhysischerDVBaustein;
import de.imise.tool3lgm.metamodel.tlgm_v3_0.node.RechAnwendungsbaustein;

public class Mapping {

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
    public Mapping() {
        super();
        loadDefaults();
    }

    /**
     * Setzt für diese Map alle Werte der übergebenen. Alle <code>GraphElementLayout</code>s der übergebenen
     * Map werden geclont.
     *
     * @param map
     */
    public final void adapt(final Mapping map) {
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

    //TODO: die Defaults müssten der Klasse irgendwie von außen übergeben werden

    public void loadDefaults() {
        elementClassToStandardLayoutMap.clear();

        setStandardBackGroundColor(Aufgabe.class, GraphElementLayout.COLORS[GraphElementLayout.RED]);
        setStandardForm(Aufgabe.class, GraphElementLayout.SHAPE.rechteck);

        setStandardBackGroundColor(Objekttyp.class, GraphElementLayout.COLORS[GraphElementLayout.BLUE]);
        setStandardForm(Objekttyp.class, GraphElementLayout.SHAPE.oval);

        setStandardBackGroundColor(Anwendungsbaustein.class, GraphElementLayout.COLORS[GraphElementLayout.GRAY]);
        setStandardForm(Anwendungsbaustein.class, GraphElementLayout.SHAPE.rundeck);

        setStandardBackGroundColor(RechAnwendungsbaustein.class, GraphElementLayout.COLORS[GraphElementLayout.LIGHTRED]);
        setStandardForm(RechAnwendungsbaustein.class, GraphElementLayout.SHAPE.rundeck);

        setStandardBackGroundColor(KonAnwendungsbaustein.class, GraphElementLayout.COLORS[GraphElementLayout.BLUE]);
        setStandardForm(KonAnwendungsbaustein.class, GraphElementLayout.SHAPE.rundeck);

        setStandardBackGroundColor(Datenbanksystem.class, GraphElementLayout.COLORS[GraphElementLayout.YELLOW]);
        setStandardForm(Datenbanksystem.class, GraphElementLayout.SHAPE.tonne);
        setStandardSize(Datenbanksystem.class, 20, 20);

        setStandardBackGroundColor(Dokumentensammlung.class, GraphElementLayout.COLORS[GraphElementLayout.WHITE]);
        setStandardForm(Dokumentensammlung.class, GraphElementLayout.SHAPE.ordner);
        setStandardSize(Dokumentensammlung.class, 20, 20);

        setStandardBackGroundColor(Organisationsplan.class, GraphElementLayout.COLORS[GraphElementLayout.ORANGE]);
        setStandardForm(Organisationsplan.class, GraphElementLayout.SHAPE.wabe);

        setStandardBackGroundColor(TextfeldFach.class, new Color(0, 0, 0, 0));
        setStandardForm(TextfeldFach.class, GraphElementLayout.SHAPE.rechteck);
        setStandardBackGroundColor(TextfeldLog.class, new Color(0, 0, 0, 0));
        setStandardForm(TextfeldLog.class, GraphElementLayout.SHAPE.rechteck);
        setStandardBackGroundColor(TextfeldPhy.class, new Color(0, 0, 0, 0));
        setStandardForm(TextfeldPhy.class, GraphElementLayout.SHAPE.rechteck);

        setStandardBackGroundColor(Bausteinschnittstelle.class, GraphElementLayout.COLORS[GraphElementLayout.LIGHTGREEN]);
        setStandardForm(Bausteinschnittstelle.class, GraphElementLayout.SHAPE.oval);
        setStandardSize(Bausteinschnittstelle.class, 15, 15);

        setStandardBackGroundColor(Benutzungsschnittstelle.class, GraphElementLayout.COLORS[GraphElementLayout.ORANGE]);
        setStandardForm(Benutzungsschnittstelle.class, GraphElementLayout.SHAPE.oval);
        setStandardSize(Benutzungsschnittstelle.class, 15, 15);

        setStandardBackGroundColor(PhysischerDVBaustein.class, GraphElementLayout.COLORS[GraphElementLayout.ORANGE]);
        setStandardForm(PhysischerDVBaustein.class, GraphElementLayout.SHAPE.rechteck);

        //		layout_new[ModelConstants.KANTE].bg_color = Color.black;
        //		layout_new[ModelConstants.DOPPELKANTE].bg_color = Color.black;
        //		layout_new[ModelConstants.AUF_OBJ_VERBINDUNG].bg_color = Color.black;

        setStandardSize(Knickpunkt.class, 10, 10);

        //		Alte Darstellung (entfernen, wenn das oben läuft)
        //		for (int c=0;c<ModelConstants.HIGHEST_CLASS_TYPE;c++) {
        //			layout_new[c].reset();
        //			layout_new[c].font = STANDARD_FONT;
        //		}
        //		layout_new[ModelConstants.AUFGABE].bg_color = COLORS[RED];
        //		layout_new[ModelConstants.AUFGABE].form = RECHTECK;
        //
        //		layout_new[ModelConstants.OBJEKTTYP].bg_color = COLORS[BLUE];
        //		layout_new[ModelConstants.OBJEKTTYP].form = OVAL;
        //
        //		layout_new[ModelConstants.ANWENDUNGSBAUSTEIN].bg_color = COLORS[GRAY];
        //		layout_new[ModelConstants.ANWENDUNGSBAUSTEIN].form = RUNDECK;
        //
        //		layout_new[ModelConstants.RECHANWENDUNGSBAUSTEIN].bg_color = COLORS[LIGHTRED];
        //		layout_new[ModelConstants.RECHANWENDUNGSBAUSTEIN].form = RUNDECK;
        //
        //		layout_new[ModelConstants.KONANWENDUNGSBAUSTEIN].bg_color = COLORS[BLUE];
        //		layout_new[ModelConstants.KONANWENDUNGSBAUSTEIN].form = RUNDECK;
        //
        //		layout_new[ModelConstants.DATENBANKSYSTEM].bg_color = COLORS[YELLOW];
        //		layout_new[ModelConstants.DATENBANKSYSTEM].form = TONNE;
        //		layout_new[ModelConstants.DATENBANKSYSTEM].width = 20;
        //		layout_new[ModelConstants.DATENBANKSYSTEM].height = 20;
        //
        //		layout_new[ModelConstants.DOKUMENTENSAMMLUNG].bg_color = COLORS[WHITE];
        //		layout_new[ModelConstants.DOKUMENTENSAMMLUNG].form = ORDNER;
        //		layout_new[ModelConstants.DOKUMENTENSAMMLUNG].width = 20;
        //		layout_new[ModelConstants.DOKUMENTENSAMMLUNG].height = 20;
        //
        //		layout_new[ModelConstants.ORGANISATIONSPLAN].bg_color = COLORS[GREEN];
        //		layout_new[ModelConstants.ORGANISATIONSPLAN].form = RHOMBUS;
        //
        //		layout_new[ModelConstants.ANWENDUNGSPROGRAMM].bg_color = COLORS[ORANGE];
        //		layout_new[ModelConstants.ANWENDUNGSPROGRAMM].form = WABE;
        //
        //		layout_new[ModelConstants.SOFTWAREPRODUKT].bg_color = COLORS[WHITE];
        //		layout_new[ModelConstants.SOFTWAREPRODUKT].form = RHOMBUS;
        //
        //		layout_new[ModelConstants.TEXTFELDFACH].bg_color = new Color(0,0,0,0);
        //		layout_new[ModelConstants.TEXTFELDFACH].form = RECHTECK;
        //
        //		layout_new[ModelConstants.TEXTFELDLOG].bg_color = new Color(0,0,0,0);
        //		layout_new[ModelConstants.TEXTFELDLOG].form = RECHTECK;
        //
        //		layout_new[ModelConstants.TEXTFELDPHY].bg_color = new Color(0,0,0,0);
        //		layout_new[ModelConstants.TEXTFELDPHY].form = RECHTECK;
        //
        //		layout_new[ModelConstants.BAUSTEINSCHNITTSTELLE].bg_color = COLORS[LIGHTGREEN];
        //		layout_new[ModelConstants.BAUSTEINSCHNITTSTELLE].form = OVAL;
        //		layout_new[ModelConstants.BAUSTEINSCHNITTSTELLE].width = 15;
        //		layout_new[ModelConstants.BAUSTEINSCHNITTSTELLE].height = 15;
        //
        //		layout_new[ModelConstants.BENUTZUNGSSCHNITTSTELLE].bg_color = COLORS[ORANGE];
        //		layout_new[ModelConstants.BENUTZUNGSSCHNITTSTELLE].form = OVAL;
        //		layout_new[ModelConstants.BENUTZUNGSSCHNITTSTELLE].width = 15;
        //		layout_new[ModelConstants.BENUTZUNGSSCHNITTSTELLE].height = 15;
        //
        //		layout_new[ModelConstants.NACHRICHTENTYP].bg_color = COLORS[YELLOW];
        //		layout_new[ModelConstants.NACHRICHTENTYP].form = RHOMBUS;
        //
        //		layout_new[ModelConstants.KOMMUNIKATIONSSTANDARD].bg_color = COLORS[GREEN];
        //		layout_new[ModelConstants.KOMMUNIKATIONSSTANDARD].form = DREIECK;
        //
        //		layout_new[ModelConstants.DBVERWALTUNGSSYSTEM].bg_color = COLORS[GRAY];
        //		layout_new[ModelConstants.DBVERWALTUNGSSYSTEM].form = TONNE;
        //
        //		layout_new[ModelConstants.PHYSISCHER_DV_BAUSTEIN].bg_color = COLORS[ORANGE];
        //		layout_new[ModelConstants.PHYSISCHER_DV_BAUSTEIN].form = RECHTECK;
        //
        //		layout_new[ModelConstants.NETZTYP].bg_color = COLORS[BLUE];
        //		layout_new[ModelConstants.NETZTYP].form = RHOMBUS;
        //
        //		layout_new[ModelConstants.SUBNETZ].bg_color = COLORS[YELLOW];
        //		layout_new[ModelConstants.SUBNETZ].form = RUNDECK;
        //
        //		layout_new[ModelConstants.NETZPROTOKOLL].bg_color = COLORS[WHITE];
        //		layout_new[ModelConstants.NETZPROTOKOLL].form = OVAL;
        //
        //		layout_new[ModelConstants.STANDORT].bg_color = COLORS[GRAY];
        //		layout_new[ModelConstants.STANDORT].form = RECHTECK;
        //
        //		layout_new[ModelConstants.BAUSTEINTYP].bg_color = COLORS[RED];
        //		layout_new[ModelConstants.BAUSTEINTYP].form = OVAL;
        //
        //		layout_new[ModelConstants.KANTE].bg_color = Color.black;
        //		layout_new[ModelConstants.DOPPELKANTE].bg_color = Color.black;
        //		layout_new[ModelConstants.AUF_OBJ_VERBINDUNG].bg_color = Color.black;
        //
        //		layout_new[ModelConstants.KNICKPUNKTKNOTEN].width = 10;
        //		layout_new[ModelConstants.KNICKPUNKTKNOTEN].height = 10;

    }

}