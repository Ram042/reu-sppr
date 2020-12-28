package app;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import com.google.common.collect.Tables;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CalculationModel {

    private final List<String> alternatives = Collections.synchronizedList(new ArrayList<>(20));
    private final List<String> criterion = Collections.synchronizedList(new ArrayList<>(20));
    /**
     * row - alternative
     * column - criterion
     */
    private final Table<String, String, Integer> values = Tables
            .synchronizedTable(HashBasedTable.create(20, 20));

    public List<String> getAlternativeList() {
        return alternatives;
    }

    public void clearAlternative(String name) {
        values.row(name).clear();
    }

    public void clearCriterion(String name) {
        values.column(name).clear();
    }

    public List<String> getCriterionList() {
        return criterion;
    }

    public Table<String, String, Integer> getValues() {
        return values;
    }

    public void setValue(String alternative, String criterion, int value) {
        values.put(alternative, criterion, value);
    }

    public int getValue(String alternative, String criterion) {
        Integer value = values.get(alternative, criterion);
        return value == null ? 0 : value;
    }
}
