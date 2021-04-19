package logic;

import common.EMFactory;
import common.TomcatStartUp;
import common.ValidationException;
import entity.BloodBank;
import entity.BloodDonation;
import entity.BloodGroup;
import entity.DonationRecord;
import entity.DonationRecord;
import entity.Person;
import entity.RhesusFactor;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.function.Consumer;
import java.util.function.IntFunction;
import javax.persistence.EntityManager;
import org.hibernate.Hibernate;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


/**
 *
 * @author aksha
 */
public class DonationRecordTest {
    
     private DonationRecordLogic logic;
     private DonationRecord expectedEntity;
     private int drId = 1;
     private boolean testedTest = true;
     private String hospitalTest = "Ottawa General Hospital";
     private String adminTest = "Bob Hope";
     private String dateTestString;
     private int testPersonId = 1;
     private int testBloodDonationId = 1;
     Date dateTest;
     
     @BeforeAll
    final static void setUpBeforeClass() throws Exception {
        TomcatStartUp.createTomcat( "/SimpleBloodBank", "common.ServletListener", "simplebloodbank-PU-test" );
    }
    
       @AfterAll
    final static void tearDownAfterClass() throws Exception {
        TomcatStartUp.stopAndDestroyTomcat();
    }
    
    /**
     * 
     **/
    @BeforeEach
    final void setUp() throws Exception {

        logic = LogicFactory.getFor( "DonationRecord" );
        /* **********************************
         * ***********IMPORTANT**************
         * **********************************/
        //we only do this for the test.
        //always create Entity using logic.
        //we manually make the account to not rely on any logic functionality , just for testing
        

        //get an instance of EntityManager
        EntityManager em = EMFactory.getEMF().createEntityManager();
        //start a Transaction
        em.getTransaction().begin();
        
        dateTestString = "2020-01-01 22:22:22";
        dateTest = logic.convertStringToDate(dateTestString);
        //em.find(Perons.class, testBloodBankId );
        // get dependancy bloodbank n put it in the db
        BloodDonation bd = em.find(BloodDonation.class, testBloodDonationId );
        if(bd == null){
            bd = new BloodDonation();
            bd.setCreated(dateTest);
            bd.setMilliliters(100);
            bd.setRhd(RhesusFactor.Negative);
            bd.setBloodGroup(BloodGroup.B);
            em.persist(bd);
        }
        
        testBloodDonationId = bd.getId();
        // get dependancy person n put it in the db
        Person p = em.find(Person.class, testPersonId);
        if(p == null){
            p = new Person();
            p.setFirstName("Bob");
            p.setLastName("Hope");
            p.setBirth(dateTest);
            p.setAddress("London");
            p.setPhone("403-900-9383");
            em.persist(p);
        }
        testPersonId = p.getId();
        
        // create entity 
        DonationRecord entity = new DonationRecord();
        
        entity.setHospital(hospitalTest);
        entity.setId(drId);
        entity.setCreated(dateTest);
        entity.setAdministrator(adminTest);
        entity.setPerson(p);
        entity.setBloodDonation(bd);
       
        //add an DonationRecord to hibernate, DonationRecord is now managed.
        //we use merge instead of add so we can get the updated generated ID.
        expectedEntity = em.merge( entity );
        //commit the changes
        em.getTransaction().commit();
        //close EntityManager
        em.close();
    }

    @AfterEach
    final void tearDown() throws Exception {
        if( expectedEntity != null ){
            logic.delete( expectedEntity );
        }
        // delete dependencies from database
        //get an instance of EntityManager
        EntityManager em = EMFactory.getEMF().createEntityManager();
        //start a Transaction
        em.getTransaction().begin();
        // 
        Person testPerson = em.find( Person.class, testPersonId );
        BloodDonation testBloodDonation = em.find(BloodDonation.class, testBloodDonationId);
        if (testPerson!= null) {
            em.remove(testPerson);
        }
        if(testBloodDonation != null){
            em.remove(testBloodDonation);
        }
        //commit the changes
        em.getTransaction().commit();
        //close EntityManager
        em.close();
    }

    @Test
    final void testGetAll() {
        //get all the DonationRecords from the DB
        List<DonationRecord> list = logic.getAll();
        
        //store the size of list, this way we know how many accounts exits in DB
        int originalSize = list.size();

        //make sure DonationRecord was created successfully
        assertNotNull( expectedEntity );
        //add the new account CAN USE MINUS IF THIS DOESNT WORK
        logic.delete( expectedEntity );

        //get all DonationRecords again
        list = logic.getAll();
        //the new size of accounts must be one more
        assertEquals( originalSize - 1, list.size() );
    }
   
    /**
     * helper method for testing all DonationRecord fields
     *
     * @param expected
     * @param actual
     */
    
    private void assertDonationRecordEquals( DonationRecord expected, DonationRecord actual ) {
        //assert all field to guarantee they are the same
        assertEquals( expected.getId(), actual.getId() );
        assertEquals( expected.getAdministrator(), actual.getAdministrator() );
        assertEquals( expected.getHospital(), actual.getHospital() );
        assertEquals( expected.getCreated(), actual.getCreated() );
        assertEquals( expected.getPerson().getId(), actual.getPerson().getId() );
        assertEquals(expected.getBloodDonation().getId() , actual.getBloodDonation().getId());
    }

    @Test
    final void testGetWithId() {
        //using the id of test donationRecord to get the is of a donationRecord from the db 
        DonationRecord returnedDonationRecord = logic.getWithId( expectedEntity.getId() );
        //the two accounts (testDonationRecord and returnedDonationRecord) must be the same
        returnedDonationRecord.setBloodDonation((BloodDonation)Hibernate.unproxy(returnedDonationRecord.getBloodDonation()));
        //
        assertDonationRecordEquals( expectedEntity, returnedDonationRecord );
    }
    
    @Test
    final void testGetDonationRecordWithPerson() {
        List<DonationRecord> returnedDonationRecords = logic.getDonationRecordWithPersonID( expectedEntity.getPerson().getId() );
        for(DonationRecord returnedDonationRecord : returnedDonationRecords){
            assertEquals(expectedEntity.getPerson().getId(), returnedDonationRecord.getPerson().getId());
        }
    }     
    
    @Test
    final void testGetDonationRecordWithHospital() {
         List<DonationRecord> returnedDonationRecord = logic.getDonationRecordWithHospital( expectedEntity.getHospital());
         
         // for each donation record in the list see if the hospital equal expected entity
         returnedDonationRecord.forEach( dr -> assertEquals( expectedEntity.getHospital() , dr.getHospital()));        
        //the two accounts (testAcounts and returnedAccounts) must be the same
    }
    
     @Test
    final void testGetDonationRecordWithAdministrator() {
         List<DonationRecord> returnedDonationRecords = logic.getDonationRecordWithHospital( expectedEntity.getAdministrator());
         
         // for each donation record in the list and see if testDonationRecord and DonationRecord are the same
         returnedDonationRecords.forEach( dr -> assertEquals( expectedEntity.getAdministrator(), dr.getAdministrator()));
         
        //the two DonationRecord (testDonationRecord and returnedDonationRecord) must be the same
        
    }

     @Test
    final void testGetDonationRecordWithCreated(){
         List<DonationRecord> returnedDonationRecords = logic.getDonationRecordWithCreated(expectedEntity.getCreated());
         
         // for each donation record in the list see if the hospital equal expected entity
        for(DonationRecord returnedDonationRecord : returnedDonationRecords){
                 assertEquals( expectedEntity.getCreated(), returnedDonationRecord.getCreated());
        }
        //the two accounts (testAcounts and returnedAccounts) must be the same
        
    }

     @Test
    final void testGetDonationRecordWithTested(){
         List<DonationRecord> returnedDonationRecord = logic.getDonationRecordWithTested( expectedEntity.getTested());
         
         // for each donation record in the list see if the hospital equal expected entity
         returnedDonationRecord.forEach( dr -> assertEquals( expectedEntity.getTested() , dr.getTested()));
        //the two accounts (testAcounts and returnedAccounts) must be the same   
    }
    
    @Test
    final void testCreateEntityAndAdd() {
            
           // CREATING A DIFFERENT ENTITY FROM EXPECTED 
        Map<String, String[]> sampleMap = new HashMap<>();
        sampleMap.put( DonationRecordLogic.ADMINISTRATOR, new String[]{ adminTest } );
        sampleMap.put( DonationRecordLogic.HOSPITAL, new String[]{ hospitalTest } );
        sampleMap.put( DonationRecordLogic.TESTED, new String[]{ String.valueOf(testedTest) } );
        sampleMap.put( DonationRecordLogic.CREATED, new String[]{ dateTestString } );
        
       // sampleMap.put( DonationRecordLogic.DONATION_ID, new String[]{ "12" } );
        //sampleMap.put( DonationRecordLogic.PERSON_ID, new String[]{ "10" } );

        DonationRecord newDonationRecord = logic.createEntity( sampleMap );
        
        // adds returned donation to database
        logic.add(  newDonationRecord);

        DonationRecord returnedDonationRecord =  logic.getWithId( newDonationRecord.getId() );
        
        assertEquals( newDonationRecord.getAdministrator() , returnedDonationRecord.getAdministrator() );
        assertEquals( newDonationRecord.getHospital(), returnedDonationRecord.getHospital() );
        assertEquals( newDonationRecord.getTested(), returnedDonationRecord.getTested() );
        assertEquals(newDonationRecord.getCreated(), returnedDonationRecord.getCreated());

        logic.delete( returnedDonationRecord );
    }
    
    @Test
    final void testCreateEntity() {
        Map<String, String[]> sampleMap = new HashMap<>();
        sampleMap.put( DonationRecordLogic.ID, new String[]{ Integer.toString( expectedEntity.getId() ) } );
        sampleMap.put( DonationRecordLogic.ADMINISTRATOR, new String[]{ expectedEntity.getAdministrator()} );
        
       //  dateTest = logic.convertStringToDate(new SimpleDateFormat("yyyy-MM-dd kk:mm:ss").format(expectedEntity.getCreated()));
        // dateTestString = dateTest.toString();
        sampleMap.put( DonationRecordLogic.CREATED, new String[]{dateTestString });
        sampleMap.put( DonationRecordLogic.TESTED, new String[]{ Boolean.toString(expectedEntity.getTested()) } );
        sampleMap.put( DonationRecordLogic.HOSPITAL, new String[]{ expectedEntity.getHospital() } );
        
        
        DonationRecord returnedDonationRecord = logic.createEntity( sampleMap );
        
        int personId = expectedEntity.getPerson().getId();
        int donationId = expectedEntity.getBloodDonation().getId();
        if(expectedEntity.getBloodDonation() != null){
            returnedDonationRecord.setBloodDonation(EMFactory.getEMF().createEntityManager().find(BloodDonation.class, donationId ));
        }
        if(expectedEntity.getPerson() != null){
            returnedDonationRecord.setPerson(EMFactory.getEMF().createEntityManager().find(Person.class, personId ));
        }
        assertDonationRecordEquals( expectedEntity, returnedDonationRecord );
    }

    @Test
    final void testCreateEntityNullAndEmptyValues() {
        Map<String, String[]> sampleMap = new HashMap<>();
        Consumer<Map<String, String[]>> fillMap = ( Map<String, String[]> map ) -> {
            map.clear();
            map.put( DonationRecordLogic.ID, new String[]{ Integer.toString( expectedEntity.getId() ) } );
            map.put( DonationRecordLogic.ADMINISTRATOR, new String[]{ expectedEntity.getAdministrator() } );
            map.put( DonationRecordLogic.TESTED, new String[]{ Boolean.toString(expectedEntity.getTested()) } );
            map.put( DonationRecordLogic.HOSPITAL, new String[]{ expectedEntity.getHospital() } );
            map.put( DonationRecordLogic.CREATED, new String[]{ logic.convertDateToString(expectedEntity.getCreated() )} );
            map.put(DonationRecordLogic.PERSON_ID, new String[]{ expectedEntity.getPerson().getId().toString()});
            map.put(DonationRecordLogic.DONATION_ID, new String[]{ expectedEntity.getBloodDonation().getId().toString()});
        };

        //idealy every test should be in its own method
        fillMap.accept( sampleMap );
        sampleMap.replace( DonationRecordLogic.ID, null );
        assertThrows( NullPointerException.class, () -> logic.createEntity( sampleMap ) );
        sampleMap.replace( DonationRecordLogic.ID, new String[]{} );
        assertThrows( IndexOutOfBoundsException.class, () -> logic.createEntity( sampleMap ) );

        fillMap.accept( sampleMap );
        //can be null
        sampleMap.replace( DonationRecordLogic.ADMINISTRATOR, new String[]{} );
        assertThrows( IndexOutOfBoundsException.class, () -> logic.createEntity( sampleMap ) );

         fillMap.accept( sampleMap );
        sampleMap.replace( DonationRecordLogic.TESTED, null );
        assertThrows( NullPointerException.class, () -> logic.createEntity( sampleMap ) );
        sampleMap.replace( DonationRecordLogic.TESTED, new String[]{} );
        assertThrows( IndexOutOfBoundsException.class, () -> logic.createEntity( sampleMap ) );

        fillMap.accept( sampleMap );
        sampleMap.replace( DonationRecordLogic.HOSPITAL, null );
        assertThrows( NullPointerException.class, () -> logic.createEntity( sampleMap ) );
        sampleMap.replace( DonationRecordLogic.HOSPITAL, new String[]{} );
        assertThrows( IndexOutOfBoundsException.class, () -> logic.createEntity( sampleMap ) );
        
        fillMap.accept( sampleMap );
        sampleMap.replace( DonationRecordLogic.CREATED, null );
        assertThrows( NullPointerException.class, () -> logic.createEntity( sampleMap ) );
        sampleMap.replace( DonationRecordLogic.CREATED, new String[]{} );
        assertThrows( IndexOutOfBoundsException.class, () -> logic.createEntity( sampleMap ) );
    }
    
    @Test
    final void testCreateEntityBadTestedValue() {
        Map<String, String[]> sampleMap = new HashMap<>();
        Consumer<Map<String, String[]>> fillMap = ( Map<String, String[]> map ) -> {
            map.clear();
            map.put( DonationRecordLogic.ID, new String[]{ ( expectedEntity.getId().toString() ) } );
            map.put( DonationRecordLogic.ADMINISTRATOR, new String[]{ expectedEntity.getAdministrator() } );
            map.put( DonationRecordLogic.TESTED, new String[]{ Boolean.toString(expectedEntity.getTested()) } );
            map.put( DonationRecordLogic.HOSPITAL, new String[]{ expectedEntity.getHospital() } );
            map.put( DonationRecordLogic.CREATED, new String[]{ dateTestString} );
            map.put(DonationRecordLogic.PERSON_ID, new String[]{ expectedEntity.getPerson().getId().toString()});
            map.put(DonationRecordLogic.DONATION_ID, new String[]{ expectedEntity.getBloodDonation().getId().toString()});
        };

        fillMap.accept( sampleMap );
        sampleMap.replace( DonationRecordLogic.TESTED, new String[]{ "" } );
        assertThrows( ValidationException.class, () -> logic.createEntity( sampleMap ) );
        
    }
    
     @Test
    final void testCreateEntityBadIdValue() {
        Map<String, String[]> sampleMap = new HashMap<>();
        Consumer<Map<String, String[]>> fillMap = ( Map<String, String[]> map ) -> {
            map.clear();
            map.put( DonationRecordLogic.ID, new String[]{ ( expectedEntity.getId().toString() ) } );
            map.put( DonationRecordLogic.ADMINISTRATOR, new String[]{ expectedEntity.getAdministrator() } );
            map.put( DonationRecordLogic.TESTED, new String[]{ Boolean.toString(expectedEntity.getTested()) } );
            map.put( DonationRecordLogic.HOSPITAL, new String[]{ expectedEntity.getHospital() } );
            map.put( DonationRecordLogic.CREATED, new String[]{ logic.convertDateToString(expectedEntity.getCreated() )} );
            map.put(DonationRecordLogic.PERSON_ID, new String[]{ expectedEntity.getPerson().getId().toString()});
            map.put(DonationRecordLogic.DONATION_ID, new String[]{ expectedEntity.getBloodDonation().getId().toString()});
        };

        fillMap.accept( sampleMap );
        sampleMap.replace( DonationRecordLogic.ID, new String[]{ "" } );
        assertThrows( ValidationException.class, () -> logic.createEntity( sampleMap ) );
        
    }
    
        @Test
    final void testCreateEntityBadHospitalValue() {
        Map<String, String[]> sampleMap = new HashMap<>();
        Consumer<Map<String, String[]>> fillMap = ( Map<String, String[]> map ) -> {
            map.clear();
            map.put( DonationRecordLogic.ID, new String[]{ Integer.toString( expectedEntity.getId() ) } );
            map.put( DonationRecordLogic.ADMINISTRATOR, new String[]{ expectedEntity.getAdministrator() } );
            map.put( DonationRecordLogic.TESTED, new String[]{ Boolean.toString(expectedEntity.getTested()) } );
            map.put( DonationRecordLogic.HOSPITAL, new String[]{ expectedEntity.getHospital() } );
            map.put( DonationRecordLogic.CREATED, new String[]{ logic.convertDateToString(expectedEntity.getCreated() )} );
        };

        //idealy every test should be in its own method
        fillMap.accept( sampleMap );
        sampleMap.replace( DonationRecordLogic.HOSPITAL, new String[]{ "" } );
        assertThrows( ValidationException.class, () -> logic.createEntity( sampleMap ) );
    }

    @Test
    final void testGetColumnNames() {
        List<String> list = logic.getColumnNames();
        assertEquals( Arrays.asList( "ID", "PersonID", "DonationID", "Tested", "Administrator", "Hospital" , "Created"), list );
    }

    @Test
    final void testGetColumnCodes() {
        List<String> list = logic.getColumnCodes();
        assertEquals( Arrays.asList( DonationRecordLogic.ID, DonationRecordLogic.PERSON_ID, 
                DonationRecordLogic.DONATION_ID, DonationRecordLogic.TESTED,
                DonationRecordLogic.ADMINISTRATOR, DonationRecordLogic.HOSPITAL, DonationRecordLogic.CREATED ), list );
    }

    @Test
    final void testExtractDataAsList() {
        List<?> list = logic.extractDataAsList( expectedEntity );
        assertEquals( expectedEntity.getId(), list.get( 0 ) );
        assertEquals( expectedEntity.getPerson().getId(), list.get( 1 ) );
        assertEquals( expectedEntity.getBloodDonation().getId(), list.get( 2 ) );
        assertEquals( expectedEntity.getTested(), list.get( 3 ) );
        assertEquals( expectedEntity.getAdministrator(), list.get( 4 ) );
        assertEquals( expectedEntity.getHospital(), list.get( 5 ) );
        assertEquals( expectedEntity.getCreated(), list.get( 6 ) );    
    }
}


     
     
 
