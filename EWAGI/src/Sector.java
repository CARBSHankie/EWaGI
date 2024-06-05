package eWAGI_Firms;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import repast.simphony.engine.schedule.ScheduledMethod;
import repast.simphony.space.continuous.ContinuousSpace;
import repast.simphony.space.grid.Grid;
public class Sector {
	private ContinuousSpace<Object> space;
	private Grid<Object> grid;
	ArrayList<TechnicalCoefficient> technicalCoefficients;
	int sectorID;
	int type;
	int numFirms; 
	double capitalCoefficent;
	double laborCoefficient;
	double intermediateInputCoeffient;
	double energyCoefficient;
	double serviceCoefficient;
	double employmentShare;
	double employmentData;
	double laborForce;
	double wage, totalLaborCosts;
	double initialOutput;
	double importQuota;
	double priceIndex;
	double avgKnowledgeStock;
	LaborMarket laborMarket;
	//Constructor sector
	Sector(ContinuousSpace<Object> space, Grid<Object> grid, int sectorID, double totalSizeLaborForce, int numFirms) {
		initialOutput = 0;
		this.sectorID = sectorID;
		this.space = space;
		this.grid = grid;
		this.numFirms = numFirms;
		//holds the technical coefficients derived from the input output table
		technicalCoefficients = new ArrayList<TechnicalCoefficient>();
		String line = "";
		ArrayList<Double> invs = new ArrayList<Double>();
		// Create sectoral labor market
		laborMarket = new LaborMarket(sectorID);
		//read in investment data
		for(int i=0; i < 12;i++) {
			try {
			line = Files.readAllLines(Paths.get("investments.csv")).get(i+1);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			List<String> items = Arrays.asList(line.split("\\s*,\\s*"));
			invs.add(Double.parseDouble(items.get(1)));
		}
		//read in input coefficient data
		try {
			line = Files.readAllLines(Paths.get("input_coefficents.csv")).get(sectorID);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		List<String> items = Arrays.asList(line.split("\\s*,\\s*"));
		double sumCoeffT1 = 0;
		double sumCoeffT2 = 0;
		double sumCoeffT3 = 0;
		//Assign type: type 1 -> goods, type 2 -> service, type 3 -> energy
		for(int i=1; i < items.size();i++) {
			int type= 1;
			if(i==8) {
				type = 3;
			}else if(i>9) {
				type = 2;
			}
			double coef = Double.parseDouble(items.get(i));
			// Add technocal coefficients
			technicalCoefficients.add(new TechnicalCoefficient( i,  Double.parseDouble(items.get(i)), invs.get(i-1),type));
			if(type==1) {
				sumCoeffT1 += coef;
			}else if(type==2) {
				sumCoeffT2 += coef;
			}else if(type==3) {
				sumCoeffT3 += coef;
			}
		}
		//These are used to compute the input coefficients for the three product groups goods, service and energy)
		for(int i=0; i < technicalCoefficients.size();i++) {
			if(technicalCoefficients.get(i).type==1) {
				technicalCoefficients.get(i).sumCoefficentsType= sumCoeffT1;
			}else if(technicalCoefficients.get(i).type==2) {
				technicalCoefficients.get(i).sumCoefficentsType= sumCoeffT2;
			}else if(technicalCoefficients.get(i).type==3) {
				technicalCoefficients.get(i).sumCoefficentsType= sumCoeffT3;
			}
		}
		//Read other data from csv files
		try {
			line = Files.readAllLines(Paths.get("cap_stock.csv")).get(sectorID);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		List<String> items1 = Arrays.asList(line.split("\\s*,\\s*"));
		try {
			line = Files.readAllLines(Paths.get("prod_value.csv")).get(sectorID);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		List<String> items2 = Arrays.asList(line.split("\\s*,\\s*"));
		try {
			line = Files.readAllLines(Paths.get("empl.csv")).get(sectorID);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		List<String> items3 = Arrays.asList(line.split("\\s*,\\s*"));
		try {
			line = Files.readAllLines(Paths.get("empl_shares.csv")).get(sectorID);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		List<String> items3a = Arrays.asList(line.split("\\s*,\\s*"));
		try {
			line = Files.readAllLines(Paths.get("energy_coefficient.csv")).get(sectorID);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		List<String> items4 = Arrays.asList(line.split("\\s*,\\s*"));
		try {
			line = Files.readAllLines(Paths.get("intermediate_coefficient.csv")).get(sectorID);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		List<String> items5 = Arrays.asList(line.split("\\s*,\\s*"));
		try {
			line = Files.readAllLines(Paths.get("service_coefficient.csv")).get(sectorID);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		List<String> items6 = Arrays.asList(line.split("\\s*,\\s*"));
		try {
			line = Files.readAllLines(Paths.get("labor_costs.csv")).get(sectorID);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		List<String> items7 = Arrays.asList(line.split("\\s*,\\s*"));
		try {
			line = Files.readAllLines(Paths.get("imports.csv")).get(sectorID);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		List<String> items8 = Arrays.asList(line.split("\\s*,\\s*"));
		double productionvalue = Double.parseDouble(items2.get(1));
		importQuota = Double.parseDouble(items8.get(1))/ productionvalue;
		double capitalStockValue = Double.parseDouble(items1.get(1));
		//Emplyoment data
		employmentData = Double.parseDouble(items3.get(1));
		employmentShare =  Double.parseDouble(items3a.get(1));
		laborForce = employmentShare*totalSizeLaborForce;
		totalLaborCosts = Double.parseDouble(items7.get(1));
		wage = totalLaborCosts/ employmentData;
		//Coefficents that enter the production function of firms
		capitalCoefficent = productionvalue / capitalStockValue  ;
		laborCoefficient = productionvalue / employmentData;	
		energyCoefficient =  Double.parseDouble(items4.get(1));;
		intermediateInputCoeffient =  Double.parseDouble(items5.get(1));;
		serviceCoefficient  =  Double.parseDouble(items6.get(1));;
	}
	//Execute labor market matching: 
	@ScheduledMethod (start = 1, interval = 1, priority = 58)
	public void laborMarketMatching() {
		Collections.shuffle(laborMarket.vacancyList);
		//Sort vanacny list by wages in descending order
		Collections.sort(laborMarket.vacancyList, new VacancyComparator());
		Collections.shuffle(laborMarket.jobSearchList);
		for(int i=0; i <laborMarket.vacancyList.size();i++) {
			for(int j=0; j < laborMarket.jobSearchList.size();j++) {
				// If wage offer > reservation wage of household
				if(laborMarket.vacancyList.get(i).wage>laborMarket.jobSearchList.get(j).reservationWage) {
					WorkContract aWorkContract = new WorkContract(laborMarket.jobSearchList.get(j).household,laborMarket.vacancyList.get(i).firm, laborMarket.vacancyList.get(i).wage);
					laborMarket.jobSearchList.get(j).household.activateNewWorkContract(aWorkContract);
					laborMarket.vacancyList.get(i).firm.workContractList.add(aWorkContract);
					laborMarket.vacancyList.remove(i);
					laborMarket.jobSearchList.remove(j);
					i--;
					break;
				}
			}
		}
		laborMarket.openPositions = laborMarket.vacancyList.size();
	}
	@ScheduledMethod (start = 1, interval = 1, priority = 5)
	public void cleanUp() {
		laborMarket.jobSearchList.clear();
		laborMarket.vacancyList.clear();
	}
}