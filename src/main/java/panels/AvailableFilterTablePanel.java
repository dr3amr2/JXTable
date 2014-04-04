package panels; /**
* Created by dnguyen on 3/24/14.
*/

import utils.CustomColumnFactory;
import org.jdesktop.beansbinding.BeanProperty;
import org.jdesktop.beansbinding.BindingGroup;
import org.jdesktop.beansbinding.Bindings;
import org.jdesktop.swingx.JXFrame;
import org.jdesktop.swingx.JXStatusBar;
import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.JXTableHeader;
import org.jdesktop.swingx.decorator.ColorHighlighter;
import org.jdesktop.swingx.decorator.HighlightPredicate;
import org.jdesktop.swingx.decorator.HighlighterFactory;
import org.jdesktop.swingx.sort.RowFilters;

import javax.swing.*;
import javax.swing.SwingWorker.StateValue;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableModel;
import java.awt.*;
import java.util.Comparator;
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
//        DemoUtils.injectResources(this);
        bind();
    }

    /**
     * Customizes display properties of contained components.
     * This is data-unrelated.
     */
    private void configureDisplayProperties() {
        //<snip> JXTable display properties
        // show column control
        filterTable.setColumnControlVisible(true);
        // replace grid lines with striping
        filterTable.setShowGrid(false, false);
        filterTable.addHighlighter(HighlighterFactory.createSimpleStriping());
        // initialize preferred size for table's viewable area
        filterTable.setVisibleRowCount(10);
        //        </snip>

        //<snip> JXTable column properties
        // create and configure a custom column factory
        CustomColumnFactory factory = new CustomColumnFactory();
//        OscarRendering.configureColumnFactory(factory, getClass());
        // set the factory before setting the table model
        filterTable.setColumnFactory(factory);
        //        </snip>

//        DemoUtils.setSnippet("JXTable display properties", filterTable);
//        DemoUtils.setSnippet("JXTable column properties", filterTable.getTableHeader());
//        DemoUtils.setSnippet("Filter control", filterField, winnersCheckbox, tableStatus, tableRows);
//        DemoUtils.setSnippet("Use SwingWorker to asynchronously load the data", statusBarLeft,
//                (JComponent) statusBarLeft.getParent());
    }

    /**
     * Binds components to data and user interaction.
     */
    protected void bind() {

        //<snip> JXTable data properties
        filterModel = new FilterTableModel();
        // set the table model after setting the factory
        filterTable.setModel(filterModel);
        //        </snip>

        // <snip> Filter control
        // create the controller
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
        //        </snip>
        filterModel.addTableModelListener(new TableModelListener() {
            public void tableChanged(TableModelEvent e) {
                updateStatusBar();
            }
        });

        //<snip> JXTable column properties
        // some display properties can be configured only after the model has been set, here:
        // configure the view sequence of columns to be different from the model
        filterTable.setColumnSequence(new Object[]{"nameColumn", "descriptionColumn", "userColumn", "filterColumn"});
        // </snip>
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
//        DemoUtils.injectResources(this, tableStatus);
        tableRows.setText("" + filterTable.getRowCount());
    }

    /**
     * Callback method for demo loader.
     */
    public void start() {
        if (filterModel.getRowCount() != 0) return;
        //<snip>Use SwingWorker to asynchronously load the data
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
        //        </snip>
    }

    /**
     * Callback for worker's state notification: cleanup if done.
     * @param state
     */
    public void setLoadState(StateValue state) {
        //<snip>Use SwingWorker to asynchronously load the data
        // remove progressbar if done loading
        if (state != StateValue.DONE) return;
        statusBarLeft.remove(progressBar);
        statusBarLeft.remove(actionStatus);
        revalidate();
        repaint();
        //        </snip>
    }

    //<snip>Use SwingWorker to asynchronously load the data
    // specialized on FilterDataBean


    //------------------ init ui
    //<snip> JXTable display properties
    // center column header text
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
                    //                    </snip>

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
        JXTable jxTable = initTable();
        configureJXTable(jxTable);

        //Create the scroll pane and add the table to it.
        JScrollPane scrollPane = new JScrollPane(jxTable);

        //Add the scroll pane to this panel.
        add(scrollPane, BorderLayout.CENTER);



//        add(scrollPane, BorderLayout.CENTER);
        add(createStatusBar(), BorderLayout.SOUTH);
    }



    /** Initialize our JXTable; this is standard stuff, just as with JTable */
    private JXTable initTable() {
        // boilerplate table-setup; this would be the same for a JTable
        JXTable table = new JXTable();

        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        // if we would want a per-table ColumnFactory we would have
        // to set it here, before setting the model
        // table.setColumnFactory(myVerySpecialColumnFactory);
        SampleTableModel model = new SampleTableModel();
        table.setModel(model);
        model.loadData();
        return table;
    }

    private void configureJXTable(JXTable jxTable) {
        // set the number of visible rows
        jxTable.setVisibleRowCount(30);
        // set the number of visible columns
        jxTable.setVisibleColumnCount(8);
        // This turns horizontal scrolling on or off. If the table is too large for the scrollpane,
        // and horizontal scrolling is off, columns will be resized to fit within the pane, which can
        // cause them to be unreadable. Setting this flag causes the table to be scrollable right to left.
        jxTable.setHorizontalScrollEnabled(true);

        // This shows the column control on the right-hand of the header.
        // All there is to it--users can now select which columns to view
        jxTable.setColumnControlVisible(true);

        // our data is pulling in too many columns by default, so let's hide some of them
        // column visibility is a property of the TableColumnExt class; we can look up a
        // TCE using a column's display name or its index
        jxTable.getColumnExt("LATITUDE").setVisible(false);
        jxTable.getColumnExt("LONGITUDE").setVisible(false);
        jxTable.getColumnExt("DEWPOINT").setVisible(false);
        jxTable.getColumnExt("VISIBILITY").setVisible(false);
        jxTable.getColumnExt("WIND_SPEED").setVisible(false);
        jxTable.getColumnExt("GUST_SPEED").setVisible(false);
        jxTable.getColumnExt("VISIBILITY_QUAL").setVisible(false);
        jxTable.getColumnExt("WIND_DIR").setVisible(false);
        jxTable.getColumnExt("WIND_DEG").setVisible(false);
        jxTable.getColumnExt("REGION").setVisible(false);

        // we can changed our mind
        jxTable.getColumnExt("LATITUDE").setVisible(true);

        // Sorting by clicking on column headers is on by default. However, the comparison
        // between rows uses a default compare on the column's type, and elevations
        // are not sorting how we want.
        //
        // We will override the Comparator assigned to the TableColumnExt instance assigned
        // to the elevation column. TableColumnExt has a property comparator will be used
        // by JXTable's sort methods.
        // By using a custom Comparator we can control how sorting in any column takes place
        Comparator numberComparator = new Comparator() {
            public int compare(Object o1, Object o2) {
                Double d1 = Double.valueOf(o1 == null ? "0" : (String) o1);
                Double d2 = Double.valueOf(o2 == null ? "0" : (String) o2);
                return d1.compareTo(d2);
            }
        };

        // comparators are good for special situations where the default comparator doesn't
        // understand our data.

        // setting the custom comparator
//        jxTable.getColumnExt("ELEVATION").setComparator(numberComparator);
//        jxTable.getColumnExt("TEMPERATURE").setComparator(numberComparator);

        // We'll add a highlighter to offset different row numbers
        // Note the setHighlighters() takes an array parameter; you can chain these together.
        jxTable.setHighlighters(HighlighterFactory.createSimpleStriping());

        // ...oops! we forgot one
        jxTable.addHighlighter(new ColorHighlighter(HighlightPredicate.ROLLOVER_ROW, Color.BLACK, Color.WHITE));

        // add a filter: include countries starting with a only
        int col = jxTable.getColumn("COUNTRY").getModelIndex();
        RowFilter<TableModel,Integer> temp = RowFilters.regexFilter(0, "^A", col);
        jxTable.setRowFilter(temp);

        // resize all the columns in the table to fit their contents
        // this is available as an item in the column control drop down as well, so the user can trigger it.
        int margin = 5;
        jxTable.packTable(margin);

        // we want the country name to always show, so we'll repack just that column
        // we can set a max size; if -1, the column is forced to be as large as necessary for the
        // text
        margin = 10;
        int max = -1;
        // JW: don't - all column indices are view coordinates
        // JW: maybe we need xtable api to take a TableColumn as argument?
        //jxTable.packColumn(jxTable.getColumnExt("COUNTRY").getModelIndex(), margin, max);
        int viewIndex = jxTable.convertColumnIndexToView(jxTable.getColumnExt("COUNTRY").getModelIndex());
        jxTable.packColumn(viewIndex, margin, max);
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
        winnersCheckbox.setName("winnersLabel");
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

    public static void main(String args[]) {

        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                AvailableFilterTablePanel availableFilterTablePanel = new AvailableFilterTablePanel();
                JXFrame frame = new JXFrame("Add Filter", true);
                frame.add(availableFilterTablePanel);
                frame.setSize(700, 400);
                frame.setVisible(true);

                availableFilterTablePanel.start();
            }
        });
    }

    //-----do nothing methods (keep beansbinding happy)

    public Object getStatusContent() {
        return null;
    }

    public StateValue getLoadState() {
        return null;
    }

}
