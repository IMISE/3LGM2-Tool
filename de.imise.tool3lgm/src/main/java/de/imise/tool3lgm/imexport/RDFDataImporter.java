package de.imise.tool3lgm.imexport;

import static de.imise.tool3lgm.imexport.RDFDataImporter.NameCreationPatternStandardIndentifier.LABEL;
import static de.imise.tool3lgm.imexport.RDFDataImporter.NameCreationPatternStandardIndentifier.LOCAL_NAME;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.apache.jena.ontology.AnnotationProperty;
import org.apache.jena.ontology.Individual;
import org.apache.jena.ontology.ObjectProperty;
import org.apache.jena.ontology.OntClass;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.ontology.OntProperty;
import org.apache.jena.ontology.OntResource;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;
import org.apache.jena.util.iterator.ExtendedIterator;

import com.google.common.base.Strings;

import de.imise.tool3lgm.graphtools.metamodel.MetaModel;
import de.imise.tool3lgm.graphtools.metamodel.elements.Edge;
import de.imise.tool3lgm.graphtools.metamodel.elements.ModelElement;
import de.imise.tool3lgm.graphtools.metamodel.elements.Node;
import de.imise.tool3lgm.graphtools.model.GDCollection;
import de.imise.util.DataPrinter;
import de.imise.util.StringUtils;

/**
 * Allgemeiner Importer für OWL RDF-Dateien. Der Importer fragt das OWL-Model nach genau den Knoten- und Kantenklassen bzw. deren Instanzen im
 * OWL-Model und baut daraus ein äquivalentes 3LGM2-Modell zusammen.
 * Der DataImporter wird mit dem Typ &ltObject&gt initialisiert, weil anscheinend alle Objekte, die man aus dem OntModel holen kann, immer als
 * hashCode den hashCode der Uri zurück liefern. Daher kann man in die Map der Source-Knoten auf die Target-Knoten mit jedem Object fragen und nicht
 * nur mit Objecten eines ganz bestimmten Typs.
 *
 * @author AXS (26 Jun 2019)
 */
public abstract class RDFDataImporter extends UrlSourceDataImporter<Object> implements DataPrinter {

    public static enum NameCreationPatternStandardIndentifier {
        LOCAL_NAME,
        LABEL,
    }

    /**
     * Hülle um einen String, damit dieser in Namenspattern als Name einer Objectproperty erkannt werden kann.
     *
     * @author AXS (24.09.2019)
     */
    public static class OntPropertyName {

        private final String propertyUriOrLocalName;

        public OntPropertyName(final String propertyUriOrLocalName) {
            this.propertyUriOrLocalName = propertyUriOrLocalName;
        }

    }

    private static final class OntPropertyResolver {

        private final OntModel ontModel;

        private final OntProperty property;

        /**
         * @param ontModel
         */
        public OntPropertyResolver(final OntModel ontModel, final OntPropertyName ontPropertyName) {
            this(ontModel, ontPropertyName.propertyUriOrLocalName);
        }

        public OntPropertyResolver(final OntModel ontModel, final String annotationPropertyUriOrLocalName) {
            this.ontModel = ontModel;
            property = getProperty(annotationPropertyUriOrLocalName);
        }

        /**
         * Liefert die Property, die die Beschreibung enthält. Es kann sein, dass es Ontologien gibt,
         * bei denen das hier nicht stimmt, weil {@link AnnotationProperty}s nicht in jeder OWL-Datei
         * vorkommen oder die Description irgendwas anderes beschreibt.
         *
         * @param propertyUriOrLocalName
         * @return
         */
        private OntProperty getProperty(final String propertyUriOrLocalName) {
            for (ExtendedIterator<OntProperty> listProperties = ontModel.listOntProperties(); listProperties.hasNext();) {
                OntProperty property = listProperties.next();
                String localOrUriName = property.getURI();
                if (propertyUriOrLocalName.equals(localOrUriName)) {
                    return property;
                }
                localOrUriName = property.getLocalName();
                if (propertyUriOrLocalName.equals(localOrUriName)) {
                    return property;
                }
            }
            return null;
        }

        public String getValue(final OntResource ontNode) {
            if (property == null) {
                return "";
            }
            RDFNode propertyValue = ontNode.getPropertyValue(property);
            String value = propertyValue == null ? "" : propertyValue.toString();
            return value;
        }

        public String getValue(final Property predicate) {
            if (property == null) {
                return "";
            }
            Statement statement = predicate.getProperty(property);
            RDFNode statementNode = statement == null ? null : statement.getObject();
            String value = statementNode == null ? "" : statementNode.toString();
            return value;
        }

    }

    /**
     * @param file
     * @throws MalformedURLException
     */
    public RDFDataImporter(final File file) throws MalformedURLException {
        super(file);
    }

    /**
     * @param url
     */
    public RDFDataImporter(final URL url) {
        super(url);
    }

    /**
     * @return Anhang des Namens, um den der Name von den 3LGM-Kantenklassen des Import-Metamodells von den Namen der zu importierdenden
     *         ObjectProperties abweicht
     */
    public abstract String getEdgeClassNamePostfix();

    @Override
    public boolean importData(final URL url) {
        OntModel ontModel = ModelFactory.createOntologyModel();
        String urlString = url.toString();
        ontModel.read(urlString);
        String descriptionPropertyNameOrUri = getDescriptionPropertyUriOrLocalName();
        OntPropertyResolver descriptionPropertyResolver = new OntPropertyResolver(ontModel, descriptionPropertyNameOrUri);
        importNodes(ontModel, descriptionPropertyResolver);
        importEdges(ontModel, descriptionPropertyResolver);
        return true;
    }

    /**
     * @param ontModel
     * @param elementClassName
     * @return
     */
    private List<Object> createRealPattern(final OntModel ontModel, final String elementClassName) {
        MetaModel metaModel = getMetaModel();
        Class<? extends ModelElement> elementClass = metaModel.getClassForName(elementClassName);
        return createRealPattern(ontModel, elementClass);
    }

    /**
     * @param ontModel
     * @param elementClass
     * @return
     */
    private List<Object> createRealPattern(final OntModel ontModel, final Class<? extends ModelElement> elementClass) {
        final Object[] patternObjects = elementClassToNamePattern.get(elementClass);
        if (patternObjects == null) {
            return null;
        }
        List<Object> realPattern = new ArrayList<>();
        for (Object patternObject : patternObjects) {
            if (patternObject instanceof OntPropertyName) {
                OntPropertyName ontPropertyName = (OntPropertyName) patternObject;
                OntPropertyResolver ontPropertyResolver = new OntPropertyResolver(ontModel, ontPropertyName);
                realPattern.add(ontPropertyResolver);
            } else {
                realPattern.add(patternObject);
            }
        }
        return realPattern;
    }

    /**
     * @param ontNode
     * @return
     */
    private String getLabel(final OntResource ontNode) {
        Locale locale = Locale.getDefault();
        String language = locale.getLanguage();
        String name = ontNode.getLabel(language); //ontNode.getLocalName(); //label ist der Anzeigename und localName ist der techn. Bezeichner
        if (Strings.isNullOrEmpty(name)) {
            name = ontNode.getLabel(null);
        }
        return name;
    }

    /**
     * @param ontResource
     * @param namePattern
     * @return
     */
    private String getName(final OntResource ontResource, final List<Object> namePattern) {
        if (namePattern == null || namePattern.isEmpty()) {
            String name = getLabel(ontResource);
            if (Strings.isNullOrEmpty(name)) {
                name = ontResource.getLocalName();
            }
            return name;
        }
        StringBuilder sb = new StringBuilder();
        for (Object patternObject : namePattern) {
            String append;
            if (patternObject == LABEL) {
                append = getLabel(ontResource);
            } else if (patternObject == LOCAL_NAME) {
                append = ontResource.getLocalName();
            } else if (patternObject instanceof OntPropertyResolver) {
                OntPropertyResolver propertyResolver = (OntPropertyResolver) patternObject;
                append = propertyResolver.getValue(ontResource);
            } else {
                append = Objects.toString(patternObject);
            }
            sb.append(append);
        }
        return sb.toString();
    }

    /**
     * Fragt in der Ontologie alle Class-Knoten ab, deren Klasse denselben Namen hat, wie eine Klasse aus dem Metamodell des zu füllenden
     * 3LGM2-Modells und legt in diesem 3LGM2-Modell für jede Instanz des Class-Knotens im RDF-Quellmodell einen Knoten im 3LGM2-Zielmodell an.
     * Außerdem werden die Class-Knoten und der korrespondierende 3LGM2-Knoten in einer Map in DataImporter gespeichert.
     *
     * @param ontModel Quellmodell
     * @param annotationPropertyResolver
     */
    private void importNodes(final OntModel ontModel, final OntPropertyResolver descriptionPropertyResolver) {
        GDCollection gdcoll = getCollection();
        MetaModel metaModel = gdcoll.getMetaModel();
        Collection<String> classNames = getSimpleClassNames(metaModel.allNodesSet, "");
        for (Iterator<OntClass> ontClasses = ontModel.listNamedClasses(); ontClasses.hasNext();) {
            OntClass ontClass = ontClasses.next();
            String ontClassName = ontClass.getLocalName();
            if (classNames.contains(ontClassName)) {
                Class<? extends ModelElement> lgmClass = metaModel.getClassForName(ontClassName);
                Class<? extends Node> lgmNodeClass = lgmClass.asSubclass(Node.class);
                print(ontClassName + " > " + lgmClass);
                List<Object> namePattern = createRealPattern(ontModel, lgmClass);
                int i = 1;
                for (Iterator<? extends OntResource> ontNodes = ontClass.listInstances(true); ontNodes.hasNext();) {
                    //Wenn die Ontologie nicht ganz richtig modelliert ist, kann es vorkommen, dass ontClass.listInstances(true) auch
                    //Instanzen anderer als der eigentlichen Zielklasse zurück liefert.
                    //Das lässt sich durch das nun folgende beheben, indem man aus dem Model über dei URI die Individuals holt und
                    //von denen die Klasse vergleicht. Das hier behebt aber nur die Symptome. Die Ursache ist ein Fehler in der Ontologie!
                    OntResource ontNode = ontNodes.next();
                    String uri = ontNode.getURI();
                    Individual individual = ontModel.getIndividual(uri);
                    OntClass individualOntClass = individual.getOntClass();
                    if (individualOntClass.equals(ontClass)) {
                        String name = getName(ontNode, namePattern);
                        String description = descriptionPropertyResolver.getValue(ontNode);
                        String hashString = ontNode.getURI(); //originale URI übernehmen
                        Node lgmNode = addNode(ontNode, lgmNodeClass, name, description, hashString);
                        printe(i++ + "\t" + ontClassName + " -> " + ontNode + "  ->  " + lgmNode);
                    } else {
                        printe("ERROR: " + individualOntClass + "  !=  " + ontClass + "      ------>      " + individual);
                    }
                }
            }
        }
    }

    //    private void property(final OntModel ontModel, final Property p) {
    //        ontModel.listObjectProperties();
    //        System.err.println(p);
    //
    //    }
    //
    //    private void statement(final Statement s) {
    //        System.err.println(s);
    //    }

    private OntProperty getOntProperty(final Property property, final Map<ObjectProperty, String> importableObjectPropertiesToTargetEdgeClassName) {
        for (ObjectProperty objectProperty : importableObjectPropertiesToTargetEdgeClassName.keySet()) {
            if (objectProperty.equals(property)) {
                return objectProperty;
            }
        }
        return null;
    }

    /**
     * Liefert alle Statements, die eine Kante repräsentieren, die ins Zielmodell übernommen werden muss.
     *
     * @param ontModel
     * @param descriptionPropertyResolver
     * @return Map mit allen Statements, die eine zu importierende Kante repräsentieren als Key und dem Namen der daraus zu erzeugenden Kantenart im
     *         Zielmodell als value
     */
    private void importEdges(final OntModel ontModel, final OntPropertyResolver descriptionPropertyResolver) {
        //ObjectProperty -> Kantenklassenname
        Map<ObjectProperty, String> importableObjectPropertiesToTargetEdgeClassName = getImportableObjetctProperties(ontModel);
        int i = 1;
        for (StmtIterator statements = ontModel.listStatements(); statements.hasNext();) {
            //Statement
            Statement statement = statements.next();
            //Predicate == importiertbare ObjectProperty?
            Property predicate = statement.getPredicate();
            String targetEdgeClassName = importableObjectPropertiesToTargetEdgeClassName.get(predicate);

            List<Object> namePattern = createRealPattern(ontModel, targetEdgeClassName);
            if (targetEdgeClassName != null) {
                //Subject == Knoten aus dem SourceModel?
                Resource subjectResource = statement.getSubject();
                Node startNode = getTargetNode(subjectResource);
                if (startNode != null) {
                    //Object == Knoten aus dem SourceModel?
                    RDFNode objectNode = statement.getObject();
                    Node endNode = getTargetNode(objectNode);
                    if (endNode != null) {
                        //Predicate -> Edge
                        String edgeHash = predicate.getURI(); //originale URI übernehmen
                        OntProperty ontProperty = getOntProperty(predicate, importableObjectPropertiesToTargetEdgeClassName);
                        String name = getName(ontProperty, namePattern);
                        String description = descriptionPropertyResolver.getValue(predicate);
                        try {
                            Edge lgmEdge = addEdge(targetEdgeClassName, name, edgeHash, startNode, endNode);
                            lgmEdge.setDescription(description);
                            printe(i++ + "\t" + targetEdgeClassName + " (" + lgmEdge.getHashString() + ")" + " -> " + startNode + "  ->  " + endNode + " " + lgmEdge + " " + description);
                        } catch (Exception e) {
                            // hier kann es zu java.lang.InstantiationExceptions kommen, wenn die EdgeClass abstract ist, weil nur für Unterklassen der ObjectProperty Edges angelegt werden sollen
                            printe("SKIPPED " + statement);
                        }
                    }
                }
            }
        }
    }

    /**
     * Bestimmt alle ObjectProperties, die ins Zielmodell als Kante übernommen werden sollen.
     *
     * @param ontModel
     * @return Map, die von ObjectProperties, bei denen der eigene Name oder der einer SuperProperty sich der Name aus einem Kantenklassennamen aus
     *         dem ImportMetamodell ableiten lässt, auf den Namen dieser Kantenklasse aus dem ImportMetaModell
     */
    private Map<ObjectProperty, String> getImportableObjetctProperties(final OntModel ontModel) {
        Map<ObjectProperty, String> importableObjectPropertiesToTargetEdgeClassName = new HashMap<>();
        MetaModel metaModel = gdcoll.getMetaModel();
        //gültige ObjectProperty-Klassennamen
        Collection<String> objectPropertyNames = getSimpleClassNames(metaModel.allEdgesSet, getEdgeClassNamePostfix());
        for (Iterator<ObjectProperty> objectProperties = ontModel.listObjectProperties(); objectProperties.hasNext();) {
            //ObjectProperty-Instanz
            ObjectProperty objectProperty = objectProperties.next();
            //ObjectProperties mit echten Unterklassen sollen selbst nicht hinzugefügt werden
            if (hasSubProperties(objectProperty)) {
                continue;
            }
            String name = objectProperty.getLocalName();
            //solange die Oberklassen durchsuchen, bis ein gültiger ObjectProperty-Klassenname gefunden wurde oder nicht
            String superPropertyName = name;
            if (!objectPropertyNames.contains(name)) {
                superPropertyName = getImportableSuperObjectPropertyName(objectProperty, objectPropertyNames);
            }
            if (!Strings.isNullOrEmpty(superPropertyName)) {
                superPropertyName += getEdgeClassNamePostfix();
                superPropertyName = StringUtils.capitalizeFirstChar(superPropertyName); //Kantenklassennamen sind immer groß geschrieben - zugehörige, gleich heißende EdgeProperty evtl. klein -> Umwandeln
                importableObjectPropertiesToTargetEdgeClassName.put(objectProperty, superPropertyName);
            }
        }
        return importableObjectPropertiesToTargetEdgeClassName;
    }

    /**
     * Liefert <code>true</code>, wenn dei übergebene ObjectProperty wenigstens eine SubProperty hat, die nicht sie selbst ist (das ist nämlich immer
     * eine SubProperty)
     *
     * @param objectProperty
     * @return
     */
    private final boolean hasSubProperties(final ObjectProperty objectProperty) {
        for (Iterator<? extends OntProperty> subProperties = objectProperty.listSubProperties(); subProperties.hasNext();) {
            OntProperty subProperty = subProperties.next();
            if (!objectProperty.equals(subProperty)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Prüft rekursiv, ob die übergebene ObjectProperty eine Super-ObjectProperty besitzt, deren Name zu den zu importierenden ObjectProperties
     * gehört.
     *
     * @param objectProperty aktuelle Property, deren Oberklassen rekursiv durchsucht werden sollen
     * @param importableObjectPropertyNames gültige ObjectProperty-Klassennamen
     * @return wird eine passende Super-Property mit gültigem Namen gefeunden, kommt deren Name zurück, sonst <code>null</code>
     */
    private final String getImportableSuperObjectPropertyName(final OntProperty objectProperty, final Collection<String> importableObjectPropertyNames) {
        for (Iterator<? extends OntProperty> superProperties = objectProperty.listSuperProperties(); superProperties.hasNext();) {
            OntProperty superProperty = superProperties.next();
            if (superProperty instanceof OntProperty) {
                String name = superProperty.getLocalName();
                if (importableObjectPropertyNames.contains(name)) {
                    return name;
                }
                return getImportableSuperObjectPropertyName(superProperty, importableObjectPropertyNames);
            }
        }
        return null;
    }

    /**
     * Liefert aus der übergebenen Menge von Klassen ein Set der darin enthaltenen simplen Klassennamen. Wird ein postfix-String übergeben, dann wird
     * dieser von allen Klassennamen abgezogen, in denen er vorkommt. Jeder der gefundenen Klassennamen wird einmal mit großem und einmal mit kleinem
     * Anfangsbuchstaben ins Set geschrieben.
     *
     * @param classes
     * @param postfix
     * @return
     */
    private <T> Collection<String> getSimpleClassNames(final Iterable<Class<? extends T>> classes, final String postfix) {
        Set<String> returnSet = new HashSet<>();
        int postfixLength = postfix.length();
        for (Class<? extends T> clazz : classes) {
            String simpleName = clazz.getSimpleName();
            if (postfixLength > 0 && simpleName.endsWith(postfix)) {
                simpleName = simpleName.substring(0, simpleName.length() - postfixLength);
            }
            returnSet.add(simpleName);
            //Wenn der SimpleClassName mit einem Großbuchstaben anfing, dann auch den SimpleClassName mit Kleinbuchstaben ins Set schreiben.
            //Wenn er mit einem Kleinbuchstaben los geht, dann Groß machen. Das ist nätig, weil due ObjectProperties in OWL laut Konvention
            //mit einem Kleinbuchstaben beginnen, die korrespondierende Java-Klasse, aber immer groß beginnt, aber sonst gleich heißt (bis
            //auf einen fixen Namenszusatz wie "_Edge")
            char firstChar = simpleName.charAt(0);
            firstChar = Character.isUpperCase(firstChar) ? Character.toLowerCase(firstChar) : Character.toUpperCase(firstChar);
            simpleName = firstChar + simpleName.substring(1);
            returnSet.add(simpleName);
        }
        return returnSet;
    }

    /**
     *
     */
    private final Map<Class<? extends ModelElement>, Object[]> elementClassToNamePattern = new HashMap<>();

    /**
     * Fügt ein Pattern zur Erzeugung des Namens der Elemente aus der Importquelle hinzu.
     *
     * @param elementClass
     * @param patternObjects
     */
    protected void addNamePattern(final Class<? extends ModelElement> elementClass, final Object... patternObjects) {
        elementClassToNamePattern.put(elementClass, patternObjects);
    }

    /**
     * Liefert den localName der OntProperty mit der Description oder dessen URI
     *
     * @return
     */
    public abstract String getDescriptionPropertyUriOrLocalName();

}
