package eWAGI_Firms;
public class TechnicalCoefficient {
	int sectorID;
	double technicalCoefficient;
	double shareCapitalInvestments;
	int type;
	double sumCoefficentsType;
	TechnicalCoefficient(int sectorID,double technicalCoefficient,double shareCapitalInvestments, int type){
		this.sectorID =  sectorID;
		this.technicalCoefficient = technicalCoefficient;
		this.shareCapitalInvestments = shareCapitalInvestments;
		this.type = type;
		sumCoefficentsType = 0;
	}
}