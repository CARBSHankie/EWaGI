package eWAGI_Firms;
public class BalanceSheetBank {
	Assets assets;
	Liabilities liabilities;
	double sumAssets, sumLiabilities, equity;
	double profits;
	BalanceSheetBank(){
		assets = new Assets();
		liabilities = new Liabilities();
	}
	class Assets{
		double cashReserves;
		double outstandingLoans;
	}
	class Liabilities{
		double householdDeposits;
		double firmDeposits;
		double standingFacility;
	}
	void computeEquity() {
		sumAssets = assets.cashReserves + assets.outstandingLoans ;
		sumLiabilities =  liabilities.householdDeposits + liabilities.firmDeposits + liabilities.standingFacility;
		equity =  sumAssets - sumLiabilities;
	}
}