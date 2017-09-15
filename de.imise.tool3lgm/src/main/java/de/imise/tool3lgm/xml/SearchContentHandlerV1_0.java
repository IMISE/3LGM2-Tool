/*
 * Created on 06.02.2005
 */
package de.imise.tool3lgm.xml;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.xml.sax.Attributes;

import de.imise.tool3lgm.Tool3lgmConstants;
import de.imise.tool3lgm.graphtools.metamodel.Edge;
import de.imise.tool3lgm.graphtools.metamodel.ModelConstants;
import de.imise.tool3lgm.graphtools.metamodel.ModelElement;
import de.imise.tool3lgm.graphtools.model.GDCollection;
import de.imise.tool3lgm.log.Log;

/**
 * @author Thomas Wendt
 *         für Dateiversion mit Knickpunkten
 */
public class SearchContentHandlerV1_0 {

    protected GDCollection gdcoll = null;

    protected ArrayList<ArrayList<ModelElement>> path = new ArrayList<>(50);
    protected Map<String, ArrayList<ModelElement>> variables = new HashMap<>(10);
    protected ArrayList<ModelElement> roots = new ArrayList<>(5000);
    protected ArrayList<ModelElement> currentKnots = new ArrayList<>(5000);
    protected ArrayList<Edge> currentTraces = new ArrayList<>(5000);

    /**
     * @param coll
     */
    public SearchContentHandlerV1_0(final GDCollection coll) {
        gdcoll = coll;
    }

    public void startDocument() {
        for (ModelElement me : gdcoll.getMainGraphDocument().getSelectedElements()) {
            roots.add(me);
        }
        path.add(roots);
        currentKnots.addAll(roots);
    }

    public void endDocument() {
        roots.clear();
        path.clear();
        variables.clear();
        gdcoll = null;
    }

    public void startElement(final String namespaceURI, final String localName, final String qName, final Attributes atts) {
        if (qName.equals("select")) {
            String varName = atts.getValue("identifier");
            if (varName != null) {
                variables.put(varName, roots);
            }
        } else if (qName.equals("forward")) {
            String targetClass = atts.getValue("to");
            if (targetClass == null) {
                return;
            }
            Class<? extends ModelElement> elementClass = ModelConstants.getClassForName(targetClass);
            for (ModelElement me : path.get(path.size() - 1)) {
                currentKnots.addAll(me.getConnectedElements(elementClass));
                currentTraces.addAll(me.getEdgesWith(elementClass));
                if (currentKnots.size() != currentTraces.size()) {
                    Log.show(Log.ERROR, Tool3lgmConstants.getErrString("inkons_modelldaten_bei") + " " + me.getHashString() + " (" + me.getName() + ")");
                }
            }
        } else if (qName.equals("backward")) {
            String targetClass = atts.getValue("to");
            if (targetClass == null) {
                return;
            }
            Class<? extends ModelElement> elementClass = ModelConstants.getClassForName(targetClass);
            for (ModelElement me : path.get(path.size() - 1)) {
                currentKnots.addAll(me.getConnectedElements(elementClass));
                currentTraces.addAll(me.getEdgesWith(elementClass));
                if (currentKnots.size() != currentTraces.size()) {
                    Log.show(Log.ERROR, Tool3lgmConstants.getErrString("inkons_modelldaten_bei") + " " + me.getHashString() + " (" + me.getName() + ")");
                }
            }
        } else if (qName.equals("any")) {
        } else if (qName.equals("both")) {
        }
    }

    public void endElement(final String namespaceURI, final String localName, final String qName) {
        if (qName.equals("select")) {
        } else if (qName.equals("forward")) {
            path.add(new ArrayList<ModelElement>(currentTraces));
            path.add(new ArrayList<>(currentKnots));
            currentKnots.clear();
            currentTraces.clear();
        } else if (qName.equals("backward")) {
            path.add(new ArrayList<ModelElement>(currentTraces));
            path.add(new ArrayList<>(currentKnots));
            currentKnots.clear();
            currentTraces.clear();
        } else if (qName.equals("any")) {
            path.add(new ArrayList<ModelElement>(currentTraces));
            path.add(new ArrayList<>(currentKnots));
            currentKnots.clear();
            currentTraces.clear();
        } else if (qName.equals("both")) {
            path.add(new ArrayList<ModelElement>(currentTraces));
            path.add(new ArrayList<>(currentKnots));
            currentKnots.clear();
            currentTraces.clear();
        }
    }
}
