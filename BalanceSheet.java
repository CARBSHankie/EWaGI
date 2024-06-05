package eWAGI_Firms;
public class BalanceSheet {
	Assets assets;
	Liabilities liabilities;
	double sumAssets, sumLiabilities, equity;
	BalanceSheet(){
		assets = new Assets();
		liabilities = new Liabilities();
	}
	class Assets{
		double capitalStockValue;
		double inventoryFinalProductValue;
		double inventoryIntermediateProductsValue;
		double cash;
		double financialCapitalValue;
	}
	class Liabilities{
		double totalDebt;
	}
	void computeEquity() {
		sumAssets = assets.capitalStockValue + assets.cash + assets.financialCapitalValue + assets.inventoryFinalProductValue + assets.inventoryIntermediateProductsValue;
		sumLiabilities =  liabilities.totalDebt;
		equity =  sumAssets - sumLiabilities;
	}
}