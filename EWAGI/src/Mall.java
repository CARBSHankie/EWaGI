package eWAGI_Firms;
import java.util.ArrayList;
import repast.simphony.space.continuous.ContinuousSpace;
import repast.simphony.space.grid.Grid;
public class Mall{
	private ContinuousSpace<Object> space;
	private Grid<Object> grid;
	int sectorID;
	int mallID;
	double totalDemand,totalSupply,demandFirms, demandHouseholds, primaryDemand,excessDemand, totalSales, unsoldUnits,salesToHouseholds, avPrice;
	Mall(ContinuousSpace<Object> space, Grid<Object> grid,int mallID,int sectorID){
		this.mallID = mallID;
		this.sectorID = sectorID;
		this.space = space;
		this.grid = grid;
	}
}