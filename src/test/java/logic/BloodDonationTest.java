package logic;

import common.EMFactory;
import common.TomcatStartUp;
import entity.BloodBank;
import entity.BloodDonation;
import entity.BloodGroup;
import entity.RhesusFactor;
import entity.RhesusFactorConvertor;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import javax.persistence.EntityManager;
import javax.validation.ValidationException;
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
 * This class is has the example of how to add dependency when working with junit. it is commented because some
 * components need to be made first. You will have to import everything you need.
 *
 * @author Shariar (Shawn) Emami, Matthew Ellero
 */
class BloodDonationTest {

    private BloodDonationLogic logic;
    private BloodDonation expectedEntity;
    private int bbId = 1;
    private final BloodGroup testBloodGroup = BloodGroup.AB;
    private final int testMilliliters = 100;
    private final RhesusFactor testRhd = RhesusFactor.Negative;
    private Date testDate;

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

        logic = LogicFactory.getFor( "BloodDonation" );

        //get an instance of EntityManager
        EntityManager em = EMFactory.getEMF().createEntityManager();
        //start a Transaction
        em.getTransaction().begin();
        //check if the depdendecy exists on DB already
        //em.find takes two arguments, the class type of return result and the primary key.
        BloodBank testBloodBank = em.find( BloodBank.class, bbId );
        //if result is null create the entity and persist it
        if( testBloodBank == null ){
            //create object
            testBloodBank = new BloodBank();
            testBloodBank.setName( "JUNIT" );
            testBloodBank.setPrivatelyOwned( true );
            testBloodBank.setEstablished( logic.convertStringToDate( "1111-11-11 11:11:11" ) );
            testBloodBank.setEmployeeCount( 111 );
            //persist the dependency first
            em.persist( testBloodBank );
        }
        
        bbId = testBloodBank.getId();
        testDate = logic.convertStringToDate( "1111-11-11 11:11:11" );

        //create the desired entity
        BloodDonation entity = new BloodDonation();
        entity.setMilliliters( testMilliliters );
        entity.setBloodGroup( testBloodGroup );
        entity.setRhd( testRhd );
        entity.setCreated( testDate );
        //add dependency to the desired entity
        entity.setBloodBank( testBloodBank );

        //add desired entity to hibernate, entity is now managed.
        //we use merge instead of add so we can get the managed entity.
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
        
        BloodBank testBloodBank = em.find( BloodBank.class, bbId );
        if (testBloodBank != null) {
            em.remove(testBloodBank);
        }
        //commit the changes
        em.getTransaction().commit();
        //close EntityManager
        em.close();
    }

    @Test
    final void testGetAll() {
        //get all the blood donations from the DB
        List<BloodDonation> list = logic.getAll();
        //store the size of list, this way we know how many blood donation exist in DB
        int originalSize = list.size();

        //make sure blood donation was created successfully
        assertNotNull( expectedEntity );
        //delete the new blood donation
        logic.delete( expectedEntity );

        //get all blood donations again
        list = logic.getAll();
        //the new size of blood donations must be one less
        assertEquals( originalSize - 1, list.size() );
    }
    
    /**
     * helper method for testing all blood donation fields
     *
     * @param expected
     * @param actual
     */
    private void assertBloodDonationEquals( BloodDonation expected, BloodDonation actual ) {
        //assert all field to guarantee they are the same
        assertEquals( expected.getId(), actual.getId() );
        assertEquals( expected.getBloodBank().getId(), actual.getBloodBank().getId());
        assertEquals( expected.getBloodGroup(), actual.getBloodGroup());
        assertEquals( expected.getCreated(), actual.getCreated());
        assertEquals( expected.getMilliliters(), actual.getMilliliters());
        assertEquals( expected.getRhd(), actual.getRhd());

    }
    
    @Test
    final void testGetWithId() {
        //using the id of test entity get another entity from logic
        BloodDonation returnedBloodDonation = logic.getWithId( expectedEntity.getId() );

        //the two blood donations must be the same
        assertBloodDonationEquals( expectedEntity, returnedBloodDonation );
    }
    
    @Test
    final void testGetBloodDonationsWithBloodBank(){
        List<BloodDonation> returnedBloodDonations =
                logic.getBloodDonationsWithBloodBank(expectedEntity.getBloodBank().getId());
        
        returnedBloodDonations.forEach(bloodDonation -> {
            // Use Hibernate to unproxy the retrieved bloodBank association 
            BloodBank returnedBloodBank = (BloodBank) Hibernate.unproxy(bloodDonation.getBloodBank());
            // Assert that all returned entities have the same blood bank
            assertEquals(expectedEntity.getBloodBank(), returnedBloodBank);
        });
    }
    
    @Test
    final void testGetBloodDonationsWithBloodGroup(){
        List<BloodDonation> returnedBloodDonations =
                logic.getBloodDonationsWithBloodGroup(expectedEntity.getBloodGroup());
        returnedBloodDonations.forEach(bloodDonation -> {
            // Assert that all returned entities have the same blood group
            assertEquals(expectedEntity.getBloodGroup(), bloodDonation.getBloodGroup());
        });
    }
    
    @Test
    final void testGetBloodDonationsWithCreated(){
        List<BloodDonation> returnedBloodDonations =
                logic.getBloodDonationsWithCreated(expectedEntity.getCreated());
        returnedBloodDonations.forEach(bloodDonation -> {
            // Assert that all returned entities have the same created date
            assertEquals(expectedEntity.getCreated(), bloodDonation.getCreated());
        });
    }
    
    @Test
    final void testGetBloodDonationsWithMilliliters(){
        List<BloodDonation> returnedBloodDonations =
                logic.getBloodDonationsWithMilliliters(expectedEntity.getMilliliters());
        returnedBloodDonations.forEach(bloodDonation -> {
            // Assert that all returned entities have the same milliliter amount
            assertEquals(expectedEntity.getMilliliters(), bloodDonation.getMilliliters());
        });
    }
    
    @Test
    final void testGetBloodDonationsWithRhd(){
        List<BloodDonation> returnedBloodDonations =
                logic.getBloodDonationsWithRhd(expectedEntity.getRhd());
        returnedBloodDonations.forEach(bloodDonation -> {
            // Assert that all returned entities have the same rhesus factor
            assertEquals(expectedEntity.getRhd(), bloodDonation.getRhd());
        });
    }
>>>>>>> d6abd822200f92886b6ea3bbf443cb0477f4cb2a
//
//    @Test
//    final void testSearch() {
////        int foundFull = 0;
////        //search for a substring of one of the fields in the expectedBloodDonation
////        String searchString = expectedEntity.getBloodGroup().substring( 3 );
////        //in account we only search for display name and user, this is completely based on your design for other entities.
////        List<BloodDonation> returnedBloodDonations = logic.search( searchString );
////        for( BloodDonation account: returnedBloodDonations ) {
////            //all accounts must contain the substring
////            assertTrue( account.getNickname().contains( searchString ) || account.getUsername().contains( searchString ) );
////            //exactly one account must be the same
////            if( account.getId().equals( expectedEntity.getId() ) ){
////                assertBloodDonationEquals( expectedEntity, account );
////                foundFull++;
////            }
////        }
////        assertEquals( 1, foundFull, "if zero means not found, if more than one means duplicate" );
//    }
<<<<<<< HEAD
//
//    @Test
//    final void testCreateEntityAndAdd() {
//        Map<String, String[]> sampleMap = new HashMap<>();
//        sampleMap.put( BloodDonationLogic.MILLILITERS, new String[]{ Integer.toString(testMilliliters) } );
//        sampleMap.put( BloodDonationLogic.BLOOD_GROUP, new String[]{ testBloodGroup.toString() } );
//        sampleMap.put( BloodDonationLogic.RHESUS_FACTOR, new String[]{ testRhd.toString() } );
//        sampleMap.put( BloodDonationLogic.CREATED, new String[]{ logic.convertDateToString(testDate) } );
//        
//        BloodDonation returnedBloodDonation = logic.createEntity( sampleMap );
//        // Add Blood Bank dependency
//        returnedBloodDonation.setBloodBank(
//                EMFactory.getEMF().createEntityManager().find( BloodBank.class,  bbId)
//        );
//        
//        logic.add( returnedBloodDonation );
//
//        returnedBloodDonation = 
//                logic.getBloodDonationsWithBloodBank(returnedBloodDonation.getBloodBank().getId())
//                     .get(0);
//
//        assertEquals( bbId, returnedBloodDonation.getBloodBank().getId());
//        assertEquals( Integer.valueOf(sampleMap.get( BloodDonationLogic.MILLILITERS )[ 0 ]), 
//                      returnedBloodDonation.getMilliliters());
//        assertEquals( BloodGroup.valueOf(sampleMap.get( BloodDonationLogic.BLOOD_GROUP )[ 0 ]), 
//                      returnedBloodDonation.getBloodGroup());
//        assertEquals( RhesusFactor.getRhesusFactor(sampleMap.get( BloodDonationLogic.RHESUS_FACTOR )[ 0 ]), 
//                      returnedBloodDonation.getRhd() );
//        assertEquals( logic.convertStringToDate(sampleMap.get( BloodDonationLogic.CREATED )[ 0 ]), 
//                      returnedBloodDonation.getCreated() );
//
//        logic.delete( returnedBloodDonation );
//    }
//
//    @Test
//    final void testCreateEntity() {
//        Map<String, String[]> sampleMap = new HashMap<>();
//        sampleMap.put( BloodDonationLogic.ID, new String[]{ Integer.toString( expectedEntity.getId() ) } );
//        sampleMap.put( BloodDonationLogic.MILLILITERS, new String[]{ Integer.toString(expectedEntity.getMilliliters()) } );
//        sampleMap.put( BloodDonationLogic.BLOOD_GROUP, new String[]{ expectedEntity.getBloodGroup().toString() } );
//        sampleMap.put( BloodDonationLogic.RHESUS_FACTOR, new String[]{ expectedEntity.getRhd().toString() } );
//        sampleMap.put( BloodDonationLogic.CREATED, new String[]{ logic.convertDateToString(expectedEntity.getCreated()) } );
//
//        BloodDonation returnedBloodDonation = logic.createEntity( sampleMap );
//        
//        // Add Blood Bank dependency
//        int bank_id = expectedEntity.getBloodBank().getId(); 
//        returnedBloodDonation.setBloodBank(
//                EMFactory.getEMF().createEntityManager().find( BloodBank.class,  bank_id)
//        );
//
//        assertBloodDonationEquals( expectedEntity, returnedBloodDonation );
//    }
//
//    @Test
//    final void testCreateEntityNullAndEmptyValues() {
//        Map<String, String[]> sampleMap = new HashMap<>();
//        Consumer<Map<String, String[]>> fillMap = ( Map<String, String[]> map ) -> {
//            map.clear();
//            map.put( BloodDonationLogic.ID, new String[]{ Integer.toString( expectedEntity.getId() ) } );
//            map.put( BloodDonationLogic.MILLILITERS, new String[]{ Integer.toString(expectedEntity.getMilliliters()) } );
//            map.put( BloodDonationLogic.BLOOD_GROUP, new String[]{ expectedEntity.getBloodGroup().toString() } );
//            map.put( BloodDonationLogic.RHESUS_FACTOR, new String[]{ expectedEntity.getRhd().toString() } );
//            map.put( BloodDonationLogic.CREATED, new String[]{ logic.convertDateToString(expectedEntity.getCreated()) } );
//        };
//
//        //idealy every test should be in its own method
//        fillMap.accept( sampleMap );
//        sampleMap.replace( BloodDonationLogic.ID, null );
//        assertThrows( NullPointerException.class, () -> logic.createEntity( sampleMap ) );
//        sampleMap.replace( BloodDonationLogic.ID, new String[]{} );
//        assertThrows( IndexOutOfBoundsException.class, () -> logic.createEntity( sampleMap ) );
//
//        fillMap.accept( sampleMap );
//        sampleMap.replace( BloodDonationLogic.MILLILITERS, null );
//        assertThrows( NullPointerException.class, () -> logic.createEntity( sampleMap ) );
//        sampleMap.replace( BloodDonationLogic.MILLILITERS, new String[]{} );
//
//        fillMap.accept( sampleMap );
//        sampleMap.replace( BloodDonationLogic.BLOOD_GROUP, null );
//        assertThrows( NullPointerException.class, () -> logic.createEntity( sampleMap ) );
//        sampleMap.replace( BloodDonationLogic.BLOOD_GROUP, new String[]{} );
//        assertThrows( IndexOutOfBoundsException.class, () -> logic.createEntity( sampleMap ) );
//
//        fillMap.accept( sampleMap );
//        sampleMap.replace( BloodDonationLogic.RHESUS_FACTOR, null );
//        assertThrows( NullPointerException.class, () -> logic.createEntity( sampleMap ) );
//        sampleMap.replace( BloodDonationLogic.RHESUS_FACTOR, new String[]{} );
//        assertThrows( IndexOutOfBoundsException.class, () -> logic.createEntity( sampleMap ) );
//        
//        fillMap.accept( sampleMap );
//        sampleMap.replace( BloodDonationLogic.CREATED, null );
//        assertThrows( NullPointerException.class, () -> logic.createEntity( sampleMap ) );
//        sampleMap.replace( BloodDonationLogic.CREATED, new String[]{} );
//        assertThrows( IndexOutOfBoundsException.class, () -> logic.createEntity( sampleMap ) );
//    }
//
//    @Test
//    final void testCreateEntityBadBloodGroupValue() {
//        Map<String, String[]> sampleMap = new HashMap<>();
//        Consumer<Map<String, String[]>> fillMap = ( Map<String, String[]> map ) -> {
//            map.clear();
//            map.put( BloodDonationLogic.ID, new String[]{ Integer.toString( expectedEntity.getId() ) } );
//            map.put( BloodDonationLogic.MILLILITERS, new String[]{ Integer.toString(expectedEntity.getMilliliters()) } );
//            map.put( BloodDonationLogic.BLOOD_GROUP, new String[]{ expectedEntity.getBloodGroup().toString() } );
//            map.put( BloodDonationLogic.RHESUS_FACTOR, new String[]{ expectedEntity.getRhd().toString() } );
//            map.put( BloodDonationLogic.CREATED, new String[]{ logic.convertDateToString(expectedEntity.getCreated()) } );
//        };
//
//        //idealy every test should be in its own method
//        fillMap.accept( sampleMap );
//        sampleMap.replace( BloodDonationLogic.BLOOD_GROUP, new String[]{ "" } );
//        assertThrows( IllegalArgumentException.class, () -> logic.createEntity( sampleMap ) );
//    }
//    
//    @Test
//    final void testCreateEntityBadRhesusFactorValue() {
//        Map<String, String[]> sampleMap = new HashMap<>();
//        Consumer<Map<String, String[]>> fillMap = ( Map<String, String[]> map ) -> {
//            map.clear();
//            map.put( BloodDonationLogic.ID, new String[]{ Integer.toString( expectedEntity.getId() ) } );
//            map.put( BloodDonationLogic.MILLILITERS, new String[]{ Integer.toString(expectedEntity.getMilliliters()) } );
//            map.put( BloodDonationLogic.BLOOD_GROUP, new String[]{ expectedEntity.getBloodGroup().toString() } );
//            map.put( BloodDonationLogic.RHESUS_FACTOR, new String[]{ expectedEntity.getRhd().toString() } );
//            map.put( BloodDonationLogic.CREATED, new String[]{ logic.convertDateToString(expectedEntity.getCreated()) } );
//        };
//
//        //idealy every test should be in its own method
//        fillMap.accept( sampleMap );
//        sampleMap.replace( BloodDonationLogic.RHESUS_FACTOR, new String[]{ "" } );
//        assertThrows( IllegalArgumentException.class, () -> logic.createEntity( sampleMap ) );
//    }
//    
//    @Test
//    final void testCreateEntityBadCreatedValue() {
//        Map<String, String[]> sampleMap = new HashMap<>();
//        Consumer<Map<String, String[]>> fillMap = ( Map<String, String[]> map ) -> {
//            map.clear();
//            map.put( BloodDonationLogic.ID, new String[]{ Integer.toString( expectedEntity.getId() ) } );
//            map.put( BloodDonationLogic.MILLILITERS, new String[]{ Integer.toString(expectedEntity.getMilliliters()) } );
//            map.put( BloodDonationLogic.BLOOD_GROUP, new String[]{ expectedEntity.getBloodGroup().toString() } );
//            map.put( BloodDonationLogic.RHESUS_FACTOR, new String[]{ expectedEntity.getRhd().toString() } );
//            map.put( BloodDonationLogic.CREATED, new String[]{ logic.convertDateToString(expectedEntity.getCreated()) } );
//        };
//
//        //idealy every test should be in its own method
//        fillMap.accept( sampleMap );
//        sampleMap.replace( BloodDonationLogic.CREATED, new String[]{ "" } );
//        
//        // Assert that the current date minus one minute is before the date that is created, if a bad 
//        // value is supplied. Ie., If the given date in unparsable, we use a "current" date
//        Date withinOneMinute = new Date(System.currentTimeMillis() - (60 * 1000));
//        assertTrue(withinOneMinute.before(logic.createEntity( sampleMap ).getCreated()));
//    }
//
//    @Test
//    final void testGetColumnNames() {
//        List<String> list = logic.getColumnNames();
//        assertEquals( Arrays.asList( "ID", "Blood Bank ID", "Milliliters",
//                              "Blood Group", "Rhesus Factor",  "Created" ), list );
//    }
//
//    @Test
//    final void testGetColumnCodes() {
//        List<String> list = logic.getColumnCodes();
//        assertEquals( Arrays.asList( 
//                    BloodDonationLogic.ID, BloodDonationLogic.BANK_ID, 
//                    BloodDonationLogic.MILLILITERS, BloodDonationLogic.BLOOD_GROUP, 
//                    BloodDonationLogic.RHESUS_FACTOR, BloodDonationLogic.CREATED 
//                    ), list );
//    }
//
//    @Test
//    final void testExtractDataAsList() {
//        List<?> list = logic.extractDataAsList( expectedEntity );
//        assertEquals( expectedEntity.getId(), list.get( 0 ) );
//        assertEquals( expectedEntity.getBloodBank().getId(), list.get( 1 ) );
//        assertEquals( expectedEntity.getMilliliters(), list.get( 2 ) );
//        assertEquals( expectedEntity.getBloodGroup(), list.get( 3 ) );
//        assertEquals( expectedEntity.getRhd(), list.get( 4 ) );
//        assertEquals( expectedEntity.getCreated(), list.get( 5 ) );
//    }
//    
//    
//}
=======

    @Test
    final void testCreateEntityAndAdd() {
        Map<String, String[]> sampleMap = new HashMap<>();
        sampleMap.put( BloodDonationLogic.MILLILITERS, new String[]{ Integer.toString(testMilliliters) } );
        sampleMap.put( BloodDonationLogic.BLOOD_GROUP, new String[]{ testBloodGroup.toString() } );
        sampleMap.put( BloodDonationLogic.RHESUS_FACTOR, new String[]{ testRhd.toString() } );
        sampleMap.put( BloodDonationLogic.CREATED, new String[]{ logic.convertDateToString(testDate) } );
        
        BloodDonation createdBloodDonation = logic.createEntity( sampleMap );
        // Add Blood Bank dependency
        createdBloodDonation.setBloodBank(
                EMFactory.getEMF().createEntityManager().find( BloodBank.class,  bbId)
        );
        
        logic.add( createdBloodDonation );

        BloodDonation returnedBloodDonation = 
                logic.getBloodDonationsWithBloodBank(createdBloodDonation.getBloodBank().getId())
                     .get(0);

        assertEquals( createdBloodDonation.getBloodBank().getId(), returnedBloodDonation.getBloodBank().getId());
        assertEquals( createdBloodDonation.getMilliliters(), returnedBloodDonation.getMilliliters());
        assertEquals( createdBloodDonation.getBloodGroup(), returnedBloodDonation.getBloodGroup());
        assertEquals( createdBloodDonation.getRhd(), returnedBloodDonation.getRhd() );
        assertEquals( createdBloodDonation.getCreated(), returnedBloodDonation.getCreated() );

        logic.delete( createdBloodDonation );
    }

    @Test
    final void testCreateEntity() {
        Map<String, String[]> sampleMap = new HashMap<>();
        sampleMap.put( BloodDonationLogic.ID, new String[]{ Integer.toString( expectedEntity.getId() ) } );
        sampleMap.put( BloodDonationLogic.MILLILITERS, new String[]{ Integer.toString(expectedEntity.getMilliliters()) } );
        sampleMap.put( BloodDonationLogic.BLOOD_GROUP, new String[]{ expectedEntity.getBloodGroup().toString() } );
        sampleMap.put( BloodDonationLogic.RHESUS_FACTOR, new String[]{ expectedEntity.getRhd().toString() } );
        sampleMap.put( BloodDonationLogic.CREATED, new String[]{ logic.convertDateToString(expectedEntity.getCreated()) } );

        BloodDonation returnedBloodDonation = logic.createEntity( sampleMap );
        
        // Add Blood Bank dependency
        int bank_id = expectedEntity.getBloodBank().getId(); 
        returnedBloodDonation.setBloodBank(
                EMFactory.getEMF().createEntityManager().find( BloodBank.class,  bank_id)
        );

        assertBloodDonationEquals( expectedEntity, returnedBloodDonation );
    }

    @Test
    final void testCreateEntityNullAndEmptyValues() {
        Map<String, String[]> sampleMap = new HashMap<>();
        Consumer<Map<String, String[]>> fillMap = ( Map<String, String[]> map ) -> {
            map.clear();
            map.put( BloodDonationLogic.ID, new String[]{ Integer.toString( expectedEntity.getId() ) } );
            map.put( BloodDonationLogic.MILLILITERS, new String[]{ Integer.toString(expectedEntity.getMilliliters()) } );
            map.put( BloodDonationLogic.BLOOD_GROUP, new String[]{ expectedEntity.getBloodGroup().toString() } );
            map.put( BloodDonationLogic.RHESUS_FACTOR, new String[]{ expectedEntity.getRhd().toString() } );
            map.put( BloodDonationLogic.CREATED, new String[]{ logic.convertDateToString(expectedEntity.getCreated()) } );
        };

        //idealy every test should be in its own method
        fillMap.accept( sampleMap );
        sampleMap.replace( BloodDonationLogic.ID, null );
        assertThrows( NullPointerException.class, () -> logic.createEntity( sampleMap ) );
        sampleMap.replace( BloodDonationLogic.ID, new String[]{} );
        assertThrows( IndexOutOfBoundsException.class, () -> logic.createEntity( sampleMap ) );

        fillMap.accept( sampleMap );
        sampleMap.replace( BloodDonationLogic.MILLILITERS, null );
        assertThrows( NullPointerException.class, () -> logic.createEntity( sampleMap ) );
        sampleMap.replace( BloodDonationLogic.MILLILITERS, new String[]{} );

        fillMap.accept( sampleMap );
        sampleMap.replace( BloodDonationLogic.BLOOD_GROUP, null );
        assertThrows( NullPointerException.class, () -> logic.createEntity( sampleMap ) );
        sampleMap.replace( BloodDonationLogic.BLOOD_GROUP, new String[]{} );
        assertThrows( IndexOutOfBoundsException.class, () -> logic.createEntity( sampleMap ) );

        fillMap.accept( sampleMap );
        sampleMap.replace( BloodDonationLogic.RHESUS_FACTOR, null );
        assertThrows( NullPointerException.class, () -> logic.createEntity( sampleMap ) );
        sampleMap.replace( BloodDonationLogic.RHESUS_FACTOR, new String[]{} );
        assertThrows( IndexOutOfBoundsException.class, () -> logic.createEntity( sampleMap ) );
        
        fillMap.accept( sampleMap );
        sampleMap.replace( BloodDonationLogic.CREATED, null );
        assertThrows( NullPointerException.class, () -> logic.createEntity( sampleMap ) );
        sampleMap.replace( BloodDonationLogic.CREATED, new String[]{} );
        assertThrows( IndexOutOfBoundsException.class, () -> logic.createEntity( sampleMap ) );
    }

    @Test
    final void testCreateEntityBadBloodGroupValue() {
        Map<String, String[]> sampleMap = new HashMap<>();
        Consumer<Map<String, String[]>> fillMap = ( Map<String, String[]> map ) -> {
            map.clear();
            map.put( BloodDonationLogic.ID, new String[]{ Integer.toString( expectedEntity.getId() ) } );
            map.put( BloodDonationLogic.MILLILITERS, new String[]{ Integer.toString(expectedEntity.getMilliliters()) } );
            map.put( BloodDonationLogic.BLOOD_GROUP, new String[]{ expectedEntity.getBloodGroup().toString() } );
            map.put( BloodDonationLogic.RHESUS_FACTOR, new String[]{ expectedEntity.getRhd().toString() } );
            map.put( BloodDonationLogic.CREATED, new String[]{ logic.convertDateToString(expectedEntity.getCreated()) } );
        };

        //idealy every test should be in its own method
        fillMap.accept( sampleMap );
        sampleMap.replace( BloodDonationLogic.BLOOD_GROUP, new String[]{ "" } );
        assertThrows( IllegalArgumentException.class, () -> logic.createEntity( sampleMap ) );
    }
    
    @Test
    final void testCreateEntityBadRhesusFactorValue() {
        Map<String, String[]> sampleMap = new HashMap<>();
        Consumer<Map<String, String[]>> fillMap = ( Map<String, String[]> map ) -> {
            map.clear();
            map.put( BloodDonationLogic.ID, new String[]{ Integer.toString( expectedEntity.getId() ) } );
            map.put( BloodDonationLogic.MILLILITERS, new String[]{ Integer.toString(expectedEntity.getMilliliters()) } );
            map.put( BloodDonationLogic.BLOOD_GROUP, new String[]{ expectedEntity.getBloodGroup().toString() } );
            map.put( BloodDonationLogic.RHESUS_FACTOR, new String[]{ expectedEntity.getRhd().toString() } );
            map.put( BloodDonationLogic.CREATED, new String[]{ logic.convertDateToString(expectedEntity.getCreated()) } );
        };

        //idealy every test should be in its own method
        fillMap.accept( sampleMap );
        sampleMap.replace( BloodDonationLogic.RHESUS_FACTOR, new String[]{ "" } );
        assertThrows( IllegalArgumentException.class, () -> logic.createEntity( sampleMap ) );
    }
    
    @Test
    final void testCreateEntityBadCreatedValue() {
        Map<String, String[]> sampleMap = new HashMap<>();
        Consumer<Map<String, String[]>> fillMap = ( Map<String, String[]> map ) -> {
            map.clear();
            map.put( BloodDonationLogic.ID, new String[]{ Integer.toString( expectedEntity.getId() ) } );
            map.put( BloodDonationLogic.MILLILITERS, new String[]{ Integer.toString(expectedEntity.getMilliliters()) } );
            map.put( BloodDonationLogic.BLOOD_GROUP, new String[]{ expectedEntity.getBloodGroup().toString() } );
            map.put( BloodDonationLogic.RHESUS_FACTOR, new String[]{ expectedEntity.getRhd().toString() } );
            map.put( BloodDonationLogic.CREATED, new String[]{ logic.convertDateToString(expectedEntity.getCreated()) } );
        };

        //idealy every test should be in its own method
        fillMap.accept( sampleMap );
        sampleMap.replace( BloodDonationLogic.CREATED, new String[]{ "" } );
        
        // Assert that the current date minus one minute is before the date that is created, if a bad 
        // value is supplied. Ie., If the given date in unparsable, we use a "current" date
        Date withinOneMinute = new Date(System.currentTimeMillis() - (60 * 1000));
        assertTrue(withinOneMinute.before(logic.createEntity( sampleMap ).getCreated()));
    }

    @Test
    final void testGetColumnNames() {
        List<String> list = logic.getColumnNames();
        assertEquals( Arrays.asList( "ID", "Blood Bank ID", "Milliliters",
                              "Blood Group", "Rhesus Factor",  "Created" ), list );
    }

    @Test
    final void testGetColumnCodes() {
        List<String> list = logic.getColumnCodes();
        assertEquals( Arrays.asList( 
                    BloodDonationLogic.ID, BloodDonationLogic.BANK_ID, 
                    BloodDonationLogic.MILLILITERS, BloodDonationLogic.BLOOD_GROUP, 
                    BloodDonationLogic.RHESUS_FACTOR, BloodDonationLogic.CREATED 
                    ), list );
    }

    @Test
    final void testExtractDataAsList() {
        List<?> list = logic.extractDataAsList( expectedEntity );
        assertEquals( expectedEntity.getId(), list.get( 0 ) );
        assertEquals( expectedEntity.getBloodBank().getId(), list.get( 1 ) );
        assertEquals( expectedEntity.getMilliliters(), list.get( 2 ) );
        assertEquals( expectedEntity.getBloodGroup(), list.get( 3 ) );
        assertEquals( expectedEntity.getRhd(), list.get( 4 ) );
        assertEquals( expectedEntity.getCreated(), list.get( 5 ) );
    }
}
>>>>>>> d6abd822200f92886b6ea3bbf443cb0477f4cb2a
