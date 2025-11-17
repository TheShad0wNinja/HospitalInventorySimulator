package com.bank.ui.pages;

import com.bank.controllers.HistoryPageController;
import com.bank.models.SimulationHistoryRecord;
import com.bank.ui.Theme;
import com.bank.ui.components.ThemeButton;
import com.bank.ui.components.ThemePanel;

import javax.swing.*;
import java.awt.*;

public class HistoryPage extends JPanel {
    private JPanel historyListPanel;
    private JLabel emptyStateLabel;
    private HistoryPageController controller;

    public HistoryPage() {
        setLayout(new BorderLayout());
        setBackground(Theme.BACKGROUND);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JPanel header = new JPanel();
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
        header.setBackground(Theme.BACKGROUND);

        JLabel title = new JLabel("History");
        title.setFont(Theme.HEADER_FONT);
        title.setAlignmentX(Component.LEFT_ALIGNMENT);
        header.add(title);

        header.add(Box.createVerticalStrut(40));

        add(header, BorderLayout.NORTH);

        historyListPanel = new JPanel();
        historyListPanel.setLayout(new BoxLayout(historyListPanel, BoxLayout.Y_AXIS));
        historyListPanel.setBackground(Theme.BACKGROUND);

        emptyStateLabel = new JLabel("No past simulations found.");
        emptyStateLabel.setFont(Theme.TITLE_FONT);
        emptyStateLabel.setForeground(Theme.TEXT_SECONDARY);
        emptyStateLabel.setHorizontalAlignment(SwingConstants.CENTER);
        emptyStateLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        add(historyListPanel, BorderLayout.CENTER);

        controller = new HistoryPageController(this);
    }

    public void refresh() {
        controller.loadHistory();
    }

    public void clearHistory() {
        historyListPanel.removeAll();
        historyListPanel.revalidate();
        historyListPanel.repaint();
    }

    public void showEmptyState() {
        clearHistory();
        historyListPanel.add(Box.createVerticalGlue());
        historyListPanel.add(emptyStateLabel);
        historyListPanel.add(Box.createVerticalGlue());
        historyListPanel.revalidate();
        historyListPanel.repaint();
    }

    public void addHistoryItem(String label, SimulationHistoryRecord record) {
        ThemePanel itemPanel = new ThemePanel();
        itemPanel.setLayout(new BorderLayout());
        itemPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Theme.BORDER),
                BorderFactory.createEmptyBorder(15, 20, 15, 20)
        ));
        itemPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        itemPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));

        JLabel itemLabel = new JLabel(label);
        itemLabel.setFont(Theme.DEFAULT_FONT.deriveFont(Font.PLAIN, 16f));
        itemLabel.setForeground(Theme.TEXT_PRIMARY);
        itemPanel.add(itemLabel, BorderLayout.CENTER);

        JPanel buttonPanel = prepareButtonRow(record);

        itemPanel.add(buttonPanel, BorderLayout.EAST);

        historyListPanel.add(itemPanel);
        historyListPanel.add(Box.createVerticalStrut(10));
        historyListPanel.revalidate();
        historyListPanel.repaint();
    }

    private JPanel prepareButtonRow(SimulationHistoryRecord record) {
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonPanel.setBackground(Theme.PANEL_BG);

        ThemeButton viewButton = new ThemeButton("View Details", ThemeButton.Variant.PRIMARY);
        viewButton.addActionListener(e -> controller.showHistoryDetail(record));

        ThemeButton deleteButton = new ThemeButton("Delete", ThemeButton.Variant.DEFAULT);
        deleteButton.addActionListener(e -> controller.deleteHistory(record));

        buttonPanel.add(viewButton);
        buttonPanel.add(deleteButton);
        return buttonPanel;
    }
}
