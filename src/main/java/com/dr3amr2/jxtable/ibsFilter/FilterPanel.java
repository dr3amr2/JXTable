package com.dr3amr2.jxtable.ibsFilter;

import com.dr3amr2.jxtable.utils.Stacker;
import com.jgoodies.forms.factories.FormFactory;
import com.jgoodies.forms.layout.*;
import org.jdesktop.swingx.JXSearchField;
import org.jdesktop.swingx.JXStatusBar;
import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.JXTableHeader;

import javax.swing.*;
import javax.swing.table.JTableHeader;
import java.awt.*;

/**
 * Created by dnguyen on 4/14/2014.
 *
 * This FilterPanel class is in charge of all things visual (non-data related) for all the filters
 */
public class FilterPanel extends JPanel {
    // Filter Control Panel variables
    public JCheckBox activeFiltersCheckbox;
    public JCheckBox inactiveFiltersCheckbox;
    public JXSearchField filterField;

    // JX Table Panel variables
    public JXTable filterTable;
    public Stacker dataPanel;

    // Status Bar variables
    public JComponent statusBarLeft;
    public JProgressBar progressBar;
    public JLabel actionStatus;
    public JLabel tableStatus;
    public JLabel tableRows;

    public FilterPanel(){
        initComponents();
    }

    /**
     * Initialize and add 3 main components to the main JPanel
     */
    protected void initComponents() {
        setLayout(new BorderLayout());

        // Filter Control Panel
        JPanel controlPanel = createControlPanel();
        add(controlPanel, BorderLayout.NORTH);

        // JX Table Panel
        filterTable = createJXTable();
        JScrollPane scrollPane = new JScrollPane(filterTable);
        dataPanel = new Stacker(scrollPane);
        add(dataPanel, BorderLayout.CENTER);

        // Status Bar Panel
        add(createStatusBar(), BorderLayout.SOUTH);

    }

    /**
     * This has the checkboxes for both active and inactive filters along with a searchbox
     * @return JPanel component
     */
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
        inactiveFiltersCheckbox = new JCheckBox();
        inactiveFiltersCheckbox.setText("Non Active Only");
        filterOptionPanel.add(inactiveFiltersCheckbox, cc.xy(5, 1, CellConstraints.RIGHT, CellConstraints.DEFAULT));

        return filterOptionPanel;
    }


    /**
     *  JXTable display properties all set to "centered" column alignment
     * @return JXTable component
     */
    private JXTable createJXTable() {
        return new JXTable() {

            @Override
            protected JTableHeader createDefaultTableHeader() {
                return new JXTableHeader(columnModel) {

                    @Override
                    public void updateUI() {
                        super.updateUI();
                        // need to do in updateUI to survive toggling of LAF
                        if (getDefaultRenderer() instanceof JLabel) {
                            ((JLabel) getDefaultRenderer()).setHorizontalAlignment(JLabel.CENTER);

                        }
                    }
                };
            }

        };
    }

    /**
     * This creates the status bar that display loading data in progess along with number of available data
     * @return JXStatusBar component
     */
    protected Container createStatusBar() {

        JXStatusBar statusBar = new JXStatusBar();
        statusBar.putClientProperty("auto-add-separator", Boolean.FALSE);

        // Left status area
        statusBar.add(Box.createRigidArea(new Dimension(10, 22)));
        statusBarLeft = Box.createHorizontalBox();
        statusBar.add(statusBarLeft, JXStatusBar.Constraint.ResizeBehavior.FILL);
        actionStatus = new JLabel();
        actionStatus.setHorizontalAlignment(JLabel.LEADING);
        statusBarLeft.add(actionStatus);

        statusBar.add(Box.createVerticalGlue());
        statusBar.add(Box.createRigidArea(new Dimension(50, 0)));

        // Right status area
        tableStatus = new JLabel();
        JComponent bar = Box.createHorizontalBox();
        bar.add(tableStatus);
        tableRows = new JLabel("0");
        bar.add(tableRows);

        statusBar.add(bar);
        statusBar.add(Box.createHorizontalStrut(12));

        return statusBar;
    }

}
