package com.dr3amr2.jxtable.ibsFilter;

import com.dr3amr2.jxtable.ibsFilter.CustomTable.DataFiltering;
import com.dr3amr2.jxtable.ibsFilter.CustomTable.FilterDataLoader;
import com.dr3amr2.jxtable.ibsFilter.CustomTable.FilterRendering;
import com.dr3amr2.jxtable.ibsFilter.CustomTable.FilterTableModel;
import com.dr3amr2.jxtable.utils.CustomColumnFactory;
import org.jdesktop.beansbinding.BeanProperty;
import org.jdesktop.beansbinding.BindingGroup;
import org.jdesktop.beansbinding.Bindings;
import org.jdesktop.swingx.decorator.HighlighterFactory;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import java.util.logging.Logger;

import static org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ;

/**
 * Created by dnguyen on 4/14/2014.
 *
 * Filter Controller that coordinates all interactions between user and the panel
 */
public class FilterController {

    private final Logger logger = Logger.getLogger(FilterController.class.getName());

    private FilterTableModel filterTableModel;
    private FilterPanel panel;
    private DataFiltering filterTableController;

    // JX Table Variables
    protected int visibleRowCount = 20;
    protected String dataLocation = "/demo/DummyFilterContacts.xml";


    public FilterController() {
        panel = new FilterPanel();
        configureTableDisplayProperties();
        bind();
        setTableColumnSequence();
        registerListener();
        loadExternalData();
    }

    private void setTableColumnSequence() {
        //  JXTable column properties
        //      some display properties can be configured only after the model has been set, here:
        //      configure the view sequence of columns to be different from the model
        panel.filterTable.setColumnSequence(new Object[]{
                FilterTableModel.name_ID,
                FilterTableModel.description_ID,
                FilterTableModel.user_ID,
                FilterTableModel.filter_ID
        });
    }


    /**
     * Section that register Listeners to all the components
     */
    private void registerListener() {
        // TODO: This is a just a temp fix to isolate the issue when both active and nonactive check box are selected
        panel.activeFiltersCheckbox.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                if(panel.activeFiltersCheckbox.isSelected() && panel.inactiveFiltersCheckbox.isSelected()){
                    panel.inactiveFiltersCheckbox.setSelected(false);
                }
            }
        });

        panel.inactiveFiltersCheckbox.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                if (panel.activeFiltersCheckbox.isSelected() && panel.inactiveFiltersCheckbox.isSelected()) {
                    panel.activeFiltersCheckbox.setSelected(false);
                }
            }
        });

        // Update Status bar whenever the table size changes.
        filterTableModel.addTableModelListener(new TableModelListener() {
            public void tableChanged(TableModelEvent e) {
                updateStatusBar();
            }
        });
    }

    /**
     * Customizes display properties of contained components - filterTable.
     * This is data-unrelated.
     */
    private void configureTableDisplayProperties() {
        // Disable Column Controller
        panel.filterTable.setColumnControlVisible(false);

        // Replace grid lines with striping
        panel.filterTable.setShowGrid(false, false);
        panel.filterTable.addHighlighter(HighlighterFactory.createSimpleStriping());

        // Init with preferred number of visible rows
        panel.filterTable.setVisibleRowCount(visibleRowCount);

        // Create and configure a custom column factory
        CustomColumnFactory factory = new CustomColumnFactory();
        FilterRendering.configureColumnFactory(factory, getClass());

        // Set the factory before setting the table model
        panel.filterTable.setColumnFactory(factory);
    }

    /**
     * Binds components to data and user interaction.
     */
    protected void bind() {

        filterTableModel = new FilterTableModel();
        panel.filterTable.setModel(filterTableModel);
        filterTableController = new DataFiltering(panel.filterTable);

        // Bind controller properties to input components
        BindingGroup filterGroup = new BindingGroup();

        // Bind Active Checkbox to DataFiltering
        filterGroup.addBinding(Bindings.createAutoBinding(READ,
                panel.activeFiltersCheckbox, BeanProperty.create("selected"),
                filterTableController, BeanProperty.create(FilterTableModel.activeFilters_FireProperty)));

        // Bind NonActive Checkbox to DataFiltering
        filterGroup.addBinding(Bindings.createAutoBinding(READ,
                panel.inactiveFiltersCheckbox, BeanProperty.create("selected"),
                filterTableController, BeanProperty.create(FilterTableModel.inactiveFilters_FireProperty)));

        // Bind Search text to DataFiltering
        filterGroup.addBinding(Bindings.createAutoBinding(READ,
                panel.filterField, BeanProperty.create("text"),
                filterTableController, BeanProperty.create(FilterTableModel.filterString_FireProperty)));

        // Binding user inputs to update statusContent (IE update number of available fitlers)
        filterGroup.addBinding(Bindings.createAutoBinding(READ,
                filterTableController, BeanProperty.create(FilterTableModel.activeFilters_FireProperty),
                this, BeanProperty.create(FilterTableModel.statusContent_FireProperty)));

        filterGroup.addBinding(Bindings.createAutoBinding(READ,
                filterTableController, BeanProperty.create(FilterTableModel.inactiveFilters_FireProperty),
                this, BeanProperty.create(FilterTableModel.statusContent_FireProperty)));

        filterGroup.addBinding(Bindings.createAutoBinding(READ,
                filterTableController, BeanProperty.create(FilterTableModel.filterString_FireProperty),
                this, BeanProperty.create(FilterTableModel.statusContent_FireProperty)));

        filterGroup.bind();
    }

    private void loadExternalData() {
        if (filterTableModel.getRowCount() != 0) return;

        // Loading Data on Separate thread
        SwingWorker<?, ?> loader = new FilterDataLoader(
                FilterController.class.getResource(dataLocation), filterTableModel, panel.dataPanel);

        // Update Progress bar while loading Data
        panel.progressBar = new JProgressBar();
        panel.statusBarLeft.add(panel.progressBar);

        // bind the worker's progress notification to the progressBar
        // and the worker's state notification to this
        BindingGroup group = new BindingGroup();

        group.addBinding(Bindings.createAutoBinding(READ,
                loader, BeanProperty.create("progress"),
                panel.progressBar, BeanProperty.create("value")));

        group.addBinding(Bindings.createAutoBinding(READ,
                loader, BeanProperty.create("state"),
                this, BeanProperty.create("loadState")));

        group.bind();

        loader.execute();

        // TODO - There's a bug where it doesn't properly display number of filters on first bootup
    }

    /**
     * Callback for worker's state notification: cleanup if done.
     * @param state - Current state of SwingWorker
     */
    @SuppressWarnings("unused")
    public void setLoadState(SwingWorker.StateValue state) {
        // Use SwingWorker to asynchronously load the data
        // remove progressbar if done loading
        if (state != SwingWorker.StateValue.DONE) return;

        panel.statusBarLeft.remove(panel.progressBar);
        panel.statusBarLeft.remove(panel.actionStatus);
        panel.revalidate();
        panel.repaint();
    }

    /**
     * Updates status labels. Called during loading and on
     * changes to the filter controller state.
     */
    protected void updateStatusBar() {
        if (filterTableController.isFilteringByString()) {
            panel.tableStatus.setName("searchCountLabel");
        }
        else panel.tableStatus.setName("rowCountLabel");

        panel.tableRows.setText("" + panel.filterTable.getRowCount() + " Filters");
        panel.filterTable.packAll();
    }

    public JPanel getPanel(){
        return panel;
    }

    //-----do nothing methods (keep beansbinding happy)
    @SuppressWarnings("unused")
    public Object getStatusContent() {
        return null;
    }
    @SuppressWarnings("unused")
    public SwingWorker.StateValue getLoadState() {
        return null;
    }

    /**
     * Binding artefact method: crude hack to update the status bar on state changes
     * from the controller.
     */
    @SuppressWarnings("unused")
    public void setStatusContent(Object dummy) {
        updateStatusBar();
    }

}

