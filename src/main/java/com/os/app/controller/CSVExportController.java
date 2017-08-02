package com.os.app.controller;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.os.app.beans.CSVGenearteInputRequest;
import com.os.app.enums.ErrorKeys;
import com.os.app.exception.EmpTrackerException;
import com.os.app.service.CSVExportService;

import ch.qos.logback.classic.Logger;

@RestController
@RequestMapping(value = CommonController.API + "/csv")
public class CSVExportController extends CommonController {

	@Autowired
	CSVExportService csvExportService;

	private static Logger logger = (Logger) LoggerFactory.getLogger(CSVExportController.class);

	@RequestMapping(value = "/generate", method = RequestMethod.POST)
	@PreAuthorize("isAuthenticated()")
	public Map<String, URI> generateCSVFromFile(@RequestBody @Valid CSVGenearteInputRequest inputRequest,
			HttpServletResponse response) {
		// Need to create a file and upload to some place
		long randomLong = csvExportService.createFile(inputRequest);
		Map<String, URI> returnValue = new HashMap<String, URI>();
		returnValue.put("url", linkTo(CSVExportController.class).slash(randomLong).toUri());
		return returnValue;
	}

	@RequestMapping(value = "{randomLong}")
	@PreAuthorize("isAuthenticated()")
	public void downloadCSVFile(@PathVariable long randomLong, HttpServletResponse response) {
		String filepath = csvExportService.getAUniqueFileName(randomLong);
		try {
			InputStream is = new FileInputStream(filepath);
			org.apache.commons.io.IOUtils.copy(is, response.getOutputStream());
			response.flushBuffer();
		} catch (IOException ex) {
			logger.info("Error writing file to output stream. Filename was", filepath, ex);
			throw new EmpTrackerException(ErrorKeys.FILE_NOT_FOUND);
		}
	}

}
