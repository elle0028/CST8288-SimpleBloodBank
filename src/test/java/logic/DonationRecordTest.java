package logic;

import common.EMFactory;
import common.TomcatStartUp;
import common.ValidationException;
import entity.Account;
import entity.DonationRecord;
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
     
     @BeforeAll
    final static void setUpBeforeClass() throws Exception {
        TomcatStartUp.createTomcat( "/SimpleBloodBank", "common.ServletListener", "simplebloodbank-PU-test" );
    }
    
       @AfterAll
    final static void tearDownAfterClass() throws Exception {
        TomcatStartUp.stopAndDestroyTomcat();
    }
    
    
    @BeforeEach
    final void setUp() throws Exception {

        logic = LogicFactory.getFor( "Account" );
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
        entity.setAdministrator("jay" );
        entity.setHospital(hospitalTest);
        entity.setCreated(dateTest);
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
        //get all the accounts from the DB
        List<DonationRecord> list = logic.getAll();
        
        //store the size of list, this way we know how many accounts exits in DB
        int originalSize = list.size();

        //make sure account was created successfully
        assertNotNull( expectedEntity );
        //delete the new account
        logic.delete( expectedEntity );

        //get all accounts again
        list = logic.getAll();
        //the new size of accounts must be one less
        assertEquals( originalSize - 1, list.size() );
    }

    /**
     * helper method for testing all account fields
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
        assertEquals( expected.getPerson(), actual.getPerson() );
    }

    @Test
    final void testGetWithId() {
        //using the id of test donationRecord to get the is of a donationRecord form the db 
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
        sampleMap.put( AccountLogic.NICKNAME, new String[]{ "Test Create Entity" } );
        sampleMap.put( AccountLogic.USERNAME, new String[]{ "testCreateAccount" } );
        sampleMap.put( AccountLogic.PASSWORD, new String[]{ "create" } );
        sampleMap.put( AccountLogic.NAME, new String[]{ "create" } );

        DonationRecord donationRecord = logic.createEntity( sampleMap );
        logic.add( returnedAccount );

        returnedAccount = logic.getAccountWithUsername( returnedAccount.getUsername() );

        assertEquals( sampleMap.get( AccountLogic.NICKNAME )[ 0 ], returnedAccount.getNickname() );
        assertEquals( sampleMap.get( AccountLogic.USERNAME )[ 0 ], returnedAccount.getUsername() );
        assertEquals( sampleMap.get( AccountLogic.PASSWORD )[ 0 ], returnedAccount.getPassword() );
        assertEquals( sampleMap.get( AccountLogic.NAME )[ 0 ], returnedAccount.getName() );

        logic.delete( returnedAccount );
    }

    @Test
    final void testCreateEntity() {
        Map<String, String[]> sampleMap = new HashMap<>();
        sampleMap.put( AccountLogic.ID, new String[]{ Integer.toString( expectedEntity.getId() ) } );
        sampleMap.put( AccountLogic.NICKNAME, new String[]{ expectedEntity.getNickname() } );
        sampleMap.put( AccountLogic.USERNAME, new String[]{ expectedEntity.getUsername() } );
        sampleMap.put( AccountLogic.PASSWORD, new String[]{ expectedEntity.getPassword() } );
        sampleMap.put( AccountLogic.NAME, new String[]{ expectedEntity.getName() } );

        Account returnedAccount = logic.createEntity( sampleMap );

        assertAccountEquals( expectedEntity, returnedAccount );
    }

    @Test
    final void testCreateEntityNullAndEmptyValues() {
        Map<String, String[]> sampleMap = new HashMap<>();
        Consumer<Map<String, String[]>> fillMap = ( Map<String, String[]> map ) -> {
            map.clear();
            map.put( AccountLogic.ID, new String[]{ Integer.toString( expectedEntity.getId() ) } );
            map.put( AccountLogic.NICKNAME, new String[]{ expectedEntity.getNickname() } );
            map.put( AccountLogic.USERNAME, new String[]{ expectedEntity.getUsername() } );
            map.put( AccountLogic.PASSWORD, new String[]{ expectedEntity.getPassword() } );
            map.put( AccountLogic.NAME, new String[]{ expectedEntity.getName() } );
        };

        //idealy every test should be in its own method
        fillMap.accept( sampleMap );
        sampleMap.replace( AccountLogic.ID, null );
        assertThrows( NullPointerException.class, () -> logic.createEntity( sampleMap ) );
        sampleMap.replace( AccountLogic.ID, new String[]{} );
        assertThrows( IndexOutOfBoundsException.class, () -> logic.createEntity( sampleMap ) );

        fillMap.accept( sampleMap );
        sampleMap.replace( AccountLogic.NAME, null );
        assertThrows( NullPointerException.class, () -> logic.createEntity( sampleMap ) );
        sampleMap.replace( AccountLogic.NAME, new String[]{} );
        assertThrows( IndexOutOfBoundsException.class, () -> logic.createEntity( sampleMap ) );

        fillMap.accept( sampleMap );
        //can be null
        sampleMap.replace( AccountLogic.NICKNAME, new String[]{} );
        assertThrows( IndexOutOfBoundsException.class, () -> logic.createEntity( sampleMap ) );

        fillMap.accept( sampleMap );
        sampleMap.replace( AccountLogic.USERNAME, null );
        assertThrows( NullPointerException.class, () -> logic.createEntity( sampleMap ) );
        sampleMap.replace( AccountLogic.USERNAME, new String[]{} );
        assertThrows( IndexOutOfBoundsException.class, () -> logic.createEntity( sampleMap ) );

        fillMap.accept( sampleMap );
        sampleMap.replace( AccountLogic.PASSWORD, null );
        assertThrows( NullPointerException.class, () -> logic.createEntity( sampleMap ) );
        sampleMap.replace( AccountLogic.PASSWORD, new String[]{} );
        assertThrows( IndexOutOfBoundsException.class, () -> logic.createEntity( sampleMap ) );
    }

    @Test
    final void testCreateEntityBadLengthValues() {
        Map<String, String[]> sampleMap = new HashMap<>();
        Consumer<Map<String, String[]>> fillMap = ( Map<String, String[]> map ) -> {
            map.clear();
            map.put( AccountLogic.ID, new String[]{ Integer.toString( expectedEntity.getId() ) } );
            map.put( AccountLogic.NICKNAME, new String[]{ expectedEntity.getNickname() } );
            map.put( AccountLogic.USERNAME, new String[]{ expectedEntity.getUsername() } );
            map.put( AccountLogic.PASSWORD, new String[]{ expectedEntity.getPassword() } );
            map.put( AccountLogic.NAME, new String[]{ expectedEntity.getName() } );
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
        sampleMap.replace( AccountLogic.ID, new String[]{ "" } );
        assertThrows( ValidationException.class, () -> logic.createEntity( sampleMap ) );
        sampleMap.replace( AccountLogic.ID, new String[]{ "12b" } );
        assertThrows( ValidationException.class, () -> logic.createEntity( sampleMap ) );

        fillMap.accept( sampleMap );
        sampleMap.replace( AccountLogic.NICKNAME, new String[]{ "" } );
        assertThrows( ValidationException.class, () -> logic.createEntity( sampleMap ) );
        sampleMap.replace( AccountLogic.NICKNAME, new String[]{ generateString.apply( 46 ) } );
        assertThrows( ValidationException.class, () -> logic.createEntity( sampleMap ) );

        fillMap.accept( sampleMap );
        sampleMap.replace( AccountLogic.NAME, new String[]{ "" } );
        assertThrows( ValidationException.class, () -> logic.createEntity( sampleMap ) );
        sampleMap.replace( AccountLogic.NAME, new String[]{ generateString.apply( 46 ) } );
        assertThrows( ValidationException.class, () -> logic.createEntity( sampleMap ) );

        fillMap.accept( sampleMap );
        sampleMap.replace( AccountLogic.USERNAME, new String[]{ "" } );
        assertThrows( ValidationException.class, () -> logic.createEntity( sampleMap ) );
        sampleMap.replace( AccountLogic.USERNAME, new String[]{ generateString.apply( 46 ) } );
        assertThrows( ValidationException.class, () -> logic.createEntity( sampleMap ) );

        fillMap.accept( sampleMap );
        sampleMap.replace( AccountLogic.PASSWORD, new String[]{ "" } );
        assertThrows( ValidationException.class, () -> logic.createEntity( sampleMap ) );
        sampleMap.replace( AccountLogic.PASSWORD, new String[]{ generateString.apply( 46 ) } );
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
        sampleMap.put( AccountLogic.ID, new String[]{ Integer.toString( 1 ) } );
        sampleMap.put( AccountLogic.NICKNAME, new String[]{ generateString.apply( 1 ) } );
        sampleMap.put( AccountLogic.USERNAME, new String[]{ generateString.apply( 1 ) } );
        sampleMap.put( AccountLogic.PASSWORD, new String[]{ generateString.apply( 1 ) } );
        sampleMap.put( AccountLogic.NAME, new String[]{ generateString.apply( 1 ) } );

        //idealy every test should be in its own method
        Account returnedAccount = logic.createEntity( sampleMap );
        assertEquals( Integer.parseInt( sampleMap.get( AccountLogic.ID )[ 0 ] ), returnedAccount.getId() );
        assertEquals( sampleMap.get( AccountLogic.NICKNAME )[ 0 ], returnedAccount.getNickname() );
        assertEquals( sampleMap.get( AccountLogic.USERNAME )[ 0 ], returnedAccount.getUsername() );
        assertEquals( sampleMap.get( AccountLogic.PASSWORD )[ 0 ], returnedAccount.getPassword() );
        assertEquals( sampleMap.get( AccountLogic.NAME )[ 0 ], returnedAccount.getName() );

        sampleMap = new HashMap<>();
        sampleMap.put( AccountLogic.ID, new String[]{ Integer.toString( 1 ) } );
        sampleMap.put( AccountLogic.NICKNAME, new String[]{ generateString.apply( 45 ) } );
        sampleMap.put( AccountLogic.USERNAME, new String[]{ generateString.apply( 45 ) } );
        sampleMap.put( AccountLogic.PASSWORD, new String[]{ generateString.apply( 45 ) } );
        sampleMap.put( AccountLogic.NAME, new String[]{ generateString.apply( 45 ) } );

        //idealy every test should be in its own method
        returnedAccount = logic.createEntity( sampleMap );
        assertEquals( Integer.parseInt( sampleMap.get( AccountLogic.ID )[ 0 ] ), returnedAccount.getId() );
        assertEquals( sampleMap.get( AccountLogic.NICKNAME )[ 0 ], returnedAccount.getNickname() );
        assertEquals( sampleMap.get( AccountLogic.USERNAME )[ 0 ], returnedAccount.getUsername() );
        assertEquals( sampleMap.get( AccountLogic.PASSWORD )[ 0 ], returnedAccount.getPassword() );
        assertEquals( sampleMap.get( AccountLogic.NAME )[ 0 ], returnedAccount.getName() );
    }

    @Test
    final void testGetColumnNames() {
        List<String> list = logic.getColumnNames();
        assertEquals( Arrays.asList( "ID", "Name", "Nickname", "Username", "Password" ), list );
    }

    @Test
    final void testGetColumnCodes() {
        List<String> list = logic.getColumnCodes();
        assertEquals( Arrays.asList( AccountLogic.ID, AccountLogic.NAME, AccountLogic.NICKNAME, AccountLogic.USERNAME, AccountLogic.PASSWORD ), list );
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
