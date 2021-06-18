/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package student;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import rs.etf.sab.operations.CityOperations;

/**
 *
 * @author aleks
 */
public class ba170390_CityOperations implements CityOperations {
    
    @Override
    public int insertCity(String name, String postalcode) {
        int id = -1;
        
        Connection conn=DB.getInstance().getConnection();
        String query="insert into City(Name, PostCode) values (?, ?)";
        try (PreparedStatement stmt=conn.prepareStatement(query, PreparedStatement.RETURN_GENERATED_KEYS);){
            stmt.setString(1, name);
            stmt.setString(2, postalcode);
            stmt.executeUpdate();
            ResultSet rs = stmt.getGeneratedKeys();
            if(rs.next()){
                id = rs.getInt(1);
            }
        } catch (SQLException ex) {
            //Logger.getLogger(ba170390_CityOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        return id;
    }

    @Override
    public int deleteCity(String... names) {
        int numDeleted = 0;
        if(names.length == 0)
            return numDeleted;
        
        Connection conn=DB.getInstance().getConnection();
        String query="delete from City where Name=?";
        try (PreparedStatement stmt=conn.prepareStatement(query);){
            for(int i = 0; i < names.length; i++){
                String name = names[i];
                stmt.setString(1, name);
                numDeleted += stmt.executeUpdate();
            }

        } catch (SQLException ex) {
            //Logger.getLogger(ba170390_CityOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return numDeleted;              
    }

    @Override
    public boolean deleteCity(int id) {
        boolean res = false;
        
        Connection conn=DB.getInstance().getConnection();
        String query="delete from City where IdCity=?";
        try (PreparedStatement stmt=conn.prepareStatement(query);){
            stmt.setInt(1, id);
            int tmp = stmt.executeUpdate();
            if(tmp == 1)
                res = true;
        } catch (SQLException ex) {
            //Logger.getLogger(ba170390_CityOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        return res;
    }

    @Override
    public List<Integer> getAllCities() {
        List<Integer> list = new LinkedList<>();
        
        Connection conn=DB.getInstance().getConnection();
        String query="select IdCity from City";
        try (PreparedStatement stmt=conn.prepareStatement(query);){            
            ResultSet rs = stmt.executeQuery();
            while(rs.next()){
                list.add(rs.getInt(1));
            }
        } catch (SQLException ex) {
           // Logger.getLogger(ba170390_CityOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return list;
    }
    
     
    
}
