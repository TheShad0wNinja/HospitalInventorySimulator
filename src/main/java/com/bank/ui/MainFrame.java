package com.bank.ui;

import com.bank.ui.components.*;
import com.bank.ui.pages.*;

import javax.swing.*;
import java.awt.*;
import java.util.LinkedHashMap;
import java.util.Map;

public class MainFrame extends JFrame {

    private final JPanel contentPanel;
    private final CardLayout cardLayout;

    private final String defaultPage = "simulation";
    private final Map<String, JPanel> pages = new LinkedHashMap<>(){{
        put("simulation", new SimulationPage());
        put("history", new HistoryPage());
        put("settings", new SettingsPage());
    }};

    public MainFrame() {
        setTitle("BankQueue Sim");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 700);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        getContentPane().setBackground(Theme.BACKGROUND);

        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        contentPanel.setBackground(Theme.BACKGROUND);

        pages.forEach((k, v) -> {
            JScrollPane scrollPane = new JScrollPane(v);
            scrollPane.setBorder(BorderFactory.createEmptyBorder());
            scrollPane.getVerticalScrollBar().setUnitIncrement(16);
            contentPanel.add(scrollPane, k);
        });
        add(contentPanel, BorderLayout.CENTER);
        showPanel(defaultPage);

        MainSideNav sideNav = new MainSideNav(pages.keySet().toArray(String[]::new), defaultPage);
        sideNav.setOnSelect(this::showPanel);

        add(sideNav, BorderLayout.WEST);
    }

    private void showPanel(String name) {
        cardLayout.show(contentPanel, name);
        if (name.equals("history")) {
            HistoryPage historyPage = (HistoryPage) pages.get("history");
            historyPage.refresh();
        }
    }
}
