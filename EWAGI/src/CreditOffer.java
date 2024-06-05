package eWAGI_Firms;
public class CreditOffer {
	int bankID;
	int firmID;
	double interestRate;
	double creditOffered;
	int installmentPeriod;
	CreditOffer(int bankID, int firmID,double interestRate,double creditOffered,int installmentPeriod){
		this.bankID = bankID;
		this.firmID = firmID;
		this.interestRate = interestRate;
		this.creditOffered = creditOffered;
		this.installmentPeriod = installmentPeriod;
	}
}