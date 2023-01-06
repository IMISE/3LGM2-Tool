package de.imise.util.image;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
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
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.filechooser.FileSystemView;

import org.apache.batik.dom.GenericDOMImplementation;
import org.apache.batik.svggen.SVGGraphics2D;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;

import de.imise.util.MemoryHandler;
import de.imise.util.StringUtils;
import de.imise.util.pair.Pair;
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
     * {@link FileFilterType#toString()}. For the list of accepted extensions,
     * the same key-string is formed and the
     * {@link FILE_FILTER_RESOURCE_EXTENSION_POSTFIX} is appended. Furthermore,
     * the names of these enum entries correspond exactly to the names ImageIO
     * wants to have for identifying the codec to be used.
     */
    public static enum FileFilterType {
        JPG,
        TIFF,
        BMP,
        PNG,
        SVG
    }

    /**
     * All Infos of an image that was created by this class
     */
    public static class ImageInfo {

        public int width = -1;
        public int heigth = -1;
        public File file;
        public FileFilterType type;

        public ImageInfo() {
        }

        public ImageInfo(int width, int heigth, File file, FileFilterType type) {
            this.width = width;
            this.heigth = heigth;
            this.file = file;
            this.type = type;
        }

        @Override
        public String toString() {
            return "ImageInfo [width=" + width + ", heigth=" + heigth + ", file=" + file + ", type=" + type + "]";
        }

    }

    /**
     * Anfang des ResourceString, mit dem bei jedem über die Funktion zu
     * ladenden FileFilter der Key-String der Beschreibung und der
     * Dateierweiterungen beginnen muss.
     */
    public static final String FILE_FILTER_RESOURCE_PREFIX = "FILE_FILTER_";

    /** Ende des Key-Strings für die Dateiertweiterungen eines FileFilters */
    public static final String FILE_FILTER_RESOURCE_EXTENSION_POSTFIX = "_EXT";

    /** Ressourcenhandler */
    private static final DialogResourceHandler drh = new DialogResourceHandler(ComponentAsImageExportHandler.class);

    /**
     * Liefert für die übergebenen filterNamen ein Array von FileFiltern, wenn
     * die Beschreibung und die Liste der Erweiterungen den Konventionen
     * entsprechend in den Resoourcendateien abgelegt sind.
     *
     * @param filterNames
     * @return
     */
    private static final FileNameExtensionFilter[] getFileNameExtensionFilters(final FileFilterType... filterNames) {
        FileNameExtensionFilter[] returnFilter = new FileNameExtensionFilter[filterNames.length];
        for (int i = 0; i < filterNames.length; i++) {
            String fileFilterResourceKey = FILE_FILTER_RESOURCE_PREFIX + filterNames[i];
            returnFilter[i] = new FileNameExtensionFilter(drh.getResString(fileFilterResourceKey), StringUtils.tokenize(drh.getResString(fileFilterResourceKey + FILE_FILTER_RESOURCE_EXTENSION_POSTFIX), " ", false));
        }
        return returnFilter;
    }

    /**
     * @param comp
     * @param fileName
     * @return the crated imgage file or <code>null</code> if an error has
     *         occured
     */
    public static final ImageInfo createFile(final JComponent comp, final String fileName) {
        return createFile(comp, fileName, FileFilterType.JPG);
    }

    /**
     * @param comp
     * @param fileType
     * @param filename
     * @return the crated imgage file or <code>null</code> if an error has
     *         occured
     */
    public static final ImageInfo createFile(final JComponent comp, final String filename, final FileFilterType fileType) {
        return createFileInternal(comp, filename, fileType, false, null);
    }

    /**
     * @param comp
     * @param filename
     * @param fileType
     * @param maximizeSize
     * @return the crated imgage file or <code>null</code> if an error has
     *         occured
     */
    public static final ImageInfo createFile(final JComponent comp, final String filename, final FileFilterType fileType, final boolean maximizeSize, final Double zoomScale) {
        return createFileInternal(comp, filename, fileType, maximizeSize, zoomScale);
    }

    /**
     * @param comp
     * @param maximizeSize
     * @param zoomScale
     * @return original zoom
     */
    private static double setNewZoom(final JComponent comp, boolean maximizeSize, final Double zoomScale) {
        // not zoomable?
        if (!(comp instanceof ZoomableComponent)) {
            return -1d;
        }
        // nothing to zoom?
        if (!maximizeSize && (zoomScale == null || zoomScale == 1d)) {
            return -1d;
        }
        ZoomableComponent zoomComp = (ZoomableComponent) comp;
        // store original zoom
        double originalZoom = zoomComp.getZoom();
        // zoom to max sizeß?
        if (maximizeSize) {
            // set full zoom
            zoomComp.setZoomToMaximum();
        } else if (zoomScale != null) {
            zoomComp.setZoom(zoomScale);
        }
        setHeapAvailableMaximumExportSize(comp);
        return originalZoom;
    }

    /**
     * @param comp
     * @param filename
     * @param fileType
     * @param maximizeSize
     * @param zoomScale
     * @return the crated imgage file or <code>null</code> if an error has
     *         occured
     */
    private static final ImageInfo createFileInternal(final JComponent comp, final String filename, final FileFilterType fileType, boolean maximizeSize, final Double zoomScale) {
        double originalZoom = setNewZoom(comp, maximizeSize, zoomScale);
        File createdFile = null;
        int width = -1;
        int height = -1;
        try {
            if (fileType == FileFilterType.SVG) {
                Dimension imageSize = exportAsSVG(comp, filename);
                width = imageSize.width;
                height = imageSize.height;
            } else {
                BufferedImage image = createImage(comp);
                createdFile = new File(filename);
                String exportFileType = fileType.name();
                createdFile.getParentFile().mkdirs();
                ImageIO.write(image, exportFileType, createdFile);
                width = image.getWidth();
                height = image.getHeight();
            }
        } catch (Exception e) {
            // ignore
            // Component parent = ParentComponentFinder.getFrameOrDialog(comp);
            // JOptionPane.showMessageDialog(parent, drh.getResString("ERROR_MESSAGE"), drh.getResString("ERROR_TITLE"), JOptionPane.ERROR_MESSAGE);
        }
        // reset zoom to original value
        // if comp is not a zoomable component then originalZoom is -1
        if (originalZoom >= 0d) {
            ((ZoomableComponent) comp).setZoom(originalZoom);
        }
        return new ImageInfo(width, height, createdFile, fileType);
    }

    /**
     * @param image
     * @param type
     * @return
     */
    public static byte[] toByteArray(BufferedImage image, String type) {
        try {
            try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
                ImageIO.write(image, type, out);
                return out.toByteArray();
            }
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * @param comp
     * @param maximizeSize
     * @param zoomScale
     * @return
     */
    public static BufferedImage createZoomedImage(final JComponent comp, boolean maximizeSize, final Double zoomScale) {
        double originalZoom = setNewZoom(comp, maximizeSize, zoomScale);
        BufferedImage image = createImage(comp);
        // reset zoom to original value
        // if comp is not a zoomable component then originalZoom is -1
        if (originalZoom >= 0d) {
            ((ZoomableComponent) comp).setZoom(originalZoom);
        }
        return image;
    }

    /**
     * @param comp
     * @return
     */
    public static BufferedImage createImage(final JComponent comp) {
        Dimension preferredSize = comp.getPreferredSize();
        //MemoryHandler.printMaxNowAvailableMemory();
        //this here is the critical memory operation
        BufferedImage buffer = new BufferedImage(preferredSize.width, preferredSize.height, BufferedImage.TYPE_3BYTE_BGR);
        //MemoryHandler.printMaxNowAvailableMemory();

        Graphics og = buffer.getGraphics();
        og.setColor(new Color(255, 255, 255, 255));
        og.fillRect(0, 0, preferredSize.width, preferredSize.height);
        comp.printAll(og);

        return buffer;
    }

    /**
     * @param preferredSize
     * @return a scaled size of the given size in the same aspect ratio that
     *         will fit in memory when exporting an image of that size
     */
    private static Dimension getMaxHeapAvailableSize(final Dimension preferredSize) {
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
     * Sets the maximum available size for the component so that the image to be
     * exported fits in memory and the export process can be executed without
     * memory overflow.
     *
     * @param comp
     */
    private static void setHeapAvailableMaximumExportSize(final JComponent comp) {
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
     * Calculates the size of the image in the memory. The size results from the
     * width multiplied by the height multiplied by 3, since each pixel of a
     * {@link BufferedImage#TYPE_3BYTE_BGR} consumes 3 bytes per pixel.
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
    private static final Dimension exportAsSVG(final JComponent comp, final String fileName) throws IOException {
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
        return preferredSize;
    }

    /**
     * @param comp
     * @param file
     * @param fileFormat
     * @return Pair containing the destination of the exported file and the file
     *         type
     */
    public static final Pair<File, FileFilterType> createFile(final JComponent comp, final File file, final FileFilterType fileFormat) {
        return createFileInternal(comp, file, fileFormat);
    }

    /**
     * @param comp
     * @param file
     * @param fileFormat
     * @return Pair containing the destination of the exported file and the file
     *         type
     */
    private static final Pair<File, FileFilterType> createFileInternal(final JComponent comp, final File file, final FileFilterType fileFormat) {
        ExtendedFileChooser fc = new ExtendedFileChooser(ComponentAsImageExportHandler.class, file);
        fc.setFileSystemView(FileSystemView.getFileSystemView());
        fc.setAcceptAllFileFilterUsed(false);

        JRadioButton normalSizeRBut = null;
        JRadioButton mediumSizeRBut = null;
        JRadioButton highSizeRBut = null;
        JRadioButton saveMaximumSizeRBut = null;
        if (comp instanceof ZoomableComponent) {
            JPanel sizeOptionPanel = new JPanel(new BorderLayout());
            sizeOptionPanel.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

            normalSizeRBut = new JRadioButton(drh.getResString("RADIO_BUTTON_NORMAL_SIZE"));
            mediumSizeRBut = new JRadioButton(drh.getResString("RADIO_BUTTON_MEDIUM_SIZE"));
            highSizeRBut = new JRadioButton(drh.getResString("RADIO_BUTTON_HIGH_SIZE"));

            JRadioButton saveOriginalSizeRBut = new JRadioButton(drh.getResString("RADIO_BUTTON_ORIGINAL_SIZE"));
            saveMaximumSizeRBut = new JRadioButton(drh.getResString("RADIO_BUTTON_MAXIMUM_SIZE"));
            ButtonGroup buttonGroup = new ButtonGroup();
            buttonGroup.add(saveOriginalSizeRBut);
            buttonGroup.add(normalSizeRBut);
            buttonGroup.add(mediumSizeRBut);
            buttonGroup.add(highSizeRBut);
            buttonGroup.add(saveMaximumSizeRBut);

            JLabel label = new JLabel("<html>" + drh.getResString("TITLE_RESOLUTION") + "</html>");
            JPanel buttonPanel = new JPanel(new GridLayout(6, 1));
            buttonPanel.add(label);
            buttonPanel.add(saveOriginalSizeRBut);
            buttonPanel.add(normalSizeRBut);
            buttonPanel.add(mediumSizeRBut);
            buttonPanel.add(highSizeRBut);
            buttonPanel.add(saveMaximumSizeRBut);
            saveOriginalSizeRBut.setSelected(true);

            sizeOptionPanel.add(buttonPanel, BorderLayout.NORTH);
            fc.setAccessory(sizeOptionPanel);
            label.setMaximumSize(new Dimension(buttonPanel.getPreferredSize().width, Integer.MAX_VALUE));
            label.setPreferredSize(new Dimension(label.getMaximumSize().width, label.getPreferredSize().height));
        }

        fc.setMultiSelectionEnabled(false);

        FileNameExtensionFilter[] fileFilters = getFileNameExtensionFilters(FileFilterType.values());
        FileNameExtensionFilter[] lastSelectedFileNameExtensionFilter = getFileNameExtensionFilters(fileFormat);
        FileNameExtensionFilter selectedFileFilter = lastSelectedFileNameExtensionFilter.length > 0 ? lastSelectedFileNameExtensionFilter[0] : null;
        fc.showSaveDialog(comp, drh.getResString("DIALOG_TITLE"), false, selectedFileFilter, fileFilters);

        boolean maximizeImage = saveMaximumSizeRBut != null && saveMaximumSizeRBut.isSelected();
        FileFilterType type = null;
        FileFilter choosedFileFilter = fc.getFileFilter();
        for (int c = 0; c < fileFilters.length; c++) {
            if (choosedFileFilter == fileFilters[c]) {
                type = FileFilterType.values()[c];
                break;
            }
        }
        if (type == null) {
            type = fileFormat;
        }

        File f = fc.getSelectedFile();
        Pair<File, FileFilterType> fileAndType = new Pair<>(f, type);
        if (f == null) {
            return fileAndType;
        }

        //in case a zoomscale is selected
        Double zoomScale = null;
        if (!maximizeImage) {
            if (normalSizeRBut.isSelected()) {
                zoomScale = 1.0;
            } else if (mediumSizeRBut.isSelected()) {
                zoomScale = 2.0;
            } else if (highSizeRBut.isSelected()) {
                zoomScale = 5.0;
            }
        }

        createFile(comp, f.getPath(), type, maximizeImage, zoomScale);
        return fileAndType;
    }

    /**
     * For subclasses of this class, you can select whether the component should
     * be exported with maximum zoom when exporting.
     *
     * @author AXS
     * @create 20.02.2013
     */
    public static interface ZoomableComponent {

        /**
         * Sets the maximum zoom.
         *
         * @return the old zoom value
         */
        public abstract double setZoomToMaximum();

        /**
         * Sets the zoom according to the passed value
         *
         * @param zoom
         * @return the old zoom value
         */
        public abstract double setZoom(double zoom);

        /**
         * @return the current zoom value
         */
        public abstract double getZoom();

    }

}
