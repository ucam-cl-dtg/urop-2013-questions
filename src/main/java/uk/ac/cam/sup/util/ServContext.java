package uk.ac.cam.sup.util;

public class ServContext {
	private static String apiKey = null;
	private static String dashboardUrl;
	
	public static void setApiKey(String key){
		apiKey = key;
	}
	public static void setDashboardUrl(String url){
		dashboardUrl = url;
	}
	
	public static String getApiKey(){return apiKey;}
	public static String getDashboardUrl(){return dashboardUrl;}
}
