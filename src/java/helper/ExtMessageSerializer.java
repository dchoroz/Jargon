package helper;

import config.EntityConfig;
import config.InitialConfig;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Stateless;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.JsonSerializer;
import org.codehaus.jackson.map.SerializerProvider;

@Stateless
public class ExtMessageSerializer extends JsonSerializer<ExtMessage> {

    @Override
    public void serialize(ExtMessage t, JsonGenerator jg, SerializerProvider sp) throws IOException, JsonProcessingException {
        jg.writeStartObject();
        jg.writeObjectField("success", true);
        jg.writeObjectField("time", t.getTime()); 
        jg.writeObjectField("messageId", t.getMessageId());
        if(t.getResource() != null){
            jg.writeObjectField("uuid",t.getResource().uuid());
        }else{
            jg.writeObjectField("uuid","-1");
        }
        jg.writeObjectField("transaction", t.isTransaction());
        jg.writeArrayFieldStart("operations");
        
        for(Operation operation : t.getOperations()){
            jg.writeStartObject();
            jg.writeObjectField("action", operation.getAction());
            jg.writeObjectField("operationId", operation.getOperationId());
            jg.writeObjectField("type", operation.getType().getCanonicalName().replace(".","-"));
            jg.writeObjectField("target", operation.getTarget());
            jg.writeArrayFieldStart("data");
            EntityConfig ecd = this.getEntityConfig(operation.getType().getCanonicalName());
            for(Object o : operation.getData()){
//                jg.writeStartObject();
//                List<Field> fields = ecd.getFields();
//                System.out.println("fields List size "+fields.size());
//                Map<Field, Annotation> assocFields = ecd.getAssociationFields();
//                for(Field f : fields){
//                    try {
//                        Object object = f.get(operation.getType().cast(o));
//                        jg.writeObject(object);
//                    } catch (IllegalArgumentException ex) {
//                        Logger.getLogger(ExtMessageSerializer.class.getName()).log(Level.SEVERE, null, ex);
//                    } catch (IllegalAccessException ex) {
//                        Logger.getLogger(ExtMessageSerializer.class.getName()).log(Level.SEVERE, null, ex);
//                    }
//                }
            //    operation.getType().cast(o);
//                jg.writeEndObject();
                  jg.writeObject(o);
            }

            jg.writeEndArray();
            jg.writeEndObject();
        }
        jg.writeEndArray();
        jg.writeEndObject();
        
        System.out.println(jg.toString());
    }
    
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
//
//{"success":false,
//"time":1394739489352,
//"messageId":"1c8558b9-b521-48d7-bad5-b13fb4800a3d",
//"transaction":false,
//"target":"basic.SimpleCrud.crudData",
//"operations":[{"action":"create",
//"operationId":"ext-gen1099",
//"type":"basic-Person",
//"data":[{"id":0,
//"lname":"yfy",
//"fname":"ff",
//"phoneNumbers":"",
//"internalId":"ext-record-1"}
//]}
//]}
