package milestone2;

import java.io.DataInputStream;
import java.io.PrintStream;
import java.io.IOException;
import java.net.Socket;
import java.net.ServerSocket;

public class server {

	

	  
	  private static ServerSocket serverSocket = null;
	  
	  private static Socket clientSocket = null;

	  
	  
	  private static final int maxClientsCount = 20;
	  private static final clientThread[] threads = new clientThread[maxClientsCount];
	  private static final String[] list = new String[20];

	  public static void main(String args[]) {

	   
	    int portNumber = 3733;
	    if (args.length < 1) {
	      System.out.println("Usage: java MultiThreadChatServerSync <portNumber>\n"
	          + "Now using port number=" + portNumber);
	    } else {
	      portNumber = Integer.valueOf(args[0]).intValue();
	    }

	    
	    try {
	      serverSocket = new ServerSocket(portNumber);
	    } catch (IOException e) {
	      System.out.println(e);
	    }

	   
	    while (true) {
	      try {
	        clientSocket = serverSocket.accept();
	        
	        int i = 0;
	        for (i = 0; i < maxClientsCount; i++) {
	          if (threads[i] == null) {
	            (threads[i] = new clientThread(clientSocket, threads,list)).start();
	            break;
	          }
	        }
	        if (i == maxClientsCount) {
	          PrintStream os = new PrintStream(clientSocket.getOutputStream());
	          os.println("Server too busy. Try later.");
	          os.close();
	          clientSocket.close();
	        }
	      } catch (IOException e) {
	        System.out.println(e);
	      }
	    }
	  }
	}


	class clientThread extends Thread {

	  private Socket subserver1 =null;
	  private String clientName = null;
	  private DataInputStream is = null;
	  private PrintStream os = null;
	  private Socket clientSocket = null;
	  private final clientThread[] threads;
	  private int maxClientsCount;
	  private String[] list;
	  

	  public clientThread(Socket clientSocket, clientThread[] threads,String[] list) {
	    this.clientSocket = clientSocket;
	    this.threads = threads;
	    maxClientsCount = threads.length;
	    this.list=list;
	    
	  }

	  @SuppressWarnings("deprecation")
	public void run() {
	    int maxClientsCount = this.maxClientsCount;
	    clientThread[] threads = this.threads;

	    try {
	     
	      is = new DataInputStream(clientSocket.getInputStream());
	      os = new PrintStream(clientSocket.getOutputStream());
	      
	      boolean cheker=false;
		     
	      System.out.println("1");
	      
	      String test =null;
	      
	      test=is.readLine().trim();
	    	if(test.contains("$01")) {
	    		cheker=true;
	    		//send message to destination
	    	      System.out.println(test);	    	      
	    	          String[] words1 = test.split("\\s", 3);
	    	          words1.toString();
	    	          if (words1.length > 1 && words1[2] != null) {
	    	            words1[2] = words1[2].trim();
	    	            if (!words1[2].isEmpty()) {
	    	              synchronized (this) {            	 
	    	                for (int i = 0; i < maxClientsCount; i++) {
	    	                  if (threads[i] != null && threads[i] != this
	    	                      && threads[i].clientName != null
	    	                      && threads[i].clientName.equals(words1[1])) {
	    	                    threads[i].os.println(words1[2]);	                   
	    	                    this.os.println(words1[2]);
	    	                    
	    	                    
	    	                  }
	    	                  
	    	                }
	    	                for (int i = 0; i < maxClientsCount; i++) {
	    		                  if (threads[i]==null) {
	    		                	  threads[i-1]=null;
	    		                	  break;
	    		                  }
	    	              }
	    	            }
	    	          }
	    	          }
	    	}
	      
	      
	      
	    	if(!cheker) {
	      
	      String name;
	      while (true) {
	        os.println("Enter your name.");
	        name = is.readLine().trim(); 
	    
	        System.out.println("name"+ " hayy");
	        
	        if (name.indexOf('@') == -1) {
	        	System.out.println("henaa");
	        	    for(int i = 0; i < maxClientsCount; i++) {
	       	 if (threads[i] != null && threads[i] != this && (threads[i].clientName)!=null) {
	       		 if((threads[i].clientName).equals(name) ) {
	       			 os.println("The name is already used.");
	       			 break;
	       		 }
	       	 }
	       }
	        	
	          break;
	        } else {
	          os.println("The name should not contain '@' character.");
	        }
	        
	      }

	      
	      os.println("Welcome " + name
	          + " to our chat room.\nTo leave enter /quit in a new line.");
	      synchronized (this) {
	        for (int i = 0; i < maxClientsCount; i++) {
	          if (threads[i] != null && threads[i] == this) {
	            clientName = "@" + name;
	            String n = name + " on server A";
	            for (int j = 0; j < list.length; j++) {
	            	if (list[i] == null) {
	            		list[i]=n;
	            	}
				}
	            
	            break;
	          }
	        }
	        for (int i = 0; i < maxClientsCount; i++) {
	          if (threads[i] != null && threads[i] != this) {
	            threads[i].os.println("*** A new user " + name
	             + " entered the chat room !!! ***");
	          }
	          
	        }
	      }
	      
	      
	      
	     
	      while (true) {
	    	  
	        String line = is.readLine();
	        if (line.startsWith("quit")) {
	          break;
	        }
	        
	        if(line.startsWith("sl")) {
		    	  for (int i = 0; i < list.length; i++) {
		    		  if (list[i] != null) {	    			  
		    			  this.os.println(list[i]);	
		    			  		    		  
		    		  }
		    	  }	    			        
	        }
	       
	        boolean b= false;
	        if (line.startsWith("@")) {
	          String[] words = line.split("\\s", 2);
	          if (words.length > 1 && words[1] != null) {
	            words[1] = words[1].trim();
	            if (!words[1].isEmpty()) {
	              synchronized (this) {            	 
	                for (int i = 0; i < maxClientsCount; i++) {
	                  if (threads[i] != null && threads[i] != this
	                      && threads[i].clientName != null
	                      && threads[i].clientName.equals(words[0])) {
	                	b =true;
	                    threads[i].os.println("<" + name + "> " + words[1]);	                   
	                    this.os.println(">" + name + "> " + words[1]);
	                    
	                    break;
	                  }
	                }
	                //Not found in this network
	    	        if (b==false ){
	    	        	
	    	        	try {
	    					subserver1 = new Socket("localhost",3734);
	    				    //subserver1.getOutputStream().flush(); 
	    					System.out.println("A socket to connect  subserver2 is created");
	    				} catch (Exception e) {
	    					System.out.println("laaaa");
	    				}
	    	        	
	    	        	PrintStream	os1=null;
	    	        try {
	    				os1 = new PrintStream(subserver1.getOutputStream());
	    				String s = "$01 "+ line+" From "+name;
	    				os1.println(s);
	    				System.out.println("the desired client is not in your netwoek");
	    				
	    			} catch (Exception e) {
	    				System.out.println("fy 7aga hena");
	    			}
	    	    	
	    	        os1.close();
	    	        subserver1.close();	
	    	        }
	                
	              }
	            }
	          }
	        }
        	 else {
        		 if(!line.startsWith("sl") ) {
        			 System.out.println("please enter a reciever name");
        		 }
        		 
	        
	        }
	      }
	      synchronized (this) {
	        for (int i = 0; i < maxClientsCount; i++) {
	          if (threads[i] != null && threads[i] != this
	              && threads[i].clientName != null) {
	            threads[i].os.println("*** The user " + name
	                + " is leaving the chat room !!! ***");
	          }
	        }
	      }
	      System.out.println("kk");
	      os.println("*** Bye " + name + " ***");

	     
	      synchronized (this) {
	        for (int i = 0; i < maxClientsCount; i++) {
	          if (threads[i] == this) {
	            threads[i] = null;
	            list[i]=null;
	          }
	        }
	       
	      }
	      
	      is.close();
	      os.close();
	      clientSocket.close();
	      
	    }
	    } catch (IOException e) {
	    }
	  }

	
	
	
}
