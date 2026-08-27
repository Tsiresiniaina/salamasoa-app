package com.salamasoa.salamasoa_app.view;

import com.salamasoa.salamasoa_app.model.Medecin;
import com.salamasoa.salamasoa_app.service.MedecinService;
import com.salamasoa.salamasoa_app.view.Form.MedecinFormDialog;

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

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class MedecinPanel extends JPanel {

    private static final Color PRIMARY_COLOR = new Color(199, 0, 61);
    private static final Color ACTIVE_ROW_COLOR = new Color(255, 216, 220);
    private static final Color TEXT_COLOR = new Color(45, 45, 48);
    private static final Color SECONDARY_TEXT_COLOR = new Color(120, 120, 125);
    private static final Color BORDER_COLOR = new Color(238, 238, 240);

    private final MedecinService medecinService;

    private JTable medecinTable;
    private DefaultTableModel tableModel;
    private JComboBox<String> gradeComboBox;

    private List<Medecin> allMedecins = new ArrayList<>();

    /*
     * Mot-clé courant de la barre de recherche du haut.
     * Vide = aucun filtrage par le texte.
     */
    private String searchKeyword = "";

    public MedecinPanel(MedecinService medecinService) {
        this.medecinService = medecinService;

        setBackground(Color.WHITE);
        setBorder(new EmptyBorder(26, 28, 28, 28));
        setLayout(new BorderLayout(0, 18));

        add(createTopSection(), BorderLayout.NORTH);
        add(createTableSection(), BorderLayout.CENTER);

        loadMedecins();
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

            if (!dialog.isSaved()) {
                return;
            }

            saveMedecinFromDialog(dialog);
        });

        headerPanel.add(titleLabel, BorderLayout.WEST);
        headerPanel.add(newDoctorButton, BorderLayout.EAST);

        return headerPanel;
    }

    private void saveMedecinFromDialog(MedecinFormDialog dialog) {
        new SwingWorker<Medecin, Void>() {

            @Override
            protected Medecin doInBackground() {
                return medecinService.createMedecin(
                        dialog.getFullName(),
                        dialog.getGrade()
                );
            }

            @Override
            protected void done() {
                try {
                    Medecin savedMedecin = get();

                    JOptionPane.showMessageDialog(
                            MedecinPanel.this,
                            "Médecin enregistré avec succès.\n\n"
                                    + "Code médecin : "
                                    + savedMedecin.getCodemed(),
                            "Enregistrement réussi",
                            JOptionPane.INFORMATION_MESSAGE
                    );

                    loadMedecins();

                } catch (InterruptedException exception) {
                    Thread.currentThread().interrupt();

                } catch (ExecutionException exception) {
                    String message = exception.getCause() == null
                            ? exception.getMessage()
                            : exception.getCause().getMessage();

                    JOptionPane.showMessageDialog(
                            MedecinPanel.this,
                            "Impossible d'enregistrer le médecin :\n"
                                    + message,
                            "Erreur d'enregistrement",
                            JOptionPane.ERROR_MESSAGE
                    );
                }
            }
        }.execute();
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
                "Médecin chef",
                "Médecin généraliste",
                "Cardiologue",
                "Pédiatre",
                "Chirurgien"
        };

        gradeComboBox = new JComboBox<>(grades);
        styleComboBox(gradeComboBox);

        gradeComboBox.addActionListener(event ->
                displayMedecins(allMedecins)
        );

        filterPanel.add(gradeLabel);
        filterPanel.add(gradeComboBox);

        return filterPanel;
    }

    private void styleComboBox(JComboBox<String> comboBox) {
        comboBox.setFont(new Font("SansSerif", Font.PLAIN, 10));
        comboBox.setForeground(TEXT_COLOR);
        comboBox.setBackground(Color.WHITE);
        comboBox.setPreferredSize(new Dimension(170, 28));
        comboBox.setBorder(new LineBorder(BORDER_COLOR, 1, true));
        comboBox.setCursor(new Cursor(Cursor.HAND_CURSOR));
        comboBox.setFocusable(false);
    }

    private JScrollPane createTableSection() {
        String[] columnNames = {
                "NOM DU MÉDECIN",
                "ID",
                "GRADE",
                "STATUT",
                "ACTION"
        };

        tableModel = new DefaultTableModel(columnNames, 0) {

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

        medecinTable.getColumnModel().getColumn(0).setPreferredWidth(240);
        medecinTable.getColumnModel().getColumn(1).setPreferredWidth(120);
        medecinTable.getColumnModel().getColumn(2).setPreferredWidth(190);
        medecinTable.getColumnModel().getColumn(3).setPreferredWidth(120);
        medecinTable.getColumnModel().getColumn(4).setPreferredWidth(80);

        medecinTable.setDefaultRenderer(Object.class, new MedecinCellRenderer());

        medecinTable.getColumnModel().getColumn(3)
                .setCellRenderer(new StatusCellRenderer());

        medecinTable.getColumnModel().getColumn(4)
                .setCellRenderer(new ActionCellRenderer());

        configureActionMenu();

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

    /**
     * Charge les médecins MySQL en arrière-plan.
     */
    public void loadMedecins() {
        new SwingWorker<List<Medecin>, Void>() {

            @Override
            protected List<Medecin> doInBackground() {
                return medecinService.getAllMedecins();
            }

            @Override
            protected void done() {
                try {
                    allMedecins = get();
                    displayMedecins(allMedecins);

                } catch (InterruptedException exception) {
                    Thread.currentThread().interrupt();

                } catch (ExecutionException exception) {
                    String message = exception.getCause() == null
                            ? exception.getMessage()
                            : exception.getCause().getMessage();

                    JOptionPane.showMessageDialog(
                            MedecinPanel.this,
                            "Impossible de charger les médecins :\n"
                                    + message,
                            "Erreur MySQL",
                            JOptionPane.ERROR_MESSAGE
                    );
                }
            }
        }.execute();
    }

    /**
     * Applique le mot-clé de recherche venant de la barre du haut.
     *
     * Le filtrage se fait en mémoire sur les médecins déjà chargés :
     * pas de requête à la base à chaque frappe. Le résultat se combine
     * avec le filtre par grade du panneau.
     */
    public void applySearch(String keyword) {
        this.searchKeyword = keyword == null ? "" : keyword.trim();
        displayMedecins(allMedecins);
    }

    /**
     * Indique si un médecin correspond au mot-clé saisi.
     *
     * La recherche porte sur le nom, le prénom, le code médecin et le
     * grade, sans tenir compte de la casse.
     */
    private boolean matchesSearch(Medecin medecin) {
        if (searchKeyword.isEmpty()) {
            return true;
        }

        String keyword = searchKeyword.toLowerCase();

        String nom = medecin.getNom() == null
                ? ""
                : medecin.getNom().toLowerCase();

        String prenom = medecin.getPrenom() == null
                ? ""
                : medecin.getPrenom().toLowerCase();

        String codemed = medecin.getCodemed() == null
                ? ""
                : medecin.getCodemed().toLowerCase();

        String grade = medecin.getGrade() == null
                ? ""
                : medecin.getGrade().toLowerCase();

        return nom.contains(keyword)
                || prenom.contains(keyword)
                || codemed.contains(keyword)
                || grade.contains(keyword);
    }

    /**
     * Affiche les médecins selon le grade sélectionné.
     */
    private void displayMedecins(List<Medecin> medecins) {
        tableModel.setRowCount(0);

        String selectedGrade =
                gradeComboBox.getSelectedItem().toString();

        for (Medecin medecin : medecins) {
            boolean mustBeDisplayed =
                    selectedGrade.equals("Tous les grades")
                            || medecin.getGrade()
                            .equalsIgnoreCase(selectedGrade);

            // Le filtre de grade et la recherche s'appliquent ensemble.
            if (!mustBeDisplayed || !matchesSearch(medecin)) {
                continue;
            }

            tableModel.addRow(new Object[]{
                    formatMedecinName(medecin),
                    medecin.getCodemed(),
                    medecin.getGrade(),
                    medecin.isActif() ? "Actif" : "Inactif",
                    "⋮"
            });
        }

        if (tableModel.getRowCount() > 0) {
            medecinTable.setRowSelectionInterval(0, 0);
        }
    }

    private String formatMedecinName(Medecin medecin) {
        String nom = medecin.getNom() == null ? "" : medecin.getNom();
        String prenom = medecin.getPrenom() == null
                ? ""
                : medecin.getPrenom();

        String fullName = (nom + " " + prenom).trim();

        return "<html><b>"
                + fullName
                + "</b><br><span style='font-size:9px; color:#777777;'>"
                + medecin.getGrade()
                + "</span></html>";
    }

    private void configureActionMenu() {
        medecinTable.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseClicked(MouseEvent event) {
                int row = medecinTable.rowAtPoint(event.getPoint());
                int column = medecinTable.columnAtPoint(event.getPoint());

                /*
                 * Colonnes :
                 * NOM, ID, GRADE, STATUT, ACTION
                 */
                if (row < 0 || column != 4) {
                    return;
                }

                medecinTable.setRowSelectionInterval(row, row);

                String codemed = tableModel.getValueAt(row, 1).toString();
                String status = tableModel.getValueAt(row, 3).toString();

                showMedecinActionsMenu(codemed, status, event);
            }
        });
    }

    private void showMedecinActionsMenu(
            String codemed,
            String status,
            MouseEvent event
    ) {
        JPopupMenu popupMenu = new JPopupMenu();

        JMenuItem editItem = new JMenuItem("Modifier");

        JMenuItem toggleStatusItem = new JMenuItem(
                status.equals("Actif") ? "Désactiver" : "Activer"
        );

        editItem.addActionListener(actionEvent ->
                openEditMedecinDialog(codemed)
        );

        toggleStatusItem.addActionListener(actionEvent ->
                toggleMedecinStatus(codemed)
        );


        popupMenu.add(editItem);
        popupMenu.addSeparator();
        popupMenu.add(toggleStatusItem);

        popupMenu.show(
                medecinTable,
                event.getX(),
                event.getY()
        );
    }
    private void openEditMedecinDialog(String codemed) {
        /*
         * Retrouve le médecin réel correspondant à la ligne
         * sélectionnée dans le tableau.
         */
        Medecin medecin = allMedecins.stream()
                .filter(currentMedecin ->
                        currentMedecin.getCodemed().equals(codemed)
                )
                .findFirst()
                .orElse(null);

        if (medecin == null) {
            JOptionPane.showMessageDialog(
                    this,
                    "Impossible de retrouver ce médecin.",
                    "Médecin introuvable",
                    JOptionPane.ERROR_MESSAGE
            );
            return;
        }

        Window window = SwingUtilities.getWindowAncestor(this);

        /*
         * Le second paramètre active le mode modification
         * et préremplit les données du médecin.
         */
        MedecinFormDialog dialog =
                new MedecinFormDialog(window, medecin);

        dialog.setVisible(true);

        if (!dialog.isSaved()) {
            return;
        }

        updateMedecinFromDialog(codemed, dialog);
    }

    private void updateMedecinFromDialog(
            String codemed,
            MedecinFormDialog dialog
    ) {
        new SwingWorker<Medecin, Void>() {

            @Override
            protected Medecin doInBackground() {
                return medecinService.updateMedecin(
                        codemed,
                        dialog.getFullName(),
                        dialog.getGrade()
                );
            }

            @Override
            protected void done() {
                try {
                    Medecin updatedMedecin = get();

                    JOptionPane.showMessageDialog(
                            MedecinPanel.this,
                            "Le médecin "
                                    + updatedMedecin.getCodemed()
                                    + " a été mis à jour avec succès.",
                            "Modification réussie",
                            JOptionPane.INFORMATION_MESSAGE
                    );

                    loadMedecins();

                } catch (InterruptedException exception) {
                    Thread.currentThread().interrupt();

                } catch (ExecutionException exception) {
                    showMedecinActionError(
                            "Impossible de modifier le médecin.",
                            exception
                    );
                }
            }
        }.execute();
    }
    private void toggleMedecinStatus(String codemed) {
        new SwingWorker<Medecin, Void>() {

            @Override
            protected Medecin doInBackground() {
                return medecinService.toggleMedecinStatus(codemed);
            }

            @Override
            protected void done() {
                try {
                    Medecin medecin = get();

                    String newStatus = medecin.isActif()
                            ? "actif"
                            : "inactif";

                    JOptionPane.showMessageDialog(
                            MedecinPanel.this,
                            "Le médecin " + medecin.getCodemed()
                                    + " est maintenant " + newStatus + ".",
                            "Statut mis à jour",
                            JOptionPane.INFORMATION_MESSAGE
                    );

                    loadMedecins();

                } catch (InterruptedException exception) {
                    Thread.currentThread().interrupt();

                } catch (ExecutionException exception) {
                    showMedecinActionError(
                            "Impossible de modifier le statut du médecin.",
                            exception
                    );
                }
            }
        }.execute();
    }

    private void showMedecinActionError(
            String title,
            ExecutionException exception
    ) {
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