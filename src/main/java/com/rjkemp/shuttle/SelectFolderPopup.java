package com.rjkemp.shuttle;

import java.awt.*;
import javax.swing.*;
import java.awt.MenuItem;
import java.io.File;

public class SelectFolderPopup extends JPanel {
    private File watchedDirectory;
    private MenuItem currentDirectory;

    public File getWatchedDirectory() {
        return this.watchedDirectory;
    }

    public String getWatchedDirectoryString() {
        if (watchedDirectory == null) {
            return "No watched directory";
        } else {
            return String.format("Selected: %s", watchedDirectory.getAbsolutePath());
        }
    }

    // used when we want to set the label on a menu item from the parent
    public SelectFolderPopup(MenuItem currentDirectory) {
        super(new BorderLayout());

        this.currentDirectory = currentDirectory;
    }

    public SelectFolderPopup() {
        super(new BorderLayout());
    }

    public void createAndShowGUI() {
        JFrame frame = new JFrame("Select a folder");
        frame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);

        JFileChooser chooser = new JFileChooser();

        chooser.setCurrentDirectory(new java.io.File(System.getProperty("user.home")));
        chooser.setDialogTitle("Pick folder");
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

        // disable the "All files" option.
        chooser.setAcceptAllFileFilterUsed(false);

        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            watchedDirectory = chooser.getSelectedFile();
            System.out.println("Selected directory: " + watchedDirectory);
            if (currentDirectory != null) {
                currentDirectory.setLabel(getWatchedDirectoryString());
            }
        } else {
            System.out.println("No directory selected");
        }

        frame.getContentPane().add(this, "Center");
        frame.setSize(this.getPreferredSize());
    }

}