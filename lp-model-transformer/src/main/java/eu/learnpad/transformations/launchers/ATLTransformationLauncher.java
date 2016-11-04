package eu.learnpad.transformations.launchers;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
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

public class ATLTransformationLauncher{

	private String LPAD_MM_ROOT = "metamodels/learnpad/";
	private String ADOXX_MM = "metamodels/adoxx/ado.ecore";
    private String LPAD_MM = "learnPAd.ecore";
    private String LPAD_MM_WITH_FULL_PATH = "learnPAd2.ecore";
    private String XWIKI_MM = "metamodels/xwiki/XWIKI.ecore";
    private final String ADOXX_TYPE = "ADOXX";
    private final String MD_TYPE = "MD";
    private final String LPAD_TYPE = "LPAD";
    private final String XWIKI_TYPE = "XWIKI";
    
    private String BPMN20_ECORE = "bpmn2.0/BPMN20.ecore";
    private String BPMNDI_ECORE = "bpmn2.0/BPMNDI.ecore";
    private String DC_ECORE = "bpmn2.0/DC.ecore";
    private String DI_ECORE = "bpmn2.0/DI.ecore";
    private String BMM_ECORE = "bmm/BMM.ecore";
    private String COMPETENCY_ECORE = "competency/Competency.ecore";
    private String DOCUMENTANDKNOWLEDGE_ECORE = "documentAndKnowledge/DocumentAndKnowledge.ecore";
    private String KPI_ECORE = "kpi/kpi.ecore";
    private String ORGANIZATIONAL_ECORE = "organizational/Organisational.ecore";
    
    private String ADOXX2XWIKI_ATL_TRANSFORMATION = "transformation/ado2xwiki.atl";
    private String LPAD2ADOXX_ATL_TRANSFORMATION = "transformation/lpad2adoxx.atl";

	/**
	 * This method substitute full paths to the relative once for all referenced
	 * metamodels in the learnPAd metamodel.
	 * it creates a second learnPAd metamodel in order to have intact the original metamodel 
	 * and work with the metamodel with the correct path.
	 * 
	 * The full path is calculated at runtime, on the server the module is working when the method
	 * is invoked in order to always be aligned.
	 * 
	 * @throws IOException
	 */
	private void allineateLPADMM() throws IOException {

		String resourceFolder = this.getClass().getClassLoader().getResource(LPAD_MM_ROOT).getPath();

		String learnPAd = resourceFolder + LPAD_MM;
		String learnPAd2 = resourceFolder + LPAD_MM_WITH_FULL_PATH;

		try {
			FileReader fr_learnPAd = new FileReader(learnPAd);
			BufferedReader br = new BufferedReader(fr_learnPAd);
			FileWriter fw_learnPAd2 = new FileWriter(learnPAd2);
			BufferedWriter bw = new BufferedWriter(fw_learnPAd2);
			String line = null;

			while ((line = br.readLine()) != null) {

				line = line.replaceAll("bpmn2.0/BPMN20.ecore#", resourceFolder + "bpmn2.0/BPMN20.ecore#");
				line = line.replaceAll("bmm/BMM.ecore#", resourceFolder + "bmm/BMM.ecore#");
				line = line.replaceAll("competency/Competency.ecore#", resourceFolder + "competency/Competency.ecore#");
				line = line.replaceAll("documentAndKnowledge/DocumentAndKnowledge.ecore#", resourceFolder + "documentAndKnowledge/DocumentAndKnowledge.ecore#");
				line = line.replaceAll("organizational/Organisational.ecore#", resourceFolder + "organizational/Organisational.ecore#");
				line = line.replaceAll("kpi/kpi.ecore#", resourceFolder + "kpi/kpi.ecore#");

				bw.write(line + "\n");
			}
			bw.close();
		} catch (IOException ioe) {
		}

		/*
		 * Check the result of the metamodel with full path.
		 */
//		 String line2 = null;
//		 FileReader fr_learnPAd3 = new FileReader(learnPAd2);
//		 BufferedReader br2 = new BufferedReader(fr_learnPAd3);
//		 while((line2=br2.readLine()) != null) {
//		 System.out.println(line2);
//		 }

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
    

    /**
     * Execution of the ATL Transformation with a pre-processing with alignment. This method take an XML file as
     * InputStream and after a pre-precessing phase execute the transformation with the resulting XMI model file.
     * 
     * @param model The InputStream of the model file to be transformed.
     * @throws Exception
     */
    public boolean transform(InputStream modelInputStream, String type, OutputStream out) throws Exception{
    	
        String metamodel_in = "";
        String metamodel_out = "";
        InputStream atlStream;
        List<InputStream> atlStreams = new ArrayList<InputStream>();
        String inTag = "";
        String outTag = "";

        switch (type) {
            case ADOXX_TYPE:
                metamodel_in = ADOXX_MM;
                metamodel_out = XWIKI_MM;
                atlStream = this.getClass().getClassLoader().getResourceAsStream(ADOXX2XWIKI_ATL_TRANSFORMATION);
                atlStreams.add(atlStream);
                inTag = ADOXX_TYPE;
                outTag = XWIKI_TYPE;
                break;
            case MD_TYPE:
            	
            	//Modify the LPAD MM inserting absolute paths of references MM
            	allineateLPADMM();
            	
            	String resourceFolder = this.getClass().getClassLoader().getResource(LPAD_MM_ROOT).getPath();
            	
                metamodel_in = LPAD_MM_ROOT + LPAD_MM_WITH_FULL_PATH; //metamodels/lpad/learnPAd2.ecore
                metamodel_out = ADOXX_MM;
                atlStream = this.getClass().getClassLoader().getResourceAsStream(LPAD2ADOXX_ATL_TRANSFORMATION);
                atlStreams.add(atlStream);
                inTag = LPAD_TYPE;
                outTag = ADOXX_TYPE;
                
                //Register all the metamodels (possibly) involved in the transformation
                String bpmn20MetamodelPath = resourceFolder + BPMN20_ECORE;
                String bpmndiMetamodelPath = resourceFolder + BPMNDI_ECORE;
                String dcMetamodelPath = resourceFolder + DC_ECORE;
                String diMetamodelPath = resourceFolder + DI_ECORE;
                String bmmMetamodelPath = resourceFolder + BMM_ECORE;
                String competencyMetamodelPath = resourceFolder + COMPETENCY_ECORE;
                String documentAndKnowledgeMetamodelPath = resourceFolder + DOCUMENTANDKNOWLEDGE_ECORE;
                String kpiMetamodelPath = resourceFolder + KPI_ECORE;
                String organizationalMetamodelPath = resourceFolder + ORGANIZATIONAL_ECORE;
                
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
    

    public static void main(String[] args) throws Exception{

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
