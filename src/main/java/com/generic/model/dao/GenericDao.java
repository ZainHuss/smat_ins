package com.generic.model.dao;

import java.io.Serializable;
import java.time.Instant;


import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import javax.persistence.Tuple;


import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

import com.generic.util.DbUtilityHelper.ComparisonOperator;
import com.generic.util.DbUtilityHelper.ConditionGroup;
import com.generic.util.DbUtilityHelper.CriteriaBuilderHelper;
import com.generic.util.DbUtilityHelper.FetchStrategy;
import com.generic.util.DbUtilityHelper.GraphQuery;
import com.generic.util.DbUtilityHelper.LogicalOperator;
import com.generic.util.DbUtilityHelper.Operator;

public interface GenericDao<T extends Serializable, Id extends Serializable> extends Serializable {

	// Basic CRUD operations
	T insertOrupdate(T t) throws Exception;

	T insert(T t) throws Exception;

	List<T> insert(List<T> t) throws Exception;

	boolean update(T t) throws Exception;

	boolean update(List<T> t) throws Exception;

	boolean delete(T t) throws Exception;

	boolean delete(List<T> t) throws Exception;

	boolean merge(T t) throws Exception;

	// Query operations
	T getById(Id id) throws Exception;

	T load(Id id) throws Exception;

	T getByUniqueField(String propertyName, Object propertyValue) throws Exception;

	List<T> getAll() throws Exception;

	Long getCountAll() throws Exception;

	// Pagination
	List<T> getPages(Integer start, Integer pageSize) throws Exception;

	List<T> getPagesByFilter(Integer start, Integer pageSize, String propertyName, Object propertyValue)
			throws Exception;

	// Filtered queries
	List<T> getAllByFilter(String propertyName, Object propertyValue) throws Exception;

	Long getCountAllByFilter(String propertyName, Object propertyValue) throws Exception;
	
	// Advanced Query Methods
	List<T> findByConditions(ConditionGroup conditionGroup);
	List<T> findByConditions(ConditionGroup conditionGroup, Integer start, Integer pageSize);
	List<T> findByConditions(ConditionGroup conditionGroup, Integer start, Integer pageSize, Sort sort);
	<R> List<R> findByConditionsWithProjection(ConditionGroup conditionGroup, Class<R> resultType, List<String> properties);
	List<Tuple> findByConditionsReturningTuples(ConditionGroup conditionGroup, List<String> properties);
	List<T> findByConditionsWithFetch(ConditionGroup conditionGroup, String... fetchPaths);
	// Enhanced methods
	
	
	
	
	List<T> findByCriteria(Map<String, Object> propertyMap, Map<String, Operator> operators, Integer start,
			Integer pageSize, LogicalOperator logicalOperator) throws Exception;

	List<T> findByCriteria(Map<String, Object> propertyMap, Integer start, Integer pageSize, LogicalOperator operator)
			throws Exception;

	List<T> findByCriteria(Map<String, Object> propertyMap, Integer start, Integer pageSize,
			LogicalOperator defaultOperator, Map<String, LogicalOperator> customOperators) throws Exception;
	
	List<T> findByCriteria(Map<String, Object> propertyMap, Map<String, Operator> operators, Integer start, Integer pageSize,
			LogicalOperator defaultOperator, Map<String, LogicalOperator> customOperators) throws Exception;
	Long countByCriteria(Map<String, Object> propertyMap, Map<String, Operator> operators,
            LogicalOperator defaultOperator, Map<String, LogicalOperator> customOperators) throws Exception;

	List<T> findByCriteria(Map<String, Object> criteria, int first, int pageSize, String sortField, boolean ascending)
			throws Exception;

	Long countByCriteria(Map<String, Object> criteria) throws Exception;

	List<T> findByCriteria(Map<String, Object> propertyMap) throws Exception;

	List<T> findByCriteria(Map<String, Object> propertyMap, Integer start, Integer pageSize) throws Exception;

	List<T> findByLike(String propertyName, String value) throws Exception;

	List<T> findByRange(String propertyName, Comparable fromValue, Comparable toValue) throws Exception;

	List<T> findIn(String propertyName, List<?> values) throws Exception;

	List<T> getPagesWithSorting(Integer start, Integer pageSize, String sortProperty, boolean ascending)
			throws Exception;

	List<T> findAdvanced(Integer start, Integer pageSize, String sortProperty, Boolean ascending,
			Map<String, Object> filters) throws Exception;

	// Child/parent relationship methods
	T findByIdWithChildren(Id id, String... childRelations) throws Exception;
	T findByIdWithAllRelations(Id id) throws Exception;

	<C> List<T> findByChildCriteria(Class<C> childEntityClass, String childProperty, Object childValue,
			String... fetchRelations) throws Exception;

	<C> List<T> findByChildCollection(String childCollectionProperty, Consumer<CriteriaBuilderHelper<C>> childCriteria,
			FetchStrategy fetchStrategy) throws Exception;

	<C> T updateChildCollection(Id parentId, String childCollectionProperty, List<C> childrenToAdd,
			List<C> childrenToRemove, BiConsumer<C, T> childParentSetter) throws Exception;

	List<T> findByChildCount(String childCollectionProperty, ComparisonOperator comparisonOperator, long count)
			throws Exception;

	<C> List<T> findByChildExists(String childCollectionProperty, Consumer<CriteriaBuilderHelper<C>> childCriteria)
			throws Exception;

	T findWithHierarchy(Id id, int maxDepth) throws Exception;

	// Bulk operations
	int bulkUpdate(Map<String, Object> updateProperties, Map<String, Object> criteriaProperties) throws Exception;

	int bulkDelete(Map<String, Object> criteriaProperties) throws Exception;

	List<T> batchInsert(List<T> entities, Integer batchSize) throws Exception;

	// ===== Batch Fetching Enhancements =====
	List<T> findBatchWithChildren(List<Id> ids, String... childRelations) throws Exception;

	T findByIdWithCustomFetch(Id id, List<String> fetchAttributes) throws Exception;

	List<T> getAllWithFetchParent(String... parentRelations) throws Exception;

	// Utility operations
	boolean exists(String propertyName, Object propertyValue) throws Exception;

	T deepCopy(T entity, boolean copyChildren, String... childProperties) throws Exception;

	void refresh(T entity) throws Exception;

	void flush() throws Exception;

	void clear() throws Exception;

	// Custom query execution
	List<T> executeHQLQuery(String hql, Map<String, Object> params) throws Exception;

	List<T> executeNativeQuery(String sql, Map<String, Object> params) throws Exception;

	// ========== ADVANCED QUERY METHODS ==========

	// 1. Dynamic Specification Query
	List<T> findBySpecification(Specification<T> spec, Pageable pageable) throws Exception;

	// 2. GraphQL-Style Flexible Query
	List<T> findByGraphQuery(GraphQuery<T> graphQuery) throws Exception;

	// 3. Full-Text Search
	List<T> fullTextSearch(String searchTerm, String... fields) throws Exception;

	// 4. Temporal Query
	List<T> findAsOfDate(Instant timestamp, Specification<T> spec) throws Exception;

	// 5. Recursive CTE Query
	List<T> findHierarchy(Id rootId, int depth) throws Exception;

	// 6. JSON Path Query
	List<T> findByJsonPath(String jsonPath, String value) throws Exception;

	// 7. Spatial Query
	List<T> findWithinRadius(double lat, double lng, double radiusKm) throws Exception;

	// ========== SUPPORTING CLASSES ==========

	

	

}