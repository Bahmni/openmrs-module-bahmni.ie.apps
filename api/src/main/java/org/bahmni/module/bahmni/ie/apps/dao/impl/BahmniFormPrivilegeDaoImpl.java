package org.bahmni.module.bahmni.ie.apps.dao.impl;

import org.bahmni.module.bahmni.ie.apps.dao.BahmniFormPrivilegeDao;
import org.bahmni.module.bahmni.ie.apps.model.FormPrivilege;
import org.hibernate.*;
import org.hibernate.criterion.Restrictions;

import org.openmrs.api.db.DAOException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Repository
public class BahmniFormPrivilegeDaoImpl implements BahmniFormPrivilegeDao {

    private SessionFactory sessionFactory;

    @Autowired
    public BahmniFormPrivilegeDaoImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public List<FormPrivilege> getAllPrivilegesForForm(Integer formId,String formVersion) throws DAOException {
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(FormPrivilege.class);
        criteria.add(Restrictions.eq("formId", formId));
        criteria.add(Restrictions.eq("formVersion", formVersion));
        return criteria.list();
    }
    @Override
    public FormPrivilege getFormPrivilege(String formPrivilegeName, Integer formId) throws DAOException {
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(FormPrivilege.class);
        criteria.add(Restrictions.eq("formId", formId));
        criteria.add(Restrictions.eq("privilegeName",formPrivilegeName));
        return (FormPrivilege) criteria.uniqueResult();
    }
    @Override
    public List<FormPrivilege> getFormPrivilegeGivenFormUuid(String formVersion, Integer formId) throws DAOException {
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(FormPrivilege.class);
        criteria.add(Restrictions.eq("formId", formId));
        criteria.add(Restrictions.eq("formVersion",formVersion));
        return  criteria.list();
    }


    @Override
    @Transactional
    public FormPrivilege saveFormPrivilege(FormPrivilege formPrivilege) {
        sessionFactory.getCurrentSession().saveOrUpdate(formPrivilege);        
        return formPrivilege;
    }


    @Override
    @Transactional
    public FormPrivilege deleteFormPrivilege(FormPrivilege formPrivilege) {
         sessionFactory.getCurrentSession().delete(formPrivilege);
         sessionFactory.getCurrentSession().flush();
         return formPrivilege;
    }

}
