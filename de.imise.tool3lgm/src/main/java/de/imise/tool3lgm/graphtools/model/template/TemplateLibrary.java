package de.imise.tool3lgm.graphtools.model.template;

import java.util.Collection;
import java.util.Iterator;

/**
 * Ungenutzt. Eventuell sollte das hier eine Unterklasse von GDCollection
 * werden. Das hier war ein erster Entwurf. Er erübrigt sicht erst einmal mit
 * der ModelCategory.REGULAR und TEMPLATE
 *
 * @author AXS (12.09.2019)
 */
public class TemplateLibrary {

    private String id;

    private String version;

    private String name;

    private String description;

    private String maintainer;

    private Collection<String> tags; //wahrscheinlich reicht Collection -> HashSet, weil die Liste eine Zusatzinformationen enthält

    public TemplateLibrary() {
        super();
    }

    public String getId() {
        return id;
    }

    public void setId(final String id) {
        this.id = id;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(final String version) {
        this.version = version;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(final String description) {
        this.description = description;
    }

    public String getMaintainer() {
        return maintainer;
    }

    public void setMaintainer(final String maintainer) {
        this.maintainer = maintainer;
    }

    public boolean containsTag(final Object o) {
        return tags.contains(o);
    }

    public Iterator<String> tagIterator() {
        return tags.iterator();
    }

    public boolean addTag(final String e) {
        return tags.add(e);
    }

    public boolean containsAllTags(final Collection<?> c) {
        return tags.containsAll(c);
    }

}
