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
    static MenuItem pickFolder, startMonitoring, exitItem, currentDirectory, watchStatus, selectedTransferType,
            stopMonitoring;
    static JRadioButtonMenuItem transferType;
    static JFrame frame;
    static DirectoryWatcher watcher;
    static TransferSettingsPage transferSettingsPage;

    public static String getWatchedDirectory() {
        if (watchedDirectory == null) {
            return "No watched directory";
        } else {
            return String.format("Selected: %s", watchedDirectory.getAbsolutePath());
        }
    }

    public static void updateMenuButtons() {
        // Start Monitoring button
        if (startMonitoring != null) {
            // no watched directory to monitor
            if (watchedDirectory == null) {
                startMonitoring.setEnabled(false);
            } else {
                startMonitoring.setEnabled(true);
            }
        }

        if (stopMonitoring != null) {
            // no watcher, so nothing to stop
            if (watcher == null) {
                stopMonitoring.setEnabled(false);
            } else {
                stopMonitoring.setEnabled(true);
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
        final TrayIcon trayIcon = new TrayIcon(createImage("/logo.png", "tray icon"));
        final SystemTray tray = SystemTray.getSystemTray();

        // Create popup menu components
        pickFolder = new MenuItem("Pick Folder");
        startMonitoring = new MenuItem("Start Shuttling");
        stopMonitoring = new MenuItem("Stop Shuttling");
        exitItem = new MenuItem("Stop Shuttle");
        currentDirectory = new MenuItem(getWatchedDirectory());
        watchStatus = new MenuItem(DirectoryWatcher.WatchStatus.INACTIVE.toString());
        selectedTransferType = new MenuItem(TransferSettingsPage.TRANSFER_TYPE.LOCAL.toString());
        currentDirectory.setEnabled(false);
        watchStatus.setEnabled(false);
        selectedTransferType.setEnabled(false);

        transferSettingsPage = new TransferSettingsPage(selectedTransferType);

        updateMenuButtons();

        // Add components to popup menu
        popup.add(watchStatus);
        popup.add(currentDirectory);
        popup.add(selectedTransferType);
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
                transferSettingsPage.createAndShowGUI();
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
                updateMenuButtons();
            }
        });

        stopMonitoring.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                watcher.cancel(true);
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
        chooser.setDialogTitle("Pick folder");
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
