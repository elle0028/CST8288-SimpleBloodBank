package dal;

import entity.BloodBank;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *  BloodBankDAL
 *  @author Andrew O'Hara
 *  April 2021
 *  
 *   extends GenericDAL and implements BloodBank specific DAL operations
 */
public class BloodBankDAL extends GenericDAL <BloodBank> {
   
    /**
     *  BloodBankDAL constructor simply calls GenericDAL super() with 
     *   BloodBank.class
     */
    public BloodBankDAL() {
        super(BloodBank.class);
    }
    
    /**
     * findAll :
     * @return list of all BloodBanks in database
     */
    @Override
    public List<BloodBank> findAll() {
        return findResults( "BloodBank.findAll", null );
    }

    /**
     * findById :
     * @param bankId - database id of BloodBank to get
     * @return BloodBank in database matching bankId
     */
    @Override
    public BloodBank findById(int bankId) {
        Map<String, Object> map = new HashMap<>();
        map.put( "bankId", bankId );
      
        return findResult( "BloodBank.findByBankId", map );
    }    
    
    /**
     * findByName :
     * @param name - name to match in the database
     * @return BloodBank in database matching name
     */
    public BloodBank findByName(String name) {
        Map<String, Object> map = new HashMap<>();
        map.put( "name", name );
        return findResult( "BloodBank.findByName", map );
    }
   
    /**
     * findByPrivatelyOwned :
     * @param privatelyOwned - find private BloodBanks or not
     * @return list of all BloodBanks in database that are privately owned or not
     *   based on privatelyOwned
     */
    public List<BloodBank> findByPrivatelyOwned(boolean privatelyOwned) {
        Map<String, Object> map = new HashMap<>();
        map.put( "privatelyOwned", privatelyOwned );
        return findResults( "BloodBank.findByPrivatelyOwned", map );
    }
    
    /**
     * findByEstablished :
     * @param established - Date to match when searching the database
     * @return list of all BloodBanks in database that were established on 
     *   the date passed in
     */
    public List<BloodBank> findByEstablished(Date established) {
        Map<String, Object> map = new HashMap<>();
        map.put( "established", established );
        return findResults( "BloodBank.findByEstablished", map );
    }
    
    /**
     * findByEmployeeCount :
     * @param employeeCount - number of employees to match when searching database
     * @return list of all BloodBanks in database with employeeCount number of 
     *  employees
     */
    public List<BloodBank> findByEmployeeCount(int employeeCount) {
        Map<String, Object> map = new HashMap<>();
        map.put( "employeeCount", employeeCount );
        return findResults( "BloodBank.findByEmployeeCount", map );
    }
    
    /**
     * findByOwner :
     * @param ownerId- id of Person owner to search for in the database
     * @return BloodBank in database owned by Person with ownerId
     */
    public BloodBank findByOwner(int ownerId) {
        Map<String, Object> map = new HashMap<>();
        map.put( "ownerId", ownerId );
        return findResult( "BloodBank.findByOwner", map );
    }
    
    /**
     * findContaining :
     * @param search- search term to match when searching databse
     * @return list of BloodBanks in database that match the search term
     */
    public List<BloodBank> findContaining(String search) {
        Map<String, Object> map = new HashMap<>();
        map.put( "search", search );
        return findResults( "BloodBank.findContaining", map );
    }        
}
