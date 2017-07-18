package pt.ncastro.csvprocessor.service;

import static org.junit.Assert.assertEquals;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.logging.Logger;

import org.junit.Test;

import pt.ncastro.csvprocessor.domain.CSVDecisionFile;



/**
 * This is the unit testing class for the application.
 * 
 * @author Nuno de Castro
 *
 */
public class CSVProcessingServiceTest {

	private static final Logger log = Logger.getLogger(CSVProcessingServiceTest.class.getName());
	private static final String RESOURCES_DIR = "testingResources/";


	/**
	 * The testing function for the given examples.
	 * 
	 * @param fileNameIn
	 * @param fileNameOut
	 */
	private void csvTestCaseN(String fileNameIn, String fileNameOut) {
		log.info("Testing '" + fileNameIn + "' against '" + fileNameOut + "'.");
		BufferedReader brIn = null;
		BufferedReader brOut = null;
		try {
			String line;
			brIn = new BufferedReader(new FileReader(RESOURCES_DIR + fileNameIn));
			brOut = new BufferedReader(new FileReader(RESOURCES_DIR + fileNameOut));

			// load file result
			StringBuffer sb = new StringBuffer();
			while ((line = brOut.readLine()) != null) {
				sb.append(line + "\r\n");
			}
			CSVDecisionFile fileOut = new CSVDecisionFile(sb.toString());
			log.info("File output has " + fileOut.getNumColumns() + " columns, " + fileOut.getNumLines() + " lines.");

			// load file input
			sb = new StringBuffer();
			while ((line = brIn.readLine()) != null) {
				sb.append(line + "\r\n");
			}
			CSVDecisionFile fileIn = CSVDecisionFile.createFilteredCSVDecisionFile(sb.toString());
			log.info("Processed file input has "
					+ fileIn.getNumColumns()
					+ " columns, "
					+ fileIn.getNumLines()
					+ " lines.");

			assertEquals("File input after processing and file output must be the same", fileIn, fileOut);

		} catch (Exception e) {
			log.severe("Error performing test: " + e);
			assertEquals("Error performing test", true, false);

		} finally {
			try {
				if (brIn != null) {
					brIn.close();
				}
				if (brOut != null) {
					brOut.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}


	@Test
	public void csvTestCase1() {
		csvTestCaseN("exampleA_input.csv", "exampleA_output.csv");
	}


	@Test
	public void csvTestCase2() {
		csvTestCaseN("exampleB_input.csv", "exampleB_output.csv");
	}


	@Test
	public void csvTestCase3() {
		csvTestCaseN("exampleC_input.csv", "exampleC_output.csv");
	}
}