package com.rjkemp.shuttle;

import java.nio.file.*;
import static java.nio.file.StandardCopyOption.*;
import java.io.*;
import java.util.*;

public class PathManager {
    // Display a message, preceded by
    // the name of the current thread
    private static void threadMessage(String message) {
        String threadName = Thread.currentThread().getName();
        System.out.format("%s: %s%n",
                threadName,
                message);
    }

    private static class TaskRunner implements Runnable {
        private List<Path> pathsBatch = new ArrayList<Path>();
        private Path destDir;

        public TaskRunner(List<Path> pathsBatch, Path destDir) {
            this.pathsBatch = pathsBatch;
            this.destDir = destDir;
        }

        public void run() {
            String threadName = Thread.currentThread().getName();
            System.out.format("Processing batch: %s %s %n", threadName,
                    pathsBatch);

            for (int i = 0; i < pathsBatch.size(); i++) {
                // the full path of the changed file e.g. C:\directory\file.extension
                Path path = pathsBatch.get(i);
                Path destPath = Paths.get(destDir.toString(), path.getFileName().toString());
                try {
                    Thread.sleep(50);
                    System.out.format("Moving file %s", path);
                    Files.move(path, destPath, REPLACE_EXISTING, ATOMIC_MOVE);
                    pathsBatch.remove(i);
                } catch (InterruptedException e) {
                    threadMessage("Thread interrupted");
                } catch (IOException exception) {
                    threadMessage(exception.getStackTrace().toString());
                } catch (Exception exception) {
                    System.out.format("Could not move path: %s", destPath);
                }
            }
        }
    }

    public void processBatch(List<Path> paths, Path destDir) throws InterruptedException {
        Thread t = new Thread(new TaskRunner(paths, destDir));
        t.start();
        while (t.isAlive()) {
            threadMessage("Currently moving...");
            t.join(30_000);
        }
        threadMessage("Done.");
    }

}
