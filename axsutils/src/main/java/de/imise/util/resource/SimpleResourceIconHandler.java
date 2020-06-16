package de.imise.util.resource;

import java.net.URL;

import javax.swing.ImageIcon;

/**
 * @author AXS (19.05.2020)
 */
public class SimpleResourceIconHandler implements SimpleResourceSource {

    /**
     *
     */
    private final Class<?> resourcePackageSource;

    /**
     *
     */
    public SimpleResourceIconHandler() {
        this(null);
    }

    /**
     * @param resourcePackageSource
     */
    public SimpleResourceIconHandler(final Class<?> resourcePackageSource) {
        this.resourcePackageSource = resourcePackageSource;
    }

    }

    /**
     * Gibt das spezifizierte ImageIcon aus dem Standard-Iconpfad zurück.
     *
     * @param resourcePackageSource
     * @param name
     * @return
     */
    public static ImageIcon getIcon(final Class<?> resourcePackageSource, final String name) {
        SimpleResourceSource simpleResourceSource = new SimpleResourceIconHandler();
        String iconFileName = simpleResourceSource.getResourceFileName(resourcePackageSource, name);
        return getImageIcon(iconFileName);
    }

    /**
     * Gibt das spezifizierte ImageIcon aus dem Standard-Iconpfad zurück.
     *
     * @param name
     * @return ImageIcon
     */
    public ImageIcon getIcon(final String name) {
        String iconFileName = getResourceFileName(resourcePackageSource == null ? getClass() : resourcePackageSource, name);
        return getImageIcon(iconFileName);
    }

    /**
     * Versucht ein {@link ImageIcon} aus dem spezifizierten Verzeichnis zu laden und es wiederzugeben
     *
     * @param dir
     *            Verzeichnis des tatsächlichen Bilds
     * @return
     */
    public static ImageIcon getImageIcon(final String dir) {
        URL url = ClassLoader.getSystemClassLoader().getResource(dir);
        if (url == null && !dir.endsWith(".gif")) {
            url = ClassLoader.getSystemClassLoader().getResource(dir + ".gif");
        }
        ImageIcon icon;
        if (url != null) {
            icon = new ImageIcon(url);
        } else {
            icon = new ImageIcon(dir);
        }
        if (icon.getIconWidth() == -1 && icon.getIconHeight() == -1) {
            if (!dir.endsWith(".gif")) {
                String nameWithGifEnding = dir + ".gif";
                icon = new ImageIcon(nameWithGifEnding);
            }
            if (icon.getIconWidth() == -1 && icon.getIconHeight() == -1) {
                return null;
            }
        }
        return icon;
    }

}
