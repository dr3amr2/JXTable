/*
 * Created on 09.04.2008
 *
 */
package com.dr3amr2.jxtable.swingxDemo;

import org.jdesktop.beans.AbstractBean;
import org.jdesktop.swingx.JXTable;

import javax.swing.*;
import javax.swing.table.TableModel;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;


public class OscarFiltering extends AbstractBean {
    private RowFilter<TableModel, Integer> winnerFilter;
    private RowFilter<TableModel, Integer> searchFilter;
   
    private String filterString;
    private boolean showOnlyWinners = false;
    private JXTable oscarTable;

    
    public OscarFiltering(JXTable oscarTable) {
        this.oscarTable = oscarTable;
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
     * @param showOnlyWinners the showOnlyWinners to set
     */
    public void setShowOnlyWinners(boolean showOnlyWinners) {
        if (isShowOnlyWinners() == showOnlyWinners) return;
        boolean oldValue = isShowOnlyWinners();
        this.showOnlyWinners = showOnlyWinners;
        updateWinnerFilter();
        firePropertyChange("showOnlyWinners", oldValue, isShowOnlyWinners());
    }

    /**
     * @return the showOnlyWinners
     */
    public boolean isShowOnlyWinners() {
        return showOnlyWinners;
    }


    private void updateWinnerFilter() {
        winnerFilter = showOnlyWinners ? createWinnerFilter() : null;
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
        if ((searchFilter != null) && (winnerFilter != null)) {
            List<RowFilter<TableModel, Integer>> filters =
                new ArrayList<RowFilter<TableModel, Integer>>(2);
            filters.add(winnerFilter);
            filters.add(searchFilter);
            RowFilter<TableModel, Integer> comboFilter = RowFilter.andFilter(filters);
            oscarTable.setRowFilter(comboFilter);
        } else if (searchFilter != null) {
            oscarTable.setRowFilter(searchFilter);
        } else {
            oscarTable.setRowFilter(winnerFilter);
        }
    }


    private RowFilter<TableModel, Integer> createWinnerFilter() {
        RowFilter<TableModel, Integer> newRowFilter = new RowFilter<TableModel, Integer>() {
            @Override
            public boolean include(Entry<? extends TableModel, ? extends Integer> entry) {
                OscarTableModel oscarModel = (OscarTableModel) entry.getModel();
                OscarCandidate candidate = oscarModel.getCandidate(((Integer) entry.getIdentifier()).intValue());
                if (candidate.isWinner()) {
                    // Returning true indicates this row should be shown.
                    return true;
                }
                // loser
                return false;
            }
        };

        return newRowFilter;
    }
    
    //  Filter control
    //      create and return a custom RowFilter specialized on OscarCandidate
    private RowFilter<TableModel, Integer> createSearchFilter(final String filterString){
        return new RowFilter<TableModel, Integer>() {
            @Override
            public boolean include(Entry<? extends TableModel, ? extends Integer> entry) {
                OscarTableModel oscarModel = (OscarTableModel) entry.getModel();
                OscarCandidate candidate = oscarModel.getCandidate(((Integer) entry.getIdentifier()).intValue());
                boolean matches = false;
                Pattern p = Pattern.compile(filterString + ".*", Pattern.CASE_INSENSITIVE);
                // match against movie title
                String movie = candidate.getMovieTitle();
                if (movie != null) {
                    //                    if (movie.startsWith("The ")) {
                    //                        movie = movie.replace("The ", "");
                    //                    } else if (movie.startsWith("A ")) {
                    //                        movie = movie.replace("A ", "");
                    //                    }
                    // Returning true indicates this row should be shown.
                    matches = p.matcher(movie).matches();
                }
                List<String> persons = candidate.getPersons();
                for (String person : persons) {
                    // match against persons as well
                    if (p.matcher(person).matches()) {
                        matches = true;
                    }
                }
                return matches;
            }
        };
    }
}
