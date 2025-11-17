package com.bank.ui.components;

import com.bank.ui.Theme;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.util.Map;

public class ProbabilitiesTable extends JPanel {
    private final JTable table;
    private DefaultTableModel tableModel;
    private JPanel buttonPanel;

    public ProbabilitiesTable(Map<Integer, Double> probabilities) {
        this();

        for(var entry : probabilities.entrySet()) {
            tableModel.addRow(new Object[]{entry.getKey(), entry.getValue(), ""});
        }
        updateCumulativeProbabilities();
    }

    public ProbabilitiesTable() {
        setLayout(new BorderLayout(0, 10));
        setBackground(Theme.PANEL_BG);

        setPreferredSize(new Dimension(100, 250));
        setMinimumSize(new Dimension(100, 250));

        String[] columnNames = {"Value", "Probability", "Cumulative Probability"};

        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column != 2;
            }

            @Override
            public void setValueAt(Object aValue, int row, int column) {
                if (column == 1) {
                    try {
                        double value = Double.parseDouble(aValue.toString());
                        if (value > 1.0) value = 1.0;
                        if (value < 0.0) value = 0.0;
                        super.setValueAt(value, row, column);
                        updateCumulativeProbabilities();
                        return;
                    } catch (NumberFormatException e) {
                        return;
                    }
                } else if (column == 0) {
                    try {
                        double value = Double.parseDouble(aValue.toString());
                        super.setValueAt((int) Math.floor(value), row, column);
                        updateCumulativeProbabilities();
                        return;
                    } catch (NumberFormatException e) {
                        return;
                    }

                }
                super.setValueAt(aValue, row, column);
            }
        };

        table = new JTable(tableModel);


        table.setRowHeight(40);
        table.setFont(Theme.DEFAULT_FONT.deriveFont(14f));
        table.setForeground(Theme.TEXT_PRIMARY);
        table.setGridColor(Theme.BORDER);
        table.setIntercellSpacing(new Dimension(1, 0));
        table.setShowGrid(true);
        table.setShowVerticalLines(true);
        table.setShowHorizontalLines(false);
        table.setFillsViewportHeight(true);


        table.setSelectionBackground(Theme.PRIMARY_LIGHT);
        table.setSelectionForeground(Theme.PRIMARY);


        DefaultTableCellRenderer cellRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (isSelected) {
                    setBackground(table.getSelectionBackground());
                    setForeground(table.getSelectionForeground());
                } else {
                    setBackground(row % 2 == 0 ? Theme.PANEL_BG : Theme.BACKGROUND);

                    if (column == 2) {
                        setForeground(Theme.TEXT_SECONDARY);
                    } else {
                        setForeground(Theme.TEXT_PRIMARY);
                    }
                }
                setBorder(BorderFactory.createEmptyBorder(5, 12, 5, 12));
                return this;
            }
        };
        table.setDefaultRenderer(Object.class, cellRenderer);


        JTableHeader header = table.getTableHeader();
        header.setReorderingAllowed(false);
        header.setResizingAllowed(false);
        header.setFont(Theme.DEFAULT_FONT.deriveFont(Font.BOLD, 12f));
        header.setForeground(Theme.TEXT_SECONDARY);
        header.setBackground(Theme.BACKGROUND);
        header.setOpaque(false);


        DefaultTableCellRenderer headerRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createMatteBorder(0, 0, 2, 0, Theme.BORDER),
                        BorderFactory.createEmptyBorder(10, 12, 10, 12)
                ));
                setHorizontalAlignment(SwingConstants.LEFT);
                setBackground(Theme.BACKGROUND);
                return this;
            }
        };
        header.setDefaultRenderer(headerRenderer);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createLineBorder(Theme.BORDER));
        scrollPane.getViewport().setBackground(Theme.PANEL_BG);

        add(scrollPane, BorderLayout.CENTER);

        buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonPanel.setBackground(Theme.BACKGROUND);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));

        ThemeButton addButton = new ThemeButton("Add Row", ThemeButton.Variant.PRIMARY);
        addButton.addActionListener(e -> addRow());

        ThemeButton removeButton = new ThemeButton("Remove Selected Row", ThemeButton.Variant.PRIMARY);
        removeButton.setBackgroundColor(Color.RED);
        removeButton.addActionListener(e -> removeSelectedRow());

        buttonPanel.add(addButton);
        buttonPanel.add(removeButton);

        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void addRow() {
        tableModel.addRow(new Object[]{"0", "0.0", "0.0"});
        updateCumulativeProbabilities();
    }

    private void removeSelectedRow() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow >= 0) {
            tableModel.removeRow(selectedRow);
            updateCumulativeProbabilities();
        } else {
            JOptionPane.showMessageDialog(this, "Please select a row to remove.", "No Row Selected", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void updateCumulativeProbabilities() {
        double cumulative = 0.0;
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            Object probValue = tableModel.getValueAt(i, 1);
            double prob = 0.0;
            try {
                prob = Double.parseDouble(probValue.toString());
            } catch (NumberFormatException e) {
                prob = 0.0;
            }
            cumulative += prob;
            tableModel.setValueAt(String.format("%.4f", cumulative), i, 2);
        }
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
        if (buttonPanel != null) {
            buttonPanel.setVisible(enabled);
        }
        DefaultTableModel model = (DefaultTableModel) table.getModel();
        String[] columnNames = new String[model.getColumnCount()];
        for (int i = 0; i < model.getColumnCount(); i++) {
            columnNames[i] = model.getColumnName(i);
        }
        DefaultTableModel newModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return enabled && column != 2;
            }
        };
        for (int i = 0; i < model.getRowCount(); i++) {
            Object[] rowData = new Object[model.getColumnCount()];
            for (int j = 0; j < model.getColumnCount(); j++) {
                rowData[j] = model.getValueAt(i, j);
            }
            newModel.addRow(rowData);
        }
        table.setModel(newModel);
        this.tableModel = newModel;
        if (enabled) {
            updateCumulativeProbabilities();
        }
    }
}