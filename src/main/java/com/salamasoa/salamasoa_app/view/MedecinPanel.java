package com.salamasoa.salamasoa_app.view;

import com.salamasoa.salamasoa_app.view.Form.MedecinFormDialog;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;

public class MedecinPanel extends JPanel {

    private static final Color PRIMARY_COLOR = new Color(199, 0, 61);
    private static final Color ACTIVE_ROW_COLOR = new Color(255, 216, 220);
    private static final Color TEXT_COLOR = new Color(45, 45, 48);
    private static final Color SECONDARY_TEXT_COLOR = new Color(120, 120, 125);
    private static final Color BORDER_COLOR = new Color(238, 238, 240);

    private JTable medecinTable;
    private DefaultTableModel tableModel;

    public MedecinPanel() {
        setBackground(Color.WHITE);
        setBorder(new EmptyBorder(26, 28, 28, 28));
        setLayout(new BorderLayout(0, 18));

        add(createTopSection(), BorderLayout.NORTH);
        add(createTableSection(), BorderLayout.CENTER);
    }

    private JPanel createTopSection() {
        JPanel topSection = new JPanel();
        topSection.setBackground(Color.WHITE);
        topSection.setLayout(new BoxLayout(topSection, BoxLayout.Y_AXIS));

        topSection.add(createTitleAndButtonPanel());
        topSection.add(Box.createVerticalStrut(16));
        topSection.add(createFilterPanel());

        return topSection;
    }

    private JPanel createTitleAndButtonPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(Color.WHITE);
        headerPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));

        JLabel titleLabel = new JLabel("Gérez vos médecins");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
        titleLabel.setForeground(TEXT_COLOR);

        JButton newDoctorButton = new JButton("+  Nouveau");
        newDoctorButton.setFont(new Font("SansSerif", Font.BOLD, 12));
        newDoctorButton.setForeground(Color.WHITE);
        newDoctorButton.setBackground(PRIMARY_COLOR);
        newDoctorButton.setFocusPainted(false);
        newDoctorButton.setBorderPainted(false);
        newDoctorButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        newDoctorButton.setBorder(new EmptyBorder(10, 16, 10, 16));

        newDoctorButton.addActionListener(event -> {
            Window window = SwingUtilities.getWindowAncestor(this);

            MedecinFormDialog dialog = new MedecinFormDialog(window);
            dialog.setVisible(true);
        });

        headerPanel.add(titleLabel, BorderLayout.WEST);
        headerPanel.add(newDoctorButton, BorderLayout.EAST);

        return headerPanel;
    }

    private JPanel createFilterPanel() {
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        filterPanel.setBackground(Color.WHITE);
        filterPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel gradeLabel = new JLabel("Grade :");
        gradeLabel.setFont(new Font("SansSerif", Font.BOLD, 11));
        gradeLabel.setForeground(TEXT_COLOR);

        String[] grades = {
                "Tous les grades",
                "Médecin généraliste",
                "Cardiologue",
                "Pédiatre",
                "Chirurgien",
                "Infirmier / Infirmière"
        };

        JComboBox<String> gradeComboBox = new JComboBox<>(grades);
        styleComboBox(gradeComboBox);

        filterPanel.add(gradeLabel);
        filterPanel.add(gradeComboBox);

        return filterPanel;
    }

    private void styleComboBox(JComboBox<String> comboBox) {
        comboBox.setFont(new Font("SansSerif", Font.PLAIN, 10));
        comboBox.setForeground(TEXT_COLOR);
        comboBox.setBackground(Color.WHITE);
        comboBox.setPreferredSize(new Dimension(160, 28));
        comboBox.setBorder(new LineBorder(BORDER_COLOR, 1, true));
        comboBox.setCursor(new Cursor(Cursor.HAND_CURSOR));
        comboBox.setFocusable(false);
    }

    private JScrollPane createTableSection() {
        String[] columnNames = {
                "NOM DU MÉDECIN",
                "ID",
                "GRADE",
                "ACTION"
        };

        Object[][] medecinData = {
                {
                        "<html><b>Dr. James Smith</b>"
                                + "<br><span style='font-size:9px; color:#777777;'>Médecine générale</span></html>",
                        "#MD-1001",
                        "Médecin généraliste",
                        "⋮"
                },
                {
                        "<html><b>Dr. Ana Martinez</b>"
                                + "<br><span style='font-size:9px; color:#777777;'>Cardiologie</span></html>",
                        "#MD-1002",
                        "Cardiologue",
                        "⋮"
                },
                {
                        "<html><b>Dr. Sarah Johnson</b>"
                                + "<br><span style='font-size:9px; color:#777777;'>Pédiatrie</span></html>",
                        "#MD-1003",
                        "Pédiatre",
                        "⋮"
                },
                {
                        "<html><b>Dr. Patrick Brown</b>"
                                + "<br><span style='font-size:9px; color:#777777;'>Chirurgie</span></html>",
                        "#MD-1004",
                        "Chirurgien",
                        "⋮"
                }
        };

        tableModel = new DefaultTableModel(medecinData, columnNames) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        medecinTable = new JTable(tableModel);
        medecinTable.setRowHeight(58);
        medecinTable.setFont(new Font("SansSerif", Font.PLAIN, 11));
        medecinTable.setForeground(TEXT_COLOR);
        medecinTable.setBackground(Color.WHITE);
        medecinTable.setGridColor(BORDER_COLOR);
        medecinTable.setShowVerticalLines(false);
        medecinTable.setShowHorizontalLines(true);
        medecinTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        medecinTable.setRowSelectionAllowed(true);
        medecinTable.setColumnSelectionAllowed(false);

        // Première ligne sélectionnée au démarrage.
        medecinTable.setRowSelectionInterval(0, 0);

        medecinTable.getColumnModel().getColumn(0).setPreferredWidth(260);
        medecinTable.getColumnModel().getColumn(1).setPreferredWidth(130);
        medecinTable.getColumnModel().getColumn(2).setPreferredWidth(220);
        medecinTable.getColumnModel().getColumn(3).setPreferredWidth(90);

        medecinTable.setDefaultRenderer(Object.class, new MedecinCellRenderer());
        medecinTable.getColumnModel().getColumn(3)
                .setCellRenderer(new ActionCellRenderer());

        JTableHeader tableHeader = medecinTable.getTableHeader();
        tableHeader.setFont(new Font("SansSerif", Font.BOLD, 9));
        tableHeader.setForeground(new Color(100, 100, 105));
        tableHeader.setBackground(Color.WHITE);
        tableHeader.setPreferredSize(new Dimension(0, 33));
        tableHeader.setReorderingAllowed(false);

        JScrollPane scrollPane = new JScrollPane(medecinTable);
        scrollPane.setBorder(new LineBorder(BORDER_COLOR, 1, true));
        scrollPane.getViewport().setBackground(Color.WHITE);
        scrollPane.setBackground(Color.WHITE);

        return scrollPane;
    }

    private class MedecinCellRenderer extends DefaultTableCellRenderer {

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