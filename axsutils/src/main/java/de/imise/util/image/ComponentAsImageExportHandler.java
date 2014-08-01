package de.imise.util.image;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.RandomAccessFile;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.filechooser.FileSystemView;

import com.sun.media.jai.codec.BMPEncodeParam;
import com.sun.media.jai.codec.ImageCodec;
import com.sun.media.jai.codec.ImageEncodeParam;
import com.sun.media.jai.codec.ImageEncoder;
import com.sun.media.jai.codec.JPEGEncodeParam;
import com.sun.media.jai.codec.PNGEncodeParam;
import com.sun.media.jai.codec.SeekableOutputStream;
import com.sun.media.jai.codec.TIFFEncodeParam;
import de.imise.util.StringUtils;
import de.imise.util.swing.dialog.DialogResourceHandler;
import de.imise.util.swing.dialog.ExtendedFileChooser;

/**
 * @author AXS
 * @create 07.07.2012
 */
public class ComponentAsImageExportHandler {

	/**
	 * Mögliche Typen der FileFilter, die dieser Dialog anzeigen kann. Für alle diese Typen gibt es 
	 * Ressourcen-Strings, deren Key für die Beschreibung sich über den zusammengebausten String aus
	 * {@link FILE_FILTER_RESOURCE_PREFIX} + {@link FileFilterType#toString()} ergbibt.
	 * Für die Liste der akzeptierten Erweiterungen wird der gleiche Key-String gebildet und noch der
	 * {@link FILE_FILTER_RESOURCE_EXTENSION_POSTFIX} angehängt.
	 */
	public static enum FileFilterType {JPEG, TIFF, BMP, PNG}
	
	/** 
	 * Anfang des ResourceString, mit dem bei jedem über die Funktion zu ladenden FileFilter
	 * der Key-String der Beschreibung und der Dateierweiterungen beginnen muss.
	 */
	public static final String FILE_FILTER_RESOURCE_PREFIX = "FILE_FILTER_";
	
	/** Ende des Key-Strings für die Dateiertweiterungen eines FileFilters */
	public static final String FILE_FILTER_RESOURCE_EXTENSION_POSTFIX = "_EXT";
	
	/** Ressourcenhandler */
	private DialogResourceHandler drh = new DialogResourceHandler(ComponentAsImageExportHandler.class);
	
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
	private final FileNameExtensionFilter[] getFileNameExtensionFilters(FileFilterType... filterNames) {
		FileNameExtensionFilter[] returnFilter = new FileNameExtensionFilter[filterNames.length];
		for (int i = 0; i < filterNames.length; i++)
			returnFilter[i] = new FileNameExtensionFilter(drh.getString(FILE_FILTER_RESOURCE_PREFIX + filterNames[i]), StringUtils.tokenize(drh.getString(FILE_FILTER_RESOURCE_PREFIX + filterNames[i] + FILE_FILTER_RESOURCE_EXTENSION_POSTFIX), " ", false));
		return returnFilter;
	}

	/**
	 * @param comp
	 * @param fileName
	 */
	public static final void createFile(JComponent comp, String fileName) {
		createFile(comp, FileFilterType.JPEG, fileName);
	}

	/**
	 * @param comp
	 * @param fileFormat
	 * @param filename
	 */
	public static final void createFile(JComponent comp, FileFilterType fileFormat, String filename) {
		new ComponentAsImageExportHandler().createFileInternal(comp, fileFormat, filename, false);
	}
	
	/**
	 * 
	 * COMMENTME
	 * @param comp
	 * @param fileFormat
	 * @param filename
	 * @param maximizeSize
	 */
	public static final void createFile(JComponent comp, FileFilterType fileFormat, String filename, boolean maximizeSize) {
		new ComponentAsImageExportHandler().createFileInternal(comp, fileFormat, filename, maximizeSize);
	}

	/**
	 * @param comp
	 * @param fileFormat
	 * @param filename
	 * @param maximizeSize
	 */
	private final void createFileInternal(JComponent comp, FileFilterType fileFormat, String filename, boolean maximizeSize) {
		if (fileFormat == null)
			return;
		//wenn das Bild mit maximaler Größe gespeichert werden soll und das Bild auch maximierbar ist
		maximizeSize = comp instanceof ZoomableComponent && maximizeSize;
		double zoom = 0d;
		Dimension size = null;
		//maximale Bildgröße speichern?
		if (maximizeSize) {
			//vollen Zoom setzen und alten zoom merken
			zoom = ((ZoomableComponent)comp).setZoomToMaximum();
			size = comp.getSize();
			comp.setSize(comp.getPreferredSize());
		}
		int w = (new Double(comp.getPreferredSize().getWidth())).intValue();
		int h = (new Double(comp.getPreferredSize().getHeight())).intValue();

		BufferedImage buffer = new BufferedImage(w, h, BufferedImage.TYPE_3BYTE_BGR);
		Graphics og = buffer.getGraphics();
		ImageEncoder encoder;
		ImageEncodeParam param;
		og.setColor(new Color(255, 255, 255, 255));
		og.fillRect(0, 0, w, h);
		comp.printAll(og);
		//ggf. Zoom auf alten Wert zurück setzen
		if (maximizeSize) {
			((ZoomableComponent)comp).setZoom(zoom);
			comp.setSize(size);
		}
		
		try {
			RandomAccessFile raf = new RandomAccessFile(filename, "rw");
			SeekableOutputStream os = new SeekableOutputStream(raf);
			switch (fileFormat) {
			case JPEG:
				param = new JPEGEncodeParam();
				((JPEGEncodeParam) param).setQuality(1);
				encoder = ImageCodec.createImageEncoder("JPEG", os, param);
				break;
			case TIFF:
				param = new TIFFEncodeParam();
				((TIFFEncodeParam) param).setCompression(TIFFEncodeParam.COMPRESSION_PACKBITS);
				encoder = ImageCodec.createImageEncoder("TIFF", os, param);
				break;
			case BMP:
				param = new BMPEncodeParam();
				((BMPEncodeParam) param).setCompressed(true);
				encoder = ImageCodec.createImageEncoder("BMP", os, param);
				break;
			case PNG:
				param = new PNGEncodeParam.RGB();
				encoder = ImageCodec.createImageEncoder("PNG", os, param);
				break;
			default:
				os.close();
				return;
			}
			encoder.encode(buffer);
			os.close();
		} catch (Exception e) {
			JOptionPane.showMessageDialog(getFrameOrDialog(comp), drh.getString("ERROR_MESSAGE"), drh.getString("ERROR_TITLE"), JOptionPane.ERROR_MESSAGE);
		} catch (Error err) {
			JOptionPane.showMessageDialog(getFrameOrDialog(comp), drh.getString("ERROR_MESSAGE"), drh.getString("ERROR_TITLE"), JOptionPane.ERROR_MESSAGE);
		}
	}

	/**
	 * 
	 * @param comp
	 */
	private final void createFileInternal(JComponent comp) {
		ExtendedFileChooser fc = new ExtendedFileChooser(ComponentAsImageExportHandler.class);
		fc.setFileSystemView(FileSystemView.getFileSystemView());
		fc.setAcceptAllFileFilterUsed(false);
		
		JRadioButton saveMaximumSizeRBut = null;
		if (comp instanceof ZoomableComponent) {
			JPanel sizeOptionPanel = new JPanel(new BorderLayout());
			sizeOptionPanel.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
			JLabel label = new JLabel("<html>" + drh.getString("MESSAGE_OUT_OF_MEMORY") + "</html>");
			sizeOptionPanel.add(label, BorderLayout.CENTER);

			JRadioButton saveOriginalSizeRBut = new JRadioButton(drh.getString("RADIO_BUTTON_ORIGINAL_SIZE"));
			saveMaximumSizeRBut = new JRadioButton(drh.getString("RADIO_BUTTON_MAXIMUM_SIZE"));
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
		fc.showSaveDialog(comp, drh.getString("DIALOG_TITLE"), false, fileFilters);
		
		boolean maximizeImage = saveMaximumSizeRBut != null && saveMaximumSizeRBut.isSelected();
		FileFilterType type = null;
		for (int c = 0; c < fileFilters.length; c++)
			if (fc.getFileFilter() == fileFilters[c]) {
				type = FileFilterType.values()[c];
				break;
			}
		File f = fc.getSelectedFile();
		if (f == null)
			return;
		
		createFile(comp, type, f.getPath(), maximizeImage);
	}
	
	/**
	 * @param comp
	 */
	public static final void createFile(JComponent comp) {
		new ComponentAsImageExportHandler().createFileInternal(comp);
	}

	/**
	 * Liefert den Dialog oder das Fenster, der die übergebene Komponente enthält
	 * @param comp
	 * @return
	 */
	public static final Component getFrameOrDialog(Component comp) {
		Component parent = comp.getParent();
		while (parent != null) {
			if (parent instanceof JDialog || parent instanceof JFrame)
				return parent;
			parent = parent.getParent();
		}
		return null;
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
		 * @return
		 */
		public abstract double setZoomToMaximum();
		
		/**
		 * Setzt den Zoom entsprechend dem übergebenen Wert
		 * @param zoom
		 * @return
		 */
		public abstract void setZoom(double zoom);
	}
	
}
