package com.salamasoa.salamasoa_app.view;

import com.formdev.flatlaf.icons.FlatSearchIcon;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.function.Consumer;

public class TopBarPanel extends JPanel {

    private static final Color TOP_BAR_COLOR = Color.WHITE;
    private static final Color TEXT_COLOR = new Color(45, 45, 48);

    private static final String PLACEHOLDER_TEXT =
            "Rechercher un patient : nom ou code...";

    private JTextField searchField;
    private final Consumer<String> searchHandler;

    public TopBarPanel() {
        this(keyword -> { });
    }

    public TopBarPanel(Consumer<String> searchHandler) {
        this.searchHandler = searchHandler == null
                ? keyword -> { }
                : searchHandler;

        setBackground(TOP_BAR_COLOR);
        setPreferredSize(new Dimension(0, 64));
        setBorder(new EmptyBorder(12, 32, 12, 32));
        setLayout(new BorderLayout());

        setLayout(new BorderLayout());

        JPanel center = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        center.setOpaque(false);
        center.add(createSearchField());
        add(center, BorderLayout.CENTER);
    }

    private JTextField createSearchField() {
        searchField = new JTextField();
        searchField.setFont(searchField.getFont().deriveFont(Font.PLAIN, 13f));
        searchField.setForeground(TEXT_COLOR);
        searchField.setPreferredSize(new Dimension(360, 36));
        searchField.setOpaque(false);

        searchField.putClientProperty("JTextField.placeholderText", PLACEHOLDER_TEXT);
        searchField.putClientProperty("JTextField.leadingIcon", new FlatSearchIcon());
        searchField.putClientProperty("JTextField.showClearButton", true);
        searchField.putClientProperty("JComponent.roundRect", true);
        searchField.putClientProperty("FlatLaf.style",
                "arc: 999;"
                        + "borderWidth: 1;"
                        + "focusWidth: 0;"
                        + "innerFocusWidth: 0;"
                        + "padding: 6,14,6,14;"
                        + "background: #FAF7F8;"
                        + "focusedBackground: #FFFFFF");

        configureSearchListener();
        return searchField;
    }

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

    public void clearSearch() {
        searchField.setText("");
    }

    public void setPlaceholder(String placeholder) {
        searchField.putClientProperty("JTextField.placeholderText", placeholder);
        searchField.repaint();
    }

    public String getSearchText() {
        return searchField.getText().trim();
    }

    public JTextField getSearchField() {
        return searchField;
    }
}