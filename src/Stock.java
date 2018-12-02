public class Stock{

private String symbol, securityName;
private  double price;
private Bidder bidder;

public Stock(String symbol,String name, double price){
this.symbol =symbol;
this.securityName = name ;
this.price = price ;

}

public String getSymbol(){
         return this.symbol;
    }
    
    public String getName(){
        return this.securityName;
    }
    
    public double getPrice(){
        return this.price;
    }
    
    public void updatePrice(double price){
        this.price = price;
    }
    
    public void setBidder(Bidder bidder){
        this.bidder = bidder;
    }
    
    public Bidder getBidder(){
        return this.bidder;
    }



}
