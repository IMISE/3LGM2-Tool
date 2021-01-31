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
    public default ImageIcon getIcon(final String name) {
        Class<?> resourcePackageSource = getResourcePackageSource();
        String iconFileName = getResourceFileName(resourcePackageSource == null ? getClass() : resourcePackageSource, name);
        return SimpleResourceIconSource.getImageIcon(iconFileName);
    }

    /**
     * @return
     */
    public default Class<?> getResourcePackageSource() {
        return null;
    }

    /**
     * Return the ImageIcon from the path of the package of the given class.
     *
     * @param resourcePackageSource
     * @param name
     * @return
     */
    public static ImageIcon getIcon(final Class<?> resourcePackageSource, final String name) {
        SimpleResourceFileLoader simpleResourceSource = new SimpleResourceIconSourceAdapter();
        String iconFileName = simpleResourceSource.getResourceFileName(resourcePackageSource, name);
        return getImageIcon(iconFileName);
    }

    /**
     * Versucht ein {@link ImageIcon} aus dem spezifizierten Verzeichnis zu
     * laden und es wiederzugeben
     *
     * @param dir Verzeichnis des tatsächlichen Bilds
     * @return
     */
    public static ImageIcon getImageIcon(final String dir) {
        ImageIcon imageIcon = getImageIcon(dir, ".gif");
        if (imageIcon == null) {
            imageIcon = getImageIcon(dir, ".jpg");
        }
        if (imageIcon == null) {
            imageIcon = getImageIcon(dir, ".jpeg");
        }
        if (imageIcon == null) {
            imageIcon = getImageIcon(dir, ".png");
        }
        return imageIcon;
    }

    /**
     * Versucht ein {@link ImageIcon} aus dem spezifizierten Verzeichnis zu
     * laden und es wiederzugeben
     *
     * @param dir Verzeichnis des tatsächlichen Bilds
     * @param extension Erweiterung des tatsächlichen Bilds
     * @return
     */
    public static ImageIcon getImageIcon(final String dir, final String extension) {
        ClassLoader systemClassLoader = ClassLoader.getSystemClassLoader();
        URL url = systemClassLoader.getResource(dir);
        if (url == null && !dir.endsWith(extension)) {
            url = systemClassLoader.getResource(dir + extension);
        }
        ImageIcon icon;
        if (url != null) {
            icon = new ImageIcon(url);
        } else {
            icon = new ImageIcon(dir);
        }
        if (icon.getIconWidth() == -1 && icon.getIconHeight() == -1) {
            if (!dir.endsWith(extension)) {
                String nameWithGifEnding = dir + extension;
                icon = new ImageIcon(nameWithGifEnding);
            }
            if (icon.getIconWidth() == -1 && icon.getIconHeight() == -1) {
                return null;
            }
        }
        return icon;
    }

}