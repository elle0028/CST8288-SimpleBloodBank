package dal;

import entity.Person;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Date;

/**
 * CST8288
 * @author Fargol Azimi
 */
public class PersonDAL extends GenericDAL<Person> {

    public PersonDAL() {
        super(Person.class);
    }

    /**
     * first argument is a name given to a named query defined in appropriate entity
     * second argument is map used for parameter substitution.
     * parameters are names starting with : in named queries, :[name]
     * @return List<Person>
     */
    @Override
    public List<Person> findAll() {
        return findResults("Person.findAll", null);
    }
    /**
     * parameter names "id" and value is put to map
     * @param id
     * @return Person that was found with the id
     */
    @Override
    public Person findById(int id) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", id);
        return findResult("Person.findById", map);
    }
    /**
    * parameter names "firstName" and value is put to map
    * @param firstName
    * @return Person that was found with the firstName
    */
    public List<Person> findByFirstName(String firstName) {
        Map<String, Object> map = new HashMap<>();
        map.put("firstname", firstName);
        return findResults("Person.findByFirstName", map);
    }
    /**
    * parameter names "lastName" and value is put to map
    * @param lastName
    * @return Person that was found with the lastName
    */
    public List<Person> findByLastName(String lastName) {
        Map<String, Object> map = new HashMap<>();
        map.put("lastname", lastName);
        return findResults("Person.findByLastName", map);
    }
    /**
    * parameter names "phone" and value is put to map
    * @param phone
    * @return Person that was found with the phone
    */
    public List<Person> findByPhone(String phone) {
        Map<String, Object> map = new HashMap<>();
        map.put("phone", phone);
        return findResults("Person.findByLastName", map);
    }
    /**
    * parameter names "address" and value is put to map
    * @param address
    * @return Person that was found with the address
    */
    public List<Person> findByAddress(String address) {
        Map<String, Object> map = new HashMap<>();
        map.put("address", address);
        return findResults("Person.findByLastName", map);
    }
    /**
    * parameter names "birth" and value is put to map
    * @param birth
    * @return Person that was found with the birth
    */
    public List<Person> findByBirth(Date birth) {
        Map<String, Object> map = new HashMap<>();
        map.put("birth", birth);
        return findResults("Person.findByLastName", map);
    }
    /**
    * search and finds the Person with the contained String
    * @param search
    * @return Person that was found with the specific String
    */
    public List<Person> findContaining(String search) {
        Map<String, Object> map = new HashMap<>();
        map.put("search", search);
        return findResults("Person.findContaining", map);
    }

}
