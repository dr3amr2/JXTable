package com.dr3amr2.jxtable.ibsFilter.CustomTable;

import com.dr3amr2.jxtable.ibsFilter.FilterModel;
import com.dr3amr2.jxtable.utils.Stacker;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by dnguyen on 3/27/2014.
 *
 * FilterDataLoader fetches the data and loads them into the table model
 */
public class FilterDataLoader extends SwingWorker<List<FilterModel>, FilterModel> {
    private final URL filterDataURL;
    private final FilterTableModel filterTableModel;
    private final List<FilterModel> contacts = new ArrayList<>();
    private final Stacker dataPanel;
    private JLabel credits;

    public FilterDataLoader(URL filterURL, FilterTableModel filterTableModel, Stacker dataPanel) {
        this.filterDataURL = filterURL;
        this.filterTableModel = filterTableModel;
        this.dataPanel = dataPanel;
    }

    //Use SwingWorker to asynchronously load the data
    // background task let a parser do its stuff and
    // update a progress bar
    @Override
    public List<FilterModel> doInBackground() {
        FilterParser parser = new FilterParser() {
            @Override
            protected void addCandidate(FilterModel contact) {
                contacts.add(contact);
                if (contacts.size() % 3 == 0) {
                    try { // slow it down so we can see progress :-)
                        Thread.sleep(1);
                    } catch (Exception ignored) {
                    }
                }
                publish(contact);
                setProgress(100 * contacts.size() / 8545);
            }
        };
        parser.parseDocument(filterDataURL);
        return contacts;
    }
    //        </snip>

    @Override
    protected void process(List<FilterModel> moreContacts) {
        if (credits == null) {
            showCredits();
        }
        filterTableModel.add(moreContacts);
    }

    // For older Java 6 on OS X
    @SuppressWarnings("unused")
    protected void process(FilterModel... moreContacts) {
        for (FilterModel contact : moreContacts) {
            filterTableModel.add(contact);
        }
    }

    //  Use SwingWorker to asynchronously load the data
    //      show a transparent overlay on start loading
    private void showCredits() {
        credits = new JLabel();
        credits.setName("credits");
        credits.setFont(UIManager.getFont("Table.font").deriveFont(24f));
        credits.setHorizontalAlignment(JLabel.CENTER);
        credits.setBorder(new CompoundBorder(new TitledBorder(""),
                new EmptyBorder(20,20,20,20)));

        dataPanel.showMessageLayer(credits, .75f);
    }

    @Override
    //Use SwingWorker to asynchronously load the data
    // hide transparend overlay on end loading
    protected void done() {
        setProgress(100);
            dataPanel.hideMessageLayer();
    }
}