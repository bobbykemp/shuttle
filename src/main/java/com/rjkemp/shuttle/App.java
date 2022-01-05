package com.rjkemp.shuttle;

import java.awt.*;
import java.awt.event.*;
import java.net.URL;
import javax.swing.*;
import java.io.File;
import java.io.IOException;

public class App {
    static File watchedDirectory;
    static MenuItem currentDirectory;

    public static String getWatchedDirectory() {
        if (watchedDirectory == null) {
            return "No watched directory";
        } else {
            return String.format("Watching: %s", watchedDirectory.getAbsolutePath());
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
        final TrayIcon trayIcon = new TrayIcon(createImage("images/bulb.gif", "tray icon"));
        final SystemTray tray = SystemTray.getSystemTray();

        // Create a popup menu components
        MenuItem pickFolder = new MenuItem("Select a folder");
        MenuItem exitItem = new MenuItem("Exit");
        currentDirectory = new MenuItem(getWatchedDirectory());

        // Add components to popup menu
        popup.add(pickFolder);
        popup.add(currentDirectory);
        popup.addSeparator();
        popup.add(exitItem);

        trayIcon.setPopupMenu(popup);

        try {
            tray.add(trayIcon);
        } catch (AWTException e) {
            System.out.println("TrayIcon could not be added.");
            return;
        }

        trayIcon.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(null,
                        "This dialog box is run from System Tray");
            }
        });

        pickFolder.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                createAndShowSelectFolderPopup();
            }
        });

        exitItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                tray.remove(trayIcon);
                System.exit(0);
            }
        });
    }

    private static void createAndShowSelectFolderPopup() {
        JFrame frame = new JFrame("");
        final DirectoryChooser panel = new DirectoryChooser();
        frame.addWindowListener(
                new WindowAdapter() {
                    // used as callback for selection of a new directory to watch
                    public void windowClosing(WindowEvent e) {
                        watchedDirectory = panel.selection;
                        currentDirectory.setLabel(getWatchedDirectory());
                        System.out.println("Now watching directory:" + watchedDirectory);

                        try {
                            new DirectoryWatcher(watchedDirectory.toPath(), true).processEvents();
                        } catch (IOException error) {
                            error.printStackTrace();
                        }
                    }
                });

        frame.getContentPane().add(panel, "Center");
        frame.setSize(panel.getPreferredSize());
        frame.setResizable(false);
        frame.setVisible(true);
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
