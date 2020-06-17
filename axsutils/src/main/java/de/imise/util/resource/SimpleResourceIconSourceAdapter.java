package de.imise.util.resource;

import javax.swing.ImageIcon;

/**
 * @author AXS (19.05.2020)
 */
public class SimpleResourceIconSourceAdapter implements SimpleResourceIconSource {

    /**
     *
     */
    private final Class<?> resourcePackageSource;

    /**
     *
     */
    public SimpleResourceIconSourceAdapter() {
        this(null);
    }

    /**
     * @param resourcePackageSource
     */
    public SimpleResourceIconSourceAdapter(final Class<?> resourcePackageSource) {
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
