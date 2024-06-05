package eWAGI_Firms;
import java.util.ArrayList;
import repast.simphony.context.Context;
import repast.simphony.engine.environment.RunEnvironment;
import repast.simphony.engine.schedule.ScheduledMethod;
import repast.simphony.util.ContextUtils;
import java.util.logging.Logger;
public class Household {
	int ID;
	int tick;
	int bankID;
	int clearingHouseID;
	double consumptionBudget;
	double consumptionExpenditures;
	double reservationWage;
	int generalSkillLevel;
	double disposableIncome = GlobalParameters.disposableIncome ;	
	double propensityToConsume = GlobalParameters.propensityToConsume;
	double probChangeSupplier = GlobalParameters.probChangeSupplier;
	double probChangeSectorUnemployed = GlobalParameters.probChangeSectorUnemployed;
	double intensityChoiceSectorUnemployed = GlobalParameters.intensityChoiceSectorUnemployed;
	double intensityChoicePrice = GlobalParameters.intensityChoicePrice;
	double intensityChoiceQuality = GlobalParameters.intensityChoiceQuality;
	double onTheJobSearchRate = GlobalParameters.onTheJobSearchRate;
	double initAccountbalance = GlobalParameters.initAccountbalance;
	double interestIncome;
	boolean employed = false;
	boolean onTheJobSearch = false;
	WorkContract workContract;
	Sector currentWorkingSector; 
	PaymentAccount paymentAccount;
	ArrayList<SpecificSkill> specificSkillPortfolio;
	SpecificSkill relevantSpecificSkills;
	double dividendIncome;
	double laborIncome;
	ArrayList<ConsumptionCoefficient> consumptionCoefficientList;
	ArrayList<Inventory> potentialSupplierList;
	ArrayList<PortfolioItem> assetPortfolio;
	Household(int ID, int bankID, int clearingHouseID){
		this.ID = ID;
		this.bankID = bankID;
		this.clearingHouseID = clearingHouseID;
		consumptionCoefficientList = new ArrayList<ConsumptionCoefficient>();
		potentialSupplierList = new ArrayList<Inventory>();
		assetPortfolio = new ArrayList<PortfolioItem> ();;
		specificSkillPortfolio = new ArrayList<SpecificSkill>();
		paymentAccount = new PaymentAccount(ID, bankID,initAccountbalance,2);		
	}
	//determines wage offer using greedy algorithm
	@ScheduledMethod (start = 1, interval = 1, priority = 100)
	public void setupIteration() {
		tick = (int) RunEnvironment.getInstance().getCurrentSchedule().getTickCount();
		if(tick==1) {
			// Goods
			Context  context = ContextUtils.getContext(this);
			Iterable<GoodsFirm> allGoodsFirms = context.getObjects(GoodsFirm.class);
			for(GoodsFirm obj : allGoodsFirms) {
				assetPortfolio.add( new PortfolioItem (obj.firmStock,1));
			}
			Iterable<ServiceFirm> allServiceFirms = context.getObjects(ServiceFirm.class);
			for(ServiceFirm obj : allServiceFirms) {
				assetPortfolio.add( new PortfolioItem (obj.firmStock,1));
			}
			Iterable<EnergyFirm> allEnergyFirms = context.getObjects(EnergyFirm.class);
			for(EnergyFirm obj : allEnergyFirms) {
				assetPortfolio.add( new PortfolioItem (obj.firmStock,1));
			}
			Iterable<ClearingHouse> allClearingHouses = context.getObjects(ClearingHouse.class);
			for(ClearingHouse obj : allClearingHouses) {
				if(obj.ID == clearingHouseID) {
					for(int i=0; i < assetPortfolio.size();i++) {
						obj.assetPortfolioList.add(assetPortfolio.get(i));
					}
					break;
				}
			}
		}
		for(int i=0; i < consumptionCoefficientList.size();i++) {
			consumptionCoefficientList.get(i).demandQuantity =0.0;
			consumptionCoefficientList.get(i).receivedQuantity = 0.0;
		}
		dividendIncome = 0;
		paymentAccount.pendingOutgoingTransactionsList.clear();
		paymentAccount.receivedTransactionsList.clear();
	}

	@ScheduledMethod (start = 1, interval = 1, priority = 90)
	public void determineConsumptionBudget() {
		consumptionBudget = disposableIncome*propensityToConsume;	
	}

	@ScheduledMethod (start = 1, interval = 1, priority = 85)
	public void budgetAllocation() {
		for(int i=0; i < consumptionCoefficientList.size();i++) {
			consumptionCoefficientList.get(i).budget = consumptionCoefficientList.get(i).coefficient * this.consumptionBudget ;
		}
	}
	
	@ScheduledMethod (start = 1, interval = 1, priority = 65)
	public void shoppingActivity() {
		Context  context = ContextUtils.getContext(this);
		// Goods
		Iterable<GoodsSector> allSectors = context.getObjects(GoodsSector.class);
		for(int i=0; i < consumptionCoefficientList.size();i++) {
			for(GoodsSector obj : allSectors) {
				if(obj.sectorID==consumptionCoefficientList.get(i).sectorID) {
					GoodsSector aSector = obj;
					GoodsMall aMall = aSector.mallList.get(0);
					//Choose firm
					double random = Math.random();
					if(this.tick ==1 || ( random < probChangeSupplier || consumptionCoefficientList.get(i).supplier.stockOut)) {
						double denLogit = 0.0; 
						potentialSupplierList.clear();
						for(int j =0 ; j < aMall.firmInventories.size(); j++) {
							potentialSupplierList.add(aMall.firmInventories.get(j));
							denLogit += Math.exp(this.intensityChoicePrice*aMall.firmInventories.get(j).price + this.intensityChoiceQuality*aMall.firmInventories.get(j).quality);
						}
						double random1 = Math.random();
						double prob = 0;
						for(int j =0; j < potentialSupplierList.size();j++) {
							prob +=  Math.exp(this.intensityChoicePrice* potentialSupplierList.get(j).price + this.intensityChoiceQuality*potentialSupplierList.get(j).quality)/denLogit;
							if(random1<= prob) {
								consumptionCoefficientList.get(i).supplier = potentialSupplierList.get(j);
								break;
							}
						}
					}
					for(int j=0; j < aSector.mallList.get(0).firmInventories.size();j++) {
						if(consumptionCoefficientList.get(i).supplier.firmID==aSector.mallList.get(0).firmInventories.get(j).firmID) {
						
							((ConsumptionCoefficientGoods) consumptionCoefficientList.get(i)).goodsOrder = new GoodsOrder(this.ID, consumptionCoefficientList.get(i).supplier.firmID,consumptionCoefficientList.get(i).budget/consumptionCoefficientList.get(i).supplier.price, false);
							aSector.mallList.get(0).firmInventories.get(j).orderBook.add(((ConsumptionCoefficientGoods) consumptionCoefficientList.get(i)).goodsOrder);
							if (((ConsumptionCoefficientGoods) consumptionCoefficientList.get(i)).goodsOrder.orderQuantity < 0) {
								break;
							}
							break;
						}
					}
					break;
				}
			}
		}		
		//Services 
		Iterable<ServiceSector> allServiceSectors = context.getObjects(ServiceSector.class);
		for(int i=0; i < consumptionCoefficientList.size();i++) {
			for(ServiceSector obj : allServiceSectors) {
				if(obj.sectorID==consumptionCoefficientList.get(i).sectorID) {
					ServiceSector aSector = obj;
					ServiceMall aMall = aSector.mallList.get(0);
					//Choose firm
					double random = Math.random();
					if(this.tick ==1 || ( random < probChangeSupplier || consumptionCoefficientList.get(i).supplier.stockOut)) {
						double denLogit = 0.0; 
						potentialSupplierList.clear();
						for(int j =0 ; j < aMall.firmInventories.size(); j++) {
							potentialSupplierList.add(aMall.firmInventories.get(j));
							denLogit += Math.exp(this.intensityChoicePrice*aMall.firmInventories.get(j).price+ this.intensityChoiceQuality*aMall.firmInventories.get(j).quality);
						}
						double random1 = Math.random();
						double prob = 0;
						for(int j =0; j < potentialSupplierList.size();j++) {
							prob +=  Math.exp(this.intensityChoicePrice* potentialSupplierList.get(j).price+ this.intensityChoiceQuality*potentialSupplierList.get(j).quality)/denLogit;
							if(random1<= prob) {
								consumptionCoefficientList.get(i).supplier = potentialSupplierList.get(j);
								break;
							}
						}
					}
					for(int j=0; j < aSector.mallList.get(0).firmInventories.size();j++) {
						if(consumptionCoefficientList.get(i).supplier.firmID==aSector.mallList.get(0).firmInventories.get(j).firmID) {
							((ConsumptionCoefficientService)consumptionCoefficientList.get(i)).serviceOrder = new ServiceOrder(this.ID,aSector.mallList.get(0).firmInventories.get(j).firmID,consumptionCoefficientList.get(i).budget/consumptionCoefficientList.get(i).supplier.price, false);
							aSector.mallList.get(0).firmInventories.get(j).orderBook.add(((ConsumptionCoefficientService)consumptionCoefficientList.get(i)).serviceOrder);
							break;
						}
					}
					break;
				}
			}
		}		
		//Energy
		Iterable<EnergySector> allEnergySectors = context.getObjects(EnergySector.class);
		for(int i=0; i < consumptionCoefficientList.size();i++) {
			for(EnergySector obj : allEnergySectors) {
				if(obj.sectorID==consumptionCoefficientList.get(i).sectorID) {
					EnergySector aSector = obj;
					EnergyMall aMall = aSector.mallList.get(0);
					
					//Choose firm
					double random = Math.random();
					if(this.tick ==1 || ( random < probChangeSupplier || consumptionCoefficientList.get(i).supplier.stockOut)) {
						double denLogit = 0.0; 
						potentialSupplierList.clear();
						
						for(int j =0 ; j < aMall.firmInventories.size(); j++) {
							potentialSupplierList.add(aMall.firmInventories.get(j));
							denLogit += Math.exp(this.intensityChoicePrice*aMall.firmInventories.get(j).price+ this.intensityChoiceQuality*aMall.firmInventories.get(j).quality);
						}
						double random1 = Math.random();
						double prob = 0;
						for(int j =0; j < potentialSupplierList.size();j++) {
							prob +=  Math.exp(this.intensityChoicePrice* potentialSupplierList.get(j).price + this.intensityChoiceQuality*potentialSupplierList.get(j).quality)/denLogit;
							if(random1<= prob) {
								consumptionCoefficientList.get(i).supplier = potentialSupplierList.get(j);
								
								break;
							}
						}
					}
					for(int j=0; j < aSector.mallList.get(0).firmInventories.size();j++) {
						if(consumptionCoefficientList.get(i).supplier.firmID==aSector.mallList.get(0).firmInventories.get(j).firmID) {
							((ConsumptionCoefficientEnergy)consumptionCoefficientList.get(i)).energyOrder = new EnergyOrder(this.ID,aSector.mallList.get(0).firmInventories.get(j).firmID,consumptionCoefficientList.get(i).budget/consumptionCoefficientList.get(i).supplier.price, false);
							aSector.mallList.get(0).firmInventories.get(j).orderBook.add(((ConsumptionCoefficientEnergy)consumptionCoefficientList.get(i)).energyOrder);
							
							break;
							
						}
					}
					break;
					
				}
			}
		}		
	}
	
	@ScheduledMethod (start = 1, interval = 1, priority = 59)
	public void laborMarketActivities2() {
		onTheJobSearch = false;		
		if(workContract != null && workContract.canceled) {
			workContract = null;
			employed = false;
		}
		if(!employed) {
			double rand = Math.random();
			if(rand < probChangeSectorUnemployed) {
				Context  context = ContextUtils.getContext(this);
				// Goods
				Iterable<Sector> allSectors = context.getObjects(Sector.class);
				double denLogit = 0; 
				for(Sector obj : allSectors) {
					denLogit += Math.exp(this.intensityChoiceSectorUnemployed*obj.laborMarket.openPositions);
				}
				double random1 = Math.random();
				double prob = 0;
				for(Sector obj : allSectors) {
					prob +=  Math.exp(this.intensityChoiceSectorUnemployed*obj.laborMarket.openPositions)/denLogit;
					if(random1<= prob) {
						currentWorkingSector = obj;
						break;
					}
				}
			}
		}else{
			double rand = Math.random();
			if(rand < onTheJobSearchRate) {
				onTheJobSearch = true;
			}
		}
		if(!employed || onTheJobSearch) {
			JobSearch jobSearch = new JobSearch(ID,generalSkillLevel,reservationWage, 0, this);
			this.currentWorkingSector.laborMarket.jobSearchList.add(jobSearch);
			
		}
	}

	@ScheduledMethod (start = 1, interval = 1, priority = 50)
	public void consumption() {
		for(int i =0; i < consumptionCoefficientList.size();i++) {
			consumptionCoefficientList.get(i).receivedQuantity = consumptionCoefficientList.get(i).getAcceptedQuantity();
			consumptionExpenditures +=   consumptionCoefficientList.get(i).getInvoiceAmount();
			paymentAccount.pendingOutgoingTransactionsList.add(new PaymentTransaction(ID, consumptionCoefficientList.get(i).supplier.firmID, paymentAccount.bankID, consumptionCoefficientList.get(i).supplier.bankID, consumptionCoefficientList.get(i).getInvoiceAmount(), "salesRevenue"));
			
		}
	}

	@ScheduledMethod (start = 1, interval = 1, priority = 20)
	public void collectDividends() {
		for(int i=0; i< assetPortfolio.size(); i++) {
			double dividendOfFirm = assetPortfolio.get(i).numShares* assetPortfolio.get(i).stock.dividendPerShare; 
			dividendIncome += dividendOfFirm;
			
		}
	}
	
	@ScheduledMethod (start = 1, interval = 1, priority = 5)
	public void computeIncome() {
		Context  context = ContextUtils.getContext(this);
		// Goods
		Iterable<StatisticAgency> allStatisticAgencies = context.getObjects(StatisticAgency.class);
		if(workContract!=null) {
			laborIncome = workContract.wage;
		}else {
			for(StatisticAgency obj : allStatisticAgencies) {
				laborIncome = 1*obj.avgWage;
				
			}
		}
		interestIncome = paymentAccount.interestPayment;
		disposableIncome= laborIncome + dividendIncome + interestIncome;		
	}
	
	void activateNewWorkContract(WorkContract newWorkContract) {
		if(workContract!= null) {
			workContract.canceled = true;
			//Check if the old contract is canceled in the firm
		}
		workContract = newWorkContract;
		employed = true;
	}
}


