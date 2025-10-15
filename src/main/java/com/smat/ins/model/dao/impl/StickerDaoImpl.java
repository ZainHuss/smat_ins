package com.smat.ins.model.dao.impl;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import com.generic.model.dao.impl.GenericDaoImpl;
import com.smat.ins.model.dao.StickerDao;
import com.smat.ins.model.entity.Sticker;
import java.util.List;
import javax.persistence.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StickerDaoImpl extends GenericDaoImpl<Sticker, Long> implements StickerDao {

    private static final Logger logger = LoggerFactory.getLogger(StickerDaoImpl.class);
    
    /**
     * 
     */
    private static final long serialVersionUID = 2439513234405937760L;

    @Override
    public Long getLastSeq() {
        // TODO Auto-generated method stub
        Session session = null;
        Long sequence = Long.valueOf(0);
        try {
            session = sessionFactory.getCurrentSession();
            CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
            CriteriaQuery<Long> criteriaQuery = criteriaBuilder.createQuery(Long.class);
            Root<Sticker> root = criteriaQuery.from(Sticker.class);
            criteriaQuery.select(criteriaBuilder.max(root.get("seq")));

            TypedQuery<Long> typedQuery = session.createQuery(criteriaQuery);
            sequence = typedQuery.getSingleResult();
            if (sequence == null)
                return Long.valueOf(1);
            else
                return sequence + 1;

        }

        catch (Exception e) {
            logger.error(persistentClass + " can't be persisted in DB because of the following Exception ");
            e.printStackTrace();
        }
        return sequence;
    }

    @Override
    public Integer getLastStickerNo() {
        // TODO Auto-generated method stub
        Session session = null;
        Integer maxStickerNoCode = null;

        try {
            session = sessionFactory.getCurrentSession();
            
                
                return Integer.parseInt( (String) session.createNativeQuery(
                        "SELECT COALESCE(MAX(SUBSTR(sticker_no,-5)),0) as Max_Sticker_Code from sticker".toLowerCase())
                        .addScalar("Max_Sticker_Code").uniqueResult());
            
                

        } catch (HibernateException e) {
            e.printStackTrace();
            return maxStickerNoCode;
        }
    }

    @Override
    public Integer getLastSerialID() {
        // TODO Auto-generated method stub
        Session session = null;
        Integer maxSerialNoCode = null;

        try {
            session = sessionFactory.getCurrentSession();
                return Integer.parseInt( (String) session.createNativeQuery(
                        "SELECT COALESCE(MAX(SUBSTR(serial_no,-4)),0) as Max_Serial_Code from sticker s".toLowerCase())
                        .addScalar("Max_Serial_Code").uniqueResult());
            
        } catch (HibernateException e) {
            e.printStackTrace();
            return maxSerialNoCode;
        }
    }

    @Override
    public List<Sticker> searchStickers(String searchText) {
        Session session = null;
        try {
            session = sessionFactory.getCurrentSession();
            
            // جرب هذا الاستعلام أولاً - بدون JOIN
            String queryStr = "SELECT s FROM Sticker s " +
                             "WHERE LOWER(s.sysUserByForUser.displayName) LIKE LOWER(:searchText) " +
                             "OR s.serialNo LIKE :searchText " +
                             "OR s.stickerNo LIKE :searchText";
            
            Query query = session.createQuery(queryStr);
            query.setParameter("searchText", "%" + searchText + "%");
            
            @SuppressWarnings("unchecked")
            List<Sticker> result = query.getResultList();
            return result;
            
        } catch (Exception e) {
            logger.error("Error searching stickers", e);
            
            // إذا فشل الاستعلام الأول، جرب استعلاماً بديلاً
            try {
                String queryStr = "SELECT s FROM Sticker s " +
                                 "WHERE s.serialNo LIKE :searchText " +
                                 "OR s.stickerNo LIKE :searchText";
                
                Query query = session.createQuery(queryStr);
                query.setParameter("searchText", "%" + searchText + "%");
                
                @SuppressWarnings("unchecked")
                List<Sticker> result = query.getResultList();
                return result;
                
            } catch (Exception ex) {
                logger.error("Error in alternative search query", ex);
                return java.util.Collections.emptyList();
            }
        }
    }
}