package uk.ac.cam.sup;

import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.core.Application;

import uk.ac.cam.sup.controllers.DevelopmentController;
import uk.ac.cam.sup.controllers.MainController;
import uk.ac.cam.sup.controllers.MiscController;
import uk.ac.cam.sup.controllers.QuestionEditController;
import uk.ac.cam.sup.controllers.QuestionSetController;
import uk.ac.cam.sup.controllers.QuestionViewController;
import uk.ac.cam.sup.controllers.TagController;

import com.googlecode.htmleasy.HtmleasyProviders;

public class QuestionsApp extends Application {
	public Set<Class<?>> getClasses() {
		Set<Class<?>> myServices = new HashSet<Class<?>>();
		
		myServices.add(MainController.class);
		myServices.add(QuestionViewController.class);
		myServices.add(QuestionEditController.class);
		myServices.add(QuestionSetController.class);
		myServices.add(TagController.class);
		myServices.add(DevelopmentController.class);
		myServices.add(MiscController.class);
		
		// Add Htmleasy Providers
		myServices.addAll(HtmleasyProviders.getClasses());
		
		return myServices;
	}
}
