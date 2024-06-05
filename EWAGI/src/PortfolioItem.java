package eWAGI_Firms;
public class PortfolioItem {
	Stock stock;
	double numShares;
	PortfolioItem(Stock stock,double numShares ){
		this.stock = stock;
		this.numShares=numShares;
	}
}