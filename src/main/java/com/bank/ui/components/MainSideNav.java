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
        IconFontSwing.register(GoogleMaterialDesignIcons.getIconFont());

        setLayout(new BorderLayout());
        setBackground(Theme.PANEL_BG);

        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBackground(Theme.PANEL_BG);
        content.setBorder(BorderFactory.createEmptyBorder(20, 16, 20, 16));

        JPanel logoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        logoPanel.setBackground(Theme.PANEL_BG);
        logoPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        logoPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));

        Icon logoIcon = IconFontSwing.buildIcon(GoogleMaterialDesignIcons.LOCAL_HOSPITAL, 42, Theme.PRIMARY);
        JLabel logoIconLabel = new JLabel(logoIcon);

        JPanel logoTextPanel = new JPanel();
        logoTextPanel.setLayout(new BoxLayout(logoTextPanel, BoxLayout.Y_AXIS));
        logoTextPanel.setBackground(Theme.PANEL_BG);

        JLabel logoTitleTop = new JLabel("Hospital Inventory");
        logoTitleTop.setFont(Theme.TITLE_FONT);
        logoTitleTop.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel logoTitleBottom = new JLabel("Simulator");
        logoTitleBottom.setFont(Theme.TITLE_FONT);
        logoTitleBottom.setAlignmentX(Component.LEFT_ALIGNMENT);

        logoTextPanel.add(logoTitleTop);
        logoTextPanel.add(logoTitleBottom);

        logoPanel.add(logoIconLabel);
        logoPanel.add(logoTextPanel);

        content.add(logoPanel);
        content.add(Box.createVerticalStrut(40));

        for (String page : pages) {
            ThemeToggledButton btn = createNavButton(page);

            if (page.equals(defaultPage)) {
                currentPage = btn;
                currentPage.toggle();
            }

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
        btn.setAlignmentX(Component.LEFT_ALIGNMENT);
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        btn.setMinimumSize(new Dimension(180, 44));

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
    }
}