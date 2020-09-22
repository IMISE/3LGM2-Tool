package de.imise.util.image;

import static de.imise.util.swing.component.ParentComponentFinder.getFrameOrDialog;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.image.BufferedImage;
import java.io.File;
import java.math.BigDecimal;

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

import de.imise.util.StringUtils;
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
        PNG
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
     *
     */
    private ComponentAsImageExportHandler() {
        super();
    }

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
        //wenn das Bild mit maximaler Größe gespeichert werden soll und das Bild auch maximierbar ist
        maximizeSize = comp instanceof ZoomableComponent && maximizeSize;
        double zoom = 0d;
        Dimension size = null;
        //maximale Bildgröße speichern?
        if (maximizeSize) {
            //vollen Zoom setzen und alten zoom merken
            zoom = ((ZoomableComponent) comp).setZoomToMaximum();
            size = comp.getSize();
            comp.setSize(comp.getPreferredSize());
        }
        Dimension preferredSize = comp.getPreferredSize();

        BigDecimal tempSize = getTempHeapSize(preferredSize);
        // skaliert das Bild runter, sodass die später berechnete Größe nicht den Integer Wert überschreitet (Negative Array Length error)
        while (tempSize.compareTo(BigDecimal.valueOf(Integer.MAX_VALUE)) == 1) {
            preferredSize = downscaleSize(comp, preferredSize, Integer.MAX_VALUE);
            tempSize = getTempHeapSize(preferredSize);
        }

        // da die Runtime.getRuntime().freeMemory(), sehr unzuverlässig ist, wird einfach etwas weniger als das Maximum des verfügbaren Speichers genommen
        // und anschließend noch halbiert, da es später beim Speichern auch nochmal eingespeichert werden musss
        long freeHeapSpace = (Runtime.getRuntime().maxMemory() - 100000000) / 2;
        // skaliert das Bild runter, sodass es in den Heap passt
        if (tempSize.longValue() > freeHeapSpace) {
            preferredSize = downscaleSize(comp, preferredSize, freeHeapSpace);
        }

        BufferedImage buffer = new BufferedImage(preferredSize.width, preferredSize.height, BufferedImage.TYPE_3BYTE_BGR);
        Graphics og = buffer.getGraphics();
        og.setColor(new Color(255, 255, 255, 255));
        og.fillRect(0, 0, preferredSize.width, preferredSize.height);
        comp.printAll(og);
        //ggf. Zoom auf alten Wert zurück setzen
        if (maximizeSize) {
            ((ZoomableComponent) comp).setZoom(zoom);
            comp.setSize(size);
        }

        File saveFile = new File(filename);
        String exportFileType = fileFormat.name();
        try {
            ImageIO.write(buffer, exportFileType, saveFile);
        } catch (Throwable e) {
            JOptionPane.showMessageDialog(getFrameOrDialog(comp), drh.getResString("ERROR_MESSAGE"), drh.getResString("ERROR_TITLE"), JOptionPane.ERROR_MESSAGE);
        }

    }

    /**
     * diese Berechnung wird durchgeführt, da für das BufferedImage mittels der Breite und der Höhe
     * eine "size" berechnet wird und diese anschließend in ein Byte Array eingespeist wird.
     * Daher sollte die "size" den maximalen Integer Wert und den offenen Heapspace nicht überschreiten.
     * folgende Formel ist in der Klasse Raster.java zu finden und wirt mit folgenden Eingaben aufgerufen:
     * BufferedImage.java:
     * raster = Raster.createInterleavedRaster(DataBuffer.TYPE_BYTE, width, height, width*3, 3, bOffs, null);
     * Raster.java:
     * createInterleavedRaster(int dataType, int w, int h, int scanlineStride, int pixelStride, int[] bandOffsets, Point location)
     * int size = scanlineStride * (h - 1) + pixelStride * w;
     *
     * @param preferredSize
     * @return
     */
    private static BigDecimal getTempHeapSize(final Dimension preferredSize) {
        int w = preferredSize.width;
        int h = preferredSize.height;
        BigDecimal tempSize = BigDecimal.valueOf(w);
        BigDecimal bigThree = BigDecimal.valueOf(3);
        tempSize = tempSize.multiply(bigThree);
        BigDecimal hMinus1 = BigDecimal.valueOf(h - 1);
        tempSize = tempSize.multiply(hMinus1);
        BigDecimal wMult3 = BigDecimal.valueOf(3 * w);
        tempSize = tempSize.add(wMult3);
        return tempSize;
    }

    /**
     * skaliert das bild runter, indem der Zoom angepasst wird
     * hierzu wird eine Berechnung durchgeführt, aus folgender Formel hergeleitet wurde:
     * size = scanlineStride * (h - 1) + pixelStride * w;
     * => size = width * 3 * (h - 1) + 3 * width
     * da das Verhältnis von Breite zu Höhe bekannt ist kann man durch das tempRatio die Höhe ersetzen
     * tempRatio = width / height
     * => size = width * 3 * (width / tempRatio - 1) + 3 * width
     * => size = 3 * width^2 / tempRatio - 3 * width + 3 * width
     * => size = 3 * width^2 / tempRatio
     * da die maximale Größe schon vorher bekannt ist (maximaler Integer Wert oder verfügbarer Heap Space)
     * => width = sqrt( size * tempRation / 3 )
     *
     * @param comp
     * @param preferredSize
     * @param freeSpace
     * @return
     */
    private final Dimension downscaleSize(final JComponent comp, final Dimension preferredSize, final long freeSpace) {
        double tempWidth = preferredSize.width;
        double tempHeight = preferredSize.height;
        double tempRatio = tempWidth / tempHeight;
        double maxWidth = Math.sqrt(freeSpace * tempRatio / 3);
        double zoomRatio = maxWidth / tempWidth;
        double scaleZoom = ((ZoomableComponent) comp).getZoom() * zoomRatio;
        ((ZoomableComponent) comp).setZoom(scaleZoom);
        return comp.getPreferredSize();
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
