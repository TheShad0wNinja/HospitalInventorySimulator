package com.bank.ui.components;

import com.bank.ui.Theme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class ThemeTextField extends JTextField {
    private int cornerRadius = 10;

    public ThemeTextField(int columns) {
        super(columns);

        setFont(Theme.DEFAULT_FONT);
        setOpaque(false);
        setBorder(new EmptyBorder(8, 10, 8, 10));
        setBackground(Theme.PANEL_BG);
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(Theme.PANEL_BG);
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), cornerRadius, cornerRadius);
        g2.setColor(Theme.BORDER);
        g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, cornerRadius, cornerRadius);
        super.paintComponent(g);
        g2.dispose();
    }
}
