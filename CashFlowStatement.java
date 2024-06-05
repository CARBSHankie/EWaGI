package eWAGI_Firms;

public class CashFlowStatement {            //paymentAccount???
	CashFromOperation cashFromOperation;                 
	CashFromInvesting cashFromInvesting;                
	CashFromFinancing cashFromFinancing;                       
	double cash, cashfromoperation, cashfrominvesting, cashfromfinancing; 
	
	CashFlowStatement(){
		cashFromOperation = new CashFromOperation();	
		cashFromInvesting = new CashFromInvesting();
		cashFromFinancing = new CashFromFinancing();
	}


	class CashFromOperation{                      
		double netEarning;                 
		double additionsToCash;
		double depreciation;
		double accountsReceivable;
		double accountsPayable;
		double inventoryIncrease;      // Subtractions from Cash
		double taxPayable;
		
		
	}
	class CashFromInvesting{
		double equipment;
	
	}
	class CashFromFinancing{
		double NotesPayable;
		
	}
	
	void computeNetCash() {
		cashfromoperation = cashFromOperation.netEarning + cashFromOperation.additionsToCash - cashFromOperation.inventoryIncrease;
		cashfrominvesting = cashFromInvesting.equipment;
		cashfromfinancing = cashFromFinancing.NotesPayable;
		cash =  cashfromoperation - cashfrominvesting  + cashfromfinancing;  
	}
}
