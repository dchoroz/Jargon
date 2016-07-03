package helper;

import java.sql.Timestamp;
import java.util.List;
import org.atmosphere.cpr.AtmosphereResource;
import org.codehaus.jackson.map.annotate.JsonDeserialize;
import org.codehaus.jackson.map.annotate.JsonSerialize;

/**
 *
 * Message structure:
 * 
 * {
 * success: boolean,
 * time: Timestamp,
 * target: String target method for data
 * transaction: boolean,
 * operations :{action: enum RestAction,
 *              type: Java Class of data,
 *              operationId: String,
 *              data: Array
 *              }
 * }
 */

@JsonDeserialize(using = ExtMessageDeserializer.class)
@JsonSerialize(using = ExtMessageSerializer.class)
public class ExtMessage {

    private boolean success;
    private Timestamp time;
    private String messageId;
    private boolean transaction;
    private AtmosphereResource resource;
    private List<Operation> operations;

    public ExtMessage() {
    }

    public ExtMessage(boolean success, Timestamp time, String messageId, boolean transaction, List<Operation> operations) {
        this.success = success;
        this.time = time;
        this.messageId = messageId;
        this.transaction = transaction;
        this.operations = operations;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public Timestamp getTime() {
        return time;
    }

    public void setTime(Timestamp time) {
        this.time = time;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public boolean isTransaction() {
        return transaction;
    }

    public void setTransaction(boolean transaction) {
        this.transaction = transaction;
    }

    public List<Operation> getOperations() {
        return operations;
    }

    public void setOperations(List<Operation> operations) {
        this.operations = operations;
    }

    public void contains(Object entity){
        for(Operation operation: this.operations){
            for(Object record: operation.getData()){

            }
        }
    }

    public AtmosphereResource getResource() {
        return resource;
    }

    public void setResource(AtmosphereResource resource) {
        this.resource = resource;
    }


    
    private boolean equals(){
        
        
        
        return false;
    }
}
