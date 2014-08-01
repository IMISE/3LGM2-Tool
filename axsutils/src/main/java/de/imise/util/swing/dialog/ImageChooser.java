package de.imise.util.swing.dialog;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.Insets;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.filechooser.FileSystemView;

import de.imise.util.StringUtils;

/**
 * @author Thomas Rudert
 *
 * Dialog zum Auswählen/Öffnen eines Bitmaps mit Bildvorschau
 */
public class ImageChooser extends ExtendedFileChooser {

	/**
	 * Mögliche Typen der FileFilter, die dieser Dialog anzeigen kann. Für alle diese Typen gibt es 
	 * Ressourcen-Strings, deren Key für die Beschreibung sich über den zusammengebausten String aus
	 * {@link FILE_FILTER_RESOURCE_PREFIX} + {@link FileFilterType#toString()} ergbibt.
	 * Für die Liste der akzeptierten Erweiterungen wird der gleiche Key-String gebildet und noch der
	 * {@link FILE_FILTER_RESOURCE_EXTENSION_POSTFIX} angehängt.
	 */
	public static enum FileFilterType {IMAGE, JPEG, GIF, PNG}
	
	/** 
	 * Anfang des ResourceString, mit dem bei jedem über die Funktion zu ladenden FileFilter
	 * der Key-String der Beschreibung und der Dateierweiterungen beginnen muss.
	 */
	public static final String FILE_FILTER_RESOURCE_PREFIX = "FILE_FILTER_";
	
	/** Ende des Key-Strings für die Dateiertweiterungen eines FileFilters */
	public static final String FILE_FILTER_RESOURCE_EXTENSION_POSTFIX = "_EXT";
	
	/** Ressourcenhandler */
	private DialogResourceHandler drh = new DialogResourceHandler(ImageChooser.class);
	
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
	 * Ein Label auf dem ein skaliertes Vorschaubild eines aktuell über diesen 
	 * Dialog ausgewählten Bildes angezeigt werden kann.
	 */
	private ImagePreviewer previewer = new ImagePreviewer();
	
	/**
	 * Panel, das den {@link ImagePreviewer} enthält
	 */
	private PreviewPanel previewPanel = new PreviewPanel();	
	
	/**
	 * @param pathKey
	 */
	public ImageChooser(Object pathKey) {
		super(pathKey);
		setFileSystemView(FileSystemView.getFileSystemView());
		setFileSelectionMode(FILES_ONLY);
		setFileFilters(false, getFileNameExtensionFilters(FileFilterType.values()));
		
		this.setAccessory(previewPanel);
		this.addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(final ComponentEvent e) {
				super.componentResized(e);
				previewer.resize();
			}
		});
		this.addPropertyChangeListener(new PropertyChangeListener() {
			@Override
			public void propertyChange(final PropertyChangeEvent e) {
				if (e.getPropertyName().equals(SELECTED_FILE_CHANGED_PROPERTY)) {
					File f = (File)e.getNewValue();
					if (f != null && f.isFile())
						previewer.configure(f);
					else
						previewer.configure(null);
				}
			}
		});
	}

	/**
	 * 
	 */
	private class ImagePreviewer extends JLabel {
		
		/**
		 * COMMENTME
		 */
		private ImageIcon orginal;
		
		/**
		 * @param f
		 */
		public void configure(File f) {
					
			// neues Bild in voller Größe
			try {
				orginal = f == null  ||  ! f.isFile() ? null : new ImageIcon(f.getPath());
				resize();
			} catch (Exception e) {
			}
		}
		
		/**
		 * 
		 */
		public void resize() {
			if (orginal == null) {
				setIcon(null);
				return;
			}
			Dimension size = getSize();
			Insets insets = getInsets();
			
			double width = size.width - insets.left - insets.right;
			double height = size.height - insets.top - insets.bottom;
			double scale;
			
			// Bild ist zu gross
			if (orginal.getIconWidth() > width || orginal.getIconHeight() > height) {
				if ((orginal.getIconWidth() / width) > (orginal.getIconHeight() / height))
					scale = width / orginal.getIconWidth();
				else
					scale = height / orginal.getIconHeight();
				
				setIcon(new ImageIcon(orginal.getImage().getScaledInstance((int)(scale * orginal.getIconWidth()), (int)(scale * orginal.getIconHeight()), Image.SCALE_SMOOTH)));
			} else
				setIcon(orginal);
		}
	}
	
	/**
	 * 
	 */
	private class PreviewPanel extends JPanel {
		
		/**
		 * 
		 */
		public PreviewPanel() {
			JLabel label = new JLabel(new DialogResourceHandler(ImageChooser.class).getString("LABEL_PREVIEW"), SwingConstants.CENTER);
			
			setPreferredSize(new Dimension(150, 0));
			setBorder(BorderFactory.createEtchedBorder());
			
			setLayout(new BorderLayout());
			label.setBorder(BorderFactory.createEtchedBorder());
			add(label, BorderLayout.NORTH);
			add(previewer, BorderLayout.CENTER);
		}
	}
}
