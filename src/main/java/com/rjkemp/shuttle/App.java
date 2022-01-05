package com.rjkemp.shuttle;

import java.awt.*;
import java.awt.event.*;
import java.net.URL;

import javax.swing.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.AccessDeniedException;

public class App {
    static File watchedDirectory;
    static MenuItem pickFolder, startMonitoring, exitItem, currentDirectory, watchStatus, stopMonitoring;
    static JFrame frame;
    static DirectoryWatcher watcher;
    static DirectoryWatcher.WatchStatus watchStatusMessage = DirectoryWatcher.WatchStatus.INACTIVE;

    public static void setWatchStatusMessage(DirectoryWatcher.WatchStatus message) {
        watchStatusMessage = message;
    }

    public static DirectoryWatcher.WatchStatus getWatchStatusMessage() {
        return watchStatusMessage;
    }

    public static String getWatchedDirectory() {
        if (watchedDirectory == null) {
            return "No watched directory";
        } else {
            return String.format("Watching: %s", watchedDirectory.getAbsolutePath());
        }
    }

    public static void updateMenuButtons() {
        if (startMonitoring != null) {
            if (watchedDirectory == null) {
                // disable monitoring button if there is no watched directory
                startMonitoring.setEnabled(false);
            } else {
                startMonitoring.setEnabled(true);
            }
        }
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
            // UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");
        } catch (UnsupportedLookAndFeelException ex) {
            ex.printStackTrace();
        } catch (IllegalAccessException ex) {
            ex.printStackTrace();
        } catch (InstantiationException ex) {
            ex.printStackTrace();
        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
        }
        /* Turn off metal's use of bold fonts */
        UIManager.put("swing.boldMetal", Boolean.FALSE);
        // Schedule a job for the event-dispatching thread:
        // adding TrayIcon.
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createAndShowTrayItem();
            }
        });
    }

    private static void createAndShowTrayItem() {
        // Check the SystemTray support
        if (!SystemTray.isSupported()) {
            System.out.println("SystemTray is not supported");
            return;
        }

        final PopupMenu popup = new PopupMenu();
        final TrayIcon trayIcon = new TrayIcon(createImage("/bulb.gif", "tray icon"));
        final SystemTray tray = SystemTray.getSystemTray();

        // Create a popup menu components
        pickFolder = new MenuItem("Select a folder");
        startMonitoring = new MenuItem("Start monitoring");
        stopMonitoring = new MenuItem("Stop monitoring");
        exitItem = new MenuItem("Exit");
        currentDirectory = new MenuItem(getWatchedDirectory());
        watchStatus = new MenuItem(getWatchStatusMessage().toString());
        currentDirectory.setEnabled(false);
        watchStatus.setEnabled(false);

        updateMenuButtons();

        // Add components to popup menu
        popup.add(watchStatus);
        popup.add(currentDirectory);
        popup.addSeparator();
        popup.add(pickFolder);
        popup.add(startMonitoring);
        popup.add(stopMonitoring);
        popup.addSeparator();
        popup.add(exitItem);

        trayIcon.setPopupMenu(popup);

        // add icon to tray
        try {
            tray.add(trayIcon);
        } catch (AWTException e) {
            System.out.println("TrayIcon could not be added.");
            return;
        }

        // double left click tray icon
        trayIcon.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(null,
                        "This dialog box is run from System Tray");
            }
        });

        // click choose folder button
        pickFolder.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                createAndShowSelectFolderPopup();
                updateMenuButtons();
            }
        });

        // click start monitoring button
        startMonitoring.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    (watcher = new DirectoryWatcher(watchedDirectory.toPath(), true, watchStatus)).execute();
                } catch (AccessDeniedException error) {
                    // show an error dialog
                    showErrorModal(
                            String.format("Access denied for folder %s\nFailed to start watching selected directory.",
                                    error.getMessage()));
                } catch (IOException error) {
                    showErrorModal(String.format(error.getMessage()));
                    error.printStackTrace();
                }
            }
        });

        stopMonitoring.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                watcher.cancel(false);
            }
        });

        // click exit button
        exitItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                tray.remove(trayIcon);
                System.exit(0);
            }
        });
    }

    private static void showErrorModal(String errorMessage) {
        frame = new JFrame();

        JOptionPane.showConfirmDialog(null,
                errorMessage,
                "Error",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.ERROR_MESSAGE);

    }

    private static void createAndShowSelectFolderPopup() {
        frame = new JFrame();
        frame.setResizable(false);

        JPanel chooserPanel = new JPanel();
        JFileChooser chooser = new JFileChooser();

        chooser.setCurrentDirectory(new java.io.File(System.getProperty("user.home")));
        chooser.setDialogTitle("Choose a folder");
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

        // disable the "All files" option.
        chooser.setAcceptAllFileFilterUsed(false);

        if (chooser.showOpenDialog(chooserPanel) == JFileChooser.APPROVE_OPTION) {
            watchedDirectory = chooser.getSelectedFile();
            System.out.println("Selected directory: " + watchedDirectory);
            currentDirectory.setLabel(getWatchedDirectory());
        } else {
            watchedDirectory = null;
            System.out.println("No directory selected");
        }

        frame.getContentPane().add(chooserPanel, "Center");
        frame.setSize(chooserPanel.getPreferredSize());
    }

    // Obtain the image URL
    protected static Image createImage(String path, String description) {
        URL imageURL = App.class.getResource(path);

        if (imageURL == null) {
            System.err.println("Resource not found: " + path);
            return null;
        } else {
            return (new ImageIcon(imageURL, description)).getImage();
        }
    }
}
