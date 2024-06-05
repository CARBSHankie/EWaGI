package eWAGI_Firms;
public class JobSearch {
	int workerID;
	int generalSKill;
	double experienceLevel;
	double reservationWage;
	Household household;
	JobSearch(int workerID, int generalSkill, double reservationWage, double experienceLevel, Household household){
		this.workerID = workerID;
		this.generalSKill = generalSkill;
		this.experienceLevel = experienceLevel;
		this.reservationWage = reservationWage;
		this.household = household;
	}
}