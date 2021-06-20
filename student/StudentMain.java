package student;

import java.math.BigDecimal;
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

   /* public static void main(String[] args) {
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
    }*/
    
    public static void main(String[] args) {
        
        System.out.println("------------------------------------------------------------");
        ba170390_PackageOperations obj = new ba170390_PackageOperations();
       
        BigDecimal tmp = obj.getPriceOfDelivery(1);
        if( null == tmp){
            System.out.println("null je");
        }else 
                System.out.println(tmp);
                
        
        
        
    }
}
