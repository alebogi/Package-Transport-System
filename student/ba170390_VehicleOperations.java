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
import rs.etf.sab.operations.VehicleOperations;

/**
 *
 * @author aleks
 */
public class ba170390_VehicleOperations implements VehicleOperations {

    @Override
    public boolean insertVehicle(String licencePlateNumber, int fuelType, BigDecimal fuelConsumtion) {
        boolean res = false;
         
        if(fuelType<0 || fuelType>2)
            return false;
        
        Connection conn=DB.getInstance().getConnection();
        String query="insert into Vehicle(LicencePlateNum, FuelType, FuelConsumption) values (?, ?, ?)";
        try (PreparedStatement stmt=conn.prepareStatement(query);){
            stmt.setString(1, licencePlateNumber);
            stmt.setInt(2, fuelType);
            stmt.setBigDecimal(3, fuelConsumtion);
            int tmp = stmt.executeUpdate();
            if(tmp == 1)
                res = true;
        } catch (SQLException ex) {
            //Logger.getLogger(ba170390_VehicleOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        return res;
    }

    @Override
    public int deleteVehicles(String... licencePlateNumbers) {
        int numDeleted = 0;
        if(licencePlateNumbers.length == 0)
            return numDeleted;
        
        Connection conn=DB.getInstance().getConnection();
        String query="delete from Vehicle where LicencePlateNum=?";
        try (PreparedStatement stmt=conn.prepareStatement(query);){
            for(int i = 0; i < licencePlateNumbers.length; i++){
                String licenseNum = licencePlateNumbers[i];
                stmt.setString(1, licenseNum);
                numDeleted += stmt.executeUpdate();
            }

        } catch (SQLException ex) {
            //Logger.getLogger(ba170390_VehicleOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return numDeleted; 
    }

    @Override
    public List<String> getAllVehichles() {
        List<String> list = new LinkedList<>();
        
        Connection conn=DB.getInstance().getConnection();
        String query="select LicencePlateNum from Vehicle";
        try (PreparedStatement stmt=conn.prepareStatement(query);){            
            ResultSet rs = stmt.executeQuery();
            while(rs.next()){
                list.add(rs.getString(1));
            }
        } catch (SQLException ex) {
           // Logger.getLogger(ba170390_VehicleOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return list;
    }

    @Override
    public boolean changeFuelType(String licensePlateNumber, int fuelType) {
        boolean res = false;
         
        if(fuelType<0 || fuelType>2)
            return false;
        
        Connection conn=DB.getInstance().getConnection();
        String query="update Vehicle\n" +
                    "set FuelType=?\n" +
                    "where LicencePlateNum=?";
        try (PreparedStatement stmt=conn.prepareStatement(query);){
            stmt.setInt(1, fuelType);
            stmt.setString(2, licensePlateNumber);         
            int tmp = stmt.executeUpdate();
            if(tmp == 1)
                res = true;
        } catch (SQLException ex) {
            //Logger.getLogger(ba170390_VehicleOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        return res;
    }

    @Override
    public boolean changeConsumption(String licensePlateNumber, BigDecimal fuelConsumption) {
        boolean res = false;
                 
        Connection conn=DB.getInstance().getConnection();
        String query="update Vehicle\n" +
                    "set FuelConsumption=?\n" +
                    "where LicencePlateNum=?";
        try (PreparedStatement stmt=conn.prepareStatement(query);){
            stmt.setBigDecimal(1, fuelConsumption);
            stmt.setString(2, licensePlateNumber);         
            int tmp = stmt.executeUpdate();
            if(tmp == 1)
                res = true;
        } catch (SQLException ex) {
            //Logger.getLogger(ba170390_VehicleOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        return res;
    }
    
}
