package me.dusanov.etl.workshift.etljobapp;

import com.fasterxml.jackson.databind.ObjectMapper;
import me.dusanov.etl.workshift.etljobapp.dto.ShiftDto;
import me.dusanov.etl.workshift.etljobapp.model.Allowance;
import me.dusanov.etl.workshift.etljobapp.model.AwardInterpretation;
import me.dusanov.etl.workshift.etljobapp.model.Break;
import me.dusanov.etl.workshift.etljobapp.model.Shift;
import me.dusanov.etl.workshift.etljobapp.repo.AllowanceRepo;
import me.dusanov.etl.workshift.etljobapp.repo.AwardInterpretationRepo;
import me.dusanov.etl.workshift.etljobapp.repo.BreakRepo;
import me.dusanov.etl.workshift.etljobapp.repo.ShiftRepo;
import me.dusanov.etl.workshift.etljobapp.service.ShiftService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.json.BasicJsonTester;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.client.ExpectedCount;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.DefaultMockMvcBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.WebApplicationContext;

import javax.transaction.Transactional;
import java.net.URI;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;

@ActiveProfiles("test")
@SpringBootTest
class EtljobappApplicationTests {
	
	@Autowired	private RestTemplate restTemplate;
	@Autowired	private WebApplicationContext wac;
	@Autowired	private ShiftService shiftService;
	@Autowired	private ShiftRepo shiftRepo;
	@Autowired	private AwardInterpretationRepo awRepo;
	@Autowired	private BreakRepo breakRepo;
	@Autowired	private AllowanceRepo allowanceRepo;

	@Value("classpath:/shift_data_326872_example.json")
	Resource jsonFile;

	private MockMvc mockMvc;
	private MockRestServiceServer mockServer;
	private final ObjectMapper mapper = new ObjectMapper();
	private final BasicJsonTester jsonTester = new BasicJsonTester(getClass());

	@BeforeEach
	public void setup () {
		this.mockServer = MockRestServiceServer.createServer(restTemplate);
		DefaultMockMvcBuilder builder = MockMvcBuilders.webAppContextSetup(this.wac);
		this.mockMvc = builder.build();
	}

	@Test
	void testMockedResponseFromAJsonFileToString() throws Exception {
		mockServer.expect(ExpectedCount.once(),
				requestTo(new URI("http://localhost:8080/api/v1/shifts/1")))
				.andExpect(method(HttpMethod.GET))
				.andRespond(withStatus(HttpStatus.OK)
						.contentType(MediaType.APPLICATION_JSON)
						.body(jsonTester.from(jsonFile).getJson())
				);

		ResponseEntity<String> resp = restTemplate.getForEntity("http://localhost:8080/api/v1/shifts/1", String.class);
		mockServer.verify();
		assertEquals(resp.getStatusCode(), HttpStatus.OK);
		assertTrue(jsonTester.from(resp.getBody()).getJson().contains("\"timesheet_id\":47237,"));

	}

	@Test
	void testMockedResponseFromAFileToDTO() throws Exception{
		mockServer.expect(ExpectedCount.once(),
				requestTo(new URI("http://localhost:8080/api/v1/shifts/1")))
				.andExpect(method(HttpMethod.GET))
				.andRespond(withStatus(HttpStatus.OK)
						.contentType(MediaType.APPLICATION_JSON)
						.body(jsonTester.from(jsonFile).getJson())
				);

		ResponseEntity<ShiftDto[]> resp = restTemplate.getForEntity("http://localhost:8080/api/v1/shifts/1", ShiftDto[].class);
		mockServer.verify();
		assertEquals(resp.getStatusCode(), HttpStatus.OK);
		assertNotNull(resp.getBody()[0]);

		assertNotNull(resp.getBody()[0].getTimesheetId());
		assertTrue(resp.getBody()[0].getTimesheetId().equals(47237));
		assertTrue(resp.getBody()[0].getCostBreakdown().getAwardCost().equals(136.16046));
		assertTrue(resp.getBody()[0].getAwardInterpretation().get(0).getExportName().equals("SOH"));
		assertTrue(resp.getBody()[0].getAwardInterpretation().get(3).getCost().equals(0.69));

	}

	@Test
	@Transactional
	void testShiftServiceSave() throws Exception {
		//sanity check
		/*
		assertEquals(((List<Shift>)shiftRepo.findAll()).size(), 0);
		assertEquals(((List<Break>)breakRepo.findAll()).size(), 0);
		assertEquals(((List<Allowance>)allowanceRepo.findAll()).size(), 0);
		assertEquals(((List<AwardInterpretation>)awRepo.findAll()).size(), 0);
		*/
		mockServer.expect(ExpectedCount.once(),
				requestTo(new URI("http://localhost:8080/api/v1/shifts/1")))
				.andExpect(method(HttpMethod.GET))
				.andRespond(withStatus(HttpStatus.OK)
						.contentType(MediaType.APPLICATION_JSON)
						.body(jsonTester.from(jsonFile).getJson())
				);

		ResponseEntity<ShiftDto[]> resp = restTemplate.getForEntity("http://localhost:8080/api/v1/shifts/1", ShiftDto[].class);
		mockServer.verify();
		assertEquals(resp.getStatusCode(), HttpStatus.OK);
		assertNotNull(resp.getBody()[0]);
		shiftService.save(resp.getBody()[0]);
		assertEquals(((List<Shift>)shiftRepo.findAll()).size(), 1);
		assertEquals(((List<Break>)breakRepo.findAll()).size(), 1);
		assertEquals(((List<Allowance>)allowanceRepo.findAll()).size(), 1);
		assertEquals(((List<AwardInterpretation>)awRepo.findAll()).size(), 4);
	}

	@Test
	@Transactional
	void testRollbackBySavingTwice() throws Exception {
		//sanity check
		assertEquals(((List<Shift>)shiftRepo.findAll()).size(), 0);
		assertEquals(((List<Break>)breakRepo.findAll()).size(), 0);
		assertEquals(((List<Allowance>)allowanceRepo.findAll()).size(), 0);
		assertEquals(((List<AwardInterpretation>)awRepo.findAll()).size(), 0);

		mockServer.expect(ExpectedCount.once(),
				requestTo(new URI("http://localhost:8080/api/v1/shifts/1")))
				.andExpect(method(HttpMethod.GET))
				.andRespond(withStatus(HttpStatus.OK)
						.contentType(MediaType.APPLICATION_JSON)
						.body(jsonTester.from(jsonFile).getJson())
				);

		ResponseEntity<ShiftDto[]> resp = restTemplate.getForEntity("http://localhost:8080/api/v1/shifts/1", ShiftDto[].class);
		mockServer.verify();
		assertEquals(resp.getStatusCode(), HttpStatus.OK);
		assertNotNull(resp.getBody()[0]);
		shiftService.save(resp.getBody()[0]);
		assertEquals(((List<Shift>)shiftRepo.findAll()).size(), 1);
		assertEquals(((List<Break>)breakRepo.findAll()).size(), 1);
		assertEquals(((List<Allowance>)allowanceRepo.findAll()).size(), 1);
		assertEquals(((List<AwardInterpretation>)awRepo.findAll()).size(), 4);

		try{
			shiftService.save(resp.getBody()[0]);
		} catch (Exception e){
			//not sure how good this test actually is
			assertNotNull(e);
			assertEquals(e.getMessage(),"Error on Shift Save !");
			assertEquals(((List<Shift>)shiftRepo.findAll()).size(), 1);
			assertEquals(((List<Break>)breakRepo.findAll()).size(), 1);
			assertEquals(((List<Allowance>)allowanceRepo.findAll()).size(), 1);
			assertEquals(((List<AwardInterpretation>)awRepo.findAll()).size(), 4);
		}
	}

	@Test
	@Transactional
	void testConvertTimestamp() throws Exception {
		//String dtoJsonString = jsonTester.from(jsonFile).getJson();
		//ShiftDto dto = mapper.convertValue(dtoJsonString.substring(1,dtoJsonString.length()-1),ShiftDto.class);
		mockServer.expect(ExpectedCount.once(),
				requestTo(new URI("http://localhost:8080/api/v1/shifts/1")))
				.andExpect(method(HttpMethod.GET))
				.andRespond(withStatus(HttpStatus.OK)
						.contentType(MediaType.APPLICATION_JSON)
						.body(jsonTester.from(jsonFile).getJson())
				);

		ResponseEntity<ShiftDto[]> resp = restTemplate.getForEntity("http://localhost:8080/api/v1/shifts/1", ShiftDto[].class);
		mockServer.verify();
		assertEquals(resp.getStatusCode(), HttpStatus.OK);
		assertNotNull(resp.getBody()[0]);
		ShiftDto shiftDto = resp.getBody()[0];
		Shift shift = shiftService.save(resp.getBody()[0]);
		assertEquals(1595526660 * 1000L,shift.getStart().getTime());
	}
}