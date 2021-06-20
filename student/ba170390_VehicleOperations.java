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
    public int deleteVehicles(String... strings) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<String> getAllVehichles() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean changeFuelType(String string, int i) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean changeConsumption(String string, BigDecimal bd) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
