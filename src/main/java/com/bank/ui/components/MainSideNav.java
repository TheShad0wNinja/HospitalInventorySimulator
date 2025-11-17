package com.bank.ui.components;

import com.bank.ui.Theme;
import com.bank.utils.TextUtils;
import jiconfont.icons.google_material_design_icons.GoogleMaterialDesignIcons;
import jiconfont.swing.IconFontSwing;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class MainSideNav extends ThemePanel {
    private Consumer<String> onSelect;
    private ThemeToggledButton currentPage;

    private static final Map<String, GoogleMaterialDesignIcons> PAGE_ICONS = new HashMap<>();
    static {
        PAGE_ICONS.put("simulation", GoogleMaterialDesignIcons.PLAY_CIRCLE_OUTLINE);
        PAGE_ICONS.put("settings", GoogleMaterialDesignIcons.SETTINGS);
        PAGE_ICONS.put("history", GoogleMaterialDesignIcons.DASHBOARD);
    }

    public void setOnSelect(Consumer<String> onSelect) {
        this.onSelect = onSelect;
    }

    public MainSideNav(String[] pages, String defaultPage) {

        setLayout(new BorderLayout());
        setBackground(Theme.PANEL_BG);
        setPreferredSize(new Dimension(250, 0));

        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBackground(Theme.PANEL_BG);
        content.setBorder(BorderFactory.createMatteBorder(20, 16, 20, 16, Color.GREEN));

        JPanel logoPanel = new JPanel();
        logoPanel.setBackground(Theme.PANEL_BG);
        logoPanel.setLayout(new BoxLayout(logoPanel, BoxLayout.X_AXIS));
        logoPanel.setBorder(BorderFactory.createMatteBorder(5, 5, 5, 5, Theme.PRIMARY));
        Icon icon = IconFontSwing.buildIcon(GoogleMaterialDesignIcons.LOCAL_HOSPITAL, 20, Theme.TEXT_PRIMARY);
        JLabel logoIcon = new JLabel(icon);
        JLabel logo = new JLabel("Bank Queue Sim");
        logo.setFont(Theme.TITLE_FONT);
        logo.setAlignmentX(Component.LEFT_ALIGNMENT);

        logoPanel.add(logoIcon);
        logoPanel.add(logo);

        content.add(logoPanel);
        content.add(Box.createVerticalStrut(40));

        for (String page : pages) {
            ThemeToggledButton btn = createNavButton(page);

            if (page.equals(defaultPage)) {
                currentPage = btn;
                currentPage.toggle();
            }

            btn.setAlignmentX(Component.LEFT_ALIGNMENT);
            btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
            btn.setPreferredSize(new Dimension(218, 44));

            content.add(btn);
            content.add(Box.createVerticalStrut(8));
        }

        content.add(Box.createVerticalGlue());

        add(content, BorderLayout.CENTER);
    }

    private ThemeToggledButton createNavButton(String page) {
        String label = TextUtils.capitalize(page);

        GoogleMaterialDesignIcons icon = PAGE_ICONS.get(page.toLowerCase());

        ThemeToggledButton btn = icon != null
                ? new ThemeToggledButton(label, icon, 18)
                : new ThemeToggledButton(label);

        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setBorder(BorderFactory.createEmptyBorder(10, 12, 10, 12));
//        btn.setBorder(BorderFactory.createMatteBorder(5, 5, 5, 5, Color.RED));


        btn.addActionListener(e -> {
            if (btn.equals(currentPage)) {
                return;
            }

            if (currentPage != null) {
                currentPage.toggle();
            }

            currentPage = btn;
            currentPage.toggle();

            if (onSelect != null) {
                onSelect.accept(page);
            }
        });

        return btn;
    }}