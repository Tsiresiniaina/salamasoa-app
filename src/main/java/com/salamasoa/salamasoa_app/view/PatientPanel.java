package com.salamasoa.salamasoa_app.view;

import com.salamasoa.salamasoa_app.model.Patient;
import com.salamasoa.salamasoa_app.service.PatientService;
import com.salamasoa.salamasoa_app.view.Form.PatientFormDialog;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class PatientPanel extends JPanel {

    private static final Color PRIMARY_COLOR = new Color(199, 0, 61);
    private static final Color ACTIVE_ROW_COLOR = new Color(255, 216, 220);
    private static final Color TEXT_COLOR = new Color(45, 45, 48);
    private static final Color SECONDARY_TEXT_COLOR = new Color(120, 120, 125);
    private static final Color BORDER_COLOR = new Color(238, 238, 240);

    private final PatientService patientService;

    private JTable patientTable;
    private DefaultTableModel tableModel;
    private JComboBox<String> statusComboBox;

    /*
     * Cette liste contient les patients réellement récupérés depuis MySQL.
     * Elle est utilisée pour appliquer le filtre Actif / Inactif.
     */
    private List<Patient> allPatients = new ArrayList<>();

    public PatientPanel(PatientService patientService) {
        this.patientService = patientService;

        setBackground(Color.WHITE);
        setBorder(new EmptyBorder(26, 28, 28, 28));
        setLayout(new BorderLayout(0, 18));

        add(createTopSection(), BorderLayout.NORTH);
        add(createTableSection(), BorderLayout.CENTER);

        // Charge les patients MySQL sans bloquer l'interface Swing.
        loadPatients();
    }

    private JPanel createTopSection() {
        JPanel topSection = new JPanel();
        topSection.setBackground(Color.WHITE);
        topSection.setLayout(new BoxLayout(topSection, BoxLayout.Y_AXIS));

        topSection.add(createTitleAndButtonPanel());
        topSection.add(Box.createVerticalStrut(16));
        topSection.add(createFiltersPanel());

        return topSection;
    }

    private JPanel createTitleAndButtonPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(Color.WHITE);
        headerPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));

        JLabel titleLabel = new JLabel("Prenez soin de vos patients");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
        titleLabel.setForeground(TEXT_COLOR);

        JButton newPatientButton = new JButton("+  Nouveau");
        newPatientButton.setFont(new Font("SansSerif", Font.BOLD, 12));
        newPatientButton.setForeground(Color.WHITE);
        newPatientButton.setBackground(PRIMARY_COLOR);
        newPatientButton.setFocusPainted(false);
        newPatientButton.setBorderPainted(false);
        newPatientButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        newPatientButton.setBorder(new EmptyBorder(10, 16, 10, 16));

        newPatientButton.addActionListener(event -> {
            Window window = SwingUtilities.getWindowAncestor(this);

            PatientFormDialog dialog = new PatientFormDialog(window);
            dialog.setVisible(true);

            /*
             * Si l'utilisateur a cliqué sur Annuler ou ×,
             * aucun patient ne doit être créé.
             */
            if (!dialog.isSaved()) {
                return;
            }

            savePatientFromDialog(dialog);
        });

        headerPanel.add(titleLabel, BorderLayout.WEST);
        headerPanel.add(newPatientButton, BorderLayout.EAST);

        return headerPanel;
    }

    private void savePatientFromDialog(PatientFormDialog dialog) {
        new SwingWorker<Patient, Void>() {

            @Override
            protected Patient doInBackground() {
                return patientService.createPatient(
                        dialog.getFullName(),
                        dialog.getSexe(),
                        dialog.getAddress()
                );
            }

            @Override
            protected void done() {
                try {
                    Patient savedPatient = get();

                    JOptionPane.showMessageDialog(
                            PatientPanel.this,
                            "Patient enregistré avec succès.\n\n"
                                    + "Code patient : "
                                    + savedPatient.getCodepat(),
                            "Enregistrement réussi",
                            JOptionPane.INFORMATION_MESSAGE
                    );

                    /*
                     * Recharge les données depuis MySQL afin que le
                     * nouveau patient apparaisse immédiatement dans JTable.
                     */
                    loadPatients();

                } catch (InterruptedException exception) {
                    Thread.currentThread().interrupt();

                } catch (ExecutionException exception) {
                    String message = exception.getCause() == null
                            ? exception.getMessage()
                            : exception.getCause().getMessage();

                    JOptionPane.showMessageDialog(
                            PatientPanel.this,
                            "Impossible d'enregistrer le patient :\n"
                                    + message,
                            "Erreur d'enregistrement",
                            JOptionPane.ERROR_MESSAGE
                    );
                }
            }
        }.execute();
    }
    private JPanel createFiltersPanel() {
        JPanel filtersPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        filtersPanel.setBackground(Color.WHITE);
        filtersPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        String[] statuses = {
                "Tous les statuts",
                "Actif",
                "Inactif"
        };

        statusComboBox = new JComboBox<>(statuses);
        styleComboBox(statusComboBox);

        /*
         * Ce filtre travaille sur les patients déjà chargés dans allPatients.
         */
        statusComboBox.addActionListener(event ->
                displayPatients(allPatients)
        );

        filtersPanel.add(statusComboBox);

        return filtersPanel;
    }

    private void styleComboBox(JComboBox<String> comboBox) {
        comboBox.setFont(new Font("SansSerif", Font.PLAIN, 10));
        comboBox.setForeground(TEXT_COLOR);
        comboBox.setBackground(Color.WHITE);
        comboBox.setPreferredSize(new Dimension(130, 28));
        comboBox.setBorder(new LineBorder(BORDER_COLOR, 1, true));
        comboBox.setCursor(new Cursor(Cursor.HAND_CURSOR));
        comboBox.setFocusable(false);
    }

    private JScrollPane createTableSection() {
        String[] columnNames = {
                "NOM DU PATIENT",
                "ID",
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

        patientTable = new JTable(tableModel);
        patientTable.setRowHeight(58);
        patientTable.setFont(new Font("SansSerif", Font.PLAIN, 11));
        patientTable.setForeground(TEXT_COLOR);
        patientTable.setBackground(Color.WHITE);
        patientTable.setGridColor(BORDER_COLOR);
        patientTable.setShowVerticalLines(false);
        patientTable.setShowHorizontalLines(true);
        patientTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        patientTable.setRowSelectionAllowed(true);
        patientTable.setColumnSelectionAllowed(false);

        patientTable.getColumnModel().getColumn(0).setPreferredWidth(250);
        patientTable.getColumnModel().getColumn(1).setPreferredWidth(140);
        patientTable.getColumnModel().getColumn(2).setPreferredWidth(110);
        patientTable.getColumnModel().getColumn(3).setPreferredWidth(130);
        patientTable.getColumnModel().getColumn(4).setPreferredWidth(80);

        patientTable.setDefaultRenderer(Object.class, new PatientCellRenderer());

        patientTable.getColumnModel().getColumn(3)
                .setCellRenderer(new StatusCellRenderer());

        patientTable.getColumnModel().getColumn(4)
                .setCellRenderer(new ActionCellRenderer());

        JTableHeader tableHeader = patientTable.getTableHeader();
        tableHeader.setFont(new Font("SansSerif", Font.BOLD, 9));
        tableHeader.setForeground(new Color(100, 100, 105));
        tableHeader.setBackground(Color.WHITE);
        tableHeader.setPreferredSize(new Dimension(0, 33));
        tableHeader.setReorderingAllowed(false);

        JScrollPane scrollPane = new JScrollPane(patientTable);
        scrollPane.setBorder(new LineBorder(BORDER_COLOR, 1, true));
        scrollPane.getViewport().setBackground(Color.WHITE);
        scrollPane.setBackground(Color.WHITE);

        return scrollPane;
    }

    /**
     * Récupère tous les patients en arrière-plan.
     * MySQL ne bloque donc pas le thread graphique Swing.
     */
    public void loadPatients() {
        new SwingWorker<List<Patient>, Void>() {

            @Override
            protected List<Patient> doInBackground() {
                return patientService.getAllPatients();
            }

            @Override
            protected void done() {
                try {
                    allPatients = get();
                    displayPatients(allPatients);

                } catch (InterruptedException exception) {
                    Thread.currentThread().interrupt();

                } catch (ExecutionException exception) {
                    JOptionPane.showMessageDialog(
                            PatientPanel.this,
                            "Impossible de charger les patients : "
                                    + exception.getCause().getMessage(),
                            "Erreur MySQL",
                            JOptionPane.ERROR_MESSAGE
                    );
                }
            }
        }.execute();
    }

    /**
     * Met à jour les lignes du JTable selon le filtre choisi.
     */
    private void displayPatients(List<Patient> patients) {
        tableModel.setRowCount(0);

        String selectedStatus =
                statusComboBox.getSelectedItem().toString();

        for (Patient patient : patients) {

            boolean mustBeDisplayed =
                    selectedStatus.equals("Tous les statuts")
                            || (selectedStatus.equals("Actif")
                            && patient.isActif())
                            || (selectedStatus.equals("Inactif")
                            && !patient.isActif());

            if (!mustBeDisplayed) {
                continue;
            }

            tableModel.addRow(new Object[]{
                    formatPatientName(patient),
                    patient.getCodepat(),
                    formatSexe(patient.getSexe()),
                    patient.isActif() ? "Actif" : "Inactif",
                    "⋮"
            });
        }

        // Sélectionne visuellement la première ligne si elle existe.
        if (tableModel.getRowCount() > 0) {
            patientTable.setRowSelectionInterval(0, 0);
        }
    }

    private String formatPatientName(Patient patient) {
        String nom = patient.getNom() == null ? "" : patient.getNom();
        String prenom = patient.getPrenom() == null ? "" : patient.getPrenom();

        String fullName = (nom + " " + prenom).trim();

        return "<html><b>"
                + fullName
                + "</b><br><span style='font-size:9px; color:#777777;'>"
                + patient.getAdresse()
                + "</span></html>";
    }

    private String formatSexe(char sexe) {
        if (sexe == 'H' || sexe == 'h') {
            return "Homme";
        }

        if (sexe == 'F' || sexe == 'f') {
            return "Femme";
        }

        return "Non renseigné";
    }

    private class PatientCellRenderer extends DefaultTableCellRenderer {

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
                    table, value, isSelected, hasFocus, row, column
            );

            setFont(new Font("SansSerif", Font.PLAIN, 11));
            setBorder(new EmptyBorder(6, 12, 6, 12));
            setVerticalAlignment(SwingConstants.CENTER);

            if (isSelected) {
                setBackground(ACTIVE_ROW_COLOR);
            } else {
                setBackground(Color.WHITE);
            }

            if (column == 0) {
                setForeground(TEXT_COLOR);
            } else {
                setForeground(SECONDARY_TEXT_COLOR);
            }

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
            statusLabel.setFont(new Font("SansSerif", Font.BOLD, 9));
            statusLabel.setOpaque(true);
            statusLabel.setBorder(new EmptyBorder(4, 7, 4, 7));

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

            panel.setBackground(isSelected ? ACTIVE_ROW_COLOR : Color.WHITE);
            statusLabel.setText(status);

            if (status.equals("Actif")) {
                statusLabel.setForeground(new Color(0, 135, 120));
                statusLabel.setBackground(new Color(218, 247, 241));
            } else {
                statusLabel.setForeground(new Color(135, 100, 100));
                statusLabel.setBackground(new Color(245, 235, 235));
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
                    table, value, isSelected, hasFocus, row, column
            );

            setText("⋮");
            setHorizontalAlignment(SwingConstants.CENTER);
            setFont(new Font("SansSerif", Font.BOLD, 16));
            setForeground(new Color(100, 100, 105));
            setBackground(isSelected ? ACTIVE_ROW_COLOR : Color.WHITE);
            setBorder(new EmptyBorder(0, 0, 0, 0));

            return this;
        }
    }
}