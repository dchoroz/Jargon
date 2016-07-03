/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package core;

import config.InitialConfig;
import helper.RestAction;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.persistence.PostPersist;
import javax.persistence.PostRemove;
import javax.persistence.PostUpdate;

/**JSR 317 page 94.
 * The Object argument is the entity instance for which the callback method is invoked. It may be
 * declared as the actual entity type.
 */
public class EntityCrudListener {

    @EJB
    private MessagePool mp;

    public EntityCrudListener() {
    }

    @PostPersist
    void onPostPersist(Object object) {
        
        System.out.println("PostPersist works " + object.getClass().getCanonicalName());

        this.getMessagePool().notifyAll(object,  object.getClass(), RestAction.create);

    }

    /*Callback on READ, currently not needed because no broadcast is needed*/
    @PostUpdate
    void onPostUpdate(Object object) {
        System.out.println("PostUpdate works " + object.getClass().getCanonicalName());

        this.getMessagePool().notifyAll(object,  object.getClass(), RestAction.update);
    }

    @PostRemove
    void onPostRemove(Object object) {
        System.out.println("PostRemove works " + object.getClass().getCanonicalName());

        this.getMessagePool().notifyAll(object,  object.getClass(), RestAction.destroy);
    }

    /*Currently using JNDI lookup to take the Beans needed by the listener
     EJBs can be injected in  EntityListeners, available in JPA 2.1*/
    private InitialConfig getEntityClasses() {
        InitialConfig ec = null;
        try {
            ec = (InitialConfig) new InitialContext().lookup("java:global/TestApp/EntityClasses");

        } catch (NamingException ex) {
            Logger.getLogger(ConnectionManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        return ec;
    }

    private MessagePool getMessagePool() {
        MessagePool mp = null;
        try {
            mp = (MessagePool) new InitialContext().lookup("java:global/TestApp/MessagePool");
        } catch (NamingException ex) {
            Logger.getLogger(ConnectionManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        return mp;
    }
}
