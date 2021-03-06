package com.dr3amr2.jxtable.ibsFilter.CustomTable;

import com.dr3amr2.jxtable.ibsFilter.FilterModel;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;

/**
 * FilterTableModel class holds all the data related model for the JXTable
 *
 * @author dnguyen
 */

public class FilterTableModel extends AbstractTableModel {
    public static final int NAME_COLUMN = 0;
    public static final int DESCRIPTION_COLUMN = 1;
    public static final int USER_COLUMN = 2;
    public static final int FILTER_COLUMN = 3;
    public static final int ACTIVE_COLUMN = 4;
    public static final int COLUMN_COUNT = 5;

    public static final String name_ID = "Name";
    public static final String description_ID = "Description";
    public static final String user_ID = "User";
    public static final String filter_ID = "Filter";
    public static final String active_ID = "isActive";

    // String must match variable name in DataFiltering class (Binding variables)
    public static final String activeFilters_FireProperty = "showOnlyActiveFilter";
    public static final String inactiveFilters_FireProperty = "showOnlyInactiveFilter";
    public static final String statusContent_FireProperty = "statusContent";
    public static final String filterString_FireProperty = "filterString";


    private static final String[] columnIds = {
            name_ID,
            description_ID,
            user_ID,
            filter_ID,
            active_ID
    };

    @Override
    public String getColumnName(int column) {
        return columnIds[column];
    }

    private final List<FilterModel> filterList = new ArrayList<>();

    public void add(List<FilterModel> newFilters) {
        int first = filterList.size();
        int last = first + newFilters.size() - 1;
        filterList.addAll(newFilters);
        fireTableRowsInserted(first, last);
    }

    public void add(FilterModel filter) {
        int index = this.filterList.size();
        this.filterList.add(filter);
        fireTableRowsInserted(index, index);
    }

    public int getRowCount() {
        return filterList.size();
    }

    public int getColumnCount() {
        return COLUMN_COUNT;
    }

    @Override
    public Class<?> getColumnClass(int column) {
        return getValueAt(0, column).getClass();
    }

    public FilterModel getCandidate(int row) {
        return filterList.get(row);
    }

    public Object getValueAt(int row, int column) {
        if (row >= getRowCount()) {
            return new Object();
        }
        switch (column) {
            case NAME_COLUMN:
                return getCandidate(row).getName();
            case DESCRIPTION_COLUMN:
                return getCandidate(row).getDescription();
            case FILTER_COLUMN:
                return getCandidate(row).getFilter();
            case ACTIVE_COLUMN:
                return getCandidate(row).isActive() ? Boolean.TRUE : Boolean.FALSE;
            case USER_COLUMN:
                return getCandidate(row).getUser();
        }
        return null;
    }
}