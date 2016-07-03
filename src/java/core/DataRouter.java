package core;

import helper.ExtMessage;
import helper.Operation;
import helper.RestAction;
import static helper.RestAction.create;
import static helper.RestAction.destroy;
import static helper.RestAction.read;
import static helper.RestAction.update;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.ejb.Stateless;

@Stateless
public class DataRouter {

    private static final Logger logger = Logger.getLogger(DataRouter.class.getName());
   
    @EJB
    SimpleCrud simpleCrud;
//    @EJB
//    TransactionCrud transactionCrud;
    @EJB
    MessagePool messagePool;
    @EJB
    MessageManager mm;

    public void processRequest(ExtMessage rMessage) {
        
        List resultObject = null;
        
        if (rMessage.isTransaction()) {
            /**if currently not needed because the container starts a transaction for each operation. 
            * With a container-managed entity manager, an EntityManager instance’s persistence context 
            * is automatically propagated by the container to all application components that use 
            * the EntityManager instance within a single Java Transaction API (JTA) transaction.
            * When crudData calls a method of SimpleCrud then am EntityManager is given to the 
            * bean. for each entityMannager a single transaction takes place. Also the annotation 
            * REQUIRED is default for the transactions so no need for annotation either.
            */           
            for (Operation operation : rMessage.getOperations()) {
                try{    
                    resultObject = this.crudData(operation);
                } catch (InterruptedException ex) {
                    Logger.getLogger(SimpleCrud.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        } else {
            for (Operation operation : rMessage.getOperations()) {
                try{
                    resultObject = this.crudData(operation);
                } catch (InterruptedException ex) {
                    Logger.getLogger(SimpleCrud.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }

        ExtMessage responseMessage = messagePool.completed(rMessage.getMessageId(), resultObject);
        mm.broadcastMessage(rMessage);
    }

    public List crudData(Operation operation) throws InterruptedException{

        RestAction action = operation.getAction();
        List data = operation.getData();

        Class<?> type = operation.getType();

        List returnedData;
        switch (action) {
            case read:
                returnedData = this.listData(data, type);
//                System.out.println("simpleCrud " + returnedData.size());
                return returnedData;
            case create:
                this.createData(data, type);
                break;
            case update:
                this.updateData(data, type);
                break;
            case destroy:
                this.deleteData(data, type);
                break;
            default:
                System.out.println(action + ": no such CRUD action");
        }

        return null;
    }

    public List listData(List data, Class type) throws InterruptedException {
        return simpleCrud.listData(data, type);
    }
    
    public List createData(List data, Class type) throws InterruptedException {

//        ObjectMapper mapper = new ObjectMapper();
//        person = dataManager.find(id);
//        System.out.print(person.getId());
//        System.out.println(d.length);

        for (int i = 0; i < data.size(); i++) {
            data.set(i, simpleCrud.create(data.get(i), type));
        }

        System.out.println("createData: " + type.getSimpleName() + " persisted");
        return data;
    }

    public void updateData(List data, Class type) {

        for (int i = 0; i < data.size(); i++) {
            System.out.println(data.get(i).getClass().getSimpleName() + " " + type.getSimpleName());
            simpleCrud.edit(data.get(i), type);
        }


//        Object entity = dataManager.find(id, type);
//        System.out.print(person.getId());

//        person.setFname(cfname);
//        person.setLname(clname);
//        dataManager.edit(person);
//        System.out.println("updatePerson");
    }

    public void deleteData(List data, Class type) {

        for (int i = 0; i < data.size(); i++) {
            System.out.println(data.get(i).getClass().getSimpleName() + " " + type.getSimpleName());
            simpleCrud.remove(data.get(i), type);
        }
//        System.out.println("deletePerson");

    }
//    
    /*Find the Bean and the specific method that is the target of the data on this request.*/
//    public void processRequest(ExtMessage rMessage) throws NamingException, JsonMappingException, JsonParseException, IOException{
//        List data = null;
//        ObjectMapper mapper = new ObjectMapper();
//        java.util.Date date = new java.util.Date();
//        for(Operation operation: rMessage.getOperations()){
//            System.out.println("Method String " + operation.getTarget() );
//
//            Object bean = this.getBean(operation.getTarget().substring(0, operation.getTarget().lastIndexOf(".")));
//        
//            Method target = this.getMethod(operation.getTarget());
//            List resultObject = null;
//            try {
//               resultObject = (List) target.invoke(bean, operation);
//    //        data = dataManager.crudData(rMessage.getAction(), rMessage.getData(), rMessage.getType());
//            } catch (IllegalAccessException ex) {
//                Logger.getLogger(DataRouter.class.getName()).log(Level.SEVERE, null, ex);
//            } catch (IllegalArgumentException ex) {
//                Logger.getLogger(DataRouter.class.getName()).log(Level.SEVERE, null, ex);
//            } catch (InvocationTargetException ex) {
//                Logger.getLogger(DataRouter.class.getName()).log(Level.SEVERE, null, ex);
//            }
//            if(resultObject != null){
////                messagePool.notifyReadComplete(rMessage.getMessageId(), resultObject);
//            }
//        }
//        
//    }
//    
//    private Method getMethod(String target){
//        Method method = null;
//        
//        System.out.println("Method String: "+target +" "+ target.compareTo("basic.SimpleCrud.crudData"));
//        
//        if(target.compareTo("basic.SimpleCrud.crudData") == 0){
//            try {
//                return this.simpleCrud.getClass().getMethod("crudData", Operation.class);
//            } catch (NoSuchMethodException ex) {
//                Logger.getLogger(DataRouter.class.getName()).log(Level.SEVERE, null, ex);
//            } catch (SecurityException ex) {
//                Logger.getLogger(DataRouter.class.getName()).log(Level.SEVERE, null, ex);
//            }
//        }
//        
//        String beanString = target.substring(0, target.lastIndexOf("."));
//        String methodString = target.substring(target.lastIndexOf(".")+1);
//        Object bean = this.getBean(beanString);
//        try {
//            method = bean.getClass().getMethod(methodString, List.class);
//        } catch (NoSuchMethodException ex) {
//            Logger.getLogger(DataRouter.class.getName()).log(Level.SEVERE, null, ex);
//        } catch (SecurityException ex) {
//            Logger.getLogger(DataRouter.class.getName()).log(Level.SEVERE, null, ex);
//        }
//        
//        return method;
//    }
//    
//    private Object getBean(String beanString){
//        
//        System.out.println("Bean String: " + beanString );
//
//        Class<?> clazz = null;
//        try {
//            //Change this to support DataRouter as default bean
//            clazz = Class.forName(beanString);
//        } catch (ClassNotFoundException ex) {
//            Logger.getLogger(DataRouter.class.getName()).log(Level.SEVERE, null, ex);
//        }
//        Object bean = null; 
//        
//        try {
//            bean = clazz.cast(new InitialContext().lookup("java:global/TestApp/"+beanString.substring(beanString.lastIndexOf(".")+1)+"!" + beanString));
////                        return (ExtMessage) c.lookup("java:global/TestApp/ExtMessage!helper.ExtMessage");
//
//        } catch (NamingException ex) {
//            Logger.getLogger(ConnectionManager.class.getName()).log(Level.SEVERE, null, ex);
//        }
//        
//        return bean;
//    }
}
