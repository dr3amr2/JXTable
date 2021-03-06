/*
* Created on 08.04.2008
*
*/
package com.dr3amr2.jxtable.ibsFilter.CustomTable;

import com.dr3amr2.jxtable.ibsFilter.FilterModel;
import com.dr3amr2.jxtable.utils.CustomColumnFactory;
import com.dr3amr2.jxtable.utils.ImageUtils;
import org.jdesktop.swingx.decorator.*;
import org.jdesktop.swingx.renderer.LabelProvider;
import org.jdesktop.swingx.renderer.StringValue;
import org.jdesktop.swingx.renderer.StringValues;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.logging.Logger;

public class FilterRendering {
    @SuppressWarnings("unused")
    private static final Logger LOG = Logger.getLogger(FilterRendering.class
            .getName());

    //  JXTable column properties
    //      Note: the custom column factory is a feature enhanced factory
    //      which allows column configuration based on column identifier
    public static void configureColumnFactory(CustomColumnFactory factory,
            Class<?> resourceBase) {
        // set location to load resources from
        factory.setBaseClass(resourceBase);

        // Section to Hide Columns
        factory.addHiddenNames(FilterTableModel.active_ID);

        // register a custom comparator
        Comparator<FilterModel> comparator = new Comparator<FilterModel>() {

            public int compare(FilterModel o1, FilterModel o2) {
                String contact1 = o1.getFilter();
                String contact2 = o2.getFilter();
                if (contact1 == null) return -1;
                if (contact2 == null) return 1;
                return contact1.compareTo(contact2);
            }

        };
        factory.addComparator(FilterTableModel.filter_ID, comparator);

        // add hints for column sizing
        FilterModel prototype = new FilterModel("IBS Filters");

        factory.addPrototypeValue(FilterTableModel.name_ID, prototype.getName());
        factory.addPrototypeValue(FilterTableModel.description_ID, prototype.getDescription());
        factory.addPrototypeValue(FilterTableModel.filter_ID, prototype.getFilter());
        factory.addPrototypeValue(FilterTableModel.user_ID, prototype.getUser());

        // register component providers per column identifier
        factory.addComponentProvider(FilterTableModel.name_ID, new LabelProvider(JLabel.LEFT));
        factory.addComponentProvider(FilterTableModel.description_ID, new LabelProvider(JLabel.LEFT));
        factory.addComponentProvider(FilterTableModel.user_ID, new LabelProvider(JLabel.LEFT));

        // Visual Decorators
        // .... and more

        // ToolTips for nominees column
        int iconSize = 24;
        ImageIcon activeFilterIconRaw = new ImageIcon(ImageUtils.getImageFromResources("/icons/on.png"));
        Icon activeFilterIcon = new ImageIcon(ImageUtils.getScaledInstance((BufferedImage) activeFilterIconRaw.getImage(), iconSize));

        ImageIcon nonActiveFilterIconRaw = new ImageIcon(ImageUtils.getImageFromResources("/icons/off.png"));
        Icon nonActiveFilterIcon = new ImageIcon(ImageUtils.getScaledInstance((BufferedImage) nonActiveFilterIconRaw.getImage(), iconSize));

        // Icon and tool tip decorator for active/non-active fitlers
        IconHighlighter activeIconHighlighter = new IconHighlighter(activeFilterIcon);
        ToolTipHighlighter activeFilterToolTip = new ToolTipHighlighter();
        activeFilterToolTip.addStringValue(new ListStringValue(true, "Active Filter", null), FilterTableModel.name_ID);

        // Icon and tool tip decorators for nominees
        IconHighlighter nonActiveIconHighlighter = new IconHighlighter(nonActiveFilterIcon);
        ToolTipHighlighter nonActiveFilterToolTip = new ToolTipHighlighter();
        nonActiveFilterToolTip.addStringValue(new ListStringValue(true, "Non-Active Filter", null), FilterTableModel.name_ID);

        // the predicate to decide which to use
        HighlightPredicate activeFilterPredicate = new HighlightPredicate() {

            public boolean isHighlighted(Component renderer,
                    ComponentAdapter adapter) {
                int modelColumn = adapter.getColumnIndex(FilterTableModel.active_ID);
                return (Boolean) adapter.getValue(modelColumn);
            }

        };
        // compound per-predicate and add as column highlighter to the factory
        factory.addHighlighter(FilterTableModel.name_ID, new CompoundHighlighter(
                new CompoundHighlighter(activeFilterPredicate, activeIconHighlighter, activeFilterToolTip),
                new CompoundHighlighter(new HighlightPredicate.NotHighlightPredicate(activeFilterPredicate),
                        nonActiveIconHighlighter, nonActiveFilterToolTip)));
    }

//----------------- not special to Rendering, but needed for SwingX


    /**
     *
     */
    public static class ToolTipHighlighter extends AbstractHighlighter {

        private List<StringValue> stringValues;
        private List<Object> sourceColumns;
        private String delimiter;


        /**
         * Adds a StringValue to use on the given sourceColumn.
         *
         * @param sv the StringValue to use.
         * @param sourceColumn the column identifier of the column to use.
         */
        public void addStringValue(StringValue sv, Object sourceColumn) {
           if (stringValues == null) {
               stringValues = new ArrayList<>();
               sourceColumns = new ArrayList<>();
           }
           stringValues.add(sv);
           sourceColumns.add(sourceColumn);
        }

        /**
         * Sets the delimiter to use between StringValues.
         *
         * @param delimiter the delimiter to use between StringValues, if there are more than one.
         */
        @SuppressWarnings("unused")
        public void setDelimiter(String delimiter) {
            this.delimiter = delimiter;
        }

        @Override
        protected Component doHighlight(Component component,
                ComponentAdapter adapter) {
            String toolTip = getToolTipText(adapter);
            // PENDING: treetableCellRenderer doesn't reset tooltip!
            if (toolTip != null) {
                ((JComponent) component).setToolTipText(toolTip);
            }
            return component;
        }

        private String getToolTipText(ComponentAdapter adapter) {
            if ((stringValues == null) || stringValues.isEmpty()) return null;
            String text = "";
            for (int i = 0; i < stringValues.size(); i++) {
                int modelIndex = adapter.getColumnIndex(sourceColumns.get(i));
                if (modelIndex >= 0) {
                   text += stringValues.get(i).getString(adapter.getValue(modelIndex));
                   if ((i != stringValues.size() - 1) && !isEmpty(text)){
                       text += delimiter;
                   }
                }
            }
            return isEmpty(text) ? null : text;
        }

        private boolean isEmpty(String text) {
            return text.length() == 0;
        }

        /**
         * Overridden to check for JComponent type.
         */
        @Override
        protected boolean canHighlight(Component component,
                ComponentAdapter adapter) {
            return component instanceof JComponent;
        }

    }

    public static class ListStringValue implements StringValue {

        boolean isToolTip;
        String singleToolTipPrefix;
        String multipleToolTipPrefix;

        @SuppressWarnings("unused")
        public ListStringValue() {
            this(false, null, null);
        }

        public ListStringValue(boolean asToolTip, String singleItem, String multipleItems) {
            this.isToolTip = asToolTip;
            this.singleToolTipPrefix = singleItem;
            this.multipleToolTipPrefix = multipleItems;
        }

        @SuppressWarnings("unchecked")
        public String getString(Object value) {
            if (value instanceof List) {
                List<String> persons = (List<String>) value;
                if (isToolTip) {
                    return getStringAsToolTip(persons);
                }
                return getStringAsContent(persons);
            }
            return StringValues.TO_STRING.getString(value);
        }

        private String getStringAsToolTip(List<String> persons) {
            if (persons.size() > 1) {
                StringBuffer winners = new StringBuffer("");
                if (multipleToolTipPrefix != null) {
                    winners.append(multipleToolTipPrefix);
                }
                for (String person : persons) {
                    winners.append(person);
                    winners.append(", ");
                }
                winners = winners.delete(winners.lastIndexOf(","), winners.length());
                return winners.toString();
            }
            return StringValues.TO_STRING.getString(singleToolTipPrefix);
        }

        private String getStringAsContent(List<String> persons) {
            if (persons.isEmpty()) {
                return "unknown";
            }
            if (persons.size() > 1) {
                return persons.get(0) + " + more ...";
            }
            return persons.get(0);
        }

    }
}
