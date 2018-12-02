import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JLabel;

public class Server implements Runnable{
	
	
	public static final int PORT = 2000; //connetion port 
	public static Map <String,Stock> stockList;
	public static Map <String,Bidder> bidderList =new HashMap(); //All bidders
	
	private static ServerSocket serverSocket;
	
	private Socket connectionSocket ;
	private String name ;
	
	public static JLabel bids= new JLabel("Current Bid Messages",JLabel.CENTER);
	
	public Server(int socket) throws IOException{		// socket for server
		try{
                    serverSocket = new ServerSocket(socket) ;
                }catch(IOException e){
                    System.out.println(e);
                    System.exit(0);
                }   
		
        }	  
	
	public Server(Socket socket) { 
		this.connectionSocket = socket; // socket for client
    }
	
	
	@Override
	public void run (){
		
		try{
			BufferedReader in =new BufferedReader(new InputStreamReader(this.connectionSocket.getInputStream()));
			PrintWriter out =new PrintWriter(new OutputStreamWriter(this.connectionSocket.getOutputStream()));
			// send message to reciever
			
			String line ;
			
			//Enter name
			out.print("Enter Your Name : ");
			out.flush();
			
			line=in.readLine(); // name of the bidder
			setName(line );
			bids.setText(line+" Trying To Connect.....");
			
			Bidder bidder  ;
			if(bidderList.containsKey(line)){ // check for availability
				bidder = bidderList.get(line);
				}
				else {                          // add new bidder
					bidder = new Bidder(line);
					bidderList.put(line,bidder);
					}
			
			out.print("Enter Symbol : "); 
			out.flush();
			
			for (line=in.readLine();line != null && !line.equals("quit");line=in.readLine()){
				
				String display ;
			try{	
				if (stockList.get(line).getBidder()==null){ // check for current top bidder
					display = bidder.getName()+" : "+line;
					System.out.println(display);
					}
				else {
					display =bidder.getName()+" : "+ "(currently goes to "+stockList.get(line).getBidder().getName()+ ")";
					System.out.println(display);
					
					}
				
			
					if(stockList.containsKey(line)) {
					String sym=line;
					out.print(stockList.get(line).getPrice() +"\n");
                    out.flush();
                    
                    out.print("Enter Your Bid : ");
                    out.flush();
                    
                    line= in.readLine();
                try{   
                    if(Double.valueOf(line) > stockList.get(sym).getPrice()){
						
  synchronized(this){	
						bidder.updateList(sym,Double.valueOf(line));			 // update the bidder price
                        stockList.get(sym).updatePrice(Double.valueOf(line));                       
                        stockList.get(sym).setBidder(bidder);
					}
						display = bidder.getName() + " set a bid on "+sym+" : "+ bidder.getBidList().get(sym);
						 System.out.println(display);
						 
						 out.print("Your bid is susesfully added.\n");
                        out.flush();
                       bids.setText(display); 			// message to bid messages  
						}
						else {
							display = bidder.getName() + " enterd smaller bid.";
                        System.out.println(display);
							
						out.print("Place a bid above " + stockList.get(sym).getPrice() + " .\n");
                        out.flush();
							
							}
						}catch (NumberFormatException ex){
							out.println("Number Format Error");
								out.flush();
							}	
					}
				}
				catch(NullPointerException e){
                    out.println("-1");
                    out.flush();
                }
				out.print("Enter Symbol : ");
							out.flush();

		}
			
		
		
		}
	catch (IOException e ){
		System.out.println(e);
		}
	
	try{
		this.connectionSocket.close();
		}
		catch (IOException e){
			}
			
		
	}
		public void setName(String nam){
        this.name = nam;
    }
    
    public void setMap(Map mapList){
        stockList = mapList;
    }
    
   
    
    public void server_loop() throws IOException{
        while(true){
            Socket socket = serverSocket.accept();
            Thread worker = new Thread(new Server(socket)); // assign thread for each connection
            worker.start();
        }
    }
}
