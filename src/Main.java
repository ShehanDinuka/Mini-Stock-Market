import java.awt.BorderLayout;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.*;

import javax.swing.Timer;
import javax.swing.*;
import javax.swing.JLabel;

import java.io.IOException;
import java.util.*;


import java.io.BufferedReader;
import java.io.FileReader;

public class Main extends JFrame implements ActionListener {

	private static Map <String,Stock> stockList = new HashMap<> ();  // stock list
	
	String[] columns = new String[] {"Symbol", "Company Name", "Price" ,"Bidder"}; // columns of table
	
	private static final String file = "stocks.csv"; // csv file
	
	public static int symbolIndex =-1,nameIndex =-1,priceIndex =-1 ;
	
	private JTable table;
	private JLabel bids; // labels for GUI
    private Color backgroundColor = new Color(16, 16, 16); // background color
	
	// companies on Auction
	String[] companies = new String[]{"FB", "VRTU","MSFT", "GOOGL", "YHOO", "XLNX", "TSLA", "TXN"}; // list of cpmpanies 
	Object[][] data  = new Object[companies.length][4];
	
	JPanel panel = new JPanel(); // panel
	Dimension Fsize = new Dimension(720,660); // size of panel
	
	

	public Main(){
	
        setSize(Fsize);
        setLayout(new GridLayout()); // for Jframe
        setResizable(false);
        pack();
            
		panel = new JPanel(); // create panel
        panel.setLayout(null);
        panel.setBackground(backgroundColor);
        panel.setPreferredSize(Fsize);
       
      
        add(panel,BorderLayout.CENTER);
        Timer timer = new Timer(500, this); 
        
        JLabel topic = new JLabel("BIDDING SERVER",JLabel.CENTER);    // jlabel for topic
        topic.setForeground(Color.WHITE);
        topic.setFont(new Font("Tahoma",Font.BOLD , 28));
        topic.setSize(720, 50);
        topic.setLocation(0, 0);
        panel.add(topic);
        
        bids = new JLabel("Current Bid Messages",JLabel.CENTER); // Jlabel for Messages
		bids.setSize(720, 100);
        bids.setBorder(BorderFactory.createLineBorder(Color.black));
        bids.setBackground(Color.BLACK);
        bids.setLocation(0, 60);
        bids.setFont(new Font("Tahoma",Font.BOLD , 20));
        bids.setForeground(Color.GREEN);
        panel.add(bids);
        
        JLabel itemStock = new JLabel("Item stock"); // jlabel for stock items
        itemStock.setForeground(Color.CYAN);
        itemStock.setFont(new Font("Tahoma",Font.BOLD , 22));
        itemStock.setSize(150, 40);
        itemStock.setLocation(0, 170);
        panel.add(itemStock);
        
        
        
        table = new JTable(data, columns);   // information table
        JScrollPane scrollP = new JScrollPane();
        scrollP.setViewportView(table);
        scrollP.setSize(720, 200);
        scrollP.setLocation(0, 220);
        
        table.getColumnModel().getColumn(0).setPreferredWidth(100); // column sizes
        table.getColumnModel().getColumn(1).setPreferredWidth(340);
        table.getColumnModel().getColumn(2).setPreferredWidth(100);
        table.getColumnModel().getColumn(3).setPreferredWidth(160);
        
		table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
		table.setBackground(Color.BLACK);
		table.setForeground(Color.GREEN);
        table.getTableHeader().setReorderingAllowed(false);        
		table.setBackground(new Color(16, 16, 16));
        table.setFont(new java.awt.Font("Tahoma", Font.PLAIN, 13)); 
        table.setForeground(new Color(153, 153, 153));
        table.setFocusable(false);
        table.setGridColor(new Color(51, 51, 51));
        table.setRowHeight(22);
        table.setForeground(Color.GREEN);
        panel.add(scrollP);
        
        super.setTitle("Stocks");  // title bar
        super.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);    
        this.pack();
        this.setVisible(true);
        timer.start(); 
		
	}

	public void actionPerformed(ActionEvent e) {
		//update data that should view in the table
		//update every 500ms
		for(int i=0;i<companies.length;i++){
				data[i][0] = companies[i];
				data[i][1] = stockList.get(companies[i]).getName();
				data[i][2] =stockList.get(companies[i]).getPrice();
				if(stockList.get(companies[i]).getBidder()!= null){
				data[i][3]= stockList.get(companies[i]).getBidder().getName();
				}
				else data[i][3]="";
				bids.setText(Server.bids.getText());
		} 
		panel.revalidate();	//refresh jpanel
		table.repaint();	//refresh jtable
		
    }
	
	
	public static Map createStockList(){              //get stocks from csv file
		
		
		
		try (BufferedReader br = new BufferedReader(new FileReader(file))){
			
			String currentLine;
			
			if ((currentLine=br.readLine())!= null){
				String str[] =currentLine.split(",");
				
				for(int i=0; i< str.length;i++){
					if(str[i].equals("Symbol"))
					symbolIndex=i;
					else if(str[i].equals("Security Name"))
					nameIndex=i;
					else if(str[i].equals("Price"))
					priceIndex=i;
					}
				}
			
			if(symbolIndex==-1||nameIndex==-1||priceIndex==-1)
			System.out.println("CSV file is not in correct format");
			
			while ((currentLine=br.readLine())!=null){	// read every line in csv file
				
				String str[] =currentLine.split(",");
                        if(str.length==7) {       
				if(str[priceIndex].equals("N"))
					str[priceIndex]="0";
				
				stockList.put(str[symbolIndex],new Stock(str[symbolIndex],str[nameIndex],Double.parseDouble(str[priceIndex]))); // add stock to list
                        }
                        else {
                            if(str[priceIndex].equals("N"))
					str[priceIndex]="0";
				
				stockList.put(str[symbolIndex],new Stock(str[symbolIndex],str[nameIndex]+""+str[nameIndex+str.length-7],Double.parseDouble(str[priceIndex+str.length-7])));
                        }
				}
			
			}
			catch (IOException e){
				e.printStackTrace();
				}
			return stockList;
		}
	
	 public static void main(String [] args) throws IOException { 
		//Create and set up the window.      
        Main main = new Main();
        Map stockList = main.createStockList();

        Server server = new Server(2000); 
        server.setMap(main.stockList);
        server.server_loop();    // run server

    }
	
	

}
