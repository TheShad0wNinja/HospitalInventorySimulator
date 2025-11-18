package com.bank.ui.pages;

import com.bank.controllers.SimulationPageController;
import com.bank.ui.Theme;
import com.bank.ui.components.*;
import jiconfont.icons.google_material_design_icons.GoogleMaterialDesignIcons;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;

public class SimulationPage extends JPanel {
    private JPanel paramsPanel;
    private JButton startSimulationButton;
    private JPanel resultsPanel;
    private JLabel resultsPanelLabel;

    public SimulationPage() {
        setLayout(new BorderLayout());
        setBackground(Theme.BACKGROUND);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBackground(Theme.BACKGROUND);

        JLabel header = new JLabel("Simulation");
        header.setFont(Theme.HEADER_FONT);
        content.add(header);
        content.add(Box.createVerticalStrut(40));

        JLabel simulationParametersLabel = new JLabel("Simulation Parameters");
        simulationParametersLabel.setFont(Theme.TITLE_FONT);
        simulationParametersLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        content.add(simulationParametersLabel);
        content.add(Box.createVerticalStrut(5));
        content.add(prepareSimulationParametersPanel());
        content.add(Box.createVerticalStrut(20));

        content.add(prepareSimulationStartButton());
        content.add(Box.createVerticalStrut(40));

        resultsPanelLabel = new JLabel("Simulation Results");
        resultsPanelLabel.setFont(Theme.TITLE_FONT);
        resultsPanelLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        resultsPanelLabel.setVisible(false);
        content.add(resultsPanelLabel);
        content.add(Box.createVerticalStrut(5));
        content.add(prepareSimulationResultsPanel());

        add(content, BorderLayout.CENTER);

        new SimulationPageController(this);
    }

    private JButton prepareSimulationStartButton() {
        startSimulationButton = new ThemeButton("Start Simulation", ThemeButton.Variant.PRIMARY, true, GoogleMaterialDesignIcons.PLAY_CIRCLE_OUTLINE);
        startSimulationButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        return startSimulationButton;
    }

    private JPanel prepareSimulationParametersPanel() {
        ThemePanel panel = new ThemePanel();
        panel.setLayout(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);

        paramsPanel = new JPanel(new GridLayout(0, 2, 10, 10));
        paramsPanel.setBackground(Theme.PANEL_BG);

        panel.add(paramsPanel, BorderLayout.CENTER);

        return panel;
    }

    private JPanel prepareSimulationResultsPanel() {
        resultsPanel = new ThemePanel();
        resultsPanel.setLayout(new BoxLayout(resultsPanel, BoxLayout.Y_AXIS));
        resultsPanel.setBorder(BorderFactory.createEmptyBorder(0, 20, 30, 20));
        resultsPanel.setVisible(false);
        resultsPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        return resultsPanel;
    }

    public void addDataTable(String title, JPanel tablePanel, int height) {
        JLabel label = new JLabel(title);
        label.setFont(Theme.TITLE_FONT);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);

        tablePanel.setPreferredSize(new Dimension(900, height));
        tablePanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, height));
        tablePanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        resultsPanel.add(Box.createVerticalStrut(30));
        resultsPanel.add(label);
        resultsPanel.add(Box.createVerticalStrut(10));
        resultsPanel.add(tablePanel);

        resultsPanel.revalidate();
        resultsPanel.repaint();
    }

    public void addChart(String title, JFreeChart chart) {
        JLabel label = new JLabel(title);
        label.setFont(Theme.TITLE_FONT);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);

        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new Dimension(900, 400));
        chartPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 400));
        chartPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        resultsPanel.add(Box.createVerticalStrut(30));
        resultsPanel.add(label);
        resultsPanel.add(Box.createVerticalStrut(10));
        resultsPanel.add(chartPanel);

        resultsPanel.revalidate();
        resultsPanel.repaint();
    }

    public Map<String, JTextField> addParameters(String[][] parameters) {
        paramsPanel.removeAll();
        Map<String, JTextField> map = new HashMap<>();

        for (String[] entry : parameters) {
            JPanel panel = new JPanel(new BorderLayout(5, 5));
            panel.setBackground(Theme.PANEL_BG);

            JLabel label = new JLabel(entry[1]);
            label.setFont(Theme.DEFAULT_FONT);
            label.setForeground(Theme.TEXT_PRIMARY);
            panel.add(label, BorderLayout.NORTH);

            ThemeTextField textField = new ThemeTextField(25);
            textField.setText(entry[2]);
            map.put(entry[0], textField);
            panel.add(textField, BorderLayout.CENTER);

            paramsPanel.add(panel);
        }

        paramsPanel.revalidate();
        paramsPanel.repaint();

        return map;
    }

    public void setStartButtonAction(ActionListener action) {
        for (var listener : startSimulationButton.getActionListeners()) {
            startSimulationButton.removeActionListener(listener);
        }
        startSimulationButton.addActionListener(action);
    }

    public void showResults() {
        resultsPanelLabel.setVisible(true);
        resultsPanel.setVisible(true);
        resultsPanel.revalidate();
        resultsPanel.repaint();
    }

    public void clearSimulationResults() {
        resultsPanel.removeAll();
    }
}