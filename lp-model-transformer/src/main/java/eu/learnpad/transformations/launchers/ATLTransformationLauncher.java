package eu.learnpad.transformations.launchers;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.util.BasicExtendedMetaData;
import org.eclipse.emf.ecore.util.ExtendedMetaData;
import org.eclipse.emf.ecore.xmi.XMLResource;
import org.eclipse.emf.ecore.xmi.impl.EcoreResourceFactoryImpl;

import eu.learnpad.transformations.model2model.ATLTransformation;

public class ATLTransformationLauncher
{
    private final String ADOXX_TYPE = "ADOXX";

    private final String LPAD_TYPE = "LPAD";

    private String ADOXX2XWIKI_ATL_TRANSFORMATION = "transformation/ado2xwiki.atl";

    private String LPAD2ADOXX_ATL_TRANSFORMATION = "transformation/lpad2adoxx.atl";

    /**
     * Execution of the ATL Transformation with a pre-processing with alignment. This method take an XML file as
     * InputStream and after a pre-precessing phase execute the transformation with the resulting XMI model file.
     * 
     * @param model The InputStream of the model file to be transformed.
     * @throws Exception
     */
    public boolean transform(InputStream modelInputStream, String type, OutputStream out) throws Exception
    {
        String metamodel_in = "";
        String metamodel_out = "";
        InputStream atlStream;
        List<InputStream> atlStreams = new ArrayList<InputStream>();
        String inTag = "";
        String outTag = "";

        switch (type) {
            case ADOXX_TYPE:
                metamodel_in = "metamodels/adoxx/ado.ecore";
                metamodel_out = "metamodels/xwiki/XWIKI.ecore";
                atlStream = this.getClass().getClassLoader().getResourceAsStream(ADOXX2XWIKI_ATL_TRANSFORMATION);
                atlStreams.add(atlStream);
                inTag = "ADOXX";
                outTag = "XWIKI";
                break;
            case LPAD_TYPE:
                metamodel_in = "metamodels/lpad/learnPAd.ecore";
                metamodel_out = "metamodels/adoxx/ado.ecore";
                atlStream = this.getClass().getClassLoader().getResourceAsStream(LPAD2ADOXX_ATL_TRANSFORMATION);
                atlStreams.add(atlStream);
                inTag = "LPAD";
                outTag = "ADOXX";
                
                //Register all the metamodels (possibly) involved in the transformation
                String bpmn20MetamodelPath = this.getClass().getClassLoader().getResource("metamodels/lpad/bpmn2.0/BPMN20.ecore").getPath();
                String bpmndiMetamodelPath = this.getClass().getClassLoader().getResource("metamodels/lpad/bpmn2.0/BPMNDI.ecore").getPath();
                String dcMetamodelPath = this.getClass().getClassLoader().getResource("metamodels/lpad/bpmn2.0/DC.ecore").getPath();
                String diMetamodelPath = this.getClass().getClassLoader().getResource("metamodels/lpad/bpmn2.0/DI.ecore").getPath();
                String bmmMetamodelPath = this.getClass().getClassLoader().getResource("metamodels/lpad/bmm/BMM.ecore").getPath();
                String competencyMetamodelPath = this.getClass().getClassLoader().getResource("metamodels/lpad/competency/Competency.ecore").getPath();
                String documentAndKnowledgeMetamodelPath = this.getClass().getClassLoader().getResource("metamodels/lpad/documentAndKnowledge/DocumentAndKnowledge.ecore").getPath();
                String kpiMetamodelPath = this.getClass().getClassLoader().getResource("metamodels/lpad/kpi/kpi.ecore").getPath();
                String organizationalMetamodelPath = this.getClass().getClassLoader().getResource("metamodels/lpad/organizational/Organisational.ecore").getPath();
                
                lazyMetamodelRegistration(bpmn20MetamodelPath);
                lazyMetamodelRegistration(bpmndiMetamodelPath);
                lazyMetamodelRegistration(dcMetamodelPath);
                lazyMetamodelRegistration(diMetamodelPath);
                lazyMetamodelRegistration(bmmMetamodelPath);
                lazyMetamodelRegistration(competencyMetamodelPath);
                lazyMetamodelRegistration(documentAndKnowledgeMetamodelPath);
                lazyMetamodelRegistration(kpiMetamodelPath);
                lazyMetamodelRegistration(organizationalMetamodelPath);
                break;
            default:
                System.out.println("Type not allowed!");
                break;
        }

        ATLTransformation myT = new ATLTransformation();
        System.out.println("Starting ATL Model2Model transformation...");
        
       
        String inputMetamodelPath = this.getClass().getClassLoader().getResource(metamodel_in).getPath();
        String outputMetamodelPath = this.getClass().getClassLoader().getResource(metamodel_out).getPath();
        lazyMetamodelRegistration(inputMetamodelPath);
        lazyMetamodelRegistration(outputMetamodelPath);
        
       
        
        
        
        InputStream learnpadMetamodelStream = this.getClass().getClassLoader().getResourceAsStream(metamodel_in);
        InputStream xwikiMetamodelStream = this.getClass().getClassLoader().getResourceAsStream(metamodel_out);
        
        
        
        
        myT.run(modelInputStream, learnpadMetamodelStream, xwikiMetamodelStream, atlStreams, inTag, outTag, out);
        learnpadMetamodelStream.close();
        xwikiMetamodelStream.close();
        for (InputStream module : atlStreams) {
            module.close();
        }

        return true;
    }
    
    
    /*
	 * This method does two things, it initializes an Ecore parser and then programmatically looks for
	 * the package definition on it, obtains the NsUri and registers it.
	 */
	private String lazyMetamodelRegistration(String metamodelPath){
		
		Resource.Factory.Registry.INSTANCE.getExtensionToFactoryMap().put("ecore", new EcoreResourceFactoryImpl());
   	
	    ResourceSet rs = new ResourceSetImpl();
	    // Enables extended meta-data, weird we have to do this but well...
	    final ExtendedMetaData extendedMetaData = new BasicExtendedMetaData(EPackage.Registry.INSTANCE);
	    rs.getLoadOptions().put(XMLResource.OPTION_EXTENDED_META_DATA, extendedMetaData);
	
	    Resource r = rs.getResource(URI.createFileURI(metamodelPath), true);
	    EObject eObject = r.getContents().get(0);
	    // A meta-model might have multiple packages we assume the main package is the first one listed
	    if (eObject instanceof EPackage) {
	        EPackage p = (EPackage)eObject;
	        System.out.println(p.getNsURI());
	        EPackage.Registry.INSTANCE.put(p.getNsURI(), p);
	        return p.getNsURI();
	    }
	    return null;
	}
    
    
    

    public static void main(String[] args) throws Exception
    {

        // String model_in = "resources/model/ado4f16a6bb-9318-4908-84a7-c2d135253dc9.xml";
        String model_in = "resources/model/learnpad.xmi";
        String file_out = "/tmp/testTransformationOutputStream.xmi";
//        String type = "ADOXX";
        String type = "LPAD";

        FileInputStream fis = new FileInputStream(model_in);
        OutputStream out = new FileOutputStream(file_out);

        ATLTransformationLauncher atlTL = new ATLTransformationLauncher();
        System.out.println("*******STARTING THE ATL TRANSFORMATION*******");
        atlTL.transform(fis, type, out);
        System.out.println("*******FINISHED THE ATL TRANSFORMATION*******");
    }
}
