package eWAGI_Firms;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import repast.simphony.context.Context;
import repast.simphony.engine.environment.RunEnvironment;
import repast.simphony.engine.schedule.ScheduledMethod;
import repast.simphony.util.ContextUtils;
public class ROW {
	int ID;
	int tick;
	int foreignBankID;
	double intensityChoiceSupplier;
	PaymentAccount paymentAccount;
	ArrayList<SectorExportProfile> sectorExportProfiles;
	ArrayList<SectorImportProfile> sectorImportProfiles;
	double nominalExportVolumeTarget;
	double nominalExportVolume;
	double totalImports, totalExports;
	ROW(int ID, int foreignBankID){
		this.ID = ID;
		this.foreignBankID = foreignBankID;
		sectorExportProfiles = new ArrayList<SectorExportProfile>();
		sectorImportProfiles = new ArrayList<SectorImportProfile>();
		this.totalImports= 0;
		this.totalExports = 0;
		paymentAccount = new PaymentAccount(ID, foreignBankID,0,3);
	}
	@ScheduledMethod (start = 1, interval = 1, priority = 100)
	public void setupIteration() {
		tick = (int) RunEnvironment.getInstance().getCurrentSchedule().getTickCount();
		nominalExportVolume = 0;
		if(this.tick ==1) {
			Context  context = ContextUtils.getContext(this);
			Iterable<GoodsSector> allSectors = context.getObjects(GoodsSector.class);
			for(GoodsSector obj : allSectors) {
				GoodsSector aSector = obj;
				String line = "";
				try {
					line = Files.readAllLines(Paths.get("exports.csv")).get(aSector.sectorID);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				List<String> items2 = Arrays.asList(line.split("\\s*,\\s*"));
				sectorExportProfiles.add(new SectorExportProfile(aSector.sectorID,Double.parseDouble(items2.get(1))));
				int numImporters = (int) Math.ceil(aSector.numFirms * aSector.importQuota);
				SectorImportProfile aSectorImportProfile = new SectorImportProfile(ID,aSector.sectorID, 1.0, aSector.initialOutput*aSector.importQuota, numImporters, 1  );
				for(int i=0; i < aSectorImportProfile.importFirmListGoods.size();i++) {
					Iterable<ForeignBank> allForeignBanks = context.getObjects(ForeignBank.class);
					for(ForeignBank obj2 : allForeignBanks) {
						if(obj2.ID == foreignBankID) {
							ForeignBank fbank = obj2; 
							fbank.paymentAccountList.add(aSectorImportProfile.importFirmListGoods.get(i).paymentAccount);
							break;
						}
					}
					aSector.mallList.get(0).firmInventories.add(aSectorImportProfile.importFirmListGoods.get(i).inventory);
				}
				sectorImportProfiles.add(aSectorImportProfile);
				paymentAccount.pendingOutgoingTransactionsList.clear();
				paymentAccount.receivedTransactionsList.clear();
			}
			Iterable<ServiceSector> allSectorsService = context.getObjects(ServiceSector.class);
			for(ServiceSector obj : allSectorsService) {
				ServiceSector aSector = obj;
				String line = "";
				try {
					line = Files.readAllLines(Paths.get("exports.csv")).get(aSector.sectorID);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				List<String> items2 = Arrays.asList(line.split("\\s*,\\s*"));
				sectorExportProfiles.add(new SectorExportProfile(aSector.sectorID,Double.parseDouble(items2.get(1))));
				int numImporters = (int) Math.ceil(aSector.numFirms * aSector.importQuota);
				SectorImportProfile aSectorImportProfile = new SectorImportProfile(ID,aSector.sectorID, 1.0, aSector.initialOutput*aSector.importQuota, numImporters, 2  );
				for(int i=0; i < aSectorImportProfile.importFirmListService.size();i++) {
					aSector.mallList.get(0).firmInventories.add(aSectorImportProfile.importFirmListService.get(i).inventory);
					Iterable<ForeignBank> allForeignBanks = context.getObjects(ForeignBank.class);
					for(ForeignBank obj2 : allForeignBanks) {
						if(obj2.ID == foreignBankID) {
							ForeignBank fbank = obj2; 
							fbank.paymentAccountList.add(aSectorImportProfile.importFirmListService.get(i).paymentAccount);
							break;
						}
					}
				}
				sectorImportProfiles.add(aSectorImportProfile);
			}
			Iterable<EnergySector> allSectorsEnergy = context.getObjects(EnergySector.class);
			for(EnergySector obj : allSectorsEnergy) {
				EnergySector aSector = obj;
				String line = "";
				try {
					line = Files.readAllLines(Paths.get("exports.csv")).get(aSector.sectorID);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				List<String> items2 = Arrays.asList(line.split("\\s*,\\s*"));
				sectorExportProfiles.add(new SectorExportProfile(aSector.sectorID,Double.parseDouble(items2.get(1))));
				int numImporters = (int) Math.ceil(aSector.numFirms * aSector.importQuota);
				SectorImportProfile aSectorImportProfile = new SectorImportProfile(ID,aSector.sectorID, 1.0, aSector.initialOutput*aSector.importQuota, numImporters, 3  );
				for(int i=0; i < aSectorImportProfile.importFirmListEnergy.size();i++) {
					aSector.mallList.get(0).firmInventories.add(aSectorImportProfile.importFirmListEnergy.get(i).inventory);
					Iterable<ForeignBank> allForeignBanks = context.getObjects(ForeignBank.class);
					for(ForeignBank obj2 : allForeignBanks) {
						if(obj2.ID == foreignBankID) {
							ForeignBank fbank = obj2; 
							fbank.paymentAccountList.add(aSectorImportProfile.importFirmListEnergy.get(i).paymentAccount);
							break;
						}
					}
				}
				sectorImportProfiles.add(aSectorImportProfile);
			}
		}
	}

	@ScheduledMethod (start = 1, interval = 1, priority = 85)
	public void budgetAllocation() {
			for(int i=0; i < sectorExportProfiles.size();i++) {
				sectorExportProfiles.get(i).budget = sectorExportProfiles.get(i).share * this.nominalExportVolumeTarget ;
			}
	}
	@ScheduledMethod (start = 1, interval = 1, priority = 65)
	public void exportRequestsGoods() {
		Context  context = ContextUtils.getContext(this);
		Iterable<GoodsSector> allSectors = context.getObjects(GoodsSector.class);
		for(int i=0; i < sectorExportProfiles.size();i++) {
		for(GoodsSector obj : allSectors) {
			if(obj.sectorID==sectorExportProfiles.get(i).sectorID) {
				sectorExportProfiles.get(i).firmExportsGoods.clear();
					GoodsSector aSector = obj;
					GoodsMall aMall = aSector.mallList.get(0);
						double denLogit = 0.0; 
						for(int j =0 ; j < aMall.firmInventories.size(); j++) {
							if(aMall.firmInventories.get(j).domestic)
								denLogit += Math.exp(this.intensityChoiceSupplier*aMall.firmInventories.get(j).price);
						}
						for(int j =0 ; j < aMall.firmInventories.size(); j++) {
							if(aMall.firmInventories.get(j).domestic) {
								sectorExportProfiles.get(i).firmExportsGoods.add(new ExportItemGoods( aMall.firmInventories.get(j).firmID,aMall.firmInventories.get(j).bankID, Math.exp(this.intensityChoiceSupplier* aMall.firmInventories.get(j).price)/denLogit));
								double quantity = sectorExportProfiles.get(i).firmExportsGoods.get(sectorExportProfiles.get(i).firmExportsGoods.size()-1).share*sectorExportProfiles.get(i).budget/aMall.firmInventories.get(j).price;
								sectorExportProfiles.get(i).firmExportsGoods.get(sectorExportProfiles.get(i).firmExportsGoods.size()-1).order = new GoodsOrder (ID, aMall.firmInventories.get(j).firmID,quantity, false );
								aSector.mallList.get(0).firmInventories.get(j).orderBook.add(sectorExportProfiles.get(i).firmExportsGoods.get(sectorExportProfiles.get(i).firmExportsGoods.size()-1).order);
							}
						}
			}				
			}
			}			
		}
	@ScheduledMethod (start = 1, interval = 1, priority = 65)
	public void exportRequestsService() {
		Context  context = ContextUtils.getContext(this);
		Iterable<ServiceSector> allSectors = context.getObjects(ServiceSector.class);
		for(int i=0; i < sectorExportProfiles.size();i++) {
		for(ServiceSector obj : allSectors) {
			if(obj.sectorID==sectorExportProfiles.get(i).sectorID) {
				sectorExportProfiles.get(i).firmExportsService.clear();
					ServiceSector aSector = obj;
					ServiceMall aMall = aSector.mallList.get(0);
						double denLogit = 0.0; 
						for(int j =0 ; j < aMall.firmInventories.size(); j++) {
							if(aMall.firmInventories.get(j).domestic)
								denLogit += Math.exp(this.intensityChoiceSupplier*aMall.firmInventories.get(j).price);
						}
						for(int j =0 ; j < aMall.firmInventories.size(); j++) {
							if(aMall.firmInventories.get(j).domestic) {
								sectorExportProfiles.get(i).firmExportsService.add(new ExportItemService( aMall.firmInventories.get(j).firmID,aMall.firmInventories.get(j).bankID, Math.exp(this.intensityChoiceSupplier* aMall.firmInventories.get(j).price)/denLogit));
								double quantity = sectorExportProfiles.get(i).firmExportsService.get(sectorExportProfiles.get(i).firmExportsService.size()-1).share*sectorExportProfiles.get(i).budget/aMall.firmInventories.get(j).price;
								sectorExportProfiles.get(i).firmExportsService.get(sectorExportProfiles.get(i).firmExportsService.size()-1).order = new ServiceOrder (ID, 0, quantity, true );
								aSector.mallList.get(0).firmInventories.get(j).orderBook.add(sectorExportProfiles.get(i).firmExportsService.get(sectorExportProfiles.get(i).firmExportsService.size()-1).order);
							}
						}
			}				
			}
			}			
		}
	@ScheduledMethod (start = 1, interval = 1, priority = 65)
	public void exportRequestsEnergy() {
		Context  context = ContextUtils.getContext(this);
		Iterable<EnergySector> allSectors = context.getObjects(EnergySector.class);
		for(int i=0; i < sectorExportProfiles.size();i++) {
		for(EnergySector obj : allSectors) {
			if(obj.sectorID==sectorExportProfiles.get(i).sectorID) {
				sectorExportProfiles.get(i).firmExportsEnergy.clear();
					EnergySector aSector = obj;
					EnergyMall aMall = aSector.mallList.get(0);
						double denLogit = 0.0; 
						for(int j =0 ; j < aMall.firmInventories.size(); j++) {
							if(aMall.firmInventories.get(j).domestic)
								denLogit += Math.exp(this.intensityChoiceSupplier*aMall.firmInventories.get(j).price);
						}
						for(int j =0 ; j < aMall.firmInventories.size(); j++) {
							if(aMall.firmInventories.get(j).domestic) {
								sectorExportProfiles.get(i).firmExportsEnergy.add(new ExportItemEnergy( aMall.firmInventories.get(j).firmID,aMall.firmInventories.get(j).bankID, Math.exp(this.intensityChoiceSupplier* aMall.firmInventories.get(j).price)/denLogit));
								double quantity = sectorExportProfiles.get(i).firmExportsEnergy.get(sectorExportProfiles.get(i).firmExportsEnergy.size()-1).share*sectorExportProfiles.get(i).budget/aMall.firmInventories.get(j).price;
								sectorExportProfiles.get(i).firmExportsEnergy.get(sectorExportProfiles.get(i).firmExportsEnergy.size()-1).order =new EnergyOrder (ID, 0, quantity, true );
								aSector.mallList.get(0).firmInventories.get(j).orderBook.add(sectorExportProfiles.get(i).firmExportsEnergy.get(sectorExportProfiles.get(i).firmExportsEnergy.size()-1).order);
							}
						}
			}				
			}
			}			
		}
	@ScheduledMethod (start = 1, interval = 1, priority = 45)
	public void receiveExports() {
		totalExports = 0;
		for(int i =0; i < sectorExportProfiles.size();i++) {
			for(int j =0; j <sectorExportProfiles.get(i).firmExportsGoods.size();j++) {
				sectorExportProfiles.get(i).firmExportsGoods.get(j).receivedQuantity = sectorExportProfiles.get(i).firmExportsGoods.get(j).order.acceptedQuantity;
				nominalExportVolume +=  sectorExportProfiles.get(i).firmExportsGoods.get(j).order.invoiceAmount;
				this.paymentAccount.pendingOutgoingTransactionsList.add(new PaymentTransaction(ID, sectorExportProfiles.get(i).firmExportsGoods.get(j).firmID, paymentAccount.bankID, sectorExportProfiles.get(i).firmExportsGoods.get(j).bankID,sectorExportProfiles.get(i).firmExportsGoods.get(j).order.invoiceAmount ,"salesRevenue"));
				totalExports += sectorExportProfiles.get(i).firmExportsGoods.get(j).receivedQuantity;
			}
			for(int j =0; j <sectorExportProfiles.get(i).firmExportsService.size();j++) {
				sectorExportProfiles.get(i).firmExportsService.get(j).receivedQuantity = sectorExportProfiles.get(i).firmExportsService.get(j).order.acceptedQuantity;
				nominalExportVolume +=  sectorExportProfiles.get(i).firmExportsService.get(j).order.invoiceAmount;
				this.paymentAccount.pendingOutgoingTransactionsList.add(new PaymentTransaction(ID, sectorExportProfiles.get(i).firmExportsService.get(j).firmID, paymentAccount.bankID, sectorExportProfiles.get(i).firmExportsService.get(j).bankID,sectorExportProfiles.get(i).firmExportsService.get(j).order.invoiceAmount ,"salesRevenue"));
				totalExports += sectorExportProfiles.get(i).firmExportsService.get(j).receivedQuantity;
			}
			for(int j =0; j <sectorExportProfiles.get(i).firmExportsEnergy.size();j++) {
				sectorExportProfiles.get(i).firmExportsEnergy.get(j).receivedQuantity = sectorExportProfiles.get(i).firmExportsEnergy.get(j).order.acceptedQuantity;
				nominalExportVolume +=  sectorExportProfiles.get(i).firmExportsEnergy.get(j).order.invoiceAmount;
				this.paymentAccount.pendingOutgoingTransactionsList.add(new PaymentTransaction(ID, sectorExportProfiles.get(i).firmExportsEnergy.get(j).firmID, paymentAccount.bankID, sectorExportProfiles.get(i).firmExportsEnergy.get(j).bankID,sectorExportProfiles.get(i).firmExportsEnergy.get(j).order.invoiceAmount ,"salesRevenue"));
				totalExports += sectorExportProfiles.get(i).firmExportsEnergy.get(j).receivedQuantity;
			}
		}
	}
	@ScheduledMethod (start = 1, interval = 1, priority = 44)
	public void adjustImports() {
		for(int i =0; i < sectorImportProfiles.size();i++) {
			for(int j=0; j < sectorImportProfiles.get(i).importFirmListGoods.size();j++ )
			{
				double currentStock = sectorImportProfiles.get(i).importFirmListGoods.get(j).inventory.inventoryStock;
				double refillLevel = sectorImportProfiles.get(i).importFirmListGoods.get(j).inventory.refillLevel ;
					if(sectorImportProfiles.get(i).importFirmListGoods.get(j).inventory.totalExcessDemand>0) {
						sectorImportProfiles.get(i).importFirmListGoods.get(j).inventory.refillLevel = sectorImportProfiles.get(i).importFirmListGoods.get(j).inventory.refillLevel + GlobalParameters.refillLeveladjustmentcoefficient * sectorImportProfiles.get(i).importFirmListGoods.get(j).inventory.totalExcessDemand;
						//sectorImportProfiles.get(i).importFirmListGoods.get(j).inventory.refillLevel = sectorImportProfiles.get(i).importFirmListGoods.get(j).inventory.refillLevel*(1.05);
					}
					if(sectorImportProfiles.get(i).importFirmListGoods.get(j).inventory.avPrice>0)
						sectorImportProfiles.get(i).importFirmListGoods.get(j).inventory.price = sectorImportProfiles.get(i).importFirmListGoods.get(j).inventory.avPrice;
			}
			for(int j=0; j < sectorImportProfiles.get(i).importFirmListService.size();j++ )
			{
				double currentStock = sectorImportProfiles.get(i).importFirmListService.get(j).inventory.inventoryStock;
				double refillLevel = sectorImportProfiles.get(i).importFirmListService.get(j).inventory.refillLevel ;
					if(sectorImportProfiles.get(i).importFirmListService.get(j).inventory.totalExcessDemand>0) {
						sectorImportProfiles.get(i).importFirmListService.get(j).inventory.refillLevel = sectorImportProfiles.get(i).importFirmListService.get(j).inventory.refillLevel + GlobalParameters.refillLeveladjustmentcoefficient * sectorImportProfiles.get(i).importFirmListService.get(j).inventory.totalExcessDemand;
						//sectorImportProfiles.get(i).importFirmListService.get(j).inventory.refillLevel = sectorImportProfiles.get(i).importFirmListService.get(j).inventory.refillLevel*(1.05);
				}
					if(sectorImportProfiles.get(i).importFirmListService.get(j).inventory.avPrice>0)
						sectorImportProfiles.get(i).importFirmListService.get(j).inventory.price = sectorImportProfiles.get(i).importFirmListService.get(j).inventory.avPrice;
			}
			for(int j=0; j < sectorImportProfiles.get(i).importFirmListEnergy.size();j++ )
			{
				double currentStock = sectorImportProfiles.get(i).importFirmListEnergy.get(j).inventory.inventoryStock;
				double refillLevel = sectorImportProfiles.get(i).importFirmListEnergy.get(j).inventory.refillLevel ;
				if(sectorImportProfiles.get(i).importFirmListEnergy.get(j).inventory.totalExcessDemand>0) {
					sectorImportProfiles.get(i).importFirmListEnergy.get(j).inventory.refillLevel = sectorImportProfiles.get(i).importFirmListEnergy.get(j).inventory.refillLevel + GlobalParameters.refillLeveladjustmentcoefficient * sectorImportProfiles.get(i).importFirmListEnergy.get(j).inventory.totalExcessDemand;
					//sectorImportProfiles.get(i).importFirmListEnergy.get(j).inventory.refillLevel = sectorImportProfiles.get(i).importFirmListEnergy.get(j).inventory.refillLevel*(1.05);
				//	refillLevel = sectorImportProfiles.get(i).importFirmListEnergy.get(j).inventory.refillLevel ;
				}
				if(sectorImportProfiles.get(i).importFirmListEnergy.get(j).inventory.avPrice>0)
					sectorImportProfiles.get(i).importFirmListEnergy.get(j).inventory.price = sectorImportProfiles.get(i).importFirmListEnergy.get(j).inventory.avPrice;
			}
		}
	}
	@ScheduledMethod (start = 1, interval = 1, priority = 43)
	public void deliverImports() {
		totalImports = 0.0;
		for(int i =0; i < sectorImportProfiles.size();i++) {
			for(int j=0; j < sectorImportProfiles.get(i).importFirmListGoods.size();j++ )
			{
				sectorImportProfiles.get(i).importFirmListGoods.get(j).quantity = Math.max(0 ,sectorImportProfiles.get(i).importFirmListGoods.get(j).inventory.refillLevel -  sectorImportProfiles.get(i).importFirmListGoods.get(j).inventory.inventoryStock);
				totalImports += sectorImportProfiles.get(i).importFirmListGoods.get(j).quantity;
				sectorImportProfiles.get(i).importFirmListGoods.get(j).inventory.inventoryStock += sectorImportProfiles.get(i).importFirmListGoods.get(j).quantity;
				sectorImportProfiles.get(i).importFirmListGoods.get(j).inventory.quality = sectorImportProfiles.get(i).importFirmListGoods.get(j).quality;
			}
			for(int j=0; j < sectorImportProfiles.get(i).importFirmListService.size();j++ )
			{
				sectorImportProfiles.get(i).importFirmListService.get(j).quantity = Math.max(0 ,sectorImportProfiles.get(i).importFirmListService.get(j).inventory.refillLevel -  sectorImportProfiles.get(i).importFirmListService.get(j).inventory.inventoryStock);
				totalImports += sectorImportProfiles.get(i).importFirmListService.get(j).quantity;
				sectorImportProfiles.get(i).importFirmListService.get(j).inventory.inventoryStock += sectorImportProfiles.get(i).importFirmListService.get(j).quantity;
				sectorImportProfiles.get(i).importFirmListService.get(j).inventory.quality = sectorImportProfiles.get(i).importFirmListService.get(j).quality;
			}
			for(int j=0; j < sectorImportProfiles.get(i).importFirmListEnergy.size();j++ )
			{
				sectorImportProfiles.get(i).importFirmListEnergy.get(j).quantity = Math.max(0 ,sectorImportProfiles.get(i).importFirmListEnergy.get(j).inventory.refillLevel -  sectorImportProfiles.get(i).importFirmListEnergy.get(j).inventory.inventoryStock);
				totalImports += sectorImportProfiles.get(i).importFirmListEnergy.get(j).quantity;
				sectorImportProfiles.get(i).importFirmListEnergy.get(j).inventory.inventoryStock += sectorImportProfiles.get(i).importFirmListEnergy.get(j).quantity;
				sectorImportProfiles.get(i).importFirmListEnergy.get(j).inventory.quality = sectorImportProfiles.get(i).importFirmListEnergy.get(j).quality;
			}
		}
	}
	class SectorExportProfile{
		int sectorID;
		double share;
		double budget;
		ArrayList<ExportItemGoods> firmExportsGoods;
		ArrayList<ExportItemService> firmExportsService;
		ArrayList<ExportItemEnergy> firmExportsEnergy;
		SectorExportProfile(int sectorID, double share){
			this.sectorID = sectorID;
			firmExportsGoods = new ArrayList<ExportItemGoods>();
			firmExportsService = new ArrayList<ExportItemService>();
			firmExportsEnergy = new ArrayList<ExportItemEnergy>();
			this.share = share;
			this.budget = 0.0;
		}
	}
class SectorImportProfile{
		int sectorID;
		double price;
		double quantity;
		ArrayList<ImportGoodsFirm> importFirmListGoods;
		ArrayList<ImportFirmService> importFirmListService;
		ArrayList<ImportFirmEnergy> importFirmListEnergy;
		SectorImportProfile(int id, int sectorID, double price , double quantity, int numImporters, int type ){
			this.sectorID = sectorID;
			this.price = price;
			importFirmListGoods =  new ArrayList<ImportGoodsFirm>();
			importFirmListService =  new ArrayList<ImportFirmService>();
			importFirmListEnergy =  new ArrayList<ImportFirmEnergy>();
			this.quantity = quantity;
			if(type==1) {
				for(int i =0; i < numImporters; i++) {
					ImportGoodsFirm aImportGoodFirm = new ImportGoodsFirm(ID + i,sectorID,foreignBankID,price, quantity/numImporters);
					importFirmListGoods.add(aImportGoodFirm );
				}
			}else if(type==2) {
				for(int i =0; i < numImporters; i++) {
					ImportFirmService aImportSericeFirm = new ImportFirmService(ID + i,sectorID,foreignBankID,price, quantity/numImporters);
					importFirmListService.add(aImportSericeFirm );
				}
			}else {
				for(int i =0; i < numImporters; i++) {
					ImportFirmEnergy aImportEnergyFirm = new ImportFirmEnergy(ID + i,sectorID,foreignBankID,price, quantity/numImporters);
					importFirmListEnergy.add(aImportEnergyFirm );
				}
			}
		}
	}
	class ExportItemGoods{
		int firmID;
		int bankID;
		double share;
		double demand;
		double receivedQuantity;
		GoodsOrder order;
		ExportItemGoods(int firmID,int bankID,double share){
			this.firmID = firmID;
			this.bankID= bankID;
			this.share = share;
			this.receivedQuantity = 0;
		}
	}
	class ExportItemService{
		int firmID;
		int bankID;
		double share;
		double demand;
		double receivedQuantity;
		ServiceOrder order;
		ExportItemService(int firmID,int bankID,double share){
			this.firmID = firmID;
			this.bankID= bankID;
			this.share = share;
			this.receivedQuantity = 0;
		}
	}
	class ExportItemEnergy{
		int firmID;
		int bankID;
		double share;
		double demand;
		double receivedQuantity;
		EnergyOrder order;
		ExportItemEnergy(int firmID,int bankID, double share){
			this.firmID = firmID;
			this.bankID= bankID;
			this.share = share;
			this.receivedQuantity = 0;
		}
	}
}