/*
 * Created by JFormDesigner on Mon Apr 14 17:21:26 MDT 2014
 */

package com.dr3amr2.jxtable.ibsFilter;

import com.jgoodies.forms.factories.FormFactory;
import com.jgoodies.forms.layout.*;

import javax.swing.*;
import java.awt.*;

/**
 * @author User #12
 */
public class FilterPanelTest extends JPanel {
    public FilterPanelTest() {
        initComponents();
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        filterOptionPanel = new JPanel();
        JTextField searchField = new JTextField();
        JCheckBox activeFilterOnlyCheckBox = new JCheckBox();
        JCheckBox nonActiveFilterOnlyCheckBox = new JCheckBox();
        CellConstraints cc = new CellConstraints();

        //======== this ========
        setLayout(new BorderLayout());

        //======== filterOptionPanel ========
        {
            filterOptionPanel.setLayout(new FormLayout(
                new ColumnSpec[] {
                    new ColumnSpec(ColumnSpec.FILL, Sizes.dluX(20), FormSpec.DEFAULT_GROW),
                    FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
                    FormFactory.DEFAULT_COLSPEC,
                    FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
                    FormFactory.DEFAULT_COLSPEC
                },
                RowSpec.decodeSpecs("default")));
            filterOptionPanel.add(searchField, cc.xy(1, 1));

            //---- activeFilterOnlyCheckBox ----
            activeFilterOnlyCheckBox.setText("Active Only");
            filterOptionPanel.add(activeFilterOnlyCheckBox, cc.xy(3, 1, CellConstraints.RIGHT, CellConstraints.DEFAULT));

            //---- nonActiveFilterOnlyCheckBox ----
            nonActiveFilterOnlyCheckBox.setText("Non Active Only");
            filterOptionPanel.add(nonActiveFilterOnlyCheckBox, cc.xy(5, 1, CellConstraints.RIGHT, CellConstraints.DEFAULT));
        }
        add(filterOptionPanel, BorderLayout.NORTH);
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    private JPanel filterOptionPanel;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
