package eWAGI_Firms;
class GoodsOrder extends Order{
	boolean isFirm;
	GoodsOrder(int ID,int orderFirmID, double orderQuantity,boolean isFirm ){
		super(ID, orderQuantity);
		this.orderQuantity =orderQuantity;
		this.acceptedQuantity = 0;
		this.invoiceAmount = 0;
		this.isFirm = isFirm; 
	}
}