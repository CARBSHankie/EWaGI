package eWAGI_Firms;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import org.apache.commons.math3.util.Precision;
import repast.simphony.context.Context;
import repast.simphony.context.space.continuous.ContinuousSpaceFactory;
import repast.simphony.context.space.continuous.ContinuousSpaceFactoryFinder;
import repast.simphony.context.space.grid.GridFactory;
import repast.simphony.context.space.grid.GridFactoryFinder;
import repast.simphony.dataLoader.ContextBuilder;
import repast.simphony.engine.environment.RunEnvironment;
import repast.simphony.parameter.Parameters;
import repast.simphony.space.continuous.ContinuousSpace;
import repast.simphony.space.continuous.NdPoint;
import repast.simphony.space.continuous.RandomCartesianAdder;
import repast.simphony.space.grid.Grid;
import repast.simphony.space.grid.GridBuilderParameters;
import repast.simphony.space.grid.SimpleGridAdder;
import repast.simphony.space.grid.WrapAroundBorders;
import repast.simphony.util.ContextUtils;
public class Model implements ContextBuilder<Object>{
	@Override
	public Context build(Context<Object> context) {
		context.setId("EWAGI_Firms");
		int numPositions  = 104; // number of positions on Salop circle  - set it equal to number of workers
		                         // further must define number of positions such that it gives an even number when divided by 2 and divided by 4, e.g. 104
		ContinuousSpaceFactory spaceFactory = ContinuousSpaceFactoryFinder.createContinuousSpaceFactory(null);
		//defines space for numPositionsX1 and torus (with WrapAroundBorders)
		ContinuousSpace<Object> space = spaceFactory.createContinuousSpace(
				"space", context, new RandomCartesianAdder<Object>(),
		        new repast.simphony.space.continuous.WrapAroundBorders(), numPositions,1);
		//discretize the space
		GridFactory gridFactory = GridFactoryFinder.createGridFactory(null);
		Grid<Object> grid = gridFactory.createGrid("grid", context,
				new GridBuilderParameters<Object>(new WrapAroundBorders(),
						new SimpleGridAdder<Object>(), true, numPositions, 1));
		// Agent demography
		int numSectors = GlobalParameters.numSectors;
		int numGoodsSectors = GlobalParameters.numSectors;
		int serviceSectors = GlobalParameters.serviceSectors;
		int numEnergySectors = GlobalParameters.numEnergySectors;
		int numHouseholds = GlobalParameters.numHouseholds;
		int numFirmsPerSector = GlobalParameters.numFirmsPerSector;
		int numBanks = GlobalParameters.numBanks;
		double employmentShare = GlobalParameters.employmentShare;
		double totalSizeLaborForce = employmentShare*numHouseholds;
		int ID = 0;
		ArrayList<Sector> tempSectorList = new ArrayList<Sector> ();
		ArrayList<Bank> tempBankList = new ArrayList<Bank> ();
		
		ArrayList<ConsumptionCoefficient> consumptionCoefficientList = new ArrayList<ConsumptionCoefficient>();
		double totalLaborIncome = 0;
		double totalLaborForceData = 0;
			
		//Start agent initialization
		// Central bank
		CentralBank aCentralBank = new CentralBank(ID);
		int centralBankID=ID;
		context.add(aCentralBank);
		ID++;
		//Foreign Bank; for export and import with RoW (rest of world)
		ForeignBank aForeignBank = new ForeignBank(ID);	
		context.add(aForeignBank);
		ID++;
		//Local Banks
		for(int i=0; i < numBanks; i++) {
			Bank aBank = new Bank(ID, centralBankID,aForeignBank.ID);
			context.add(aBank);
			tempBankList.add(aBank);
			//Add Bank account and standing facility to CB
			aCentralBank.bankReserveList.add(aBank.centralBankReserves);
			aCentralBank.bankStandingFacilitiesList.add(aBank.standingFacility);
			ID++;
		}
		// For the next agents, banks are assigned randomly
		int bankIndex =  (int)(Math.random() * ((numBanks-1) + 1));
		int bankID =  tempBankList.get(bankIndex).ID;
		//Clearing House; for distributing diviends, and finacial market operations (not implemented yet)
		ClearingHouse aClearingHouse = new ClearingHouse(ID, bankID);
		context.add(aClearingHouse);
		//Add Payment account to bank
		tempBankList.get(bankIndex).paymentAccountList.add(aClearingHouse.paymentAccount);
		tempBankList.get(bankIndex).centralBankReserves.accountBalance += aClearingHouse.paymentAccount.accountBalance;
		ID++;
		// Create sectors; attention: this is not generic at the moment. The initialization is linked to the 12 sector input output table, in which sector 1-7 and sector 9 are industrial sectors
		// sector 8 is the energy sector, and sector 10 to 12 are service sectors
		for (int i = 1; i < numSectors+1; i++) {
			double initPaymentAccount = GlobalParameters.initAccountBalance[i-1];
			
			//Insutrial sectors, hereafter goods sector
			if(i < 8 || i== 9) {
				GoodsSector aSector = new GoodsSector(space, grid, i,totalSizeLaborForce , numFirmsPerSector);
				totalLaborIncome += aSector.totalLaborCosts;
				totalLaborForceData+= aSector.employmentData;
				// Add (one) mall to the sectors
				aSector.mallList.add(new GoodsMall(space, grid, i, i));
				context.add(aSector);
				tempSectorList.add(aSector);
				//Add create firms and add them to the sector
				for (int j = 0; j < numFirmsPerSector; j++) {
					int index =  (int)(Math.random() * ((numBanks-1) + 1));
					int tempBankID = tempBankList.get(index).ID;
					GoodsFirm aFirm = new GoodsFirm(space, grid,ID,  aSector,tempBankID, aClearingHouse.ID, initPaymentAccount);
					tempBankList.get(index).paymentAccountList.add(aFirm.paymentAccount);
					tempBankList.get(index).centralBankReserves.accountBalance += aFirm.paymentAccount.accountBalance;
					//Add instance f inventory; which is put in the memory of the firm and the mall.
					GoodsInventory aInventory = new GoodsInventory(aFirm.ID, aSector.sectorID, tempBankList.get(index).ID, aFirm.price);
					/*TODO Chnage code here if more malls per sector are allowed*/
					aInventory.inventoryStock = aFirm.initialOutput;
					aSector.mallList.get(0).firmInventories.add(aInventory);
					aFirm.mallInventoryList.add(aInventory);
					aSector.initialOutput += aFirm.initialOutput;
					context.add(aFirm);
					ID++;
				}
			// Energy sector
			}else if(i == 8) {
				EnergySector aSector = new EnergySector(space, grid, i,totalSizeLaborForce , numFirmsPerSector);
				totalLaborIncome += aSector.totalLaborCosts;
				totalLaborForceData+= aSector.employmentData;
				aSector.mallList.add(new EnergyMall(space, grid, i, i));
				context.add(aSector);
				tempSectorList.add(aSector);
				//Create energy firms and add to sector
				for (int j = 0; j < numFirmsPerSector; j++) {
					int index =  (int)(Math.random() * ((numBanks-1) + 1));
					int tempBankID =  tempBankList.get(index).ID;
					EnergyFirm aFirm = new EnergyFirm(space, grid,ID,  aSector,tempBankID,aClearingHouse.ID, initPaymentAccount);
					tempBankList.get(index).paymentAccountList.add(aFirm.paymentAccount);
					tempBankList.get(index).centralBankReserves.accountBalance += aFirm.paymentAccount.accountBalance;
					EnergyInventory aInventory = new EnergyInventory(aFirm.ID, aSector.sectorID, tempBankList.get(index).ID, aFirm.price);
					aFirm.mallInventoryList.add(aInventory);
					aInventory.plannedDelivery = aFirm.initialOutput;  
					aInventory.inventoryStock = aFirm.initialOutput;
					/*TODO: change if more malls are allowed*/
					aSector.mallList.get(0).firmInventories.add(aInventory);
					aSector.firmList.add(aFirm);
					aSector.initialOutput += aFirm.initialOutput;
					context.add(aFirm);
					ID++;
				}
			// Service sector
			}else {			
				ServiceSector aSector = new ServiceSector(space, grid, i,totalSizeLaborForce , numFirmsPerSector);
				totalLaborIncome += aSector.totalLaborCosts;
				totalLaborForceData+= aSector.employmentData;
				aSector.mallList.add(new ServiceMall(space, grid, i, i));
				context.add(aSector);
				tempSectorList.add(aSector);
				for (int j = 0; j < numFirmsPerSector; j++) {
					int index =  (int)(Math.random() * ((numBanks-1) + 1));
					int tempBankID = tempBankList.get(index).ID;
					ServiceFirm aFirm = new ServiceFirm(space, grid,ID,  aSector,tempBankID,aClearingHouse.ID, initPaymentAccount);
					tempBankList.get(index).paymentAccountList.add(aFirm.paymentAccount);
					tempBankList.get(index).centralBankReserves.accountBalance += aFirm.paymentAccount.accountBalance;
					ServiceInventory aInventory = new ServiceInventory(aFirm.ID, aSector.sectorID, tempBankList.get(index).ID, aFirm.price);
					aFirm.mallInventoryList.add(aInventory);
					aInventory.plannedDelivery = aFirm.initialOutput; // ToDo: Check the correct variable
					aInventory.inventoryStock = aFirm.initialOutput;
					/*TODO: change if more malls are allower*/
					aSector.mallList.get(0).firmInventories.add(aInventory);
					aSector.initialOutput += aFirm.initialOutput;
					aSector.firmList.add(aFirm);
					context.add(aFirm);
					ID++;
				}
			}		
			//Read in consumption coefficients of households, i.e. how much of the consumption budget is spent on this product category
			String line = "";
			try {
				line = Files.readAllLines(Paths.get("consumption.csv")).get(i);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			List<String> items2 = Arrays.asList(line.split("\\s*,\\s*"));			
			//Goods			
			if(i < 8 || i== 9) {				
				consumptionCoefficientList.add(new ConsumptionCoefficientGoods(i,Double.parseDouble(items2.get(1) )));				
			//Energy	
			}else if(i==8) {				
				consumptionCoefficientList.add(new ConsumptionCoefficientEnergy(i,Double.parseDouble(items2.get(1) )));				
			//Service	
			}else {				
				consumptionCoefficientList.add(new ConsumptionCoefficientService(i,Double.parseDouble(items2.get(1) )));				
			}
		} 
		//Create households		
		double cumLabForce = 0;
		int j = 0;
		for(int i=0; i < numHouseholds; i++) {
			//Assign workers to sectoral labor force
			if(i - cumLabForce > tempSectorList.get(j).laborForce) {				
				cumLabForce += tempSectorList.get(j).laborForce;
				j++;
			}			
			// Assign bank
			int index =  (int)(Math.random() * ((numBanks-1) + 1));
			int tempBankID =  tempBankList.get(index).ID;
			Household aHousehold = new Household(ID,tempBankID,aClearingHouse.ID);			
			tempBankList.get(index).paymentAccountList.add(aHousehold.paymentAccount);
			tempBankList.get(index).centralBankReserves.accountBalance += aHousehold.paymentAccount.accountBalance;			
			aHousehold.consumptionCoefficientList = consumptionCoefficientList;
			aHousehold.disposableIncome = totalLaborIncome/totalLaborForceData;
			/*TODO: this line should be made generic*/
			aHousehold.propensityToConsume =     1519948.0/totalLaborIncome;			
			aHousehold.currentWorkingSector = tempSectorList.get(j);
			context.add(aHousehold);
			ID++;	
		}
		// Create rest of world
		ROW restOfWorld = new ROW(ID,aForeignBank.ID);
		restOfWorld.nominalExportVolumeTarget =  totalLaborIncome/totalLaborForceData*numHouseholds * (1596846/1853274.0) ;
		aForeignBank.paymentAccountList.add(restOfWorld.paymentAccount);
		context.add(restOfWorld);	
		ID++;	
		double totalOutputID=0;	
		
		//Create statistic agency
		StatisticAgency aStatisticAgency = new StatisticAgency(space, grid,ID,totalOutputID, numHouseholds);
		context.add(aStatisticAgency);
		for ( Object obj : context ) {
			 NdPoint pt = space . getLocation ( obj );
			 grid . moveTo ( obj , ( int ) pt . getX () , ( int ) pt . getY ());
		 }
		//System.out.println("Test");
		//necessary to end batch runs, does XXX iterations (or XXX ticks)
		//to generate multiple runs for a particular parameter constellation
		//define as many random sees as you want runs
	//	if (RunEnvironment.getInstance().isBatch()) {
	//		RunEnvironment.getInstance().endAt(numIterationsBatchRuns);
	//	}	
		return context;
	}
}