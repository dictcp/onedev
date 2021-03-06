package io.onedev.server.search.entity.issue;

import javax.annotation.Nullable;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import io.onedev.server.model.Issue;
import io.onedev.server.model.Project;

import io.onedev.server.search.entity.EntityQuery;
import io.onedev.server.util.ProjectScopedNumber;
import io.onedev.server.util.query.IssueQueryConstants;

public class NumberCriteria extends IssueCriteria {

	private static final long serialVersionUID = 1L;

	private final int operator;
	
	private final String value;
	
	private final ProjectScopedNumber number;
	
	public NumberCriteria(@Nullable Project project, String value, int operator) {
		this.operator = operator;
		this.value = value;
		number = EntityQuery.getEntityNumber(project, value);
	}

	@Override
	public Predicate getPredicate(Root<Issue> root, CriteriaBuilder builder) {
		Path<Long> attribute = root.get(IssueQueryConstants.ATTR_NUMBER);
		Predicate numberPredicate;
		
		if (operator == IssueQueryLexer.Is)
			numberPredicate = builder.equal(attribute, number.getNumber());
		else if (operator == IssueQueryLexer.IsGreaterThan)
			numberPredicate = builder.greaterThan(attribute, number.getNumber());
		else
			numberPredicate = builder.lessThan(attribute, number.getNumber());
		
		return builder.and(
				builder.equal(root.get(IssueQueryConstants.ATTR_PROJECT), number.getProject()),
				numberPredicate);
	}

	@Override
	public boolean matches(Issue issue) {
		if (issue.getProject().equals(number.getProject())) {
			if (operator == IssueQueryLexer.Is)
				return issue.getNumber() == number.getNumber();
			else if (operator == IssueQueryLexer.IsGreaterThan)
				return issue.getNumber() > number.getNumber();
			else
				return issue.getNumber() < number.getNumber();
		} else {
			return false;
		}
	}

	@Override
	public String asString() {
		return quote(IssueQueryConstants.FIELD_NUMBER) + " " 
				+ IssueQuery.getRuleName(operator) + " " 
				+ quote(value);
	}

}
