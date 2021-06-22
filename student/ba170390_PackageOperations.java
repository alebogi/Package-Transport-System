/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package student;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import rs.etf.sab.operations.PackageOperations;

/**
 *
 * @author aleks
 */
public class ba170390_PackageOperations implements PackageOperations {
    
    private static HashMap<String, ba170390_Pair<Integer, Integer>> lastVisited = new HashMap<>();
    
    public static class ba170390_Pair<T, X> implements Pair{
        private final T i;
        private final X bd;
        
        public ba170390_Pair(T ii, X bbdd){
            this.i = ii;
            this.bd = bbdd;
        }

        @Override
        public Object getFirstParam() {
            return i;
        }

        @Override
        public Object getSecondParam() {
            return bd;
        }
        
    }
    
    public int getFuelPrice(int fuelType){
        int res = 0;
        switch(fuelType){
            case 0: res = 15;
                break;
           case 1: res = 32;
                break;
           case 2: res = 36;
               break;
           default:
               break;
        }
        return res;
    }

    @Override
    public int insertPackage(int districtFrom, int districtTo, String userName, int packageType, BigDecimal weight) {
        int id = -1;
        
        if(packageType < 0 || packageType > 2)
            return -1;
        
        Connection conn=DB.getInstance().getConnection();
        String query="insert into Package(DistrictFrom, DistrictTo, UserUsername, Type, Weight) values (?, ?, ?, ?, ?)";
        try (PreparedStatement stmt=conn.prepareStatement(query, PreparedStatement.RETURN_GENERATED_KEYS);){
            stmt.setInt(1, districtFrom);
            stmt.setInt(2, districtTo);
            stmt.setString(3, userName);
            stmt.setInt(4, packageType);
            stmt.setBigDecimal(5, weight);
            stmt.executeUpdate();
            ResultSet rs = stmt.getGeneratedKeys();
            if(rs.next()){
                id = rs.getInt(1);
            }
        } catch (SQLException ex) {
            //Logger.getLogger(ba170390_PackageOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        return id;
    }

    @Override
    public int insertTransportOffer(String couriersUserName, int packageId, BigDecimal pricePercentage) {
        int id = -1; int status = -1;
        
        Connection conn=DB.getInstance().getConnection();
        
        String query1=" select Status from Courier where CourierUsername=?";
        try (PreparedStatement stmt1=conn.prepareStatement(query1, PreparedStatement.RETURN_GENERATED_KEYS);){
            stmt1.setString(1, couriersUserName);
            ResultSet rs = stmt1.executeQuery();
            if(rs.next()){
                status = rs.getInt(1);
            }
        } catch (SQLException ex) {
            //Logger.getLogger(ba170390_PackageOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        if(status != 0)
            return -1;
        
        
        String query="insert into TransportOffer(CourierUsername, IdPckg, PricePercentage) values (?, ?, ?)";
        try (PreparedStatement stmt=conn.prepareStatement(query, PreparedStatement.RETURN_GENERATED_KEYS);){
            stmt.setString(1, couriersUserName);
            stmt.setInt(2, packageId);
            stmt.setBigDecimal(3, pricePercentage);
            stmt.executeUpdate();
            ResultSet rs = stmt.getGeneratedKeys();
            if(rs.next()){
                id = rs.getInt(1);
            }
        } catch (SQLException ex) {
            //Logger.getLogger(ba170390_PackageOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        return id;
    }
    
    private double euclidean(final int x1, final int y1, final int x2, final int y2) {
        return Math.sqrt((x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2));
    }
    
    private BigDecimal getPackagePrice(final int type, final BigDecimal weight, final double distance, BigDecimal percentage) {
        percentage = percentage.divide(new BigDecimal(100));
        switch (type) {
            case 0: {
                return new BigDecimal(10.0 * distance).multiply(percentage.add(new BigDecimal(1)));
            }
            case 1: {
                return new BigDecimal((25.0 + weight.doubleValue() * 100.0) * distance).multiply(percentage.add(new BigDecimal(1)));
            }
            case 2: {
                return new BigDecimal((75.0 + weight.doubleValue() * 300.0) * distance).multiply(percentage.add(new BigDecimal(1)));
            }
            default: {
                return null;
            }
        }
    }

    public double calculateDistance(int idDistrFrom, int idDistrTo){
        double res = 0; 
        int x1, y1, x2, y2;
        Connection conn=DB.getInstance().getConnection();
        String queryFrom = "select * from District where IdDistr=?";
        String queryTo = "select * from District where IdDistr=?";
        try (PreparedStatement stmtFrom=conn.prepareStatement(queryFrom);
                PreparedStatement stmtTo=conn.prepareStatement(queryTo);){    
            stmtFrom.setInt(1, idDistrFrom);
            stmtTo.setInt(1, idDistrTo);
            ResultSet rsFrom = stmtFrom.executeQuery();
            ResultSet rsTo = stmtTo.executeQuery();
            if(rsFrom.next()){
                x1 = rsFrom.getInt("xCord");
                y1 = rsFrom.getInt("yCord");
            }else{
                return -1;
            }
            if(rsTo.next()){
                x2 = rsTo.getInt("xCord");
                y2 = rsTo.getInt("yCord");       
            }else{
                return -1;
            }
            
            res = euclidean(x1, y1, x2, y2);
        } catch (SQLException ex) {
            Logger.getLogger(ba170390_PackageOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return res;
    }
    
    public BigDecimal calculatePrice(int idPckg, BigDecimal pricePercentage){
        BigDecimal res = BigDecimal.ZERO;
        
        Connection conn=DB.getInstance().getConnection();
        String query="select * from Package where IdPckg=?";
        try (PreparedStatement stmt=conn.prepareStatement(query);){    
            stmt.setInt(1, idPckg);
            ResultSet rs = stmt.executeQuery();
            if(rs.next()){
                BigDecimal weight = rs.getBigDecimal("Weight");
                int type = rs.getInt("Type");
                int distrFromId = rs.getInt("DistrictFrom");
                int distrToId = rs.getInt("DistrictTo");
                
                final double dist = calculateDistance(distrFromId, distrToId);
                
                res = getPackagePrice(type, weight, dist, pricePercentage);
                
            }else{
                return null;
            }
        } catch (SQLException ex) {
            Logger.getLogger(ba170390_PackageOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return res;
    }
    
    @Override
    public boolean acceptAnOffer(int offerId) {
        boolean res = false;
        
        String courier = "";
        BigDecimal pricePercentage = BigDecimal.ZERO;
        int idPckg = -1;
        BigDecimal price = BigDecimal.ZERO;
        
        Connection conn=DB.getInstance().getConnection();
        String queryGetOffer="select PricePercentage, CourierUsername, IdPckg from TransportOffer where IdOffer=?";
        String queryUpdate="update Package\n" +
                    "set CourierUsername=?, AcceptanceTime=CURRENT_TIMESTAMP, Status=1, Price=?\n" +
                    "where IdPckg=?";
        try (PreparedStatement stmtGetOffer=conn.prepareStatement(queryGetOffer);
                PreparedStatement stmtUpdate=conn.prepareStatement(queryUpdate);){
            stmtGetOffer.setInt(1, offerId);
            ResultSet rsGetOffer = stmtGetOffer.executeQuery();
            if(rsGetOffer.next()){
                courier = rsGetOffer.getString("CourierUsername");        
                pricePercentage = rsGetOffer.getBigDecimal("PricePercentage");
                idPckg = rsGetOffer.getInt("IdPckg");
                price = calculatePrice(idPckg, pricePercentage);              
            }else{
                return false;
            }
                    
            stmtUpdate.setString(1, courier);
            stmtUpdate.setBigDecimal(2, price);
            stmtUpdate.setInt(3, idPckg);
            int tmp = stmtUpdate.executeUpdate();
            if(tmp == 1)
                res = true;
            
            //offers for this pckg will be deleted by trigger
        } catch (SQLException ex) {
            
            Logger.getLogger(ba170390_PackageOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        return res;
    }

    @Override
    public List<Integer> getAllOffers() {
        List<Integer> list = new LinkedList<>();
        
        Connection conn=DB.getInstance().getConnection();
        String query="select IdOffer from TransportOffer";
        try (PreparedStatement stmt=conn.prepareStatement(query);){            
            ResultSet rs = stmt.executeQuery();
            while(rs.next()){
                list.add(rs.getInt(1));
            }
        } catch (SQLException ex) {
            Logger.getLogger(ba170390_PackageOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return list;
    }

    @Override
    public List<Pair<Integer, BigDecimal>> getAllOffersForPackage(int packageId) {
        List<Pair<Integer, BigDecimal>> list = new LinkedList<>();
        
        Connection conn=DB.getInstance().getConnection();
        String query="select IdOffer, PricePercentage from TransportOffer where IdPckg=?";
        try (PreparedStatement stmt=conn.prepareStatement(query);){  
            stmt.setInt(1, packageId);
            ResultSet rs = stmt.executeQuery();
            while(rs.next()){
                int id = rs.getInt("IdOffer");
                BigDecimal pp = rs.getBigDecimal("PricePercentage");
                ba170390_Pair pair = new ba170390_Pair(id, pp);
                list.add(pair);
            }
        } catch (SQLException ex) {
            Logger.getLogger(ba170390_PackageOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return list;
    }

    @Override
    public boolean deletePackage(int packageId) {
        boolean res = false;
        
        Connection conn=DB.getInstance().getConnection();
        String query="delete from Package where IdPckg=?";
        try (PreparedStatement stmt=conn.prepareStatement(query);){
            stmt.setInt(1, packageId);
            int tmp = stmt.executeUpdate();
            if(tmp == 1)
                res = true;
        } catch (SQLException ex) {
            Logger.getLogger(ba170390_PackageOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        return res;
    }

    @Override
    public boolean changeWeight(int packageId, BigDecimal newWeight) {
        boolean res = false;
        
        Connection conn=DB.getInstance().getConnection();
        String query="update Package\n" +
                    "set Weight=?\n" +
                    "where IdPckg=?";
        try (PreparedStatement stmt=conn.prepareStatement(query);){
            stmt.setBigDecimal(1, newWeight);
            stmt.setInt(2, packageId);
            int tmp = stmt.executeUpdate();
            if(tmp == 1)
                res = true;
        } catch (SQLException ex) {
            Logger.getLogger(ba170390_PackageOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        return res;
    }

    @Override
    public boolean changeType(int packageId, int newType) {
        boolean res = false;
        
        if(newType < 0 || newType > 2)
            return false;
        
        Connection conn=DB.getInstance().getConnection();
        String query="update Package\n" +
                    "set Type=?\n" +
                    "where IdPckg=?";
        try (PreparedStatement stmt=conn.prepareStatement(query);){
            stmt.setInt(1, newType);
            stmt.setInt(2, packageId);
            int tmp = stmt.executeUpdate();
            if(tmp == 1)
                res = true;
        } catch (SQLException ex) {
            Logger.getLogger(ba170390_PackageOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        return res;
    }

    @Override
    public Integer getDeliveryStatus(int packageId) {
        int res = -1;
        
        Connection conn=DB.getInstance().getConnection();
        String query="select Status from Package where IdPckg=?";
        try (PreparedStatement stmt=conn.prepareStatement(query);){    
            stmt.setInt(1, packageId);
            ResultSet rs = stmt.executeQuery();
            if(rs.next()){
                res = rs.getInt(1);
                return res;
            }else{
                return null;
            }
        } catch (SQLException ex) {
            Logger.getLogger(ba170390_PackageOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return res;
    }

    @Override
    public BigDecimal getPriceOfDelivery(int packageId) {
        BigDecimal res = null;
        
        Connection conn=DB.getInstance().getConnection();
        String query="select Price from Package where IdPckg=?";
        try (PreparedStatement stmt=conn.prepareStatement(query);){    
            stmt.setInt(1, packageId);
            ResultSet rs = stmt.executeQuery();
            if(rs.next()){
                res = rs.getBigDecimal(1);
                if(res.compareTo(BigDecimal.ZERO)==0 )
                    return null;
                else
                    return res;
            }else{
                return null;
            }
        } catch (SQLException ex) {
            Logger.getLogger(ba170390_PackageOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return res;
    }

    @Override
    public Date getAcceptanceTime(int packageId) {
        Date res = null;
        
        Connection conn=DB.getInstance().getConnection();
        String query="select AcceptanceTime from Package where IdPckg=?";
        try (PreparedStatement stmt=conn.prepareStatement(query);){    
            stmt.setInt(1, packageId);
            ResultSet rs = stmt.executeQuery();
            if(rs.next()){
                res = rs.getDate(1);
                return res;
            }else{
                return null;
            }
        } catch (SQLException ex) {
            Logger.getLogger(ba170390_PackageOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return res;
    }

    @Override
    public List<Integer> getAllPackagesWithSpecificType(int type) {
        List<Integer> list = new LinkedList<>();
        
        Connection conn=DB.getInstance().getConnection();
        String query="select IdPckg from Package where Type=?";
        try (PreparedStatement stmt=conn.prepareStatement(query);){
            stmt.setInt(1, type);
            ResultSet rs = stmt.executeQuery();
            while(rs.next()){
                list.add(rs.getInt(1));
            }
        } catch (SQLException ex) {
            Logger.getLogger(ba170390_PackageOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        //if(list.isEmpty())
        //    return null;
        return list;
    }

    @Override
    public List<Integer> getAllPackages() {
        List<Integer> list = new LinkedList<>();
        
        Connection conn=DB.getInstance().getConnection();
        String query="select IdPckg from Package";
        try (PreparedStatement stmt=conn.prepareStatement(query);){
            ResultSet rs = stmt.executeQuery();
            while(rs.next()){
                list.add(rs.getInt(1));
            }
        } catch (SQLException ex) {
            Logger.getLogger(ba170390_PackageOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        
       // if(list.isEmpty())
        //    return null;
        
        return list;
    }

    @Override
    public List<Integer> getDrive(String courierUsername) {
        List<Integer> list = new LinkedList<>();
        
        Connection conn=DB.getInstance().getConnection();
        String query="select IdPckg from Package where Status=2";
        try (PreparedStatement stmt=conn.prepareStatement(query);){
            ResultSet rs = stmt.executeQuery();
            while(rs.next()){
                list.add(rs.getInt(1));
            }
        } catch (SQLException ex) {
            Logger.getLogger(ba170390_PackageOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        
       // if(list.isEmpty())
        //    return null;
        
        return list;
    }
    
    public String getCourierCar(String courierUserName){
        String res = "";
        Connection conn=DB.getInstance().getConnection();
        String query="select LicencePlateNum from Courier where CourierUsername=?";
        try (PreparedStatement stmt=conn.prepareStatement(query);){
            stmt.setString(1, courierUserName);
            ResultSet rs = stmt.executeQuery();
            if(rs.next()){
                res = rs.getString("LicencePlateNum");
            }else{
                return "";
            }
        } catch (SQLException ex) {
            Logger.getLogger(ba170390_PackageOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return res;
    }
    
    public boolean startDeliveryForCourier(String courierUserName){
        boolean res = false;
        
        Connection conn=DB.getInstance().getConnection();
        
        //proveriti da li neko vec vozi kola, ako vozi ne moze da se startuje i vrati false
        String courierLicencePlate = getCourierCar(courierUserName);
        int taken = 0;
        String query="select count(CourierUsername) as Taken\n" +
                    "from Courier\n" +
                    "where Status=1 and LicencePlateNum=?";
        try (PreparedStatement stmt=conn.prepareStatement(query);){
            stmt.setString(1, courierLicencePlate);
            ResultSet rs = stmt.executeQuery();
            if(rs.next()){
                taken = rs.getInt("Taken");
            }else{
                return false; //greska, not gonna happen
            }
        } catch (SQLException ex) {
            Logger.getLogger(ba170390_PackageOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
              
        if(taken == 1){
            res = false;
            return res;
        }
        
        
        String query1="update Courier\n" +
                    "set Status=1\n" +
                    "where CourierUsername=?";
        try (PreparedStatement stmt1=conn.prepareStatement(query1);){
            stmt1.setString(1, courierUserName);
            stmt1.executeUpdate();     
            res = true;
            return res;
        } catch (SQLException ex) {
            Logger.getLogger(ba170390_PackageOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        lastVisited.put(courierUserName, new ba170390_Pair<Integer, Integer>(-1, -1));
        
        return res;
    }
    
   
    
 
    public int sortPckgsAndGetFirst(String courierUserName, int deliveryStarted){
        int res = -1;
             
        Connection conn=DB.getInstance().getConnection();
        
        if(deliveryStarted == 0){ //svim paketima za datog kurira stavimo da su pokupljeni (status=2), posto je voznja pocela           
            if(!startDeliveryForCourier(courierUserName)){ //pocni voznju, ako se vrati false nemoguce je poceti voznju trenutno
                return -1;
            }
            String query1="ALTER TABLE Package DISABLE TRIGGER [TR_TransportOffer_DeleteAllOffersForPackage]\n" +
                            "update Package\n" +
                            "set Status=2\n" +
                            "where CourierUsername=?\n" +
                            "ALTER TABLE Package ENABLE TRIGGER [TR_TransportOffer_DeleteAllOffersForPackage]";
  
            try (PreparedStatement stmt1=conn.prepareStatement(query1);){
                stmt1.setString(1, courierUserName);
                stmt1.executeUpdate();          
            } catch (SQLException ex) {
                Logger.getLogger(ba170390_PackageOperations.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        
        String query="select IdPckg from Package where Status=2 and CourierUsername=? order by AcceptanceTime asc";
        try (PreparedStatement stmt=conn.prepareStatement(query);){
            stmt.setString(1, courierUserName);
            ResultSet rs = stmt.executeQuery();
            if(rs.next()){
                res = rs.getInt(1);
            }else{
                return -1;
            }
        } catch (SQLException ex) {
            Logger.getLogger(ba170390_PackageOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return res;
    }
    
    public ba170390_Pair<Integer, Integer> getCordForDistritFromOrTo(int idPckg, String direction){
        ba170390_Pair<Integer, Integer> res = null;
        int idDistr = -1;
        String columName = "";
        int x = -1, y = -1;
        
        switch(direction){
            case "from": columName = "DistrictFrom";
                break;
            case "to": columName = "DistrictTo";
                break;
        }
        Connection conn=DB.getInstance().getConnection();
        String query="select DistrictFrom, DistrictTo from Package IdPckg=?";
        try (PreparedStatement stmt=conn.prepareStatement(query);){
            stmt.setInt(1, idPckg);
            ResultSet rs = stmt.executeQuery();
            if(rs.next()){
                idDistr = rs.getInt("columName");
            }else{
                return null;
            }
        } catch (SQLException ex) {
            Logger.getLogger(ba170390_PackageOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        //imamo idDistr, sad trazimo koordinate
        String query1="select xCord, yCord from District where IdDistr=?";
        try (PreparedStatement stmt1=conn.prepareStatement(query1);){
            stmt1.setInt(1, idDistr);
            ResultSet rs1 = stmt1.executeQuery();    
            if(rs1.next()){
                x = rs1.getInt("xCord");
                y = rs1.getInt("yCord");
                res = new ba170390_Pair<Integer, Integer>(x, y);
                return res;
            }
        } catch (SQLException ex) {
            Logger.getLogger(ba170390_PackageOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        return res;
    }
    
    public BigDecimal calculateProfit(String courierUserName, int idPckg){
         //cena isporuke - trosak voznje
        int xStart = -1, yStart = -1;
        int xMiddle = -1, yMiddle = -1;
        int xEnd = -1, yEnd = -1;
        BigDecimal profit = BigDecimal.ZERO;
        
        ba170390_Pair<Integer, Integer> xyStart = lastVisited.get(courierUserName);
        if((int)xyStart.getFirstParam() == -1 && (int)xyStart.getSecondParam() == -1){
            //jos nije bio nigde, polazna tacka je districtFrom, poslednja tacka je districtTo
            xStart = (int)getCordForDistritFromOrTo(idPckg, "from").getFirstParam();
            yStart = (int)getCordForDistritFromOrTo(idPckg, "from").getSecondParam();
            xEnd = (int)getCordForDistritFromOrTo(idPckg, "to").getFirstParam();
            yEnd = (int)getCordForDistritFromOrTo(idPckg, "to").getSecondParam();
        }else{
            //bio je negde, polazna tacka je tacka iz hashmap-e, ide do mesta FROM, potom ide do mesta TO
            xStart = (int)xyStart.getFirstParam();
            yStart = (int)xyStart.getSecondParam();
            xMiddle = (int)getCordForDistritFromOrTo(idPckg, "from").getFirstParam();
            yMiddle = (int)getCordForDistritFromOrTo(idPckg, "from").getSecondParam();
            xEnd = (int)getCordForDistritFromOrTo(idPckg, "to").getFirstParam();
            yEnd = (int)getCordForDistritFromOrTo(idPckg, "to").getSecondParam();          
        }
        //mesto TO je poslednje mesto koje je obisao
        lastVisited.put(courierUserName, new ba170390_Pair<Integer, Integer>(xEnd, yEnd));
        
        //racunamo distancu
        double distance = 0;
        if(xMiddle== -1 && yMiddle == -1){
            //ne postoji middle, idemo samo od start do end
            distance += euclidean(xStart, yStart, xEnd, yEnd);
        }else{
            distance += euclidean(xStart, yStart, xMiddle, yMiddle);
            distance += euclidean(xMiddle, yMiddle, xEnd, yEnd);
        }
        
        //treba nam cena paketa
        BigDecimal price = BigDecimal.ZERO;
        Connection conn=DB.getInstance().getConnection();
        String query="select Price from Package IdPckg=?";
        try (PreparedStatement stmt=conn.prepareStatement(query);){
            stmt.setInt(1, idPckg);
            ResultSet rs = stmt.executeQuery();
            if(rs.next()){
                price = rs.getBigDecimal("Price");
            }else{
                return null;
            }
        } catch (SQLException ex) {
            Logger.getLogger(ba170390_PackageOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        // treba nam tablica kola koje kurir vozi
        String carLicence = getCourierCar(courierUserName);
        
        //treba nam potrosnja goriva i tip goriva
        BigDecimal fuelConsumption = BigDecimal.ZERO;
        int fuelType = -1;
        String query1="select FuelConsumption, FuelType from Vehicle where LicencePlateNum=?";
        try (PreparedStatement stmt1=conn.prepareStatement(query1);){
            stmt1.setString(1, carLicence);
            ResultSet rs1 = stmt1.executeQuery();     
            if(rs1.next()){
                fuelConsumption = rs1.getBigDecimal("FuelConsumption");
                fuelType = rs1.getInt("FuelType"); 
            }else{
                return null;
            }
        } catch (SQLException ex) {
            Logger.getLogger(ba170390_PackageOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        //treba nam cena goriva
        int fuelPricePerL = 0;
        switch(fuelType){
            case 0: fuelPricePerL = 15;
                break;
            case 1: fuelPricePerL = 32;
                break;
            case 2: fuelPricePerL = 36;
                break;
        }
        
        BigDecimal fuelCharge = ((BigDecimal.valueOf(distance)).multiply(fuelConsumption)).multiply(BigDecimal.valueOf(fuelPricePerL));
        profit = price.subtract(fuelCharge);
         
        return profit;
    }
    
    
    public void updateCourier(String courierUserName, int idPckg){
        BigDecimal profit = BigDecimal.ZERO;       
        profit = calculateProfit(courierUserName, idPckg);  
        
        Connection conn=DB.getInstance().getConnection();
        String query="update Courier\n" +
                    "set NumOfDeliveredPckgs=NumOfDeliveredPckgs+1, Profit=Profit+?\n" +
                    "where CourierUsername=?";
        try (PreparedStatement stmt=conn.prepareStatement(query);){
            stmt.setBigDecimal(1, profit);
            stmt.setString(2, courierUserName);
            stmt.executeUpdate();          
        } catch (SQLException ex) {
            Logger.getLogger(ba170390_PackageOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    
    public void deliverPckg(int idPckg){
        Connection conn=DB.getInstance().getConnection();
        String query1="update Package\n" +
                    "set Status=3\n" +
                    "where IdPckg=?";
        try (PreparedStatement stmt1=conn.prepareStatement(query1);){
            stmt1.setInt(1, idPckg);
            stmt1.executeUpdate();          
        } catch (SQLException ex) {
            Logger.getLogger(ba170390_PackageOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
       

    public void stopDelivery(String courierUserName){
        Connection conn=DB.getInstance().getConnection();
        String query1="update Courier\n" +
                    "set Status=0\n" +
                    "where CourierUsername=?";
        try (PreparedStatement stmt1=conn.prepareStatement(query1);){
            stmt1.setString(1, courierUserName);
            stmt1.executeUpdate();          
        } catch (SQLException ex) {
            Logger.getLogger(ba170390_PackageOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    
    
    
    public int getCourierStatus(String courierUserName){
        int res = -1;
        Connection conn=DB.getInstance().getConnection();
        String query="select Status from Courier where CourierUsername=?";
        try (PreparedStatement stmt=conn.prepareStatement(query);){
            stmt.setString(1, courierUserName);
            ResultSet rs = stmt.executeQuery();
            if(rs.next()){
                res = rs.getInt("Status");
            }else{
                return -1;
            }
        } catch (SQLException ex) {
            Logger.getLogger(ba170390_PackageOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return res;
    }
    
    @Override
    public int driveNextPackage(String courierUserName) {
        final int NOTHING_TO_DRIVE = -1;
        final int OTHER = -2;
        
        int deliveryStarted = getCourierStatus(courierUserName);
               
        int idPckg = sortPckgsAndGetFirst(courierUserName, deliveryStarted);
        if(idPckg > 0){ //ima paketa za dostavljanje i kurir moze da ih dostavlja
            deliverPckg(idPckg);
            updateCourier(courierUserName, idPckg);
            return idPckg;
        }else if(idPckg == -1){ //nema paketa
            stopDelivery(courierUserName);
            return NOTHING_TO_DRIVE;
        }else if(idPckg == -2){ //kola zauzeta, kurir ne moze da vrsi dostavu
            return OTHER;
        }
        
        return OTHER;
    }
    
}
