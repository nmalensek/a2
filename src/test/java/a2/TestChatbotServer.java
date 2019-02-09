package a2;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

/**
 *
 * @author <Nick Malensek>
 */

@RunWith(MockitoJUnitRunner.class)
public class TestChatbotServer {

    public String lineSeparator = System.getProperty("line.separator");

    @Mock
    public Chatbot mockChatbot;

    @Mock
    public ServerSocket mockServerSocket;

    @Mock
    public Socket mockSocket;

    public ChatbotServer myServer;

    @Before
    public void setUp() {
        myServer = new ChatbotServer(mockChatbot, mockServerSocket);
    }

    @After
    public void tearDown() {

    }

//    @Test
//    public void testOutput() throws Exception{
//        when(mockServerSocket.accept()).thenReturn(mockSocket);
//
//        OutputStream outputStream = new ByteArrayOutputStream();
//        when(mockSocket.getOutputStream()).thenReturn(outputStream);
//
//        myServer.handleOneClient();
//
//        assertEquals("Output\n", outputStream.toString());
//    }

    @Test
    public void testOneInput() throws IOException, AIException {
        when(mockServerSocket.accept()).thenReturn(mockSocket);
        InputStream s = new ByteArrayInputStream(("chatbot\n").getBytes());

        when(mockSocket.getInputStream()).thenReturn(s);

        OutputStream outputStream = new ByteArrayOutputStream();
        when(mockSocket.getOutputStream()).thenReturn(outputStream);

        when(mockChatbot.getResponse("chatbot")).thenReturn("chatbot response");

        myServer.handleOneClient();

        assertEquals("chatbot response", mockChatbot.getResponse("chatbot"));
        assertEquals("chatbot response\n", outputStream.toString());
    }

    @Test
    public void testMultipleInputs() throws IOException, AIException {
        when(mockServerSocket.accept()).thenReturn(mockSocket);
        InputStream s = new ByteArrayInputStream(("hello\nhow are you?\nstop copying me").getBytes());

        when(mockSocket.getInputStream()).thenReturn(s);

        OutputStream outputStream = new ByteArrayOutputStream();
        when(mockSocket.getOutputStream()).thenReturn(outputStream);

        when(mockChatbot.getResponse("hello")).thenReturn("hello response");
        when(mockChatbot.getResponse("how are you?")).thenReturn("how are you? response");
        when(mockChatbot.getResponse("stop copying me")).thenReturn("stop copying me response");

        myServer.handleOneClient();

        assertEquals("hello response\nhow are you? response\nstop copying me response\n", outputStream.toString());
    }

    @Test
    public void canHandleMultipleClients() throws IOException, AIException {
        int i = 0;

        while (i < 5) {
            when(mockServerSocket.accept()).thenReturn(mockSocket);
            InputStream s = new ByteArrayInputStream(("hello" + i + "\n").getBytes());

            when(mockSocket.getInputStream()).thenReturn(s);

            OutputStream outputStream = new ByteArrayOutputStream();
            when(mockSocket.getOutputStream()).thenReturn(outputStream);

            when(mockChatbot.getResponse("hello" + i)).thenReturn("hello" + i + " response");

            myServer.handleOneClient();

            assertEquals("hello" + i +" response\n", outputStream.toString());
            i++;
        }

        verify(mockServerSocket, times(5)).accept();
    }

    @Test
    public void clientCanDisconnect() throws IOException {

    }

    @Test
    public void newClientCanConnectAfterDisconnect() throws IOException {

    }

    @Test
    public void chatBotServerReturnsAiExceptionToClient() {

    }

    @Test
    public void onNetworkExceptionPrintStackTraceAndWaitForNewConnection() {

    }
}
