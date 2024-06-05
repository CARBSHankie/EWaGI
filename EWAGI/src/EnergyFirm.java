package eWAGI_Firms;
import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Random;

import eWAGI_Firms.Firm.InputItem;
import repast.simphony.context.Context;
import repast.simphony.engine.environment.RunEnvironment;
import repast.simphony.engine.schedule.ScheduledMethod;
import repast.simphony.space.continuous.ContinuousSpace;
import repast.simphony.space.grid.Grid;
import repast.simphony.util.ContextUtils;
public class EnergyFirm extends Firm{
	ArrayList<EnergyInventory> mallInventoryList;
	double sumContractedEnergyAsProvider;
	double soldEnergyUnits;
	EnergyFirm(ContinuousSpace<Object> space, Grid<Object> grid, int ID, EnergySector ownSector, int bankID, int clearingHouseID, double initPaymentAccount){
		super(space,grid, ID,bankID,clearingHouseID, initPaymentAccount);
		mallInventoryList = new ArrayList<EnergyInventory>();
		this.ownSector = ownSector;
		super.wage = ownSector.wage;
		super.laborStock = ownSector.laborForce / ownSector.numFirms;
		super.initialOutput = laborStock*ownSector.laborCoefficient;
 		super.intermediateGoodsStock =  GlobalParameters.initInputOutputRatio * initialOutput /ownSector.intermediateInputCoeffient;
		super.intermediateInputValue = 1.0* super.intermediateGoodsStock;
		super.capitalStock = initialOutput /ownSector.capitalCoefficent ;
		super.capitalStockValue = 1.0*super.capitalStock;
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
		this.calcServicePrice = 1.0;
		this.serviceLevel = GlobalParameters.initInputOutputRatio *initialOutput/ownSector.serviceCoefficient;
		this.serviceLevelValue = this.calcServicePrice *this.serviceLevel ;
		this.calcEnergyPrice = 1.0;
		this.energyLevel = GlobalParameters.initInputOutputRatio *initialOutput/ownSector.energyCoefficient;
		this.energyLevelValue = this.calcEnergyPrice *this.energyLevel ;
		super.laborStock = 0;
		updateBalanceSheet();
}
	//determines wage offer using greedy algorithm
	@ScheduledMethod (start = 1, interval = 1, priority = 100)
	public void setupIteration() {
	tick = (int) RunEnvironment.getInstance().getCurrentSchedule().getTickCount();
	if(tick==1) {
		this.mallInventoryList.get(0).inventoryStock = 1.0*initialOutput;
		this.mallInventoryList.get(0).inventoryStockEndOfIteration = 1.0*initialOutput;
		this.output = this.initialOutput;
	}
	if(previousOutputs.size() ==10 )
		previousOutputs.remove(0);
	previousOutputs.add(this.output);
	avgPreviousOutputs = 0;
	for(int i=0; i < previousOutputs.size();i++) {
		avgPreviousOutputs += previousOutputs.get(i);
	}
	if(previousOutputs.size()>0)
		avgPreviousOutputs = avgPreviousOutputs / previousOutputs.size();
	if(totalMarketSales>0)
		marketShare = totalSoldQuantities / totalMarketSales;
	diffMarketShares = (-1)*this.avgMarketShare;
	if(this.avgMarketShare >0)
		this.avgMarketShare = this.smoothAvgMarketShare*this.avgMarketShare + (1-this.smoothAvgMarketShare)*marketShare;
	else
		this.avgMarketShare = this.marketShare;
	diffMarketShares += this.avgMarketShare;
	this.capitalInvestmentReal = 0;
	this.capitalInvestment = 0;
	this.energyInput = 0;
	this.intermediateInputExpenditures=0;
	this.serviceExpenditure = 0;
	this.energyExpenditure = 0;
	this.serviceInputValue = 0;
	this.energyInputValue=0;
	this.qualityCapitalInvestments = 0;
	outputRationed = false;
	paymentAccount.pendingOutgoingTransactionsList.clear();
	paymentAccount.receivedTransactionsList.clear();
	}
	//determines wage offer using greedy algorithm
		@ScheduledMethod (start = 1, interval = 1, priority = 95)
			public void pricing() {
			if(tick>1) {
				deltaOutput = plannedOutput - output;
				if(deltaOutput > plannedOutput*0.05) {
					outputRationed = true;
				}
				if(targetMarkUp==0)
					targetMarkUp = markUp;
				if(diffMarketShares > 0.005 ||  outputRationed)
					targetMarkUp += 0.001;
				else if(diffMarketShares < -0.005)
					targetMarkUp -= 0.001;
				if(targetMarkUp <1.0)
					targetMarkUp = 1.0;
					for(int i=0; i < mallInventoryList.size();i++) {
						Inventory aInventory = mallInventoryList.get(i);
						aInventory.price = avgUnitCosts * targetMarkUp;
						//aInventory.price = 1.0;
					}
			}
		}
		//determines wage offer using greedy algorithm
		@ScheduledMethod (start = 1, interval = 1, priority = 90)
		public void computePlannedOutput() {
			this.plannedOutput = 0;
			plannedOutputProduction = 0;
			this.plannedOutputBeforeSmoothing = 0;
			for(int i=0; i < mallInventoryList.size();i++) {
				EnergyInventory aInventory = mallInventoryList.get(i);
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
				//aInventory.plannedDelivery  = aInventory.expectedDemand ;
				this.plannedOutputBeforeSmoothing += aInventory.plannedDelivery;
			}
			this.plannedOutputProduction = (1 - this.outputSmoothing)*this.plannedOutputBeforeSmoothing + this.outputSmoothing * this.avgPreviousOutputs;
			plannedOutput += plannedOutputProduction;
			for(int i=0; i < mallInventoryList.size();i++) {
				EnergyInventory aInventory = mallInventoryList.get(i);
				if(this.plannedOutputBeforeSmoothing>0)
					aInventory.plannedDelivery  = aInventory.plannedDelivery * this.plannedOutput / this.plannedOutputBeforeSmoothing;
			}
		}
		@ScheduledMethod (start = 1, interval = 1, priority = 80)
		public void chooseGoodsSupplier1() {
			Context  context = ContextUtils.getContext(this);
			double random = Math.random();
			for(int i=0; i < inputGoodsRequirementList.size();i++) {
				potentialSupplierList.clear();
				if(this.tick ==1 || ( random < probChangeSupplierGoods )) {
					double denLogit = 0;
					//TODO: Implement mall choice
					Iterable<GoodsSector> allSectors = context.getObjects(GoodsSector.class);
					for(GoodsSector obj : allSectors) {
						if(obj.sectorID == inputGoodsRequirementList.get(i).sectorID) {
							GoodsSector aSector= obj;
							for(int j =0; j < aSector.mallList.size()  ;j++) {
								for(int k =0 ; k < aSector.mallList.get(j).firmInventories.size(); k++) {
									potentialSupplierList.add( aSector.mallList.get(j).firmInventories.get(k));
									denLogit += Math.exp(this.intensityChoicePriceGoods*aSector.mallList.get(j).firmInventories.get(k).price );
								}
							}
							break;
						}
					}
					double random1 = Math.random();
					double prob = 0;
					for(int j =0; j < potentialSupplierList.size();j++) {
						prob +=  Math.exp(this.intensityChoicePriceGoods* potentialSupplierList.get(j).price)/denLogit;
						if(random1<= prob) {
								for(int k=0; k < currentSupplierList.size(); k++) {
									if(currentSupplierList.get(k).sectorID==inputGoodsRequirementList.get(i).sectorID) {
										currentSupplierList.remove(k);
										break;
									}
								}
								currentSupplierList.add(potentialSupplierList.get(j));
								inputGoodsRequirementList.get(i).supplier = potentialSupplierList.get(j);
							break;
						}
					}
				}
			}
		}
		//determines wage offer using greedy algorithm
		@ScheduledMethod (start = 1, interval = 1, priority = 75)
		public void computeInputDemand() {
			computeInputDemandForProductionPlan();
		}
		//determines wage offer using greedy algorithm
		@ScheduledMethod (start = 1, interval = 1, priority = 60)
		public void labormarketActivities() {
			if(redundancies> 0) {
				Collections.shuffle(workContractList);
				do {
					workContractList.get(0).canceled = true;
					workContractList.remove(0);
					redundancies-=1;
				}while(redundancies>0 && workContractList.size()>1);
			}
			if(vacancies>0) {
				for(int i =0; i < (int) vacancies; i++) {
					Vacancy aVacancy = new Vacancy(ID, wage, this);
					ownSector.laborMarket.vacancyList.add(aVacancy);
				}
			}
		}
		//determines wage offer using greedy algorithm
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
		//determines wage offer using greedy algorithm
		@ScheduledMethod (start = 1, interval = 1, priority = 42)
		public void production() {
			this.potentialOutput =  totalFactorProductivity*Math.min(ownSector.laborCoefficient * this.laborStock, Math.min(ownSector.capitalCoefficent*this.capitalStock, Math.min(ownSector.serviceCoefficient * this.serviceLevel, Math.min(ownSector.intermediateInputCoeffient*this.intermediateGoodsStock, ownSector.energyCoefficient *this.energyLevel ))));
			outputIntermediate = (totalFactorProductivity*ownSector.intermediateInputCoeffient*this.intermediateGoodsStock);
			outputLabor = (totalFactorProductivity*ownSector.laborCoefficient * this.laborStock);
			outputCapital = (totalFactorProductivity*ownSector.capitalCoefficent*this.capitalStock);
			outputService = (totalFactorProductivity*ownSector.serviceCoefficient * this.serviceLevel);
			outputEnergy = (totalFactorProductivity*ownSector.energyCoefficient *this.energyLevel);
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
					EnergyInventory aInventory = mallInventoryList.get(i);
					delivery = Math.min(this.internalInventoy.inventoryStock, this.internalInventoy.inventoryStock * aInventory.plannedDelivery/plannedOutputProduction);				
//					delivery = this.internalInventoy.inventoryStock * aInventory.plannedDelivery/plannedOutputProduction;
					double newStock = aInventory.inventoryStock + delivery;
					if(newStock>0)
						aInventory.quality = (aInventory.quality*aInventory.inventoryStock + delivery*this.internalInventoy.quality)/newStock;
					aInventory.inventoryStock+=  delivery;
					this.internalInventoy.inventoryStock -=  delivery;
				}
			}
//			if(this.ownSector.sectorID==8) {	
			double currentTick = RunEnvironment.getInstance().getCurrentSchedule().getTickCount();		
			try {
	            File file = new File("outputEnergyFirm.txt");
	            FileOutputStream fos = new FileOutputStream(file, true);		            	            
	            PrintStream ps = new PrintStream(fos);      // Create a PrintStream that writes to the file          
	            System.setOut(ps);                          // Redirect System.out to the PrintStream	            
	            System.out.println(
	            		"Tick:" + currentTick +  "   Firm " + this.ID + "  Sector ID: " + this.ownSector.sectorID + "    Output: "+this.output+"	Planned output: "+this.plannedOutput+
	            		"  avg. Market Share: "+this.avgMarketShare+"  avg. Unit Costs:"+this.avgUnitCosts+
	            		"  Assets: "+this.balanceSheet.sumAssets+"  Equity: "+this.balanceSheet.equity+"  Liabilities: "+this.balanceSheet.sumLiabilities+
	            		"  Cash: "+this.balanceSheet.assets.cash
	            		);
//				System.out.println("Tick:" + currentTick + "   outputCapital:	 " + outputCapital + " outputIntermediate:	"+ outputIntermediate + "	outputLabor: " + outputLabor + "	outputIntermediate:	" + outputIntermediate + "	outputService:	" + outputService + "	outputEnergy:	"+outputEnergy);
	            fos.close();
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
				
//			}
		     }
		//determines wage offer using greedy algorithm
		@ScheduledMethod (start = 1, interval = 1, priority = 25)
		public void accounting() {
			revenue = 0;
			changeInventories = 0;
			valueInventory = this.internalInventoy.inventoryStock*price;
			interestIncome = paymentAccount.interestPayment;
			for(int i=0; i < mallInventoryList.size();i++) {
				EnergyInventory aInventory = mallInventoryList.get(i);
				revenue += aInventory.revenues;
				paymentAccount.accountBalance += aInventory.revenues;
				salesToFirms += aInventory.salesToFirms;
				salesToHouseholds += aInventory.salesToHouseholds;
				totalSoldQuantities += aInventory.soldQuantities ;
				totalMarketSales += aInventory.totalMarketSales;
				valueInventory += aInventory.inventoryStock * aInventory.price;
				aInventory.changeInventory = 	aInventory.inventoryStock - aInventory.inventoryStockEndOfIteration;
				aInventory.inventoryStockEndOfIteration = aInventory.inventoryStock;
				changeInventories += aInventory.changeInventory;
			}
			this.internalInventoy.inventoryStock = (1-this.serviceDepreciation)*this.internalInventoy.inventoryStock;
			laborCosts = this.wage*this.laborStock;
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
			this.markUp = price/unitCosts;
			this.earnings = revenue + changeInventories*price + interestIncome- totalCosts;
			updateBalanceSheet();
			double tempDiv = this.dividendRatio * Math.max(earnings, 0);
			if(balanceSheet.equity > tempDiv) {
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
			//System.out.println("Equity	"+balanceSheet.equity);
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
			public void computeInputDemandForProductionPlan() {
				this.expectedServiceCosts = 0;
				this.expectedEnergyCosts = 0;
				this.expectedIntermediateCosts=0;
				//Capital Demand
				this.capitalRequired = this.plannedOutput / (totalFactorProductivity*ownSector.capitalCoefficent);
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
				delta1 =  Math.max(0, ( this.plannedOutput / (totalFactorProductivity*ownSector.energyCoefficient) -   this.energyLevel));
				delta2 =  Math.max(0, ( this.energyLevel - this.plannedOutput / (totalFactorProductivity*ownSector.energyCoefficient)));
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
				this.laborDemand = this.plannedOutput / (totalFactorProductivity*ownSector.laborCoefficient);
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