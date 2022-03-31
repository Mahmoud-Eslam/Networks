
import java.io.*;
import java.net.* ;
import java.util.LinkedList;
import java.util.concurrent.*;

public class  Server {
	String [] clients = new String[200]  ;
	//LinkedList<Integer> ports = new LinkedList<Integer>();
	LinkedList<String> names = new LinkedList<String>();
	LinkedList<OutputStream> outputstreams= new LinkedList<OutputStream>();
	static int clientcount=0 ; 
	String clientSentence;
	String capitalizedSentence ;
	
 public static void main(String argv[]) throws Exception{
	Server serverobj=new Server();
    serverobj.startServer();
 } 
 
public void startServer() throws IOException {
	Socket connectionSocket;
	ExecutorService pool = Executors.newFixedThreadPool(200);
	int portn=1300;
	@SuppressWarnings("resource")
	ServerSocket welcomeSocket= new ServerSocket(portn);
	System.out.println("Server Started");
	while(true) {
		connectionSocket = welcomeSocket.accept();
		BufferedReader inFromClient=new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
  		DataOutputStream outToClient =new DataOutputStream(connectionSocket.getOutputStream());
  		clientSentence = inFromClient.readLine();
		if(clientSentence.length()>3&&(clientSentence.substring(0,4).equals("Join")||clientSentence.substring(0,4).equals("join")))
		{
			if(!(names.contains(clientSentence.substring(5,clientSentence.length())))) {
				//ports.add(connectionSocket.getPort());
				names.add(clientSentence.substring(5,clientSentence.length()));
				outputstreams.add(connectionSocket.getOutputStream());
				outToClient.writeBytes("Conection accepted at port "+portn+'\n');
				outToClient.writeBytes("Type @name,message,TTL<Default=2> to chat OR GetMemberList to get a list of members OR quit to disconnect."+'\n');
				clientcount++;
		        ServerThread runnable= new ServerThread(connectionSocket,clientcount,this);
		        pool.execute(runnable);
			}else {
		  		outToClient.writeBytes("Name repeated, connection rejected."+ '\n');
				//System.out.print("Name repeated, connection rejected.");
			}
		}else {
	  		outToClient.writeBytes("No init message <Join>, connection rejected."+ '\n');
		}
  	}
	}
}

class ServerThread implements Runnable {
	String clientSentence;
	String capitalizedSentence;
    int ctr=0;
    Server server=null;
    Socket client=null; 
    int id;

     
    ServerThread(Socket client, int count ,Server server ) throws IOException {
        this.client=client;
        this.server=server;
        this.id=count;
        System.out.println("Connection "+id+" established with client "+client);
    }

    
    @Override
    public void run() {
     try{
     while(true){
    	BufferedReader inFromClient=new BufferedReader(new InputStreamReader(client.getInputStream()));
  		DataOutputStream outToClient =new DataOutputStream(client.getOutputStream());
  		clientSentence = inFromClient.readLine();
  		if(clientSentence.equals("GetMemberList")) {
   			LinkedList<String> tmp = new LinkedList<String>();
   			for(int i=0;i<server.names.size();i++) {
   				if(server.names.get(i).substring(0,3).equals("tmp")) {
   				}else {
   					tmp.add(server.names.get(i));
   				}
   			}
 	  		outToClient.writeBytes(tmp.toString()+ '\n');
  		}else {
  			if(clientSentence.length()>3&&clientSentence.substring(0,4).equals("quit"))
  			{
				String [] x = clientSentence.split(","); 
  				server.names.remove(x[1]);
  				//inFromClient.close();
  				//outToClient.close();
  				return;
  			}else {
  				if(clientSentence.charAt(0)=='@') {
  					//System.out.println(clientSentence);
  					String [] x = clientSentence.split(","); 
  					String ttls = x[2];
  					int ttl = Integer.parseInt(ttls) - 1;
  					String sender = x[3];
  					String recname = x[0].substring(1);
  					if(!(ttl <= 0)) {
  					if(!(server.names.contains(recname)))
  					{
  						if(ttl>1) {
  	  				    	Socket clientSocket= new Socket("localhost",1200);
  	  				    	DataOutputStream outToServer=new DataOutputStream(clientSocket.getOutputStream());
  	  				    	BufferedReader inFromServer =new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
  	  				 	   	String name = "tmp"+ctr;
  	  				    	outToServer.writeBytes("join"+" "+name+'\n');
  	  				 	    @SuppressWarnings("unused")
  							String modifiedSentence = inFromServer.readLine();
  	  				 	    //System.out.println(modifiedSentence);
  	  				    	outToServer.writeBytes(x[0]+","+x[1]+","+ttl+","+x[3]+'\n');
  	  				    	modifiedSentence = inFromServer.readLine();
  	  				    	outToServer.writeBytes("quit,"+name+'\n');
  	  					    modifiedSentence = inFromServer.readLine();
  	  				 	    //System.out.println(x[0]+','+x[1]+','+ttl+','+x[3]);
  	  						outToClient.writeBytes("Message Sent!"+'\n');
  	  				    	//TimeUnit.MILLISECONDS.wait(150);
      				    	//outToServer.close();
      				    	//inFromServer.close();
  	  						clientSocket.close();
  	  						ctr++;
  						}else {
  	  						outToClient.writeBytes("Message Lost!"+ '\n');
  						}
  					}
  					else 
  					{
	  				 	//System.out.println(clientSentence);
  	  					int loc = server.names.indexOf(recname);
  		  				OutputStream os2 = server.outputstreams.get(loc);
  		  				outToClient.writeBytes("Message Sent!"+ '\n');
  		  				outToClient =new DataOutputStream(os2) ;
  					  	outToClient.writeBytes("From " +sender+": "+x[1]+ '\n');
  					}
  					}else {
  						outToClient.writeBytes("Message Lost!"+ '\n');
  					}
  				}else {
  					capitalizedSentence = clientSentence.toUpperCase() + '\n';
  					outToClient.writeBytes(capitalizedSentence);
  				}
  			}
  		}
     }
     }
     catch(Exception ex){
    	 try {
			//notfound(client,clientSentence);
		} catch (Exception e) {
			e.printStackTrace();
		}
     }
		
}
 /*   public void notfound(Socket sender,String message) throws UnknownHostException, IOException {
    	int i =server.outputstreams.indexOf(sender.getOutputStream());
    	String y = server.names.get(i);
    	Socket clientSocket= new Socket("localhost",1200);
    	DataOutputStream outToServer=new DataOutputStream(clientSocket.getOutputStream());
    	BufferedReader inFromServer =new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
 	    outToServer.writeBytes("join"+" "+y+'\n');
 	    @SuppressWarnings("unused")
 	    String modifiedSentence = inFromServer.readLine();
    	outToServer.writeBytes(message +'\n');
    	modifiedSentence = inFromServer.readLine();
		DataOutputStream outToClient =new DataOutputStream(client.getOutputStream());
		outToClient.writeBytes("Message Sent!"+ '\n');
		clientSocket.close();
    }*/
     }