package com.salamasoa.salamasoa_app.view;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.util.Calendar;
import java.util.Date;

public class VisitePanel extends JPanel {

    private static final Color PRIMARY_COLOR = new Color(199, 0, 61);
    private static final Color TEXT_COLOR = new Color(42, 42, 45);
    private static final Color SECONDARY_TEXT_COLOR = new Color(125, 125, 130);
    private static final Color BORDER_COLOR = new Color(235, 235, 237);

    public VisitePanel() {
        setBackground(Color.WHITE);
        setBorder(new EmptyBorder(18, 26, 26, 26));
        setLayout(new BorderLayout(0, 16));

        add(createTopSection(), BorderLayout.NORTH);
        add(createVisitsSection(), BorderLayout.CENTER);
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
                JOptionPane.showMessageDialog(
                        this,
                        "Le formulaire de création d'une visite "
                                + "sera créé prochainement.",
                        "Nouvelle visite",
                        JOptionPane.INFORMATION_MESSAGE
                )
        );

        headerPanel.add(titlePanel, BorderLayout.WEST);
        headerPanel.add(newVisitButton, BorderLayout.EAST);

        return headerPanel;
    }

    private JPanel createStatisticsPanel() {
        JPanel statisticsPanel = new JPanel(new GridLayout(1, 3, 12, 0));
        statisticsPanel.setBackground(Color.WHITE);
        statisticsPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 84));
        statisticsPanel.setPreferredSize(new Dimension(0, 84));
        statisticsPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        statisticsPanel.add(createStatisticCard(
                "AUJOURD'HUI",
                "42",
                "▣",
                new Color(255, 228, 235),
                PRIMARY_COLOR
        ));

        statisticsPanel.add(createStatisticCard(
                "SALLE D'ATTENTE",
                "7",
                "♙",
                new Color(243, 238, 241),
                new Color(115, 95, 105)
        ));

        statisticsPanel.add(createStatisticCard(
                "EFFECTUÉE",
                "18",
                "◎",
                new Color(243, 238, 241),
                new Color(115, 95, 105)
        ));

        return statisticsPanel;
    }

    private JPanel createStatisticCard(
            String title,
            String value,
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

        JLabel valueLabel = new JLabel(value);
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

        Calendar calendar = Calendar.getInstance();

        SpinnerDateModel dateModel = new SpinnerDateModel(
                calendar.getTime(),
                null,
                null,
                Calendar.DAY_OF_MONTH
        );

        JSpinner dateSpinner = new JSpinner(dateModel);
        dateSpinner.setFont(new Font("SansSerif", Font.PLAIN, 11));
        dateSpinner.setPreferredSize(new Dimension(120, 28));
        dateSpinner.setBorder(new LineBorder(BORDER_COLOR, 1, true));

        JSpinner.DateEditor dateEditor =
                new JSpinner.DateEditor(dateSpinner, "dd/MM/yyyy");

        dateSpinner.setEditor(dateEditor);

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
                "STATUS",
                "ACTION"
        };

        Object[][] visitData = {
                {
                        "10:00 AM",
                        "<html><b>Sarah Jenkins</b>"
                                + "<br><span style='font-size:8px; color:#888888;'>ID: 8492-A</span></html>",
                        "Homme",
                        "En cours",
                        "⋮"
                },
                {
                        "10:30 AM",
                        "<html><b>Michael Reed</b>"
                                + "<br><span style='font-size:8px; color:#888888;'>ID: 9123-B</span></html>",
                        "Femme",
                        "Effectuée",
                        "⋮"
                },
                {
                        "11:00 AM",
                        "<html><b>Elena Torres</b>"
                                + "<br><span style='font-size:8px; color:#888888;'>ID: 3341-C</span></html>",
                        "Homme",
                        "Retardé (15mn)",
                        "⋮"
                },
                {
                        "14:00 PM",
                        "<html><b>David Wu</b>"
                                + "<br><span style='font-size:8px; color:#888888;'>ID: 7752-D</span></html>",
                        "Homme",
                        "Planifiée",
                        "⋮"
                }
        };

        DefaultTableModel tableModel = new DefaultTableModel(
                visitData,
                columnNames
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable visitTable = new JTable(tableModel);
        visitTable.setRowHeight(42);
        visitTable.setFont(new Font("SansSerif", Font.PLAIN, 9));
        visitTable.setForeground(TEXT_COLOR);
        visitTable.setBackground(Color.WHITE);
        visitTable.setGridColor(BORDER_COLOR);
        visitTable.setShowVerticalLines(false);
        visitTable.setShowHorizontalLines(true);
        visitTable.setRowSelectionAllowed(false);
        visitTable.setFocusable(false);

        visitTable.getColumnModel().getColumn(0).setPreferredWidth(75);
        visitTable.getColumnModel().getColumn(1).setPreferredWidth(155);
        visitTable.getColumnModel().getColumn(2).setPreferredWidth(105);
        visitTable.getColumnModel().getColumn(3).setPreferredWidth(150);
        visitTable.getColumnModel().getColumn(4).setPreferredWidth(55);

        visitTable.setDefaultRenderer(Object.class, new VisitCellRenderer());

        visitTable.getColumnModel().getColumn(3)
                .setCellRenderer(new StatusCellRenderer());

        visitTable.getColumnModel().getColumn(4)
                .setCellRenderer(new ActionCellRenderer());

        JTableHeader tableHeader = visitTable.getTableHeader();
        tableHeader.setFont(new Font("SansSerif", Font.BOLD, 8));
        tableHeader.setForeground(new Color(115, 115, 120));
        tableHeader.setBackground(new Color(253, 251, 251));
        tableHeader.setPreferredSize(new Dimension(0, 30));
        tableHeader.setReorderingAllowed(false);

        JScrollPane scrollPane = new JScrollPane(visitTable);
        scrollPane.setBorder(null);
        scrollPane.getViewport().setBackground(Color.WHITE);

        return scrollPane;
    }

    private class VisitCellRenderer extends DefaultTableCellRenderer {

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
            } else if (status.equals("Retardé (15mn)")) {
                statusLabel.setForeground(new Color(200, 45, 45));
                statusLabel.setBackground(new Color(255, 234, 234));
            } else if (status.equals("Planifiée")) {
                statusLabel.setForeground(new Color(65, 100, 180));
                statusLabel.setBackground(new Color(228, 237, 255));
            } else {
                statusLabel.setForeground(new Color(130, 130, 135));
                statusLabel.setBackground(new Color(244, 242, 243));
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