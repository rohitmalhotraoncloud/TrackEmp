package com.os.app.controller;

import static org.hamcrest.Matchers.containsString;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.ResultActions;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.os.BaseTest;
import com.os.app.beans.CSVGenearteInputRequest;
import com.os.app.entity.SystemUser;
import com.os.app.enums.Role;

@RunWith(SpringJUnit4ClassRunner.class)
public class CSVExportControllerTest extends BaseTest {

	@Test
	public void whenUploadingWithutAuthorizationReturnUnauthorzed() throws Exception {
		this.mockMvc.perform(post("/api/csv/generate").content(getSomeData(5, 10)))
				.andExpect(status().isUnauthorized());
	}

	@Test
	public void whenUploadingAInvalidDataThenReturnBadRequest() throws Exception {
		SystemUser staff = this.setupUser(Role.STAFF);
		this.mockMvc.perform(post("/api/csv/generate").content("{}").with(this.addTokenAsUser(staff)))
				.andExpect(status().isBadRequest());
	}

	@Test
	public void whenUploadingValidJSONPayloadThenReturnTheURL() throws Exception {
		SystemUser staff = this.setupUser(Role.STAFF);
		ResultActions resultActions = this.mockMvc
				.perform(post("/api/csv/generate").with(this.addTokenAsUser(staff)).content(getSomeData(5, 10)))
				.andExpect(status().isOk()).andExpect(jsonPath("$.url").exists());
		File createdFile = getFileCountInPath(tempFilePath);
		assertNotNull(createdFile);
		String fileNameWithoutContext = createdFile.getName().replace(BaseTest.USER_EMAIL + "_", "").replace(".csv",
				"");
		resultActions.andExpect(jsonPath("$.url", containsString(fileNameWithoutContext)));
		assertEquals(11, countLines(createdFile.getAbsolutePath()));
	}

	@Test
	public void whenRequestingNonExistingFileThenReturnNotFound() throws Exception {
		SystemUser staff = this.setupUser(Role.STAFF);
		this.mockMvc.perform(get("/api/csv/123231").with(this.addTokenAsUser(staff))).andExpect(status().isNotFound());
	}

	@Test
	public void whenRequestingExistingFileThenReturnNotFound() throws Exception {
		SystemUser staff = this.setupUser(Role.STAFF);
		this.setSecurityContextToUser(staff);
		long randomLong = 1231L;
		String filePath = tempFilePath + "/" + SecurityContextHolder.getContext().getAuthentication().getName() + "_"
				+ randomLong + ".csv";
		new File(filePath).createNewFile();
		this.mockMvc.perform(get("/api/csv/" + randomLong).with(this.addTokenAsUser(staff))).andExpect(status().isOk());
	}

	public static int countLines(String filename) throws IOException {
		InputStream is = new BufferedInputStream(new FileInputStream(filename));
		try {
			byte[] c = new byte[1024];
			int count = 0;
			int readChars = 0;
			boolean empty = true;
			while ((readChars = is.read(c)) != -1) {
				empty = false;
				for (int i = 0; i < readChars; ++i) {
					if (c[i] == '\n') {
						++count;
					}
				}
			}
			return (count == 0 && !empty) ? 1 : count;
		} finally {
			is.close();
		}
	}

	private File getFileCountInPath(String tempFilePath) {
		File[] list = new File(tempFilePath).listFiles();
		if (null == list) {
			return null;
		}
		return list[0];
	}

	private static String getSomeData(int maxColumns, int maxDataCount) throws JsonProcessingException {
		List<String> columns = new ArrayList<String>();
		for (int i = 1; i <= maxColumns; i++) {
			columns.add("column_" + i);
		}
		List<List<String>> datas = new ArrayList<>();

		for (int dataCount = 1; dataCount <= maxDataCount; dataCount++) {
			List<String> data = new ArrayList<>();
			for (int i = 1; i <= maxColumns; i++) {
				data.add("data_" + dataCount + "_column_" + i);
			}
			datas.add(data);
		}
		CSVGenearteInputRequest csvGenearteInputRequest = new CSVGenearteInputRequest();

		csvGenearteInputRequest.setColumns(columns);
		csvGenearteInputRequest.setData(datas);
		return mapper.writeValueAsString(csvGenearteInputRequest);
	}

}
