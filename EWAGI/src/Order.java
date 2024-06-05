package eWAGI_Firms;
public class Order {
	int ID;
	double orderQuantity;
	double acceptedQuantity;
	double invoiceAmount;
	double quality;
	Order(int ID, double orderQuantity){
		this.ID = ID;
		this.orderQuantity = orderQuantity;
		acceptedQuantity = 0;;
		invoiceAmount =0;
		quality = 0;
	}
}