package core;

import config.InitialConfig;
import helper.ExtMessage;
import helper.Operation;
import helper.RestAction;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import org.atmosphere.cpr.AtmosphereResource;
import org.atmosphere.cpr.Broadcaster;
import org.atmosphere.cpr.BroadcasterFactory;
import org.codehaus.jackson.map.ObjectMapper;

@Stateless
public class MessageManager {

    @EJB
    InitialConfig ec;
    @EJB
    MessagePool mp;

    public ExtMessage parseRequest(String request, AtmosphereResource resource) {

        ObjectMapper mapper = new ObjectMapper();

//        mapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        ExtMessage rMessage = null;
        
        try {
            rMessage = mapper.readValue(request, ExtMessage.class);
        } catch (IOException ex) {
            Logger.getLogger(MessageManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        rMessage.setResource(resource);
        mp.addMessage(rMessage);
        this.addBroadcasters(rMessage.getOperations(), resource);

        return rMessage;
        
        
    }

    public void broadcastMessage(ExtMessage message) {

        
        System.out.println("Message operations size: " + message.getOperations().size());
        ObjectMapper mapper = new ObjectMapper();
        String rMessage = "failed";
        try {
            rMessage = mapper.writeValueAsString(message);
        } catch (IOException ex) {
            Logger.getLogger(MessageManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        if(message.getOperations().size() == 1) {
            Operation operation = message.getOperations().get(0);
            if(operation.getAction().equals(RestAction.read)){
                message.getResource().write(rMessage);
                return;
            }
        }
        
        /**Supporting messages containing multiple operations from different Entities. 
         * Collecting broadcasters that should broadcast the current message.
         * If the message needs to be sent through more than one broadcaster, AtmosphereResources that
         * belong to more than one broadcaster get the same message more than one time.
         * So we collect the AtmosphereResources from all the broadcasters and then write the message to each one of them.
         * This may cause problems to Filters that i may need.
        **/
        Set<Broadcaster> broads = new HashSet<Broadcaster>();
        
        for(Operation op : message.getOperations()){
            if(op.getAction().equals(RestAction.read)){
                message.getResource().write(rMessage);
            }else{                
                broads.add(ec.getBroadcasters().get(op.getType().getCanonicalName()));
            }
        }
       
        if(broads.size() > 1){
            Set<AtmosphereResource> resources = new HashSet<AtmosphereResource>();
            for(Broadcaster b : broads){
//                System.out.println("MessageManager broadcastMessage: BROADCASTING to " + b.getAtmosphereResources().size() + " clients. "+b);
                resources.addAll(b.getAtmosphereResources());
            }
            for(AtmosphereResource ar : resources){
                ar.write(rMessage);
            }
        }else{
            for(Broadcaster b : broads){
                System.out.println("MessageManager broadcastMessage: BROADCASTING to " + b.getAtmosphereResources().size() + " clients. "+b);
                b.broadcast(rMessage);
            }
        }
           
        
        

    }

    public void createMessage(Object entity, Class type, RestAction action){
        List data = new ArrayList();
        List operations = new ArrayList();
        Broadcaster b;
        
        data.add(entity);
        
        Operation operation = new Operation(action, "-1", type, "null", data);
        operations.add(operation);
        
        ExtMessage message = new ExtMessage(true, null, "-1", false, operations);
        ObjectMapper mapper = new ObjectMapper();
        String rMessage = "failed";
        try {
            rMessage = mapper.writeValueAsString(message);
        } catch (IOException ex) {
            Logger.getLogger(MessageManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        System.out.println("MessageManager createMessage: Entity Type " +type+ " clients "+ type.getCanonicalName());
        b = ec.getBroadcasters().get(type.getCanonicalName());
        if(b != null){
            System.out.println("BROADCASTING to " + b.getAtmosphereResources().size() + " clients.");
            b.broadcast(rMessage);
        }
    }
    
//    public void broadcastPersist(Object object, Class<?> type) {
//        java.util.Date date = new java.util.Date();
//        List data = new ArrayList();
//        data.add(object);
//
//        ExtMessage message = new ExtMessage();
//        Broadcaster b = ec.getBroadcasters().get(type.getCanonicalName());
//        System.out.println("BROADCASTING to " + b.getAtmosphereResources().size() + " clients."+b.getID());
//        b.broadcast(message);
//    }
//
//    public void broadcastUpdate(Object object, Class<?> type) {
//        java.util.Date date = new java.util.Date();
//        List data = new ArrayList();
//        data.add(object);
//
//        ExtMessage message = new ExtMessage();
//        Broadcaster b = ec.getBroadcasters().get(type.getCanonicalName());
//        b.broadcast(message);
//    }
//
//    public void broadcastRemove(Object object, Class<?> type) {
//        java.util.Date date = new java.util.Date();
//        List data = new ArrayList();
//        data.add(object);
//
//        ExtMessage message = new ExtMessage();
//        Broadcaster b = ec.getBroadcasters().get(type.getCanonicalName());
//        b.broadcast(message);
//    }

//    private String getSimpleClassName(String name) {
//        int lastDot = name.lastIndexOf('.');
//        if (lastDot >= 0) {
//            name = name.substring(lastDot + 1);
//        }
//
//        return name;
//    }

    private void addBroadcasters(List<Operation> operations, AtmosphereResource resource) {
        
        String type;
        for (Operation operation : operations) {
            Broadcaster b;
            type = operation.getType().getCanonicalName();

            if (!ec.getBroadcasters().containsKey(type)) {
                System.out.println("MessageManager addBroadcasters: creating broadcaster for "+type);
                b = BroadcasterFactory.getDefault().get(type);
                ec.getBroadcasters().put(type, b);
                b.addAtmosphereResource(resource);

            } else {
                b = ec.getBroadcasters().get(type);
                b.addAtmosphereResource(resource);
            }     
        }
    }
    
    /*If a record is already being changed by another client, MessagePool returns the frameId in the failed variable.
     This function sets the success property of the messasge to false and returns the failed record frameId*/
    private ExtMessage returnFailed(Object failed, ExtMessage rMessage){
        
        rMessage.setOperations(null);
        rMessage.setSuccess(false);
        
        return rMessage;
    }
}
