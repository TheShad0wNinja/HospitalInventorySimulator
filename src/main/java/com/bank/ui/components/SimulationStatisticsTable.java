package com.bank.ui.components;

import com.bank.simulation.SimulationStatistics;
import com.bank.ui.Theme;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.util.ArrayList;

public class SimulationStatisticsTable extends JPanel {

    private final JTable table;
    private final DefaultTableModel tableModel;

    public SimulationStatisticsTable() {
        setLayout(new BorderLayout(0, 10));
        setBackground(Theme.PANEL_BG);

        String[] columnNames = {"Metric", "Value"};

        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };

        table = new JTable(tableModel);
        table.setFont(Theme.DEFAULT_FONT.deriveFont(13f));
        table.setRowHeight(32);
        table.setForeground(Theme.TEXT_PRIMARY);
        table.setGridColor(Theme.BORDER);
        table.setShowGrid(true);
        table.setIntercellSpacing(new Dimension(1, 0));

        DefaultTableCellRenderer cellRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(
                    JTable table, Object value, boolean isSelected,
                    boolean hasFocus, int row, int column
            ) {
                super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (isSelected) {
                    setBackground(Theme.PRIMARY_LIGHT);
                    setForeground(Theme.PRIMARY);
                } else {
                    setBackground(row % 2 == 0 ? Theme.PANEL_BG : Theme.BACKGROUND);
                    setForeground(Theme.TEXT_PRIMARY);
                }
                setBorder(BorderFactory.createEmptyBorder(5, 12, 5, 12));
                return this;
            }
        };
        table.setDefaultRenderer(Object.class, cellRenderer);
        this.setEnabled(false);

        JTableHeader header = table.getTableHeader();
        header.setReorderingAllowed(false);
        header.setFont(Theme.DEFAULT_FONT.deriveFont(Font.BOLD, 12f));
        header.setForeground(Theme.TEXT_SECONDARY);
        header.setBackground(Theme.BACKGROUND);

        DefaultTableCellRenderer headerRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(
                    JTable table, Object value, boolean isSelected,
                    boolean hasFocus, int row, int column
            ) {
                super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createMatteBorder(0, 0, 2, 0, Theme.BORDER),
                        BorderFactory.createEmptyBorder(8, 12, 8, 12)
                ));
                setHorizontalAlignment(SwingConstants.LEFT);
                return this;
            }
        };
        header.setDefaultRenderer(headerRenderer);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createLineBorder(Theme.BORDER));
        scrollPane.getViewport().setBackground(Theme.PANEL_BG);

        add(scrollPane, BorderLayout.CENTER);
    }

    public void setStatistics(ArrayList<SimulationStatistics.Statistic> statistics) {
        clearStatistics();
        for (SimulationStatistics.Statistic stat : statistics) {
            tableModel.addRow(new Object[]{stat.label(), stat.value()});
        }
        JScrollBar bar = ((JScrollPane) getComponent(0)).getVerticalScrollBar();
        bar.setValue(bar.getMaximum());
    }

    public void clearStatistics() {
        tableModel.setRowCount(0);
    }

    public Object[][] getTableData() {
        int rowCount = tableModel.getRowCount();
        int colCount = tableModel.getColumnCount();
        Object[][] data = new Object[rowCount][colCount];

        for (int i = 0; i < rowCount; i++) {
            for (int j = 0; j < colCount; j++) {
                data[i][j] = tableModel.getValueAt(i, j);
            }
        }
        return data;
    }

    public void setEnabled(boolean enabled) {
        table.setEnabled(enabled);
    }
}
