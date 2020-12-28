package app.components;

import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import javax.swing.event.ChangeListener;

public class CheckBoxWithLabel extends JPanel {

    private final JCheckBox checkBox;

    public CheckBoxWithLabel(String label, ChangeListener changeListener) {
        super(new MigLayout());

        JLabel jLabel = new JLabel(label);

        checkBox = new JCheckBox();
        checkBox.addChangeListener(changeListener);

        add(jLabel);
        add(checkBox);
    }
    public void setValue(boolean value) {
        checkBox.setSelected(value);
    }
}
