package eWAGI_Firms;
import java.util.ArrayList;
import repast.simphony.engine.schedule.ScheduledMethod;
import repast.simphony.space.continuous.ContinuousSpace;
import repast.simphony.space.grid.Grid;
public class CentralBank {
	int ID;
	BalanceSheetCentralBank balanceSheet;
	private ContinuousSpace<Object> space;
	private Grid<Object> grid;
	ArrayList<PaymentAccount> bankReserveList;
	ArrayList<Credit> bankStandingFacilitiesList;
	double interestBaseRate= 0.02, capitalAdequacyRatio = 0.1, reserveRequirementRatio = 0.1, interestMarkDownbankReserves = 0.5;
	CentralBank(int ID){
		this.ID = ID;
		bankReserveList = new ArrayList<PaymentAccount>() ;
		bankStandingFacilitiesList = new ArrayList<Credit>();;
		balanceSheet = new BalanceSheetCentralBank();
	}
		@ScheduledMethod (start = 1, interval = 1, priority = 25)
		public void profitAccounting() {
			updateBalanceSheet(); 
		}
		@ScheduledMethod (start = 1, interval = 1, priority = 20)
		public void updateCBBalanceSheet() {
			updateBalanceSheet() ;
		}
		public void updateBalanceSheet() {
			balanceSheet.liabilities.bankReserves = 0;
			for(int i=0; i < bankReserveList.size();i++) {
				balanceSheet.liabilities.bankReserves += bankReserveList.get(i).accountBalance;
			}
			balanceSheet.assets.standingFacilities = 0;
			for(int i=0; i <bankStandingFacilitiesList.size();i++) {
				balanceSheet.assets.standingFacilities += bankStandingFacilitiesList.get(i).currentDebtLevel;
			}
			balanceSheet.computeEquity();
//			System.out.println("Flag in CentralBank ");
		}
}