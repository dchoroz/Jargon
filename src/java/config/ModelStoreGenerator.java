/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package config;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import org.slf4j.LoggerFactory;

/**
 *
 * @author kounabi
 */
public class ModelStoreGenerator implements ServletContextListener {
    
    private final static org.slf4j.Logger logger = LoggerFactory.getLogger(ModelStoreGenerator.class);

    @EJB
    InitialConfig ic;

    @Override
    public void contextDestroyed(ServletContextEvent event) {
        logger.info("ModelStoreGenerator destroyed");
    }

    @Override
    public void contextInitialized(ServletContextEvent event) {
        logger.info("ModelStoreGenerator started " + event.getServletContext().getContextPath());
        this.generateJavascript(event.getServletContext().getRealPath("/app"));
    }

    public void generateJavascript(String path) {
        ic.mergeScript();
        Map<String, String[]> mss = ic.getModelStoreScript();
        Set<String> keySet = mss.keySet();
    
        for (String name : keySet) {
            File modelFile = new File(path + "/model", name + ".js");
            File storeFile = new File(path + "/store", name + ".js");

            if (modelFile.exists()) {
                logger.info(name + ".js Model file already exists. Recreating file");
                modelFile.delete();
            }
            if (storeFile.exists()) {
                logger.info(name + ".js Store file already exists. Recreating file");
                storeFile.delete();
            }

            try {
                modelFile.createNewFile();
                storeFile.createNewFile();

                FileWriter modelFstream = new FileWriter(modelFile);
                BufferedWriter mOut = new BufferedWriter(modelFstream);
                mOut.write(mss.get(name)[0]);
                mOut.close();

                FileWriter storeFstream = new FileWriter(storeFile);
                BufferedWriter sOut = new BufferedWriter(storeFstream);
                sOut.write(mss.get(name)[1]);
                sOut.close();
            } catch (IOException ex) {
                Logger.getLogger(ModelStoreGenerator.class.getName()).log(Level.SEVERE, null, ex);
            }

        }
    }
}
