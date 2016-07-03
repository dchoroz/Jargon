package config;

import annotations.Include;
import annotations.Merge;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.inject.Inject;
import javax.persistence.Embedded;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import org.atmosphere.cpr.Broadcaster;
import org.slf4j.LoggerFactory;

/**
 *
 * @author kounabi
 */
/*Class for holding all the EntityConfig*/
@Startup
@Singleton
public class InitialConfig {

    private final static org.slf4j.Logger logger = LoggerFactory.getLogger(InitialConfig.class);

    Set<EntityConfig> entitiesList;
    String fullJavascript;
    @Inject JargonExtension ee;
    Map<String, Broadcaster> broadcasters;
    Map<String, String[]> modelStoreScript;
    
    @PostConstruct
    private void init() {
        
        
        List<Class<?>> list = ee.getEntitiesList();
        this.fullJavascript = "";
        logger.info("///////////////////////*********EntityClasses Configuring for "+list.size()+" Entities************///////////////////////// ");
        this.broadcasters = new HashMap<String, Broadcaster>();
        this.modelStoreScript = new HashMap<String, String[]>();
        
        for (Class<?> c : list) {
            this.addEntityClass(c);
        }
        
        for (EntityConfig c : this.entitiesList) {
            logger.info("EntityClasses, init: ecd "+c.getFullName());
            logger.info(c.getClazz().getCanonicalName()+" "+c.getPrimaryKey().getName());
        }
        System.out.println("EntityClasses, init: entitiesList size: "+this.entitiesList.size());
//        this.fullJavascript = this.mergeScript();
    }
    
    @PreDestroy
    private void destroy() {
        logger.info("EntityClasses, Destroy");
        this.broadcasters.clear();
        this.modelStoreScript.clear();
        this.entitiesList.clear();
    }

    public InitialConfig() {
        entitiesList = new HashSet<EntityConfig>();
    }

    public String getFullJavascript() {
        return fullJavascript;
    }
    
    public Set<EntityConfig> getList() {
        return this.entitiesList;
    }

    public Map<String, String[]> getModelStoreScript() {
        return modelStoreScript;
    }

    public void setModelStoreScript(Map<String, String[]> modelStoreScript) {
        this.modelStoreScript = modelStoreScript;
    }
    
    private void addEntityClass(Class<?> c) {
        
        EntityConfig ecd = new EntityConfig();
        ecd.setFullName(c.getCanonicalName());
        ecd.setSimpleName(c.getSimpleName());
        ecd.setClazz(c);
        String mapping = "";
        this.setFields(ecd, ecd.getClazz().getDeclaredFields(), ecd.getClazz().getMethods(), mapping);
        this.addInherited(ecd, ecd.getClazz(), ecd.getFields(), mapping);
        
        ecd.setIdField();
//        BroadcasterFactory.getDefault().get(c.getCanonicalName());
//        this.broadcasters.put(c.getCanonicalName(), BroadcasterFactory.getDefault().get(c.getCanonicalName()));
        this.entitiesList.add(ecd);
    }
   
    public EntityConfig getEntityClassData(String className){
       
       for(EntityConfig ecd: this.entitiesList){
           if(ecd.getFullName().compareTo(className) == 0) return ecd;
       }
       
       return null;
    }
    
    private void setFields(EntityConfig ecd, Field[] classFields, Method[] classMethods, String mapping) {
        
        boolean isField;
        for (Field f : classFields) {
//                System.out.println("field: " + f + " " + f.getAnnotations().length);
            if (this.isUserDeclared(f.getName())) {
                isField = false;
                Annotation[] fieldAnnotations = f.getDeclaredAnnotations();
               
                String up = f.getName().substring(0, 1).toUpperCase();
                String fieldGetMethod = ("get"+up+f.getName().substring(1, f.getName().length()));
                String fieldIsMethod = ("is"+up+f.getName().substring(1, f.getName().length()));
                Annotation[] fieldMethodAnnotations = null;      
                
                for(Method m : classMethods){
                    if((m.getName().compareTo(fieldGetMethod) == 0 || m.getName().compareTo(fieldIsMethod) == 0) && m.getDeclaredAnnotations().length > 0){
                        fieldMethodAnnotations = new Annotation[fieldAnnotations.length+m.getDeclaredAnnotations().length];
                        System.arraycopy(fieldAnnotations, 0, fieldMethodAnnotations, 0, fieldAnnotations.length);
                        System.arraycopy(m.getDeclaredAnnotations(),0,fieldMethodAnnotations,fieldAnnotations.length, m.getDeclaredAnnotations().length);
                    }
                }      
                
                if(fieldMethodAnnotations == null){
                    fieldMethodAnnotations = fieldAnnotations;
                }
                
                for (Annotation an : fieldMethodAnnotations) {
                    logger.info(f.getName());                    
                    if (an instanceof OneToOne || an instanceof OneToMany || an instanceof ManyToOne || an instanceof ManyToMany) {
                        logger.info("association field: "+an.annotationType().getSimpleName()+" "+ f.getName());
                        ecd.associationFields.put(f, an);
                        ecd.fieldMapping.put(f, mapping);
                        isField = true;
                    }else if(an instanceof Embedded){                        
                        this.addEmbedded(ecd, f, mapping);
                        isField = true;
                    }
                }
                
                if(!isField){
                    logger.info("EntityClasses, setFields: field: " + f.getName());
                    ecd.classFields.put(f, Arrays.asList(fieldMethodAnnotations));
                    ecd.fieldMapping.put(f, mapping);
                }
            }
        }   
        
    }

    public Map<String, Broadcaster> getBroadcasters() {
        return this.broadcasters;
    }

    public String mergeScript() {

        Set<EntityConfig> list = this.getList();
        String script = "";

        for(EntityConfig ecd: list){
            String[] modelStore = new String[2];
            logger.info("EntityClasses, mergeScript: creating script for "+ ecd.getFullName());
            ecd.createExtjsScript(this.entitiesList);
            script = script + ecd.getExtjsScript();
            modelStore[0] = ecd.getModelScript();
            modelStore[1] = ecd.getStoreScript();
            this.modelStoreScript.put(ecd.getScriptName(), modelStore);
        }

        return script;
    }

    public Field getEntityPrimaryKey(Class<?> clazz){
        Field field = null;
        logger.info("ENTERED GET PRIMARY KEY");
        for(EntityConfig ecd: this.entitiesList){
            if(clazz.equals(ecd.getClazz())){
                return ecd.getPrimaryKey();
            }
        }
        
        return field;
    }
    
    private void addInherited(EntityConfig ecd, Class clazz, Map<Field, List<Annotation>> fields, String mapping){
        
            /**Start process. Check @Merge annotation presence to add superClasses' fields**/
            Merge merge = (Merge) clazz.getAnnotation(Merge.class);
            if(merge != null){
                Class[] classList = merge.value();
                for(Class superClazz : classList){                    
                    this.setFields(ecd, superClazz.getDeclaredFields(), superClazz.getMethods(), mapping);
                }
            }
            /**End process. **/
            Class superClazz = clazz.getSuperclass();
            logger.info("Adding Inhereted fields: "+clazz.getCanonicalName());
            logger.info(superClazz.getCanonicalName());
            if(!superClazz.getCanonicalName().equals(Object.class.getCanonicalName())){
                if(superClazz.isAnnotationPresent(Include.class)){
                    this.setFields(ecd, superClazz.getDeclaredFields(), superClazz.getMethods(), mapping);
                }
                this.addInherited(ecd, superClazz, fields, mapping);
            }
        
    }
    
    private void addEmbedded(EntityConfig ecd, Field field, String mapping){
        
        mapping = mapping+field.getName()+".";
        Field[] fields = field.getType().getDeclaredFields();
        this.setFields(ecd, fields, field.getType().getMethods(), mapping);
    }
    
    private boolean isUserDeclared(String field) {

        if (field.equals("_persistence_primaryKey")) {
            return false;
        } else if (field.equals("_persistence_listener")) {
            return false;
        } else if (field.equals("_persistence_fetchGroup")) {
            return false;
        } else if (field.equals("_persistence_shouldRefreshFetchGroup")) {
            return false;
        } else if (field.equals("_persistence_session")) {
            return false;
        } else if (field.equals("serialVersionUID")) {
            return false;
        }

        return true;
    }
}
