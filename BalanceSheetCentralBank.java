package eWAGI_Firms;
public class BalanceSheetCentralBank {
	Assets assets;
	Liabilities liabilities;
	double sumAssets, sumLiabilities, equity;
	double profits;
	BalanceSheetCentralBank(){
		assets = new Assets();
		liabilities = new Liabilities();
	}
	class Assets{
		double standingFacilities =0;
		double governmentCredits =0;
		double otherAssets=0;
		double cash =0 ;
	}
	class Liabilities{
		double bankNotes=0;
		double bankReserves=0;
		double otherLiabilities=0;
	}
	void computeEquity() {
		sumAssets = assets.standingFacilities + assets.governmentCredits + assets.otherAssets ;
		sumLiabilities =  liabilities.bankNotes + liabilities.bankReserves + liabilities.otherLiabilities;
		equity =  sumAssets - sumLiabilities;
	}
}