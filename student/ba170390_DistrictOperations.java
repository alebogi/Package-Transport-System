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
import rs.etf.sab.operations.DistrictOperations;

/**
 *
 * @author aleks
 */
public class ba170390_DistrictOperations implements DistrictOperations {

    @Override
    public int insertDistrict(String name, int cityId, int xCord, int yCord) {
        int id = -1;
        
        Connection conn=DB.getInstance().getConnection();
        String query="insert into District(Name, xCord, yCord, IdCity) values (?, ?, ?, ?)";
        try (PreparedStatement stmt=conn.prepareStatement(query, PreparedStatement.RETURN_GENERATED_KEYS);){
            stmt.setString(1, name);
            stmt.setInt(2, xCord);
            stmt.setInt(3, yCord);
            stmt.setInt(4, cityId);
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
    public int deleteDistricts(String... names) {
        int numDeleted = 0;
        if(names.length == 0)
            return numDeleted;
        
        Connection conn=DB.getInstance().getConnection();
        String query="delete from District where Name=?";
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
    public boolean deleteDistrict(int idDistrict) {
        boolean res = false;
        
        Connection conn=DB.getInstance().getConnection();
        String query="delete from District where IdDistr=?";
        try (PreparedStatement stmt=conn.prepareStatement(query);){
            stmt.setInt(1, idDistrict);
            int tmp = stmt.executeUpdate();
            if(tmp == 1)
                res = true;
        } catch (SQLException ex) {
            //Logger.getLogger(ba170390_CityOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        return res;
    }

    @Override
    public int deleteAllDistrictsFromCity(String nameOfTheCity) {
        int numDeleted = 0;
        
        Connection conn=DB.getInstance().getConnection();
        String query="delete\n" +
                    "from District\n" +
                    "where IdCity=(\n" +
                    "	select IdCity\n" +
                    "	from City\n" +
                    "	where Name=?\n" +
                    ")";
        try (PreparedStatement stmt=conn.prepareStatement(query);){
            stmt.setString(1, nameOfTheCity);
            numDeleted += stmt.executeUpdate();

        } catch (SQLException ex) {
            //Logger.getLogger(ba170390_CityOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return numDeleted;
    }

    @Override
    public List<Integer> getAllDistrictsFromCity(int idCity) {
        List<Integer> list = new LinkedList<>();
        
        Connection conn=DB.getInstance().getConnection();
        String query="select IdDistr from District where IdCity=?";
        try (PreparedStatement stmt=conn.prepareStatement(query);){  
            stmt.setInt(1, idCity);
            ResultSet rs = stmt.executeQuery();
            while(rs.next()){
                list.add(rs.getInt(1));
            }
        } catch (SQLException ex) {
           // Logger.getLogger(ba170390_CityOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return list;
    }

    @Override
    public List<Integer> getAllDistricts() {
        List<Integer> list = new LinkedList<>();
        
        Connection conn=DB.getInstance().getConnection();
        String query="select IdDistr from District";
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
