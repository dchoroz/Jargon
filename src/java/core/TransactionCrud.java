///*
// * To change this template, choose Tools | Templates
// * and open the template in the editor.
// */
//package core;
//
//import config.EntityClassData;
//import config.EntityClasses;
//import extra.Person;
//import helper.Operation;
//import helper.RestAction;
//import static helper.RestAction.create;
//import static helper.RestAction.destroy;
//import static helper.RestAction.read;
//import static helper.RestAction.update;
//import helper.StoreFilter;
//import java.io.IOException;
//import java.lang.annotation.Annotation;
//import java.lang.reflect.Field;
//import java.lang.reflect.ParameterizedType;
//import java.lang.reflect.Type;
//import java.util.ArrayList;
//import java.util.EmptyStackException;
//import java.util.List;
//import java.util.Map;
//import java.util.logging.Level;
//import java.util.logging.Logger;
//import javax.ejb.EJB;
//import javax.ejb.Stateless;
//import javax.ejb.TransactionAttribute;
//import javax.ejb.TransactionAttributeType;
//import javax.naming.NamingException;
//import javax.persistence.EntityManager;
//import javax.persistence.ManyToOne;
//import javax.persistence.OneToMany;
//import javax.persistence.OneToOne;
//import javax.persistence.PersistenceContext;
//import javax.persistence.TypedQuery;
//import javax.persistence.criteria.CriteriaBuilder;
//import javax.persistence.criteria.CriteriaQuery;
//import javax.persistence.criteria.Predicate;
//import javax.persistence.criteria.Root;
//import org.codehaus.jackson.JsonParseException;
//import org.codehaus.jackson.map.JsonMappingException;
//
///**
// *
// * @author kounabi
// */
//@TransactionAttribute(TransactionAttributeType.MANDATORY)
//@Stateless
//public class TransactionCrud<T> {
//    //    @Inject
////    ExtjsExtension extjsextension;
//    @PersistenceContext
//    EntityManager em;
//    @EJB
//    EntityClasses ec;
//    
//
//    public TransactionCrud() {
//    }
//
//    protected EntityManager getEntityManager() {
//        return this.em;
//    }
//
//    /**
//     * called from dataRouter to try to commit the operations from the client
//     */@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
//    public List crudData(Operation operation) {
//
//        RestAction action = operation.getAction();
//        List data = operation.getData();
//
//        Class<?> type = operation.getType();
//
//        List returnedData;
//        switch (action) {
//            case read:
//                returnedData = this.listData(data, type);
////                System.out.println("simpleCrud " + returnedData.size());
//                return returnedData;
//            case create:
//
//                this.createData(data, type);
//
//                //                person = rMessage.getLperson().get(0);
//                //                this.createPerson(person.getFname(), person.getLname());
//                break;
//            case update:
//                this.updateData(data, type);
//                //                person = rMessage.getLperson().get(0);
//                //                this.updatePerson(person.getId(), person.getFname(), person.getLname());
//                break;
//            case destroy:
//                this.deleteData(data, type);
//                //                person = rMessage.getLperson().get(0);
//                //                this.deletePerson(person.getId());
//                break;
//            default:
//                System.out.println(action + ": no such CRUD action");
//        }
//
//        return null;
//    }
//
//    public List createData(List data, Class type) {
//
////        ObjectMapper mapper = new ObjectMapper();
////        person = dataManager.find(id);
////        System.out.print(person.getId());
////        System.out.println(d.length);
//
//        for (int i = 0; i < data.size(); i++) {
//            if(i>3){
//                throw new EmptyStackException();
//            }
//            data.set(i, this.create(data.get(i), type));
//        }
//
//        System.out.println("createData: " + type.getSimpleName() + " persisted");
//        return data;
//    }
//
//    public void updateData(List data, Class type) {
//
//        for (int i = 0; i < data.size(); i++) {
//            System.out.println(data.get(i).getClass().getSimpleName() + " " + type.getSimpleName());
//            this.edit(data.get(i), type);
//        }
//
//
////        Object entity = dataManager.find(id, type);
////        System.out.print(person.getId());
//
////        person.setFname(cfname);
////        person.setLname(clname);
////        dataManager.edit(person);
////        System.out.println("updatePerson");
//    }
//
//    public void deleteData(List data, Class type) {
//
//        for (int i = 0; i < data.size(); i++) {
//            System.out.println(data.get(i).getClass().getSimpleName() + " " + type.getSimpleName());
//            this.remove(data.get(i), type);
//        }
////        System.out.println("deletePerson");
//
//    }
////    
//
//    public List listData(List data, Class entityClass) {
//
//        List result = null;
//
//        if (data.isEmpty()) {//no need to filter data
//            result = this.findAll(entityClass);
//
//        } else {//filtering to data needs to be done
//
//            List<StoreFilter> filters = (List<StoreFilter>) data;
//            CriteriaBuilder cb = em.getCriteriaBuilder();
//            CriteriaQuery cq = cb.createQuery(entityClass);
////            Metamodel m = em.getMetamodel();
////            EntityType et = m.entity(entityClass);
//            Root root = cq.from(entityClass);
//            List<Predicate> predicates = new ArrayList<Predicate>();
////            Pattern pattern = Pattern.compile(filters.get(0).getValue());
//            this.addPredicates(cb, cq, predicates, root, filters, data);
//
//            TypedQuery q = em.createQuery(cq);
//            result = q.getResultList();
//            System.out.println("SimpleCrud listData, returned resultset size: " + result.size());
//
//        }
//
//        return result;
//    }
//
//    /**
//     * *******************************
//     * simpleCrud **********************************
//     */
//    public Object create(Object entity, Class entityClass) {
////        Set<String> emfSet = getEntityManager(entityClass.getCanonicalName());
////        for (String puName : emfSet) {
////            EntityManagerFactory emf = Persistence.createEntityManagerFactory(puName);
////            EntityManager em = emf.createEntityManager();
////            EntityTransaction et = em.getTransaction();
////            et.begin();
////        Person entity1 = (Person) entity;
////        entity1.phoneNumbers.add(new Phone(2017438233));
//        getEntityManager().persist(entityClass.cast(entity));
//        return entity;
////        this.mergeAssociated(entity, entityClass);
//
////        System.out.println("SimpleCrud, create: persons phoneNUmbers list size: "+((Person)entity).phoneNumbers.size());
//
//
////            et.commit();
////            em.close();
////            emf.close();
////        }
//    }
//
//    public void edit(Object entity, Class entityClass) {
////        Set<EntityManagerFactory> emfSet = getEntityManager(entityClass.getCanonicalName());
////        for (EntityManagerFactory emf : emfSet) {
////            EntityManager em = emf.createEntityManager();
////            EntityTransaction et = em.getTransaction();
////            et.begin();
//        
//        EntityClassData ecd = ec.getEntityClassData(entityClass.getCanonicalName());
//        Map<Field, Annotation> ass = ecd.getAssociationFields();
//        for (Field f : ass.keySet()) {
//            f.setAccessible(true);
//            try {
//                if (f.get(entity) != null) {
//                    List assList;
//                    Class type;
//                    if (ass.get(f) instanceof OneToOne || ass.get(f) instanceof ManyToOne) {
//                        assList = new ArrayList();
//                        assList.add(f.get(entity));
//                        type = f.getType();
//                        assList = this.createData(assList, type);
//                        f.set(entity, assList.get(0));
//                    }else{
//                        assList = (List) f.get(entity);
//                        ParameterizedType aType = (ParameterizedType) f.getGenericType();
//                        Type[] fieldArgTypes = aType.getActualTypeArguments();
//                        type = (Class) fieldArgTypes[0];
//                        assList = this.createData(assList, type);
//                        f.set(entity, assList);
//                    }
//                    
//                }
//            getEntityManager().merge(entityClass.cast(entity));
//
//            } catch (IllegalAccessException ex) {
//                Logger.getLogger(SimpleCrud.class.getName()).log(Level.SEVERE, null, ex);
//            }
//        }
////            et.commit();
////            em.close();
////        }
////   
//    }
//
//    public void remove(Object entity, Class entityClass) {
////        Set<EntityManagerFactory> emfSet = getEntityManager(entityClass.getCanonicalName());
////        for (EntityManagerFactory emf : emfSet) {
////            EntityManager em = emf.createEntityManager();
////            EntityTransaction et = em.getTransaction();
////            et.begin();            
////        Object entity1 = getEntityManager().merge(entityClass.cast(entity));
//        EntityClassData ecd = ec.getEntityClassData(entityClass.getCanonicalName());
//        Field f = ecd.getPrimaryKey();
//        f.setAccessible(true);
//        try {
//            Object entity1 = this.find(f.get(entity),entityClass);
//            getEntityManager().remove(entityClass.cast(entity1));
//
//        } catch (IllegalArgumentException ex) {
//            Logger.getLogger(SimpleCrud.class.getName()).log(Level.SEVERE, null, ex);
//        } catch (IllegalAccessException ex) {
//            Logger.getLogger(SimpleCrud.class.getName()).log(Level.SEVERE, null, ex);
//        }
////            et.commit();
////            em.close();
////        }        
//    }
//
//    public Object find(Object id, Class entityClass) {
////        Set<EntityManagerFactory> emfSet = getEntityManager(entityClass.getCanonicalName());
//        Object object;
////        
////        for (EntityManagerFactory emf : emfSet) {
////            EntityManager em = emf.createEntityManager();
////            EntityTransaction et = em.getTransaction();
////            et.begin();
//        object = getEntityManager().find(entityClass, id);
////            et.commit();//is it necessery?
////            em.close();
////            if(object != null) return object;
////        }    
//        return object;
//    }
//
//    private void addPredicates(CriteriaBuilder cb, CriteriaQuery cq, List<Predicate> predicates, Root root, List<StoreFilter> filters, List data) {
//        predicates.add(cb.equal(root.get(filters.get(0).getProperty()), filters.get(0).getValue()));
//        cq.where(predicates.get(0));
//        for (int i = 1; i < data.size(); i++) {
//            predicates.add(cb.like(root.get(filters.get(i).getProperty()), filters.get(i).getValue()));
//            cq.where(predicates.get(i));
//        }
//    }
//
//    public List<T> findAll(Class entityClass) {
////        Set<String> emfSet = getEntityManager(entityClass.getCanonicalName());
//        List<T> objectList = new ArrayList<T>();
////       
////        for (String puName : emfSet) {
////            EntityManagerFactory emf = Persistence.createEntityManagerFactory(puName);
////            EntityManager em = emf.createEntityManager();
////            EntityTransaction et = em.getTransaction();
////            et.begin();
//        javax.persistence.criteria.CriteriaQuery cq = getEntityManager().getCriteriaBuilder().createQuery();
//        cq.select(cq.from(entityClass));
////        System.out.println("EMF: " + getEntityManager().createQuery(cq).getResultList().size());
//        objectList.addAll(getEntityManager().createQuery(cq).getResultList());
////            et.commit();
////            em.close();
////            emf.close();
////        }    
////        System.out.print("LIst size: "+objectList.size());
//        return objectList;
//    }
//
//    public List<T> findRange(int[] range, Class entityClass) {
//        javax.persistence.criteria.CriteriaQuery cq = getEntityManager().getCriteriaBuilder().createQuery();
//        cq.select(cq.from(entityClass));
//        javax.persistence.Query q = getEntityManager().createQuery(cq);
//        q.setMaxResults(range[1] - range[0]);
//        q.setFirstResult(range[0]);
//        return q.getResultList();
//    }
//
//    public int count(Class entityClass) {
//        javax.persistence.criteria.CriteriaQuery cq = getEntityManager().getCriteriaBuilder().createQuery();
//        javax.persistence.criteria.Root<T> rt = cq.from(entityClass);
//        cq.select(getEntityManager().getCriteriaBuilder().count(rt));
//        javax.persistence.Query q = getEntityManager().createQuery(cq);
//        return ((Long) q.getSingleResult()).intValue();
//    }
//
//    private void mergeAssociated(Object entity, Class entityClass) {
//
//        Map<Field, Annotation> associations = this.getAssociations(entityClass.getCanonicalName());
//        System.out.println("SimpleCrud, mergeAssociated:");
//        System.out.println("Class " + entityClass.getCanonicalName());
//        System.out.println("associations length " + associations.size());
//
//        for (Field f : associations.keySet()) {
//            System.out.println("field name " + f.getName());
//            if (!associations.get(f).annotationType().equals(OneToMany.class)) {
//                try {
////                    System.out.println("Entity "+((Phone)entity).getPerson());
//                    Object value = f.get(entity);
//                    EntityClassData ecd = ec.getEntityClassData(f.getType().getCanonicalName());
//                    Field keyField = ecd.getPrimaryKey();
//                    Object keyValue = keyField.get(value);
//
//                    Object associatedEntity = this.find(keyValue, f.getType());
//                    Person p = (Person) associatedEntity;
//                    f.set(entity, associatedEntity);
//                    System.out.println("SimpleCrud, mergeAssociated: " + p);
//                } catch (IllegalArgumentException ex) {
//                    Logger.getLogger(SimpleCrud.class.getName()).log(Level.SEVERE, null, ex);
//                } catch (IllegalAccessException ex) {
//                    Logger.getLogger(SimpleCrud.class.getName()).log(Level.SEVERE, null, ex);
//                }
//            }
//        }
//
//        System.out.println("SimpleCrud, mergeAssociation: " + associations);
//
//    }
//
//    private Map<Field, Annotation> getAssociations(String className) {
//
//        return this.getEntityConfig(className).getAssociationFields();
//
//    }
//
//    private EntityClassData getEntityConfig(String className) {
//
//        return ec.getEntityClassData(className);
//
//    }
//}
