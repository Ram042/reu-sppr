package app;

import app.components.CheckBoxWithLabel;
import app.components.SingleColumnTableModel;
import app.components.SpinnerWithLabel;
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
    private final JTable alternativesTable;
    private final JTable criterionTable;
    private SpinnerWithLabel alternativesSpinner;
    private SpinnerWithLabel criterionSpinner;
    private SpinnerWithLabel maxSpinner;
    private SpinnerWithLabel minSpinner;
    private CheckBoxWithLabel paretoFilterCheckbox;

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

        alternativesTable = new JTable(alternativeTableModel);
        criterionTable = new JTable(criterionTableModel);

        //form
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        setTitle("Лексикографический метод © Хамитов Рамиль 2020");
        setSize(800, 600);
        setMinimumSize(new Dimension(500, 450));

        getContentPane().add(tabbedPane);

        //tabs
        initSettingsTab();
        initAlternativesTab();
        initCriterionTab();
        initInputTab();
        initCalculationTab();

        //init tables
        alternativesSpinner.setValue(3);
        criterionSpinner.setValue(3);
        minSpinner.setValue(1);
        maxSpinner.setValue(10);
        paretoFilterCheckbox.setValue(true);

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
            if (alternativesTable.getCellEditor() != null) {
                alternativesTable.getCellEditor().stopCellEditing();
            }
            if (criterionTable.getCellEditor() != null) {
                criterionTable.getCellEditor().stopCellEditing();
            }
        });

        setVisible(true);
    }

    private void initInputTab() {
        tabbedPane.addTab("Ввод данных", inputPanel);
    }

    void initSettingsTab() {
        var panel = new JPanel(new MigLayout("wrap 1"));
        tabbedPane.addTab("Настройки", panel);

        alternativesSpinner = new SpinnerWithLabel(
                "Количество альтернатив",
                e -> {
                    if (alternativesTable.getCellEditor() != null) {
                        alternativesTable.getCellEditor().cancelCellEditing();
                    }
                    ((SingleColumnTableModel) alternativesTable.getModel())
                            .setCount((Integer) ((JSpinner) e.getSource()).getValue());
                    inputPanelRebuildRequired = true;
                }, 2, 2, 20, 1
        );
        panel.add(alternativesSpinner);

        criterionSpinner = new SpinnerWithLabel(
                "Количество критериев",
                e -> {
                    if (criterionTable.getCellEditor() != null) {
                        criterionTable.getCellEditor().cancelCellEditing();
                    }
                    ((SingleColumnTableModel) criterionTable.getModel())
                            .setCount(((Integer) ((JSpinner) e.getSource()).getValue()));
                    inputPanelRebuildRequired = true;
                }, 2, 2, 20, 1
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

        paretoFilterCheckbox = new CheckBoxWithLabel("Выделять множество парето",
                e -> {
                    calculationView.setParetoFilter(((JCheckBox) e.getSource()).isSelected());
                    inputPanelRebuildRequired=true;
                });
        panel.add(paretoFilterCheckbox);
    }

    void initCalculationTab() {
        tabbedPane.addTab("Расчеты", calculationView.getContent());
    }

    void initAlternativesTab() {
        var panel = new JPanel(new MigLayout(""));
        tabbedPane.addTab("Альтернативы", new JScrollPane(panel));

        panel.add(alternativesTable, "width 400px");
        tabbedPane.addChangeListener(e -> {
            if (alternativesTable.getCellEditor() != null) {
                alternativesTable.getCellEditor().stopCellEditing();
            }
        });
        alternativesTable.getModel().addTableModelListener(e -> {
            inputPanelRebuildRequired = true;
        });
    }

    void initCriterionTab() {
        JPanel panel = new JPanel(new MigLayout("", "[shrink][]", "[][][grow]"));
        tabbedPane.addTab("Критерии", panel);

        criterionTable.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        panel.add(criterionTable, "spany 2, height pref,width 400px");

        tabbedPane.addChangeListener(e -> {
            if (criterionTable.getCellEditor() != null) {
                criterionTable.getCellEditor().stopCellEditing();
            }
        });
        criterionTable.getModel().addTableModelListener(e -> {
            inputPanelRebuildRequired = true;
        });

        var upButton = new BasicArrowButton(SwingConstants.NORTH);
        panel.add(upButton, "wrap, pushy 1,bottom, w 20px, h 20px");
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
        panel.add(downButton, "pushy 1,top, w 20px, h 20px");
        downButton.addActionListener(e -> {
            var row = criterionTable.getSelectedRow();

            if (row < 0 || row == criterionTable.getRowCount() - 1) {
                return;
            }
            if (criterionTable.getCellEditor() != null) {
                criterionTable.getCellEditor().stopCellEditing();
            }
            criterionTableModel.swap(row + 1, row);

            criterionTable.setRowSelectionInterval(row + 1, row + 1);
        });
    }
}
