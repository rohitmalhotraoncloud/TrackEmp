package com.os.app.controller;

import java.util.HashMap;
import java.util.Map;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.os.app.dto.CompanyDTO;
import com.os.app.dto.EmployeeCountDTO;
import com.os.app.entity.Company;
import com.os.app.service.CompanyService;
import com.os.app.service.EmployeeService;
import com.os.app.service.SystemUserService;
import com.os.app.validator.EmployeeValidator;

@RestController
@RequestMapping("/api")
public class CompanyController extends CommonController {

	@Autowired
	CompanyService companyService;
	@Autowired
	SystemUserService systemUserService;
	@Autowired
	EmployeeService employeeService;

	@Autowired
	EmployeeValidator employeeValidator;

	@PreAuthorize("hasAnyAuthority('SYSTEMADMIN','COMPANYADMIN')")
	@RequestMapping(value = { "/company" }, method = RequestMethod.POST)
	public ResponseEntity<?> createCompany(@Valid @RequestBody Company company) {

		return new ResponseEntity<Map<String, Object>>(
				createResponse("company", new CompanyDTO(companyService.saveCompany(company))), HttpStatus.OK);

	}

	@PreAuthorize("hasAuthority('COMPANYADMIN')")
	@RequestMapping(value = { "/company/{id}" }, method = RequestMethod.PUT)
	public ResponseEntity<?> updateCompany(@PathVariable long id, @RequestBody HashMap<String, CompanyDTO> companyObj,
			Errors errors) {
		CompanyDTO companyDTO = companyObj.get("company");
		Company company = companyDTO.toCompany();
		return new ResponseEntity<Map<String, Object>>(
				createResponse("company", new CompanyDTO(companyService.patch(id, company))), HttpStatus.OK);

	}

	@PreAuthorize("hasAnyAuthority('SYSTEMADMIN','COMPANYADMIN')")
	@RequestMapping(value = { "/companyUserCount{s*}" }, method = RequestMethod.GET, params = { "company" })
	public ResponseEntity<EmployeeCountDTO> getcompanyUserCount(@RequestParam long company) {

		return new ResponseEntity<EmployeeCountDTO>(companyService.getCompanyUserCount(getCurrentUser(), company),
				HttpStatus.OK);

	}

	@PreAuthorize("hasAnyAuthority('SYSTEMADMIN','COMPANYADMIN','STAFF')")
	@RequestMapping(value = { "/company/{id}", "/companies/{id}" }, method = RequestMethod.GET)
	public ResponseEntity<?> getCompany(@PathVariable Long id) {

		return new ResponseEntity<Map<String, Object>>(
				createResponse("company",
						new CompanyDTO(companyService.findCompanyByIdAndAccountOwnerId(id, getCurrentUser()))),
				HttpStatus.OK);
	}

	@RequestMapping(value = { "/company" }, method = RequestMethod.GET)
	public ResponseEntity<?> getCompanies() {

		return new ResponseEntity<Map<String, Object>>(createResponse("companies", companyService.findCompaniesDto()),
				HttpStatus.OK);

	}

}
