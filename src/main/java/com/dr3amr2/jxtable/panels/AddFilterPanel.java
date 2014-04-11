package com.dr3amr2.jxtable.panels;/*
 * Created by JFormDesigner on Mon Mar 24 12:58:44 MDT 2014
 */

import com.jgoodies.forms.factories.Borders;
import com.jgoodies.forms.factories.FormFactory;
import com.jgoodies.forms.layout.*;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;

/**
 * @author Rocky Nguyen
 */
public class AddFilterPanel extends JDialog {
    public AddFilterPanel(Frame owner) {
        super(owner);
        initComponents();
    }

    public AddFilterPanel(Dialog owner) {
        super(owner);
        initComponents();
    }

    public JPanel getAvailableFiltersPanel() {
        return availableFiltersPanel;
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        dialogPane = new JPanel();
        contentPanel = new JPanel();
        newFilterRadioButton = new JRadioButton();
        existingFilterRadioButton = new JRadioButton();
        availableFiltersPanel = new JPanel();
        label1 = new JLabel();
        displayRadioButton = new JRadioButton();
        radioButton2 = new JRadioButton();
        applyToPanel = new JPanel();
        checkBox1 = new JCheckBox();
        buttonBar = new JPanel();
        addButton = new JButton();
        cancelButton = new JButton();
        CellConstraints cc = new CellConstraints();

        //======== this ========
        setTitle("Add Filter");
        setModal(true);
        Container contentPane = getContentPane();
        contentPane.setLayout(new BorderLayout());

        //======== dialogPane ========
        {
            dialogPane.setBorder(Borders.DIALOG_BORDER);
            dialogPane.setLayout(new BorderLayout());

            //======== contentPanel ========
            {
                contentPanel.setLayout(new FormLayout(
                    ColumnSpec.decodeSpecs("default, default:grow"),
                    new RowSpec[] {
                        FormFactory.DEFAULT_ROWSPEC,
                        FormFactory.LINE_GAP_ROWSPEC,
                        FormFactory.DEFAULT_ROWSPEC,
                        FormFactory.LINE_GAP_ROWSPEC,
                        FormFactory.DEFAULT_ROWSPEC,
                        FormFactory.LINE_GAP_ROWSPEC,
                        FormFactory.DEFAULT_ROWSPEC,
                        FormFactory.LINE_GAP_ROWSPEC,
                        FormFactory.DEFAULT_ROWSPEC,
                        FormFactory.LINE_GAP_ROWSPEC,
                        FormFactory.DEFAULT_ROWSPEC,
                        FormFactory.LINE_GAP_ROWSPEC,
                        FormFactory.DEFAULT_ROWSPEC
                    }));

                //---- newFilterRadioButton ----
                newFilterRadioButton.setText("New");
                contentPanel.add(newFilterRadioButton, cc.xy(1, 1));

                //---- existingFilterRadioButton ----
                existingFilterRadioButton.setText("Existing");
                contentPanel.add(existingFilterRadioButton, cc.xy(1, 3));

                //======== availableFiltersPanel ========
                {
                    availableFiltersPanel.setBorder(new TitledBorder("Available Filters"));
                    availableFiltersPanel.setLayout(new BorderLayout());
                }
                contentPanel.add(availableFiltersPanel, cc.xywh(1, 5, 2, 1));

                //---- label1 ----
                label1.setText("Apply To:");
                contentPanel.add(label1, cc.xy(1, 7));

                //---- displayRadioButton ----
                displayRadioButton.setText("Display");
                contentPanel.add(displayRadioButton, cc.xy(2, 7));

                //---- radioButton2 ----
                radioButton2.setText("Receiver");
                contentPanel.add(radioButton2, cc.xy(2, 9));

                //======== applyToPanel ========
                {
                    applyToPanel.setLayout(new BorderLayout());
                }
                contentPanel.add(applyToPanel, cc.xywh(1, 11, 2, 1));

                //---- checkBox1 ----
                checkBox1.setText("Share Filter");
                contentPanel.add(checkBox1, cc.xy(1, 13));
            }
            dialogPane.add(contentPanel, BorderLayout.CENTER);

            //======== buttonBar ========
            {
                buttonBar.setBorder(Borders.BUTTON_BAR_GAP_BORDER);
                buttonBar.setLayout(new FormLayout(
                    new ColumnSpec[] {
                        FormFactory.GLUE_COLSPEC,
                        FormFactory.BUTTON_COLSPEC,
                        FormFactory.RELATED_GAP_COLSPEC,
                        FormFactory.BUTTON_COLSPEC
                    },
                    RowSpec.decodeSpecs("pref")));

                //---- addButton ----
                addButton.setText("Add");
                buttonBar.add(addButton, cc.xy(2, 1));

                //---- cancelButton ----
                cancelButton.setText("Cancel");
                buttonBar.add(cancelButton, cc.xy(4, 1));
            }
            dialogPane.add(buttonBar, BorderLayout.SOUTH);
        }
        contentPane.add(dialogPane, BorderLayout.CENTER);
        pack();
        setLocationRelativeTo(getOwner());
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    private JPanel dialogPane;
    private JPanel contentPanel;
    private JRadioButton newFilterRadioButton;
    private JRadioButton existingFilterRadioButton;
    private JPanel availableFiltersPanel;
    private JLabel label1;
    private JRadioButton displayRadioButton;
    private JRadioButton radioButton2;
    private JPanel applyToPanel;
    private JCheckBox checkBox1;
    private JPanel buttonBar;
    private JButton addButton;
    private JButton cancelButton;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
