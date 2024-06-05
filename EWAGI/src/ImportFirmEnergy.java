package eWAGI_Firms;
public class ImportFirmEnergy extends ImportFirm{
	EnergyInventory inventory;
	double quantity;
	ImportFirmEnergy(int ID, int sectorID, int bankID, double price, double quantity){
		super(ID, sectorID, bankID, price);
		inventory = new EnergyInventory(ID, sectorID, bankID,price);
		inventory.domestic = false;
		inventory.inventoryStock = quantity;
		//inventory. = quantity;
		this.quantity = quantity;
	}
}