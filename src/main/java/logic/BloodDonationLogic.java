package logic;

import common.ValidationException;
import dal.BloodDonationDAL;
import entity.BloodBank;
import entity.BloodDonation;
import entity.BloodGroup;
import entity.RhesusFactor;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * BONUS: This class implements updateEntity to allow for the updating of BloodDonation entities
 * @author Shariar (Shawn) Emami, Matthew Ellero
 */
public class BloodDonationLogic extends GenericLogic<BloodDonation, BloodDonationDAL> {

    /**
     * create static final variables with proper name of each column. this way you will never manually type it again,
     * instead always refer to these variables.
     *
     * by using the same name as column id and HTML element names we can make our code simpler. this is not recommended
     * for proper production project.
     */
    public static final String BANK_ID = "bank_id";
    public static final String MILLILITERS = "milliliters";
    public static final String BLOOD_GROUP = "blood_group";
    public static final String RHESUS_FACTOR = "rhesus_factor";
    public static final String CREATED = "created";
    public static final String ID = "id";

    BloodDonationLogic() {
        super( new BloodDonationDAL() );
    }

    @Override
    public List<BloodDonation> getAll() {
        return get( () -> dal().findAll() );
    }

    @Override
    public BloodDonation getWithId( int id ) {
        return get( () -> dal().findById( id ) );
    }
    
    public List<BloodDonation> getBloodDonationsWithMilliliters(int milliliters) {
        return get(() -> dal().findByMilliliters(milliliters));
    }
    
    public List<BloodDonation> getBloodDonationsWithBloodGroup(BloodGroup bloodGroup) {
        return get(() -> dal().findByBloodGroup(bloodGroup));
    }
    
    public List<BloodDonation> getBloodDonationsWithCreated(Date created) {
        return get(() -> dal().findByCreated(created));
    }
    
    public List<BloodDonation> getBloodDonationsWithRhd(RhesusFactor rhd) {
        return get(() -> dal().findByRhd(rhd));
    }
    
    public List<BloodDonation> getBloodDonationsWithBloodBank(int bankId) {
        return get(() -> dal().findByBloodBank(bankId));
    }

    @Override
    public BloodDonation createEntity( Map<String, String[]> parameterMap ) {
        //do not create any logic classes in this method.

//        return new BloodDonationBuilder().SetData( parameterMap ).build();
        Objects.requireNonNull( parameterMap, "parameterMap cannot be null" );

        //create a new Entity object
        BloodDonation entity = new BloodDonation();
        
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

        // The act of retreiving these values will validate them
        BloodGroup bloodGroup = BloodGroup.valueOf(parameterMap.get(BLOOD_GROUP)[0]);
        Date created = new Date();
        try {
           created = convertStringToDate(parameterMap.get(CREATED)[0]);
        } catch (ValidationException e) {
            Logger.getLogger( BloodDonationLogic.class.getName() ).log( Level.SEVERE, null, e );
            created = convertStringToDate(new SimpleDateFormat( "yyyy-MM-dd kk:mm:ss" ).format(created));
        }
        
        int milliliters = Integer.parseInt(parameterMap.get(MILLILITERS)[0]);
        RhesusFactor rhd = RhesusFactor.getRhesusFactor(parameterMap.get(RHESUS_FACTOR)[0]); 

        entity.setBloodGroup(bloodGroup);
        entity.setCreated(created);
        entity.setMilliliters(milliliters);
        entity.setRhd(rhd);
        
        return entity;
    }
    
    /**
     * this method is only needed for bonus. this method needs to be overridden if the entity has dependencies. within
     * the method other logic's can be created to manipulate the dependencies. by default this method does the exact
     * same thing as createEntity method.
     *
     * @param parameterMap - new data with which to update an entity
     *
     * @return an updated entity with given requestData
     */
    @Override
    public BloodDonation updateEntity( Map<String, String[]> parameterMap ) {
        // Dependency logic
        BloodBankLogic bbLogic = LogicFactory.getFor("BloodBank");
        // Main logic
        BloodDonationLogic bdLogic = LogicFactory.getFor( "BloodDonation" );
        
        Integer id = Integer.parseInt(parameterMap.get(ID)[0]);
        Integer newMilliliters = Integer.parseInt(parameterMap.get(MILLILITERS)[0]);
        BloodGroup newBloodGroup = BloodGroup.valueOf(parameterMap.get(BLOOD_GROUP)[0]);
        RhesusFactor newRHD = RhesusFactor.getRhesusFactor(parameterMap.get(RHESUS_FACTOR)[0]);
        
        
        //getwithid(id) get the current entity from db
        BloodDonation originalBD = bdLogic.getWithId(id);
        //check data from map against entity and udpate it
        if (originalBD.getMilliliters() != newMilliliters) {
            originalBD.setMilliliters(newMilliliters);
        }
        if (originalBD.getBloodGroup() != newBloodGroup) {
            originalBD.setBloodGroup(newBloodGroup);
        }
        if (originalBD.getRhd() != newRHD) {
            originalBD.setRhd(newRHD);
        }
        //check if depdendecy has changed, if so update it using depedency logic

        try {
            int bankId = Integer.parseInt(parameterMap.get(BloodDonationLogic.BANK_ID)[0]);
            BloodBank bloodBank = bbLogic.getWithId(bankId);
            if (bloodBank == null) {
                throw new IllegalArgumentException("Bank ID not found");
            }
            
            if (originalBD.getBloodBank() != bloodBank) {
                originalBD.setBloodBank(bloodBank);
                bdLogic.update( originalBD );
            }
            
        } catch( NumberFormatException ex ) {
            Logger.getLogger( BloodDonationLogic.class.getName() ).log( Level.SEVERE, null, ex );
        }
        
        return originalBD;
    }

    /**
     * this method is used to send a list of all names to be used form table column headers. by having all names in one
     * location there is less chance of mistakes.
     *
     * this list must be in the same order as getColumnCodes and extractDataAsList
     *
     * @return list of all column names to be displayed.
     */
    @Override
    public List<String> getColumnNames() {
        return Arrays.asList( "ID", "Blood Bank ID", "Milliliters",
                              "Blood Group", "Rhesus Factor",  "Created" );
    }

    /**
     * this method returns a list of column names that match the official column names in the db. by having all names in
     * one location there is less chance of mistakes.
     *
     * this list must be in the same order as getColumnNames and extractDataAsList
     *
     * @return list of all column names in DB.
     */
    @Override
    public List<String> getColumnCodes() {
        return Arrays.asList( ID, BANK_ID, MILLILITERS,
                              BLOOD_GROUP, RHESUS_FACTOR, CREATED );
    }

    /**
     * return the list of values of all columns (variables) in given entity.
     *
     * this list must be in the same order as getColumnNames and getColumnCodes
     *
     * @param e - given Entity to extract data from.
     *
     * @return list of extracted values
     */
    @Override
    public List<?> extractDataAsList( BloodDonation e ) {
        return Arrays.asList( e.getId(), e.getBloodBank().getId(), e.getMilliliters(),
                              e.getBloodGroup(), e.getRhd(), e.getCreated() );
    }
}
