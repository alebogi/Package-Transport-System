/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package student;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import rs.etf.sab.operations.CourierOperations;

/**
 *
 * @author aleks
 */
public class ba170390_CourierOperations implements CourierOperations {

    @Override
    public boolean insertCourier(String courierUserName, String licencePlateNumber) {
        boolean res = false;
               
        Connection conn=DB.getInstance().getConnection();
        String query="insert into Courier(CourierUsername, LicencePlateNum) values (?, ?)";
        try (PreparedStatement stmt=conn.prepareStatement(query);){
            stmt.setString(1, courierUserName);
            stmt.setString(2, licencePlateNumber);
            int tmp = stmt.executeUpdate();
            if(tmp == 1)
                res = true;
        } catch (SQLException ex) {
            Logger.getLogger(ba170390_CourierOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        return res;
    }

    @Override
    public boolean deleteCourier(String courierUserName) {
        boolean res = false;
        
        Connection conn=DB.getInstance().getConnection();
        String query="delete from Courier where CourierUsername=?";
        try (PreparedStatement stmt=conn.prepareStatement(query);){
            stmt.setString(1, courierUserName);
            int tmp = stmt.executeUpdate();
            if(tmp == 1)
                res = true;
        } catch (SQLException ex) {
            Logger.getLogger(ba170390_CourierOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        return res;
    }

    @Override
    public List<String> getCouriersWithStatus(int statusOfCourier) {
        List<String> list = new LinkedList<>();
        
        Connection conn=DB.getInstance().getConnection();
        String query="select CourierUsername from Courier where Status=?";
        try (PreparedStatement stmt=conn.prepareStatement(query);){ 
            stmt.setInt(1, statusOfCourier);
            ResultSet rs = stmt.executeQuery();
            while(rs.next()){
                list.add(rs.getString(1));
            }
        } catch (SQLException ex) {
            Logger.getLogger(ba170390_CourierOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return list;
    }

    @Override
    public List<String> getAllCouriers() {
        List<String> list = new LinkedList<>();
        
        Connection conn=DB.getInstance().getConnection();
        String query="select CourierUsername from Courier order by Profit DESC";
        try (PreparedStatement stmt=conn.prepareStatement(query);){            
            ResultSet rs = stmt.executeQuery();
            while(rs.next()){
                list.add(rs.getString(1));
            }
        } catch (SQLException ex) {
            Logger.getLogger(ba170390_CourierOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return list;
    }

    @Override
    public BigDecimal getAverageCourierProfit(int numberOfDeliveries) {
        BigDecimal sum = BigDecimal.ZERO;
        BigDecimal profit = BigDecimal.ZERO;
        int cnt = 0;
        BigDecimal avg = BigDecimal.ZERO;
        
        Connection conn=DB.getInstance().getConnection();
        String query="select NumOfDeliveredPckgs, Profit from Courier";
        try (PreparedStatement stmt=conn.prepareStatement(query);){ 
            ResultSet rs = stmt.executeQuery();
            while(rs.next()){
                int tmp = rs.getInt("NumOfDeliveredPckgs");
                profit = rs.getBigDecimal("Profit");
                if(tmp >= numberOfDeliveries){
                    sum = sum.add(profit);
                    cnt++;
                }               
            }
            avg = sum.divide(BigDecimal.valueOf(cnt));
            return avg;
        } catch (SQLException ex) {
            Logger.getLogger(ba170390_CourierOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return avg;
    }
    
}
