package com.rjkemp.shuttle;

import java.awt.*;
import java.awt.Window.Type;
import java.awt.event.*;
import java.awt.event.MouseEvent;
import java.net.URL;
import javax.swing.*;
import java.io.IOException;
import java.nio.file.AccessDeniedException;

public class App {
    static MenuItem pickFolder, startMonitoring, exitItem, currentDirectory, watchStatus, selectedTransferType,
            stopMonitoring, destinationDirectory;
    static JRadioButtonMenuItem transferType;
    static final JFrame masterFrame = new JFrame();
    static final JFrame destinationRemoteDialogFrame = new JFrame();
    static DirectoryWatcher watcher;
    static TransferSettingsPage transferSettingsPage;
    static MainPage mainPage;
    static SelectFolderPopup selectFolderPopupSource, selectFolderPopupDestination;
    static RemoteDialog destinationRemoteDialog;

    public static void updateMenuButtons() {
        // Start Monitoring button
        if (startMonitoring != null) {
            // no watched directory to monitor
            if (selectFolderPopupSource.getWatchedDirectory() == null) {
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

    private static void createAndShowGUI() {
        masterFrame.setUndecorated(true);
        masterFrame.setType(Type.UTILITY);
        masterFrame.setVisible(true);

        destinationRemoteDialogFrame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);

        // Create and set up the content pane.
        RemoteDialog newContentPane = new RemoteDialog(destinationRemoteDialogFrame);
        newContentPane.setOpaque(true); // content panes must be opaque
        destinationRemoteDialogFrame.setContentPane(newContentPane);

        // Display the window.
        destinationRemoteDialogFrame.pack();

        // Check the SystemTray support
        if (!SystemTray.isSupported()) {
            System.out.println("SystemTray is not supported");
            return;
        }

        final PopupMenu popup = new PopupMenu();
        final TrayIcon trayIcon = new TrayIcon(createImage("/logo.png", "tray icon"));
        final SystemTray tray = SystemTray.getSystemTray();

        // Create popup menu components
        pickFolder = new MenuItem("Pick Source");
        startMonitoring = new MenuItem("Start Shuttling");
        stopMonitoring = new MenuItem("Stop Shuttling");
        exitItem = new MenuItem("Stop Shuttle");
        currentDirectory = new MenuItem("No source directory");
        destinationDirectory = new MenuItem("No destination directory");
        watchStatus = new MenuItem(DirectoryWatcher.WatchStatus.INACTIVE.toString());
        selectedTransferType = new MenuItem(TransferSettingsPage.TRANSFER_TYPE.LOCAL.toString());
        currentDirectory.setEnabled(false);
        destinationDirectory.setEnabled(false);
        watchStatus.setEnabled(false);
        selectedTransferType.setEnabled(false);

        Menu destinationTypeMenu = new Menu("Pick Destination");
        final CheckboxMenuItem destinationTypeLocal = new CheckboxMenuItem("Local");
        final CheckboxMenuItem destinationTypeRemote = new CheckboxMenuItem("Remote");

        // user selects local destination transfer
        destinationTypeLocal.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                // set the other button to false to be checkbox-like
                destinationTypeRemote.setState(false);

                selectFolderPopupDestination.createAndShowGUI();

                if (selectFolderPopupDestination.getWatchedDirectory() != null) {
                    destinationTypeLocal.setState(true);
                }

                updateMenuButtons();
            }
        });

        // user selects remote destination transfer
        destinationTypeRemote.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                destinationTypeRemote.setState(true);
                destinationTypeLocal.setState(false);

                destinationRemoteDialogFrame.setVisible(true);
            }
        });

        transferSettingsPage = new TransferSettingsPage(selectedTransferType);
        selectFolderPopupSource = new SelectFolderPopup(currentDirectory, "No source directory",
                "Source directory: %s");
        selectFolderPopupDestination = new SelectFolderPopup(destinationDirectory, "No destination directory",
                "Destination directory: %s");

        destinationRemoteDialog = new RemoteDialog(masterFrame);

        mainPage = new MainPage();

        updateMenuButtons();

        // Add components to popup menu
        popup.add(watchStatus);
        popup.add(currentDirectory);
        popup.add(destinationDirectory);
        popup.add(selectedTransferType);
        popup.addSeparator();
        popup.add(pickFolder);
        popup.add(destinationTypeMenu);
        popup.addSeparator();
        popup.add(startMonitoring);
        popup.add(stopMonitoring);
        popup.addSeparator();

        destinationTypeMenu.add(destinationTypeLocal);
        destinationTypeMenu.add(destinationTypeRemote);

        popup.add(exitItem);

        trayIcon.setPopupMenu(popup);

        trayIcon.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON1) {
                    masterFrame.add(popup);
                    popup.show(masterFrame, e.getX(), e.getY());
                }

            }

            @Override
            public void mousePressed(MouseEvent e) {
                // TODO Auto-generated method stub

            }

            @Override
            public void mouseReleased(MouseEvent e) {
                // TODO Auto-generated method stub

            }

            @Override
            public void mouseEntered(MouseEvent e) {
                // TODO Auto-generated method stub

            }

            @Override
            public void mouseExited(MouseEvent e) {
                // TODO Auto-generated method stub

            }
        });

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
                mainPage.createAndShowGUI();
            }
        });

        // click choose folder button
        pickFolder.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                selectFolderPopupSource.createAndShowGUI();
                updateMenuButtons();
            }
        });

        // click start monitoring button
        startMonitoring.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    (watcher = new DirectoryWatcher(selectFolderPopupSource.getWatchedDirectory().toPath(),
                            selectFolderPopupDestination.getWatchedDirectory().toPath(), true,
                            watchStatus)).execute();
                } catch (AccessDeniedException error) {
                    // show an error dialog
                    ErrorModal.show(
                            String.format("Access denied for folder %s\nFailed to start watching selected directory.",
                                    error.getMessage()));
                } catch (IOException error) {
                    ErrorModal.show(String.format(error.getMessage()));
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

        masterFrame.pack();
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

    private static void initLookAndFeel() {
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
    }

    public static void main(String[] args) {
        initLookAndFeel();

        // Schedule a job for the event-dispatching thread:
        // adding TrayIcon.
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createAndShowGUI();
            }
        });
    }
}