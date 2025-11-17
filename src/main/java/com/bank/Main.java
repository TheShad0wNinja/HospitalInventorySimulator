package com.bank;

import com.bank.ui.MainFrame;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new MainFrame();
            frame.setVisible(true);
        });
    }
}