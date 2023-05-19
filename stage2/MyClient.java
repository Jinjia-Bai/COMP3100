import java.net.*;  
import java.io.*;  
class MyClient{
	public static void main(String args[])throws Exception{  
		Socket s = new Socket("localhost",50000);  //port  
		DataOutputStream dout = new DataOutputStream(s.getOutputStream());  
		//BufferedReader br = new BufferedReader(new InputStreamReader(System.in));  
		BufferedReader in = new BufferedReader(new InputStreamReader(s.getInputStream()));
		
		//Handshaking
		String str = "",str2 = "";
		//send HELO
		dout.write(("HELO\n").getBytes());
		dout.flush(); 
		//receive OK
		str = in.readLine();
		//if(!str.equals("OK")) System.out.println("ERROR 250");
		//else System.out.println("Server says: "+str);
		//send AUTH
		String username = System.getProperty("user.name");
		dout.write(("AUTH "+username+"\n").getBytes());
		dout.flush(); 
		//receive OK
		str = in.readLine();
		//System.out.println("Welcome "+username);
		
		//job 1-n
		int jobID = 0, max = 0, nRecs = 0, core = 0, memory = 0, disk = 0, serverID = 0, first_serverID = 0, wjobs = 0, rjobs = 0;
		String type = "", first_type = "";
		String[] array = null;
		while(!str.equals("NONE")){
			boolean isFirst = true, isGet = false;
			//send REDY
			dout.write(("REDY\n").getBytes());
			//receive JOBN,JCPL or NONE
			str2 = in.readLine();
			if(str2.contains("JOBN")){
				array = str2.split(" ");
				jobID = Integer.parseInt(array[2]);
				core = Integer.parseInt(array[4]);
				memory = Integer.parseInt(array[5]);
				disk = Integer.parseInt(array[6]);
				//send GETS All
				dout.write(("GETS Capable " + core + " " + memory + " " + disk + "\n").getBytes());
				dout.flush();
				//Receive DATA nRecs recSize
				str = in.readLine();
				String[] array3=null;
				array3 = str.split(" ");
				nRecs = Integer.parseInt(array3[1]);
				sendOK(dout);
				//receive records and identify the First Capable server
				for(int i = 0;i<nRecs;i++){
					String[] array2=null;
					str=in.readLine();
					array2 = str.split(" ");
					if(isFirst){
						first_type = array2[0];
						first_serverID = Integer.parseInt(array2[1]);
						isFirst = false;
					}
					wjobs = Integer.parseInt(array2[7]);
					//rjobs = Integer.parseInt(array2[8]);
					//not having rjobs and wjobs at the same time
					if(wjobs == 0 && isGet == false){
						type = array2[0];
						serverID = Integer.parseInt(array2[1]);
						isGet = true;
					}
				}
				//no s* found, select the first capable server
				if(isGet == false){
				type = first_type;
				serverID = first_serverID;
				}
				sendOK(dout);
				//receive .
				str = in.readLine();
				
				//schedule a job
				dout.write(("SCHD " + jobID + " " + type + " " + serverID + "\n").getBytes());
				dout.flush();
				//receive OK
				str = in.readLine();
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
	}
	
	static void sendOK(DataOutputStream dout)throws Exception{
	dout.write(("OK\n").getBytes());
	dout.flush();
	}
}
