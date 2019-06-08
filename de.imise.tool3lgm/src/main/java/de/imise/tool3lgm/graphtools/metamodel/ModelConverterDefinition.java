package de.imise.tool3lgm.graphtools.metamodel;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nonnull;

import de.imise.tool3lgm.MetaModelContext;
import de.imise.tool3lgm.Tool3lgmMetaModelContext;
import de.imise.tool3lgm.graphtools.metamodel.elements.Edge;
import de.imise.tool3lgm.graphtools.metamodel.elements.Node;
import de.imise.tool3lgm.graphtools.model.GDCollection;
import de.imise.tool3lgm.graphtools.path.meta.AbstractMetaPath;
import de.imise.tool3lgm.graphtools.path.meta.SimpleMetaPath;

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
    public Map<Class<? extends Node>, Class<? extends Node>> getDirectMappingNodeClasses() {
        return new HashMap<>();
    }

    /**
     * Liefert eine Map, die von Kantenklassen aus dem Source-Metamodell direkt auf jeweils eine Kantenklasse im Targetmetamodell mappt. Dabei
     * bleibt auch die Richtung der Kante erhalten. Deckt den Fall 2 ab.
     *
     * @return Map von direkt aufeinander abbildbaren Knotenklassen
     */
    public Map<Class<? extends Edge>, Class<? extends Edge>> getDirectMappingEdgeClasses() {
        return new HashMap<>();
    }

    /**
     * Liefert eine Map, die von Kantenklassen aus dem Source-Metamodell direkt auf jeweils eine Kantenklasse im Targetmetamodell mappt. Dabei
     * wird die Richtung der Kante gedreht. Deckt den Fall 3 ab.
     *
     * @return Map von direkt aufeinander abbildbaren Knotenklassen
     */
    public Map<Class<? extends Edge>, Class<? extends Edge>> getDirectMappingSwitchedEdgeClasses() {
        return new HashMap<>();
    }

    /**
     * Liefert eine Map, die von Kantenklassen aus dem Source-Metamodell auf einen Pfad im Targetmetamodell mappt. Deckt den Fall 4 ab.
     *
     * @return Map von direkt aufeinander abbildbaren Knotenklassen
     */
    public Map<Class<? extends Edge>, SimpleMetaPath> getEdgesMappingMetaPaths() {
        return new HashMap<>();
    }

    /**
     * Liefert eine Map, die von einem Pfad aus dem Source-Metamodell auf Kantenklassen im Targetmetamodell mappt. Deckt den Fall 5 ab.
     *
     * @return Map von direkt aufeinander abbildbaren Knotenklassen
     */
    public Map<SimpleMetaPath, Class<? extends Edge>> getMetaPathsMappingEdges() {
        return new HashMap<>();
    }

    /**
     * Liefert eine Map, die von einem Pfad aus dem Source-Metamodell auf einen Pfad im Targetmetamodell mappt. Deckt den Fall 6 ab.
     *
     * @return Map von direkt aufeinander abbildbaren Knotenklassen
     */
    public Map<AbstractMetaPath, SimpleMetaPath> getMetaPathsMappingMetaPaths() {
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
}
