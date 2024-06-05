package eWAGI_Firms;
import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import eWAGI_Firms.Firm.InputItem;
import repast.simphony.context.Context;
import repast.simphony.engine.environment.RunEnvironment;
import repast.simphony.engine.schedule.ScheduledMethod;
import repast.simphony.space.continuous.ContinuousSpace;
import repast.simphony.space.grid.Grid;
import repast.simphony.util.ContextUtils;
public class GoodsFirm extends Firm{
	double outputSmoothing = GlobalParameters.outputSmoothing;
	ArrayList<GoodsInventory> mallInventoryList;
	//Constructor
	GoodsFirm(ContinuousSpace<Object> space, Grid<Object> grid, int ID, GoodsSector ownSector,int bankID, int clearingHouseID,double initPaymentAccount){
		super(space,grid, ID,bankID,clearingHouseID, initPaymentAccount);
		mallInventoryList = new ArrayList<GoodsInventory>();
		this.ownSector = ownSector;
		super.wage = ownSector.wage;
		super.laborStock = ownSector.laborForce / ownSector.numFirms;
		super.initialOutput = laborStock*ownSector.laborCoefficient;	
 		super.intermediateGoodsStock = GlobalParameters.initInputOutputRatio * initialOutput /ownSector.intermediateInputCoeffient;		
		super.intermediateInputValue = 1.0* super.intermediateGoodsStock;
		super.capitalStock = initialOutput /ownSector.capitalCoefficent ;
		super.capitalStockValue = 1.0*super.capitalStock;
		this.calcServicePrice = 1.0;		
		this.serviceLevel = GlobalParameters.initInputOutputRatio * initialOutput/ownSector.serviceCoefficient;		
		this.serviceLevelValue = this.calcServicePrice *this.serviceLevel ;
		this.calcEnergyPrice = 1.0;
		this.energyLevel = GlobalParameters.initInputOutputRatio * initialOutput/ownSector.energyCoefficient;
		this.energyLevelValue = this.calcEnergyPrice *this.energyLevel ;
		//Set up input requirement lists
		for(int i=0; i < ownSector.technicalCoefficients.size();i++) {
			if(ownSector.technicalCoefficients.get(i).technicalCoefficient>0) {
				if(ownSector.technicalCoefficients.get(i).type==1)
					inputGoodsRequirementList.add(new InputItem(ownSector.technicalCoefficients.get(i).sectorID,ownSector.technicalCoefficients.get(i).type));
				else if(ownSector.technicalCoefficients.get(i).type==2)
					inputServiceRequirementList.add(new InputItem(ownSector.technicalCoefficients.get(i).sectorID,ownSector.technicalCoefficients.get(i).type));
				else
					inputEnergyRequirementList.add(new InputItem(ownSector.technicalCoefficients.get(i).sectorID,ownSector.technicalCoefficients.get(i).type));
			}
		}
		super.laborStock = 0;
		updateBalanceSheet();
	}
	//Resetting at the beginning of the iteration
	@ScheduledMethod (start = 1, interval = 1, priority = 100)
	public void setupIteration() {
		tick = (int) RunEnvironment.getInstance().getCurrentSchedule().getTickCount();
		if(tick==1) {
			this.mallInventoryList.get(0).inventoryStock = 1.0*initialOutput;
			this.mallInventoryList.get(0).inventoryStockEndOfIteration = 1.0*initialOutput;
			this.output = this.initialOutput;
		}
		/*TODO add parameter*/
		if(previousOutputs.size() ==10 )
			previousOutputs.remove(0);
		previousOutputs.add(this.output);
		avgPreviousOutputs = 0;
		for(int i=0; i < previousOutputs.size();i++) {
			avgPreviousOutputs += previousOutputs.get(i);
		}
		if(previousOutputs.size()>0)
			avgPreviousOutputs = avgPreviousOutputs / previousOutputs.size();
		this.capitalInvestmentReal = 0;
		this.capitalInvestment = 0;
		this.serviceLevel = (1-this.serviceDepreciation)*this.serviceLevel;
		this.serviceLevelValue = (1-this.serviceDepreciation)*this.serviceLevelValue;
		this.energyLevel = (1-this.energyDepreciation)*this.energyLevel;
		this.energyLevelValue = (1-this.energyDepreciation)*this.energyLevelValue;
		this.intermediateInputExpenditures=0;
		this.serviceExpenditure = 0;
		this.energyExpenditure = 0;
		this.serviceInputValue = 0;
		this.energyInputValue = 0;
		this.qualityCapitalInvestments = 0;
		outputRationed = false;
		paymentAccount.pendingOutgoingTransactionsList.clear();
		paymentAccount.receivedTransactionsList.clear();
	}
	//Price setting
	@ScheduledMethod (start = 1, interval = 1, priority = 95)
	public void pricing() {
		deltaOutput = plannedOutput - output;
		if(deltaOutput > plannedOutput*0.05) {
			outputRationed = true;
		}
		if(tick>1) {
			if(targetMarkUp==0 && markUp>0)
				targetMarkUp = markUp;
			    if(diffMarketShares > 0.005 || outputRationed)
				    targetMarkUp += 0.001;
			    else if(diffMarketShares < -0.005)
				    targetMarkUp -= 0.001;
			    if(targetMarkUp <1.0)
				    targetMarkUp = 1.0;
			        price = 0;
			double sumSales = 0;
			for(int i=0; i < mallInventoryList.size();i++) {
				Inventory aInventory = mallInventoryList.get(i);
				if(avgUnitCosts>0 && targetMarkUp>0)
					aInventory.price = avgUnitCosts * targetMarkUp;
				else
					aInventory.price = 1.0;
				price += aInventory.price*aInventory.soldQuantities;
				sumSales += aInventory.soldQuantities;
			}
			if(sumSales>0)
				price = price/sumSales;
			else
				price=1.0;
		}
	}
	//Output planning
	@ScheduledMethod (start = 1, interval = 1, priority = 90)
	public void computePlannedOutput() {	
		this.plannedOutput = 0;
		plannedOutputProduction = 0;
		this.plannedOutputBeforeSmoothing = 0;
		for(int i=0; i < mallInventoryList.size();i++) {
			GoodsInventory aInventory = mallInventoryList.get(i);
			if(this.tick<2) {
				aInventory.expectedDemand = initialOutput;
				aInventory.refillLevel= initialOutput;
			}else {
				aInventory.expectedDemand = aInventory.primaryDemand;
				if(aInventory.totalExcessDemand>0 && !outputRationed)
					aInventory.expectedDemand += GlobalParameters.refillLeveladjustmentcoefficient* aInventory.totalExcessDemand;
			}
			aInventory.refillLevel = aInventory.expectedDemand*(1+ this.inventoryBufferPCT);
			aInventory.plannedDelivery = Math.max(0.0,  aInventory.refillLevel - Math.max(0,  aInventory.inventoryStock -  aInventory.expectedDemand ));
			this.plannedOutputBeforeSmoothing += aInventory.plannedDelivery;
		}
		// Output smoothing
		this.plannedOutputProduction = (1 - this.outputSmoothing)*this.plannedOutputBeforeSmoothing + this.outputSmoothing * this.avgPreviousOutputs;
		plannedOutput += plannedOutputProduction;
		for(int i=0; i < mallInventoryList.size();i++) {
			GoodsInventory aInventory = mallInventoryList.get(i);
			if(this.plannedOutputBeforeSmoothing>0)
				aInventory.plannedDelivery  = aInventory.plannedDelivery * this.plannedOutput / this.plannedOutputBeforeSmoothing;
		}
	}
	//Compute input demands
	@ScheduledMethod (start = 1, interval = 1, priority = 75)
	public void computeInputDemand() {
		
		computeInputDemandForProductionPlan();
	}
	//Labor market activities; check if firm has to hire or fire workers
	@ScheduledMethod (start = 1, interval = 1, priority = 60)
	public void labormarketActivities() {
		if(redundancies> 0) {
			unmatchedVacancies = 0;
			Collections.shuffle(workContractList);
			do {
				workContractList.get(0).canceled = true;
				workContractList.remove(0);
				redundancies-=1;
			}while(redundancies>0 && workContractList.size()>1);
		}
		if(vacancies>0) {
			unmatchedVacancies = vacancies;
			for(int i =0; i < (int) vacancies; i++) {
				Vacancy aVacancy = new Vacancy(ID, wage, this);
				ownSector.laborMarket.vacancyList.add(aVacancy);
			}
		}
	}
	//Labor market activities 2: Check for on the job search cancelations;
		@ScheduledMethod (start = 1, interval = 1, priority = 56)
		public void labormarketActivities2() {
			laborStock = 0;
			for(int i =0; i < workContractList.size(); i++) {
				if(workContractList.get(i).canceled) {
					workContractList.remove(i);
					i--;
				}else {
					laborStock +=1;
				}
			}
		}
	//Production
	@ScheduledMethod (start = 1, interval = 1, priority = 42)
	public void production() {
		this.potentialOutput =  totalFactorProductivity*Math.min(ownSector.laborCoefficient * this.laborStock, Math.min(ownSector.capitalCoefficent*this.capitalStock, Math.min(ownSector.serviceCoefficient * this.serviceLevel, Math.min(ownSector.intermediateInputCoeffient*this.intermediateGoodsStock, ownSector.energyCoefficient *this.energyLevel ))));
		outputIntermediate = totalFactorProductivity*ownSector.intermediateInputCoeffient*this.intermediateGoodsStock;
		outputLabor = totalFactorProductivity*ownSector.laborCoefficient * this.laborStock;
		outputCapital = totalFactorProductivity*ownSector.capitalCoefficent*this.capitalStock;
		outputService = totalFactorProductivity*ownSector.serviceCoefficient * this.serviceLevel;
		outputEnergy = totalFactorProductivity*ownSector.energyCoefficient *this.energyLevel;
		
		try {
            File file = new File("outputfactor.txt");
            FileOutputStream fos = new FileOutputStream(file, true);		            	            
            PrintStream ps = new PrintStream(fos);      // Create a PrintStream that writes to the file          
            System.setOut(ps);                          // Redirect System.out to the PrintStream	            
            System.out.println(
            	    // Firm ID
            	    "Tick:" + tick + "   Firm " + this.ID + "  Sector ID: " + this.ownSector.sectorID +
            	    // Production
            	    "    potentialOutput: " + this.potentialOutput + "    Planned output: " + this.plannedOutput +
            	    // Income statement
            	    "  output Intermediate: " + outputIntermediate + "  output Labor: " + outputLabor + "  output Capital: " + outputCapital + "  output Service: " + outputService + "  output Energy: " + outputEnergy );
            fos.close();
           } catch (Exception e) {
               e.printStackTrace();
           }			
   
		
		
		
		this.output = Math.min(this.plannedOutput, this.potentialOutput);
		this.intermediateInput = this.output / (totalFactorProductivity*ownSector.intermediateInputCoeffient);
		this.intermediateGoodsStock -= this.intermediateInput;
		this.intermediateInputValue -= this.intermediateInput*this.calcIntermediateGoodsPrice;
		if(this.intermediateGoodsStock < 1e-10)
			this.intermediateGoodsStock = 0;
		this.serviceInput = this.output/(totalFactorProductivity*ownSector.serviceCoefficient);
		this.serviceLevel -=  this.serviceInput;
		this.serviceInputValue = this.serviceInput*this.calcServicePrice;
		this.serviceLevelValue -= this.serviceInputValue ;
		this.energyInput = this.output/(totalFactorProductivity*ownSector.energyCoefficient);
		this.energyLevel -=  this.energyInput;
		this.energyInputValue = this.energyInput*this.calcEnergyPrice;
		this.energyLevelValue -= this.energyInputValue ;
		double newStock = this.internalInventoy.inventoryStock + this.output;
		if(newStock>0)
			this.internalInventoy.quality =  (this.internalInventoy.inventoryStock*this.internalInventoy.quality + this.output*quality)/newStock;
		this.internalInventoy.inventoryStock += this.output;
	}
	//determines wage offer using greedy algorithm
	@ScheduledMethod (start = 1, interval = 1, priority = 40)
	public void capitalStockAdjustment() {
		//Depreciation
		if( this.capitalInvestmentReal>0)
			this.qualityCapitalInvestments = this.qualityCapitalInvestments/  this.capitalInvestmentReal;
		double depreciatedCapital = this.depreciationRate * this.output/(totalFactorProductivity*ownSector.capitalCoefficent);
		this.capitalStock =  this.capitalStock - depreciatedCapital;
		this.capitalProductivity = (this.capitalStock*this.capitalProductivity + this.qualityCapitalInvestments *this.capitalInvestmentReal)/(this.capitalStock +this.capitalInvestmentReal );
		this.capitalStock += this.capitalInvestmentReal;
		this.capitalStockValue = calcCapitalPrice*capitalStock;
		this.calcCapitalCosts = depreciatedCapital*calcCapitalPrice;
	}
	//determines wage offer using greedy algorithm
	@ScheduledMethod (start = 1, interval = 1, priority = 35)
	public void outputDelivery() {
		double delivery = 0.0;
		double outputGap =  output - plannedOutput;
		if(plannedOutput>0) {
			if(outputGap<0){
				outputToMarket = output*(plannedOutputProduction/plannedOutput); 
				outputToRD = output - outputToMarket;
			}else {
				outputToRD = plannedOutputRD;
				outputToMarket = output - outputToRD;
			}
			rdKnowledgeStock += outputToRD;
			this.internalInventoy.inventoryStock -= outputToRD;
			for(int i=0; i < mallInventoryList.size();i++) {
				GoodsInventory aInventory = mallInventoryList.get(i);
				delivery = Math.min(this.internalInventoy.inventoryStock, this.internalInventoy.inventoryStock * aInventory.plannedDelivery/plannedOutputProduction);				
//				delivery = this.internalInventoy.inventoryStock * aInventory.plannedDelivery/plannedOutputProduction;
				double newStock = aInventory.inventoryStock + delivery;
				if(newStock>0)
					aInventory.quality = (aInventory.quality*aInventory.inventoryStock + delivery*this.internalInventoy.quality)/newStock;
				aInventory.inventoryStock+=  delivery;
				this.internalInventoy.inventoryStock -=  delivery;
			}
		}
//		if(this.ownSector.sectorID==2) {	
		
	}
	//Accounting
	@ScheduledMethod (start = 1, interval = 1, priority = 25)
	public void accounting() {
//		System.out.println("accounting goods firm called at tick: " + tick);
		revenue = 0;
		changeInventories = 0;
		salesToFirms = 0;
		salesToHouseholds = 0;
		totalSoldQuantities = 0;
		totalMarketSales = 0;
		changeInventoryValue =0;
		valueInventory = this.internalInventoy.inventoryStock*price;
		interestIncome = paymentAccount.interestPayment;
		for(int i=0; i < mallInventoryList.size();i++) {
			GoodsInventory aInventory = mallInventoryList.get(i);
			revenue += aInventory.revenues;
			salesToFirms += aInventory.salesToFirms;
			salesToHouseholds += aInventory.salesToHouseholds;
			totalSoldQuantities += aInventory.soldQuantities ;
			totalMarketSales += aInventory.totalMarketSales;
			valueInventory += aInventory.inventoryStock * aInventory.price;
			aInventory.changeInventory = 	aInventory.inventoryStock - aInventory.inventoryStockEndOfIteration;
			aInventory.inventoryStockEndOfIteration = aInventory.inventoryStock;
			changeInventories += aInventory.changeInventory;
		}
		if(totalMarketSales>0)
			marketShare = totalSoldQuantities/totalMarketSales;
		else
			marketShare = 0.0;
		intermediateInputCosts = this.intermediateInput * this.calcIntermediateGoodsPrice;
		this.calcEnergyCosts =  this.energyInput* this.calcEnergyPrice;
		this.calcServiceCosts = this.serviceInput* this.calcServicePrice;
		this.totalCosts = laborCosts + intermediateInputCosts + calcEnergyCosts + calcServiceCosts + calcCapitalCosts;
		if(this.output>0)
			this.unitCosts = totalCosts/this.output;
		rdInvestment = this.unitCosts* outputToRD;
		if(this.avgUnitCosts >0)
			this.avgUnitCosts = this.smoothAvgUnitCosts*this.avgUnitCosts + (1-this.smoothAvgUnitCosts)*unitCosts;
		else
			this.avgUnitCosts = this.unitCosts;
		diffMarketShares = (-1)*this.avgMarketShare;
		if(this.avgMarketShare >0)
			this.avgMarketShare = this.smoothAvgMarketShare*this.avgMarketShare + (1-this.smoothAvgMarketShare)*marketShare;
		else
			this.avgMarketShare = this.marketShare;
		diffMarketShares += this.avgMarketShare;
		if(unitCosts>0)
			this.markUp = price/unitCosts;
			this.changeInventoryValue = changeInventories * price;
		this.earnings = revenue + changeInventoryValue + interestIncome - totalCosts;
		double ROE = revenue/this.earnings;          //revenue over earnings
		double profits = revenue - totalCosts;       //Product profits
		double POE = profits / this.earnings;        //Profits over earnings 		
		double IOE = interestIncome/this.earnings;   //Interests income over earnings		
		updateBalanceSheet();
		double tempDiv = this.dividendRatio * Math.max(earnings, 0);
		if( balanceSheet.equity>tempDiv) {
			this.dividends = this.dividendRatio * Math.max(earnings, 0);
			if(numOutstandingShares>0 )
				this.dividendPerShare = this.dividends / this.numOutstandingShares; 
			firmStock.dividendPerShare = dividendPerShare;
			Context  context = ContextUtils.getContext(this);
			Iterable<ClearingHouse> clearingHouse = context.getObjects(ClearingHouse.class);
			for(ClearingHouse obj : clearingHouse) {
				if(obj.ID == clearingHouseID) {
					paymentAccount.pendingOutgoingTransactionsList.add(new PaymentTransaction(ID, obj.ID, paymentAccount.bankID, obj.bankID, dividends, "dividends"));
					obj.totalDividends += dividends;
					break;
				}
			}
			balanceSheet.assets.cash = paymentAccount.accountBalance;
			balanceSheet.computeEquity();
		}else {
			this.dividends = 0;
			this.dividendPerShare = 0;
		}
			
		try {
            File file = new File("outputGoodsFirm.txt");
            FileOutputStream fos = new FileOutputStream(file, true);		            	            
            PrintStream ps = new PrintStream(fos);      // Create a PrintStream that writes to the file          
            System.setOut(ps);                          // Redirect System.out to the PrintStream	            
            System.out.println(
            	    // Firm ID
            	    "Tick:" + tick + "   Firm " + this.ID + "  Sector ID: " + this.ownSector.sectorID +
            	    // Production
            	    "    Output: " + this.output + "    Planned output: " + this.plannedOutput +
            	    // Income statement
            	    "  Revenue: " + this.revenue + "  Inventory change: " + this.changeInventoryValue + "  Interest Income: " + this.interestIncome + "  Costs: " + this.totalCosts + "  Earnings: " + this.earnings +
            	    // Cash Flow statement
            	    "  Cash: " + this.balanceSheet.assets.cash +
            	    // Balance sheet
            	    "  Assets: " + this.balanceSheet.sumAssets + "  Equity: " + this.balanceSheet.equity + "  Liabilities: " + this.balanceSheet.sumLiabilities
            	);

            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }			
	}
	ServiceInventory chooseServiceProvider(ArrayList<ServiceInventory> firmInventories) {
		potentialSupplierList.clear();
		double denLogit = 0;
		for(int i =0 ; i < firmInventories.size(); i++) {
			potentialSupplierList.add( firmInventories.get(i));
			denLogit += Math.exp(this.intensityChoicePriceService*firmInventories.get(i).price );
		}
		double random1 = Math.random();
		double prob = 0;
		for(int i =0; i < potentialSupplierList.size();i++) {
			prob +=  Math.exp(this.intensityChoicePriceGoods* potentialSupplierList.get(i).price)/denLogit;
			if(random1<= prob) {
				potentialSupplierList.get(i);
				return (ServiceInventory) potentialSupplierList.get(i);
			}
		}
		return null;
	}
	// 
	public void computeInputDemandForProductionPlan() {
		this.expectedServiceCosts = 0;
		this.expectedEnergyCosts = 0;
		this.expectedIntermediateCosts=0;
		//Capital Demand
		this.capitalRequired = this.plannedOutput / ownSector.capitalCoefficent;
		this.expectedInvestmentExpenditures = 0;
		if(this.capitalStock < this.capitalRequired) {
			this.capitalDemand =  this.capitalRequired - this.capitalStock;
		}
		// Intermediate input demand
		double delta1 =  Math.max(0, ( this.plannedOutput / (totalFactorProductivity*ownSector.intermediateInputCoeffient) -   this.intermediateGoodsStock));
		double delta2 =  Math.max(0, ( this.intermediateGoodsStock - this.plannedOutput / (totalFactorProductivity*ownSector.intermediateInputCoeffient)));
		this.intermediateGoodsDemand = Math.max(delta1, this.muBufferIntermediateGoods * this.averageIntermediateGoodConsumption - delta2) ;
		for(int i =0; i < inputGoodsRequirementList.size();i++) {
			inputGoodsRequirementList.get(i).demandCapital = 0;
			inputGoodsRequirementList.get(i).demandDirect = 0;
			inputGoodsRequirementList.get(i).totalDemand = 0;
			for(int j=0; j < ownSector.technicalCoefficients.size();j++) {
				if(ownSector.technicalCoefficients.get(j).sectorID == inputGoodsRequirementList.get(i).sectorID) {
					// Intermediate Input
					double adjTechCoefficient = ownSector.technicalCoefficients.get(j).technicalCoefficient / ownSector.technicalCoefficients.get(j).sumCoefficentsType;
					inputGoodsRequirementList.get(i).demandDirect =adjTechCoefficient * this.intermediateGoodsDemand;
					expectedIntermediateCosts += inputGoodsRequirementList.get(i).demandDirect *inputGoodsRequirementList.get(i).supplier.price;
					inputGoodsRequirementList.get(i).totalDemand += inputGoodsRequirementList.get(i).demandDirect;
					break;
				}
			}
			if(this.capitalDemand>0) {
				for(int j=0; j < ownSector.technicalCoefficients.size();j++) {
					if(ownSector.technicalCoefficients.get(j).sectorID == inputGoodsRequirementList.get(i).sectorID) {
						inputGoodsRequirementList.get(i).demandCapital = this.capitalDemand*ownSector.technicalCoefficients.get(j).shareCapitalInvestments;
						inputGoodsRequirementList.get(i).totalDemand += inputGoodsRequirementList.get(i).demandCapital;
						expectedInvestmentExpenditures+= inputGoodsRequirementList.get(i).demandCapital* inputGoodsRequirementList.get(i).supplier.price;
						break;
					}
			}
		}
		}
		// Energy demand
		//this.energyDemand = this.plannedOutput / ownSector.energyCoefficient;
		delta1 =  Math.max(0, ( this.plannedOutput / (ownSector.energyCoefficient*totalFactorProductivity) -   this.energyLevel));
		delta2 =  Math.max(0, ( this.energyLevel - this.plannedOutput /(totalFactorProductivity* ownSector.energyCoefficient)));
		this.energyDemand = Math.max(delta1, this.muBufferIntermediateGoods * this.averageEnergyGoodConsumption - delta2) ;
		for(int i =0; i < inputEnergyRequirementList.size();i++) {
			inputEnergyRequirementList.get(i).demandCapital = 0;
			inputEnergyRequirementList.get(i).demandDirect = 0;
			inputEnergyRequirementList.get(i).totalDemand = 0;
			for(int j=0; j < ownSector.technicalCoefficients.size();j++) {
				if(ownSector.technicalCoefficients.get(j).sectorID == inputEnergyRequirementList.get(i).sectorID) {
					// Intermediate Input
					double adjTechCoefficient = ownSector.technicalCoefficients.get(j).technicalCoefficient / ownSector.technicalCoefficients.get(j).sumCoefficentsType;
					inputEnergyRequirementList.get(i).demandDirect =adjTechCoefficient * this.energyDemand;
					expectedEnergyCosts += inputEnergyRequirementList.get(i).demandDirect *inputEnergyRequirementList.get(i).supplier.price;
					inputEnergyRequirementList.get(i).totalDemand += inputEnergyRequirementList.get(i).demandDirect;
					break;
				}
			}
			if(this.capitalDemand>0) {
				for(int j=0; j < ownSector.technicalCoefficients.size();j++) {
					if(ownSector.technicalCoefficients.get(j).sectorID == inputEnergyRequirementList.get(i).sectorID) {
						inputEnergyRequirementList.get(i).demandCapital = this.capitalDemand*ownSector.technicalCoefficients.get(j).shareCapitalInvestments;
						inputEnergyRequirementList.get(i).totalDemand += inputEnergyRequirementList.get(i).demandCapital;
						expectedInvestmentExpenditures+= inputGoodsRequirementList.get(i).demandCapital* inputGoodsRequirementList.get(i).supplier.price;
						break;
					}
			}
		}	
		}
		//Services demand
		//this.serviceDemand = this.plannedOutput / ownSector.serviceCoefficient;
		delta1 =  Math.max(0, ( this.plannedOutput / (totalFactorProductivity*ownSector.serviceCoefficient) -   this.serviceLevel));
		delta2 =  Math.max(0, ( this.serviceLevel - this.plannedOutput / (totalFactorProductivity*ownSector.serviceCoefficient)));
		this.serviceDemand = Math.max(delta1, this.muBufferIntermediateGoods * this.averageServiceGoodConsumption - delta2) ;
		for(int i =0; i < inputServiceRequirementList.size();i++) {
			inputServiceRequirementList.get(i).demandCapital = 0;
			inputServiceRequirementList.get(i).demandDirect = 0;
			inputServiceRequirementList.get(i).totalDemand = 0;
			for(int j=0; j < ownSector.technicalCoefficients.size();j++) {
				if(ownSector.technicalCoefficients.get(j).sectorID == inputServiceRequirementList.get(i).sectorID) {
					// Intermediate Input
					double adjTechCoefficient = ownSector.technicalCoefficients.get(j).technicalCoefficient / ownSector.technicalCoefficients.get(j).sumCoefficentsType;
					inputServiceRequirementList.get(i).demandDirect =adjTechCoefficient * this.serviceDemand;
					expectedServiceCosts += inputServiceRequirementList.get(i).demandDirect *inputServiceRequirementList.get(i).supplier.price;
					inputServiceRequirementList.get(i).totalDemand += inputServiceRequirementList.get(i).demandDirect;
					break;
				}
			}
				if(this.capitalDemand>0) {
				for(int j=0; j < ownSector.technicalCoefficients.size();j++) {
					if(ownSector.technicalCoefficients.get(j).sectorID == inputServiceRequirementList.get(i).sectorID) {
						inputServiceRequirementList.get(i).demandCapital = this.capitalDemand*ownSector.technicalCoefficients.get(j).shareCapitalInvestments;
						inputServiceRequirementList.get(i).totalDemand += inputServiceRequirementList.get(i).demandCapital;
						expectedInvestmentExpenditures+= inputGoodsRequirementList.get(i).demandCapital* inputGoodsRequirementList.get(i).supplier.price;
						break;
					}
			}
		}	
		}
		// Labor demand
		this.laborDemand = this.plannedOutput /(totalFactorProductivity* ownSector.laborCoefficient);
		if(laborDemand > laborStock) {
			this.redundancies = 0;
			this.vacancies = Math.ceil(this.laborDemand - this.laborStock);
		}else {
			this.redundancies = Math.floor(this.laborStock - this.laborDemand); ;
			this.vacancies = 0;
		}
		expectedLaborCosts = this.laborDemand*this.wage;
	}
	EnergyInventory chooseEnergyProvider(ArrayList<EnergyInventory> firmInventories) {
		potentialSupplierList.clear();
		double denLogit = 0;
		for(int i =0 ; i < firmInventories.size(); i++) {
			potentialSupplierList.add( firmInventories.get(i));
			denLogit += Math.exp(this.intensityChoicePriceService*firmInventories.get(i).price );
		}
		double random1 = Math.random();
		double prob = 0;
		for(int i =0; i < potentialSupplierList.size();i++) {
			prob +=  Math.exp(this.intensityChoicePriceGoods* potentialSupplierList.get(i).price)/denLogit;
			if(random1<= prob) {
				potentialSupplierList.get(i);
				return (EnergyInventory) potentialSupplierList.get(i);
			}
		}
		return null;
	}
}