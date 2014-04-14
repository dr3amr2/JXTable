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
    private RowFilter<TableModel, Integer> searchFilter;

    private String filterString;
    private boolean showOnlyActive = false;
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
        firePropertyChange("filterString", oldValue, getFilterString());
    }

    /**
     * @return the filterString
     */
    public String getFilterString() {
        return filterString;
    }

    /**
     * @param showOnlyActive the showOnlyActive to set
     */
    public void setShowOnlyActive(boolean showOnlyActive) {
        if (isShowOnlyActive() == showOnlyActive) return;
        boolean oldValue = isShowOnlyActive();
        this.showOnlyActive = showOnlyActive;
        updateActiveFilter();
        firePropertyChange(FilterTableModel.activeFilters_FireProperty, oldValue, isShowOnlyActive());
    }

    /**
     * @return the showOnlyActive
     */
    public boolean isShowOnlyActive() {
        return showOnlyActive;
    }


    private void updateActiveFilter() {
        activeFilter = showOnlyActive ? createActiveFilter() : null;
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
        if ((searchFilter != null) && (activeFilter != null)) {
            List<RowFilter<TableModel, Integer>> filters =
                    new ArrayList<>(2);
            filters.add(activeFilter);
            filters.add(searchFilter);
            RowFilter<TableModel, Integer> comboFilter = RowFilter.andFilter(filters);
            filterTable.setRowFilter(comboFilter);
        } else {
            if (searchFilter != null) {

                filterTable.setRowFilter(searchFilter);
            } else {
                filterTable.setRowFilter(activeFilter);
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
