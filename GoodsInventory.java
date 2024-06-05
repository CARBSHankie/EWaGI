package eWAGI_Firms;
import java.util.ArrayList;
public class GoodsInventory  extends Inventory{
	double refillLevel;
	double changeInventory;
	double inventoryStockEndOfIteration;
	ArrayList<GoodsOrder> orderBook;
	GoodsInventory(int firmID,  int sectorID, int bankID,double price){
		super(firmID,sectorID,bankID,price);
		orderBook = new ArrayList<GoodsOrder>();
		this.changeInventory = 0.0;
		this.inventoryStock = 0;
		this.refillLevel = 0;
		this.inventoryStockEndOfIteration=0;
	}
}