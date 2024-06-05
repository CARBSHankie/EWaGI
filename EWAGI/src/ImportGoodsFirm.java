package eWAGI_Firms;
public class ImportGoodsFirm extends ImportFirm{
	GoodsInventory inventory;
	double quantity;
	ImportGoodsFirm(int ID, int sectorID,int bankID, double price, double quantity){
		super(ID, sectorID,bankID, price);
		inventory = new GoodsInventory(ID, sectorID, bankID, price);
		inventory.inventoryStock = quantity;
		inventory.domestic = false;
		inventory.refillLevel = quantity;
		this.quantity = quantity;
	}
}