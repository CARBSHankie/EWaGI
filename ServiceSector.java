package eWAGI_Firms;
import java.util.ArrayList;
import repast.simphony.context.Context;
import repast.simphony.engine.schedule.ScheduledMethod;
import repast.simphony.space.continuous.ContinuousSpace;
import repast.simphony.space.grid.Grid;
import repast.simphony.util.ContextUtils;
	public class ServiceSector extends Sector{
		ArrayList<ServiceMall> mallList;
		ArrayList<ServiceFirm> firmList;
		double totalMarketSales;
		ServiceSector(ContinuousSpace<Object> space, Grid<Object> grid, int sectorID, double totalSizeLaborForce, int numFirms){
			super(space, grid,sectorID,  totalSizeLaborForce,  numFirms);
			mallList = new ArrayList<ServiceMall>();
			firmList = new ArrayList<ServiceFirm>();
		}
		//determines wage offer using greedy algorithm
		@ScheduledMethod (start = 1, interval = 1, priority = 79)
		public void clearOrderBooks1() {
			for(int i=0; i < mallList.size();i++) {
				for(int j=0; j < mallList.get(i).firmInventories.size();j++) {
					mallList.get(i).firmInventories.get(j).orderBook.clear();
				}
			}
		}
		//determines wage offer using greedy algorithm
			@ScheduledMethod (start = 1, interval = 1, priority = 55)
			public void allocateOrders1() {
				for(int i=0; i < mallList.size();i++) {
					mallList.get(i).setup();
					mallList.get(i).allocateOrders();
				}
			}
			//determines wage offer using greedy algorithm
			@ScheduledMethod (start = 1, interval = 1, priority = 50)
			public void clearOrderBooks2() {
				for(int i=0; i < mallList.size();i++) {
					for(int j=0; j < mallList.get(i).firmInventories.size();j++) {
						mallList.get(i).firmInventories.get(j).orderBook.clear();
					}
				}
			}
			//determines wage offer using greedy algorithm
					@ScheduledMethod (start = 1, interval = 1, priority = 47)
					public void allocateOrders2() {
						for(int i=0; i < mallList.size();i++) {
							mallList.get(i).allocateOrders();
							mallList.get(i).closeMall();
						}
					}
					//determines wage offer using greedy algorithm
					@ScheduledMethod (start = 1, interval = 1, priority = 47)
					public void computeStatistics() {
						double totalSales = 0;
						priceIndex = 0;
						double totalKnowledgeStock=0;
						avgKnowledgeStock = 0;
						for(int i=0; i < mallList.size();i++) {
							for(int j=0; j < mallList.get(i).firmInventories.size();j++) {
								totalSales += mallList.get(i).firmInventories.get(j).soldQuantities;
								priceIndex += mallList.get(i).firmInventories.get(j).soldQuantities*mallList.get(i).firmInventories.get(j).price;
							}
						}
						priceIndex = priceIndex / totalSales;
						int counter = 0;
						Context  context = ContextUtils.getContext(this);
						//TODO: Implement mall choice
						Iterable<ServiceFirm> allFirms = context.getObjects(ServiceFirm.class);
						for(ServiceFirm obj : allFirms) {
							if(obj.ownSector.sectorID == sectorID) {
								totalKnowledgeStock += obj.rdKnowledgeStock;
								counter ++;
							}
						}
						if(counter>0)
							avgKnowledgeStock = totalKnowledgeStock/counter;
					}
	}