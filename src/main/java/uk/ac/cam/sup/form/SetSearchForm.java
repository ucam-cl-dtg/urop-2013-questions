package uk.ac.cam.sup.form;

import uk.ac.cam.sup.models.QuestionSet;
import uk.ac.cam.sup.queries.Query;
import uk.ac.cam.sup.queries.QuestionSetQuery;

public class SetSearchForm extends SearchForm<QuestionSet> {

	@Override
	protected Query<QuestionSet> getQueryObject() {
		return QuestionSetQuery.all();
	}

}
