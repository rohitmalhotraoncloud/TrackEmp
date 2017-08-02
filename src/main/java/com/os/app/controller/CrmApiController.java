package com.os.app.controller;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.os.app.beans.RestTemplateFactory;
import com.os.app.dto.PartyDTO;

@RestController
@RequestMapping("/api/crm")
public class CrmApiController extends CommonController {

	public static String BASE_URL = "https://tpmoutdoor.capsulecrm.com/api";
	@Autowired
	RestTemplateFactory restTemplateFactory;

	@RequestMapping(value = { "/party" }, method = RequestMethod.GET)
	public ResponseEntity<?> getParty(@RequestParam(required = false) String q) {
		String url = "/party";
		if (q != null) {
			url = url + "?q=" + q;
		}
		PartyDTO response = restTemplateFactory.getObject().getForObject(BASE_URL + url, PartyDTO.class);
		return new ResponseEntity<Map<String, Object>>(createResponse("parties", response), HttpStatus.OK);
	}

	@RequestMapping(value = { "opportunity/{opportunityId}/history" }, method = RequestMethod.POST)
	public ResponseEntity<?> createHistoryOnPartyAndOpportunity(@PathVariable Long opportunityId,
			@RequestBody HashMap<String, Object> req) {

		String content = (String) req.get("content");

		Map<String, Object> payload = new HashMap<String, Object>();
		Map<String, String> item = new HashMap<String, String>();
		item.put("note", content);
		payload.put("historyItem", item);

		URI resOpt = restTemplateFactory.getObject()
				.postForLocation(BASE_URL + "/opportunity/" + opportunityId + "/history", payload);

		return new ResponseEntity<Map<String, Object>>(createResponse("success", true), HttpStatus.OK);
	}

}