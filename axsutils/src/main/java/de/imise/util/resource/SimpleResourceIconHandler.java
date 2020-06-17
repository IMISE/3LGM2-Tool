package de.imise.util.resource;

import javax.swing.ImageIcon;

/**
 * @author AXS (19.05.2020)
 */
public class SimpleResourceIconHandler implements SimpleResourceIconSource {

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

    /**
     * Gibt das spezifizierte ImageIcon aus dem Standard-Iconpfad zurück.
     *
     * @param name
     * @return ImageIcon
     */
    @Override
    public ImageIcon getIcon(final String name) {
        String iconFileName = getResourceFileName(resourcePackageSource == null ? getClass() : resourcePackageSource, name);
        return SimpleResourceIconSource.getImageIcon(iconFileName);
    }

}
