package com.salamasoa.salamasoa_app.view;

import com.salamasoa.salamasoa_app.model.Patient;
import com.salamasoa.salamasoa_app.service.PatientService;
import com.salamasoa.salamasoa_app.view.Form.PatientFormDialog;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class PatientPanel extends JPanel {

    private static final Color PAGE_BG = Color.WHITE;
    private static final Color PRIMARY_COLOR = new Color(199, 0, 61);
    private static final Color ACTIVE_ROW_COLOR = new Color(255, 232, 236);
    private static final Color HOVER_ROW_COLOR = new Color(255, 232, 236);
    private static final Color ROW_COLOR = new Color(250, 247, 248);
    private static final Color TEXT_COLOR = new Color(45, 45, 48);
    private static final Color SECONDARY_TEXT_COLOR = new Color(120, 120, 125);
    private static final Color BORDER_COLOR = new Color(236, 232, 234);

    private static final Color[] AVATAR_COLORS = {
            new Color(199, 0, 61),
            new Color(156, 80, 110),
            new Color(90, 110, 150),
            new Color(0, 137, 123),
            new Color(180, 110, 70),
            new Color(110, 90, 160)
    };

    private final PatientService patientService;

    private JTable patientTable;
    private DefaultTableModel tableModel;
    private JComboBox<String> statusComboBox;
    private JLabel metaLabel;

    private List<Patient> allPatients = new ArrayList<>();
    private String searchKeyword = "";
    private int hoverRow = -1;

    public PatientPanel(PatientService patientService) {
        this.patientService = patientService;

        setBackground(PAGE_BG);
        setBorder(new EmptyBorder(28, 32, 28, 32));
        setLayout(new BorderLayout(0, 20));

        add(createTopSection(), BorderLayout.NORTH);
        add(createTableSection(), BorderLayout.CENTER);

        loadPatients();
    }

    private JPanel createTopSection() {
        JPanel topSection = new JPanel();
        topSection.setOpaque(false);
        topSection.setLayout(new BoxLayout(topSection, BoxLayout.Y_AXIS));

        JPanel header = createTitleAndButtonPanel();
        header.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel filters = createFiltersPanel();
        filters.setAlignmentX(Component.LEFT_ALIGNMENT);

        topSection.add(header);
        topSection.add(Box.createVerticalStrut(18));
        topSection.add(filters);
        return topSection;
    }

    private JPanel createTitleAndButtonPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout(16, 0));
        headerPanel.setOpaque(false);
        headerPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 72));

        JPanel titles = new JPanel();
        titles.setOpaque(false);
        titles.setLayout(new BoxLayout(titles, BoxLayout.Y_AXIS));

        JLabel titleLabel = new JLabel("Patients");
        titleLabel.setFont(titleLabel.getFont().deriveFont(Font.BOLD, 22f));
        titleLabel.setForeground(TEXT_COLOR);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        metaLabel = new JLabel("Chargement…");
        metaLabel.setFont(metaLabel.getFont().deriveFont(Font.PLAIN, 13f));
        metaLabel.setForeground(SECONDARY_TEXT_COLOR);
        metaLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        titles.add(titleLabel);
        titles.add(Box.createVerticalStrut(4));
        titles.add(metaLabel);

        JButton newPatientButton = new JButton("Nouveau patient");
        newPatientButton.setFont(newPatientButton.getFont().deriveFont(Font.BOLD, 13f));
        newPatientButton.setForeground(Color.WHITE);
        newPatientButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        newPatientButton.setFocusPainted(false);
        newPatientButton.setOpaque(false);
        newPatientButton.setContentAreaFilled(true);
        newPatientButton.setMargin(new Insets(10, 18, 10, 18));
        newPatientButton.putClientProperty("FlatLaf.style",
                "arc: 999;"
                        + "borderWidth: 0;"
                        + "focusWidth: 0;"
                        + "innerFocusWidth: 0;"
                        + "background: #C7003D;"
                        + "hoverBackground: #B00036;"
                        + "pressedBackground: #99002F;"
                        + "foreground: #FFFFFF;"
                        + "hoverForeground: #FFFFFF;"
                        + "pressedForeground: #FFFFFF");

        newPatientButton.addActionListener(event -> {
            Window window = SwingUtilities.getWindowAncestor(this);
            PatientFormDialog dialog = new PatientFormDialog(window);
            dialog.setVisible(true);
            if (dialog.isSaved()) {
                savePatientFromDialog(dialog);
            }
        });

        headerPanel.add(titles, BorderLayout.WEST);
        headerPanel.add(newPatientButton, BorderLayout.EAST);
        return headerPanel;
    }

    private JPanel createFiltersPanel() {
        JPanel filtersPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        filtersPanel.setOpaque(false);
        filtersPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));

        JLabel statusLabel = new JLabel("Statut");
        statusLabel.setFont(statusLabel.getFont().deriveFont(Font.BOLD, 12f));
        statusLabel.setForeground(SECONDARY_TEXT_COLOR);

        statusComboBox = new JComboBox<>(new String[]{
                "Tous les statuts",
                "Actif",
                "Inactif"
        });
        styleComboBox(statusComboBox);
        statusComboBox.addActionListener(event -> displayPatients(allPatients));

        filtersPanel.add(statusLabel);
        filtersPanel.add(statusComboBox);
        return filtersPanel;
    }

    private void styleComboBox(JComboBox<String> comboBox) {
        comboBox.setFont(comboBox.getFont().deriveFont(Font.PLAIN, 13f));
        comboBox.setForeground(TEXT_COLOR);
        comboBox.setPreferredSize(new Dimension(200, 34));
        comboBox.setCursor(new Cursor(Cursor.HAND_CURSOR));
        comboBox.setFocusable(false);
        comboBox.putClientProperty("JComponent.roundRect", true);
        comboBox.putClientProperty("FlatLaf.style",
                "arc: 12;"
                        + "borderWidth: 1;"
                        + "focusWidth: 0;"
                        + "innerFocusWidth: 0;"
                        + "padding: 4,12,4,12;"
                        + "background: #FFFFFF");
    }

    private JPanel createTableSection() {
        String[] columnNames = {
                "Patient",
                "Identifiant",
                "Sexe",
                "Statut",
                ""
        };

        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        patientTable = new JTable(tableModel) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(
                        RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON
                );

                int arc = 16;
                int hInset = 10;
                int vGap = 8;

                for (int row = 0; row < getRowCount(); row++) {
                    Rectangle cell = getCellRect(row, 0, true);
                    boolean selected = isRowSelected(row);
                    g2.setColor(selected
                            ? ACTIVE_ROW_COLOR
                            : row == hoverRow ? HOVER_ROW_COLOR : ROW_COLOR);
                    g2.fillRoundRect(
                            hInset,
                            cell.y + vGap / 2,
                            getWidth() - hInset * 2,
                            cell.height - vGap,
                            arc,
                            arc
                    );
                }
                g2.dispose();
                super.paintComponent(g);
            }

            @Override
            public Component prepareRenderer(
                    TableCellRenderer renderer,
                    int row,
                    int column
            ) {
                Component component = super.prepareRenderer(renderer, row, column);
                if (component instanceof JComponent jComponent) {
                    jComponent.setOpaque(false);
                }
                return component;
            }
        };

        patientTable.setOpaque(false);
        patientTable.setBackground(new Color(0, 0, 0, 0));
        patientTable.setRowHeight(70);
        patientTable.setFont(patientTable.getFont().deriveFont(Font.PLAIN, 13f));
        patientTable.setForeground(TEXT_COLOR);
        patientTable.setShowGrid(false);
        patientTable.setIntercellSpacing(new Dimension(0, 0));
        patientTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        patientTable.setRowSelectionAllowed(true);
        patientTable.setColumnSelectionAllowed(false);
        patientTable.setFillsViewportHeight(true);
        patientTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);

        patientTable.getColumnModel().getColumn(0).setPreferredWidth(280);
        patientTable.getColumnModel().getColumn(1).setPreferredWidth(130);
        patientTable.getColumnModel().getColumn(2).setPreferredWidth(110);
        patientTable.getColumnModel().getColumn(3).setPreferredWidth(110);
        patientTable.getColumnModel().getColumn(4).setPreferredWidth(56);
        patientTable.getColumnModel().getColumn(4).setMaxWidth(64);

        patientTable.setDefaultRenderer(Object.class, new TextCellRenderer());
        patientTable.getColumnModel().getColumn(0).setCellRenderer(new NameCellRenderer());
        patientTable.getColumnModel().getColumn(3).setCellRenderer(new StatusCellRenderer());
        patientTable.getColumnModel().getColumn(4).setCellRenderer(new ActionCellRenderer());

        styleTableHeader(patientTable.getTableHeader());
        configureActionMenu();
        configureHover();

        JScrollPane scrollPane = new JScrollPane(patientTable);
        scrollPane.setBorder(null);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        ShadowCard card = new ShadowCard(18, false);
        JLabel tableTitle = new JLabel("Liste des patients");
        tableTitle.setFont(tableTitle.getFont().deriveFont(Font.BOLD, 14f));
        tableTitle.setForeground(TEXT_COLOR);
        tableTitle.setBorder(new EmptyBorder(4, 18, 10, 8));

        JPanel content = new JPanel(new BorderLayout());
        content.setOpaque(false);
        content.add(tableTitle, BorderLayout.NORTH);
        content.add(scrollPane, BorderLayout.CENTER);

        card.add(content, BorderLayout.CENTER);
        return card;
    }

    private void styleTableHeader(JTableHeader header) {
        header.setPreferredSize(new Dimension(0, 40));
        header.setReorderingAllowed(false);
        header.setOpaque(false);
        header.setBackground(new Color(0, 0, 0, 0));
        header.setDefaultRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(
                    JTable table, Object value, boolean isSelected,
                    boolean hasFocus, int row, int column
            ) {
                super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                setOpaque(false);
                setFont(getFont().deriveFont(Font.BOLD, 11f));
                setForeground(SECONDARY_TEXT_COLOR);
                setBackground(new Color(0, 0, 0, 0));
                setBorder(new EmptyBorder(0, 16, 0, 16));
                setHorizontalAlignment(column == 4 ? SwingConstants.CENTER : SwingConstants.LEFT);
                return this;
            }
        });
    }

    private void configureHover() {
        MouseAdapter hoverAdapter = new MouseAdapter() {
            @Override
            public void mouseMoved(MouseEvent event) {
                int row = patientTable.rowAtPoint(event.getPoint());
                if (row != hoverRow) {
                    hoverRow = row;
                    patientTable.repaint();
                }
            }

            @Override
            public void mouseExited(MouseEvent event) {
                hoverRow = -1;
                patientTable.repaint();
            }
        };
        patientTable.addMouseMotionListener(hoverAdapter);
        patientTable.addMouseListener(hoverAdapter);
    }

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
                    String message = exception.getCause() == null
                            ? exception.getMessage()
                            : exception.getCause().getMessage();
                    JOptionPane.showMessageDialog(
                            PatientPanel.this,
                            "Impossible de charger les patients :\n" + message,
                            "Erreur MySQL",
                            JOptionPane.ERROR_MESSAGE
                    );
                }
            }
        }.execute();
    }

    public void applySearch(String keyword) {
        this.searchKeyword = keyword == null ? "" : keyword.trim();
        displayPatients(allPatients);
    }

    private boolean matchesSearch(Patient patient) {
        if (searchKeyword.isEmpty()) {
            return true;
        }

        String keyword = searchKeyword.toLowerCase();
        String nom = patient.getNom() == null ? "" : patient.getNom().toLowerCase();
        String prenom = patient.getPrenom() == null ? "" : patient.getPrenom().toLowerCase();
        String codepat = patient.getCodepat() == null ? "" : patient.getCodepat().toLowerCase();

        return nom.contains(keyword)
                || prenom.contains(keyword)
                || codepat.contains(keyword);
    }

    private void displayPatients(List<Patient> patients) {
        tableModel.setRowCount(0);

        String selectedStatus = statusComboBox.getSelectedItem().toString();

        for (Patient patient : patients) {
            boolean mustBeDisplayed =
                    selectedStatus.equals("Tous les statuts")
                            || (selectedStatus.equals("Actif") && patient.isActif())
                            || (selectedStatus.equals("Inactif") && !patient.isActif());

            if (!mustBeDisplayed || !matchesSearch(patient)) {
                continue;
            }

            tableModel.addRow(new Object[]{
                    patient,
                    patient.getCodepat(),
                    formatSexe(patient.getSexe()),
                    patient.isActif() ? "Actif" : "Inactif",
                    "⋮"
            });
        }

        updateMetaLabel();
    }

    private void updateMetaLabel() {
        int shown = tableModel.getRowCount();
        int total = allPatients.size();

        if (total == 0) {
            metaLabel.setText("Aucun patient enregistré");
        } else if (shown == total) {
            metaLabel.setText(total + (total > 1 ? " patients" : " patient"));
        } else {
            metaLabel.setText(shown + " sur " + total + " patients");
        }
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
                            "Patient enregistré avec succès.\n\nCode patient : "
                                    + savedPatient.getCodepat(),
                            "Enregistrement réussi",
                            JOptionPane.INFORMATION_MESSAGE
                    );
                    loadPatients();
                } catch (InterruptedException exception) {
                    Thread.currentThread().interrupt();
                } catch (ExecutionException exception) {
                    String message = exception.getCause() == null
                            ? exception.getMessage()
                            : exception.getCause().getMessage();
                    JOptionPane.showMessageDialog(
                            PatientPanel.this,
                            "Impossible d'enregistrer le patient :\n" + message,
                            "Erreur d'enregistrement",
                            JOptionPane.ERROR_MESSAGE
                    );
                }
            }
        }.execute();
    }

    private void configureActionMenu() {
        patientTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent event) {
                int row = patientTable.rowAtPoint(event.getPoint());
                int column = patientTable.columnAtPoint(event.getPoint());
                if (row < 0 || column != 4) {
                    return;
                }

                patientTable.setRowSelectionInterval(row, row);
                String codepat = tableModel.getValueAt(row, 1).toString();
                String status = tableModel.getValueAt(row, 3).toString();
                showPatientActionsMenu(codepat, status, event);
            }
        });
    }

    private void showPatientActionsMenu(String codepat, String status, MouseEvent event) {
        JPopupMenu popupMenu = new JPopupMenu();
        popupMenu.putClientProperty("FlatLaf.style", "arc: 12; borderInsets: 6,8,6,8");

        JMenuItem editItem = new JMenuItem("Modifier");
        JMenuItem toggleStatusItem = new JMenuItem(
                status.equals("Actif") ? "Désactiver" : "Activer"
        );

        editItem.addActionListener(actionEvent -> openEditPatientDialog(codepat));
        toggleStatusItem.addActionListener(actionEvent -> confirmAndToggleStatus(codepat, status));

        popupMenu.add(editItem);
        popupMenu.addSeparator();
        popupMenu.add(toggleStatusItem);
        popupMenu.show(patientTable, event.getX(), event.getY());
    }

    private void openEditPatientDialog(String codepat) {
        Patient patient = allPatients.stream()
                .filter(current -> current.getCodepat().equals(codepat))
                .findFirst()
                .orElse(null);

        if (patient == null) {
            JOptionPane.showMessageDialog(
                    this,
                    "Impossible de retrouver ce patient.",
                    "Patient introuvable",
                    JOptionPane.ERROR_MESSAGE
            );
            return;
        }

        Window window = SwingUtilities.getWindowAncestor(this);
        PatientFormDialog dialog = new PatientFormDialog(window, patient);
        dialog.setVisible(true);
        if (dialog.isSaved()) {
            updatePatientFromDialog(codepat, dialog);
        }
    }

    private void updatePatientFromDialog(String codepat, PatientFormDialog dialog) {
        new SwingWorker<Patient, Void>() {
            @Override
            protected Patient doInBackground() {
                return patientService.updatePatient(
                        codepat,
                        dialog.getFullName(),
                        dialog.getSexe(),
                        dialog.getAddress()
                );
            }

            @Override
            protected void done() {
                try {
                    Patient updatedPatient = get();
                    JOptionPane.showMessageDialog(
                            PatientPanel.this,
                            "Le patient " + updatedPatient.getCodepat()
                                    + " a été mis à jour avec succès.",
                            "Modification réussie",
                            JOptionPane.INFORMATION_MESSAGE
                    );
                    loadPatients();
                } catch (InterruptedException exception) {
                    Thread.currentThread().interrupt();
                } catch (ExecutionException exception) {
                    showActionError("Impossible de modifier le patient.", exception);
                }
            }
        }.execute();
    }

    private void togglePatientStatus(String codepat) {
        new SwingWorker<Patient, Void>() {
            @Override
            protected Patient doInBackground() {
                return patientService.togglePatientStatus(codepat);
            }

            @Override
            protected void done() {
                try {
                    Patient patient = get();
                    String newStatus = patient.isActif() ? "actif" : "inactif";
                    JOptionPane.showMessageDialog(
                            PatientPanel.this,
                            "Le patient " + patient.getCodepat()
                                    + " est maintenant " + newStatus + ".",
                            "Statut mis à jour",
                            JOptionPane.INFORMATION_MESSAGE
                    );
                    loadPatients();
                } catch (InterruptedException exception) {
                    Thread.currentThread().interrupt();
                } catch (ExecutionException exception) {
                    showActionError("Impossible de modifier le statut du patient.", exception);
                }
            }
        }.execute();
    }

    private void confirmAndToggleStatus(String codepat, String status) {
        boolean desactivation = status.equals("Actif");

        if (desactivation) {
            int choice = JOptionPane.showConfirmDialog(
                    this,
                    "Voulez-vous vraiment désactiver le patient "
                            + codepat + " ?\n\n"
                            + "Il n'apparaîtra plus dans les listes de "
                            + "sélection et ne pourra plus recevoir de "
                            + "nouvelle visite.\n"
                            + "Son historique est conservé, et vous pourrez "
                            + "le réactiver à tout moment.",
                    "Confirmation de désactivation",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE
            );

            if (choice != JOptionPane.YES_OPTION) {
                return;
            }
        }

        togglePatientStatus(codepat);
    }

    private void showActionError(String title, ExecutionException exception) {
        String message = exception.getCause() == null
                ? exception.getMessage()
                : exception.getCause().getMessage();
        JOptionPane.showMessageDialog(
                this,
                title + "\n\n" + message,
                "Erreur",
                JOptionPane.ERROR_MESSAGE
        );
    }

    private static String initialsOf(Patient patient) {
        String nom = patient.getNom() == null ? "" : patient.getNom().trim();
        String prenom = patient.getPrenom() == null ? "" : patient.getPrenom().trim();
        String a = nom.isEmpty() ? "" : nom.substring(0, 1);
        String b = prenom.isEmpty() ? "" : prenom.substring(0, 1);
        String initials = (a + b).toUpperCase();
        return initials.isEmpty() ? "?" : initials;
    }

    private static String fullNameOf(Patient patient) {
        String nom = patient.getNom() == null ? "" : patient.getNom();
        String prenom = patient.getPrenom() == null ? "" : patient.getPrenom();
        return (nom + " " + prenom).trim();
    }

    private Color avatarColor(String key) {
        int index = Math.abs(key.hashCode()) % AVATAR_COLORS.length;
        return AVATAR_COLORS[index];
    }

    private class NameCellRenderer implements TableCellRenderer {
        private final JPanel panel = new JPanel(new BorderLayout(12, 0));
        private final JLabel avatar = new JLabel("", SwingConstants.CENTER);
        private final JLabel nameLabel = new JLabel();
        private final JLabel addressLabel = new JLabel();

        NameCellRenderer() {
            panel.setOpaque(false);
            panel.setBorder(new EmptyBorder(8, 16, 8, 12));
            avatar.setPreferredSize(new Dimension(36, 36));
            avatar.setOpaque(false);

            JPanel text = new JPanel();
            text.setOpaque(false);
            text.setLayout(new BoxLayout(text, BoxLayout.Y_AXIS));

            nameLabel.setOpaque(false);
            nameLabel.setFont(nameLabel.getFont().deriveFont(Font.BOLD, 13f));
            nameLabel.setForeground(TEXT_COLOR);
            nameLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

            addressLabel.setOpaque(false);
            addressLabel.setFont(addressLabel.getFont().deriveFont(Font.PLAIN, 11f));
            addressLabel.setForeground(SECONDARY_TEXT_COLOR);
            addressLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

            text.add(nameLabel);
            text.add(Box.createVerticalStrut(2));
            text.add(addressLabel);

            panel.add(avatar, BorderLayout.WEST);
            panel.add(text, BorderLayout.CENTER);
        }

        @Override
        public Component getTableCellRendererComponent(
                JTable table, Object value, boolean isSelected,
                boolean hasFocus, int row, int column
        ) {
            Patient patient = (Patient) value;
            String name = patient == null ? "" : fullNameOf(patient);
            String address = patient == null || patient.getAdresse() == null
                    ? ""
                    : patient.getAdresse();

            nameLabel.setText(name);
            addressLabel.setText(address);
            avatar.setIcon(patient == null
                    ? null
                    : new InitialsIcon(initialsOf(patient), avatarColor(name)));
            return panel;
        }
    }

    private class TextCellRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(
                JTable table, Object value, boolean isSelected,
                boolean hasFocus, int row, int column
        ) {
            super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            setOpaque(false);
            setFont(getFont().deriveFont(Font.PLAIN, 13f));
            setForeground(SECONDARY_TEXT_COLOR);
            setBorder(new EmptyBorder(0, 16, 0, 12));
            return this;
        }
    }

    private class StatusCellRenderer implements TableCellRenderer {
        private final JPanel panel = new JPanel(new GridBagLayout());
        private final PillLabel pill = new PillLabel();

        StatusCellRenderer() {
            panel.setOpaque(false);
            panel.add(pill);
        }

        @Override
        public Component getTableCellRendererComponent(
                JTable table, Object value, boolean isSelected,
                boolean hasFocus, int row, int column
        ) {
            boolean actif = "Actif".equals(String.valueOf(value));
            pill.setText(actif ? "Actif" : "Inactif");
            if (actif) {
                pill.setColors(new Color(0, 120, 110), new Color(220, 245, 240));
            } else {
                pill.setColors(new Color(140, 90, 90), new Color(246, 236, 236));
            }
            return panel;
        }
    }

    private class ActionCellRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(
                JTable table, Object value, boolean isSelected,
                boolean hasFocus, int row, int column
        ) {
            super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            setOpaque(false);
            setText("⋮");
            setHorizontalAlignment(SwingConstants.CENTER);
            setFont(getFont().deriveFont(Font.BOLD, 18f));
            setForeground(SECONDARY_TEXT_COLOR);
            setBorder(new EmptyBorder(0, 0, 0, 8));
            return this;
        }
    }

    private static class ShadowCard extends JPanel {
        private final int arc;

        ShadowCard(int arc, boolean drawBorder) {
            this.arc = arc;
            setOpaque(false);
            setLayout(new BorderLayout());
            setBorder(new EmptyBorder(8, 8, 16, 8));
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            Insets in = getInsets();
            int x = in.left;
            int y = in.top;
            int w = getWidth() - in.left - in.right;
            int h = getHeight() - in.top - in.bottom;

            for (int i = 1; i <= 8; i++) {
                int alpha = Math.max(4, 22 - i * 2);
                g2.setColor(new Color(90, 40, 55, alpha));
                g2.fillRoundRect(x, y + i, w, h, arc, arc);
            }

            g2.setColor(Color.WHITE);
            g2.fillRoundRect(x, y, w, h, arc, arc);

            g2.dispose();
            super.paintComponent(g);
        }

        @Override
        protected void paintChildren(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            Insets in = getInsets();
            g2.clip(new RoundRectangle2D.Float(
                    in.left,
                    in.top,
                    getWidth() - in.left - in.right,
                    getHeight() - in.top - in.bottom,
                    arc,
                    arc
            ));
            super.paintChildren(g2);
            g2.dispose();
        }
    }

    private static class PillLabel extends JLabel {
        private Color pillBackground = Color.LIGHT_GRAY;

        PillLabel() {
            setOpaque(false);
            setFont(getFont().deriveFont(Font.BOLD, 11f));
            setBorder(new EmptyBorder(5, 10, 5, 10));
        }

        void setColors(Color foreground, Color background) {
            setForeground(foreground);
            this.pillBackground = background;
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(pillBackground);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), getHeight(), getHeight());
            g2.dispose();
            super.paintComponent(g);
        }
    }

    private static class InitialsIcon implements Icon {
        private final String initials;
        private final Color background;

        InitialsIcon(String initials, Color background) {
            this.initials = initials;
            this.background = background;
        }

        @Override
        public void paintIcon(Component c, Graphics g, int x, int y) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(background);
            g2.fillOval(x, y, 36, 36);
            g2.setColor(Color.WHITE);
            g2.setFont(c.getFont().deriveFont(Font.BOLD, 12f));
            FontMetrics fm = g2.getFontMetrics();
            int tx = x + (36 - fm.stringWidth(initials)) / 2;
            int ty = y + (36 - fm.getHeight()) / 2 + fm.getAscent();
            g2.drawString(initials, tx, ty);
            g2.dispose();
        }

        @Override
        public int getIconWidth() {
            return 36;
        }

        @Override
        public int getIconHeight() {
            return 36;
        }
    }
}