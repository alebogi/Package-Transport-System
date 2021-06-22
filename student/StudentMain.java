package student;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.List;
import rs.etf.sab.*;
import rs.etf.sab.operations.CityOperations;
import rs.etf.sab.operations.CourierOperations;
import rs.etf.sab.operations.CourierRequestOperation;
import rs.etf.sab.operations.DistrictOperations;
import rs.etf.sab.operations.GeneralOperations;
import rs.etf.sab.operations.PackageOperations;
import rs.etf.sab.operations.UserOperations;
import rs.etf.sab.operations.VehicleOperations;
import rs.etf.sab.tests.TestHandler;
import rs.etf.sab.tests.TestRunner;



public class StudentMain {

    public static void main(String[] args) {
        CityOperations cityOperations = new ba170390_CityOperations(); 
        DistrictOperations districtOperations = new ba170390_DistrictOperations(); 
        CourierOperations courierOperations = new ba170390_CourierOperations(); 
        CourierRequestOperation courierRequestOperation = new ba170390_CourierRequestOperation();
        GeneralOperations generalOperations = new ba170390_GeneralOperations();
        UserOperations userOperations = new ba170390_UserOperations();
        VehicleOperations vehicleOperations = new ba170390_VehicleOperations();
        PackageOperations packageOperations = new ba170390_PackageOperations();

        TestHandler.createInstance(
                cityOperations,
                courierOperations,
                courierRequestOperation,
                districtOperations,
                generalOperations,
                userOperations,
                vehicleOperations,
                packageOperations);

        TestRunner.runTests();
    }
    
    
   /* 
   public static void main(String[] args) {
        
        System.out.println("------------------------------------------------------------");
        ba170390_UserOperations u = new ba170390_UserOperations();
        ba170390_VehicleOperations v = new ba170390_VehicleOperations();
        ba170390_CourierRequestOperation cr = new ba170390_CourierRequestOperation();
       ba170390_CityOperations c = new ba170390_CityOperations();
       ba170390_DistrictOperations d = new ba170390_DistrictOperations();
       ba170390_PackageOperations p = new ba170390_PackageOperations();
 
                
      
    }*/
}
