package com.salamasoa.salamasoa_app.view;

import org.springframework.stereotype.Component;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

@Component
public class MainFrame extends JFrame {

    public static final String PAGE_DASHBOARD = "DASHBOARD";
    public static final String PAGE_VISITES = "VISITES";
    public static final String PAGE_PATIENTS = "PATIENTS";
    public static final String PAGE_MEDECINS = "MEDECINS";

    private final CardLayout cardLayout;
    private final JPanel pagesPanel;

    public MainFrame() {
        setTitle("SalamaSoa - Centre médical");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1250, 760);
        setMinimumSize(new Dimension(1000, 650));
        setLocationRelativeTo(null);

        // Panneau général : menu à gauche, contenu à droite.
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(Color.WHITE);
        setContentPane(mainPanel);

        // Menu latéral déjà créé.
        SidebarPanel sidebarPanel = new SidebarPanel(this::showPage);
        mainPanel.add(sidebarPanel, BorderLayout.WEST);

        // Toute la zone de droite.
        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.setBackground(Color.WHITE);

        // Barre de recherche.
        TopBarPanel topBarPanel = new TopBarPanel();
        rightPanel.add(topBarPanel, BorderLayout.NORTH);

        /*
         * CardLayout permet d'avoir plusieurs pages dans une même zone,
         * mais une seule page est visible à la fois.
         */
        cardLayout = new CardLayout();
        pagesPanel = new JPanel(cardLayout);
        pagesPanel.setBackground(Color.WHITE);

        // Pages provisoires : elles seront remplacées progressivement.
        pagesPanel.add(
                new DashboardPanel(),
                PAGE_DASHBOARD
        );

        pagesPanel.add(
                createPlaceholderPage(
                        "Visite du jour",
                        "La page des visites du jour sera créée prochainement."
                ),
                PAGE_VISITES
        );

        // Page Patients réelle, déjà créée.
        pagesPanel.add(new PatientPanel(), PAGE_PATIENTS);

        pagesPanel.add(
                createPlaceholderPage(
                        "Médecins",
                        "La page de gestion des médecins sera créée prochainement."
                ),
                PAGE_MEDECINS
        );

        rightPanel.add(pagesPanel, BorderLayout.CENTER);
        mainPanel.add(rightPanel, BorderLayout.CENTER);

        // La page Patients s'affiche au démarrage.
        showPage(PAGE_PATIENTS);
    }

    /**
     * Affiche la page demandée dans la zone de contenu.
     *
     * Exemple :
     * showPage(MainFrame.PAGE_PATIENTS);
     */
    public void showPage(String pageName) {
        cardLayout.show(pagesPanel, pageName);
    }

    /**
     * Crée une page temporaire, utilisée tant que la vraie page
     * n'a pas encore été développée.
     */
    private JPanel createPlaceholderPage(String title, String message) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(35, 35, 35, 35));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 22));
        titleLabel.setForeground(new Color(45, 45, 48));

        JLabel messageLabel = new JLabel(message);
        messageLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        messageLabel.setForeground(new Color(125, 125, 130));

        JPanel textPanel = new JPanel();
        textPanel.setBackground(Color.WHITE);
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));

        titleLabel.setAlignmentX(java.awt.Component.LEFT_ALIGNMENT);
        messageLabel.setAlignmentX(java.awt.Component.LEFT_ALIGNMENT);

        textPanel.add(titleLabel);
        textPanel.add(Box.createVerticalStrut(10));
        textPanel.add(messageLabel);

        panel.add(textPanel, BorderLayout.NORTH);

        return panel;
    }
}