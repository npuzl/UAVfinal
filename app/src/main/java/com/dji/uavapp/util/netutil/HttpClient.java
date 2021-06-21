package com.dji.uavapp.util.netutil;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Date;
import java.util.StringTokenizer;

/**
 * Class <em>HttpClient</em> is a class representing a simple HTTP client.
 *
 * @author
 */

public class HttpClient {
	int n = 0;
	/**
	 * default HTTP port is port 80
	 */
	private static int port = 80;
	//File root = new File("D:\\code");
	/**
	 * Allow a maximum buffer size of 8192 bytes
	 */
	private static int buffer_size = 1024;

	/**
	 * Response is stored in a byte array.
	 */
	private byte[] buffer;

	/**
	 * My socket to the world.
	 */
	Socket socket = null;

	/**
	 * Default port is 80.
	 */
	private static final int PORT = 100;

	/**
	 * Output stream to the socket.
	 */
	BufferedOutputStream ostream = null;

	/**
	 * Input stream from the socket.
	 */
	BufferedInputStream istream = null;

	/**
	 * StringBuffer storing the header
	 */
	private StringBuffer header = null;

	/**
	 * StringBuffer storing the response.
	 */
	private StringBuffer response = null;
	private String request = null;
	/**
	 * String to represent the Carriage Return and Line Feed character sequence.
	 */
	static private String CRLF = "\r\n";

	/**
	 * HttpClient constructor;
	 */
	public HttpClient() {
		buffer = new byte[buffer_size];
		header = new StringBuffer();
		response = new StringBuffer();
		request = new String();
	}

	/**
	 * <em>connect</em> connects to the input host on the default http port --
	 * port 80. This function opens the socket and creates the input and output
	 * streams used for communication.
	 */
	public void connect(String host) throws Exception {

		/**
		 * Open my socket to the specified host at the default port.
		 */
		socket = new Socket(host, PORT);
		/**
		 * 设置socket读的阻塞时间
		 */
		socket.setSoTimeout(5000);//put image8000   get image 2000    4000
		/**
		 * Create the output stream.
		 */
		ostream = new BufferedOutputStream(socket.getOutputStream());

		/**
		 * Create the input stream.
		 */
		istream = new BufferedInputStream(socket.getInputStream());
	}

	/**
	 *
	 * @param request  下载的命令
	 * @param savepath 文件保存位置
	 * @return 成功，失败
	 * @throws Exception
	 */
	public String processGetRequest(String request,String savepath) throws Exception {
		/**
		 * get请求设置阻塞时间
		 */
		socket.setSoTimeout(3000);
		/**
		 * 发送命令字符串
		 */
		request += CRLF + CRLF;
		buffer = request.getBytes();
		ostream.write(buffer, 0, request.length());
		ostream.flush();
		/**
		 * 处理服务器是否接受到命令
		 */
		processRequestresponse();
		/**
		 * 接收数据
		 */
		String result = processGetresponse(savepath);
		/**
		 * 判断服务器是否已经处理完命令，处理完才可以进行下一个请求，达到双方同步的要求
		 */
		processDataresponse();
		return result;
	}

	/**
	 *
	 * @param request 登陆的命令
	 * @return 成功，失败
	 * @throws Exception
	 */
	public String processLoginRequest(String request) throws Exception {
		request += CRLF + CRLF;
		/**
		 * 发送命令字符串
		 */
		buffer = request.getBytes();
		ostream.write(buffer, 0, request.length());
		ostream.flush();
		System.out.println("login request");
		/**
		 * 处理服务器是否接受到命令
		 */
		processRequestresponse();
		/**
		 * 判断服务器是否已经处理完命令，处理完才可以进行下一个请求，达到双方同步的要求
		 */
		return processDataresponse();
	}

	/**
	 *
	 * @param request
	 * @return
	 * @throws Exception
	 */
	public String processCountFileRequest(String request) throws Exception {
		request += CRLF + CRLF;
		/**
		 * 发送命令字符串
		 */
		buffer = request.getBytes();
		ostream.write(buffer, 0, request.length());
		ostream.flush();
		System.out.println("CountFile request");
		/**
		 * 处理服务器是否接受到命令
		 */
		processRequestresponse();
		/**
		 * 判断服务器是否已经处理完命令，处理完才可以进行下一个请求，达到双方同步的要求
		 */
		return processResponse();
	}

	public String processPriviewDataRequest(String request,String savepath) throws Exception {
		/**
		 * get请求设置阻塞时间
		 */
		socket.setSoTimeout(3000);
		/**
		 * 发送命令字符串
		 */
		request += CRLF + CRLF;
		buffer = request.getBytes();
		ostream.write(buffer, 0, request.length());
		ostream.flush();
		/**
		 * 处理服务器是否接受到命令
		 */
		processRequestresponse();
		/**
		 * 接收数据
		 */
		String result = processGetresponse(savepath);
		/**
		 * 判断服务器是否已经处理完命令，处理完才可以进行下一个请求，达到双方同步的要求
		 */
		processDataresponse();
		return result;
	}
	/**
	 * 查询并返回用户的相关信息
	 * @param request
	 * @return
	 * @throws Exception
	 */
	public String processQueryRequest(String request) throws Exception {
		request += CRLF + CRLF;
		/**
		 * 发送命令字符串
		 */
		buffer = request.getBytes();
		ostream.write(buffer, 0, request.length());
		ostream.flush();
		System.out.println("query request");
		/**
		 * 处理服务器是否接受到命令
		 */
		processRequestresponse();

		return processResponse();
		/**
		 * 判断服务器是否已经处理完命令，处理完才可以进行下一个请求，达到双方同步的要求
		 */
	}

	public String processUpdateRequest(String request) throws Exception{
		request += CRLF+CRLF;
		buffer = request.getBytes();
		ostream.write(buffer, 0, request.length());
		ostream.flush();
		System.out.println("update request");
		processRequestresponse();

		return processDataresponse();
	}

	/**
	 *
	 * @param request 注册命令
	 * @return 成功，失败
	 * @throws Exception
	 */
	public String processRegisterRequest(String request) throws Exception {
		request += CRLF + CRLF;
		/**
		 * 发送命令字符串
		 */
		buffer = request.getBytes();
		ostream.write(buffer, 0, request.length());
		ostream.flush();
		/**
		 * 处理服务器是否接受到命令
		 */
		processRequestresponse();
		/**
		 * 判断服务器是否已经处理完命令，处理完才可以进行下一个请求，达到双方同步的要求
		 */
		return processDataresponse();
	}

	/**
	 * @param request
	 * @param filepath 上传文件所在位置
	 * @return 成功，失败
	 * @throws Exception
	 */
	public String processPutRequest(String request,String filepath) throws Exception {
		//String filePath = null;
		//StringTokenizer tokenizer = new StringTokenizer(request," ");
		int size = 0;
		long content_length = 0;
		/**
		 * 设置传输阻塞时间
		 */
		socket.setSoTimeout(10000);
		request += CRLF;

		try {
			File sendFile = new File(filepath);
			if (sendFile.isFile()) {
				content_length = sendFile.length();
				request += "Content-Length: "+content_length+CRLF+CRLF;
				buffer = request.getBytes();
				/**
				 * 发送命令字符串
				 */
				ostream.write(buffer, 0, request.length());
				ostream.flush();
				System.out.println("put start");
				/**
				 * 处理服务器是否接受到命令
				 */
				processRequestresponse();
				try {
					/**
					 * 发送数据
					 */
					BufferedInputStream fis = new BufferedInputStream(new FileInputStream(sendFile));
					byte[] sendBuffer = new byte[buffer_size];
					while(true) {
						int readed = fis.read(sendBuffer);
						if(readed == -1) {
							break;
						}
						ostream.write(sendBuffer, 0, readed);
						ostream.flush();
					}
					fis.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
				/**
				 * 等待服务器处理完毕返回反馈
				 */
				return processDataresponse();
			}else {
				System.out.println("该文件不存在");
				return "失败";
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "失败";
	}

	/**
	 * 在发送下载命令后的接收数据操作
	 * @param savepath 下载文件的保存路径，如：D：/code/smile.jpg
	 * @throws IOException
	 */
	public String processGetresponse(String savepath) throws IOException {
		int last = 0, c = 0;
		/**
		 * 接受对面的关于下载目标是否存在的反馈，200表示目标存在可以下载
		 */
		boolean inHeader = true; // loop control
		FileOutputStream outfile = null;
		try {
			while (inHeader && ((c = istream.read()) != -1)) {
				switch (c) {
					case '\r':
						break;
					case '\n':
						if (c == last) {
							inHeader = false;
							break;
						}
						last = c;
						header.append("\n");
						break;
					default:
						last = c;
						header.append((char) c);
				}
			}
			StringTokenizer tokenizer = new StringTokenizer(getHeader(), " ");
			tokenizer.nextToken();
			String statusCode = tokenizer.nextToken();
			if (statusCode.equals("200")) {
				outfile = new FileOutputStream(savepath);
				int size = 0;
				return202();
				while (true) {
					size = istream.read(buffer);
					if (size == -1) {
						break;
					}
					outfile.write(buffer, 0, size);
					outfile.flush();
				}
				outfile.close();
			}else{
				System.out.println("下载的文件不存在");
				return "失败";
			}
		}catch (Exception e){
			e.printStackTrace();
		}finally {
			/**
			 * 在读完数据之后告诉服务器下载命令客户端已经执行完毕
			 */
			return202();
			header.delete(0, header.length());
			System.out.println("文件下载执行完毕");
			return "成功";//判断文件大小是否符合
		}
	}

	/**
	 * 得到命令发给服务端后，服务端的回应
	 * @throws Exception
	 */
	public void processRequestresponse() throws Exception {
		int last = 0, c = 0;
		/**
		 * Process the header and add it to the header StringBuffer.
		 */
		boolean inHeader = true; // loop control
		while (inHeader && ((c = istream.read()) != -1)) {
			switch (c) {
				case '\r':
					break;
				case '\n':
					if (c == last) {
						inHeader = false;
						break;
					}
					last = c;
					header.append("\n");
					break;
				default:
					last = c;
					header.append((char) c);
			}
		}
		StringTokenizer tokenizer = new StringTokenizer(getHeader(), " ");
		tokenizer.nextToken();
		String statusCode = tokenizer.nextToken();
		if (statusCode.equals("201")) {
			System.out.println("命令已接受");
			header.delete(0, header.length());
		}else{
			System.out.println("命令未到达，或命令错乱");
		}
	}

	/**
	 * 得到服务器执行命令是否完成的反馈
	 * @throws Exception
	 */
	public String processDataresponse() throws Exception {
		int last = 0,c = 0;

		boolean inHeader = true; // loop control
		while (inHeader && ((c = istream.read()) != -1)) {
			switch (c) {
				case '\r':
					break;
				case '\n':
					if (c == last) {
						inHeader = false;
						break;
					}
					last = c;
					header.append("\n");
					break;
				default:
					last = c;
					header.append((char) c);
			}
		}
		StringTokenizer tokenizer2 = new StringTokenizer(getHeader(), " ");
		tokenizer2.nextToken();
		String statusCode2 = tokenizer2.nextToken();
		if (statusCode2.equals("202")) {
			System.out.println("命令已完成");
			header.delete(0,header.length());
			return "成功";
		}else{
			System.out.println("命令未完成");
			header.delete(0,header.length());
			return "失败";
		}
	}

	/**
	 *
	 * @return返回查询到的信息
	 * @throws Exception
	 */
	public String processResponse() throws Exception {
		int last = 0,c = 0;
		socket.setSoTimeout(12000);
		boolean inHeader = true; // loop control
		while (inHeader && ((c = istream.read()) != -1)) {
			switch (c) {
				case '\r':
					break;
				case '\n':
					if (c == last) {
						inHeader = false;
						break;
					}
					last = c;
					header.append("\n");
					break;
				default:
					last = c;
					header.append((char) c);
			}
		}
		String ans = getHeader();
		header.delete(0,header.length());
		return ans;
	}
	/**
	 * Get the response header.
	 */
	public String getHeader() {
		return header.toString();
	}

	/**
	 * Get the server's response.
	 */
	public String getResponse() {
		return response.toString();
	}

	public String getServer(String name) {
		return "GET /"+name+" HTTP/1.0";
	}

	public String putServer(String name) {
		return "PUT /"+name+" HTTP/1.0";
	}

	public String loginServer(String name,String password) {
		return "LOGIN "+name+" "+password;
	}

	public String RegisterServer(String name,String password) {
		return "REG "+name+" "+password;
	}

	public String queryServer(String name) {
		return "QUERY "+name+" ";
	}

	public String updateServer(String method,String attribute,String value) {
		StringTokenizer tokenizer = new StringTokenizer(method, " ");
		tokenizer.nextToken();
		String user = tokenizer.nextToken();
		return "UPDATE "+user+" "+attribute+" "+value+" ";
	}

	public String countFileServer(String username,String type) {
		return "FileInfro "+username+" "+type+" ";
	}

	public String priviewDataServer(String method,String file_name) {
		StringTokenizer tokenizer = new StringTokenizer(method, " ");
		tokenizer.nextToken();
		String user = tokenizer.nextToken();
		return "priview "+user+" "+file_name+" ";
	}
	/**
	 * eg:
	 * myClient.serverice("register", "apple3","123456");注册 用户名 密码***
	 * myClient.serverice("login", "apple3","123456");登录 用户名 密码***
	 * myClient.serverice("put", "smile.jpg","D:/code/smile.jpg");上传 文件 文件所在位置***
	 * myClient.serverice("get", "2.jpg","D:/2/2.jpg");下载 文件 文件保存位置***
	 * @param method 方法名 login登录; get下载；put上传；register 注册
	 * @param param1	login用户名; get文件名；put文件名；register 用户名
	 * @param param2      login密码; get保存路径和文件名；put文件路径；register密码
	 * @return 成功，失败
	 * @throws Exception  **
	 */
	public String serverice(String method,String param1,String param2) throws Exception {
		if(method.equals("login")){
			request = loginServer(param1,param2);
			return processLoginRequest(request);
		}else if(method.equals("get")){
			request = getServer(param1);
			return processGetRequest(request,param2);
		}else if(method.equals("put")){
			request = putServer(param1);
			return processPutRequest(request,param2);
		}else if(method.equals("register")){
			request = RegisterServer(param1,param2);
			return processRegisterRequest(request);
		}else if(method.equals("query")){
			request = queryServer(param1);
			return processQueryRequest(request);
		}else if(method.startsWith("update")){
			request = updateServer(method,param1,param2);
			return processUpdateRequest(request);
		}else if(method.startsWith("fileInform")){
			request = countFileServer(param1,param2);
			return processCountFileRequest(request);
		}else if(method.startsWith("fileData")){
			request = priviewDataServer(method,param1);
			return processPriviewDataRequest(request,param2);
		}
		return "失败";
	}
	/**
	 * Close all open connections -- sockets and streams.
	 */
	public void close() throws Exception {
		socket.close();
		istream.close();
		ostream.close();
	}

	/**
	 * 告诉服务器有关操作已经执行完毕
	 */
	public void return202() {
		String response = "";
		response += "version" + " 202 Created" + CRLF;
		response += "Server: MyHttpServer/1.0" + CRLF;
		response += "Content-Type: text/html" + CRLF;
		response += "Content-Language: en" + CRLF;
		response += "Content-Length: " + response.length() + CRLF;
		response += "Date: " + (new Date().toString()) + CRLF + CRLF;
		try {
			ostream.write(response.getBytes(), 0, response.length());
			ostream.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
