package com.generic.model.service.impl;

import com.generic.model.dao.GenericDao;

import com.generic.model.service.GenericService;
import com.generic.util.DbUtilityHelper.ComparisonOperator;
import com.generic.util.DbUtilityHelper.ConditionGroup;
import com.generic.util.DbUtilityHelper.CriteriaBuilderHelper;
import com.generic.util.DbUtilityHelper.FetchStrategy;
import com.generic.util.DbUtilityHelper.GraphQuery;
import com.generic.util.DbUtilityHelper.LogicalOperator;
import com.generic.util.DbUtilityHelper.Operator;

import java.io.Serializable;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import javax.persistence.Tuple;
import javax.persistence.criteria.Predicate;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

public class GenericServiceImpl<Entity extends Serializable,Dao extends Serializable, Id extends Serializable> implements GenericService<Entity, Id> {

    /**
	 * 
	 */
	private static final long serialVersionUID = 3168671635360975210L;
    private static final Logger log = LoggerFactory.getLogger(GenericServiceImpl.class);


    protected Dao dao;
	
	

	public Dao getDao() {
		return dao;
	}

	public void setDao(Dao dao) {
		this.dao = dao;
	}
	

    // Basic CRUD operations
    @SuppressWarnings("unchecked")
	@Override
    public Entity saveOrUpdate(Entity entity) throws Exception {
        return ((GenericDao<Entity, Id>)dao).insertOrupdate(entity);
    }

    @SuppressWarnings("unchecked")
	@Override
    public Entity save(Entity entity) throws Exception {
        return ((GenericDao<Entity, Id>)dao).insert(entity);
    }

    @SuppressWarnings("unchecked")
	@Override
    public List<Entity> saveAll(List<Entity> entities) throws Exception {
        return ((GenericDao<Entity, Id>)dao).insert(entities);
    }

    @SuppressWarnings("unchecked")
	@Override
    public boolean update(Entity entity) throws Exception {
        return ((GenericDao<Entity, Id>)dao).update(entity);
    }

    @SuppressWarnings("unchecked")
	@Override
    public boolean update(List<Entity> entities) throws Exception {
        return ((GenericDao<Entity, Id>)dao).update(entities);
    }

    @SuppressWarnings("unchecked")
	@Override
    public boolean delete(Entity entity) throws Exception {
        return ((GenericDao<Entity, Id>)dao).delete(entity);
    }

    @SuppressWarnings("unchecked")
	@Override
    public boolean delete(List<Entity> entities) throws Exception {
        return ((GenericDao<Entity, Id>)dao).delete(entities);
    }

    @SuppressWarnings("unchecked")
	@Override
    public boolean merge(Entity entity) throws Exception {
        return ((GenericDao<Entity, Id>)dao).merge(entity);
    }

    @SuppressWarnings("unchecked")
    @Override
    public Entity findById(Id id) throws Exception {
        if (id == null) {
            log.warn("findById called with null id for service: " + this.getClass().getName());
            return null;
        }
        return ((GenericDao<Entity, Id>)dao).getById(id);
    }



    @SuppressWarnings("unchecked")
	@Override
    public Entity load(Id id) throws Exception {
        return ((GenericDao<Entity, Id>)dao).load(id);
    }

    @SuppressWarnings("unchecked")
	@Override
    public Entity findByUniqueField(String propertyName, Object propertyValue) throws Exception {
        return ((GenericDao<Entity, Id>)dao).getByUniqueField(propertyName, propertyValue);
    }

    @SuppressWarnings("unchecked")
	@Override
    public List<Entity> findAll() throws Exception {
        return ((GenericDao<Entity, Id>)dao).getAll();
    }

    @SuppressWarnings("unchecked")
	@Override
    public Long countAll() throws Exception {
        return ((GenericDao<Entity, Id>)dao).getCountAll();
    }

    // Pagination
    @SuppressWarnings("unchecked")
	@Override
    public List<Entity> findPaginated(Integer start, Integer pageSize) throws Exception {
        return ((GenericDao<Entity, Id>)dao).getPages(start, pageSize);
    }

    @SuppressWarnings("unchecked")
	@Override
    public List<Entity> findPaginatedByFilter(Integer start, Integer pageSize, String propertyName, Object propertyValue) throws Exception {
        return ((GenericDao<Entity, Id>)dao).getPagesByFilter(start, pageSize, propertyName, propertyValue);
    }

    // Filtered queries
    @SuppressWarnings("unchecked")
	@Override
    public List<Entity> findAllByFilter(String propertyName, Object propertyValue) throws Exception {
        return ((GenericDao<Entity, Id>)dao).getAllByFilter(propertyName, propertyValue);
    }

    @SuppressWarnings("unchecked")
	@Override
    public Long countAllByFilter(String propertyName, Object propertyValue) throws Exception {
        return ((GenericDao<Entity, Id>)dao).getCountAllByFilter(propertyName, propertyValue);
    }
    
    //  Advanced Query Methods
    @SuppressWarnings("unchecked")
	@Override
	public List<Entity> findByConditions(ConditionGroup conditionGroup) {
		// TODO Auto-generated method stub
		 return ((GenericDao<Entity, Id>)dao).findByConditions(conditionGroup);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Entity> findByConditions(ConditionGroup conditionGroup, Integer start, Integer pageSize) {
		// TODO Auto-generated method stub
		return ((GenericDao<Entity, Id>)dao).findByConditions(conditionGroup, start, pageSize);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Entity> findByConditions(ConditionGroup conditionGroup, Integer start, Integer pageSize, Sort sort) {
		// TODO Auto-generated method stub
		return ((GenericDao<Entity, Id>)dao).findByConditions(conditionGroup, start, pageSize, sort);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <R> List<R> findByConditionsWithProjection(ConditionGroup conditionGroup, Class<R> resultType,
			List<String> properties) {
		// TODO Auto-generated method stub
		return ((GenericDao<Entity, Id>)dao).findByConditionsWithProjection(conditionGroup, resultType, properties);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Tuple> findByConditionsReturningTuples(ConditionGroup conditionGroup, List<String> properties) {
		// TODO Auto-generated method stub
		return ((GenericDao<Entity, Id>)dao).findByConditionsReturningTuples(conditionGroup, properties);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Entity> findByConditionsWithFetch(ConditionGroup conditionGroup, String... fetchPaths) {
		// TODO Auto-generated method stub
		return ((GenericDao<Entity, Id>)dao).findByConditionsWithFetch(conditionGroup, fetchPaths);
	}

    

    // Enhanced methods
    
    @SuppressWarnings("unchecked")
	@Override
	public List<Entity> findByCriteria(Map<String, Object> propertyMap, Map<String, Operator> operators, Integer start,
			Integer pageSize, LogicalOperator logicalOperator) throws Exception {
		// TODO Auto-generated method stub
		return ((GenericDao<Entity, Id>)dao).findByCriteria(propertyMap, operators, start, pageSize, logicalOperator);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Entity> findByCriteria(Map<String, Object> propertyMap, Integer start, Integer pageSize,
			LogicalOperator operator) throws Exception {
		// TODO Auto-generated method stub
		return ((GenericDao<Entity, Id>)dao).findByCriteria(propertyMap, start, pageSize, operator);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Entity> findByCriteria(Map<String, Object> propertyMap, Integer start, Integer pageSize,
			LogicalOperator defaultOperator, Map<String, LogicalOperator> customOperators) throws Exception {
		// TODO Auto-generated method stub
		return ((GenericDao<Entity, Id>)dao).findByCriteria(propertyMap, start, pageSize, defaultOperator, customOperators);
	}
	
	
    
    @SuppressWarnings("unchecked")
	@Override
	public List<Entity> findByCriteria(Map<String, Object> propertyMap, Map<String, Operator> operators, Integer start,
			Integer pageSize, LogicalOperator defaultOperator, Map<String, LogicalOperator> customOperators)
			throws Exception {
		// TODO Auto-generated method stub
		return ((GenericDao<Entity, Id>)dao).findByCriteria(propertyMap, operators, start, pageSize, defaultOperator, customOperators);
	}
    
    

	@SuppressWarnings("unchecked")
	@Override
	public Long countByCriteria(Map<String, Object> propertyMap, Map<String, Operator> operators,
			LogicalOperator defaultOperator, Map<String, LogicalOperator> customOperators) throws Exception {
		// TODO Auto-generated method stub
		return ((GenericDao<Entity, Id>)dao).countByCriteria(propertyMap, operators, defaultOperator, customOperators);
	}

	@SuppressWarnings("unchecked")
	@Override
  	public List<Entity> findByCriteria(Map<String, Object> criteria, int first, int pageSize, String sortField,
  			boolean ascending) throws Exception {
  		// TODO Auto-generated method stub
  		return ((GenericDao<Entity, Id>)dao).findByCriteria(criteria, first, pageSize, sortField, ascending);
  	}

	@SuppressWarnings("unchecked")
	@Override
  	public Long countByCriteria(Map<String, Object> criteria) throws Exception {
  		// TODO Auto-generated method stub
  		return ((GenericDao<Entity, Id>)dao).countByCriteria(criteria);
  	}
    
    @SuppressWarnings("unchecked")
	@Override
    public List<Entity> findByCriteria(Map<String, Object> propertyMap) throws Exception {
        return ((GenericDao<Entity, Id>)dao).findByCriteria(propertyMap);
    }

	@SuppressWarnings("unchecked")
	@Override
    public List<Entity> findByCriteria(Map<String, Object> propertyMap, Integer start, Integer pageSize) throws Exception {
        return ((GenericDao<Entity, Id>)dao).findByCriteria(propertyMap, start, pageSize);
    }

    @SuppressWarnings("unchecked")
	@Override
    public List<Entity> findByLike(String propertyName, String value) throws Exception {
        return ((GenericDao<Entity, Id>)dao).findByLike(propertyName, value);
    }

    @SuppressWarnings("unchecked")
	@Override
    public List<Entity> findByRange(String propertyName, Comparable fromValue, Comparable toValue) throws Exception {
        return ((GenericDao<Entity, Id>)dao).findByRange(propertyName, fromValue, toValue);
    }

    @SuppressWarnings("unchecked")
	@Override
    public List<Entity> findIn(String propertyName, List<?> values) throws Exception {
        return ((GenericDao<Entity, Id>)dao).findIn(propertyName, values);
    }

    @SuppressWarnings("unchecked")
	@Override
    public List<Entity> findPaginatedWithSorting(Integer start, Integer pageSize, String sortProperty, boolean ascending) throws Exception {
        return ((GenericDao<Entity, Id>)dao).getPagesWithSorting(start, pageSize, sortProperty, ascending);
    }

    @SuppressWarnings("unchecked")
	@Override
    public List<Entity> findAdvanced(Integer start, Integer pageSize, String sortProperty, Boolean ascending, Map<String, Object> filters) throws Exception {
        return ((GenericDao<Entity, Id>)dao).findAdvanced(start, pageSize, sortProperty, ascending, filters);
    }

    // Relationship methods
    @SuppressWarnings("unchecked")
	@Override
    public Entity findByIdWithChildren(Id id, String... childRelations) throws Exception {
        return ((GenericDao<Entity, Id>)dao).findByIdWithChildren(id, childRelations);
    }

    @SuppressWarnings("unchecked")
	@Override
	public Entity findByIdWithAllRelations(Id id) throws Exception {
		// TODO Auto-generated method stub
    	return ((GenericDao<Entity, Id>)dao).findByIdWithAllRelations(id);
	}

	@SuppressWarnings("unchecked")
	@Override
    public <C> List<Entity> findByChildCriteria(Class<C> childEntityClass, String childProperty, Object childValue, String... fetchRelations) throws Exception {
        return ((GenericDao<Entity, Id>)dao).findByChildCriteria(childEntityClass, childProperty, childValue, fetchRelations);
    }

    @SuppressWarnings("unchecked")
	@Override
    public <C> List<Entity> findByChildCollection(String childCollectionProperty, Consumer<CriteriaBuilderHelper<C>> childCriteria, 
                                           FetchStrategy fetchStrategy) throws Exception {
        return ((GenericDao<Entity, Id>)dao).findByChildCollection(childCollectionProperty, childCriteria, fetchStrategy);
    }

    @SuppressWarnings("unchecked")
	@Override
    public <C> Entity updateChildCollection(Id parentId, String childCollectionProperty, List<C> childrenToAdd, 
                                     List<C> childrenToRemove, BiConsumer<C, Entity> childParentSetter) throws Exception {
        return ((GenericDao<Entity, Id>)dao).updateChildCollection(parentId, childCollectionProperty, childrenToAdd, childrenToRemove, childParentSetter);
    }

    @SuppressWarnings("unchecked")
	@Override
    public List<Entity> findByChildCount(String childCollectionProperty, ComparisonOperator comparisonOperator, long count) throws Exception {
        return ((GenericDao<Entity, Id>)dao).findByChildCount(childCollectionProperty, comparisonOperator, count);
    }

    @SuppressWarnings("unchecked")
	@Override
    public <C> List<Entity> findByChildExists(String childCollectionProperty, Consumer<CriteriaBuilderHelper<C>> childCriteria) throws Exception {
        return ((GenericDao<Entity, Id>)dao).findByChildExists(childCollectionProperty, childCriteria);
    }

    @SuppressWarnings("unchecked")
	@Override
    public Entity findWithHierarchy(Id id, int maxDepth) throws Exception {
        return ((GenericDao<Entity, Id>)dao).findWithHierarchy(id, maxDepth);
    }

    // Batch operations
    @SuppressWarnings("unchecked")
	@Override
    public List<Entity> findBatchWithChildren(List<Id> ids, String... childRelations) throws Exception {
        return ((GenericDao<Entity, Id>)dao).findBatchWithChildren(ids, childRelations);
    }

    @SuppressWarnings("unchecked")
	@Override
    public Entity findByIdWithCustomFetch(Id id, List<String> fetchAttributes) throws Exception {
        return ((GenericDao<Entity, Id>)dao).findByIdWithCustomFetch(id, fetchAttributes);
    }
    
    @SuppressWarnings("unchecked")
	@Override
    public List<Entity> getAllWithFetchParent(String... parentRelations) throws Exception {
    	return ((GenericDao<Entity, Id>)dao).getAllWithFetchParent(parentRelations);
    }

    @SuppressWarnings("unchecked")
	@Override
    public int bulkUpdate(Map<String, Object> updateProperties, Map<String, Object> criteriaProperties) throws Exception {
        return ((GenericDao<Entity, Id>)dao).bulkUpdate(updateProperties, criteriaProperties);
    }

    @SuppressWarnings("unchecked")
	@Override
    public int bulkDelete(Map<String, Object> criteriaProperties) throws Exception {
        return ((GenericDao<Entity, Id>)dao).bulkDelete(criteriaProperties);
    }

    @SuppressWarnings("unchecked")
	@Override
    public List<Entity> batchInsert(List<Entity> entities, Integer batchSize) throws Exception {
        return ((GenericDao<Entity, Id>)dao).batchInsert(entities, batchSize);
    }

    // Utility operations
    @SuppressWarnings("unchecked")
	@Override
    public boolean exists(String propertyName, Object propertyValue) throws Exception {
        return ((GenericDao<Entity, Id>)dao).exists(propertyName, propertyValue);
    }

    @SuppressWarnings("unchecked")
	@Override
    public Entity deepCopy(Entity entity, boolean copyChildren, String... childProperties) throws Exception {
        return ((GenericDao<Entity, Id>)dao).deepCopy(entity, copyChildren, childProperties);
    }

    @SuppressWarnings("unchecked")
	@Override
    public void refresh(Entity entity) throws Exception {
        ((GenericDao<Entity, Id>)dao).refresh(entity);
    }

    @SuppressWarnings("unchecked")
	@Override
    public void flush() throws Exception {
        ((GenericDao<Entity, Id>)dao).flush();
    }

    @SuppressWarnings("unchecked")
	@Override
    public void clear() throws Exception {
        ((GenericDao<Entity, Id>)dao).clear();
    }

	@SuppressWarnings("unchecked")
	@Override
	public List<Entity> executeHQLQuery(String hql, Map<String, Object> params) throws Exception {
		// TODO Auto-generated method stub
		return ((GenericDao<Entity, Id>)dao).executeHQLQuery(hql, params);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Entity> executeNativeQuery(String sql, Map<String, Object> params) throws Exception {
		// TODO Auto-generated method stub
		return ((GenericDao<Entity, Id>)dao).executeNativeQuery(sql, params);
	}
	
	// ========== ADVANCED QUERY IMPLEMENTATIONS ==========

    @SuppressWarnings("unchecked")
	@Override
    public List<Entity> findBySpecification(Specification<Entity> spec, Pageable pageable) throws Exception {
        return ((GenericDao<Entity, Id>)dao).findBySpecification(spec, pageable);
    }

    @SuppressWarnings("unchecked")
	@Override
    public List<Entity> findByGraphQuery(GraphQuery<Entity> graphQuery) throws Exception {
        return ((GenericDao<Entity, Id>)dao).findByGraphQuery(graphQuery);
    }

    @SuppressWarnings("unchecked")
	@Override
    public List<Entity> fullTextSearch(String searchTerm, String... fields) throws Exception {
        return ((GenericDao<Entity, Id>)dao).fullTextSearch(searchTerm, fields);
    }

    @SuppressWarnings("unchecked")
	@Override
    public List<Entity> findAsOfDate(Instant timestamp, Specification<Entity> spec) throws Exception {
        return ((GenericDao<Entity, Id>)dao).findAsOfDate(timestamp, spec);
    }

    @SuppressWarnings("unchecked")
	@Override
    public List<Entity> findHierarchy(Id rootId, int depth) throws Exception {
        return ((GenericDao<Entity, Id>)dao).findHierarchy(rootId, depth);
    }

    @SuppressWarnings("unchecked")
	@Override
    public List<Entity> findByJsonPath(String jsonPath, String value) throws Exception {
        return ((GenericDao<Entity, Id>)dao).findByJsonPath(jsonPath, value);
    }

    @SuppressWarnings("unchecked")
	@Override
    public List<Entity> findWithinRadius(double lat, double lng, double radiusKm) throws Exception {
        return ((GenericDao<Entity, Id>)dao).findWithinRadius(lat, lng, radiusKm);
    }

    // ========== UTILITY METHODS ==========

    @Override
    public Specification<Entity> createSpecification(Map<String, Object> filters) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            filters.forEach((field, value) -> {
                if (value != null) {
                    if (value instanceof String) {
                        predicates.add(cb.like(root.get(field), "%" + value + "%"));
                    } else {
                        predicates.add(cb.equal(root.get(field), value));
                    }
                }
            });
            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}