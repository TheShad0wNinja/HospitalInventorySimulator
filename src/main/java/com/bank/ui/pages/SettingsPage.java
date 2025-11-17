package com.bank.ui.pages;

import com.bank.controllers.SettingsPageController;
import com.bank.models.EmployeeData;
import com.bank.ui.Theme;
import com.bank.ui.components.*;
import com.bank.utils.TextUtils;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class SettingsPage extends JPanel {
    private final Map<String, JTextField> generalConfigs = new HashMap<>();
    private static final String[][] generalConfigLabels = new String[][]{
            {"outdoorQueueSize", "Outdoor Queue Size"},
            {"cashCustomerProp", "Probability of Cash Customer"},
            {"numOutdoorTellers", "Number of Outdoor Tellers"},
            {"numIndoorTellers", "Number of Indoor Tellers"},
            {"numIndoorServiceEmp", "Number of Indoor Service Employees"},
    };

    private JPanel distributionsPanel;
    private final Map<String, ProbabilitiesTable> employeeTables = new HashMap<>();
    private ProbabilitiesTable timeBetweenArrivalsTable;

    private JButton saveBtn;
    private JButton resetBtn;

    public SettingsPage() {
        setLayout(new BorderLayout());
        setBackground(Theme.BACKGROUND);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBackground(Theme.BACKGROUND);

        JPanel header = prepareHeaderPanel();
        header.setAlignmentX(Component.LEFT_ALIGNMENT);
        content.add(header);
        content.add(Box.createVerticalStrut(40));

        JLabel generalConfigTitle = new JLabel("General Configurations");
        generalConfigTitle.setFont(Theme.TITLE_FONT);
        generalConfigTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        content.add(generalConfigTitle);
        content.add(Box.createVerticalStrut(5));
        content.add(prepareGeneralSettingsPanel());
        content.add(Box.createVerticalStrut(40));

        JLabel distributionsTitle = new JLabel("Distributions");
        distributionsTitle.setFont(Theme.TITLE_FONT);
        distributionsTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        content.add(distributionsTitle);
        content.add(Box.createVerticalStrut(5));
        content.add(prepareDistributionsPanel());

        add(content, BorderLayout.CENTER);
        new SettingsPageController(this);
    }

    private JPanel prepareHeaderPanel() {
        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.X_AXIS));
        headerPanel.setBackground(Theme.BACKGROUND);
        headerPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));

        JPanel titleSection = new JPanel();
        titleSection.setLayout(new BoxLayout(titleSection, BoxLayout.Y_AXIS));
        titleSection.setBackground(Theme.BACKGROUND);

        JLabel title = new JLabel("Simulation Settings");
        title.setFont(Theme.HEADER_FONT);
        title.setAlignmentX(Component.LEFT_ALIGNMENT);
        titleSection.add(title);
        titleSection.add(Box.createVerticalStrut(5));

        JLabel subtitle = new JLabel("Configure the parameters for the simulation");
        subtitle.setFont(Theme.TITLE_FONT);
        subtitle.setForeground(Theme.TEXT_SECONDARY);
        subtitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        titleSection.add(subtitle);

        headerPanel.add(titleSection);
        headerPanel.add(Box.createHorizontalGlue());

        resetBtn = new ThemeButton("Reset to Default", ThemeButton.Variant.DEFAULT);
        resetBtn.setFont(Theme.DEFAULT_FONT.deriveFont(Font.PLAIN, 18));
        headerPanel.add(resetBtn);
        headerPanel.add(Box.createRigidArea(new Dimension(10, 0)));

        saveBtn = new ThemeButton("Save", ThemeButton.Variant.PRIMARY);
        saveBtn.setFont(Theme.DEFAULT_FONT.deriveFont(Font.PLAIN, 18));
        headerPanel.add(saveBtn);

        return headerPanel;
    }

    private JPanel prepareGeneralSettingsPanel() {
        ThemePanel panel = new ThemePanel();
        panel.setLayout(new GridLayout(0, 2, 20, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);

        for (String[] configLabel : generalConfigLabels) {
            JPanel cell = new JPanel(new BorderLayout(5, 5));
            cell.setBackground(Theme.PANEL_BG);

            JLabel label = new JLabel(configLabel[1]);
            label.setFont(Theme.DEFAULT_FONT);
            label.setForeground(Theme.TEXT_PRIMARY);
            cell.add(label, BorderLayout.NORTH);

            ThemeTextField field = new ThemeTextField(25);
            generalConfigs.put(configLabel[0], field);
            cell.add(field, BorderLayout.CENTER);

            panel.add(cell);
        }

        return panel;
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
        distributionsPanel.revalidate();
        distributionsPanel.repaint();
    }

    public void setGeneralConfigField(String key, String value) {
        JTextField field = generalConfigs.get(key);
        if (field != null) {
            field.setText(value);
        }
    }

    public String getGeneralConfigField(String key) {
        JTextField field = generalConfigs.get(key);
        return field != null ? field.getText() : "";
    }

    public void clearTables() {
        employeeTables.clear();
        timeBetweenArrivalsTable = null;
        distributionsPanel.removeAll();
        distributionsPanel.revalidate();
        distributionsPanel.repaint();
    }

    public void setTimeBetweenArrivalsTable(Map<Integer, Double> probabilities) {
        timeBetweenArrivalsTable = new ProbabilitiesTable(probabilities);
        addDistributionTable("Time Between Arrivals", timeBetweenArrivalsTable);
    }

    public void addEmployeeTable(String employeeKey, EmployeeData employeeData) {
        if (!employeeTables.containsKey(employeeKey)) {
            String employeeLabel = TextUtils.capitalize(
                    String.join(" ",
                            Arrays.stream(employeeKey.split("_"))
                                    .map(str ->
                                            str.matches("-?\\d+(\\.\\d+)?")
                                                    ? String.valueOf(Integer.parseInt(str) + 1)
                                                    : str
                                    ).toList()));

            ProbabilitiesTable table = new ProbabilitiesTable(employeeData.getServiceTimeProbabilities());
            employeeTables.put(employeeKey, table);
            addDistributionTable(employeeLabel, table);
        }
    }

    public ProbabilitiesTable getTimeBetweenArrivalsTable() {
        return timeBetweenArrivalsTable;
    }

    public Map<String, ProbabilitiesTable> getAllEmployeeTables() {
        return new HashMap<>(employeeTables);
    }

    public void setSaveButtonAction(java.awt.event.ActionListener action) {
        for (var listener : saveBtn.getActionListeners()) {
            saveBtn.removeActionListener(listener);
        }
        saveBtn.addActionListener(action);
    }

    public void setResetButtonAction(java.awt.event.ActionListener action) {
        for (var listener : resetBtn.getActionListeners()) {
            resetBtn.removeActionListener(listener);
        }
        resetBtn.addActionListener(action);
    }
}