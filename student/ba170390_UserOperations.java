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
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import rs.etf.sab.operations.UserOperations;

/**
 *
 * @author aleks
 */
public class ba170390_UserOperations implements UserOperations {

    @Override
    public boolean insertUser(String userName, String firstName, String lastName, String password) {
        Pattern patternFirstLetterUppercase = Pattern.compile("^([A-Z][a-zA-Z]*)");
        Matcher matcher = patternFirstLetterUppercase.matcher(firstName);
        boolean matchFound = matcher.find();
        if(!matchFound) {        
          System.out.println("Firstname of user must begin with uppercase letter.");
          return false;
        }
        matcher = patternFirstLetterUppercase.matcher(lastName);
        matchFound = matcher.find();
        if(!matchFound) {        
          System.out.println("Lastname of user must begin with uppercase letter.");
          return false;
        }
        
        Pattern patternPssword = Pattern.compile("^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,}$");     
        matcher = patternPssword.matcher(password);
        matchFound = matcher.find();
        if(!matchFound) {        
          System.out.println("Password has to be longer than 8 characters. It should contain at least one number and one letter.");
          return false;
        }
        
        boolean res = false;
        
        Connection conn=DB.getInstance().getConnection();
        String query="insert into [User](Username, Firstname, Lastname, Password) values (?, ?, ?, ?)";
        try (PreparedStatement stmt=conn.prepareStatement(query);){
            stmt.setString(1, userName);
            stmt.setString(2, firstName);
            stmt.setString(3, lastName);
            stmt.setString(4, password);
            int tmp = stmt.executeUpdate();
            if(tmp == 1)
                res = true;
        } catch (SQLException ex) {
            //Logger.getLogger(ba170390_CityOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        return res;
    }

    @Override
    public int declareAdmin(String username) {
        final int SUCCESS = 0;
        final int ALREADY_ADMIN = 1;
        final int USER_DOESNT_EXISTS = 2;
        final int ERROR = -1;
        
        Connection conn=DB.getInstance().getConnection();
        String query="select Username from [User]\n" +
                     "where Username=?";
        try (PreparedStatement stmtCheckUser=conn.prepareStatement(query);){  
            stmtCheckUser.setString(1, username);
            ResultSet rsCheckUser = stmtCheckUser.executeQuery();
            if(rsCheckUser.next()){
                //check if already admin
                query="select AdminUsername from [Admin]\n" +
                      "where AdminUsername=?";
                PreparedStatement stmtCheckIfAdmin=conn.prepareStatement(query);
                stmtCheckIfAdmin.setString(1, username);
                ResultSet rsCheckIfAdmin = stmtCheckIfAdmin.executeQuery();
                if(rsCheckIfAdmin.next()){
                    stmtCheckIfAdmin.close();
                    return ALREADY_ADMIN;
                }else{
                    //declare admin
                    stmtCheckIfAdmin.close();
                    query = "insert into [Admin] values (?)";
                    PreparedStatement stmtDeclare=conn.prepareStatement(query);
                    stmtDeclare.setString(1, username);
                    int tmp = stmtDeclare.executeUpdate();
                    stmtDeclare.close();
                    if(tmp == 1){
                        return SUCCESS;
                    }
                }
                
               
            }else{
                return USER_DOESNT_EXISTS; 
            }
        } catch (SQLException ex) {
            Logger.getLogger(ba170390_UserOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        return ERROR;
    }

    @Override
    public Integer getSentPackages(String... userNames) {
        int sum = 0;
        
        if(userNames.length == 0)
            return null;
        
        Connection conn=DB.getInstance().getConnection();
        String query="select Username from [User]\n" +
                     "where Username=?";
        try (PreparedStatement stmtCheckUser=conn.prepareStatement(query);){  
            for(int i = 0; i < userNames.length; i++){
                stmtCheckUser.setString(1, userNames[i]);
                ResultSet rsCheckUser = stmtCheckUser.executeQuery();
                if(!rsCheckUser.next()){
                    return null;
                }
            }
       
        } catch (SQLException ex) {
           // Logger.getLogger(ba170390_CityOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        
        
        query="select NumOfSentPckgs\n" +
                    "from [User]\n" +
                    "where Username=?";
        try (PreparedStatement stmt=conn.prepareStatement(query);){
            for(int i = 0; i < userNames.length; i++){
                String username = userNames[i];
                stmt.setString(1, username);
                ResultSet rs = stmt.executeQuery();
                while (rs.next()){
                    sum += rs.getInt(1);
                }
            }

        } catch (SQLException ex) {
            //Logger.getLogger(ba170390_CityOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return sum;
    }

    @Override
    public int deleteUsers(String... userNames) {
        int numDeleted = 0;
        if(userNames.length == 0)
            return 0;
        
        Connection conn=DB.getInstance().getConnection();
        String query="delete\n" +
                    "from [User]\n" +
                    "where Username=?";
        try (PreparedStatement stmt=conn.prepareStatement(query);){
            for(int i = 0; i < userNames.length; i++){
                String username = userNames[i];
                stmt.setString(1, username);
                numDeleted += stmt.executeUpdate();
            }

        } catch (SQLException ex) {
            //Logger.getLogger(ba170390_CityOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        return numDeleted;
    }

    @Override
    public List<String> getAllUsers() {
        List<String> list = new LinkedList<>();
        
        Connection conn=DB.getInstance().getConnection();
        String query="select Username from [User]";
        try (PreparedStatement stmt=conn.prepareStatement(query);){            
            ResultSet rs = stmt.executeQuery();
            while(rs.next()){
                list.add(rs.getString(1));
            }
        } catch (SQLException ex) {
           // Logger.getLogger(ba170390_CityOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return list;
    }
    
}
