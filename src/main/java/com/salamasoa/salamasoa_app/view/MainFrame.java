package com.salamasoa.salamasoa_app.view;

import com.salamasoa.salamasoa_app.service.MedecinService;
import com.salamasoa.salamasoa_app.service.PatientService;
import com.salamasoa.salamasoa_app.service.VisiteService;
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

    /*
     * Conservés pour relier la barre de recherche à la liste des patients.
     */
    private TopBarPanel topBarPanel;
    private PatientPanel patientPanel;

    // Page actuellement affichée, pour savoir si la recherche s'applique.
    private String currentPage = PAGE_PATIENTS;

    //SERVICES
    private final PatientService patientService;
    private final MedecinService medecinService;
    private final VisiteService visiteService;

    public MainFrame(
            PatientService _patientService,
            MedecinService _medecinService,
            VisiteService _visiteService) {

        this.patientService = _patientService;
        this.medecinService = _medecinService;
        this.visiteService = _visiteService;
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

        // Barre de recherche : chaque frappe filtre la liste des patients.
        topBarPanel = new TopBarPanel(this::onSearch);
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
                new VisitePanel(
                        visiteService,
                        patientService,
                        medecinService
                ),
                PAGE_VISITES
        );

        /*
         * Le panneau est conservé dans un champ : la barre de recherche
         * doit pouvoir lui transmettre le mot-clé saisi.
         */
        patientPanel = new PatientPanel(patientService);
        pagesPanel.add(patientPanel, PAGE_PATIENTS);

        pagesPanel.add(
                new MedecinPanel(medecinService),
                PAGE_MEDECINS
        );

        rightPanel.add(pagesPanel, BorderLayout.CENTER);
        mainPanel.add(rightPanel, BorderLayout.CENTER);

        // La page Patients s'affiche au démarrage.
        showPage(PAGE_PATIENTS);
    }

    /**
     * Affiche la page demandée dans la zone de contenu.
     * <p>
     * Exemple :
     * showPage(MainFrame.PAGE_PATIENTS);
     */
    public void showPage(String pageName) {
        cardLayout.show(pagesPanel, pageName);
        currentPage = pageName;

        /*
         * La recherche ne concerne pour l'instant que les patients.
         * En quittant cette page, on vide le champ afin de ne pas laisser
         * un filtre actif invisible lors du retour.
         */
        if (!PAGE_PATIENTS.equals(pageName)
                && topBarPanel != null
                && !topBarPanel.getSearchText().isEmpty()) {

            topBarPanel.clearSearch();
        }

        // Le champ n'est utile que sur la page Patients.
        if (topBarPanel != null) {
            topBarPanel.getSearchField()
                    .setEnabled(PAGE_PATIENTS.equals(pageName));
        }
    }

    /**
     * Transmet le mot-clé saisi à la liste des patients.
     *
     * Appelée à chaque frappe par TopBarPanel.
     */
    private void onSearch(String keyword) {
        if (patientPanel == null) {
            return;
        }

        /*
         * Le filtrage n'a de sens que sur la page Patients. Le champ y est
         * de toute façon désactivé ailleurs, mais ce garde-fou évite un
         * filtrage fantôme si la page change pendant la saisie.
         */
        if (!PAGE_PATIENTS.equals(currentPage)) {
            return;
        }

        patientPanel.applySearch(keyword);
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