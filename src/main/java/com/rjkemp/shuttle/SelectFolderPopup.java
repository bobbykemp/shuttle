package com.rjkemp.shuttle;

import java.awt.*;
import javax.swing.*;
import java.awt.MenuItem;
import java.io.File;

public class SelectFolderPopup extends JPanel {
    private File watchedDirectory;
    private MenuItem currentDirectory;
    private String noDirectoryLabel, selectedDirectoryLabel;

    public File getWatchedDirectory() {
        return this.watchedDirectory;
    }

    public String getWatchedDirectoryString() {
        if (watchedDirectory == null) {
            return this.noDirectoryLabel;
        } else {
            return String.format(this.selectedDirectoryLabel, watchedDirectory.getAbsolutePath());
        }
    }

    // used when we want to set the label on a menu item from the parent
    public SelectFolderPopup(MenuItem currentDirectory, String noDirectoryLabel, String selectedDirectoryLabel) {
        super(new BorderLayout());
        this.currentDirectory = currentDirectory;
        this.noDirectoryLabel = noDirectoryLabel;
        this.selectedDirectoryLabel = selectedDirectoryLabel;
    }

    public SelectFolderPopup(String noDirectoryLabel, String selectedDirectoryLabel) {
        super(new BorderLayout());
        this.noDirectoryLabel = noDirectoryLabel;
        this.selectedDirectoryLabel = selectedDirectoryLabel;
    }

    public void createAndShowGUI() {
        JFrame frame = new JFrame("Select a folder");
        frame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);

        JFileChooser chooser = new JFileChooser();

        if (watchedDirectory != null) {
            chooser.setCurrentDirectory(watchedDirectory);
        } else {
            chooser.setCurrentDirectory(new java.io.File(System.getProperty("user.home")));
        }
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