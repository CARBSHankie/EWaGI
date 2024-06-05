package eWAGI_Firms;
import repast.simphony.context.Context;
import repast.simphony.util.ContextUtils;
public class ImportFirm {
	int ID;
	int sectorID;
	int bankID;
	double price, quality;
	PaymentAccount paymentAccount;
	ImportFirm(int ID, int sectorID, int bankID, double price){
		this.ID = ID;
		this.bankID = bankID;
		this.sectorID = sectorID;
		this.quality = 1.0;
		this.price = price;
		paymentAccount = new PaymentAccount(ID, bankID, 0.0, 1);
	}
}