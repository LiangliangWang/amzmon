/**
 * 
 */
package amzmon;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author Leo
 * 
 */
public class Crawler {
	public static final String LOGFILE_PATH = "logs\\crawler.log";
	public static final String DATA_DIR = "data\\";

	public String startUrl;
	public int itemsLimit;
	public int itemsPerPage;

	private Date startDate = new Date();
	private PrintWriter logPrintWriter;
	private PrintWriter dataPrintWriter;
	private PrintWriter outOfStockWriter;
	private Parser parser;

	public Crawler(String url, int limit, int ipp) {
		init_log();
		init_data();
		this.startUrl = url;
		this.itemsLimit = limit;
		this.itemsPerPage = ipp;
		this.parser = new Parser();

		logPrintWriter.println(startDate + ": Crawler initialized.");
		logPrintWriter.println("Starting URL: " + url);
		logPrintWriter.println("Item limit: " + limit);
		logPrintWriter.flush();

		System.out.println(startDate + ": Crawler initialized.");
		System.out.println("Starting URL: " + url);
		System.out.println("Item limit: " + limit);
	}

	private void init_log() {
		File logFile = new File(LOGFILE_PATH);
		try {
			logFile.createNewFile();
			this.logPrintWriter = new PrintWriter(new BufferedWriter(
					new FileWriter(logFile, true)));
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("Failed to open log file.");
		}
	}

	private void init_data() {
		SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy");
		File dataFile = new File(DATA_DIR + sdf.format(startDate) + ".txt");
		File outOfStockFile = new File(DATA_DIR + sdf.format(startDate)
				+ "_out_stock.txt");

		try {
			dataFile.createNewFile();
			outOfStockFile.createNewFile();
			this.dataPrintWriter = new PrintWriter(new BufferedWriter(
					new FileWriter(dataFile, true)));
			this.outOfStockWriter = new PrintWriter(new BufferedWriter(
					new FileWriter(outOfStockFile, true)));
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("Failed to create data file");
		}
	}

	public void start() {
		System.out.println(new Date() + ": Crawler is started.");

		int itemParsed = 0;
		int pageNum = 1;
		int trial = 0;
		int outOfStockIndex = 0;
		while (itemParsed < itemsLimit) {
			try {
				Item[] items = parser.getSinglePageItems(startUrl, pageNum,
						itemsPerPage);
				for (int i = 0; i < itemsPerPage; i++) {
					if (items[i] != null) {
						dataPrintWriter.println(new Integer(itemParsed + i)
								+ ".");
						items[i].writeItem(dataPrintWriter);
						if (items[i].isOutOfStock()) {
							outOfStockWriter.println(outOfStockIndex + ".");
							items[i].writeItem(outOfStockWriter);
							outOfStockIndex++;
						}
					}
				}
				dataPrintWriter.flush();
				outOfStockWriter.flush();
				itemParsed += itemsPerPage;
				pageNum++;
				trial = 0;

				if (pageNum % 10 == 0) {
					logPrintWriter.println(new Date() + ": " + pageNum
							+ " pages processed.");
					logPrintWriter.flush();
					System.out.println(new Date() + ": " + pageNum
							+ " pages processed.");
				}
			} catch (IOException e) {
				e.printStackTrace();
				trial++;
				if (trial >= 3) {
					logPrintWriter.println(new Date()
							+ ": Failed after processed " + itemParsed
							+ " items.");
					logPrintWriter.flush();
					System.out.println(new Date() + ": Failed after processed "
							+ itemParsed + " items.");
					return;
				}
			}
		}
		logPrintWriter.println(new Date() + ": Finished task.");
		logPrintWriter.flush();
		System.out.println(new Date() + ": Crawler is terminated.");
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		String elec_search_url = "http://www.amazon.com/s/ref=nb_sb_noss?rh=n%3A172282&page=";
		Crawler crawler = new Crawler(elec_search_url, 10000, 24);
		crawler.start();
	}
}
