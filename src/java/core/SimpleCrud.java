package core;

import extra.Person;
import config.EntityConfig;
import config.InitialConfig;
import helper.StoreFilter;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

@Stateless
public class SimpleCrud<T> {

    @PersistenceContext EntityManager em;
    @EJB InitialConfig ec;
    
    public SimpleCrud() {
    }

//    protected EntityManager getEntityManager() {
//        return em;
//    }

//    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public Object create(Object entity, Class entityClass) {

        em.persist(entityClass.cast(entity));
        em.flush();
        return entity;

    }

    public void edit(Object entity, Class entityClass) {

        EntityConfig ecd = ec.getEntityClassData(entityClass.getCanonicalName());
        Map<Field, Annotation> ass = ecd.getAssociationFields();
        for (Field f : ass.keySet()) {
            f.setAccessible(true);
            try {
                if (f.get(entity) != null) {
                    List assList;
//                    Class type;
                    if (ass.get(f) instanceof OneToOne || ass.get(f) instanceof ManyToOne) {
//                        assList = new ArrayList();
//                        assList.add(f.get(entity));
//                        type = f.getType();
//                        assList = this.createData(assList, type);
                        f.set(entity, f.get(entity));
                    }else{
                        assList = (List) f.get(entity);
//                        ParameterizedType aType = (ParameterizedType) f.getGenericType();
//                        Type[] fieldArgTypes = aType.getActualTypeArguments();
//                        type = (Class) fieldArgTypes[0];
//                        assList = this.createData(assList, type);
                        f.set(entity, assList);
                    }
                    
                }
            em.merge(entityClass.cast(entity));

            } catch (IllegalAccessException ex) {
                Logger.getLogger(SimpleCrud.class.getName()).log(Level.SEVERE, null, ex);

            }
        }

    }

    public void remove(Object entity, Class entityClass) {

        EntityConfig ecd = ec.getEntityClassData(entityClass.getCanonicalName());
        Field f = ecd.getPrimaryKey();
        f.setAccessible(true);
        try {
            System.out.println(f.get(entity)+" "+entity+" "+entityClass.getCanonicalName());
            Object entity1 = this.find(f.get(entity),entityClass);
            em.remove(entityClass.cast(entity1));

        } catch (IllegalArgumentException ex) {
            Logger.getLogger(SimpleCrud.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            Logger.getLogger(SimpleCrud.class.getName()).log(Level.SEVERE, null, ex);
        }
       
    }
    public List listData(List data, Class entityClass) {

        List result = null;

        if (data.isEmpty()) {//no need to filter data
            result = this.findAll(entityClass);

        } else {//filtering to data needs to be done

            List<StoreFilter> filters = (List<StoreFilter>) data;
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery cq = cb.createQuery(entityClass);
//            Metamodel m = em.getMetamodel();
//            EntityType et = m.entity(entityClass);
            Root root = cq.from(entityClass);
            List<Predicate> predicates = new ArrayList<Predicate>();
//            Pattern pattern = Pattern.compile(filters.get(0).getValue());
            this.addPredicates(cb, cq, predicates, root, filters, data);

            TypedQuery q = em.createQuery(cq);
            result = q.getResultList();
            System.out.println("SimpleCrud listData, returned resultset size: " + result.size());

        }

        return result;
    }
    public Object find(Object id, Class entityClass) {
        Object object;

        object = em.find(entityClass, id);

        return object;
    }

    private void addPredicates(CriteriaBuilder cb, CriteriaQuery cq, List<Predicate> predicates, Root root, List<StoreFilter> filters, List data) {
        predicates.add(cb.equal(root.get(filters.get(0).getProperty()), filters.get(0).getValue()));
        cq.where(predicates.get(0));
        for (int i = 1; i < data.size(); i++) {
            predicates.add(cb.like(root.get(filters.get(i).getProperty()), filters.get(i).getValue()));
            cq.where(predicates.get(i));
        }
    }

    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public List<T> findAll(Class entityClass) {
        List<T> objectList = new ArrayList<T>();

        javax.persistence.criteria.CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
        cq.select(cq.from(entityClass));
        objectList.addAll(em.createQuery(cq).getResultList());

        return objectList;
    }

    public List<T> findRange(int[] range, Class entityClass) {
        javax.persistence.criteria.CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
        cq.select(cq.from(entityClass));
        javax.persistence.Query q = em.createQuery(cq);
        q.setMaxResults(range[1] - range[0]);
        q.setFirstResult(range[0]);
        return q.getResultList();
    }

    public int count(Class entityClass) {
        javax.persistence.criteria.CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
        javax.persistence.criteria.Root<T> rt = cq.from(entityClass);
        cq.select(em.getCriteriaBuilder().count(rt));
        javax.persistence.Query q = em.createQuery(cq);
        return ((Long) q.getSingleResult()).intValue();
    }

    private void mergeAssociated(Object entity, Class entityClass) {

        Map<Field, Annotation> associations = this.getAssociations(entityClass.getCanonicalName());
        System.out.println("SimpleCrud, mergeAssociated:");
        System.out.println("Class " + entityClass.getCanonicalName());
        System.out.println("associations length " + associations.size());

        for (Field f : associations.keySet()) {
            System.out.println("field name " + f.getName());
            if (!associations.get(f).annotationType().equals(OneToMany.class)) {
                try {
//                    System.out.println("Entity "+((Phone)entity).getPerson());
                    Object value = f.get(entity);
                    EntityConfig ecd = ec.getEntityClassData(f.getType().getCanonicalName());
                    Field keyField = ecd.getPrimaryKey();
                    Object keyValue = keyField.get(value);

                    Object associatedEntity = this.find(keyValue, f.getType());
                    Person p = (Person) associatedEntity;
                    f.set(entity, associatedEntity);
                    System.out.println("SimpleCrud, mergeAssociated: " + p);
                } catch (IllegalArgumentException ex) {
                    Logger.getLogger(SimpleCrud.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IllegalAccessException ex) {
                    Logger.getLogger(SimpleCrud.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }

        System.out.println("SimpleCrud, mergeAssociation: " + associations);

    }

    private Map<Field, Annotation> getAssociations(String className) {

        return this.getEntityConfig(className).getAssociationFields();

    }

    private EntityConfig getEntityConfig(String className) {

        return ec.getEntityClassData(className);

    }
}