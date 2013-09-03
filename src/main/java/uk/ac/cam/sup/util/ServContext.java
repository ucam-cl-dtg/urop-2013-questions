package uk.ac.cam.sup.util;

public class ServContext {
	private static String apiKey = null;
	private static String dashboardUrl;
	private static String uploadsDir;
	
	public static void setApiKey(String key){
		apiKey = key;
	}
	public static void setDashboardUrl(String url){
		dashboardUrl = url;
	}
	public static void setUploadsDir(String dir){
		uploadsDir = dir;
	}
	
	public static String getApiKey(){return apiKey;}
	public static String getDashboardUrl(){return dashboardUrl;}
	public static String getUploadsDir(){return uploadsDir;}
}
