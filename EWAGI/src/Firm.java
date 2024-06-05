package eWAGI_Firms;
import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;
import repast.simphony.context.Context;
import repast.simphony.engine.environment.RunEnvironment;
import repast.simphony.engine.schedule.ScheduledMethod;
import repast.simphony.space.continuous.ContinuousSpace;
import repast.simphony.space.grid.Grid;
import repast.simphony.util.ContextUtils;
import java.util.logging.Logger;

//New  test


public class Firm{
	private static final Logger logger = Logger.getLogger(Firm.class.getName());
	private ContinuousSpace<Object> space;
	private Grid<Object> grid;
	int tick;
	int ID;
	int clearingHouseID, bankID;
	boolean SWITCH_LaborMarket = true;
	boolean SWITCH_Pricing = true;
	boolean SWITCH_RD = true;
	boolean SWITCH_Banking = true;
	boolean SWITCH_Bankruptcy = true;
	ArrayList<Inventory> currentSupplierList;
	ArrayList<Inventory> potentialSupplierList;
	ArrayList<InputItem> inputGoodsRequirementList;
	ArrayList<InputItem> inputServiceRequirementList;
	ArrayList<InputItem> inputEnergyRequirementList;
	ArrayList<ServiceContract> serviceContracts;
	ArrayList<EnergyContract> energyContracts;
	ArrayList<WorkContract> workContractList; 
	ArrayList<CreditOffer> creditOfferList;
	ArrayList<Credit> creditList;
	Stock firmStock;
	PaymentAccount paymentAccount;
	Sector ownSector;
	BalanceSheet balanceSheet;
	ArrayList<Plant> plantList;
	boolean financialCrisis = false;
	boolean bankcrupcyInsolvency = false;
	boolean bankcrupcyIlliquidity = false;
	boolean finaciallyConstrained = false;
	boolean debtRestructuring = false;
	double outputIntermediate;
	double outputLabor;
	double outputCapital;
	double outputService ;
	double outputEnergy;
	ArrayList<Double> previousOutputs;
	double avgPreviousOutputs, plannedOutputBeforeSmoothing;
	double revenue;
	double changeInventories;
	double earnings;
	double totalCosts;
	double unitCosts;
	double laborCosts;
	double capitalCosts;
	double creditCosts;
	double initialOutput;
	double markUp;
	double changeInventoryValue;
	double targetMarkUp;
	double marketShare;
	double totalSoldQuantities;
	double totalMarketSales;
	double diffMarketShares;
	double interests;
	double dividends;
	double dividendPerShare;
	int numOutstandingShares;
	double plannedOutputProduction, firstPlannedOutput;
	double technology;
	double valueInventory;
	double capitalProductivity, meanSpecificSkills, quality, totalFactorProductivity,laborProductivity, cumulatedProcessInnovations,productivityEffectProductInnovation;
	double productInnoEfficiency, processInnoEfficiency;
	double productInnoInvestment, processinnoInvestment;
	double rdKnowledgeStock;
	double shareProductInnovation;
	double plannedRDExpenditure;
	double rdIntensity;
	double plannedOutputRD;
	double outputToMarket; 
	double outputToRD;
	double rdInvestment;
	double probChangeSupplierGoods = GlobalParameters.probChangeSupplierGoods;
	double probChangeSupplierService = GlobalParameters.probChangeSupplierService;
	double probChangeSupplierEnergy = GlobalParameters.probChangeSupplierEnergy;
	double intensityChoicePriceGoods = GlobalParameters.intensityChoicePriceGoods;
	double intensityChoicePriceService = GlobalParameters.intensityChoicePriceService;
	double intensityChoicePriceEnergy = GlobalParameters.intensityChoicePriceEnergy;
	double intensityChoiceQualityGoods = GlobalParameters.intensityChoiceQualityGoods;
	double intensityChoiceQualityService = GlobalParameters.intensityChoiceQualityService;
	double intensityChoiceQualityEnergy = GlobalParameters.intensityChoiceQualityEnergy;
	double PARoutputAdjustment = GlobalParameters.PARoutputAdjustment;
	double priceDelta = GlobalParameters.priceDelta;
	double inventoryBufferPCT = GlobalParameters.inventoryBufferPCT;
	double serviceDepreciation = GlobalParameters.serviceDepreciation;
	double energyDepreciation = GlobalParameters.energyDepreciation;
	double smoothAvgUnitCosts = GlobalParameters.smoothAvgUnitCosts;
	double smoothAvgMarketShare = GlobalParameters.smoothAvgMarketShare;
	double outputSmoothing = GlobalParameters.outputSmoothing;
	double dividendRatio = GlobalParameters.dividendRatio;
	double knowledgeDepreciation = GlobalParameters.knowledgeDepreciation;
	double stdDevProductInno = GlobalParameters.stdDevProductInno;
	double stdDevProcessInno = GlobalParameters.stdDevProcessInno;
	double productivityEffectProductInnovationAdjustment = GlobalParameters.productivityEffectProductInnovationAdjustment;
	double specificSkillAdjustment = GlobalParameters.specificSkillAdjustment;
	
//	double initAccountBalance = GlobalParameters.initAccountBalance1;	
	
	// final product
	double price, output, deltaOutput;;
	boolean outputRationed;
	Inventory internalInventoy;
	double avgMarketShare;
	double avgUnitCosts;
	//Intermediate Goods
	double intermediateGoodsStock, intermediateGoodsDemand, intermediateInput, averageIntermediateGoodConsumption,muBufferIntermediateGoods, intermediateInputValue, intermediateInputExpenditures, calcIntermediateGoodsPrice,intermediateInputCosts;
	//Capital
	double capitalStock;
	double depreciationRate;
	double capitalRequired, capitalDemand, qualityCapitalInvestments;
	double capitalInvestmentReal, capitalStockValue,capitalInvestment, calcCapitalPrice, calcCapitalCosts;
	//Labor 
	double laborStock, laborDemand;
	double vacancies;
	double unmatchedVacancies;
	double redundancies;
	double wage;
	double laborStockProduction, laborStockRD;
	//Service
	double serviceDemand,serviceInput, serviceInputValue,calcServicePrice, serviceLevel, serviceLevelValue, averageServiceGoodConsumption, serviceExpenditure, calcServiceCosts,newRequiredServiceContracts;
	double sumContractedServiceAsClient = 0;
	//Energy
	double energyDemand, energyInput, energyLevel, energyLevelValue, energyInputValue, energyExpenditure, calcEnergyPrice, calcEnergyCosts, newRequiredEnergyContracts;
	double sumContractedEnergyAsClient= 0, averageEnergyGoodConsumption = 0;
	double expectedLaborCosts,expectedInvestmentExpenditures,expectedIntermediateCosts,expectedEnergyCosts,expectedServiceCosts;
	double externalFinancialNeeds, externalFinancialObtained, totalFinancialNeedsProduction, financialNeeds, internalFinancialResources;
	double totalfinancialNeedsFinancialObligations, totalFinancialNeeds;
	double taxPayments;
	double interestPayments, plannedInterestPayments;
	double interestIncome;
	double debtInstallmentPayments,plannedDebtInstallmentPayments;
	int debtRestructuringCounter = 0;;
	int debtRestructuringPeriod = 6;
	double inventoryStock;
	double mu,muBufferOutput,plannedOutput,potentialOutput;
	double salesToFirms, salesToHouseholds;
	Firm(ContinuousSpace<Object> space, Grid<Object> grid, int ID, int bankID, int clearingHouseID, double initialAccountBalance){
		this.ID = ID;
		this.bankID = bankID;
		this.clearingHouseID = clearingHouseID;
		this.space = space;
		this.grid = grid;
		previousOutputs = new ArrayList<Double>();
		avgPreviousOutputs = 0;
		deltaOutput = 0;
		firmStock = new Stock(ID);
		
		
		
		paymentAccount = new PaymentAccount(ID, bankID, initialAccountBalance,1);
		
		
		
		
		
		balanceSheet = new BalanceSheet();
		internalInventoy = new Inventory(ID,0,0,0);
		currentSupplierList = new ArrayList<Inventory>();
		potentialSupplierList = new ArrayList<Inventory>();
		inputGoodsRequirementList = new ArrayList<InputItem>();
		inputServiceRequirementList = new ArrayList<InputItem>();
		inputEnergyRequirementList = new ArrayList<InputItem>();
		serviceContracts = new ArrayList<ServiceContract>() ;
		energyContracts = new ArrayList<EnergyContract>();
		workContractList = new ArrayList<WorkContract>();
		creditOfferList = new ArrayList<CreditOffer>();
		creditList = new ArrayList<Credit>();
		this.price = 1.0;
		this.depreciationRate = 0.03;
		muBufferOutput = 0.2;
		this.mu = 0.05;
		numOutstandingShares = 100000;
		quality = 1.0;
		capitalProductivity = 1.0;
		meanSpecificSkills = 1.0;
		totalFactorProductivity = 1.0;
		laborProductivity = 1.0;
		cumulatedProcessInnovations= 0.0;
		productivityEffectProductInnovation  = 0.0;
		shareProductInnovation = 0.5;
		rdIntensity = 0.03;
		technology = 1.0;
		productInnoEfficiency = 0.05; 
		processInnoEfficiency = 0.05;
		outputIntermediate = 0.0;
		outputLabor = 0.0;
		outputCapital = 0.0;
		outputService = 0.0 ;
		outputEnergy = 0.0;
		valueInventory = 0;
		salesToFirms = 0; 
		salesToHouseholds = 0;
	}
	//Executed every iteration at the beginning of each loop
	@ScheduledMethod (start = 1, interval = 1, priority = 100)
	public void setupIteration() {
		Logger LOGGER = Logger.getLogger(Firm.class.getName());

	}
	//Price setting
	@ScheduledMethod (start = 1, interval = 1, priority = 95)
	public void pricing() {
		Logger LOGGER = Logger.getLogger(Firm.class.getName());
	}
	//Computes planned output
	@ScheduledMethod (start = 1, interval = 1, priority = 90)
	public void computePlannedOutput() {
		Logger LOGGER = Logger.getLogger(Firm.class.getName());
	}
	// Choose goods input supplier
	@ScheduledMethod (start = 1, interval = 1, priority = 80)
	public void chooseGoodsSupplier1() {		
		Context  context = ContextUtils.getContext(this);
		for(int i=0; i < inputGoodsRequirementList.size();i++) {
			potentialSupplierList.clear();
			double random = Math.random();
			//Supplier is changed with certain probability; choice is based on a logit modle that takes prices into account
			if(this.tick ==1 || ( random < probChangeSupplierGoods ) || inputGoodsRequirementList.get(i).rationed ) {
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
				//Actual choice based on logit
				for(int j =0; j < potentialSupplierList.size();j++) {
					prob +=  Math.exp(this.intensityChoicePriceGoods* potentialSupplierList.get(j).price+ this.intensityChoiceQualityGoods * potentialSupplierList.get(j).quality)/denLogit;
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
	
	// R&D planning; R&D expenditures are linked to a percentage of revenues (R&D intensity)
	@ScheduledMethod (start = 1, interval = 1, priority = 80)
	public void rdPlanning() {
		Logger LOGGER = Logger.getLogger(Firm.class.getName());
		this.plannedRDExpenditure = revenue*this.rdIntensity;
		plannedOutputRD = 0.0;
		if(this.unitCosts > 0 ) {
			plannedOutputRD = plannedRDExpenditure / unitCosts;
		}
		//From the expenditures, we derive the planned output that is dedicated to R&D
		plannedOutput += plannedOutputRD;
	}
	// Choose service input supplier
	@ScheduledMethod (start = 1, interval = 1, priority = 80)
	public void chooseServiceSupplier1() {
		Logger LOGGER = Logger.getLogger(Firm.class.getName());
		Context  context = ContextUtils.getContext(this);
		//Service providers chosen based on logit model; choice is taken with a given hazard rate
		for(int i=0; i < inputServiceRequirementList.size();i++) {
			potentialSupplierList.clear();
			double random = Math.random();
			if(this.tick ==1 || ( random < probChangeSupplierService )|| inputServiceRequirementList.get(i).rationed) {
				double denLogit = 0;
				//TODO: Implement mall choice
				Iterable<ServiceSector> allSectors = context.getObjects(ServiceSector.class);
				for(ServiceSector obj : allSectors) {
					if(obj.sectorID == inputServiceRequirementList.get(i).sectorID) {
						ServiceSector aSector= obj;
						for(int j =0; j < aSector.mallList.size()  ;j++) {
							for(int k =0 ; k < aSector.mallList.get(j).firmInventories.size(); k++) {
								potentialSupplierList.add( aSector.mallList.get(j).firmInventories.get(k));
								denLogit += Math.exp(this.intensityChoicePriceService*aSector.mallList.get(j).firmInventories.get(k).price + this.intensityChoiceQualityService * aSector.mallList.get(j).firmInventories.get(k).quality);
							}
						}
						break;
					}
				}
				double random1 = Math.random();
				double prob = 0;
				for(int j =0; j < potentialSupplierList.size();j++) {
					prob +=  Math.exp(this.intensityChoicePriceService* potentialSupplierList.get(j).price + this.intensityChoiceQualityService * potentialSupplierList.get(j).quality)/denLogit;
					if(random1<= prob) {
						for(int k=0; k < currentSupplierList.size(); k++) {
							if(currentSupplierList.get(k).sectorID==inputServiceRequirementList.get(i).sectorID) {
								currentSupplierList.remove(k);
								break;
							}
						}
						currentSupplierList.add(potentialSupplierList.get(j));
						inputServiceRequirementList.get(i).supplier = potentialSupplierList.get(j);
						break;
					}
				}
			}
		}
	}
	//Choose energy supplier; based on logit model; decision taken with given hazard rate
	@ScheduledMethod (start = 1, interval = 1, priority = 80)
	public void chooseEnergySupplier1() {
		Logger LOGGER = Logger.getLogger(Firm.class.getName());
		Context  context = ContextUtils.getContext(this);
		// Energy provider
		for(int i=0; i < inputEnergyRequirementList.size();i++) {
			potentialSupplierList.clear();
			double random = Math.random();
			if(this.tick ==1 || ( random < probChangeSupplierService )|| inputEnergyRequirementList.get(i).rationed) {
				double denLogit = 0;
				//TODO: Implement mall choice
				Iterable<EnergySector> allSectors = context.getObjects(EnergySector.class);
				for(EnergySector obj : allSectors) {
					if(obj.sectorID == inputEnergyRequirementList.get(i).sectorID) {
						EnergySector aSector= obj;
						for(int j =0; j < aSector.mallList.size()  ;j++) {
							for(int k =0 ; k < aSector.mallList.get(j).firmInventories.size(); k++) {
								potentialSupplierList.add( aSector.mallList.get(j).firmInventories.get(k));
								denLogit += Math.exp(this.intensityChoicePriceEnergy*aSector.mallList.get(j).firmInventories.get(k).price + this.intensityChoiceQualityEnergy * aSector.mallList.get(j).firmInventories.get(k).quality);
							}
						}
						break;
					}
				}
				double random1 = Math.random();
				double prob = 0;
				for(int j =0; j < potentialSupplierList.size();j++) {
					prob +=  Math.exp(this.intensityChoicePriceEnergy* potentialSupplierList.get(j).price+ this.intensityChoiceQualityEnergy * potentialSupplierList.get(j).quality )/denLogit;
					if(random1<= prob) {
						for(int k=0; k < currentSupplierList.size(); k++) {
							if(currentSupplierList.get(k).sectorID==inputEnergyRequirementList.get(i).sectorID) {
								currentSupplierList.remove(k);
								break;
							}
						}
						currentSupplierList.add(potentialSupplierList.get(j));
						inputEnergyRequirementList.get(i).supplier = potentialSupplierList.get(j);
						break;
					}
				}
			}
		}
	}
	//Determine the input demand given the planned output; here also the expected costs for the inputs are determined
	@ScheduledMethod (start = 1, interval = 1, priority = 75)
	public void computeInputDemand() {
		
	}

	@ScheduledMethod (start = 1, interval = 1, priority = 70)
	public void financialPlanning() {
		this.totalFinancialNeedsProduction =  expectedLaborCosts + expectedInvestmentExpenditures + expectedIntermediateCosts + expectedEnergyCosts + expectedServiceCosts ;
		internalFinancialResources = this.paymentAccount.accountBalance;
		externalFinancialObtained = 0.0;
		plannedDebtInstallmentPayments = 0.0;
		plannedInterestPayments = 0;
		for(int i=0; i < this.creditList.size();i++) {
			plannedDebtInstallmentPayments += creditList.get(i).installmentPayment;
			plannedInterestPayments += creditList.get(i).currentDebtLevel*creditList.get(i).interestRateMonthly;
		}
		this.totalfinancialNeedsFinancialObligations = taxPayments + debtInstallmentPayments + interestPayments;
		this.totalFinancialNeeds = totalfinancialNeedsFinancialObligations + totalFinancialNeedsProduction;
		this.externalFinancialNeeds = Math.max(0, totalFinancialNeeds - internalFinancialResources );
		try {
            File file = new File("TotalFinancialNeed.txt");
            FileOutputStream fos = new FileOutputStream(file, true);		            	            
            PrintStream ps = new PrintStream(fos);      // Create a PrintStream that writes to the file          
            System.setOut(ps);                          // Redirect System.out to the PrintStream	            
            System.out.println("Tick:" + tick + "   Firm " + this.ID + "  Sector ID: " + this.ownSector.sectorID +
    				"external Financial Needs"+ externalFinancialNeeds+ "Total Financial Need"+totalFinancialNeeds);
            ps.close();   // Close the streams
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }			

	}
		
	// If external financing is needed, the firm applies for bank loans; in the current implementation, the firm only applies at the home bank
	@ScheduledMethod (start = 1, interval = 1, priority = 69)
	public void creditApplication() {
		creditOfferList.clear();
		if(externalFinancialNeeds>0) {
			//First version: firm only applies at home bank
			Context  context = ContextUtils.getContext(this);
			Iterable<Bank> Banks = context.getObjects(Bank.class);
			for(Bank obj : Banks) {
				if(obj.ID ==this.paymentAccount.bankID) {
					Bank aBank = obj;
					aBank.creditApplicationList.add(new CreditApplication(ID, aBank.ID,externalFinancialNeeds, balanceSheet.equity, balanceSheet.liabilities.totalDebt ));
							
				}
			}
		}
	}
	// Firm checks credit offers
	@ScheduledMethod (start = 1, interval = 1, priority = 67)
	public void chooseCreditOffers() {	
		CreditOffer bestOffer = null;
		CreditOffer rationedOffer = null;
		CreditOffer acceptedOffer = null ;
		//Check if the offered credit is equal or lower the requested credit; if full credit is accommodated, choose the one with the lowest interest rate
		if(creditOfferList.size()>0) {
			for(int i=0; i < creditOfferList.size(); i++) {
				if(Math.abs(creditOfferList.get(i).creditOffered-externalFinancialNeeds)<1e-5) {
					if(bestOffer==null || bestOffer.interestRate > creditOfferList.get(i).interestRate) {
						bestOffer = creditOfferList.get(i);
					}
				}else {
					if(rationedOffer==null || rationedOffer.creditOffered > creditOfferList.get(i).creditOffered) {
						rationedOffer = creditOfferList.get(i);
					}
				}
			}
			//If there is a credit that offers full financing, this one is chose
			if(bestOffer!=null){
				acceptedOffer = bestOffer;
			}else {
				acceptedOffer = rationedOffer;
			}
		}
		//Add accepted offer at bank's accepted credit list
		if(acceptedOffer != null) {
			Context  context = ContextUtils.getContext(this);
			Iterable<Bank> Banks = context.getObjects(Bank.class);
			for(Bank obj : Banks) {
				if(obj.ID ==acceptedOffer.bankID) {
					Bank aBank = obj;
					aBank.acceptedCreditApplications.add(acceptedOffer);
					break;
				}
			}
		}
	}
// Check financial status after external financing; if available resources are sufficient to 
// finance all activities, the firm remains financially sound.
//If the firm can cover all financial obligations but not the full production, then the firm
	//is financially constrained and adjust its output
	//If the firm cannot cover all financial obligations, it is in a financial crisis
	@ScheduledMethod (start = 1, interval = 1, priority = 66)
	public void financialStatus() {
		Logger LOGGER = Logger.getLogger(Firm.class.getName());
		internalFinancialResources = internalFinancialResources + externalFinancialObtained;
		this.totalFinancialNeeds = totalfinancialNeedsFinancialObligations + totalFinancialNeedsProduction;
		if(internalFinancialResources >= totalFinancialNeeds) {
			financialCrisis = false;
			bankcrupcyIlliquidity = false;
			finaciallyConstrained = false;
		} else if (internalFinancialResources < totalFinancialNeeds && internalFinancialResources > this.totalfinancialNeedsFinancialObligations) {
			financialCrisis = false;
			bankcrupcyIlliquidity = false;
			finaciallyConstrained = true;
		}else {
			financialCrisis = true;
			bankcrupcyIlliquidity = false;
			finaciallyConstrained = false;
		}
	}
//The firm pays interest and debt installments on its loans
	@ScheduledMethod (start = 1, interval = 1, priority = 65)
	public void interestInstallmentPayments() {
		Logger LOGGER = Logger.getLogger(Firm.class.getName());
		if(!financialCrisis) {
			debtInstallmentPayments = 0;
			interestPayments = 0;
			Context  context = ContextUtils.getContext(this);
			Iterable<Bank> Banks = context.getObjects(Bank.class);
			for(int i=0; i < this.creditList.size();i++) {
				if(creditList.get(i).justGranted) {
					creditList.get(i).justGranted=false;
				}else {
				for(Bank obj : Banks) {
					if(obj.ID ==creditList.get(i).bankID) {
						Bank aBank = obj;
							creditList.get(i).currentDebtLevel -= creditList.get(i).installmentPayment;
							debtInstallmentPayments += creditList.get(i).installmentPayment;
							internalFinancialResources -= creditList.get(i).installmentPayment;
							creditList.get(i).interestPayment =  creditList.get(i).currentDebtLevel*creditList.get(i).interestRateMonthly;
							interestPayments += creditList.get(i).interestPayment;
							internalFinancialResources -= creditList.get(i).interestPayment;
							paymentAccount.pendingOutgoingTransactionsList.add(new PaymentTransaction(ID, aBank.ID, paymentAccount.bankID, aBank.centralBankReserves.bankID, creditList.get(i).installmentPayment, "debtInstallment"));
							paymentAccount.pendingOutgoingTransactionsList.add(new PaymentTransaction(ID, aBank.ID, paymentAccount.bankID, aBank.centralBankReserves.bankID, creditList.get(i).interestPayment, "interest"));
							//aBank.centralBankReserves.accountBalance += creditList.get(i).installmentPayment + creditList.get(i).currentDebtLevel*creditList.get(i).interestRateMonthly;
						break;
					}
				}
				}
			}
		}
	}
// Debt restructuring if the firm is in a financial crisis
	@ScheduledMethod (start = 1, interval = 1, priority = 65)
	public void debtRestructuring() {		
		if(financialCrisis) {
			debtRestructuringCounter = debtRestructuringPeriod;
			debtInstallmentPayments = 0;
			interestPayments = 0;
			Context  context = ContextUtils.getContext(this);
			Iterable<Bank> Banks = context.getObjects(Bank.class);
			for(int i=0; i < this.creditList.size();i++) {
				if(!creditList.get(i).restructuring) {
					if(!creditList.get(i).restructured) {
						creditList.get(i).restructuring = true;
					}else {
						bankcrupcyIlliquidity = true;
						break;
					}	
				}
			}
			//If the firm is in finacial crisis but not (yet) bankrupt, it can pause the repayment of the loans and the payment of the interets for a given period. This can only be once for a open loan.
			//In this case, the not paid interest is added to the debt
			//If a loan is about to fail a second time, the debt restucturing has failed and the firm has to declare bankruptcy
			/*TODO Tax payments are not considered yet*/
			if(!bankcrupcyIlliquidity) {
				for(int i=0; i < this.creditList.size();i++) {
					double interestPayment =  creditList.get(i).currentDebtLevel*creditList.get(i).interestRateMonthly;
					creditList.get(i).currentDebtLevel += interestPayment;
					creditList.get(i).interestPayment = 0;
					creditList.get(i).installmentPayment = 0;
					if(debtRestructuringCounter==0) {
						creditList.get(i).restructured = true;
						creditList.get(i).restructuring = false;
					}
				}
				if(debtRestructuringCounter==0)
					this.financialCrisis = false;
				debtRestructuringCounter --;
				//If bankrupt, the start creit default procedure
			}else {
				for(int i=0; i < this.creditList.size();i++) {
					for(Bank obj : Banks) {
						if(obj.ID ==creditList.get(i).bankID) {
							Bank aBank = obj;
							creditList.get(i).defaulted = true;
							aBank.creditDefaultProcedure(creditList.get(i));
							break;
						}
					}
				}
			}
		}
}
	//In case the firm is financial constrained or in financial crisis, the output planning is adjusted.
	// 1. All r&D plans are canceled
	// 2. The planned output is iteratively decremented until the available recourses are
	//sufficient to pay all obligations and the adjusted output
	@ScheduledMethod (start = 1, interval = 1, priority = 64)
	public void replanningProduction() {
		firstPlannedOutput = plannedOutput;
		if(finaciallyConstrained ||  financialCrisis) {
			// Set R&D to 0
			System.out.println("Firm ID"+this.ID + "Sector ID"+ownSector.sectorID +"rationed" + "A & B"+ finaciallyConstrained + financialCrisis  );
			plannedOutput = plannedOutput - plannedOutputRD;
			plannedOutputRD = 0;
			//Output decrements
			plannedOutput = plannedOutput*(1- PARoutputAdjustment);
			plannedOutputProduction = plannedOutput;
			computeInputDemandForProductionPlan();
			double updatedFinancialNeedsProduction =  expectedLaborCosts + expectedInvestmentExpenditures + expectedIntermediateCosts + expectedEnergyCosts + expectedServiceCosts ;			
			int counter = 0;				
			while (updatedFinancialNeedsProduction > this.internalFinancialResources && plannedOutput > 0.05*firstPlannedOutput) {				
				plannedOutput = plannedOutput*(1- PARoutputAdjustment);				
				plannedOutputProduction = plannedOutput;				
				computeInputDemandForProductionPlan();				
				updatedFinancialNeedsProduction =  expectedLaborCosts + expectedInvestmentExpenditures + expectedIntermediateCosts + expectedEnergyCosts + expectedServiceCosts ;			
				counter++;			
				}
			}
 }			  
//Labor market activities
@ScheduledMethod (start = 1, interval = 1, priority = 60)
public void labormarketActivities() {
	logger.info("Executing inputGoodsOrderRound1()");
	
}
//Labor market activities
@ScheduledMethod (start = 1, interval = 1, priority = 56)
public void labormarketActivities2() {
}
//The firms enter the goods malls for obtaining the inputs needed for production
@ScheduledMethod (start = 1, interval = 1, priority = 56)
public void inputGoodsOrderRound1() {	
	Context  context = ContextUtils.getContext(this);
	for(int i =0; i < inputGoodsRequirementList.size();i++) {
		Iterable<GoodsSector> allSectors = context.getObjects(GoodsSector.class);
		for(GoodsSector obj : allSectors) {
			if(obj.sectorID == inputGoodsRequirementList.get(i).sectorID) {
				GoodsSector aSector= obj;
				/*TODO*: Change if more malls per sector are allowed*/
				for(int j=0; j < aSector.mallList.get(0).firmInventories.size();j++) {
					if(inputGoodsRequirementList.get(i).supplier.firmID==aSector.mallList.get(0).firmInventories.get(j).firmID) {
						inputGoodsRequirementList.get(i).order = new GoodsOrder(this.ID, inputGoodsRequirementList.get(i).supplier.firmID,inputGoodsRequirementList.get(i).totalDemand, true);
						//Place the order
						aSector.mallList.get(0).firmInventories.get(j).orderBook.add((GoodsOrder) inputGoodsRequirementList.get(i).order);
						break;
					}
				}
				break;
			}
		}
	}
}
//The firms enter the service malls for obtaining the inputs needed for production
@ScheduledMethod (start = 1, interval = 1, priority = 56)
public void inputServiceOrderRound1() {
	Logger LOGGER = Logger.getLogger(Firm.class.getName());
	Context  context = ContextUtils.getContext(this);
	for(int i =0; i < inputServiceRequirementList.size();i++) {
		Iterable<ServiceSector> allSectors = context.getObjects(ServiceSector.class);
		for(ServiceSector obj : allSectors) {
			if(obj.sectorID == inputServiceRequirementList.get(i).sectorID) {
				ServiceSector aSector= obj;
				for(int j=0; j < aSector.mallList.get(0).firmInventories.size();j++) {
					if(inputServiceRequirementList.get(i).supplier.firmID==aSector.mallList.get(0).firmInventories.get(j).firmID) {
						inputServiceRequirementList.get(i).order = new ServiceOrder(this.ID, inputServiceRequirementList.get(i).supplier.firmID,inputServiceRequirementList.get(i).totalDemand, true);
						aSector.mallList.get(0).firmInventories.get(j).orderBook.add((ServiceOrder) inputServiceRequirementList.get(i).order);
						break;
					}
				}
				break;
			}
		}
	}
}
//The firms enter the enery malls for obtaining the inputs needed for production
@ScheduledMethod (start = 1, interval = 1, priority = 56)
public void inputEnergyOrderRound1() {
	Logger LOGGER = Logger.getLogger(Firm.class.getName());
	Context  context = ContextUtils.getContext(this);
	for(int i =0; i < inputEnergyRequirementList.size();i++) {
		Iterable<EnergySector> allSectors = context.getObjects(EnergySector.class);
		for(EnergySector obj : allSectors) {
			if(obj.sectorID == inputEnergyRequirementList.get(i).sectorID) {
				EnergySector aSector= obj;
				for(int j=0; j < aSector.mallList.get(0).firmInventories.size();j++) {
					if(inputEnergyRequirementList.get(i).supplier.firmID==aSector.mallList.get(0).firmInventories.get(j).firmID) {
						inputEnergyRequirementList.get(i).order = new EnergyOrder(this.ID, inputEnergyRequirementList.get(i).supplier.firmID,inputEnergyRequirementList.get(i).totalDemand, true);
						aSector.mallList.get(0).firmInventories.get(j).orderBook.add((EnergyOrder) inputEnergyRequirementList.get(i).order);
						break;
					}
				}
				break;
			}
		}
	}
}

//Firm received input goods 
@ScheduledMethod (start = 1, interval = 1, priority = 50)
public void inputGoodsDeliveryRound1() {
	Logger LOGGER = Logger.getLogger(Firm.class.getName());
	for(int i =0; i < inputGoodsRequirementList.size();i++) {
		inputGoodsRequirementList.get(i).receivedQuantity =inputGoodsRequirementList.get(i).order.acceptedQuantity;
		GoodsOrder thisOrder = ((GoodsOrder) inputGoodsRequirementList.get(i).order);
		int supplierID = inputGoodsRequirementList.get(i).supplier.firmID;
		int supplierBankID = inputGoodsRequirementList.get(i).supplier.bankID;
		this.paymentAccount.pendingOutgoingTransactionsList.add(new PaymentTransaction(ID, supplierID, paymentAccount.bankID, supplierBankID, thisOrder.invoiceAmount, "salesRevenue"));
	    //Check if the order has largely be satisfied	
		if(inputGoodsRequirementList.get(i).receivedQuantity < 0.95* inputGoodsRequirementList.get(i).totalDemand ) {
			inputGoodsRequirementList.get(i).rationed = true;
		}else {
			inputGoodsRequirementList.get(i).rationed = false;
		}
		//If the received quantity is larger then the inputs needed as intermediate products directly in the production process (i.e. the rest can be used for investments) 
		if( inputGoodsRequirementList.get(i).receivedQuantity >  inputGoodsRequirementList.get(i).demandDirect) {
			this.intermediateGoodsStock += inputGoodsRequirementList.get(i).demandDirect;
			this.intermediateInputExpenditures += inputGoodsRequirementList.get(i).order.invoiceAmount*  inputGoodsRequirementList.get(i).demandDirect/inputGoodsRequirementList.get(i).receivedQuantity;
			this.capitalInvestmentReal += (inputGoodsRequirementList.get(i).receivedQuantity  - inputGoodsRequirementList.get(i).demandDirect);
			this.capitalInvestment +=  inputGoodsRequirementList.get(i).order.invoiceAmount*(inputGoodsRequirementList.get(i).receivedQuantity  - inputGoodsRequirementList.get(i).demandDirect)/inputGoodsRequirementList.get(i).receivedQuantity;
			this.qualityCapitalInvestments += (inputGoodsRequirementList.get(i).receivedQuantity  - inputGoodsRequirementList.get(i).demandDirect)* inputGoodsRequirementList.get(i).order.quality;
			//Otherwise the input is fully used for production -> no capital investment
		}else {
			this.intermediateGoodsStock += inputGoodsRequirementList.get(i).receivedQuantity;
//			System.out.println(inputGoodsRequirementList.get(i).receivedQuantity);
			this.intermediateInputExpenditures +=  inputGoodsRequirementList.get(i).order.invoiceAmount;
			this.capitalInvestmentReal += 0.0;
		}
		//System.out.println("Rationing	Type 1     Sector id :"+ inputRequirementList.get(i).sectorID    +":	"+inputRequirementList.get(i).rationingQuota );
		inputGoodsRequirementList.get(i).totalDemand -= inputGoodsRequirementList.get(i).receivedQuantity;
	}
}
//Firm received input service 
@ScheduledMethod (start = 1, interval = 1, priority = 50)
public void inputServicesDeliveryRound1() {
	Logger LOGGER = Logger.getLogger(Firm.class.getName());
	for(int i =0; i < inputServiceRequirementList.size();i++) {
		inputServiceRequirementList.get(i).receivedQuantity =inputServiceRequirementList.get(i).order.acceptedQuantity;
		//	this.paymentAccount.accountBalance -=  inputServiceRequirementList.get(i).order.invoiceAmount;
		ServiceOrder thisOrder = ((ServiceOrder) inputServiceRequirementList.get(i).order);
		int supplierID = inputServiceRequirementList.get(i).supplier.firmID;
		int supplierBankID = inputServiceRequirementList.get(i).supplier.bankID;
		this.paymentAccount.pendingOutgoingTransactionsList.add(new PaymentTransaction(ID, supplierID, paymentAccount.bankID, supplierBankID, thisOrder.invoiceAmount,"salesRevenue"));
	    //Check if the order has largely be satisfied	
		if(inputServiceRequirementList.get(i).receivedQuantity < 0.95* inputServiceRequirementList.get(i).totalDemand ) {
			inputServiceRequirementList.get(i).rationed = true;
		}else {
			inputServiceRequirementList.get(i).rationed = false;
		}
		//If the received quantity is larger then the inputs needed as intermediate products directly in the production process (i.e. the rest can be used for investments) 
		if( inputServiceRequirementList.get(i).receivedQuantity >  inputServiceRequirementList.get(i).demandDirect) {
			this.serviceLevel += inputServiceRequirementList.get(i).demandDirect;
			this.serviceExpenditure +=  inputServiceRequirementList.get(i).order.invoiceAmount*  inputServiceRequirementList.get(i).demandDirect/inputServiceRequirementList.get(i).receivedQuantity;
			this.capitalInvestmentReal += (inputServiceRequirementList.get(i).receivedQuantity  - inputServiceRequirementList.get(i).demandDirect);
			this.capitalInvestment +=   inputServiceRequirementList.get(i).order.invoiceAmount*(inputServiceRequirementList.get(i).receivedQuantity  - inputServiceRequirementList.get(i).demandDirect)/inputServiceRequirementList.get(i).receivedQuantity;
			this.qualityCapitalInvestments += (inputServiceRequirementList.get(i).receivedQuantity  - inputServiceRequirementList.get(i).demandDirect)* inputServiceRequirementList.get(i).order.quality;
			//Otherwise the input is fully used for production -> no capital investment
		}else {
			this.serviceLevel += inputServiceRequirementList.get(i).receivedQuantity;
			this.serviceExpenditure += inputServiceRequirementList.get(i).order.invoiceAmount;
			this.capitalInvestmentReal += 0.0;
		}
		//System.out.println("Rationing	Type 1     Sector id :"+ inputRequirementList.get(i).sectorID    +":	"+inputRequirementList.get(i).rationingQuota );
		inputServiceRequirementList.get(i).totalDemand -= inputServiceRequirementList.get(i).receivedQuantity;
	}
}
//Firm received input energy 
@ScheduledMethod (start = 1, interval = 1, priority = 50)
public void inputEnergyDeliveryRound1() {
	Logger LOGGER = Logger.getLogger(Firm.class.getName());
	for(int i =0; i < inputEnergyRequirementList.size();i++) {
		inputEnergyRequirementList.get(i).receivedQuantity = inputEnergyRequirementList.get(i).order.acceptedQuantity;
		//this.paymentAccount.accountBalance -= inputEnergyRequirementList.get(i).order.invoiceAmount;
		EnergyOrder thisOrder = ((EnergyOrder) inputEnergyRequirementList.get(i).order);
		int supplierID = inputEnergyRequirementList.get(i).supplier.firmID;
		int supplierBankID = inputEnergyRequirementList.get(i).supplier.bankID;
		this.paymentAccount.pendingOutgoingTransactionsList.add(new PaymentTransaction(ID, supplierID, paymentAccount.bankID, supplierBankID, thisOrder.invoiceAmount,"salesRevenue"));
	    //Check if the order has largely be satisfied	
		if(inputEnergyRequirementList.get(i).receivedQuantity < 0.95* inputEnergyRequirementList.get(i).totalDemand ) {
			inputEnergyRequirementList.get(i).rationed = true;
		}else {
			inputEnergyRequirementList.get(i).rationed = false;
		}
		//If the received quantity is larger then the inputs needed as intermediate products directly in the production process (i.e. the rest can be used for investments) 
		if( inputEnergyRequirementList.get(i).receivedQuantity >  inputEnergyRequirementList.get(i).demandDirect) {
			this.energyLevel += inputEnergyRequirementList.get(i).demandDirect;
			this.energyExpenditure += inputEnergyRequirementList.get(i).order.invoiceAmount*  inputEnergyRequirementList.get(i).demandDirect/inputEnergyRequirementList.get(i).receivedQuantity;
			this.capitalInvestmentReal += (inputEnergyRequirementList.get(i).receivedQuantity  - inputEnergyRequirementList.get(i).demandDirect);
			this.capitalInvestment +=   inputEnergyRequirementList.get(i).order.invoiceAmount*(inputEnergyRequirementList.get(i).receivedQuantity  - inputEnergyRequirementList.get(i).demandDirect)/inputEnergyRequirementList.get(i).receivedQuantity;
			this.qualityCapitalInvestments += (inputEnergyRequirementList.get(i).receivedQuantity  - inputEnergyRequirementList.get(i).demandDirect)* inputEnergyRequirementList.get(i).order.quality;
			//Otherwise the input is fully used for production -> no capital investment
		}else {
			this.energyLevel += inputEnergyRequirementList.get(i).receivedQuantity;
			this.energyExpenditure +=   inputEnergyRequirementList.get(i).order.invoiceAmount;
			this.capitalInvestmentReal += 0.0;
		}
		//System.out.println("Rationing	Type 1     Sector id :"+ inputRequirementList.get(i).sectorID    +":	"+inputRequirementList.get(i).rationingQuota );
		inputEnergyRequirementList.get(i).totalDemand -= inputEnergyRequirementList.get(i).receivedQuantity;
	}
}
// if the firms has been rationed in the first round; choose new supplier
@ScheduledMethod (start = 1, interval = 1, priority = 49)
public void chooseGoodsSupplier2() {
	Logger LOGGER = Logger.getLogger(Firm.class.getName());
	Context  context = ContextUtils.getContext(this);
	double random = Math.random();
	for(int i=0; i < inputGoodsRequirementList.size();i++) {
		potentialSupplierList.clear();
		if(inputGoodsRequirementList.get(i).rationed) {
			double denLogit = 0;
			//TODO: Implement mall choice
			Iterable<GoodsSector> allSectors = context.getObjects(GoodsSector.class);
			for(GoodsSector obj : allSectors) {
				if(obj.sectorID == inputGoodsRequirementList.get(i).sectorID) {
					GoodsSector aSector= obj;
					for(int j =0; j < aSector.mallList.size()  ;j++) {
						for(int k =0 ; k < aSector.mallList.get(j).firmInventories.size(); k++) {
							if(aSector.mallList.get(j).firmInventories.get(k).inventoryStock>0) {
								potentialSupplierList.add( aSector.mallList.get(j).firmInventories.get(k));
								denLogit += Math.exp(this.intensityChoicePriceGoods*aSector.mallList.get(j).firmInventories.get(k).price + this.intensityChoiceQualityGoods * aSector.mallList.get(j).firmInventories.get(k).quality );
							}
						}
					}
					break;
				}
			}
			double random1 = Math.random();
			double prob = 0;
			for(int j =0; j < potentialSupplierList.size();j++) {
				prob +=  Math.exp(this.intensityChoicePriceGoods* potentialSupplierList.get(j).price+ this.intensityChoiceQualityGoods * potentialSupplierList.get(j).quality)/denLogit;
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
//if the firms has been rationed in the first round; choose new supplier
@ScheduledMethod (start = 1, interval = 1, priority = 49)
public void chooseServiceSupplier2() {
	Logger LOGGER = Logger.getLogger(Firm.class.getName());
	Context  context = ContextUtils.getContext(this);
	double random = Math.random();
	for(int i=0; i < inputServiceRequirementList.size();i++) {
		potentialSupplierList.clear();
		if(inputServiceRequirementList.get(i).rationed) {
			double denLogit = 0;
			//TODO: Implement mall choice
			Iterable<ServiceSector> allSectors = context.getObjects(ServiceSector.class);
			for(ServiceSector obj : allSectors) {
				if(obj.sectorID == inputServiceRequirementList.get(i).sectorID) {
					ServiceSector aSector= obj;
					for(int j =0; j < aSector.mallList.size()  ;j++) {
						for(int k =0 ; k < aSector.mallList.get(j).firmInventories.size(); k++) {
							if(aSector.mallList.get(j).firmInventories.get(k).inventoryStock>0) {
								potentialSupplierList.add( aSector.mallList.get(j).firmInventories.get(k));
								denLogit += Math.exp(this.intensityChoicePriceService*aSector.mallList.get(j).firmInventories.get(k).price  + this.intensityChoiceQualityService * aSector.mallList.get(j).firmInventories.get(k).quality);
							}
						}
					}
					break;
				}
			}
			double random1 = Math.random();
			double prob = 0;
			for(int j =0; j < potentialSupplierList.size();j++) {
				prob +=  Math.exp(this.intensityChoicePriceService* potentialSupplierList.get(j).price+ this.intensityChoiceQualityService * potentialSupplierList.get(j).quality)/denLogit;
				if(random1<= prob) {
					for(int k=0; k < currentSupplierList.size(); k++) {
						if(currentSupplierList.get(k).sectorID==inputServiceRequirementList.get(i).sectorID) {
							currentSupplierList.remove(k);
							break;
						}
					}
					currentSupplierList.add(potentialSupplierList.get(j));
					inputServiceRequirementList.get(i).supplier = potentialSupplierList.get(j);
					break;
				}
			}
		}
	}
}
//if the firms has been rationed in the first round; choose new supplier
@ScheduledMethod (start = 1, interval = 1, priority = 49)
public void chooseEnergySupplier2() {
	Logger LOGGER = Logger.getLogger(Firm.class.getName());
	Context  context = ContextUtils.getContext(this);
	double random = Math.random();
	for(int i=0; i < inputEnergyRequirementList.size();i++) {
		potentialSupplierList.clear();
		if(inputEnergyRequirementList.get(i).rationed) {
			double denLogit = 0;
			//TODO: Implement mall choice
			Iterable<EnergySector> allSectors = context.getObjects(EnergySector.class);
			for(EnergySector obj : allSectors) {
				if(obj.sectorID == inputEnergyRequirementList.get(i).sectorID) {
					EnergySector aSector= obj;
					for(int j =0; j < aSector.mallList.size()  ;j++) {
						for(int k =0 ; k < aSector.mallList.get(j).firmInventories.size(); k++) {
							if(aSector.mallList.get(j).firmInventories.get(k).inventoryStock>0) {
								potentialSupplierList.add( aSector.mallList.get(j).firmInventories.get(k));
								denLogit += Math.exp(this.intensityChoicePriceEnergy*aSector.mallList.get(j).firmInventories.get(k).price+ this.intensityChoiceQualityEnergy * aSector.mallList.get(j).firmInventories.get(k).quality );
							}
						}
					}
					break;
				}
			}
			double random1 = Math.random();
			double prob = 0;
			for(int j =0; j < potentialSupplierList.size();j++) {
				prob +=  Math.exp(this.intensityChoicePriceEnergy* potentialSupplierList.get(j).price+ this.intensityChoiceQualityEnergy * potentialSupplierList.get(j).quality)/denLogit;
				if(random1<= prob) {
					for(int k=0; k < currentSupplierList.size(); k++) {
						if(currentSupplierList.get(k).sectorID==inputEnergyRequirementList.get(i).sectorID) {
							currentSupplierList.remove(k);
							break;
						}
					}
					currentSupplierList.add(potentialSupplierList.get(j));
					inputEnergyRequirementList.get(i).supplier = potentialSupplierList.get(j);
					break;
				}
			}
		}
	}
}
//Order new inputs in case of rationing 
/*TODO The financing of the second round has to be checked as well*/
@ScheduledMethod (start = 1, interval = 1, priority = 48)
public void inputGoodsOrderRound2() {
	Logger LOGGER = Logger.getLogger(Firm.class.getName());
	Context  context = ContextUtils.getContext(this);
	for(int i =0; i < inputGoodsRequirementList.size();i++) {
		if(inputGoodsRequirementList.get(i).rationed) {
			Iterable<GoodsSector> allSectors = context.getObjects(GoodsSector.class);
			for(GoodsSector obj : allSectors) {
				if(obj.sectorID == inputGoodsRequirementList.get(i).sectorID) {
					GoodsSector aSector= obj;
					for(int j=0; j < aSector.mallList.get(0).firmInventories.size();j++) {
						if(inputGoodsRequirementList.get(i).supplier.firmID==aSector.mallList.get(0).firmInventories.get(j).firmID) {
							inputGoodsRequirementList.get(i).order = new GoodsOrder(this.ID, inputGoodsRequirementList.get(i).supplier.firmID,inputGoodsRequirementList.get(i).totalDemand, true);
							aSector.mallList.get(0).firmInventories.get(j).orderBook.add((GoodsOrder) inputGoodsRequirementList.get(i).order);
							break;
						}
					}
					break;
				}
			}
		}
	}
}
//Order new inputs in case of rationing 
/*TODO The financing of the second round has to be checked as well*/
@ScheduledMethod (start = 1, interval = 1, priority = 48)
public void inputServiceOrderRound2() {
	Logger LOGGER = Logger.getLogger(Firm.class.getName());
	Context  context = ContextUtils.getContext(this);
	for(int i =0; i < inputServiceRequirementList.size();i++) {
		if(inputServiceRequirementList.get(i).rationed) {
			Iterable<ServiceSector> allSectors = context.getObjects(ServiceSector.class);
			for(ServiceSector obj : allSectors) {
				if(obj.sectorID == inputServiceRequirementList.get(i).sectorID) {
					ServiceSector aSector= obj;
					for(int j=0; j < aSector.mallList.get(0).firmInventories.size();j++) {
						if(inputServiceRequirementList.get(i).supplier.firmID==aSector.mallList.get(0).firmInventories.get(j).firmID) {
							inputServiceRequirementList.get(i).order = new ServiceOrder(this.ID, inputServiceRequirementList.get(i).supplier.firmID,inputServiceRequirementList.get(i).totalDemand, true);
							aSector.mallList.get(0).firmInventories.get(j).orderBook.add((ServiceOrder) inputServiceRequirementList.get(i).order);
							break;
						}
					}
					break;
				}
			}
		}
	}
}
//Order new inputs in case of rationing 
/*TODO The financing of the second round has to be checked as well*/
@ScheduledMethod (start = 1, interval = 1, priority = 48)
public void inputEnergyOrderRound2() {
	Logger LOGGER = Logger.getLogger(Firm.class.getName());
	Context  context = ContextUtils.getContext(this);
	for(int i =0; i < inputEnergyRequirementList.size();i++) {
		if(inputEnergyRequirementList.get(i).rationed) {
			Iterable<EnergySector> allSectors = context.getObjects(EnergySector.class);
			for(EnergySector obj : allSectors) {
				if(obj.sectorID == inputEnergyRequirementList.get(i).sectorID) {
					EnergySector aSector= obj;
					for(int j=0; j < aSector.mallList.get(0).firmInventories.size();j++) {
						if(inputEnergyRequirementList.get(i).supplier.firmID==aSector.mallList.get(0).firmInventories.get(j).firmID) {
							inputEnergyRequirementList.get(i).order = new EnergyOrder(this.ID, inputEnergyRequirementList.get(i).supplier.firmID,inputEnergyRequirementList.get(i).totalDemand, true);
							aSector.mallList.get(0).firmInventories.get(j).orderBook.add((EnergyOrder) inputEnergyRequirementList.get(i).order);
							break;
						}
					}
					break;
				}
			}
		}
	}
}
//Firm received second round of input deliveries
@ScheduledMethod (start = 1, interval = 1, priority = 46)
public void inputGoodsDeliveryRound2() {
	Context  context = ContextUtils.getContext(this);
	for(int i =0; i < inputGoodsRequirementList.size();i++) {
		if(inputGoodsRequirementList.get(i).rationed) {
			int type = inputGoodsRequirementList.get(i).type;
			inputGoodsRequirementList.get(i).receivedQuantity =((GoodsOrder) inputGoodsRequirementList.get(i).order).acceptedQuantity;
			GoodsOrder thisOrder = ((GoodsOrder) inputGoodsRequirementList.get(i).order);
			int supplierID = inputGoodsRequirementList.get(i).supplier.firmID;
			int supplierBankID = inputGoodsRequirementList.get(i).supplier.bankID;
			this.paymentAccount.pendingOutgoingTransactionsList.add(new PaymentTransaction(ID, supplierID, paymentAccount.bankID, supplierBankID, thisOrder.invoiceAmount,"salesRevenue"));
			if( inputGoodsRequirementList.get(i).receivedQuantity >  inputGoodsRequirementList.get(i).demandDirect) {
				this.intermediateGoodsStock += inputGoodsRequirementList.get(i).demandDirect;
				this.intermediateInputExpenditures += ((GoodsOrder) inputGoodsRequirementList.get(i).order).invoiceAmount*  inputGoodsRequirementList.get(i).demandDirect/inputGoodsRequirementList.get(i).receivedQuantity;
				this.capitalInvestmentReal += (inputGoodsRequirementList.get(i).receivedQuantity  - inputGoodsRequirementList.get(i).demandDirect);
				this.capitalInvestment +=  ((GoodsOrder) inputGoodsRequirementList.get(i).order).invoiceAmount*(inputGoodsRequirementList.get(i).receivedQuantity  - inputGoodsRequirementList.get(i).demandDirect)/inputGoodsRequirementList.get(i).receivedQuantity;
				this.qualityCapitalInvestments += (inputGoodsRequirementList.get(i).receivedQuantity  - inputGoodsRequirementList.get(i).demandDirect)* inputGoodsRequirementList.get(i).order.quality;
			}else {
				this.intermediateGoodsStock += inputGoodsRequirementList.get(i).receivedQuantity;
				this.intermediateInputExpenditures +=  ((GoodsOrder) inputGoodsRequirementList.get(i).order).invoiceAmount;
				this.capitalInvestmentReal += 0.0;
			}
			//		System.out.println("Rationing	Type 1  " );
		}
	}
//Determine value and calculatory price of intermediate goods 
	this.intermediateInputValue += intermediateInputExpenditures;
	if(intermediateGoodsStock>0)
		this.calcIntermediateGoodsPrice = intermediateInputValue/intermediateGoodsStock;
}
//Firm receiveds second round of input deliveries
@ScheduledMethod (start = 1, interval = 1, priority = 46)
public void inputServiceDeliveryRound2() {
	Logger LOGGER = Logger.getLogger(Firm.class.getName());
	Context  context = ContextUtils.getContext(this);
	for(int i =0; i < inputServiceRequirementList.size();i++) {
		if(inputServiceRequirementList.get(i).rationed) {
			int type = inputServiceRequirementList.get(i).type;
			inputServiceRequirementList.get(i).receivedQuantity =((ServiceOrder) inputServiceRequirementList.get(i).order).acceptedQuantity;
			//this.paymentAccount.accountBalance -= ((ServiceOrder) inputServiceRequirementList.get(i).order).invoiceAmount;
			ServiceOrder thisOrder = ((ServiceOrder) inputServiceRequirementList.get(i).order);
			int supplierID = inputGoodsRequirementList.get(i).supplier.firmID;
			int supplierBankID = inputGoodsRequirementList.get(i).supplier.bankID;
			this.paymentAccount.pendingOutgoingTransactionsList.add(new PaymentTransaction(ID, supplierID, paymentAccount.bankID, supplierBankID, thisOrder.invoiceAmount,"salesRevenue"));
			if( inputServiceRequirementList.get(i).receivedQuantity >  inputServiceRequirementList.get(i).demandDirect) {
				this.serviceLevel += inputServiceRequirementList.get(i).demandDirect;
				this.serviceExpenditure += ((ServiceOrder) inputServiceRequirementList.get(i).order).invoiceAmount*  inputServiceRequirementList.get(i).demandDirect/inputServiceRequirementList.get(i).receivedQuantity;
				this.capitalInvestmentReal += (inputServiceRequirementList.get(i).receivedQuantity  - inputServiceRequirementList.get(i).demandDirect);
				this.capitalInvestment +=  ((ServiceOrder) inputServiceRequirementList.get(i).order).invoiceAmount*(inputServiceRequirementList.get(i).receivedQuantity  - inputServiceRequirementList.get(i).demandDirect)/inputServiceRequirementList.get(i).receivedQuantity;
				this.qualityCapitalInvestments += (inputServiceRequirementList.get(i).receivedQuantity  - inputServiceRequirementList.get(i).demandDirect)* inputServiceRequirementList.get(i).order.quality;
			}else {
				this.serviceLevel += inputServiceRequirementList.get(i).receivedQuantity;
				this.serviceExpenditure +=  ((ServiceOrder) inputServiceRequirementList.get(i).order).invoiceAmount;
				this.capitalInvestmentReal += 0.0;
			}
			//		System.out.println("Rationing	");//Type 1     Sector id :"+ inputServiceRequirementList.get(i).sectorID    +":	"+inputServiceRequirementList.get(i).rationingQuota );
		}
	}
// Compute value of service
	this.serviceInputValue += serviceExpenditure;
	this.serviceLevelValue += this.serviceInputValue;
	if(serviceLevel>0)
		this.calcServicePrice = serviceLevelValue/serviceLevel;
}
//Firm receiveds second round of input deliveries
@ScheduledMethod (start = 1, interval = 1, priority = 46)
public void inputEnergyDeliveryRound2() {
	Logger LOGGER = Logger.getLogger(Firm.class.getName());
	Context  context = ContextUtils.getContext(this);
	for(int i =0; i < inputEnergyRequirementList.size();i++) {
		if(inputEnergyRequirementList.get(i).rationed) {
			int type = inputEnergyRequirementList.get(i).type;
			inputEnergyRequirementList.get(i).receivedQuantity =inputEnergyRequirementList.get(i).order.acceptedQuantity;
			//this.paymentAccount.accountBalance -=  inputEnergyRequirementList.get(i).order.invoiceAmount;
			EnergyOrder thisOrder = ((EnergyOrder) inputEnergyRequirementList.get(i).order);
			int supplierID = inputEnergyRequirementList.get(i).supplier.firmID;
			int supplierBankID = inputEnergyRequirementList.get(i).supplier.bankID;
			this.paymentAccount.pendingOutgoingTransactionsList.add(new PaymentTransaction(ID, supplierID, paymentAccount.bankID, supplierBankID, thisOrder.invoiceAmount,"salesRevenue"));
			if( inputEnergyRequirementList.get(i).receivedQuantity >  inputEnergyRequirementList.get(i).demandDirect) {
				this.energyLevel += inputEnergyRequirementList.get(i).demandDirect;
				this.energyExpenditure +=  inputEnergyRequirementList.get(i).order.invoiceAmount*  inputEnergyRequirementList.get(i).demandDirect/inputEnergyRequirementList.get(i).receivedQuantity;
				this.capitalInvestmentReal += (inputEnergyRequirementList.get(i).receivedQuantity  - inputEnergyRequirementList.get(i).demandDirect);
				this.capitalInvestment +=   inputEnergyRequirementList.get(i).order.invoiceAmount*(inputEnergyRequirementList.get(i).receivedQuantity  - inputEnergyRequirementList.get(i).demandDirect)/inputEnergyRequirementList.get(i).receivedQuantity;
				this.qualityCapitalInvestments += (inputEnergyRequirementList.get(i).receivedQuantity  - inputEnergyRequirementList.get(i).demandDirect)* inputEnergyRequirementList.get(i).order.quality;
			}else {
				this.energyLevel += inputEnergyRequirementList.get(i).receivedQuantity;
				this.energyExpenditure +=   inputEnergyRequirementList.get(i).order.invoiceAmount;
				this.capitalInvestmentReal += 0.0;
			}
			//		System.out.println("Rationing");//	Type 1     Sector id :"+ inputEnergyRequirementList.get(i).sectorID    +":	"+inputEnergyRequirementList.get(i).rationingQuota );
		}
	}
//Compute value of energy and capital stock
	if(this.capitalInvestmentReal>0)
		this.calcCapitalPrice = this.capitalInvestment/ this.capitalInvestmentReal;
	this.energyInputValue += this.energyExpenditure;
	this.energyLevelValue +=this.energyInputValue;
	if(energyLevel>0)
		this.calcEnergyPrice = this.energyLevelValue/energyLevel;
}
//Production of output
@ScheduledMethod (start = 1, interval = 1, priority = 42)
public void production() {
	Logger LOGGER = Logger.getLogger(Firm.class.getName());
}
//PAy wages to all workers
@ScheduledMethod (start = 1, interval = 1, priority = 41)
public void payWages() {
	laborCosts = 0;
	for(int i =0; i<workContractList.size();i++) {
		WorkContract aContract = workContractList.get(i);
		paymentAccount.pendingOutgoingTransactionsList.add(new PaymentTransaction(ID,aContract.workerID,paymentAccount.bankID, aContract.worker.paymentAccount.bankID,aContract.wage ,"wagePayment" ));
		laborCosts += aContract.wage;
	}
}
//Adjust capital stock
@ScheduledMethod (start = 1, interval = 1, priority = 40)
public void capitalStockAdjustment() {
	Logger LOGGER = Logger.getLogger(Firm.class.getName());
}
//determines wage offer using greedy algorithm
@ScheduledMethod (start = 1, interval = 1, priority = 35)
public void outputDelivery() {
}
//Carry out process innovations
@ScheduledMethod (start = 1, interval = 1, priority = 30)
public void processInnovation() {
	double random = Math.random();
	double rdProbabilityProcess = 0;
	if(ownSector.avgKnowledgeStock>0)
		rdProbabilityProcess = 1- Math.exp( - (1-shareProductInnovation)*Math.min(5.0,rdKnowledgeStock/ownSector.avgKnowledgeStock)* processInnoEfficiency);
	if(random < rdProbabilityProcess) {
		Random r = new Random();
		double randomInnovationDraw = r.nextGaussian()*stdDevProcessInno;;
		if(randomInnovationDraw>0)
			cumulatedProcessInnovations = cumulatedProcessInnovations + randomInnovationDraw;
	}
}
//Product innovations
@ScheduledMethod (start = 1, interval = 1, priority = 30)
public void productInnovation() {
	double random = Math.random();
	double rdProbabilityProduct = 0;
	if(ownSector.avgKnowledgeStock>0)
		rdProbabilityProduct = 1- Math.exp( - (shareProductInnovation)*Math.min(5.0,rdKnowledgeStock/ownSector.avgKnowledgeStock)* productInnoEfficiency);
	if(random < rdProbabilityProduct) {
		Random r = new Random();
		double randomInnovationDraw = r.nextGaussian()*stdDevProductInno;;
		if(randomInnovationDraw>0) {
			quality = quality + randomInnovationDraw;
			productivityEffectProductInnovation = Math.random()*randomInnovationDraw;
		}
	}else {
		productivityEffectProductInnovation = (1- productivityEffectProductInnovationAdjustment)*productivityEffectProductInnovation;
	}
}
//Adjustment of specific skills of workers
@ScheduledMethod (start = 1, interval = 1, priority = 29)
public void specificSkillUpdate() {
	Logger LOGGER = Logger.getLogger(Firm.class.getName());
	laborProductivity = laborProductivity + specificSkillAdjustment*Math.max(technology - laborProductivity,0);
}
//Adjust the different productivities
@ScheduledMethod (start = 1, interval = 1, priority = 28)
public void productivityAdjustment() {
	technology = capitalProductivity* (1 + cumulatedProcessInnovations);
	totalFactorProductivity = Math.min(technology, laborProductivity*(1 - productivityEffectProductInnovation));
	rdKnowledgeStock  = (1-knowledgeDepreciation)*rdKnowledgeStock;
}
//Accounting
@ScheduledMethod (start = 1, interval = 1, priority = 25)
public void accounting() {
}
/*Aux functions*/
void computeInputDemandForProductionPlan() {
}
void updateBalanceSheet(){
	balanceSheet.assets.capitalStockValue = capitalStockValue;
	balanceSheet.assets.cash = paymentAccount.accountBalance;
	balanceSheet.assets.financialCapitalValue = 0;
	balanceSheet.assets.inventoryFinalProductValue = valueInventory;
	balanceSheet.assets.inventoryIntermediateProductsValue = intermediateInputValue + this.serviceLevelValue + this.energyLevelValue;
	balanceSheet.computeEquity();
}
ServiceInventory chooseServiceProvider(ArrayList<ServiceInventory> firmInventories) {
	return null;
}
EnergyInventory chooseEnergyProvider(ArrayList<EnergyInventory> firmInventories) {
	return null;
}
/*Classes */
class InputItem{
	int sectorID;
	int type;
	boolean rationed;
	double demandDirect;
	double demandCapital;
	double totalDemand;
	double receivedQuantity;
	Inventory supplier;
	Order order;
	InputItem(int sectorID, int type){
		this.sectorID =  sectorID;
		this.type = type;
		this.rationed = false;
		this.demandDirect =  0;
		this.demandCapital =  0;
		this.totalDemand = 0;
		this.receivedQuantity = 0;
	}
}
}
