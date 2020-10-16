package de.imise.tool3lgm.metamodel.service;

import java.util.Collection;

import com.google.common.collect.ImmutableSet;

import de.imise.tool3lgm.graphtools.consistency.ModelValidatorDefinition;
import de.imise.tool3lgm.graphtools.consistency.error.condition.MissingPathErrorCheckCondition;
import de.imise.tool3lgm.graphtools.metamodel.MetaModelDefinition;
import de.imise.tool3lgm.graphtools.path.metapaths.SimpleMetaPath;
import de.imise.tool3lgm.graphtools.path.metapaths.SimpleMetaPathCreator;
import de.imise.tool3lgm.metamodel.service.edge.ApplicationSystem_IheActorInstance_Edge;
import de.imise.tool3lgm.metamodel.service.edge.IheActor_IheActorInstance_Edge;
import de.imise.tool3lgm.metamodel.service.edge.IheActor_IheActor_MustBeGroupedWith_Edge;
import de.imise.tool3lgm.metamodel.service.node.ApplicationSystem;
import de.imise.tool3lgm.metamodel.service.node.IheActor;
import de.imise.tool3lgm.metamodel.service.node.IheActorInstance;

/**
 * @author AXS (24.03.2020)
 */
public class TLGMServiceModelValidatorDefinition extends ModelValidatorDefinition {

    @Override
    public Class<? extends MetaModelDefinition> getMetaModelDefinitionClass() {
        return TLGMServiceMetaModel.class;
    }

    // wenn man keine Error-Solution für einen Min oder Max-Fehler findet, wird ein neues
    // PathConnectionPanel angefügt, das den Fehler anzeigt wenn die minimale Kardinalität
    //eines initalSubTypes unterschritten ist, dann einfach einen neuen anlegen
    //(Anwendungsparogramm bei AWB, Orgplan bei KAWB) Kanten ohne start und end einfach
    //löschen (das macht der ModelCleaner beim Einlesen und Speichern)

    //    @Override
    //    protected final Collection<ErrorSolution> getCardinalityErrorSolutions() {
    //        SimpleMetaPathCreator smpc = new SimpleMetaPathCreator(this);
    //        SimpleMetaPath pathToPropertyDialogElement = smpc.createSimpleMetaPath(IheActorInstance.class, ApplicationSystem.class, ApplicationSystem_IheActorInstance_Edge.class);
    //        SimpleMetaPath panelMetaPath = smpc.createSimpleMetaPath(ApplicationSystem.class, IheActor.class, ApplicationSystem_IheActorInstance_Edge.class, IheActor_IheActorInstance_Edge.class);
    //        ErrorSolution solution1 = new CardinalityErrorSolution(IheActor_IheActor_MustBeGroupedWith_Edge.class, pathToPropertyDialogElement, panelMetaPath);
    //        return ImmutableList.of(solution1);
    //    }

    /**
     * Liste aller Elementarten, die bei Unterschreitung der Anzahl der zugehörigen Kantenart sofort
     * gelöscht werden. Das hier ist nur beispielhaft aus dem
     *
     * @return
     */
    //    private final Pair<Class<? extends ModelElement>, Class<? extends Edge>>[] MINCARDINALITY_NO_SOLUTION_ERRORS = {
    //            new Pair<>(DBKonfiguration.class, PdvbkAwbVerbindung.class), new Pair<>(ABKonfiguration.class, AwbkAufOrgVerbindung.class),
    //    };

    @Override
    public Collection<MissingPathErrorCheckCondition> getMissingPathErrorCheckConditions() {
        //Der folgende MetaPfad beschreibt die nicht erfüllte must-be-grouped-with-Beziehung ausgehend vom Anwendungssystem. Fehlt eine must-be-grouped-with-Beziehung,
        //dann kommt der Fehler, dass diesem Anwendungssystem eine weitere IheActorInsatnce zugeordnet werden muss. Hat irgendeine zu gruppierende IheActorInstance gar
        //kein Anwendungssystem, dann kommt kein Fehler.
        //Problem: hiermit funktioniert die Kaskade von Anhängigkeiten  nicht richtig. Also eine Bedingung gilt schon als erfüllt, sobald nur eine einzige, aber
        //nicht alle benötigten IheActorInstances an dem Anwendungssystem hängen. Warum das so ist, müsste mal nachvollzogen werden, weil eigentlich halte ich das
        //hier für den besseren Pfad, da kein Fehler entsteht, wenn die Ihe ActorInstance kein Anwendungssystem hat. Bsp.-Kaskade: XDS-Doc-Consumer mal mit allen
        //Varianten des Hinzufügens benötigter Anhängigkeiten ausprobieren.
        SimpleMetaPathCreator smpc = new SimpleMetaPathCreator(this);
        SimpleMetaPath toRealStartElements = smpc.createSimpleMetaPath(ApplicationSystem.class, IheActorInstance.class, ApplicationSystem_IheActorInstance_Edge.class);
        SimpleMetaPath toConnectableElements = smpc.createSimpleMetaPath(IheActorInstance.class, IheActor.class, IheActor_IheActorInstance_Edge.class, IheActor_IheActor_MustBeGroupedWith_Edge.class);
        SimpleMetaPath toConnectedElements = smpc.createSimpleMetaPath(IheActorInstance.class, IheActor.class, ApplicationSystem_IheActorInstance_Edge.class, ApplicationSystem_IheActorInstance_Edge.class, IheActor_IheActorInstance_Edge.class);
        SimpleMetaPath errorSolutionPanelMetaPath = smpc.createSimpleMetaPath(ApplicationSystem.class, IheActor.class, ApplicationSystem_IheActorInstance_Edge.class, IheActor_IheActorInstance_Edge.class);
        MissingPathErrorCheckCondition missingPathCondition = new MissingPathErrorCheckCondition(toRealStartElements, toConnectableElements, toConnectedElements, errorSolutionPanelMetaPath);
        return ImmutableSet.of(missingPathCondition);
    }

    /////////////////////////////////////
    //  ConsistencyConditionMetaPaths  //
    /////////////////////////////////////

    //    @Override
    //    public Map<ConsistencyCheckSectionMetaPath, Class<? extends Edge>> getConsistencyConditionMissingConnectedElementsMetaPaths() {
    //        //AXS am 08.09.20220:
    //        //Dieser erste MetaPath beschreibt den Fehler aus Sicht der IheActorInstance. Fehlt für sie der muss-gruppiert-werden-mit-Partner, dann kommt der
    //        //Fehler. Problem: der Fehler sagt aus, dass dem Anwendungssystem, dieser IheActorInstance ein weiteres Anwendungssystem zugeordnet werden muss.
    //        //Das kommt auch, wenn die IheActorInstance gar keinem Anwendungssystem zugeordnet ist. Dadurch aber gibt es das sogenannte Element zur Fehlerbehebung
    //        //nicht, dessen Eigenschaftsdialog man öffnen könnte, um den Fehler zu beheben (denn das geht nur durch Öffnen des Dialoes für das zugehörige
    //        //Anwendungssystem, was ja nicht da ist). Dadurch gilt dieser Fehler automatisch als nicht behebbar. Dadurch würde die IheActorInsance beim Einlesen
    //        //eines solchen fehlerhaften Modells automatisch gelöscht werden (in #clearUnfixableErrors()), was nur dadurch verhindert wird, dass alle diese
    //        //MissingPathErrors in #clearUnfixableErrors() ignoriert werden. Das ist auch ok so, weil bei beliebig langen Pfaden nie weiß, warum der Fehler
    //        //aufgetreten ist und das Element somit nicht einfach löschen sollte.
    //        //Es gab mehrere Möglichkeiten, das nicht erwünschte Löschen zu umgehen:
    //        // 1.) den Fehler nicht aus Sicht der IheActorInsance sondern für das Anwenundungssystem generieren. Dann kommt der Fehler nur, wenn auch tatsächlich
    //        //ein Anwendungssystem vorhanden ist. ABER (siehe unten): die Kaskade der Abhängigkeiten funktioniert dann nciht mehr richtig
    //        //2.) Generell festlegen, dass MissingPathErrors niemals als unfixable gelten und somit die betreffenden Elemente nicht gelöscht werden. Die normalen
    //        //MIN-MAX-Errors sind davon nicht betroffen, da sie genau für eine einzelne Kante gelten.
    //        //3.) nicht wirklich praktikabel aber möglich: Man definiert noch eine Bedingung, die zutreffen muss, damit der Fehler anwendbar ist. In dem Fall hier,
    //        //müsste man den ersten Pfadschritt irgendwie als Bedingung angeben, dass es ihn geben muss, damit der zweite Pfadschritt als fehlerhaft angesehen werden
    //        //kann.
    //        //Fazit: Ich habe mich für 2.) entschieden, also MissingPathError-Elemente werden niemals gelöscht und es wird weiterhin der MetaPfad mit der
    //        //funktionierenden Abhängigkeits-Kaskade ausgeführt. Weiterhin werde ich in der Fehlertabelle der Konsistenzprüfung verhindern, dass Fehler angezeigt werden,
    //        //die man gar nicht beheben kann. Denn eigentlich müsste für die IheActorInstance noch ein Fehler oder eine Warnung erzeugt werden, dass sie mit keinem
    //        //Anwendungssystem verbunden ist.
    //        SimpleMetaPath consistencyConditionSubMetaPath1 = smp(IheActorInstance.class, IheActor.class, IheActor_IheActorInstance_Edge.class, IheActor_IheActor_MustBeGroupedWith_Edge.class);
    //        SimpleMetaPath consistencyConditionSubMetaPath2 = smp(IheActorInstance.class, IheActor.class, ApplicationSystem_IheActorInstance_Edge.class, ApplicationSystem_IheActorInstance_Edge.class, IheActor_IheActorInstance_Edge.class);
    //        ConsistencyCheckSectionMetaPath consistencyConditionMetaPathActorInstanceMustBeGroupedWith = new ConsistencyCheckSectionMetaPath("PATH_IheActorInstance_mustBeGroupedWith_IheActor", consistencyConditionSubMetaPath1,
    //                consistencyConditionSubMetaPath2);
    //
    //        //Der folgende MetaPfad beschreibt die nicht erfüllte must-be-grouped-with-Beziehung ausgehend vom Anwendungssystem. Fehlt eine must-be-grouped-with-Beziehung,
    //        //dann kommt der Fehler, dass diesem Anwendungssystem eine weitere IheActorInsatnce zugeordnet werden muss. Hat irgendeine zu gruppierende IheActorInstance gar
    //        //kein Anwendungssystem, dann kommt kein Fehler.
    //        //Problem: hiermit funktioniert die Kaskade von Anhängigkeiten  nicht richtig. Also eine Bedingung gilt schon als erfüllt, sobald nur eine einzige, aber
    //        //nicht alle benötigten IheActorInstances an dem Anwendungssystem hängen. Warum das so ist, müsste mal nachvollzogen werden, weil eigentlich halte ich das
    //        //hier für den besseren Pfad, da kein Fehler entsteht, wenn die Ihe ActorInstance kein Anwendungssystem hat. Bsp.-Kaskade: XDS-Doc-Consumer mal mit allen
    //        //Varianten des Hinzufügens benötigter Anhängigkeiten ausprobieren.
    //        SimpleMetaPath consistencyConditionSubMetaPath3 = smp(ApplicationSystem.class, IheActor.class, ApplicationSystem_IheActorInstance_Edge.class, IheActor_IheActorInstance_Edge.class, IheActor_IheActor_MustBeGroupedWith_Edge.class);
    //        SimpleMetaPath consistencyConditionSubMetaPath4 = smp(ApplicationSystem.class, IheActor.class, ApplicationSystem_IheActorInstance_Edge.class, IheActor_IheActorInstance_Edge.class);
    //        ConsistencyCheckSectionMetaPath consistencyConditionApplicationSystemNeedsGroupingOfIheActorInstances = new ConsistencyCheckSectionMetaPath("PATH_ApplicationSystem_needsGroupingOf_IheActorInstances", consistencyConditionSubMetaPath3,
    //                consistencyConditionSubMetaPath4);
    //
    //        //the identifier for the corresponding ErrorSolution is the IheActor_IheActor_MustBeGroupedWith_Edge.class
    //        //return ImmutableMap.of(consistencyConditionMetaPathActorInstanceMustBeGroupedWith, IheActor_IheActor_MustBeGroupedWith_Edge.class);//, consistencyConditionApplicationSystemNeedsGroupingOfIheActorInstances,IheActor_IheActor_MustBeGroupedWith_Edge.class);
    //        return ImmutableMap.of(consistencyConditionApplicationSystemNeedsGroupingOfIheActorInstances, IheActor_IheActor_MustBeGroupedWith_Edge.class);
    //    }

}
