package com.os.app.service;

import static org.springframework.util.StringUtils.collectionToCommaDelimitedString;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.AgeFileFilter;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.os.app.beans.CSVGenearteInputRequest;
import com.os.app.enums.ErrorKeys;
import com.os.app.exception.EmpTrackerException;

@Service
public class CSVExportService extends CommonService {

	protected static final long HOUR = 60 * 60 * 1000;

	@Value("${application.fileSystemPath:/var/tmp}")
	private String tempFilePath;

	private File tempDirectory;

	@PostConstruct
	public void postConstruct() {
		this.tempDirectory = new File(this.tempFilePath);
	}

	public long createFile(CSVGenearteInputRequest inputRequest) {
		if (0 == inputRequest.getData().size()) {
			throw new EmpTrackerException(ErrorKeys.MISSING_PARAMETERS);
		}
		logger.info("Creating the csv file for " + inputRequest);

		long randomLong = Calendar.getInstance().getTimeInMillis();
		String file = getAUniqueFileName(randomLong);

		try (PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(file, true)))) {
			out.println(collectionToCommaDelimitedString(inputRequest.getColumns()));
			for (List<String> content : inputRequest.getData()) {
				out.println(collectionToCommaDelimitedString(content));
			}
		} catch (IOException e) {
			logger.error("Error while writing the content " + inputRequest + " to the file " + file, e);
			throw new EmpTrackerException(ErrorKeys.ERROR_READING_FILE);
		}
		return randomLong;
	}

	public String getAUniqueFileName(long randomLong) {
		return tempFilePath + "/" + getCurrentUser().getUsername() + "_" + randomLong + ".csv";
	}

	@Scheduled(fixedDelay = HOUR)
	public void deleteOlderFiles() {
		logger.info("Cleaning up the folder file from the folder " + this.tempFilePath);
		Date yesterday = getYesterday();
		AgeFileFilter fileFilter = new AgeFileFilter(yesterday);
	}

	private static Date getYesterday() {
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DATE, -1);
		return cal.getTime();
	}

}
