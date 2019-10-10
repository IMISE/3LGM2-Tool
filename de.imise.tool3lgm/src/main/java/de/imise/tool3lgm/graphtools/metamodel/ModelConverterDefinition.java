package de.imise.tool3lgm.graphtools.metamodel;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import javax.annotation.Nonnull;

import de.imise.tool3lgm.MetaModelContext;
import de.imise.tool3lgm.Tool3lgmMetaModelContext;
import de.imise.tool3lgm.graphtools.metamodel.elements.Edge;
import de.imise.tool3lgm.graphtools.metamodel.elements.ModelElement;
import de.imise.tool3lgm.graphtools.metamodel.elements.Node;
import de.imise.tool3lgm.graphtools.model.GDCollection;
import de.imise.tool3lgm.graphtools.model.LGMGraphDocument;
import de.imise.tool3lgm.graphtools.path.meta.AbstractMetaPath;
import de.imise.tool3lgm.graphtools.path.meta.SimpleMetaPath;
import de.imise.tool3lgm.graphtools.path.pathmodel.ElementaryPath;
import de.imise.tool3lgm.graphtools.path.pathmodel.SimplePath;
import de.imise.tool3lgm.graphtools.undoredo.TransactionManager;
import de.imise.util.ReflectionUtils;

/**
 * Definition der Transformation eines (Meta-)Modells in ein anderes.<br>
 * Die definierbaren Transformationen im einzelnen sind im Moment:
 * <ol>
 * <li>
 * Einfaches direktes Mapping von Knotenklassen aus dem Source-Metamodell auf das Target-Metamodell (z.B. Actor im Protegé-Import-Metamodel wird
 * IheActor im 3LGM-S(IHE)-Metamodel).
 * </li>
 * <li>
 * Einfaches direktes Mapping von Kantenklassen, wenn beide Knotenklassen, die diese Kante verbindet, direkt gemappt werden (z.B. die Kante
 * «IntegrationProfile enthält Actor» im Protegé-Import-Metamodel wird im 3LGM2-S(IHE)-Metamodell zu «IheIntegrationProfile enthält IheActor"),
 * d.h
 * hier stimmt sogar die Richtung der Kante und es muss nichts herumgedreht werden.
 * </li>
 * <li>
 * Direktes Mapping von Kantenklassen, aber die Richtung der Kante muss gedreht werden.
 * </li>
 * <li>
 * Eine Kantenklasse im Source-Modell wird zu einem Pfad über mehrere Kanten im Target-Metamodell (z.B. die Kante “Actor stellt bereit
 * Transaction” im
 * Protegé-Import-Metamodel wird im 3LGM-S(IHE)-Metamodel zum Pfad über die Kanten «IheActor besitzt IheProvidingInterface" +
 * «IheProvidingInterface
 * stellt bereit IheTransaction").
 * </li>
 * <li>
 * Der umgekehrte Fall von 3. = ein Pfad im Source-Metamodell wird zu einer Kante im Target-Metamodell (diesen Fall haben wir in unserer aktuellen
 * Aufgabenstellung nicht).
 * </li>
 * <li>
 * Ein Pfad im Source-Metamodell wird zu einem völlig anderen Pfad im TagetMetamodell. Das bedeutet, 2 Elementklassen, die sich direkt aufeinander
 * mappen ließen, sind in beiden Metamodellen über verschiedene, nicht direkt aufeinander abbildbare Kantenklassen und Zwischenelemente
 * miteinander
 * verbunden (auch dieser Fall ist hypothetisch, also habe ich dafür kein Beispiel aus unserer aktuellen Aufgabenstellung).
 * </li>
 * <li>
 * Irgendwas anderes, was sich nicht so sauber auf den Metamodell-Konzepten beschreiben lässt, wie die Punkte 1–5 es zulassen würden. Entweder
 * hier oder in einem zusätzlichen Punkt müsste man so etwas wie die Optionalität der “Actor stellt bereit Transaction”-Kanten übertragen.
 * </li>
 * </ol>
 * Für die Punkte 1–6 kann man Maps angeben, die diese Transformationen definieren. Der Punkt 7 wird in einer zusätzlichen “Erledige den
 * Rest”-Funktion untergebracht, in der beliebiger Java-Code stehen kanm und in der man somit auch die Punkte 1–6 erledigen könnte, wenn man zu
 * bequem
 * ist, die Mappings für die Funktionen 1–6 explizit zu definieren.
 *
 * @author AXS (7 Jun 2019)
 */
public abstract class ModelConverterDefinition {

    /** Klasse der Definition des Quellmetamodells dieses Converters */
    protected final Class<? extends MetaModelDefinition> sourceMetaModelDefinitionClass;

    /** Klasse der Definition des Zielmetamodells dieses Converters */
    protected final Class<? extends MetaModelDefinition> targetMetaModelDefinitionClass;

    /**
     * @param sourceMetaModelDefinitionClass
     * @param targetMetaModelDefinitionClass
     */
    public ModelConverterDefinition(@Nonnull final Class<? extends MetaModelDefinition> sourceMetaModelDefinitionClass, @Nonnull final Class<? extends MetaModelDefinition> targetMetaModelDefinitionClass) {
        this.sourceMetaModelDefinitionClass = sourceMetaModelDefinitionClass;
        this.targetMetaModelDefinitionClass = targetMetaModelDefinitionClass;
    }

    /** {@link MetaModelContext} des Quellmetamodells dieses Converters */
    private MetaModelContext sourceMetaModelContext;

    /** {@link MetaModelContext} des Zielmetamodells dieses Converters */
    private MetaModelContext targetMetaModelContext;

    /**
     * @return Klasse der Definition des Quellmetamodells dieses Converters
     */
    public final Class<? extends MetaModelDefinition> getSourceMetaModelDefinitionClass() {
        return sourceMetaModelDefinitionClass;
    }

    /**
     * @return Klasse der Definition des Zielmetamodells dieses Converters
     */
    public final Class<? extends MetaModelDefinition> getTargetMetaModelDefinitionClass() {
        return targetMetaModelDefinitionClass;
    }

    /**
     * @param clazz
     * @return <code>true</code>, wenn die übergebene Klasse zuweisungskompatibel zur {@link #sourceMetaModelDefinitionClass} ist, sonst
     *         <code>false</code>
     */
    public final boolean isSourceMetaModelDefinitionClass(final Class<?> clazz) {
        return ReflectionUtils.isAssignable(sourceMetaModelDefinitionClass, clazz);
    }

    /**
     * @param clazz
     * @return <code>true</code>, wenn die übergebene Klasse zuweisungskompatibel zur {@link #targetMetaModelDefinitionClass} ist, sonst
     *         <code>false</code>
     */
    public final boolean isTargetMetaModelDefinitionClass(final Class<?> clazz) {
        return ReflectionUtils.isAssignable(targetMetaModelDefinitionClass, clazz);
    }

    /**
     * @param metaModelDefinitionClass
     * @return <code>true</code>, wenn die übergebene MetaModelDefinition-Klasse der {@link #sourceMetaModelDefinitionClass} entspricht, sonst
     *         <code>false</code>
     */
    public final boolean canConvert(final Class<? extends MetaModelDefinition> metaModelDefinitionClass) {
        return isSourceMetaModelDefinitionClass(metaModelDefinitionClass);
    }

    /**
     * @return {@link MetaModelContext} zur {@link #sourceMetaModelDefinitionClass}.
     */
    public final MetaModelContext getSourceMetaModelContext() {
        if (sourceMetaModelContext == null) {
            sourceMetaModelContext = Tool3lgmMetaModelContext.getMetaModelContextForDefinitionClass(sourceMetaModelDefinitionClass);
        }
        return sourceMetaModelContext;
    }

    /**
     * @return {@link MetaModelContext} zur {@link #targetMetaModelDefinitionClass}.
     */
    public final MetaModelContext getTargetMetaModelContext() {
        if (targetMetaModelContext == null) {
            targetMetaModelContext = Tool3lgmMetaModelContext.getMetaModelContextForDefinitionClass(targetMetaModelDefinitionClass);
        }
        return targetMetaModelContext;
    }

    /**
     * Liefert eine Map, die von Knotenklassen aus dem Source-Metamodell direkt auf jeweils eine Knotenklasse im Targetmetamodell mappt. Deckt den
     * Fall 1 ab.
     *
     * @return Map von direkt aufeinander abbildbaren Knotenklassen
     */
    public Map<Class<? extends Node>, Class<? extends Node>> getSourceNodeClassesToTargetNodeClasses() {
        return new HashMap<>();
    }

    /**
     * Liefert eine Map, die von Kantenklassen aus dem Source-Metamodell direkt auf jeweils eine Kantenklasse im Targetmetamodell mappt. Dabei
     * bleibt auch die Richtung der Kante erhalten. Deckt den Fall 2 ab.
     *
     * @return Map von direkt aufeinander abbildbaren Knotenklassen
     */
    public Map<Class<? extends Edge>, Class<? extends Edge>> getSourceEdgeClassesToTargetEdgeClasses() {
        return new HashMap<>();
    }

    /**
     * Liefert eine Map, die von Kantenklassen aus dem Source-Metamodell direkt auf jeweils eine Kantenklasse im Targetmetamodell mappt. Dabei
     * wird die Richtung der Kante gedreht. Deckt den Fall 3 ab.
     *
     * @return Map von direkt aufeinander abbildbaren Knotenklassen
     */
    public Map<Class<? extends Edge>, Class<? extends Edge>> getSourceEdgeClassesToSwitchedTargetEdgeClasses() {
        return new HashMap<>();
    }

    /**
     * Liefert eine Map, die von Kantenklassen aus dem Source-Metamodell auf einen Pfad im Targetmetamodell mappt. Deckt den Fall 4 ab. Der Pfad
     * steckt in den Value-Klassen und außerdem noch eine Definition, wie Namen von Zwischenelemente, die beim Erzeugen des Pfades angelegt werden
     * erzeugt werden sollen.
     *
     * @return Map von direkt aufeinander abbildbaren Knotenklassen
     */
    public Map<Class<? extends Edge>, TargetMetaPathsCreationDefinition> getSourceEdgeClassesToTargetMetaPaths() {
        return new HashMap<>();
    }

    /**
     * Liefert eine Map, die von einem Pfad aus dem Source-Metamodell auf Kantenklassen im Targetmetamodell mappt. Deckt den Fall 5 ab.
     *
     * @return Map von direkt aufeinander abbildbaren Knotenklassen
     */
    public Map<SimpleMetaPath, Class<? extends Edge>> getSourceMetaPathsToEdgeClasses() {
        return new HashMap<>();
    }

    /**
     * Liefert eine Map, die von einem Pfad aus dem Source-Metamodell auf einen Pfad im Targetmetamodell mappt. Deckt den Fall 6 ab.
     *
     * @return Map von direkt aufeinander abbildbaren Knotenklassen
     */
    public Map<AbstractMetaPath, SimpleMetaPath> getSourceMetaPathsToTargetMetaPaths() {
        return new HashMap<>();
    }

    /**
     * Wandelt das Modell source in das Modell target um. Es ist so gedacht, dass hier all das noch getan werden kann, was die anderen Funktionen
     * oben nicht abdecken. Das ist der Fall 7.
     *
     * @param source
     * @param target
     */
    public void transform(final GDCollection source, final GDCollection target) {
        //subclasses can do special transforms here
    }

    /**
     * Spezielle Datenklasse für das Mapping von Kanten auf SimpleMetaPaths. Außer welcher Pfad aus der jeweiligen Kante entstehen soll, wird hier
     * auch noch defniert, welche Namen die neu anzulegenden Zwischenelemente erhalten sollen. Diese Namen bleiben entweder die generierten
     * Standardnamen oder werden ein String, der sich irgendwie aus der Kante bzw. deren Name oder dem Namen der durch die Kante verbundenen Elemente
     * ergibt.
     *
     * @author AXS (12 Jun 2019)
     */
    public static class TargetMetaPathsCreationDefinition {

        public enum NameSource {
            /** Name des Startelementes der Kante */
            PATH_STEP_START_ELEMENT_NAME,
            /** Name des Endelementes der Kante */
            PATH_STEP_END_ELEMENT_NAME,
            /** Name der Kante */
            PATH_STEP_EDGE_NAME,
        }

        /** MetaPfad, der über diese Defintion angelegt werden kann */
        private final SimpleMetaPath simpleMetaPath2Create;

        /**
         * Mappt von dem Index des Endelementes eines Pfadschrittes auf ein Pattern, über das Elemente, die an diesem Pfadschritt angelegt wurden,
         * umbenannt werden können.
         */
        private Map<Integer, Object[]> pathStepElementIndexToElementNameCreationPattern;

        /**
         * @param simpleMetaPath2Create
         */
        public TargetMetaPathsCreationDefinition(final SimpleMetaPath simpleMetaPath2Create) {
            this.simpleMetaPath2Create = simpleMetaPath2Create;
        }

        /**
         * Fügt ein Pattern für den Pfadschritt mit dem angegebenen Index hinzu
         *
         * @param pathStepElementIndex
         *            Index des Zwischenelementes im Pfad, dessen Name generiert werden soll.
         * @param patternObjects
         *            Die Objekte, aus denen der Name generiert wird. Das ist eine Liste von beliebigen Objekten und {@link NameSource}s. Alle Objekte
         *            außer {@link NameSource}s werden einfach über {@link String#valueOf(Object)} in Strings umgewandelt und aneinandergehängt. Die
         *            NameSources werden durch den von ihnen beschriebenen String ersetzt und dann angehängt.
         */
        public void addElementNameCreationPattern(final int pathStepElementIndex, final Object... patternObjects) {
            if (pathStepElementIndexToElementNameCreationPattern == null) {
                pathStepElementIndexToElementNameCreationPattern = new HashMap<>();
            }
            pathStepElementIndexToElementNameCreationPattern.put(pathStepElementIndex, patternObjects);
        }

        /**
         * @return the simpleMetaPath2Create MetaPfad, der über diese Defintion angelegt werden kann
         */
        public SimpleMetaPath getSimpleMetaPath2Create() {
            return simpleMetaPath2Create;
        }

        /**
         * Zwischenelemente des übergebenen Pfades werden entsprechend der Definition umbenannt und wenn sie dann gleich heißen, wie bereits zuvor
         * umbenannte Elemente derselben Art, dann werden die Elemente vereinigt.
         *
         * @param simplePath
         * @param nameSourceEdge
         * @return Set aller umbenannten Elemente
         */
        public Set<ModelElement> renameAndJoinEqualNamedElements(final SimplePath simplePath, final Edge nameSourceEdge, final Set<ModelElement> alreadyRenamedElements) {
            Set<Integer> pathStepIndices = pathStepElementIndexToElementNameCreationPattern.keySet();
            for (int pathStepIndex : pathStepIndices) {
                List<ElementaryPath> elementaryPaths = simplePath.getElementaryPaths();
                ElementaryPath elementaryPath = elementaryPaths.get(pathStepIndex);
                ModelElement pathStepEndElement = elementaryPath.getEndElement();
                Object[] patternObjetcs = pathStepElementIndexToElementNameCreationPattern.get(pathStepIndex);
                ModelElement renamedElement = renameElement(pathStepEndElement, nameSourceEdge, patternObjetcs);
                addOrJoinRenamedElement(renamedElement, alreadyRenamedElements);
            }
            return alreadyRenamedElements;
        }

        private void addOrJoinRenamedElement(final ModelElement renamedElement, final Set<ModelElement> alreadyRenamedElements) {
            if (renamedElement == null) {
                return;
            }
            String renamedName = renamedElement.getName();
            String renamedHash = renamedElement.getHashString();
            GDCollection gdcoll = renamedElement.getCollection();
            LGMGraphDocument mainDoc = gdcoll.getMainGraphDocument();
            Class<? extends ModelElement> renamedElementClass = renamedElement.getClass();
            boolean joined = false;
            for (ModelElement me : alreadyRenamedElements) {
                Class<? extends ModelElement> elementClass = me.getClass();
                if (elementClass == renamedElementClass) {
                    String name = me.getName();
                    if (Objects.equals(name, renamedName)) {
                        String hash = me.getHashString();
                        //System.err.println("JOINED ########## " + renamedElement);
                        joined = gdcoll.join(hash, renamedHash, mainDoc, TransactionManager.STANDARD_PID);
                        break;
                    }
                }
            }
            if (!joined) {
                alreadyRenamedElements.add(renamedElement);
            }
        }

        /**
         * @param element2Rename
         * @param nameSourceEdge
         * @param patternObjetcs
         */
        private ModelElement renameElement(final ModelElement element2Rename, final Edge nameSourceEdge, final Object[] patternObjetcs) {
            if (patternObjetcs == null || patternObjetcs.length == 0) {
                return null;
            }
            StringBuilder newName = new StringBuilder();
            for (Object patternObject : patternObjetcs) {
                if (patternObject instanceof NameSource) {
                    ModelElement nameSourceElement = null;
                    NameSource nameSource = (NameSource) patternObject;
                    switch (nameSource) {
                    case PATH_STEP_START_ELEMENT_NAME:
                        nameSourceElement = nameSourceEdge.getStart();
                        break;
                    case PATH_STEP_END_ELEMENT_NAME:
                        nameSourceElement = nameSourceEdge.getEnd();
                        break;
                    case PATH_STEP_EDGE_NAME:
                        nameSourceElement = nameSourceEdge;
                        break;
                    default:
                        break;
                    }
                    if (nameSourceElement != null) {
                        newName.append(nameSourceElement.getName());
                    }

                } else {
                    newName.append(String.valueOf(patternObject));
                }
            }
            element2Rename.setName(newName.toString());
            String description = nameSourceEdge.getDescription();
            element2Rename.setDescription(description); //ACHTUNG: Das hier ist ne Krücke um mal schnell was für IHE zu sehen, da jetzt jedes Element, das Namensbestandteile bekommt, immer die Beschreibung des Elementes erhält
            return element2Rename;
        }
    }

}
