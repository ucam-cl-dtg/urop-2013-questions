package uk.ac.cam.sup.models;

import java.util.Map;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.ac.cam.cl.dtg.teaching.api.DashboardApi.DashboardApiWrapper;
import uk.ac.cam.cl.dtg.teaching.api.DashboardApi.Settings;
import uk.ac.cam.sup.util.ServContext;

@Entity
@Table( name = "users" )
public class User extends Model {
	private static Logger log = LoggerFactory.getLogger(User.class);
	
	@Transient private Boolean supervisor = null;
	@Id private String id;
	
	public User() {}
	
	public User(String i) {
		id = i;
	}
	
	public String getId() {return id;}
	
	public void setId(String i) {id = i;}
	
	public boolean getSupervisor(){
		if(supervisor != null) return supervisor;
		try{
			supervisor = (Boolean)getSettings().get("supervisor");
			if(supervisor == null) throw new Exception("Supervisor status of user  was null!");
			return supervisor;
		} catch(Exception e){
			log.error("Error while trying to retrive supervisor status of user " + id + ". Message: " + e.getMessage());
			return false;
		}
	}
	
	public Map<String, Object> getSettings(){
		DashboardApiWrapper d = new DashboardApiWrapper(ServContext.getDashboardUrl(), ServContext.getApiKey());
		Settings s = d.getUserSettings(id);
		return s.getSettings();
	}
	
	@Override
	public boolean equals(Object user){
		if (user == null || !(user instanceof User)) {
			return false;
		}
		return getId().equals(((User)user).getId());
	}
	
	@Override
	public int hashCode(){
		return 0;
	}
}