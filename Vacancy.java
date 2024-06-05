package eWAGI_Firms;
public class Vacancy {
	int firmID;
	double wage;
	Firm firm;
	Vacancy(int firmID, double wage, Firm firm){
		this.firmID= firmID;
		this.wage = wage;
		this.firm = firm;
	}
}