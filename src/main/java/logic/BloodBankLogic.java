package logic;

import common.ValidationException;
import dal.BloodBankDAL;
import entity.BloodBank;
import java.time.LocalDate;
import java.util.Date;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.ObjIntConsumer;

/**
 *
 * 
 */
public class BloodBankLogic extends GenericLogic <BloodBank, BloodBankDAL> {

    public static final String OWNER_ID = "owner_id";
    public static final String PRIVATELY_OWNED = "privately_owned";
    public static final String ESTABLISHED = "established";
    public static final String NAME = "name";
    public static final String EMPLOYEE_COUNT = "employee_count";
    public static final String ID = "id";
    
    public BloodBankLogic() {
        super (new BloodBankDAL());
    }    
    
    @Override
    public List getAll() {
        return get( () -> dal().findAll() );
    }

    @Override
    public BloodBank getWithId(int id) {
        return get( () -> dal().findById( id ) );
    } 
    
    public BloodBank getBloodBankWithName(String name) {
        return get( () -> dal().findByName( name ) );
    }
    
    public List<BloodBank> getBloodBankWithPrivatelyOwned(boolean privatelyOwned) {
        return get( () -> dal().findByPrivatelyOwned( privatelyOwned ) );
    }
    
    public List<BloodBank> getBloodBankWithEstablished(Date established) {
        return get( () -> dal().findByEstablished( established ) );
    } 
    
    public BloodBank getBloodBanksWithOwner(int ownerId) {
        return get( () -> dal().findByOwner( ownerId ) );
    }
    
    public List<BloodBank> getBloodBanksWithEmployeeCount(int count) {
        return get( () -> dal().findByEmployeeCount( count ) );
    }
    
    @Override
    public BloodBank createEntity(Map<String, String[]> parameterMap) {
       //do not create any logic classes in this method.

//        return new AccountBuilder().SetData( parameterMap ).build();
        Objects.requireNonNull( parameterMap, "parameterMap cannot be null" );
        //same as if condition below
//        if (parameterMap == null) {
//            throw new NullPointerException("parameterMap cannot be null");
//        }

        //create a new Entity object
        BloodBank entity = new BloodBank();

        //ID is generated, so if it exists add it to the entity object
        //otherwise it does not matter as mysql will create an if for it.
        //the only time that we will have id is for update behaviour.
        if( parameterMap.containsKey( ID ) ){
            try {
                entity.setId( Integer.parseInt( parameterMap.get( ID )[ 0 ] ) );
            } catch( java.lang.NumberFormatException ex ) {
                throw new ValidationException( ex );
            }
        }

        //before using the values in the map, make sure to do error checking.
        //simple lambda to validate a string, this can also be place in another
        //method to be shared amoung all logic classes.
        ObjIntConsumer< String> validator = ( value, length ) -> {
            if( value == null || value.trim().isEmpty() || value.length() > length ){
                String error = "";
                if( value == null || value.trim().isEmpty() ){
                    error = "value cannot be null or empty: " + value;
                }
                if( value.length() > length ){
                    error = "string length is " + value.length() + " > " + length;
                }
                throw new ValidationException( error );
            }
        };

        //extract the date from map first.
        //everything in the parameterMap is string so it must first be
        //converted to appropriate type. have in mind that values are
        //stored in an array of String; almost always the value is at
        //index zero unless you have used duplicated key/name somewhere.
        
        String employeeCount = parameterMap.get(EMPLOYEE_COUNT)[0];        
        
        // ------------------------------------------------------
        String privatelyOwned = parameterMap.get(PRIVATELY_OWNED)[0];
        String name = parameterMap.get(NAME)[0];        
        
        String ownerID = null;
        if( ownerID != null && parameterMap.containsKey( OWNER_ID ) ){
            ownerID = parameterMap.get(OWNER_ID)[0];
            validator.accept( ownerID, 45 );
        }        

        //validate the data
        validator.accept( employeeCount, 45 );        
        validator.accept( privatelyOwned, 45 );
        validator.accept( name, 45 );
                
        LocalDate today = LocalDate.now();
        String day = today.toString();
        day = day.replace("-", "/");        
        
        //set values on entity
        entity.setEmployeeCount( Integer.parseInt(employeeCount) );
        // Date is deprecated, but the project is set up to use it
        entity.setEstablished( new Date(day));
        entity.setPrivatelyOwned( Boolean.parseBoolean(privatelyOwned) );
        entity.setName( name );
        
        //TODO
        //entity.setOwner(); // this needs PersonLogic.getWithID(ownerID)

        return entity;
    }
     
       

   @Override
    public List<String> getColumnNames() {
        return Arrays.asList( "ID", "EmployeeCount", "Name", "Established", 
                "PrivatelyOwned", "owner_id" );
    }
    
    @Override
    public List<String> getColumnCodes() {
        return Arrays.asList( ID, EMPLOYEE_COUNT, NAME, ESTABLISHED,
                PRIVATELY_OWNED, OWNER_ID );
    }

    @Override
    public List<?> extractDataAsList( BloodBank e ) {
        return Arrays.asList( e.getId(), e.getEmployeeCount(), e.getName(), e.getEstablished(),
                e.getPrivatelyOwned(), e.getOwner() ); // getOwner not OwnerID?
    }
}
