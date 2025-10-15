package com.generic.model.dao.impl;

import com.generic.model.dao.GenericDao;

import com.generic.util.DbUtilityHelper.ComparisonOperator;
import com.generic.util.DbUtilityHelper.Condition;
import com.generic.util.DbUtilityHelper.ConditionGroup;
import com.generic.util.DbUtilityHelper.CriteriaBuilderHelper;
import com.generic.util.DbUtilityHelper.FetchStrategy;
import com.generic.util.DbUtilityHelper.GeometryUtils;
import com.generic.util.DbUtilityHelper.GraphQuery;
import com.generic.util.DbUtilityHelper.LogicalOperator;
import com.generic.util.DbUtilityHelper.Operator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.dialect.Dialect;
import org.hibernate.engine.spi.SessionFactoryImplementor;

import org.hibernate.persister.entity.EntityPersister;
import org.hibernate.query.Query;
import org.hibernate.search.jpa.FullTextEntityManager;
import org.hibernate.search.jpa.FullTextQuery;
import org.hibernate.search.jpa.Search;
import org.hibernate.search.query.dsl.BooleanJunction;
import org.hibernate.search.query.dsl.QueryBuilder;

import org.hibernate.type.Type;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.DirectFieldAccessor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import org.springframework.data.geo.Point;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.*;
import javax.persistence.criteria.*;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@SuppressWarnings("hiding")
public class GenericDaoImpl<T extends Serializable, Id extends Serializable> implements GenericDao<T, Id> {

	private static final long serialVersionUID = -7955429492191209773L;
	final protected static Log log = LogFactory.getLog(GenericDaoImpl.class);
	private static final Logger LOGGER = LoggerFactory.getLogger(GenericDaoImpl.class);
	transient protected SessionFactory sessionFactory;
	protected Class<T> persistentClass;

	@SuppressWarnings("unchecked")
	public GenericDaoImpl() {
		persistentClass = (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass())
				.getActualTypeArguments()[0];
	}

	// Getters and setters
	public SessionFactory getSessionFactory() {
		return sessionFactory;
	}

	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	public Class<T> getPersistentClass() {
		return persistentClass;
	}

	protected Session getCurrentSession() {
		return sessionFactory.getCurrentSession();
	}

	// Basic CRUD operations implementation
	@Override
	public T insertOrupdate(T t) throws Exception {
		try {
			getCurrentSession().saveOrUpdate(t);
			log.debug(persistentClass.getSimpleName() + " saved or updated: " + t);
			return t;
		} catch (Exception e) {
			getCurrentSession().clear();
			e.printStackTrace();
			throw e;
		}
	}

	@Override
	public T insert(T t) throws Exception {
		try {
			Serializable id = getCurrentSession().save(t);
			log.debug(persistentClass.getSimpleName() + " persisted with id: " + id);
			return t;
		} catch (Exception e) {
			getCurrentSession().clear();
			e.printStackTrace();
			throw e;
		}
	}

	@Override
	public List<T> insert(List<T> t) throws Exception {
		try {
			Session session = getCurrentSession();
			for (T entity : t) {
				session.save(entity);
			}
			log.debug("Inserted " + t.size() + " " + persistentClass.getSimpleName() + " entities");
			return t;
		} catch (Exception e) {
			getCurrentSession().clear();
			e.printStackTrace();
			throw e;
		}
	}

	@Override
	public boolean update(T t) throws Exception {
		try {
			getCurrentSession().update(t);
			log.debug(persistentClass.getSimpleName() + " updated: " + t);
			return true;
		} catch (Exception e) {
			getCurrentSession().clear();
			e.printStackTrace();
			throw e;
		}
	}

	@Override
	public boolean update(List<T> t) throws Exception {
		try {
			Session session = getCurrentSession();
			for (T entity : t) {
				session.update(entity);
			}
			log.debug("Updated " + t.size() + " " + persistentClass.getSimpleName() + " entities");
			return true;
		} catch (Exception e) {
			getCurrentSession().clear();
			e.printStackTrace();
			throw e;
		}
	}

	@Override
	public boolean delete(T t) throws Exception {
		try {
			getCurrentSession().delete(t);
			log.debug(persistentClass.getSimpleName() + " deleted: " + t);
			return true;
		} catch (Exception e) {
			getCurrentSession().clear();
			e.printStackTrace();
			throw e;
		}
	}

	@Override
	public boolean delete(List<T> t) throws Exception {
		try {
			Session session = getCurrentSession();
			for (T entity : t) {
				session.delete(entity);
			}
			log.debug("Deleted " + t.size() + " " + persistentClass.getSimpleName() + " entities");
			return true;
		} catch (Exception e) {
			getCurrentSession().clear();
			e.printStackTrace();
			throw e;
		}
	}

	@Override
	public boolean merge(T t) throws Exception {
		try {
			getCurrentSession().merge(t);
			log.debug(persistentClass.getSimpleName() + " merged: " + t);
			return true;
		} catch (Exception e) {
			getCurrentSession().clear();
			e.printStackTrace();
			throw e;
		}
	}


        // Query operations implementation
    @Override
    public T getById(Id id) throws Exception {
        log.debug("Fetching " + persistentClass.getSimpleName() + " with id: " + id);
        if (id == null) {
            log.warn("getById called with null id for entity: " + persistentClass.getName() +
                    ". Returning null to avoid IllegalArgumentException.");
            return null;
        }
        return getCurrentSession().get(persistentClass, id);
    }


	@Override
	public T load(Id id) throws Exception {
		log.debug("Loading proxy for " + persistentClass.getSimpleName() + " with id: " + id);
		return getCurrentSession().load(persistentClass, id);
	}

	@Override
	public T getByUniqueField(String propertyName, Object propertyValue) throws Exception {
		CriteriaBuilder cb = getCurrentSession().getCriteriaBuilder();
		CriteriaQuery<T> cq = cb.createQuery(persistentClass);
		Root<T> root = cq.from(persistentClass);

		cq.select(root).where(cb.equal(root.get(propertyName), propertyValue));

		try {
			return getCurrentSession().createQuery(cq).getSingleResult();
		} catch (NoResultException e) {
			log.debug("No " + persistentClass.getSimpleName() + " found with " + propertyName + " = " + propertyValue);
			return null;
		}
	}

	@Override
	public List<T> getAll() throws Exception {
		CriteriaBuilder cb = getCurrentSession().getCriteriaBuilder();
		CriteriaQuery<T> cq = cb.createQuery(persistentClass);
		cq.select(cq.from(persistentClass));
		return getCurrentSession().createQuery(cq).getResultList();
	}

	@Override
	public Long getCountAll() throws Exception {
		CriteriaBuilder cb = getCurrentSession().getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		cq.select(cb.count(cq.from(persistentClass)));
		return getCurrentSession().createQuery(cq).getSingleResult();
	}

	// Pagination implementation
	@Override
	public List<T> getPages(Integer start, Integer pageSize) throws Exception {
		CriteriaBuilder cb = getCurrentSession().getCriteriaBuilder();
		CriteriaQuery<T> cq = cb.createQuery(persistentClass);
		cq.select(cq.from(persistentClass));

		TypedQuery<T> query = getCurrentSession().createQuery(cq);
		if (start != null)
			query.setFirstResult(start);
		if (pageSize != null)
			query.setMaxResults(pageSize);

		return query.getResultList();
	}

	@Override
	public List<T> getPagesByFilter(Integer start, Integer pageSize, String propertyName, Object propertyValue)
			throws Exception {
		CriteriaBuilder cb = getCurrentSession().getCriteriaBuilder();
		CriteriaQuery<T> cq = cb.createQuery(persistentClass);
		Root<T> root = cq.from(persistentClass);

		cq.select(root).where(cb.equal(root.get(propertyName), propertyValue));

		TypedQuery<T> query = getCurrentSession().createQuery(cq);
		if (start != null)
			query.setFirstResult(start);
		if (pageSize != null)
			query.setMaxResults(pageSize);

		return query.getResultList();
	}

	// Filtered queries implementation
	@Override
	public List<T> getAllByFilter(String propertyName, Object propertyValue) throws Exception {
		return getPagesByFilter(null, null, propertyName, propertyValue);
	}

	@Override
	public Long getCountAllByFilter(String propertyName, Object propertyValue) throws Exception {
		CriteriaBuilder cb = getCurrentSession().getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<T> root = cq.from(persistentClass);

		cq.select(cb.count(root)).where(cb.equal(root.get(propertyName), propertyValue));

		return getCurrentSession().createQuery(cq).getSingleResult();
	}

	// Advanced Query Methods
	@Override
	public List<T> findByConditions(ConditionGroup conditionGroup) {
		return findByConditions(conditionGroup, null, null, null);
	}

	@Override
	public List<T> findByConditions(ConditionGroup conditionGroup, Integer start, Integer pageSize) {
		return findByConditions(conditionGroup, start, pageSize, null);
	}

	@Override
	public List<T> findByConditions(ConditionGroup conditionGroup, Integer start, Integer pageSize, Sort sort) {
		CriteriaBuilder cb = getCurrentSession().getCriteriaBuilder();
		CriteriaQuery<T> cq = cb.createQuery(persistentClass);
		Root<T> root = cq.from(persistentClass);

		// Build predicates
		Predicate predicate = buildPredicates(cb, root, conditionGroup);
		cq.where(predicate);

		// Apply sorting
		if (sort != null) {
			List<Order> orders = new ArrayList<>();
			for (Sort.Order sortOrder : sort) {
				Path<?> path = resolvePath(root, sortOrder.getProperty());
				orders.add(sortOrder.isAscending() ? cb.asc(path) : cb.desc(path));
			}
			cq.orderBy(orders);
		}

		TypedQuery<T> query = getCurrentSession().createQuery(cq);
		if (start != null)
			query.setFirstResult(start);
		if (pageSize != null)
			query.setMaxResults(pageSize);

		return query.getResultList();
	}

	@Override
	public <R> List<R> findByConditionsWithProjection(ConditionGroup conditionGroup, Class<R> resultType,
			List<String> properties) {
		CriteriaBuilder cb = getCurrentSession().getCriteriaBuilder();
		CriteriaQuery<R> cq = cb.createQuery(resultType);
		Root<T> root = cq.from(persistentClass);

		// Build predicates
		cq.where(buildPredicates(cb, root, conditionGroup));

		// Build projection
		List<Selection<?>> selections = properties.stream().map(prop -> resolvePath(root, prop))
				.collect(Collectors.toList());

		cq.multiselect(selections);

		return getCurrentSession().createQuery(cq).getResultList();
	}

	@Override
	public List<Tuple> findByConditionsReturningTuples(ConditionGroup conditionGroup, List<String> properties) {
		CriteriaBuilder cb = getCurrentSession().getCriteriaBuilder();
		CriteriaQuery<Tuple> cq = cb.createTupleQuery();
		Root<T> root = cq.from(persistentClass);

		// Build predicates
		cq.where(buildPredicates(cb, root, conditionGroup));

		// Build tuple selection
		List<Selection<?>> selections = properties.stream().map(prop -> resolvePath(root, prop).alias(prop))
				.collect(Collectors.toList());

		cq.multiselect(selections);

		return getCurrentSession().createQuery(cq).getResultList();
	}

	public List<T> findByConditionsWithFetch(ConditionGroup conditionGroup, String... fetchPaths) {
		CriteriaBuilder cb = getCurrentSession().getCriteriaBuilder();
		CriteriaQuery<T> cq = cb.createQuery(persistentClass);
		Root<T> root = cq.from(persistentClass);

		// Apply fetch joins
		for (String path : fetchPaths) {
			String[] parts = path.split("\\.");
			From<?, ?> from = root;

			// Join all path segments except the last one
			for (int i = 0; i < parts.length - 1; i++) {
				from = from.join(parts[i], JoinType.LEFT);
			}

			// Fetch the last segment
			from.fetch(parts[parts.length - 1], JoinType.LEFT);
		}

		// Build predicates
		Predicate predicate = buildPredicates(cb, root, conditionGroup);
		if (predicate != null) {
			cq.where(predicate);
		}

		return getCurrentSession().createQuery(cq).getResultList();
	}

	// Helper methods
	private Predicate buildPredicates(CriteriaBuilder cb, From<?, ?> from, ConditionGroup group) {
		List<Predicate> predicates = new ArrayList<>();

		// Process individual conditions
		for (Condition condition : group.getConditions()) {
			Path<?> path = resolvePath(from, condition.getProperty());
			predicates.add(buildPredicate(cb, path, condition));
		}

		// Process nested groups
		for (ConditionGroup subGroup : group.getGroups()) {
			predicates.add(buildPredicates(cb, from, subGroup));
		}

		return group.getOperator() == LogicalOperator.OR ? cb.or(predicates.toArray(new Predicate[0]))
				: cb.and(predicates.toArray(new Predicate[0]));
	}

	private Path<?> resolvePath(From<?, ?> from, String propertyPath) {
		if (!propertyPath.contains(".")) {
			return from.get(propertyPath);
		}

		String[] parts = propertyPath.split("\\.");
		From<?, ?> currentFrom = from;

		for (int i = 0; i < parts.length - 1; i++) {
			currentFrom = currentFrom.join(parts[i], JoinType.LEFT);
		}

		return currentFrom.get(parts[parts.length - 1]);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private Predicate buildPredicate(CriteriaBuilder cb, Path<?> path, Condition condition) {
		Object value = condition.getValue();
		Operator operator = condition.getOperator();

		// Handle case-insensitive for String paths
		if (condition.isIgnoreCase() && value instanceof String && path.getJavaType() == String.class) {
			Expression<String> lowerPath = cb.lower((Expression<String>) path);
			String lowerValue = ((String) value).toLowerCase();

			switch (operator) {
			case EQUAL:
				return cb.equal(lowerPath, lowerValue);
			case NOT_EQUAL:
				return cb.notEqual(lowerPath, lowerValue);
			case LIKE:
				return cb.like(lowerPath, "%" + lowerValue + "%");
			case ILIKE:
				return cb.like(lowerPath, "%" + lowerValue + "%");
			case NOT_LIKE:
				return cb.notLike(lowerPath, "%" + lowerValue + "%");
			case STARTS_WITH:
				return cb.like(lowerPath, lowerValue + "%");
			case ENDS_WITH:
				return cb.like(lowerPath, "%" + lowerValue);
			default: // fall through to regular handling
			}
		}

		// Type-safe handling based on path type
		switch (operator) {
		case EQUAL:
			return cb.equal(path, value);
		case NOT_EQUAL:
			return cb.notEqual(path, value);
		case GREATER_THAN:
			if (Number.class.isAssignableFrom(path.getJavaType())) {
				return cb.gt((Path<Number>) path, (Number) value);
			} else if (Comparable.class.isAssignableFrom(path.getJavaType())) {
				return cb.greaterThan((Path<Comparable>) path, (Comparable) value);
			}
			break;
		case GREATER_THAN_OR_EQUAL:
			if (Number.class.isAssignableFrom(path.getJavaType())) {
				return cb.ge((Path<Number>) path, (Number) value);
			} else if (Comparable.class.isAssignableFrom(path.getJavaType())) {
				return cb.greaterThanOrEqualTo((Path<Comparable>) path, (Comparable) value);
			}
			break;
		case LESS_THAN:
			if (Number.class.isAssignableFrom(path.getJavaType())) {
				return cb.lt((Path<Number>) path, (Number) value);
			} else if (Comparable.class.isAssignableFrom(path.getJavaType())) {
				return cb.lessThan((Path<Comparable>) path, (Comparable) value);
			}
			break;
		case LESS_THAN_OR_EQUAL:
			if (Number.class.isAssignableFrom(path.getJavaType())) {
				return cb.le((Path<Number>) path, (Number) value);
			} else if (Comparable.class.isAssignableFrom(path.getJavaType())) {
				return cb.lessThanOrEqualTo((Path<Comparable>) path, (Comparable) value);
			}
			break;
		case LIKE:
			if (path.getJavaType() == String.class) {
				return cb.like((Path<String>) path, "%" + value + "%");
			}
			break;
		case ILIKE:
			if (path.getJavaType() == String.class) {
				return cb.like(cb.lower((Path<String>) path), "%" + ((String) value).toLowerCase() + "%");
			}
			break;
		case NOT_LIKE:
			if (path.getJavaType() == String.class) {
				return cb.notLike((Path<String>) path, "%" + value + "%");
			}
			break;
		case IS_NULL:
			return cb.isNull(path);
		case IS_NOT_NULL:
			return cb.isNotNull(path);
		case IN:
			if (value instanceof Collection) {
				return path.in((Collection<?>) value);
			} else if (value instanceof Object[]) {
				return path.in((Object[]) value);
			}
			break;
		case NOT_IN:
			if (value instanceof Collection) {
				return cb.not(path.in((Collection<?>) value));
			} else if (value instanceof Object[]) {
				return cb.not(path.in((Object[]) value));
			}
			break;
		case BETWEEN:
			Object[] range = (Object[]) value;
			return cb.between((Path<Comparable>) path, (Comparable) range[0], (Comparable) range[1]);
		case DATE_BEFORE:
			if (Date.class.isAssignableFrom(path.getJavaType())) {
				return cb.lessThan((Path<Date>) path, (Date) value);
			}
			break;
		case DATE_AFTER:
			if (Date.class.isAssignableFrom(path.getJavaType())) {
				return cb.greaterThan((Path<Date>) path, (Date) value);
			}
			break;
		case DATE_ON:
			if (Date.class.isAssignableFrom(path.getJavaType())) {
				Date date = (Date) value;
				Calendar cal = Calendar.getInstance();
				cal.setTime(date);
				cal.add(Calendar.DAY_OF_MONTH, 1);
				return cb.between((Path<Date>) path, date, cal.getTime());
			}
			break;
		case CONTAINS:
			if (Collection.class.isAssignableFrom(path.getJavaType())) {
				return cb.isMember(value, (Path<Collection>) path);
			}
			break;
		case STARTS_WITH:
			if (path.getJavaType() == String.class) {
				return cb.like((Path<String>) path, value + "%");
			}
			break;
		case ENDS_WITH:
			if (path.getJavaType() == String.class) {
				return cb.like((Path<String>) path, "%" + value);
			}
			break;
		case JSON_CONTAINS:
			return cb.equal(cb.function("json_contains", Boolean.class, path, cb.literal(value)), true);
		case JSON_PATH:
			return cb.equal(cb.function("json_extract", String.class, path, cb.literal("$." + value)), value);
		case IS_TRUE:
			if (path.getJavaType() == Boolean.class || path.getJavaType() == boolean.class) {
				return cb.isTrue((Expression<Boolean>) path);
			}
			break;
		case IS_FALSE:
			if (path.getJavaType() == Boolean.class || path.getJavaType() == boolean.class) {
				return cb.isFalse((Expression<Boolean>) path);
			}
			break;
		}

		throw new IllegalArgumentException("Unsupported operator/type combination: " + operator + " for type "
				+ path.getJavaType().getSimpleName());
	}

	// Enhanced methods implementation

	public List<T> findByCriteria(Map<String, Object> propertyMap, Map<String, Operator> operators, Integer start,
			Integer pageSize, LogicalOperator logicalOperator) throws Exception {

		CriteriaBuilder cb = getCurrentSession().getCriteriaBuilder();
		CriteriaQuery<T> cq = cb.createQuery(persistentClass);
		Root<T> root = cq.from(persistentClass);

		List<Predicate> predicates = new ArrayList<>();

		for (Map.Entry<String, Object> entry : propertyMap.entrySet()) {
			String propertyPath = entry.getKey();
			Object value = entry.getValue();
			Operator operator = operators.getOrDefault(propertyPath, Operator.EQUAL);

			if (propertyPath.contains(".")) {
				String[] pathParts = propertyPath.split("\\.");
				From<?, ?> from = root;

				for (int i = 0; i < pathParts.length - 1; i++) {
					from = from.join(pathParts[i], JoinType.INNER);
				}

				predicates.add(createPredicate(cb, from.get(pathParts[pathParts.length - 1]), value, operator));
			} else {
				predicates.add(createPredicate(cb, root.get(propertyPath), value, operator));
			}
		}

		// Apply the logical operator (AND or OR)
		Predicate finalPredicate = logicalOperator == LogicalOperator.OR ? cb.or(predicates.toArray(new Predicate[0]))
				: cb.and(predicates.toArray(new Predicate[0]));

		cq.select(root).where(finalPredicate);

		TypedQuery<T> query = getCurrentSession().createQuery(cq);
		if (start != null)
			query.setFirstResult(start);
		if (pageSize != null)
			query.setMaxResults(pageSize);

		return query.getResultList();
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private Predicate createPredicate(CriteriaBuilder cb, Path<?> path, Object value, Operator operator) {
		switch (operator) {
		case EQUAL:
			return cb.equal(path, value);
		case NOT_EQUAL:
			return cb.notEqual(path, value);
		case GREATER_THAN:
			return cb.greaterThan((Path<Comparable>) path, (Comparable) value);
		case GREATER_THAN_OR_EQUAL:
			return cb.greaterThanOrEqualTo((Path<Comparable>) path, (Comparable) value);
		case LESS_THAN:
			return cb.lessThan((Path<Comparable>) path, (Comparable) value);
		case LESS_THAN_OR_EQUAL:
			return cb.lessThanOrEqualTo((Path<Comparable>) path, (Comparable) value);
		case LIKE:
			return cb.like((Path<String>) path, "%" + value + "%");
		case ILIKE:
			return cb.like(cb.lower((Path<String>) path), "%" + ((String) value).toLowerCase() + "%");
		case NOT_LIKE:
			return cb.notLike((Path<String>) path, "%" + value + "%");
		case IS_NULL:
			return cb.isNull(path);
		case IS_NOT_NULL:
			return cb.isNotNull(path);
		case IN:
			return path.in((Collection<?>) value);
		case NOT_IN:
			return cb.not(path.in((Collection<?>) value));
		case BETWEEN:
			Object[] range = (Object[]) value;
			return cb.between((Path<Comparable>) path, (Comparable) range[0], (Comparable) range[1]);
		case IS_TRUE:
			if (path.getJavaType() == Boolean.class || path.getJavaType() == boolean.class) {
				return cb.isTrue((Expression<Boolean>) path);
			}
			break;
		case IS_FALSE:
			if (path.getJavaType() == Boolean.class || path.getJavaType() == boolean.class) {
				return cb.isFalse((Expression<Boolean>) path);
			}
			break;
		default:
			throw new IllegalArgumentException("Unsupported operator: " + operator);
		}
		throw new IllegalArgumentException("Unsupported operator/type combination: " + operator + " for type "
				+ path.getJavaType().getSimpleName());
	}

	@Override
	public List<T> findByCriteria(Map<String, Object> propertyMap, Integer start, Integer pageSize,
			LogicalOperator operator) throws Exception {
		CriteriaBuilder cb = getCurrentSession().getCriteriaBuilder();
		CriteriaQuery<T> cq = cb.createQuery(persistentClass);
		Root<T> root = cq.from(persistentClass);

		List<Predicate> predicates = new ArrayList<>();

		for (Map.Entry<String, Object> entry : propertyMap.entrySet()) {
			String propertyPath = entry.getKey();
			Object value = entry.getValue();

			if (propertyPath.contains(".")) {
				String[] pathParts = propertyPath.split("\\.");
				From<?, ?> from = root;

				for (int i = 0; i < pathParts.length - 1; i++) {
					from = from.join(pathParts[i], JoinType.INNER);
				}

				predicates.add(cb.equal(from.get(pathParts[pathParts.length - 1]), value));
			} else {
				predicates.add(cb.equal(root.get(propertyPath), value));
			}
		}

		// Apply the logical operator (AND or OR)
		Predicate finalPredicate;
		if (operator == LogicalOperator.OR) {
			finalPredicate = cb.or(predicates.toArray(new Predicate[0]));
		} else {
			// Default to AND if not specified
			finalPredicate = cb.and(predicates.toArray(new Predicate[0]));
		}

		cq.select(root).where(finalPredicate);

		TypedQuery<T> query = getCurrentSession().createQuery(cq);
		if (start != null)
			query.setFirstResult(start);
		if (pageSize != null)
			query.setMaxResults(pageSize);

		return query.getResultList();
	}

	@Override
	public List<T> findByCriteria(Map<String, Object> propertyMap, Integer start, Integer pageSize,
			LogicalOperator defaultOperator, Map<String, LogicalOperator> customOperators) throws Exception {

		CriteriaBuilder cb = getCurrentSession().getCriteriaBuilder();
		CriteriaQuery<T> cq = cb.createQuery(persistentClass);
		Root<T> root = cq.from(persistentClass);

		List<Predicate> predicates = new ArrayList<>();

		for (Map.Entry<String, Object> entry : propertyMap.entrySet()) {
			String propertyPath = entry.getKey();
			Object value = entry.getValue();
			LogicalOperator currentOperator = customOperators.getOrDefault(propertyPath, defaultOperator);

			if (propertyPath.contains(".")) {
				String[] pathParts = propertyPath.split("\\.");
				From<?, ?> from = root;

				for (int i = 0; i < pathParts.length - 1; i++) {
					from = from.join(pathParts[i], JoinType.INNER);
				}

				predicates.add(createPredicate(cb, from.get(pathParts[pathParts.length - 1]), value, currentOperator));
			} else {
				predicates.add(createPredicate(cb, root.get(propertyPath), value, currentOperator));
			}
		}

		cq.select(root).where(cb.and(predicates.toArray(new Predicate[0])));

		TypedQuery<T> query = getCurrentSession().createQuery(cq);
		if (start != null)
			query.setFirstResult(start);
		if (pageSize != null)
			query.setMaxResults(pageSize);

		return query.getResultList();
	}

	@Override
	public List<T> findByCriteria(Map<String, Object> propertyMap, Map<String, Operator> operators, Integer start,
			Integer pageSize, LogicalOperator defaultOperator, Map<String, LogicalOperator> customOperators)
			throws Exception {
		CriteriaBuilder cb = getCurrentSession().getCriteriaBuilder();
		CriteriaQuery<T> cq = cb.createQuery(persistentClass);
		Root<T> root = cq.from(persistentClass);

		// Separate predicates into OR and AND groups
		List<Predicate> orPredicates = new ArrayList<>();
		List<Predicate> andPredicates = new ArrayList<>();

		// Process each property in the propertyMap
		for (Map.Entry<String, Object> entry : propertyMap.entrySet()) {
			String propertyPath = entry.getKey();
			Object value = entry.getValue();
			Operator operator = operators != null ? operators.getOrDefault(propertyPath, Operator.EQUAL)
					: Operator.EQUAL;
			LogicalOperator logicalOperator = customOperators != null
					? customOperators.getOrDefault(propertyPath, defaultOperator)
					: defaultOperator;

			// Handle nested properties (e.g., "address.city")
			Path<?> path;
			if (propertyPath.contains(".")) {
				String[] pathParts = propertyPath.split("\\.");
				From<?, ?> from = root;
				for (int i = 0; i < pathParts.length - 1; i++) {
					from = from.join(pathParts[i], JoinType.INNER);
				}
				path = from.get(pathParts[pathParts.length - 1]);
			} else {
				path = root.get(propertyPath);
			}

			// Create predicate using the operator
			Predicate predicate = createPredicate(cb, path, value, operator);

			// Add to appropriate predicate list based on logical operator
			if (logicalOperator == LogicalOperator.AND) {
				andPredicates.add(predicate);
			} else {
				orPredicates.add(predicate);
			}
		}

		// Combine predicates
		Predicate finalPredicate;
		if (!orPredicates.isEmpty() && !andPredicates.isEmpty()) {
			// Combine OR predicates into a single predicate
			Predicate orPredicate = cb.or(orPredicates.toArray(new Predicate[0]));
			// Combine with AND predicates
			List<Predicate> allPredicates = new ArrayList<>();
			allPredicates.add(orPredicate);
			allPredicates.addAll(andPredicates);
			finalPredicate = cb.and(allPredicates.toArray(new Predicate[0]));
		} else if (!orPredicates.isEmpty()) {
			// Only OR predicates
			finalPredicate = cb.or(orPredicates.toArray(new Predicate[0]));
		} else if (!andPredicates.isEmpty()) {
			// Only AND predicates
			finalPredicate = cb.and(andPredicates.toArray(new Predicate[0]));
		} else {
			// No predicates, return all results
			finalPredicate = cb.conjunction();
		}

		// Apply the final predicate to the query
		cq.select(root).where(finalPredicate);

		// Create and configure the query
		TypedQuery<T> query = getCurrentSession().createQuery(cq);
		if (start != null) {
			query.setFirstResult(start);
		}
		if (pageSize != null) {
			query.setMaxResults(pageSize);
		}

		try {
			return query.getResultList();
		} catch (Exception e) {
			LOGGER.error("Error executing findByCriteria with properties: {}", propertyMap, e);
			throw new Exception("Failed to execute findByCriteria", e);
		}
	}

	@Override
	public Long countByCriteria(Map<String, Object> propertyMap, Map<String, Operator> operators,
			LogicalOperator defaultOperator, Map<String, LogicalOperator> customOperators) throws Exception {
		CriteriaBuilder cb = getCurrentSession().getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<T> root = cq.from(persistentClass);

		// Separate predicates into OR and AND groups
		List<Predicate> orPredicates = new ArrayList<>();
		List<Predicate> andPredicates = new ArrayList<>();

		// Process each property in the propertyMap
		for (Map.Entry<String, Object> entry : propertyMap.entrySet()) {
			String propertyPath = entry.getKey();
			Object value = entry.getValue();
			Operator operator = operators != null ? operators.getOrDefault(propertyPath, Operator.EQUAL)
					: Operator.EQUAL;
			LogicalOperator logicalOperator = customOperators != null
					? customOperators.getOrDefault(propertyPath, defaultOperator)
					: defaultOperator;

			// Handle nested properties (e.g., "address.city")
			Path<?> path;
			if (propertyPath.contains(".")) {
				String[] pathParts = propertyPath.split("\\.");
				From<?, ?> from = root;
				for (int i = 0; i < pathParts.length - 1; i++) {
					from = from.join(pathParts[i], JoinType.INNER);
				}
				path = from.get(pathParts[pathParts.length - 1]);
			} else {
				path = root.get(propertyPath);
			}

			// Create predicate using the operator
			Predicate predicate = createPredicate(cb, path, value, operator);

			// Add to appropriate predicate list based on logical operator
			if (logicalOperator == LogicalOperator.AND) {
				andPredicates.add(predicate);
			} else {
				orPredicates.add(predicate);
			}
		}

		// Combine predicates
		Predicate finalPredicate;
		if (!orPredicates.isEmpty() && !andPredicates.isEmpty()) {
			Predicate orPredicate = cb.or(orPredicates.toArray(new Predicate[0]));
			List<Predicate> allPredicates = new ArrayList<>();
			allPredicates.add(orPredicate);
			allPredicates.addAll(andPredicates);
			finalPredicate = cb.and(allPredicates.toArray(new Predicate[0]));
		} else if (!orPredicates.isEmpty()) {
			finalPredicate = cb.or(orPredicates.toArray(new Predicate[0]));
		} else if (!andPredicates.isEmpty()) {
			finalPredicate = cb.and(andPredicates.toArray(new Predicate[0]));
		} else {
			finalPredicate = cb.conjunction();
		}

		// Apply the final predicate to the count query
		cq.select(cb.count(root)).where(finalPredicate);

		try {
			return getCurrentSession().createQuery(cq).getSingleResult();
		} catch (Exception e) {
			LOGGER.error("Error executing countByCriteria with properties: {}", propertyMap, e);
			throw new Exception("Failed to execute countByCriteria", e);
		}
	}

	private Predicate createPredicate(CriteriaBuilder cb, Path<?> path, Object value, LogicalOperator operator) {
		if (operator == LogicalOperator.OR) {
			return cb.or(cb.equal(path, value));
		}
		return cb.equal(path, value);
	}

	@Override
	public List<T> findByCriteria(Map<String, Object> criteria, int first, int pageSize, String sortField,
			boolean ascending) throws Exception {
		try {
			CriteriaBuilder cb = getCurrentSession().getCriteriaBuilder();
			CriteriaQuery<T> cq = cb.createQuery(persistentClass);
			Root<T> root = cq.from(persistentClass);
			List<Predicate> predicates = new ArrayList<>();

			// Handle criteria
			if (criteria != null) {
				for (Map.Entry<String, Object> entry : criteria.entrySet()) {
					String key = entry.getKey();
					Object value = entry.getValue();
					if (value != null) {
						try {
							Path<?> path = getPath(root, key);
							if (value instanceof String && ((String) value).contains("%")) {
								predicates
										.add(cb.like(cb.lower(path.as(String.class)), ((String) value).toLowerCase()));
							} else {
								predicates.add(cb.equal(path, value));
							}
						} catch (Exception e) {
							LOGGER.error("Failed to process criterion: {} = {}", key, value, e);
						}
					}
				}
			}

			// Apply predicates
			cq.select(root).where(cb.and(predicates.toArray(new Predicate[0]))).distinct(true);

			// Apply sorting
			if (sortField != null && !sortField.isEmpty()) {
				try {
					Path<?> sortPath = getPath(root, sortField);
					cq.orderBy(ascending ? cb.asc(sortPath) : cb.desc(sortPath));
				} catch (Exception e) {
					LOGGER.error("Failed to apply sorting for field: {}", sortField, e);
				}
			}

			// Create query
			TypedQuery<T> query = getCurrentSession().createQuery(cq);
			query.setFirstResult(first);
			query.setMaxResults(pageSize);

			return query.getResultList();
		} catch (Exception e) {
			LOGGER.error("Error in findByCriteria", e);
			throw new Exception("Failed to execute findByCriteria", e);
		}
	}

	@Override
	public Long countByCriteria(Map<String, Object> criteria) throws Exception {
		try {
			CriteriaBuilder cb = getCurrentSession().getCriteriaBuilder();
			CriteriaQuery<Long> cq = cb.createQuery(Long.class);
			Root<T> root = cq.from(persistentClass);
			List<Predicate> predicates = new ArrayList<>();

			// Handle criteria
			if (criteria != null) {
				for (Map.Entry<String, Object> entry : criteria.entrySet()) {
					String key = entry.getKey();
					Object value = entry.getValue();
					if (value != null) {
						try {
							Path<?> path = getPath(root, key);
							if (value instanceof String && ((String) value).contains("%")) {
								predicates
										.add(cb.like(cb.lower(path.as(String.class)), ((String) value).toLowerCase()));
							} else {
								predicates.add(cb.equal(path, value));
							}
						} catch (Exception e) {
							LOGGER.error("Failed to process count criterion: {} = {}", key, value, e);
						}
					}
				}
			}

			// Apply predicates
			cq.select(cb.countDistinct(root)).where(cb.and(predicates.toArray(new Predicate[0])));

			return getCurrentSession().createQuery(cq).getSingleResult();
		} catch (Exception e) {
			LOGGER.error("Error in countByCriteria", e);
			throw new Exception("Failed to execute countByCriteria", e);
		}
	}

	private Path<?> getPath(Root<T> root, String path) throws Exception {
		String[] parts = path.split("\\.");
		Path<?> currentPath = root;
		Class<?> currentClass = persistentClass;

		for (int i = 0; i < parts.length; i++) {
			String part = parts[i];
			try {
				// Check if the property is a collection
				boolean isCollection = isCollectionProperty(currentClass, part);
				if (isCollection && currentPath instanceof Root) {
					currentPath = ((Root<?>) currentPath).join(part, JoinType.LEFT);
					// Update currentClass to the collection's element type
					Field field = getField(currentClass, part);
					if (field.getGenericType() instanceof ParameterizedType) {
						ParameterizedType pType = (ParameterizedType) field.getGenericType();
						currentClass = (Class<?>) pType.getActualTypeArguments()[0];
					}
				} else {
					currentPath = currentPath.get(part);
					// Update currentClass to the property's type
					Field field = getField(currentClass, part);
					currentClass = field.getType();
				}
			} catch (Exception e) {
				LOGGER.error("Invalid path segment: {} in {}", part, path, e);
				throw new Exception("Invalid property path: " + path, e);
			}
		}
		return currentPath;
	}

	private boolean isCollectionProperty(Class<?> clazz, String propertyName) {
		try {
			Field field = getField(clazz, propertyName);
			return Collection.class.isAssignableFrom(field.getType());
		} catch (Exception e) {
			LOGGER.warn("Could not determine if {} is a collection in {}", propertyName, clazz.getSimpleName());
			return false;
		}
	}

	private Field getField(Class<?> clazz, String fieldName) throws NoSuchFieldException {
		try {
			return clazz.getDeclaredField(fieldName);
		} catch (NoSuchFieldException e) {
			// Try superclass
			if (clazz.getSuperclass() != null) {
				return getField(clazz.getSuperclass(), fieldName);
			}
			throw e;
		}
	}

	@Override
	public List<T> findByCriteria(Map<String, Object> propertyMap) throws Exception {
		return findByCriteria(propertyMap, null, null);
	}

	@Override
	public List<T> findByCriteria(Map<String, Object> propertyMap, Integer start, Integer pageSize) throws Exception {
		CriteriaBuilder cb = getCurrentSession().getCriteriaBuilder();
		CriteriaQuery<T> cq = cb.createQuery(persistentClass);
		Root<T> root = cq.from(persistentClass);

		List<Predicate> predicates = new ArrayList<>();

		for (Map.Entry<String, Object> entry : propertyMap.entrySet()) {
			String propertyPath = entry.getKey();
			Object value = entry.getValue();

			// Handle nested properties (e.g., "address.city")
			if (propertyPath.contains(".")) {
				String[] pathParts = propertyPath.split("\\.");
				From<?, ?> from = root;

				// Navigate through the path (except the last part)
				for (int i = 0; i < pathParts.length - 1; i++) {
					from = from.join(pathParts[i], JoinType.INNER);
				}

				// Add predicate for the final property
				predicates.add(cb.equal(from.get(pathParts[pathParts.length - 1]), value));
			} else {
				// Simple property
				predicates.add(cb.equal(root.get(propertyPath), value));
			}
		}

		cq.select(root).where(cb.and(predicates.toArray(new Predicate[0])));

		TypedQuery<T> query = getCurrentSession().createQuery(cq);
		if (start != null)
			query.setFirstResult(start);
		if (pageSize != null)
			query.setMaxResults(pageSize);

		return query.getResultList();
	}

	@Override
	public List<T> findByLike(String propertyName, String value) throws Exception {
		CriteriaBuilder cb = getCurrentSession().getCriteriaBuilder();
		CriteriaQuery<T> cq = cb.createQuery(persistentClass);
		Root<T> root = cq.from(persistentClass);

		cq.select(root).where(cb.like(root.get(propertyName), "%" + value + "%"));

		return getCurrentSession().createQuery(cq).getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<T> findByRange(String propertyName, Comparable fromValue, Comparable toValue) throws Exception {
		CriteriaBuilder cb = getCurrentSession().getCriteriaBuilder();
		CriteriaQuery<T> cq = cb.createQuery(persistentClass);
		Root<T> root = cq.from(persistentClass);

		cq.select(root).where(cb.between(root.get(propertyName), fromValue, toValue));

		return getCurrentSession().createQuery(cq).getResultList();
	}

	@Override
	public List<T> findIn(String propertyName, List<?> values) throws Exception {
		if (values == null || values.isEmpty())
			return new ArrayList<>();

		CriteriaBuilder cb = getCurrentSession().getCriteriaBuilder();
		CriteriaQuery<T> cq = cb.createQuery(persistentClass);
		Root<T> root = cq.from(persistentClass);

		cq.select(root).where(root.get(propertyName).in(values));

		return getCurrentSession().createQuery(cq).getResultList();
	}

	@Override
	public List<T> getPagesWithSorting(Integer start, Integer pageSize, String sortProperty, boolean ascending)
			throws Exception {
		CriteriaBuilder cb = getCurrentSession().getCriteriaBuilder();
		CriteriaQuery<T> cq = cb.createQuery(persistentClass);
		Root<T> root = cq.from(persistentClass);

		Order order = ascending ? cb.asc(root.get(sortProperty)) : cb.desc(root.get(sortProperty));
		cq.select(root).orderBy(order);

		TypedQuery<T> query = getCurrentSession().createQuery(cq);
		if (start != null)
			query.setFirstResult(start);
		if (pageSize != null)
			query.setMaxResults(pageSize);

		return query.getResultList();
	}

	@Override
	public List<T> findAdvanced(Integer start, Integer pageSize, String sortProperty, Boolean ascending,
			Map<String, Object> filters) throws Exception {
		CriteriaBuilder cb = getCurrentSession().getCriteriaBuilder();
		CriteriaQuery<T> cq = cb.createQuery(persistentClass);
		Root<T> root = cq.from(persistentClass);

		// Build WHERE clause
		if (filters != null && !filters.isEmpty()) {
			List<Predicate> predicates = new ArrayList<>();
			filters.forEach((property, value) -> {
				if (value != null) {
					predicates.add(cb.equal(root.get(property), value));
				}
			});
			cq.where(cb.and(predicates.toArray(new Predicate[0])));
		}

		// Add ORDER BY
		if (sortProperty != null && !sortProperty.isEmpty()) {
			Order order = Boolean.TRUE.equals(ascending) ? cb.asc(root.get(sortProperty))
					: cb.desc(root.get(sortProperty));
			cq.orderBy(order);
		}

		TypedQuery<T> query = getCurrentSession().createQuery(cq);
		if (start != null && start >= 0) {
			query.setFirstResult(start);
		}
		if (pageSize != null && pageSize > 0) {
			query.setMaxResults(pageSize);
		}

		return query.getResultList();
	}

	// Child/parent relationship methods implementation
	@Override
	public T findByIdWithChildren(Id id, String... childRelations) throws Exception {
		CriteriaBuilder cb = getCurrentSession().getCriteriaBuilder();
		CriteriaQuery<T> cq = cb.createQuery(persistentClass);
		Root<T> root = cq.from(persistentClass);

		for (String relation : childRelations) {
			root.fetch(relation, JoinType.LEFT);
		}

		cq.select(root).where(cb.equal(root.get("id"), id));

		try {
			return getCurrentSession().createQuery(cq).getSingleResult();
		} catch (NoResultException e) {
			return null;
		}
	}

	@Override
	public T findByIdWithAllRelations(Id id) throws Exception {
		if (id == null) {
			return null;
		}

		Session session = getCurrentSession();
		SessionFactoryImplementor sfi = (SessionFactoryImplementor) session.getSessionFactory();
		EntityPersister persister = sfi.getMetamodel().entityPersister(persistentClass.getName());

		CriteriaBuilder cb = session.getCriteriaBuilder();
		CriteriaQuery<T> cq = cb.createQuery(persistentClass);
		Root<T> root = cq.from(persistentClass);

		// Get all property names from the HBM mapping
		String[] propertyNames = persister.getPropertyNames();

		for (String propertyName : propertyNames) {
			Type propertyType = persister.getPropertyType(propertyName);

			if (propertyType.isAssociationType()) {
				try {
					if (propertyType.isCollectionType()) {
						// Handle collection-valued associations (one-to-many)
						root.fetch(propertyName, JoinType.LEFT);
					} else {
						// Handle single-valued associations (many-to-one)
						root.fetch(propertyName, JoinType.LEFT);
					}
				} catch (Exception e) {
					log.warn("Failed to fetch association field: " + propertyName, e);
				}
			}
		}

		// Use the correct identifier property name from the mapping
		String idPropertyName = persister.getIdentifierPropertyName();
		cq.select(root).where(cb.equal(root.get(idPropertyName), id));

		try {
			return session.createQuery(cq).setHint("org.hibernate.cacheable", false).getSingleResult();
		} catch (NoResultException e) {
			return null;
		}
	}

	@Override
	public <C> List<T> findByChildCriteria(Class<C> childEntityClass, String childProperty, Object childValue,
			String... fetchRelations) throws Exception {
		CriteriaBuilder cb = getCurrentSession().getCriteriaBuilder();
		CriteriaQuery<T> cq = cb.createQuery(persistentClass);
		Root<T> root = cq.from(persistentClass);

		Join<T, C> childJoin = root.join(childEntityClass.getSimpleName(), JoinType.INNER);

		for (String relation : fetchRelations) {
			root.fetch(relation, JoinType.LEFT);
		}

		cq.select(root).distinct(true).where(cb.equal(childJoin.get(childProperty), childValue));

		return getCurrentSession().createQuery(cq).getResultList();
	}

	@SuppressWarnings("unused")
	@Override
	public <C> List<T> findByChildCollection(String childCollectionProperty,
			Consumer<CriteriaBuilderHelper<C>> childCriteria, FetchStrategy fetchStrategy) throws Exception {
		CriteriaBuilder cb = getCurrentSession().getCriteriaBuilder();
		CriteriaQuery<T> cq = cb.createQuery(persistentClass);
		Root<T> root = cq.from(persistentClass);

		// Handle the join based on fetch strategy
		Join<T, C> childJoin = null;
		if (fetchStrategy == FetchStrategy.EAGER || fetchStrategy == FetchStrategy.JOIN) {
			childJoin = root.join(childCollectionProperty,
					fetchStrategy == FetchStrategy.JOIN ? JoinType.INNER : JoinType.LEFT);
		}

		if (childCriteria != null) {
			// Create the appropriate From object for criteria building
			From<?, C> criteriaFrom = childJoin != null ? childJoin : root.join(childCollectionProperty);

			// Create new Root<C> for the CriteriaBuilderHelper
			CriteriaQuery<C> childCq = cb.createQuery(getChildEntityClass(persistentClass, childCollectionProperty));
			Root<C> childRoot = childCq.from(getChildEntityClass(persistentClass, childCollectionProperty));

			// Create the helper with the proper types
			CriteriaBuilderHelper<C> helper = new CriteriaBuilderHelper<>(cb, childRoot);
			childCriteria.accept(helper);

			// Apply the predicate to our main query
			cq.where(helper.build());
		}

		cq.select(root).distinct(true);
		return getCurrentSession().createQuery(cq).getResultList();
	}

	@SuppressWarnings("unchecked")
	private <C> Class<C> getChildEntityClass(Class<T> parentClass, String collectionProperty) {
		try {
			java.lang.reflect.Field field = parentClass.getDeclaredField(collectionProperty);
			ParameterizedType genericType = (ParameterizedType) field.getGenericType();
			return (Class<C>) genericType.getActualTypeArguments()[0];
		} catch (Exception e) {
			throw new IllegalArgumentException(
					"Could not determine child entity type for property " + collectionProperty, e);
		}
	}

	@Override
	public <C> T updateChildCollection(Id parentId, String childCollectionProperty, List<C> childrenToAdd,
			List<C> childrenToRemove, BiConsumer<C, T> childParentSetter) throws Exception {
		Session session = getCurrentSession();
		T parent = session.get(persistentClass, parentId);

		if (parent == null) {
			throw new EntityNotFoundException("Parent entity not found with id: " + parentId);
		}

		@SuppressWarnings("unchecked")
		Collection<C> children = (Collection<C>) new DirectFieldAccessor(parent)
				.getPropertyValue(childCollectionProperty);

		if (childrenToRemove != null) {
			for (C child : childrenToRemove) {
				children.remove(child);
				childParentSetter.accept(child, null);
			}
		}

		if (childrenToAdd != null) {
			for (C child : childrenToAdd) {
				children.add(child);
				childParentSetter.accept(child, parent);
			}
		}

		session.update(parent);
		return parent;
	}

	@Override
	public List<T> findByChildCount(String childCollectionProperty, ComparisonOperator comparisonOperator, long count)
			throws Exception {
		CriteriaBuilder cb = getCurrentSession().getCriteriaBuilder();
		CriteriaQuery<T> cq = cb.createQuery(persistentClass);
		Root<T> root = cq.from(persistentClass);

		Subquery<Long> subquery = cq.subquery(Long.class);
		Root<T> subRoot = subquery.from(persistentClass);
		subquery.select(cb.count(subRoot.join(childCollectionProperty))).where(cb.equal(subRoot, root));

		Predicate countPredicate;
		switch (comparisonOperator) {
		case GT:
			countPredicate = cb.gt(subquery, count);
			break;
		case LT:
			countPredicate = cb.lt(subquery, count);
			break;
		case GE:
			countPredicate = cb.ge(subquery, count);
			break;
		case LE:
			countPredicate = cb.le(subquery, count);
			break;
		default:
			countPredicate = cb.equal(subquery, count);
		}

		cq.select(root).where(countPredicate);
		return getCurrentSession().createQuery(cq).getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public <C> List<T> findByChildExists(String childCollectionProperty,
			Consumer<CriteriaBuilderHelper<C>> childCriteria) throws Exception {
		CriteriaBuilder cb = getCurrentSession().getCriteriaBuilder();
		CriteriaQuery<T> cq = cb.createQuery(persistentClass);
		Root<T> root = cq.from(persistentClass);

		Subquery<C> subquery = cq.subquery((Class<C>) childCriteria.getClass());
		Root<C> childRoot = subquery.from((Class<C>) childCriteria.getClass());
		subquery.select(childRoot).where(cb.equal(childRoot.get("parent"), root),
				buildChildPredicate(cb, childRoot, childCriteria));

		cq.select(root).where(cb.exists(subquery));
		return getCurrentSession().createQuery(cq).getResultList();
	}

	private <C> Predicate buildChildPredicate(CriteriaBuilder cb, Root<C> root,
			Consumer<CriteriaBuilderHelper<C>> criteria) {
		CriteriaBuilderHelper<C> helper = new CriteriaBuilderHelper<>(cb, root);
		criteria.accept(helper);
		return helper.build();
	}

	@Override
	public T findWithHierarchy(Id id, int maxDepth) throws Exception {
		CriteriaBuilder cb = getCurrentSession().getCriteriaBuilder();
		CriteriaQuery<T> cq = cb.createQuery(persistentClass);
		Root<T> root = cq.from(persistentClass);

		fetchRelationsRecursive(root, persistentClass, maxDepth, 1, new HashSet<>());

		cq.select(root).where(cb.equal(root.get("id"), id));

		try {
			return getCurrentSession().createQuery(cq).getSingleResult();
		} catch (NoResultException e) {
			return null;
		}
	}

	@SuppressWarnings("unused")
	private void fetchRelationsRecursive(From<?, ?> from, Class<?> entityClass, int maxDepth, int currentDepth,
			Set<String> fetchedPaths) {
		if (currentDepth > maxDepth)
			return;

		// Get all collection-valued relationships
		for (java.lang.reflect.Field field : entityClass.getDeclaredFields()) {
			if (Collection.class.isAssignableFrom(field.getType())) {
				String path = entityClass.getSimpleName() + "." + field.getName();
				if (!fetchedPaths.contains(path)) {
					// Fetch the relation
					Fetch<?, ?> fetch = from.fetch(field.getName(), JoinType.LEFT);
					fetchedPaths.add(path);

					// Get the child entity class
					Class<?> childClass = getCollectionGenericType(field);
					if (childClass != null) {
						// Create a new subquery to continue recursion
						CriteriaBuilder cb = getCurrentSession().getCriteriaBuilder();
						CriteriaQuery<?> subCq = cb.createQuery(childClass);
						Root<?> childRoot = subCq.from(childClass);

						// Recursively fetch nested relationships
						fetchRelationsRecursive(childRoot, childClass, maxDepth, currentDepth + 1, fetchedPaths);
					}
				}
			}
		}
	}

	private Class<?> getCollectionGenericType(java.lang.reflect.Field field) {
		if (field.getGenericType() instanceof ParameterizedType) {
			ParameterizedType pt = (ParameterizedType) field.getGenericType();
			if (pt.getActualTypeArguments().length > 0) {
				return (Class<?>) pt.getActualTypeArguments()[0];
			}
		}
		return null;
	}

	// Bulk operations implementation
	@Override
	public int bulkUpdate(Map<String, Object> updateProperties, Map<String, Object> criteriaProperties)
			throws Exception {
		CriteriaBuilder cb = getCurrentSession().getCriteriaBuilder();
		CriteriaUpdate<T> update = cb.createCriteriaUpdate(persistentClass);
		Root<T> root = update.from(persistentClass);

		updateProperties.forEach((property, value) -> {
			update.set(property, value);
		});

		if (criteriaProperties != null && !criteriaProperties.isEmpty()) {
			List<Predicate> predicates = new ArrayList<>();
			criteriaProperties.forEach((property, value) -> {
				predicates.add(cb.equal(root.get(property), value));
			});
			update.where(cb.and(predicates.toArray(new Predicate[0])));
		}

		return getCurrentSession().createQuery(update).executeUpdate();
	}

	@Override
	public int bulkDelete(Map<String, Object> criteriaProperties) throws Exception {
		CriteriaBuilder cb = getCurrentSession().getCriteriaBuilder();
		CriteriaDelete<T> delete = cb.createCriteriaDelete(persistentClass);
		Root<T> root = delete.from(persistentClass);

		if (criteriaProperties != null && !criteriaProperties.isEmpty()) {
			List<Predicate> predicates = new ArrayList<>();
			criteriaProperties.forEach((property, value) -> {
				predicates.add(cb.equal(root.get(property), value));
			});
			delete.where(cb.and(predicates.toArray(new Predicate[0])));
		}

		return getCurrentSession().createQuery(delete).executeUpdate();
	}

	@Override
	public List<T> batchInsert(List<T> entities, Integer batchSize) throws Exception {
		if (entities == null || entities.isEmpty()) {
			return entities;
		}

		Session session = getCurrentSession();
		int effectiveBatchSize = batchSize != null && batchSize > 0 ? batchSize : 20;

		for (int i = 0; i < entities.size(); i++) {
			session.persist(entities.get(i));

			if (i > 0 && i % effectiveBatchSize == 0) {
				session.flush();
				session.clear();
			}
		}

		return entities;
	}

	@Override
	public List<T> findBatchWithChildren(List<Id> ids, String... childRelations) throws Exception {
		if (ids == null || ids.isEmpty()) {
			return Collections.emptyList();
		}

		Session session = sessionFactory.getCurrentSession();
		CriteriaBuilder cb = session.getCriteriaBuilder();
		CriteriaQuery<T> cq = cb.createQuery(persistentClass);
		Root<T> root = cq.from(persistentClass);

		// Eagerly fetch all specified child relations
		for (String relation : childRelations) {
			root.fetch(relation, JoinType.LEFT);
		}

		// Apply ID filter
		cq.select(root).where(root.get("id").in(ids));

		// Configure batch fetching
		Query<T> query = session.createQuery(cq).setHint("hibernate.batch_fetch_style", "DYNAMIC") // Optimize
																									// collection
																									// loading
				.setHint("hibernate.batch_size", "30"); // Load collections in batches of 30

		return query.getResultList();
	}

	@Override
	public T findByIdWithCustomFetch(Id id, List<String> fetchPaths) throws Exception {
		Session session = getCurrentSession();

		// Create Criteria with dynamic fetching
		CriteriaBuilder cb = session.getCriteriaBuilder();
		CriteriaQuery<T> cq = cb.createQuery(persistentClass);
		Root<T> root = cq.from(persistentClass);

		// Apply dynamic fetch joins
		for (String path : fetchPaths) {
			String[] parts = path.split("\\.");
			From<?, ?> currentFrom = root;

			for (int i = 0; i < parts.length; i++) {
				if (i == parts.length - 1) {
					currentFrom.fetch(parts[i], JoinType.LEFT);
				} else {
					currentFrom = ((Join<?, ?>) currentFrom.join(parts[i], JoinType.LEFT));
				}
			}
		}

		cq.select(root).where(cb.equal(root.get("id"), id));

		// Apply batch fetch optimization
		Query<T> query = session.createQuery(cq).setHint("hibernate.batch_fetch_style", "DYNAMIC")
				.setHint("hibernate.batch_size", "30");

		try {
			return query.getSingleResult();
		} catch (NoResultException e) {
			return null;
		}
	}

	/**
	 * Retrieves all entities with their parent relationships fetched.
	 * 
	 * @param parentRelations The parent relationship names to fetch
	 * @return List of entities with parent relationships initialized
	 * @throws Exception if there's an error during the operation
	 */
	public List<T> getAllWithFetchParent(String... parentRelations) {
		CriteriaBuilder cb = getCurrentSession().getCriteriaBuilder();
		CriteriaQuery<T> cq = cb.createQuery(persistentClass);
		Root<T> root = cq.from(persistentClass);

		for (String relation : parentRelations) {
			fetchRelationRecursively(root, relation, cb);
		}

		cq.select(root);
		return getCurrentSession().createQuery(cq).getResultList();
	}

	private void fetchRelationRecursively(From<?, ?> from, String relationPath, CriteriaBuilder cb) {
		String[] parts = relationPath.split("\\.");
		From<?, ?> currentFrom = from;

		for (int i = 0; i < parts.length; i++) {
			String part = parts[i];
			boolean isLast = (i == parts.length - 1);

			// Check if this might be a composite key relationship
			if (isPotentialCompositeKey(currentFrom, part)) {
				currentFrom = handlePotentialCompositeKeyJoin(currentFrom, part, isLast, cb);
				if (isLast) {
					fetchAllBasicAttributes(currentFrom);
				}
				continue;
			}

			// Standard join/fetch handling
			try {
				if (isLast) {
					try {
						currentFrom.fetch(part, JoinType.LEFT);
					} catch (IllegalArgumentException e) {
						Join<?, ?> join = currentFrom.join(part, JoinType.LEFT);
						fetchAllBasicAttributes(join);
					}
				} else {
					currentFrom = currentFrom.join(part, JoinType.LEFT);
				}
			} catch (Exception e) {
				// Fallback to composite key handling if standard join fails
				currentFrom = handlePotentialCompositeKeyJoin(currentFrom, part, isLast, cb);
				if (isLast) {
					fetchAllBasicAttributes(currentFrom);
				}
			}
		}
	}

	private boolean isPotentialCompositeKey(From<?, ?> from, String relation) {
		try {
			// 1. First check if target entity has explicit composite key markers
			Class<?> targetType = from.get(relation).getJavaType();

			// Check for @EmbeddedId or @IdClass
			if (hasExplicitCompositeKey(targetType)) {
				return true;
			}

			// 2. Check field name patterns more thoroughly
			Field[] targetFields = targetType.getDeclaredFields();
			Class<?> sourceType = from.getJavaType();
			Field[] sourceFields = sourceType.getDeclaredFields();

			// Build set of potential source field names
			Set<String> potentialSourceFields = new HashSet<>();
			for (Field targetField : targetFields) {
				potentialSourceFields.add(relation.toLowerCase() + targetField.getName().toLowerCase());
			}

			// Check if source has any matching fields
			for (Field sourceField : sourceFields) {
				if (potentialSourceFields.contains(sourceField.getName().toLowerCase())) {
					return true;
				}
			}

			// 3. Check if target has multiple @Id fields (implicit composite key)
			return hasMultipleIdFields(targetType);

		} catch (Exception e) {
			return false;
		}
	}

	private boolean hasExplicitCompositeKey(Class<?> entityType) {
		// Check for @EmbeddedId
		for (Field field : entityType.getDeclaredFields()) {
			if (field.isAnnotationPresent(javax.persistence.EmbeddedId.class)) {
				return true;
			}
		}

		// Check for @IdClass
		if (entityType.isAnnotationPresent(javax.persistence.IdClass.class)) {
			return true;
		}

		return false;
	}

	private boolean hasMultipleIdFields(Class<?> entityType) {
		int idFieldCount = 0;
		for (Field field : entityType.getDeclaredFields()) {
			if (field.isAnnotationPresent(javax.persistence.Id.class)) {
				idFieldCount++;
				if (idFieldCount > 1) {
					return true;
				}
			}
		}
		return false;
	}

	private From<?, ?> handlePotentialCompositeKeyJoin(From<?, ?> from, String relation, boolean isLast,
			CriteriaBuilder cb) {
		Join<?, ?> join = from.join(relation, JoinType.LEFT);

		try {
			Class<?> targetType = join.getModel().getBindableJavaType();
			Field[] fields = targetType.getDeclaredFields();

			for (Field field : fields) {
				String sourceFieldName = relation + capitalize(field.getName());
				try {
					Path<?> sourcePath = from.get(sourceFieldName);
					Path<?> targetPath = join.get(field.getName());

					if (sourcePath.getJavaType() == targetPath.getJavaType()) {
						join.on(cb.equal(sourcePath, targetPath));
						continue;
					}

					if (isStringAndNumberComparison(sourcePath, targetPath)) {
						join.on(handleStringNumberComparison(cb, sourcePath, targetPath));
					} else if (isNumericComparison(sourcePath, targetPath)) {
						join.on(handleNumericComparison(cb, sourcePath, targetPath));
					} else if (isBooleanComparison(sourcePath, targetPath)) {
						join.on(handleBooleanComparison(cb, sourcePath, targetPath));
					} else {
						join.on(cb.equal(sourcePath, targetPath));
					}
				} catch (IllegalArgumentException ignored) {
					// No matching property found
				}
			}
		} catch (Exception e) {
			log.warn("Error handling potential composite key for relation: " + relation, e);
		}

		if (isLast) {
			fetchAllBasicAttributes(join);
		}

		return join;
	}

	// Type Conversion Helpers
	private Predicate handleStringNumberComparison(CriteriaBuilder cb, Path<?> path1, Path<?> path2) {
		if (path1.getJavaType() == String.class) {
			return cb.equal(path1, convertToDatabaseString(cb, path2));
		} else {
			return cb.equal(convertToDatabaseString(cb, path1), path2);
		}
	}

	private Expression<String> convertToDatabaseString(CriteriaBuilder cb, Path<?> path) {
		if (path.getJavaType() == String.class) {
			return path.as(String.class);
		}

		try {
			String dialect = getDatabaseDialect();

			if (dialect.contains("postgresql")) {
				return cb.function("CAST", String.class, path, cb.literal("text"));
			} else if (dialect.contains("mysql")) {
				return cb.function("CONVERT", String.class, path, cb.literal("CHAR"));
			} else if (dialect.contains("oracle")) {
				return cb.function("TO_CHAR", String.class, path);
			} else if (dialect.contains("sqlserver")) {
				return cb.function("CONVERT", String.class, cb.literal("VARCHAR"), path);
			}
		} catch (Exception e) {
			log.warn("Failed to detect database dialect, using fallback conversion", e);
		}

		// Universal fallback
		return cb.function("CAST", String.class, path, cb.literal(String.class));
	}

	private String getDatabaseDialect() {
		try {
			SessionFactoryImplementor sessionFactory = (SessionFactoryImplementor) getCurrentSession()
					.getSessionFactory();
			Dialect dialect = sessionFactory.getJdbcServices().getDialect();
			return dialect.getClass().getSimpleName().toLowerCase();
		} catch (Exception e) {
			log.warn("Could not determine database dialect", e);
			return "unknown";
		}
	}

	private Predicate handleNumericComparison(CriteriaBuilder cb, Path<?> path1, Path<?> path2) {
		Class<?> widerType = getWiderNumberType(path1.getJavaType(), path2.getJavaType());
		return cb.equal(path1.as(widerType), path2.as(widerType));
	}

	private Class<?> getWiderNumberType(Class<?> type1, Class<?> type2) {
		// Order from narrowest to widest
		if (type1 == Byte.class || type1 == byte.class)
			return type2;
		if (type2 == Byte.class || type2 == byte.class)
			return type1;

		if (type1 == Short.class || type1 == short.class) {
			return isWiderThanShort(type2) ? type2 : Short.class;
		}
		if (type2 == Short.class || type2 == short.class) {
			return isWiderThanShort(type1) ? type1 : Short.class;
		}

		// Default to BigDecimal for maximum precision
		return BigDecimal.class;
	}

	private boolean isWiderThanShort(Class<?> type) {
		return type == Integer.class || type == int.class || type == Long.class || type == long.class
				|| type == Float.class || type == float.class || type == Double.class || type == double.class
				|| type == BigDecimal.class;
	}

	private Predicate handleBooleanComparison(CriteriaBuilder cb, Path<?> path1, Path<?> path2) {
		return cb.equal(path1.as(Boolean.class), path2.as(Boolean.class));
	}

	private boolean isStringAndNumberComparison(Path<?> path1, Path<?> path2) {
		return (path1.getJavaType() == String.class && isNumericType(path2.getJavaType()))
				|| (path2.getJavaType() == String.class && isNumericType(path1.getJavaType()));
	}

	private boolean isNumericComparison(Path<?> path1, Path<?> path2) {
		return isNumericType(path1.getJavaType()) && isNumericType(path2.getJavaType());
	}

	private boolean isBooleanComparison(Path<?> path1, Path<?> path2) {
		return (path1.getJavaType() == Boolean.class || path1.getJavaType() == boolean.class)
				|| (path2.getJavaType() == Boolean.class || path2.getJavaType() == boolean.class);
	}

	private boolean isNumericType(Class<?> type) {
		return Number.class.isAssignableFrom(type) || type == int.class || type == long.class || type == short.class
				|| type == byte.class || type == float.class || type == double.class;
	}

	// Basic Fetching Helpers
	private void fetchAllBasicAttributes(From<?, ?> from) {
		Class<?> entityType = from.getModel().getBindableJavaType();
		for (Field field : entityType.getDeclaredFields()) {
			try {
				if (!isAssociationField(field)) {
					from.fetch(field.getName(), JoinType.LEFT);
				}
			} catch (IllegalArgumentException e) {
				// Ignore if fetch fails
			}
		}
	}

	private boolean isAssociationField(Field field) {
		return field.getType().getName().startsWith("java.util.Set")
				|| field.getType().getName().startsWith("java.util.List")
				|| !field.getType().getName().startsWith("java.");
	}

	private String capitalize(String str) {
		return str.substring(0, 1).toUpperCase() + str.substring(1);
	}

	// Utility operations implementation
	@Override
	public boolean exists(String propertyName, Object propertyValue) throws Exception {
		CriteriaBuilder cb = getCurrentSession().getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<T> root = cq.from(persistentClass);

		cq.select(cb.count(root)).where(cb.equal(root.get(propertyName), propertyValue));

		return getCurrentSession().createQuery(cq).getSingleResult() > 0;
	}

	@SuppressWarnings("unchecked")
	@Override
	public T deepCopy(T entity, boolean copyChildren, String... childProperties) throws Exception {
		if (entity == null)
			return null;

		Session session = getCurrentSession();

		T copy = session.load(persistentClass, getId(entity));
		BeanUtils.copyProperties(entity, copy);

		if (copyChildren && childProperties != null) {
			for (String childProperty : childProperties) {
				Collection<Object> children = (Collection<Object>) new DirectFieldAccessor(entity)
						.getPropertyValue(childProperty);

				if (children != null) {
					Collection<Object> childrenCopy = new ArrayList<>(children.size());
					for (Object child : children) {
						Object childCopy = deepCopyChild(child);
						childrenCopy.add(childCopy);
					}

					new DirectFieldAccessor(copy).setPropertyValue(childProperty, childrenCopy);
				}
			}
		}

		return copy;
	}

	private Object deepCopyChild(Object child) {
		try {
			Object childCopy = child.getClass().newInstance();
			BeanUtils.copyProperties(child, childCopy);
			return childCopy;
		} catch (Exception e) {
			log.error("Error creating child copy", e);
			return child;
		}
	}

	private Serializable getId(T entity) {
		return (Serializable) new DirectFieldAccessor(entity).getPropertyValue("id");
	}

	@Override
	public void refresh(T entity) throws Exception {
		getCurrentSession().refresh(entity);
	}

	@Override
	public void flush() throws Exception {
		getCurrentSession().flush();
	}

	@Override
	public void clear() throws Exception {
		getCurrentSession().clear();
	}

	// Custom query execution implementation
	@Override
	public List<T> executeHQLQuery(String hql, Map<String, Object> params) throws Exception {
		Query<T> query = getCurrentSession().createQuery(hql, persistentClass);

		if (params != null) {
			params.forEach((name, value) -> {
				query.setParameter(name, value);
			});
		}

		return query.getResultList();
	}

	@Override
	public List<T> executeNativeQuery(String sql, Map<String, Object> params) throws Exception {
		Query<T> query = getCurrentSession().createNativeQuery(sql, persistentClass);

		if (params != null) {
			params.forEach((name, value) -> {
				query.setParameter(name, value);
			});
		}

		return query.getResultList();
	}

	// ========== ADVANCED QUERY IMPLEMENTATIONS ==========

	@Override
	public List<T> findBySpecification(Specification<T> spec, Pageable pageable) throws Exception {
		CriteriaBuilder cb = getCurrentSession().getCriteriaBuilder();
		CriteriaQuery<T> cq = cb.createQuery(persistentClass);
		Root<T> root = cq.from(persistentClass);

		Predicate predicate = spec.toPredicate(root, cq, cb);
		if (predicate != null) {
			cq.where(predicate);
		}

		if (pageable.getSort() != null) {
			cq.orderBy(getOrderList(pageable.getSort(), root, cb));
		}

		Query<T> query = getCurrentSession().createQuery(cq).setFirstResult((int) pageable.getOffset())
				.setMaxResults(pageable.getPageSize());

		return query.getResultList();
	}

	private List<Order> getOrderList(Sort sort, Root<T> root, CriteriaBuilder cb) {
		return sort.stream().map(order -> order.isAscending() ? cb.asc(root.get(order.getProperty()))
				: cb.desc(root.get(order.getProperty()))).collect(Collectors.toList());
	}

	@Override
	public List<T> findByGraphQuery(GraphQuery<T> graphQuery) throws Exception {
		CriteriaBuilder cb = getCurrentSession().getCriteriaBuilder();
		CriteriaQuery<T> cq = cb.createQuery(persistentClass);
		Root<T> root = cq.from(persistentClass);

		// Field selection
		if (graphQuery.getFields() != null && !graphQuery.getFields().isEmpty()) {
			cq.multiselect(graphQuery.getFields().stream().map(root::get).toArray(Selection[]::new));
		}

		// Joins
		if (graphQuery.getJoins() != null) {
			for (GraphQuery.JoinConfig join : graphQuery.getJoins()) {
				root.fetch(join.getPath(), join.getJoinType());
			}
		}

		// Filters
		if (graphQuery.getFilters() != null) {
			List<Predicate> predicates = new ArrayList<>();
			graphQuery.getFilters().forEach((field, value) -> {
				predicates.add(cb.equal(root.get(field), value));
			});
			cq.where(predicates.toArray(new Predicate[0]));
		}

		Query<T> query = getCurrentSession().createQuery(cq);

		// Pagination
		if (graphQuery.getPagination() != null) {
			query.setFirstResult(graphQuery.getPagination().getOffset())
					.setMaxResults(graphQuery.getPagination().getLimit());
		}

		return query.getResultList();
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public List<T> fullTextSearch(String searchTerm, String... fields) throws Exception {
		FullTextEntityManager ftEm = Search.getFullTextEntityManager(getCurrentSession());
		QueryBuilder qb = ftEm.getSearchFactory().buildQueryBuilder().forEntity(persistentClass).get();

		BooleanJunction<BooleanJunction> bool = qb.bool();
		for (String field : fields) {
			bool.should(qb.keyword().wildcard().onField(field).matching("*" + searchTerm + "*").createQuery());
		}

		FullTextQuery ftQuery = ftEm.createFullTextQuery(bool.createQuery(), persistentClass);
		return ftQuery.getResultList();
	}

	@Override
	public List<T> findAsOfDate(Instant timestamp, Specification<T> spec) throws Exception {
		// For SQL Server/Oracle temporal tables
		String sql = "SELECT * FROM " + persistentClass.getSimpleName() + " FOR SYSTEM_TIME AS OF :timestamp";

		Query<T> query = getCurrentSession().createNativeQuery(sql, persistentClass).setParameter("timestamp",
				timestamp);

		return query.getResultList();
	}

	@Override
	public List<T> findHierarchy(Id rootId, int depth) throws Exception {
		String sql = "WITH RECURSIVE hierarchy AS (" + "  SELECT * FROM " + persistentClass.getSimpleName()
				+ " WHERE id = :rootId " + "  UNION ALL " + "  SELECT e.* FROM " + persistentClass.getSimpleName()
				+ " e " + "  JOIN hierarchy h ON e.parent_id = h.id " + "  WHERE h.depth < :depth"
				+ ") SELECT * FROM hierarchy";

		return getCurrentSession().createNativeQuery(sql, persistentClass).setParameter("rootId", rootId)
				.setParameter("depth", depth).getResultList();
	}

	@Override
	public List<T> findByJsonPath(String jsonPath, String value) throws Exception {
		return getCurrentSession()
				.createNativeQuery("SELECT * FROM " + persistentClass.getSimpleName()
						+ " WHERE jsonb_path_exists(data::jsonb, :jsonPath, :value)", persistentClass)
				.setParameter("jsonPath", "$." + jsonPath).setParameter("value", value).getResultList();
	}

	@Override
	public List<T> findWithinRadius(double lat, double lng, double radiusKm) throws Exception {
		// Requires Hibernate Spatial
		Point center = GeometryUtils.createPoint(lat, lng);
		return getCurrentSession()
				.createQuery("FROM " + persistentClass.getSimpleName() + " WHERE within(location, :circle) = true",
						persistentClass)
				.setParameter("circle", GeometryUtils.createCircle(center, radiusKm)).getResultList();
	}

}