package com.dji.uavapp.util.netutil;

import com.dji.uavapp.util.netutil.HttpClient;

public class Client {

	/**
	 * default HTTP port is port 80
	 */
	private static int port = 80;
	/**
	 * Allow a maximum buffer size of 8192 bytes
	 */
	private static int buffer_size = 8192;

	/**
	 * The end of line character sequence.
	 */
	private static String CRLF = "\r\n";

	public static void main(String[] args) throws Exception {
			try {
				/**
				 * Create a new HttpClient object.
				 */
				HttpClient myClient = new HttpClient();
				/**
				 * Connect to the input server
				 */
				myClient.connect("192.144.227.111");

				System.out.println(myClient.serverice("register", "apple4","123456"));
				System.out.println(myClient.serverice("login", "apple4","123456"));
				//myClient.serverice("put", "smile.jpg","D:/code/smile.jpg");
				//myClient.serverice("get", "smile.jpg","D:/2/smile.jpg");
				System.out.println(myClient.serverice("put", "smile.jpg","D:/code/smile.jpg"));
				System.out.println(myClient.serverice("put", "2.jpg","D:/code/2.jpg"));
				System.out.println(myClient.serverice("put", "3.jpg","D:/code/3.jpg"));
				System.out.println(myClient.serverice("put", "4.jpg","D:/code/4.jpg"));
				System.out.println(myClient.serverice("put", "5.jpg","D:/code/5.jpg"));
				System.out.println(myClient.serverice("get", "smile.jpg","D:/2/smile.jpg"));
				System.out.println(myClient.serverice("get", "2.jpg","D:/2/2.jpg"));
				System.out.println(myClient.serverice("get", "3.jpg","D:/2/3.jpg"));
				System.out.println(myClient.serverice("get", "4.jpg","D:/2/4.jpg"));
				System.out.println(myClient.serverice("get", "5.jpg","D:/2/5.jpg"));
				System.out.println(myClient.serverice("put", "6.mov","D:/code/6.mov"));
				System.out.println(myClient.serverice("put", "7.mov","D:/code/7.mov"));
				System.out.println(myClient.serverice("put", "8.mov","D:/code/8.mov"));
				System.out.println(myClient.serverice("get", "6.mov","D:/2/6.mov"));
				System.out.println(myClient.serverice("get", "7.mov","D:/2/7.mov"));
				System.out.println(myClient.serverice("get", "8.mov","D:/2/8.mov"));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

}
