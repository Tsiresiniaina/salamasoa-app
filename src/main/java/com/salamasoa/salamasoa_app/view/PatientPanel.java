package com.salamasoa.salamasoa_app.view;

import com.salamasoa.salamasoa_app.view.Form.PatientFormDialog;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import java.awt.*;

public class PatientPanel extends JPanel {

    private static final Color PRIMARY_COLOR = new Color(199, 0, 61);
    private static final Color ACTIVE_ROW_COLOR = new Color(255, 216, 220);
    private static final Color TEXT_COLOR = new Color(45, 45, 48);
    private static final Color SECONDARY_TEXT_COLOR = new Color(120, 120, 125);
    private static final Color BORDER_COLOR = new Color(238, 238, 240);

    private JTable patientTable;
    private DefaultTableModel tableModel;

    public PatientPanel() {
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
        });

        headerPanel.add(titleLabel, BorderLayout.WEST);
        headerPanel.add(newPatientButton, BorderLayout.EAST);

        return headerPanel;
    }

    private JPanel createFiltersPanel() {
        JPanel filtersPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        filtersPanel.setBackground(Color.WHITE);
        filtersPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        String[] statuses = {
                "Tous les statuts",
                "Active",
                "Discharged"
        };

        String[] doctors = {
                "Tous les docteurs",
                "Dr. J. Smith",
                "Dr. A. Martinez"
        };

        JComboBox<String> statusComboBox = new JComboBox<>(statuses);
        JComboBox<String> doctorComboBox = new JComboBox<>(doctors);

        styleComboBox(statusComboBox);
        styleComboBox(doctorComboBox);

        filtersPanel.add(statusComboBox);
        filtersPanel.add(doctorComboBox);

        return filtersPanel;
    }

    private void styleComboBox(JComboBox<String> comboBox) {
        comboBox.setFont(new Font("SansSerif", Font.PLAIN, 10));
        comboBox.setForeground(TEXT_COLOR);
        comboBox.setBackground(Color.WHITE);
        comboBox.setPreferredSize(new Dimension(118, 28));
        comboBox.setBorder(new LineBorder(BORDER_COLOR, 1, true));
        comboBox.setCursor(new Cursor(Cursor.HAND_CURSOR));
        comboBox.setFocusable(false);
    }

    private JScrollPane createTableSection() {
        String[] columnNames = {
                "NOM DU PATIENT",
                "ID",
                "DOCTEUR RESPONSABLE",
                "ACTION"
        };

        Object[][] patientData = {
                {
                        "<html><b>Eleanor<br>Vance</b>"
                                + "<br><span style='font-size:9px; color:#777777;'>F, 68 yrs</span></html>",
                        "#PT-<br>8492",
                        "Dr. J. Smith",
                        "⋮"
                },
                {
                        "<html><b>Marcus<br>Lin</b>"
                                + "<br><span style='font-size:9px; color:#777777;'>M, 42 yrs</span></html>",
                        "#PT-7731",
                        "Dr. A. Martinez",
                        "Discharged"
                },
                {
                        "<html><b>David<br>Osei</b>"
                                + "<br><span style='font-size:9px; color:#777777;'>M, 29 yrs</span></html>",
                        "#PT-<br>9012",
                        "Dr. J. Smith",
                        "Active"
                }
        };

        tableModel = new DefaultTableModel(patientData, columnNames) {
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

        // Première ligne sélectionnée, comme sur la maquette Figma.
        patientTable.setRowSelectionInterval(0, 0);

        patientTable.getColumnModel().getColumn(0).setPreferredWidth(210);
        patientTable.getColumnModel().getColumn(1).setPreferredWidth(105);
        patientTable.getColumnModel().getColumn(2).setPreferredWidth(160);
        patientTable.getColumnModel().getColumn(3).setPreferredWidth(95);

        patientTable.setDefaultRenderer(Object.class, new PatientCellRenderer());
        patientTable.getColumnModel().getColumn(3)
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

    private class ActionCellRenderer implements TableCellRenderer {

        private final JPanel panel;
        private final JLabel actionLabel;

        public ActionCellRenderer() {
            panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
            actionLabel = new JLabel();
            actionLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
            actionLabel.setForeground(new Color(115, 115, 120));

            panel.add(actionLabel);
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
            panel.setBackground(isSelected ? ACTIVE_ROW_COLOR : Color.WHITE);

            String action = value == null ? "" : value.toString();

            if (action.equals("Active")) {
                actionLabel.setText("Active");
                actionLabel.setFont(new Font("SansSerif", Font.BOLD, 9));
                actionLabel.setForeground(PRIMARY_COLOR);
                actionLabel.setBorder(new EmptyBorder(4, 6, 4, 6));
            } else if (action.equals("Discharged")) {
                actionLabel.setText("Discharged");
                actionLabel.setFont(new Font("SansSerif", Font.PLAIN, 8));
                actionLabel.setForeground(new Color(100, 100, 105));
                actionLabel.setBorder(new EmptyBorder(4, 6, 4, 6));
            } else {
                actionLabel.setText(action);
                actionLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
                actionLabel.setForeground(new Color(115, 115, 120));
                actionLabel.setBorder(new EmptyBorder(0, 0, 0, 0));
            }

            return panel;
        }
    }
}