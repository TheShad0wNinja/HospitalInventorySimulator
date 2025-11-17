package com.bank.ui.pages;

import com.bank.controllers.SimulationPageController;
import com.bank.ui.Theme;
import com.bank.ui.components.*;
import jiconfont.icons.google_material_design_icons.GoogleMaterialDesignIcons;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class SimulationPage extends JPanel {
    private JPanel simulationParamsPanel;
    private JButton startSimulationButton;
    private JPanel simulationResultsPanel;
    private JLabel simulationResultsLabel;

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

        simulationResultsLabel = new JLabel("Simulation Results");
        simulationResultsLabel.setFont(Theme.TITLE_FONT);
        simulationResultsLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        simulationResultsLabel.setVisible(false);
        content.add(simulationResultsLabel);
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

        simulationParamsPanel = new JPanel(new GridLayout(0, 2, 10, 10));
        simulationParamsPanel.setBackground(Theme.PANEL_BG);

        panel.add(simulationParamsPanel, BorderLayout.CENTER);

        return panel;
    }

    private JPanel prepareSimulationResultsPanel() {
        simulationResultsPanel = new ThemePanel();
        simulationResultsPanel.setLayout(new BoxLayout(simulationResultsPanel, BoxLayout.Y_AXIS));
        simulationResultsPanel.setBorder(BorderFactory.createEmptyBorder(0, 20, 30, 20));
        simulationResultsPanel.setVisible(false);
        simulationResultsPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        return simulationResultsPanel;
    }

    public void addDataTable(String title, JPanel tablePanel, int height) {
        JLabel label = new JLabel(title);
        label.setFont(Theme.TITLE_FONT);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);

        tablePanel.setPreferredSize(new Dimension(900, height));
        tablePanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, height));
        tablePanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        simulationResultsPanel.add(Box.createVerticalStrut(30));
        simulationResultsPanel.add(label);
        simulationResultsPanel.add(Box.createVerticalStrut(10));
        simulationResultsPanel.add(tablePanel);

        simulationResultsPanel.revalidate();
        simulationResultsPanel.repaint();
    }

    public void addChart(String title, JFreeChart chart) {
        JLabel label = new JLabel(title);
        label.setFont(Theme.TITLE_FONT);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);

        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new Dimension(900, 400));
        chartPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 400));
        chartPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        simulationResultsPanel.add(Box.createVerticalStrut(30));
        simulationResultsPanel.add(label);
        simulationResultsPanel.add(Box.createVerticalStrut(10));
        simulationResultsPanel.add(chartPanel);

        simulationResultsPanel.revalidate();
        simulationResultsPanel.repaint();
    }

    public void clearSimulationResults() {
        simulationResultsPanel.removeAll();
    }

    public Map<String, JTextField> addSimulationParameter(Map<String, String> parameters) {
        simulationParamsPanel.removeAll();
        Map<String, JTextField> map = new HashMap<>();

        for (var entry : parameters.entrySet()) {
            JPanel panel = new JPanel(new BorderLayout(5, 5));
            panel.setBackground(Theme.PANEL_BG);

            JLabel label = new JLabel(entry.getValue());
            label.setFont(Theme.DEFAULT_FONT);
            label.setForeground(Theme.TEXT_PRIMARY);
            panel.add(label, BorderLayout.NORTH);

            ThemeTextField textField = new ThemeTextField(25);
            textField.setText("10");
            map.put(entry.getKey(), textField);
            panel.add(textField, BorderLayout.CENTER);

            simulationParamsPanel.add(panel);
        }

        simulationParamsPanel.revalidate();
        simulationParamsPanel.repaint();
        return map;
    }

    public void setStartButtonAction(java.awt.event.ActionListener action) {
        for (var listener : startSimulationButton.getActionListeners()) {
            startSimulationButton.removeActionListener(listener);
        }
        startSimulationButton.addActionListener(action);
    }

    public void showResults() {
        simulationResultsLabel.setVisible(true);
        simulationResultsPanel.setVisible(true);
        simulationResultsPanel.revalidate();
        simulationResultsPanel.repaint();
    }
}