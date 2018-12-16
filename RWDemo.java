import java.io.*;


class ReaderWriter
{
	int ch;
	FileInputStream fin;
	FileOutputStream fout;
	boolean rdrblocked=false;
	static volatile boolean running;
	static volatile boolean eof=false;
	String s = "This will end up in a file in the same order as it appears here or else we can conclude that there is some discrepancy related to some race condition stuff";
	byte b[] = s.getBytes();
	int length = b.length;

	void writer() throws Exception
     {
        fout = new FileOutputStream("Demo.txt");
	for(int i=0;i<length;i++)
	{
	fout.write(b[i]);
	if(rdrblocked){
	synchronized(this){
        rdrblocked=false;
	notify();
	 }}}
	eof=true;
	while(running){
	synchronized(this){
        rdrblocked=false;
	notify();
	 }}
	fin.close();
		
     }

	void reader() throws Exception
     {
	running=true;
	fin = new FileInputStream("Demo.txt");
	if(fin.available()>0){
	while((ch=fin.read())!=-1)
	{													
	System.out.print((char)ch);
	while(fin.available()==0 && !eof){
	synchronized(this){
	rdrblocked=true;
	wait();
	}}}
	}
	running=false;
	fin.close();
     }
}

class Writercall implements Runnable
{
	Thread t;
	ReaderWriter obj;
	Writercall(ReaderWriter obj)
 {
	this.obj=obj;
	t=new Thread(this,"Reader");
	t.start();
 }
	public void run() 
 {
        try{
	obj.writer();}
        catch(Exception e){}
 }
}

class Readercall implements Runnable
{
	Thread t;
	ReaderWriter obj;
	Readercall(ReaderWriter obj)
 {
	this.obj=obj;
	t= new Thread(this,"Writer");
	t.start();
 }
	public void run()
 { 
        try{
	obj.reader();}
        catch(Exception e){}
 }
}

class RWDemo
{
	public static void main(String args[]) throws Exception
 {
	ReaderWriter obj1 = new ReaderWriter();
	Readercall r = new Readercall(obj1);
	Writercall w = new Writercall(obj1);
	try
	{
	r.t.join();
	w.t.join();
	}
	catch(Exception e){}
 }
}
