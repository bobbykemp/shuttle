package com.rjkemp.shuttle;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.plaf.DimensionUIResource;

import java.awt.MenuItem;

public class TransferSettingsPage extends JPanel
        implements ActionListener {

    MenuItem selectedTransferType;
    SelectFolderPopup selectFolderPopup;
    LocalTransfer localTransferPanel;
    RemoteTransfer remoteTransferPanel;

    public static enum TRANSFER_TYPE {
        LOCAL,
        REMOTE,
    }

    public TransferSettingsPage(MenuItem selectedTransferType) {
        super(new BorderLayout());
        this.selectedTransferType = selectedTransferType;

        selectFolderPopup = new SelectFolderPopup();
        localTransferPanel = new LocalTransfer();
        remoteTransferPanel = new RemoteTransfer();

    }

    /** Listens to the radio buttons. */
    public void actionPerformed(ActionEvent e) {
        selectedTransferType.setLabel(e.getActionCommand());
        remoteTransferPanel.setVisible(false);
        localTransferPanel.setVisible(false);

        switch (e.getActionCommand()) {
            case "REMOTE":
                remoteTransferPanel.setVisible(true);
                break;
            case "LOCAL":
                localTransferPanel.setVisible(true);
                break;
        }
    }

    /**
     * Create the GUI and show it. For thread safety,
     * this method should be invoked from the
     * event-dispatching thread.
     */
    public void createAndShowGUI() {
        JFrame frame = new JFrame("Transfer settings");
        frame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);

        // Create the radio buttons.
        JRadioButton localTransferButton = new JRadioButton("Local Transfer");
        localTransferButton.setActionCommand(TRANSFER_TYPE.LOCAL.toString());
        localTransferButton.setMnemonic(KeyEvent.VK_L);
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

        add(radioPanel, BorderLayout.WEST);
        add(localTransferPanel, BorderLayout.EAST);
        add(remoteTransferPanel, BorderLayout.EAST);

        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        frame.getContentPane().add(this);
        frame.setSize(new DimensionUIResource(500, 250));

        frame.setVisible(true);
    }
}