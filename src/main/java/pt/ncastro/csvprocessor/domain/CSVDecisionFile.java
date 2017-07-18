package pt.ncastro.csvprocessor.domain;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import pt.ncastro.csvprocessor.util.domainHelpers.interfaces.IExchangeableByJSON;



public class CSVDecisionFile implements Serializable, IExchangeableByJSON {

	private static final long serialVersionUID = -3071722604891631890L;

	// table fields
	private final List<String> headers;
	private final List<CSVDecisionLine> records;
	private final int idColIdx;
	private final int decisionColIdx;


	/**
	 * Takes a CSV data string and builds its representation as a
	 * CSVDecisionFile.
	 * 
	 * @param csvData
	 * @throws IOException
	 * @throws IllegalArgumentException
	 */
	public CSVDecisionFile(String csvData) throws IOException, IllegalArgumentException {
		super();

		int idIdx = -1, decIdx = -1;

		// make sure we have a valid string
		String csvDataUTF8 = new String(csvData.getBytes(), "utf-8");
		csvDataUTF8 = new String(csvDataUTF8.getBytes(), "utf-8").replaceAll("\\?", "");

		headers = new ArrayList<String>();
		records = new ArrayList<CSVDecisionLine>();

		CSVParser parsed = CSVParser.parse(csvDataUTF8, CSVFormat.DEFAULT);
		// parse and populate the records
		List<CSVRecord> records = parsed.getRecords();

		// get the header and line values
		if (records != null && records.size() > 0) {
			CSVRecord record;
			String lcHeader, header;

			Iterator<String> valueStrs = records.get(0).iterator();
			while (valueStrs.hasNext()) {
				header = valueStrs.next();
				lcHeader = header.toLowerCase();
				if (lcHeader.equals("id")) {
					idIdx = headers.size();
				} else if (lcHeader.equals("decision")) {
					decIdx = headers.size();
				}
				headers.add(header);
			}
			// verify validity
			if (idIdx < 0 || decIdx < 0) {
				throw new RuntimeException("CSV file hasn't got either an Id or Decision column");
			}

			// get the lines
			int numColumns = headers.size();
			int idx;
			long[] values;
			String valueStr;
			for (int i = 1; i < records.size(); i++) {
				record = records.get(i);
				valueStrs = record.iterator();

				values = new long[numColumns];
				idx = 0;
				while (valueStrs.hasNext() && idx < numColumns) {
					valueStr = valueStrs.next();
					try {
						values[idx] = Long.parseLong(valueStr);

					} catch (NumberFormatException nfe) {
						throw new RuntimeException("CSV file column should be numerical");
					} catch (NullPointerException npe) {
						throw new RuntimeException("CSV file column shouldn't be empty");
					}
					idx++;
				}
				// ended, test if everything ok
				if (valueStrs.hasNext() || idx < numColumns) {
					throw new RuntimeException("CSV file line " + i + " is not correct");
				}

				this.records.add(new CSVDecisionLine(values));
			}
		}

		idColIdx = idIdx;
		decisionColIdx = decIdx;
	}


	/**
	 * Returns the file number of records.
	 * 
	 * @return
	 */
	public int getNumLines() {
		return records.size();
	}


	/**
	 * Returns the file number of columns.
	 * 
	 * @return
	 */
	public int getNumColumns() {
		return headers.size();
	}


	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof CSVDecisionFile)) {
			return false;
		}
		CSVDecisionFile other = (CSVDecisionFile) obj;
		if (!Arrays.equals(headers.toArray(), other.headers.toArray())) {
			return false;
		}
		if (!Arrays.equals(records.toArray(), other.records.toArray())) {
			return false;
		}
		return true;
	}


	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * pt.ncastro.csvprocessor.util.domainHelpers.interfaces.IExchangeableByJSON
	 * #toJSON()
	 */
	@Override
	public String toJSON() throws JSONException {
		JSONObject object = new JSONObject(this);
		JSONArray hs = new JSONArray(headers);
		JSONArray rc = new JSONArray();
		for (CSVDecisionLine line : records) {
			rc.put(line.toJSON());
		}
		object.put("headers", hs);
		object.put("records", rc);
		return object.toString();
	}


	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * pt.ncastro.csvprocessor.util.domainHelpers.interfaces.IExchangeableByJSON
	 * #fromJSON(java.lang.String)
	 */
	@Override
	public void fromJSON(String json) throws JSONException {
		// TODO Add code here if we need to have bidirectional communication with the client.
	}


	/**
	 * Takes a CSV data string and creates the result decision file to send back
	 * to the client, filtering it by the requested filter: decision = 1 or
	 * there is at least one of the variable values between their min and max
	 * calculated from the items with decision=1
	 * 
	 * @param csvData
	 * @return
	 * @throws IOException
	 * @throws IllegalArgumentException
	 */
	public static CSVDecisionFile createFilteredCSVDecisionFile(String csvData) throws IOException,
			IllegalArgumentException {
		int valsIdx;

		// load CSV file
		CSVDecisionFile file = new CSVDecisionFile(csvData);

		// initialize structures for filter
		int numColumns = file.headers.size() - 2; // we don't need mins and maxs from Id and Decision columns
		long[] mins = new long[numColumns];
		long[] maxs = new long[numColumns];
		for (int i = 0; i < numColumns; i++) {
			mins[i] = Long.MAX_VALUE;
			maxs[i] = Long.MIN_VALUE;
		}

		// get min and max values for each line column
		for (CSVDecisionLine line : file.records) {
			if (line.columnData[file.decisionColIdx] == 1) { // line to be taken into consideration
				valsIdx = 0;
				for (int i = 0; i < line.columnData.length; i++) {
					if (i != file.idColIdx && i != file.decisionColIdx) {
						if (mins[valsIdx] > line.columnData[i]) {
							mins[valsIdx] = line.columnData[i];
						}
						if (maxs[valsIdx] < line.columnData[i]) {
							maxs[valsIdx] = line.columnData[i];
						}
						valsIdx++;
					}
				}
			}
		}

		// remove items outside filtering limits
		CSVDecisionLine record;
		for (int i = file.records.size() - 1; i >= 0; i--) {
			record = file.records.get(i);
			if (record.columnData[file.decisionColIdx] == 0) { // potential match to remove, check internal values
				valsIdx = 0;
				for (int k = 0; k < record.columnData.length; k++) {
					if (k != file.idColIdx && k != file.decisionColIdx) {
						if (record.columnData[k] <= maxs[valsIdx] && record.columnData[k] >= mins[valsIdx]) {
							break;
						}
						valsIdx++;
					}
				}
				// validate if we didn't find any value within limits, and remove record if so
				if (valsIdx == maxs.length) {
					file.records.remove(i);
				}
			}
		}

		return file;
	}


	/**
	 * Representation of a single csv line.
	 * 
	 * @author Nuno de Castro
	 *
	 */
	private static class CSVDecisionLine implements IExchangeableByJSON {
		private final long[] columnData;


		/**
		 * Default constructor.
		 * 
		 * @param columnValues
		 */
		private CSVDecisionLine(long[] columnValues) {
			columnData = columnValues;
		}


		/*
		 * (non-Javadoc)
		 * 
		 * @see java.lang.Object#equals(java.lang.Object)
		 */
		@Override
		public boolean equals(Object obj) {
			if (this == obj) {
				return true;
			}
			if (obj == null) {
				return false;
			}
			if (!(obj instanceof CSVDecisionLine)) {
				return false;
			}
			CSVDecisionLine other = (CSVDecisionLine) obj;
			if (!Arrays.equals(columnData, other.columnData)) {
				return false;
			}
			return true;
		}


		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * pt.ncastro.csvprocessor.util.domainHelpers.interfaces.IExchangeableByJSON
		 * #toJSON()
		 */
		@Override
		public String toJSON() throws JSONException {
			return new JSONArray(columnData).toString();
		}


		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * pt.ncastro.csvprocessor.util.domainHelpers.interfaces.IExchangeableByJSON
		 * #fromJSON(java.lang.String)
		 */
		@Override
		public void fromJSON(String json) throws JSONException {
			// TODO Add code here if we need to have bidirectional communication with the client.			
		}
	}
}
