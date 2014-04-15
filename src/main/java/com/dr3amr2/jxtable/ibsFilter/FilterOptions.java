package com.dr3amr2.jxtable.ibsFilter;

/**
 * Created by dnguyen on 4/11/2014.
 */
public enum FilterOptions {
    DISPLAY_ALL ("All"),
    DISPLAY_ACTIVE_ONLY ("Active Only"),
    DISPLAY_NONACTIVE_ONLY ("Non Active Only");

    private final String display;
    private FilterOptions(String s) {
        display = s;
    }
    @Override
    public String toString() {
        return display;
    }

}
