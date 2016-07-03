package helper;

import config.EntityConfig;
import config.InitialConfig;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Stateless;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.ObjectCodec;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.DeserializationContext;
import org.codehaus.jackson.map.JsonDeserializer;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.JavaType;

@Stateless
public class OperationDeserializer extends JsonDeserializer<Operation>{
        
    @Override
    public Operation deserialize(JsonParser jp, DeserializationContext dc) throws IOException, JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        List associatedIDs = null;
        mapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        ObjectCodec oc = jp.getCodec();
        JsonNode node = oc.readTree(jp);
        JsonNode actionNode = node.get("action");
        RestAction action = mapper.readValue(actionNode, RestAction.class);
        String operationId = node.get("operationId").getTextValue();
        String target = node.get("target").getTextValue();
        String type = node.get("type").getTextValue();
        Class<?> clazz = null;
        
        try {
            clazz = Class.forName(type.replaceAll("-", "."));
        } catch (ClassNotFoundException ex) {
            
            Logger.getLogger(OperationDeserializer.class.getName()).log(Level.SEVERE, null, ex);
        }
//        JavaType entityWrapperClass = mapper.getTypeFactory().constructParametricType(EntityWrapper.class, clazz);
        List data = new ArrayList();
        JsonNode list = node.get("data");
        
        if(action == RestAction.read){
            //if the action is a READ action then data contains the filters set by the store.
            JavaType listType = mapper.getTypeFactory().constructCollectionType(List.class, StoreFilter.class);
            data = mapper.readValue(list, listType);
//        }else if(action == RestAction.create){
//            
//            EntityConfig entityConfig = this.getEntityConfig(type.replaceAll("-", "."));
//            
//            Map<Field, Annotation> associationFields = entityConfig.getAssociationFields() ;
//            associatedIDs = new ArrayList(); 
//            
////            if(associationFields.isEmpty()){
//                System.out.println("OperationDeserializer, deserialize: "+associationFields.size());
//                JavaType listType = mapper.getTypeFactory().constructCollectionType(List.class, clazz);
//                data = mapper.readValue(list, listType);
////            }else{
//                if(((Person)data.get(0)).getPhoneNumbers().size()>0){
//                    System.out.println(((Person)data.get(0)).getPhoneNumbers().get(0));
//                }
//                
////                List<JsonNode> dataJsonNodes = mapper.readValue(list, mapper.getTypeFactory().constructCollectionType(List.class, JsonNode.class));
////                System.out.println("NODE "+dataJsonNodes.size()+" "+list.isArray()+" "+ dataJsonNodes.get(0));
////                for(JsonNode jn : dataJsonNodes){
////                    data.add(mapper.readValue(jn, clazz));
////                    for(Field f : associationFields.keySet()){
////                        if(associationFields.get(f) instanceof ManyToOne){
////                            associatedIDs.add(jn.get(f.getName()));
////                            System.out.println("ID OF ASSOCIATED: "+jn.get(f.getName()));
////                        }
////                    }  
////                }
////            }
        }else{
            JavaType listType = mapper.getTypeFactory().constructCollectionType(List.class, clazz);
            data = mapper.readValue(list, listType);
        }

        Operation operation = new Operation();

        operation.setAction(action);
        operation.setOperationId(operationId);
        operation.setType(clazz);
        operation.setTarget(target);
        operation.setData(data);
//        operation.setAssociatedIds(associatedIDs);
        
        return operation;
    }

    private Operation getOperationBean() {
        try {
            Context c = new InitialContext();
            return (Operation) c.lookup("java:global/TestApp/Operation!helper.Operation");
        } catch (NamingException ne) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, "exception caught", ne);
            throw new RuntimeException(ne);
        }
    }
    
    //Not needed (used to get entityconfig for associated data)
    private EntityConfig getEntityConfig(String className){
        
        InitialConfig ec = this.getEntityClassesBean();
        return ec.getEntityClassData(className);
        
    }
    
    private InitialConfig getEntityClassesBean() {
        
        try {
            Context c = new InitialContext();
            return (InitialConfig) c.lookup("java:global/TestApp/InitialConfig!config.InitialConfig");
        } catch (NamingException ne) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, "exception caught", ne);
            throw new RuntimeException(ne);
        }
    }
}
