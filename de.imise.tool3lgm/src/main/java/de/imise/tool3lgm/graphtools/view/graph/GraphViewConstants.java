package de.imise.tool3lgm.graphtools.view.graph;

import java.util.Arrays;
import java.util.HashSet;

import de.imise.tool3lgm.graphtools.elements.ModelElement;
import de.imise.tool3lgm.graphtools.elements.node.ABKonfiguration;
import de.imise.tool3lgm.graphtools.elements.node.Anwendungsprogramm;
import de.imise.tool3lgm.graphtools.elements.node.AufOrgKombination;
import de.imise.tool3lgm.graphtools.elements.node.Bausteintyp;
import de.imise.tool3lgm.graphtools.elements.node.DBKonfiguration;
import de.imise.tool3lgm.graphtools.elements.node.DBVerwaltungssystem;
import de.imise.tool3lgm.graphtools.elements.node.Datensatztyp;
import de.imise.tool3lgm.graphtools.elements.node.Dokumententyp;
import de.imise.tool3lgm.graphtools.elements.node.EreignisDokumentenTyp;
import de.imise.tool3lgm.graphtools.elements.node.EreignisNachrichtenTyp;
import de.imise.tool3lgm.graphtools.elements.node.Ereignistyp;
import de.imise.tool3lgm.graphtools.elements.node.Kommunikationsprozess;
import de.imise.tool3lgm.graphtools.elements.node.Kommunikationsstandard;
import de.imise.tool3lgm.graphtools.elements.node.Nachrichtentyp;
import de.imise.tool3lgm.graphtools.elements.node.Netzprotokoll;
import de.imise.tool3lgm.graphtools.elements.node.Netztyp;
import de.imise.tool3lgm.graphtools.elements.node.Organisationseinheit;
import de.imise.tool3lgm.graphtools.elements.node.Organisationsplan;
import de.imise.tool3lgm.graphtools.elements.node.Prozess;
import de.imise.tool3lgm.graphtools.elements.node.Softwareprodukt;
import de.imise.tool3lgm.graphtools.elements.node.Standort;
import de.imise.tool3lgm.graphtools.elements.node.Subnetz;

/**
 * @author AXS
 */
public class GraphViewConstants {
	
	/** Klassen aller Knoten, die nicht in der Grafik dargestellt werden */
	private static final HashSet<Class<? extends ModelElement>> UNPAINTABLE_NODES = new HashSet<Class<? extends ModelElement>>();
	static {
		@SuppressWarnings("rawtypes")
		Class[] unpaintableNodes = {
			ABKonfiguration.class,
			Anwendungsprogramm.class,
			AufOrgKombination.class,
			Bausteintyp.class,
			Datensatztyp.class,
			DBKonfiguration.class,
			DBVerwaltungssystem.class,
			Dokumententyp.class,
			EreignisDokumentenTyp.class,
			EreignisNachrichtenTyp.class,
			Ereignistyp.class,
			Kommunikationsprozess.class,
			Kommunikationsstandard.class,
			Nachrichtentyp.class,
			Netzprotokoll.class,
			Netztyp.class,
			Organisationseinheit.class,
			Organisationsplan.class,
			Prozess.class,
			Softwareprodukt.class,
			Standort.class,
			Subnetz.class,
		};
		@SuppressWarnings({ "rawtypes", "unchecked" })
		HashSet<Class<? extends ModelElement>> tmp_set = new HashSet(Arrays.asList(unpaintableNodes));
		UNPAINTABLE_NODES.addAll(tmp_set);
	}

	/**
	 * Liefert <code>true</code>, wenn die Elementklasse nicht in der Grafik dargestellt wird.
	 * @param elementClass
	 * @return
	 */
	public static final boolean isUnpaintable(Class<?> elementClass) {
		return UNPAINTABLE_NODES.contains(elementClass);
	}
	
	/**
	 * Liefert die Anzahl der nicht dargestellten instanziierbaren Unterklassen von {@link ModelElement}
	 * @return
	 */
	public static final int getUnpaintableCount(){
		return UNPAINTABLE_NODES.size();
	}
	

	
	
	
}
