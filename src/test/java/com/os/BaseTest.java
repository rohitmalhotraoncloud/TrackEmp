package com.os;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.content;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.function.Function;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.junit.After;
import org.junit.Before;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.test.web.client.ResponseActions;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.RequestPostProcessor;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.collect.Lists;
import com.os.Application;
import com.os.app.authentication.RestAuthenticationFailureHandler;
import com.os.app.authentication.StatelessLoginFilter;
import com.os.app.authentication.TokenAuthenticationService;
import com.os.app.authentication.TokenHandler;
import com.os.app.authentication.UsernamePasswordSubdomainAuthenticationToken;
import com.os.app.beans.RestTemplateFactory;
import com.os.app.config.SimpleCORSFilter;
import com.os.app.dto.BaseDTO;
import com.os.app.entity.Activity;
import com.os.app.entity.BaseEntity;
import com.os.app.entity.Company;
import com.os.app.entity.SystemUser;
import com.os.app.entity.Trip;
import com.os.app.entity.UserDevice;
import com.os.app.enums.Role;
import com.os.app.enums.TimesheetActivityStatus;
import com.os.app.enums.TimesheetPeriodType;
import com.os.app.repository.ActivityRepository;
import com.os.app.repository.CompanyRepository;
import com.os.app.repository.SystemUserRepository;
import com.os.app.repository.TripRepository;
import com.os.app.repository.UserDeviceRepository;
import com.os.app.service.MessageByLocaleService;
import com.os.app.service.UserAuthenticationService;
import com.os.app.utils.RestTemplateExecutor;
import ch.qos.logback.classic.Logger;

@SpringApplicationConfiguration(classes = { Application.class })
@WebAppConfiguration
@SuppressWarnings("javadoc")
@ActiveProfiles("test")
public class BaseTest {

	public static final String REFERENCE_ID = "REFERENCE_ID";

	public static final ObjectMapper mapper = new ObjectMapper();

	public static final String PERSON_OR_ORG_NAME = "PERSON_OR_ORG_NAME";

	public static final String PASSWORD = "123";

	private static final String ENCRYPTED_PASSWORD = new BCryptPasswordEncoder().encode(PASSWORD);

	public static final String USER_EMAIL = "ca1@email.com";

	@Autowired
	SimpleCORSFilter simpleCorsFilter;

	public static final String ACTIVITY_METADATA = "ACTIVITY_METADATA";

	public static final String SUB_DOMAIN = "sub";

	public static final String USER = "user";

	protected static final String DEVICE_PLATFORM = "iOS";

	protected static final String PHONE_MODEL = "PHONE_MODEL";

	protected static final String DEVICE_NAME = "DEVICE_NAME";

	protected static final String PHONE_VERSION = "0.1";

	public static final String CHECK_IN_TYPE = "CHECK_IN_TYPE";

	public static final String COMPANY_NAME = "COMPANY_NAME";

	protected MockMvc mockMvc;

	@Autowired
	protected SystemUserRepository systemUserRepository;

	@Autowired
	private WebApplicationContext context;

	@Autowired
	protected CompanyRepository companyRepository;

	protected MockRestServiceServer mockServer;

	@Autowired
	RestTemplateFactory restTemplateFactory;

	@Autowired
	RestTemplateExecutor restTemplateExecutor;

	@Autowired
	AuthenticationManager authenticationManager;

	@Autowired
	MessageByLocaleService messageByLocaleService;

	@Autowired
	private UserAuthenticationService userAuthService;

	@Autowired
	private TokenAuthenticationService tokenAuthenticationService;

	private StatelessLoginFilter statelessLoginFilter;

	@Autowired
	protected ActivityRepository activityRepository;

	@Autowired
	protected UserDeviceRepository userDeviceRepository;

	@Autowired
	protected TripRepository tripRepository;

	@Resource
	private FilterChainProxy springSecurityFilterChain;

	@Value("${application.fileSystemPath:/var/tmp}")
	protected String tempFilePath;

	@Autowired
	private TokenHandler tokenHandler;

	@Value("${google.maps.api.key}")
	protected String googleMapAPIKey;

	protected static Logger logger = (Logger) LoggerFactory.getLogger(BaseTest.class);

	public BaseTest() {

	}

	@PostConstruct
	public void postConstruct() {
		this.statelessLoginFilter = new StatelessLoginFilter("/api/auth/login", tokenAuthenticationService,
				userAuthService, authenticationManager, new RestAuthenticationFailureHandler(), messageByLocaleService);
	}

	@Before
	public void before() {
		setupTestingContext();
	}

	protected void setupTestingContext() {
		// Clear the security context
		SecurityContextHolder.createEmptyContext();

		this.mockMvc = MockMvcBuilders.webAppContextSetup(this.context).addFilters(simpleCorsFilter)
				.addFilter(statelessLoginFilter, "/api/auth/login").addFilter(springSecurityFilterChain)
				.defaultRequest(get("/").contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
				.build();

		restartMockServer();
		clearTheTmpDirectory();
	}

	private void clearTheTmpDirectory() {
		File tmpDirectory = new File(tempFilePath);
		if (!tmpDirectory.exists()) {
			tmpDirectory.mkdirs();
		}
		File[] listOfFiles = tmpDirectory.listFiles();
		if (null == listOfFiles) {
			return;
		}
		for (File files : listOfFiles) {
			files.delete();
		}
	}

	@After
	public void after() {
		clearDB();
	}

	protected void clearDB() {
		logger.info("Clearing the DB");
		this.activityRepository.deleteAll();
		this.tripRepository.deleteAll();
		this.userDeviceRepository.deleteAll();
		this.activityRepository.deleteAll();
		this.systemUserRepository.deleteAll();
		this.companyRepository.deleteAll();
	}

	/**
	 * 
	 */
	protected void restartMockServer() {
		RestTemplate restTemplate = new RestTemplate();
		this.mockServer = MockRestServiceServer.createServer(restTemplate);
		restTemplateFactory.setRestTemplate(restTemplate);
		restTemplateExecutor.setRestTemplate(restTemplate);
	}

	protected List<SystemUser> getAllSystemUsers() {
		return Lists.newArrayList(this.systemUserRepository.findAll());
	}

	protected SystemUser setupUser(String username, Role role) {
		return this.setupUser(username, role, SUB_DOMAIN);
	}

	protected SystemUser setupUser(String username, Role role, String companySubDomain) {
		SystemUser user = getSystemUser(username, companySubDomain);
		user.setRole(role);
		return this.systemUserRepository.save(user);
	}

	private SystemUser getSystemUser(String username) {
		return getSystemUser(username, SUB_DOMAIN);
	}

	protected SystemUser getSystemUser(String username, String subDomain) {
		SystemUser user = this.systemUserRepository.findOneByUsernameAndSubDomain(username, subDomain);
		if (null != user) {
			return user;
		}
		user = new SystemUser();
		user.setPassword(ENCRYPTED_PASSWORD);
		user.setEmail(USER_EMAIL);
		user.setUsername(username);
		user.setFirstName("firstname");
		user.setSubdomain(subDomain);
		Company companyInfo = getCompanyInfo(subDomain);
		user.setCompany(companyInfo);
		user.setIsActive(true);
		user.setRole(Role.STAFF);
		user.setIsDeleted(false);
		user.setProfilePhoto("http://www.googleimage.com?name=" + username);
		return user;
	}

	protected SystemUser getSystemUser(String username, String subDomain, Function<SystemUser, SystemUser> mutator) {
		return mutator.apply(getSystemUser(username, subDomain));
	}

	protected SystemUser setupUser(Role role) {
		SystemUser user = getAdminSystemUser();
		user.setRole(role);
		return this.systemUserRepository.save(user);
	}

	private SystemUser getAdminSystemUser() {
		return getSystemUser(USER_EMAIL);
	}

	protected Company getCompanyInfo(String subDomain) {

		Company returnCompany = getCompanyBySubDomain(subDomain);
		if (null != returnCompany) {
			return returnCompany;
		}
		returnCompany = new Company();
		returnCompany.setSubDomainName(subDomain);
		returnCompany.setHasCrmAddon(true);
		returnCompany.setCompanyName(COMPANY_NAME + Calendar.getInstance().getTimeInMillis());
		returnCompany.setMaxRadiusLocationUpdate(12);
		returnCompany.setAutomatedCheckOutRadiusMetres(12);
		returnCompany = this.companyRepository.save(returnCompany);
		return this.companyRepository.save(returnCompany);
	}

	private Company getCompanyBySubDomain(String subDomain) {
		return this.companyRepository.findBySubDomainNameIgnoreCase(subDomain);
	}

	protected static ObjectNode getEntityAsKeyValue(final String fieldName, final BaseDTO<?, ?> entity) {
		final ObjectNode returnNode = mapper.createObjectNode();
		returnNode.set(fieldName, mapper.convertValue(entity, JsonNode.class));
		return returnNode;
	}

	protected static String getEntityAsKeyValue(final String fieldName, final BaseEntity<?> entity) {
		final ObjectNode returnNode = mapper.createObjectNode();
		returnNode.set(fieldName, mapper.convertValue(entity, JsonNode.class));
		try {
			return mapper.writeValueAsString(returnNode);
		} catch (JsonProcessingException e) {
			logger.error("Could not convert");
			throw new RuntimeException(e);
		}
	}

	protected List<Activity> getAllActivities() {
		return Lists.newArrayList(this.activityRepository.findAll());
	}

	protected List<UserDevice> getAllUserDevice() {
		return Lists.newArrayList(this.userDeviceRepository.findAll());
	}

	protected Long upsertUserDevice(SystemUser systemUser) {
		List<UserDevice> userDeviceList = this.userDeviceRepository.findUserDeviceByOwnerId(systemUser.getId());
		if (0 != userDeviceList.size()) {
			return userDeviceList.get(0).getId();
		}
		UserDevice userDevice = new UserDevice();
		userDevice.setModel(PHONE_MODEL);
		userDevice.setOwner(systemUser);
		return this.userDeviceRepository.save(userDevice).getId();
	}

	private static Trip getTrip(final SystemUser systemUser) {
		final Trip trip = new Trip();
		trip.setCreatedTimestamp(Calendar.getInstance().getTime());
		trip.setExpectedEndTime(Calendar.getInstance().getTime());
		trip.setEndTime(Calendar.getInstance().getTime());
		trip.setCompanyId(systemUser.getCompany().getId());
		trip.setCreator(systemUser);
		return trip;
	}

	protected static String formatDate(Date date, String pattern) {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
		return simpleDateFormat.format(date);
	}

	protected static <T> ArrayList<T> asList(Iterable<T> iterable) {
		return Lists.newArrayList(iterable);
	}

	protected List<Company> getAllCompanies() {
		return Lists.newArrayList(this.companyRepository.findAll());
	}

	protected static void setSecurityContextToUser(SystemUser user) {
		SecurityContext context = SecurityContextHolder.createEmptyContext();
		if (null != user) {
			UsernamePasswordSubdomainAuthenticationToken usernamePasswordSubdomainAuthenticationToken = new com.os.app.authentication.UsernamePasswordSubdomainAuthenticationToken(
					user.getUsername(), user.getUsername(), user.getCompany().getSubDomainName(),
					Arrays.asList(new Role[] { user.getRole() }));
			usernamePasswordSubdomainAuthenticationToken.setDetails(user);
			context.setAuthentication(usernamePasswordSubdomainAuthenticationToken);
		}
		SecurityContextHolder.setContext(context);
	}

	protected ResponseActions mockSendingEmail(SystemUser user) {
		return this.mockServer.expect(requestTo("https://api.postmarkapp.com/email/withTemplate"))
				.andExpect(method(HttpMethod.POST)).andExpect(content().string(containsString(user.getEmail())))
				.andExpect(content().string(containsString(user.getFirstName())))
				.andExpect(content().string(containsString("templateId")))
				.andExpect(content().string(containsString(user.getEmail())))
				.andExpect(content().string(containsString(user.getCompany().getSubDomainName())));
	}

	protected RequestPostProcessor addTokenAsUser(SystemUser user) {
		return new RequestPostProcessor() {
			@Override
			public MockHttpServletRequest postProcessRequest(MockHttpServletRequest request) {
				String token = tokenHandler.createTokenForUser(user);
				request.addHeader("Authorization", "Bearer " + token);
				return request;
			}
		};
	}

	public static Calendar toCalendar(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		return calendar;
	}

}
