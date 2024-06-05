package eWAGI_Firms;
import java.util.ArrayList;
import java.util.Collections;
import repast.simphony.context.Context;
import repast.simphony.engine.schedule.ScheduledMethod;
import repast.simphony.space.continuous.ContinuousSpace;
import repast.simphony.space.grid.Grid;
import repast.simphony.util.ContextUtils;
public class Bank {
	private ContinuousSpace<Object> space;
	private Grid<Object> grid;
	int ID;
	int centralBankID;
	int foreignBankID;
	double capitalAdequacyRatio, reserveRequirementRatio;
	double riskExposureBudget,riskWeightedAssets;
	double excessLiquidity;
	double totalCreditDemand;
	double profits;
	double interestPaidOnDeposits, interestReceivedCredits, interestPaidToCB, interestsReceivedFromCB;
	long initEquity = GlobalParameters.initEquity; 
	ArrayList<PaymentAccount> paymentAccountList;
	ArrayList<Credit> creditList;
	ArrayList<CreditApplication> creditApplicationList;
	ArrayList<CreditOffer> acceptedCreditApplications;
	BalanceSheetBank balanceSheet;
	double lambdaB = GlobalParameters.lambdaB;
	int repaymentPeriod = GlobalParameters.repaymentPeriod;
	int debtRestructuringPeriod = GlobalParameters.debtRestructuringPeriod;
	double interestRateOnDeposits;
	double interestMarkDownDeposits = GlobalParameters.interestMarkDownDeposits;
	double interestMarkDownReserves;
	PaymentAccount centralBankReserves;
	Credit standingFacility;
	Bank(int ID, int centralBankID, int foreignBankID){
		this.ID = ID;
		this.centralBankID = centralBankID;
		this.foreignBankID = foreignBankID;
		paymentAccountList = new ArrayList<PaymentAccount> ();
		creditList = new ArrayList<Credit> ();
		creditApplicationList = new ArrayList<CreditApplication> ();
		acceptedCreditApplications = new ArrayList<CreditOffer> ();
		centralBankReserves = new PaymentAccount(ID,centralBankID,initEquity,0);
		standingFacility = new Credit(ID,centralBankID, 0.0, 0.0, 0,null);
		balanceSheet =  new BalanceSheetBank();
	}
	@ScheduledMethod (start = 1, interval = 1, priority = 100)
	public void resetting() {
		creditApplicationList.clear();
		acceptedCreditApplications.clear();
		centralBankReserves.pendingOutgoingTransactionsList.clear();
		centralBankReserves.receivedTransactionsList.clear();
	}
	@ScheduledMethod (start = 1, interval = 1, priority = 99)
	public void updateMonetaryVariables() {
		Context  context = ContextUtils.getContext(this);
			Iterable<CentralBank> centralBank = context.getObjects(CentralBank.class);
			for(CentralBank obj : centralBank) {
				if(obj.ID ==centralBankID) {
					CentralBank aCentralBank= obj;
					standingFacility.interestRate = aCentralBank.interestBaseRate;
					standingFacility.interestRateMonthly = standingFacility.interestRate/standingFacility.iterationPerYear;
					interestMarkDownReserves = aCentralBank.interestMarkDownbankReserves;
					interestRateOnDeposits = (1-interestMarkDownDeposits)*aCentralBank.interestBaseRate;
					this.centralBankReserves.interestRate = (1-interestMarkDownReserves)*aCentralBank.interestBaseRate;
					this.centralBankReserves.interestRateMonthly = this.centralBankReserves.interestRate/this.centralBankReserves.interationsPerYear;
					capitalAdequacyRatio = aCentralBank.capitalAdequacyRatio;
					reserveRequirementRatio = aCentralBank.reserveRequirementRatio;
				}
			}
	}
	@ScheduledMethod (start = 1, interval = 1, priority = 98)
	public void updateBankBalanceSheet() {
		updateBalanceSheet();
	}
	@ScheduledMethod (start = 1, interval = 1, priority = 69)
	public void determineCreditSupply() {
		// 1. Compute risk budget according to the capital Adequacy Ratio
		riskWeightedAssets = 0;
		for(int i=0; i < creditList.size();i++) {
			creditList.get(i).riskAssesment();
			riskWeightedAssets += creditList.get(i).exposureAtRisk;
		}
		riskExposureBudget = 0;
		double alpha;
		if(capitalAdequacyRatio>0)
			alpha= 1/(1.0*capitalAdequacyRatio);
		else
			alpha = 0.0;
		riskExposureBudget = alpha * balanceSheet.equity - riskWeightedAssets;
		// 2nd: Compute max credit supply acc to minimum reserve requirement
		excessLiquidity = balanceSheet.assets.cashReserves -  reserveRequirementRatio * (balanceSheet.liabilities.firmDeposits + balanceSheet.liabilities.householdDeposits);
	}
	@ScheduledMethod (start = 1, interval = 1, priority = 68)
	public void collectApplicationsMakeOffers() {
		totalCreditDemand = 0.0;
		double tempExcessLiquidity = excessLiquidity;
		double tempRiskExposureBudget = riskExposureBudget;
		Collections.shuffle(creditApplicationList);
		for(int i=0; i < creditApplicationList.size();i++) {
			totalCreditDemand += creditApplicationList.get(i).creditAmount;
			creditApplicationList.get(i).assesApplication();
			if(tempRiskExposureBudget - this.creditApplicationList.get(i).exposureAtRisk > 0 && tempExcessLiquidity >0) {
				//First version: firm only applies at home bank
				Context  context = ContextUtils.getContext(this);
				Iterable<Firm> Firms = context.getObjects(Firm.class);
				for(Firm obj : Firms) {
					if(obj.ID ==this.creditApplicationList.get(i).firmID) {
						Firm aFirm = obj;
						double epsilon = Math.random();
						double interestrateOffered = standingFacility.interestRate*(1 + lambdaB * creditApplicationList.get(i).riskOfDefault + epsilon);
						CreditOffer creditOffer = new CreditOffer(ID, aFirm.ID,interestrateOffered,Math.min(tempExcessLiquidity,this.creditApplicationList.get(i).creditAmount), repaymentPeriod);
						tempRiskExposureBudget -= this.creditApplicationList.get(i).exposureAtRisk;
						tempExcessLiquidity -= Math.min(tempExcessLiquidity,this.creditApplicationList.get(i).creditAmount);
						aFirm.creditOfferList.add(creditOffer);
						break;
					}
				}
			}
		}
	}
	@ScheduledMethod (start = 1, interval = 1, priority = 66)
	public void payoutCredits() {
		for(int i=0; i < acceptedCreditApplications.size();i++ ) {
			//First version: firm only applies at home bank
			Context  context = ContextUtils.getContext(this);
			Iterable<Firm> Firms = context.getObjects(Firm.class);
			for(Firm obj : Firms) {
				if(obj.ID ==acceptedCreditApplications.get(i).firmID) {
					Firm aFirm = obj;
					Credit newCredit = new Credit(acceptedCreditApplications.get(i).firmID, acceptedCreditApplications.get(i).bankID,acceptedCreditApplications.get(i).creditOffered, acceptedCreditApplications.get(i).interestRate, acceptedCreditApplications.get(i).installmentPeriod, aFirm.balanceSheet);
					aFirm.creditList.add(newCredit);
					aFirm.externalFinancialObtained += newCredit.initialDebtLevel;
					riskExposureBudget -= this.creditApplicationList.get(i).exposureAtRisk;
					excessLiquidity -= acceptedCreditApplications.get(i).creditOffered;
					//this.centralBankReserves.accountBalance -= newCredit.initialDebtLevel;
					this.centralBankReserves.pendingOutgoingTransactionsList.add(new PaymentTransaction(ID, aFirm.ID, centralBankID, aFirm.bankID, newCredit.initialDebtLevel, "creditPayment"));
					if(excessLiquidity <0) {
						standingFacility.currentDebtLevel += (-1)*excessLiquidity;
						this.centralBankReserves.accountBalance += (-1)*excessLiquidity;
						excessLiquidity = 0.0;
					}
					creditList.add(newCredit);
					break;
				}
			}
		}
	}
	@ScheduledMethod (start = 1, interval = 1, priority = 65)
	public void executeTransactions1() {
		executePendingTransactions();
	}
	@ScheduledMethod (start = 1, interval = 1, priority = 35)
	public void collectInterests() {
		interestReceivedCredits = 0;
		for(int i=0; i < creditList.size();i++) {
			interestReceivedCredits+= creditList.get(i).interestPayment;
		}
	}
	@ScheduledMethod (start = 1, interval = 1, priority = 30)
	public void payoutInterests() {
		interestPaidOnDeposits = 0;
		for(int i=0; i < paymentAccountList.size(); i++) {
			paymentAccountList.get(i).interestRate = interestRateOnDeposits;
			paymentAccountList.get(i).interestRateMonthly = paymentAccountList.get(i).interestRate / paymentAccountList.get(i).interationsPerYear;
			paymentAccountList.get(i).interestPayment = paymentAccountList.get(i).accountBalance*paymentAccountList.get(i).interestRateMonthly ;
			//paymentAccountList.get(i).accountBalance +=paymentAccountList.get(i).interestPayment ;
			//this.centralBankReserves.accountBalance -= paymentAccountList.get(i).interestPayment ;;
			this.centralBankReserves.pendingOutgoingTransactionsList.add(new PaymentTransaction(ID, paymentAccountList.get(i).holderID, centralBankID, paymentAccountList.get(i).bankID, paymentAccountList.get(i).interestPayment , "intrestPayment"));
			excessLiquidity -= paymentAccountList.get(i).interestPayment ;;
			if(excessLiquidity <0) {
				standingFacility.currentDebtLevel += (-1)*excessLiquidity;
				this.centralBankReserves.accountBalance += (-1)*excessLiquidity;
				excessLiquidity = 0.0;
			}
			interestPaidOnDeposits +=paymentAccountList.get(i).interestPayment ;
		}
		standingFacility.interestPayment = standingFacility.interestRateMonthly*standingFacility.currentDebtLevel;
		interestPaidToCB = standingFacility.interestPayment;
		this.centralBankReserves.pendingOutgoingTransactionsList.add(new PaymentTransaction(ID, centralBankID, centralBankID,centralBankID, interestPaidToCB , "intrestPayment"));
		excessLiquidity -= interestPaidToCB;;
		if(excessLiquidity <0) {
			standingFacility.currentDebtLevel += (-1)*excessLiquidity;
			this.centralBankReserves.accountBalance += (-1)*excessLiquidity;
			excessLiquidity = 0.0;
		}
	}
	@ScheduledMethod (start = 1, interval = 1, priority = 21)
	public void executeTransactions2() {
		executePendingTransactions();
	}
	@ScheduledMethod (start = 1, interval = 1, priority = 20)
	public void profitAccounting() {
		this.centralBankReserves.interestPayment = this.centralBankReserves.accountBalance*this.centralBankReserves.interestRateMonthly;
		interestsReceivedFromCB = this.centralBankReserves.interestPayment;
		this.centralBankReserves.accountBalance += interestsReceivedFromCB;
		Context  context = ContextUtils.getContext(this);
		Iterable<CentralBank> centralBank = context.getObjects(CentralBank.class);
		for(CentralBank obj : centralBank) {
			if(obj.ID ==centralBankID) {
				CentralBank aCentralBank= obj;
				aCentralBank.balanceSheet.assets.cash-=interestsReceivedFromCB;
				break;
			}
		}
		profits = interestsReceivedFromCB + interestReceivedCredits - interestPaidToCB - interestPaidOnDeposits;
	}
	
	public void executePendingTransactions() {
		for(int i=0; i < paymentAccountList.size(); i++) {
			for(int j=0; j < paymentAccountList.get(i).pendingOutgoingTransactionsList.size();j++) {
				Bank aBank = null;
					if(paymentAccountList.get(i).pendingOutgoingTransactionsList.get(j).receiverBankID==ID) {	
						aBank = (Bank) this;
					}else if(paymentAccountList.get(i).pendingOutgoingTransactionsList.get(j).receiverBankID== foreignBankID){
						Context  context = ContextUtils.getContext(this);
						Iterable<ForeignBank> allForeignBanks = context.getObjects(ForeignBank.class);
						for(ForeignBank obj : allForeignBanks) {
							if(obj.ID == paymentAccountList.get(i).pendingOutgoingTransactionsList.get(j).receiverBankID) {
								ForeignBank fbank = obj; 
								for(int k=0; k < fbank.paymentAccountList.size(); k++) {
									if(fbank.paymentAccountList.get(k).holderID==paymentAccountList.get(i).pendingOutgoingTransactionsList.get(j).receiverID) {
										paymentAccountList.get(i).accountBalance -= paymentAccountList.get(i).pendingOutgoingTransactionsList.get(j).amount;
										fbank.paymentAccountList.get(k).accountBalance += paymentAccountList.get(i).pendingOutgoingTransactionsList.get(j).amount;
										fbank.paymentAccountList.get(k).receivedTransactionsList.add(paymentAccountList.get(i).pendingOutgoingTransactionsList.get(j));
										paymentAccountList.get(i).pendingOutgoingTransactionsList.remove(j);
										j--;
										break;
									}
								}
								break;
							}
						}
					}else {
						Context  context = ContextUtils.getContext(this);
						Iterable<Bank> allBanks = context.getObjects(Bank.class);
						for(Bank obj : allBanks) {
							if(obj.ID == paymentAccountList.get(i).pendingOutgoingTransactionsList.get(j).receiverBankID) {
								aBank = (Bank) obj;
								break;
							}
						}
					}
					if(aBank != null) {		
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
					}
				}
			}
		for(int i=0; i < centralBankReserves.pendingOutgoingTransactionsList.size();i++) {
			if(centralBankReserves.pendingOutgoingTransactionsList.get(i).receiverBankID == centralBankID) {
				Context  context = ContextUtils.getContext(this);
				Iterable<CentralBank> centralBank = context.getObjects(CentralBank.class);
				for(CentralBank obj : centralBank) {
					if(obj.ID ==centralBankID) {
						CentralBank aCentralBank= obj;
						aCentralBank.balanceSheet.assets.cash+=centralBankReserves.pendingOutgoingTransactionsList.get(i).amount;
						centralBankReserves.accountBalance -= centralBankReserves.pendingOutgoingTransactionsList.get(i).amount;
						break;
					}
				}
			}else {
				Bank aBank = null;
				Context  context = ContextUtils.getContext(this);
				Iterable<Bank> allBanks = context.getObjects(Bank.class);
				for(Bank obj : allBanks) {
					if(obj.ID == centralBankReserves.pendingOutgoingTransactionsList.get(i).receiverBankID) {
						aBank = (Bank) obj;
						break;
					}
				}
				for(int k=0; k < aBank.paymentAccountList.size(); k++) {
					if(aBank.paymentAccountList.get(k).holderID==centralBankReserves.pendingOutgoingTransactionsList.get(i).receiverID) {
						centralBankReserves.accountBalance -= centralBankReserves.pendingOutgoingTransactionsList.get(i).amount;
						aBank.paymentAccountList.get(k).accountBalance += centralBankReserves.pendingOutgoingTransactionsList.get(i).amount;
						aBank.paymentAccountList.get(k).receivedTransactionsList.add(centralBankReserves.pendingOutgoingTransactionsList.get(i));
						centralBankReserves.pendingOutgoingTransactionsList.remove(i);
						i--;
						break;
					}
				}
			}
		}
		}
	
	public void creditDefaultProcedure(Credit defaultedCredit) {
	}
	
	public void updateBalanceSheet() {
		balanceSheet.assets.cashReserves = centralBankReserves.accountBalance;
		balanceSheet.assets.outstandingLoans = 0;
		for(int i=0; i < creditList.size();i++) {
			balanceSheet.assets.outstandingLoans  += creditList.get(i).currentDebtLevel;
		}
		balanceSheet.liabilities.standingFacility = standingFacility.currentDebtLevel;
		balanceSheet.liabilities.firmDeposits = 0;
		balanceSheet.liabilities.householdDeposits =0 ;
		for(int i=0; i < paymentAccountList.size();i++) {
			if(paymentAccountList.get(i).type==1)
				balanceSheet.liabilities.firmDeposits += paymentAccountList.get(i).accountBalance;
			else if(paymentAccountList.get(i).type==2)
				balanceSheet.liabilities.householdDeposits += paymentAccountList.get(i).accountBalance;
		}
		balanceSheet.computeEquity();
	}
}