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
    
    public static class ba170390_Pair<Integer, BigDecimal> implements Pair{
        private final int i;
        private final BigDecimal bd;
        
        public ba170390_Pair(int ii, BigDecimal bbdd){
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
        int id = -1;
        
        Connection conn=DB.getInstance().getConnection();
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
                price = calculatePrice(idPckg, pricePercentage);
                idPckg = rsGetOffer.getInt("IdPckg");
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
        
        if(list.isEmpty())
            return null;
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
        
        if(list.isEmpty())
            return null;
        
        return list;
    }

    @Override
    public List<Integer> getDrive(String courierUsername) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int driveNextPackage(String courierUserName) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
