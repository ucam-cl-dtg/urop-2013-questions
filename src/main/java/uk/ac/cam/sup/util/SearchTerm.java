package uk.ac.cam.sup.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.sql.Date;
import java.util.List;

import uk.ac.cam.sup.exceptions.DateFormatException;
import uk.ac.cam.sup.exceptions.InvalidRangeException;

public class SearchTerm {
	private String tagsStr;
	private List<String> tags;
	private String ownersStr;
	private List<String> owners;
	private Boolean star;
	private Boolean supervisor;
	private String afterStr;
	private String beforeStr;
	private Date after;
	private Date before;
	private Integer usageMin;
	private Integer usageMax;
	private String parentsStr;
	private List<Integer> parents;
	private Integer durMax;
	private Integer durMin;
	
	public SearchTerm() {}	
	public SearchTerm(String tags, String owners, Boolean star, Boolean supervisor, String after, 
				String before, Integer usageMin, Integer usageMax, String parents, Integer durMax, Integer durMin) throws DateFormatException, InvalidRangeException{
		this.tagsStr = tags;
		this.ownersStr = owners;
		this.star = star;
		this.supervisor = supervisor;
		this.afterStr = after;
		this.beforeStr = before;
		this.usageMin = usageMin;
		this.usageMax = usageMax;
		this.parentsStr = parents;
		this.durMax = durMax;
		this.durMin = durMin;
		sanitize();
	}
	

	
	/*private String tagsName = "tags";
	private String ownersName = "owners";
	private String starName = "star";
	private String supervisorName = "supervisor";
	private String afterName = "after";
	private String beforeName = "before";
	private String usageMinName = "usageMin";
	private String usageMaxName = "usageMax";
	private String parentIdName = "parentId";
	private String durMaxName = "durMax";
	private String durMinName = "durMin";
	
	private String tagsTitle = "Tags";
	private String ownersTitle = "Owners";
	private String starTitle = "Starred";
	private String supervisorTitle = "Supervisor only";
	private String afterTitle = "After date";
	private String beforeTitle = "Before date";
	private String usageMinTitle = "Minimum usage";
	private String usageMaxTitle = "Maximum usage";
	private String parentIdTitle = "Parent Question";
	private String durMaxTitle = "Maximum duration";
	private String durMinTitle = "Minimum duration";
	*/
	private enum SanitizationMode {NO_SPACES, ALLOW_SINGLE_SPACES};
	
	public void sanitize() throws DateFormatException, InvalidRangeException{
		tagsStr = santizeString(tagsStr, SanitizationMode.ALLOW_SINGLE_SPACES);
		ownersStr = santizeString(ownersStr, SanitizationMode.NO_SPACES);
		tags = makeStringList(tagsStr);
		owners = makeStringList(ownersStr);
		
		after = makeDate(afterStr);
		before = makeDate(beforeStr);
		parents = makeIntList(parentsStr);
		
		checkRange(after, before, "Date");
		checkRange(usageMin, usageMax, "Usage");
		checkRange(durMin, durMax, "Duration");
	}
	private String santizeString(String in, SanitizationMode level){
		if(in == null || in.equals("")) return null;
		if(level == SanitizationMode.NO_SPACES){
			return in.replace(" ", "");
		}else{
			return (in.replaceAll(", +", ",")).replaceAll(" +,", ",");
		}
	}
	private Date makeDate(String in) throws DateFormatException{
		if(in == null || in.equals("")){return null;}
		
		try{
			String[] dateParts = in.split("/");
			return Date.valueOf(dateParts[2] + "-" + dateParts[1] + "-" + dateParts[0]);
			
		} catch (Exception e) {
			throw new DateFormatException("Incorrect date format! Please check.\n" + e.getMessage());
		}
	}
	private List<Integer> makeIntList(String in){
		if(in == null || in.equals("")){return null;}
		String[] array = in.split(",");
		List<Integer> results = new ArrayList<Integer>();
		
		try{
			for(int i = 0; i < array.length; i++){
				results.add(Integer.parseInt(array[i]));
			}
		} catch(NumberFormatException e){
			throw new NumberFormatException("Incorrect number input format!\n" + e.getMessage());
		}
		return ((results.size() < 1) ? null : results);
	}
	private List<String> makeStringList(String in){
		if(in == null || in.equals("")){return null;}
		return Arrays.asList(in.split(","));
	}
	private void checkRange(Integer min, Integer max, String what) throws InvalidRangeException{
		if(min == null || max == null) return;
		checkRange(new Long(min), new Long(max), what);
	}
	private void checkRange(Long min, Long max, String what) throws InvalidRangeException{
		if(min == null || max == null) return;
		if(min > max){
			throw new InvalidRangeException("The minimum is larger than the maximum for the token '" + what + "'.");
		}
	}
	private void checkRange(Date min, Date max, String what) throws InvalidRangeException{
		if(min == null || max == null) return;
		checkRange(min.getTime(), max.getTime(), what);
	}
	
	
	public String getTagsStr() {
		return tagsStr;
	}
	public void setTagsStr(String tags) {
		this.tagsStr = tags;
	}
	public List<String> getTags(){
		return tags;
	}
	public String getOwnersStr() {
		return ownersStr;
	}
	public void setOwnersStr(String owners) {
		this.ownersStr = owners;
	}
	public List<String> getOwners(){
		return owners;
	}
	public Boolean getStar() {
		return star;
	}
	public void setStar(Boolean star) {
		this.star = star;
	}
	public Boolean getSupervisor() {
		return supervisor;
	}
	public void setSupervisor(Boolean supervisor) {
		this.supervisor = supervisor;
	}
	public String getAfterStr() {
		return afterStr;
	}
	public void setAfterStr(String after) {
		this.afterStr = after;
	}
	public Date getAfter(){
		return after;
	}
	public String getBeforeStr() {
		return beforeStr;
	}
	public void setBeforeStr(String before) {
		this.beforeStr = before;
	}
	public Date getBefore(){
		return before;
	}
	public Integer getUsageMin() {
		return usageMin;
	}
	public void setUsageMin(Integer usageMin) {
		this.usageMin = usageMin;
	}
	public Integer getUsageMax() {
		return usageMax;
	}
	public void setUsageMax(Integer usageMax) {
		this.usageMax = usageMax;
	}
	public String getParentsStr() {
		return parentsStr;
	}
	public void setParentsStr(String parentIds) {
		this.parentsStr = parentIds;
	}
	public List<Integer> getParents(){
		return parents;
	}
	public Integer getDurMax() {
		return durMax;
	}
	public void setDurMax(Integer durMax) {
		this.durMax = durMax;
	}
	public Integer getDurMin() {
		return durMin;
	}
	public void setDurMin(Integer durMin) {
		this.durMin = durMin;
	}
	/*public String getTagsName() {
		return tagsName;
	}
	public void setTagsName(String tagsName) {
		this.tagsName = tagsName;
	}
	public String getOwnersName() {
		return ownersName;
	}
	public void setOwnersName(String ownersName) {
		this.ownersName = ownersName;
	}
	public String getStarName() {
		return starName;
	}
	public void setStarName(String starName) {
		this.starName = starName;
	}
	public String getSupervisorName() {
		return supervisorName;
	}
	public void setSupervisorName(String supervisorName) {
		this.supervisorName = supervisorName;
	}
	public String getAfterName() {
		return afterName;
	}
	public void setAfterName(String afterName) {
		this.afterName = afterName;
	}
	public String getBeforeName() {
		return beforeName;
	}
	public void setBeforeName(String beforeName) {
		this.beforeName = beforeName;
	}
	public String getUsageMinName() {
		return usageMinName;
	}
	public void setUsageMinName(String usageMinName) {
		this.usageMinName = usageMinName;
	}
	public String getUsageMaxName() {
		return usageMaxName;
	}
	public void setUsageMaxName(String usageMaxName) {
		this.usageMaxName = usageMaxName;
	}
	public String getParentIdName() {
		return parentIdName;
	}
	public void setParentIdName(String parentIdName) {
		this.parentIdName = parentIdName;
	}
	public String getDurMaxName() {
		return durMaxName;
	}
	public void setDurMaxName(String durMaxName) {
		this.durMaxName = durMaxName;
	}
	public String getDurMinName() {
		return durMinName;
	}
	public void setDurMinName(String durMinName) {
		this.durMinName = durMinName;
	}
	public String getTagsTitle() {
		return tagsTitle;
	}
	public void setTagsTitle(String tagsTitle) {
		this.tagsTitle = tagsTitle;
	}
	public String getOwnersTitle() {
		return ownersTitle;
	}
	public void setOwnersTitle(String ownersTitle) {
		this.ownersTitle = ownersTitle;
	}
	public String getStarTitle() {
		return starTitle;
	}
	public void setStarTitle(String starTitle) {
		this.starTitle = starTitle;
	}
	public String getSupervisorTitle() {
		return supervisorTitle;
	}
	public void setSupervisorTitle(String supervisorTitle) {
		this.supervisorTitle = supervisorTitle;
	}
	public String getAfterTitle() {
		return afterTitle;
	}
	public void setAfterTitle(String afterTitle) {
		this.afterTitle = afterTitle;
	}
	public String getBeforeTitle() {
		return beforeTitle;
	}
	public void setBeforeTitle(String beforeTitle) {
		this.beforeTitle = beforeTitle;
	}
	public String getUsageMinTitle() {
		return usageMinTitle;
	}
	public void setUsageMinTitle(String usageMinTitle) {
		this.usageMinTitle = usageMinTitle;
	}
	public String getUsageMaxTitle() {
		return usageMaxTitle;
	}
	public void setUsageMaxTitle(String usageMaxTitle) {
		this.usageMaxTitle = usageMaxTitle;
	}
	public String getParentIdTitle() {
		return parentIdTitle;
	}
	public void setParentIdTitle(String parentIdTitle) {
		this.parentIdTitle = parentIdTitle;
	}
	public String getDurMaxTitle() {
		return durMaxTitle;
	}
	public void setDurMaxTitle(String durMaxTitle) {
		this.durMaxTitle = durMaxTitle;
	}
	public String getDurMinTitle() {
		return durMinTitle;
	}
	public void setDurMinTitle(String durMinTitle) {
		this.durMinTitle = durMinTitle;
	}*/
	
	
}
