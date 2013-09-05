package uk.ac.cam.sup.models;

import java.util.Map;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.ac.cam.cl.dtg.ldap.LDAPQueryManager;
import uk.ac.cam.cl.dtg.ldap.LDAPUser;
import uk.ac.cam.cl.dtg.teaching.api.DashboardApi.DashboardApiWrapper;
import uk.ac.cam.sup.util.ServContext;

@Entity
@Table( name = "users" )
public class User extends Model {
	private static Logger log = LoggerFactory.getLogger(User.class);
	
	private boolean supervisor;
	@Transient private boolean supervisorUpToDate = false;
	@Transient private String name = null;
	@Transient private Map<String,Object> settings = null;
	@Id private String id;
	
	public User() {}
	
	public User(String i) {
		id = i;
	}
	
	public String getId() {return id;}
	
	public void setId(String i) {id = i;}
	
	public boolean getSupervisor(){
		if(supervisorUpToDate) return supervisor;
		try{
			boolean oldSup = supervisor;
			supervisor = (Boolean)getSettings().get("supervisor");
			supervisorUpToDate = true;
			if(oldSup != supervisor) this.update();
			return supervisor;
		} catch(Exception e){
			log.error("Error while trying to retrive supervisor status of user " + id + ". Message: " + e.getMessage());
			return false;
		}
	}
	
	public Map<String, Object> getSettings(){
		if(settings == null){
			try{
				DashboardApiWrapper d = new DashboardApiWrapper(ServContext.getDashboardUrl(), ServContext.getApiKey());
				settings = d.getUserSettings(id).getSettings();
				if(settings == null) throw new Exception("settings were null!");
			}catch(Exception e){
				log.error("An error occurred while trying to retrieve settings for user " + id
						+ ". Error class: " + e.getClass() + ". Message: " + e.getMessage());
				return null;
			}
		}
		return settings;
	}
	
	public String getName(){
		if(name == null){
			try{
				LDAPUser u = LDAPQueryManager.getUser(id);
				name = u.getDisplayName();
				if(name == null) throw new Exception("name was null!");
			} catch(Exception e){
				log.error("An error occurred while trying to retrieve the display name for " + id
						+ ". Error class: " + e.getClass() + ". Message: " + e.getMessage());
				name = id;
			}
		}
		return name;
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