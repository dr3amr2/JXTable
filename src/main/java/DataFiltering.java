/**
* Created by dnguyen on 3/24/14.
*/
import org.jdesktop.beans.AbstractBean;
import org.jdesktop.swingx.JXTable;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;


public class DataFiltering extends AbstractBean {
    private RowFilter<Object, Object> winnerFilter;
    private RowFilter<Object, Object> searchFilter;

    private String filterString;
    private boolean showOnlyWinners = false;
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
        // <snip> Filter control
        // set the filter string (bound to the input in the textfield)
        // and update the search RowFilter
        this.filterString = filterString;
        updateSearchFilter();
        //        </snip>
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
//        winnerFilter = showOnlyWinners ? createWinnerFilter() : null;
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
        // <snip> Filter control
        // set the filters to the table
        if ((searchFilter != null) && (winnerFilter != null)) {
            List<RowFilter<Object, Object>> filters =
                    new ArrayList<RowFilter<Object, Object>>(2);
            filters.add(winnerFilter);
            filters.add(searchFilter);
            RowFilter<FilterTableModel, Integer> comboFilter = RowFilter.andFilter(filters);
            filterTable.setRowFilter(comboFilter);
        } else {
            if (searchFilter != null) {

//                filterTable.setRowFilter(searchFilter);
            } else {
//                filterTable.setRowFilter(winnerFilter);
            }
        }
        //        </snip>
    }


//    private RowFilter<Object, Object> createWinnerFilter() {
//        return new RowFilter<Object, Object>() {
//            @Override
//            public boolean include(Entry<? extends Object, ? extends Object> entry) {
//                FilterTableModel oscarModel = (FilterTableModel) entry.getModel();
//                FilterDataBean candidate = oscarModel.getCandidate(((Integer) entry.getIdentifier()).intValue());
//                if (candidate.isWinner()) {
//                    // Returning true indicates this row should be shown.
//                    return true;
//                }
//                // loser
//                return false;
//            }
//        };
//    }

    // <snip> Filter control
    // create and return a custom RowFilter specialized on FilterDataBean
    private RowFilter<Object, Object> createSearchFilter(final String filterString) {
        return new RowFilter<Object, Object>() {
            @Override
            public boolean include(Entry<? extends Object, ? extends Object> entry) {
                FilterTableModel oscarModel = (FilterTableModel) entry.getModel();
                FilterDataBean candidate = oscarModel.getCandidate(((Integer) entry.getIdentifier()).intValue());
                boolean matches = false;
                Pattern p = Pattern.compile(filterString + ".*", Pattern.CASE_INSENSITIVE);
                // match against movie title
                // TODO Implement to filter against all columns
                String movie = candidate.getDescription();
                if (movie != null) {
                    if (movie.startsWith("The ")) {
                        movie = movie.replace("The ", "");
                    } else if (movie.startsWith("A ")) {
                        movie = movie.replace("A ", "");
                    }
                    // Returning true indicates this row should be shown.
                    matches = p.matcher(movie).matches();
                }
                List<String> persons = candidate.getFilters();
                for (String person : persons) {
                    // match against persons as well
                    if (p.matcher(person).matches()) {
                        matches = true;
                    }
                }
                return matches;
                //                </snip>
            }
        };
    }

}
