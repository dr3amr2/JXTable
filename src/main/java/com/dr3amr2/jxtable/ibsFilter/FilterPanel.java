package com.dr3amr2.jxtable.ibsFilter;

import org.jdesktop.swingx.JXSearchField;

import javax.swing.*;
import java.awt.*;

/**
 * Created by dnguyen on 4/14/2014.
 */
public class FilterPanel extends JPanel {
    public JCheckBox activeFiltersCheckbox;
    public JCheckBox nonActiveFiltersCheckbox;
    public JXSearchField filterField;


    public FilterPanel(){
        initComponents();
    }


    protected void initComponents() {
        setLayout(new BorderLayout());

        JPanel controlPanel = createControlPanel();
        add(controlPanel, BorderLayout.NORTH);

    }

    protected JPanel createControlPanel() {
        JPanel controlPanel = new JPanel();
        GridBagLayout gridbag = new GridBagLayout();
        GridBagConstraints c = new GridBagConstraints();
        controlPanel.setLayout(gridbag);

        c.gridx = 0;
        c.gridy = 1;
        c.gridheight = 1;
        c.insets = new Insets(20, 10, 0, 10);
        c.anchor = GridBagConstraints.SOUTHWEST;
        JLabel searchLabel = new JLabel();
        searchLabel.setName("searchLabel");
        controlPanel.add(searchLabel, c);

        c.gridx = 0;
        c.gridy = 2;
        c.weightx = 1.0;
        c.insets.top = 0;
        c.insets.bottom = 12;
        c.anchor = GridBagConstraints.SOUTHWEST;
        //c.fill = GridBagConstraints.HORIZONTAL;
        filterField = new JXSearchField();
        filterField.setPrompt("  Search Filters");
        filterField.setPreferredSize(new Dimension(250, 24));

        controlPanel.add(filterField, c);

        c.gridx = 1;
        c.gridy = 2;
        c.gridwidth = GridBagConstraints.REMAINDER;
        //c.insets.right = 24;
        //c.insets.left = 12;
        c.weightx = 0.0;
        c.anchor = GridBagConstraints.EAST;
        c.fill = GridBagConstraints.NONE;
        activeFiltersCheckbox = new JCheckBox();
        activeFiltersCheckbox.setText("Show only active filters");
        controlPanel.add(activeFiltersCheckbox, c);

        nonActiveFiltersCheckbox = new JCheckBox();
        nonActiveFiltersCheckbox.setText("Show only non-active filters");
        controlPanel.add(nonActiveFiltersCheckbox, c);

        return controlPanel;
    }

}
