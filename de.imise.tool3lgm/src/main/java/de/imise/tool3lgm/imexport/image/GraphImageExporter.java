package de.imise.tool3lgm.imexport.image;

import java.awt.Dimension;
import java.util.ArrayList;
import java.util.List;

import de.imise.tool3lgm.graphtools.metamodel.ModelConstants;
import de.imise.tool3lgm.graphtools.model.Szenario;
import de.imise.tool3lgm.graphtools.view.container.LayerContainer;
import de.imise.tool3lgm.graphtools.view.graph.BasicGraphArea;
import de.imise.tool3lgm.graphtools.view.graph.BasicGraphArea.PaintState;
import de.imise.util.image.ComponentAsImageExportHandler;
import de.imise.util.image.ComponentAsImageExportHandler.ImageInfo;

/**
 * Extracted and expanded from WebExportDialog.
 *
 * @author AXS (06.01.2023)
 */
public class GraphImageExporter {

    /**
     * Adjust the view to good looking values :)<br>
     * In case of a single layer angle and gap are 0, in case of multi layer
     * view angle is 45 degree and the gap is 400 times of the pageSizeFactor of
     * the Szenario.
     *
     * @param area
     * @param layer
     */
    private static void adjustView(BasicGraphArea area, int layer) {
        if (layer < 0) {
            Szenario szen = area.getSzenario();
            double pageSizeFactor = szen.getPageSizeFactor();
            Double layerGap = 400d * pageSizeFactor;
            area.setLayerGap(layerGap.intValue());
            area.setLayerAngle(45);
            area.setMultiView(true);
        } else {
            area.setLayerAngle(0);
            area.setLayerGap(0);
            area.setMultiView(false);
        }
    }

    /**
     * Exports layer of {@link Szenario} to jpg-File
     *
     * @param szen
     * @param filename String with jpg-File
     * @param width of the result image
     * @param layer -1 for 3layerView; 0 for physical layer; 2 for logical
     *            layer; 4 for domain layer
     */
    public static final ImageInfo createImage(Szenario szen, final String filename, final int width, final int layer) {
        BasicGraphArea area = new BasicGraphArea(szen);
        return createImage(area, filename, width, layer);
    }

    /**
     * Exports layer of {@link Szenario} to jpg-File
     *
     * @param area
     * @param filename String with jpg-File
     * @param width of the result image
     * @param layer -1 for 3layerView; 0 for physical layer; 2 for logical
     *            layer; 4 for domain layer
     * @return
     */
    public static final ImageInfo createImage(BasicGraphArea area, final String filename, final int width, final int layer) {
        adjustView(area, layer);
        area.setZoom(1d);
        Dimension preferredSize = area.getPreferredSize();
        double zoomFactor = (double) width / preferredSize.width;
        return createImage(area, filename, zoomFactor, layer, false);
    }

    /**
     * Exports layer of {@link Szenario} to jpg-File
     *
     * @param szen
     * @param filename String with jpg-File
     * @param zoomFactor 0 < x < 1
     * @param layer -1 for 3layerView; 0 for physical layer; 2 for logical
     *            layer; 4 for domain layer
     */
    public static final ImageInfo createImage(Szenario szen, final String filename, final double zoomFactor, final int layer) {
        BasicGraphArea area = new BasicGraphArea(szen);
        return createImage(area, filename, zoomFactor, layer);
    }

    /**
     * Exports layer of {@link Szenario} to jpg-File
     *
     * @param area
     * @param filename String with jpg-File
     * @param zoomFactor 0 < x < 1
     * @param layer -1 for 3layerView; 0 for physical layer; 2 for logical
     *            layer; 4 for domain layer
     * @return
     */
    public static final ImageInfo createImage(BasicGraphArea area, final String filename, final double zoomFactor, final int layer) {
        return createImage(area, filename, zoomFactor, layer, true);
    }

    /**
     * Exports layer of {@link Szenario} to jpg-File
     *
     * @param area
     * @param filename String with jpg-File
     * @param zoomFactor 0 < x < 1
     * @param layer -1 for 3layerView; 0 for physical layer; 2 for logical
     *            layer; 4 for domain layer
     * @param adjustView if <code>true</code> the the view values of the area
     *            will be changed to default values. In case of a single layer
     *            angle and gap are 0, in case of multi layer view angle is 45
     *            degree and the gap is 400 times of the pageSizeFactor of the
     *            Szenario.
     * @return the {@link ImageInfo} of the created image file
     */
    private static final ImageInfo createImage(BasicGraphArea area, final String filename, final double zoomFactor, final int layer, boolean adjustView) {
        if (adjustView) {
            adjustView(area, layer);
        }
        area.setZoom(zoomFactor);
        area.setSize(area.getPreferredSize());
        area.setPaintState(PaintState.SAVE_IMAGE_AS_FILE);
        ImageInfo imageInfo = ComponentAsImageExportHandler.createFile(area, filename);
        area.setPaintState(PaintState.REGULAR);
        return imageInfo;
    }

    /**
     * Exports layer of {@link Szenario} to jpg-File with the given file name.
     * If there is only one layer with elements then the image will show only
     * this one layer. If multiple layers have elements then a 3 layer view is
     * generated. If there are no elements on the layer then <code>null</code>
     * is returned. The zoom is fix to 1.0
     *
     * @param szen
     * @param filename
     * @param width width of the result image
     * @return a model image or <code>null</code>
     */
    public static final ImageInfo createImageBestFit(final Szenario szen, final String filename, int width) {
        List<Integer> layersWithElements = new ArrayList<>(3);
        for (int l : ModelConstants.VISIBLE_LAYERS) {
            LayerContainer lc = szen.getLayer(l);
            if (lc.getNodeContainerCount() > 0) {
                layersWithElements.add(l);
            }
        }
        if (layersWithElements.isEmpty()) {
            return new ImageInfo(); // default with a null file
        }
        int layer = layersWithElements.size() == 1 ? layersWithElements.get(0) : ModelConstants.NO_LAYER;
        return createImage(szen, filename, width, layer);
    }

}
