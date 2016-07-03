package config;

import annotations.Extpose;
import java.util.ArrayList;
import java.util.List;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.spi.AfterBeanDiscovery;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.BeforeBeanDiscovery;
import javax.enterprise.inject.spi.Extension;
import javax.enterprise.inject.spi.ProcessAnnotatedType;
import javax.naming.NamingException;
import javax.persistence.Entity;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;

/**
 *
 * @author kounabi
 */
class JargonExtension implements Extension {

    List<Class<?>> entitiesList = new ArrayList();
//    List<Facade> facadesList = new ArrayList();
    private String entitiesScript;
//    private Map<String, Set<String>> classToPUMap;
//    private Map<String, EntityManagerFactory> puToEmfMap;
//    private Map<String, Set<EntityManagerFactory>> classToEmfMap;
//    @EJB EntityClasses entityClasses;
    String someText;

    void beforeBeanDiscovery(@Observes BeforeBeanDiscovery bbd) {
        System.out.println("********* BEGIN the scanning process");
    }

    <T> void processAnnotatedType(@Observes ProcessAnnotatedType<T> pat){
        
        if (pat.getAnnotatedType().isAnnotationPresent(Extpose.class)) {
            System.out.println("FOUND Extpose annoted class: " + pat.getAnnotatedType().getJavaClass());
//            if(Modifier.isAbstract(pat.getAnnotatedType().getClass().getModifiers())) System.out.println("The class is abstract");

            entitiesList.add(pat.getAnnotatedType().getJavaClass());
        } 
        if (pat.getAnnotatedType().isAnnotationPresent(Entity.class)) {
            System.out.println("FOUND Entity annoted class");
        
            //ADDED @MERGE ANNOTATION FOR SUPERCLASS
//            if(pat.getAnnotatedType().isAnnotationPresent(Merge.class)){
//                System.out.println("FOUND SuperClass Entity annoted with Merge annotation");
//            }
        }
//      Logger.global.debug("scanning type: " + pat.getAnnotatedType().getJavaClass().getName());

    }

    void afterBeanDiscovery(@Observes AfterBeanDiscovery abd, BeanManager bm) throws ParserConfigurationException, TransformerConfigurationException, TransformerException, NamingException, Exception {

        System.out.println("********* FINISHED the scanning process");
//        this.entitiesScript = "";
//        this.classToPUMap = new HashMap<String, Set<String>>();
//        this.puToEmfMap = new HashMap<String, EntityManagerFactory>();
//        this.classToEmfMap = new HashMap<String, Set<EntityManagerFactory>>();
//        Facade facade;

//        entityClasses = new EntityClasses();
//        InitialContext ic = new InitialContext();
//        System.out.println(ic.lookup("/WEB-INF/classes/META-INF/persistence.xml"));
//        EntityClasses ec = new EntityClasses();

        this.someText = "Injection of ExtjsExtension works";

//        for (Class<?> c : entitiesList) {
//
//            System.out.println("Class: " + c.getSimpleName());
//            entitiesList.add(c);
//            this.getEntitiesPersistenceUnitName(c);
//            this.entitiesScript = this.entitiesScript + "Ext.define('AM.model." + c.getSimpleName() + "', \n"
//                    + "{ extend: 'Ext.data.Model', \n";
//
////            facade = new Facade(c) {
////        
////                @PersistenceContext(unitName = "TestAppPU")
////                EntityManager em;  
////                              
////                @Override
////                protected EntityManager getEntityManager() {
////                    return em;
////                }
////               
////            };
//
////            this.facadesList.add(facade);
////            entityClasses.addEntityClass(fullName, simpleName, facade);
////            entityClasses.size++;
////            Field[] classFields = c.getDeclaredFields();
////            System.out.println(classFields.length);
//
//            this.entitiesScript = this.entitiesScript + "    fields: [";
////            for (Field f : classFields) {
//////                System.out.println("field: " + f + " " + f.getAnnotations().length);
////                if (this.isUserDeclared(f.getName())) {
////                    System.out.println("field: " + f.getName());
//                    this.entitiesScript = this.entitiesScript + "'" + f.getName() + "',";
////                }
////            }
//            this.entitiesScript = this.entitiesScript + "]\n});\n\n";
//            this.entitiesScript = this.entitiesScript + "Ext.define('AM.store." + c.getSimpleName() + "', {\n"
//                    + "    extend: 'Ext.data.Store',\n"
//                    + "    model: 'AM.model." + c.getSimpleName() + "',\n"
//                    + "    \n"
//                    + "     proxy: {\n"
//                    + "        type: 'websocketmanaged',\n"
//                    + "        reader: {\n"
//                    + "            type: 'json',\n"
//                    + "            root: 'data',\n"
//                    + "        },\n"
//                    + "        writer: {\n"
//                    + "            type: 'json'\n"
//                    + "\n"
//                    + "        }\n"
//                    + "    }\n"
//                    + "});\n\n";
//        }



//        System.out.println("********* FINISHED creating facades for Extpose Entities ");
        /*Velocity*/
//        Velocity.init();       
//        
//        Template template = Velocity.getTemplate("entities.vm");
//        VelocityContext context = new VelocityContext();
//        context.put( "name", new String("Velocity") );
//        
//        StringWriter sw = new StringWriter();
//
//        template.merge(context, sw);
    }

    public List<Class<?>> getEntitiesList() {

        return this.entitiesList;
    }

//    public Facade getEntityFacade() {
//
//        return this.facadesList.get(0);
//    }

    public String getString() {
        return this.someText;
    }

//    @Produces
//    public Set<String> getEntityManagerFactories(String clazz){
//        return this.classToPUMap.get(clazz);
//    }
//    
//    ***************Getting persistence Unit Names for Entities*******************
//    private String getEntitiesPersistenceUnitName(Class<?> c) throws ParserConfigurationException, SAXException, IOException {

//        String unitName = "";
//
//        EntityManagerFactory emf;
//        InputStream is = c.getClassLoader().getResourceAsStream("/META-INF/persistence.xml");
//        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
//        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
//        Document doc = dBuilder.parse(is);
//        doc.normalize();
//        NodeList nl = doc.getElementsByTagName("persistence-unit");
//
//
//        for (int i = 0; i < nl.getLength(); i++) {
//
//            Element e = (Element) nl.item(i);
//            unitName = e.getAttribute("name");
//            if (puToEmfMap.containsKey(unitName)) {
//                emf = puToEmfMap.get(unitName);
//            } else {
//                emf = Persistence.createEntityManagerFactory(unitName);
//                puToEmfMap.put(unitName, emf);
//            }
//            
//            System.out.println("persistence unit name: " + unitName);
//
//            NodeList classNodeList = e.getElementsByTagName("class");
//
//            if (classNodeList.getLength() == 0) {
//                if (this.classToPUMap.containsKey(c.getCanonicalName())) {
//                    this.classToPUMap.get(c.getCanonicalName()).add(unitName);
//                    this.classToEmfMap.get(c.getCanonicalName()).add(emf);
//
//                } else {
//                    Set<String> puSet = new HashSet<String>();
//                    Set<EntityManagerFactory> emfSet = new HashSet<EntityManagerFactory>();
//
//                    puSet.add(unitName);
//                    emfSet.add(emf);
//
//                    this.classToPUMap.put(c.getCanonicalName(), puSet);
//                    this.classToEmfMap.put(c.getCanonicalName(), emfSet);
//                }
//            } else {
//                for (int j = 0; j < classNodeList.getLength(); j++) {
//                    String elClass = ((Element) classNodeList.item(j)).getTextContent();
//                    if (this.classToPUMap.containsKey(elClass)) {
//                        this.classToPUMap.get(elClass).add(unitName);
//                        this.classToEmfMap.get(c.getCanonicalName()).add(emf);
//
//                    } else {
//                        Set<String> puSet = new HashSet<String>();
//                        Set<EntityManagerFactory> emfSet = new HashSet<EntityManagerFactory>();
//
//                        puSet.add(unitName);
//                        emfSet.add(emf);
//
//                        this.classToPUMap.put(c.getCanonicalName(), puSet);
//                        this.classToEmfMap.put(c.getCanonicalName(), emfSet);
//                    }
//                }
//            }
//        }
//        return unitName;
//    }

//    private boolean isUserDeclared(String field) {
//
//        if (field.equals("_persistence_primaryKey")) {
//            return false;
//        } else if (field.equals("_persistence_listener")) {
//            return false;
//        } else if (field.equals("_persistence_fetchGroup")) {
//            return false;
//        } else if (field.equals("_persistence_shouldRefreshFetchGroup")) {
//            return false;
//        } else if (field.equals("_persistence_session")) {
//            return false;
//        } else if (field.equals("serialVersionUID")) {
//            return false;
//        }
//
//        return true;
//    }
//    private void XmlCreation() throws ParserConfigurationException, TransformerConfigurationException, TransformerException {
//        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
//        DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
//
//        Document doc = docBuilder.newDocument();
//        Element rootElement = doc.createElement("entities");
//        doc.appendChild(rootElement);
//
//
//        this.someText = "Injection of JargonExtension works";
//
//        for (Class<?> c : entitiesList) {
//
//            Element clazz = doc.createElement("entity");
//            Element className = doc.createElement("className");
//            className.appendChild(doc.createTextNode(c.getSimpleName()));
//            clazz.appendChild(className);
//            rootElement.appendChild(clazz);
//
//
//            System.out.println("Class: " + c.getSimpleName());
////            facade = new Facade(c) {
////        
////                @PersistenceContext(unitName = "TestAppPU")
////                EntityManager em;  
////                              
////                @Override
////                protected EntityManager getEntityManager() {
////                    return em;
////                }
////               
////            };
//
////            this.facadesList.add(facade);
////            entityClasses.addEntityClass(fullName, simpleName, facade);
////            entityClasses.size++;
//            Field[] classFields = c.getDeclaredFields();
//            System.out.println(classFields.length);
//            /*for creation and registration of the new beans with the context*/
////            abd.addBean( (Bean<?>) facade);
//
//
////                System.out.println(c.getField("_persistence_session"));
//            Element fields = doc.createElement("fields");
//            Element associations = doc.createElement("associations");
//
//            for (Field f : classFields) {
//                System.out.println("field: " + f.getName() + " " + f.getType().getName() + " " + f.getAnnotations().length);
//                if (!this.isUserDeclared(f.getName())) {
//                    continue;
//                }
//                Element field = doc.createElement("field");
//                field.appendChild(doc.createTextNode(f.getName()));
//                fields.appendChild(field);
//            }
//            clazz.appendChild(fields);
//            clazz.appendChild(associations);
//        }
//
//        TransformerFactory transformerFactory = TransformerFactory.newInstance();
//        Transformer transformer = transformerFactory.newTransformer();
//        DOMSource source = new DOMSource(doc);
//        StreamResult result = new StreamResult(new File("file.xml"));
////       StreamResult result = new StreamResult(System.out);
//
//        transformer.transform(source, result);
//    }
}
