package eWAGI_Firms;
import repast.simphony.context.Context;
import repast.simphony.engine.environment.RunEnvironment;
import repast.simphony.engine.schedule.ScheduledMethod;
import repast.simphony.space.continuous.ContinuousSpace;
import repast.simphony.space.grid.Grid;
import repast.simphony.util.ContextUtils;
import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintStream;

public class StatisticAgency {
	int id;
	int numHouseholds;
	private ContinuousSpace<Object> space;
	private Grid<Object> grid;
	double totalOutputID,totalInventoryID, unemploymentRateID,avgWageID, outputSector1ID,totalPlannedOutputID,getTotalImportsID, salesToHouseholdsID, salesToFirmsID,  outputGoodsSectorID, outputServiceSectorID, outputEnergySectorID;  
	double totalOutput,totalPlannedOutput;
	double avgWage;
	double priceIndexDomesticFirms, priceIndexDomesticFirmsID;
	double unemploymentRate;
	double outputGoodsSector, outputServiceSector, outputEnergySector;
	double totalImports, totalExports; 
	double totalEmployment;
	double totalInventory;
	double outputSector1;
	double outputSector8;
	double plannedOutputSector8;
	double salesToHouseholds;
	double salesToFirms;
	double serviceInventoryVolum;
	double goodsInventoryVolum;
	double energyInventoryVolum;
//	double revenue;
	
	StatisticAgency(ContinuousSpace<Object> space, Grid<Object> grid, int id, double totalOutputID, int numHouseholds){
		this.space = space;
		this.grid = grid;
		this.numHouseholds = numHouseholds;
		this.totalOutputID = totalOutputID;
		this.outputSector1ID = 0;
		this.totalPlannedOutputID=0;
		this.getTotalImportsID = 0;
		this.outputSector8=0;
		this.plannedOutputSector8=0;
		this.id=id;
		this.totalExports = 0;
		this.salesToHouseholds=0;
		this.outputGoodsSector = 0;
		this.outputServiceSector = 0;
		this.outputEnergySector = 0;
		this.salesToFirms=0;
		totalInventory = 0;
		totalOutput = 0;
		this.avgWageID = 0;
		this.totalEmployment = 0;
		this.avgWage = 0;
		this.priceIndexDomesticFirms=0;
		this.priceIndexDomesticFirmsID=0;
		this.serviceInventoryVolum = 0;
		this.goodsInventoryVolum = 0;
		this.energyInventoryVolum = 0;
		
	}

	
	@ScheduledMethod (start = 1, interval = 1, priority = 10)
	public void computeFirmStatistics() {
		
		totalOutput = 0;
		totalPlannedOutput = 0;
		totalImports = 0;
		totalExports=0;
		outputSector1 = 0;
		totalInventory = 0;
		this.outputSector8=0;
		this.plannedOutputSector8=0;
		this.salesToHouseholds=0;
		this.salesToFirms=0;
		this.outputGoodsSector = 0;
		this.outputServiceSector = 0;
		this.outputEnergySector = 0;
		this.totalEmployment = 0;
		this.avgWage = 0;
		this.priceIndexDomesticFirms = 0;
		this.serviceInventoryVolum = 0;
		this.goodsInventoryVolum = 0;
		this.energyInventoryVolum = 0;
		Context  context = ContextUtils.getContext(this);
				Iterable<ServiceFirm> allFirms = context.getObjects(ServiceFirm.class);
				for(ServiceFirm obj : allFirms) {
					totalOutput += obj.output;
					outputServiceSector +=  obj.output;
					this.totalEmployment += obj.laborStock;
					totalPlannedOutput += obj.plannedOutput;
					avgWage += obj.laborStock * obj.wage;
					priceIndexDomesticFirms += obj.price* obj.output;
					totalInventory += obj.mallInventoryList.get(0).inventoryStock;
					totalInventory += obj.internalInventoy.inventoryStock;
					serviceInventoryVolum += obj.mallInventoryList.get(0).inventoryStock;
					serviceInventoryVolum += obj.internalInventoy.inventoryStock;					
					this.salesToHouseholds+= obj.mallInventoryList.get(0).salesToHouseholds;
					this.salesToFirms+= obj.mallInventoryList.get(0).salesToFirms;
					if(obj.ownSector.sectorID==1) {
						outputSector1 += obj.output;
					}else if(obj.ownSector.sectorID==8) {
						this.outputSector8+= obj.output;
						this.plannedOutputSector8+= obj.plannedOutput;
					}
				}
				Iterable<GoodsFirm> allGoodsFirms = context.getObjects(GoodsFirm.class);
				for(GoodsFirm obj : allGoodsFirms) {
					totalOutput += obj.output;
					outputGoodsSector +=  obj.output;
					totalPlannedOutput += obj.plannedOutput;
					totalInventory += obj.mallInventoryList.get(0).inventoryStock;
					goodsInventoryVolum += obj.mallInventoryList.get(0).inventoryStock;
					goodsInventoryVolum += obj.internalInventoy.inventoryStock;
					this.salesToHouseholds+= obj.mallInventoryList.get(0).salesToHouseholds;
					this.salesToFirms+= obj.mallInventoryList.get(0).salesToFirms;
					this.totalEmployment += obj.laborStock;
					avgWage += obj.laborStock * obj.wage;
					priceIndexDomesticFirms += obj.price* obj.output;
					if(obj.ownSector.sectorID==1) {
						outputSector1 += obj.output;
					}else if(obj.ownSector.sectorID==8) {
						this.outputSector8+= obj.output;
						this.plannedOutputSector8+= obj.plannedOutput;
					}
				}
				Iterable<EnergyFirm> allEnergyFirms = context.getObjects(EnergyFirm.class);
				for(EnergyFirm obj : allEnergyFirms) {
					totalOutput += obj.output;
					outputEnergySector +=  obj.output;
					totalPlannedOutput += obj.plannedOutput;
					totalInventory += obj.mallInventoryList.get(0).inventoryStock;
					energyInventoryVolum += obj.mallInventoryList.get(0).inventoryStock;
					energyInventoryVolum += obj.internalInventoy.inventoryStock;				
					this.salesToHouseholds+= obj.mallInventoryList.get(0).salesToHouseholds;
					this.salesToFirms+= obj.mallInventoryList.get(0).salesToFirms;
					this.totalEmployment += obj.laborStock;
					avgWage += obj.laborStock * obj.wage;
					priceIndexDomesticFirms += obj.price* obj.output;
					if(obj.ownSector.sectorID==1) {
						outputSector1 += obj.output;
					}else if(obj.ownSector.sectorID==8) {
						this.outputSector8+= obj.output;
						this.plannedOutputSector8+= obj.plannedOutput;
					}
				}
				if(totalEmployment>0)
					avgWage = avgWage / totalEmployment;
				priceIndexDomesticFirms = priceIndexDomesticFirms / totalOutput;
				unemploymentRate = Math.max(0,1 - totalEmployment/numHouseholds);
				Iterable<ROW> row = context.getObjects(ROW.class);
				for(ROW obj : row) {
					totalImports += obj.totalImports;
					totalExports += obj.totalExports;
				}
				
				double currentTick = RunEnvironment.getInstance().getCurrentSchedule().getTickCount();
				
				try {
		            File file = new File("output.txt");
		            FileOutputStream fos = new FileOutputStream(file, true);		            	            
		            PrintStream ps = new PrintStream(fos);      // Create a PrintStream that writes to the file          
		            System.setOut(ps);                          // Redirect System.out to the PrintStream	            
//		            System.out.println("Tick:" + currentTick+"  Total output: " + totalOutput +  "   Planned output: " + totalPlannedOutput +  "   Total Employment: " + totalEmployment);
//		            System.out.println("Tick:" + currentTick+"Service Inventory: " + serviceInventoryVolum + "   Goods Inventory: " + goodsInventoryVolum + "   Energy Inventory: " + energyInventoryVolum);
		            ps.close();   // Close the streams
		            fos.close();
		        } catch (Exception e) {
		            e.printStackTrace();
		        }
	}
	public double getUnemploymentRate() {
	    return this.unemploymentRate;
	}
	public double getUnemploymentRateID() {
	    return this.unemploymentRateID;
	}
	public double getTotalOutput() {
	    return this.totalOutput;
	}
	public double getSalesToFirms() {
	    return this.salesToFirms;
	}
	public double getSalesToHouseholds() {
	    return this.salesToHouseholds;
	}
	public double getSalesToFirmsID() {
	    return this.salesToFirmsID;
	}
	public double getPriceIndexDomesticFirms() {
	    return this.priceIndexDomesticFirms;
	}
	public double getPriceIndexDomesticFirmsID() {
	    return this.priceIndexDomesticFirmsID;
	}
	public double getSalesToHouseholdsID() {
	    return this.outputGoodsSector;
	}
	public double getOutputGoodsSector(){
	    return this.outputGoodsSector;
	} 
	public double getOutputServiceSector(){
	    return this.outputServiceSector;
	}
	public double getOutputEnergySector(){
	    return this.outputEnergySector;
	}
	public double getOutputGoodsSectorID(){
	    return this.outputGoodsSectorID;
	} 
	public double getOutputServiceSectorID(){
	    return this.outputServiceSectorID;
	}
	public double getOutputEnergySectorID(){
	    return this.outputEnergySectorID;
	}
	public double getTotalPlannedOutput() {
	    return this.totalPlannedOutput;
	}
	public double getTotalImports() {
	    return this.totalImports;
	}
	public double getTotalExports() {
	    return this.totalExports;
	}
	public double getTotalImportsID() {
	    return this.getTotalImportsID;
	}
	public double getTotalOutputID() {
	    return this.totalOutputID;
	}
	public double getTotalPlannedOutputID() {
	    return this.totalPlannedOutputID;
	}
	public double getTotalInventory() {
	    return this.totalInventory;
	}
	public double getTotalInventoryID() {
	    return this.totalInventoryID;
	}
	public double getOutputSector1() {
		return this.outputSector1;
	}
	public double getOutputSector8() {
		return this.outputSector8;
	}
	public double getPlannedOutputSector8() {
		return this.plannedOutputSector8;
	}
	public double getOutputSector1ID() {
		return this.outputSector1ID;
	}
	public double getenergyInventoryVolum() {
		return this.energyInventoryVolum;
	}
	
	public double getavgWageID(){
		return this.avgWageID;
		}
	public double getavgWage(){
		return this.avgWage;
		}
	}
