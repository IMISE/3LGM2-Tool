package de.imise.util.image;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.filechooser.FileSystemView;

import org.apache.batik.dom.GenericDOMImplementation;
import org.apache.batik.svggen.SVGGraphics2D;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;

import de.imise.util.MemoryHandler;
import de.imise.util.StringUtils;
import de.imise.util.swing.component.ParentComponentFinder;
import de.imise.util.swing.dialog.DialogResourceHandler;
import de.imise.util.swing.dialog.ExtendedFileChooser;

/**
 * @author AXS
 * @create 07.07.2012
 */
public class ComponentAsImageExportHandler {

    /**
     * Possible types of FileFilters this dialog can display. For all these
     * types there are resource strings, whose key for the description results
     * from the assembled string {@link FILE_FILTER_RESOURCE_PREFIX} +
     * {@link FileFilterType#toString()}.
     * For the list of accepted extensions, the same key-string is formed and
     * the {@link FILE_FILTER_RESOURCE_EXTENSION_POSTFIX} is appended.
     * Furthermore, the names of these enum entries correspond exactly to the
     * names ImageIO wants to have for identifying the codec to be used.
     */
    public static enum FileFilterType {
        JPG,
        TIFF,
        BMP,
        PNG,
        SVG
    }

    /**
     * Anfang des ResourceString, mit dem bei jedem über die Funktion zu ladenden FileFilter
     * der Key-String der Beschreibung und der Dateierweiterungen beginnen muss.
     */
    public static final String FILE_FILTER_RESOURCE_PREFIX = "FILE_FILTER_";

    /** Ende des Key-Strings für die Dateiertweiterungen eines FileFilters */
    public static final String FILE_FILTER_RESOURCE_EXTENSION_POSTFIX = "_EXT";

    /** Ressourcenhandler */
    private final DialogResourceHandler drh = new DialogResourceHandler(ComponentAsImageExportHandler.class);

    /**
     * Liefert für die übergebenen filterNamen ein Array von FileFiltern, wenn die Beschreibung und die Liste
     * der Erweiterungen den Konventionen entsprechend in den Resoourcendateien abgelegt sind.
     *
     * @param filterNames
     * @return
     */
    private final FileNameExtensionFilter[] getFileNameExtensionFilters(final FileFilterType... filterNames) {
        FileNameExtensionFilter[] returnFilter = new FileNameExtensionFilter[filterNames.length];
        for (int i = 0; i < filterNames.length; i++) {
            returnFilter[i] = new FileNameExtensionFilter(drh.getResString(FILE_FILTER_RESOURCE_PREFIX + filterNames[i]),
                    StringUtils.tokenize(drh.getResString(FILE_FILTER_RESOURCE_PREFIX + filterNames[i] + FILE_FILTER_RESOURCE_EXTENSION_POSTFIX), " ", false));
        }
        return returnFilter;
    }

    /**
     * @param comp
     * @param fileName
     */
    public static final void createFile(final JComponent comp, final String fileName) {
        createFile(comp, FileFilterType.JPG, fileName);
    }

    /**
     * @param comp
     * @param fileFormat
     * @param filename
     */
    public static final void createFile(final JComponent comp, final FileFilterType fileFormat, final String filename) {
        new ComponentAsImageExportHandler().createFileInternal(comp, fileFormat, filename, false);
    }

    /**
     * COMMENTME
     *
     * @param comp
     * @param fileFormat
     * @param filename
     * @param maximizeSize
     */
    public static final void createFile(final JComponent comp, final FileFilterType fileFormat, final String filename, final boolean maximizeSize) {
        new ComponentAsImageExportHandler().createFileInternal(comp, fileFormat, filename, maximizeSize);
    }

    /**
     * @param comp
     * @param fileFormat
     * @param filename
     * @param maximizeSize
     */
    private final void createFileInternal(final JComponent comp, final FileFilterType fileFormat, final String filename, boolean maximizeSize) {
        if (fileFormat == null) {
            return;
        }

        ZoomableComponent zoomComp = comp instanceof ZoomableComponent ? (ZoomableComponent) comp : null;

        //wenn das Bild mit maximaler Größe gespeichert werden soll und das Bild auch maximierbar ist
        maximizeSize = zoomComp != null && maximizeSize;
        double originalZoom = zoomComp != null ? zoomComp.getZoom() : -1d;

        //maximale Bildgröße speichern?
        if (maximizeSize) {
            //vollen Zoom setzen und alten zoom merken
            zoomComp.setZoomToMaximum();
        }
        setHeapAvailableMaximumExportSize(comp);

        try {
            if (fileFormat == FileFilterType.SVG) {
                exportAsSVG(comp, filename);
            } else {
                Dimension preferredSize = comp.getPreferredSize();
                //MemoryHandler.printMaxNowAvailableMemory();
                //this here is the critical memory operation
                BufferedImage buffer = new BufferedImage(preferredSize.width, preferredSize.height, BufferedImage.TYPE_3BYTE_BGR);
                //MemoryHandler.printMaxNowAvailableMemory();

                Graphics og = buffer.getGraphics();
                og.setColor(new Color(255, 255, 255, 255));
                og.fillRect(0, 0, preferredSize.width, preferredSize.height);
                comp.printAll(og);

                File saveFile = new File(filename);
                String exportFileType = fileFormat.name();

                ImageIO.write(buffer, exportFileType, saveFile);
            }
        } catch (Exception e) {
            Component parent = ParentComponentFinder.getFrameOrDialog(comp);
            JOptionPane.showMessageDialog(parent, drh.getResString("ERROR_MESSAGE"), drh.getResString("ERROR_TITLE"), JOptionPane.ERROR_MESSAGE);
        }
        //ggf. Zoom auf alten Wert zurück setzen
        if (originalZoom >= 0d) {
            zoomComp.setZoom(originalZoom);
        }
    }

    /**
     * @param preferredSize
     * @return a scaled size of the given size in the same aspect ratio
     *         that will fit in memory when exporting an image of that
     *         size
     */
    private Dimension getMaxHeapAvailableSize(final Dimension preferredSize) {
        Dimension maxAvailableImageSize = preferredSize;
        //70% as memory buffer for other processes during export (tested with -Xmx768m -Xss64m)
        //lower buffer values than 61% will cause an OutOfMemory exception -> 9% buffer buffer :)
        long maxImageHeapSize = MemoryHandler.getMaxNowAvailableMemory(70);
        long imageHeapSize = getImageHeapSize(preferredSize);
        if (maxImageHeapSize < imageHeapSize) {
            long squareMax = Double.valueOf(Math.sqrt(maxImageHeapSize)).longValue(); //= max possible side length in a square (rounding dosn't matter here)
            long widthPlusHeightSquare = squareMax * 2; //= width + height from the square
            long width = preferredSize.width;
            long height = preferredSize.height;
            long widthPlusHeight = width + height;
            long realWidth = widthPlusHeightSquare * width / widthPlusHeight;
            long realHeight = widthPlusHeightSquare + height / widthPlusHeight;
            //this (width or height > Integer.MAX_VALUE) will probably never happen, but one thing is certain
            long maxInt = Integer.MAX_VALUE;
            if (realWidth > maxInt) {
                realHeight = realHeight * maxInt / realWidth;
                realWidth = maxInt;
            }
            if (realHeight > maxInt) {
                realWidth = realWidth * maxInt / realHeight;
                realHeight = maxInt;
            }
            //these long values are always < Integer.MAX_VALUE -> hard cast
            int w = (int) realWidth;
            int h = (int) realHeight;
            maxAvailableImageSize = new Dimension(w, h);
        }
        return maxAvailableImageSize;
    }

    /**
     * Sets the maximum available size for the component so that the
     * image to be exported fits in memory and the export process can
     * be executed without memory overflow.
     *
     * @param comp
     */
    private void setHeapAvailableMaximumExportSize(final JComponent comp) {
        comp.getPreferredSize();
        Dimension preferredSize = comp.getPreferredSize();
        Dimension availableSize = getMaxHeapAvailableSize(preferredSize);
        double preferredWidth = preferredSize.getWidth();
        double availableWidth = availableSize.getWidth();
        if (comp instanceof ZoomableComponent) {
            if (preferredWidth > availableWidth) {
                ZoomableComponent zComp = (ZoomableComponent) comp;
                double scaleFactor = availableWidth / preferredWidth;
                double zoom = zComp.getZoom();
                zoom *= scaleFactor;
                zComp.setZoom(zoom);
                preferredSize = comp.getPreferredSize();
            }
        } else if (preferredWidth > availableWidth) {
            preferredSize = availableSize;
            comp.setPreferredSize(preferredSize);
            comp.setSize(preferredSize);
        }
    }

    /**
     * Calculates the size of the image in the memory. The size results
     * from the width multiplied by the height multiplied by 3, since
     * each pixel of a {@link BufferedImage#TYPE_3BYTE_BGR} consumes 3
     * bytes per pixel.
     *
     * @param preferredSize
     * @return
     */
    private static long getImageHeapSize(final Dimension preferredImageSize) {
        long heapSize = preferredImageSize.width * preferredImageSize.height * 3l;
        return heapSize;
    }

    /**
     * @param comp
     * @param fileName
     */
    private final void exportAsSVG(final JComponent comp, final String fileName) throws IOException {
        Dimension preferredSize = comp.getPreferredSize();
        // Get a DOMImplementation.
        DOMImplementation domImpl = GenericDOMImplementation.getDOMImplementation();
        // String qualifiedName is not relevant for the export
        Document document = domImpl.createDocument(null, "TlgmSvgExport", null);
        SVGGraphics2D svgGenerator = new SVGGraphics2D(document);
        svgGenerator.setSVGCanvasSize(preferredSize);

        comp.print(svgGenerator);

        boolean useCSS = true; // we want to use CSS style attribute

        FileOutputStream outputStream = new FileOutputStream(fileName);
        Writer out = new OutputStreamWriter(outputStream, "UTF-8");
        svgGenerator.stream(out, useCSS);
        out.close();
    }

    /**
     * @param comp
     */
    private final void createFileInternal(final JComponent comp) {
        ExtendedFileChooser fc = new ExtendedFileChooser(ComponentAsImageExportHandler.class);
        fc.setFileSystemView(FileSystemView.getFileSystemView());
        fc.setAcceptAllFileFilterUsed(false);

        JRadioButton saveMaximumSizeRBut = null;
        if (comp instanceof ZoomableComponent) {
            JPanel sizeOptionPanel = new JPanel(new BorderLayout());
            sizeOptionPanel.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
            JLabel label = new JLabel("<html>" + drh.getResString("MESSAGE_OUT_OF_MEMORY") + "</html>");
            sizeOptionPanel.add(label, BorderLayout.CENTER);

            JRadioButton saveOriginalSizeRBut = new JRadioButton(drh.getResString("RADIO_BUTTON_ORIGINAL_SIZE"));
            saveMaximumSizeRBut = new JRadioButton(drh.getResString("RADIO_BUTTON_MAXIMUM_SIZE"));
            ButtonGroup buttonGroup = new ButtonGroup();
            buttonGroup.add(saveOriginalSizeRBut);
            buttonGroup.add(saveMaximumSizeRBut);

            JPanel buttonPanel = new JPanel(new GridLayout(2, 1));
            buttonPanel.add(saveOriginalSizeRBut);
            buttonPanel.add(saveMaximumSizeRBut);
            saveOriginalSizeRBut.setSelected(true);

            sizeOptionPanel.add(buttonPanel, BorderLayout.SOUTH);
            fc.setAccessory(sizeOptionPanel);
            label.setMaximumSize(new Dimension(buttonPanel.getPreferredSize().width, Integer.MAX_VALUE));
            label.setPreferredSize(new Dimension(label.getMaximumSize().width, label.getPreferredSize().height));
        }

        fc.setMultiSelectionEnabled(false);
        FileNameExtensionFilter[] fileFilters = getFileNameExtensionFilters(FileFilterType.values());
        fc.showSaveDialog(comp, drh.getResString("DIALOG_TITLE"), false, fileFilters);

        boolean maximizeImage = saveMaximumSizeRBut != null && saveMaximumSizeRBut.isSelected();
        FileFilterType type = null;
        for (int c = 0; c < fileFilters.length; c++) {
            if (fc.getFileFilter() == fileFilters[c]) {
                type = FileFilterType.values()[c];
                break;
            }
        }
        File f = fc.getSelectedFile();
        if (f == null) {
            return;
        }

        createFile(comp, type, f.getPath(), maximizeImage);
    }

    /**
     * @param comp
     */
    public static final void createFile(final JComponent comp) {
        new ComponentAsImageExportHandler().createFileInternal(comp);
    }

    /**
     * Bei Unterklassen dieser Klasse kann beim Export ausgewählt werden,
     * ob die Componente mit maximalem Zoom exportiert werden soll.
     *
     * @author astruebi
     * @create 20.02.2013
     */
    public static interface ZoomableComponent {

        /**
         * Setzt den Maximalen Zoom und gibt den alten Zoom-Wert zurück
         *
         * @return
         */
        public abstract double setZoomToMaximum();

        /**
         * Setzt den Zoom entsprechend dem übergebenen Wert
         *
         * @param zoom
         * @return
         */
        public abstract void setZoom(double zoom);

        /**
         * Liefert den aktuellen Zoom-Wert
         *
         * @return
         */
        public abstract double getZoom();

    }

}
