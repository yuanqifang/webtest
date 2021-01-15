package kTWOServer;

import java.io.* ;
import java.net.* ;
import java.util.* ;

final class HttpRequest implements Runnable		//多线程
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
    	System.out.println("远程主机地址：" + socket.getRemoteSocketAddress());
    	DataInputStream in = new DataInputStream(socket.getInputStream());	//请求
    	DataOutputStream os = new DataOutputStream(socket.getOutputStream());	//回复
    	String requestLine = in.readLine();	//读取第一行
    	System.out.println(requestLine);
    	String headerLine = null;
    	//循环读取报文段
    	while ((headerLine = in.readLine()).length() != 0) {
            System.out.println(headerLine);
    	}
    	// Extract the filename from the request line.
    	StringTokenizer tokens = new StringTokenizer(requestLine);
    	tokens.nextToken();  // 第一个为 "GET"
    	String fileName = tokens.nextToken();	//第二个为对象路径 ，第三个为协议版本
    	fileName = "." + fileName;	//相对路径
    	if(fileName.equals("./"))	//设置默认打开的文件为index.html
    		fileName +="index.html";
    	//查找文件
    	FileInputStream fis = null;
    	boolean fileExists = true;
    	try {	
    	        fis = new FileInputStream(fileName);	//试图取文件内容
    	} catch (FileNotFoundException e) {
    	        fileExists = false;			//出错文件不存在
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
    //返回消息到浏览器
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
    //文件类型判断
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
	        int port = 8800;		//指定端口
	        //定义socket
	        ServerSocket serverSocket;
	        serverSocket = new ServerSocket(port);
	        //serverSocket.setSoTimeout(10000);	//超时时间
	        //等待连接
	       while(true){
	    	   Socket server = serverSocket.accept();		//发现请求	
		       HttpRequest t = new HttpRequest(server);		//建立连接
		       t.run();		//启动新线程
	       }
	      
	}	
}
