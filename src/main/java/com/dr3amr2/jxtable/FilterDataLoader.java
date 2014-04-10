package com.dr3amr2.jxtable;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by dnguyen on 3/27/2014.
 */
public class FilterDataLoader extends SwingWorker<List<FilterDataBean>, FilterDataBean> {
    private final URL oscarData;
    private final FilterTableModel oscarModel;
    private final List<FilterDataBean> candidates = new ArrayList<FilterDataBean>();
    private final Stacker dataPanel;
    private JLabel credits;

    public FilterDataLoader(URL filterURL, FilterTableModel filterTableModel, Stacker dataPanel) {
        this.oscarData = filterURL;
        this.oscarModel = filterTableModel;
        this.dataPanel = dataPanel;
    }

    //Use SwingWorker to asynchronously load the data
    // background task let a parser do its stuff and
    // update a progress bar
    @Override
    public List<FilterDataBean> doInBackground() {
        FilterParser parser = new FilterParser() {
            @Override
            protected void addCandidate(FilterDataBean candidate) {
                candidates.add(candidate);
                if (candidates.size() % 3 == 0) {
                    try { // slow it down so we can see progress :-)
                        Thread.sleep(1);
                    } catch (Exception ex) {
                    }
                }
                publish(candidate);
                setProgress(100 * candidates.size() / 8545);
            }
        };
        parser.parseDocument(oscarData);
        return candidates;
    }
    //        </snip>

    @Override
    protected void process(List<FilterDataBean> moreCandidates) {
        if (credits == null) {
            showCredits();
        }
        oscarModel.add(moreCandidates);
    }

    // For older Java 6 on OS X
    @SuppressWarnings("unused")
    protected void process(FilterDataBean... moreCandidates) {
        for (FilterDataBean candidate : moreCandidates) {
            oscarModel.add(candidate);
        }
    }

    //>Use SwingWorker to asynchronously load the data
    // show a transparent overlay on start loading
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