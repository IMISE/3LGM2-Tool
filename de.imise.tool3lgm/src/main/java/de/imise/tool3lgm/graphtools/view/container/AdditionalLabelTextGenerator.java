package de.imise.tool3lgm.graphtools.view.container;

import java.util.HashSet;
import java.util.List;

import javax.swing.SwingConstants;

import de.imise.tool3lgm.graphtools.metamodel.elements.ModelElement;
import de.imise.tool3lgm.graphtools.model.GraphDocument;
import de.imise.tool3lgm.graphtools.model.Szenario;
import de.imise.tool3lgm.graphtools.view.graph.GraphElementLayout;

/**
 * @author AXS
 */
public class AdditionalLabelTextGenerator {

    /**
     * Set aller {@link ElementContainer}, an die dieses Objekt etwas ranschreibt
     */
    private HashSet<ElementContainer> additionalTextTargets = new HashSet<>();

    /**
     * Liste der {@link ElementContainer}, an die dieses Objekt etwas ranschreibt / protected HashSet<ElementContainer> specialInfoTargets = null; /**
     * Layout mit dessen Farbe und Schrift die zusätzlichen Infos an Elemente geschrieben werden
     */
    protected GraphElementLayout layout;

    /**
     * Gibt an wo, die SpecialInfos hinzugefügt werden
     */
    protected int specialInfoPosition = SwingConstants.NORTH;

    /**
     * Falls ein anderes Objekt dieses Objekt nur als Stellvertreter nutzen will, kann man hier den echten Owner verpacken.
     */
    protected Object realOwner = null;

    /**
     * @param layout
     */
    public AdditionalLabelTextGenerator(final GraphElementLayout layout) {
        this(null, layout);
    }

    /**
     * @param realOwner
     * @param layout
     */
    public AdditionalLabelTextGenerator(final Object realOwner, final GraphElementLayout layout) {
        super();
        this.realOwner = realOwner == null ? this : realOwner;
        this.layout = layout;
    }

    /**
     * / private static final void removeAllAdditionalTextFromAllTargets(){ for (ElementContainer ec : additionalTextTargets)
     * ec.removeAllSpecialInfosFromThisContainer(); } /**
     *
     * @return the layout
     */
    public GraphElementLayout getLayout() {
        return layout;
    }

    /**
     * specialInfoDirection kann die 4 meoglichen Werte SwingConstants.NORTH, - EAST, -SOUTH und -WEST haben
     *
     * @param i
     */
    public void setSpecialInfoPosition(final int specialInfoPosition) {
        this.specialInfoPosition = specialInfoPosition;
    }

    //	/**
    //	 * Diese Funktion muesste für alle Node speziell ausgewertet werden, die SpecialInfos anzeigen sollen, oder
    //	 * in einem eigenen Container ueberschrieben werden.
    //	 */
    //	public boolean addSpecialInfoToMyTargets(boolean remove) {
    //		if (remove)
    //			deleteSpecialInfoFromMyTargets();
    //		return true;
    //	}

    //	/**
    //	 * Diese Funktion muesste für alle Node speziell ausgewertet werden, die SpecialInfos anzeigen sollen, oder
    //	 * in einem eigenen Container ueberschrieben werden.
    //	 */
    //	public boolean addSpecialInfoToMyTargets(boolean remove) {
    //		if (!super.addSpecialInfoToMyTargets(remove))
    //			return false;
    //		if ((me instanceof Prozess && isVisible()))
    //			writeNumberListToTargets(this, specialInfoTargets, layout, specialInfoPosition);
    //		return true;
    //	}

    /**
     * @param infoOwner
     * @param specialInfoTargets
     * @param layout
     */
    public void writeNumberListToTargets(final List<ModelElement> specialInfoTargets, final GraphDocument doc) {
        writeNumberListToTargets(specialInfoTargets, doc, SwingConstants.NORTH);
    }

    /**
     * @param infoOwner
     * @param specialInfoTargets
     * @param specialInfoPosition
     */
    private void writeNumberListToTargets(final List<ModelElement> specialInfoTargets, final GraphDocument doc, final int specialInfoPosition) {

        if (doc == null || !(doc instanceof Szenario)) {
            return;
        }

        for (int i = 0; i < specialInfoTargets.size(); i++) {
            ModelElement me = specialInfoTargets.get(i);
            ElementContainer ec = me.getContainer(doc);
            if (ec == null) {
                continue;
            }

            ec = ec.getElement().getContainer(doc);
            if (ec == null) {
                continue;
            }

            //im Prozess braucht jeder Container nur beim ersten auftauchen behandelt werden, denn
            //er bekommt mit einem mal gleich alle Nummern rangeschrieben
            boolean alreadyAdded = false;
            for (int j = 0; j < i; j++) {
                if (specialInfoTargets.get(j) == me) {
                    alreadyAdded = true;
                    break;
                }
            }
            if (alreadyAdded) {
                continue;
            }

            if (additionalTextTargets == null) {
                additionalTextTargets = new HashSet<>();
            }
            additionalTextTargets.add(ec);

            StringBuilder sb = new StringBuilder("[");
            sb.append(i + 1);
            for (int j = i + 1; j < specialInfoTargets.size(); j++) {
                if (specialInfoTargets.get(j) == me) {
                    sb.append(", ");
                    sb.append(j + 1);
                }
            }
            sb.append("]");

            ec.addSpecialInfoToThisContainer(this, sb.toString(), specialInfoPosition);
        }
    }

    /**
     * 
     */
    public void deleteSpecialInfoFromTargets() {
        for (ElementContainer target : additionalTextTargets) {
            if (target == null) {
                continue;
            }
            ModelElement me = target.getElement();
            for (ElementContainer ec : me.getElementContainers()) {
                ec.removeSpecialInfoFromThisContainer(this, specialInfoPosition);
            }
        }
    }

    //	/**
    //	 * @param target
    //	 */
    //	public void addSpecialInfoTarget(ElementContainer target) {
    //		addSpecialInfoTarget(specialInfoTargets.size(), target);
    //	}
    //
    //	/**
    //	 * @param index
    //	 * @param target
    //	 */
    //	public void addSpecialInfoTarget(int index, ElementContainer target) {
    //		deleteSpecialInfoFromMyTargets();
    //		if (index > specialInfoTargets.size())
    //			index = specialInfoTargets.size();
    //		specialInfoTargets.add(index, target);
    //		addSpecialInfoToMyTargets(false);
    //		AbstractInternalFrame frame = Tool3lgm.tool.getActiveFrame();
    //		if (frame != null)
    //			frame.repaint();
    //	}
    //
    //	/**
    //	 * @param index
    //	 */
    //	public void removeSpecialInfoTargetAt(int index) {
    //		deleteSpecialInfoFromMyTargets();
    //		if (index <= specialInfoTargets.size())
    //			specialInfoTargets.remove(index);
    //		addSpecialInfoToMyTargets(false);
    //		AbstractInternalFrame frame = Tool3lgm.tool.getActiveFrame();
    //		if (frame != null)
    //			frame.repaint();
    //	}
    //
    //	/**
    //	 * @return
    //	 */
    //	public ArrayList<ElementContainer> getSpecialInfoTargets() {
    //		return specialInfoTargets;
    //	}
    //
    //	/**
    //	 * @param newTargets
    //	 */
    //	public void setSpecialInfoTargets(ArrayList<ElementContainer> newTargets) {
    //		specialInfoTargets = newTargets;
    //	}
    //
    //	/**
    //	 * @param pos1
    //	 * @param pos2
    //	 */
    //	public void switchSpecialInfoTartgets(int pos1, int pos2) {
    //		if (specialInfoTargets != null) {
    //			deleteSpecialInfoFromMyTargets();
    //			ElementContainer dummy = specialInfoTargets.get(pos1);
    //			specialInfoTargets.set(pos1, specialInfoTargets.get(pos2));
    //			specialInfoTargets.set(pos2, dummy);
    //			addSpecialInfoToMyTargets(false);
    //			AbstractInternalFrame frame = Tool3lgm.tool.getActiveFrame();
    //			if (frame != null)
    //				frame.repaint();
    //		}
    //	}

}
