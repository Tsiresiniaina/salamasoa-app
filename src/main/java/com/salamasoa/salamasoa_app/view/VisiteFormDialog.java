package com.salamasoa.salamasoa_app.view;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;

public class VisiteFormDialog extends JDialog {

    private static final Color PRIMARY_COLOR = new Color(199, 0, 61);
    private static final Color TEXT_COLOR = new Color(42, 42, 45);
    private static final Color LABEL_COLOR = new Color(105, 75, 80);
    private static final Color INPUT_BACKGROUND = new Color(248, 248, 250);
    private static final Color PLACEHOLDER_COLOR = new Color(150, 150, 155);

    private static final String NAME_PLACEHOLDER = "e.g. Jane Doe";
    private static final String ADDRESS_PLACEHOLDER = "Adresse du patient";
    private static final String DATE_PLACEHOLDER = "00/00/0000";

    private final JTextField fullNameField;
    private final JRadioButton maleButton;
    private final JRadioButton femaleButton;
    private final JComboBox<String> doctorComboBox;
    private final JTextArea addressArea;
    private final JTextField dateField;

    private boolean saved = false;

    public VisiteFormDialog(Window owner) {
        super(owner, "Enregistrement de visite", ModalityType.APPLICATION_MODAL);

        setUndecorated(true);
        setSize(410, 455);
        setResizable(false);
        setLocationRelativeTo(owner);

        RoundedPanel mainPanel = new RoundedPanel(14);
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setLayout(new BorderLayout());

        mainPanel.add(createHeaderPanel(), BorderLayout.NORTH);

        fullNameField = createTextField(NAME_PLACEHOLDER, 40);
        maleButton = createRadioButton("HOMME");
        femaleButton = createRadioButton("FEMME");
        doctorComboBox = createDoctorComboBox();
        addressArea = createAddressArea();
        dateField = createTextField(DATE_PLACEHOLDER, 40);

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
        formPanel.setBorder(new EmptyBorder(15, 18, 14, 18));
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));

        formPanel.add(createLabel("NOM COMPLET DU PATIENT"));
        formPanel.add(Box.createVerticalStrut(7));
        formPanel.add(fullNameField);

        formPanel.add(Box.createVerticalStrut(14));
        formPanel.add(createSexAndDoctorPanel());

        formPanel.add(Box.createVerticalStrut(14));
        formPanel.add(createLabel("ADRESSE"));
        formPanel.add(Box.createVerticalStrut(7));
        formPanel.add(createAddressScrollPane());

        formPanel.add(Box.createVerticalStrut(14));
        formPanel.add(createLabel("DATE DU VISITE"));
        formPanel.add(Box.createVerticalStrut(7));
        formPanel.add(dateField);

        return formPanel;
    }

    private JPanel createSexAndDoctorPanel() {
        JPanel rowPanel = new JPanel(new GridLayout(1, 2, 18, 0));
        rowPanel.setBackground(Color.WHITE);
        rowPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        rowPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 58));

        JPanel sexPanel = new JPanel();
        sexPanel.setBackground(Color.WHITE);
        sexPanel.setLayout(new BoxLayout(sexPanel, BoxLayout.Y_AXIS));

        sexPanel.add(createLabel("SEXE"));
        sexPanel.add(Box.createVerticalStrut(6));
        sexPanel.add(createSexButtonsPanel());

        JPanel doctorPanel = new JPanel();
        doctorPanel.setBackground(Color.WHITE);
        doctorPanel.setLayout(new BoxLayout(doctorPanel, BoxLayout.Y_AXIS));

        doctorPanel.add(createLabel("DOCTEUR RESPONSABLE"));
        doctorPanel.add(Box.createVerticalStrut(6));
        doctorPanel.add(doctorComboBox);

        rowPanel.add(sexPanel);
        rowPanel.add(doctorPanel);

        return rowPanel;
    }

    private JPanel createSexButtonsPanel() {
        JPanel sexButtonsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        sexButtonsPanel.setBackground(Color.WHITE);
        sexButtonsPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        ButtonGroup sexGroup = new ButtonGroup();
        sexGroup.add(maleButton);
        sexGroup.add(femaleButton);

        sexButtonsPanel.add(maleButton);
        sexButtonsPanel.add(femaleButton);

        return sexButtonsPanel;
    }

    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("SansSerif", Font.BOLD, 9));
        label.setForeground(LABEL_COLOR);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);

        return label;
    }

    private JTextField createTextField(String placeholder, int height) {
        JTextField field = new JTextField(placeholder);

        field.setFont(new Font("SansSerif", Font.PLAIN, 13));
        field.setForeground(PLACEHOLDER_COLOR);
        field.setBackground(INPUT_BACKGROUND);
        field.setBorder(new EmptyBorder(0, 11, 0, 11));
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, height));
        field.setPreferredSize(new Dimension(0, height));
        field.setAlignmentX(Component.LEFT_ALIGNMENT);

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

        return field;
    }

    private JRadioButton createRadioButton(String text) {
        JRadioButton radioButton = new JRadioButton(text);

        radioButton.setFont(new Font("SansSerif", Font.BOLD, 9));
        radioButton.setForeground(LABEL_COLOR);
        radioButton.setBackground(Color.WHITE);
        radioButton.setFocusPainted(false);
        radioButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        return radioButton;
    }

    private JComboBox<String> createDoctorComboBox() {
        String[] doctors = {
                "Sebastien Scapin",
                "Dr. James Smith",
                "Dr. Ana Martinez",
                "Dr. Sarah Johnson"
        };

        JComboBox<String> comboBox = new JComboBox<>(doctors);

        comboBox.setFont(new Font("SansSerif", Font.PLAIN, 11));
        comboBox.setForeground(TEXT_COLOR);
        comboBox.setBackground(INPUT_BACKGROUND);
        comboBox.setBorder(new LineBorder(INPUT_BACKGROUND, 1, true));
        comboBox.setFocusable(false);
        comboBox.setCursor(new Cursor(Cursor.HAND_CURSOR));
        comboBox.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        comboBox.setPreferredSize(new Dimension(0, 36));
        comboBox.setAlignmentX(Component.LEFT_ALIGNMENT);

        return comboBox;
    }

    private JScrollPane createAddressScrollPane() {
        JScrollPane scrollPane = new JScrollPane(addressArea);
        scrollPane.setBorder(new LineBorder(INPUT_BACKGROUND, 1, true));
        scrollPane.setAlignmentX(Component.LEFT_ALIGNMENT);
        scrollPane.setMaximumSize(new Dimension(Integer.MAX_VALUE, 67));
        scrollPane.setPreferredSize(new Dimension(0, 67));

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
        textArea.setBorder(new EmptyBorder(9, 11, 9, 11));

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

        return textArea;
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

    private void saveVisit() {
        if (getFullName().isBlank()) {
            showRequiredMessage("Veuillez saisir le nom du patient.");
            fullNameField.requestFocusInWindow();
            return;
        }

        if (getSexe() == null) {
            showRequiredMessage("Veuillez sélectionner le sexe du patient.");
            return;
        }

        if (getAddress().isBlank()) {
            showRequiredMessage("Veuillez saisir l'adresse du patient.");
            addressArea.requestFocusInWindow();
            return;
        }

        if (getDateVisite().isBlank()) {
            showRequiredMessage("Veuillez saisir la date de la visite.");
            dateField.requestFocusInWindow();
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

    public String getDoctor() {
        return doctorComboBox.getSelectedItem().toString();
    }

    public String getAddress() {
        String address = addressArea.getText().trim();
        return address.equals(ADDRESS_PLACEHOLDER) ? "" : address;
    }

    public String getDateVisite() {
        String date = dateField.getText().trim();
        return date.equals(DATE_PLACEHOLDER) ? "" : date;
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