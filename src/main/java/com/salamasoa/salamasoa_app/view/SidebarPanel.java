package com.salamasoa.salamasoa_app.view;

import com.formdev.flatlaf.extras.FlatSVGIcon;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.function.Consumer;

public class SidebarPanel extends JPanel {

    private static final Color SIDEBAR_COLOR = new Color(250, 247, 248);
    private static final Color PRIMARY_COLOR = new Color(199, 0, 61);
    private static final Color ACTIVE_COLOR = new Color(250, 221, 230);
    private static final Color TEXT_COLOR = new Color(70, 70, 70);

    private final Consumer<String> navigationHandler;
    private JButton activeButton;

    public SidebarPanel(Consumer<String> navigationHandler) {
        this.navigationHandler = navigationHandler;

        setPreferredSize(new Dimension(235, 0));
        setBackground(SIDEBAR_COLOR);
        setBorder(new EmptyBorder(20, 14, 18, 14));
        setLayout(new BorderLayout());

        add(createLogoPanel(), BorderLayout.NORTH);
        add(createNavigationPanel(), BorderLayout.CENTER);
    }

    private JPanel createLogoPanel() {
        JPanel logoPanel = new JPanel();
        logoPanel.setBackground(SIDEBAR_COLOR);
        logoPanel.setLayout(new BoxLayout(logoPanel, BoxLayout.Y_AXIS));
        logoPanel.setBorder(new EmptyBorder(0, 2, 28, 0));

        String hexColor = String.format("#%06x", (PRIMARY_COLOR.getRGB() & 0x00FFFFFF));

        JLabel nameLabel = new JLabel("<html><span style='color:" + hexColor + "'>●</span> " +
                "<b style='color:#1E293B'>Salama<span style='color:" + hexColor + "'>Soa</span></b></html>");
        nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        nameLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel subtitleLabel = new JLabel("Centre médical");
        subtitleLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
        subtitleLabel.setForeground(new Color(110, 110, 110));
        subtitleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        logoPanel.add(nameLabel);
        logoPanel.add(Box.createVerticalStrut(2));
        logoPanel.add(subtitleLabel);

        return logoPanel;
    }

    private JPanel createNavigationPanel() {
        JPanel navigationPanel = new JPanel();
        navigationPanel.setBackground(SIDEBAR_COLOR);
        navigationPanel.setLayout(new BoxLayout(navigationPanel, BoxLayout.Y_AXIS));

        // Note les chemins incluant /resources/
        FlatSVGIcon iconVisites = createIcon("icons/visit.svg");
        JButton todayVisitButton = createNavigationButton(
                "Visites du jour",
                MainFrame.PAGE_VISITES,
                iconVisites
        );

        FlatSVGIcon iconPatients = createIcon("icons/patient.svg");
        JButton patientsButton = createNavigationButton(
                "Patients",
                MainFrame.PAGE_PATIENTS,
                iconPatients
        );

        FlatSVGIcon iconDoctors = createIcon("icons/doctor.svg");
        JButton doctorsButton = createNavigationButton(
                "Médecins",
                MainFrame.PAGE_MEDECINS,
                iconDoctors
        );

        navigationPanel.add(todayVisitButton);
        navigationPanel.add(Box.createVerticalStrut(8));

        navigationPanel.add(patientsButton);
        navigationPanel.add(Box.createVerticalStrut(8));

        navigationPanel.add(doctorsButton);

        setActiveButton(patientsButton);

        return navigationPanel;
    }

    /**
     * Crée une icône SVG FlatLaf redimensionnée et applique un filtre de couleur
     * pour qu'elle s'adapte automatiquement à la couleur du texte du bouton.
     */
    private FlatSVGIcon createIcon(String path) {
        FlatSVGIcon icon = new FlatSVGIcon(path, 18, 18);

        if (!icon.hasFound()) {
            System.err.println("SVG introuvable : " + path);
        }

        icon.setColorFilter(new FlatSVGIcon.ColorFilter(color -> TEXT_COLOR));
        return icon;
    }
    private void tintIcon(JButton button, Color color) {
        Icon icon = button.getIcon();
        if (icon instanceof FlatSVGIcon svg) {
            svg.setColorFilter(new FlatSVGIcon.ColorFilter(c -> color));
        }
    }
    private JButton createNavigationButton(String text, String pageName, Icon icon) {
        JButton button = new JButton(text);
        if (icon != null) {
            button.setIcon(icon);
        }
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setForeground(TEXT_COLOR);
        button.setBackground(SIDEBAR_COLOR);
        button.setIconTextGap(12);

        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);

        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setBorder(new EmptyBorder(11, 10, 11, 10));

        button.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        button.setAlignmentX(Component.LEFT_ALIGNMENT);

        button.addActionListener(event -> {
            setActiveButton(button);
            navigationHandler.accept(pageName);
        });

        return button;
    }

    private void setActiveButton(JButton selectedButton) {
        if (activeButton != null) {
            activeButton.setBackground(SIDEBAR_COLOR);
            activeButton.setForeground(TEXT_COLOR);
            activeButton.setContentAreaFilled(false);
        }

        selectedButton.setBackground(ACTIVE_COLOR);
        selectedButton.setForeground(PRIMARY_COLOR);
        selectedButton.setContentAreaFilled(true);

        activeButton = selectedButton;
    }
}