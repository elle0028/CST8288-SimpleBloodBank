package logic;

import common.ValidationException;
import dal.BloodBankDAL;
import entity.BloodBank;
import entity.Person;
import java.util.Date;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.ObjIntConsumer;

/**
 *  Contains the logic for creating an entity and interfacing with the DAL
 *  @author Andrew O'Hara
 */
public class BloodBankLogic extends GenericLogic <BloodBank, BloodBankDAL> {
    // database column names
    public static final String OWNER_ID = "owner_id";
    public static final String PRIVATELY_OWNED = "privately_owned";
    public static final String ESTABLISHED = "established";
    public static final String NAME = "name";
    public static final String EMPLOYEE_COUNT = "employee_count";
    public static final String ID = "id";
    
    // construct logic and attach BloodBankDAL
    public BloodBankLogic() {
        super (new BloodBankDAL());
    }    
    
    /**
     * getAll retrieves all BloodBanks from the database
     * @return List<BloodBank> from the database
     */
    @Override
    public List getAll() {
        return get( () -> dal().findAll() );
    }
    
    /**
     * getWithID retrieves a BloodBank from the database based on id
     * @param id  id of the BloodBank to retrieve
     * @return BloodBank with id or null
     */
    @Override
    public BloodBank getWithId(int id) {
        return get( () -> dal().findById( id ) );
    } 
    
    /**
     * getBloodBankWithName retrieves a BloodBank from the database based on it's name
     * @param name  name of the BloodBank to retrieve
     * @return BloodBank with name or null
     */
    public BloodBank getBloodBankWithName(String name) {
        return get( () -> dal().findByName( name ) );
    }
    
    /**
     * getBloodBankWithPrivatelyOwned retrieves BloodBanks from the database 
     * based on whether or not they are privately owned or not.
     * @param privatelyOwned boolean, true if privately owned
     * @return List<BloodBank> BloodBanks that share privatelyOwned status
     */
    public List<BloodBank> getBloodBankWithPrivatelyOwned(boolean privatelyOwned) {
        return get( () -> dal().findByPrivatelyOwned( privatelyOwned ) );
    }
    
    /**
     * getBloodBankWithEstablished retrieves BloodBanks from the database 
     * based on the date they were established
     * @param established
     * @return 
     */    
    public List<BloodBank> getBloodBankWithEstablished(Date established) {
        return get( () -> dal().findByEstablished( established ) );
    } 
    
    /**
     * getBloodBankWithOwner retrieves a BloodBank from the database owned by
     * a particular person
     * @param ownerId the id assigned to the Person who owns this BloodBank
     * @return BloodBank owned by Person with ownerId
     */
    public BloodBank getBloodBankWithOwner(int ownerId) {
        return get( () -> dal().findByOwner( ownerId ) );
    }
    
    /**
     * getBloodBanksWithEmployeeCount retrieves BloodBanks from the database
     * based on number of people who work there
     * @param count the number of employees to look for when selecting BloodBanks
     * @return List<BloodBank> BloodBanks with number of employees specified by count
     */
    public List<BloodBank> getBloodBanksWithEmployeeCount(int count) {
        return get( () -> dal().findByEmployeeCount( count ) );
    }    
    
    /** Search for bloodbanks based on search term
     * 
     * @param search  string containing term to search for
     * @return All bloodbanks that match the search term
     */
    @Override
    public List<BloodBank> search( String search ) {
        return get( () -> dal().findContaining( search ) );
    }    
    
    /**
     *  Update the BloodBank through the JSP
     * @param parameterMap  updated BloodBank parameters
     * @return  the updated BloodBank
     */
    @Override
    public BloodBank updateEntity(Map<String, String[]> parameterMap) {
         PersonLogic pLogic = LogicFactory.getFor("Person");
         
         //get the bloodbank we are intended to update
         int originalID = Integer.parseInt(parameterMap.get(ID)[0]);
         BloodBank bbToUpdate = getWithId(originalID);
         
         // update the name if it has changed
         String updatedName = parameterMap.get(NAME)[0];
         if(!updatedName.equals(bbToUpdate.getName()))
            bbToUpdate.setName(updatedName);         
         
        int newEmployeeCount = Integer.parseInt(parameterMap.get(EMPLOYEE_COUNT)[0]);
        if (newEmployeeCount != bbToUpdate.getEmployeeCount())
            bbToUpdate.setEmployeeCount(newEmployeeCount);         
         
         // if parameterMap established is invalid date, it should assume todays date
         Date newEstablished = convertStringToDate(parameterMap.get(ESTABLISHED)[0]);
         if (newEstablished.compareTo(bbToUpdate.getEstablished()) != 0)
            bbToUpdate.setEstablished(newEstablished);
         
         boolean wasPrivate = bbToUpdate.getPrivatelyOwned();
         int oldOwnerId = 0;
         if (wasPrivate && bbToUpdate.getOwner() != null)
             oldOwnerId = bbToUpdate.getOwner().getId();
         
         boolean isPrivate = Boolean.parseBoolean(parameterMap.get(PRIVATELY_OWNED)[0]);
         bbToUpdate.setPrivatelyOwned(isPrivate);
         
         // check and update dependency         
         // if not privately owned we simply set the owner to null
         if (!isPrivate) {
             bbToUpdate.setOwner(null);
         }
         else {
             // the new ownerId
             int newOwnerId = Integer.parseInt(parameterMap.get(OWNER_ID)[0]);
             if (oldOwnerId != newOwnerId) {
                // ownership has changed, set new owner
                Person owner = pLogic.getWithId(newOwnerId);
                if (owner != null) {
                    // we got a valid new owner from new ID
                    bbToUpdate.setOwner(owner);
                }
            }             
        }
        
        return bbToUpdate;        
    }   
    
    /**
     * createEntity Creates a BloodBank based on parameterMap. Validates parameters 
     * and returns created entity or throws ValidationException
     * @param parameterMap
     * @return 
     */
    @Override
    public BloodBank createEntity(Map<String, String[]> parameterMap) {
        // do not create any logic classes in this method.
        
        Objects.requireNonNull( parameterMap, "parameterMap cannot be null" );      

        //create a new Entity object
        BloodBank entity = new BloodBank();

        //ID is generated, so if it exists add it to the entity object
        //otherwise it does not matter as mysql will create an if for it.
        //the only time that we will have id is for update behaviour.
        if( parameterMap.containsKey( ID ) ){
            try {
                String idString = parameterMap.get( ID )[ 0 ];                
                entity.setId( Integer.parseInt( idString ) );
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
        
        String employeeCount = parameterMap.get(EMPLOYEE_COUNT)[0];        
        
        // ------------------------------------------------------
        String privatelyOwned = parameterMap.get(PRIVATELY_OWNED)[0];
        String name = parameterMap.get(NAME)[0];  
        String establishedStr = parameterMap.get(ESTABLISHED)[0];
            
        Date established = convertStringToDate(establishedStr);//        

        //validate the data       
        validator.accept( employeeCount, 45 );        
        validator.accept( privatelyOwned, 45 );
        validator.accept( name, 45 );
        validator.accept( establishedStr, 45);       
        
        //set values on entity
        entity.setEmployeeCount( Integer.parseInt(employeeCount) );
        // Date is deprecated, but the project is set up to use it
        entity.setEstablished( established );
        entity.setPrivatelyOwned( Boolean.parseBoolean(privatelyOwned) );
        entity.setName( name );           

        return entity;
    }       

    /**
     * getColumnNames 
     * @return List<String> Database column names as a list of strings
     */
   @Override
    public List<String> getColumnNames() {
        return Arrays.asList( "ID", "EmployeeCount", "Name", "Established", 
                "PrivatelyOwned", "owner_id" );
    }
    
    /**
     * getColumnCodes
     * @return List<String> Database column name constants as a list of strings
     */
    @Override
    public List<String> getColumnCodes() {
        return Arrays.asList( ID, EMPLOYEE_COUNT, NAME, ESTABLISHED,
                PRIVATELY_OWNED, OWNER_ID );
    }

    /**
     * extractDataAsList packs BloodBank data into a list
     * @param e BloodBank object to extract data from
     * @return List<?> a list of all a BloodBank objects values
     */
    @Override
    public List<?> extractDataAsList( BloodBank e ) {
        // we must extract the id from the owner for display
        int ownerId = e.getOwner() == null ? 0 : e.getOwner().getId();
        return Arrays.asList( e.getId(), e.getEmployeeCount(), e.getName(), e.getEstablished(),
                e.getPrivatelyOwned(), ownerId ); // getOwner not OwnerID?
    }
}