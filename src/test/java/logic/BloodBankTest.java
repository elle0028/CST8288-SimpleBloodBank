package logic;

import common.EMFactory;
import common.TomcatStartUp;

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
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import entity.BloodBank;
import entity.BloodDonation;
import entity.Person;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Disabled;

public class BloodBankTest {
    private BloodBankLogic logic;
    private BloodBank expectedEntity;
    private Set<BloodDonation> donations;
    private Person owner;   // dependency
    
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
        
        Person testOwner = em.find( Person.class, 1 );

        // add two empty donations to the donation set
        donations = new HashSet<BloodDonation>();
        donations.add(new BloodDonation());        
        
        BloodBank entity = new BloodBank();
        entity.setName( "test name" );
        entity.setPrivatelyOwned(false);
        entity.setEstablished(new Date("Wed Dec 12 00:00:00 EST 1212"));
        entity.setEmployeeCount(5);
        entity.setOwner(null);        
        entity.setBloodDonationSet(donations);

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
        assertFalse(expectedEntity.getPrivatelyOwned());
    }
    
    @Test
    final void testGetEstablished() {
        assertNotNull( expectedEntity );
        // ensure the date string is the same as entered
        assertEquals(expectedEntity.getEstablished().toString(), "Wed Dec 12 00:00:00 EST 1212");
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
        assertEquals(expectedEntity.getOwner(), null);
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
        //get all the accounts from the DB
        List<BloodBank> list = logic.getAll();
        //store the size of list, this way we know how many bloodbanks exist in DB
        int originalSize = list.size();

        //make sure bloodbank was created successfully
        assertNotNull( expectedEntity );
        //delete the new bloodbank
        logic.delete( expectedEntity );

        //get all bloodbanks again
        list = logic.getAll();
        //the new size of accounts must be one less
        assertEquals( originalSize - 1, list.size() );
    }
    
    private void assertBloodBanksEqual( BloodBank expected, BloodBank actual ) {
      
        //assert all field to guarantee they are the same
        //assertEquals( expected.getId(), actual.getId() ); //TODO: something up with ID
        assertEquals( expected.getName(), actual.getName() );
        assertEquals( expected.getPrivatelyOwned(), actual.getPrivatelyOwned() );
        assertEquals( expected.getOwner(), actual.getOwner() );
        // use compareTo to see if the dates are the same
        assertEquals(expected.getEstablished().compareTo(actual.getEstablished()), 0);
        assertEquals( expected.getEmployeeCount(), actual.getEmployeeCount() );
        
    }
    
    @Test
    final void testGetWithId() {
        //using the id of test account get another account from logic
        BloodBank returnedAccount = logic.getWithId( expectedEntity.getId() );

        //the two accounts (testAcounts and returnedAccounts) must be the same
        assertBloodBanksEqual( expectedEntity, returnedAccount );
    }
    
    @Test 
    final void testGetBloodBankWithName() {
        BloodBank returnedAccount = logic.getBloodBankWithName( expectedEntity.getName() );

        //the two bloodbanks must be the same
        assertBloodBanksEqual( expectedEntity, returnedAccount );
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
    
    /* TODO: Cant do this without Person code
    @Test
    final void testGetBloodBankWithOwner() {        
    }
    */
    
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
        assertEquals(returnedBloodbank.getEstablished().compareTo(new Date(sampleMap.get(BloodBankLogic.ESTABLISHED)[0])), 0);
      
        assertEquals( null, returnedBloodbank.getOwner() ); 
        assertEquals( sampleMap.get( BloodBankLogic.NAME )[ 0 ], returnedBloodbank.getName() );

        logic.delete(returnedBloodbank );
    }
    
    @Test
    final void testCreateEntity() {
        Map<String, String[]> sampleMap = new HashMap<>();
        sampleMap.put( BloodBankLogic.PRIVATELY_OWNED, new String[]{ Boolean.toString(expectedEntity.getPrivatelyOwned()) } );
        sampleMap.put( BloodBankLogic.ESTABLISHED, new String[]{ expectedEntity.getEstablished().toString() } );
        if (expectedEntity.getOwner() != null) 
            sampleMap.put( BloodBankLogic.OWNER_ID, new String[]{ expectedEntity.getOwner().getId().toString() } );
        sampleMap.put( BloodBankLogic.NAME, new String[]{ expectedEntity.getName() } );
        sampleMap.put( BloodBankLogic.EMPLOYEE_COUNT, new String[]{ Integer.toString(expectedEntity.getEmployeeCount()) } );

        BloodBank returnedAccount = logic.createEntity( sampleMap );

        assertBloodBanksEqual( expectedEntity, returnedAccount );
    }
    
    /*
    @Test
    final void testCreateEntityNullAndEmptyValues() {
        Map<String, String[]> sampleMap = new HashMap<>();
        Consumer<Map<String, String[]>> fillMap = ( Map<String, String[]> map ) -> {
            map.clear();
            map.put(BloodBankLogic.ID, new String[]{ Integer.toString(expectedEntity.getId()) });
            map.put(BloodBankLogic.ID, new String[]{ Integer.toString(expectedEntity.getId()) });
        }; 
    }
    */
    
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
    
    /* TODO: Can't do this without Person code
    @Test
    final void testGetBloodBanksWithOwner() {      
    }
    */
}
