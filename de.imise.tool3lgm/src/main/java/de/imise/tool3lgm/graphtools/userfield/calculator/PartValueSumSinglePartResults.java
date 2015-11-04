package de.imise.tool3lgm.graphtools.userfield.calculator;

import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Lists;
import com.google.common.collect.Table;

import de.imise.tool3lgm.graphtools.elements.ModelElement;
import de.imise.tool3lgm.graphtools.userfield.UserField;
import de.imise.util.Pair;

/**
 * Hält für alle ModelElemente, denen eine einfache Teilwertsummenformel als UserField zugeordnet ist,
 * eine Liste mit dem jeweiligen Element und dem Teilwert, den dieses Element zu dem Gesamtergebnis
 * der Teilwertsumme beiträgt.
 * 
 * @author Ich
 * @create 26.10.2015
 */
public class PartValueSumSinglePartResults {

    private final Table<ModelElement, UserField, ResultList> results = HashBasedTable.create();

    public PartValueSumSinglePartResults() {
    }

    public void clear() {
        results.clear();
    }

    public void add(final ModelElement fullResultElement, final UserField partValueSumField, final ModelElement valueSourceElement, final String partResult) {
        ResultList currentList = results.get(fullResultElement, partValueSumField);
        if (currentList == null) {
            currentList = new ResultList();
            results.put(fullResultElement, partValueSumField, currentList);
        }
        currentList.add(valueSourceElement, partResult);
    }

    public Iterable<Pair<ModelElement, String>> getIterable(final ModelElement fullResultElement, final UserField partValueSumField) {
        ResultList result = results.get(fullResultElement, partValueSumField);
        if (result == null) {
            result = new ResultList();
        }
        return result;
    }

    @Override
    public String toString() {
        return results.toString();
    }

    /**
     * Liste von Paaren aus einem Element und einem Ergenis. Dabei ist das Ergebnis der Anteil, der einem
     * anderen Element für das im Paar angegebene Element in einer Teilwertsumme zugerechnet wird.
     * 
     * @author Ich
     * @create 26.10.2015
     */
    public class ResultList implements Iterable<Pair<ModelElement, String>> {

        private final List<Pair<ModelElement, String>> internalResults = Lists.newArrayList();

        private void clear() {
            internalResults.clear();
        }

        private void add(final ModelElement valueSourceElement, final String partResult) {
            Pair<ModelElement, String> resultPair = new Pair<ModelElement, String>(valueSourceElement, partResult);
            internalResults.add(resultPair);
        }

        @Override
        public Iterator<Pair<ModelElement, String>> iterator() {
            return new MyIterator();
        }

        private class MyIterator implements Iterator<Pair<ModelElement, String>> {
            private int cursor;

            public MyIterator() {
                cursor = 0;
            }

            @Override
            public boolean hasNext() {
                return cursor < internalResults.size();
            }

            @Override
            public Pair<ModelElement, String> next() {
                if (hasNext()) {
                    Pair<ModelElement, String> current = internalResults.get(cursor);
                    cursor++;
                    return current;
                }
                throw new NoSuchElementException();
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException();
            }
        }

        @Override
        public String toString() {
            return internalResults.toString();
        }

    }

}
