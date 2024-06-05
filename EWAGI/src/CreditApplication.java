package eWAGI_Firms;
public class CreditApplication {
	int firmID;
	int bankID;
	double creditAmount;
	double equity;
	double debt;
	double v = 0.1;
	double riskOfDefault, exposureAtRisk, collateral, expectedLossGivenDefault;
	CreditApplication(int firmID,int bankID,double creditAmount,double equity,double debt){
		this.firmID= firmID;
		this.bankID=bankID;
		this.creditAmount = creditAmount;
		this.equity = equity;
		this.debt = debt;
		riskOfDefault = 0.0;
		exposureAtRisk = 0.0;
		collateral = 0.0;
		expectedLossGivenDefault = 1.0;
	}
	void assesApplication() {
		if(equity>0)
			riskOfDefault = Math.max(3e-4,1- Math.exp(-v*(creditAmount + debt)/equity));
		else
			riskOfDefault = 1.0;
		exposureAtRisk = riskOfDefault*expectedLossGivenDefault*creditAmount;
	}
}