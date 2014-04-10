package com.dr3amr2.jxtable.model;

import javax.swing.table.DefaultTableModel;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.net.URL;

/**
 * Created by dnguyen on 3/27/2014.
 */
public class SampleTableModel extends DefaultTableModel {
    public void loadData() {
        try {
            URL url = SampleTableModel.class.getResource("/demo/weather.txt");
            if(url != null)  {
                loadDataFromCSV(url);
            } else {
                System.out.println("Failed to load weather.txt");
                System.out.println("Path: " + SampleTableModel.class.getResource("/").getPath());
            }
        } catch (Exception e) {
            e.printStackTrace();
            loadDefaultData();
        }
    }

    private void loadDataFromCSV(URL url) {
        try {
            LineNumberReader lnr = new LineNumberReader(new InputStreamReader(url.openStream()));
            String line = lnr.readLine();
            String[] cols = line.split("\t");
            for (String col : cols) {
                addColumn(col);
            }
            while ((line = lnr.readLine()) != null) {
                addRow(line.split("\t"));
            }
        } catch (Exception e) {


            e.printStackTrace();
            loadDefaultData();
        }
    }

    private void loadDefaultData() {
        int colCnt = 6;
        int rowCnt = 10;
        for (int i = 0; i < colCnt; i++) {
            addColumn("Column-" + (i + 1));
        }
        for (int i = 0; i <= rowCnt; i++) {
            String[] row = new String[colCnt];
            for (int j = 0; j < colCnt; j++) {
                row[j] = "Row-" + i + "Column-" + (j + 1);
            }
            addRow(row);
        }
    }
}