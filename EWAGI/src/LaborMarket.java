package eWAGI_Firms;
import java.util.ArrayList;
public class LaborMarket {
	int sectorID;
	ArrayList<Vacancy> vacancyList;
	ArrayList<JobSearch> jobSearchList;
	int openPositions;
	LaborMarket(int sectorID){
		this.sectorID = sectorID;
		vacancyList = new ArrayList<Vacancy>() ;
		jobSearchList = new ArrayList<JobSearch>();
	}
}