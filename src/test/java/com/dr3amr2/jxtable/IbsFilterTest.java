package com.dr3amr2.jxtable;

import com.dr3amr2.jxtable.ibsFilter.FilterController;
import org.jdesktop.swingx.JXFrame;

/**
 * Created by dnguyen on 4/10/2014.
 */
public class IbsFilterTest {
    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                FilterController filterController = new FilterController();
                JXFrame frame = new JXFrame("Add Filter", true);
                frame.add(filterController.getPanel());
                frame.setSize(700, 400);
                frame.setVisible(true);
            }
        });
    }
}
