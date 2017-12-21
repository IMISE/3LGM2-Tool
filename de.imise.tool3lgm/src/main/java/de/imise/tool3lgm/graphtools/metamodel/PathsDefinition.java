package de.imise.tool3lgm.graphtools.metamodel;

import static de.imise.tool3lgm.Tool3lgmConstants.getResString;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import de.imise.tool3lgm.graphtools.metamodel.elements.ModelElement;
import de.imise.tool3lgm.graphtools.path.InvalidPathException;
import de.imise.tool3lgm.graphtools.path.MetaPath;

public abstract class PathsDefinition {

    /**
     * COMMENTME
     */
    private final Map<String, MetaPath[]> metaPathes = new HashMap<>();

    /**
     * Liste der Elementtypen, für die ein Pfad in init() definiert wurde (wird z.B. für die Comboboxen in der Matrixsicht gebraucht)
     */
    private final Set<Class<? extends ModelElement>> elementClassesInPathes = new HashSet<>();

    public PathsDefinition() {
        if (metaPathes.isEmpty()) {
            try {
                init();
            } catch (InvalidPathException exp) {
                exp.printStackTrace();
            }
            initElementTypesInPathes();
        }
    }

    protected abstract void init() throws InvalidPathException;

    private void initInternal() {
    }

    /**
     * Setzt den übergenen Metapfad in die HashMap aller Metapfade
     *
     * @param metaPath
     */
    public final void put(final MetaPath[] metaPath) {
        metaPathes.put(calculateKey(metaPath[0].getStartClass(), metaPath[0].getEndClass()), metaPath);
    }

    /**
     * Berechnet einen eindeutigen Schlüssel für 2 übergebene Klassen.
     *
     * @param elementClass1
     * @param elementClass2
     * @return
     */
    private static final String calculateKey(final Class<? extends ModelElement> elementClass1, final Class<? extends ModelElement> elementClass2) {
        int hash1 = elementClass1.hashCode();
        int hash2 = elementClass2.hashCode();
        StringBuilder sb = new StringBuilder();
        if (hash1 < hash2) {
            sb.append(elementClass1.hashCode());
            sb.append(elementClass2.hashCode());
        } else {
            sb.append(elementClass2.hashCode());
            sb.append(elementClass1.hashCode());
        }
        return sb.toString();
    }

    /**
     * Bildet das Set aller Klassen, für die Pfade definiert wurden.
     */
    private void initElementTypesInPathes() {
        for (MetaPath[] mps : metaPathes.values()) {
            MetaPath path = mps[0];
            elementClassesInPathes.add(path.getStartClass());
            elementClassesInPathes.add(path.getEndClass());
        }
    }

    /**
     * @return
     */
    public final Set<Class<? extends ModelElement>> getElementClassesInPathes() {
        initInternal();
        return elementClassesInPathes;
    }

    /**
     * @param resourceString
     * @return
     */
    protected static final String s(final String resourceString) {
        return getResString(resourceString);
    }

    /**
     * Liefert alle <code>MetaPath</code>es, die zwischen Elementen der Art <code>startClass</code> und <code>endClass</code> definiert sind.
     *
     * @param startClass
     * @param endClass
     * @return
     */
    public final MetaPath[] getMetaPathes(final Class<? extends ModelElement> startClass, final Class<? extends ModelElement> endClass) {
        initInternal();
        return metaPathes.get(calculateKey(startClass, endClass));
    }

}