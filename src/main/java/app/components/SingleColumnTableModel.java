package app.components;

import org.apache.commons.lang3.math.NumberUtils;

import javax.swing.table.AbstractTableModel;
import java.util.List;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkElementIndex;

public class SingleColumnTableModel extends AbstractTableModel {
    private final List<String> rows;

    public SingleColumnTableModel(List<String> rows) {
        this.rows = rows;
    }

    public List<String> getRows() {
        return rows;
    }

    public void setCount(int count) {
        checkArgument(count > 0);

        if (count < rows.size()) {
            int remove = rows.size() - count;
            for (int i = 0; i < remove; i++) {
                rows.remove(count);
            }
        } else {
            int add = count - rows.size();
            for (int i = 0; i < add; i++) {
                rows.add(generateName());
            }
        }

        fireTableDataChanged();
    }

    private String generateName() {
        return String.valueOf(rows.stream()
                .filter(NumberUtils::isCreatable)
                .mapToInt(Integer::parseInt)
                .max()
                .orElse(0) + 1);
    }

    @Override
    public int getRowCount() {
        return rows.size();
    }

    @Override
    public int getColumnCount() {
        return 1;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        return rows.get(rowIndex);
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return true;
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        rows.set(rowIndex, aValue.toString());
        fireTableDataChanged();
    }

    public void swap(int index1, int index2) {
        checkElementIndex(index1, rows.size());
        checkElementIndex(index2, rows.size());
        if (index1 == index2) {
            return;
        }

        String buffer = rows.get(index1);
        rows.set(index1, rows.get(index2));
        rows.set(index2, buffer);

        fireTableDataChanged();
    }
}
