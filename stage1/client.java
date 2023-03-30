import java.net.*;  
import java.io.*;  
class MyClient{  
public static void main(String args[])throws Exception{  
	Socket s=new Socket("localhost",50000);  //port  
	DataOutputStream dout=new DataOutputStream(s.getOutputStream());  
	BufferedReader br=new BufferedReader(new InputStreamReader(System.in));  
	BufferedReader in = new BufferedReader(new InputStreamReader(s.getInputStream()));

	//Handshaking
	String str="",str2="";
	//send HELO
	dout.write(("HELO\n").getBytes());
	dout.flush(); 
	//receive OK
	str=in.readLine();
	if(!str.equals("OK")) System.out.println("ERROR 250");
	else System.out.println("Server says: "+str);
	//send AUTH
	String username = System.getProperty("user.name");
	dout.write(("AUTH "+username+"\n").getBytes());
	dout.flush(); 
	//receive OK
	str=in.readLine();
	System.out.println("Welcome "+username);

	//job 1-n
	boolean isGETS = true;
	int jobID = 0, Max = 0, nRecs = 0, n = 0, cores = 0;
	String type="";
	String[] Array = null;
	while(!str.equals("NONE")){
		//send REDY
		dout.write(("REDY\n").getBytes());
		dout.flush();
		//receive JOBN,JCPL or NONE
		str2=in.readLine();
		System.out.println("Server says: "+str2);
		if(isGETS){//do it only once in the while loop
			//send GETS All
			dout.write(("GETS All\n").getBytes());
			dout.flush();
			//Receive DATA nRecs recSize
			str=in.readLine();
			System.out.println("Server says: "+str);
			Array = str.split(" ");
			nRecs = Integer.parseInt(Array[1]);
			//Send OK
			dout.write(("OK\n").getBytes());
			dout.flush();
			//receive records and identify the largest server type
			for(int i=0;i<nRecs;i++){
				String[] Array2=null;
				str=in.readLine();
				System.out.println("Server says: "+str);
				Array2 = str.split(" ");
				cores = Integer.parseInt(Array2[4]);
				if(cores > Max){
					type = Array2[0];
					Max = cores;
				}
				if(type.equals(Array2[0])) n = Integer.parseInt(Array2[1]);
			}
			n++;
			//Send OK
			dout.write(("OK\n").getBytes());
			dout.flush();
			//receive .
			str=in.readLine();
			System.out.println("Server says: "+str);
			isGETS = false;
		}

		//schedule a job
		if(str2.contains("JOBN")){
			dout.write(("SCHD "+jobID+" "+type+" "+jobID%n+"\n").getBytes());
			dout.flush();
			jobID++;
			//receive OK
			str=in.readLine();
			System.out.println("Server says: "+str);
		}
		else str = str2;
	}

	//send QUIT
	dout.write(("QUIT\n").getBytes());
	dout.flush();
	//receive QUIT
	//close socket
	dout.close(); 
	s.close();
}}
