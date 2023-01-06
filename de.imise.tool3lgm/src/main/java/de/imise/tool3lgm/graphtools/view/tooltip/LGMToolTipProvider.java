package de.imise.tool3lgm.graphtools.view.tooltip;

import java.awt.Toolkit;
import java.io.File;
import java.net.MalformedURLException;

import javax.swing.JComponent;

import com.google.common.base.Strings;

import de.imise.tool3lgm.Tool3lgmConstants;
import de.imise.tool3lgm.graphtools.ElementsNameBuilder;
import de.imise.tool3lgm.graphtools.consistency.error.type.AbstractConsistencyError;
import de.imise.tool3lgm.graphtools.metamodel.MetaModel;
import de.imise.tool3lgm.graphtools.metamodel.elements.ModelElement;
import de.imise.tool3lgm.graphtools.model.GDCollection;
import de.imise.tool3lgm.graphtools.model.GraphDocument;
import de.imise.tool3lgm.graphtools.model.Szenario;
import de.imise.tool3lgm.graphtools.view.container.ElementContainer;
import de.imise.tool3lgm.graphtools.view.tree.node.LGMTreeNode;
import de.imise.tool3lgm.imexport.image.GraphImageExporter;
import de.imise.util.NamedObjectContainer;
import de.imise.util.ToolTipProvider;
import de.imise.util.htmlxml.HTMLConverter;
import de.imise.util.image.ComponentImageExporter.ImageInfo;

/**
 * A {@link ToolTipProvider} implementation that provides the tooltips for
 * ModelElements
 *
 * @author AXS (09.06.2020)
 */
public class LGMToolTipProvider implements ToolTipProvider {

    /**  */
    private static final String TOOLTIP_IMAGE_CACHE_FILE_NAME = new File(Tool3lgmConstants.CACHE_DIR, "tooltip_image.jpg").getAbsolutePath();

    /** Maximum width of tooltips in pixel for tooltips with text only. */
    private static final int MAX_TOOLTIP_WIDTH_TEXT_ONLY = Toolkit.getDefaultToolkit().getScreenSize().width / 4;

    /** Maximum width of tooltips in pixel for tooltips with images. */
    private static final int MAX_TOOLTIP_WIDTH_WITH_IMAGE = Toolkit.getDefaultToolkit().getScreenSize().width / 3;

    /**
     *
     */
    public LGMToolTipProvider() {
    }

    /**
     * Registers the component as target for this tooltip provider.
     *
     * @param target
     */
    public LGMToolTipProvider(final JComponent target) {
        addToolTipMouseListeners(target);
    }

    @Override
    public String getToolTip(Object o) {
        int maxToolTipWidth = MAX_TOOLTIP_WIDTH_TEXT_ONLY;
        if (o instanceof LGMTreeNode<?>) {
            LGMTreeNode<?> treeNode = (LGMTreeNode<?>) o;
            AbstractConsistencyError consistencyError = treeNode.getConsistencyError();
            if (consistencyError != null) {
                String treeNodeToolTip = consistencyError.getLongMessage();
                if (!Strings.isNullOrEmpty(treeNodeToolTip)) {
                    return treeNodeToolTip;
                }
            }
            o = treeNode.getUserObject();
        }
        if (o instanceof NamedObjectContainer) {
            NamedObjectContainer<?> noc = (NamedObjectContainer<?>) o;
            o = noc.getObject();
        }
        StringBuilder sb = new StringBuilder();
        int initialLength = sb.length();

        if (o instanceof ElementContainer) { // Elements
            ElementContainer ec = (ElementContainer) o;
            ModelElement me = ec.getElement();
            MetaModel metaModel = ec.getMetaModel();
            ElementsNameBuilder elementsNameBuilder = metaModel.getElementsNameBuilder();
            Class<? extends ModelElement> elementClass = me.getClass();
            appendBold(sb, elementsNameBuilder.getDisplayableName(elementClass));
            sb.append("<br><br>");
            sb.append(me.getClearName());
            appendDescription(sb, me.getDescription());
        } else if (o instanceof GDCollection) { // Model = GDCollection
            GDCollection gdcoll = (GDCollection) o;
            String description = gdcoll.getDescription();
            if (Strings.isNullOrEmpty(description)) {
                description = gdcoll.getMainDoc().getDescription();
            }
            appendBold(sb, o);
            appendDescription(sb, description);
        } else if (o instanceof GraphDocument) { // Submodel = GraphDocument
            appendBold(sb, o);
            appendDescription(sb, ((GraphDocument) o).getDescription());
            if (o instanceof Szenario) {
                appendImage(sb, (Szenario) o);
                maxToolTipWidth = MAX_TOOLTIP_WIDTH_WITH_IMAGE;
            }
        } else if (o instanceof File) {
            sb.append(o);
        }
        if (sb.length() == initialLength) {
            return null;
        }

        // limit width to 500px if tooltip if it contains more than 80 chars
        sb.insert(0, sb.length() > 80 ? "<html><p width=\"" + maxToolTipWidth + "\">" : "<html><p>");
        sb.append("</p></html>");
        return sb.toString();
    }

    /**
     * @param sb
     * @param description
     */
    private static void appendDescription(StringBuilder sb, String description) {
        if (!Strings.isNullOrEmpty(description)) {
            sb.append("<br><br>");
            description = HTMLConverter.getDecimalEncodedHTMLString(description, true);
            sb.append(description);
        }
    }

    /**
     * @param sb
     * @param line
     */
    private static void appendBold(StringBuilder sb, Object line) {
        sb.append("<b>");
        sb.append(line);
        sb.append("</b>");
    }

    /**
     * @param sb
     * @param comp
     */
    private static void appendImage(StringBuilder sb, Szenario szen) {
        try {
            ImageInfo imageInfo = GraphImageExporter.createImageBestFit(szen, TOOLTIP_IMAGE_CACHE_FILE_NAME, MAX_TOOLTIP_WIDTH_WITH_IMAGE);
            if (imageInfo.file != null) {
                sb.append("<br><br><img src=\"");
                sb.append(imageInfo.file.toURI().toURL()); // exception source -> try catch
                sb.append("\" />");
            }
        } catch (MalformedURLException e) {
            // ignore
        }
    }

}
