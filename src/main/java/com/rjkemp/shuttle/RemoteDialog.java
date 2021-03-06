package com.rjkemp.shuttle;

import javax.swing.JOptionPane;
import javax.swing.JDialog;
import javax.swing.JButton;
import javax.swing.JRadioButton;
import javax.swing.ButtonGroup;
import javax.swing.JLabel;
import javax.swing.ImageIcon;
import javax.swing.BoxLayout;
import javax.swing.Box;
import javax.swing.BorderFactory;
import javax.swing.border.Border;

import net.schmizz.sshj.SSHClient;

import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.JPanel;
import javax.swing.*;
import java.beans.*; //Property change stuff
import java.io.IOException;
import java.awt.*;
import java.awt.event.*;

public class RemoteDialog extends JPanel {
    JLabel label;
    ImageIcon icon = createImageIcon("images/middle.gif");
    JFrame frame;
    String simpleDialogDesc = "Some simple message dialogs";
    String iconDesc = "A JOptionPane has its choice of icons";
    String moreDialogDesc = "Some more dialogs";
    JTextField hostname, port, username;
    JPasswordField password;
    SSHClient ssh;
    JButton select;

    public String getHostname() {
        return hostname.getText();
    }

    /** Creates the GUI shown inside the frame's content pane. */
    public RemoteDialog(JFrame frame, SSHClient ssh) {
        super(new BorderLayout());
        this.frame = frame;
        this.ssh = ssh;

        // Create the components.
        JPanel frequentPanel = createSimpleDialogBox();
        label = new JLabel("Click the \"Show it!\" button"
                + " to bring up the selected dialog.",
                JLabel.CENTER);

        // Lay them out.
        Border padding = BorderFactory.createEmptyBorder(20, 20, 5, 20);
        frequentPanel.setBorder(padding);

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("SFTP", null,
                frequentPanel,
                simpleDialogDesc); // tooltip text

        add(tabbedPane, BorderLayout.CENTER);
        add(label, BorderLayout.PAGE_END);
        label.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
    }

    /** Sets the text displayed at the bottom of the frame. */
    void setLabel(String newText) {
        label.setText(newText);
    }

    /** Returns an ImageIcon, or null if the path was invalid. */
    protected static ImageIcon createImageIcon(String path) {
        java.net.URL imgURL = RemoteDialog.class.getResource(path);
        if (imgURL != null) {
            return new ImageIcon(imgURL);
        } else {
            System.err.println("Couldn't find file: " + path);
            return null;
        }
    }

    /** Creates the panel shown by the first tab. */
    private JPanel createSimpleDialogBox() {
        JLabel hostnameLabel = new JLabel("Hostname");
        JLabel portLabel = new JLabel("Port Number");
        JLabel usernameLabel = new JLabel("User name");
        JLabel passwordLabel = new JLabel("Password");

        select = new JButton("Test");

        select.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                System.out.println("SFTP selected");
                try {
                    ssh.connect(hostname.getText());
                } catch (IOException error) {
                    error.printStackTrace();
                }
            }
        });

        hostname = new JTextField(30);
        port = new JTextField("22");
        username = new JTextField(30);
        password = new JPasswordField();

        JPanel box = new JPanel();

        box.setLayout(new BoxLayout(box, BoxLayout.PAGE_AXIS));
        box.add(hostnameLabel);
        box.add(hostname);

        box.add(portLabel);
        box.add(port);

        box.add(usernameLabel);
        box.add(username);

        box.add(passwordLabel);
        box.add(password);

        JPanel pane = new JPanel(new BorderLayout());
        pane.add(box, BorderLayout.PAGE_START);
        pane.add(select, BorderLayout.PAGE_END);
        return pane;
    }

    /**
     * Create the GUI and show it. For thread safety,
     * this method should be invoked from the
     * event-dispatching thread.
     */
    public static void createAndShowGUI(SSHClient ssh) {
        // Create and set up the window.
        JFrame frame = new JFrame("DialogDemo");
        frame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);

        // Create and set up the content pane.
        RemoteDialog newContentPane = new RemoteDialog(frame, ssh);
        newContentPane.setOpaque(true); // content panes must be opaque
        frame.setContentPane(newContentPane);

        // Display the window.
        frame.pack();
        frame.setVisible(true);
    }
}