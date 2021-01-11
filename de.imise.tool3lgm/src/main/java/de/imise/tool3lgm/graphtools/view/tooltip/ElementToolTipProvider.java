package de.imise.tool3lgm.graphtools.view.tooltip;

import javax.swing.JComponent;

import com.google.common.base.Strings;

import de.imise.tool3lgm.graphtools.ElementsNameBuilder;
import de.imise.tool3lgm.graphtools.consistency.error.type.AbstractConsistencyError;
import de.imise.tool3lgm.graphtools.metamodel.MetaModel;
import de.imise.tool3lgm.graphtools.metamodel.elements.ModelElement;
import de.imise.tool3lgm.graphtools.view.container.ElementContainer;
import de.imise.tool3lgm.graphtools.view.tree.node.LGMTreeNode;
import de.imise.util.NamedObjectContainer;
import de.imise.util.ToolTipProvider;
import de.imise.util.htmlxml.HTMLConverter;

/**
 * A {@link ToolTipProvider} implementation that provides the tooltips for
 * ModelElements
 *
 * @author AXS (09.06.2020)
 */
public class ElementToolTipProvider implements ToolTipProvider {

    /**
     *
     */
    public ElementToolTipProvider() {
    }

    /**
     * Registers the component as target for this tooltip provider.
     *
     * @param target
     */
    public ElementToolTipProvider(final JComponent target) {
        addToolTipMouseListeners(target);
    }

    @Override
    public String getToolTip(Object o) {
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
        if (o instanceof ElementContainer) {
            ElementContainer ec = (ElementContainer) o;
            ModelElement me = ec.getElement();
            MetaModel metaModel = ec.getMetaModel();
            ElementsNameBuilder elementsNameBuilder = metaModel.getElementsNameBuilder();
            StringBuilder sb = new StringBuilder("<html><p width=\"500\">");
            Class<? extends ModelElement> elementClass = me.getClass();
            String displayableClassName = elementsNameBuilder.getDisplayableName(elementClass);
            sb.append("<b>");
            sb.append(displayableClassName);
            sb.append("</b>");
            sb.append("<br><br>");
            sb.append(me.getClearName());
            String description = me.getDescription();
            if (!Strings.isNullOrEmpty(description)) {
                sb.append("<br><br>");
                description = HTMLConverter.getDecimalEncodedHTMLString(description, true);
                sb.append(description);
            }
            sb.append("</p></html>");
            return sb.toString();
        }
        return null;
    }

}
