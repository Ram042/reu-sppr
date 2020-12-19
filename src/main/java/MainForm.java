import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import javax.swing.plaf.basic.BasicArrowButton;
import java.awt.*;

public class MainForm extends JFrame {

    private final JTabbedPane tabbedPane = new JTabbedPane();
    private final SingleColumnTableModel alternativeTableModel;
    private final SingleColumnTableModel criterionTableModel;
    private final InputPanel inputPanel;
    private final CalculationModel calculationModel;
    private final CalculationView calculationView;
    boolean inputPanelRebuildRequired = true;
    private JTable alternativesTable;
    private JTable criterionTable;
    private SpinnerWithLabel alternativesSpinner;
    private SpinnerWithLabel criterionSpinner;
    private SpinnerWithLabel maxSpinner;
    private SpinnerWithLabel minSpinner;

    public MainForm() throws HeadlessException {
        super();
        //model
        calculationModel = new CalculationModel();
        alternativeTableModel = new SingleColumnTableModel(calculationModel.getAlternativeList());
        criterionTableModel = new SingleColumnTableModel(calculationModel.getCriterionList());

        inputPanel = new InputPanel(
                calculationModel.getAlternativeList(),
                calculationModel.getCriterionList(),
                calculationModel::getValue,
                1, 10);
        inputPanel.addChangeListener(calculationModel::setValue);
        calculationView = new CalculationView(calculationModel);

        //form
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        setTitle("СППР");
        setSize(800, 600);

        getContentPane().add(tabbedPane);

        //tabs
        initBasicInfoTab();
        initAlternativesTab();
        initCriterionTab();
        initInputTab();
        initCalculationTab();

        //init tables
        alternativesSpinner.setValue(3);
        criterionSpinner.setValue(3);
        minSpinner.setValue(1);
        maxSpinner.setValue(10);

        tabbedPane.addChangeListener(e -> {
            if (tabbedPane.getSelectedIndex() == 3 && inputPanelRebuildRequired) {
                inputPanel.rebuild();
                inputPanelRebuildRequired = false;
            }
        });
        tabbedPane.addChangeListener(e -> {
            if (tabbedPane.getSelectedIndex() == 4) {
                calculationView.rebuild();
            }
        });
        tabbedPane.addChangeListener(e -> {
            alternativesSpinner.submit();
            criterionSpinner.submit();
            maxSpinner.submit();
            minSpinner.submit();
        });

        setVisible(true);
    }

    private void initInputTab() {
        tabbedPane.addTab("Ввод данных", inputPanel);
    }

    void initBasicInfoTab() {
        var panel = new JPanel(new MigLayout("wrap 1"));
        tabbedPane.addTab("Настройки", panel);

        alternativesSpinner = new SpinnerWithLabel(
                "Количество альтернатив",
                e -> {
                    if (alternativesTable.getCellEditor() != null) {
                        alternativesTable.getCellEditor().cancelCellEditing();
                    }
                    System.out.println(e);
                    ((SingleColumnTableModel) alternativesTable.getModel())
                            .setCount((Integer) ((JSpinner) e.getSource()).getValue());
                    inputPanelRebuildRequired = true;
                }, 1, 1, 20, 1
        );
        panel.add(alternativesSpinner);

        criterionSpinner = new SpinnerWithLabel(
                "Количество критериев",
                e -> {
                    if (criterionTable.getCellEditor() != null) {
                        criterionTable.getCellEditor().cancelCellEditing();
                    }
                    System.out.println(e);
                    ((SingleColumnTableModel) criterionTable.getModel())
                            .setCount(((Integer) ((JSpinner) e.getSource()).getValue()));
                    inputPanelRebuildRequired = true;
                }, 1, 1, 20, 1
        );
        panel.add(criterionSpinner);

        minSpinner = new SpinnerWithLabel(
                "Минимальная оценка",
                e -> {
                    inputPanel.setMinValue((Integer) ((JSpinner) e.getSource()).getValue());
                    inputPanelRebuildRequired = true;
                },
                0, -100, 100, 1
        );
        panel.add(minSpinner);

        maxSpinner = new SpinnerWithLabel(
                "Максимальная оценка",
                e -> {
                    inputPanel.setMaxValue((Integer) ((JSpinner) e.getSource()).getValue());
                    inputPanelRebuildRequired = true;
                },
                0, -100, 100, 1
        );
        panel.add(maxSpinner);
    }

    void initCalculationTab() {
        tabbedPane.addTab("Расчеты", calculationView.getContent());
    }

    void initAlternativesTab() {
        JPanel panel = new JPanel();
        tabbedPane.addTab("Альтернативы", panel);

        alternativesTable = new JTable();
        alternativesTable.setModel(alternativeTableModel);
        panel.add(alternativesTable);
        tabbedPane.addChangeListener(e -> {
            if (alternativesTable.getCellEditor() != null) {
                alternativesTable.getCellEditor().stopCellEditing();
            }
        });
    }

    void initCriterionTab() {
        JPanel panel = new JPanel();
        tabbedPane.addTab("Критерии", panel);

        criterionTable = new JTable();
        criterionTable.setModel(criterionTableModel);
        criterionTable.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        panel.add(criterionTable);

        tabbedPane.addChangeListener(e -> {
            if (criterionTable.getCellEditor() != null) {
                criterionTable.getCellEditor().stopCellEditing();
            }
        });

        var upButton = new BasicArrowButton(SwingConstants.NORTH);
        panel.add(upButton);
        upButton.addActionListener(e -> {
            var row = criterionTable.getSelectedRow();

            if (row <= 0) {
                return;
            }
            if (criterionTable.getCellEditor() != null) {
                criterionTable.getCellEditor().stopCellEditing();
            }
            criterionTableModel.swap(row - 1, row);

            criterionTable.setRowSelectionInterval(row - 1, row - 1);
        });

        var downButton = new BasicArrowButton(SwingConstants.SOUTH);
        panel.add(downButton);
        downButton.addActionListener(e -> {
            var row = criterionTable.getSelectedRow();

            if (row < 0 || row == criterionTable.getRowCount() - 1) {
                return;
            }
            if (criterionTable.getCellEditor() != null) {
                criterionTable.getCellEditor().stopCellEditing();
            }
            alternativeTableModel.swap(row + 1, row);

            criterionTable.setRowSelectionInterval(row + 1, row + 1);
        });
    }
}
