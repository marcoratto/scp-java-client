/*
 *  Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */
package uk.co.marcoratto.apache.ssh;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.JSchException;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Iterator;

import org.apache.log4j.Logger;

import uk.co.marcoratto.scp.SCPPException;

/**
 * Utility class to carry out an upload scp transfer.
 */
public class ScpToMessage extends AbstractSshMessage {
	
	private static Logger logger = Logger.getLogger(ScpToMessage.class);
	
    private static final int HUNDRED_KILOBYTES = 102400;
    private static final int BUFFER_SIZE = 1024;

    private File localFile;
    private String remotePath;
    private List<Directory> directoryList;

    /**
     * Constructor for ScpToMessage
     * @param session the ssh session to use
     */
    public ScpToMessage(Session session) {
        super(session);
    }

    /**
     * Constructor for ScpToMessage
     * @param verbose if true do verbose logging
     * @param session the ssh session to use
     * @since Ant 1.7
     */
    public ScpToMessage(boolean verbose, Session session) {
        super(verbose, session);
    }

    /**
     * Constructor for a local file to remote.
     * @param verbose if true do verbose logging
     * @param session the scp session to use
     * @param aLocalFile the local file
     * @param aRemotePath the remote path
     * @since Ant 1.6.2
     */
    public ScpToMessage(boolean verbose,
                        Session session,
                        File aLocalFile,
                        String aRemotePath) {
        this(verbose, session, aRemotePath);

        this.localFile = aLocalFile;
    }

    /**
     * Constructor for a local directories to remote.
     * @param verbose if true do verbose logging
     * @param session the scp session to use
     * @param aDirectoryList a list of directories
     * @param aRemotePath the remote path
     * @since Ant 1.6.2
     */
    public ScpToMessage(boolean verbose,
                        Session session,
                        List<Directory> aDirectoryList,                        
                        String aRemotePath) {
        this(verbose, session, aRemotePath);

        this.directoryList = aDirectoryList;
    }

    /**
     * Constructor for ScpToMessage.
     * @param verbose if true do verbose logging
     * @param session the scp session to use
     * @param aRemotePath the remote path
     * @since Ant 1.6.2
     */
    private ScpToMessage(boolean verbose,
                         Session session,
                         String aRemotePath) {
        super(verbose, session);
        this.remotePath = aRemotePath;
    }

    /**
     * Constructor for ScpToMessage.
     * @param session the scp session to use
     * @param aLocalFile the local file
     * @param aRemotePath the remote path
     */
    public ScpToMessage(Session session,
                        File aLocalFile,
                        String aRemotePath) {
        this(false, session, aLocalFile, aRemotePath);
    }

    /**
     * Constructor for ScpToMessage.
     * @param session the scp session to use
     * @param aDirectoryList a list of directories
     * @param aRemotePath the remote path
     */
    public ScpToMessage(Session session,
                         List<Directory> aDirectoryList,
                         String aRemotePath) {
        this(false, session, aDirectoryList, aRemotePath);
    }

    /**
     * Carry out the transfer.
     * @throws IOException on i/o errors
     * @throws JSchException on errors detected by scp
     * @throws SCPPException 
     */
    public void execute() throws IOException, JSchException, SCPPException {
        if (directoryList != null) {
            doMultipleTransfer();
        }
        if (localFile != null) {
            doSingleTransfer();
        }
        logger.info("done.\n");
    }

    private void doSingleTransfer() throws IOException, JSchException, SCPPException {
        String cmd = "scp -t " + remotePath;
        Channel channel = openExecChannel(cmd);
        try {

            OutputStream out = channel.getOutputStream();
            InputStream in = channel.getInputStream();

            channel.connect();

            waitForAck(in);
            sendFileToRemote(localFile, in, out);
        } finally {
            if (channel != null) {
                channel.disconnect();
            }
        }
    }

    private void doMultipleTransfer() throws IOException, JSchException, SCPPException {
        Channel channel = openExecChannel("scp -r -d -t " + remotePath);
        try {
            OutputStream out = channel.getOutputStream();
            InputStream in = channel.getInputStream();

            channel.connect();

            waitForAck(in);
            for (Iterator<Directory> i = directoryList.iterator(); i.hasNext();) {
                Directory current = (Directory) i.next();
                sendDirectory(current, in, out);
            }
        } finally {
            if (channel != null) {
                channel.disconnect();
            }
        }
    }

    private void sendDirectory(Directory current,
                               InputStream in,
                               OutputStream out) throws IOException, SCPPException {
        for (Iterator<File> fileIt = current.filesIterator(); fileIt.hasNext();) {
        	sendFileToRemote(fileIt.next(), in, out);
        }
        for (Iterator<Directory> dirIt = current.directoryIterator(); dirIt.hasNext();) {
            Directory dir = dirIt.next();
            sendDirectoryToRemote(dir, in, out);
        }
    }

    private void sendDirectoryToRemote(Directory directory,
                                        InputStream in,
                                        OutputStream out) throws IOException, SCPPException {
        String command = "D0755 0 ";        
        command += directory.getDirectory().getName();
        command += "\n";

        if (this.getVerbose()) {
            logger.info("scp command is " + command);
        }
        
        out.write(command.getBytes());
        out.flush();

        waitForAck(in);
        sendDirectory(directory, in, out);
        out.write("E\n".getBytes());
        out.flush();
        waitForAck(in);
    }

    private void sendFileToRemote(File localFile,
                                  InputStream in,
                                  OutputStream out) throws IOException, SCPPException {
        // send "C0644 filesize filename", where filename should not include '/'
        long filesize = localFile.length();
        String command = "C0644 " + filesize + " ";
        command += localFile.getName();
        command += "\n";

        if (this.getVerbose()) {
            logger.info("scp command is " + command);
        }
        
        out.write(command.getBytes());
        out.flush();

        waitForAck(in);

        // send a content of lfile
        FileInputStream fis = new FileInputStream(localFile);
        byte[] buf = new byte[BUFFER_SIZE];
        long startTime = System.currentTimeMillis();
        long totalLength = 0;

        // only track progress for files larger than 100kb in verbose mode
        boolean trackProgress = getVerbose() && filesize > HUNDRED_KILOBYTES;
        // since filesize keeps on decreasing we have to store the
        // initial filesize
        long initFilesize = filesize;
        int percentTransmitted = 0;

        try {
            if (this.getVerbose()) {
                logger.info("Sending file " + localFile.getName() + " (" + localFile.length() + " bytes)");
            }
            while (true) {
                int len = fis.read(buf, 0, buf.length);
                if (len <= 0) {
                    break;
                }
                out.write(buf, 0, len);
                totalLength += len;

                if (trackProgress) {
                    percentTransmitted = trackProgress(initFilesize,
                                                       totalLength,
                                                       percentTransmitted);
                }
            }
            out.flush();
            sendAck(out);
            waitForAck(in);
        } finally {
            if (this.getVerbose()) {
                long endTime = System.currentTimeMillis();
                logStats(startTime, endTime, totalLength);
            }
            fis.close();
        }
    }

    /**
     * Get the local file
     * @return the local file
     */
    public File getLocalFile() {
        return localFile;
    }

    /**
     * Get the remote path
     * @return the remote path
     */
    public String getRemotePath() {
        return remotePath;
    }
}
