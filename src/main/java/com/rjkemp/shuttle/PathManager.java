package com.rjkemp.shuttle;

import java.nio.file.*;
import static java.nio.file.StandardCopyOption.*;
import java.io.*;
import java.util.*;

import net.schmizz.sshj.SSHClient;
import net.schmizz.sshj.sftp.SFTPClient;

import com.rjkemp.shuttle.Transfer.Type;

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
        private List<Path> successfullyMoved = new ArrayList<Path>();
        private Path destDir;
        private Transfer.Type transferType;
        private SSHClient ssh;

        public TaskRunner(List<Path> pathsBatch, Path destDir, Transfer.Type transferType) {
            this.transferType = transferType;
            this.pathsBatch = pathsBatch;
            this.destDir = destDir;
        }

        public TaskRunner(List<Path> pathsBatch, Path destDir, Transfer.Type transferType, SSHClient ssh) {
            this.ssh = ssh;
            this.transferType = transferType;
            this.pathsBatch = pathsBatch;
            this.destDir = destDir;
        }

        private void moveFile(Path fromPath, Path toPath) throws IOException {
            System.out.format("Moving file: %s %n", fromPath);
            Files.move(fromPath, toPath, REPLACE_EXISTING, ATOMIC_MOVE);
        }

        private void sftpFile(Path path, String toPath) throws IOException {
            final SFTPClient sftp = ssh.newSFTPClient();
            try {
                sftp.put(path.toString(), toPath);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        public void run() {
            String threadName = Thread.currentThread().getName();
            System.out.format("Processing batch: %s %s %n", threadName,
                    pathsBatch);

            for (Path path : pathsBatch) {
                Path destPath = Paths.get(destDir.toString(), path.getFileName().toString());
                try {
                    Thread.sleep(50);
                    if (transferType == Type.MOVE) {
                        moveFile(path, destPath);
                    } else if (transferType == Type.SFTP) {
                        sftpFile(path, "/");
                    }
                    successfullyMoved.add(path);
                } catch (InterruptedException exception) {
                    threadMessage("Thread interrupted");
                } catch (NoSuchFileException exception) {
                    threadMessage("no such file");
                } catch (IOException exception) {
                    exception.printStackTrace();
                } catch (Exception exception) {
                    System.out.format("Could not move path: %s", destPath);
                }
            }

            // remove all paths that were moved successfully
            pathsBatch.removeAll(successfullyMoved);
        }
    }

    public void processBatch(List<Path> paths, Path destDir, Transfer.Type transferType) throws InterruptedException {
        Thread t = new Thread(new TaskRunner(paths, destDir, transferType));
        t.start();
        while (t.isAlive()) {
            threadMessage("Currently moving...");
            t.join(30_000);
        }
        threadMessage("Done.");
    }

    public void processBatch(List<Path> paths, Path destDir, Transfer.Type transferType, SSHClient ssh)
            throws InterruptedException {
        Thread t = new Thread(new TaskRunner(paths, destDir, transferType, ssh));
        t.start();
        while (t.isAlive()) {
            threadMessage("Currently moving...");
            t.join(30_000);
        }
        threadMessage("Done.");
    }

}
