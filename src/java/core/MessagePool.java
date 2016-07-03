package core;

import config.EntityConfig;
import config.InitialConfig;
import helper.ExtMessage;
import helper.Operation;
import helper.RestAction;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;

@Singleton
@Startup
/*@Remote annotation is needed because MessagePool can be accessed through entities packaged in remote clients*/
public class MessagePool {

    /**
     * java.util.List ios not thread-safe. Check LinkedBlockingDeque, ConcurrentLinkedQueue
     */
    @EJB InitialConfig ec;
    @EJB MessageManager mm;
   
    Map<Object, String> entityToMessageId;
    Map<String, ExtMessage> MessageIdToMessage;
//    Map<String, Map<String, Operation>> MessageIdToOperations;
    Map<String, ExtMessage> MessageIdToMessageRead;

    public MessagePool() {
        entityToMessageId = new ConcurrentHashMap<Object, String>();
        MessageIdToMessage = new ConcurrentHashMap<String, ExtMessage>();
//        MessageIdToOperations = new ConcurrentHashMap<String, Map<String, Operation>>();
        MessageIdToMessageRead = new ConcurrentHashMap<String, ExtMessage>();
    }

    /*
    */
    public void addMessage(ExtMessage request) {

        String messageId = request.getMessageId();
                            

        for (Operation op : request.getOperations()) {
                EntityConfig ecd = ec.getEntityClassData(op.getType().getCanonicalName());
                
                for (Object record : op.getData()) {
//                    System.out.println("MessagePool addMessage, addinng entity: "+record);
                    this.entityToMessageId.put(record, messageId);
                    Map<Field, Annotation> ass = ecd.getAssociationFields();
                    for(Field f : ass.keySet()){
                        f.setAccessible(true);
                        try {
                            System.out.println(f.get(record));
                            if(ass.get(f) instanceof OneToOne || ass.get(f) instanceof ManyToOne){
                                this.entityToMessageId.put(f.get(record), messageId);
                            }else{
                                List assList = (List) f.get(record);
                                for(Object assObject : assList){
                                    this.entityToMessageId.put(assObject, messageId);    
                                }
                            }    
                        } catch (IllegalAccessException ex) {
                            Logger.getLogger(MessagePool.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }   
                }        
        }

        this.MessageIdToMessage.put(messageId, request);
    }

    public void notifyAll(Object entity, Class type, RestAction action) {

        System.out.println("MessagePool notifyAll, messages: " + entity.getClass().getCanonicalName());
        
        if (type == null) {
            System.out.println("MessagePool notifyAll, This is not an entity class");
            return;
        }

        this.searchMessages(entity, type, action);
    }

    public ExtMessage completed(String messageId, List readData){
        
        ExtMessage message = this.MessageIdToMessage.remove(messageId);

        if(readData != null){
            message.getOperations().get(0).getData().clear();
            message.getOperations().get(0).getData().addAll(readData);
        }
        
        return message; 
    }
    
    private boolean searchMessages(Object entity, Class type, RestAction action) {

//        String entityId = this.getFrameId(entity);
        String messageId = null;
        if(action.equals(RestAction.create)){
            messageId = this.entityToMessageId.remove(entity);
        }else{
            for(Object o : this.entityToMessageId.keySet()){
                if(o.getClass().equals(type)){
                    
                    if(this.compareRecordIds(o, entity)){
                        messageId = this.entityToMessageId.remove(o);
                    }                
                }
            }
        }
        
        if(messageId == null){
            System.out.println("MessagePool searchMessages, there is no entry in entityTomessageId for "+entity+" "+entityToMessageId.keySet().toArray()[0]);
            mm.createMessage(entity, type, action);
            return false;
        }
        else{
            System.out.println("MessagePool searchMessages, entity already contained in a message");
        }
        
        return false;
    }

    
    private boolean compareRecordIds(Object record, Object entity){
        
        Field recordField = ec.getEntityPrimaryKey(record.getClass());
        Field entityField = ec.getEntityPrimaryKey(entity.getClass());
        
        Object recordId = null;
        Object entityId = null;
        try {
            recordField.setAccessible(true);
            entityField.setAccessible(true);
            recordId = recordField.get(record);
            entityId = entityField.get(entity);
        } catch (IllegalArgumentException ex) {
            Logger.getLogger(MessagePool.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            Logger.getLogger(MessagePool.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        if((recordId.equals(entityId)) && (recordId != null) && (entityId != null)){
            return true;
        }
        
        return false;
    }
    
//    private String getFrameId(Object entity) {
//
//        String internalId = null;
//        Method method = null;
//
//        try {
//            method = entity.getClass().getMethod("getFrameId");
//            internalId = (String) method.invoke(entity);
//        } catch (NoSuchMethodException ex) {
//            Logger.getLogger(MessagePool.class.getName()).log(Level.SEVERE, null, ex);
//        } catch (SecurityException ex) {
//            Logger.getLogger(MessagePool.class.getName()).log(Level.SEVERE, null, ex);
//        } catch (IllegalAccessException ex) {
//            Logger.getLogger(MessagePool.class.getName()).log(Level.SEVERE, null, ex);
//        } catch (IllegalArgumentException ex) {
//            Logger.getLogger(MessagePool.class.getName()).log(Level.SEVERE, null, ex);
//        } catch (InvocationTargetException ex) {
//            Logger.getLogger(MessagePool.class.getName()).log(Level.SEVERE, null, ex);
//        }
//
//        return internalId;
//    }
}
 /**Attempt to make a STRUCTURE that receives data from the crud bean and not the listeners.
     * Failed because there is conflicts with actions coming from outside the framework.
     */

//    Map<String, String> entityToMessageId;
//    
//    public MessagePool() {
//        entityToMessageId = new ConcurrentHashMap<String, String>();
//    }
//    
//    public String addMessage(ExtMessage request) {
//        String messageId = request.getMessageId();
//        String frameId;
//        
//        for (Operation op : request.getOperations()) {
//            if(op.getAction() != RestAction.read){
//                for (Object record : op.getData()) {
//                    frameId = this.getFrameId(record);
//                    this.entityToMessageId.put(frameId, messageId);
//                }
//            }
//        }
//        
//        return null;
//    }
//    
//    public void notifyAll(Object entity, Class type, RestAction action) {
//                System.out.println("MessagePool notify messages: " + entity.getClass().getCanonicalName());
//        if (type == null) {
//            System.out.println("This is not an entity class");
//            return;
//        }else if(type.getCanonicalName().compareTo("basic.Person") == 0){
//            Person p = (Person) entity;
////            System.out.println("PERSON PHONENUMBERS "+p.getPhoneNumbers().size());
//
//        }
//    }


/**searchMessages comparing listener's returnedDataEntities to message records through frameId. **/
//    private boolean searchMessages(Object entity, Class type, RestAction action) {
//
//        String entityId = this.getFrameId(entity);
//        String messageId = this.entityToMessageId.get(entityId);
//        if(messageId == null){
//            System.out.println("entity "+entity+" does not belong to any messsage");
//            mm.createMessage(entity, type, action);
//            return false;
//        }
//        ExtMessage message = this.MessageIdToMessage.get(messageId);
//
//        /*Having received an entity I check the entityToMessageId to get the messages containing this entity (by frameId).
//         *Then I iterate through the operations of the message to find the entity record. When a match of type is found with
//         * an operation, I iterate through its records to find the entity. If the record is found I get the Operations of the message to be sent
//         * through MessageIdToOperations and add it to the corresponding operation.
//         * I remove the record from the original message and the entityToMessageId record
//         * (What if the entity is contained more tan once in the message? Could it be such case?).
//         * 
//         */
//       
//        for (Iterator< Operation> it = message.getOperations().iterator(); it.hasNext();) {
//            Operation operation = it.next();
//            if (operation.getType().equals(type)) {
//                System.out.println("TYPE MATCH FOUND");
//                for (Iterator< Object> it1 = operation.getData().iterator(); it1.hasNext();) {
//                    Object record = it1.next();
//                    System.out.println("recordId "+this.getFrameId(record)+" entityId "+entityId);
//                    if (this.getFrameId(record).equals(entityId)) {
//                        if(record == entity){
//                            Person p = (Person)record;
//                            System.out.println("NAI RE POUSTHHHHH"+p.getId());
//                        }
//                        System.out.println("Match found --------"
//                                + " internalId: " + this.getFrameId(record)
//                                + " record Class: " + operation.getType().getCanonicalName());
//
//                        Map<String, Operation> operations = this.MessageIdToOperations.get(messageId);
//
//                        Operation doneOperation;
//
//                        if (!operations.containsKey(operation.getOperationId())) {
//                            doneOperation = new Operation(operation.getAction(), operation.getOperationId(), operation.getType(), operation.getTarget(), new ArrayList());
//                        } else {
//                            doneOperation = operations.get(operation.getOperationId());
//                        }
//
//                        doneOperation.getData().add(entity);
//                        operations.put(operation.getOperationId(), doneOperation);
//
//                        it1.remove();
//                        this.entityToMessageId.remove(entityId);
//                        
//                        if (operation.getData().isEmpty()) {
//                            it.remove();
//                            if (message.getOperations().isEmpty()) {
//                                System.out.println("Message: " + message.getMessageId() + " is ready to be sent to the clients");
//                                for(String operationId : operations.keySet()){
//                                    message.getOperations().add(operations.get(operationId));
//                                }
////                                this.MessageIdToMessage.remove(messageId);
//                                
////                                mm.broadcastMessage(message); 
//                            }
//                        }
//                    }
//
//                    return true;
//                }
//            }
//        }
//
//        return false;
//    }

/**Working example with comparing objects rather than frameIds. */
// public MessagePool() {
//        entityToMessageId = new ConcurrentHashMap<Object, String>();
//        MessageIdToMessage = new ConcurrentHashMap<String, ExtMessage>();
//        MessageIdToOperations = new ConcurrentHashMap<String, Map<String, Operation>>();
//        MessageIdToMessageRead = new ConcurrentHashMap<String, ExtMessage>();
//    }
//
//    /*Returns null or the entities frameId that already is being changed by another client.
//     *The return value will be used to build a response to the client that the action failed to commit. 
//    */
//    public Object addMessage(ExtMessage request) {
//
//        String messageId = request.getMessageId();
//        String recordId;
//        
//        for (Operation op : request.getOperations()) {
//            if(op.getAction() == RestAction.read){
//                MessageIdToMessageRead.put(messageId, request);
//            }else{
//                for (Object record : op.getData()) {
////                    recordId = this.getFrameId(record);
//                    EntityWrapper ew = (EntityWrapper) record;
//                    System.out.println("MessagePool addMessage, addinng entity: "+ew.getEntity());
//                    if(this.entityToMessageId.containsKey(ew.getEntity())){ 
//                        return record;
//                    }
//                    this.entityToMessageId.put(ew.getEntity(), messageId);
//                }
//            }
//        }
//
//        this.MessageIdToMessage.put(messageId, request);
//        this.MessageIdToOperations.put(messageId, new ConcurrentHashMap<String, Operation>());
//        
//        return null;
//    }
//
//    public void notifyAll(Object entity, Class type, RestAction action) {
//
//        System.out.println("MessagePool notifyAll, messages: " + entity.getClass().getCanonicalName());
//        
//        if (type == null) {
//            System.out.println("MessagePool notifyAll, This is not an entity class");
//            return;
//        }
////        else if(type.getCanonicalName().compareTo("basic.Person") == 0){
////            Person p = (Person) entity;
//////            System.out.println("PERSON PHONENUMBERS "+p.getPhoneNumbers().size());
////        }
//        this.searchMessages(entity, type, action);
//
//    }
//
//    public void notifyReadComplete(String messageId, List returnedData){
//        
//        ExtMessage message = this.MessageIdToMessage.get(messageId);
//        System.out.println("MessagePool notifyReadComplete, fetched data size: "+returnedData.size());
//        List messagedata = message.getOperations().get(0).getData();
//        mm.broadcastMessage(message); 
//
//    }
//    
//    private boolean searchMessages(Object entity, Class type, RestAction action) {
//
////        String entityId = this.getFrameId(entity);
//        String messageId = this.entityToMessageId.get(entity);
//        
//        if(messageId == null){
//            System.out.println("MessagePool searchMessages, there is no entry in entityTomessageId for "+entity+" "+entityToMessageId.keySet().toArray()[0]);
//            mm.createMessage(entity, type, action);
//            return false;
//        }
//        
//        ExtMessage message = this.MessageIdToMessage.get(messageId);
//
//        /*Having received an entity I check the entityToMessageId to get the messages containing this entity (by frameId).
//         *Then I iterate through the operations of the message to find the entity record. When a match of type is found with
//         * an operation, I iterate through its records to find the entity. If the record is found I get the Operations of the message to be sent
//         * through MessageIdToOperations and add it to the corresponding operation.
//         * I remove the record from the original message and the entityToMessageId record
//         * (What if the entity is contained more tan once in the message? Could it be such case?).
//         * 
//         */
//       
//        for (Iterator< Operation> it = message.getOperations().iterator(); it.hasNext();) {
//            Operation operation = it.next();
//            if (operation.getType().equals(type)) {
//                System.out.println("MessagePool searchMessages, TYPE MATCH FOUND");
//                for (Iterator< Object> it1 = operation.getData().iterator(); it1.hasNext();) {
//                    EntityWrapper record = (EntityWrapper) it1.next();
////                    System.out.println("recordId "+this.getFrameId(record)+" entityId "+entityId);
//                    System.out.println("MessagePool searchMessages, COMPARING OBJECTS "+record.getEntity() +" "+entity);
//                    if (record.getEntity() == entity) {
//                        if(record.getEntity() == entity){                            
//                            System.out.println("MessagePool searchMessages, COMPARING OBJECTS WORKS ");
//                        }
//                        
//                        System.out.println("MessagePool searchMessages, Match found --------"
//                                + " record Class: " + operation.getType().getCanonicalName());
//
//                        Map<String, Operation> operations = this.MessageIdToOperations.get(messageId);
//
//                        Operation doneOperation;
//
//                        if (!operations.containsKey(operation.getOperationId())) {
//                            doneOperation = new Operation(operation.getAction(), operation.getOperationId(), operation.getType(), operation.getTarget(), new ArrayList());
//                        } else {
//                            doneOperation = operations.get(operation.getOperationId());
//                        }
//
//                        doneOperation.getData().add(record);
//                        operations.put(operation.getOperationId(), doneOperation);
//
//                        it1.remove();
//                        this.entityToMessageId.remove(entity);
//                        
//                        if (operation.getData().isEmpty()) {
//                            it.remove();
//                            if (message.getOperations().isEmpty()) {
//                                System.out.println("MessagePool searchMessages, Message: " + message.getMessageId() + " is ready to be sent to the clients");
//                                for(String operationId : operations.keySet()){
//                                    message.getOperations().add(operations.get(operationId));
//                                }
////                                this.MessageIdToMessage.remove(messageId);
//                                
//                                mm.broadcastMessage(message); 
//                            }
//                        }
//                    }
//
//                    return true;
//                }
//            }
//        }
//
//        return false;
//    }