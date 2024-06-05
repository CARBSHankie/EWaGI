package eWAGI_Firms;
import java.util.ArrayList;
public class ClearingHouse {
	int ID;
	int bankID;
	double totalDividends= 0;
	PaymentAccount paymentAccount;
	ArrayList<PortfolioItem> assetPortfolioList;
	ClearingHouse(int ID, int bankID){
		paymentAccount = new PaymentAccount(ID,bankID, 0,0);
		assetPortfolioList = new ArrayList<PortfolioItem>();
//		System.out.println("Flag in ClearingHouse: Not in each tick ");
	}
}