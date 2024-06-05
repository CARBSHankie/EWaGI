package eWAGI_Firms;
import java.util.ArrayList;
public class PaymentAccount {
	int holderID;
	int bankID;
	int type; // firm =1, household =2 ; bank =0
	double interestRate;
	double interestRateMonthly;
	double accountBalance;
	double interestPayment;
	ArrayList<PaymentTransaction> pendingOutgoingTransactionsList;
	ArrayList<PaymentTransaction> receivedTransactionsList;
	int interationsPerYear =12;
	PaymentAccount(int holderID,int bankID, double accountBalance, int type){
		this.holderID= holderID;
		this.bankID = bankID;
		this.accountBalance = accountBalance;
		this.type=type;
		pendingOutgoingTransactionsList = new ArrayList<PaymentTransaction>();
		receivedTransactionsList = new ArrayList<PaymentTransaction> ();
	}
}