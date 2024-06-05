package eWAGI_Firms;
public class ConsumptionCoefficient {
	int sectorID;
	double coefficient;
	Inventory supplier;
	double budget;
	double demandQuantity;
	double receivedQuantity;
	double expenditures;
	ConsumptionCoefficient(int sectorID,double coefficient){
		this.sectorID = sectorID;
		this.coefficient = coefficient;
	}
	double getAcceptedQuantity() {
		return 0;
	}
	double getInvoiceAmount() {
		return 0;

	}
	
}
