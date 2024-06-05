package eWAGI_Firms;
public class ConsumptionCoefficientEnergy extends ConsumptionCoefficient {
	EnergyOrder energyOrder;
	ConsumptionCoefficientEnergy(int sectorID,double coefficient){
		super(sectorID,coefficient);
	}
	double getAcceptedQuantity() {
		return energyOrder.acceptedQuantity;
	}
	double getInvoiceAmount() {
		return energyOrder.invoiceAmount;
	}
}