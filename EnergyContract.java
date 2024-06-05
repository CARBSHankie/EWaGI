package eWAGI_Firms;
public class EnergyContract {
	int provider;
	int client;
	int sectorID;
	int iteration;
	double volume;
	double price;
	double invoice;
	boolean renegotiate;
	boolean cancel;
	boolean fullyServed;
	double servedVolume;
	EnergyContract(int provider,int client,int sectorID,int iteration,double volume,double price) {
		this.provider=provider;
		this.client=client;
		this.sectorID=sectorID;
		this.iteration = iteration;
		this.volume=volume;
		this.price=price;
		this.invoice = 0;
		renegotiate = false;
		cancel = false;
		fullyServed = true;
		servedVolume = 0;
	}
	double getPrice() {
		return price;
	}
	double getVolume() {
		return volume;
	}
}