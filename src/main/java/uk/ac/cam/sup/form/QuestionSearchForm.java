package uk.ac.cam.sup.form;

import uk.ac.cam.sup.models.Question;
import uk.ac.cam.sup.queries.Query;
import uk.ac.cam.sup.queries.QuestionQuery;

public class QuestionSearchForm extends SearchForm<Question> {

	@Override
	protected Query<Question> getQueryObject() {
		return QuestionQuery.all();
	}

}
