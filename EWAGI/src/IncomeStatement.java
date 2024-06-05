package eWAGI_Firms;

public class IncomeStatement {
	Revenue revenue;                 
	Expenses expenses;                
	OtherGainsLoss othergainsloss;                       
	double Grossprofit, operatingExpenses, operatingIncome, incomeBeforeTax, netIncome; 
	
	IncomeStatement(){
		revenue = new Revenue();	
		expenses = new Expenses();
		othergainsloss = new OtherGainsLoss();
	}


	class Revenue{                        // We have revenue += aInventory.revenues in GoodsFirm.java, but how was it calculated? 
		double netSales;                 // how to find the netSales of each firm?   outputToMarket? 
		double costSales;             
	}
	class Expenses{
		double sellingExpenses;
		double generalExpenses;
	}
	class OtherGainsLoss{
		double otherIncome;
		double gainsFinancialInstruments;
		double gainsForeignCurrency;
		double interestExpenses;
		double incomeTax;
	}
	
	void computeNetIncome() {
		Grossprofit = revenue.netSales - revenue.costSales;
		operatingExpenses = expenses.generalExpenses + expenses.sellingExpenses;
		operatingIncome = Grossprofit - operatingExpenses;
		incomeBeforeTax = operatingIncome + othergainsloss.otherIncome + othergainsloss.gainsFinancialInstruments + othergainsloss.gainsForeignCurrency - othergainsloss.interestExpenses;
		netIncome = incomeBeforeTax - othergainsloss.incomeTax;
	}
}