package de.imise.util.resource;

import java.net.URL;

import javax.swing.ImageIcon;

/**
 * @author AXS (17.06.2020)
 */
public interface SimpleResourceIconSource extends SimpleResourceFileLoader {

    /**
     * Gibt das spezifizierte ImageIcon zurück.
     *
     * @param name
     * @return ImageIcon
     */
    public ImageIcon getIcon(String name);

    /**
     * Return the ImageIcon from the path of the package of the given class.
     *
     * @param resourcePackageSource
     * @param name
     * @return
     */
    public static ImageIcon getIcon(final Class<?> resourcePackageSource, final String name) {
        SimpleResourceFileLoader simpleResourceSource = new SimpleResourceIconHandler();
        String iconFileName = simpleResourceSource.getResourceFileName(resourcePackageSource, name);
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