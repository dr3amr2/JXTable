package com.dr3amr2.jxtable;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by dnguyen on 3/24/14.
 */

public class FilterDataBean {

    private String name;
    private String description;
    private String user;
    private String filter;
    private final ArrayList<String> filters = new ArrayList<String>();

    /**
     * Creates a new instance of FilterDataBean
     */
    public FilterDataBean(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getFilter() {
        return filter;
    }

    public void setFilter(String filter) {
        this.filter = filter;
    }

    public List<String> getFilters() {
        return filters;
    }


}