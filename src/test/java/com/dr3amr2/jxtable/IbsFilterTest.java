package com.dr3amr2.jxtable;

import com.dr3amr2.jxtable.panels.AvailableFilterTablePanel;
import org.jdesktop.swingx.JXFrame;

/**
 * Created by dnguyen on 4/10/2014.
 */
public class IbsFilterTest {
    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                AvailableFilterTablePanel availableFilterTablePanel = new AvailableFilterTablePanel();
                JXFrame frame = new JXFrame("Add Filter", true);
                frame.add(availableFilterTablePanel);
                frame.setSize(700, 400);
                frame.setVisible(true);

                availableFilterTablePanel.start();
            }
        });
    }
}
