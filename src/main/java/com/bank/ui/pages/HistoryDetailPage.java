package com.bank.ui.pages;

import com.bank.controllers.HistoryDetailPageController;
import com.bank.models.SimulationHistoryRecord;
import com.bank.ui.Theme;
import com.bank.ui.components.*;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;

import javax.swing.*;
import java.awt.*;

public class HistoryDetailPage extends JPanel {
    private JLabel subtitleLabel;
    private JPanel generalConfigPanel;
    private JPanel distributionsPanel;
    private JPanel resultsPanel;

    public HistoryDetailPage(SimulationHistoryRecord record) {
        setLayout(new BorderLayout());
        setBackground(Theme.BACKGROUND);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JPanel header = prepareHeaderPanel();
        add(header, BorderLayout.NORTH);

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setBackground(Theme.PRIMARY_LIGHT);
        tabbedPane.setForeground(Theme.PRIMARY);
        tabbedPane.setFont(Theme.DEFAULT_FONT);

        tabbedPane.addTab("Configuration", prepareConfigTab());
        tabbedPane.addTab("Results", prepareResultsTab());

        add(tabbedPane, BorderLayout.CENTER);

        new HistoryDetailPageController(this, record);
    }

    private JPanel prepareHeaderPanel() {
        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        headerPanel.setBackground(Theme.BACKGROUND);

        JLabel title = new JLabel("Simulation Details");
        title.setFont(Theme.HEADER_FONT);
        title.setAlignmentX(Component.LEFT_ALIGNMENT);
        headerPanel.add(title);

        headerPanel.add(Box.createVerticalStrut(5));

        subtitleLabel = new JLabel();
        subtitleLabel.setFont(Theme.DEFAULT_FONT);
        subtitleLabel.setForeground(Theme.TEXT_SECONDARY);
        subtitleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        headerPanel.add(subtitleLabel);

        headerPanel.add(Box.createVerticalStrut(40));

        return headerPanel;
    }

    public void setSubtitleText(String text) {
        subtitleLabel.setText(text);
    }

    private JScrollPane prepareConfigTab() {
        JPanel configPanel = new JPanel();
        configPanel.setLayout(new BoxLayout(configPanel, BoxLayout.Y_AXIS));
        configPanel.setBackground(Theme.BACKGROUND);
        configPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel generalConfigTitle = new JLabel("General Configurations");
        generalConfigTitle.setFont(Theme.TITLE_FONT);
        generalConfigTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        configPanel.add(generalConfigTitle);
        configPanel.add(Box.createVerticalStrut(5));
        configPanel.add(prepareGeneralConfigPanel());
        configPanel.add(Box.createVerticalStrut(40));

        JLabel distributionsTitle = new JLabel("Distributions");
        distributionsTitle.setFont(Theme.TITLE_FONT);
        distributionsTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        configPanel.add(distributionsTitle);
        configPanel.add(Box.createVerticalStrut(5));
        configPanel.add(prepareDistributionsPanel());

        JScrollPane scrollPane = new JScrollPane(configPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(Theme.BACKGROUND);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        return scrollPane;
    }

    private JPanel prepareGeneralConfigPanel() {
        generalConfigPanel = new ThemePanel();
        generalConfigPanel.setLayout(new GridLayout(0, 2, 20, 10));
        generalConfigPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        generalConfigPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        return generalConfigPanel;
    }

    public void setGeneralConfigPanelCells(String[][] configs) {
        generalConfigPanel.removeAll();

        for (String[] configLabel : configs) {
            JPanel cell = new JPanel(new BorderLayout(5, 5));
            cell.setBackground(Theme.PANEL_BG);

            JLabel label = new JLabel(configLabel[0]);
            label.setFont(Theme.DEFAULT_FONT);
            label.setForeground(Theme.TEXT_PRIMARY);
            cell.add(label, BorderLayout.NORTH);

            ThemeTextField field = new ThemeTextField(25);
            field.setText(configLabel[1]);
            field.setEditable(false);
            cell.add(field, BorderLayout.CENTER);

            generalConfigPanel.add(cell);
        }
    }

    private JPanel prepareDistributionsPanel() {
        ThemePanel panel = new ThemePanel();
        panel.setLayout(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);

        distributionsPanel = new JPanel();
        distributionsPanel.setLayout(new BoxLayout(distributionsPanel, BoxLayout.Y_AXIS));
        distributionsPanel.setBackground(Theme.PANEL_BG);
        panel.add(distributionsPanel, BorderLayout.CENTER);
        return panel;
    }

    public void addDistributionTable(String title, ProbabilitiesTable table) {
        JPanel wrapper = new JPanel();
        wrapper.setLayout(new BoxLayout(wrapper, BoxLayout.Y_AXIS));
        wrapper.setBackground(Theme.PANEL_BG);
        wrapper.setAlignmentX(Component.LEFT_ALIGNMENT);
        wrapper.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));

        JLabel label = new JLabel(title);
        label.setFont(Theme.DEFAULT_FONT.deriveFont(Font.BOLD, 16f));
        label.setForeground(Theme.TEXT_PRIMARY);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        wrapper.add(label);
        wrapper.add(Box.createVerticalStrut(10));

        table.setAlignmentX(Component.LEFT_ALIGNMENT);
        Dimension tablePref = table.getPreferredSize();
        table.setMaximumSize(new Dimension(Integer.MAX_VALUE, Math.min(tablePref.height, 300)));
        wrapper.add(table);

        wrapper.setMaximumSize(new Dimension(Integer.MAX_VALUE, wrapper.getPreferredSize().height));
        distributionsPanel.add(wrapper);
    }

    private JScrollPane prepareResultsTab() {
        resultsPanel = new JPanel();
        resultsPanel.setLayout(new BoxLayout(resultsPanel, BoxLayout.Y_AXIS));
        resultsPanel.setBackground(Theme.BACKGROUND);
        resultsPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JScrollPane scrollPane = new JScrollPane(resultsPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(Theme.BACKGROUND);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        return scrollPane;
    }

    public void addDataTable(String title, JPanel tablePanel, int height) {
        ThemePanel panel = new ThemePanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel label = new JLabel(title);
        label.setFont(Theme.TITLE_FONT);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);

        panel.add(label);
        panel.add(Box.createVerticalStrut(10));

        tablePanel.setPreferredSize(new Dimension(900, height));
        tablePanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, height));
        tablePanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        panel.add(tablePanel);
        resultsPanel.add(panel);
        resultsPanel.add(Box.createVerticalStrut(30));
    }

    public void addChart(String title, JFreeChart chart) {
        ThemePanel panel = new ThemePanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel label = new JLabel(title);
        label.setFont(Theme.TITLE_FONT);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);

        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new Dimension(900, 400));
        chartPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 400));
        chartPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        panel.add(label);
        panel.add(Box.createVerticalStrut(10));
        panel.add(chartPanel);
        resultsPanel.add(panel);
        resultsPanel.add(Box.createVerticalStrut(30));
    }
}

