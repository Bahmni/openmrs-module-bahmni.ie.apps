package org.bahmni.module.bahmni.ie.apps.dao.impl;

import org.bahmni.module.bahmni.ie.apps.dao.BahmniFormDao;
import org.hibernate.query.Query;
import org.bahmni.module.bahmni.ie.apps.model.BahmniForm;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.transform.ResultTransformer;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.openmrs.api.context.Context;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.ArrayList;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.verify;

@RunWith(PowerMockRunner.class)
@PrepareForTest(Context.class)
public class BahmniFormDaoImplTest {
    @Before
    public void setUp() throws Exception {

    }
    @Mock
    SessionFactory sessionFactory;

    @Mock
    Session session;

    @Mock
    Query query;

    @Test
    public void shouldGenereateQueryWithPublishedForformsWithNameTransaltionsForPublishedTrue() throws Exception {
        BahmniFormDao bahmniFormDao = new BahmniFormDaoImpl(sessionFactory);
        PowerMockito.when(sessionFactory.getCurrentSession()).thenReturn(session);
        PowerMockito.when(session.createQuery(anyString())).thenReturn(query);
        PowerMockito.when(query.setResultTransformer(any(ResultTransformer.class))).thenReturn(query);
        PowerMockito.when(query.list()).thenReturn(new ArrayList());
        bahmniFormDao.formsWithNameTransaltionsFor(null,true,false);
        verify(session).createQuery( "Select COALESCE(FR.valueReference,'[]') as nameTranslation, " +
                "F.name as name, F.formId as id, F.uuid as uuid, F.version as version, F.published as published from FormResource FR " +
                "right outer join FR.form F with " +
                "FR.datatypeClassname!='org.bahmni.customdatatype.datatype.FileSystemStorageDatatype' where  " +
                "F.published=true   order by F.name asc, F.version desc");
    }

    @Test
    public void shouldGenereateQueryWithPublishedForformsWithNameTransaltionsForPublishedTrueAndRetiredFalse() throws Exception {
        BahmniFormDao bahmniFormDao = new BahmniFormDaoImpl(sessionFactory);
        PowerMockito.when(sessionFactory.getCurrentSession()).thenReturn(session);
        PowerMockito.when(session.createQuery(anyString())).thenReturn(query);
        PowerMockito.when(query.setResultTransformer(any(ResultTransformer.class))).thenReturn(query);
        PowerMockito.when(query.list()).thenReturn(new ArrayList());
        bahmniFormDao.formsWithNameTransaltionsFor(null,false,false);
        verify(session).createQuery( "Select COALESCE(FR.valueReference,'[]') as nameTranslation, F.name as name, F.formId as id, " +
                "F.uuid as uuid, F.version as version" +
                ", F.published as published from FormResource FR right outer join FR.form F " +
                "with FR.datatypeClassname!='org.bahmni.customdatatype.datatype.FileSystemStorageDatatype' where " +
                " F.published=true and F.retired=false   order by F.name asc, F.version desc");
    }

    @Test
    public void shouldGenereateQueryWithPublishedForformsWithNameTransaltionsForPublishedFalseAndRetiredTrue() throws Exception {
        BahmniFormDao bahmniFormDao = new BahmniFormDaoImpl(sessionFactory);
        PowerMockito.when(sessionFactory.getCurrentSession()).thenReturn(session);
        PowerMockito.when(session.createQuery(anyString())).thenReturn(query);
        PowerMockito.when(query.setResultTransformer(any(ResultTransformer.class))).thenReturn(query);
        PowerMockito.when(query.list()).thenReturn(new ArrayList());
        bahmniFormDao.formsWithNameTransaltionsFor(null,true,true);
        verify(session).createQuery( "Select COALESCE(FR.valueReference,'[]') as nameTranslation, F.name as name, F.formId as id," +
                " F.uuid as uuid, F.version as version" +
                ", F.published as published from FormResource FR right outer join FR.form F " +
                "with FR.datatypeClassname!='org.bahmni.customdatatype.datatype.FileSystemStorageDatatype'" +
                "     order by F.name asc, F.version desc");
    }

    @Test
    public void shouldGenerateQueryForformsWithNameTransaltionsForPublishedFalseAndRetiredTrueAndFormNameGiven() throws Exception {
        BahmniFormDao bahmniFormDao = new BahmniFormDaoImpl(sessionFactory);
        PowerMockito.when(sessionFactory.getCurrentSession()).thenReturn(session);
        PowerMockito.when(session.createQuery(anyString())).thenReturn(query);
        PowerMockito.when(query.setResultTransformer(any(ResultTransformer.class))).thenReturn(query);
        PowerMockito.when(query.list()).thenReturn(new ArrayList());
        bahmniFormDao.formsWithNameTransaltionsFor("FormName",true,true);
        verify(session).createQuery(   "Select COALESCE(FR.valueReference,'[]') as nameTranslation, F.name as name, F.formId as id, " +
                "F.uuid as uuid, F.version as version, F.published as published from FormResource FR right outer join " +
                "FR.form F with FR.datatypeClassname!='org.bahmni.customdatatype.datatype.FileSystemStorageDatatype' " +
                "where    name= FormName order by F.name asc, F.version desc");
    }

    @Test
    public void shouldGenerateQueryForformsWithNameTransaltionsForPublishedFalseAndRetiredFalseAndFormNameGiven() throws Exception {
        BahmniFormDao bahmniFormDao = new BahmniFormDaoImpl(sessionFactory);
        PowerMockito.when(sessionFactory.getCurrentSession()).thenReturn(session);
        PowerMockito.when(session.createQuery(anyString())).thenReturn(query);
        PowerMockito.when(query.setResultTransformer(any(ResultTransformer.class))).thenReturn(query);
        PowerMockito.when(query.list()).thenReturn(new ArrayList());
        bahmniFormDao.formsWithNameTransaltionsFor("FormName",false,false);
        verify(session).createQuery(   "Select COALESCE(FR.valueReference,'[]') as nameTranslation, F.name as name, F.formId as id," +
                " F.uuid as uuid, F.version as version, F.published as published from FormResource FR right outer join " +
                "FR.form F with FR.datatypeClassname!='org.bahmni.customdatatype.datatype.FileSystemStorageDatatype' " +
                "where  F.published=true and F.retired=false  and name= FormName order by F.name asc, F.version desc");
    }
}
