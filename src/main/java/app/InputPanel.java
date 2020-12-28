package app;

import net.miginfocom.swing.MigLayout;

import javax.annotation.Nullable;
import javax.swing.*;
import javax.swing.event.EventListenerList;
import java.util.EventListener;
import java.util.Hashtable;
import java.util.List;
import java.util.function.BiFunction;

public class InputPanel extends JScrollPane {

    private final JPanel contentPanel;

    private final EventListenerList changeListeners = new EventListenerList();
    private final List<String> alternatives;
    private final List<String> criterion;
    private final SliderValueSupplier sliderValueSupplier;
    private int minValue;
    private int maxValue;
    @Nullable
    private Hashtable<Integer, JLabel> labelTable;
    public InputPanel(List<String> alternatives, List<String> criterion, SliderValueSupplier sliderValueSupplier, int minValue, int maxValue) {
        super(null, VERTICAL_SCROLLBAR_ALWAYS, HORIZONTAL_SCROLLBAR_NEVER);
        this.alternatives = alternatives;
        this.criterion = criterion;
        this.sliderValueSupplier = sliderValueSupplier;
        this.minValue = minValue;
        this.maxValue = maxValue;
        getVerticalScrollBar().setUnitIncrement(16);
        contentPanel = new JPanel(new MigLayout("flowy, fill", "[fill]"));
        setViewportView(contentPanel);
    }

    public int getMinValue() {
        return minValue;
    }

    public void setMinValue(int minValue) {
        this.minValue = minValue;
    }

    public int getMaxValue() {
        return maxValue;
    }

    public void setMaxValue(int maxValue) {
        this.maxValue = maxValue;
    }

    public void rebuild() {
        contentPanel.removeAll();
        updateLabelTable();

        alternatives.forEach(alternativeName -> {
            JPanel panel = new JPanel(new MigLayout("",
                    "[grow 0][grow 1]"));
            panel.setBorder(BorderFactory.createTitledBorder(alternativeName));
            this.contentPanel.add(panel, "dock north");

            criterion.forEach(criterionName -> {
                //init value
                //note: uninitialized values might be equal to zero
                var value = sliderValueSupplier.apply(alternativeName, criterionName);
                if (value == null) {
                    value = minValue;
                }
                if (value < minValue) {
                    value = minValue;
                }
                if (value > maxValue) {
                    value = maxValue;
                }

                JLabel label = new JLabel(criterionName);
                JSlider slider = new JSlider(minValue, maxValue, value);
                if (labelTable != null) {
                    slider.setLabelTable(labelTable);
                }
                slider.setMajorTickSpacing(1);
                slider.setPaintTicks(true);
                slider.setPaintLabels(true);


                fireChangeEvent(alternativeName, criterionName, slider.getValue());

                slider.addChangeListener(e -> {
                    fireChangeEvent(alternativeName, criterionName, slider.getValue());
                });

                panel.add(label, "grow,gap 10px 10px,baseline");
                panel.add(slider, "grow, wrap");
            });
        });
    }

    void updateLabelTable() {
        if (maxValue - minValue <= 30) {
            labelTable = null;
        } else {
            labelTable = new Hashtable<>();
            int d = (maxValue - minValue) / 25;

            for (int i = minValue; i < maxValue; i += d) {
                if (maxValue - i >= d) {
                    labelTable.put(i, new JLabel(Integer.toString(i)));
                }
            }

            labelTable.put(maxValue, new JLabel(Integer.toString(maxValue)));
        }
    }


    protected void fireChangeEvent(String alternativeName, String criterionName, int value) {
        for (ChangeListener listener : changeListeners.getListeners(ChangeListener.class)) {
            listener.valueChanged(alternativeName, criterionName, value);
        }
    }

    public void addChangeListener(ChangeListener listener) {
        changeListeners.add(ChangeListener.class, listener);
    }

    public interface ChangeListener extends EventListener {
        void valueChanged(String alternativeName, String criterionName, int value);
    }

    public interface SliderValueSupplier extends BiFunction<String, String, Integer> {
        @Override
        Integer apply(String s, String s2);
    }
}
