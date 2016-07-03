package helper;

import java.util.List;
import org.codehaus.jackson.map.annotate.JsonDeserialize;
import org.codehaus.jackson.map.annotate.JsonSerialize;

@JsonDeserialize(using = OperationDeserializer.class)
@JsonSerialize(using = OperationSerializer.class)
public class Operation {
    
    private RestAction action;
    private String operationId;
    private Class<?> type;
    private String target;
    private List data;
//    private List associatedData;

    public Operation() {}
    
    public Operation(RestAction action, String operationId, Class<?> type, String target, List<Object> data) {
        this.action = action;
        this.operationId = operationId;
        this.type = type;
        this.target = target;
        this.data = data;
    }
    
    public RestAction getAction() {
        return action;
    }

    public void setAction(RestAction action) {
        this.action = action;
    }

    public String getOperationId() {
        return operationId;
    }

    public void setOperationId(String operationId) {
        this.operationId = operationId;
    }

    public Class<?> getType() {
        return type;
    }

    public void setType(Class<?> type) {
        this.type = type;
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public List<Object> getData() {
        return data;
    }

    public void setData(List<Object> data) {
        this.data = data;
    }

//    public List getAssociatedIds() {
//        return associatedData;
//    }
//
//    public void setAssociatedIds(List associatedIds) {
//        this.associatedData = associatedIds;
//    }   
    
}
