package uk.ac.cam.sup.controllers;

import java.io.File;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.Response.Status;

import uk.ac.cam.sup.util.ServContext;

@Path("/uploads")
public class FileController {
	
	//private static Logger log = LoggerFactory.getLogger(GeneralController.class);
	
	@GET
	@Path("/{filename}")
	@Produces("*/*")
	public Response getFile(@PathParam("filename") String filename) {
		if (filename == null || filename.isEmpty()) {
			ResponseBuilder response = Response.status(Status.BAD_REQUEST);
			return response.build();
		}
		
		//ServletContext context = ResteasyProviderFactory.getContextData(ServletContext.class);
		String rootLocation = ServContext.getUploadsDir();//context.getInitParameter("storageLocation")+"local/data/questions/";
		
		File file = new File(rootLocation+filename);
		
		ResponseBuilder response = Response.ok((Object) file);
		response.header("Content-Disposition", "attachment; filename=\""+filename+"\"");
		return response.build();
	}	
}
