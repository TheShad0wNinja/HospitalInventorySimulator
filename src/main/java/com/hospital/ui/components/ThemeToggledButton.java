package com.hospital.ui.components;

import com.hospital.ui.Theme;
import jiconfont.IconCode;
import jiconfont.icons.google_material_design_icons.GoogleMaterialDesignIcons;
import jiconfont.swing.IconFontSwing;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class ThemeToggledButton extends JButton {
    private final Color onBackgroundColor = Theme.PRIMARY_LIGHT;
    private final Color onHoverColor = Theme.PRIMARY_HOVER;
    private final Color onTextColor = Theme.PRIMARY;
    private final Color offBackgroundColor = new Color(0,0,0,0);
    private final Color offHoverColor = Theme.PRIMARY_HOVER;
    private final Color offTextColor = Theme.TEXT_SECONDARY;
    private boolean hovered = false;
    private boolean toggled = false;

    private IconCode iconCode;
    private int iconSize = 18;
    private Icon onIcon;
    private Icon offIcon;
    private Icon offHoverIcon;

    public ThemeToggledButton(String text) {
        super(text);
        initButton();
    }

    public ThemeToggledButton(String text, IconCode iconCode) {
        super(text);
        this.iconCode = iconCode;
        initButton();
        buildIcons();
        updateIcon();
    }

    public ThemeToggledButton(String text, IconCode iconCode, int iconSize) {
        super(text);
        this.iconCode = iconCode;
        this.iconSize = iconSize;
        initButton();
        buildIcons();
        updateIcon();
    }

    private void initButton() {
        IconFontSwing.register(GoogleMaterialDesignIcons.getIconFont());
        setFocusPainted(false);
        setBorderPainted(false);
        setForeground(Theme.TEXT_PRIMARY);
        setBackground(offBackgroundColor);
        setFont(Theme.DEFAULT_FONT);
        setCursor(new Cursor(Cursor.HAND_CURSOR));
        setOpaque(false);
        setHorizontalAlignment(SwingConstants.LEFT);
        setIconTextGap(12);

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                hovered = true;
                updateIcon();
                repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                hovered = false;
                updateIcon();
                repaint();
            }
        });
    }

    private void buildIcons() {
        if (iconCode != null) {
            onIcon = IconFontSwing.buildIcon(iconCode, iconSize, onTextColor);
            offIcon = IconFontSwing.buildIcon(iconCode, iconSize, offTextColor);
            offHoverIcon = IconFontSwing.buildIcon(iconCode, iconSize, Color.WHITE);
        }
    }

    private void updateIcon() {
        if (iconCode == null) return;

        if (toggled) {
            setIcon(onIcon);
        } else {
            setIcon(hovered ? offHoverIcon : offIcon);
        }
    }

    public void setIconCode(IconCode iconCode) {
        this.iconCode = iconCode;
        buildIcons();
        updateIcon();
    }

    public void setIconSize(int iconSize) {
        this.iconSize = iconSize;
        if (iconCode != null) {
            buildIcons();
            updateIcon();
        }
    }

    public void toggle() {
        toggled = !toggled;
        updateIcon();
        repaint();
    }

    public void takeFullWidth(boolean fullWidth) {
        if  (fullWidth) {
            setMaximumSize(new Dimension(Integer.MAX_VALUE, getPreferredSize().height));
        } else {
            setMaximumSize(new Dimension(getPreferredSize().width, getPreferredSize().height));
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        if (toggled) {
            g2.setColor(onBackgroundColor);
            setForeground(onTextColor);
        } else {
            g2.setColor(hovered ? offHoverColor : offBackgroundColor);
            setForeground(hovered ? Color.white : offTextColor);
        }

        g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
        super.paintComponent(g2);
        g2.dispose();
    }
}