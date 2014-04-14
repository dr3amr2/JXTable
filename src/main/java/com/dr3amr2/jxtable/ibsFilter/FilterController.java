package com.dr3amr2.jxtable.ibsFilter;

import com.dr3amr2.jxtable.ibsFilter.CustomTable.DataFiltering;
import com.dr3amr2.jxtable.ibsFilter.CustomTable.FilterDataLoader;
import com.dr3amr2.jxtable.ibsFilter.CustomTable.FilterRendering;
import com.dr3amr2.jxtable.ibsFilter.CustomTable.FilterTableModel;
import com.dr3amr2.jxtable.utils.CustomColumnFactory;
import com.dr3amr2.jxtable.utils.Stacker;
import org.jdesktop.beansbinding.BeanProperty;
import org.jdesktop.beansbinding.BindingGroup;
import org.jdesktop.beansbinding.Bindings;
import org.jdesktop.swingx.JXStatusBar;
import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.JXTableHeader;
import org.jdesktop.swingx.decorator.HighlighterFactory;

import javax.swing.*;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.util.logging.Logger;

import static org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ;

/**
 * Created by dnguyen on 4/14/2014.
 *
 * Filter Controller that coordinates all interactions between user and the panel
 */
public class FilterController {

    static final Logger logger = Logger.getLogger(FilterController.class.getName());

    private FilterTableModel filterTableModel;
    private FilterPanel panel;
    private DataFiltering filterTableController;

    // JX Table Variables
    private Stacker dataPanel;
    private JXTable filterTable;
    private JScrollPane scrollPane;
    private int visibleRowCount = 20;
    private String dataLocation = "/demo/DummyFilterContacts.xml";

    // Status Bar variables
    private JComponent statusBarLeft;
    private JLabel actionStatus;
    private JLabel tableStatus;
    private JLabel tableRows;
    private JProgressBar progressBar;

    // Binding Variables
    private String bindSearchField = "bindSearchField";


    public FilterController() {
        panel = new FilterPanel();
        initComponents();
        configureTableDisplayProperties();
        bind();
    }

    /**
     * Customizes display properties of contained components - filterTable.
     * This is data-unrelated.
     */
    private void configureTableDisplayProperties() {
        // Disable Column Controller
        filterTable.setColumnControlVisible(false);

        // Replace grid lines with striping
        filterTable.setShowGrid(false, false);
        filterTable.addHighlighter(HighlighterFactory.createSimpleStriping());

        // Init with preferred number of visible rows
        filterTable.setVisibleRowCount(visibleRowCount);

        // Create and configure a custom column factory
        CustomColumnFactory factory = new CustomColumnFactory();
        FilterRendering.configureColumnFactory(factory, getClass());

        // Set the factory before setting the table model
        filterTable.setColumnFactory(factory);
    }

    /**
     * Binds components to data and user interaction.
     */
    protected void bind() {

        filterTableModel = new FilterTableModel();
        filterTable.setModel(filterTableModel);
        filterTableController = new DataFiltering(filterTable);


        // bind controller properties to input components
        BindingGroup filterGroup = new BindingGroup();

        filterGroup.addBinding(Bindings.createAutoBinding(READ,
                panel.activeFiltersCheckbox, BeanProperty.create("selected"),
                filterTableController, BeanProperty.create(FilterTableModel.activeFilters_FireProperty)));

        filterGroup.addBinding(Bindings.createAutoBinding(READ,
                panel.filterField, BeanProperty.create("text"),
                filterTableController, BeanProperty.create(FilterTableModel.filterString_FireProperty)));
        // PENDING JW: crude hack to update the statusbar - fake property
        // how-to do cleanly?
        filterGroup.addBinding(Bindings.createAutoBinding(READ,
                filterTableController, BeanProperty.create(FilterTableModel.activeFilters_FireProperty),
                this, BeanProperty.create(FilterTableModel.statusContent_FireProperty)));

        filterGroup.addBinding(Bindings.createAutoBinding(READ,
                filterTableController, BeanProperty.create(FilterTableModel.filterString_FireProperty),
                this, BeanProperty.create(FilterTableModel.statusContent_FireProperty)));
        filterGroup.bind();

        filterTableModel.addTableModelListener(new TableModelListener() {
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
        tableStatus.setName(filterTableController.isFilteringByString()
                ? "searchCountLabel" : "rowCountLabel");
        tableRows.setText("" + filterTable.getRowCount());
    }


    public void start() {
        if (filterTableModel.getRowCount() != 0) return;

        // Loading Data on Separate thread
        SwingWorker<?, ?> loader = new FilterDataLoader(
                FilterController.class.getResource(dataLocation), filterTableModel, dataPanel);

        // Update Progress bar while loading Data
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
    public void setLoadState(SwingWorker.StateValue state) {
        // Use SwingWorker to asynchronously load the data
        // remove progressbar if done loading
        if (state != SwingWorker.StateValue.DONE) return;
        statusBarLeft.remove(progressBar);
        statusBarLeft.remove(actionStatus);
        panel.revalidate();
        panel.repaint();
    }


    //  JXTable display properties all set to "centered" column alignment
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

        filterTable = createJXTable();
        scrollPane = new JScrollPane(filterTable);
        dataPanel = new Stacker(scrollPane);
        panel.add(dataPanel, BorderLayout.CENTER);

        panel.add(createStatusBar(), BorderLayout.SOUTH);
    }

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

    public JPanel getPanel(){
        return panel;
    }
    //-----do nothing methods (keep beansbinding happy)

    public Object getStatusContent() {
        return null;
    }

    public SwingWorker.StateValue getLoadState() {
        return null;
    }

}

