package com.dr3amr2.jxtable.ibsFilter.CustomTable; /**
* Created by dnguyen on 3/24/14.
*/
import com.dr3amr2.jxtable.ibsFilter.FilterModel;
import org.jdesktop.beans.AbstractBean;
import org.jdesktop.swingx.JXTable;

import javax.swing.*;
import javax.swing.table.TableModel;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;


public class DataFiltering extends AbstractBean {
    private RowFilter<TableModel, Integer> activeFilter;
    private RowFilter<TableModel, Integer> nonActiveFilter;
    private RowFilter<TableModel, Integer> searchFilter;

    // Binded variables
    private boolean showOnlyActiveFilter = false;
    private boolean showOnlyNonActiveFilter = false;
    private String filterString;

    private JXTable filterTable;


    public DataFiltering(JXTable filterTable) {
        this.filterTable = filterTable;
    }

    public boolean isFilteringByString() {
        return !isEmpty(getFilterString());
    }

    private boolean isEmpty(String filterString) {
        return filterString == null || filterString.length() == 0;
    }

    /**
     * @param filterString the filterString to set
     */
    public void setFilterString(String filterString) {
        String oldValue = getFilterString();
        //  Filter control
        //      set the filter string (bound to the input in the textfield)
        //      and update the search RowFilter
        this.filterString = filterString;
        updateSearchFilter();
        firePropertyChange(FilterTableModel.filterString_FireProperty, oldValue, getFilterString());
    }

    /**
     * @return the filterString
     */
    public String getFilterString() {
        return filterString;
    }

    /**
     * @param showOnlyActiveFilter the showOnlyActiveFilter to set
     */
    public void setShowOnlyActiveFilter(boolean showOnlyActiveFilter) {
        if (isShowOnlyActiveFilter() == showOnlyActiveFilter) return;
        boolean oldValue = isShowOnlyActiveFilter();
        this.showOnlyActiveFilter = showOnlyActiveFilter;
        updateActiveFilter();
        firePropertyChange(FilterTableModel.activeFilters_FireProperty, oldValue, isShowOnlyActiveFilter());
    }

    /**
     * @return the showOnlyActiveFilter
     */
    public boolean isShowOnlyActiveFilter() {
        return showOnlyActiveFilter;
    }

    /**
     * @param showOnlyNonActiveFilter the showOnlyNonActiveFilter to set
     */
    public void setShowOnlyNonActiveFilter(boolean showOnlyNonActiveFilter) {
        if (isShowOnlyNonActiveFilter() == showOnlyNonActiveFilter) return;
        boolean oldValue = isShowOnlyNonActiveFilter();
        this.showOnlyNonActiveFilter = showOnlyNonActiveFilter;
        updateNonActiveFilter();
        firePropertyChange(FilterTableModel.nonActiveFilters_FireProperty, oldValue, isShowOnlyNonActiveFilter());
    }

    /**
     * @return the showOnlyActiveFilter
     */
    public boolean isShowOnlyNonActiveFilter() {
        return showOnlyNonActiveFilter;
    }


    private void updateActiveFilter() {
        activeFilter = showOnlyActiveFilter ? createActiveFilter() : null;
        updateFilters();
    }

    private void updateNonActiveFilter() {
        activeFilter = showOnlyNonActiveFilter ? createNonActiveFilter() : null;
        updateFilters();
    }

    private void updateSearchFilter() {
        if ((filterString != null) && (filterString.length() > 0)) {
            searchFilter = createSearchFilter(filterString + ".*");
        } else {
            searchFilter = null;
        }
        updateFilters();
    }


    private void updateFilters() {
        //  Filter control
        //      set the filters to the table
        if ((searchFilter != null) && (activeFilter != null) && (nonActiveFilter == null)) {
            List<RowFilter<TableModel, Integer>> filters =
                    new ArrayList<>(2);
            filters.add(activeFilter);
            filters.add(searchFilter);
            RowFilter<TableModel, Integer> comboFilter = RowFilter.andFilter(filters);
            filterTable.setRowFilter(comboFilter);

        } else if ((searchFilter != null) && (activeFilter == null) && (nonActiveFilter != null)) {
            List<RowFilter<TableModel, Integer>> filters =
                    new ArrayList<>(2);
            filters.add(nonActiveFilter);
            filters.add(searchFilter);
            RowFilter<TableModel, Integer> comboFilter = RowFilter.andFilter(filters);
            filterTable.setRowFilter(comboFilter);

        } else if ((searchFilter != null) && (activeFilter != null) && (nonActiveFilter != null)) {
            List<RowFilter<TableModel, Integer>> filters =
                    new ArrayList<>(2);
            filters.add(activeFilter);
            filters.add(nonActiveFilter);
            filters.add(searchFilter);
            RowFilter<TableModel, Integer> comboFilter = RowFilter.andFilter(filters);
            filterTable.setRowFilter(comboFilter);

        } else {
            if (searchFilter != null) {
                filterTable.setRowFilter(searchFilter);
            } else if (activeFilter != null) {
                filterTable.setRowFilter(activeFilter);
            } else if (nonActiveFilter != null){
                filterTable.setRowFilter(nonActiveFilter);
            } else {
                filterTable.setRowFilter(searchFilter);
            }
        }
    }


    private RowFilter<TableModel, Integer> createActiveFilter() {
        return new RowFilter<TableModel, Integer>() {
            @Override
            public boolean include(Entry<? extends TableModel, ? extends Integer> entry) {
                FilterTableModel filterTableModel = (FilterTableModel) entry.getModel();
                FilterModel candidate = filterTableModel.getCandidate(entry.getIdentifier());
                if (candidate.isActive()) {
                    // Returning true indicates this row should be shown.
                    return true;
                }
                // loser
                return false;
            }
        };
    }

    private RowFilter<TableModel, Integer> createNonActiveFilter() {
        return new RowFilter<TableModel, Integer>() {
            @Override
            public boolean include(Entry<? extends TableModel, ? extends Integer> entry) {
                FilterTableModel filterTableModel = (FilterTableModel) entry.getModel();
                FilterModel candidate = filterTableModel.getCandidate(entry.getIdentifier());
                if (!candidate.isActive()) {
                    // Returning true indicates this row is tag as non-active
                    return true;
                }
                // else tag as false because it is active
                return false;
            }
        };
    }

    //  Filter control
    //      create and return a custom RowFilter specialized on FilterModel
    private RowFilter<TableModel, Integer> createSearchFilter(final String filterString) {
        return new RowFilter<TableModel, Integer>() {
            @Override
            public boolean include(Entry<? extends TableModel, ? extends Integer> entry) {
                FilterTableModel filterTableModel = (FilterTableModel) entry.getModel();
                FilterModel contact;
                contact = filterTableModel.getCandidate(entry.getIdentifier());
                boolean matches = false;
                Pattern p = Pattern.compile(filterString + ".*", Pattern.CASE_INSENSITIVE);

                // Match against all columns
                String filterSearch = contact.getFilter();
                if (filterSearch != null) {
                    matches = p.matcher(filterSearch).matches();
                }

                String descriptionSearch = contact.getDescription();
                if (descriptionSearch != null && !matches) {
                    matches = p.matcher(descriptionSearch).matches();
                }

                String nameSearch = contact.getName();
                if (nameSearch != null && !matches) {
                    matches = p.matcher(nameSearch).matches();
                }

                String userSearch = contact.getUser();
                if (userSearch != null && !matches) {
                    matches = p.matcher(userSearch).matches();
                }

                List<String> filters = contact.getFilters();
                for (String filter : filters) {
                    // match against persons as well
                    if (p.matcher(filter).matches()) {
                        matches = true;
                    }
                }
                return matches;
            }
        };
    }

}
