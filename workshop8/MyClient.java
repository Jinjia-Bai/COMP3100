import java.net.*;  
import java.io.*;  
class MyClient{  
public static void main(String args[])throws Exception{  
	Socket s=new Socket("localhost",50000);  //port  
	DataOutputStream dout=new DataOutputStream(s.getOutputStream());  
	//BufferedReader br=new BufferedReader(new InputStreamReader(System.in));  
	BufferedReader in = new BufferedReader(new InputStreamReader(s.getInputStream()));

	//Handshaking
	String str="",str2="";
	//send HELO
	dout.write(("HELO\n").getBytes());
	dout.flush(); 
	//receive OK
	str=in.readLine();
	//if(!str.equals("OK")) System.out.println("ERROR 250");
	//else System.out.println("Server says: "+str);
	//send AUTH
	String username = System.getProperty("user.name");
	dout.write(("AUTH "+username+"\n").getBytes());
	dout.flush(); 
	//receive OK
	str=in.readLine();
	//System.out.println("Welcome "+username);

	//job 1-n
	boolean isGETS = true, isFirst = true;
	int jobID = 0, nRecs = 0, serverID = 0, core = 0, memory = 0, disk = 0;
	String type="";
	String[] Array = null;
	while(!str.equals("NONE")){
		//send REDY
		dout.write(("REDY\n").getBytes());
		dout.flush();
		//receive JOBN,JCPL or NONE
		str2=in.readLine();
		//System.out.println("Server says: "+str2);
		if(str2.contains("JOBN")){
			if(isGETS){//do it only once in the while loop
				Array = str2.split(" ");
				core = Integer.parseInt(Array[4]);
				memory = Integer.parseInt(Array[5]);
				disk = Integer.parseInt(Array[6]);
				//send GETS All
				dout.write(("GETS Capable "+core+" "+memory+" "+disk+"\n").getBytes());
				dout.flush();
				//Receive DATA nRecs recSize
				str=in.readLine();
				//System.out.println("Server says: "+str);
				Array = str.split(" ");
				nRecs = Integer.parseInt(Array[1]);
				//Send OK
				dout.write(("OK\n").getBytes());
				dout.flush();
				//receive records and identify the First Capable server
				for(int i=0;i<nRecs;i++){
					String[] Array2=null;
					str=in.readLine();
					//System.out.println("Server says: "+str);
					Array2 = str.split(" ");
					if(isFirst){
						type = Array2[0];
						serverID = Integer.parseInt(Array2[1]);
					}
					isFirst = false;
				}
				//Send OK
				dout.write(("OK\n").getBytes());
				dout.flush();
				//receive .
				str=in.readLine();
				//System.out.println("Server says: "+str);
				isGETS = false;
			}
		//schedule a job
			dout.write(("SCHD "+jobID+" "+type+" "+serverID+"\n").getBytes());
			dout.flush();
			jobID++;
			//receive OK
			str=in.readLine();
			//System.out.println("Server says: "+str);
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
