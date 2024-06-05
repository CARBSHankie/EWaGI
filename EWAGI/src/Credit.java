package eWAGI_Firms;
public class Credit {
	int creditorID;
	int bankID;
	double initialDebtLevel;
	double currentDebtLevel;
	double exposureAtRisk;
	double interestRate;
	double interestPayment;
	double installmentPayment;
	int repaymentPeriod;
	double riskOfDefault;
	double collateral=0;
	double expectedLossGivenDefault = 1.0;
	boolean restructuring = false;
	boolean restructured = false;
	boolean defaulted = false;
	boolean justGranted = true;
	int debtRestructuringCounter;
	double interestRateMonthly;
	int iterationPerYear = 12;
	double v =0.1;
	BalanceSheet balanceSheetFirm;
	Credit(int creditorID,int bankID, double initialDebtLevel, double interestRate, int repaymentPeriod, BalanceSheet balanceSheetFirm ){
		this.initialDebtLevel = initialDebtLevel;
		this.currentDebtLevel = initialDebtLevel;
		this.interestRate = interestRate;
		this.interestRateMonthly = interestRate/iterationPerYear; 
		this.installmentPayment = 0;
		this.riskOfDefault = 0.0;
		this.balanceSheetFirm = balanceSheetFirm;
		this.repaymentPeriod = repaymentPeriod;
		if(repaymentPeriod > 0) {
			this.installmentPayment = initialDebtLevel / (1.0*repaymentPeriod);
		}
	}
void riskAssesment() {
		if(balanceSheetFirm.equity>0)
			riskOfDefault = Math.max(3e-4,1- Math.exp(-v*(balanceSheetFirm.liabilities.totalDebt)/balanceSheetFirm.equity));
		else
			riskOfDefault = 1.0;
		exposureAtRisk = riskOfDefault*expectedLossGivenDefault*currentDebtLevel;
	}
}
//credit change 