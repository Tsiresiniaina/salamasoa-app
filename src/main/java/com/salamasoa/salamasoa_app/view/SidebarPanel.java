package com.salamasoa.salamasoa_app.view;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.function.Consumer;

public class SidebarPanel extends JPanel {

    private static final Color SIDEBAR_COLOR = new Color(250, 247, 248);
    private static final Color PRIMARY_COLOR = new Color(199, 0, 61);
    private static final Color ACTIVE_COLOR = new Color(250, 221, 230);
    private static final Color TEXT_COLOR = new Color(70, 70, 70);

    /*
     * Cette variable contient l'action à effectuer lorsqu'un bouton
     * de navigation est cliqué.
     *
     * Exemple :
     * navigationHandler.accept(MainFrame.PAGE_PATIENTS);
     */
    private final Consumer<String> navigationHandler;

    private JButton activeButton;

    public SidebarPanel(Consumer<String> navigationHandler) {
        this.navigationHandler = navigationHandler;

        setPreferredSize(new Dimension(235, 0));
        setBackground(SIDEBAR_COLOR);
        setBorder(new EmptyBorder(20, 14, 18, 14));
        setLayout(new BorderLayout());

        add(createLogoPanel(), BorderLayout.NORTH);
        add(createNavigationPanel(), BorderLayout.CENTER);
        add(createBottomPanel(), BorderLayout.SOUTH);
    }

    private JPanel createLogoPanel() {
        JPanel logoPanel = new JPanel();
        logoPanel.setBackground(SIDEBAR_COLOR);
        logoPanel.setLayout(new BoxLayout(logoPanel, BoxLayout.Y_AXIS));
        logoPanel.setBorder(new EmptyBorder(0, 2, 28, 0));

        JLabel nameLabel = new JLabel("● SalamaSoa");
        nameLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
        nameLabel.setForeground(PRIMARY_COLOR);
        nameLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel subtitleLabel = new JLabel("Centre médical");
        subtitleLabel.setFont(new Font("SansSerif", Font.PLAIN, 9));
        subtitleLabel.setForeground(new Color(110, 110, 110));
        subtitleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        logoPanel.add(nameLabel);
        logoPanel.add(Box.createVerticalStrut(2));
        logoPanel.add(subtitleLabel);

        return logoPanel;
    }

    private JPanel createNavigationPanel() {
        JPanel navigationPanel = new JPanel();
        navigationPanel.setBackground(SIDEBAR_COLOR);
        navigationPanel.setLayout(new BoxLayout(navigationPanel, BoxLayout.Y_AXIS));

        JButton dashboardButton = createNavigationButton(
                "▦   Tableau de bord",
                MainFrame.PAGE_DASHBOARD
        );

        JButton todayVisitButton = createNavigationButton(
                "□   Visite du jour",
                MainFrame.PAGE_VISITES
        );

        JButton patientsButton = createNavigationButton(
                "♟   Patients",
                MainFrame.PAGE_PATIENTS
        );

        JButton doctorsButton = createNavigationButton(
                "□   Médecin",
                MainFrame.PAGE_MEDECINS
        );

        navigationPanel.add(dashboardButton);
        navigationPanel.add(Box.createVerticalStrut(6));

        navigationPanel.add(todayVisitButton);
        navigationPanel.add(Box.createVerticalStrut(6));

        navigationPanel.add(patientsButton);
        navigationPanel.add(Box.createVerticalStrut(6));

        navigationPanel.add(doctorsButton);

        // Patients est la page affichée au démarrage.
        setActiveButton(patientsButton);

        return navigationPanel;
    }

    private JPanel createBottomPanel() {
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBackground(SIDEBAR_COLOR);
        bottomPanel.setBorder(new EmptyBorder(15, 0, 0, 0));

        JSeparator separator = new JSeparator();
        separator.setForeground(new Color(235, 230, 232));
        bottomPanel.add(separator, BorderLayout.NORTH);

        JButton logoutButton = new JButton("⇥  Déconnexion");
        logoutButton.setFont(new Font("SansSerif", Font.PLAIN, 12));
        logoutButton.setForeground(TEXT_COLOR);
        logoutButton.setBackground(SIDEBAR_COLOR);
        logoutButton.setFocusPainted(false);
        logoutButton.setBorderPainted(false);
        logoutButton.setContentAreaFilled(false);
        logoutButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        logoutButton.setHorizontalAlignment(SwingConstants.LEFT);
        logoutButton.setBorder(new EmptyBorder(16, 8, 5, 8));

        /*
         * Plus tard, ce bouton servira à fermer la session
         * et revenir à une page de connexion.
         */
        logoutButton.addActionListener(event ->
                JOptionPane.showMessageDialog(
                        this,
                        "La fonctionnalité de déconnexion sera ajoutée plus tard.",
                        "Déconnexion",
                        JOptionPane.INFORMATION_MESSAGE
                )
        );

        bottomPanel.add(logoutButton, BorderLayout.CENTER);

        return bottomPanel;
    }

    private JButton createNavigationButton(String text, String pageName) {
        JButton button = new JButton(text);

        button.setFont(new Font("SansSerif", Font.PLAIN, 12));
        button.setForeground(TEXT_COLOR);
        button.setBackground(SIDEBAR_COLOR);

        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);

        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setBorder(new EmptyBorder(11, 10, 11, 10));

        button.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        button.setAlignmentX(Component.LEFT_ALIGNMENT);

        button.addActionListener(event -> {
            // 1. Mettre le bouton cliqué en rose.
            setActiveButton(button);

            // 2. Afficher la page correspondante dans MainFrame.
            navigationHandler.accept(pageName);
        });

        return button;
    }

    private void setActiveButton(JButton selectedButton) {
        if (activeButton != null) {
            activeButton.setBackground(SIDEBAR_COLOR);
            activeButton.setForeground(TEXT_COLOR);
            activeButton.setContentAreaFilled(false);
        }

        selectedButton.setBackground(ACTIVE_COLOR);
        selectedButton.setForeground(PRIMARY_COLOR);
        selectedButton.setContentAreaFilled(true);

        activeButton = selectedButton;
    }
}