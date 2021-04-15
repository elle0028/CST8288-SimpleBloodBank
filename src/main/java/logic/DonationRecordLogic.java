package logic;

import common.ValidationException;
import dal.DonationRecordDAL;
import entity.BloodDonation;
import entity.DonationRecord;
import entity.Person;

import java.util.Date;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.ObjIntConsumer;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author aksha
 */
public class DonationRecordLogic extends GenericLogic<DonationRecord, DonationRecordDAL> {
    
    public static final String ID = "recordId";
    public static final String PERSON_ID = "personId";
    public static final String DONATION_ID = "donationId";
    public static final String TESTED = "tested";
    public static final String ADMINISTRATOR = "administrator";
    public static final String HOSPITAL = "hospital";
    public static final String CREATED = "created";

       public DonationRecordLogic() {
        super(new DonationRecordDAL() );
    }
    
        @Override
    public List<DonationRecord> getAll() {
        return get( () -> dal().findAll() );
    }
   
        @Override
    public DonationRecord getWithId(int id) {
        return get( () -> dal().findById( id));
    }

   public List<DonationRecord> getDonationRecordWithTested(boolean tested) {
        return get(() -> dal().findByTested(tested));
    }
    
    /**
     *
     * @param administrator
     * @return
     */
    public List<DonationRecord> getDonationRecordWithAdministrator(String administrator) {
        return get(() -> dal().findByAdministrator(administrator));
    } 
    public List<DonationRecord> getDonationRecordWithHospital (String hospital) {
        return get(() -> dal().findByHospital(hospital));
    } 
    
    public List<DonationRecord> getDonationRecordWithCreated(Date created) {
        return get(() -> dal().findCreated(created));
    }
    
    public List<DonationRecord> getDonationRecordWithPersonID(int person_id) {
        return get(() -> dal().findByPersonId(person_id));
    } 
    public List<DonationRecord> getDonationRecordWithDonationID (int donation_id) {
        return get(() -> dal().findByDonationId(donation_id));
    } 
    
    @Override
    public List<String> getColumnNames() {
        return Arrays.asList("ID" ,"PersonID", "DonationID", "Tested", "Administrator","Hospital","Created" );
    }
     @Override
    public List<String> getColumnCodes() {
        return Arrays.asList(ID, PERSON_ID, DONATION_ID, TESTED, ADMINISTRATOR, HOSPITAL , CREATED);
    }
    @Override
    public List<?> extractDataAsList(DonationRecord e) {
        Integer personId = null;
        Integer bloodDonationId = null;
        
        if(e.getPerson() != null){
            personId = e.getPerson().getId();
        }
        
        if(e.getBloodDonation() != null){
            bloodDonationId= e.getBloodDonation().getId();
        }
        
        return Arrays.asList(e.getId(), personId, bloodDonationId, e.getTested(), e.getAdministrator(), e.getHospital(), e.getCreated());
    }
    @Override
    public DonationRecord createEntity(Map<String, String[]> parameterMap) {

        Objects.requireNonNull( parameterMap, "parameterMap cannot be null" );
 
        //create a new Entity object
        DonationRecord entity = new DonationRecord();

        //before using the values in the map, make sure to do error checking.
        //simple lambda to validate a string, this can also be place in another
        //method to be shared amoung all logic classes.
        // this is where all the input is checked and then we check to see if it has the same values in the table already
        ObjIntConsumer< String> validator = ( value, length ) -> {
            if( value == null || value.trim().isEmpty() || value.length() > length ){
                // GIVING AN ERROR WHEN I LEAVE THE STRING EMPTY
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
        
        if( parameterMap.containsKey( ID ) ){
            try {
                entity.setId( Integer.parseInt( parameterMap.get( ID )[ 0 ] ) );
            } catch( java.lang.NumberFormatException ex ) {
                throw new ValidationException(ex);
            }
        }

        //extract the date from map first.
        //everything in the parameterMap is string so it must first be
        //converted to appropriate type. have in mind that values are
        //stored in an array of String; almost always the value is at
        //index zero unless you have used duplicated key/name somewhere.
                
        String person_id = null;
        
        if( parameterMap.containsKey( PERSON_ID ) ){
            person_id = parameterMap.get( PERSON_ID )[ 0 ];
            if(!person_id.equals("")){
                validator.accept( person_id, 45 );
            } 
        }
        
        String donation_id = null;
        if( parameterMap.containsKey( DONATION_ID ) ){
            donation_id = parameterMap.get( DONATION_ID )[ 0 ];
            if(!donation_id.equals("")){
                validator.accept( donation_id, 45 );
            } 
        } 
        
        String tested = parameterMap.get( TESTED )[ 0 ];
        String administrator = parameterMap.get( ADMINISTRATOR )[ 0 ];
        String hospital = parameterMap.get( HOSPITAL )[ 0 ];
        
        // MIGHT NOT NEED TO VALIDATE CHECK MATT'S CODE 
        //validate the data
        //validator.accept( tested, 45 );
        //validator.accept( hospital, 45 );
       // validator.accept( hospital, 45 );
        //validator.accept( created, 45 );
        
        Date creation_time = new Date();
            //DateTimeFormatter myFormatObj = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
            //String formattedDate = myDateObj.format(myFormatObj);
         try{
             creation_time = convertStringToDate(parameterMap.get( CREATED )[ 0 ]);   
         } catch(ValidationException e){
             
             creation_time = convertStringToDate(new SimpleDateFormat("yyyy-MM-dd kk:mm:ss").format(creation_time));
             // created: 
         }
         
      // set entity parameters
        entity.setCreated(creation_time);
        entity.setAdministrator(administrator );
    
        entity.setTested(Boolean.parseBoolean(tested));
        entity.setHospital(hospital);
        
        if(person_id.equals("")){
            entity.setPerson(null);
        } else {
            entity.setPerson(new Person(Integer.parseInt(person_id)));
        }
        
        if(donation_id.equals("")){
            entity.setBloodDonation(null);
        } else {
            entity.setBloodDonation(new BloodDonation(Integer.parseInt(donation_id)));
        }
        
  
        return entity;
    }
}
