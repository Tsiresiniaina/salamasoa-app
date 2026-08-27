package com.salamasoa.salamasoa_app.view;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.function.Consumer;

public class TopBarPanel extends JPanel {

    private static final Color TOP_BAR_COLOR = Color.WHITE;
    private static final Color SEARCH_BACKGROUND = new Color(250, 250, 252);
    private static final Color SEARCH_BORDER_COLOR = new Color(230, 230, 234);
    private static final Color PLACEHOLDER_COLOR = new Color(170, 170, 175);
    private static final Color TEXT_COLOR = new Color(60, 60, 65);

    private static final String PLACEHOLDER_TEXT =
            "Rechercher un patient : nom ou code...";

    private JTextField searchField;

    /*
     * Action déclenchée à chaque frappe, avec le texte saisi.
     * MainFrame s'en sert pour filtrer la liste des patients.
     */
    private final Consumer<String> searchHandler;

    /*
     * Constructeur conservé pour ne pas casser un appel existant :
     * la barre s'affiche alors sans être reliée à une recherche.
     */
    public TopBarPanel() {
        this(keyword -> { });
    }

    /**
     * @param searchHandler reçoit le texte saisi à chaque frappe
     */
    public TopBarPanel(Consumer<String> searchHandler) {
        this.searchHandler = searchHandler == null
                ? keyword -> { }
                : searchHandler;

        setBackground(TOP_BAR_COLOR);
        setPreferredSize(new Dimension(0, 58));
        setBorder(new EmptyBorder(10, 30, 10, 30));
        setLayout(new BorderLayout());

        JPanel searchBox = createSearchBox();
        add(searchBox, BorderLayout.WEST);
    }

    private JPanel createSearchBox() {
        JPanel searchBox = new JPanel(new BorderLayout(8, 0));
        searchBox.setPreferredSize(new Dimension(330, 34));
        searchBox.setBackground(SEARCH_BACKGROUND);
        searchBox.setBorder(new LineBorder(SEARCH_BORDER_COLOR, 1, true));

        JLabel searchIconLabel = new JLabel("⌕");
        searchIconLabel.setFont(new Font("SansSerif", Font.PLAIN, 18));
        searchIconLabel.setForeground(PLACEHOLDER_COLOR);
        searchIconLabel.setBorder(new EmptyBorder(0, 10, 0, 0));

        searchField = new JTextField();
        searchField.setFont(new Font("SansSerif", Font.PLAIN, 11));
        searchField.setForeground(TEXT_COLOR);
        searchField.setBackground(SEARCH_BACKGROUND);
        searchField.setBorder(null);

        /*
         * Placeholder natif de FlatLaf : le champ reste réellement vide
         * tant que rien n'est saisi. L'ancienne astuce, qui écrivait le
         * texte d'invite dans le champ, aurait déclenché une recherche
         * sur ce texte à chaque perte de focus.
         */
        searchField.putClientProperty(
                "JTextField.placeholderText",
                PLACEHOLDER_TEXT
        );

        configureSearchListener();

        searchBox.add(searchIconLabel, BorderLayout.WEST);
        searchBox.add(searchField, BorderLayout.CENTER);

        return searchBox;
    }

    /**
     * Déclenche la recherche à chaque modification du champ.
     *
     * Le filtrage s'applique aux patients déjà chargés en mémoire :
     * aucune requête n'est envoyée à la base à chaque frappe.
     */
    private void configureSearchListener() {
        searchField.getDocument().addDocumentListener(new DocumentListener() {

            @Override
            public void insertUpdate(DocumentEvent event) {
                fireSearch();
            }

            @Override
            public void removeUpdate(DocumentEvent event) {
                fireSearch();
            }

            @Override
            public void changedUpdate(DocumentEvent event) {
                fireSearch();
            }
        });

        // Échap vide la recherche et réaffiche toute la liste.
        searchField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent event) {
                if (event.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    clearSearch();
                }
            }
        });
    }

    private void fireSearch() {
        searchHandler.accept(getSearchText());
    }

    /**
     * Vide le champ de recherche et notifie l'écoute.
     */
    public void clearSearch() {
        searchField.setText("");
    }

    /**
     * Change le texte d'invite du champ.
     *
     * MainFrame l'adapte à la page affichée, pour indiquer ce que la
     * barre recherche à cet endroit.
     */
    public void setPlaceholder(String placeholder) {
        searchField.putClientProperty(
                "JTextField.placeholderText",
                placeholder
        );

        searchField.repaint();
    }

    public String getSearchText() {
        return searchField.getText().trim();
    }

    public JTextField getSearchField() {
        return searchField;
    }
}
