package de.imise.tool3lgm.graphtools.userfield.definition.definitiontree;

import java.util.Iterator;

import de.imise.tool3lgm.graphtools.IDSource;
import de.imise.tool3lgm.graphtools.userfield.definition.UserField;
import de.imise.tool3lgm.graphtools.view.tree.node.LGMTreeNode;
import de.imise.tool3lgm.graphtools.view.tree.node.StringTreeNode;
import de.imise.util.NameAndDescriptionTarget;

/**
 * @author AXS (29.03.2021)
 */
public abstract class DefinitionStructureNode extends StringTreeNode implements NameAndDescriptionTarget, IDSource, Iterable<UserField> {

    /**
     *
     */
    private final String id;

    /**
     *
     */
    private String description;

    /**
     * @param name
     * @param description
     * @param id
     */
    public DefinitionStructureNode(final String name, final String description, final String id) {
        super(name);
        this.description = description;
        this.id = id == null ? createID("DSN") : id.length() < 4 ? createID(id) : id;
    }

    @Override
    public String getName() {
        return getText();
    }

    @Override
    public void setName(final String name) {
        setText(name);
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public void setDescription(final String description) {
        this.description = description;
    }

    @Override
    public String getID() {
        return id;
    }

    /**
     * @return
     */
    public Iterable<DefinitionUserFieldNode> getUserFieldNodes() {
        return new Iterable<DefinitionUserFieldNode>() {

            @Override
            public Iterator<DefinitionUserFieldNode> iterator() {
                return new Iterator<DefinitionUserFieldNode>() {

                    int i = 0;

                    Iterator<DefinitionUserFieldNode> innerIterator = null;

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
                                Iterable<DefinitionUserFieldNode> userFieldNodes = ((DefinitionStructureNode) child).getUserFieldNodes();
                                innerIterator = userFieldNodes.iterator();
                                return innerIterator.hasNext();
                            }
                        }
                        return false;
                    }

                    @Override
                    public DefinitionUserFieldNode next() {
                        if (innerIterator != null) {
                            return innerIterator.next();
                        }
                        DefinitionUserFieldNode userFieldNode = (DefinitionUserFieldNode) getChildAt(i);
                        return userFieldNode;
                    }

                };
            }
        };
    }

    @Override
    public final Iterator<UserField> iterator() {
        Iterable<DefinitionUserFieldNode> userFieldNodes = getUserFieldNodes();
        Iterator<DefinitionUserFieldNode> userFieldNodesIterator = userFieldNodes.iterator();
        return new Iterator<UserField>() {
            @Override
            public boolean hasNext() {
                return userFieldNodesIterator.hasNext();
            }

            @Override
            public UserField next() {
                DefinitionUserFieldNode userFieldNode = userFieldNodesIterator.next();
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
            } else if (child instanceof DefinitionUserFieldNode) {
                UserField nodeUserField = ((DefinitionUserFieldNode) child).getUserObject();
                if (nodeUserField.equals(userField)) {
                    remove(i);
                }
            }
        }
    }

}
