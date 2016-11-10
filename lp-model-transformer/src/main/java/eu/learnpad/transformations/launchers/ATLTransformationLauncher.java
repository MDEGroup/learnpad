package eu.learnpad.transformations.launchers;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
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

	private String LPAD_MM_ROOT_MAVEN = "target/main/resources/learnpad/";

	private String LPAD_MM_ROOT = "metamodels/learnpad/";
	private String ADOXX_MM = "metamodels/adoxx/ado.ecore";
    private String LPAD_MM = "learnPAd.ecore";
    private String LPAD_MM_WITH_FULL_PATH = "learnPAd2.ecore";
    private String XWIKI_MM = "metamodels/xwiki/XWIKI.ecore";
    private final String ADOXX_TYPE = "ADOXX";
    private final String MD_TYPE = "MD";
    private final String LPAD_TYPE = "LPAD";
    private final String XWIKI_TYPE = "XWIKI";
    
    private String BPMN20_ECORE = "BPMN20.ecore";
    private String BPMNDI_ECORE = "BPMNDI.ecore";
    private String DC_ECORE = "DC.ecore";
    private String DI_ECORE = "DI.ecore";
    private String BMM_ECORE = "BMM.ecore";
    private String COMPETENCY_ECORE = "Competency.ecore";
    private String DOCUMENTANDKNOWLEDGE_ECORE = "DocumentAndKnowledge.ecore";
    private String KPI_ECORE = "kpi.ecore";
    private String ORGANIZATIONAL_ECORE = "Organisational.ecore";
//    private String BPMN20_ECORE = "bpmn2.0/BPMN20.ecore";
//    private String BPMNDI_ECORE = "bpmn2.0/BPMNDI.ecore";
//    private String DC_ECORE = "bpmn2.0/DC.ecore";
//    private String DI_ECORE = "bpmn2.0/DI.ecore";
//    private String BMM_ECORE = "bmm/BMM.ecore";
//    private String COMPETENCY_ECORE = "competency/Competency.ecore";
//    private String DOCUMENTANDKNOWLEDGE_ECORE = "documentAndKnowledge/DocumentAndKnowledge.ecore";
//    private String KPI_ECORE = "kpi/kpi.ecore";
//    private String ORGANIZATIONAL_ECORE = "organizational/Organisational.ecore";
    
    private String ADOXX2XWIKI_ATL_TRANSFORMATION = "transformation/ado2xwiki.atl";
    private String LPAD2ADOXX_ATL_TRANSFORMATION = "transformation/lpad2adoxx.atl";
    private String LEARNPAD_METAMODEL = "metamodels/learnpad/learnPAd.ecore";
    
    private String METAMODELS_TMP_BASEPATH = "/tmp/learnpad/mt/";

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

//				line = line.replaceAll("BPMN20.ecore#", resourceFolder + "BPMN20.ecore#");
//				line = line.replaceAll("BMM.ecore#", resourceFolder + "BMM.ecore#");
//				line = line.replaceAll("Competency.ecore#", resourceFolder + "Competency.ecore#");
//				line = line.replaceAll("DocumentAndKnowledge.ecore#", resourceFolder + "DocumentAndKnowledge.ecore#");
//				line = line.replaceAll("Organisational.ecore#", resourceFolder + "Organisational.ecore#");
//				line = line.replaceAll("kpi.ecore#", resourceFolder + "kpi.ecore#");
				line = line.replaceAll("BPMN20.ecore#", METAMODELS_TMP_BASEPATH + "BPMN20.ecore#");
				line = line.replaceAll("BMM.ecore#", METAMODELS_TMP_BASEPATH + "BMM.ecore#");
				line = line.replaceAll("Competency.ecore#", METAMODELS_TMP_BASEPATH + "Competency.ecore#");
				line = line.replaceAll("DocumentAndKnowledge.ecore#", METAMODELS_TMP_BASEPATH + "DocumentAndKnowledge.ecore#");
				line = line.replaceAll("Organisational.ecore#", METAMODELS_TMP_BASEPATH + "Organisational.ecore#");
				line = line.replaceAll("kpi.ecore#", METAMODELS_TMP_BASEPATH + "kpi.ecore#");

				bw.write(line + System.lineSeparator());
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

    	InputStream atlStream;
        List<InputStream> atlStreams = new ArrayList<InputStream>();
        String inTag = "";
        String outTag = "";

        switch (type) {
            case ADOXX_TYPE:
                atlStream = this.getClass().getClassLoader().getResourceAsStream(ADOXX2XWIKI_ATL_TRANSFORMATION);
                atlStreams.add(atlStream);
                inTag = ADOXX_TYPE;
                outTag = XWIKI_TYPE;
                
                lazyMetamodelRegistration(copyResourceToTMP(ADOXX_MM, "ado.ecore"));
                lazyMetamodelRegistration(copyResourceToTMP(XWIKI_MM, "XWIKI.ecore"));
                
                break;
            case MD_TYPE:
            	
            	//Modify the LPAD MM inserting absolute paths of references MM
//            	allineateLPADMM();
            	
                atlStream = this.getClass().getClassLoader().getResourceAsStream(LPAD2ADOXX_ATL_TRANSFORMATION);
                atlStreams.add(atlStream);
                inTag = LPAD_TYPE;
                outTag = ADOXX_TYPE;

                lazyMetamodelRegistration(copyResourceToTMP(LPAD_MM_ROOT + LPAD_MM, LPAD_MM));
                lazyMetamodelRegistration(copyResourceToTMP(LPAD_MM_ROOT + BPMN20_ECORE, BPMN20_ECORE));
                lazyMetamodelRegistration(copyResourceToTMP(LPAD_MM_ROOT + BPMNDI_ECORE, BPMNDI_ECORE));
                lazyMetamodelRegistration(copyResourceToTMP(LPAD_MM_ROOT + DC_ECORE, DC_ECORE));
                lazyMetamodelRegistration(copyResourceToTMP(LPAD_MM_ROOT + DI_ECORE, DI_ECORE));
                lazyMetamodelRegistration(copyResourceToTMP(LPAD_MM_ROOT + BMM_ECORE, BMM_ECORE));
                lazyMetamodelRegistration(copyResourceToTMP(LPAD_MM_ROOT + COMPETENCY_ECORE, COMPETENCY_ECORE));
                lazyMetamodelRegistration(copyResourceToTMP(LPAD_MM_ROOT + DOCUMENTANDKNOWLEDGE_ECORE, DOCUMENTANDKNOWLEDGE_ECORE));
                lazyMetamodelRegistration(copyResourceToTMP(LPAD_MM_ROOT + KPI_ECORE, KPI_ECORE));
                lazyMetamodelRegistration(copyResourceToTMP(LPAD_MM_ROOT + ORGANIZATIONAL_ECORE, ORGANIZATIONAL_ECORE));

                lazyMetamodelRegistration(copyResourceToTMP(ADOXX_MM, "ado.ecore"));
                
               
                break;
            default:
                System.out.println("Type not allowed!");
                break;
        }

        ATLTransformation myT = new ATLTransformation();
        System.out.println("Starting ATL Model2Model transformation...");
        
        String inputMetamodelPath = METAMODELS_TMP_BASEPATH + LPAD_MM;
        String outputMetamodelPath = METAMODELS_TMP_BASEPATH + "ado.ecore";
//        String inputMetamodelPath = this.getClass().getClassLoader().getResource(metamodel_in).getPath();
//        String outputMetamodelPath = this.getClass().getClassLoader().getResource(metamodel_out).getPath();
//        lazyMetamodelRegistration(inputMetamodelPath);
//        lazyMetamodelRegistration(outputMetamodelPath);
        
        File transformationInputFileMM = new File(inputMetamodelPath);
        InputStream transformationInputMM = new FileInputStream(transformationInputFileMM);
        File transformationOutputFileMM = new File(outputMetamodelPath);
        InputStream transformationOutputMM = new FileInputStream(transformationOutputFileMM);
        
//        InputStream learnpadMetamodelStream = this.getClass().getClassLoader().getResourceAsStream(inputMetamodelPath);
//        InputStream xwikiMetamodelStream = this.getClass().getClassLoader().getResourceAsStream(outputMetamodelPath);
        
        myT.run(modelInputStream, transformationInputMM, transformationOutputMM, atlStreams, inTag, outTag, out);
        transformationInputMM.close();
        transformationOutputMM.close();
        for (InputStream module : atlStreams) {
            module.close();
        }

        return true;
    }
    



	private String copyResourceToTMP(String metamodelPath, String fileName) {

		String completeTMPPathString = METAMODELS_TMP_BASEPATH + fileName;

		Path completeTMPPath = Paths.get(completeTMPPathString);
		try {
			// Check if file already exists
			File fileToCheck = new File(completeTMPPath.toString());
			if (fileToCheck.exists()) {
				Files.delete(completeTMPPath);
			}

			InputStream metamodel = this.getClass().getClassLoader().getResourceAsStream(metamodelPath);
			byte[] buffer;

			buffer = new byte[metamodel.available()];
			metamodel.read(buffer);
			System.out.println("Readed file");
			// File targetFile = new File("/tmp/learnpad/mt/temp.ecore");
			File targetFile = new File(completeTMPPathString);
			OutputStream outStream = new FileOutputStream(targetFile);
			outStream.write(buffer);
			System.out.println("printed file");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return completeTMPPathString;

	}


	public static void main(String[] args) throws Exception{
    	

        // String model_in = "resources/model/ado4f16a6bb-9318-4908-84a7-c2d135253dc9.xml";
        String model_in = "resources/model/md_model_export.xmi";
//        String model_in = "resources/model/epbr.adoxx.xmi";
        String file_out = "/tmp/testTransformationOutputStream.xmi";
//        String type = "ADOXX";
        String type = "MD";

        FileInputStream fis = new FileInputStream(model_in);
        OutputStream out = new FileOutputStream(file_out);

        ATLTransformationLauncher atlTL = new ATLTransformationLauncher();
        System.out.println("*******STARTING THE ATL TRANSFORMATION*******");
        atlTL.transform(fis, type, out);
        System.out.println("*******FINISHED THE ATL TRANSFORMATION*******");
    	
    }
    
}
