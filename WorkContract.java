package eWAGI_Firms;
public class WorkContract {
	int employerID;
	int workerID;
	double wage;
	int duration;
	Household worker;
	Firm employer;
	boolean canceled;
	WorkContract(Household worker, Firm employer, double wage){
		this.worker = worker;
		this.workerID = worker.ID;
		this.employer = employer;
		this.employerID = employer.ID;
		this.wage = wage;
		canceled = false;
		duration = 0;
	}
}