package de.imise.tool3lgm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.imise.tool3lgm.graphtools.metamodel.elements.Bendpoint;
import de.imise.tool3lgm.graphtools.metamodel.elements.Edge;
import de.imise.tool3lgm.graphtools.metamodel.elements.ModelElement;
import de.imise.tool3lgm.graphtools.metamodel.elements.Node;
import de.imise.tool3lgm.graphtools.model.GDCollection;
import de.imise.tool3lgm.graphtools.model.GraphDocument;
import de.imise.tool3lgm.graphtools.model.Szenario;
import de.imise.tool3lgm.graphtools.view.container.ElementContainer;
import de.imise.tool3lgm.graphtools.view.container.LayerContainer;
import de.imise.util.Alphabetical;
import de.imise.util.collections.CollectionUtils;

public class PrintModelStatistics {

    /**
     * @param gdc
     *            {@link GDCollection} to print or <code>null</code> if all {@link GDCollection}s should be printed
     */
    public static final void printStatistic(final List<GDCollection> collections, final boolean useElements) {
        for (GDCollection gdcoll : collections) {
            GraphDocument mainDoc = gdcoll.getMainDoc();

            List<ElementContainer> allContainer = useElements ? null : mainDoc.getElementContainers(ModelElement.class, true);
            List<ModelElement> allElements = useElements ? mainDoc.getModelItems(ModelElement.class, true, true) : null;

            Map<Class<? extends ModelElement>, Integer> class2ElementCount = new HashMap<>();
            Map<Class<? extends ModelElement>, Integer> class2ContainerCountFromGraphDocuments = new HashMap<>();
            Map<Class<? extends ModelElement>, Integer> class2ContainerCountFromModelElements = new HashMap<>();
            //für alle ElementContainer im MainDoc = alle, die es gibt!
            for (Object ecOrMe : useElements ? allElements : allContainer) {
                //Anzahl der Modellelemente im Gesamtmodell hochzählen
                ModelElement me = useElements ? (ModelElement) ecOrMe : ((ElementContainer) ecOrMe).getElement();
                Class<? extends ModelElement> meClass = me.getClass();

                Integer count = class2ElementCount.get(meClass);
                count = count == null ? 1 : count.intValue() + 1;
                class2ElementCount.put(meClass, count);

                List<GraphDocument> docs = new ArrayList<>(gdcoll.getSzenarioCount() + 1);
                for (Szenario szen : gdcoll.getSzenarios()) {
                    docs.add(szen);
                }
                docs.add(mainDoc);

                //Anzahl der ElementContainer der Modellelemente im Gesamtmodell hochzählen
                for (GraphDocument doc : docs) {
                    for (LayerContainer lcc : doc.getLayers()) {
                        Iterable<ElementContainer> elementContainers = CollectionUtils.getCommonIterable(lcc.getBendpointContainers(), lcc.getNodeContainersAlphabetical(), lcc.getEdgeContainers());
                        for (ElementContainer layerEc : elementContainers) {
                            if (layerEc.getElement() == me) {
                                count = class2ContainerCountFromGraphDocuments.get(meClass);
                                count = count == null ? 1 : count.intValue() + 1;
                                class2ContainerCountFromGraphDocuments.put(meClass, count);
                            }
                        }
                    }
                }
                count = class2ContainerCountFromModelElements.get(meClass);
                count = count == null ? me.getContainerCount() : count.intValue() + me.getContainerCount();
                class2ContainerCountFromModelElements.put(meClass, count);
            }

            int nodeCount = 0, nodeContFromDoc = 0, nodeContFromMe = 0;
            int edgeCount = 0, edgeContFromDoc = 0, edgeContFromMe = 0;
            int bendCount = 0, bendContFromDoc = 0, bendContFromMe = 0;

            System.err.println("Modellstatistik: " + gdcoll.getName());
            System.err.println("-------------------------------------");
            List<Class<? extends ModelElement>> classList = new ArrayList<>(class2ElementCount.keySet());
            Alphabetical.sort(classList);
            //für jede Elementklasse
            for (Class<? extends ModelElement> elementClass : classList) {
                //              Integer integer = class2ElementCount.get(elementClass);
                //              int count = integer == null ? 0 : integer.intValue();
                //              integer = class2ContainerCountFromGraphDocuments.get(elementClass);
                //              int contFromDoc = integer == null ? 0 : integer.intValue();
                //              integer = class2ContainerCountFromModelElements.get(elementClass);
                //              int contFromMe = integer == null ? 0 : integer.intValue();

                int count = class2ElementCount.get(elementClass).intValue();
                int contFromDoc = class2ContainerCountFromGraphDocuments.get(elementClass).intValue();
                int contFromMe = class2ContainerCountFromModelElements.get(elementClass).intValue();

                System.err.print(elementClass.getName() + ": " + count + " " + contFromDoc + " " + contFromMe);
                if (contFromMe != contFromDoc) {
                    System.err.println(" <----------");
                } else {
                    System.err.println();
                }
                if (Bendpoint.class.isAssignableFrom(elementClass)) {
                    bendCount += count;
                    bendContFromDoc += contFromDoc;
                    bendContFromMe += contFromMe;
                } else if (Node.class.isAssignableFrom(elementClass)) {
                    nodeCount += count;
                    nodeContFromDoc += contFromDoc;
                    nodeContFromMe += contFromMe;
                } else if (Edge.class.isAssignableFrom(elementClass)) {
                    edgeCount += count;
                    edgeContFromDoc += contFromDoc;
                    edgeContFromMe += contFromMe;
                }
            }
            System.err.println("Node      " + nodeCount + " " + nodeContFromDoc + " " + nodeContFromMe);
            System.err.println("Kanten      " + edgeCount + " " + edgeContFromDoc + " " + edgeContFromMe);
            System.err.println("Knickpunkte " + bendCount + " " + bendContFromDoc + " " + bendContFromMe);

            System.err.println();
        }
    }

}
