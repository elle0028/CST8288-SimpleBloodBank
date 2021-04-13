package dal;

import entity.Person;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Date;

/**
 *
 * @author Fargol Azimi
 */
public class PersonDAL extends GenericDAL<Person>  {
    
    public PersonDAL() {
        super( Person.class );
    }

    @Override
    public List<Person> findAll() {
        return findResults( "Person.findAll", null );
    }

    @Override
    public Person findById(int id) {
        Map<String, Object> map = new HashMap<>();
        map.put( "id", id );
        return findResult( "Person.findById", map );
    }
    
    public List<Person>  findByFirstName(String firstName) {
        Map<String, Object> map = new HashMap<>();
        map.put( "firstname", firstName );
        return findResults( "Person.findByFirstName", map );
    }
    
    public List<Person>  findByLastName(String lastName) {
        Map<String, Object> map = new HashMap<>();
        map.put( "lastname", lastName );
        return findResults( "Person.findByLastName", map );
    }
    
        public List<Person>  findByPhone(String phone) {
        Map<String, Object> map = new HashMap<>();
        map.put( "lastname", phone );
        return findResults( "Person.findByLastName", map );
    }
        
        
        public List<Person>  findByAddress(String address) {
        Map<String, Object> map = new HashMap<>();
        map.put( "lastname", address );
        return findResults( "Person.findByLastName", map );
    }
        
        
        public List<Person>  findByBirth(Date birth) {
        Map<String, Object> map = new HashMap<>();
        map.put( "lastname", birth );
        return findResults( "Person.findByLastName", map );
    }
    
}
