package com.generic.model.service;

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

public interface GenericService<Entity extends Serializable, Id extends Serializable> extends Serializable {

	// Basic CRUD operations
	Entity saveOrUpdate(Entity entity) throws Exception;

	Entity save(Entity entity) throws Exception;

	List<Entity> saveAll(List<Entity> entities) throws Exception;

	boolean update(Entity entity) throws Exception;

	boolean update(List<Entity> entities) throws Exception;

	boolean delete(Entity entity) throws Exception;

	boolean delete(List<Entity> entities) throws Exception;

	boolean merge(Entity entity) throws Exception;

	// Query operations
	Entity findById(Id id) throws Exception;

	Entity load(Id id) throws Exception;

	Entity findByUniqueField(String propertyName, Object propertyValue) throws Exception;

	List<Entity> findAll() throws Exception;

	Long countAll() throws Exception;

	// Pagination
	List<Entity> findPaginated(Integer start, Integer pageSize) throws Exception;

	List<Entity> findPaginatedByFilter(Integer start, Integer pageSize, String propertyName, Object propertyValue)
			throws Exception;

	// Filtered queries
	List<Entity> findAllByFilter(String propertyName, Object propertyValue) throws Exception;

	Long countAllByFilter(String propertyName, Object propertyValue) throws Exception;

	// Advanced Query Methods
	List<Entity> findByConditions(ConditionGroup conditionGroup);

	List<Entity> findByConditions(ConditionGroup conditionGroup, Integer start, Integer pageSize);

	List<Entity> findByConditions(ConditionGroup conditionGroup, Integer start, Integer pageSize, Sort sort);

	<R> List<R> findByConditionsWithProjection(ConditionGroup conditionGroup, Class<R> resultType,
			List<String> properties);

	List<Tuple> findByConditionsReturningTuples(ConditionGroup conditionGroup, List<String> properties);

	List<Entity> findByConditionsWithFetch(ConditionGroup conditionGroup, String... fetchPaths);
	// Enhanced methods

	List<Entity> findByCriteria(Map<String, Object> propertyMap, Map<String, Operator> operators, Integer start,
			Integer pageSize, LogicalOperator logicalOperator) throws Exception;

	List<Entity> findByCriteria(Map<String, Object> propertyMap, Integer start, Integer pageSize,
			LogicalOperator operator) throws Exception;

	List<Entity> findByCriteria(Map<String, Object> propertyMap, Integer start, Integer pageSize,
			LogicalOperator defaultOperator, Map<String, LogicalOperator> customOperators) throws Exception;

	List<Entity> findByCriteria(Map<String, Object> propertyMap, Map<String, Operator> operators, Integer start,
			Integer pageSize, LogicalOperator defaultOperator, Map<String, LogicalOperator> customOperators)
			throws Exception;
	
	Long countByCriteria(Map<String, Object> propertyMap, Map<String, Operator> operators,
            LogicalOperator defaultOperator, Map<String, LogicalOperator> customOperators) throws Exception;

	List<Entity> findByCriteria(Map<String, Object> criteria, int first, int pageSize, String sortField,
			boolean ascending) throws Exception;

	Long countByCriteria(Map<String, Object> criteria) throws Exception;

	List<Entity> findByCriteria(Map<String, Object> propertyMap) throws Exception;

	List<Entity> findByCriteria(Map<String, Object> propertyMap, Integer start, Integer pageSize) throws Exception;

	List<Entity> findByLike(String propertyName, String value) throws Exception;

	List<Entity> findByRange(String propertyName, Comparable fromValue, Comparable toValue) throws Exception;

	List<Entity> findIn(String propertyName, List<?> values) throws Exception;

	List<Entity> findPaginatedWithSorting(Integer start, Integer pageSize, String sortProperty, boolean ascending)
			throws Exception;

	List<Entity> findAdvanced(Integer start, Integer pageSize, String sortProperty, Boolean ascending,
			Map<String, Object> filters) throws Exception;

	// Relationship methods
	Entity findByIdWithChildren(Id id, String... childRelations) throws Exception;

	<C> List<Entity> findByChildCriteria(Class<C> childEntityClass, String childProperty, Object childValue,
			String... fetchRelations) throws Exception;

	<C> List<Entity> findByChildCollection(String childCollectionProperty,
			Consumer<CriteriaBuilderHelper<C>> childCriteria, FetchStrategy fetchStrategy) throws Exception;

	<C> Entity updateChildCollection(Id parentId, String childCollectionProperty, List<C> childrenToAdd,
			List<C> childrenToRemove, BiConsumer<C, Entity> childParentSetter) throws Exception;

	List<Entity> findByChildCount(String childCollectionProperty, ComparisonOperator comparisonOperator, long count)
			throws Exception;

	<C> List<Entity> findByChildExists(String childCollectionProperty, Consumer<CriteriaBuilderHelper<C>> childCriteria)
			throws Exception;

	Entity findWithHierarchy(Id id, int maxDepth) throws Exception;

	// Child/parent relationship methods
	List<Entity> findBatchWithChildren(List<Id> ids, String... childRelations) throws Exception;

	Entity findByIdWithAllRelations(Id id) throws Exception;

	Entity findByIdWithCustomFetch(Id id, List<String> fetchAttributes) throws Exception;

	List<Entity> getAllWithFetchParent(String... parentRelations) throws Exception;

	// Batch operations
	int bulkUpdate(Map<String, Object> updateProperties, Map<String, Object> criteriaProperties) throws Exception;

	int bulkDelete(Map<String, Object> criteriaProperties) throws Exception;

	List<Entity> batchInsert(List<Entity> entities, Integer batchSize) throws Exception;

	// Utility operations
	boolean exists(String propertyName, Object propertyValue) throws Exception;

	Entity deepCopy(Entity entity, boolean copyChildren, String... childProperties) throws Exception;

	void refresh(Entity entity) throws Exception;

	void flush() throws Exception;

	void clear() throws Exception;

	// Custom query execution
	List<Entity> executeHQLQuery(String hql, Map<String, Object> params) throws Exception;

	List<Entity> executeNativeQuery(String sql, Map<String, Object> params) throws Exception;

// ========== ADVANCED QUERY METHODS ==========

	// 1. Dynamic Specification Query
	List<Entity> findBySpecification(Specification<Entity> spec, Pageable pageable) throws Exception;

	// 2. GraphQL-Style Flexible Query
	List<Entity> findByGraphQuery(GraphQuery<Entity> graphQuery) throws Exception;

	// 3. Full-Text Search
	List<Entity> fullTextSearch(String searchTerm, String... fields) throws Exception;

	// 4. Temporal Query
	List<Entity> findAsOfDate(Instant timestamp, Specification<Entity> spec) throws Exception;

	// 5. Recursive CTE Query
	List<Entity> findHierarchy(Id rootId, int depth) throws Exception;

	// 6. JSON Path Query
	List<Entity> findByJsonPath(String jsonPath, String value) throws Exception;

	// 7. Spatial Query
	List<Entity> findWithinRadius(double lat, double lng, double radiusKm) throws Exception;

	// ========== UTILITY METHODS ==========

	Specification<Entity> createSpecification(Map<String, Object> filters);

	/**
	 * تحديث حالة المهمة (جعلها فعالة أو غير فعالة)
	 * @param taskId معرّف المهمة
	 * @param status الحالة الجديدة (1 = فعالة، 0 = غير فعالة)
	 */
	// GenericService.java - REMOVE this method
	
}