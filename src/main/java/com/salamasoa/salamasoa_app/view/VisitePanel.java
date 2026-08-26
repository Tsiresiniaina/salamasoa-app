package com.salamasoa.salamasoa_app.view;

import com.salamasoa.salamasoa_app.model.Medecin;
import com.salamasoa.salamasoa_app.model.Patient;
import com.salamasoa.salamasoa_app.model.StatutVisite;
import com.salamasoa.salamasoa_app.model.Visite;
import com.salamasoa.salamasoa_app.service.MedecinService;
import com.salamasoa.salamasoa_app.service.PatientService;
import com.salamasoa.salamasoa_app.service.VisiteService;
import com.salamasoa.salamasoa_app.view.Form.VisiteFormDialog;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

public class VisitePanel extends JPanel {

    private static final Color PRIMARY_COLOR = new Color(199, 0, 61);
    private static final Color TEXT_COLOR = new Color(42, 42, 45);
    private static final Color SECONDARY_TEXT_COLOR = new Color(125, 125, 130);
    private static final Color BORDER_COLOR = new Color(235, 235, 237);

    private static final DateTimeFormatter TIME_FORMATTER =
            DateTimeFormatter.ofPattern("HH:mm");

    private final VisiteService visiteService;
    private final PatientService patientService;
    private final MedecinService medecinService;
    private JSpinner dateSpinner;
    private JTable visiteTable;
    private DefaultTableModel tableModel;

    private JLabel totalVisitsValueLabel;
    private JLabel waitingValueLabel;
    private JLabel completedValueLabel;

    // NOUVEAU : compteur pour ignorer les réponses périmées (voir plus bas)
    private int visiteRequestCounter = 0;

    private boolean loading = false;
    public VisitePanel(VisiteService _visiteService,PatientService _patientService,MedecinService _medecinService) {
        this.visiteService = _visiteService;
        this.medecinService = _medecinService;
        this.patientService = _patientService;
        setBackground(Color.WHITE);
        setBorder(new EmptyBorder(18, 26, 26, 26));
        setLayout(new BorderLayout(0, 16));

        add(createTopSection(), BorderLayout.NORTH);
        add(createVisitsSection(), BorderLayout.CENTER);

        loadVisitesForSelectedDate();
    }

    private JPanel createTopSection() {
        JPanel topSection = new JPanel();
        topSection.setBackground(Color.WHITE);
        topSection.setLayout(new BoxLayout(topSection, BoxLayout.Y_AXIS));

        topSection.add(createHeaderPanel());
        topSection.add(Box.createVerticalStrut(16));
        topSection.add(createStatisticsPanel());
        topSection.add(Box.createVerticalStrut(16));
        topSection.add(createDateFilterPanel());

        return topSection;
    }

    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(Color.WHITE);
        headerPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));

        JPanel titlePanel = new JPanel();
        titlePanel.setBackground(Color.WHITE);
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));

        JLabel titleLabel = new JLabel("Visite du jour");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 19));
        titleLabel.setForeground(TEXT_COLOR);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel subtitleLabel = new JLabel(
                "Planifiez et suivez les consultations de la journée"
        );
        subtitleLabel.setFont(new Font("SansSerif", Font.PLAIN, 11));
        subtitleLabel.setForeground(SECONDARY_TEXT_COLOR);
        subtitleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        titlePanel.add(titleLabel);
        titlePanel.add(Box.createVerticalStrut(4));
        titlePanel.add(subtitleLabel);

        JButton newVisitButton = new JButton("+  Nouveau");
        newVisitButton.setFont(new Font("SansSerif", Font.BOLD, 12));
        newVisitButton.setForeground(Color.WHITE);
        newVisitButton.setBackground(PRIMARY_COLOR);
        newVisitButton.setFocusPainted(false);
        newVisitButton.setBorderPainted(false);
        newVisitButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        newVisitButton.setBorder(new EmptyBorder(10, 16, 10, 16));

        newVisitButton.addActionListener(event ->
                openNewVisiteDialog()
        );

        headerPanel.add(titlePanel, BorderLayout.WEST);
        headerPanel.add(newVisitButton, BorderLayout.EAST);

        return headerPanel;
    }

    /**
     * Active ou désactive l'indicateur "chargement" : on change le curseur en
     * sablier pendant que le SwingWorker tourne, puis on le remet à la normale.
     */
    private void setLoading(boolean value) {
        this.loading = value;
        if (value) {
            setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        } else {
            setCursor(Cursor.getDefaultCursor());
        }
    }
    private void openNewVisiteDialog() {
        // 1) Charger les listes (patients et médecins actifs) via SwingWorker
        setLoading(true); // désactive l'écran pendant le chargement
        new SwingWorker<VisiteFormLists, Void>() {
            @Override
            protected VisiteFormLists doInBackground() {
                List<Patient> patients = patientService.getAllPatients().stream()
                        .filter(Patient::isActif).collect(Collectors.toList());
                List<Medecin> medecins = medecinService.getAllMedecins().stream()
                        .filter(Medecin::isActif).collect(Collectors.toList());
                return new VisiteFormLists(patients, medecins);
            }

            @Override
            protected void done() {
                setLoading(false);
                try {
                    VisiteFormLists lists = get();
                    VisiteFormDialog dialog = new VisiteFormDialog(
                            (Frame) SwingUtilities.getWindowAncestor(VisitePanel.this),
                            lists.patients(), lists.medecins());

                    dialog.setVisible(true); // bloque jusqu'à la fermeture

                    // 2) Si l'utilisateur a confirmé (saved == true), on enregistre
                    if (dialog.isSaved()) {
                        enregistrerVisite(dialog);
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(VisitePanel.this,
                            "Impossible de charger les médecins / patients.",
                            "Erreur", JOptionPane.ERROR_MESSAGE);
                }
            }
        }.execute();
    }

    /**
     * Enregistre la visite saisie dans le formulaire {@code dialog}, puis recharge
     * la liste des visites de la date sélectionnée.
     */
    private void enregistrerVisite(VisiteFormDialog dialog) {
        Patient patient = dialog.getSelectedPatient();
        Medecin medecin = dialog.getSelectedMedecin();
        LocalDateTime dateHeure = dialog.getDateHeureVisite();

        // Garde-fous : on ne tente l'enregistrement que si tout est renseigné
        if (patient == null || medecin == null || dateHeure == null) {
            JOptionPane.showMessageDialog(VisitePanel.this,
                    "Veuillez renseigner le patient, le médecin, la date et l'heure.",
                    "Formulaire incomplet", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // On crée une liste de tâches pour réutiliser la boucle de reprise au cas où
        // l'enregistrement échoue (on affiche le message puis on réouvre le formulaire).
        SwingWorker<Boolean, Void> worker = new SwingWorker<Boolean, Void>() {
            @Override
            protected Boolean doInBackground() {
                try {
                    visiteService.createVisite(
                            patient.getCodepat(), medecin.getCodemed(), dateHeure);
                    return true;
                } catch (IllegalArgumentException ex) {
                    return false; // erreur métier (conflit d'horaire, patient inactif...)
                } catch (Exception ex) {
                    throw new RuntimeException(ex);
                }
            }

            @Override
            protected void done() {
                try {
                    Boolean success = get();
                    if (Boolean.TRUE.equals(success)) {
                        JOptionPane.showMessageDialog(VisitePanel.this,
                                "Visite enregistrée avec succès.",
                                "Succès", JOptionPane.INFORMATION_MESSAGE);
                        // 3) Recharge la liste des visites pour la date courante
                        loadVisitesForSelectedDate();
                    } else {
                        // Ré-affiche le formulaire pour corriger sans perdre la saisie
                        JOptionPane.showMessageDialog(VisitePanel.this,
                                "Enregistrement impossible : le médecin est déjà "
                                        + "occupé à cet horaire, ou le patient est "
                                        + "inactif.",
                                "Enregistrement refusé", JOptionPane.WARNING_MESSAGE);
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(VisitePanel.this,
                            "Erreur lors de l'enregistrement de la visite : " +
                                    ex.getMessage(),
                            "Erreur", JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        worker.execute();
    }

    private record VisiteFormLists(
            List<Patient> patients,
            List<Medecin> medecins
    ) {
    }
    private JPanel createStatisticsPanel() {
        JPanel statisticsPanel = new JPanel(new GridLayout(1, 3, 12, 0));
        statisticsPanel.setBackground(Color.WHITE);
        statisticsPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 84));
        statisticsPanel.setPreferredSize(new Dimension(0, 84));
        statisticsPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        totalVisitsValueLabel = new JLabel("0");
        waitingValueLabel = new JLabel("0");
        completedValueLabel = new JLabel("0");

        statisticsPanel.add(createStatisticCard(
                "VISITES DU JOUR",
                totalVisitsValueLabel,
                "▣",
                new Color(255, 228, 235),
                PRIMARY_COLOR
        ));

        statisticsPanel.add(createStatisticCard(
                "SALLE D'ATTENTE",
                waitingValueLabel,
                "♙",
                new Color(243, 238, 241),
                new Color(115, 95, 105)
        ));

        statisticsPanel.add(createStatisticCard(
                "EFFECTUÉES",
                completedValueLabel,
                "◎",
                new Color(243, 238, 241),
                new Color(115, 95, 105)
        ));

        return statisticsPanel;
    }

    private JPanel createStatisticCard(
            String title,
            JLabel valueLabel,
            String icon,
            Color iconBackground,
            Color iconColor
    ) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(new LineBorder(BORDER_COLOR, 1, true));

        JPanel textPanel = new JPanel();
        textPanel.setBackground(Color.WHITE);
        textPanel.setBorder(new EmptyBorder(14, 14, 10, 0));
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 8));
        titleLabel.setForeground(SECONDARY_TEXT_COLOR);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        valueLabel.setFont(new Font("SansSerif", Font.BOLD, 23));
        valueLabel.setForeground(TEXT_COLOR);
        valueLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        textPanel.add(titleLabel);
        textPanel.add(Box.createVerticalStrut(12));
        textPanel.add(valueLabel);

        JPanel iconPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 13, 13));
        iconPanel.setBackground(Color.WHITE);

        JLabel iconLabel = new JLabel(icon, SwingConstants.CENTER);
        iconLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        iconLabel.setForeground(iconColor);
        iconLabel.setBackground(iconBackground);
        iconLabel.setOpaque(true);
        iconLabel.setBorder(new EmptyBorder(5, 6, 5, 6));

        iconPanel.add(iconLabel);

        card.add(textPanel, BorderLayout.CENTER);
        card.add(iconPanel, BorderLayout.EAST);

        return card;
    }

    private JPanel createDateFilterPanel() {
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        filterPanel.setBackground(Color.WHITE);
        filterPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel filterLabel = new JLabel("Date :");
        filterLabel.setFont(new Font("SansSerif", Font.BOLD, 11));
        filterLabel.setForeground(TEXT_COLOR);

        dateSpinner = new JSpinner(
                new SpinnerDateModel(
                        new Date(),
                        null,
                        null,
                        java.util.Calendar.DAY_OF_MONTH
                )
        );

        dateSpinner.setFont(new Font("SansSerif", Font.PLAIN, 11));
        dateSpinner.setPreferredSize(new Dimension(120, 28));
        dateSpinner.setBorder(new LineBorder(BORDER_COLOR, 1, true));

        JSpinner.DateEditor dateEditor =
                new JSpinner.DateEditor(dateSpinner, "dd/MM/yyyy");

        dateSpinner.setEditor(dateEditor);

        dateSpinner.addChangeListener(event ->
                loadVisitesForSelectedDate()
        );

        JButton todayButton = new JButton("Aujourd'hui");
        todayButton.setFont(new Font("SansSerif", Font.PLAIN, 10));
        todayButton.setForeground(PRIMARY_COLOR);
        todayButton.setBackground(Color.WHITE);
        todayButton.setFocusPainted(false);
        todayButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        todayButton.setBorder(new LineBorder(BORDER_COLOR, 1, true));
        todayButton.setPreferredSize(new Dimension(85, 28));

        todayButton.addActionListener(event ->
                dateSpinner.setValue(new Date())
        );

        filterPanel.add(filterLabel);
        filterPanel.add(dateSpinner);
        filterPanel.add(todayButton);

        return filterPanel;
    }

    private JPanel createVisitsSection() {
        JPanel visitsPanel = new JPanel(new BorderLayout());
        visitsPanel.setBackground(Color.WHITE);
        visitsPanel.setBorder(new LineBorder(BORDER_COLOR, 1, true));

        JLabel tableTitle = new JLabel("Liste des visites");
        tableTitle.setFont(new Font("SansSerif", Font.BOLD, 12));
        tableTitle.setForeground(TEXT_COLOR);
        tableTitle.setBorder(new EmptyBorder(14, 14, 9, 14));

        visitsPanel.add(tableTitle, BorderLayout.NORTH);
        visitsPanel.add(createVisitsTable(), BorderLayout.CENTER);

        return visitsPanel;
    }

    private JScrollPane createVisitsTable() {
        String[] columnNames = {
                "HEURE",
                "NOM DU PATIENT",
                "SEXE",
                "STATUT",
                "ACTION"
        };

        tableModel = new DefaultTableModel(columnNames, 0) {

            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        visiteTable = new JTable(tableModel);
        visiteTable.setRowHeight(42);
        visiteTable.setFont(new Font("SansSerif", Font.PLAIN, 9));
        visiteTable.setForeground(TEXT_COLOR);
        visiteTable.setBackground(Color.WHITE);
        visiteTable.setGridColor(BORDER_COLOR);
        visiteTable.setShowVerticalLines(false);
        visiteTable.setShowHorizontalLines(true);
        visiteTable.setRowSelectionAllowed(false);
        visiteTable.setFocusable(false);

        visiteTable.getColumnModel().getColumn(0).setPreferredWidth(75);
        visiteTable.getColumnModel().getColumn(1).setPreferredWidth(210);
        visiteTable.getColumnModel().getColumn(2).setPreferredWidth(105);
        visiteTable.getColumnModel().getColumn(3).setPreferredWidth(150);
        visiteTable.getColumnModel().getColumn(4).setPreferredWidth(55);

        visiteTable.setDefaultRenderer(Object.class, new VisiteCellRenderer());

        visiteTable.getColumnModel().getColumn(3)
                .setCellRenderer(new StatusCellRenderer());

        visiteTable.getColumnModel().getColumn(4)
                .setCellRenderer(new ActionCellRenderer());

        JTableHeader tableHeader = visiteTable.getTableHeader();
        tableHeader.setFont(new Font("SansSerif", Font.BOLD, 8));
        tableHeader.setForeground(new Color(115, 115, 120));
        tableHeader.setBackground(new Color(253, 251, 251));
        tableHeader.setPreferredSize(new Dimension(0, 30));
        tableHeader.setReorderingAllowed(false);

        JScrollPane scrollPane = new JScrollPane(visiteTable);
        scrollPane.setBorder(null);
        scrollPane.getViewport().setBackground(Color.WHITE);

        return scrollPane;
    }

    /**
     * Récupère depuis MySQL les visites de la date sélectionnée.
     */
    /**
     * Récupère depuis MySQL les visites de la date sélectionnée.
     */
    private void loadVisitesForSelectedDate() {
        LocalDate selectedDate = convertDateToLocalDate(
                (Date) dateSpinner.getValue()
        );

        // On mémorise l'identifiant de CETTE requête. Ce champ est lu et écrit
        // uniquement sur l'EDT (dateSpinner et done()), donc pas de concurrence.
        int requestId = ++visiteRequestCounter;

        new SwingWorker<List<Visite>, Void>() {

            @Override
            protected List<Visite> doInBackground() {
                return visiteService.getVisitesByDate(selectedDate);
            }

            @Override
            protected void done() {
                // Si une requête plus récente a été lancée depuis, cette réponse
                // est périmée : on l'ignore pour ne pas écraser la bonne liste.
                if (requestId != visiteRequestCounter) {
                    return;
                }

                try {
                    List<Visite> visites = get();

                    displayVisites(visites);
                    updateStatistics(visites);

                } catch (InterruptedException exception) {
                    Thread.currentThread().interrupt();

                } catch (ExecutionException exception) {
                    String message = exception.getCause() == null
                            ? exception.getMessage()
                            : exception.getCause().getMessage();

                    JOptionPane.showMessageDialog(
                            VisitePanel.this,
                            "Impossible de charger les visites :\n"
                                    + message,
                            "Erreur MySQL",
                            JOptionPane.ERROR_MESSAGE
                    );
                }
            }
        }.execute();
    }

    private void displayVisites(List<Visite> visites) {
        tableModel.setRowCount(0);

        for (Visite visite : visites) {
            tableModel.addRow(new Object[]{
                    visite.getDateheure().format(TIME_FORMATTER),
                    formatPatientName(visite),
                    formatSexe(visite),
                    visite.getStatut().getLibelle(),
                    "⋮"
            });
        }
    }

    private void updateStatistics(List<Visite> visites) {
        long waitingCount = visites.stream()
                .filter(visite ->
                        visite.getStatut() == StatutVisite.PLANIFIEE
                                || visite.getStatut()
                                == StatutVisite.EN_COURS
                )
                .count();

        long completedCount = visites.stream()
                .filter(visite ->
                        visite.getStatut() == StatutVisite.TERMINEE
                )
                .count();

        totalVisitsValueLabel.setText(
                String.valueOf(visites.size())
        );

        waitingValueLabel.setText(
                String.valueOf(waitingCount)
        );

        completedValueLabel.setText(
                String.valueOf(completedCount)
        );
    }
    private String formatPatientName(Visite visite) {
        String patientNom = visite.getPatient().getNom() == null
                ? ""
                : visite.getPatient().getNom();

        String patientPrenom = visite.getPatient().getPrenom() == null
                ? ""
                : visite.getPatient().getPrenom();

        String medecinNom = visite.getMedecin().getNom() == null
                ? ""
                : visite.getMedecin().getNom();

        String medecinPrenom = visite.getMedecin().getPrenom() == null
                ? ""
                : visite.getMedecin().getPrenom();

        String patientFullName =
                (patientNom + " " + patientPrenom).trim();

        String medecinFullName =
                (medecinNom + " " + medecinPrenom).trim();

        return "<html><b>"
                + patientFullName
                + "</b><br><span style='font-size:8px; color:#888888;'>"
                + medecinFullName
                + "</span></html>";
    }
    private String formatSexe(Visite visite) {
        char sexe = visite.getPatient().getSexe();

        if (sexe == 'H' || sexe == 'h') {
            return "Homme";
        }

        if (sexe == 'F' || sexe == 'f') {
            return "Femme";
        }

        return "Non renseigné";
    }

    private LocalDate convertDateToLocalDate(Date date) {
        Instant instant = date.toInstant();

        return instant
                .atZone(ZoneId.systemDefault())
                .toLocalDate();
    }

    private class VisiteCellRenderer extends DefaultTableCellRenderer {

        @Override
        public Component getTableCellRendererComponent(
                JTable table,
                Object value,
                boolean isSelected,
                boolean hasFocus,
                int row,
                int column
        ) {
            super.getTableCellRendererComponent(
                    table,
                    value,
                    false,
                    false,
                    row,
                    column
            );

            setBackground(Color.WHITE);
            setForeground(SECONDARY_TEXT_COLOR);
            setFont(new Font("SansSerif", Font.PLAIN, 9));
            setVerticalAlignment(SwingConstants.CENTER);
            setBorder(new EmptyBorder(3, 12, 3, 8));

            return this;
        }
    }

    private class StatusCellRenderer implements TableCellRenderer {

        private final JPanel panel;
        private final JLabel statusLabel;

        public StatusCellRenderer() {
            panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
            panel.setBackground(Color.WHITE);

            statusLabel = new JLabel();
            statusLabel.setFont(new Font("SansSerif", Font.BOLD, 8));
            statusLabel.setOpaque(true);
            statusLabel.setBorder(new EmptyBorder(3, 7, 3, 7));

            panel.add(statusLabel);
        }

        @Override
        public Component getTableCellRendererComponent(
                JTable table,
                Object value,
                boolean isSelected,
                boolean hasFocus,
                int row,
                int column
        ) {
            String status = value == null ? "" : value.toString();

            panel.setBackground(Color.WHITE);
            statusLabel.setText(status);

            if (status.equals("En cours")) {
                statusLabel.setForeground(new Color(0, 135, 120));
                statusLabel.setBackground(new Color(218, 247, 241));

            } else if (status.equals("Annulée")) {
                statusLabel.setForeground(new Color(200, 45, 45));
                statusLabel.setBackground(new Color(255, 234, 234));

            } else if (status.equals("Terminée")) {
                statusLabel.setForeground(new Color(130, 130, 135));
                statusLabel.setBackground(new Color(244, 242, 243));

            } else {
                statusLabel.setForeground(new Color(65, 100, 180));
                statusLabel.setBackground(new Color(228, 237, 255));
            }

            return panel;
        }
    }

    private class ActionCellRenderer extends DefaultTableCellRenderer {

        @Override
        public Component getTableCellRendererComponent(
                JTable table,
                Object value,
                boolean isSelected,
                boolean hasFocus,
                int row,
                int column
        ) {
            super.getTableCellRendererComponent(
                    table,
                    value,
                    false,
                    false,
                    row,
                    column
            );

            setText("⋮");
            setHorizontalAlignment(SwingConstants.CENTER);
            setFont(new Font("SansSerif", Font.BOLD, 14));
            setForeground(new Color(85, 85, 90));
            setBackground(Color.WHITE);
            setBorder(new EmptyBorder(0, 0, 0, 0));

            return this;
        }
    }
}