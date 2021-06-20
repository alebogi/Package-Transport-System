/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package student;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import rs.etf.sab.operations.CourierRequestOperation;

/**
 *
 * @author aleks
 */
public class ba170390_CourierRequestOperation implements CourierRequestOperation {

    @Override
    public boolean insertCourierRequest(String userName, String licencePlateNumber) {
        boolean res = false;
               
        Connection conn=DB.getInstance().getConnection();
        String query="insert into CourierRequest(Username, LicencePlateNum) values (?, ?)";
        try (PreparedStatement stmt=conn.prepareStatement(query);){
            stmt.setString(1, userName);
            stmt.setString(2, licencePlateNumber);
            int tmp = stmt.executeUpdate();
            if(tmp == 1)
                res = true;
        } catch (SQLException ex) {
            Logger.getLogger(ba170390_CourierRequestOperation.class.getName()).log(Level.SEVERE, null, ex);
        }
        return res;
    }

    @Override
    public boolean deleteCourierRequest(String userName) {
        boolean res = false;
        
        Connection conn=DB.getInstance().getConnection();
        String query="delete from CourierRequest where Username=?";
        try (PreparedStatement stmt=conn.prepareStatement(query);){
            stmt.setString(1, userName);
            int tmp = stmt.executeUpdate();
            if(tmp == 1)
                res = true;
        } catch (SQLException ex) {
            Logger.getLogger(ba170390_CourierRequestOperation.class.getName()).log(Level.SEVERE, null, ex);
        }
        return res;
    }

    @Override
    public boolean changeVehicleInCourierRequest(String userName, String licencePlateNumber) {
        boolean res = false;
        
        Connection conn=DB.getInstance().getConnection();
        String query="update CourierRequest\n" +
                    "set LicencePlateNum=?\n" +
                    "where Username=?";
        try (PreparedStatement stmt=conn.prepareStatement(query);){
            stmt.setString(1, licencePlateNumber);
            stmt.setString(2, userName);
            int tmp = stmt.executeUpdate();
            if(tmp == 1)
                res = true;
        } catch (SQLException ex) {
            Logger.getLogger(ba170390_CourierRequestOperation.class.getName()).log(Level.SEVERE, null, ex);
        }
        return res;
    }

    @Override
    public List<String> getAllCourierRequests() {
        List<String> list = new LinkedList<>();
        
        Connection conn=DB.getInstance().getConnection();
        String query="select Username from CourierRequest";
        try (PreparedStatement stmt=conn.prepareStatement(query);){            
            ResultSet rs = stmt.executeQuery();
            while(rs.next()){
                list.add(rs.getString(1));
            }
        } catch (SQLException ex) {
            Logger.getLogger(ba170390_CourierRequestOperation.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return list;
    }

    @Override
    public boolean grantRequest(String username) {
        //call procedure
        boolean res = false;
        
        Connection conn=DB.getInstance().getConnection();
        String query="exec spGrantRequest ?";
        try (CallableStatement stmt=conn.prepareCall(query);){
            stmt.setString(1, username);
            int tmp = stmt.executeUpdate();
            if(tmp == 1)
                res = true;
        } catch (SQLException ex) {
            Logger.getLogger(ba170390_CourierRequestOperation.class.getName()).log(Level.SEVERE, null, ex);
        }
        return res;
    }
    
}
