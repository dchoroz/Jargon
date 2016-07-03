package core;

import helper.ExtMessage;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import org.atmosphere.cache.UUIDBroadcasterCache;
import org.atmosphere.client.TrackMessageSizeInterceptor;
import org.atmosphere.config.service.AtmosphereHandlerService;
import org.atmosphere.cpr.AtmosphereHandler;
import org.atmosphere.cpr.AtmosphereRequest;
import org.atmosphere.cpr.AtmosphereResource;
import org.atmosphere.cpr.AtmosphereResourceEvent;
import org.atmosphere.cpr.AtmosphereResponse;
import org.atmosphere.interceptor.HeartbeatInterceptor;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.LoggerFactory;


//@ManagedService(path="/webresources/Connect",
//        broadcasterCache = UUIDBroadcasterCache.class)
@AtmosphereHandlerService(path = "/webresources/Connect",
        broadcasterCache = UUIDBroadcasterCache.class,
        interceptors = {
//                             AtmosphereResourceLifecycleInterceptor.class,
    //                         BroadcastOnPostAtmosphereInterceptor.class,
    //                         JavaScriptProtocol.class,
    TrackMessageSizeInterceptor.class,
    HeartbeatInterceptor.class
})
public class ConnectionManager implements AtmosphereHandler {
//    @EJB
//    InitialConfig ec;
//    @EJB
//    DataManager dataManager;

    private final static org.slf4j.Logger logger = LoggerFactory.getLogger(ConnectionManager.class);

    //@Override
    public void onRequest(AtmosphereResource resource) throws IOException, JsonMappingException, JsonParseException {
        MessageManager mm = null;
        DataRouter dataRouter = null;

        try {
            mm = (MessageManager) new InitialContext().lookup("java:global/TestApp/MessageManager");
            dataRouter = (DataRouter) new InitialContext().lookup("java:global/TestApp/DataRouter");
        } catch (NamingException ex) {
            Logger.getLogger(ConnectionManager.class.getName()).log(Level.SEVERE, null, ex);
        }

        AtmosphereRequest request = resource.getRequest();
        logger.info("AtmosphereHandler called " + request.getReader());

        if (request.getMethod().equalsIgnoreCase("GET")) {
            AtmosphereResource r = resource.suspend(-1);
            System.out.println("Client suspended: " + request.getRemoteHost() + " with UUID: " + r.uuid() + " " + r.transport());
//            r.setBroadcaster(BroadcasterFactory.getDefault().get());
        } else if (request.getMethod().equalsIgnoreCase("POST")) {
            String requestMessage = request.getReader().readLine().trim();
            System.out.println("Message received from Resource UUID: " + resource.uuid() + " Message: " + requestMessage);
            
            ExtMessage message = mm.parseRequest(requestMessage, resource);
            dataRouter.processRequest(message);
//                }else{
//                    ObjectMapper mapper = new ObjectMapper();
//                    resource.getResponse().write(mapper.writeValueAsString(message));
          

        }
    }

    //@Override
    public void onStateChange(AtmosphereResourceEvent event) {

        AtmosphereResource resource = event.getResource();
        AtmosphereResponse response = resource.getResponse();
        ObjectMapper mapper = new ObjectMapper();

//        System.out.println("onStateChange called "+event.isResumedOnTimeout()+" "+event.isResuming());
        if (resource.isSuspended()) {
            response.setContentType("application/json");

            System.out.println("onStateChange Client is suspended with UUID: " + resource.uuid());

            String rMessage = (String) event.getMessage();
            try {
                response.getWriter().write(rMessage);
                response.getWriter().flush();

            } catch (IOException ex) {
                Logger.getLogger(ConnectionManager.class.getName()).log(Level.SEVERE, null, ex);
            }
            System.out.println("Broadcasting message: " + rMessage + "to client: " + resource.uuid());
        } else if (event.isResuming()) {
            System.out.println("ConnectionManager onStateChange: resource is resuming"+resource.uuid());

        }


        System.out.println("message broadcasted");

    }

    //@Override
    public void destroy() {
    }
}
