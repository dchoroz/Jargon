package helper;

import java.io.IOException;
import javax.ejb.Stateless;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.JsonSerializer;
import org.codehaus.jackson.map.SerializerProvider;

//Not in use.
@Stateless
public class OperationSerializer extends JsonSerializer<Operation>{

    @Override
    public void serialize(Operation t, JsonGenerator jg, SerializerProvider sp) throws IOException, JsonProcessingException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
