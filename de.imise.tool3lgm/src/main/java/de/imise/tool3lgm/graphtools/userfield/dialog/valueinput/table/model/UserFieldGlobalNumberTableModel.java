package de.imise.tool3lgm.graphtools.userfield.dialog.valueinput.table.model;

import static de.imise.tool3lgm.Tool3lgmConstants.getResString;

import java.util.ArrayList;
import java.util.Vector;

import de.imise.tool3lgm.graphtools.model.GDCollection;
import de.imise.tool3lgm.graphtools.model.GraphDocument;
import de.imise.tool3lgm.graphtools.userfield.definition.UserField;
import de.imise.util.NamedObjectContainer;

public class UserFieldGlobalNumberTableModel extends AbstractUserFieldTableModel {

    public UserFieldGlobalNumberTableModel(final GraphDocument doc) {
        super(doc);
        setData();
    }

    /**
     * Erstellt und setzt Modeldaten für Modelvariablen
     */
    @SuppressWarnings({
            "rawtypes", "unchecked"
    })
    public void setData() {

        GDCollection gdcoll = definitions.getCollection();

        // columIdentifiers erzeugen
        String columnHeader = getResString("value");
        Vector<NamedObjectContainer<?>> columnIdentifiers = new Vector<>(1);
        columnIdentifiers.add(new NamedObjectContainer(columnHeader, columnHeader));

        // Liste aller globalen UserFields erstellen
        ArrayList<UserField> userFieldList = new ArrayList<>();
        for (UserField uf : definitions.getGlobalUserFields()) {
            userFieldList.add(uf);
        }

        // Wenn keine Modelldaten existieren, wird auch keine Table angezeigt
        if (userFieldList.size() == 0) {
            return;
        }

        // rowIdentifiers und dataVector erzeugen
        Vector<NamedObjectContainer<?>> rowIdentifiers = new Vector<>();

        Object[][] data = new Object[userFieldList.size()][1];
        for (int i = 0; i < userFieldList.size(); i++) {
            UserField uf = userFieldList.get(i);
            NamedObjectContainer<UserField> noc = new NamedObjectContainer<>(uf, uf.getName());
            rowIdentifiers.add(noc);
            String value = uf.getValue(gdcoll);
            data[i][0] = new NamedObjectContainer<>(uf, value);
        }

        // Daten setzen
        setDataVector(data, columnIdentifiers, rowIdentifiers);
    }
}
