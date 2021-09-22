package de.imise.util;

public class NameAndDescriptionTargetAdapter implements NameAndDescriptionTarget {

    /**
     * the name
     */
    protected String name;

    /**
     * the description
     */
    protected String description;

    /**
     *
     */
    public NameAndDescriptionTargetAdapter() {
        this("");
    }

    /**
     * @param name
     */
    public NameAndDescriptionTargetAdapter(final String name) {
        this(name, "");
    }

    /**
     * @param name
     * @param description
     */
    public NameAndDescriptionTargetAdapter(final String name, final String description) {
        this.name = name;
        this.description = description;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(final String name) {
        this.name = getCleanString(name);
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public void setDescription(final String description) {
        this.description = description == null ? "" : getCleanString(description);
    }

}
