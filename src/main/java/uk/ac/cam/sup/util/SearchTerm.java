package uk.ac.cam.sup.util;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Arrays;
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
	
	private enum SanitizationMode {NO_SPACES, ALLOW_SINGLE_SPACES};
	
	public void sanitize() throws DateFormatException, InvalidRangeException{
		tagsStr = sanitizeString(tagsStr, SanitizationMode.ALLOW_SINGLE_SPACES);
		ownersStr = sanitizeString(ownersStr, SanitizationMode.NO_SPACES);
		tags = makeStringList(tagsStr);
		owners = makeStringList(ownersStr);
		
		after = makeDate(afterStr);
		before = makeDate(beforeStr);
		parents = makeIntList(parentsStr);
		
		checkRange(after, before, "Date");
		checkRange(usageMin, usageMax, "Usage");
		checkRange(durMin, durMax, "Duration");
	}
	private String sanitizeString(String in, SanitizationMode level){
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
	
	
	
}
