package logic;

import common.EMFactory;
import common.TomcatStartUp;
import common.ValidationException;
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
 * @author Fargol Azimi
 */
class PersonTest {

    private PersonLogic logic;
    private Person expectedEntity;

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

        logic = LogicFactory.getFor( "Person" );
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

        Person entity = new Person();
        entity.setFirstName( "Mike" );
        entity.setLastName( "Johnson" );
        entity.setPhone( "4379712211" );
        entity.setAddress( "Alberta" );
        entity.setBirth(logic.convertStringToDate( "1998-02-18 13:15:15" ));

        //add a person to hibernate, person is now managed.
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
        //get all the persons from the DB
        List<Person> list = logic.getAll();
        //store the size of list, this way we know how many people exist in the DB
        int originalSize = list.size();

        //make sure person was created successfully
        assertNotNull( expectedEntity );
        //delete the new person
        logic.delete( expectedEntity );

        //get all persons again
        list = logic.getAll();
        //the new size of persons must be one less
        assertEquals( originalSize - 1, list.size() );
    }

    /**
     * helper method for testing all person fields
     *
     * @param expected
     * @param actual
     */
    private void assertPersonEquals( Person expected, Person actual ) {
        //assert all field to guarantee they are the same
        assertEquals( expected.getId(), actual.getId() );
        assertEquals( expected.getFirstName(), actual.getFirstName() );
        assertEquals( expected.getLastName(), actual.getLastName() );
        assertEquals( expected.getPhone(), actual.getPhone() );
        assertEquals( expected.getAddress(), actual.getAddress() );
        assertEquals( expected.getBirth(), actual.getBirth() );
    }

    @Test
    final void testGetWithId() {
        //using the id of test person get another person from logic
        Person returnedPerson = logic.getWithId( expectedEntity.getId() );

        //the two persons (testPerson and returnedPerson) must be the same
        assertPersonEquals( expectedEntity, returnedPerson );
    }

    @Test
    final void testGetPersonWithFirstName() {
        List <Person> returnedPerson = logic.getPersonWithFirstName( expectedEntity.getFirstName());

        //the two persons (testPerson and returnedPerson must be the same
        returnedPerson.forEach(person -> {
            assertEquals(expectedEntity.getFirstName(), person.getFirstName());
        });
    }

    @Test
    final void testGetPersonWithLastName() {
        List <Person> returnedPerson = logic.getPersonWithLastName( expectedEntity.getLastName());

        //the two persons (testPerson and returnedPerson) must be the same
        returnedPerson.forEach(person -> {
            assertEquals(expectedEntity.getLastName(), person.getLastName());
        });
    }

    @Test
    final void testGetPersonWithPhone() {
        List <Person> returnedPerson = logic.getPersonWithPhone( expectedEntity.getPhone());

        //the two accounts (testAcounts and returnedAccounts) must be the same
        returnedPerson.forEach(person -> {
            assertEquals(expectedEntity.getPhone(), person.getPhone());
        });
    }

    @Test
    final void testGetPersonsWithAddress() {
        List <Person> returnedPerson = logic.getPersonsWithAddress( expectedEntity.getAddress());

        //the two accounts (testAcounts and returnedAccounts) must be the same
        returnedPerson.forEach(person -> {
            assertEquals(expectedEntity.getAddress(), person.getAddress());
        });
    }
    
    @Test
    final void testGetPersonsWithBirth() {
        List <Person> returnedPerson = logic.getPersonsWithBirth( expectedEntity.getBirth());

        //the two accounts (testAcounts and returnedAccounts) must be the same
        returnedPerson.forEach(person -> {
            assertEquals(expectedEntity.getBirth(), person.getBirth());
        });
    }


    @Test
    final void testCreateEntityAndAdd() {
        Map<String, String[]> sampleMap = new HashMap<>();
        sampleMap.put( PersonLogic.FIRST_NAME, new String[]{ "Test Create Entity" } );
        sampleMap.put( PersonLogic.LAST_NAME, new String[]{ "testCreatePerson" } );
        sampleMap.put( PersonLogic.PHONE, new String[]{ "create" } );
        sampleMap.put( PersonLogic.ADDRESS, new String[]{ "create" } );
        sampleMap.put( PersonLogic.BIRTH, new String[]{ "1998-02-18 13:15:15" } );

        Person returnedPerson = logic.createEntity( sampleMap );
        logic.add( returnedPerson );

        returnedPerson = logic.getWithId( returnedPerson.getId());


        assertEquals( sampleMap.get( PersonLogic.FIRST_NAME )[ 0 ], returnedPerson.getFirstName() );
        assertEquals( sampleMap.get( PersonLogic.LAST_NAME )[ 0 ], returnedPerson.getLastName() );
        assertEquals( sampleMap.get( PersonLogic.PHONE )[ 0 ], returnedPerson.getPhone() );
        assertEquals( sampleMap.get( PersonLogic.ADDRESS )[ 0 ], returnedPerson.getAddress() );
        assertEquals( sampleMap.get( PersonLogic.BIRTH)[ 0 ], logic.convertDateToString(returnedPerson.getBirth()) );

        logic.delete( returnedPerson );
    }

    @Test
    final void testCreateEntity() {
        Map<String, String[]> sampleMap = new HashMap<>();
        sampleMap.put( PersonLogic.ID, new String[]{ Integer.toString( expectedEntity.getId() ) } );
        sampleMap.put( PersonLogic.FIRST_NAME, new String[]{ expectedEntity.getFirstName() } );
        sampleMap.put( PersonLogic.LAST_NAME, new String[]{ expectedEntity.getLastName() } );
        sampleMap.put( PersonLogic.PHONE, new String[]{ expectedEntity.getPhone() } );
        sampleMap.put( PersonLogic.ADDRESS, new String[]{ expectedEntity.getAddress() } );
        sampleMap.put( PersonLogic.BIRTH, new String[]{ logic.convertDateToString(expectedEntity.getBirth()) } );

        Person returnedPerson= logic.createEntity( sampleMap );

       assertPersonEquals( expectedEntity, returnedPerson );
    }

    @Test
    final void testCreateEntityNullAndEmptyValues() {
        Map<String, String[]> sampleMap = new HashMap<>();
        Consumer<Map<String, String[]>> fillMap = ( Map<String, String[]> map ) -> {
            map.clear();
        map.put( PersonLogic.ID, new String[]{ Integer.toString( expectedEntity.getId() ) }  );
        map.put( PersonLogic.FIRST_NAME, new String[]{ expectedEntity.getFirstName() } );
        map.put( PersonLogic.LAST_NAME, new String[]{ expectedEntity.getLastName() } );
        map.put( PersonLogic.PHONE, new String[]{ expectedEntity.getPhone() } );
        map.put( PersonLogic.ADDRESS, new String[]{ expectedEntity.getAddress() } );
        map.put( PersonLogic.BIRTH, new String[]{ logic.convertDateToString(expectedEntity.getBirth()) } );
        };

        //idealy every test should be in its own method
        //ID
        fillMap.accept( sampleMap );
        sampleMap.replace( PersonLogic.ID, null );
        assertThrows( NullPointerException.class, () -> logic.createEntity( sampleMap ) );
        sampleMap.replace( PersonLogic.ID, new String[]{} );
        assertThrows( IndexOutOfBoundsException.class, () -> logic.createEntity( sampleMap ) );

        //First_Name
        fillMap.accept( sampleMap );
        sampleMap.replace( PersonLogic.FIRST_NAME, null );
        assertThrows( NullPointerException.class, () -> logic.createEntity( sampleMap ) );
        sampleMap.replace( PersonLogic.FIRST_NAME, new String[]{} );
        assertThrows( IndexOutOfBoundsException.class, () -> logic.createEntity( sampleMap ) );

        //Last_Name
        fillMap.accept( sampleMap );
        sampleMap.replace( PersonLogic.LAST_NAME, null );
        assertThrows( NullPointerException.class, () -> logic.createEntity( sampleMap ) );
        sampleMap.replace( PersonLogic.LAST_NAME, new String[]{} );
        assertThrows( IndexOutOfBoundsException.class, () -> logic.createEntity( sampleMap ) );

        //Phone
        fillMap.accept( sampleMap );
        sampleMap.replace( PersonLogic.PHONE, null );
        assertThrows( NullPointerException.class, () -> logic.createEntity( sampleMap ) );
        sampleMap.replace( PersonLogic.PHONE, new String[]{} );
        assertThrows( IndexOutOfBoundsException.class, () -> logic.createEntity( sampleMap ) );

        //Address
        fillMap.accept( sampleMap );
        sampleMap.replace( PersonLogic.ADDRESS, null );
        assertThrows( NullPointerException.class, () -> logic.createEntity( sampleMap ) );
        sampleMap.replace( PersonLogic.ADDRESS, new String[]{} );
        assertThrows( IndexOutOfBoundsException.class, () -> logic.createEntity( sampleMap ) );
        
        //Birth
        fillMap.accept( sampleMap );
        sampleMap.replace( PersonLogic.BIRTH, null );
        assertThrows( NullPointerException.class, () -> logic.createEntity( sampleMap ) );
        sampleMap.replace( PersonLogic.BIRTH, new String[]{} );
        assertThrows( IndexOutOfBoundsException.class, () -> logic.createEntity( sampleMap ) );
    }

    @Test
    final void testCreateEntityBadLengthValues() {
        Map<String, String[]> sampleMap = new HashMap<>();
        Consumer<Map<String, String[]>> fillMap = ( Map<String, String[]> map ) -> {
            map.clear();
            map.put( PersonLogic.ID, new String[]{ Integer.toString( expectedEntity.getId() ) } );
            map.put( PersonLogic.FIRST_NAME, new String[]{ expectedEntity.getFirstName() } );
            map.put( PersonLogic.LAST_NAME, new String[]{ expectedEntity.getLastName() } );
            map.put( PersonLogic.PHONE, new String[]{ expectedEntity.getPhone() } );
            map.put( PersonLogic.ADDRESS, new String[]{ expectedEntity.getAddress() } );
            map.put( PersonLogic.BIRTH, new String[]{ logic.convertDateToString(expectedEntity.getBirth()) } );
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
        sampleMap.replace( PersonLogic.ID, new String[]{ "" } );
        assertThrows( ValidationException.class, () -> logic.createEntity( sampleMap ) );
        sampleMap.replace( PersonLogic.ID, new String[]{ "12b" } );
        assertThrows( ValidationException.class, () -> logic.createEntity( sampleMap ) );

        fillMap.accept( sampleMap );
        sampleMap.replace( PersonLogic.FIRST_NAME, new String[]{ "" } );
        assertThrows( ValidationException.class, () -> logic.createEntity( sampleMap ) );
        sampleMap.replace( PersonLogic.FIRST_NAME, new String[]{ generateString.apply( 46 ) } );
        assertThrows( ValidationException.class, () -> logic.createEntity( sampleMap ) );

        fillMap.accept( sampleMap );
        sampleMap.replace( PersonLogic.LAST_NAME, new String[]{ "" } );
        assertThrows( ValidationException.class, () -> logic.createEntity( sampleMap ) );
        sampleMap.replace( PersonLogic.LAST_NAME, new String[]{ generateString.apply( 46 ) } );
        assertThrows( ValidationException.class, () -> logic.createEntity( sampleMap ) );

        fillMap.accept( sampleMap );
        sampleMap.replace( PersonLogic.PHONE, new String[]{ "" } );
        assertThrows( ValidationException.class, () -> logic.createEntity( sampleMap ) );
        sampleMap.replace( PersonLogic.PHONE, new String[]{ generateString.apply( 46 ) } );
        assertThrows( ValidationException.class, () -> logic.createEntity( sampleMap ) );

        fillMap.accept( sampleMap );
        sampleMap.replace( PersonLogic.ADDRESS, new String[]{ "" } );
        assertThrows( ValidationException.class, () -> logic.createEntity( sampleMap ) );
        sampleMap.replace( PersonLogic.ADDRESS, new String[]{ generateString.apply( 46 ) } );
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
        sampleMap.put( PersonLogic.ID, new String[]{ Integer.toString( 1 ) } );
        sampleMap.put( PersonLogic.FIRST_NAME, new String[]{ generateString.apply( 1 ) } );
        sampleMap.put( PersonLogic.LAST_NAME, new String[]{ generateString.apply( 1 ) } );
        sampleMap.put( PersonLogic.PHONE, new String[]{ generateString.apply( 1 ) } );
        sampleMap.put( PersonLogic.ADDRESS, new String[]{ generateString.apply( 1 ) } );
        sampleMap.put( PersonLogic.BIRTH, new String[]{ logic.convertDateToString(new Date(1)) } );

        //idealy every test should be in its own method
        Person returnedPerson = logic.createEntity( sampleMap );
        assertEquals( Integer.parseInt( sampleMap.get( PersonLogic.ID )[ 0 ] ), returnedPerson.getId() );
        assertEquals( sampleMap.get( PersonLogic.FIRST_NAME )[ 0 ], returnedPerson.getFirstName() );
        assertEquals( sampleMap.get( PersonLogic.LAST_NAME )[ 0 ], returnedPerson.getLastName() );
        assertEquals( sampleMap.get( PersonLogic.PHONE )[ 0 ], returnedPerson.getPhone() );
        assertEquals( sampleMap.get( PersonLogic.ADDRESS )[ 0 ], returnedPerson.getAddress() );
        assertEquals( sampleMap.get( PersonLogic.BIRTH )[ 0 ], logic.convertDateToString(returnedPerson.getBirth()) );

        sampleMap = new HashMap<>();
        sampleMap.put( PersonLogic.ID, new String[]{ Integer.toString( 1 ) } );
        sampleMap.put( PersonLogic.FIRST_NAME, new String[]{ generateString.apply( 45 ) } );
        sampleMap.put( PersonLogic.LAST_NAME, new String[]{ generateString.apply( 45 ) } );
        sampleMap.put( PersonLogic.PHONE, new String[]{ generateString.apply( 45 ) } );
        sampleMap.put( PersonLogic.ADDRESS, new String[]{ generateString.apply( 45 ) } );
        sampleMap.put( PersonLogic.BIRTH, new String[]{ logic.convertDateToString(new Date(100)) } );

        //idealy every test should be in its own method
        returnedPerson = logic.createEntity( sampleMap );
        assertEquals( Integer.parseInt( sampleMap.get( PersonLogic.ID )[ 0 ] ), returnedPerson.getId() );
        assertEquals( sampleMap.get( PersonLogic.FIRST_NAME )[ 0 ], returnedPerson.getFirstName() );
        assertEquals( sampleMap.get( PersonLogic.LAST_NAME )[ 0 ], returnedPerson.getLastName() );
        assertEquals( sampleMap.get( PersonLogic.PHONE )[ 0 ], returnedPerson.getPhone() );
        assertEquals( sampleMap.get( PersonLogic.ADDRESS )[ 0 ], returnedPerson.getAddress() );
        assertEquals( sampleMap.get( PersonLogic.BIRTH)[ 0 ], logic.convertDateToString(returnedPerson.getBirth()) );
    }

    @Test
    final void testGetColumnNames() {
        List<String> list = logic.getColumnNames();
        assertEquals( Arrays.asList( "ID", "FirstName", "LastName", "Phone", "Address", "Birth" ), list );
    }

    @Test
    final void testGetColumnCodes() {
        List<String> list = logic.getColumnCodes();
        assertEquals( Arrays.asList( PersonLogic.ID, PersonLogic.FIRST_NAME, PersonLogic.LAST_NAME, PersonLogic.PHONE, PersonLogic.ADDRESS , PersonLogic.BIRTH) , list );
    }

    @Test
    final void testExtractDataAsList() {
        List<?> list = logic.extractDataAsList( expectedEntity );
        assertEquals( expectedEntity.getId(), list.get( 0 ) );
        assertEquals( expectedEntity.getFirstName(), list.get( 1 ) );
        assertEquals( expectedEntity.getLastName(), list.get( 2 ) );
        assertEquals( expectedEntity.getPhone(), list.get( 3 ) );
        assertEquals( expectedEntity.getAddress(), list.get( 4 ) );
        assertEquals( expectedEntity.getBirth(), list.get( 5 ) );
    }
}
