package eWAGI_Firms;
public class EnergyOrder extends Order {
	boolean isFirm;
	EnergyOrder(int ID,int orderID, double orderQuantity, boolean isFirm){
		super(ID, orderQuantity);
		this.orderQuantity =orderQuantity;
		this.acceptedQuantity = 0;
		this.invoiceAmount = 0;
		this.isFirm = isFirm; 
	}
}