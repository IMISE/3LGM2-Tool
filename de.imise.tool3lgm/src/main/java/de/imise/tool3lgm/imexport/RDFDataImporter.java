package de.imise.tool3lgm.imexport;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

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

import com.google.common.base.Strings;

import de.imise.tool3lgm.graphtools.metamodel.MetaModel;
import de.imise.tool3lgm.graphtools.metamodel.elements.Edge;
import de.imise.tool3lgm.graphtools.metamodel.elements.ModelElement;
import de.imise.tool3lgm.graphtools.metamodel.elements.Node;
import de.imise.tool3lgm.graphtools.model.GDCollection;
import de.imise.tool3lgm.userproperties.UserProperties;
import de.imise.util.DataPrinter;
import de.imise.util.StringUtils;

/**
 * @author AXS (26 Jun 2019)
 */
public abstract class RDFDataImporter extends UrlSourceDataImporter<OntResource> implements DataPrinter {

    ///////////////////////////////////
    // Eigentlicher Start der Klasse //
    ///////////////////////////////////

    /**
     * @param urlString
     */
    public RDFDataImporter(final String urlString) {
        super(urlString);
    }

    /**
     * @return Anhang des Namens, um den der Name von den 3LGM-Kantenklassen des Import-Metamodells von den Namen der zu importierdenden
     *         ObjectProperties abweicht
     */
    public abstract String getEdgeClassNamePostfix();

    @Override
    public boolean importData(final String urlString) {
        OntModel ontModel = ModelFactory.createOntologyModel();
        ontModel.read(urlString);
        importNodes(ontModel);
        importEdges(ontModel);
        return true;
    }

    /**
     * Fragt in der Ontologie alle Class-Knoten ab, deren Klasse denselben Namen hat, wie eine Klasse aus dem Metamodell des zu füllenden
     * 3LGM2-Modells und legt in diesem 3LGM2-Modell dafür einen Knoten an. Außerdem werden die Class-Knoten und der korrespondierende 3LGM2-Knoten in
     * einer Map in DataImporter gespeichert.
     *
     * @param ontModel Quellmodell
     */
    private void importNodes(final OntModel ontModel) {
        GDCollection gdcoll = getCollection();
        MetaModel metaModel = gdcoll.getMetaModel();
        Collection<String> classNames = getSimpleClassNames(metaModel.allNodesSet, "");
        Locale locale = UserProperties.getLocale();
        String localeCountry = locale.getCountry();
        for (Iterator<OntClass> ontClasses = ontModel.listNamedClasses(); ontClasses.hasNext();) {
            OntClass ontClass = ontClasses.next();
            String ontClassName = ontClass.getLocalName();
            if (classNames.contains(ontClassName)) {
                Class<? extends ModelElement> lgmClass = metaModel.getClassForName(ontClassName);
                Class<? extends Node> lgmNodeClass = lgmClass.asSubclass(Node.class);
                print(ontClassName + " > " + lgmClass);
                print(ontClass.listInstances(true));
                int i = 1;
                for (Iterator<? extends OntResource> ontNodes = ontClass.listInstances(true); ontNodes.hasNext();) {
                    //Aus irgendeinem kruden Grund kommen bei der IntegrationProfile-Klasse auch Instances von IheActor zurück???
                    //Und bei IheAcor auch InheIntegrationProfiles
                    //Das lässt sich durch das nun folgende beheben, indem man aus dem Model über dei URI die Individuals holt und
                    //von denen die Klasse vergleicht.
                    OntResource ontNode = ontNodes.next();
                    String uri = ontNode.getURI();
                    Individual individual = ontModel.getIndividual(uri);
                    OntClass individualOntClass = individual.getOntClass();
                    if (individualOntClass.equals(ontClass)) {
                        String name = ontNode.getLocalName();
                        String description = ontNode.getComment(localeCountry);
                        String hashString = ontNode.getURI();
                        Node lgmNode = addNode(ontNode, lgmNodeClass, name, description, hashString);
                        printe(i++ + "\t" + ontClassName + " -> " + ontNode + "  ->  " + lgmNode);
                    }
                }
            }
        }
    }

    /**
     * Fragt im Quellmodell alle ObjectProperties ab, deren Klassenname auch im Metamodell des Zielmodells als Kantenklassenname vorkommt. Dann werden
     * im Zielmodell die entsprechenden Elemente verlinkt, die die Objectproperty im Quellmodell verbindet.
     *
     * @param ontModel Quellmodell
     */
    private void importEdges(final OntModel ontModel) {
        Map<Statement, String> edgeStatementsToTargetEdgeClassName = getEdgeStatements(ontModel);
        print(edgeStatementsToTargetEdgeClassName);
        int i = 1;
        for (Statement statement : edgeStatementsToTargetEdgeClassName.keySet()) {
            //Statement
            String targetEdgeClassName = edgeStatementsToTargetEdgeClassName.get(statement);
            targetEdgeClassName += getEdgeClassNamePostfix();
            targetEdgeClassName = StringUtils.capitalizeFirstChar(targetEdgeClassName); //Kantenklassennamen sind immer groß geschrieben - zugehörige, gleich heißende EdgeProperty evtl. klein -> Umwandeln
            //Predicate -> Edge
            Property predicate = statement.getPredicate();
            String predicateUri = predicate.getURI();
            String predicateLocalName = predicate.getLocalName();
            //Subject -> startNode
            Resource subjectResource = statement.getSubject();
            String subjectUri = subjectResource.getURI();
            OntResource subject = ontModel.getIndividual(subjectUri);
            Node startNode = getTargetNode(subject);
            //Object -> endNode
            RDFNode objectNode = statement.getObject();
            Resource objectResource = objectNode.asResource();
            String objectUri = objectResource.getURI();
            OntResource object = ontModel.getIndividual(objectUri);
            Node endNode = getTargetNode(object);
            //Edge
            Edge lgmEdge = addEdge(targetEdgeClassName, predicateLocalName, predicateUri, startNode, endNode);
            printe(i++ + "\t" + targetEdgeClassName + " -> " + startNode + "  ->  " + endNode + " " + lgmEdge);
        }
    }

    /**
     * Liefert alle Statements, die eine Kante repräsentieren, die ins Zielmodell übernommen werden muss.
     *
     * @param ontModel
     * @return Map mit allen Statements, die eine zu importierende Kante repräsentieren als Key und dem Namen der daraus zu erzeugenden Kantenart im
     *         Zielmodell als value
     */
    private Map<Statement, String> getEdgeStatements(final OntModel ontModel) {
        Map<Statement, String> edgeStatementToTargetEdgeClassName = new HashMap<>();
        //ObjectProperty -> Kantenklassenname
        Map<ObjectProperty, String> importableObjectPropertiesToTargetEdgeClassName = getImportableObjetctProperties(ontModel);
        Set<ObjectProperty> importableObjectProperties = importableObjectPropertiesToTargetEdgeClassName.keySet();
        StmtIterator statements = ontModel.listStatements();
        Set<OntResource> sourceNodes = getSourceNodes();
        while (statements.hasNext()) {
            //Statement
            Statement statement = statements.next();
            //Predicate
            Property predicate = statement.getPredicate();
            if (importableObjectProperties.contains(predicate)) {
                //Subject
                Resource subject = statement.getSubject();
                if (sourceNodes.contains(subject)) {
                    //Object
                    RDFNode object = statement.getObject();
                    if (sourceNodes.contains(object)) {
                        //Edge
                        String targetEdgeClassName = importableObjectPropertiesToTargetEdgeClassName.get(predicate);
                        edgeStatementToTargetEdgeClassName.put(statement, targetEdgeClassName);
                    }
                }
            }
        }
        return edgeStatementToTargetEdgeClassName;
    }

    /**
     * Bestimmt alle ObjectProperties, die ins Zielmodell als Kante übernommen werden sollen.
     *
     * @param ontModel
     * @return Map, die von ObjectProperties, bei denen der eigene Name oder der einer SuperProperty mit einem Kantenklassennamen aus dem
     *         ImportMetamodell übereinstimmt, auf den Namen Art der ObjectProperty mappt = Klassennamen der ObjectProperty.
     */
    private Map<ObjectProperty, String> getImportableObjetctProperties(final OntModel ontModel) {
        Map<ObjectProperty, String> importableObjectPropertiesToTargetEdgeClassName = new HashMap<>();
        MetaModel metaModel = gdcoll.getMetaModel();
        //gültige ObjectProperty-Klassennamen
        Collection<String> objectPropertyNames = getSimpleClassNames(metaModel.allEdgesSet, getEdgeClassNamePostfix());
        for (Iterator<ObjectProperty> objectProperties = ontModel.listObjectProperties(); objectProperties.hasNext();) {
            //ObjectProperty-Instanz
            ObjectProperty objectProperty = objectProperties.next();
            String name = objectProperty.getLocalName();
            //solange die Oberklassen durchsuchen, bis ein gültiger ObjectProperty-Klassenname gefunden wurde oder nicht
            String superPropertyName = name;
            if (!objectPropertyNames.contains(name)) {
                superPropertyName = getImportableSuperObjectPropertyName(objectProperty, objectPropertyNames);
                if (!Strings.isNullOrEmpty(superPropertyName)) {
                    importableObjectPropertiesToTargetEdgeClassName.put(objectProperty, superPropertyName);
                }
            }
        }
        return importableObjectPropertiesToTargetEdgeClassName;
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

}
