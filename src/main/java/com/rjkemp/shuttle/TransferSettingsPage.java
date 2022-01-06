package com.rjkemp.shuttle;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.awt.MenuItem;

public class TransferSettingsPage extends JPanel
        implements ActionListener {

    MenuItem selectedTransferType;

    public static enum TRANSFER_TYPE {
        LOCAL,
        REMOTE,
    }

    public TransferSettingsPage(MenuItem selectedTransferType) {
        super(new BorderLayout());

        this.selectedTransferType = selectedTransferType;

        // Create the radio buttons.
        JRadioButton localTransferButton = new JRadioButton("Local Transfer");
        localTransferButton.setMnemonic(KeyEvent.VK_L);
        localTransferButton.setActionCommand(TRANSFER_TYPE.LOCAL.toString());
        localTransferButton.setSelected(true);

        JRadioButton remoteTransferButton = new JRadioButton("Remote Transfer");
        localTransferButton.setActionCommand(TRANSFER_TYPE.REMOTE.toString());
        remoteTransferButton.setMnemonic(KeyEvent.VK_R);

        // Group the radio buttons.
        ButtonGroup group = new ButtonGroup();
        group.add(localTransferButton);

        // Register a listener for the radio buttons.
        localTransferButton.addActionListener(this);

        // Put the radio buttons in a column in a panel.
        JPanel radioPanel = new JPanel(new GridLayout(0, 1));
        radioPanel.add(localTransferButton);

        add(radioPanel, BorderLayout.LINE_START);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
    }

    /** Listens to the radio buttons. */
    public void actionPerformed(ActionEvent e) {
        selectedTransferType.setLabel(e.getActionCommand());
    }

    /**
     * Create the GUI and show it. For thread safety,
     * this method should be invoked from the
     * event-dispatching thread.
     */
    public void createAndShowGUI() {
        // Create and set up the window.
        JFrame frame = new JFrame("Transfer settings");
        frame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);

        // Create and set up the content pane.
        JComponent newContentPane = new TransferSettingsPage();
        newContentPane.setOpaque(true); // content panes must be opaque
        frame.setContentPane(newContentPane);

        // Display the window.
        frame.pack();
        frame.setVisible(true);
    }
}