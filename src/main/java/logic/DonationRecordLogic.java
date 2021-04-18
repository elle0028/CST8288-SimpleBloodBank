package logic;

import common.ValidationException;
import dal.DonationRecordDAL;
import entity.BloodDonation;
import entity.DonationRecord;
import entity.Person;

import java.util.Date;

import java.text.SimpleDateFormat;

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
        
        
        //validate the data
        validator.accept( tested, 45 );
        validator.accept( hospital, 45 );
        validator.accept( hospital, 45 );
        //validator.accept( created, 45 );
        
//        Date creation_time = new Date();
//            //DateTimeFormatter myFormatObj = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
//            //String formattedDate = myDateObj.format(myFormatObj);
//         try{
            Date creation_time = convertStringToDate(parameterMap.get( CREATED )[ 0 ]);
//            //creation_time = new Date(parameterMap.get( CREATED )[ 0 ]);   
//         } catch(ValidationException e){
//             creation_time = convertStringToDate(new SimpleDateFormat("yyyy-MM-dd kk:mm:ss").format(creation_time));
//             // created: 
//         }
         
      // set entity parameters
        entity.setCreated(creation_time);
        entity.setAdministrator(administrator );
    
        entity.setTested(Boolean.parseBoolean(tested));
        entity.setHospital(hospital);
        
        if(person_id == null || person_id.equals("")){
            entity.setPerson(null);
        }
//        } else {
//            entity.setPerson(new Person(Integer.parseInt(person_id)));
//        }
//        
        if(donation_id == null || donation_id.equals("")){
            entity.setBloodDonation(null);
        } 
//else {
//            entity.setBloodDonation(new BloodDonation(Integer.parseInt(donation_id)));
//        }
        
  
        return entity;
    }
    
    @Override
    public DonationRecord updateEntity(Map<String, String[]> parameterMap) {

        Objects.requireNonNull( parameterMap, "parameterMap cannot be null" );
        
        // Get dependencies
        PersonLogic pLogic = LogicFactory.getFor("PersonLogic");
        BloodDonationLogic bdLogic = LogicFactory.getFor("BloodDonationLogic");
 
        // Get current entity
        DonationRecord entityToUpdate = getWithId(Integer.parseInt(parameterMap.get(ID)[0]));

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
  
        
        int person_id = -1;
        if (entityToUpdate.getPerson() != null) {
            person_id = entityToUpdate.getPerson().getId();
        }
        if( parameterMap.containsKey( PERSON_ID ) && !parameterMap.get( PERSON_ID )[ 0 ].equals("")){
            int newPerson_id = Integer.parseInt(parameterMap.get( PERSON_ID )[ 0 ]);
            if(newPerson_id != person_id){
                person_id = newPerson_id;
            } 
        }
        
        
        int donation_id = -1;
        if (entityToUpdate.getBloodDonation() != null) {
            donation_id = entityToUpdate.getBloodDonation().getId();
        }
        if( parameterMap.containsKey( DONATION_ID ) && !parameterMap.get( DONATION_ID )[ 0 ].equals("")){
            int newDonation_id = Integer.parseInt(parameterMap.get( DONATION_ID )[ 0 ]);
            if(newDonation_id != donation_id){
                donation_id = newDonation_id;
            } 
        } 
        
        Boolean tested = entityToUpdate.getTested();
        if( parameterMap.containsKey( TESTED ) ){
            Boolean newTested = Boolean.parseBoolean(parameterMap.get( TESTED )[ 0 ]);
            if(newTested != tested){
                tested = newTested;
            } 
        }
        
        String administrator = entityToUpdate.getAdministrator();
        if (parameterMap.containsKey(ADMINISTRATOR)) {
            String newAdministrator = parameterMap.get( ADMINISTRATOR )[ 0 ];
            if (!newAdministrator.equals(administrator)) {
                administrator = newAdministrator;
            }
        }
        
        String hospital = entityToUpdate.getHospital();
        if (parameterMap.containsKey(HOSPITAL)) {
            String newHospital = parameterMap.get( HOSPITAL )[ 0 ];
            if (!newHospital.equals(hospital)) {
                hospital = newHospital;
            }
        }

        validator.accept( administrator, 45 );
        validator.accept( hospital, 45 );

       Date creation_time = entityToUpdate.getCreated();
       if (parameterMap.containsKey(CREATED)) {
           Date newCreated = convertStringToDate(parameterMap.get( CREATED )[ 0 ]);
           if (newCreated.compareTo(creation_time) != 0) {
               creation_time = newCreated;
           }
       }

      // set entity parameters
        entityToUpdate.setCreated(creation_time);
        entityToUpdate.setAdministrator(administrator );
        entityToUpdate.setTested(tested);
        entityToUpdate.setHospital(hospital);
        if (person_id != -1) {
            entityToUpdate.setPerson(pLogic.getWithId(person_id));
        }
        if (donation_id != -1) {
            entityToUpdate.setBloodDonation(bdLogic.getWithId(donation_id));
        }
        
        return entityToUpdate;
    }
    
    @Override
    public List<DonationRecord> search( String search ) {
        return get( () -> dal().findContaining( search ) );
    }
}