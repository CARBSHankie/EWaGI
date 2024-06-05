package eWAGI_Firms;
import java.util.ArrayList;
import repast.simphony.context.Context;
import repast.simphony.engine.schedule.ScheduledMethod;
import repast.simphony.util.ContextUtils;
public class ForeignBank{
	int ID;
	ArrayList<PaymentAccount> paymentAccountList;
	ForeignBank(int ID){
		this.ID = ID;
		paymentAccountList = new ArrayList<PaymentAccount>();
	}
	@ScheduledMethod (start = 1, interval = 1, priority = 65)
	public void executeTransactions1() {
		executePendingTransactions();
	}
	@ScheduledMethod (start = 1, interval = 1, priority = 21)
	public void executeTransactions2() {
		executePendingTransactions();
	}
	public void executePendingTransactions() {
		Bank aBank;
		for(int i=0; i < paymentAccountList.size(); i++) {
			for(int j=0; j < paymentAccountList.get(i).pendingOutgoingTransactionsList.size();j++) {
						Context  context = ContextUtils.getContext(this);
						Iterable<Bank> allBanks = context.getObjects(Bank.class);
						for(Bank obj : allBanks) {
							if(obj.ID == paymentAccountList.get(i).pendingOutgoingTransactionsList.get(j).receiverBankID) {
								aBank = (Bank) obj;
							for(int k=0; k < aBank.paymentAccountList.size(); k++) {
								if(aBank.paymentAccountList.get(k).holderID==paymentAccountList.get(i).pendingOutgoingTransactionsList.get(j).receiverID) {
									paymentAccountList.get(i).accountBalance -= paymentAccountList.get(i).pendingOutgoingTransactionsList.get(j).amount;
									aBank.paymentAccountList.get(k).accountBalance += paymentAccountList.get(i).pendingOutgoingTransactionsList.get(j).amount;
									aBank.paymentAccountList.get(k).receivedTransactionsList.add(paymentAccountList.get(i).pendingOutgoingTransactionsList.get(j));
									paymentAccountList.get(i).pendingOutgoingTransactionsList.remove(j);
									j--;
									break;
								}
							}
							break;
					}
				}
			}
		}
		}
}