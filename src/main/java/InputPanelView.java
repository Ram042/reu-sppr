import net.miginfocom.swing.MigLayout;

import javax.swing.*;

import static javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER;
import static javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS;

public class InputPanelView {

    private final JPanel contentPanel;
    private final JScrollPane scrollPanel;
    private SingleColumnTableModel alternativeModel;
    private SingleColumnTableModel criterionModel;
    private boolean rebuildRequired = true;
    private CalculationModel calculationModel;

    public InputPanelView(SingleColumnTableModel alternativeModel, SingleColumnTableModel criterionModel,
                          CalculationModel calculationModel) {
        this.calculationModel = calculationModel;
        contentPanel = new JPanel(new MigLayout("flowy, fill", "[fill]"));
        scrollPanel = new JScrollPane(contentPanel, VERTICAL_SCROLLBAR_ALWAYS, HORIZONTAL_SCROLLBAR_NEVER);
        scrollPanel.getVerticalScrollBar().setUnitIncrement(16);

        this.alternativeModel = alternativeModel;
        alternativeModel.addTableModelListener(e -> {
            rebuildRequired = true;
        });

        this.criterionModel = criterionModel;
        criterionModel.addTableModelListener(e -> {
            rebuildRequired = true;
        });
    }


    public void rebuild() {
        if (rebuildRequired) {
            rebuildRequired = false;
        } else {
            return;
        }
        System.out.println("rebuild");
        contentPanel.removeAll();

        alternativeModel.getRows().forEach(alternativeName -> {
            JPanel panel = new JPanel(new MigLayout("",
                    "[grow 0][grow 1]"));
            panel.setBorder(BorderFactory.createTitledBorder(alternativeName));
            this.contentPanel.add(panel, "dock north");

            criterionModel.getRows().forEach(criterionName -> {
                JLabel label = new JLabel(criterionName);
                JSlider slider = new JSlider(-10, 10);
                slider.setMajorTickSpacing(1);
                slider.setPaintTicks(true);
                slider.setPaintLabels(true);
                slider.setValue(calculationModel.getValue(alternativeName,criterionName));

                slider.addChangeListener(e -> {
                    calculationModel.setValue(alternativeName, criterionName, slider.getValue());
                });

                panel.add(label, "grow,gap 10px 10px,baseline");
                panel.add(slider, "grow, wrap");
            });
        });

    }

    public JScrollPane getPanel() {
        return scrollPanel;
    }
}
