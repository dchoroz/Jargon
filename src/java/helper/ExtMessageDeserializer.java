package helper;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Stateless;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.ObjectCodec;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.DeserializationContext;
import org.codehaus.jackson.map.JsonDeserializer;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.JavaType;

@Stateless
public class ExtMessageDeserializer extends JsonDeserializer<ExtMessage> {

    
    @Override
    public ExtMessage deserialize(JsonParser jp, DeserializationContext dc) {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        ObjectCodec oc = jp.getCodec();
        JsonNode node = null;
       
        try {
            node = oc.readTree(jp);
        } catch (IOException ex) {
            Logger.getLogger(ExtMessageDeserializer.class.getName()).log(Level.SEVERE, null, ex);
        }

        /**no need to parse success. It gains meaning only at the response**/
//        JsonNode successAttr = node.get("success");
//        boolean success = mapper.readValue(successAttr, Boolean.class);
        JsonNode timeNode = node.get("time");
        Timestamp time = null;
        try {
            time = mapper.readValue(timeNode, Timestamp.class);
        } catch (IOException ex) {
            Logger.getLogger(ExtMessageDeserializer.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        String messageId = node.get("messageId").getTextValue();
        
        JsonNode transactionNode = node.get("transaction");
        boolean transaction = false;
        try {
            transaction = mapper.readValue(transactionNode, Boolean.class);
        } catch (IOException ex) {
            Logger.getLogger(ExtMessageDeserializer.class.getName()).log(Level.SEVERE, null, ex);
        }
        JsonNode operationsNode = node.get("operations");
        JavaType listOperations = mapper.getTypeFactory().constructCollectionType(List.class, Operation.class);
        List operations = null;
        try {
            mapper.setDeserializationConfig(dc.getConfig());
            operations = mapper.readValue(operationsNode, listOperations);
        } catch (IOException ex) {
            Logger.getLogger(ExtMessageDeserializer.class.getName()).log(Level.SEVERE, null, ex);
        }

        ExtMessage message = new ExtMessage();

        message.setSuccess(true);
        message.setTime(time);
        message.setMessageId(messageId);
        message.setTransaction(transaction);
        message.setOperations(operations);

        return message;
    }

    private ExtMessage lookupMessageBean() {
        try {
            Context c = new InitialContext();
            return (ExtMessage) c.lookup("java:global/TestApp/ExtMessage!helper.ExtMessage");
        } catch (NamingException ne) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, "exception caught", ne);
            throw new RuntimeException(ne);
        }
    }
}