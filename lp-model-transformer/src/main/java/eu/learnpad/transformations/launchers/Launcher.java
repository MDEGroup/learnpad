package eu.learnpad.transformations.launchers;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.resource.Resource;

import eu.learnpad.transformations.metamodel_corpus.xwiki.XwikiPackage;

public class Launcher
{
    /**
     * Alignment Launcher: starting from an XML file as InputStream it create a valid XMI model file directly into the
     * OutputStream provided as input.
     * 
     * @param model
     * @param type
     * @param out
     * @return boolean
     * @throws Exception
     */
    public boolean align(InputStream model, String type, OutputStream out) throws Exception
    {
        AlignmentLauncher aL = new AlignmentLauncher();
        return aL.align(model, type, out);
    }

    /**
     * Execution of the ATL Transformation with a pre-processing with alignment. This method take an XML file as
     * InputStream and after a pre-precessing phase execute the transformation with the resulting XMI model file.
     * 
     * @param model
     * @param out
     * @param type
     * @return boolean
     * @throws Exception
     */
    public boolean transform(InputStream model, String type, OutputStream out) throws Exception
    {
        ATLTransformationLauncher atlTL = new ATLTransformationLauncher();
        return atlTL.transform(model, type, out);
    }

    /**
     * Acceleo Transformation Launcher (MODEL2CODE Transformation).
     * 
     * @param model
     * @return Path
     */
    public Path write(InputStream model)
    {
        AcceleoTransformationLauncher acceleoTL = new AcceleoTransformationLauncher();
        return acceleoTL.write(model);
    }

    /**
     * Execute the chain of transformation composed by: ATL Transformation (MODEL2MODEL Transformation) and Acceleo
     * Transformation (MODEL2TEXT Transformation).
     * 
     * @param model
     * @param type
     * @param out
     * @return Path
     * @throws Exception
     */
    public Path chain(InputStream model, String type) throws Exception
    {
        Path lpModelPath = Paths.get("/tmp/learnpad/mt/model.xmi");
        Path xwikiModelPath = Paths.get("/tmp/learnpad/mt/model.xwiki.xmi");

        // ALIGN
        AlignmentLauncher alignLauncher = new AlignmentLauncher();
        OutputStream alignOutputStream = Files.newOutputStream(lpModelPath);
        boolean isAlign = alignLauncher.align(model, type, alignOutputStream);

        // TRANSFORM
        ATLTransformationLauncher transformLauncher = new ATLTransformationLauncher();
        InputStream transformInputStream = Files.newInputStream(lpModelPath);
        OutputStream transformOutputStream = Files.newOutputStream(xwikiModelPath);
        boolean isTransformed = transformLauncher.transform(transformInputStream, type, transformOutputStream);

        // WRITE
        AcceleoTransformationLauncher writeLauncher = new AcceleoTransformationLauncher();
        InputStream writeInputStream = Files.newInputStream(xwikiModelPath);
        Path path = writeLauncher.write(writeInputStream);

        // CLEAN
        alignOutputStream.close();
        transformInputStream.close();
        transformOutputStream.close();
        writeInputStream.close();
//        Files.delete(lpModelPath);
//        Files.delete(xwikiModelPath);

        if (isAlign && isTransformed && path != null) {
            return path;
        } else {
            return null;
        }
    }
    
    
    
    
    
    public Path chain2(InputStream model, String type) throws Exception
    {
        Path mdModelPath = Paths.get("/tmp/learnpad/mt/model.md.xmi");
        Path adoxxModelPath = Paths.get("/tmp/learnpad/mt/model.adoxx.xmi");
        Path xwikiModelPath = Paths.get("/tmp/learnpad/mt/model.xwiki.xmi");
        
        AlignmentLauncher alignLauncher = new AlignmentLauncher();
        
        // ALIGN MD
        OutputStream alignOutputStream = Files.newOutputStream(mdModelPath);
        boolean isAlign2 = alignLauncher.align(model, "MD", alignOutputStream);
        
        ATLTransformationLauncher transformLauncher = new ATLTransformationLauncher();
        
        //MD->ADOXX
        
        File modelInputFile = new File(mdModelPath.toString());
        InputStream modelInputIS = new FileInputStream(modelInputFile);
//        InputStream transformInputStream2 = Files.newInputStream(mdModelPath);
        OutputStream md2adoxxOutputStream = Files.newOutputStream(adoxxModelPath);
        boolean isTransformed = transformLauncher.transform(modelInputIS, "MD", md2adoxxOutputStream);
        
        

        // ALIGN ADOXX
//        OutputStream alignOutputStream2 = Files.newOutputStream(adoxxModelPath);
//        boolean isAlign = alignLauncher.align(model, "ADOXX", alignOutputStream2);

//        EPackage.Registry.INSTANCE.put(XwikiPackage.eNS_URI, XwikiPackage.eINSTANCE);

        // ADOXX->XWIKI
        InputStream transformInputStream = Files.newInputStream(adoxxModelPath);
        OutputStream transformOutputStream = Files.newOutputStream(xwikiModelPath);
        boolean isTransformed2 = transformLauncher.transform(transformInputStream, "ADOXX", transformOutputStream);

        // Have to unregister them because if not, they stay loaded and are messing up with ATL module
        // They are automatically loaded by Acceleo in the Generate class
//        EPackage.Registry.INSTANCE.remove(XwikiPackage.eNS_URI);
//        Resource.Factory.Registry.INSTANCE.getExtensionToFactoryMap().remove("*");
        
        // WRITE
        AcceleoTransformationLauncher writeLauncher = new AcceleoTransformationLauncher();
        InputStream writeInputStream = Files.newInputStream(xwikiModelPath);
        Path path = writeLauncher.write(writeInputStream);

        // CLEAN
//        alignOutputStream.close();
//        alignOutputStream2.close();
        transformInputStream.close();
//        transformInputStream2.close();
        modelInputIS.close();
        transformOutputStream.close();
        md2adoxxOutputStream.close();
        writeInputStream.close();
//        Files.delete(lpModelPath);
//        Files.delete(xwikiModelPath);

        if (isTransformed && path != null) {
            return path;
        } else {
            return null;
        }
    }

    public static void main(String[] args) throws Exception
    {

        String model_in = "resources/model/md_model_export.xmi";
        String file_out = "/tmp/testAlignmentOutputStream.xmi";

        String type = "MD"; //ADOXX

        // create a new output stream
        OutputStream out = new FileOutputStream(file_out);

        Launcher launcher = new Launcher();
        FileInputStream fis = new FileInputStream(model_in);
        
        launcher.chain2(fis, type);

       
        
        
        
        
        
        
        
        
        
        
        
        /*
         * ****************************************** ****ALIGNMENT EXAMPLE**********************
         * ******************************************
         */
//        launcher.align(fis, type, out);

        /*
         * ****************************************** ****ATL TRANSFORMATION EXAMPLE*************
         * ******************************************
         */
        // launcher.transform(fis, type, out);

        /*
         * ****************************************** ****ACCELEO TRANSFORMATION EXAMPLE*********
         * ******************************************
         */
        // launcher.write(fis);

        /*
         * ****************************************** ****CHAIN EXAMPLE*************************
         * ******************************************
         */
        

    }
}
