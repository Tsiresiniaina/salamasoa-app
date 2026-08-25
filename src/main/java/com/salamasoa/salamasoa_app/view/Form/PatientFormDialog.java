package com.salamasoa.salamasoa_app.view.Form;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import com.salamasoa.salamasoa_app.view.GlassPane.BackgroundOverlay;

import com.salamasoa.salamasoa_app.model.Patient;

public class PatientFormDialog extends JDialog {

    private static final Color PRIMARY_COLOR = new Color(199, 0, 61);
    private static final Color TEXT_COLOR = new Color(42, 42, 45);
    private static final Color LABEL_COLOR = new Color(105, 75, 80);
    private static final Color INPUT_BACKGROUND = new Color(248, 248, 250);
    private static final Color BORDER_COLOR = new Color(230, 227, 229);
    private static final Color PLACEHOLDER_COLOR = new Color(150, 150, 155);

    private static final String NAME_PLACEHOLDER = "e.g. Jane Doe";
    private static final String ADDRESS_PLACEHOLDER = "Adresse du patient";

    private final JTextField fullNameField;
    private final JRadioButton maleButton;
    private final JRadioButton femaleButton;
    private final JTextArea addressArea;

    private final Patient patientToEdit;
    private final boolean editMode;

    private boolean saved = false;
    private final BackgroundOverlay.OverlayHandle overlayHandle;

    /*
     * Constructeur utilisé pour créer un nouveau patient.
     */
    public PatientFormDialog(Window owner) {
        this(owner, null);
    }

    /*
     * Constructeur utilisé pour modifier un patient existant.
     */
    public PatientFormDialog(Window owner, Patient patientToEdit) {
        super(
                owner,
                patientToEdit == null
                        ? "Nouveau Patient"
                        : "Modifier le patient",
                ModalityType.APPLICATION_MODAL
        );

        this.patientToEdit = patientToEdit;
        this.editMode = patientToEdit != null;

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
        setSize(410, 390);
        setResizable(false);
        setLocationRelativeTo(owner);

        RoundedPanel mainPanel = new RoundedPanel(14);
        mainPanel.setLayout(new BorderLayout());
        mainPanel.setBackground(Color.WHITE);

        mainPanel.add(createHeaderPanel(), BorderLayout.NORTH);

        JPanel formPanel = new JPanel();
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(new EmptyBorder(18, 20, 18, 20));
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));

        fullNameField = createTextField(NAME_PLACEHOLDER);
        maleButton = createRadioButton("HOMME");
        femaleButton = createRadioButton("FEMME");
        addressArea = createAddressArea();

        formPanel.add(createLabel("NOM COMPLET DU PATIENT"));
        formPanel.add(Box.createVerticalStrut(7));
        formPanel.add(fullNameField);

        formPanel.add(Box.createVerticalStrut(15));
        formPanel.add(createLabel("SEXE"));
        formPanel.add(Box.createVerticalStrut(6));
        formPanel.add(createSexPanel());

        formPanel.add(Box.createVerticalStrut(17));
        formPanel.add(createLabel("ADRESSE"));
        formPanel.add(Box.createVerticalStrut(7));
        formPanel.add(createAddressScrollPane());

        mainPanel.add(formPanel, BorderLayout.CENTER);
        mainPanel.add(createFooterPanel(), BorderLayout.SOUTH);

        /*
         * En mode modification, les champs sont remplis
         * à partir des données réelles du patient.
         */
        if (editMode) {
            fillFormWithPatientData();
        }

        setContentPane(mainPanel);
    }

    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(Color.WHITE);
        headerPanel.setBorder(
                new EmptyBorder(18, 20, 16, 16)
        );

        JLabel titleLabel = new JLabel(
                editMode ? "Modifier le patient" : "Nouveau Patient"
        );
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 17));
        titleLabel.setForeground(TEXT_COLOR);

        JButton closeButton = new JButton("×");
        closeButton.setFont(new Font("SansSerif", Font.PLAIN, 24));
        closeButton.setForeground(new Color(95, 65, 70));
        closeButton.setBorderPainted(false);
        closeButton.setContentAreaFilled(false);
        closeButton.setFocusPainted(false);
        closeButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        closeButton.addActionListener(event -> dispose());

        headerPanel.add(titleLabel, BorderLayout.WEST);
        headerPanel.add(closeButton, BorderLayout.EAST);

        return headerPanel;
    }

    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("SansSerif", Font.BOLD, 10));
        label.setForeground(LABEL_COLOR);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);

        return label;
    }

    private JTextField createTextField(String placeholder) {
        JTextField field = new JTextField(placeholder);

        field.setFont(new Font("SansSerif", Font.PLAIN, 13));
        field.setForeground(PLACEHOLDER_COLOR);
        field.setBackground(INPUT_BACKGROUND);
        field.setBorder(new EmptyBorder(0, 12, 0, 12));
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        field.setPreferredSize(new Dimension(0, 40));
        field.setAlignmentX(Component.LEFT_ALIGNMENT);

        configureTextFieldPlaceholder(field, placeholder);

        return field;
    }

    private JPanel createSexPanel() {
        JPanel sexPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        sexPanel.setBackground(Color.WHITE);
        sexPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        ButtonGroup sexGroup = new ButtonGroup();
        sexGroup.add(maleButton);
        sexGroup.add(femaleButton);

        sexPanel.add(maleButton);
        sexPanel.add(femaleButton);

        return sexPanel;
    }

    private JRadioButton createRadioButton(String text) {
        JRadioButton radioButton = new JRadioButton(text);

        radioButton.setFont(new Font("SansSerif", Font.BOLD, 10));
        radioButton.setForeground(LABEL_COLOR);
        radioButton.setBackground(Color.WHITE);
        radioButton.setFocusPainted(false);
        radioButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        return radioButton;
    }

    private JScrollPane createAddressScrollPane() {
        JScrollPane scrollPane = new JScrollPane(addressArea);
        scrollPane.setBorder(new LineBorder(INPUT_BACKGROUND, 1, true));
        scrollPane.setAlignmentX(Component.LEFT_ALIGNMENT);
        scrollPane.setMaximumSize(new Dimension(Integer.MAX_VALUE, 70));
        scrollPane.setPreferredSize(new Dimension(0, 70));

        return scrollPane;
    }

    private JTextArea createAddressArea() {
        JTextArea textArea = new JTextArea(3, 20);

        textArea.setText(ADDRESS_PLACEHOLDER);
        textArea.setFont(new Font("SansSerif", Font.PLAIN, 13));
        textArea.setForeground(PLACEHOLDER_COLOR);
        textArea.setBackground(INPUT_BACKGROUND);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        textArea.setBorder(new EmptyBorder(10, 12, 10, 12));

        configureTextAreaPlaceholder(textArea);

        return textArea;
    }

    private JPanel createFooterPanel() {
        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 16, 12));
        footerPanel.setBackground(new Color(253, 249, 250));
        footerPanel.setBorder(new EmptyBorder(0, 20, 0, 20));

        JButton cancelButton = new JButton("Annuler");
        cancelButton.setFont(new Font("SansSerif", Font.BOLD, 14));
        cancelButton.setForeground(PRIMARY_COLOR);
        cancelButton.setBackground(new Color(253, 249, 250));
        cancelButton.setFocusPainted(false);
        cancelButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        cancelButton.setBorder(new LineBorder(PRIMARY_COLOR, 1, true));
        cancelButton.setPreferredSize(new Dimension(102, 44));

        JButton saveButton = new JButton(
                editMode ? "Mettre à jour  →" : "Enregistrer  →"
        );
        saveButton.setFont(new Font("SansSerif", Font.BOLD, 14));
        saveButton.setForeground(Color.WHITE);
        saveButton.setBackground(PRIMARY_COLOR);
        saveButton.setFocusPainted(false);
        saveButton.setBorderPainted(false);
        saveButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        saveButton.setPreferredSize(new Dimension(144, 44));

        cancelButton.addActionListener(event -> dispose());
        saveButton.addActionListener(event -> savePatient());

        footerPanel.add(cancelButton);
        footerPanel.add(saveButton);

        return footerPanel;
    }

    private void fillFormWithPatientData() {
        String nom = patientToEdit.getNom() == null
                ? ""
                : patientToEdit.getNom();

        String prenom = patientToEdit.getPrenom() == null
                ? ""
                : patientToEdit.getPrenom();

        String fullName = (nom + " " + prenom).trim();

        fullNameField.setText(fullName);
        fullNameField.setForeground(TEXT_COLOR);

        if (patientToEdit.getSexe() == 'H'
                || patientToEdit.getSexe() == 'h') {

            maleButton.setSelected(true);

        } else if (patientToEdit.getSexe() == 'F'
                || patientToEdit.getSexe() == 'f') {

            femaleButton.setSelected(true);
        }

        addressArea.setText(patientToEdit.getAdresse());
        addressArea.setForeground(TEXT_COLOR);
    }

    private void savePatient() {
        if (getFullName().isBlank()) {
            JOptionPane.showMessageDialog(
                    this,
                    "Veuillez saisir le nom complet du patient.",
                    "Champ obligatoire",
                    JOptionPane.WARNING_MESSAGE
            );
            fullNameField.requestFocusInWindow();
            return;
        }

        if (getSexe() == null) {
            JOptionPane.showMessageDialog(
                    this,
                    "Veuillez sélectionner le sexe du patient.",
                    "Champ obligatoire",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        if (getAddress().isBlank()) {
            JOptionPane.showMessageDialog(
                    this,
                    "Veuillez saisir l'adresse du patient.",
                    "Champ obligatoire",
                    JOptionPane.WARNING_MESSAGE
            );
            addressArea.requestFocusInWindow();
            return;
        }

        saved = true;
        dispose();
    }

    private void configureTextFieldPlaceholder(
            JTextField field,
            String placeholder
    ) {
        field.addFocusListener(new FocusAdapter() {

            @Override
            public void focusGained(FocusEvent event) {
                if (field.getText().equals(placeholder)) {
                    field.setText("");
                    field.setForeground(TEXT_COLOR);
                }
            }

            @Override
            public void focusLost(FocusEvent event) {
                if (field.getText().trim().isEmpty()) {
                    field.setText(placeholder);
                    field.setForeground(PLACEHOLDER_COLOR);
                }
            }
        });
    }

    private void configureTextAreaPlaceholder(JTextArea textArea) {
        textArea.addFocusListener(new FocusAdapter() {

            @Override
            public void focusGained(FocusEvent event) {
                if (textArea.getText().equals(ADDRESS_PLACEHOLDER)) {
                    textArea.setText("");
                    textArea.setForeground(TEXT_COLOR);
                }
            }

            @Override
            public void focusLost(FocusEvent event) {
                if (textArea.getText().trim().isEmpty()) {
                    textArea.setText(ADDRESS_PLACEHOLDER);
                    textArea.setForeground(PLACEHOLDER_COLOR);
                }
            }
        });
    }

    public boolean isSaved() {
        return saved;
    }

    public String getFullName() {
        String name = fullNameField.getText().trim();

        return name.equals(NAME_PLACEHOLDER) ? "" : name;
    }

    public String getSexe() {
        if (maleButton.isSelected()) {
            return "HOMME";
        }

        if (femaleButton.isSelected()) {
            return "FEMME";
        }

        return null;
    }

    public String getAddress() {
        String address = addressArea.getText().trim();

        return address.equals(ADDRESS_PLACEHOLDER) ? "" : address;
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