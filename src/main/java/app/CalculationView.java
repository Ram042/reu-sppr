package app;

import net.miginfocom.swing.MigLayout;

import javax.swing.*;

public class CalculationView {

    private final CalculationModel calculationModel;
    private final JPanel content;
    private boolean paretoFilter;

    public boolean isParetoFilter() {
        return paretoFilter;
    }

    public void setParetoFilter(boolean paretoFilter) {
        this.paretoFilter = paretoFilter;
    }

    public CalculationView(CalculationModel calculationModel) {
        this.calculationModel = calculationModel;
        content = new JPanel(new MigLayout());
    }

    public void rebuild() {
        var stream = calculationModel.getAlternativeList().stream();

        if (paretoFilter) {
            stream = stream.filter(alt1 -> {
                //if there is better alternative, then exclude this alternative
                for (String alt2 : calculationModel.getAlternativeList()) {
                    if (!alt1.equals(alt2)) {
                        for (String criterion : calculationModel.getCriterionList()) {
                            int v1 = calculationModel.getValue(alt1, criterion);
                            int v2 = calculationModel.getValue(alt2, criterion);

                            if (v1 >= v2) {
                                return true;
                            }
                        }
                        return false;
                    }
                }
                return true;
            });
        }

        var collect = stream.sorted((s1, s2) -> {
            for (String criterion : calculationModel.getCriterionList()) {
                int v1 = calculationModel.getValue(s1, criterion);
                int v2 = calculationModel.getValue(s2, criterion);

                if (v1 != v2) {
                    return Integer.compare(v2, v1);
                }
            }
            return 0;
        }).toArray(String[]::new);

        //calculate ranks
        var ranks = new int[collect.length];
        int currentRank = 1;
        ranks[0] = 1;
        for (int i = 1; i < collect.length; i++) {
            for (String criterion : calculationModel.getCriterionList()) {
                int now = calculationModel.getValue(collect[i], criterion);
                int previous = calculationModel.getValue(collect[i - 1], criterion);
                if (now != previous) {
                    currentRank++;
                    break;
                }
            }
            ranks[i] = currentRank;
        }

        var values = new Object[collect.length][2];
        for (int i = 0; i < collect.length; i++) {
            values[i] = new Object[]{collect[i], ranks[i]};
        }

        content.removeAll();
        content.add(new JLabel("Ранжирование альтернатив"), "wrap");
        content.add(new JScrollPane(new JTable(values, new Object[]{"Альтернатива", "Ранг"})));
    }

    public JPanel getContent() {
        return content;
    }
}
