/*
 * Created on 20.07.2004
 */
package de.imise.tool3lgm.graphtools.path;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import de.imise.tool3lgm.Tool3lgmConstants;
import de.imise.tool3lgm.graphtools.GDCollection;
import de.imise.tool3lgm.graphtools.elements.Doppelkante;
import de.imise.tool3lgm.graphtools.elements.Kante;
import de.imise.tool3lgm.graphtools.elements.ModelElement;
import de.imise.tool3lgm.graphtools.elements.PartOfBeziehung;
import de.imise.tool3lgm.graphtools.elements.edge.AufAufOrgVerbindung;
import de.imise.tool3lgm.graphtools.elements.edge.AufAufVerbindung;
import de.imise.tool3lgm.graphtools.elements.edge.AufObjVerbindung;
import de.imise.tool3lgm.graphtools.elements.edge.AwbAwbkVerbindung;
import de.imise.tool3lgm.graphtools.elements.edge.AwbKawbVerbindung;
import de.imise.tool3lgm.graphtools.elements.edge.AwbKommssVerbindung;
import de.imise.tool3lgm.graphtools.elements.edge.AwbkAufOrgVerbindung;
import de.imise.tool3lgm.graphtools.elements.edge.AwpSwpVerbindung;
import de.imise.tool3lgm.graphtools.elements.edge.BssEtntVerbindung;
import de.imise.tool3lgm.graphtools.elements.edge.BssKommstVerbindung;
import de.imise.tool3lgm.graphtools.elements.edge.DatenuebertragungsVerbindung;
import de.imise.tool3lgm.graphtools.elements.edge.DbsDatVerbindung;
import de.imise.tool3lgm.graphtools.elements.edge.DoksDokVerbindung;
import de.imise.tool3lgm.graphtools.elements.edge.EtAufVerbindung;
import de.imise.tool3lgm.graphtools.elements.edge.EtntDotVerbindung;
import de.imise.tool3lgm.graphtools.elements.edge.EtntNatVerbindung;
import de.imise.tool3lgm.graphtools.elements.edge.KawbDoksVerbindung;
import de.imise.tool3lgm.graphtools.elements.edge.KawbOrgpVerbindung;
import de.imise.tool3lgm.graphtools.elements.edge.KommBeziehung;
import de.imise.tool3lgm.graphtools.elements.edge.ObjLogspVerbindung;
import de.imise.tool3lgm.graphtools.elements.edge.ObjObjVerbindung;
import de.imise.tool3lgm.graphtools.elements.edge.ObjReprVerbindung;
import de.imise.tool3lgm.graphtools.elements.edge.OrgAufOrgVerbindung;
import de.imise.tool3lgm.graphtools.elements.edge.OrgOrgVerbindung;
import de.imise.tool3lgm.graphtools.elements.edge.PdvbBtypVerbindung;
import de.imise.tool3lgm.graphtools.elements.edge.PdvbPdvbVerbindung;
import de.imise.tool3lgm.graphtools.elements.edge.PdvbPdvbkVerbindung;
import de.imise.tool3lgm.graphtools.elements.edge.PdvbStoVerbindung;
import de.imise.tool3lgm.graphtools.elements.edge.PdvbSubnVerbindung;
import de.imise.tool3lgm.graphtools.elements.edge.PdvbkAwbVerbindung;
import de.imise.tool3lgm.graphtools.elements.edge.RawbAwpVerbindung;
import de.imise.tool3lgm.graphtools.elements.edge.RawbDbsVerbindung;
import de.imise.tool3lgm.graphtools.elements.edge.RawbRawbVerbindung;
import de.imise.tool3lgm.graphtools.elements.edge.SubnNetzpVerbindung;
import de.imise.tool3lgm.graphtools.elements.edge.SubnNetztVerbindung;
import de.imise.tool3lgm.graphtools.elements.edge.SwpAufVerbindung;
import de.imise.tool3lgm.graphtools.elements.node.Anwendungsbaustein;
import de.imise.tool3lgm.graphtools.elements.node.Aufgabe;
import de.imise.tool3lgm.graphtools.elements.node.Bausteinschnittstelle;
import de.imise.tool3lgm.graphtools.elements.node.Bausteintyp;
import de.imise.tool3lgm.graphtools.elements.node.Benutzungsschnittstelle;
import de.imise.tool3lgm.graphtools.elements.node.Datenbanksystem;
import de.imise.tool3lgm.graphtools.elements.node.Datensatztyp;
import de.imise.tool3lgm.graphtools.elements.node.Dokumentensammlung;
import de.imise.tool3lgm.graphtools.elements.node.Dokumententyp;
import de.imise.tool3lgm.graphtools.elements.node.Ereignistyp;
import de.imise.tool3lgm.graphtools.elements.node.Kommunikationsstandard;
import de.imise.tool3lgm.graphtools.elements.node.KonAnwendungsbaustein;
import de.imise.tool3lgm.graphtools.elements.node.Nachrichtentyp;
import de.imise.tool3lgm.graphtools.elements.node.Netzprotokoll;
import de.imise.tool3lgm.graphtools.elements.node.Netztyp;
import de.imise.tool3lgm.graphtools.elements.node.Objekttyp;
import de.imise.tool3lgm.graphtools.elements.node.Organisationseinheit;
import de.imise.tool3lgm.graphtools.elements.node.Organisationsplan;
import de.imise.tool3lgm.graphtools.elements.node.PhysischerDVBaustein;
import de.imise.tool3lgm.graphtools.elements.node.RechAnwendungsbaustein;
import de.imise.tool3lgm.graphtools.elements.node.Softwareprodukt;
import de.imise.tool3lgm.graphtools.elements.node.Standort;
import de.imise.tool3lgm.graphtools.elements.node.Subnetz;
import de.imise.tool3lgm.userproperties.UserProperties;

/**
 * Stellt Funktionen bereit, mit denen konkrete Pfade zu definierten Metapfaden ermittelt werden können.
 * 
 * @author Thomas Rudert
 * @author AXS (5.10.2007)
 */
public final class PathFinder {

    /**
     * COMMENTME
     */
    private static HashMap<String, MetaPath[]> metaPathes = new HashMap<String, MetaPath[]>(100);

    /**
     * Liste der Elementtypen, für die ein Pfad in init() definiert wurde (wird z.B. für die Comboboxen in der Matrixsicht gebraucht)
     */
    private static HashSet<Class<? extends ModelElement>> elementClassesInPathes = new HashSet<Class<? extends ModelElement>>();

    static {
        try {
            init();
        } catch (InvalidPathException exp) {

        }
    }

    /**
     * Liefert alle <code>MetaPath</code>es, die zwischen Elementen der Art <code>startClass</code> und <code>endClass</code> definiert sind.
     * 
     * @param startClass
     * @param endClass
     * @return
     */
    public static final MetaPath[] getMetaPathes(final Class<? extends ModelElement> startClass, final Class<? extends ModelElement> endClass) {
        return metaPathes.get(calculateKey(startClass, endClass));
    }

    //	/**
    //	 * Wenn die übergebene <code>startClass</code> die Start- oder Endklasse der ersten Assoziation dieses 
    //	 * Pfades ist und die übergebene <code>endClass</code> in der letzten Assoziation des Pfades vorkommt, 
    //	 * dann kommt der übergebene Pfad zurück.<br />
    //	 * 
    //	 * Wenn die übergebene <code>startClass</code> eine Endklasse des übergebenen Metapfades ist und die übergebene
    //	 * <code>endClass</code> eine Startklasse des übergebenen Metapfades, dann kommt ein Metapfad zurück, in dem 
    //	 * die Assoziationen genau anders herum sind als im übergebenen MetaPfad.<br />
    //	 * 
    //	 * Wenn Start- und Endklassen in keiner der Richtungen übereinstimmen, kommt <code>null</code> zurück.
    //	 * 
    //	 * @param startClass
    //	 * @param endClass
    //	 * @param metaPath
    //	 * @return
    //	 * /
    //    public static final MetaPath getDirectedMetaPath(Class<? extends ModelElement> startClass, Class<? extends ModelElement> endClass, MetaPath metaPath) {
    //		boolean switchAssociations = false;
    //		for (int ep = 0 ; ep<metaPath.countPathes(); ep++){
    //			Class<? extends Kante>[] edgeClasses = metaPath.getEdgeClasses(ep);
    //			int lastIndex = edgeClasses.length-1;
    //			if (Kante.isStartOrEndClass(edgeClasses[0], startClass) && Kante.isStartOrEndClass(edgeClasses[lastIndex], endClass))
    //				return metaPath;
    //			if (Kante.isStartOrEndClass(edgeClasses[0], endClass) && Kante.isStartOrEndClass(edgeClasses[lastIndex], startClass))
    //				switchAssociations = true;
    //		}
    //		//wenn switchAssociations immer noch false ist und er bis hier gekommen ist, dann passt der MetaPath gar
    //		//nicht zu den Start und Zielklassen -> es kommt null zurück
    //		if (!switchAssociations)
    //			return null;
    //		
    //		@SuppressWarnings("unchecked")
    //		Class<? extends Kante>[][] path = new Class[metaPath.countPathes()][metaPath.getEdgeClasses(0).length];
    //		//von allen Assoziationslisten alle Assoziationen umdrehen
    //		for (int ep = 0 ; ep<metaPath.countPathes(); ep++){
    //			Class<? extends Kante>[] edgeClasses = metaPath.getEdgeClasses(ep);
    //			int lastIndex = edgeClasses.length-1;
    //			for (int i=0; i<edgeClasses.length; i++){
    //				path[ep][lastIndex-i] = edgeClasses[i];
    //			}
    //		}
    //		return new MetaPath(startClass, endClass, path);
    //	}

    /**
     * @param element1
     * @param element2
     * @param metaPath
     * @param doc
     * @return Doppelkante.NOTCONNECTED / Doppelkante.FORWARD / Doppelkante.BACKWARD / Doppelkante.DOUBLE if path.isImmediate otherwise
     *         Doppelkante.NOTCONNECTED / Doppelkante.DOUBLE
     */
    public static final int isConnected(ModelElement element1, ModelElement element2, final MetaPath metaPath) {
        if (!UserProperties.isSearchParts() && !UserProperties.isSearchParents()) {
            return isConnected(element1, element2, metaPath, false);
        }

        int retVal = Doppelkante.NOTCONNECTED;

        Set<ModelElement> list1 = new HashSet<ModelElement>();
        Set<ModelElement> list2 = new HashSet<ModelElement>();
        list1.add(element1);
        list2.add(element2);
        if (UserProperties.isSearchParts()) {
            list1.addAll(element1.getPartElements());
            list2.addAll(element2.getPartElements());
        }
        if (UserProperties.isSearchParents()) {
            list1.addAll(element1.getParentElements());
            list2.addAll(element2.getParentElements());
        }
        Iterator<ModelElement> iterator1 = list1.iterator();
        Iterator<ModelElement> iterator2;

        while (iterator1.hasNext()) {
            element1 = iterator1.next();
            iterator2 = list2.iterator();
            while (iterator2.hasNext()) {
                element2 = iterator2.next();
                int con = isConnected(element1, element2, metaPath, false);
                switch (con) {
                case Doppelkante.DOUBLE:
                    return Doppelkante.DOUBLE;
                case Doppelkante.FORWARD:
                    if (retVal == Doppelkante.BACKWARD) {
                        return Doppelkante.DOUBLE;
                    }
                    return Doppelkante.FORWARD;
                case Doppelkante.BACKWARD:
                    if (retVal == Doppelkante.FORWARD) {
                        return Doppelkante.DOUBLE;
                    }
                    return Doppelkante.BACKWARD;
                }
            }
        }
        return retVal;
    }

    /**
     * @param element1
     * @param element2
     * @param metaPath
     * @param reverse
     * @return
     */
    private static final int isConnected(final ModelElement element1, final ModelElement element2, final MetaPath metaPath, final boolean reverse) {
        if (!metaPath.getStartClass().isAssignableFrom(element1.getClass()) || !metaPath.getEndClass().isAssignableFrom(element2.getClass())) {
            if (metaPath.getEndClass().isAssignableFrom(element1.getClass()) && metaPath.getStartClass().isAssignableFrom(element2.getClass())) {
                return isConnected(element2, element1, metaPath, !reverse);
            }
            return Doppelkante.NOTCONNECTED;
        }

        for (int pathIndex = 0; pathIndex < metaPath.countPathes(); pathIndex++) {
            // direkt vebunden?
            if (metaPath.isImmediate(pathIndex)) {
                if (element1.isConnectedTo(element2, metaPath.getEdgeClasses(pathIndex)[0]) && element1.isConnectedFrom(element2)) {
                    return Doppelkante.DOUBLE;
                } else if (element1.isConnectedTo(element2, metaPath.getEdgeClasses(pathIndex)[0])) {
                    return reverse && metaPath.isDirectional() ? Doppelkante.BACKWARD : Doppelkante.FORWARD;
                } else if (element1.isConnectedFrom(element2, metaPath.getEdgeClasses(pathIndex)[0])) {
                    return reverse && metaPath.isDirectional() ? Doppelkante.FORWARD : PartOfBeziehung.class.isAssignableFrom(metaPath.getEdgeClasses(pathIndex)[0]) ? Doppelkante.NOTCONNECTED : Doppelkante.BACKWARD;
                }
            } else {
                switch (isConnected(element1, element2, metaPath, 0, pathIndex)) {
                case Doppelkante.DOUBLE:
                    return Doppelkante.DOUBLE;
                case Doppelkante.FORWARD:
                    return reverse && metaPath.isDirectional() ? Doppelkante.BACKWARD : Doppelkante.FORWARD;
                case Doppelkante.BACKWARD:
                    return reverse && metaPath.isDirectional() ? Doppelkante.FORWARD : Doppelkante.BACKWARD;
                }
            }
        }
        return Doppelkante.NOTCONNECTED;
    }

    /**
     * @param current
     * @param end
     * @param metaPath
     * @param position
     * @param pathIndex
     * @return
     */

    private static final int isConnected(final ModelElement current, final ModelElement end, final MetaPath metaPath, final int position, final int pathIndex) {
        if (position == metaPath.getLength(pathIndex)) {
            if (current.equals(end)) {
                return Doppelkante.DOUBLE;
            }
            return Doppelkante.NOTCONNECTED;
        }

        int retVal;

        ArrayList<ModelElement> elements = current.getConnectedElementsByEdge(metaPath.getEdgeClasses(pathIndex)[position]);

        for (ModelElement me : elements) {
            if ((retVal = isConnected(me, end, metaPath, position + 1, pathIndex)) != Doppelkante.NOTCONNECTED) {
                if (metaPath.getControl() == position) {
                    if (current.isConnectedTo(me) && current.isConnectedFrom(me)) {
                        return Doppelkante.DOUBLE;
                    } else if (current.isConnectedTo(me)) {
                        return Doppelkante.FORWARD;
                    } else if (current.isConnectedFrom(me)) {
                        return Doppelkante.BACKWARD;
                    } else {
                        return Doppelkante.NOTCONNECTED;
                    }
                }
                return retVal;
            }
        }
        return Doppelkante.NOTCONNECTED;
    }

    //	/**
    //	 * Liefert alle Elemente, die im angegebenen Modell mit dem übergebenen Element über den angegebenen Pfad verbunden sind.
    //	 * 
    //	 * @param me
    //	 * @param metaPath
    //	 * @param gdcoll
    //	 * @return
    //	 * /
    //	public static final HashSet<ModelElement> getDirectConnectedElements(ModelElement me, MetaPath metaPath, GDCollection gdcoll){
    //		HashSet<ModelElement> startElements = new HashSet<ModelElement>();
    //		startElements.add(me);
    //		HashSet<ModelElement> endElements = new HashSet<ModelElement>();
    //		for (int assoIndex = 0; assoIndex < metaPath.getLength(); assoIndex++){
    //			for (int pathIndex = 0; pathIndex < metaPath.countPathes(); pathIndex++){
    //				endElements.clear();
    //				for (ModelElement startElem : startElements)
    //					endElements.addAll(startElem.getConnectedElementsByEdge(metaPath.getEdgeClasses(pathIndex)[assoIndex]));
    //			}
    //			startElements = endElements; 
    //		}
    //		return endElements;
    //	}

    /**
     * Liefert alle Elemente, die im angegebenen Modell mit dem übergebenen Element über den angegebenen Pfad verbunden sind.
     * 
     * @param me
     * @param metaPath
     * @param gdcoll
     * @return
     */
    public static final HashSet<ModelElement> getDirectConnectedElements(final ModelElement me, final MetaPath metaPath, final GDCollection gdcoll) {
        HashSet<ModelElement> startElements = new HashSet<ModelElement>();
        startElements.add(me);
        HashSet<ModelElement> endElements = new HashSet<ModelElement>();
        for (int assoIndex = 0; assoIndex < metaPath.getLength(); assoIndex++) {
            for (int pathIndex = 0; pathIndex < metaPath.countPathes(); pathIndex++) {
                endElements.clear();
                for (ModelElement startElem : startElements) {
                    endElements.addAll(startElem.getConnectedElementsByEdge(metaPath.getEdgeClasses(pathIndex)[assoIndex]));
                }
            }
            startElements.clear();
            startElements.addAll(endElements);
        }
        return endElements;
    }

    //	/**
    //	 * Liefert alle Elemente die mit dem übergebeben Element oder seinen Elternelementen über den
    //	 * angegebenen Pfad verbunden Elemente zurück.  
    //	 * 
    //	 * @param me
    //	 * @param targetElementClass
    //	 * 		Klasse, von der die Zielelemente sein sollen. Diese muss nicht mit der letzten Elementklasse des 
    //	 * 		Metapfades übereinstimmen, sondern kann eine spezielle Unterklasse sein.
    //	 * @param metaPath
    //	 * @param gdcoll
    //	 * @return
    //	 * /
    //	public static final HashSet<ModelElement> getConnectedElements(ModelElement me, Class<? extends ModelElement>targetElementClass, MetaPath metaPath, GDCollection gdcoll){
    //		HashSet<ModelElement> startElements = me.getParentElements();
    //		startElements.add(me);
    //		HashSet<ModelElement> endElements = null;
    //		boolean pathStartClass = Kante.isStartOrEndClass(metaPath.getEdgeClasses(0)[0], me.getClass());
    //		for (int assoIndex = 0; assoIndex < metaPath.getLength(); assoIndex++){
    //			for (int pathIndex = 0; pathIndex < metaPath.countPathes(); pathIndex++){
    //				endElements = new HashSet<ModelElement>();
    //				int index = pathStartClass?assoIndex:metaPath.getLength()-1-assoIndex;
    //				for (ModelElement startElem : startElements)
    //					endElements.addAll(startElem.getConnectedElementsByEdge(metaPath.getEdgeClasses(pathIndex)[index]));
    //			}
    //			startElements = endElements; 
    //		}
    //		if (endElements==null)
    //			return null;
    //		HashSet<ModelElement> reallyEndElements = new HashSet<ModelElement>(endElements.size());
    //		for (ModelElement elem : endElements)
    //			if (targetElementClass.isAssignableFrom(elem.getClass()))
    //				reallyEndElements.add(elem);
    //		return reallyEndElements;
    //	}

    /**
     * Liefert alle Elemente die mit dem übergebeben Element oder seinen Elternelementen über den angegebenen Pfad verbunden Elemente zurück.
     * 
     * @param me
     * @param targetElementClass Klasse, von der die Zielelemente sein sollen. Diese muss nicht mit der letzten Elementklasse des Metapfades
     *            übereinstimmen, sondern kann eine spezielle Unterklasse sein.
     * @param metaPath
     * @return
     */
    public static final HashSet<ModelElement> getConnectedElements(final ModelElement me, final Class<? extends ModelElement> targetElementClass, final MetaPath metaPath) {
        HashSet<ModelElement> startElements = me.getParentElements();
        startElements.add(me);
        HashSet<ModelElement> endElements = null;
        //das übergebene Element ist Startklasse des Pfades?
        boolean pathStartClass = Kante.isStartOrEndClass(metaPath.getEdgeClasses(0)[0], me.getClass());
        //für alle Assoziationen jedes inneren MetaPfades des übergebenen Gesamtmetapfades
        for (int assoIndex = 0; assoIndex < metaPath.getLength(); assoIndex++) {
            //für jeden inneren MetaPfad
            for (int pathIndex = 0; pathIndex < metaPath.countPathes(); pathIndex++) {
                endElements = new HashSet<ModelElement>();
                //wenn das übergebene Element von der Startklasse des Pfades ist, laufe die einzelnen
                //Assoziationen des Metapfades von vorne durch, sonst von hinten
                int index = pathStartClass ? assoIndex : metaPath.getLength() - 1 - assoIndex;
                //zu allen Endelementen alle über die aktuelle Assoziation verbunenen Elemente hinzufügen
                for (ModelElement startElem : startElements) {
                    endElements.addAll(startElem.getConnectedElementsByEdge(metaPath.getEdgeClasses(pathIndex)[index]));
                }
            }
            //Startelemente auf die Endelemente setzen
            startElements.clear();
            startElements.addAll(endElements);
        }
        //wenn es keine Endelemente gibt
        if (endElements == null) {
            return null;
        }
        HashSet<ModelElement> reallyEndElements = new HashSet<ModelElement>(endElements.size());
        for (ModelElement elem : endElements) {
            if (targetElementClass.isAssignableFrom(elem.getClass())) {
                reallyEndElements.add(elem);
            }
        }
        return reallyEndElements;
    }

    /**
     * Berechnet einen eindeutigen Schlüssel für 2 übergebene Klassen.
     * 
     * @param elementClass1
     * @param elementClass2
     * @return
     */
    private static final String calculateKey(final Class<? extends ModelElement> elementClass1, final Class<? extends ModelElement> elementClass2) {
        int hash1 = elementClass1.hashCode();
        int hash2 = elementClass2.hashCode();
        StringBuilder sb = new StringBuilder();
        if (hash1 < hash2) {
            sb.append(elementClass1.hashCode());
            sb.append(elementClass2.hashCode());
        } else {
            sb.append(elementClass2.hashCode());
            sb.append(elementClass1.hashCode());
        }
        return sb.toString();
    }

    /**
     * Setzt den übergenen Metapfad in die HashMap aller Metapfade
     * 
     * @param metaPath
     */
    private static final void put(final MetaPath[] metaPath) {
        metaPathes.put(calculateKey(metaPath[0].getStartClass(), metaPath[0].getEndClass()), metaPath);
    }

    /**
     * @throws InvalidPathException
     */
    @SuppressWarnings("unchecked")
    private static final void init() throws InvalidPathException {

        /* Aufgabe - Objekttyp */
        put(new MetaPath[] {
            new MetaPath(Aufgabe.class, Objekttyp.class, new Class[][] {
                {
                    AufObjVerbindung.class
                }
            }, new Color[] {
                    Color.ORANGE, Color.BLUE, Color.GREEN
            }, new String[] {
                    s("Aufgabe") + " " + s("text_bearb") + " " + s("text_und") + " " + s("text_inter") + " " + s("Objekttyp"), s("Aufgabe") + " " + s("text_bearb") + " " + s("Objekttyp"), s("Aufgabe") + " " + s("text_inter") + " " + s("Objekttyp")
            })
        });

        /* Aufgabe - Aufgabe */
        put(new MetaPath[] {
            new MetaPath(Aufgabe.class, Aufgabe.class, new Class[][] {
                {
                    AufAufVerbindung.class
                }
            }, s("zeile") + " " + s("text_teil_von") + " " + s("spalte"), true)
        });

        /* Aufgabe - Organisationseinheit */
        put(new MetaPath[] {
            new MetaPath(Aufgabe.class, Organisationseinheit.class, new Class[][] {
                {
                        AufAufOrgVerbindung.class, OrgAufOrgVerbindung.class
                }
            }, s("Aufgabe") + " " + s("text_wird_erledigt_in") + " " + s("Organisationseinheit"))
        });

        /* Aufgabe - Anwendungsbaustein */
        put(new MetaPath[] {
            new MetaPath(Aufgabe.class, Anwendungsbaustein.class, new Class[][] {
                {
                        AufAufOrgVerbindung.class, AwbkAufOrgVerbindung.class, AwbAwbkVerbindung.class
                }
            }, s("Aufgabe") + " " + s("text_unterstuetzt") + " " + s("Anwendungsbaustein"))
        });

        /* Aufgabe - RechAnwendungsbaustein */
        put(new MetaPath[] {
            new MetaPath(Aufgabe.class, RechAnwendungsbaustein.class, new Class[][] {
                {
                        AufAufOrgVerbindung.class, AwbkAufOrgVerbindung.class, AwbAwbkVerbindung.class
                }
            }, s("Aufgabe") + " " + s("text_unterstuetzt") + " " + s("Anwendungsbaustein"))
        });

        /* Aufgabe - KonAnwendungsbaustein */
        put(new MetaPath[] {
            new MetaPath(Aufgabe.class, KonAnwendungsbaustein.class, new Class[][] {
                {
                        AufAufOrgVerbindung.class, AwbkAufOrgVerbindung.class, AwbAwbkVerbindung.class
                }
            }, s("Aufgabe") + " " + s("text_unterstuetzt") + " " + s("Anwendungsbaustein"))
        });

        /* Aufgabe - PhyDVBaustein */
        put(new MetaPath[] {
            new MetaPath(Aufgabe.class, PhysischerDVBaustein.class, new Class[][] {
                {
                        AufAufOrgVerbindung.class, AwbkAufOrgVerbindung.class, AwbAwbkVerbindung.class, PdvbkAwbVerbindung.class, PdvbPdvbkVerbindung.class
                }
            }, s("Aufgabe") + " " + s("text_unterstuetzt") + " " + s("PhysischerDVBaustein"))
        });

        /* Aufgabe - Standort */
        put(new MetaPath[] {
            new MetaPath(Aufgabe.class, Standort.class, new Class[][] {
                {
                        AufAufOrgVerbindung.class, AwbkAufOrgVerbindung.class, AwbAwbkVerbindung.class, PdvbkAwbVerbindung.class, PdvbPdvbkVerbindung.class, PdvbStoVerbindung.class
                }
            }, s("Aufgabe") + " " + s("text_erledigt") + " " + s("Standort"))
        });

        /* Objekttyp - Datensatztyp */
        put(new MetaPath[] {
            new MetaPath(Objekttyp.class, Datensatztyp.class, new Class[][] {
                {
                    ObjReprVerbindung.class
                }
            }, s("Objekttyp") + " " + s("text_repraesentiert") + " " + s("Datensatztyp"))
        });

        /* Objekttyp - Dokumententyp */
        put(new MetaPath[] {
            new MetaPath(Objekttyp.class, Dokumententyp.class, new Class[][] {
                {
                    ObjReprVerbindung.class
                }
            }, s("Objekttyp") + " " + s("text_repraesentiert") + " " + s("Dokumententyp"))
        });

        /* Objekttyp - Nachrichtentyp */
        put(new MetaPath[] {
            new MetaPath(Objekttyp.class, Nachrichtentyp.class, new Class[][] {
                {
                    ObjReprVerbindung.class
                }
            }, s("Objekttyp") + " " + s("text_repraesentiert") + " " + s("Nachrichtentyp"))
        });

        /* Objekttyp - Dokumentensammlung */
        put(new MetaPath[] {
            new MetaPath(Objekttyp.class, Dokumentensammlung.class, new Class[][] {
                {
                    ObjLogspVerbindung.class
                }
            }, s("Objekttyp") + " " + s("text_fuehrend") + " " + s("Dokumentensammlung"))
        });

        /* Objekttyp - Datenbanksystem */
        put(new MetaPath[] {
                new MetaPath(Objekttyp.class, Datenbanksystem.class, new Class[][] {
                    {
                        ObjLogspVerbindung.class
                    }
                }, s("Objekttyp") + " " + s("text_fuehrend") + " " + s("Datenbanksystem")), new MetaPath(Objekttyp.class, Datenbanksystem.class, new Class[][] {
                    {
                            ObjReprVerbindung.class, DbsDatVerbindung.class,
                    }
                }, s("Objekttyp") + " " + s("text_speichert") + " " + s("Datenbanksystem")),
        });

        /* Aufgabe - Softwareprodukt */
        put(new MetaPath[] {
            new MetaPath(Aufgabe.class, Softwareprodukt.class, new Class[][] {
                {
                    SwpAufVerbindung.class
                }
            }, s("Aufgabe") + " " + s("text_kann_unterstuetzt") + " " + s("Softwareprodukt"))
        });

        /* Aufgabe - Ereignistyp */
        put(new MetaPath[] {
            new MetaPath(Aufgabe.class, Ereignistyp.class, new Class[][] {
                {
                    EtAufVerbindung.class
                }
            }, s("Ereignistyp") + " " + s("text_ausloesen") + " " + s("Aufgabe"))
        });

        /* Anwendungsbaustein - Bausteinschnittstelle */
        put(new MetaPath[] {
            new MetaPath(Anwendungsbaustein.class, Bausteinschnittstelle.class, new Class[][] {
                {
                    AwbKommssVerbindung.class
                }
            }, s("Anwendungsbaustein") + " " + s("text_besitzt") + " " + s("Bausteinschnittstelle"))
        });

        /* RechAnwendungsbaustein - Bausteinschnittstelle */
        put(new MetaPath[] {
            new MetaPath(RechAnwendungsbaustein.class, Bausteinschnittstelle.class, new Class[][] {
                {
                    AwbKommssVerbindung.class
                }
            }, s("Anwendungsbaustein") + " " + s("text_besitzt") + " " + s("Bausteinschnittstelle"))
        });

        /* KonAnwendungsbaustein - Bausteinschnittstelle */
        put(new MetaPath[] {
            new MetaPath(KonAnwendungsbaustein.class, Bausteinschnittstelle.class, new Class[][] {
                {
                    AwbKommssVerbindung.class
                }
            }, s("Anwendungsbaustein") + " " + s("text_besitzt") + " " + s("Bausteinschnittstelle"))
        });

        /* Anwendungsbaustein - Benutzungsschnittstelle */
        put(new MetaPath[] {
            new MetaPath(Anwendungsbaustein.class, Benutzungsschnittstelle.class, new Class[][] {
                {
                    AwbKommssVerbindung.class
                }
            }, s("Anwendungsbaustein") + " " + s("text_besitzt") + " " + s("Bausteinschnittstelle"))
        });

        /* RechAnwendungsbaustein - Benutzungsschnittstelle */
        put(new MetaPath[] {
            new MetaPath(RechAnwendungsbaustein.class, Benutzungsschnittstelle.class, new Class[][] {
                {
                    AwbKommssVerbindung.class
                }
            }, s("Anwendungsbaustein") + " " + s("text_besitzt") + " " + s("Bausteinschnittstelle"))
        });

        /* KonAnwendungsbaustein - Benutzungsschnittstelle */
        put(new MetaPath[] {
            new MetaPath(KonAnwendungsbaustein.class, Benutzungsschnittstelle.class, new Class[][] {
                {
                    AwbKommssVerbindung.class
                }
            }, s("Anwendungsbaustein") + " " + s("text_besitzt") + " " + s("Bausteinschnittstelle"))
        });

        /* RechAnwendungsbaustein - Datenbanksystem */
        put(new MetaPath[] {
            new MetaPath(RechAnwendungsbaustein.class, Datenbanksystem.class, new Class[][] {
                {
                    RawbDbsVerbindung.class
                }
            }, s("RechAnwendungsbaustein") + " " + s("text_besitzt") + " " + s("Datenbanksystem"))
        });

        /* KonAnwendungsbaustein - Dokumentensammlung */
        put(new MetaPath[] {
            new MetaPath(KonAnwendungsbaustein.class, Dokumentensammlung.class, new Class[][] {
                {
                    KawbDoksVerbindung.class
                }
            }, s("KonAnwendungsbaustein") + " " + s("text_besitzt") + " " + s("Dokumentensammlung"))
        });

        /* Anwendungsbaustein - PhyDVBaustein */
        put(new MetaPath[] {
            new MetaPath(Anwendungsbaustein.class, PhysischerDVBaustein.class, new Class[][] {
                {
                        PdvbkAwbVerbindung.class, PdvbPdvbkVerbindung.class
                }
            }, s("Anwendungsbaustein") + " " + s("text_installiert") + " " + s("PhysischerDVBaustein"))
        });

        /* RechAnwendungsbaustein - PhyDVBaustein */
        put(new MetaPath[] {
            new MetaPath(RechAnwendungsbaustein.class, PhysischerDVBaustein.class, new Class[][] {
                {
                        PdvbkAwbVerbindung.class, PdvbPdvbkVerbindung.class
                }
            }, s("Anwendungsbaustein") + " " + s("text_installiert") + " " + s("PhysischerDVBaustein"))
        });

        /* KonAnwendungsbaustein - PhyDVBaustein */
        put(new MetaPath[] {
            new MetaPath(KonAnwendungsbaustein.class, PhysischerDVBaustein.class, new Class[][] {
                {
                        PdvbkAwbVerbindung.class, PdvbPdvbkVerbindung.class
                }
            }, s("Anwendungsbaustein") + " " + s("text_installiert") + " " + s("PhysischerDVBaustein"))
        });

        /* Organisationseinheit - Organisationseinheit */
        put(new MetaPath[] {
            new MetaPath(Organisationseinheit.class, Organisationseinheit.class, new Class[][] {
                {
                    OrgOrgVerbindung.class
                }
            }, s("zeile") + " " + s("text_teil_von") + " " + s("spalte"), true)
        });

        /* Organisationseinheit - PhyDVBaustein */
        put(new MetaPath[] {
            new MetaPath(Organisationseinheit.class, PhysischerDVBaustein.class, new Class[][] {
                {
                        OrgAufOrgVerbindung.class, AwbkAufOrgVerbindung.class, AwbAwbkVerbindung.class, PdvbkAwbVerbindung.class, PdvbPdvbkVerbindung.class
                }
            }, s("Organisationseinheit") + " " + s("text_nutzt") + " " + s("PhysischerDVBaustein"))
        });

        /* Organisationseinheit - Anwendungsbaustein */
        put(new MetaPath[] {
            new MetaPath(Organisationseinheit.class, Anwendungsbaustein.class, new Class[][] {
                {
                        OrgAufOrgVerbindung.class, AwbkAufOrgVerbindung.class, AwbAwbkVerbindung.class
                }
            }, s("Organisationseinheit") + " " + s("text_nutzt") + " " + s("Anwendungsbaustein"))
        });

        /* Organisationseinheit - RechAnwendungsbaustein */
        put(new MetaPath[] {
            new MetaPath(Organisationseinheit.class, RechAnwendungsbaustein.class, new Class[][] {
                {
                        OrgAufOrgVerbindung.class, AwbkAufOrgVerbindung.class, AwbAwbkVerbindung.class
                }
            }, s("Organisationseinheit") + " " + s("text_nutzt") + " " + s("Anwendungsbaustein"))
        });

        /* Organisationseinheit - KonAnwendungsbaustein */
        put(new MetaPath[] {
            new MetaPath(Organisationseinheit.class, KonAnwendungsbaustein.class, new Class[][] {
                {
                        OrgAufOrgVerbindung.class, AwbkAufOrgVerbindung.class, AwbAwbkVerbindung.class
                }
            }, s("Organisationseinheit") + " " + s("text_nutzt") + " " + s("Anwendungsbaustein"))
        });

        /* Objekttyp - Objekttyp */
        put(new MetaPath[] {
            new MetaPath(Objekttyp.class, Objekttyp.class, new Class[][] {
                {
                    ObjObjVerbindung.class
                }
            }, s("zeile") + " " + s("text_teil_von") + " " + s("spalte"), true)
        });

        /* Subnetz - Netzprotokoll */
        put(new MetaPath[] {
            new MetaPath(Subnetz.class, Netzprotokoll.class, new Class[][] {
                {
                    SubnNetzpVerbindung.class
                }
            }, s("Subnetz") + " " + s("text_basiert") + " " + s("Netzprotokoll"))
        });

        /* Subnetz - Netztyp */
        put(new MetaPath[] {
            new MetaPath(Subnetz.class, Netztyp.class, new Class[][] {
                {
                    SubnNetztVerbindung.class
                }
            }, s("Subnetz") + " " + s("text_basiert") + " " + s("Netztyp"))
        });

        /* PhyDVBaustein - Subnetz */
        put(new MetaPath[] {
            new MetaPath(PhysischerDVBaustein.class, Subnetz.class, new Class[][] {
                {
                    PdvbSubnVerbindung.class
                }
            }, s("PhysischerDVBaustein") + " " + s("text_gehoert") + " " + s("Subnetz"))
        });

        /* PhyDVBaustein - Standort */
        put(new MetaPath[] {
            new MetaPath(PhysischerDVBaustein.class, Standort.class, new Class[][] {
                {
                    PdvbStoVerbindung.class
                }
            }, s("PhysischerDVBaustein") + " " + s("text_hat") + " " + s("Standort"))
        });

        /* PhyDVBaustein - Bausteintyp */
        put(new MetaPath[] {
            new MetaPath(PhysischerDVBaustein.class, Bausteintyp.class, new Class[][] {
                {
                    PdvbBtypVerbindung.class
                }
            }, s("PhysischerDVBaustein") + " " + s("text_ist") + " " + s("Bausteintyp"))
        });

        /* PhyDVBaustein - PhyDVBaustein */
        put(new MetaPath[] {
                new MetaPath(PhysischerDVBaustein.class, PhysischerDVBaustein.class, new Class[][] {
                    {
                        PdvbPdvbVerbindung.class
                    }
                }, s("zeile") + " " + s("text_teil_von") + " " + s("spalte"), true), new MetaPath(PhysischerDVBaustein.class, PhysischerDVBaustein.class, new Class[][] {
                    {
                        DatenuebertragungsVerbindung.class
                    }
                }, s("PhysischerDVBaustein") + " " + s("text_verbunden") + " " + s("PhysischerDVBaustein"))
        });

        /* Organisationseinheit - Softwareprodukt */
        put(new MetaPath[] {
            new MetaPath(Organisationseinheit.class, Softwareprodukt.class, new Class[][] {
                {
                        OrgAufOrgVerbindung.class, AwbkAufOrgVerbindung.class, AwbAwbkVerbindung.class, RawbAwpVerbindung.class, AwpSwpVerbindung.class
                }
            }, s("Organisationseinheit") + " " + s("text_nutzt") + " " + s("Softwareprodukt"))
        });

        /* Anwendungsbaustein - Kommunikationsstandard */
        put(new MetaPath[] {
            new MetaPath(Anwendungsbaustein.class, Kommunikationsstandard.class, new Class[][] {
                {
                        AwbKommssVerbindung.class, BssKommstVerbindung.class
                }
            }, s("Anwendungsbaustein") + " " + s("text_nutzt") + " " + s("Kommunikationsstandard"))
        });

        /* RechAnwendungsbaustein - Kommunikationsstandard */
        put(new MetaPath[] {
            new MetaPath(RechAnwendungsbaustein.class, Kommunikationsstandard.class, new Class[][] {
                {
                        AwbKommssVerbindung.class, BssKommstVerbindung.class
                }
            }, s("Anwendungsbaustein") + " " + s("text_nutzt") + " " + s("Kommunikationsstandard"))
        });

        /* KonAnwendungsbaustein - Kommunikationsstandard */
        put(new MetaPath[] {
            new MetaPath(KonAnwendungsbaustein.class, Kommunikationsstandard.class, new Class[][] {
                {
                        AwbKommssVerbindung.class, BssKommstVerbindung.class
                }
            }, s("Anwendungsbaustein") + " " + s("text_nutzt") + " " + s("Kommunikationsstandard"))
        });

        /* RechAnwendungsbaustein - Softwareprodukt */
        put(new MetaPath[] {
            new MetaPath(RechAnwendungsbaustein.class, Softwareprodukt.class, new Class[][] {
                {
                        RawbAwpVerbindung.class, AwpSwpVerbindung.class
                }
            }, s("Anwendungsbaustein") + " " + s("text_gesteuert") + " " + s("Softwareprodukt"))
        });

        /* KonAnwendungsbaustein - Organisationsplan */
        put(new MetaPath[] {
            new MetaPath(KonAnwendungsbaustein.class, Organisationsplan.class, new Class[][] {
                {
                    KawbOrgpVerbindung.class
                }
            }, s("Anwendungsbaustein") + " " + s("text_gesteuert") + " " + s("Organisationsplan"))
        });

        /* Objekttyp - RechAnwendungsbaustein */
        put(new MetaPath[] {
                new MetaPath(Objekttyp.class, RechAnwendungsbaustein.class, new Class[][] {
                    {
                            ObjLogspVerbindung.class, RawbDbsVerbindung.class
                    }
                }, s("Objekttyp") + " " + s("text_fuehrenden") + " " + s("Anwendungsbaustein")), new MetaPath(Objekttyp.class, RechAnwendungsbaustein.class, new Class[][] {
                    {
                            ObjReprVerbindung.class, DbsDatVerbindung.class, RawbDbsVerbindung.class
                    }
                }, s("Objekttyp") + " " + s("text_speichert") + " " + s("Anwendungsbaustein")), new MetaPath(Objekttyp.class, RechAnwendungsbaustein.class, new Class[][] {
                        {
                                ObjReprVerbindung.class, EtntNatVerbindung.class, BssEtntVerbindung.class, AwbKommssVerbindung.class
                        }, {
                                ObjReprVerbindung.class, EtntDotVerbindung.class, BssEtntVerbindung.class, AwbKommssVerbindung.class
                        }
                }, s("Objekttyp") + " " + s("text_kommuniziert") + " " + s("Anwendungsbaustein"))
        });

        /* Objekttyp - KonAnwendungsbaustein */
        put(new MetaPath[] {
                new MetaPath(Objekttyp.class, KonAnwendungsbaustein.class, new Class[][] {
                    {
                            ObjLogspVerbindung.class, KawbDoksVerbindung.class
                    }
                }, s("Objekttyp") + " " + s("text_fuehrenden") + " " + s("Anwendungsbaustein")), new MetaPath(Objekttyp.class, KonAnwendungsbaustein.class, new Class[][] {
                    {
                            ObjReprVerbindung.class, DoksDokVerbindung.class, KawbDoksVerbindung.class
                    }
                }, s("Objekttyp") + " " + s("text_speichert") + " " + s("Anwendungsbaustein")), new MetaPath(Objekttyp.class, KonAnwendungsbaustein.class, new Class[][] {
                        {
                                ObjReprVerbindung.class, EtntNatVerbindung.class, BssEtntVerbindung.class, AwbKommssVerbindung.class
                        }, {
                                ObjReprVerbindung.class, EtntDotVerbindung.class, BssEtntVerbindung.class, AwbKommssVerbindung.class
                        }
                }, s("Objekttyp") + " " + s("text_kommuniziert") + " " + s("Anwendungsbaustein"))
        });

        /* Anwendungsbaustein - Anwendungsbaustein */
        put(new MetaPath[] {
                new MetaPath(Anwendungsbaustein.class, Anwendungsbaustein.class, new Class[][] {
                        {
                            RawbRawbVerbindung.class
                        }, {
                            AwbKawbVerbindung.class
                        }
                }, s("zeile") + " " + s("text_teil_von") + " " + s("spalte"), true), new MetaPath(Anwendungsbaustein.class, Anwendungsbaustein.class, new Class[][] {
                    {
                            AwbKommssVerbindung.class, KommBeziehung.class, AwbKommssVerbindung.class
                    }
                }, new Color[] {
                        Color.ORANGE, Color.BLUE, Color.GREEN
                }, new String[] {
                        s("zeile") + " " + s("text_empfaengt_sendet") + " " + s("spalte"), s("zeile") + " " + s("text_sendet") + " " + s("spalte"), s("zeile") + " " + s("text_empfaengt") + " " + s("spalte"),
                }, 1, true),
        });

        /* RechAnwendungsbaustein - RechAnwendungsbaustein */
        put(new MetaPath[] {
                new MetaPath(RechAnwendungsbaustein.class, RechAnwendungsbaustein.class, new Class[][] {
                    {
                        RawbRawbVerbindung.class
                    }
                }, s("zeile") + " " + s("text_teil_von") + " " + s("spalte"), true), new MetaPath(RechAnwendungsbaustein.class, RechAnwendungsbaustein.class, new Class[][] {
                    {
                            AwbKommssVerbindung.class, KommBeziehung.class, AwbKommssVerbindung.class
                    }
                }, new Color[] {
                        Color.ORANGE, Color.BLUE, Color.GREEN
                }, new String[] {
                        s("zeile") + " " + s("text_empfaengt_sendet") + " " + s("spalte"), s("zeile") + " " + s("text_sendet") + " " + s("spalte"), s("zeile") + " " + s("text_empfaengt") + " " + s("spalte"),
                }, 1, true)
        });

        /* KonAnwendungsbaustein - Anwendungsbaustein */
        put(new MetaPath[] {
                new MetaPath(KonAnwendungsbaustein.class, Anwendungsbaustein.class, new Class[][] {
                    {
                        AwbKawbVerbindung.class
                    }
                }, s("zeile") + " " + s("text_teil_von") + " " + s("spalte"), true), new MetaPath(KonAnwendungsbaustein.class, Anwendungsbaustein.class, new Class[][] {
                    {
                            AwbKommssVerbindung.class, KommBeziehung.class, AwbKommssVerbindung.class
                    }
                }, new Color[] {
                        Color.ORANGE, Color.BLUE, Color.GREEN
                }, new String[] {
                        s("zeile") + " " + s("text_empfaengt_sendet") + " " + s("spalte"), s("zeile") + " " + s("text_sendet") + " " + s("spalte"), s("zeile") + " " + s("text_empfaengt") + " " + s("spalte"),
                }, 1, true)
        });

        /* KonAnwendungsbaustein - KonAnwendungsbaustein */
        put(new MetaPath[] {
                new MetaPath(KonAnwendungsbaustein.class, KonAnwendungsbaustein.class, new Class[][] {
                    {
                        AwbKawbVerbindung.class
                    }
                }, s("zeile") + " " + s("text_teil_von") + " " + s("spalte"), true), new MetaPath(KonAnwendungsbaustein.class, KonAnwendungsbaustein.class, new Class[][] {
                    {
                            AwbKommssVerbindung.class, KommBeziehung.class, AwbKommssVerbindung.class
                    }
                }, new Color[] {
                        Color.ORANGE, Color.BLUE, Color.GREEN
                }, new String[] {
                        s("zeile") + " " + s("text_empfaengt_sendet") + " " + s("spalte"), s("zeile") + " " + s("text_sendet") + " " + s("spalte"), s("zeile") + " " + s("text_empfaengt") + " " + s("spalte"),
                }, 1, true)
        });

        /* RechAnwendungsbaustein - KonAnwendungsbaustein */
        put(new MetaPath[] {
            new MetaPath(RechAnwendungsbaustein.class, KonAnwendungsbaustein.class, new Class[][] {
                {
                        AwbKommssVerbindung.class, KommBeziehung.class, AwbKommssVerbindung.class
                }
            }, new Color[] {
                    Color.ORANGE, Color.BLUE, Color.GREEN
            }, new String[] {
                    s("zeile") + " " + s("text_empfaengt_sendet") + " " + s("spalte"), s("zeile") + " " + s("text_sendet") + " " + s("spalte"), s("zeile") + " " + s("text_empfaengt") + " " + s("spalte"),
            }, 1, true)
        });

        initElementTypesInPathes();
    }

    /**
     * Bildet das Set aller Klassen, für die Pfade definiert wurden.
     */
    private static void initElementTypesInPathes() {
        for (MetaPath[] mps : metaPathes.values()) {
            MetaPath path = mps[0];
            elementClassesInPathes.add(path.getStartClass());
            elementClassesInPathes.add(path.getEndClass());
        }
    }

    /**
     * @return
     */
    public static final Set<Class<? extends ModelElement>> getElementClassesInPathes() {
        return elementClassesInPathes;
    }

    /**
     * @param resourceString
     * @return
     */
    private static final String s(final String resourceString) {
        return Tool3lgmConstants.getResString(resourceString);
    }
}