package com.salamasoa.salamasoa_app.view;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;

public class MedecinFormDialog extends JDialog {

    private static final Color PRIMARY_COLOR = new Color(199, 0, 61);
    private static final Color TEXT_COLOR = new Color(42, 42, 45);
    private static final Color LABEL_COLOR = new Color(105, 75, 80);
    private static final Color INPUT_BACKGROUND = new Color(248, 248, 250);
    private static final Color PLACEHOLDER_COLOR = new Color(150, 150, 155);

    private static final String NAME_PLACEHOLDER = "e.g. Jane Doe";

    private final JTextField fullNameField;
    private final JComboBox<String> gradeComboBox;

    private boolean saved = false;

    public MedecinFormDialog(Window owner) {
        super(owner, "Nouveau médecin", ModalityType.APPLICATION_MODAL);

        setUndecorated(true);
        setSize(390, 305);
        setResizable(false);
        setLocationRelativeTo(owner);

        RoundedPanel mainPanel = new RoundedPanel(14);
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setLayout(new BorderLayout());

        mainPanel.add(createHeaderPanel(), BorderLayout.NORTH);

        fullNameField = createNameField();
        gradeComboBox = createGradeComboBox();

        mainPanel.add(createFormPanel(), BorderLayout.CENTER);
        mainPanel.add(createFooterPanel(), BorderLayout.SOUTH);

        setContentPane(mainPanel);
    }

    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(Color.WHITE);
        headerPanel.setBorder(new EmptyBorder(17, 18, 15, 14));

        JLabel titleLabel = new JLabel("Nouveau médecin");
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

        formPanel.add(createLabel("NOM COMPLET DU DOCTEUR"));
        formPanel.add(Box.createVerticalStrut(7));
        formPanel.add(fullNameField);

        formPanel.add(Box.createVerticalStrut(15));
        formPanel.add(createLabel("GRADE DU DOCTEUR"));
        formPanel.add(Box.createVerticalStrut(7));
        formPanel.add(gradeComboBox);

        return formPanel;
    }

    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("SansSerif", Font.BOLD, 9));
        label.setForeground(LABEL_COLOR);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);

        return label;
    }

    private JTextField createNameField() {
        JTextField field = new JTextField(NAME_PLACEHOLDER);

        field.setFont(new Font("SansSerif", Font.PLAIN, 13));
        field.setForeground(PLACEHOLDER_COLOR);
        field.setBackground(INPUT_BACKGROUND);
        field.setBorder(new EmptyBorder(0, 11, 0, 11));
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        field.setPreferredSize(new Dimension(0, 40));
        field.setAlignmentX(Component.LEFT_ALIGNMENT);

        field.addFocusListener(new FocusAdapter() {

            @Override
            public void focusGained(FocusEvent event) {
                if (field.getText().equals(NAME_PLACEHOLDER)) {
                    field.setText("");
                    field.setForeground(TEXT_COLOR);
                }
            }

            @Override
            public void focusLost(FocusEvent event) {
                if (field.getText().trim().isEmpty()) {
                    field.setText(NAME_PLACEHOLDER);
                    field.setForeground(PLACEHOLDER_COLOR);
                }
            }
        });

        return field;
    }

    private JComboBox<String> createGradeComboBox() {
        String[] grades = {
                "Médecin chef",
                "Médecin généraliste",
                "Cardiologue",
                "Pédiatre",
                "Chirurgien"
        };

        JComboBox<String> comboBox = new JComboBox<>(grades);

        comboBox.setFont(new Font("SansSerif", Font.PLAIN, 12));
        comboBox.setForeground(TEXT_COLOR);
        comboBox.setBackground(INPUT_BACKGROUND);
        comboBox.setBorder(new LineBorder(INPUT_BACKGROUND, 1, true));
        comboBox.setFocusable(false);
        comboBox.setCursor(new Cursor(Cursor.HAND_CURSOR));
        comboBox.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        comboBox.setPreferredSize(new Dimension(0, 38));
        comboBox.setAlignmentX(Component.LEFT_ALIGNMENT);

        return comboBox;
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

        JButton saveButton = new JButton("Enregistrer  →");
        saveButton.setFont(new Font("SansSerif", Font.BOLD, 13));
        saveButton.setForeground(Color.WHITE);
        saveButton.setBackground(PRIMARY_COLOR);
        saveButton.setFocusPainted(false);
        saveButton.setBorderPainted(false);
        saveButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        saveButton.setPreferredSize(new Dimension(138, 43));

        cancelButton.addActionListener(event -> dispose());
        saveButton.addActionListener(event -> saveDoctor());

        footerPanel.add(cancelButton);
        footerPanel.add(saveButton);

        return footerPanel;
    }

    private void saveDoctor() {
        if (getFullName().isBlank()) {
            JOptionPane.showMessageDialog(
                    this,
                    "Veuillez saisir le nom complet du médecin.",
                    "Champ obligatoire",
                    JOptionPane.WARNING_MESSAGE
            );

            fullNameField.requestFocusInWindow();
            return;
        }

        saved = true;
        dispose();
    }

    public boolean isSaved() {
        return saved;
    }

    public String getFullName() {
        String fullName = fullNameField.getText().trim();

        return fullName.equals(NAME_PLACEHOLDER) ? "" : fullName;
    }

    public String getGrade() {
        return gradeComboBox.getSelectedItem().toString();
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