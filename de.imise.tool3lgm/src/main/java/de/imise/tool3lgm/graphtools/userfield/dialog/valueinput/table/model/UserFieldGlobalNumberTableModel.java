package de.imise.tool3lgm.graphtools.userfield.dialog.valueinput.table.model;

import java.util.ArrayList;

import de.imise.tool3lgm.Tool3lgmConstants;
import de.imise.tool3lgm.graphtools.GDCollection;
import de.imise.tool3lgm.graphtools.GraphDocument;
import de.imise.tool3lgm.graphtools.userfield.UserField;
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
        String columnHeader = Tool3lgmConstants.getResString("value");
        Object[] columnIdentifiers = new NamedObjectContainer[] {
            new NamedObjectContainer(columnHeader, columnHeader)
        };

        // Liste aller globalen UserFields erstellen
        ArrayList<UserField> userFieldList = new ArrayList<UserField>();
        for (UserField uf : definitions.getGlobalUserFields()) {
            if (!uf.hasStyle(UserField.Style.FORMAT)) {
                userFieldList.add(uf);
            }
        }

        // Wenn keine Modelldaten existieren, wird auch keine Table angezeigt
        if (userFieldList.size() == 0) {
            return;
        }

        // rowIdentifiers und dataVector erzeugen
        Object[] rowIdentifiers = new Object[userFieldList.size()];
        Object[][] data = new Object[userFieldList.size()][1];
        for (int i = 0; i < rowIdentifiers.length; i++) {
            UserField uf = userFieldList.get(i);
            NamedObjectContainer<UserField> noc = new NamedObjectContainer<UserField>(uf, uf.getName());
            rowIdentifiers[i] = noc;
            String value = uf.getValue(gdcoll);
            data[i][0] = new NamedObjectContainer<UserField>(uf, value);
        }

        // Daten setzen
        setDataVector(data, columnIdentifiers, rowIdentifiers);
    }
}
