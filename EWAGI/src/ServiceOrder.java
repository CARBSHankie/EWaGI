package eWAGI_Firms;
public class ServiceOrder extends Order {
	boolean isFirm;
	ServiceOrder(int ID, int orderFirmID,double orderQuantity, boolean isFirm){
		super(ID, orderQuantity);
		this.orderQuantity =orderQuantity;
		this.acceptedQuantity = 0;
		this.invoiceAmount = 0;
		this.isFirm = isFirm; 
	}
}