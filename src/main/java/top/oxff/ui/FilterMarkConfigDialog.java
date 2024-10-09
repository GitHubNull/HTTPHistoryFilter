package top.oxff.ui;

import burp.api.montoya.core.HighlightColor;
import burp.api.montoya.proxy.ProxyHttpRequestResponse;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class FilterMarkConfigDialog extends javax.swing.JDialog{

    JPanel northPanel;
    JPanel centerPanel;
    JPanel southPanel;

    ButtonGroup buttonGroup;
    JRadioButton noteRadioButton;
    JRadioButton highlightRadioButton;

    HighlightColor[] highlightColors = {
            HighlightColor.RED,
            HighlightColor.ORANGE,
            HighlightColor.YELLOW,
            HighlightColor.GREEN,
            HighlightColor.CYAN,
            HighlightColor.BLUE,
            HighlightColor.PINK,
            HighlightColor.MAGENTA,
            HighlightColor.GRAY
    };
    String[] highlightColorNames = {
            "Red",
            "Orange",
            "Yellow",
            "Green",
            "Cyan",
            "Blue",
            "Pink",
            "Magenta",
            "Gray"
    };

    Map<String, HighlightColor> highlightColorMap = new java.util.HashMap<>();

    JPanel highlightPanel;
    ButtonGroup clearOrKeppHightLightButtonGroup;
    JRadioButton clearHighlightRadioButton;
    JRadioButton keepHighlightRadioButton;
    JComboBox<String> highlightComboBox;

    JPanel notePanel;
    ButtonGroup clearOrKeepNoteButtonGroup;
    JRadioButton clearNoteRadioButton;
    JRadioButton keepNoteRadioButton;
    JTextField noteTextField;

    JButton okButton;
    JButton cancelButton;

    HTTPHistoryFilterContextMenuItemsProvider httpHistoryFilterContextMenuItemsProvider;
    List<ProxyHttpRequestResponse> proxyHttpRequestResponseList;


    public FilterMarkConfigDialog(HTTPHistoryFilterContextMenuItemsProvider  httpHistoryFilterContextMenuItemsProvider, List<ProxyHttpRequestResponse> proxyHttpRequestResponseList) {
        this.httpHistoryFilterContextMenuItemsProvider = httpHistoryFilterContextMenuItemsProvider;
        this.proxyHttpRequestResponseList = proxyHttpRequestResponseList;
        initComponents();
        initData();
        initComponentsActionListeners();
    }

    private void initComponents() {
        setLayout(new BorderLayout());

        northPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        centerPanel = new JPanel(new GridLayout(2, 1));
        southPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        noteRadioButton = new JRadioButton("Note");
        highlightRadioButton = new JRadioButton("Highlight");

        buttonGroup = new ButtonGroup();
        buttonGroup.add(noteRadioButton);
        buttonGroup.add(highlightRadioButton);
        highlightRadioButton.setSelected(true);

        northPanel.add(noteRadioButton);
        northPanel.add(highlightRadioButton);

        highlightPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        highlightComboBox = new JComboBox<>(highlightColorNames);
        highlightComboBox.setRenderer(new DefaultListCellRenderer(){
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                label.setOpaque(true);
                if (isSelected){
                    setForeground(getBackground());
                    label.setBackground(list.getBackground());
                }else{
                    setForeground(Color.WHITE);
                    label.setBackground(Color.getColor(highlightColorMap.get(value.toString()).name()));
                }
                return label;
            }
        });
        highlightComboBox.setSelectedIndex(0);
        highlightComboBox.setEnabled(true);
        highlightPanel.add(highlightComboBox);

        clearHighlightRadioButton = new JRadioButton("Clear");
        keepHighlightRadioButton = new JRadioButton("Keep");
        clearOrKeppHightLightButtonGroup = new ButtonGroup();
        clearOrKeppHightLightButtonGroup.add(clearHighlightRadioButton);
        clearOrKeppHightLightButtonGroup.add(keepHighlightRadioButton);

        clearHighlightRadioButton.setSelected(true);
        keepHighlightRadioButton.setEnabled(false);

        highlightPanel.add(clearHighlightRadioButton);
        highlightPanel.add(keepHighlightRadioButton);

        centerPanel.add(highlightPanel);

        notePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        noteTextField = new JTextField("",20);
        noteTextField.setEnabled(false);
        notePanel.add(noteTextField);

        clearOrKeepNoteButtonGroup = new ButtonGroup();

        clearNoteRadioButton = new JRadioButton("Clear");
        keepNoteRadioButton = new JRadioButton("Keep");

        clearOrKeepNoteButtonGroup.add(clearNoteRadioButton);
        clearOrKeepNoteButtonGroup.add(keepNoteRadioButton);

        clearNoteRadioButton.setSelected(true);
        keepNoteRadioButton.setEnabled(false);

        notePanel.add(clearNoteRadioButton);
        notePanel.add(keepNoteRadioButton);

        centerPanel.add(notePanel);

        // enable highlight config panel
        setEnabledHighlightComponents(true);

        // disable note config panel
        setEnabledNoteComponents(false);

        okButton = new JButton("OK");
        cancelButton = new JButton("Cancel");

        southPanel.add(okButton);
        southPanel.add(cancelButton);

        add(northPanel, BorderLayout.NORTH);
        add(centerPanel, BorderLayout.CENTER);
        add(southPanel, BorderLayout.SOUTH);

        pack();
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    }

    private void initData() {
        for (int i = 0; i < highlightColors.length; i++) {
            highlightColorMap.put(highlightColorNames[i], highlightColors[i]);
        }
    }

    private void setEnabledHighlightComponents(boolean enabled) {
        // enable highlight config panel
        highlightPanel.setVisible(enabled);
        highlightPanel.setEnabled(enabled);

        keepHighlightRadioButton.setEnabled(enabled);
        keepHighlightRadioButton.setVisible(enabled);

        clearHighlightRadioButton.setEnabled(enabled);
        clearHighlightRadioButton.setVisible(enabled);

        if (enabled){
            clearHighlightRadioButton.setSelected(true);
            keepHighlightRadioButton.setSelected(false);
            highlightComboBox.setSelectedIndex(0);
        }

        highlightComboBox.setEnabled(enabled);
        highlightComboBox.setVisible(enabled);
    }

    private void setEnabledNoteComponents(boolean enabled) {
        notePanel.setEnabled(enabled);
        notePanel.setVisible(enabled);

        noteTextField.setEnabled(enabled);
        noteTextField.setVisible(enabled);

        clearNoteRadioButton.setEnabled(enabled);
        clearNoteRadioButton.setVisible(enabled);

        if (enabled){
            noteTextField.setText("[QQQQQQQQ]");
            clearNoteRadioButton.setSelected(true);
        }

        keepNoteRadioButton.setEnabled(enabled);
        keepNoteRadioButton.setVisible(enabled);
    }

    private void initComponentsActionListeners() {
        highlightRadioButton.addActionListener(e -> initHighlightRadioButtonActionListener());
        noteRadioButton.addActionListener(e -> initNoteRadioButtonActionListener());

        okButton.addActionListener(e -> {
            if (highlightRadioButton.isSelected()) {
                boolean clear = clearHighlightRadioButton.isSelected();
                httpHistoryFilterContextMenuItemsProvider.highlight(proxyHttpRequestResponseList, highlightColorMap.get(Objects.requireNonNull(highlightComboBox.getSelectedItem()).toString()), clear);
            } else {
                boolean clear = clearNoteRadioButton.isSelected();
                httpHistoryFilterContextMenuItemsProvider.note(proxyHttpRequestResponseList, noteTextField.getText(), clear);
            }
            dispose();
        });
        cancelButton.addActionListener(e -> dispose());
    }

    private void initHighlightRadioButtonActionListener() {
        // enable highlight config panel
        setEnabledHighlightComponents(true);

        // disable note config panel
        setEnabledNoteComponents(false);

        pack();
        setLocationRelativeTo(null);
    }

    private void initNoteRadioButtonActionListener() {
        // disable highlight config panel
        setEnabledHighlightComponents(false);

        // enable note config panel
        setEnabledNoteComponents(true);

        pack();
        setLocationRelativeTo(null);
    }
}
