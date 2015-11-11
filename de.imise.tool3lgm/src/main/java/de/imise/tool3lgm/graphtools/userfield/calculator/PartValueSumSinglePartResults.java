package de.imise.tool3lgm.graphtools.userfield.calculator;

import java.util.Map;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Maps;
import com.google.common.collect.Table;

import de.imise.tool3lgm.graphtools.elements.ModelElement;
import de.imise.tool3lgm.graphtools.userfield.UserField;

/**
 * Hält für alle ModelElemente, denen eine einfache Teilwertsummenformel als UserField zugeordnet ist,
 * eine Liste mit dem jeweiligen Element und dem Teilwert, den dieses Element zu dem Gesamtergebnis
 * der Teilwertsumme beiträgt.
 * 
 * @author Ich
 * @create 26.10.2015
 */
public class PartValueSumSinglePartResults {

    private final Map<ModelElement, Table<UserField, ModelElement, String>> results = Maps.newHashMap();

    public PartValueSumSinglePartResults() {
    }

    public void clear() {
        results.clear();
    }

    public void add(final ModelElement fullResultElement, final UserField partValueSumField, final ModelElement valueSourceElement, final String partResult) {
        Table<UserField, ModelElement, String> resultTable = results.get(fullResultElement);
        if (resultTable == null) {
            resultTable = HashBasedTable.create();
            results.put(fullResultElement, resultTable);
        }
        resultTable.put(partValueSumField, valueSourceElement, partResult);
    }

    public String get(final ModelElement fullResultElement, final UserField partValueSumField, final ModelElement valueSourceElement) {
        Table<UserField, ModelElement, String> resultTable = results.get(fullResultElement);
        if (resultTable == null) {
            return null;
        }
        String result = resultTable.get(partValueSumField, valueSourceElement);
        return result;
    }

    @Override
    public String toString() {
        return results.toString();
    }

}
