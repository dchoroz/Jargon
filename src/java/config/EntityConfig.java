/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package config;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.persistence.EmbeddedId;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import org.slf4j.LoggerFactory;

/**
 *
 * @author kounabi
 */
public class EntityConfig {

    private final static org.slf4j.Logger logger = LoggerFactory.getLogger(EntityConfig.class);

    String fullName;
    String simpleName;
    Map<Field, List<Annotation>> classFields;
    Map<Field, Annotation> associationFields;
    Map<Field, String> fieldMapping;
    Field primaryKey;
    String extjsScript;
    String modelScript;
    String storeScript;
    String scriptName;
    Class<?> clazz;

    public EntityConfig() {
        classFields = new HashMap();
        associationFields = new HashMap();
        fieldMapping = new HashMap();
    }

    public Map<Field, List<Annotation>> getFields() {
        return classFields;
    }

    public String getScriptName() {
        return scriptName;
    }

    public void setScriptName(String scriptName) {
        this.scriptName = scriptName;
    }

    public void setFields(Map<Field, List<Annotation>> fields) {
        this.classFields = fields;
    }

    public Field getPrimaryKey() {
        return primaryKey;
    }

    public void setPrimaryKey(Field primaryKey) {
        this.primaryKey = primaryKey;
    }

    public String getExtjsScript() {
        return extjsScript;
    }

    public String getModelScript() {
        return modelScript;
    }

    public void setModelScript(String modelScript) {
        this.modelScript = modelScript;
    }

    public String getStoreScript() {
        return storeScript;
    }

    public Map<Field, Annotation> getAssociationFields() {
        return associationFields;
    }

    public Map<Field, List<Annotation>> getClassFields() {
        return classFields;
    }

    public void setClassFields(Map<Field, List<Annotation>> classFields) {
        this.classFields = classFields;
    }

    public Map<Field, String> getFieldMapping() {
        return fieldMapping;
    }

    public void setFieldMapping(Map<Field, String> fieldMapping) {
        this.fieldMapping = fieldMapping;
    }
    
    public void setStoreScript(String storeScript) {
        this.storeScript = storeScript;
    }
    
    public Class<?> getClazz() {
        return clazz;
    }

    public void setClazz(Class<?> clazz) {
        this.clazz = clazz;
    }

    public EntityConfig(String fullName, String simpleName) {
        this.fullName = fullName;
        this.simpleName = simpleName;
        this.classFields = new HashMap<Field, List<Annotation>>();
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getSimpleName() {
        return simpleName;
    }

    public void setSimpleName(String simpleName) {
        this.simpleName = simpleName;
    }

    public void addClassField(Field field, List annotations) {
        this.classFields.put(field, annotations);
    }

    //Create the extjs code String for corresponding model and store of the entity
    public void createExtjsScript(Set<EntityConfig> entitiesList) {
        for(Field f : this.fieldMapping.keySet()){
            logger.info(this.fieldMapping.get(f));
        }

        String extjsScript = "";
//        Method[] classMethods = this.clazz.getDeclaredMethods();
        this.scriptName = this.fullName.replace(".", "-");
        //Begin Model definition
        extjsScript = extjsScript + "Ext.define('AM.model." + this.scriptName + "', \n"
                + "{ extend: 'AM.myStuff.BaseModel', ";

        extjsScript = extjsScript + "writeAllFields: 'true',\n\n";

        extjsScript = extjsScript + "idProperty: '" + this.getPrimaryKey().getName() + "',\n\n";
                
        extjsScript = extjsScript + "fields: [";

        for (Field f : this.classFields.keySet()) {
//            System.out.println("field: " + f.getName() + " " + f.getAnnotations().length);
//                    System.out.println("field: " + f.getName());
            String mapping = this.fieldMapping.get(f);
            if(mapping.isEmpty()){
                extjsScript = extjsScript + "'" + f.getName() + "',";

            }else{
                extjsScript = extjsScript+  "{name :'" + f.getName() + "', mapping: '"+mapping+f.getName()+"'},";
            }
        }
        //Create extjs code for associations in model
        List<String> associationList = new ArrayList<String>();
        
        this.getAssociations(associationList, entitiesList);
        extjsScript += associationList.get(0);
        extjsScript = extjsScript + "],\n\n";
        extjsScript += associationList.get(1);        
        extjsScript = extjsScript + "proxy: {\n"
                + "    type: 'websocketmanaged',\n"
                + "    reader: {\n"
                + "        type: 'json',\n"
                + "        root: 'data',\n"
                + "    },\n"
                + "    writer: {\n"
                + "        type: 'bjson',\n"
                + "        root: 'data',\n"
//                + "        getRecordData: function (record) {\n"
//                + "             var phonenumbers = [],\n"
//                + "             data = record.getphoneNumbersList().data.items,\n"
//                + "             finalData = [];\n"
//                + "             for(var i=0; i< data.length; i++){\n"
//                + "                 finalData = finalData.concat(data[i].data);\n"
//                + "             }\n" 
//                + "             record.set('phoneNumbers', finalData);\n" 
//                + "             return record.data;"
//                + "         }\n"
                + "    }\n"
                
                + "}\n\n});\n\n";
        
        //this is used for modelstoregenerator
        this.modelScript = extjsScript;
        //End Model definition

        //Begin Store definition
        extjsScript = extjsScript + "Ext.define('AM.store." + this.scriptName + "', {\n"
                + "    extend: 'Ext.data.Store',\n"
                + "    model: 'AM.model." + this.scriptName + "',\n"
                + "    \n"
                + "});\n\n";
         //this is used for modelstoregenerator
         this.storeScript = "Ext.define('AM.store." + this.scriptName + "', {\n"
                + "    extend: 'Ext.data.Store',\n"
                + "    model: 'AM.model." + this.scriptName + "',\n"
                + "    \n"
                + "});\n\n";
        
        //End Store definition

        this.extjsScript = extjsScript;
    }

    //Create and return the extjs code for the associations for this model.
    private List<String> getAssociations(List<String> associationList, Set<EntityConfig> entitiesList) {
        String associations = "associations:[";
        String foreignFields = "";
        //First check class fields for annotations
        for (Field f : this.associationFields.keySet()) {
//            Annotation[] fieldAnnotations = f.getDeclaredAnnotations();
            //For each field check the annotations
            Annotation an = this.associationFields.get(f);

                if (an instanceof OneToOne) {
                    foreignFields += "'"+ f.getName()+"',";
                    associations += "\n             {type: 'hasOne',\n"
                            + "     model: 'AM.model." + f.getType().getCanonicalName().replace(".", "-")+ "',\n"
                            + "     foreignKey: '" + f.getName() + "',\n"
//                            + "     associationKey: '" + f.getName() + "',\n"
                            + "     getterName: 'get" + f.getType().getSimpleName() + "',\n"
                            + "     setterName: 'set" + f.getType().getSimpleName() + "',\n"
                            //primaryKey defaults to the idProperty of the associated model
                            + "     primaryKey: '" + this.getAssociatedPrimaryKey(f, entitiesList) +  "',\n"
                            + "},";

                } else if (an instanceof OneToMany) {
//                    foreignFields += "'"+ f.getName()+"',";
                    associations += "\n     {type: 'hasMany',\n"
//                            /**Change with List<type>**/
                            + "     model: 'AM.model." +this.getActualType(f) + "',\n"
                             + "     foreignKey: '" + this.simpleName.toLowerCase().replace(".","-") + "',\n"
//                            + "     foreignKey: '" + this.fullName.toLowerCase().replace(".","-") + "',\n"
                            + "     associationKey: '" + f.getName() + "',\n"
                            + "     name: '" + f.getName() + "',\n"
                             //primaryKey defaults to the idProperty of the associated model
                            + "     primaryKey: '" + this.getPrimaryKey().getName() + "',\n"
//                            + "     storeConfig:{id:'"+f.getName()+"'}"
                            + "     },";
                } else if (an instanceof ManyToOne) {
                                        foreignFields += "'"+ f.getName() +"',";

//                    foreignFields += "'"+ f.getType().getCanonicalName().toLowerCase().replace(".","-") +"',";
                    associations += "\n     {type: 'belongsTo',\n"
                            + "     model: 'AM.model." + f.getName() + "',\n"
                            + "     foreignKey: '" + f.getName() + "',\n"

//                            + "     foreignKey: '" + f.getType().getCanonicalName().toLowerCase().replace(".","-") + "',\n"
//                            + "     associationKey: '" + f.getName() + "',\n"
                            + "     getterName: 'get" + f.getType().getSimpleName() + "',\n"
                            + "     setterName: 'set" + f.getType().getSimpleName() + "',\n"
                             //primaryKey defaults to the idProperty of the associated model
                            + "     primaryKey: '" + this.getAssociatedPrimaryKey(f, entitiesList) + "',\n"
                            + "},";
                    
                } else if (an instanceof ManyToMany) {
                    /*TODO*/
                }
            
        }
        associations += "],\n";
        associationList.add(foreignFields);
        associationList.add(associations);
        return associationList;
    }

    //Find the primary key of the entity and set it to the primakyKey attribute
    public void setIdField() {
        Field idField = null;

        for (Field f : this.classFields.keySet()) {
            List<Annotation> fieldAnnotations = this.classFields.get(f);
            //For each field check the annotations
            for (Annotation an : fieldAnnotations) {
                if (an instanceof Id || an instanceof IdClass || an instanceof EmbeddedId) {
                    System.out.println("EntityClassData, setIdField: Found id field "+f.getName());
                    this.setPrimaryKey(f);
                }
            }
        }
    }
    
//    public List stringToEntityList(String jsonList) throws InstantiationException, IllegalAccessException{
//        
//        List list = new ArrayList();
//        ObjectMapper mapper = new ObjectMapper();
//        
//        while(true){
//            
//            Object newObject = this.clazz.newInstance();
//            for(Field f : this.classFields){
//                
//            }
//            break;
//        }
//        
//        return list;
//    }
    
    private String getActualType(Field f){
        String name = "";
        ParameterizedType aType = (ParameterizedType) f.getGenericType();
        Type[] fieldArgTypes = aType.getActualTypeArguments();
        Class<?> clazz = (Class) fieldArgTypes[0];
        name = clazz.getCanonicalName().replace(".","-");
        
        return name;
    }
    
    private String getAssociatedPrimaryKey(Field field, Set<EntityConfig> entitiesList) {
        String pk = "";
        Class clazz = null;
        System.out.println("EntityClassData, getAssociatedPrimaryKey: Associated class field name: "+field.getName()+" "+this.fullName);

        Type genericFieldType = field.getGenericType();

        if (genericFieldType instanceof ParameterizedType) {

            ParameterizedType aType = (ParameterizedType) genericFieldType;
            Type[] fieldArgTypes = aType.getActualTypeArguments();

            for (Type fieldArgType : fieldArgTypes) {
                clazz = (Class) fieldArgType;
            }

        } else {
            clazz = field.getType();
        }
        
//        try {
//            Context c = new InitialContext();
//            entityClasses = (EntityClasses) c.lookup("java:global/TestApp/EntityClasses!config.EntityClasses");
//        } catch (NamingException ne) {
//            Logger.getLogger(getClass().getName()).log(Level.SEVERE, "exception caught", ne);
//            throw new RuntimeException(ne);
//        }
//       System.out.println("EntityConfig, getAssociatedPrimaryKey: entityClasses "+this.entityClasses);
//        pk = entityClasses.getEntityPrimaryKey(clazz).getName();
        for(EntityConfig ecd: entitiesList){
                      
            if(clazz.getCanonicalName().compareTo(ecd.getClazz().getCanonicalName()) == 0){
                pk =  ecd.getPrimaryKey().getName();
//                System.out.println("EntityConfig, getAssociatedPrimaryKey: "+ pk+" "+ecd.getClazz().getCanonicalName()+" "+clazz.getCanonicalName());
            }
        }
        
        return pk;
    }

    private String getForeignKey(Field field) {
        String fk = "";

        Type genericFieldType = field.getGenericType();

        if (genericFieldType instanceof ParameterizedType) {

            ParameterizedType aType = (ParameterizedType) genericFieldType;
            Type[] fieldArgTypes = aType.getActualTypeArguments();

            for (Type fieldArgType : fieldArgTypes) {
                Class fieldArgClass = (Class) fieldArgType;
                System.out.println("fieldArgClass = " + fieldArgClass);
                fk = fieldArgClass.getSimpleName().toLowerCase();
            }

        } else {
            fk = field.getType().getSimpleName().toLowerCase();
        }

        return fk;
    }
}
