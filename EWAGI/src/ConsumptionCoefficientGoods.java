package eWAGI_Firms;
public class ConsumptionCoefficientGoods extends ConsumptionCoefficient{
	GoodsOrder goodsOrder;
		ConsumptionCoefficientGoods(int sectorID,double coefficient){
			super(sectorID,coefficient);
		}
		double getAcceptedQuantity() {
			return goodsOrder.acceptedQuantity;
		}
		double getInvoiceAmount() {
			return goodsOrder.invoiceAmount;
		}
}