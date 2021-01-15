package kTWOServer;

import java.io.* ;
import java.net.* ;
import java.util.* ;

final class HttpRequest implements Runnable		//���߳�
{
	final static String CRLF = "\r\n";
    Socket socket;

    // Constructor
    public HttpRequest(Socket socket) throws Exception 
    {
           this.socket = socket;
    }

    // Implement the run() method of the Runnable interface.
    public void run()
    {
	     try {
	    	 processRequest();
	     } catch (Exception e) {
	         System.out.println(e);
	     }
    }

    @SuppressWarnings("deprecation")
	private void processRequest() throws Exception
    {
    	System.out.println("Զ��������ַ��" + socket.getRemoteSocketAddress());
    	DataInputStream in = new DataInputStream(socket.getInputStream());	//����
    	DataOutputStream os = new DataOutputStream(socket.getOutputStream());	//�ظ�
    	String requestLine = in.readLine();	//��ȡ��һ��
    	System.out.println(requestLine);
    	String headerLine = null;
    	//ѭ����ȡ���Ķ�
    	while ((headerLine = in.readLine()).length() != 0) {
            System.out.println(headerLine);
    	}
    	// Extract the filename from the request line.
    	StringTokenizer tokens = new StringTokenizer(requestLine);
    	tokens.nextToken();  // ��һ��Ϊ "GET"
    	String fileName = tokens.nextToken();	//�ڶ���Ϊ����·�� ��������ΪЭ��汾
    	fileName = "." + fileName;	//���·��
    	if(fileName.equals("./"))	//����Ĭ�ϴ򿪵��ļ�Ϊindex.html
    		fileName +="index.html";
    	//�����ļ�
    	FileInputStream fis = null;
    	boolean fileExists = true;
    	try {	
    	        fis = new FileInputStream(fileName);	//��ͼȡ�ļ�����
    	} catch (FileNotFoundException e) {
    	        fileExists = false;			//�����ļ�������
    	}

    	String statusLine = null;
    	String contentTypeLine = null;
    	String entityBody = null;
    	if (fileExists) {
    	        statusLine = "HTTP/1.1 200 OK" + CRLF;
    	        contentTypeLine = "Content-type: " + 
    	               contentType( fileName ) + CRLF;
    	} else {
    	        statusLine = "HTTP/1.1 404 Not Found" + CRLF;
    	        contentTypeLine = "Content-Type: text/html" + CRLF;
    	        entityBody = "<HTML>" + 
    	               "<HEAD><TITLE>Not Found</TITLE></HEAD>" +
    	               "<BODY>Not Found</BODY></HTML>";
    	}
    	// Send the status line.
    	os.writeBytes(statusLine);
    	 
    	// Send the content type line.
    	os.writeBytes(contentTypeLine);
    	 
    	// Send a blank line to indicate the end of the header lines.
    	os.writeBytes(CRLF);
    	// Send the entity body.
    	if (fileExists) {
    	        sendBytes(fis, os);
    	        fis.close();
    	} else {
    	        os.writeBytes(entityBody);
    	}
    	in.close();
    	os.close();
    	socket.close();
    }
    //������Ϣ�������
    private static void sendBytes(FileInputStream fis, OutputStream os) 
    		throws Exception
    		{
    		   // Construct a 1K buffer to hold bytes on their way to the socket.
    		   byte[] buffer = new byte[1024];
    		   int bytes = 0;
    		 
    		   // Copy requested file into the socket's output stream.
    		   while((bytes = fis.read(buffer)) != -1 ) {
    		      os.write(buffer, 0, bytes);
    		   }
    		}
    //�ļ������ж�
    private static String contentType(String fileName)
    {
            if(fileName.endsWith(".htm") || fileName.endsWith(".html")) {
                   return "text/html";
            }
            if(fileName.endsWith(".jpg")) {
            	return "image/jpg";
            }
            if(fileName.endsWith(".txt")) {
            	return "text/txt";
            }
            return "application/octet-stream";
    }
    
}

public class WebServer {
	
	@SuppressWarnings("resource")
	public static void main(String argv[]) throws Exception
	{
	        int port = 8800;		//ָ���˿�
	        //����socket
	        ServerSocket serverSocket;
	        serverSocket = new ServerSocket(port);
	        //serverSocket.setSoTimeout(10000);	//��ʱʱ��
	        //�ȴ�����
	       while(true){
	    	   Socket server = serverSocket.accept();		//��������	
		       HttpRequest t = new HttpRequest(server);		//��������
		       t.run();		//�������߳�
	       }
	      
	}	
}
