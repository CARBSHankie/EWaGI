package eWAGI_Firms;
import java.util.ArrayList;
public class Inventory {
	boolean domestic = true;
	int firmID,sectorID, bankID;
	double soldQuantities;
	double totalMarketSales;
	double revenues;
	double price;
	double quality;
	double expectedDemand;
	double plannedDelivery;
	double sumOrders;
	boolean stockOut;
	double inventoryStock;
	double salesToFirms;
	double salesToHouseholds;
	double avPrice;
	double totalExcessDemand;
	double marketShare;
	double primaryDemand,totalDemand;
	Inventory(int firmID,  int sectorID, int bankID, double price){
		this.firmID = firmID;	
		this.sectorID = sectorID;
		this.bankID= bankID;
		this.quality = 1.0;
		this.soldQuantities = 1.0;
		this.revenues = 0;
		this.price = price;
		this.expectedDemand =0;
		this.sumOrders= 0;
		this.stockOut = false;
		this.avPrice = 0;
		this.primaryDemand = 0.0;
		this.totalDemand = 0.0;
		salesToFirms = 0;
		salesToHouseholds = 0;
	}
}