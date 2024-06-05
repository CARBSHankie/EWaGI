package eWAGI_Firms;
public class ImportFirmService extends ImportFirm{
	ServiceInventory inventory;
	double quantity;
	ImportFirmService(int ID, int sectorID,int bankID, double price, double quantity){
		super(ID, sectorID,bankID, price);
		inventory = new ServiceInventory(ID, sectorID,bankID, price);
		inventory.domestic = false;
		inventory.inventoryStock = quantity;
		//inventory.refillLevel = quantity;
		this.quantity = quantity;
	}
}