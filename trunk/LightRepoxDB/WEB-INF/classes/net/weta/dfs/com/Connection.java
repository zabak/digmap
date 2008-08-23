package net.weta.dfs.com;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketException;

import net.weta.dfs.util.FileUtil;

/**
 * A connection with which you can communicate with a server through request
 * command & response command or simply transfer data.
 * 
 * <br/><br/>created on 19.01.2005
 * 
 * @version $Revision: 1.3 $
 */
public class Connection {

    private InputStream fInputStream;

    private OutputStream fOutputStream;

    private Socket fServerSocket;

    private String fIpAddress;

    private int fPort;

    private ObjectOutputStream fOoStream;

    private ObjectInputStream fOiStream;

    /**
     * @param ipAddress
     * @param port
     */
    public Connection(String ipAddress, int port) {
        this.fIpAddress = ipAddress;
        this.fPort = port;
        this.fServerSocket = null;
    }

    /**
     * @return Returns the ipAddress.
     */
    public String getIpAddress() {
        return this.fIpAddress;
    }

    /**
     * @return Returns the port.
     */
    public int getPort() {
        return this.fPort;
    }

    /**
     * @return The next byte of the data or -1.
     * @throws IOException
     */
    public int read() throws IOException {
        return this.fInputStream.read();
    }

    /**
     * @param data
     * @return The total number of bytes read into the buffer, or -1 if there is
     *         no more data because the end of the stream has been reached.
     * @throws IOException
     */
    public int read(byte[] data) throws IOException {
        return this.fInputStream.read(data);
    }

    /**
     * @throws IOException
     */
    public void flush() throws IOException {
        this.fOutputStream.flush();
    }

    /**
     * @param data
     * @throws IOException
     */
    public void write(int data) throws IOException {
        this.fOutputStream.write(data);
    }

    /**
     * @param data
     * @throws IOException
     */
    public void write(byte[] data) throws IOException {
        this.fOutputStream.write(data);
    }

    /**
     * @param data
     * @param off
     * @param length
     * @throws IOException
     */
    public void write(byte[] data, int off, int length) throws IOException {
        this.fOutputStream.write(data, off, length);
    }

    /**
     * @param data
     * @throws IOException
     */
    public void writeInt(int data) throws IOException {
        FileUtil.writeInt(this.fOutputStream, data);
    }

    /**
     * @return The number of bytes that can be read from this input stream
     *         without blocking.
     * @throws IOException
     *  
     */
    public int available() throws IOException {
        return this.fInputStream.available();
    }

    /**
     * @return The ComandResponse object.
     * @throws IOException
     *             if there is a connection error
     * @throws CommandException
     *             if there is a server error
     */
    public CommandResult receiveResponse() throws IOException, CommandException {
        ICommandResponse responseObject;
        try {
            responseObject = (ICommandResponse) this.fOiStream.readObject();
        } catch (ClassNotFoundException e) {
            throw new IOException(e.getMessage());
        }
        if (responseObject instanceof CommandException)
            throw (CommandException) responseObject;

        return (CommandResult) responseObject;
    }

    /**
     * @param request
     * @throws IOException
     */
    public void sendRequest(CommandRequest request) throws IOException {
        if (!isConnected()) {
            connect();
        }
        try {
            this.fOoStream.writeObject(request);
            this.fOoStream.flush();
        } catch (SocketException e) {
            // if connection is lost, try one reconnect
            connect();
            this.fOoStream.writeObject(request);
            this.fOoStream.flush();
        }
    }

    /**
     * Connects to the server. Is automatically called on an request.
     * 
     * @throws IOException
     */
    public void connect() throws IOException {
        this.fServerSocket = new Socket(this.fIpAddress, this.fPort);
        this.fInputStream = this.fServerSocket.getInputStream();
        this.fOutputStream = this.fServerSocket.getOutputStream();

        this.fOoStream = new ObjectOutputStream(this.fOutputStream);
        this.fOiStream = new ObjectInputStream(this.fInputStream);
    }

    /**
     * @return Returns True if the connection is established otherwise false.
     */
    public boolean isConnected() {
        return this.fServerSocket != null && this.fServerSocket.isConnected();
    }

    /**
     * @throws IOException
     *  
     */
    public void close() throws IOException {
        if (isConnected()) {
            this.fServerSocket.close();
        }
        this.fServerSocket = null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#finalize()
     */
    protected void finalize() throws Throwable {
        close();
        super.finalize();
    }
}