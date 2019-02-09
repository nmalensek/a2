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
import static org.junit.Assert.assertNotEquals;
import static org.mockito.Mockito.*;

/**
 * @author Nick Malensek
 */

@RunWith(MockitoJUnitRunner.class)
public class TestChatbotServer {

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

    @Test
    public void testBlankInput() throws Exception {
        when(mockServerSocket.accept()).thenReturn(mockSocket);
        InputStream inputStream1 = new ByteArrayInputStream(("\n").getBytes());
        InputStream inputStream2 = new ByteArrayInputStream("hello\n".getBytes());

        when(mockSocket.getInputStream()).thenReturn(inputStream1).thenReturn(inputStream2);

        OutputStream outputStream = new ByteArrayOutputStream();
        when(mockSocket.getOutputStream()).thenReturn(outputStream);

        when(mockChatbot.getResponse("")).thenReturn("");
        when(mockChatbot.getResponse("hello")).thenReturn("hello, how are you?");

        for (int i = 0; i < 2; i++) {
            myServer.handleOneClient();
        }

        verify(mockServerSocket, times(2)).accept();
        assertEquals("\nhello, how are you?\n", outputStream.toString());

    }

    @Test
    public void testOneInput() throws Exception {
        when(mockServerSocket.accept()).thenReturn(mockSocket);
        InputStream s = new ByteArrayInputStream(("chatbot\n").getBytes());

        when(mockSocket.getInputStream()).thenReturn(s);

        OutputStream outputStream = new ByteArrayOutputStream();
        when(mockSocket.getOutputStream()).thenReturn(outputStream);

        when(mockChatbot.getResponse("chatbot")).thenReturn("chatbot response");

        myServer.handleOneClient();

        assertEquals("chatbot response", mockChatbot.getResponse("chatbot"));
        assertEquals("chatbot response\n", outputStream.toString());
        verify(mockServerSocket, times(1)).accept();
        verify(mockSocket, times(1)).close();
    }

    @Test
    public void testMultipleInputsSameClient() throws Exception {
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
        verify(mockServerSocket, times(1)).accept();
        verify(mockSocket, times(1)).close();
    }

    @Test
    public void canHandleMultipleClients() throws Exception {
        int i = 0;

        while (i < 5) {
            when(mockServerSocket.accept()).thenReturn(mockSocket);
            InputStream s = new ByteArrayInputStream(("hello" + i + "\n").getBytes());

            when(mockSocket.getInputStream()).thenReturn(s);

            OutputStream outputStream = new ByteArrayOutputStream();
            when(mockSocket.getOutputStream()).thenReturn(outputStream);

            when(mockChatbot.getResponse("hello" + i)).thenReturn("hello" + i + " response");

            myServer.handleOneClient();

            assertEquals("hello" + i + " response\n", outputStream.toString());
            i++;
        }

        verify(mockServerSocket, times(5)).accept();
        verify(mockSocket, times(5)).close();
    }

    @Test
    public void chatBotServerReturnsAiExceptionToClient() throws Exception {
        when(mockServerSocket.accept()).thenReturn(mockSocket);
        InputStream inputStream = new ByteArrayInputStream(("hola\n").getBytes());

        when(mockSocket.getInputStream()).thenReturn(inputStream);

        OutputStream outputStream = new ByteArrayOutputStream();
        when(mockSocket.getOutputStream()).thenReturn(outputStream);

        AIException aiException = new AIException("Input string is not in English");

        when(mockChatbot.getResponse("hola")).thenThrow(aiException);

        myServer.handleOneClient();

        assertEquals("Got AIException: Input string is not in English\n", outputStream.toString());

    }

    @Test
    public void onNetworkExceptionPrintStackTraceAndWaitForNewConnection() throws Exception {

        when(mockServerSocket.accept()).thenReturn(mockSocket);
        InputStream inputStream = new ByteArrayInputStream(("hello\n").getBytes());

        when(mockSocket.getInputStream()).thenThrow(IOException.class).thenReturn(inputStream);

        OutputStream outputStream = new ByteArrayOutputStream();
        when(mockSocket.getOutputStream()).thenReturn(outputStream);

        when(mockChatbot.getResponse("hello")).thenReturn("hello response");

        for (int i = 0; i < 2; i++) {
            myServer.handleOneClient();
        }

        verify(mockChatbot, times(1)).getResponse("hello");
        verify(mockSocket, times(2)).close();
    }
}
