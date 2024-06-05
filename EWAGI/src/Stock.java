package eWAGI_Firms;
public class Stock {
	int firmID;
	double sharePrice = 0;
	double dividendPerShare = 0;
	double numOutstandingShares;
	Stock(int firmID){
		this.firmID = firmID;
	}
}