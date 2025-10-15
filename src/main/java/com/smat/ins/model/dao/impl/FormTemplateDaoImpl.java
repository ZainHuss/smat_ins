package com.smat.ins.model.dao.impl;

import java.util.List;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Root;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;

import com.generic.model.dao.impl.GenericDaoImpl;
import com.smat.ins.model.dao.FormTemplateDao;
import com.smat.ins.model.entity.EquipmentCategory;
import com.smat.ins.model.entity.EquipmentInspectionForm;
import com.smat.ins.model.entity.FormTemplate;

public class FormTemplateDaoImpl extends GenericDaoImpl<FormTemplate, Integer> implements FormTemplateDao {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6688855515663018679L;

	@Override
	public Integer getMaxFormCode() {
		// TODO Auto-generated method stub
		Session session = null;
		Integer maxFormCode = null;
		try {
			session = sessionFactory.getCurrentSession();
			return Integer.parseInt((String) session
					.createNativeQuery(
							"SELECT COALESCE(MAX(SUBSTR(code,-3)),0) as Max_Template_Code from form_template".toLowerCase())
					.addScalar("Max_Template_Code").uniqueResult());

		} catch (HibernateException e) {
			e.printStackTrace();
			return maxFormCode;
		}
	}

	@Override
	public FormTemplate getBy(Integer formId) {
		// TODO Auto-generated method stub
		Session session = null;
		FormTemplate formTemplate = null;
		try {
			session = sessionFactory.getCurrentSession();
			CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
			CriteriaQuery<FormTemplate> criteriaQuery = criteriaBuilder.createQuery(FormTemplate.class);
			Root<FormTemplate> root = criteriaQuery.from(FormTemplate.class);
			root.fetch("equipmentCategory", JoinType.INNER);
			criteriaQuery.select(root);
			if (formId != null)
				criteriaQuery.where(criteriaBuilder.equal(root.get("id"), formId));

			TypedQuery<FormTemplate> typedQuery = session.createQuery(criteriaQuery);
			formTemplate = typedQuery.getSingleResult();
			return formTemplate;

		} catch (Exception e) {
			log.error(persistentClass + " can't be fetched from DB because of the following Exception ");
			e.printStackTrace();
			return formTemplate;
		}
	}

	@Override
	public FormTemplate getBy(String equipmentCatCode) {
		// TODO Auto-generated method stub
		Session session = null;
		FormTemplate formTemplate = null;
		try {
			session = sessionFactory.getCurrentSession();
			CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
			CriteriaQuery<FormTemplate> criteriaQuery = criteriaBuilder.createQuery(FormTemplate.class);
			Root<FormTemplate> root = criteriaQuery.from(FormTemplate.class);
			root.fetch("equipmentCategory", JoinType.INNER);
			criteriaQuery.select(root);
			if (equipmentCatCode != null && !equipmentCatCode.isEmpty())
				criteriaQuery.where(criteriaBuilder.equal(root.get("equipmentCategory").get("code"), equipmentCatCode));

			TypedQuery<FormTemplate> typedQuery = session.createQuery(criteriaQuery);
			formTemplate = typedQuery.getSingleResult();
			return formTemplate;

		} catch (Exception e) {
			log.error(persistentClass + " can't be fetched from DB because of the following Exception ");
			e.printStackTrace();
			return formTemplate;
		}
	}

	@Override
	public FormTemplate getByTemplateName(String templateName) {
		// TODO Auto-generated method stub
		Session session = null;
		FormTemplate formTemplate = null;
		try {
			session = sessionFactory.getCurrentSession();
			CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
			CriteriaQuery<FormTemplate> criteriaQuery = criteriaBuilder.createQuery(FormTemplate.class);
			Root<FormTemplate> root = criteriaQuery.from(FormTemplate.class);
			root.fetch("equipmentCategory", JoinType.INNER);
			criteriaQuery.select(root);
			if (templateName != null && !templateName.isEmpty())
				criteriaQuery.where(criteriaBuilder.equal(root.get("title"), templateName));

			TypedQuery<FormTemplate> typedQuery = session.createQuery(criteriaQuery);
			formTemplate = typedQuery.getSingleResult();
			return formTemplate;

		} catch (Exception e) {
			log.error(persistentClass + " can't be fetched from DB because of the following Exception ");
			e.printStackTrace();
			return formTemplate;
		}
	}

	@Override
	public Boolean checkIfUsed(Integer formId) {
		// TODO Auto-generated method stub
		Session session = null;
		try {
			session = sessionFactory.getCurrentSession();
			CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
			CriteriaQuery<EquipmentInspectionForm> criteriaQuery = criteriaBuilder
					.createQuery(EquipmentInspectionForm.class);
			Root<EquipmentInspectionForm> root = criteriaQuery.from(EquipmentInspectionForm.class);
			Join<EquipmentInspectionForm, EquipmentCategory> joinEquipmentCat = root.join("equipmentCategory",
					JoinType.INNER);
			Join<EquipmentCategory, FormTemplate> joinForTemplate = joinEquipmentCat.join("formTemplates",
					JoinType.INNER);

			criteriaQuery.select(root);
			if (formId != null)
				criteriaQuery.where(criteriaBuilder.equal(joinForTemplate.get("id"), formId));

			TypedQuery<EquipmentInspectionForm> typedQuery = session.createQuery(criteriaQuery);
			List<EquipmentInspectionForm> equipmentInspectionForms = typedQuery.getResultList();
			if (equipmentInspectionForms != null && !equipmentInspectionForms.isEmpty())
				return true;
			else
				return false;

		} catch (Exception e) {
			log.error(persistentClass + " can't be fetched from DB because of the following Exception ");
			e.printStackTrace();
			return true;
		}
	}

	@Override
	public List<FormTemplate> getWithAllRelated() {
		// TODO Auto-generated method stub
		Session session = null;
	    List<FormTemplate> formTemplates = null;
	    try {
	        session = sessionFactory.getCurrentSession();
	        String hql = "SELECT DISTINCT ft FROM FormTemplate ft "
	                   + "LEFT JOIN FETCH ft.printedDoc "
	                   + "LEFT JOIN FETCH ft.equipmentCategory "
	                   + "LEFT JOIN FETCH ft.sysUser "
	                   + "LEFT JOIN FETCH ft.formRows ";
	        TypedQuery<FormTemplate> query = session.createQuery(hql, FormTemplate.class);
	        formTemplates = query.getResultList();
	    } catch (Exception e) {
	        log.error("Error fetching FormTemplate with all related entities", e);
	        // Handle exception or return null
	    }
	    return formTemplates;
	}

}
