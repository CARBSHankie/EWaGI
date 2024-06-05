package eWAGI_Firms;
class ServiceContract {
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
	ServiceContract(int provider,int client,int sectorID, int iteration, double volume,double price) {
		this.provider=provider;
		this.client=client;
		this.sectorID=sectorID;
		this.volume=volume;
		this.price=price;
		this.invoice = 0;
		this.iteration = iteration;
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