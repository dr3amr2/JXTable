package com.dr3amr2.jxtable.ibsFilter; /**
 * Created by dnguyen on 3/24/14.
 */

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;

/**
 * Data model for filterList data: a list of filterList beans.
 *
 * @author dnguyen
 */

public class FilterTableModel extends AbstractTableModel {
    public static final int NAME_COLUMN = 0;
    public static final int DESCRIPTION_COLUMN = 1;
    public static final int USER_COLUMN = 2;
    public static final int FILTER_COLUMN = 3;
    public static final int FILTER_ON_COLUMN = 4;
    public static final int COLUMN_COUNT = 5;

    public static final String name_ID = "Name";
    public static final String description_ID = "Description";
    public static final String user_ID = "User";
    public static final String filter_ID = "Filter";
    public static final String filter_on_ID = "Filter On";


    private static final String[] columnIds = {
            name_ID,
            description_ID,
            user_ID,
            filter_ID,
            filter_on_ID
    };

    @Override
    public String getColumnName(int column) {
        return columnIds[column];
    }

    private final List<FilterDataBean> filterList = new ArrayList<FilterDataBean>();

    public void add(List<FilterDataBean> newFilters) {
        int first = filterList.size();
        int last = first + newFilters.size() - 1;
        filterList.addAll(newFilters);
        fireTableRowsInserted(first, last);
    }

    public void add(FilterDataBean filter) {
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

    public FilterDataBean getCandidate(int row) {
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
            case FILTER_ON_COLUMN:
                return getCandidate(row).isFilterOn() ? Boolean.TRUE : Boolean.FALSE;
            case USER_COLUMN:
                return getCandidate(row).getUser();
        }
        return null;
    }
}