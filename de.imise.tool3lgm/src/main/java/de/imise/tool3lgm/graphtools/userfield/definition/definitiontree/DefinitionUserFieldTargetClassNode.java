package de.imise.tool3lgm.graphtools.userfield.definition.definitiontree;

import java.util.Iterator;

import de.imise.tool3lgm.graphtools.userfield.definition.UserField;
import de.imise.tool3lgm.graphtools.userfield.definition.UserFieldTarget;
import de.imise.tool3lgm.graphtools.view.tree.node.IconifiedTreeNode;
import de.imise.tool3lgm.graphtools.view.tree.node.LGMTreeNode;

/**
 * @author AXS (29.03.2021)
 */
public class DefinitionUserFieldTargetClassNode extends IconifiedTreeNode<Class<? extends UserFieldTarget>> implements Iterable<UserField> {

    /**
     * @param targetClass
     * @param label
     */
    public DefinitionUserFieldTargetClassNode(final Class<? extends UserFieldTarget> targetClass, final String label) {
        super(targetClass, label, false);
    }

    @Override
    public Iterator<UserField> iterator() {
        return new Iterator<UserField>() {

            int i = 0;

            Iterator<UserField> innerIterator = null;

            @Override
            public boolean hasNext() {
                if (innerIterator != null) {
                    if (innerIterator.hasNext()) {
                        return true;
                    }
                    innerIterator = null;
                }
                for (int j = i + 1; j < getChildCount(); j++) {
                    LGMTreeNode<?> child = getChildAt(j);
                    if (child instanceof DefinitionUserFieldNode) {
                        i = j;
                        return true;
                    } else if (child instanceof DefinitionStructureNode) {
                        i = j;
                        innerIterator = ((DefinitionStructureNode) child).iterator();
                        return hasNext();
                    }
                }
                return false;
            }

            @Override
            public UserField next() {
                if (innerIterator != null) {
                    return innerIterator.next();
                }
                DefinitionUserFieldNode userFieldNode = (DefinitionUserFieldNode) getChildAt(i);
                return userFieldNode.getUserObject();
            }

        };
    }

    /**
     * @param userField
     */
    public void removeChildNodesWith(final UserField userField) {
        for (int i = getChildCount() - 1; i >= 0; i--) {
            LGMTreeNode<?> child = getChildAt(i);
            if (child instanceof DefinitionStructureNode) {
                ((DefinitionStructureNode) child).removeChildNodesWith(userField);
            }
        }
    }

}
