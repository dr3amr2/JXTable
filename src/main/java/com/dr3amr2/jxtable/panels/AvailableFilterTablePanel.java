package com.dr3amr2.jxtable.panels; /**
* Created by dnguyen on 3/24/14.
*/

import com.dr3amr2.jxtable.ibsFilter.DataFiltering;
import com.dr3amr2.jxtable.ibsFilter.FilterDataLoader;
import com.dr3amr2.jxtable.ibsFilter.FilterRendering;
import com.dr3amr2.jxtable.utils.Stacker;
import com.dr3amr2.jxtable.ibsFilter.FilterTableModel;
import com.dr3amr2.jxtable.utils.CustomColumnFactory;
import org.jdesktop.beansbinding.BeanProperty;
import org.jdesktop.beansbinding.BindingGroup;
import org.jdesktop.beansbinding.Bindings;
import org.jdesktop.swingx.JXStatusBar;
import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.JXTableHeader;
import org.jdesktop.swingx.decorator.HighlighterFactory;

import javax.swing.*;
import javax.swing.SwingWorker.StateValue;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.util.logging.Logger;

import static org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ;


public class AvailableFilterTablePanel extends JPanel {
    static final Logger logger = Logger.getLogger(AvailableFilterTablePanel.class.getName());

    private FilterTableModel filterModel;

    private JPanel controlPanel;
    private Stacker dataPanel;
    private JXTable filterTable;
    private JCheckBox winnersCheckbox;
    private JTextField filterField;
    private JComponent statusBarLeft;
    private JLabel actionStatus;
    private JLabel tableStatus;
    private JLabel tableRows;
    private JProgressBar progressBar;
    private JScrollPane scrollPane;

    private DataFiltering filterController;

    public AvailableFilterTablePanel() {
        initComponents();
        configureDisplayProperties();
        bind();
    }

    /**
     * Customizes display properties of contained components.
     * This is data-unrelated.
     */
    private void configureDisplayProperties() {
        // show column control
        filterTable.setColumnControlVisible(true);
        // replace grid lines with striping
        filterTable.setShowGrid(false, false);
        filterTable.addHighlighter(HighlighterFactory.createSimpleStriping());
        // initialize preferred size for table's viewable area
        filterTable.setVisibleRowCount(10);

        // create and configure a custom column factory
        CustomColumnFactory factory = new CustomColumnFactory();
        FilterRendering.configureColumnFactory(factory, getClass());

        // set the factory before setting the table model
        filterTable.setColumnFactory(factory);
    }

    /**
     * Binds components to data and user interaction.
     */
    protected void bind() {

        //  JXTable data properties
        filterModel = new FilterTableModel();
        // set the table model after setting the factory
        filterTable.setModel(filterModel);

        //  Filter control
        //      create the controller
        filterController = new DataFiltering(filterTable);
        // bind controller properties to input components
        BindingGroup filterGroup = new BindingGroup();
        filterGroup.addBinding(Bindings.createAutoBinding(READ,
                winnersCheckbox, BeanProperty.create("selected"),
                filterController, BeanProperty.create("showOnlyWinners")));
        filterGroup.addBinding(Bindings.createAutoBinding(READ,
                filterField, BeanProperty.create("text"),
                filterController, BeanProperty.create("filterString")));
        // PENDING JW: crude hack to update the statusbar - fake property
        // how-to do cleanly?
        filterGroup.addBinding(Bindings.createAutoBinding(READ,
                filterController, BeanProperty.create("showOnlyWinners"),
                this, BeanProperty.create("statusContent")));
        filterGroup.addBinding(Bindings.createAutoBinding(READ,
                filterController, BeanProperty.create("filterString"),
                this, BeanProperty.create("statusContent")));
        filterGroup.bind();

        filterModel.addTableModelListener(new TableModelListener() {
            public void tableChanged(TableModelEvent e) {
                updateStatusBar();
            }
        });

        //  JXTable column properties
        //      some display properties can be configured only after the model has been set, here:
        //      configure the view sequence of columns to be different from the model
        filterTable.setColumnSequence(new Object[]{
                FilterTableModel.name_ID,
                FilterTableModel.description_ID,
                FilterTableModel.user_ID,
                FilterTableModel.filter_ID
        });
    }

    /**
     * Binding artefact method: crude hack to update the status bar on state changes
     * from the controller.
     */
    public void setStatusContent(Object dummy) {
        updateStatusBar();
    }

    /**
     * Updates status labels. Called during loading and on
     * changes to the filter controller state.
     */
    protected void updateStatusBar() {
        tableStatus.setName(filterController.isFilteringByString()
                ? "searchCountLabel" : "rowCountLabel");
        tableRows.setText("" + filterTable.getRowCount());
    }


    public void start() {
        if (filterModel.getRowCount() != 0) return;
        // create SwingWorker which will load the data on a separate thread
        SwingWorker<?, ?> loader = new FilterDataLoader(
                AvailableFilterTablePanel.class.getResource("/demo/oscars.xml"), filterModel, dataPanel);

        // display progress bar while data loads
        progressBar = new JProgressBar();
        statusBarLeft.add(progressBar);
        // bind the worker's progress notification to the progressBar
        // and the worker's state notification to this
        BindingGroup group = new BindingGroup();
        group.addBinding(Bindings.createAutoBinding(READ,
                loader, BeanProperty.create("progress"),
                progressBar, BeanProperty.create("value")));
        group.addBinding(Bindings.createAutoBinding(READ,
                loader, BeanProperty.create("state"),
                this, BeanProperty.create("loadState")));
        group.bind();
        loader.execute();
    }

    /**
     * Callback for worker's state notification: cleanup if done.
     * @param state
     */
    public void setLoadState(StateValue state) {
        // Use SwingWorker to asynchronously load the data
        // remove progressbar if done loading
        if (state != StateValue.DONE) return;
        statusBarLeft.remove(progressBar);
        statusBarLeft.remove(actionStatus);
        revalidate();
        repaint();
    }

    //  Use SwingWorker to asynchronously load the data
    //      specialized on IbsContact


    //------------------ init ui
    //  JXTable display properties
    //      center column header text
    private JXTable createJXTable() {
        JXTable table = new JXTable() {

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
        return table;
    }

    protected void initComponents() {
        setLayout(new BorderLayout());

        controlPanel = createControlPanel();
        add(controlPanel, BorderLayout.NORTH);

        filterTable = createJXTable();
        filterTable.setName("filterTable");

        scrollPane = new JScrollPane(filterTable);
        dataPanel = new Stacker(scrollPane);
        add(dataPanel, BorderLayout.CENTER);

        add(createStatusBar(), BorderLayout.SOUTH);
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
        filterField = new JTextField(24);
        controlPanel.add(filterField, c);

        c.gridx = 1;
        c.gridy = 2;
        c.gridwidth = GridBagConstraints.REMAINDER;
        //c.insets.right = 24;
        //c.insets.left = 12;
        c.weightx = 0.0;
        c.anchor = GridBagConstraints.EAST;
        c.fill = GridBagConstraints.NONE;
        winnersCheckbox = new JCheckBox();
        winnersCheckbox.setText("Show only active filters");
        controlPanel.add(winnersCheckbox, c);

        return controlPanel;
    }

    protected Container createStatusBar() {

        JXStatusBar statusBar = new JXStatusBar();
        statusBar.putClientProperty("auto-add-separator", Boolean.FALSE);
        // Left status area
        statusBar.add(Box.createRigidArea(new Dimension(10, 22)));
        statusBarLeft = Box.createHorizontalBox();
        statusBar.add(statusBarLeft, JXStatusBar.Constraint.ResizeBehavior.FILL);
        actionStatus = new JLabel();
        actionStatus.setName("loadingStatusLabel");
        actionStatus.setHorizontalAlignment(JLabel.LEADING);
        statusBarLeft.add(actionStatus);

        // Middle (should stretch)
        //        statusBar.add(Box.createHorizontalGlue());
        //        statusBar.add(Box.createHorizontalGlue());
        statusBar.add(Box.createVerticalGlue());
        statusBar.add(Box.createRigidArea(new Dimension(50, 0)));

        // Right status area
        tableStatus = new JLabel();
        tableStatus.setName("rowCountLabel");
        JComponent bar = Box.createHorizontalBox();
        bar.add(tableStatus);
        tableRows = new JLabel("0");
        bar.add(tableRows);

        statusBar.add(bar);
        statusBar.add(Box.createHorizontalStrut(12));
        return statusBar;
    }

    //-----do nothing methods (keep beansbinding happy)

    public Object getStatusContent() {
        return null;
    }

    public StateValue getLoadState() {
        return null;
    }

}
