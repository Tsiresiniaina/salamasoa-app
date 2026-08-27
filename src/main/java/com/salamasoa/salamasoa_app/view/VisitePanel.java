package com.salamasoa.salamasoa_app.view;

import com.salamasoa.salamasoa_app.model.Medecin;
import com.salamasoa.salamasoa_app.model.Patient;
import com.salamasoa.salamasoa_app.model.StatutVisite;
import com.salamasoa.salamasoa_app.model.Visite;
import com.salamasoa.salamasoa_app.service.MedecinService;
import com.salamasoa.salamasoa_app.service.PatientService;
import com.salamasoa.salamasoa_app.service.VisiteService;
import com.salamasoa.salamasoa_app.view.Form.VisiteFormDialog;

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
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

public class VisitePanel extends JPanel {

    private static final Color PAGE_BG = new Color(250, 247, 248);
    private static final Color CARD_BG = Color.WHITE;
    private static final Color PRIMARY_COLOR = new Color(199, 0, 61);
    private static final Color HOVER_ROW_COLOR = new Color(255, 232, 236);
    private static final Color ROW_COLOR = new Color(250, 247, 248);
    private static final Color TEXT_COLOR = new Color(42, 42, 45);
    private static final Color SECONDARY_TEXT_COLOR = new Color(125, 125, 130);
    private static final Color BORDER_COLOR = new Color(236, 232, 234);

    private static final Color[] AVATAR_COLORS = {
            new Color(199, 0, 61),
            new Color(156, 80, 110),
            new Color(90, 110, 150),
            new Color(0, 137, 123),
            new Color(180, 110, 70),
            new Color(110, 90, 160)
    };

    private static final DateTimeFormatter TIME_FORMATTER =
            DateTimeFormatter.ofPattern("HH:mm");

    private final VisiteService visiteService;
    private final PatientService patientService;
    private final MedecinService medecinService;

    private JSpinner dateSpinner;
    private JTable visiteTable;
    private DefaultTableModel tableModel;

    private JLabel totalVisitsValueLabel;
    private JLabel waitingValueLabel;
    private JLabel completedValueLabel;

    private int visiteRequestCounter = 0;
    private int statsRequestCounter = 0;
    private boolean loading = false;
    private int hoverRow = -1;

    private final List<Visite> displayedVisites = new ArrayList<>();

    public VisitePanel(
            VisiteService _visiteService,
            PatientService _patientService,
            MedecinService _medecinService
    ) {
        this.visiteService = _visiteService;
        this.patientService = _patientService;
        this.medecinService = _medecinService;

        setBackground(PAGE_BG);
        setBorder(new EmptyBorder(24, 28, 28, 28));
        setLayout(new BorderLayout(0, 8));

        add(createTopSection(), BorderLayout.NORTH);
        add(createVisitsSection(), BorderLayout.CENTER);

        loadVisitesForSelectedDate();
        refreshStatisticsToday();
    }

    private JPanel createTopSection() {
        JPanel topSection = new JPanel();
        topSection.setOpaque(false);
        topSection.setLayout(new BoxLayout(topSection, BoxLayout.Y_AXIS));

        JPanel header = createHeaderPanel();
        header.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel stats = createStatisticsPanel();
        stats.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel filter = createDateFilterPanel();
        filter.setAlignmentX(Component.LEFT_ALIGNMENT);

        topSection.add(header);
        topSection.add(Box.createVerticalStrut(8));
        topSection.add(stats);
        topSection.add(filter);
        return topSection;
    }

    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout(16, 0));
        headerPanel.setOpaque(false);
        headerPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 72));

        JPanel titlePanel = new JPanel();
        titlePanel.setOpaque(false);
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));

        JLabel titleLabel = new JLabel("Visites");
        titleLabel.setFont(titleLabel.getFont().deriveFont(Font.BOLD, 22f));
        titleLabel.setForeground(TEXT_COLOR);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel subtitleLabel = new JLabel(
                "Planifiez et suivez les consultations de la journée"
        );
        subtitleLabel.setFont(subtitleLabel.getFont().deriveFont(Font.PLAIN, 13f));
        subtitleLabel.setForeground(SECONDARY_TEXT_COLOR);
        subtitleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        titlePanel.add(titleLabel);
        titlePanel.add(Box.createVerticalStrut(4));
        titlePanel.add(subtitleLabel);

        JButton newVisitButton = new JButton("Nouvelle visite");
        newVisitButton.setFont(newVisitButton.getFont().deriveFont(Font.BOLD, 13f));
        newVisitButton.setForeground(Color.WHITE);
        newVisitButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        newVisitButton.setFocusPainted(false);
        newVisitButton.setOpaque(false);
        newVisitButton.setContentAreaFilled(true);
        newVisitButton.setMargin(new Insets(10, 18, 10, 18));
        newVisitButton.putClientProperty("FlatLaf.style",
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
        newVisitButton.addActionListener(event -> openNewVisiteDialog());

        headerPanel.add(titlePanel, BorderLayout.WEST);
        headerPanel.add(newVisitButton, BorderLayout.EAST);
        return headerPanel;
    }

    private JPanel createStatisticsPanel() {
        JPanel statisticsPanel = new JPanel(new GridLayout(1, 3, 4, 0));
        statisticsPanel.setOpaque(false);
        statisticsPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 132));
        statisticsPanel.setPreferredSize(new Dimension(0, 132));

        totalVisitsValueLabel = new JLabel("0");
        waitingValueLabel = new JLabel("0");
        completedValueLabel = new JLabel("0");

        statisticsPanel.add(createStatisticCard(
                "Visites du jour",
                totalVisitsValueLabel,
                "▣",
                new Color(255, 228, 235),
                PRIMARY_COLOR,
                true
        ));
        statisticsPanel.add(createStatisticCard(
                "Salle d'attente",
                waitingValueLabel,
                "◷",
                new Color(243, 238, 241),
                new Color(115, 95, 105),
                false
        ));
        statisticsPanel.add(createStatisticCard(
                "Effectuées",
                completedValueLabel,
                "◎",
                new Color(220, 245, 240),
                new Color(0, 120, 110),
                false
        ));

        return statisticsPanel;
    }

    private JPanel createStatisticCard(
            String title,
            JLabel valueLabel,
            String icon,
            Color iconBackground,
            Color iconColor,
            boolean accent
    ) {
        ShadowCard card = new ShadowCard(18, accent, true);

        JPanel inner = new JPanel(new BorderLayout(8, 0));
        inner.setOpaque(false);
        inner.setBorder(new EmptyBorder(6, 8, 6, 6));

        JPanel textPanel = new JPanel();
        textPanel.setOpaque(false);
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));

        JLabel titleLabel = new JLabel(title.toUpperCase());
        titleLabel.setFont(titleLabel.getFont().deriveFont(Font.BOLD, 11f));
        titleLabel.setForeground(SECONDARY_TEXT_COLOR);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        valueLabel.setFont(valueLabel.getFont().deriveFont(Font.BOLD, 28f));
        valueLabel.setForeground(accent ? PRIMARY_COLOR : TEXT_COLOR);
        valueLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        textPanel.add(titleLabel);
        textPanel.add(Box.createVerticalStrut(10));
        textPanel.add(valueLabel);

        inner.add(textPanel, BorderLayout.CENTER);
        inner.add(new IconBadge(icon, iconBackground, iconColor), BorderLayout.EAST);
        card.add(inner, BorderLayout.CENTER);
        return card;
    }

    private JPanel createDateFilterPanel() {
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        filterPanel.setOpaque(false);
        filterPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));

        JLabel filterLabel = new JLabel("Date");
        filterLabel.setFont(filterLabel.getFont().deriveFont(Font.BOLD, 12f));
        filterLabel.setForeground(SECONDARY_TEXT_COLOR);

        dateSpinner = new JSpinner(new SpinnerDateModel(
                new Date(), null, null, java.util.Calendar.DAY_OF_MONTH
        ));
        dateSpinner.setFont(dateSpinner.getFont().deriveFont(Font.PLAIN, 13f));
        dateSpinner.setPreferredSize(new Dimension(148, 34));
        dateSpinner.setEditor(new JSpinner.DateEditor(dateSpinner, "dd/MM/yyyy"));
        dateSpinner.putClientProperty("JComponent.roundRect", true);
        dateSpinner.putClientProperty("FlatLaf.style",
                "arc: 12; focusWidth: 0; innerFocusWidth: 0; borderWidth: 1");
        dateSpinner.addChangeListener(event -> loadVisitesForSelectedDate());

        JButton todayButton = new JButton("Aujourd'hui");
        todayButton.setFont(todayButton.getFont().deriveFont(Font.BOLD, 12f));
        todayButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        todayButton.setFocusPainted(false);
        todayButton.setOpaque(false);
        todayButton.setContentAreaFilled(true);
        todayButton.setMargin(new Insets(6, 14, 6, 14));
        todayButton.putClientProperty("FlatLaf.style",
                "arc: 12;"
                        + "borderWidth: 1;"
                        + "focusWidth: 0;"
                        + "innerFocusWidth: 0;"
                        + "background: #FFFFFF;"
                        + "hoverBackground: #FADDE6;"
                        + "pressedBackground: #F5C6D2;"
                        + "foreground: #C7003D;"
                        + "hoverForeground: #C7003D;"
                        + "pressedForeground: #C7003D");
        todayButton.addActionListener(event -> dateSpinner.setValue(new Date()));

        filterPanel.add(filterLabel);
        filterPanel.add(dateSpinner);
        filterPanel.add(todayButton);
        return filterPanel;
    }

    private JPanel createVisitsSection() {
        ShadowCard card = new ShadowCard(18, false, false);

        JLabel tableTitle = new JLabel("Liste des visites");
        tableTitle.setFont(tableTitle.getFont().deriveFont(Font.BOLD, 14f));
        tableTitle.setForeground(TEXT_COLOR);
        tableTitle.setBorder(new EmptyBorder(4, 18, 10, 8));

        JPanel content = new JPanel(new BorderLayout());
        content.setOpaque(false);
        content.add(tableTitle, BorderLayout.NORTH);
        content.add(createVisitsTable(), BorderLayout.CENTER);

        card.add(content, BorderLayout.CENTER);
        return card;
    }

    private JScrollPane createVisitsTable() {
        String[] columnNames = {
                "Heure",
                "Patient",
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

        visiteTable = new JTable(tableModel) {
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
                    g2.setColor(row == hoverRow ? HOVER_ROW_COLOR : ROW_COLOR);
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

        visiteTable.setOpaque(false);
        visiteTable.setBackground(new Color(0, 0, 0, 0));
        visiteTable.setRowHeight(70);
        visiteTable.setFont(visiteTable.getFont().deriveFont(Font.PLAIN, 13f));
        visiteTable.setForeground(TEXT_COLOR);
        visiteTable.setShowGrid(false);
        visiteTable.setIntercellSpacing(new Dimension(0, 0));
        visiteTable.setRowSelectionAllowed(false);
        visiteTable.setFocusable(false);
        visiteTable.setFillsViewportHeight(true);
        visiteTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);

        visiteTable.getColumnModel().getColumn(0).setPreferredWidth(80);
        visiteTable.getColumnModel().getColumn(1).setPreferredWidth(260);
        visiteTable.getColumnModel().getColumn(2).setPreferredWidth(100);
        visiteTable.getColumnModel().getColumn(3).setPreferredWidth(130);
        visiteTable.getColumnModel().getColumn(4).setPreferredWidth(56);
        visiteTable.getColumnModel().getColumn(4).setMaxWidth(64);

        visiteTable.setDefaultRenderer(Object.class, new TextCellRenderer());
        visiteTable.getColumnModel().getColumn(0).setCellRenderer(new TimeCellRenderer());
        visiteTable.getColumnModel().getColumn(1).setCellRenderer(new NameCellRenderer());
        visiteTable.getColumnModel().getColumn(3).setCellRenderer(new StatusCellRenderer());
        visiteTable.getColumnModel().getColumn(4).setCellRenderer(new ActionCellRenderer());

        styleTableHeader(visiteTable.getTableHeader());
        configureActionMenu();
        configureHover();

        JScrollPane scrollPane = new JScrollPane(visiteTable);
        scrollPane.setBorder(null);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        return scrollPane;
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
                int row = visiteTable.rowAtPoint(event.getPoint());
                if (row != hoverRow) {
                    hoverRow = row;
                    visiteTable.repaint();
                }
            }

            @Override
            public void mouseExited(MouseEvent event) {
                hoverRow = -1;
                visiteTable.repaint();
            }
        };
        visiteTable.addMouseMotionListener(hoverAdapter);
        visiteTable.addMouseListener(hoverAdapter);
    }

    private void setLoading(boolean value) {
        this.loading = value;
        setCursor(value
                ? Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR)
                : Cursor.getDefaultCursor());
    }

    private void openNewVisiteDialog() {
        setLoading(true);
        new SwingWorker<VisiteFormLists, Void>() {
            @Override
            protected VisiteFormLists doInBackground() {
                List<Patient> patients = patientService.getAllPatients().stream()
                        .filter(Patient::isActif)
                        .collect(Collectors.toList());
                List<Medecin> medecins = medecinService.getAllMedecins().stream()
                        .filter(Medecin::isActif)
                        .collect(Collectors.toList());
                return new VisiteFormLists(patients, medecins);
            }

            @Override
            protected void done() {
                setLoading(false);
                try {
                    VisiteFormLists lists = get();
                    VisiteFormDialog dialog = new VisiteFormDialog(
                            (Frame) SwingUtilities.getWindowAncestor(VisitePanel.this),
                            lists.patients(),
                            lists.medecins()
                    );
                    dialog.setVisible(true);
                    if (dialog.isSaved()) {
                        enregistrerVisite(dialog);
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(
                            VisitePanel.this,
                            "Impossible de charger les médecins / patients.",
                            "Erreur",
                            JOptionPane.ERROR_MESSAGE
                    );
                }
            }
        }.execute();
    }

    private void enregistrerVisite(VisiteFormDialog dialog) {
        Patient patient = dialog.getSelectedPatient();
        Medecin medecin = dialog.getSelectedMedecin();
        LocalDateTime dateHeure = dialog.getDateHeureVisite();

        if (patient == null || medecin == null || dateHeure == null) {
            JOptionPane.showMessageDialog(
                    VisitePanel.this,
                    "Veuillez renseigner le patient, le médecin, la date et l'heure.",
                    "Formulaire incomplet",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        new SwingWorker<Boolean, Void>() {
            @Override
            protected Boolean doInBackground() {
                try {
                    visiteService.createVisite(
                            patient.getCodepat(),
                            medecin.getCodemed(),
                            dateHeure
                    );
                    return true;
                } catch (IllegalArgumentException ex) {
                    return false;
                } catch (Exception ex) {
                    throw new RuntimeException(ex);
                }
            }

            @Override
            protected void done() {
                try {
                    Boolean success = get();
                    if (Boolean.TRUE.equals(success)) {
                        JOptionPane.showMessageDialog(
                                VisitePanel.this,
                                "Visite enregistrée avec succès.",
                                "Succès",
                                JOptionPane.INFORMATION_MESSAGE
                        );
                    } else {
                        JOptionPane.showMessageDialog(
                                VisitePanel.this,
                                "Enregistrement impossible : le médecin est déjà "
                                        + "occupé à cet horaire, ou le patient est inactif.",
                                "Enregistrement refusé",
                                JOptionPane.WARNING_MESSAGE
                        );
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(
                            VisitePanel.this,
                            "Erreur lors de l'enregistrement de la visite : " + ex.getMessage(),
                            "Erreur",
                            JOptionPane.ERROR_MESSAGE
                    );
                } finally {
                    dateSpinner.setValue(Date.from(
                            dateHeure.atZone(ZoneId.systemDefault()).toInstant()
                    ));
                    loadVisitesForSelectedDate();
                    refreshStatisticsToday();
                }
            }
        }.execute();
    }

    private record VisiteFormLists(List<Patient> patients, List<Medecin> medecins) {
    }

    private void loadVisitesForSelectedDate() {
        LocalDate selectedDate = convertDateToLocalDate((Date) dateSpinner.getValue());
        int requestId = ++visiteRequestCounter;

        new SwingWorker<List<Visite>, Void>() {
            @Override
            protected List<Visite> doInBackground() {
                return visiteService.getVisitesByDate(selectedDate);
            }

            @Override
            protected void done() {
                if (requestId != visiteRequestCounter) {
                    return;
                }
                try {
                    displayVisites(get());
                } catch (InterruptedException exception) {
                    Thread.currentThread().interrupt();
                } catch (ExecutionException exception) {
                    String message = exception.getCause() == null
                            ? exception.getMessage()
                            : exception.getCause().getMessage();
                    JOptionPane.showMessageDialog(
                            VisitePanel.this,
                            "Impossible de charger les visites :\n" + message,
                            "Erreur MySQL",
                            JOptionPane.ERROR_MESSAGE
                    );
                }
            }
        }.execute();
    }

    private void displayVisites(List<Visite> visites) {
        tableModel.setRowCount(0);
        displayedVisites.clear();
        displayedVisites.addAll(visites);

        for (Visite visite : visites) {
            tableModel.addRow(new Object[]{
                    visite.getDateheure().format(TIME_FORMATTER),
                    visite,
                    formatSexe(visite),
                    visite.getStatut().getLibelle(),
                    "⋮"
            });
        }
    }

    private void configureActionMenu() {
        visiteTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent event) {
                int row = visiteTable.rowAtPoint(event.getPoint());
                int column = visiteTable.columnAtPoint(event.getPoint());
                if (row < 0 || column != 4 || row >= displayedVisites.size()) {
                    return;
                }
                showVisiteActionsMenu(displayedVisites.get(row), event);
            }
        });
    }

    private void showVisiteActionsMenu(Visite visite, MouseEvent event) {
        JPopupMenu popupMenu = new JPopupMenu();
        popupMenu.putClientProperty("FlatLaf.style", "arc: 12; borderInsets: 6,8,6,8");

        StatutVisite statut = visite.getStatut();
        boolean modifiable = statut == StatutVisite.PLANIFIEE;

        JMenuItem editItem = new JMenuItem("Modifier la visite");
        editItem.setEnabled(modifiable);
        editItem.addActionListener(actionEvent -> openEditVisiteDialog(visite));
        if (!modifiable) {
            editItem.setToolTipText(
                    statut == StatutVisite.EN_COURS
                            ? "La consultation a déjà commencé"
                            : "Visite " + statut.getLibelle().toLowerCase()
            );
        }

        popupMenu.add(editItem);
        popupMenu.addSeparator();

        if (statut == StatutVisite.PLANIFIEE) {
            popupMenu.add(createStatusMenuItem(
                    "Démarrer la consultation", visite, StatutVisite.EN_COURS
            ));
        }
        if (statut == StatutVisite.EN_COURS) {
            popupMenu.add(createStatusMenuItem(
                    "Marquer comme terminée", visite, StatutVisite.TERMINEE
            ));
        }
        if (statut == StatutVisite.PLANIFIEE || statut == StatutVisite.EN_COURS) {
            popupMenu.add(createStatusMenuItem(
                    "Annuler la visite", visite, StatutVisite.ANNULEE
            ));
        }
        if (statut == StatutVisite.TERMINEE || statut == StatutVisite.ANNULEE) {
            JMenuItem infoItem = new JMenuItem("Visite " + statut.getLibelle().toLowerCase());
            infoItem.setEnabled(false);
            popupMenu.add(infoItem);
        }

        popupMenu.show(visiteTable, event.getX(), event.getY());
    }

    private JMenuItem createStatusMenuItem(String label, Visite visite, StatutVisite nouveauStatut) {
        JMenuItem menuItem = new JMenuItem(label);
        menuItem.addActionListener(actionEvent -> {
            if (nouveauStatut == StatutVisite.ANNULEE) {
                int choice = JOptionPane.showConfirmDialog(
                        VisitePanel.this,
                        "Voulez-vous vraiment annuler la visite de "
                                + getPatientDisplayName(visite) + " ?\n\n"
                                + "Cette action est définitive.",
                        "Confirmation d'annulation",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.WARNING_MESSAGE
                );
                if (choice != JOptionPane.YES_OPTION) {
                    return;
                }
            }
            changeVisiteStatus(visite, nouveauStatut);
        });
        return menuItem;
    }

    private void changeVisiteStatus(Visite visite, StatutVisite nouveauStatut) {
        setLoading(true);
        new SwingWorker<Visite, Void>() {
            @Override
            protected Visite doInBackground() {
                return visiteService.updateVisiteStatus(visite.getCodevisite(), nouveauStatut);
            }

            @Override
            protected void done() {
                setLoading(false);
                try {
                    get();
                    JOptionPane.showMessageDialog(
                            VisitePanel.this,
                            "La visite de " + getPatientDisplayName(visite)
                                    + " est maintenant : " + nouveauStatut.getLibelle() + ".",
                            "Statut mis à jour",
                            JOptionPane.INFORMATION_MESSAGE
                    );
                    loadVisitesForSelectedDate();
                    refreshStatisticsToday();
                } catch (InterruptedException exception) {
                    Thread.currentThread().interrupt();
                } catch (ExecutionException exception) {
                    showActionError("Impossible de changer le statut de la visite.", exception);
                }
            }
        }.execute();
    }

    private void openEditVisiteDialog(Visite visite) {
        setLoading(true);
        new SwingWorker<VisiteFormLists, Void>() {
            @Override
            protected VisiteFormLists doInBackground() {
                List<Patient> patients = patientService.getAllPatients().stream()
                        .filter(Patient::isActif)
                        .collect(Collectors.toList());
                List<Medecin> medecins = medecinService.getAllMedecins().stream()
                        .filter(Medecin::isActif)
                        .collect(Collectors.toList());

                if (visite.getPatient() != null
                        && patients.stream().noneMatch(patient ->
                        patient.getCodepat().equals(visite.getPatient().getCodepat()))) {
                    patients.add(visite.getPatient());
                }
                if (visite.getMedecin() != null
                        && medecins.stream().noneMatch(medecin ->
                        medecin.getCodemed().equals(visite.getMedecin().getCodemed()))) {
                    medecins.add(visite.getMedecin());
                }

                return new VisiteFormLists(patients, medecins);
            }

            @Override
            protected void done() {
                setLoading(false);
                try {
                    VisiteFormLists lists = get();
                    VisiteFormDialog dialog = new VisiteFormDialog(
                            SwingUtilities.getWindowAncestor(VisitePanel.this),
                            lists.patients(),
                            lists.medecins(),
                            visite
                    );
                    dialog.setVisible(true);
                    if (dialog.isSaved()) {
                        updateVisiteFromDialog(visite, dialog);
                    }
                } catch (InterruptedException exception) {
                    Thread.currentThread().interrupt();
                } catch (ExecutionException exception) {
                    showActionError("Impossible de charger les médecins / patients.", exception);
                }
            }
        }.execute();
    }

    private void updateVisiteFromDialog(Visite visite, VisiteFormDialog dialog) {
        Patient patient = dialog.getSelectedPatient();
        Medecin medecin = dialog.getSelectedMedecin();
        LocalDateTime dateHeure = dialog.getDateHeureVisite();

        if (patient == null || medecin == null || dateHeure == null) {
            JOptionPane.showMessageDialog(
                    VisitePanel.this,
                    "Veuillez renseigner le patient, le médecin, la date et l'heure.",
                    "Formulaire incomplet",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        setLoading(true);
        new SwingWorker<Visite, Void>() {
            @Override
            protected Visite doInBackground() {
                return visiteService.updateVisite(
                        visite.getCodevisite(),
                        patient.getCodepat(),
                        medecin.getCodemed(),
                        dateHeure
                );
            }

            @Override
            protected void done() {
                setLoading(false);
                try {
                    get();
                    JOptionPane.showMessageDialog(
                            VisitePanel.this,
                            "La visite " + visite.getCodevisite()
                                    + " a été modifiée avec succès.",
                            "Modification réussie",
                            JOptionPane.INFORMATION_MESSAGE
                    );
                    dateSpinner.setValue(Date.from(
                            dateHeure.atZone(ZoneId.systemDefault()).toInstant()
                    ));
                    loadVisitesForSelectedDate();
                    refreshStatisticsToday();
                } catch (InterruptedException exception) {
                    Thread.currentThread().interrupt();
                } catch (ExecutionException exception) {
                    showActionError("Impossible de modifier la visite.", exception);
                }
            }
        }.execute();
    }

    private void showActionError(String title, ExecutionException exception) {
        String message = exception.getCause() == null
                ? exception.getMessage()
                : exception.getCause().getMessage();
        JOptionPane.showMessageDialog(this, title + "\n\n" + message, "Erreur", JOptionPane.ERROR_MESSAGE);
    }

    private String getPatientDisplayName(Visite visite) {
        if (visite.getPatient() == null) {
            return visite.getCodevisite();
        }
        String nom = visite.getPatient().getNom() == null ? "" : visite.getPatient().getNom();
        String prenom = visite.getPatient().getPrenom() == null ? "" : visite.getPatient().getPrenom();
        String fullName = (nom + " " + prenom).trim();
        return fullName.isBlank() ? visite.getCodevisite() : fullName;
    }

    private void updateStatistics(List<Visite> todayVisites) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime endWindow = now.plusHours(2);

        long activeCount = todayVisites.stream()
                .filter(visite -> visite.getStatut() != StatutVisite.ANNULEE)
                .count();
        long waitingCount = todayVisites.stream()
                .filter(visite -> visite.getStatut() == StatutVisite.PLANIFIEE)
                .filter(visite -> !visite.getDateheure().isBefore(now)
                        && !visite.getDateheure().isAfter(endWindow))
                .count();
        long completedCount = todayVisites.stream()
                .filter(visite -> visite.getStatut() == StatutVisite.TERMINEE)
                .count();

        totalVisitsValueLabel.setText(String.valueOf(activeCount));
        waitingValueLabel.setText(String.valueOf(waitingCount));
        completedValueLabel.setText(String.valueOf(completedCount));
    }

    private void refreshStatisticsToday() {
        LocalDate today = LocalDate.now();
        int requestId = ++statsRequestCounter;

        new SwingWorker<List<Visite>, Void>() {
            @Override
            protected List<Visite> doInBackground() {
                return visiteService.getVisitesByDate(today);
            }

            @Override
            protected void done() {
                if (requestId != statsRequestCounter) {
                    return;
                }
                try {
                    updateStatistics(get());
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
            }
        }.execute();
    }

    private String formatSexe(Visite visite) {
        if (visite.getPatient() == null) {
            return "Non renseigné";
        }
        char sexe = visite.getPatient().getSexe();
        if (sexe == 'H' || sexe == 'h') {
            return "Homme";
        }
        if (sexe == 'F' || sexe == 'f') {
            return "Femme";
        }
        return "Non renseigné";
    }

    private LocalDate convertDateToLocalDate(Date date) {
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }

    private static String initialsOf(Patient patient) {
        if (patient == null) {
            return "?";
        }
        String nom = patient.getNom() == null ? "" : patient.getNom().trim();
        String prenom = patient.getPrenom() == null ? "" : patient.getPrenom().trim();
        String a = nom.isEmpty() ? "" : nom.substring(0, 1);
        String b = prenom.isEmpty() ? "" : prenom.substring(0, 1);
        String initials = (a + b).toUpperCase();
        return initials.isEmpty() ? "?" : initials;
    }

    private static String medecinName(Visite visite) {
        if (visite.getMedecin() == null) {
            return "";
        }
        String nom = visite.getMedecin().getNom() == null ? "" : visite.getMedecin().getNom();
        String prenom = visite.getMedecin().getPrenom() == null ? "" : visite.getMedecin().getPrenom();
        String fullName = (nom + " " + prenom).trim();
        return fullName.isEmpty() ? "" : "Dr " + fullName;
    }

    private Color avatarColor(String key) {
        int index = Math.abs(key.hashCode()) % AVATAR_COLORS.length;
        return AVATAR_COLORS[index];
    }

    private class TimeCellRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(
                JTable table, Object value, boolean isSelected,
                boolean hasFocus, int row, int column
        ) {
            super.getTableCellRendererComponent(table, value, false, false, row, column);
            setOpaque(false);
            setFont(getFont().deriveFont(Font.BOLD, 14f));
            setForeground(PRIMARY_COLOR);
            setBorder(new EmptyBorder(0, 16, 0, 8));
            return this;
        }
    }

    private class NameCellRenderer implements TableCellRenderer {
        private final JPanel panel = new JPanel(new BorderLayout(12, 0));
        private final JLabel avatar = new JLabel("", SwingConstants.CENTER);
        private final JLabel nameLabel = new JLabel();
        private final JLabel doctorLabel = new JLabel();

        NameCellRenderer() {
            panel.setOpaque(false);
            panel.setBorder(new EmptyBorder(8, 8, 8, 12));
            avatar.setPreferredSize(new Dimension(36, 36));
            avatar.setOpaque(false);

            JPanel text = new JPanel();
            text.setOpaque(false);
            text.setLayout(new BoxLayout(text, BoxLayout.Y_AXIS));

            nameLabel.setOpaque(false);
            nameLabel.setFont(nameLabel.getFont().deriveFont(Font.BOLD, 13f));
            nameLabel.setForeground(TEXT_COLOR);
            nameLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

            doctorLabel.setOpaque(false);
            doctorLabel.setFont(doctorLabel.getFont().deriveFont(Font.PLAIN, 11f));
            doctorLabel.setForeground(SECONDARY_TEXT_COLOR);
            doctorLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

            text.add(nameLabel);
            text.add(Box.createVerticalStrut(2));
            text.add(doctorLabel);

            panel.add(avatar, BorderLayout.WEST);
            panel.add(text, BorderLayout.CENTER);
        }

        @Override
        public Component getTableCellRendererComponent(
                JTable table, Object value, boolean isSelected,
                boolean hasFocus, int row, int column
        ) {
            Visite visite = (Visite) value;
            String name = visite == null ? "" : getPatientDisplayName(visite);

            nameLabel.setText(name);
            doctorLabel.setText(visite == null ? "" : medecinName(visite));
            avatar.setIcon(visite == null
                    ? null
                    : new InitialsIcon(initialsOf(visite.getPatient()), avatarColor(name)));
            return panel;
        }
    }

    private class TextCellRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(
                JTable table, Object value, boolean isSelected,
                boolean hasFocus, int row, int column
        ) {
            super.getTableCellRendererComponent(table, value, false, false, row, column);
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
            String status = value == null ? "" : value.toString();
            pill.setText(status);

            if (status.equals("En cours")) {
                pill.setColors(new Color(0, 120, 110), new Color(220, 245, 240));
            } else if (status.equals("Annulée")) {
                pill.setColors(new Color(180, 50, 50), new Color(255, 234, 234));
            } else if (status.equals("Terminée")) {
                pill.setColors(new Color(110, 110, 115), new Color(240, 238, 239));
            } else {
                pill.setColors(new Color(55, 95, 175), new Color(228, 237, 255));
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
            super.getTableCellRendererComponent(table, value, false, false, row, column);
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
        private final boolean accent;
        private final boolean drawBorder;

        ShadowCard(int arc, boolean accent, boolean drawBorder) {
            this.arc = arc;
            this.accent = accent;
            this.drawBorder = drawBorder;
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

            g2.setColor(accent ? new Color(255, 248, 250) : Color.WHITE);
            g2.fillRoundRect(x, y, w, h, arc, arc);

            if (drawBorder) {
                g2.setColor(accent ? new Color(255, 214, 224) : BORDER_COLOR);
                g2.drawRoundRect(x, y, w - 1, h - 1, arc, arc);
            }

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

    private static class IconBadge extends JComponent {
        private final String glyph;
        private final Color background;
        private final Color foreground;

        IconBadge(String glyph, Color background, Color foreground) {
            this.glyph = glyph;
            this.background = background;
            this.foreground = foreground;
            setPreferredSize(new Dimension(42, 42));
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(background);
            g2.fillOval(0, 1, 40, 40);
            g2.setColor(foreground);
            g2.setFont(getFont().deriveFont(Font.BOLD, 15f));
            FontMetrics fm = g2.getFontMetrics();
            int tx = (40 - fm.stringWidth(glyph)) / 2;
            int ty = (40 - fm.getHeight()) / 2 + fm.getAscent() + 1;
            g2.drawString(glyph, tx, ty);
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