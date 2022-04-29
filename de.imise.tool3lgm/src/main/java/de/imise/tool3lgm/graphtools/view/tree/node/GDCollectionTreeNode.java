package de.imise.tool3lgm.graphtools.view.tree.node;

import javax.swing.ImageIcon;

import de.imise.tool3lgm.Tool3lgmConstants;
import de.imise.tool3lgm.graphtools.model.GDCollection;

/**
 * @author AXS (28.04.2022)
 */
public class GDCollectionTreeNode extends IconifiedTreeNode<GDCollection> {

    /**
     * @param gdcoll
     */
    public GDCollectionTreeNode(GDCollection gdcoll) {
        this(gdcoll, true);
    }

    /**
     * @param gdcoll
     * @param icon
     */
    public GDCollectionTreeNode(GDCollection gdcoll, boolean defaultIcon) {
        this(gdcoll, defaultIcon ? Tool3lgmConstants.TOOL_ICON_TRANSPARENT_16 : null);
    }

    /**
     * @param gdcoll
     * @param icon
     */
    public GDCollectionTreeNode(GDCollection gdcoll, ImageIcon icon) {
        super(gdcoll, getNameWithoutFileExtension(gdcoll), false, icon);
    }

    /**
     * @param gdcoll
     * @return
     */
    private static String getNameWithoutFileExtension(GDCollection gdcoll) {
        String name = gdcoll.getName();
        int lastIndexOfPoint = name.lastIndexOf('.');
        if (lastIndexOfPoint > 0) {
            name = name.substring(0, lastIndexOfPoint);
        }
        return name;
    }

}
