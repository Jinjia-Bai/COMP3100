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
String Rep=""; 
//send HELO
dout.write(("HELO\n").getBytes());
dout.flush(); 
//get OK about accessing server successfully
Rep=in.readLine();
if(Rep!="OK") System.out.println("ERROR:250 STMP");
//send username
String username = System.getProperty("user.name");
dout.write(("AUTH "+username+"\n").getBytes());
dout.flush(); 
//get OK about AUTH from server
Rep=in.readLine();
if(Rep=="OK") System.out.println("Welcome "+username);
//send REDY
dout.write(("REDY\n").getBytes());
dout.flush();
Rep=in.readLine();
System.out.println(Rep);

String str="",str2="";  
while(!str.equals("BYE")){  
str=br.readLine();  
dout.write((str).getBytes());  
dout.flush();  
str2=in.readLine();  
System.out.println("Server says: "+str2);  
str = str2;
}  
  
dout.close();  
s.close();  
}}  
