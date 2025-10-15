package com.smat.ins.model.dao.impl;

import java.util.List;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Root;

import org.hibernate.HibernateException;
import org.hibernate.Session;

import com.generic.model.dao.impl.GenericDaoImpl;
import com.smat.ins.model.dao.InspectionFormWorkflowStepDao;
import com.smat.ins.model.entity.EquipmentInspectionForm;

import com.smat.ins.model.entity.InspectionFormWorkflowStep;

public class InspectionFormWorkflowStepDaoImpl extends GenericDaoImpl<InspectionFormWorkflowStep, Long>
		implements InspectionFormWorkflowStepDao {

	@Override
	public List<InspectionFormWorkflowStep> getByInspectionForm(Long inspectionFormId) {
		// TODO Auto-generated method stub
		Session session = null;
		List<InspectionFormWorkflowStep> inspectionFormWorkflowSteps = null;
		try {
			session = sessionFactory.getCurrentSession();
			CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
			CriteriaQuery<InspectionFormWorkflowStep> criteriaQuery = criteriaBuilder
					.createQuery(InspectionFormWorkflowStep.class);
			Root<InspectionFormWorkflowStep> root = criteriaQuery.from(InspectionFormWorkflowStep.class);
			Join<InspectionFormWorkflowStep, EquipmentInspectionForm> joinEquipmentInspectionForm = root
					.join("equipmentInspectionForm");

			criteriaQuery.select(root);
			criteriaQuery.where(criteriaBuilder.equal(joinEquipmentInspectionForm.get("id"), inspectionFormId));
			TypedQuery<InspectionFormWorkflowStep> typedQuery = session.createQuery(criteriaQuery);
			inspectionFormWorkflowSteps = typedQuery.getResultList();
			return inspectionFormWorkflowSteps;
		} catch (Exception e) {
			log.error(persistentClass + " can't be fetched from DB because of the following Exception ");
			e.printStackTrace();
			return inspectionFormWorkflowSteps;
		}
	}

	@Override
	public List<InspectionFormWorkflowStep> getByInspectionFormAndStepCode(Long inspectionFormId, String stepCode) {
		// TODO Auto-generated method stub
		Session session = null;
		List<InspectionFormWorkflowStep> inspectionFormWorkflowSteps = null;
		try {
			session = sessionFactory.getCurrentSession();
			CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
			CriteriaQuery<InspectionFormWorkflowStep> criteriaQuery = criteriaBuilder
					.createQuery(InspectionFormWorkflowStep.class);
			Root<InspectionFormWorkflowStep> root = criteriaQuery.from(InspectionFormWorkflowStep.class);
			Join<InspectionFormWorkflowStep, EquipmentInspectionForm> joinEquipmentInspectionForm = root
					.join("equipmentInspectionForm");

			criteriaQuery.select(root);
			criteriaQuery.where(criteriaBuilder.equal(joinEquipmentInspectionForm.get("id"), inspectionFormId),
					criteriaBuilder.equal(root.get("workflowDefinition").get("step").get("code"), stepCode));
			TypedQuery<InspectionFormWorkflowStep> typedQuery = session.createQuery(criteriaQuery);
			inspectionFormWorkflowSteps = typedQuery.getResultList();
			return inspectionFormWorkflowSteps;
		} catch (Exception e) {
			log.error(persistentClass + " can't be fetched from DB because of the following Exception ");
			e.printStackTrace();
			return inspectionFormWorkflowSteps;
		}
	}

	@Override
	public Short getLastStepSeq(Long inspectionFormId) {
		// TODO Auto-generated method stub
		Session session = null;
		Short maxId = null;

		try {
			session = sessionFactory.getCurrentSession();
           if(inspectionFormId == null) {
			maxId = (Short) session.createNativeQuery(
					"SELECT max(ifws.step_seq) AS max_step_seq  FROM inspection_form_workflow_step ifws WHERE ifws.equipment_inspection_form is null".toLowerCase())
					.addScalar("max_step_seq").uniqueResult();
           }else {
        	   maxId = (Short) session.createNativeQuery(
   					"SELECT max(ifws.step_seq) AS max_step_seq  FROM inspection_form_workflow_step ifws WHERE ifws.equipment_inspection_form=?".toLowerCase())
   					.setParameter(1, inspectionFormId).addScalar("max_step_seq").uniqueResult(); 
           }
			if (maxId == null)
				return 0;
			else
				return maxId;
		} catch (HibernateException e) {
			e.printStackTrace();
			return maxId;
		}
	}

	@Override
	public InspectionFormWorkflowStep getLastStep(Long inspectionFormId) {
		// TODO Auto-generated method stub
		Session session = null;
		InspectionFormWorkflowStep inspectionFormWorkflowStep = null;
		try {
			session = sessionFactory.getCurrentSession();
			CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
			CriteriaQuery<InspectionFormWorkflowStep> criteriaQuery = criteriaBuilder
					.createQuery(InspectionFormWorkflowStep.class);
			Root<InspectionFormWorkflowStep> root = criteriaQuery.from(InspectionFormWorkflowStep.class);
			Join<InspectionFormWorkflowStep, EquipmentInspectionForm> joinEquipmentInspectionForm = root
					.join("equipmentInspectionForm");

			criteriaQuery.select(root);
			criteriaQuery.where(criteriaBuilder.equal(joinEquipmentInspectionForm.get("id"), inspectionFormId));
			criteriaQuery.orderBy(criteriaBuilder.desc(root.get("stepSeq")));
			TypedQuery<InspectionFormWorkflowStep> typedQuery = session.createQuery(criteriaQuery);
			typedQuery.setFirstResult(0);
			typedQuery.setMaxResults(1);
			List<InspectionFormWorkflowStep> inspectionFormWorkflowSteps = typedQuery.getResultList();

			if (inspectionFormWorkflowSteps != null && !inspectionFormWorkflowSteps.isEmpty())
				inspectionFormWorkflowStep = inspectionFormWorkflowSteps.get(0);
			else
				inspectionFormWorkflowStep = null;
			return inspectionFormWorkflowStep;
		} catch (Exception e) {
			log.error(persistentClass + " can't be fetched from DB because of the following Exception ");
			e.printStackTrace();
			return inspectionFormWorkflowStep;
		}
	}

}
