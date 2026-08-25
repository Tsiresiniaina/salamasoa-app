package com.salamasoa.salamasoa_app.view;

import org.springframework.stereotype.Component;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

@Component
public class MainFrame extends JFrame {

    private final JPanel contentPanel;

    public MainFrame() {
        // Configuration de la fenêtre
        setTitle("SalamaSoa - Centre médical");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1250, 760);
        setMinimumSize(new Dimension(1000, 650));
        setLocationRelativeTo(null);

        // Conteneur principal
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(Color.WHITE);
        setContentPane(mainPanel);

        // Emplacement temporaire du futur menu latéral
        SidebarPanel sidebarPanel = new SidebarPanel();
        mainPanel.add(sidebarPanel, BorderLayout.WEST);

        // Emplacement temporaire du contenu principal
        contentPanel = createContentPlaceholder();
        mainPanel.add(contentPanel, BorderLayout.CENTER);
    }

    private JPanel createSidebarPlaceholder() {
        JPanel sidebar = new JPanel();
        sidebar.setPreferredSize(new Dimension(235, 0));
        sidebar.setBackground(new Color(250, 247, 248));
        sidebar.setBorder(new EmptyBorder(25, 18, 25, 18));
        sidebar.setLayout(new BorderLayout());

        JLabel logoLabel = new JLabel(
                "<html><span style='color:#C7003D; font-size:18px;'><b>● SalamaSoa</b></span>"
                        + "<br><span style='font-size:9px; color:#666666;'>Centre médical</span></html>"
        );

        sidebar.add(logoLabel, BorderLayout.NORTH);

        JLabel temporaryLabel = new JLabel(
                "<html><div style='text-align:center; color:#777777;'>"
                        + "Le menu latéral<br>sera ajouté ensuite."
                        + "</div></html>",
                SwingConstants.CENTER
        );

        sidebar.add(temporaryLabel, BorderLayout.CENTER);

        return sidebar;
    }

    private JPanel createContentPlaceholder() {
        JPanel content = new JPanel(new BorderLayout());
        content.setBackground(Color.WHITE);
        content.setBorder(new EmptyBorder(35, 35, 35, 35));

        JLabel titleLabel = new JLabel("Bienvenue dans SalamaSoa");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 24));
        titleLabel.setForeground(new Color(35, 35, 35));

        JLabel informationLabel = new JLabel(
                "La structure principale de l'application est prête."
        );
        informationLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        informationLabel.setForeground(new Color(110, 110, 110));

        JPanel textPanel = new JPanel();
        textPanel.setBackground(Color.WHITE);
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));

        titleLabel.setAlignmentX(java.awt.Component.LEFT_ALIGNMENT);
        informationLabel.setAlignmentX(java.awt.Component.LEFT_ALIGNMENT);

        textPanel.add(titleLabel);
        textPanel.add(Box.createVerticalStrut(10));
        textPanel.add(informationLabel);

        content.add(textPanel, BorderLayout.NORTH);

        return content;
    }

    public JPanel getContentPanel() {
        return contentPanel;
    }
}

