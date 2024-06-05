package eWAGI_Firms;
public class ConsumptionCoefficientService extends ConsumptionCoefficient {
	ServiceOrder serviceOrder;
	ConsumptionCoefficientService(int sectorID,double coefficient){
		super(sectorID,coefficient);
	}
	double getAcceptedQuantity() {
		return serviceOrder.acceptedQuantity;
	}
	double getInvoiceAmount() {
		return serviceOrder.invoiceAmount;
	}
}