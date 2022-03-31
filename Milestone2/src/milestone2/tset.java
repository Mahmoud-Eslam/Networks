package milestone2;

import java.util.Scanner;

public class tset {

	public static void main(String[] args) {
		int portNumber;
		  String x;
	      System.out.println("Enter server name");
	      @SuppressWarnings("resource")
		Scanner in = new Scanner(System.in);
	      x =in.next();
	      
	      if(x.equals("server1")) {
	    	  portNumber=3733;
	      }
	      else {
	    	  portNumber=3734;
	      }
	      
	      System.out.println(portNumber);
	}
}
