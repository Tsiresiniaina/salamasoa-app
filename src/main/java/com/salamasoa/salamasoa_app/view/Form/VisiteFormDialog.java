package com.salamasoa.salamasoa_app.view.Form;

import com.salamasoa.salamasoa_app.model.Medecin;
import com.salamasoa.salamasoa_app.model.Patient;
import com.salamasoa.salamasoa_app.view.GlassPane.BackgroundOverlay;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class VisiteFormDialog extends JDialog {

    private static final Color PRIMARY_COLOR = new Color(199, 0, 61);
    private static final Color TEXT_COLOR = new Color(42, 42, 45);
    private static final Color LABEL_COLOR = new Color(105, 75, 80);
    private static final Color INPUT_BACKGROUND = new Color(248, 248, 250);
    private static final Color PLACEHOLDER_COLOR = new Color(150, 150, 155);

    private final List<Patient> allPatients;

    private final JComboBox<Patient> patientComboBox;
    private final JComboBox<Medecin> doctorComboBox;

    private final JSpinner dateSpinner;
    private final JSpinner timeSpinner;

    private boolean saved = false;
    private boolean filteringPatients = false;

    private final BackgroundOverlay.OverlayHandle overlayHandle;

    /*
     * Constructeur temporaire conservé pour ne pas casser
     * les anciens appels au formulaire.
     */
    public VisiteFormDialog(Window owner) {
        this(owner, List.of(), List.of());
    }

    public VisiteFormDialog(
            Window owner,
            List<Patient> patients,
            List<Medecin> medecins
    ) {
        super(owner, "Enregistrement de visite", ModalityType.APPLICATION_MODAL);

        this.allPatients = new ArrayList<>(patients);

        overlayHandle = BackgroundOverlay.show(owner);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent event) {
                if (overlayHandle != null) {
                    overlayHandle.close();
                }
            }
        });

        setUndecorated(true);
        setSize(430, 395);
        setResizable(false);
        setLocationRelativeTo(owner);

        RoundedPanel mainPanel = new RoundedPanel(14);
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setLayout(new BorderLayout());

        mainPanel.add(createHeaderPanel(), BorderLayout.NORTH);

        patientComboBox = createPatientComboBox(patients);
        doctorComboBox = createDoctorComboBox(medecins);

        dateSpinner = createDateSpinner();
        timeSpinner = createTimeSpinner();

        mainPanel.add(createFormPanel(), BorderLayout.CENTER);
        mainPanel.add(createFooterPanel(), BorderLayout.SOUTH);

        setContentPane(mainPanel);
    }

    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(Color.WHITE);
        headerPanel.setBorder(new EmptyBorder(17, 18, 15, 14));

        JLabel titleLabel = new JLabel("Enregistrement de visite");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        titleLabel.setForeground(TEXT_COLOR);

        JButton closeButton = new JButton("×");
        closeButton.setFont(new Font("SansSerif", Font.PLAIN, 24));
        closeButton.setForeground(new Color(95, 65, 70));
        closeButton.setFocusPainted(false);
        closeButton.setBorderPainted(false);
        closeButton.setContentAreaFilled(false);
        closeButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        closeButton.addActionListener(event -> dispose());

        headerPanel.add(titleLabel, BorderLayout.WEST);
        headerPanel.add(closeButton, BorderLayout.EAST);

        return headerPanel;
    }

    private JPanel createFormPanel() {
        JPanel formPanel = new JPanel();
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(new EmptyBorder(18, 18, 18, 18));
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));

        formPanel.add(createLabel("NOM COMPLET DU PATIENT"));
        formPanel.add(Box.createVerticalStrut(7));
        formPanel.add(patientComboBox);

        formPanel.add(Box.createVerticalStrut(15));
        formPanel.add(createLabel("DOCTEUR RESPONSABLE"));
        formPanel.add(Box.createVerticalStrut(7));
        formPanel.add(doctorComboBox);

        formPanel.add(Box.createVerticalStrut(15));
        formPanel.add(createDateAndTimePanel());

        return formPanel;
    }

    private JPanel createDateAndTimePanel() {
        JPanel rowPanel = new JPanel(new GridLayout(1, 2, 18, 0));
        rowPanel.setBackground(Color.WHITE);
        rowPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        rowPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 62));

        JPanel datePanel = new JPanel();
        datePanel.setBackground(Color.WHITE);
        datePanel.setLayout(new BoxLayout(datePanel, BoxLayout.Y_AXIS));

        datePanel.add(createLabel("DATE DE LA VISITE"));
        datePanel.add(Box.createVerticalStrut(6));
        datePanel.add(dateSpinner);

        JPanel timePanel = new JPanel();
        timePanel.setBackground(Color.WHITE);
        timePanel.setLayout(new BoxLayout(timePanel, BoxLayout.Y_AXIS));

        timePanel.add(createLabel("HEURE DE LA VISITE"));
        timePanel.add(Box.createVerticalStrut(6));
        timePanel.add(timeSpinner);

        rowPanel.add(datePanel);
        rowPanel.add(timePanel);

        return rowPanel;
    }

    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("SansSerif", Font.BOLD, 9));
        label.setForeground(LABEL_COLOR);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);

        return label;
    }

    private JComboBox<Patient> createPatientComboBox(
            List<Patient> patients
    ) {
        JComboBox<Patient> comboBox = new JComboBox<>(
                new DefaultComboBoxModel<>(
                        patients.toArray(new Patient[0])
                )
        );

        styleComboBox(comboBox);

        comboBox.setEditable(true);
        comboBox.setRenderer(new PatientComboBoxRenderer());

        JTextField editor = (JTextField) comboBox
                .getEditor()
                .getEditorComponent();

        editor.setFont(new Font("SansSerif", Font.PLAIN, 12));
        editor.setForeground(PLACEHOLDER_COLOR);
        editor.setText("Rechercher un patient...");

        editor.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusGained(java.awt.event.FocusEvent event) {
                if (editor.getText().equals("Rechercher un patient...")) {
                    editor.setText("");
                    editor.setForeground(TEXT_COLOR);
                }
            }

            @Override
            public void focusLost(java.awt.event.FocusEvent event) {
                if (editor.getText().trim().isEmpty()
                        && comboBox.getSelectedItem() == null) {

                    editor.setText("Rechercher un patient...");
                    editor.setForeground(PLACEHOLDER_COLOR);
                }
            }
        });

        /*
         * Chaque frappe filtre la liste selon le nom complet.
         */
        editor.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent event) {
                if (filteringPatients) {
                    return;
                }

                int keyCode = event.getKeyCode();

                if (keyCode == KeyEvent.VK_UP
                        || keyCode == KeyEvent.VK_DOWN
                        || keyCode == KeyEvent.VK_ENTER
                        || keyCode == KeyEvent.VK_ESCAPE) {
                    return;
                }

                filterPatientComboBox(editor.getText());
            }
        });

        comboBox.addActionListener(event -> {
            if (filteringPatients) {
                return;
            }

            Object selectedItem = comboBox.getSelectedItem();

            if (selectedItem instanceof Patient) {
                Patient selectedPatient = (Patient) selectedItem;

                JTextField selectedEditor = (JTextField) comboBox
                        .getEditor()
                        .getEditorComponent();

                selectedEditor.setText(getPatientFullName(selectedPatient));
                selectedEditor.setForeground(TEXT_COLOR);
            }
        });

        comboBox.setSelectedItem(null);

        return comboBox;
    }

    private JComboBox<Medecin> createDoctorComboBox(
            List<Medecin> medecins
    ) {
        JComboBox<Medecin> comboBox = new JComboBox<>(
                new DefaultComboBoxModel<>(
                        medecins.toArray(new Medecin[0])
                )
        );

        styleComboBox(comboBox);
        comboBox.setRenderer(new MedecinComboBoxRenderer());

        /*
         * Oblige l'utilisateur à choisir explicitement un médecin.
         */
        comboBox.setSelectedItem(null);

        return comboBox;
    }

    private void styleComboBox(JComboBox<?> comboBox) {
        comboBox.setFont(new Font("SansSerif", Font.PLAIN, 11));
        comboBox.setForeground(TEXT_COLOR);
        comboBox.setBackground(INPUT_BACKGROUND);
        comboBox.setBorder(new LineBorder(INPUT_BACKGROUND, 1, true));
        comboBox.setCursor(new Cursor(Cursor.HAND_CURSOR));
        comboBox.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        comboBox.setPreferredSize(new Dimension(0, 38));
        comboBox.setAlignmentX(Component.LEFT_ALIGNMENT);
    }

    private JSpinner createDateSpinner() {
        // Date du jour : valeur initiale ET limite minimale du spinner.
        // L'utilisateur ne peut donc jamais descendre avant aujourd'hui.
        Date aujourdhui = new Date();

        JSpinner spinner = new JSpinner(
                new SpinnerDateModel(
                        aujourdhui,          // valeur initiale
                        aujourdhui,          // minimum = aujourd'hui
                        null,                // maximum illimité (futur)
                        Calendar.DAY_OF_MONTH
                )
        );

        spinner.setFont(new Font("SansSerif", Font.PLAIN, 11));
        spinner.setBackground(INPUT_BACKGROUND);
        spinner.setBorder(new LineBorder(INPUT_BACKGROUND, 1, true));
        spinner.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        spinner.setPreferredSize(new Dimension(0, 36));

        spinner.setEditor(
                new JSpinner.DateEditor(spinner, "dd/MM/yyyy")
        );

        return spinner;
    }

    private JSpinner createTimeSpinner() {
        JSpinner spinner = new JSpinner(
                new SpinnerDateModel(
                        new Date(),
                        null,
                        null,
                        Calendar.MINUTE
                )
        );

        spinner.setFont(new Font("SansSerif", Font.PLAIN, 11));
        spinner.setBackground(INPUT_BACKGROUND);
        spinner.setBorder(new LineBorder(INPUT_BACKGROUND, 1, true));
        spinner.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        spinner.setPreferredSize(new Dimension(0, 36));

        spinner.setEditor(
                new JSpinner.DateEditor(spinner, "HH:mm")
        );

        return spinner;
    }

    private JPanel createFooterPanel() {
        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 14, 11));
        footerPanel.setBackground(new Color(253, 249, 250));
        footerPanel.setBorder(new EmptyBorder(0, 18, 0, 18));

        JButton cancelButton = new JButton("Annuler");
        cancelButton.setFont(new Font("SansSerif", Font.BOLD, 14));
        cancelButton.setForeground(PRIMARY_COLOR);
        cancelButton.setBackground(new Color(253, 249, 250));
        cancelButton.setFocusPainted(false);
        cancelButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        cancelButton.setBorder(new LineBorder(PRIMARY_COLOR, 1, true));
        cancelButton.setPreferredSize(new Dimension(97, 43));

        JButton confirmButton = new JButton("Confirmer la visite  →");
        confirmButton.setFont(new Font("SansSerif", Font.BOLD, 13));
        confirmButton.setForeground(Color.WHITE);
        confirmButton.setBackground(PRIMARY_COLOR);
        confirmButton.setFocusPainted(false);
        confirmButton.setBorderPainted(false);
        confirmButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        confirmButton.setPreferredSize(new Dimension(184, 43));

        cancelButton.addActionListener(event -> dispose());
        confirmButton.addActionListener(event -> saveVisit());

        footerPanel.add(cancelButton);
        footerPanel.add(confirmButton);

        return footerPanel;
    }

    private void filterPatientComboBox(String keyword) {
        filteringPatients = true;

        List<Patient> filteredPatients = allPatients.stream()
                .filter(patient -> getPatientFullName(patient)
                        .toLowerCase()
                        .contains(keyword.trim().toLowerCase()))
                .toList();

        patientComboBox.setModel(
                new DefaultComboBoxModel<>(
                        filteredPatients.toArray(new Patient[0])
                )
        );

        patientComboBox.setSelectedItem(null);

        JTextField editor = (JTextField) patientComboBox
                .getEditor()
                .getEditorComponent();

        editor.setText(keyword);
        editor.setForeground(TEXT_COLOR);

        filteringPatients = false;

        if (!filteredPatients.isEmpty() && patientComboBox.isShowing()) {
            patientComboBox.setPopupVisible(true);
        }
    }

    private void saveVisit() {
        if (getSelectedPatient() == null) {
            showRequiredMessage(
                    "Veuillez sélectionner un patient existant."
            );
            return;
        }

        if (getSelectedMedecin() == null) {
            showRequiredMessage(
                    "Veuillez sélectionner un médecin."
            );
            return;
        }
// Règle métier (validée aussi côté service) : on vérifie ici de façon
        // conviviale qu'on ne planifie pas une visite dans le passé.
        if (getDateHeureVisite().isBefore(LocalDateTime.now())) {
            showRequiredMessage(
                    "La date et l'heure de la visite doivent être dans le futur."
            );
            return;
        }
        saved = true;
        dispose();
    }

    private void showRequiredMessage(String message) {
        JOptionPane.showMessageDialog(
                this,
                message,
                "Champ obligatoire",
                JOptionPane.WARNING_MESSAGE
        );
    }

    public boolean isSaved() {
        return saved;
    }

    public Patient getSelectedPatient() {
        Object selectedItem = patientComboBox.getSelectedItem();

        if (selectedItem instanceof Patient) {
            return (Patient) selectedItem;
        }

        return null;
    }

    public Medecin getSelectedMedecin() {
        Object selectedItem = doctorComboBox.getSelectedItem();

        if (selectedItem instanceof Medecin) {
            return (Medecin) selectedItem;
        }

        return null;
    }

    public LocalDateTime getDateHeureVisite() {
        Date selectedDate = (Date) dateSpinner.getValue();
        Date selectedTime = (Date) timeSpinner.getValue();

        LocalDate date = Instant.ofEpochMilli(selectedDate.getTime())
                .atZone(ZoneId.systemDefault())
                .toLocalDate();

        LocalTime time = Instant.ofEpochMilli(selectedTime.getTime())
                .atZone(ZoneId.systemDefault())
                .toLocalTime()
                .withSecond(0)
                .withNano(0);

        return LocalDateTime.of(date, time);
    }

    private String getPatientFullName(Patient patient) {
        String nom = patient.getNom() == null ? "" : patient.getNom();

        String prenom = patient.getPrenom() == null
                ? ""
                : patient.getPrenom();

        return (nom + " " + prenom).trim();
    }

    private String getMedecinFullName(Medecin medecin) {
        String nom = medecin.getNom() == null ? "" : medecin.getNom();

        String prenom = medecin.getPrenom() == null
                ? ""
                : medecin.getPrenom();

        return (nom + " " + prenom).trim();
    }

    private class PatientComboBoxRenderer
            extends DefaultListCellRenderer {

        @Override
        public Component getListCellRendererComponent(
                JList<?> list,
                Object value,
                int index,
                boolean isSelected,
                boolean cellHasFocus
        ) {
            super.getListCellRendererComponent(
                    list,
                    value,
                    index,
                    isSelected,
                    cellHasFocus
            );

            if (value instanceof Patient) {
                setText(getPatientFullName((Patient) value));
            }

            return this;
        }
    }

    private class MedecinComboBoxRenderer
            extends DefaultListCellRenderer {

        @Override
        public Component getListCellRendererComponent(
                JList<?> list,
                Object value,
                int index,
                boolean isSelected,
                boolean cellHasFocus
        ) {
            super.getListCellRendererComponent(
                    list,
                    value,
                    index,
                    isSelected,
                    cellHasFocus
            );

            if (value instanceof Medecin) {
                setText(getMedecinFullName((Medecin) value));
            }

            return this;
        }
    }

    private static class RoundedPanel extends JPanel {

        private final int radius;

        public RoundedPanel(int radius) {
            this.radius = radius;
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics graphics) {
            Graphics2D graphics2D = (Graphics2D) graphics.create();

            graphics2D.setRenderingHint(
                    RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON
            );

            graphics2D.setColor(getBackground());
            graphics2D.fillRoundRect(
                    0,
                    0,
                    getWidth(),
                    getHeight(),
                    radius,
                    radius
            );

            graphics2D.dispose();
            super.paintComponent(graphics);
        }
    }
}