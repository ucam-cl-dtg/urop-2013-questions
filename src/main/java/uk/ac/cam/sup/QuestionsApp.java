package uk.ac.cam.sup;

import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.core.Application;

import uk.ac.cam.sup.controllers.DevelopmentController;
import uk.ac.cam.sup.controllers.MainController;
import uk.ac.cam.sup.controllers.MiscController;
import uk.ac.cam.sup.controllers.QuestionController;
import uk.ac.cam.sup.controllers.QuestionSetEditController;
import uk.ac.cam.sup.controllers.QuestionSetViewController;
import uk.ac.cam.sup.controllers.TagController;

import com.googlecode.htmleasy.HtmleasyProviders;

public class QuestionsApp extends Application {
	public Set<Class<?>> getClasses() {
		Set<Class<?>> myServices = new HashSet<Class<?>>();
		
		myServices.add(MainController.class);
		myServices.add(QuestionSetEditController.class);
		myServices.add(QuestionSetViewController.class);
		myServices.add(QuestionController.class);
		myServices.add(TagController.class);
		myServices.add(DevelopmentController.class);
		myServices.add(MiscController.class);
		
		// Add Htmleasy Providers
		myServices.addAll(HtmleasyProviders.getClasses());
		
		return myServices;
	}
}
