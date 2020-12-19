import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import javax.swing.plaf.basic.BasicArrowButton;
import java.awt.*;

public class MainForm extends JFrame {

    private final JTabbedPane tabbedPane = new JTabbedPane();
    private JTable alternativesTable;

    private final SingleColumnTableModel alternativeTableModel;
    private JTable criterionTable;
    private final SingleColumnTableModel criterionTableModel;

    private JSpinner alternativeCountSelector;

    private JSpinner criterionCountSelector;
    private final InputPanelView inputPanelManager;
    private final CalculationModel calculationModel;

    private final CalculationView calculationView;

    public MainForm() throws HeadlessException {
        super();
        //model
        calculationModel = new CalculationModel();
        alternativeTableModel = new SingleColumnTableModel(calculationModel.getAlternativeList());
        criterionTableModel = new SingleColumnTableModel(calculationModel.getCriterionList());

        inputPanelManager = new InputPanelView(alternativeTableModel, criterionTableModel, calculationModel);
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

        //init alternatives table
        alternativeCountSelector.getChangeListeners()[0].stateChanged(null);
        criterionCountSelector.getChangeListeners()[0].stateChanged(null);

        tabbedPane.addChangeListener(e -> {
            if (tabbedPane.getSelectedIndex() == 3) {
                inputPanelManager.rebuild();
            }
        });
        tabbedPane.addChangeListener(e -> {
            if (tabbedPane.getSelectedIndex() == 4) {
                calculationView.rebuild();
            }
        });

        setVisible(true);
    }

    private void initInputTab() {
        tabbedPane.addTab("Ввод данных", inputPanelManager.getPanel());
    }

    void initBasicInfoTab() {
        var panel = new JPanel(new MigLayout());
        tabbedPane.addTab("Настройки", panel);

        //region alternative count selector
        JPanel alternativeCountPanel = new JPanel();

        alternativeCountSelector = new JSpinner(new SpinnerNumberModel(3, 1, 20, 1));
        alternativeCountSelector.addChangeListener(e -> {
            if (alternativesTable.getCellEditor() != null) {
                alternativesTable.getCellEditor().cancelCellEditing();
            }
            ((SingleColumnTableModel) alternativesTable.getModel())
                    .setCount((int) alternativeCountSelector.getModel().getValue());
        });

        alternativeCountPanel.add(new JLabel("Количество альтернатив"));
        alternativeCountPanel.add(alternativeCountSelector);
        panel.add(alternativeCountPanel);
        //endregion


        //region criterion count selector
        JPanel criterionCountPanel = new JPanel();

        criterionCountSelector = new JSpinner(new SpinnerNumberModel(3, 1, 20, 1));
        criterionCountSelector.addChangeListener(e -> {
            if (criterionTable.getCellEditor() != null) {
                criterionTable.getCellEditor().cancelCellEditing();
            }
            ((SingleColumnTableModel) criterionTable.getModel())
                    .setCount((int) criterionCountSelector.getModel().getValue());
        });

        criterionCountPanel.add(new JLabel("Количество критериев"));
        criterionCountPanel.add(criterionCountSelector);
        panel.add(criterionCountPanel);
        //endregion
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
