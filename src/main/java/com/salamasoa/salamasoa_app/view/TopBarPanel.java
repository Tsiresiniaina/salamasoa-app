package com.salamasoa.salamasoa_app.view;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;

public class TopBarPanel extends JPanel {

    private static final Color TOP_BAR_COLOR = Color.WHITE;
    private static final Color SEARCH_BACKGROUND = new Color(250, 250, 252);
    private static final Color SEARCH_BORDER_COLOR = new Color(230, 230, 234);
    private static final Color PLACEHOLDER_COLOR = new Color(170, 170, 175);
    private static final Color TEXT_COLOR = new Color(60, 60, 65);

    private static final String PLACEHOLDER_TEXT =
            "Search patients, doctors, or ID...";

    private JTextField searchField;

    public TopBarPanel() {
        this.searchField = searchField;
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

        searchField = new JTextField(PLACEHOLDER_TEXT);
        searchField.setFont(new Font("SansSerif", Font.PLAIN, 11));
        searchField.setForeground(PLACEHOLDER_COLOR);
        searchField.setBackground(SEARCH_BACKGROUND);
        searchField.setBorder(null);

        configurePlaceholder();

        searchBox.add(searchIconLabel, BorderLayout.WEST);
        searchBox.add(searchField, BorderLayout.CENTER);

        return searchBox;
    }

    private void configurePlaceholder() {
        searchField.addFocusListener(new FocusAdapter() {

            @Override
            public void focusGained(FocusEvent event) {
                if (searchField.getText().equals(PLACEHOLDER_TEXT)) {
                    searchField.setText("");
                    searchField.setForeground(TEXT_COLOR);
                }
            }

            @Override
            public void focusLost(FocusEvent event) {
                if (searchField.getText().trim().isEmpty()) {
                    searchField.setText(PLACEHOLDER_TEXT);
                    searchField.setForeground(PLACEHOLDER_COLOR);
                }
            }
        });
    }

    public String getSearchText() {
        String text = searchField.getText().trim();

        if (text.equals(PLACEHOLDER_TEXT)) {
            return "";
        }

        return text;
    }

    public JTextField getSearchField() {
        return searchField;
    }
}