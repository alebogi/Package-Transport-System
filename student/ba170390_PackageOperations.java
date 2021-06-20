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
        private int i;
        private BigDecimal bd;
        
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

    @Override
    public boolean acceptAnOffer(int offerId) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
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
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<Integer> getAllPackagesWithSpecificType(int type) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<Integer> getAllPackages() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
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
