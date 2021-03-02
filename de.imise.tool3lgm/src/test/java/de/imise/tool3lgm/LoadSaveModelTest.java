package de.imise.tool3lgm;

import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import de.imise.tool3lgm.graphtools.model.GDCollection;
import de.imise.tool3lgm.graphtools.model.GDCollectionFileHandler;
import de.imise.tool3lgm.gui.MainFrame;
import de.imise.util.Sys;

/**
 * These test can be used to load and save model files without any user
 * interaction. It was designed as a debug test to load and save the same model
 * multiple times to check if there are random or unecessary changes in the
 * saved files.<br>
 * If it should be used as a regular (always enabled) test then the test model
 * must be committed and there must be an always valid save path
 *
 * @author Ich (02.03.2021)
 */
public class LoadSaveModelTest {

    public static final String TEST_MODEL_FILE_NAME = "Beispiel_geändert_test"; //name of the model file to load without extension

    private static String FILE_EXTENSION = ".3lgm"; //file extension

    private final File saveFilePath = new File("D:\\Eigene Projekte\\Bitbucket\\tool-3lgm2\\de.imise.tool3lgm\\Testmodelle"); //to use this test change this path to a path where the test models should be written

    //activate the test by removing the comment signs in the next line
    //@Test
    public void loadSaveModelTest() throws URISyntaxException {

        File lastWrittenFile = null;

        for (int i = 1; i <= 3; i++) {
            File testModelFile;
            if (lastWrittenFile == null) {
                URL testModelUrl = ClassLoader.getSystemResource(TEST_MODEL_FILE_NAME + FILE_EXTENSION); //this file must be located in src/main/resources/
                URI testModelUri = testModelUrl.toURI();
                testModelFile = new File(testModelUri);
            } else {
                testModelFile = lastWrittenFile;
            }
            String testModelFileName = testModelFile.toString();
            String[] args = {
                    testModelFileName, "-i", "-n" //-i = invisible and without user interactions; -n = always a new instance of the tool
            };
            Tool3lgmMain.main(args);
            Tool3lgm tool = Static.getTool();
            assertNotNull(tool);

            Sys.err1(tool.hashCode(), testModelFileName);

            MainFrame mainFrame = Static.getMainFrame();
            assertNotNull(mainFrame);

            assertFalse(tool.hasVisibleMainFrame());

            GDCollection gdcoll = Static.getSelectedGDCollection();
            assertNotNull(gdcoll);

            GDCollectionFileHandler fileHandler = gdcoll.getFileHandler();
            assertNotNull(fileHandler);

            lastWrittenFile = new File(saveFilePath, TEST_MODEL_FILE_NAME + i + FILE_EXTENSION);
            fileHandler.setSaveFile(lastWrittenFile, false);

            Tool3lgm.saveToFile(gdcoll);

        }
    }

}
