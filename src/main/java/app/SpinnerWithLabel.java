package app.components;

import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import javax.swing.event.ChangeListener;
import java.text.ParseException;

public class SpinnerWithLabel extends JPanel {

    private final JSpinner spinner;

    public SpinnerWithLabel(String label, ChangeListener changeListener, int value, int min, int max, int step) {
        super(new MigLayout());

        JLabel jLabel = new JLabel(label);

        spinner = new JSpinner(new SpinnerNumberModel(value, min, max, step));
        spinner.addChangeListener(changeListener);

        add(jLabel);
        add(spinner);


    }


    public void setValue(int value) {
        spinner.setValue(value);
    }

    public void submit() {
        try {
            spinner.commitEdit();
        } catch (ParseException e) {
            spinner.setValue(spinner.getValue());
        }
    }
}
