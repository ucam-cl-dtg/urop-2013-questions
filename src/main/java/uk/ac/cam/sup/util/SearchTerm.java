package uk.ac.cam.sup.util;

public class SearchTerm {
	public SearchTerm(String tags, String owners, Boolean star, Boolean supervisor, Long after, 
				Long before, Integer usageMin, Integer usageMax, Integer parentId, Integer durMax, Integer durMin){
		this.tags = tags;
		this.owners = owners;
		this.setStar(star);
		this.setSupervisor(supervisor);
		this.after = after;
		this.before = before;
		this.usageMin = usageMin;
		this.usageMax = usageMax;
		this.parentId = parentId;
		this.durMax = durMax;
		this.durMin = durMin;
		sanitize();
	}
	
	private String tags;
	private String owners;
	private Boolean star;
	private Boolean supervisor;
	private Long after;
	private Long before;
	private Integer usageMin;
	private Integer usageMax;
	private Integer parentId;
	private Integer durMax;
	private Integer durMin;
	
	private String tagsName = "tags";
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
	
	private enum SanitizationMode {NO_SPACES, ALLOW_SINGLE_SPACES};
	
	public void sanitize(){
		tags = stringSanitization(tags, SanitizationMode.ALLOW_SINGLE_SPACES);
		owners = stringSanitization(owners, SanitizationMode.NO_SPACES);
	}
	private String stringSanitization(String in, SanitizationMode level){
		if(in == null) return null;
		if(level == SanitizationMode.NO_SPACES){
			return in.replace(" ", "");
		}else{
			return (in.replaceAll(", +", ",")).replaceAll(" +,", ",");
		}
	}
	
	
	public String getTags() {
		return tags;
	}
	public void setTags(String tags) {
		this.tags = tags;
	}
	public String getOwners() {
		return owners;
	}
	public void setOwners(String owners) {
		this.owners = owners;
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
	public Long getAfter() {
		return after;
	}
	public void setAfter(Long after) {
		this.after = after;
	}
	public Long getBefore() {
		return before;
	}
	public void setBefore(Long before) {
		this.before = before;
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
	public Integer getParentId() {
		return parentId;
	}
	public void setParentId(Integer parentId) {
		this.parentId = parentId;
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
	public String getTagsName() {
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
	}
	
	
}
