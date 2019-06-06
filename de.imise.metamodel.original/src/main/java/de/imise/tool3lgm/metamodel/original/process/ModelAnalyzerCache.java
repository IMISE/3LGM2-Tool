package de.imise.tool3lgm.metamodel.original.process;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import de.imise.tool3lgm.graphtools.metamodel.MetaModel;
import de.imise.tool3lgm.graphtools.metamodel.elements.ModelElement;
import de.imise.tool3lgm.graphtools.model.GDCollection;
import de.imise.tool3lgm.metamodel.original.edge.AwbKommssVerbindung;
import de.imise.tool3lgm.metamodel.original.edge.DbsDatVerbindung;
import de.imise.tool3lgm.metamodel.original.edge.DoksDokVerbindung;
import de.imise.tool3lgm.metamodel.original.edge.ObjLogspVerbindung;
import de.imise.tool3lgm.metamodel.original.edge.ObjReprVerbindung;
import de.imise.tool3lgm.metamodel.original.node.Anwendungsbaustein;
import de.imise.tool3lgm.metamodel.original.node.Objekttyp;
import de.imise.tool3lgm.metamodel.original.node.Repraesentationsform;

/**
 * Stellt Funktionen bereit, um 'teure' Anfragen an ein Model effizient wiederholen zu können.
 *
 * @author AXS Created on 15.07.2008
 */

public class ModelAnalyzerCache {

    /**
     * Das HauptDokument, das analysiert werden soll
     */
    private final GDCollection gdcoll;

    /**
     * Mapppt von einem Anwendungsbausteins auf eine Set aller seiner eigenen Schnittstellen und die
     * seiner Parts und Parents. Wir sehen die Teil-Von-Beziehung zwischen Anwendungsbausteinen als
     * bidirektionale Kommunikationsbeziehung, über die jeder Objekttyp ausgetauscht werden kann.
     * Das bedeutet, dass die Eigenschaften einer Schnittstelle, die ein AWB besitzt, an alle
     * anderen AWBs übergehen, die mit dem AWB über einen beliebigen Pfad aus Teil-Von-Beziehungen
     * verbunden sind.
     */
    private final HashMap<ModelElement, Set<ModelElement>> appSysToInterfaceSet = new HashMap<>();

    /**
     * Mappt für jeden <code>Anwendungsbaustein</code> auf die Liste aller seiner Eltern, Kinder und
     * Geschwister - also aller Anwendungsbausteine, mit denen er eine Einheit bildet.
     */
    private final HashMap<ModelElement, Collection<ModelElement>> appSysToSameAppSysCollection = new HashMap<>();

    /**
     * Mappt von einem Objekttyp auf eine <code>Collection</code>, die ihn selbst und alle seine
     * übergeordneten Objekttypen enthält.
     */
    private final HashMap<ModelElement, Collection<ModelElement>> objectTypeToObjectTypeAndParentsCollection = new HashMap<>();

    /**
     * Mappt von einem Objekttyp auf alle Anwendungsbausteine, auf denen er als gespeichert gilt.
     * Dies sind alle Eltern, Kindern und Geschwister der Anwendungssysteme, die den Objekttypen
     * direkt speichern.
     */
    private final HashMap<ModelElement, Set<ModelElement>> objectTypeToFullStoringAppSysSet = new HashMap<>();

    /**
     * Mappt von einem Objekttyp auf alle Anwendungsbausteine, auf denen er direkt gespeichert wird.
     */
    private final HashMap<ModelElement, Set<ModelElement>> objectTypeToDirectStoringAppSysSet = new HashMap<>();

    /**
     * Mappt von einem Objekttyp auf alle Anwendungsbausteine mit, die den Objekttyp als Master
     * speichern und alle Eltern, Kindern und Geschwister dieses Knotens.
     */
    private final HashMap<ModelElement, Set<ModelElement>> objectTypeToFullMasterAppSysSet = new HashMap<>();

    /**
     * Mappt von einem Objekttyp auf ein Set alle Anwendungsbausteine die den Objekttyp oder einen
     * seiner Parent-Objekttypen als Master speichern. Dies darf aus Konsistenzgründen eigentlich
     * immer nur einer sein.
     */
    private final HashMap<ModelElement, Set<ModelElement>> objectTypeToDirectMasterAppSysSet = new HashMap<>();

    /**
     * Mappt von einem Objekttyp auf seine Master-Datenbanksysteme (das darf eigentlich immer nur 1
     * sein)
     */
    private final HashMap<ModelElement, Set<ModelElement>> objectTypeToMasterDBSSet = new HashMap<>();

    /**
     * @param gdcoll Modell, für das ein Analyzer angelegt werden soll
     */
    public ModelAnalyzerCache(final GDCollection gdcoll) {
        super();
        this.gdcoll = gdcoll;
    }

    /**
     * Liefert alle Anwendungssysteme, die diesen Objekttyp oder einen Oberobjekttyp von ihm als
     * Master speichern, sowie alle Teil- und Oberanwendungssysteme davon.
     *
     * @param objectType
     * @return
     */
    @Deprecated
    public Set<ModelElement> _getFullMasterApplicationSystems(final Objekttyp objectType) {
        Set<ModelElement> masterSetObject = objectTypeToFullMasterAppSysSet.get(objectType);
        if (masterSetObject != null) {
            return masterSetObject;
        }
        initOTStorageAndMaster(objectType);
        return objectTypeToFullMasterAppSysSet.get(objectType);
    }

    /**
     * Liefert alle Anwendungssysteme, die den Objekttyp speichern. Die Master-Eigenschaft zählt
     * dabei nicht. Es sind alle Teile und alle Oberbausteine der Baustein enthalten, die den
     * Objekttyp eigentlich speichern.
     *
     * @param objectType
     * @return
     */
    @Deprecated
    public Set<ModelElement> _getFullStorageApplicationSystems(final ModelElement objectType) {
        Set<ModelElement> storingSetObject = objectTypeToFullStoringAppSysSet.get(objectType);
        if (storingSetObject != null) {
            return storingSetObject;
        }
        initOTStorageAndMaster((Objekttyp) objectType);
        return objectTypeToFullStoringAppSysSet.get(objectType);
    }

    /**
     * @param objectType
     * @return
     */
    @Deprecated
    public Set<ModelElement> _getMasterDBS(final Objekttyp objectType) {
        Set<ModelElement> masterSetObject = objectTypeToMasterDBSSet.get(objectType);
        if (masterSetObject != null) {
            return masterSetObject;
        }
        initOTStorageAndMaster(objectType);
        return objectTypeToMasterDBSSet.get(objectType);
    }

    /**
     * Liefert ein Set von Elementen, in dem alle übergebenen Elemente und alle mit den übergebenen
     * durch Teil-Von-Beziehungen irgendwie, also auch über mehrere andere Anwendungssysteme,
     * verbundenen Elemente enthalten sind.
     */
    public Set<ModelElement> expandPartOfElementSet(final Collection<ModelElement> modelElements) {
        HashSet<ModelElement> returnSet = new HashSet<>(modelElements.size() * 2);
        for (ModelElement me : modelElements) {
            if (me instanceof Anwendungsbaustein) {
                returnSet.addAll(getSameApplicationSystems(me));
            } else {
                returnSet.addAll(me.getPartAndParentElements());
            }
        }
        return returnSet;
    }

    /**
     * Liefert das Modell des Caches
     *
     * @return
     */
    public GDCollection getCollection() {
        return gdcoll;
    }

    /**
     * Liefert die Menge aller Anwendungssysteme, auf denen der Objekttyp direkt gespeichert wird
     * oder die direkt Master des Objekttyps sind.
     *
     * @param objectType
     * @return
     */
    public Set<ModelElement> getDirectMasterAndStorageApplicationSystems(final Objekttyp objectType) {
        Set<ModelElement> master = getDirectMasterApplicationSystems(objectType);
        Set<ModelElement> storage = getDirectStorageApplicationSystems(objectType);
        HashSet<ModelElement> returnSet = new HashSet<>(master.size() + storage.size());
        returnSet.addAll(master);
        returnSet.addAll(storage);
        return returnSet;
    }

    /**
     * Liefert alle Anwendungssysteme, die diesen Objekttyp oder einen Oberobjekttyp von ihm direkt
     * als Master speichern.
     *
     * @param objectType
     * @return
     */
    public Set<ModelElement> getDirectMasterApplicationSystems(final Objekttyp objectType) {
        Set<ModelElement> masterSetObject = objectTypeToDirectMasterAppSysSet.get(objectType);
        if (masterSetObject != null) {
            return masterSetObject;
        }
        initOTStorageAndMaster(objectType);
        return objectTypeToDirectMasterAppSysSet.get(objectType);
    }

    /**
     * Liefert alle Anwendungssysteme, die den Objekttyp direkt speichern. Die Master-Eigenschaft
     * zählt dabei nicht. Direktes Speichern bedeutet dabei, dass der Objekttyp selbst oder einer
     * seiner übergeordneten Objekttypen gespeichert wird.
     *
     * @param objectType
     * @return
     */
    public Set<ModelElement> getDirectStorageApplicationSystems(final Objekttyp objectType) {
        Set<ModelElement> storingSetObject = objectTypeToDirectStoringAppSysSet.get(objectType);
        if (storingSetObject != null) {
            return storingSetObject;
        }
        initOTStorageAndMaster(objectType);
        return objectTypeToDirectStoringAppSysSet.get(objectType);
    }

    /**
     * @param applicationSystem
     * @return <code>Collection</code> of all interfaces at the same application system
     */
    public Set<ModelElement> getInterfaces(final ModelElement applicationSystem) {
        Set<ModelElement> interfaceListObject = appSysToInterfaceSet.get(applicationSystem);
        if (interfaceListObject != null) {
            return interfaceListObject;
        }
        Collection<ModelElement> sameAWBCol = getSameApplicationSystems(applicationSystem);

        // neue Schnittstellenliste, die für jeden Einzel-AWB eines Gesamt-AWB identisch sein wird
        Set<ModelElement> sameInterfaceSet = new HashSet<>();
        for (ModelElement sameAWB : sameAWBCol) {
            // hole alle seine Schnittstellen und füge sie zur Gesamtliste hinzu
            sameInterfaceSet.addAll(sameAWB.getConnectedElements(AwbKommssVerbindung.class));
            // lege die Gesamtliste für den Einzel-AWB in die globale HashMap
            appSysToInterfaceSet.put(sameAWB, sameInterfaceSet);
        }
        return sameInterfaceSet;
    }

    /**
     * Liefert alle Elemente der angegebenen Art, die mehr als einen Parent besitzen.
     *
     * @param elementClass
     * @return
     */
    public Set<ModelElement> getMultipleParentElements(final Class<? extends ModelElement> elementClass) {
        HashSet<ModelElement> returnSet = new HashSet<>();
        MetaModel metaModel = gdcoll.getMetaModel();
        if (metaModel.canHaveParts(elementClass)) {
            for (ModelElement me : gdcoll.getMainGraphDocument().getModelItems(elementClass, true, true)) {
                if (me.getDirectParentElements().size() > 1) {
                    returnSet.add(me);
                }
            }
        }
        return returnSet;
    }

    /**
     * Returns a collection with the given objecttype and all of his parents
     *
     * @param objectType
     * @return <code>Collection</code> with the given objecttype and all of his parents
     */
    public Collection<ModelElement> getObjectTypeAndParents(final ModelElement objectType) {
        Collection<ModelElement> objectTypeAndParents = objectTypeToObjectTypeAndParentsCollection.get(objectType);
        if (objectTypeAndParents != null) {
            return objectTypeAndParents;
        }
        Collection<ModelElement> all = objectType.getParentElements(true);
        for (ModelElement me : all) {
            objectTypeToObjectTypeAndParentsCollection.put(me, all);
        }
        return all;
    }

    /**
     * Returns a collection of all parts, parents and brothers of the given application system. The
     * collection includes the given application system, too.
     *
     * @param applicationSystem
     * @return <code>Collection</code> of all application systems at the same application system
     */
    public Collection<ModelElement> getSameApplicationSystems(final ModelElement applicationSystem) {
        Collection<ModelElement> sameAssSysListObject = appSysToSameAppSysCollection.get(applicationSystem);
        if (sameAssSysListObject != null) {
            return sameAssSysListObject;
        }
        Collection<ModelElement> all = applicationSystem.getPartAndParentElements();
        for (ModelElement me : all) {
            appSysToSameAppSysCollection.put(me, all);
        }
        return all;
    }

    /**
     * Initialisiert die <code>HashMap</code>s <code>objekttypToMasterAWBs</code> und <code>objekttypToStoringAWBs</code>.
     */
    private void initOTStorageAndMaster(final Objekttyp objectType) {
        Collection<ModelElement> otAndParents = getObjectTypeAndParents(objectType);
        HashSet<ModelElement> fullStoreAWB = new HashSet<>();
        HashSet<ModelElement> directStoreAWB = new HashSet<>();
        HashSet<ModelElement> fullMasterAWBs = new HashSet<>();
        HashSet<ModelElement> directMasterAWBs = new HashSet<>();
        HashSet<ModelElement> allMasterDBS = new HashSet<>();
        for (ModelElement otOrParent : otAndParents) {
            // hole das Master-DBS oder Dok-Sammlung
            for (ModelElement master : otOrParent.getConnectedElements(ModelElement.class, ObjLogspVerbindung.class)) {
                allMasterDBS.add(master);
                // das ist eigentlich immer nur 1 AWB aber trotzdem nicht nur
                // auf Element 0 gehen
                for (ModelElement awb : master.getConnectedElements(Anwendungsbaustein.class)) {
                    directMasterAWBs.add(awb);
                    fullMasterAWBs.addAll(getSameApplicationSystems(awb));
                }
            }

            // hole alle AWB, die ein DBS oder DokS haben, die den Objekttyp speichert
            for (ModelElement repraesForm : otOrParent.getConnectedElements(Repraesentationsform.class, ObjReprVerbindung.class)) {
                Collection<ModelElement> storing = repraesForm.getConnectedElements(ModelElement.class, DbsDatVerbindung.class);
                storing.addAll(repraesForm.getConnectedElements(ModelElement.class, DoksDokVerbindung.class));
                for (ModelElement storePlace : storing) {
                    // das ist eigentlich immer nur 1 AWB aber trotzdem nicht
                    // nur auf Element 0 gehen
                    for (ModelElement awb : storePlace.getConnectedElements(Anwendungsbaustein.class)) {
                        directStoreAWB.add(awb);
                        fullStoreAWB.addAll(getSameApplicationSystems(awb));
                    }
                }
            }
        }
        objectTypeToMasterDBSSet.put(objectType, allMasterDBS);
        objectTypeToFullMasterAppSysSet.put(objectType, fullMasterAWBs);
        objectTypeToDirectMasterAppSysSet.put(objectType, directMasterAWBs);
        objectTypeToFullStoringAppSysSet.put(objectType, fullStoreAWB);
        objectTypeToDirectStoringAppSysSet.put(objectType, directStoreAWB);
    }
}
