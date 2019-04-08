package de.imise.tool3lgm.graphtools.path.meta;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import de.imise.tool3lgm.graphtools.metamodel.ModelConstants;
import de.imise.tool3lgm.graphtools.metamodel.elements.Edge;
import de.imise.tool3lgm.graphtools.metamodel.elements.Edge.Direction;
import de.imise.tool3lgm.graphtools.metamodel.elements.ModelElement;
import de.imise.util.ReflectionUtils;

/**
 * @author AXS (5 Dec 2018)
 */
public class SimpleMetaPathCreator {

    //    /**
    //     * Erzeugt aus den übergebenen Assoziationen einen {@link SequenceMetaPath} ausgehend von der Startklasse, die übergeben wurde. Die
    //     * Richtungen werden aus dder Startklasse abgeleitet. Wenn es nicht eindeutig ist, ob die Startklasse die Kante vorwärts oder
    //     * rückwärts dreht, dann wird immer vorwärts angenommen.
    //     *
    //     * @param startClass
    //     * @param associations
    //     * @return
    //     */
    //    @SafeVarargs
    //    public static final SimpleMetaPath createSimpleMetaPath(final Class<? extends ModelElement> startClass, final Class<? extends Edge>... associations) {
    //        return createSimpleMetaPath(startClass, null, null, associations);
    //    }
    //
    /**
     * Erzeugt aus den übergebenen Assoziationen einen {@link SequenceMetaPath} zwischen der Start- und Endklasse, die übergeben wurden. Die
     * Richtungen werden aus diesen Start- und Endklassen abgeleitet. Wenn es nicht eindeutig ist, ob die Startklasse die Kante vorwärts oder
     * rückwärts dreht, dann wird immer vorwärts angenommen.
     *
     * @param startClass
     * @param endClass
     * @param associations
     * @return
     */
    @SafeVarargs
    public static final SimpleMetaPath createSimpleMetaPath(final Class<? extends ModelElement> startClass, final Class<? extends ModelElement> endClass, final Class<? extends Edge>... associations) {
        return createSimpleMetaPath(startClass, endClass, null, -1, associations);
    }

    /**
     * @param startClass
     * @param endClass
     * @param metaPaths
     */
    public static SimpleMetaPath createSimpleMetaPath(final Class<? extends ModelElement> startClass, final Class<? extends ModelElement> endClass, final ElementaryMetaPath... metaPaths) {
        return new SimpleMetaPath(initFullPath(startClass, endClass, metaPaths));
    }

    /**
     * @param startClass
     * @param endClass
     * @param baseResKeyOrName
     * @param metaPaths
     */
    public SimpleMetaPath createSimpleMetaPath(final Class<? extends ModelElement> startClass, final Class<? extends ModelElement> endClass, final String baseResKeyOrName, final ElementaryMetaPath... metaPaths) {
        return new SimpleMetaPath(baseResKeyOrName, initFullPath(startClass, endClass, metaPaths));
    }

    /**
     * @param startClass
     * @param endClass
     * @param metaPathStepWithPathName
     *            Index des Elementarpfadschrittes, der den Namen des Gesamtpfades festlegt. Ist er kleiner 0 läuft die Namensgenerierung über den
     *            super-Namensmechanismus, der den baseResKeyOrName auswertet und wenn er damit auch nichts findet "ist verbunden mit" ausgibt.
     * @param metaPaths
     */
    public SimpleMetaPath createSimpleMetaPath(final Class<? extends ModelElement> startClass, final Class<? extends ModelElement> endClass, final int metaPathStepWithPathName, final ElementaryMetaPath... metaPaths) {
        return new SimpleMetaPath(metaPathStepWithPathName, initFullPath(startClass, endClass, metaPaths));
    }

    /**
     * Erzeugt aus den übergebenen Assoziationen einen {@link SequenceMetaPath} zwischen der Start- und Endklasse, die übergeben wurden. Die
     * Richtungen werden aus diesen Start- und Endklassen abgeleitet. Wenn es nicht eindeutig ist, ob die Startklasse die Kante vorwärts oder
     * rückwärts dreht, dann wird immer vorwärts angenommen.
     *
     * @param startClass
     * @param endClass
     * @param baseResKeyOrName
     * @param associations Das ist eine Liste aus Element- und Kantenklassen. Diese Liste kann nur einen validen Pfad definieren, wenn niemals zwei
     *            reine Elementklassen (die also keine Kantenklassen sind) hintereinander stehen. Es steht immer eine Kantenklasse hinter einer
     * @return
     * @throws IllegalArgumentException
     */
    @SafeVarargs
    public static final SimpleMetaPath createSimpleMetaPath(final Class<? extends ModelElement> startClass, final Class<? extends ModelElement> endClass, final String baseResKeyOrName, final Class<? extends Edge>... associations)
            throws IllegalArgumentException {
        return createSimpleMetaPath(startClass, endClass, baseResKeyOrName, -1, associations);
    }

    /**
     * Erzeugt aus den übergebenen Assoziationen einen {@link SequenceMetaPath} zwischen der Start- und Endklasse, die übergeben wurden. Die
     * Richtungen werden aus diesen Start- und Endklassen abgeleitet. Wenn es nicht eindeutig ist, ob die Startklasse die Kante vorwärts oder
     * rückwärts dreht, dann wird immer vorwärts angenommen.
     *
     * @param startClass
     * @param endClass
     * @param metaPathStepWithPathName
     *            Index des Elementarpfadschrittes, der den Namen des Gesamtpfades festlegt. Ist er kleiner 0 läuft die Namensgenerierung über den
     *            super-Namensmechanismus, der den baseResKeyOrName auswertet und wenn er damit auch nichts findet "ist verbunden mit" ausgibt.
     * @param associations Das ist eine Liste aus Element- und Kantenklassen. Diese Liste kann nur einen validen Pfad definieren, wenn niemals zwei
     *            reine Elementklassen (die also keine Kantenklassen sind) hintereinander stehen. Es steht immer eine Kantenklasse hinter einer
     * @return
     * @throws IllegalArgumentException
     */
    @SafeVarargs
    public static final SimpleMetaPath createSimpleMetaPath(final Class<? extends ModelElement> startClass, final Class<? extends ModelElement> endClass, final int metaPathStepWithPathName, final Class<? extends Edge>... associations)
            throws IllegalArgumentException {
        return createSimpleMetaPath(startClass, endClass, null, metaPathStepWithPathName, associations);
    }
    /**
     * Erzeugt aus den übergebenen Assoziationen einen {@link SequenceMetaPath} zwischen der Start- und Endklasse, die übergeben wurden. Die
     * Richtungen werden aus diesen Start- und Endklassen abgeleitet. Wenn es nicht eindeutig ist, ob die Startklasse die Kante vorwärts oder
     * rückwärts dreht, dann wird immer vorwärts angenommen.
     *
     * @param startClass
     * @param endClass
     * @param baseResKeyOrName
     * @param metaPathStepWithPathName
     *            Index des Elementarpfadschrittes, der den Namen des Gesamtpfades festlegt. Ist er kleiner 0 läuft die Namensgenerierung über den
     *            super-Namensmechanismus, der den baseResKeyOrName auswertet und wenn er damit auch nichts findet "ist verbunden mit" ausgibt.
     * @param associations Das ist eine Liste aus Element- und Kantenklassen. Diese Liste kann nur einen validen Pfad definieren, wenn niemals zwei
     *            reine Elementklassen (die also keine Kantenklassen sind) hintereinander stehen. Es steht immer eine Kantenklasse hinter einer
     * @return
     * @throws IllegalArgumentException
     */
    @SafeVarargs
    private static final SimpleMetaPath createSimpleMetaPath(final Class<? extends ModelElement> startClass, final Class<? extends ModelElement> endClass, final String baseResKeyOrName, final int metaPathStepWithPathName,
            final Class<? extends Edge>... associations) throws IllegalArgumentException {
        ElementaryMetaPath[] metaPaths = new ElementaryMetaPath[associations.length];
        Class<? extends ModelElement> start = startClass;
        for (int i = 0; i < associations.length; i++) {
            Class<? extends Edge> edgeClass = associations[i];
            Direction direction = getEdgeDirection(start, edgeClass, i == associations.length - 1 ? endClass : null); // bei der letzten Kante muss die Endklasse passen. Wenn bei einer Kante in der Mitte des Pfades die nächste Kante nicht passt, dann wird das unten druch Zurücklaufen erkannt
            //die Elementklasse passt nicht zur aktuellen Kante
            if (direction == null) {
                //solange zur vorherigen Kante zurück gehen, bis man eine findet, die sowohl vorwärts als auch rückwärts passt und diese dann mit rückwärts probieren
                for (--i; i >= 0; i--) {
                    if (metaPaths[i].hasDirectionForward()) { //das bedeutet, dass die aktuelle Kante in Vorwärsrichtung gelesen genommen wurde, was immer die zuerst gesuchte Richtung ist
                        if (isEdgeDirectionBackward(startClass, edgeClass)) { //falls die Kante auch rückwärts im Pfad sein kann
                            direction = Direction.BACKWARD;
                            break;
                        }
                    }
                }
                if (i < 0) {
                    //der Pfad ist fehlerhaft, d. h. trotz Zurücklaufen und Test mit der Gegenrichtung passen die Kanten nicht zueinander
                    throw new IllegalArgumentException("EdgeClasses dosn't define a valid metapath");
                }
            }
            ElementaryMetaPath metaPath;
            if (metaPaths.length == 1) {
                metaPath = ElementaryMetaPathHandler.getMetaPath(start, edgeClass, direction, endClass);
            } else if (i == 0) {
                metaPath = ElementaryMetaPathHandler.getMetaPath(start, edgeClass, direction);
            } else if (i == metaPaths.length - 1 && endClass != null) {
                metaPath = ElementaryMetaPathHandler.getMetaPath(edgeClass, direction, endClass);
            } else {
                metaPath = ElementaryMetaPathHandler.getMetaPath(edgeClass, direction);
            }
            metaPaths[i] = metaPath;
            start = metaPath.getEndClass();
        }
        SimpleMetaPath simpleMetaPath = metaPathStepWithPathName < 0 ? new SimpleMetaPath(baseResKeyOrName, metaPaths) : new SimpleMetaPath(metaPathStepWithPathName, metaPaths);
        return simpleMetaPath;
    }

    /**
     * Erzeugt ein Array von allen konkreten MetaPfaden, die dem ggf. abstrakten übergebenen MetaPfad entsprechen. Ist keine der übergebenen
     * Kantenklassen abstrakt, dann kommt in dem Set nur der übergebene Pfad zurück.
     *
     * @param metaPathStepWithPathName
     * @param edgeClasses
     * @return
     */
    @SafeVarargs
    public static final SimpleMetaPath[] createSimpleMetaPaths(final Class<? extends ModelElement> startClass, final int metaPathStepWithPathName, final Class<? extends Edge>... edgeClasses) {
        SimpleMetaPath path = SimpleMetaPathCreator.createSimpleMetaPath(startClass, null, metaPathStepWithPathName, edgeClasses);
        Collection<SimpleMetaPath> simpleMetaPathsNonAbstract = getSimpleMetaPathsNonAbstract(path);
        SimpleMetaPath[] simpleMetaPaths = new SimpleMetaPath[simpleMetaPathsNonAbstract.size()];
        simpleMetaPaths = simpleMetaPathsNonAbstract.toArray(simpleMetaPaths);
        return simpleMetaPaths;
    }

    /**
     * Wenn die übergebene Startklasse nicht dieselbe Klasse ist, wie die Startklasse des ersten Metapfades, dann wird im Ergebis-Array aller
     * MetaPfade ein MetaPfad vorangestellt, der nur die übergebene Startklasse enthält. Dasselbe gilt für die Endklasse und die Endklasse des letzten
     * Elementarpfades.
     *
     * @param startClass
     * @param endClass
     * @param metaPaths
     * @return
     */
    private static final ElementaryMetaPath[] initFullPath(final Class<? extends ModelElement> startClass, final Class<? extends ModelElement> endClass, final ElementaryMetaPath... metaPaths) {
        int lastMetaPathIndex = metaPaths.length - 1;
        if (lastMetaPathIndex == 0) {
            metaPaths[0] = ElementaryMetaPathHandler.getMetaPath(startClass, metaPaths[0], endClass);
        } else if (lastMetaPathIndex > 0) {
            metaPaths[0] = ElementaryMetaPathHandler.getMetaPath(startClass, metaPaths[0], metaPaths[0].getEndClass());
            metaPaths[lastMetaPathIndex] = ElementaryMetaPathHandler.getMetaPath(metaPaths[lastMetaPathIndex].getStartClass(), metaPaths[lastMetaPathIndex], endClass);
        }
        return metaPaths;
    }

    /**
     * Wenn die übergebene Elementklasse die Startklasse der übergebenen Kantenklasse ist, dann kommt Direction.FORWARD zurück.
     * Ist sie die Endklasse, kommt Direction.BACKWARD zurück und wenn sie gar nicht passt, dann null. Es wird genau in dieser
     * Reihenfolge geprüft, also wenn die übergebene Klasse Start- und Endklasse der Kantenklasse ist, dann kommt Direction.FORWARD.
     *
     * @param startClass
     * @param edgeClass
     * @param endClass ist diese Klasse null, wird nur die startClass berücksichtigt
     * @return
     */
    public static final Direction getEdgeDirection(final Class<? extends ModelElement> startClass, final Class<? extends Edge> edgeClass, final Class<? extends ModelElement> endClass) {
        Direction direction = Edge.isStartClass(edgeClass, startClass) && (endClass == null || Edge.isEndClass(edgeClass, endClass)) ? Direction.FORWARD
                : Edge.isEndClass(edgeClass, startClass) && (endClass == null || Edge.isStartClass(edgeClass, endClass)) ? Direction.BACKWARD : null;
        return direction;
    }

    public static boolean isEdgeDirectionBackward(final Class<? extends ModelElement> startClass, final Class<? extends Edge> edgeClass) {
        return Edge.isEndClass(edgeClass, startClass);
    }

    /**
     * Es werden alle verschiedenen {@link SimpleMetaPath}s zurück gegeben, bei denen die im übergebenen {@link SimpleMetaPath} eventuell abstrakten
     * Kantenklassen durch konkrete ersetzt wurden.
     *
     * @param simpleMetaPath
     * @return
     */
    public static Collection<SimpleMetaPath> getSimpleMetaPathsNonAbstract(final SimpleMetaPath simpleMetaPath) {
        //Ergebnisliste
        List<SimpleMetaPath> simpleMetaPaths = new ArrayList<>();
        //übergebenen MetaPfad als erstes in die Ergebnisliste schreiben
        simpleMetaPaths.add(simpleMetaPath);
        //jetzt für jeden Elementarpfadschritt des Ausgangspfades immer alle Pfade in die Ergebnisliste schreiben, die nur noch Elementarpfadschritte mit nocht-abstrakten Kantenklassen haben
        List<ElementaryMetaPath> elementaryMetaPaths = simpleMetaPath.getElementaryMetaPaths();
        for (int i = 0; i < elementaryMetaPaths.size(); i++) {
            getSimpleMetaPathsNonAbstract(simpleMetaPaths, i);
        }
        return simpleMetaPaths;
    }

    /**
     * Für jeden der Pfade in der Liste wird geprüft, ob der Elementarpfadschritt mit dem übergebenen Index abstract ist. Wenn er abstract ist, dann
     * wird der Metapfad in der Liste durch alle MetaPfade ersetzt, bei denen die abstrakte Kantenklasse durch alle konkreten ersetzt wurde.
     *
     * @param simpleMetaPaths
     * @param currentPathStepIndex
     * @return
     */
    private static List<SimpleMetaPath> getSimpleMetaPathsNonAbstract(final List<SimpleMetaPath> simpleMetaPaths, final int currentPathStepIndex) {
        //bei jedem MetaPfad der Liste
        for (int p = 0; p < simpleMetaPaths.size(); p++) {
            SimpleMetaPath simpleMetaPath = simpleMetaPaths.get(p);
            List<ElementaryMetaPath> elementaryMetaPaths = simpleMetaPath.getElementaryMetaPaths();
            //hole die Kantenklasse des aktuellen Pfadschrittes
            ElementaryMetaPath elementaryMetaPath = elementaryMetaPaths.get(currentPathStepIndex);
            Class<? extends Edge> edgeClass = elementaryMetaPath.getEdgeClass();
            //wenn die Kantenklasse abstract ist
            if (ModelConstants.isAbstract(edgeClass)) {
                //Start- und Edklasse des aktuellen Pfadschrittes aus dem originalen MetaPfad ermitteln
                Class<? extends ModelElement> pathStepConnectingStartClass = currentPathStepIndex == 0 ? simpleMetaPath.getStartClass() : simpleMetaPath.getPathStepElementClass(currentPathStepIndex - 1);
                Class<? extends ModelElement> pathStepConnectingEndClass = simpleMetaPath.getPathStepElementClass(currentPathStepIndex);
                //alle nicht-abstrakten Kantenklassen zwischen dieser Start- und Endklasse ermitteln
                Class<? extends Edge>[] edgeTypes = ModelConstants.getEdgeTypes(pathStepConnectingStartClass, pathStepConnectingEndClass);
                //Der erste neue SimpleMetaPtah, bei dem der aktuelle Elementarpfadschritt durch einen mit nicht-abstrakter Kantenklasse ersetzt wurde, muss in der Ergenisliste den Original-MetaPfad ersetzen.
                //All anderen danach werden dahinter eingefügt und der Index des aktuellen Elementarpfadschrittes erhöht.
                boolean replaceOriginalMetaPathInResultList = true;
                //für alle gefundenen nicht-abstrakten Kantenarten zwischen der Start- und Endklasse des Original-MetaPfades
                for (Class<? extends Edge> edgeType : edgeTypes) {
                    //wenn die nicht-abstrakte Kantenklasse eine Unterklasse der abstrakten des Original-MetaPfades ist
                    if (edgeClass.isAssignableFrom(edgeType)) {
                        //Erzeuge ein neues Array aus Elementarpfaden, bei dem der aktuelle Pfadschritt immer durch einen Elementarmetapfad mit der nicht-abstrakten Kantenklasse ersetzt wird
                        ElementaryMetaPath[] elementaryMetaPathArray = new ElementaryMetaPath[elementaryMetaPaths.size()];
                        elementaryMetaPathArray = elementaryMetaPaths.toArray(elementaryMetaPathArray);
                        Direction direction = elementaryMetaPath.getDirection();
                        //Start- und Endklasse des neuen Pfadschrittes ist die speziellere der jeweilgen Klassen vom Original-MetaPafd und der nicht-abstrakten Kantenklasse
                        Class<? extends ModelElement> pathStepStartClass = direction == Direction.BACKWARD ? Edge.getEndClass(edgeType) : Edge.getStartClass(edgeType);
                        Class<? extends ModelElement> pathStepEndClass = direction == Direction.BACKWARD ? Edge.getStartClass(edgeType) : Edge.getEndClass(edgeType);
                        pathStepStartClass = ReflectionUtils.getMostSpecialElementClass(pathStepConnectingStartClass, pathStepStartClass);
                        pathStepEndClass = ReflectionUtils.getMostSpecialElementClass(pathStepConnectingEndClass, pathStepEndClass);
                        //jetzt den neuen Elementarpfadschritt mit den speziellen Start- und Endklasse in derselben Richtung wie das Original anlegen
                        elementaryMetaPathArray[currentPathStepIndex] = ElementaryMetaPathHandler.getMetaPath(pathStepStartClass, edgeType, elementaryMetaPath.getDirection(), pathStepEndClass);
                        //den neuen SimpleMetaPfad mit der nicht-abstrakten Kantenklasse analog zum original anlegen (also mit den Index der Kante, die den Namen festlegt übernehmen)
                        int metaPathStepWithPathName = simpleMetaPath.getMetaPathStepWithPathName();
                        SimpleMetaPath newSimpleMetaPath = new SimpleMetaPath(metaPathStepWithPathName, elementaryMetaPathArray);
                        //bei der ersten nicht-abstrakten Kantenklasse wird der neue MetaPfad in der Ergebnisliste einfach über den neuen geschrieben
                        if (replaceOriginalMetaPathInResultList) {
                            replaceOriginalMetaPathInResultList = false;
                            simpleMetaPaths.set(p, newSimpleMetaPath); //den originalen MetaPfad durch den ersten neuen ersetzen
                        } else {
                            simpleMetaPaths.add(++p, newSimpleMetaPath); //den neuen MetaPfad einfügen und Index des aktuellen MetaPfades in der Gesamtliste hochsetzen
                        }
                    }
                }
                //keine einzige nicht-abstrakte Kantenklasse passte -> lösche den Original-MetaPfad aus der Ergebnisliste. Das hier ist relevant, wenn die SimpleMetaPfade nicht nur
                //über eine Folge von zusammenpassenden Kantenklassen sondern über nichtzusammenpassende Kantenklassen oder über Elementarpfadlisten definiert werden, bei denen die
                //z.B. mitten im Pfad zwei hintereinanderfolgende Pfadschtitte haben, die bei nur aus der abtrakten Klasse Edge.class bestehen und danach wieder irgendeine sehr spezielle
                //Kantenklasse, die sich mit vielen der bis dahin entstandenen SimpleMetaPaths nicht mehr zu einem sinnvollen MetaPfad zusammenfügen lassen -> die Pfade löschen
                if (replaceOriginalMetaPathInResultList) {
                    simpleMetaPaths.remove(p--);
                }
            }
        }
        return simpleMetaPaths;
    }

}
