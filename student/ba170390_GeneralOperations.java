/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package student;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import rs.etf.sab.operations.GeneralOperations;

/**
 *
 * @author aleks
 */
public class ba170390_GeneralOperations implements GeneralOperations {

    @Override
    public void eraseAll() {
        Connection conn=DB.getInstance().getConnection();
        String [] names = {"Admin", "City", "Courier", "CourierRequest", "District", "Package", "TransportOffer", "User", "Vehicle"}; 
        String query="delete from ?";
        try (PreparedStatement stmt=conn.prepareStatement(query);){
            for(int i = 0; i < names.length; i++){
                String name = names[i];
                stmt.setString(1, name);
                stmt.executeUpdate();
            }

        } catch (SQLException ex) {
            //Logger.getLogger(ba170390_CityOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}
