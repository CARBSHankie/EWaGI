package eWAGI_Firms;
public class PaymentTransaction {
	int senderID;
	int receiverID;
	int senderBankID;
	int receiverBankID;
	double amount;
	String reference;
	PaymentTransaction(int senderID, int receiverID, int senderBankID, int receiverBankID,double amount, String reference){
	this.senderID  = senderID;
	this.receiverID = receiverID;
	this.senderBankID=senderBankID;
	this.receiverBankID = receiverBankID;
	this.amount = amount;;
	this.reference = reference;
	}
}