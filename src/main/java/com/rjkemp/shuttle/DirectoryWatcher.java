package com.rjkemp.shuttle;

import java.nio.file.*;
import static java.nio.file.StandardWatchEventKinds.*;
import static java.nio.file.StandardCopyOption.*;
import static java.nio.file.LinkOption.*;
import java.nio.file.attribute.*;
import javax.swing.SwingWorker;
import java.io.*;
import java.awt.MenuItem;
import java.util.*;

public class DirectoryWatcher extends SwingWorker<Void, Path> {
    private final WatchService watcher;
    private final Map<WatchKey, Path> keys;
    private List<Path> paths = new ArrayList<Path>();
    private final Path destDir;
    private final boolean recursive;
    private boolean trace = false;
    private PathManager pathManager = new PathManager();

    private MenuItem statusLabel;

    public static enum WatchStatus {
        INACTIVE,
        ACTIVE,
        SCANNING,
        READY,
    }

    private WatchStatus status;

    private void setWatcherStatus(WatchStatus status) {
        System.out.println(status.toString());
        this.status = status;
        if (this.statusLabel != null) {
            this.statusLabel.setLabel(status.toString());
        }
    }

    public WatchStatus getWatchStatus() {
        return status;
    }

    public List<Path> getPaths() {
        return this.paths;
    }

    @SuppressWarnings("unchecked")
    static <T> WatchEvent<T> cast(WatchEvent<?> event) {
        return (WatchEvent<T>) event;
    }

    /**
     * Register the given directory with the WatchService
     */
    private void register(Path dir) throws IOException {
        WatchKey key = dir.register(watcher, ENTRY_CREATE, ENTRY_MODIFY);
        if (trace) {
            Path prev = keys.get(key);
            if (prev == null) {
                System.out.format("register: %s\n", dir);
            } else {
                if (!dir.equals(prev)) {
                    System.out.format("update: %s -> %s\n", prev, dir);
                }
            }
        }
        keys.put(key, dir);
    }

    /**
     * Register the given directory, and all its sub-directories, with the
     * WatchService.
     */
    private void registerAll(final Path start) throws IOException {
        // register directory and sub-directories
        Files.walkFileTree(start, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs)
                    throws IOException {
                register(dir);
                System.out.println(dir.toAbsolutePath().toString());
                for (final File entry : dir.toFile().listFiles()) {
                    if (entry.isFile()) {
                        System.out.println(entry.getName());
                    }
                }
                return FileVisitResult.CONTINUE;
            }
        });
    }

    DirectoryWatcher(Path sourceDir, Path destDir, boolean recursive, MenuItem statusLabel) throws IOException {
        setWatcherStatus(WatchStatus.INACTIVE);

        this.destDir = destDir;
        this.statusLabel = statusLabel;
        this.watcher = FileSystems.getDefault().newWatchService();
        this.keys = new HashMap<WatchKey, Path>();
        this.recursive = recursive;

        if (recursive) {
            setWatcherStatus(WatchStatus.SCANNING);
            System.out.format("Scanning %s ...\n", sourceDir);
            registerAll(sourceDir);
            System.out.println("Done.");
        } else {
            register(sourceDir);
        }

        setWatcherStatus(WatchStatus.READY);

        // enable trace after initial registration
        this.trace = true;
    }

    /**
     * Creates a WatchService and registers the given directory
     */
    DirectoryWatcher(Path dir, boolean recursive, MenuItem statusLabel) throws IOException {
        setWatcherStatus(WatchStatus.INACTIVE);

        this.destDir = null;
        this.statusLabel = statusLabel;
        this.watcher = FileSystems.getDefault().newWatchService();
        this.keys = new HashMap<WatchKey, Path>();
        this.recursive = recursive;

        if (recursive) {
            setWatcherStatus(WatchStatus.SCANNING);
            System.out.format("Scanning %s ...\n", dir);
            registerAll(dir);
            System.out.println("Done.");
        } else {
            register(dir);
        }

        setWatcherStatus(WatchStatus.READY);

        // enable trace after initial registration
        this.trace = true;
    }

    /**
     * Process all events for keys queued to the watcher
     */
    @Override
    protected Void doInBackground() {
        setWatcherStatus(WatchStatus.ACTIVE);

        while (!isCancelled()) {
            // wait for key to be signalled
            WatchKey key;

            try {
                key = watcher.take();
            } catch (InterruptedException x) {
                setWatcherStatus(WatchStatus.INACTIVE);
                return null;
            }

            Path dir = keys.get(key);
            if (dir == null) {
                System.err.println("WatchKey not recognized!!");
                continue;
            }

            for (WatchEvent<?> event : key.pollEvents()) {
                WatchEvent.Kind kind = event.kind();

                // TBD - provide example of how OVERFLOW event is handled
                if (kind == OVERFLOW) {
                    System.out.println("OVERFLOW");
                    continue;
                }

                // Context for directory entry event is the file name of entry
                WatchEvent<Path> ev = cast(event);
                Path name = ev.context();
                Path child = dir.resolve(name);

                // print out event
                System.out.format("%s: %s\n", event.kind().name(), child);
                publish(child);

                // if directory is created, and watching recursively, then
                // register it and its sub-directories
                if (recursive && (kind == ENTRY_CREATE)) {
                    try {
                        if (Files.isDirectory(child, NOFOLLOW_LINKS)) {
                            registerAll(child);
                        }
                    } catch (IOException x) {
                        // ignore to keep sample readbale
                    }
                }
            }

            // reset key and remove from set if directory no longer accessible
            boolean valid = key.reset();
            if (!valid) {
                keys.remove(key);

                // all directories are inaccessible
                if (keys.isEmpty()) {
                    break;
                }
            }
        }

        return null;
    }

    @Override
    protected void process(List<Path> paths) {
        try {
            pathManager.processBatch(paths, destDir);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
