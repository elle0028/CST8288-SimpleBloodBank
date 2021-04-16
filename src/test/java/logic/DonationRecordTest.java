package logic;

import common.EMFactory;
import common.TomcatStartUp;
import common.ValidationException;
import entity.BloodBank;
import entity.DonationRecord;
import entity.DonationRecord;
import entity.Person;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.function.Consumer;
import java.util.function.IntFunction;
import javax.persistence.EntityManager;
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
     private boolean testedTest;
     private String hospitalTest;
     private String adminTest;
     private Date dateTest;
    // private Person testPersonId;
     //private BloodBank testBloodBankId;
     
     @BeforeAll
    final static void setUpBeforeClass() throws Exception {
        TomcatStartUp.createTomcat( "/SimpleBloodBank", "common.ServletListener", "simplebloodbank-PU-test" );
    }
    
       @AfterAll
    final static void tearDownAfterClass() throws Exception {
        TomcatStartUp.stopAndDestroyTomcat();
    }
    
    /**
     
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
       
        DonationRecord entity = new DonationRecord();
        entity.setName( "Junit 5 Test" );
        entity.setHospital(hospitalTest);
        entity.setId(drId);
        entity.setCreated(dateTest);
        entity.setAdministrator(adminTest);
        //entity.setPerons();
        //entity.setBloodDonation(bloodDonation);
        //entity.setPassword( "junit5" );

        //add an account to hibernate, account is now managed.
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
 // added this because bottom is commented out DELETE    
}
/*
    /**
     * helper method for testing all account fields
     *
     * @param expected
     * @param actual
     */
    /*
    private void assertDonationRecordEquals( DonationRecord expected, DonationRecord actual ) {
        //assert all field to guarantee they are the same
        assertEquals( expected.getId(), actual.getId() );
        assertEquals( expected.getAdministrator(), actual.getAdministrator() );
        assertEquals( expected.getHospital(), actual.getHospital() );
        assertEquals( expected.getCreated(), actual.getCreated() );
        assertEquals( expected.getPerson(), actual.getPerson() );
        assertEquals(expected.getDonationId(), actual.getDonationId());
    }
/*
    @Test
    final void testGetWithId() {
        //using the id of test donationRecord to get the is of a donationRecord from the db 
        DonationRecord returnedDonationRecord = logic.getWithId( expectedEntity.getId() );
        
        //the two accounts (testAcounts and returnedAccounts) must be the same
        assertDonationRecordEquals( expectedEntity, returnedDonationRecord );
    }
    
    @Test
    final void testGetDonationRecordWithPerson() {
        List<DonationRecord> returnedAccount = logic.getDonationRecordWithPersonID( expectedEntity.getPerson().getId() );

        //the two accounts (testAcounts and returnedAccounts) must be the same
        assertEquals( expectedEntity, returnedAccount );
    }
    

    @Test
    final void testGetDonationRecordWithHospital() {
         List<DonationRecord> returnedDonationRecord = logic.getDonationRecordWithHospital( expectedEntity.getHospital());
         
         // for each donation record in the list see if the hospital equal expected entity
         returnedDonationRecord.forEach( dr -> 
                 assertEquals( expectedEntity.getHospital() , dr.getHospital()));
         
        //the two accounts (testAcounts and returnedAccounts) must be the same
        
    }
    
     @Test
    final void testGetDonationRecordWithAdministrator() {
         List<DonationRecord> returnedDonationRecord = logic.getDonationRecordWithHospital( expectedEntity.getAdministrator());
         
         // for each donation record in the list and see if testDonationRecord and DonationRecord are the same
         returnedDonationRecord.forEach( dr -> 
                 assertEquals( expectedEntity.getAdministrator(), dr.getAdministrator()));
         
        //the two accounts (testAcounts and returnedAccounts) must be the same
        
    }
    
     @Test
    final void testGetDonationRecordWithCreated(){
         List<DonationRecord> returnedDonationRecord = logic.getDonationRecordWithCreated(expectedEntity.getCreated());
         
         // for each donation record in the list see if the hospital equal expected entity
         returnedDonationRecord.forEach( dr -> 
                 assertEquals( expectedEntity.getCreated(), dr.getCreated()));
         
        //the two accounts (testAcounts and returnedAccounts) must be the same
        
    }
    
     @Test
    final void testGetDonationRecordWithTested(){
         List<DonationRecord> returnedDonationRecord = logic.getDonationRecordWithTested( expectedEntity.getTested());
         
         // for each donation record in the list see if the hospital equal expected entity
         returnedDonationRecord.forEach( dr -> 
                 assertEquals( expectedEntity.getTested() , dr.getTested()));
        //the two accounts (testAcounts and returnedAccounts) must be the same   
    }

    @Test
    final void testCreateEntityAndAdd() {
        Map<String, String[]> sampleMap = new HashMap<>();
        sampleMap.put( DonationRecordLogic.NICKNAME, new String[]{ "Test Create Entity" } );
        sampleMap.put( DonationRecordLogic.USERNAME, new String[]{ "testCreateAccount" } );
        sampleMap.put( DonationRecordLogic.PASSWORD, new String[]{ "create" } );
        sampleMap.put( DonationRecordLogic.NAME, new String[]{ "create" } );

        DonationRecord returnedDonationRecord = logic.createEntity( sampleMap );
        logic.add(  returnedDonationRecord);

        returnedDonationRecord = logic.getAccountWithUsername( returnedDonationRecord.getUsername() );

        assertEquals( sampleMap.get( DonationRecordLogic. )[ 0 ], returnedDonationRecord.getNickname() );
        assertEquals( sampleMap.get( DonationRecordLogic.USERNAME )[ 0 ], returnedDonationRecord.getUsername() );
        assertEquals( sampleMap.get( DonationRecordLogic.PASSWORD )[ 0 ], returnedDonationRecord.getPassword() );
        assertEquals( sampleMap.get( DonationRecordLogic.NAME )[ 0 ], returnedDonationRecord.getName() );

        logic.delete( returnedAccount );
    }

    @Test
    final void testCreateEntity() {
        Map<String, String[]> sampleMap = new HashMap<>();
        sampleMap.put( DonationRecordLogic.ID, new String[]{ Integer.toString( expectedEntity.getId() ) } );
        sampleMap.put( DonationRecordLogic.ADMINISTRATOR, new String[]{ expectedEntity.getAdministrator()} );
        sampleMap.put( DonationRecordLogic.CREATED, new String[]{ String.valueOf(expectedEntity.getCreated())} );
        sampleMap.put( DonationRecordLogic.HOSPITAL, new String[]{ expectedEntity.getHospital() } );
        sampleMap.put( DonationRecordLogic.NAME, new String[]{ expectedEntity.getName() } );

        DonationRecord returnedAccount = logic.createEntity( sampleMap );

        assertAccountEquals( expectedEntity, returnedAccount );
    }

    @Test
    final void testCreateEntityNullAndEmptyValues() {
        Map<String, String[]> sampleMap = new HashMap<>();
        Consumer<Map<String, String[]>> fillMap = ( Map<String, String[]> map ) -> {
            map.clear();
            map.put( DonationRecordLogic.ID, new String[]{ Integer.toString( expectedEntity.getId() ) } );
            map.put( DonationRecordLogic.NICKNAME, new String[]{ expectedEntity.getNickname() } );
            map.put( DonationRecordLogic.USERNAME, new String[]{ expectedEntity.getUsername() } );
            map.put( DonationRecordLogic.PASSWORD, new String[]{ expectedEntity.getPassword() } );
            map.put( DonationRecordLogic.NAME, new String[]{ expectedEntity.getName() } );
        };

        //idealy every test should be in its own method
        fillMap.accept( sampleMap );
        sampleMap.replace( DonationRecordLogic.ID, null );
        assertThrows( NullPointerException.class, () -> logic.createEntity( sampleMap ) );
        sampleMap.replace( DonationRecordLogic.ID, new String[]{} );
        assertThrows( IndexOutOfBoundsException.class, () -> logic.createEntity( sampleMap ) );

        fillMap.accept( sampleMap );
        sampleMap.replace( DonationRecordLogic.NAME, null );
        assertThrows( NullPointerException.class, () -> logic.createEntity( sampleMap ) );
        sampleMap.replace( DonationRecordLogic.NAME, new String[]{} );
        assertThrows( IndexOutOfBoundsException.class, () -> logic.createEntity( sampleMap ) );

        fillMap.accept( sampleMap );
        //can be null
        sampleMap.replace( DonationRecordLogic.NICKNAME, new String[]{} );
        assertThrows( IndexOutOfBoundsException.class, () -> logic.createEntity( sampleMap ) );

        fillMap.accept( sampleMap );
        sampleMap.replace( DonationRecordLogic.USERNAME, null );
        assertThrows( NullPointerException.class, () -> logic.createEntity( sampleMap ) );
        sampleMap.replace( DonationRecordLogic.USERNAME, new String[]{} );
        assertThrows( IndexOutOfBoundsException.class, () -> logic.createEntity( sampleMap ) );

        fillMap.accept( sampleMap );
        sampleMap.replace( DonationRecordLogic.PASSWORD, null );
        assertThrows( NullPointerException.class, () -> logic.createEntity( sampleMap ) );
        sampleMap.replace( DonationRecordLogic.PASSWORD, new String[]{} );
        assertThrows( IndexOutOfBoundsException.class, () -> logic.createEntity( sampleMap ) );
    }

    @Test
    final void testCreateEntityBadLengthValues() {
        Map<String, String[]> sampleMap = new HashMap<>();
        Consumer<Map<String, String[]>> fillMap = ( Map<String, String[]> map ) -> {
            map.clear();
            map.put( DonationRecordLogic.ID, new String[]{ Integer.toString( expectedEntity.getId() ) } );
            map.put( DonationRecordLogic.NICKNAME, new String[]{ expectedEntity.getNickname() } );
            map.put( DonationRecordLogic.USERNAME, new String[]{ expectedEntity.getUsername() } );
            map.put( DonationRecordLogic.PASSWORD, new String[]{ expectedEntity.getPassword() } );
            map.put( DonationRecordLogic.NAME, new String[]{ expectedEntity.getName() } );
        };

        IntFunction<String> generateString = ( int length ) -> {
            //https://www.baeldung.com/java-random-string#java8-alphabetic
            //from 97 inclusive to 123 exclusive
            return new Random().ints( 'a', 'z' + 1 ).limit( length )
                    .collect( StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append )
                    .toString();
        };

        //idealy every test should be in its own method
        fillMap.accept( sampleMap );
        sampleMap.replace( DonationRecordLogic.ID, new String[]{ "" } );
        assertThrows( ValidationException.class, () -> logic.createEntity( sampleMap ) );
        sampleMap.replace( DonationRecordLogic.ID, new String[]{ "12b" } );
        assertThrows( ValidationException.class, () -> logic.createEntity( sampleMap ) );

        fillMap.accept( sampleMap );
        sampleMap.replace( DonationRecordLogic.NICKNAME, new String[]{ "" } );
        assertThrows( ValidationException.class, () -> logic.createEntity( sampleMap ) );
        sampleMap.replace( DonationRecordLogic.NICKNAME, new String[]{ generateString.apply( 46 ) } );
        assertThrows( ValidationException.class, () -> logic.createEntity( sampleMap ) );

        fillMap.accept( sampleMap );
        sampleMap.replace( DonationRecordLogic.NAME, new String[]{ "" } );
        assertThrows( ValidationException.class, () -> logic.createEntity( sampleMap ) );
        sampleMap.replace( DonationRecordLogic.NAME, new String[]{ generateString.apply( 46 ) } );
        assertThrows( ValidationException.class, () -> logic.createEntity( sampleMap ) );

        fillMap.accept( sampleMap );
        sampleMap.replace( DonationRecordLogic.USERNAME, new String[]{ "" } );
        assertThrows( ValidationException.class, () -> logic.createEntity( sampleMap ) );
        sampleMap.replace( DonationRecordLogic.USERNAME, new String[]{ generateString.apply( 46 ) } );
        assertThrows( ValidationException.class, () -> logic.createEntity( sampleMap ) );

        fillMap.accept( sampleMap );
        sampleMap.replace( DonationRecordLogic.PASSWORD, new String[]{ "" } );
        assertThrows( ValidationException.class, () -> logic.createEntity( sampleMap ) );
        sampleMap.replace( DonationRecordLogic.PASSWORD, new String[]{ generateString.apply( 46 ) } );
        assertThrows( ValidationException.class, () -> logic.createEntity( sampleMap ) );
    }

    @Test
    final void testCreateEntityEdgeValues() {
        IntFunction<String> generateString = ( int length ) -> {
            //https://www.baeldung.com/java-random-string#java8-alphabetic
            return new Random().ints( 'a', 'z' + 1 ).limit( length )
                    .collect( StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append )
                    .toString();
        };

        Map<String, String[]> sampleMap = new HashMap<>();
        sampleMap.put( DonationRecordLogic.ID, new String[]{ Integer.toString( 1 ) } );
        sampleMap.put( DonationRecordLogic.NICKNAME, new String[]{ generateString.apply( 1 ) } );
        sampleMap.put( DonationRecordLogic.USERNAME, new String[]{ generateString.apply( 1 ) } );
        sampleMap.put( DonationRecordLogic.PASSWORD, new String[]{ generateString.apply( 1 ) } );
        sampleMap.put( DonationRecordLogic.NAME, new String[]{ generateString.apply( 1 ) } );

        //idealy every test should be in its own method
        DonationRecord returnedAccount = logic.createEntity( sampleMap );
        assertEquals( Integer.parseInt( sampleMap.get( DonationRecordLogic.ID )[ 0 ] ), returnedAccount.getId() );
        assertEquals( sampleMap.get( DonationRecordLogic.NICKNAME )[ 0 ], returnedAccount.getNickname() );
        assertEquals( sampleMap.get( DonationRecordLogic.USERNAME )[ 0 ], returnedAccount.getUsername() );
        assertEquals( sampleMap.get( DonationRecordLogic.PASSWORD )[ 0 ], returnedAccount.getPassword() );
        assertEquals( sampleMap.get( DonationRecordLogic.NAME )[ 0 ], returnedAccount.getName() );

        sampleMap = new HashMap<>();
        sampleMap.put( DonationRecordLogic.ID, new String[]{ Integer.toString( 1 ) } );
        sampleMap.put( DonationRecordLogic.NICKNAME, new String[]{ generateString.apply( 45 ) } );
        sampleMap.put( DonationRecordLogic.USERNAME, new String[]{ generateString.apply( 45 ) } );
        sampleMap.put( DonationRecordLogic.PASSWORD, new String[]{ generateString.apply( 45 ) } );
        sampleMap.put( DonationRecordLogic.NAME, new String[]{ generateString.apply( 45 ) } );

        //idealy every test should be in its own method
        returnedAccount = logic.createEntity( sampleMap );
        assertEquals( Integer.parseInt( sampleMap.get( DonationRecordLogic.ID )[ 0 ] ), returnedAccount.getId() );
        assertEquals( sampleMap.get( DonationRecordLogic.NICKNAME )[ 0 ], returnedAccount.getNickname() );
        assertEquals( sampleMap.get( DonationRecordLogic.USERNAME )[ 0 ], returnedAccount.getUsername() );
        assertEquals( sampleMap.get( DonationRecordLogic.PASSWORD )[ 0 ], returnedAccount.getPassword() );
        assertEquals( sampleMap.get( DonationRecordLogic.NAME )[ 0 ], returnedAccount.getName() );
    }

    @Test
    final void testGetColumnNames() {
        List<String> list = logic.getColumnNames();
        assertEquals( Arrays.asList( "ID", "Name", "Nickname", "Username", "Password" ), list );
    }

    @Test
    final void testGetColumnCodes() {
        List<String> list = logic.getColumnCodes();
        assertEquals( Arrays.asList( DonationRecordLogic.ID, DonationRecordLogic.NAME, DonationRecordLogic.NICKNAME, DonationRecordLogic.USERNAME, DonationRecordLogic.PASSWORD ), list );
    }

    @Test
    final void testExtractDataAsList() {
        List<?> list = logic.extractDataAsList( expectedEntity );
        assertEquals( expectedEntity.getId(), list.get( 0 ) );
        assertEquals( expectedEntity.getName(), list.get( 1 ) );
        assertEquals( expectedEntity.getNickname(), list.get( 2 ) );
        assertEquals( expectedEntity.getUsername(), list.get( 3 ) );
        assertEquals( expectedEntity.getPassword(), list.get( 4 ) );
    }
}


     
     
    
}
*/