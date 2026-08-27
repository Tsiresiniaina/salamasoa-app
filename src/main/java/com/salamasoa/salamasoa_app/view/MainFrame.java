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
    private MedecinPanel medecinPanel;

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

        /*
         * Conservé comme le panneau Patients : la barre de recherche doit
         * pouvoir lui transmettre le mot-clé saisi.
         */
        medecinPanel = new MedecinPanel(medecinService);
        pagesPanel.add(medecinPanel, PAGE_MEDECINS);

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

        if (topBarPanel == null) {
            return;
        }

        boolean rechercheDisponible = isSearchablePage(pageName);

        /*
         * En changeant de page, on repart d'une recherche vide : un filtre
         * hérité de la page précédente donnerait une liste incomplète sans
         * que l'utilisateur comprenne pourquoi.
         */
        if (!topBarPanel.getSearchText().isEmpty()) {
            topBarPanel.clearSearch();
        }

        topBarPanel.getSearchField().setEnabled(rechercheDisponible);

        // Le texte d'invite indique ce que la barre recherche ici.
        if (PAGE_MEDECINS.equals(pageName)) {
            topBarPanel.setPlaceholder(
                    "Rechercher un médecin : nom, code ou grade..."
            );

        } else if (PAGE_PATIENTS.equals(pageName)) {
            topBarPanel.setPlaceholder(
                    "Rechercher un patient : nom ou code..."
            );

        } else {
            topBarPanel.setPlaceholder(
                    "Recherche indisponible sur cette page"
            );
        }
    }

    /**
     * Indique si la page affichée gère la recherche.
     */
    private boolean isSearchablePage(String pageName) {
        return PAGE_PATIENTS.equals(pageName)
                || PAGE_MEDECINS.equals(pageName);
    }

    /**
     * Transmet le mot-clé saisi à la liste affichée.
     *
     * Appelée à chaque frappe par TopBarPanel. Le panneau destinataire
     * dépend de la page courante.
     */
    private void onSearch(String keyword) {
        if (PAGE_PATIENTS.equals(currentPage) && patientPanel != null) {
            patientPanel.applySearch(keyword);
            return;
        }

        if (PAGE_MEDECINS.equals(currentPage) && medecinPanel != null) {
            medecinPanel.applySearch(keyword);
        }
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