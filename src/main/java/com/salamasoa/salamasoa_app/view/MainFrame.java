package com.salamasoa.salamasoa_app.view;

import org.springframework.stereotype.Component;

import javax.swing.*;
import java.awt.*;

@Component
public class MainFrame extends JFrame {

    public MainFrame() {
        setTitle("Gestion de Clinique - Salamasoa");
        setSize(1100, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Centrer l'application sur l'écran

        // Conteneur principal
        JPanel mainPanel = new JPanel(new BorderLayout());

        JLabel titleLabel = new JLabel("Bienvenue dans l'application Salamasoa", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));

        mainPanel.add(titleLabel, BorderLayout.CENTER);
        add(mainPanel);
    }
}