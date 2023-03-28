import java.net.*;  
import java.io.*;  
class MyClient{  
public static void main(String args[])throws Exception{  
Socket s=new Socket("localhost",50000);  //port
//DataInputStream din=new DataInputStream(s.getInputStream());  
DataOutputStream dout=new DataOutputStream(s.getOutputStream());  
BufferedReader br=new BufferedReader(new InputStreamReader(System.in));  
BufferedReader in = new BufferedReader(new InputStreamReader(s.getInputStream()));

//Handshaking
String str="",str2=""; 
//send HELO
dout.write(("HELO\n").getBytes());
dout.flush(); 
//get OK about accessing server successfully
str2=in.readLine();
if(!str2.equals("OK")) System.out.println("ERROR 250");
else System.out.println("Server says: "+str2);
//send username
String username = System.getProperty("user.name");
dout.write(("AUTH "+username+"\n").getBytes());
dout.flush(); 
//get OK about AUTH from server
str2=in.readLine();
System.out.println("Welcome "+username);





int jobID = 0;
String str3="";
while(!str3.equals("NONE")){
//send REDY
dout.write(("REDY\n").getBytes());
dout.flush();
//receive message
str3=in.readLine();
System.out.println("Server says: "+str3);
//schedule a job
	if(str3.contains("JOBN")){
		dout.write(("SCHD "+jobID+" medium 0\n").getBytes());
		dout.flush();
		jobID++;
	}
}

while(!str.equals("QUIT")){
str=br.readLine();  
dout.write((str+"\n").getBytes());  
dout.flush();  
str2=in.readLine();  
System.out.println("Server says: "+str2);  
str = str2;
}  
  
dout.close();  
s.close();  
}}  
