package com.bank.ui.components;

import com.bank.ui.Theme;
import jiconfont.IconCode;
import jiconfont.icons.google_material_design_icons.GoogleMaterialDesignIcons;
import jiconfont.swing.IconFontSwing;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class ThemeButton extends JButton {
    public enum Variant {
        PRIMARY,
        DEFAULT
    }

    private Variant variant = Variant.PRIMARY;
    private Color backgroundColor = Theme.PRIMARY;
    private Color hoverColor = Theme.PRIMARY_HOVER;
    private Color borderColor = Theme.BORDER;
    private boolean hasBorder = true;
    private boolean hovered = false;

    private IconCode iconCode;
    private int iconSize = 18;

    public ThemeButton(String text, Variant variant, boolean takeFullWidth) {
        super(text);
        this.variant = variant;
        initButton(takeFullWidth);
        applyVariantStyle();
    }

    public ThemeButton(String text, Variant variant, boolean takeFullWidth, IconCode iconCode) {
        super(text);
        this.variant = variant;
        this.iconCode = iconCode;
        initButton(takeFullWidth);
        applyVariantStyle();
        buildIcon();
    }

    public ThemeButton(String text, Variant variant, boolean takeFullWidth, IconCode iconCode, int iconSize) {
        super(text);
        this.variant = variant;
        this.iconCode = iconCode;
        this.iconSize = iconSize;
        initButton(takeFullWidth);
        applyVariantStyle();
        buildIcon();
    }

    public ThemeButton(String text, Variant variant) {
        this(text, variant, false);
    }

    public ThemeButton(String text) {
        this(text, Variant.DEFAULT, false);
    }

    private void initButton(boolean takeFullWidth) {
        IconFontSwing.register(GoogleMaterialDesignIcons.getIconFont());
        setFocusPainted(false);
        setBorderPainted(false);
        setForeground(Color.WHITE);
        setBackground(backgroundColor);
        setFont(Theme.DEFAULT_FONT);
        setCursor(new Cursor(Cursor.HAND_CURSOR));
        setMargin(new Insets(8, 12, 8, 12));
        setOpaque(false);
        setIconTextGap(8);

        if (takeFullWidth) {
            setMaximumSize(new Dimension(Integer.MAX_VALUE, getPreferredSize().height));
        }

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                hovered = true;
                repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                hovered = false;
                repaint();
            }
        });
    }

    private void applyVariantStyle() {
        switch (variant) {
            case PRIMARY:
                backgroundColor = Theme.PRIMARY;
                hoverColor = Theme.PRIMARY_HOVER;
                hasBorder = false;
                setForeground(Color.WHITE);
                break;
            default:
                backgroundColor = Theme.PANEL_BG;
                hoverColor = Theme.BORDER;
                hasBorder = true;
                setForeground(Color.BLACK);
        }
    }

    private void buildIcon() {
        if (iconCode != null) {
            Color iconColor = variant == Variant.PRIMARY ? Color.WHITE : Theme.TEXT_PRIMARY;
            Icon icon = IconFontSwing.buildIcon(iconCode, iconSize, iconColor);
            setIcon(icon);
        }
    }

    public void setIconCode(IconCode iconCode) {
        this.iconCode = iconCode;
        buildIcon();
    }

    public void setIconSize(int iconSize) {
        this.iconSize = iconSize;
        if (iconCode != null) {
            buildIcon();
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int arc = 12;
        int borderThickness = 2;

        g2.setColor(hovered ? hoverColor : backgroundColor);
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), arc, arc);

        if (hasBorder) {
            g2.setStroke(new BasicStroke(borderThickness));
            g2.setColor(borderColor);
            g2.drawRoundRect(
                    borderThickness / 2,
                    borderThickness / 2,
                    getWidth() - borderThickness,
                    getHeight() - borderThickness,
                    arc,
                    arc
            );
        }

        super.paintComponent(g2);
        g2.dispose();
    }

    public void setBackgroundColor(Color backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    public void setHoverColor(Color hoverColor) {
        this.hoverColor = hoverColor;
    }

    public void setBorderColor(Color borderColor) {
        this.borderColor = borderColor;
    }
}