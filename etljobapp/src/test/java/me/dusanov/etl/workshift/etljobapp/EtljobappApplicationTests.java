package me.dusanov.etl.workshift.etljobapp;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import me.dusanov.etl.workshift.etljobapp.dto.ShiftDto;
import me.dusanov.etl.workshift.etljobapp.model.*;
import me.dusanov.etl.workshift.etljobapp.repo.*;
import me.dusanov.etl.workshift.etljobapp.service.ShiftService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.json.BasicJsonTester;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestConstructor;
import org.springframework.test.web.client.ExpectedCount;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.DefaultMockMvcBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.WebApplicationContext;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
//import javax.transaction.Transactional;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.List;
import java.util.TimeZone;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;

@ConfigurationProperties(prefix = "workshift.endpoint")
@ActiveProfiles("test")
@SpringBootTest
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@RequiredArgsConstructor//(onConstructor_ = @Autowired)
class EtljobappApplicationTests {

	private final RestTemplate restTemplate = new RestTemplate();
	private final WebApplicationContext wac;
	private final ShiftService shiftService;
	private final ShiftRepo shiftRepo;
	private final AwardInterpretationRepo awRepo;
	private final BreakRepo breakRepo;
	private final AllowanceRepo allowanceRepo;
	private final BatchRepo batchRepo;
	private final ShiftFailedRepo shiftFailedRepo;
  @PersistenceContext//(unitName="etlJobAppTests")
  private final EntityManager entitymanager;

	@Value("classpath:/shift_data_326872_example.json")
	Resource jsonFile;
	@Getter
	@Setter
	private String url;
	private final Batch batch = new Batch();

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
		assertEquals(HttpStatus.OK, resp.getStatusCode());
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
		ShiftDto[] shifts = mapper.readValue(new File(jsonFile.getURI()),ShiftDto[].class);
		shiftService.saveShift(shifts[0],this.batch);
		assertEquals(1, ((List<Shift>)shiftRepo.findAll()).size());
		assertEquals(1, ((List<Break>)breakRepo.findAll()).size());
		assertEquals(1, ((List<Allowance>)allowanceRepo.findAll()).size());
		assertEquals(4, ((List<AwardInterpretation>)awRepo.findAll()).size());
	}

	@Test
	@Transactional
	void testRollbackBySavingTwice() throws Exception {
		//sanity check
		assertEquals(0, ((List<Shift>)shiftRepo.findAll()).size());
		assertEquals(0, ((List<Break>)breakRepo.findAll()).size());
		assertEquals(0, ((List<Allowance>)allowanceRepo.findAll()).size());
		assertEquals(0, ((List<AwardInterpretation>)awRepo.findAll()).size());

		ShiftDto[] shifts = mapper.readValue(new File(jsonFile.getURI()),ShiftDto[].class);
		shiftService.saveShift(shifts[0],batch);

		assertEquals(1, ((List<Shift>)shiftRepo.findAll()).size());
		assertEquals(1, ((List<Break>)breakRepo.findAll()).size());
		assertEquals(1, ((List<Allowance>)allowanceRepo.findAll()).size());
		assertEquals(4, ((List<AwardInterpretation>)awRepo.findAll()).size());

		try{
			shiftService.saveShift(shifts[0],batch);
		} catch (Exception e){
			assertTrue(e.getMessage().contains("A different object with the same identifier value was already associated with the session"));
			//assertTrue(e.getMessage().contains("something completely different"));
		}

		assertEquals(1, ((List<Shift>)shiftRepo.findAll()).size());
		assertEquals(1, ((List<Break>)breakRepo.findAll()).size());
		assertEquals(1, ((List<Allowance>)allowanceRepo.findAll()).size());
		assertEquals(4, ((List<AwardInterpretation>)awRepo.findAll()).size());
	}

	@Test
	@Transactional
	void testRollbackByDeletingTheShiftRecord() throws Exception {
		ShiftDto[] shifts = mapper.readValue(new File(jsonFile.getURI()),ShiftDto[].class);
		Shift shift = shiftService.saveShift(shifts[0],batch);

		assertEquals(1, ((List<Shift>)shiftRepo.findAll()).size());
//		assertEquals(1, ((List<Break>)breakRepo.findAll()).size());
//		assertEquals(1, ((List<Allowance>)allowanceRepo.findAll()).size());
//		assertEquals(4, ((List<AwardInterpretation>)awRepo.findAll()).size());

		shiftRepo.delete(shift);

		assertEquals(0, ((List<Shift>)shiftRepo.findAll()).size());
//		assertEquals(1, ((List<Break>)breakRepo.findAll()).size());
//		assertEquals(1, ((List<Allowance>)allowanceRepo.findAll()).size());
//		assertEquals(4, ((List<AwardInterpretation>)awRepo.findAll()).size());

		try{
			shiftService.saveShift(shifts[0],batch);
		} catch (Exception e){
			System.out.println(e.getMessage());
			assertTrue(e.getMessage().contains("A different object with the same identifier value was already associated with the session"));
			//assertTrue(e.getMessage().contains("something completely different"));
		}

		assertEquals(0, ((List<Shift>)shiftRepo.findAll()).size());
//		assertEquals(1, ((List<Break>)breakRepo.findAll()).size());
//		assertEquals(1, ((List<Allowance>)allowanceRepo.findAll()).size());
//		assertEquals(4, ((List<AwardInterpretation>)awRepo.findAll()).size());

	}

	@Test
	@Transactional
	void testConvertTimestamp() throws Exception {
		ShiftDto[] shifts = mapper.readValue(new File(jsonFile.getURI()),ShiftDto[].class);
		ShiftDto shiftDto = shifts[0];
		Shift shift = shiftService.saveShift(shiftDto,batch);
		assertEquals(1595526660 * 1000L,shift.getStart().getTime());

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		sdf.setTimeZone(TimeZone.getTimeZone("America/New_York"));

		assertEquals("2020-07-23 13:51:00",sdf.format(shift.getStart()));
	}

	@Test
	@Transactional
	void testBatchCreation (){
		Batch b = batchRepo.save(new Batch());
		assertEquals(b.getId(), batchRepo.findById(b.getId()).get().getId());
	}

	@Test
	@Transactional
	void testBatchPersistWithRollback () throws IOException {
		Batch batch1 = batchRepo.save(new Batch());
		ShiftDto[] shifts = mapper.readValue(new File(jsonFile.getURI()),ShiftDto[].class);
		shiftService.saveBatch(batch1, Arrays.asList(shifts));

		assertEquals(1,((List<Batch>)batchRepo.findAll()).size());
		//test shift failed
		assertEquals(0, ((List<BatchShiftFailed>)shiftFailedRepo.findAll()).size());
		assertEquals(1, ((List<Shift>)shiftRepo.findAll()).size());
		assertEquals(1, ((List<Break>)breakRepo.findAll()).size());
		assertEquals(1, ((List<Allowance>)allowanceRepo.findAll()).size());
		assertEquals(4, ((List<AwardInterpretation>)awRepo.findAll()).size());

		try {
			Batch batch2 = batchRepo.save(new Batch());
			shiftService.saveBatch(batch2, Arrays.asList(shifts));
		}
		catch (Exception e){
			assertTrue(e.getMessage().contains("A different object with the same identifier value was already associated with the session"));
		}

		assertEquals(2,((List<Batch>)batchRepo.findAll()).size());
		//test shift failed
		assertEquals(1, ((List<BatchShiftFailed>)shiftFailedRepo.findAll()).size());
		assertEquals(1, ((List<Shift>)shiftRepo.findAll()).size());
		assertEquals(1, ((List<Break>)breakRepo.findAll()).size());
		assertEquals(1, ((List<Allowance>)allowanceRepo.findAll()).size());
		assertEquals(4, ((List<AwardInterpretation>)awRepo.findAll()).size());

	}
}
