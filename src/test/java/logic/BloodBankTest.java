package logic;

import common.EMFactory;
import common.TomcatStartUp;
import common.ValidationException;
import java.util.Arrays;
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
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import entity.BloodBank;
import entity.BloodDonation;
import entity.Person;
import java.util.HashSet;
import java.util.Set;
import org.hibernate.Hibernate;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class BloodBankTest {
    private BloodBankLogic logic;
    private BloodBank expectedEntity;
    private Set<BloodDonation> donations;
    private Person testPerson;   // dependency
    private int personID = 2;
    
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

        logic = LogicFactory.getFor( "BloodBank" );             

        //get an instance of EntityManager
        EntityManager em = EMFactory.getEMF().createEntityManager();
        //start a Transaction
        em.getTransaction().begin();         
        
        // set up person dependency
        testPerson = em.find( Person.class, personID );
        //if result is null create the entity and persist it
        if( testPerson == null ){
            //create object
            testPerson = new Person();
            testPerson.setFirstName("Clark");
            testPerson.setLastName("Kent");
            testPerson.setPhone("613-316-1361");
            testPerson.setAddress("123 Road Street"); 
            testPerson.setBirth(logic.convertStringToDate("2020-3-1"));
            em.persist( testPerson );
        }       

        // keep personID aligned with current testPerson
        personID = testPerson.getId();
        // add two empty donations to the donation set
        donations = new HashSet<BloodDonation>();
        donations.add(new BloodDonation());        
        
        // create entity
        BloodBank entity = new BloodBank();
        entity.setName( "test name" );
        entity.setPrivatelyOwned(true);
        entity.setEstablished(logic.convertStringToDate("1212-12-12"));
        entity.setEmployeeCount(5);
        entity.setOwner(testPerson);        
        entity.setBloodDonationSet(donations);

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
        
        //get an instance of EntityManager
        EntityManager em = EMFactory.getEMF().createEntityManager();
        //start a Transaction
        em.getTransaction().begin();
        
        Person testPerson = em.find( Person.class, personID );
        if (testPerson!= null) {
            em.remove(testPerson);            
        }
        //commit the changes
        em.getTransaction().commit();
        //close EntityManager
        em.close();
    }
    
    @Test
    final void testGetName() {
        assertNotNull( expectedEntity );
        expectedEntity.setName("test name");
        String name = expectedEntity.getName();
        assertEquals(name, "test name");
    }
    
    @Test
    final void testGetPrivatelyOwned() {
        assertNotNull( expectedEntity );
        // should return false
        assertTrue(expectedEntity.getPrivatelyOwned());
    }
    
    @Test
    final void testGetEstablished() {
        assertNotNull( expectedEntity );
        // ensure the date string is the same as entered
        assertEquals(expectedEntity.getEstablished(), logic.convertStringToDate("1212-12-12"));
    }
    
    @Test
    final void testGetEmployeeCount() {
        assertNotNull( expectedEntity );
        // ensure there are 5 employees, as set in  setup
        assertEquals(expectedEntity.getEmployeeCount(), 5);
    }
    
    @Test
    final void testGetOwner() {
        assertNotNull( expectedEntity );
        // owner can be null and should be in this case
        assertEquals(expectedEntity.getOwner(), testPerson);
    }    
    
    @Test
    final void testGetBloodDonationSet() {
        assertNotNull( expectedEntity );
        // ensure we get a set with 2 blood donations back
        assertEquals(expectedEntity.getBloodDonationSet().size(), 1);
    }
    
    @Test
    final void testEquals() {
        assertNotNull( expectedEntity );
        // comparing an object against itself, should return true (are equal)
        assertTrue(expectedEntity.equals(expectedEntity));
    }
    
    @Test
    final void testGetAll() {
        //get all the BloodBanks from the DB
        List<BloodBank> list = logic.getAll();
        //store the size of list, this way we know how many bloodbanks exist in DB
        int originalSize = list.size();

        //make sure bloodbank was created successfully
        assertNotNull( expectedEntity );
        //delete the new bloodbank
        logic.delete( expectedEntity );

        //get all BloodBanks again
        list = logic.getAll();
        //the new size of BloodBanks must be one less
        assertEquals( originalSize - 1, list.size() );
    }
    
    private void assertBloodBanksEqual( BloodBank expected, BloodBank actual ) {
        //assert all field to guarantee they are the same
        assertEquals( expected.getId(), actual.getId() ); //TODO: something up with ID
        assertEquals(expected.getName(), actual.getName() );
        assertEquals(expected.getPrivatelyOwned(), actual.getPrivatelyOwned() );
        assertEquals(expected.getOwner(), actual.getOwner() );
        // use compareTo to see if the dates are the same
        assertEquals(expected.getEstablished().compareTo(actual.getEstablished()), 0);
        assertEquals(expected.getEmployeeCount(), actual.getEmployeeCount() );        
    }
    
    @Test
    final void testGetWithId() {
        //using the id of test BloodBank get another BloodBank from logic
        BloodBank returnedBank = logic.getWithId( expectedEntity.getId() );

        // change strange Hibernate Person to a plain old Person
        returnedBank.setOwner((Person)Hibernate.unproxy(returnedBank.getOwner()));
        
        //the two BloodBanks (testAcounts and returnedBloodBanks) must be the same
        assertBloodBanksEqual( expectedEntity, returnedBank );
    }
    
    @Test 
    final void testGetBloodBankWithName() {
        BloodBank returnedBank = logic.getBloodBankWithName( expectedEntity.getName() );
        
        // change strange Hibernate Person to a plain old Person
        returnedBank.setOwner((Person)Hibernate.unproxy(returnedBank.getOwner()));        
        
        //the two bloodbanks must be the same
        assertBloodBanksEqual( expectedEntity, returnedBank );
    }
    
    @Test
    final void testGetBloodBankWithPrivatelyOwned() {
        List<BloodBank> returnedBanks = logic.getBloodBankWithPrivatelyOwned( expectedEntity.getPrivatelyOwned() );
        int found = 0;
        // each returned bloodbank should have save privately owned value as
        // expectedEntity
        for(BloodBank bank : returnedBanks) {
            assertEquals(bank.getPrivatelyOwned(), expectedEntity.getPrivatelyOwned());
            if (bank.getId() == expectedEntity.getId()) {
                assertBloodBanksEqual(bank, expectedEntity);
                ++found;
            }
        } 
        assertEquals(found, 1);
    }   
    
    @Test
    final void testGetBloodBanksWithEstablished() {
        List<BloodBank> returnedBanks = logic.getBloodBankWithEstablished( expectedEntity.getEstablished() );
        int found = 0;
        for(BloodBank bank : returnedBanks) {
            // ensure established dates are the same
            assertEquals(expectedEntity.getEstablished().compareTo(bank.getEstablished()), 0);
            if (bank.getId() == expectedEntity.getId()) {
                assertBloodBanksEqual(bank, expectedEntity);
                ++found;
            }
        }
        assertEquals(found, 1);
    }
    
    @Test
    final void testGetBloodBanksWithEmployeeCount() {
        List<BloodBank> returnedBanks = logic.getBloodBankWithEstablished( expectedEntity.getEstablished() );
        int found = 0;
        for(BloodBank bank : returnedBanks) {
            // ensure established dates are the same
            assertEquals(expectedEntity.getEmployeeCount(), bank.getEmployeeCount());
            if (bank.getId() == expectedEntity.getId()) {
                assertBloodBanksEqual(bank, expectedEntity);
                ++found;
            }
        }
        assertEquals(found, 1);
    }
    
    @Test
    final void testGetBloodBanksWithOwner() {
        BloodBank returnedBank = logic.getBloodBankWithOwner( expectedEntity.getOwner().getId() );
        // fix hibernate Person back into regular Person
        returnedBank.setOwner((Person)Hibernate.unproxy(returnedBank.getOwner()));
        assertBloodBanksEqual(expectedEntity, returnedBank);
    }    
    
    @Test
    final void testCreateEntityAndAdd() {
        Map<String, String[]> sampleMap = new HashMap<>();
                     
        // this entity is different from expected entity
        sampleMap.put( BloodBankLogic.PRIVATELY_OWNED, new String[]{ "false" } );
        sampleMap.put( BloodBankLogic.ESTABLISHED, new String[]{ "12/12/1212" } );       
        sampleMap.put( BloodBankLogic.NAME, new String[]{ "test names" } );
        sampleMap.put( BloodBankLogic.EMPLOYEE_COUNT, new String[]{ "5" } );        
        
        BloodBank returnedBloodbank = logic.createEntity( sampleMap );
        logic.add(returnedBloodbank );

        returnedBloodbank = logic.getBloodBankWithName(returnedBloodbank.getName() );  
        
        assertEquals( false, returnedBloodbank.getPrivatelyOwned() );
        assertEquals( 5, returnedBloodbank.getEmployeeCount() );
        // if the dates are the same compareTo will return 0
        assertEquals(returnedBloodbank.getEstablished().compareTo(logic.convertStringToDate(sampleMap.get(BloodBankLogic.ESTABLISHED)[0])), 0);
      
        assertEquals( null, returnedBloodbank.getOwner() ); 
        assertEquals( sampleMap.get( BloodBankLogic.NAME )[ 0 ], returnedBloodbank.getName() );

        logic.delete(returnedBloodbank );
    }
    
    @Test
    final void testCreateEntity() {
        Map<String, String[]> sampleMap = new HashMap<>();
        sampleMap.put( BloodBankLogic.ID, new String[]{ Integer.toString(expectedEntity.getId()) } );
        sampleMap.put( BloodBankLogic.PRIVATELY_OWNED, new String[]{ Boolean.toString(expectedEntity.getPrivatelyOwned()) } );
        sampleMap.put( BloodBankLogic.ESTABLISHED, new String[]{ logic.convertDateToString(expectedEntity.getEstablished()) } );
        if (expectedEntity.getOwner() != null) 
            sampleMap.put( BloodBankLogic.OWNER_ID, new String[]{ expectedEntity.getOwner().getId().toString() } );
        sampleMap.put( BloodBankLogic.NAME, new String[]{ expectedEntity.getName() } );
        sampleMap.put( BloodBankLogic.EMPLOYEE_COUNT, new String[]{ Integer.toString(expectedEntity.getEmployeeCount()) } );

        BloodBank returnedBank = logic.createEntity( sampleMap );
        if (expectedEntity.getOwner() != null) {            
            returnedBank.setOwner(EMFactory.getEMF().createEntityManager().find( Person.class, personID));
        }
        
        assertBloodBanksEqual( expectedEntity, returnedBank );
    }

    @Test
    final void testCreateEntityNullAndEmptyValues() {
        Map<String, String[]> sampleMap = new HashMap<>();
        Consumer<Map<String, String[]>> fillMap = ( Map<String, String[]> map ) -> {
            map.clear();
            map.put( BloodBankLogic.ID, new String[]{ Integer.toString(expectedEntity.getId()) } );
            map.put( BloodBankLogic.PRIVATELY_OWNED, new String[]{ Boolean.toString(expectedEntity.getPrivatelyOwned()) } );
            map.put( BloodBankLogic.ESTABLISHED, new String[]{ expectedEntity.getEstablished().toString() } );
            if (expectedEntity.getOwner() != null) 
                map.put( BloodBankLogic.OWNER_ID, new String[]{ expectedEntity.getOwner().getId().toString() } );
            map.put( BloodBankLogic.NAME, new String[]{ expectedEntity.getName() } );
            map.put( BloodBankLogic.EMPLOYEE_COUNT, new String[]{ Integer.toString(expectedEntity.getEmployeeCount()) } );

        };
        
        fillMap.accept( sampleMap );
        sampleMap.replace( BloodBankLogic.ID, null );
        assertThrows( NullPointerException.class, () -> logic.createEntity( sampleMap ) );
        sampleMap.replace( BloodBankLogic.ID, new String[]{} );
        assertThrows( IndexOutOfBoundsException.class, () -> logic.createEntity( sampleMap ) );
        
        fillMap.accept( sampleMap );
        sampleMap.replace( BloodBankLogic.NAME, null );
        assertThrows( NullPointerException.class, () -> logic.createEntity( sampleMap ) );
        sampleMap.replace( BloodBankLogic.NAME, new String[]{} );
        assertThrows( IndexOutOfBoundsException.class, () -> logic.createEntity( sampleMap ) );
        
        fillMap.accept( sampleMap );
        sampleMap.replace( BloodBankLogic.PRIVATELY_OWNED, null );
        assertThrows( NullPointerException.class, () -> logic.createEntity( sampleMap ) );
        sampleMap.replace( BloodBankLogic.PRIVATELY_OWNED, new String[]{} );
        assertThrows( IndexOutOfBoundsException.class, () -> logic.createEntity( sampleMap ) );
                
        fillMap.accept( sampleMap );
        sampleMap.replace( BloodBankLogic.ESTABLISHED, null );
        assertThrows( NullPointerException.class, () -> logic.createEntity( sampleMap ) );
        sampleMap.replace( BloodBankLogic.ESTABLISHED, new String[]{} );
        assertThrows( IndexOutOfBoundsException.class, () -> logic.createEntity( sampleMap ) );
        
        fillMap.accept( sampleMap );
        sampleMap.replace( BloodBankLogic.EMPLOYEE_COUNT, null );
        assertThrows( NullPointerException.class, () -> logic.createEntity( sampleMap ) );
        sampleMap.replace( BloodBankLogic.EMPLOYEE_COUNT, new String[]{} );
        assertThrows( IndexOutOfBoundsException.class, () -> logic.createEntity( sampleMap ) );
                
    }
    
    @Test
    final void testCreateEntityBadLengthValues() {
        Map<String, String[]> sampleMap = new HashMap<>();
        Consumer<Map<String, String[]>> fillMap = ( Map<String, String[]> map ) -> {
            map.clear();
            map.put( BloodBankLogic.ID, new String[]{ Integer.toString(expectedEntity.getId()) } );
            map.put( BloodBankLogic.PRIVATELY_OWNED, new String[]{ Boolean.toString(expectedEntity.getPrivatelyOwned()) } );
            map.put( BloodBankLogic.ESTABLISHED, new String[]{ expectedEntity.getEstablished().toString() } );
            if (expectedEntity.getOwner() != null) 
                map.put( BloodBankLogic.OWNER_ID, new String[]{ expectedEntity.getOwner().getId().toString() } );
            map.put( BloodBankLogic.NAME, new String[]{ expectedEntity.getName() } );
            map.put( BloodBankLogic.EMPLOYEE_COUNT, new String[]{ Integer.toString(expectedEntity.getEmployeeCount()) } );
        };
        
        IntFunction<String> generateString = ( int length ) -> {
            //https://www.baeldung.com/java-random-string#java8-alphabetic
            //from 97 inclusive to 123 exclusive
            return new Random().ints( 'a', 'z' + 1 ).limit( length )
                    .collect( StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append )
                    .toString();
        };        
               
       /* This code seemingly does throw ValidationExceptions from createEntity, but the assert still fails */
        fillMap.accept( sampleMap );
        sampleMap.replace( BloodBankLogic.ID, new String[]{ "" } );
        assertThrows( ValidationException.class, () -> logic.createEntity( sampleMap ) );
        sampleMap.replace( BloodBankLogic.ID, new String[]{ "12b" } );
        assertThrows( ValidationException.class, () -> logic.createEntity( sampleMap ) );
        
       
        // Test PRIVATELY_OWNED with bad string lengths
        fillMap.accept( sampleMap );
        sampleMap.replace( BloodBankLogic.PRIVATELY_OWNED, new String[]{ "" } );
        assertThrows( common.ValidationException.class, () -> logic.createEntity( sampleMap ) );
        sampleMap.replace( BloodBankLogic.PRIVATELY_OWNED, new String[]{ generateString.apply( 46 ) } );
        assertThrows( common.ValidationException.class, () -> logic.createEntity( sampleMap ) );
       
        // Test ESTABLISHED with bad string lengths
        fillMap.accept( sampleMap );
        sampleMap.replace( BloodBankLogic.ESTABLISHED, new String[]{ "" } );
        assertThrows( common.ValidationException.class, () -> logic.createEntity( sampleMap ) );
        sampleMap.replace( BloodBankLogic.ESTABLISHED, new String[]{ generateString.apply( 46 ) } );
        assertThrows( common.ValidationException.class, () -> logic.createEntity( sampleMap ) );
        
        // Test NAME with bad string lengths
        fillMap.accept( sampleMap );
        sampleMap.replace( BloodBankLogic.NAME, new String[]{ "" } );
        assertThrows( common.ValidationException.class, () -> logic.createEntity( sampleMap ) );
        sampleMap.replace( BloodBankLogic.NAME, new String[]{ generateString.apply( 46 ) } );
        assertThrows( common.ValidationException.class, () -> logic.createEntity( sampleMap ) );
        
        // Test EMPLOYEE_COUNT with bad string lengths
        fillMap.accept( sampleMap );
        sampleMap.replace( BloodBankLogic.EMPLOYEE_COUNT, new String[]{ "" } );
        assertThrows( common.ValidationException.class, () -> logic.createEntity( sampleMap ) );
        sampleMap.replace( BloodBankLogic.EMPLOYEE_COUNT, new String[]{ generateString.apply( 46 ) } );
        assertThrows( common.ValidationException.class, () -> logic.createEntity( sampleMap ) );        
    }    
    
    @Test
    final void testExtractDataAsList() {
        List<?> list = logic.extractDataAsList( expectedEntity );
       assertEquals( expectedEntity.getId(), list.get( 0 ) );
       assertEquals( expectedEntity.getEmployeeCount(), list.get( 1 ) );  
       assertEquals( expectedEntity.getName(), list.get( 2 ) );
       assertEquals( expectedEntity.getEstablished(), list.get( 3 ) );
       assertEquals( expectedEntity.getPrivatelyOwned(), list.get( 4 ) );        
       if (expectedEntity.getOwner() != null)
           assertEquals( expectedEntity.getOwner().getId(), list.get( 5 ) );        
    }
    
    @Test
    final void testGetColumnNames() {
        List<String> list = logic.getColumnNames();
        assertEquals( Arrays.asList("ID", "EmployeeCount", "Name", "Established", 
                "PrivatelyOwned", "owner_id" ), list );
    }

    @Test
    final void testGetColumnCodes() {
        List<String> list = logic.getColumnCodes();
        assertEquals( Arrays.asList( BloodBankLogic.ID, BloodBankLogic.EMPLOYEE_COUNT, BloodBankLogic.NAME, BloodBankLogic.ESTABLISHED,
                BloodBankLogic.PRIVATELY_OWNED, BloodBankLogic.OWNER_ID ), list );
    }   
}
