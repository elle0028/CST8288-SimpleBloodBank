package dal;

import entity.DonationRecord;
import java.util.Date;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
/**
 *
 * @author aksha
 */
public class DonationRecordDAL extends GenericDAL<DonationRecord>{
    
    public DonationRecordDAL() {
        super(DonationRecord.class);
    }
    @Override
    public List<DonationRecord> findAll() {
        return findResults("DonationRecord.findAll",null); 
    }
    
    @Override
    public DonationRecord findById(int id) {
        Map<String, Object> map = new HashMap<>();
        map.put( "recordId", id );
        return findResult("DonationRecord.findByRecordId" , map);
    }
    
    public List<DonationRecord> findByPersonId( int person_id ) {
        Map<String, Object> map = new HashMap<>();
        map.put( "personId", person_id );
        return findResults( "DonationRecord.findByPersonId", map );
    }
    
    public List<DonationRecord> findByDonationId( int donation_id ) {
        Map<String, Object> map = new HashMap<>();
        map.put( "donationId", donation_id );
        return findResults( "DonationRecord.findByDonationId", map );
    }
    
    public List<DonationRecord> findByTested( boolean tested ) {
        Map<String, Object> map = new HashMap<>();
        map.put( "tested", tested );
        return findResults( "DonationRecord.findByTested", map);
    }  
    
    public List<DonationRecord> findByHospital( String hospital ) {
        Map<String, Object> map = new HashMap<>();
        map.put( "hospital", hospital );
        return findResults( "DonationRecord.findByHospital", map );
    }
    
    public List<DonationRecord> findByAdministrator( String administrator ) {
        Map<String, Object> map = new HashMap<>();
        map.put( "administrator", administrator );
        return findResults( "DonationRecord.findByAdministrator", map );
    }
    
    public List<DonationRecord> findCreated( Date created ) {
        Map<String, Object> map = new HashMap<>();
        map.put( "created", created );
        return findResults( "DonationRecord.findCreated", map );
    }
    
    
}
