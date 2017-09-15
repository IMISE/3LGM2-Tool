package de.imise.tool3lgm.graphtools.userfield.dialog.valueinput.table.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import com.google.common.collect.ImmutableSet;

import de.imise.tool3lgm.graphtools.metamodel.ModelElement;
import de.imise.tool3lgm.graphtools.model.GraphDocument;
import de.imise.tool3lgm.graphtools.userfield.UserField;
import de.imise.util.NamedObjectContainer;

/**
 * Erzeugt ein neues Model für den Table des <code>AbstractElementTypeUserFieldEditorPanel</code>s
 *
 * @author Ich
 * @create 19.08.2015
 */
public class GeneralUserFieldTableModel extends AbstractUserFieldTableModel {

    /**
     * Erzeugt ein neues Model für den Table des <code>AbstractElementTypeUserFieldEditorPanel</code>s
     *
     * @param doc
     */
    public GeneralUserFieldTableModel(final GraphDocument doc) {
        super(doc);
    }

    /**
     * @param doc
     * @param elementClass in der <code>elementTypeBox</code> des <code>AbstractElementTypeUserFieldEditorPanel</code>s ausgewählte Klasse
     * @param elementClass
     * @param showTopLevel
     * @param showInner
     * @param showLeafs
     * @param userFieldStyle
     */
    public GeneralUserFieldTableModel(final GraphDocument doc, final Class<? extends ModelElement> elementClass, final boolean showTopLevel, final boolean showInner, final boolean showLeafs, final UserField.Style userFieldStyle) {
        this(doc, elementClass, showTopLevel, showInner, showLeafs, ImmutableSet.of(userFieldStyle));
    }

    /**
     * @param doc
     * @param elementClass in der <code>elementTypeBox</code> des <code>AbstractElementTypeUserFieldEditorPanel</code>s ausgewählte Klasse
     * @param elementClass
     * @param showTopLevel
     * @param showInner
     * @param showLeafs
     * @param userFieldStyles
     */
    public GeneralUserFieldTableModel(final GraphDocument doc, final Class<? extends ModelElement> elementClass, final boolean showTopLevel, final boolean showInner, final boolean showLeafs, final Set<UserField.Style> userFieldStyles) {
        super(doc);
        setData(elementClass, showTopLevel, showInner, showLeafs, userFieldStyles);
    }

    /**
     * Erstellt und setzt Kennzahlen-Modeldaten
     *
     * @param elementClass
     * @param showTopLevel
     * @param showInner
     * @param showLeafs
     * @param userFieldStyle
     */
    private void setData(final Class<? extends ModelElement> elementClass, final boolean showTopLevel, final boolean showInner, final boolean showLeafs, final Set<UserField.Style> userFieldStyle) {
        // Ermitteln der UserFields zu elementClass
        List<UserField> userFieldList = definitions.getUserFields(elementClass, userFieldStyle);

        //Ermitteln der ModelElemente zu elementClass
        List<ModelElement> allModelElements = doc.getModelItems(elementClass, true, true);
        List<ModelElement> modelElements = new ArrayList<>(allModelElements.size());
        //TODO:FST,XHB. Wenn die Edge PDVBKAWBVerb übergeben wurde, bleibt allModelElements leer. Ist auch richtig,solange es keine Soclhe Verbindung gibt.
        // Dann sollte aber auch keine Exception mehr fliegen. prüf mal bitte, warum das so ist?!

        for (int i = 0; i < allModelElements.size(); i++) {
            ModelElement me = allModelElements.get(i);
            if (showTopLevel && !me.hasDirectParentContainer(doc)) { // Top-Level-E. anfügen
                modelElements.add(me);
            } else if (showInner && me.hasDirectParentContainer(doc) && me.hasDirectPartContainer(doc)) { // Innere E. anfügen
                modelElements.add(me);
            } else if (showLeafs && !me.hasDirectPartContainer(doc)) { // Blatt-E. anfügen
                modelElements.add(me);
            }
        }

        if (modelElements.size() == 0 || userFieldList.size() == 0) {
            modelElements.clear();
            userFieldList.clear();
        }

        Vector<NamedObjectContainer<?>> rowIdentifiers = new Vector<>(modelElements.size());
        // RowHeader aufbauen
        for (ModelElement me : modelElements) {
            NamedObjectContainer<ModelElement> rowElementContainer = new NamedObjectContainer<>(me, me.getName());
            rowIdentifiers.add(rowElementContainer);
        }

        Vector<NamedObjectContainer<?>> columnIdentifiers = new Vector<>(userFieldList.size());
        // ColumnHeader aufbauen
        for (UserField userField : userFieldList) {
            NamedObjectContainer<UserField> columnUserFieldContainer = new NamedObjectContainer<>(userField, userField.getName());
            columnIdentifiers.add(columnUserFieldContainer);
        }

        //DataVector aufbauen
        Object[][] data = new Object[modelElements.size()][userFieldList.size()];
        for (int i = 0; i < data.length; i++) {
            ModelElement me = modelElements.get(i);
            for (int j = 0; j < data[0].length; j++) {
                UserField uf = userFieldList.get(j);
                String value = uf.getValue(me);
                data[i][j] = new NamedObjectContainer<>(uf, value);
            }
        }

        //Daten setzen
        setDataVector(data, columnIdentifiers, rowIdentifiers);
    }
}
