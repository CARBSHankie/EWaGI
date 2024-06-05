package eWAGI_Firms;
import java.util.Random;

public class GlobalParameters {

// A randomize parameter setting function with max-deviation 	
    private static double addRandomVariability(double parameterValue, double maxDeviation) {
        Random random = new Random();
        double deviation = (random.nextDouble() * 2 * maxDeviation) - maxDeviation;
        return parameterValue + deviation;
    }
// A randomize parameter setting function with boundaries   
    private static final Random random = new Random();
    private static double generateRandomParameter(double lowerBound, double upperBound) {
        return lowerBound + random.nextDouble() * (upperBound - lowerBound);
    }
// Generate random parameter value from a Normal distribution
    private static double generateFromDistribution(double mean, double stdDev) {
        return random.nextGaussian() * stdDev + mean;
    }
    
//From Model.java
	public static final int numSectors = 12;
	public static final int numGoodsSectors = 8;
	public static final int serviceSectors = 3;
	public static final int numEnergySectors = 1;
	public static final int numHouseholds = 10000;
	public static final int numFirmsPerSector = 10;
	public static final int numBanks = 2;
	public static final double employmentShare = 1.0;	
// From Firm.java	
	public static final double probChangeSupplierGoods = 0.05;        
	public static final double probChangeSupplierService = 0.05;
	public static final double probChangeSupplierEnergy = 0.05;
	public static final double intensityChoicePriceGoods = (-1)*2;
	public static final double intensityChoicePriceService = (-1)*2;
	public static final double intensityChoicePriceEnergy = (-1)*2;
	public static final double intensityChoiceQualityGoods = 2;
	public static final double intensityChoiceQualityService = 2;
	public static final double intensityChoiceQualityEnergy = 2;
	public static final double PARoutputAdjustment = 0.1;
	public static final double priceDelta = 0.02;
	public static final double inventoryBufferPCT = 0.05;
	public static final double serviceDepreciation = 0.0;
	public static final double energyDepreciation = 0.0;
	public static final double smoothAvgUnitCosts = 0.5;
	public static final double smoothAvgMarketShare = 0.5;
	public static final double outputSmoothing = 0.5;
	public static final double dividendRatio = 0.8;
	public static final double knowledgeDepreciation = 0.05;
	public static final double stdDevProductInno = 0.02;
	public static final double stdDevProcessInno = 0.01;
	public static final double productivityEffectProductInnovationAdjustment = 0.1;
	public static final double specificSkillAdjustment = 0.125;	
// 	public static final double[] initAccountBalance = {200000000,200000000,200000000,200000000,200000000,200000000,200000000,200000000,200000000,200000000,200000000,200000000};
	public static final double[] initAccountBalance = {1000.0,500.0,4000.0,4000.0,8000.0, 8000.0,15000.0,4000.0,000.0,20000.0, 35000.0, 20000.0};
	public static final double refillLeveladjustmentcoefficient = 0.05;	
// From Bank.java
	public static final long initEquity = 25000000000000L; 
	public static final double lambdaB = 3.0;
	public static final int repaymentPeriod = 18;
	public static final int debtRestructuringPeriod = 6;
	public static final double interestMarkDownDeposits = 0.8;
// From Household.java
	public static final double disposableIncome =10000;
	public static final double propensityToConsume = 0.95;
	public static final double probChangeSupplier = 0.2;
	public static final double probChangeSectorUnemployed = 0.2;
	public static final double intensityChoiceSectorUnemployed = 2.0;
	public static final double intensityChoicePrice = (-1)*2.0;
	public static final double intensityChoiceQuality = 2;
	public static final double onTheJobSearchRate = 0.02;
	public static final double initAccountbalance = 5;
	
	public static final double initInputOutputRatio = 0.2;




	
}
	
	
	


