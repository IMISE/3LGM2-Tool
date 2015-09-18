package de.imise.tool3lgm.graphtools.view.tree;

/**
 * @author N.N.
 */
class HyperlinkString {
    private String name = "";
    private String value = "";

    public HyperlinkString(final String name, final String value) {
        this.name = name;
        this.value = value;
    }

    @Override
    public String toString() {
        return name;
    }

    public String getValue() {
        return value;
    }
}