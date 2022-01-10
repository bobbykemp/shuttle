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

    public TransferSettingsPage() {
        super(new BorderLayout());

        // Create the radio buttons.
        JRadioButton localTransferButton = new JRadioButton("Local Transfer");
        localTransferButton.setMnemonic(KeyEvent.VK_L);
        localTransferButton.setActionCommand(TRANSFER_TYPE.LOCAL.toString());
        localTransferButton.setSelected(true);

        JRadioButton remoteTransferButton = new JRadioButton("Remote Transfer");
        remoteTransferButton.setActionCommand(TRANSFER_TYPE.REMOTE.toString());
        remoteTransferButton.setMnemonic(KeyEvent.VK_R);

        // Group the radio buttons.
        ButtonGroup group = new ButtonGroup();
        group.add(localTransferButton);
        group.add(remoteTransferButton);

        // Register a listener for the radio buttons.
        localTransferButton.addActionListener(this);
        remoteTransferButton.addActionListener(this);

        // Put the radio buttons in a column in a panel.
        JPanel radioPanel = new JPanel(new GridLayout(0, 1));
        radioPanel.add(localTransferButton);
        radioPanel.add(remoteTransferButton);

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
    public void createAndShowGUI(MenuItem selectedTransferType) {
        // Create and set up the window.
        JFrame frame = new JFrame("Transfer settings");
        frame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);

        this.selectedTransferType = selectedTransferType;

        // Create and set up the content pane.
        JComponent newContentPane = new TransferSettingsPage();
        newContentPane.setOpaque(true); // content panes must be opaque
        frame.setContentPane(newContentPane);

        // Display the window.
        frame.pack();
        frame.setVisible(true);
    }
}