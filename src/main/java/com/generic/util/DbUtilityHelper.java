package com.generic.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.data.geo.Circle;
import org.springframework.data.geo.Point;

public class DbUtilityHelper {
	public class GraphQuery<T> {
		private List<String> fields;
		private Map<String, Object> filters;
		private List<JoinConfig> joins;
		private Pagination pagination;

		// Getters and setters
		public List<String> getFields() {
			return fields;
		}

		public void setFields(List<String> fields) {
			this.fields = fields;
		}

		public Map<String, Object> getFilters() {
			return filters;
		}

		public void setFilters(Map<String, Object> filters) {
			this.filters = filters;
		}

		public List<JoinConfig> getJoins() {
			return joins;
		}

		public void setJoins(List<JoinConfig> joins) {
			this.joins = joins;
		}

		public Pagination getPagination() {
			return pagination;
		}

		public void setPagination(Pagination pagination) {
			this.pagination = pagination;
		}

		public class JoinConfig {
			private String path;
			private JoinType joinType;

			// Getters and setters
			public String getPath() {
				return path;
			}

			public void setPath(String path) {
				this.path = path;
			}

			public JoinType getJoinType() {
				return joinType;
			}

			public void setJoinType(JoinType joinType) {
				this.joinType = joinType;
			}
		}

		public class Pagination {
			private int offset;
			private int limit;

			// Getters and setters
			public int getOffset() {
				return offset;
			}

			public void setOffset(int offset) {
				this.offset = offset;
			}

			public int getLimit() {
				return limit;
			}

			public void setLimit(int limit) {
				this.limit = limit;
			}
		}
	}

	public static class CriteriaBuilderHelper<C> {
		private final CriteriaBuilder cb;
		private final Root<C> root;
		private final List<Predicate> predicates = new ArrayList<>();

		public CriteriaBuilderHelper(CriteriaBuilder cb, Root<C> root) {
			this.cb = cb;
			this.root = root;
		}

		public CriteriaBuilderHelper<C> eq(String property, Object value) {
			predicates.add(cb.equal(root.get(property), value));
			return this;
		}

		public CriteriaBuilderHelper<C> ne(String property, Object value) {
			predicates.add(cb.notEqual(root.get(property), value));
			return this;
		}

		public CriteriaBuilderHelper<C> like(String property, String value) {
			predicates.add(cb.like(root.get(property), value));
			return this;
		}

		@SuppressWarnings("unchecked")
		public CriteriaBuilderHelper<C> gt(String property, Comparable value) {
			predicates.add(cb.greaterThan(root.get(property), value));
			return this;
		}

		@SuppressWarnings("unchecked")
		public CriteriaBuilderHelper<C> lt(String property, Comparable value) {
			predicates.add(cb.lessThan(root.get(property), value));
			return this;
		}

		@SuppressWarnings("unchecked")
		public CriteriaBuilderHelper<C> between(String property, Comparable from, Comparable to) {
			predicates.add(cb.between(root.get(property), from, to));
			return this;
		}

		public CriteriaBuilderHelper<C> isNull(String property) {
			predicates.add(cb.isNull(root.get(property)));
			return this;
		}

		public CriteriaBuilderHelper<C> isNotNull(String property) {
			predicates.add(cb.isNotNull(root.get(property)));
			return this;
		}

		public Predicate build() {
			return predicates.isEmpty() ? cb.conjunction() : cb.and(predicates.toArray(new Predicate[0]));
		}
	}

	// Enums
	public enum FetchStrategy {
		LAZY, EAGER, JOIN
	}

	public enum ComparisonOperator {
		GT, LT, GE, LE, EQ
	}

	public enum Operator {
		EQUAL("="), NOT_EQUAL("!="), GREATER_THAN(">"), GREATER_THAN_OR_EQUAL(">="), LESS_THAN("<"),
		LESS_THAN_OR_EQUAL("<="), LIKE("like"), ILIKE("ilike"), NOT_LIKE("not like"), IS_NULL("is null"),
		IS_NOT_NULL("is not null"), IN("in"), NOT_IN("not in"), BETWEEN("between"), DATE_BEFORE("before"),
		DATE_AFTER("after"), DATE_ON("on"), CONTAINS("contains"), STARTS_WITH("starts with"), ENDS_WITH("ends with"),
		JSON_CONTAINS("json_contains"), JSON_PATH("json_path"), IS_TRUE("is true"), IS_FALSE("is false");

		private final String symbol;

		Operator(String symbol) {
			this.symbol = symbol;
		}

		public String getSymbol() {
			return symbol;
		}
	}

	public enum LogicalOperator {
		AND, OR
	}

	public class Condition {
		private String property;
		private Object value;
		private Operator operator;
		private boolean ignoreCase;

		public Condition() {
		}

		public Condition(String property, Object value, Operator operator) {
			this.property = property;
			this.value = value;
			this.operator = operator;
		}

		// Getters and setters
		public String getProperty() {
			return property;
		}

		public void setProperty(String property) {
			this.property = property;
		}

		public Object getValue() {
			return value;
		}

		public void setValue(Object value) {
			this.value = value;
		}

		public Operator getOperator() {
			return operator;
		}

		public void setOperator(Operator operator) {
			this.operator = operator;
		}

		public boolean isIgnoreCase() {
			return ignoreCase;
		}

		public void setIgnoreCase(boolean ignoreCase) {
			this.ignoreCase = ignoreCase;
		}
	}

	public class ConditionGroup {
		private List<Condition> conditions = new ArrayList<>();
		private List<ConditionGroup> groups = new ArrayList<>();
		private LogicalOperator operator = LogicalOperator.AND;

		public ConditionGroup() {
		}

		public ConditionGroup(LogicalOperator operator) {
			this.operator = operator;
		}

		// Builder-style methods
		public ConditionGroup addCondition(Condition condition) {
			this.conditions.add(condition);
			return this;
		}

		public ConditionGroup addGroup(ConditionGroup group) {
			this.groups.add(group);
			return this;
		}

		// Getters
		public List<Condition> getConditions() {
			return conditions;
		}

		public List<ConditionGroup> getGroups() {
			return groups;
		}

		public LogicalOperator getOperator() {
			return operator;
		}
	}

	public static class GeometryUtils {
		public static Point createPoint(double lat, double lng) {
			// Implementation depends on your spatial library
			return new Point(lat, lng);
		}

		public static Circle createCircle(Point center, double radiusKm) {
			// Implementation depends on your spatial library
			return new Circle(center, radiusKm);
		}
	}

}
