package de.imise.tool3lgm.graphtools.metamodel;

import java.util.List;
import java.util.Objects;
import java.util.Set;

import de.imise.tool3lgm.graphtools.metamodel.ModelConverterDefinition.TargetPathsCreationDefinition;
import de.imise.tool3lgm.graphtools.metamodel.ModelConverterDefinition.TargetPathsCreationDefinition.NameSource;
import de.imise.tool3lgm.graphtools.metamodel.elements.CompositionEdge;
import de.imise.tool3lgm.graphtools.metamodel.elements.Edge;
import de.imise.tool3lgm.graphtools.metamodel.elements.ModelElement;
import de.imise.tool3lgm.graphtools.metamodel.elements.Node;
import de.imise.tool3lgm.graphtools.model.GDCollection;
import de.imise.tool3lgm.graphtools.model.GraphDocument;
import de.imise.tool3lgm.graphtools.model.LGMGraphDocument;
import de.imise.tool3lgm.graphtools.path.paths.ElementaryPath;
import de.imise.tool3lgm.graphtools.path.paths.SimplePath;
import de.imise.tool3lgm.graphtools.undoredo.TransactionManager;

/**
 * @author AXS (18.11.2019)
 */
public class ModelConverterUtils {

    /**
     *
     */
    public ModelConverterUtils() {
    }

    /**
     * Replaces the automatic generated hashStrings in a generated path by a
     * given hashString. The element in the middle gets the original given
     * hashString. All the others get the same with an unique number appended.
     * This algorithm is deterministic, so converting the same model again will
     * generate the same hashStrings. The hashStrings of the start- and
     * endElement of the path will not be changed. If there is an Element with
     * the same type and with the same hash id (ignoring the added number) than
     * this element is created by the same edge and is the same element)
     *
     * @param path
     * @param hashString the generated element in the middle of the path gets
     *            this hash
     */
    static void replaceGeneratedHashStringsAndJoinEqualsElements(final SimplePath path, final String hashString) {
        // Sys.err1(hashString + "\r\t" + path);
        ModelElement middleElement = getMiddleElement(path);
        GDCollection gdcoll = middleElement.getCollection();

        int counter = setHashStringOrJoinElementIfEqualsExists(gdcoll, middleElement, null, hashString, path, 0);
        middleElement = getMiddleElement(path); // if joined -> middle element
                                                // has changed

        int pathLength = path.size();
        for (int i = 0; i < pathLength; i++) {
            ElementaryPath pathStep = path.getPathStep(i);
            // edge
            Edge edge = pathStep.getEdge();
            // Do not join edges, because the same kind of edge in the path can
            // be created with the same hash string between completely different
            // elements.
            counter = setHashString(gdcoll, edge, hashString, counter);
            if (i < pathLength - 1) { // endElement
                ModelElement endElement = pathStep.getEndElement();
                counter = setHashStringOrJoinElementIfEqualsExists(gdcoll, endElement, middleElement, hashString, path, counter);
            }
        }
    }

    /**
     * Subordinated elements are merged, which connect the same elements on the
     * path. In IHE models, interfaces that reside on the same application
     * system and can call or provide the same transaction become one interface,
     * rather than as many as the application system originally had transaction
     * connections.
     *
     * @param path
     */
    static void joinSubordinatedBetweenEqualsElements(final SimplePath path) {
        int pathLength = path.size();
        GDCollection gdcoll = null;
        for (int i = 0; i < pathLength - 1; i++) {
            ElementaryPath pathStep = path.getPathStep(i);
            ElementaryPath nextPathStep = path.getPathStep(i + 1);
            Edge edge = pathStep.getEdge();
            Edge nextPathStepEdge = nextPathStep.getEdge();
            ModelElement slaveElementCandidate = pathStep.getEndElement();
            boolean compositionSlaveOfPathStep = MetaModel.isCompositionSlave(edge, slaveElementCandidate);
            boolean compositionSlaveOfNextPathStep = !compositionSlaveOfPathStep && MetaModel.isCompositionSlave(nextPathStepEdge, slaveElementCandidate);
            if (!(compositionSlaveOfPathStep || compositionSlaveOfNextPathStep)) {
                continue;
            }
            ModelElement master = compositionSlaveOfPathStep ? pathStep.getStartElement() : nextPathStep.getEndElement();
            Class<? extends Edge> compositionEdgeClass = compositionSlaveOfPathStep ? edge.getClass() : nextPathStepEdge.getClass();
            Class<? extends Edge> otherPathStepEdgeClass = compositionSlaveOfPathStep ? nextPathStepEdge.getClass() : edge.getClass();
            ModelElement otherPathStepOtherElement = compositionSlaveOfPathStep ? nextPathStep.getEndElement() : pathStep.getStartElement();
            List<ModelElement> allSlaveElements = master.getConnectedElements(compositionEdgeClass, CompositionEdge.MASTER_TO_SLAVE_DIRECTION);
            for (ModelElement slaveElement : allSlaveElements) {
                if (slaveElement != slaveElementCandidate) {
                    if (slaveElement.isConnectedWith(otherPathStepOtherElement, otherPathStepEdgeClass)) {
                        if (gdcoll == null) {
                            gdcoll = slaveElementCandidate.getCollection();
                        }
                        LGMGraphDocument mainDoc = gdcoll.getMainDoc();
                        String elementHash = slaveElementCandidate.getHashString();
                        String resultingJoinedElementHash = slaveElement.getHashString();
                        // Element hash for the resulting element must be the second
                        // parameter!
                        slaveElement = gdcoll.join(elementHash, resultingJoinedElementHash, mainDoc, false, TransactionManager.STANDARD_PID);
                        // if joined with an existing element -> replace the renamed
                        // element in the path by the joined one
                        path.replaceElement(slaveElementCandidate, slaveElement);
                    }
                }
            }
        }
    }

    /**
     * @param gdcoll
     * @param me
     * @param ignoreElement
     * @param hashString
     * @param path
     * @param counter
     * @return
     */
    private static int setHashStringOrJoinElementIfEqualsExists(final GDCollection gdcoll, final ModelElement me, final ModelElement ignoreElement, final String hashString, final SimplePath path, int counter) {
        if (me != ignoreElement && me instanceof Node) {
            // gibt es bereits ein Element wie das middleElement, das denselben
            // HastString-Prefix hat (dieses Element ist aus derselben Kante
            // entstanden)
            ModelElement equalElement = getEqualElement(me, hashString);
            if (equalElement != null) {
                String resultingJoinedElementHash = equalElement.getHashString();
                // System.err.println("JOINED ########## " + renamedElement);

                // to prevent that the name or description will be joined too ->
                // set it to the same value
                String elementHash = me.getHashString();
                String resultingName = equalElement.getName();
                String resultingDescription = equalElement.getDescription();
                me.setName(resultingName);
                me.setDescription(resultingDescription);

                LGMGraphDocument mainDoc = gdcoll.getMainDoc();

                // Achtung: Das Join haut nicht richtig hin! daher muss es im
                // Moment umgenagen werden, indem man dafür sorgt, dass es
                // nichts zu joinen gibt und den 2.Pfad, der entstehen soll, auf
                // andere Weise anlegt.
                // Element hash for the resulting element must be the second
                // parameter!
                equalElement = gdcoll.join(elementHash, resultingJoinedElementHash, mainDoc, false, TransactionManager.STANDARD_PID);
                // if joined with an existing element -> replace the renamed
                // element in the path by the joined one
                path.replaceElement(me, equalElement);
            } else {
                counter = setHashString(gdcoll, me, hashString, counter);
            }
        }
        return counter;
    }

    /**
     * Searches the model for an element with the same type and the given
     * hashString prefix. If there is such an element it will be returned. If
     * not <code>null</code> will be returned. The hashString of the returned
     * element is the same like the given or it starts with this hashString
     * followed by an underscore '_' and then by a number.
     *
     * @param me
     * @param hashString
     * @return same type element with the given hashStringPrefix
     */
    private static ModelElement getEqualElement(final ModelElement me, final String hashString) {
        GDCollection gdcoll = me.getCollection();
        LGMGraphDocument mainDoc = gdcoll.getMainDoc();
        Class<? extends ModelElement> elementClass = me.getClass();
        List<ModelElement> elements = mainDoc.getModelItems(elementClass);
        String hashStringPrefix = hashString + '_';
        int hashStringPrefixLength = hashStringPrefix.length();
        for (ModelElement element : elements) {
            String elementHashString = element.getHashString();
            if (elementHashString.equals(hashString)) {
                return element;
            } else if (elementHashString.startsWith(hashStringPrefix)) {
                String number = elementHashString.substring(hashStringPrefixLength);
                try {
                    Integer.parseInt(number);
                    return element;
                } catch (Exception e) {
                }
            }
        }
        return null;
    }

    /**
     * @param gdcoll
     * @param me
     * @param hashString
     * @param counter
     * @return
     */
    private static int setHashString(final GDCollection gdcoll, final ModelElement me, final String hashString, int counter) {
        GraphDocument mainDoc = gdcoll.getMainDoc();
        String fullHashString = counter < 1 ? hashString : hashString + "_" + (++counter);
        while (mainDoc.findElementCoded(fullHashString) != null) {
            fullHashString = hashString + "_" + (++counter);
        }
        me.setHashString(fullHashString);
        return counter;
    }

    /**
     * @param path
     * @return
     */
    private static ModelElement getMiddleElement(final SimplePath path) {
        int pathLength = path.size();
        int middlePathStep = pathLength / 2;
        ElementaryPath pathStep = path.getPathStep(middlePathStep);
        // even path step count -> node in the middle; odd pathStepCount -> edge
        // in the middle
        ModelElement middleElement = pathLength % 2 == 0 ? pathStep.getStartElement() : pathStep.getEdge();
        return middleElement;
    }

    ///////////////////////////////////////////
    // Rename and Join equals named elements //
    ///////////////////////////////////////////

    /**
     * Intermediate elements of the passed path are renamed according to the
     * definition and if they then have the same name as previously renamed
     * elements of the same type, then the elements are merged.
     *
     * @param targetMetaPathsCreationDefinition
     * @param simplePath
     * @param nameSourceEdge
     * @return Set aller umbenannten Elemente
     */
    public static final Set<ModelElement> renameAndJoinEqualNamedElements(final TargetPathsCreationDefinition targetMetaPathsCreationDefinition, final SimplePath simplePath, final Edge nameSourceEdge, final Set<ModelElement> alreadyRenamedElements) {
        for (int pathStepIndex : targetMetaPathsCreationDefinition) {
            List<ElementaryPath> elementaryPaths = simplePath.getElementaryPaths();
            ElementaryPath elementaryPath = elementaryPaths.get(pathStepIndex);
            ModelElement pathStepEndElement = elementaryPath.getEndElement();
            Object[] patternObjetcs = targetMetaPathsCreationDefinition.getPatternObjetcs(pathStepIndex);
            ModelElement renamedElement = renameElement(pathStepEndElement, nameSourceEdge, patternObjetcs);
            ModelElement addedOrJoinedElement = addOrJoinRenamedElement(renamedElement, alreadyRenamedElements);
            // if joined with an existing element -> replace the renamed element
            // in the path by the joined one
            if (renamedElement != addedOrJoinedElement) {
                simplePath.replaceElement(renamedElement, addedOrJoinedElement);
            }
        }
        return alreadyRenamedElements;
    }

    /**
     * @param renamedElement
     * @param alreadyRenamedElements
     * @return <code>true</code> if the given element was joined with an element
     *         in alreadyRenamedElements with the same name
     */
    private static ModelElement addOrJoinRenamedElement(final ModelElement renamedElement, final Set<ModelElement> alreadyRenamedElements) {
        if (renamedElement == null) {
            return null;
        }
        String renamedName = renamedElement.getName();
        String renamedHash = renamedElement.getHashString();
        GDCollection gdcoll = renamedElement.getCollection();
        LGMGraphDocument mainDoc = gdcoll.getMainDoc();
        Class<? extends ModelElement> renamedElementClass = renamedElement.getClass();
        ModelElement resultElement = renamedElement;
        for (ModelElement me : alreadyRenamedElements) {
            Class<? extends ModelElement> elementClass = me.getClass();
            if (elementClass == renamedElementClass) {
                String name = me.getName();
                if (Objects.equals(name, renamedName)) {
                    String resultingJoinedElementHash = me.getHashString();
                    // System.err.println("JOINED ########## " +
                    // renamedElement);
                    // Element hash for the resulting element must be the second
                    // parameter!
                    resultElement = gdcoll.join(renamedHash, resultingJoinedElementHash, mainDoc, false, TransactionManager.STANDARD_PID);
                    break;
                }
            }
        }
        // no join with an existing element -> remember as original renamed
        // element
        if (resultElement == renamedElement) {
            alreadyRenamedElements.add(renamedElement);
        }
        return resultElement;
    }

    /**
     * @param element2Rename
     * @param nameSourceEdge
     * @param patternObjetcs
     */
    private static ModelElement renameElement(final ModelElement element2Rename, final Edge nameSourceEdge, final Object[] patternObjetcs) {
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
        // ATTENTION: This is a crutch to quickly see something for IHE, because
        // now every element that gets name parts always gets the description of
        // the element
        String description = nameSourceEdge.getDescription();
        element2Rename.setDescription(description);
        return element2Rename;
    }

}
