package com.rjkemp.shuttle;

import javax.swing.*;
import java.awt.event.*;
import java.awt.*;
import java.io.File;

public class DirectoryChooser extends JPanel
        implements ActionListener {

    JButton go;
    JLabel label, closeLabel;
    JFileChooser chooser;
    String choosertitle = "Select a directory to watch for changes";
    File selection;

    private String getSelectedAbsPath() {
        if (selection == null) {
            return "No folder selected yet";
        } else {
            return selection.getAbsolutePath();
        }
    }

    public DirectoryChooser() {
        go = new JButton("Select Watched Directory");
        label = new JLabel(getSelectedAbsPath());
        closeLabel = new JLabel("Close this dialog to select the above directory");
        go.addActionListener(this);
        add(go);
        add(label);
        add(closeLabel);
    }

    public void actionPerformed(ActionEvent e) {
        chooser = new JFileChooser();
        chooser.setCurrentDirectory(new java.io.File(System.getProperty("user.home")));
        chooser.setDialogTitle(choosertitle);
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

        // disable the "All files" option.
        chooser.setAcceptAllFileFilterUsed(false);

        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            selection = chooser.getSelectedFile();
        } else {
            selection = null;
        }

        label.setText(getSelectedAbsPath());
    }

    public Dimension getPreferredSize() {
        return new Dimension(400, 250);
    }
}