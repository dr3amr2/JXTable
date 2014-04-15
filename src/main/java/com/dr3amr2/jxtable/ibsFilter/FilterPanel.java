package com.dr3amr2.jxtable.ibsFilter;

import com.jgoodies.forms.factories.FormFactory;
import com.jgoodies.forms.layout.*;
import org.jdesktop.swingx.JXSearchField;

import javax.swing.*;
import java.awt.*;

/**
 * Created by dnguyen on 4/14/2014.
 */
public class FilterPanel extends JPanel {
    public JCheckBox activeFiltersCheckbox;
    public JCheckBox nonActiveFiltersCheckbox;
    public JComboBox filterOptionComboxBox;
    public JXSearchField filterField;
    public JRadioButton activeFilterOnlyButton;
    public JRadioButton nonActiveFilterOnlyButton;


    public FilterPanel(){
        initComponents();
    }


    protected void initComponents() {
        setLayout(new BorderLayout());

        filterOptionComboxBox = new JComboBox();
        filterOptionComboxBox.setModel(new DefaultComboBoxModel(FilterOptions.values()));

        JPanel controlPanel = createControlPanel();
        add(controlPanel, BorderLayout.NORTH);

    }

    protected JPanel createControlPanel() {
        JPanel filterOptionPanel = new JPanel();
        CellConstraints cc = new CellConstraints();

        filterOptionPanel.setLayout(new FormLayout(
                new ColumnSpec[] {
                        new ColumnSpec(ColumnSpec.FILL, Sizes.dluX(20), FormSpec.DEFAULT_GROW),
                        FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
                        FormFactory.DEFAULT_COLSPEC,
                        FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
                        FormFactory.DEFAULT_COLSPEC
                },
                RowSpec.decodeSpecs("default")));
        filterField = new JXSearchField();
        filterField.setPrompt("  Search Filter");
        filterField.setPreferredSize(new Dimension(250, 24));
        filterOptionPanel.add(filterField, cc.xy(1, 1));

        //---- activeFilterOnlyCheckBox ----
        activeFiltersCheckbox = new JCheckBox();
        activeFiltersCheckbox.setText("Active Only");
        filterOptionPanel.add(activeFiltersCheckbox, cc.xy(3, 1, CellConstraints.RIGHT, CellConstraints.DEFAULT));

        //---- nonActiveFilterOnlyCheckBox ----
        nonActiveFiltersCheckbox = new JCheckBox();
        nonActiveFiltersCheckbox.setText("Non Active Only");
        filterOptionPanel.add(nonActiveFiltersCheckbox, cc.xy(5, 1, CellConstraints.RIGHT, CellConstraints.DEFAULT));


        return filterOptionPanel;
    }

}
