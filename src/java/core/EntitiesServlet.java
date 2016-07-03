///*
// * To change this template, choose Tools | Templates
// * and open the template in the editor.
// */
//package core;
//
//import config.EntityData;
//import config.InitialConfig;
//import java.io.BufferedReader;
//import java.io.File;
//import java.io.FileInputStream;
//import java.io.IOException;
//import java.io.InputStream;
//import java.io.OutputStream;
//import java.util.Set;
//import java.util.logging.Logger;
//import javax.ejb.EJB;
//import javax.ejb.Stateless;
//import javax.inject.Inject;
//import javax.servlet.ServletContext;
//import javax.servlet.ServletException;
//import javax.servlet.http.HttpServlet;
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//import javax.ws.rs.GET;
//import javax.ws.rs.Path;
//import javax.ws.rs.PathParam;
//import javax.ws.rs.Produces;
//
//@Path("/Entities")
//@Stateless
//public class EntitiesServlet extends HttpServlet {
//
//    @EJB
//    InitialConfig ec;
//    
//    @GET
//    @Produces("text/javascript")
//    protected void doGet( HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
//   
//        ServletContext servletContext = req.getServletContext();
//        String script = this.ec.getFullJavascript();
//        
//        File file = new File(servletContext.getRealPath("")+"/app/model/models.js");
//        file.createNewFile();
//        System.out.println("EntitiesServlet called. Context real path: "+servletContext.getRealPath("")+" "+file.getAbsolutePath()+" "+file.exists());
//        resp.getWriter().write(script);
//    }
//    
//
//}
