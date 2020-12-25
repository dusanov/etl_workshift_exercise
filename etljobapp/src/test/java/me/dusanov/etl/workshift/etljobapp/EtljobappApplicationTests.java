package me.dusanov.etl.workshift.etljobapp;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import me.dusanov.etl.workshift.etljobapp.dto.ShiftDto;
import me.dusanov.etl.workshift.etljobapp.model.*;
import me.dusanov.etl.workshift.etljobapp.repo.*;
import me.dusanov.etl.workshift.etljobapp.service.WorkShiftService;
import org.junit.jupiter.api.*;
import org.mockito.Mock;
import org.mockito.Mockito;
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
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.WebApplicationContext;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;

@ConfigurationProperties(prefix = "workshift.endpoint")
@ActiveProfiles("test")
@SpringBootTest
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@RequiredArgsConstructor
class EtljobappApplicationTests {

	private final RestTemplate restTemplate = new RestTemplate();
	private final WebApplicationContext wac;
	private final WorkShiftService workShiftService;
	private final ShiftRepo shiftRepo;
	private final AwardInterpretationRepo awRepo;
	private final BreakRepo breakRepo;
	private final AllowanceRepo allowanceRepo;
	@Mock AllowanceRepo mockAllowanceRepo;
	private final BatchRepo batchRepo;
	private final ShiftFailedRepo shiftFailedRepo;

	@Value("classpath:/shift_data_326872_example.json")
	Resource jsonFile;
	@Value("classpath:/shift_data_326872_duplicate_AW.json")
	Resource jsonFileDuplicateAW;
	@Value("classpath:/shift_data_326872_gt_max_int.json")
	Resource jsonFileGtMaxInt;
	@Value("classpath:/shift_data_326872_break_it.json")
	Resource jsonFileBreakIt;
	@Getter
	@Setter
	private String url;
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

	/*//fake rollback 	 */
	@AfterEach
	private void cleanup() {
		batchRepo.deleteAll();
		shiftRepo.deleteAll();
		breakRepo.deleteAll();
		allowanceRepo.deleteAll();
		awRepo.deleteAll();
		shiftFailedRepo.deleteAll();
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
		assertEquals((int) resp.getBody()[0].getTimesheetId(), 47237);
		assertEquals(resp.getBody()[0].getCostBreakdown().getAwardCost(), 136.16046);
		assertEquals(resp.getBody()[0].getAwardInterpretation().get(0).getExportName(), "SOH");
		assertEquals(resp.getBody()[0].getAwardInterpretation().get(3).getCost(), 0.69);

	}

	@Test
	public void testBatchCreation (){
		Batch batch = batchRepo.save(new Batch());
		assertEquals(batch.getId(), batchRepo.findById(batch.getId()).get().getId());
		assertEquals(batch.getDateCreated().getTime(),batchRepo.findById(batch.getId()).get().getDateCreated().getTime());
	}

	@Test
	public void testShiftServiceSaveShift() throws Exception {
		//sanity check
		assertEquals(0, ((List<Shift>)shiftRepo.findAll()).size());
		assertEquals(0, ((List<Break>)breakRepo.findAll()).size());
		assertEquals(0, ((List<Allowance>)allowanceRepo.findAll()).size());
		assertEquals(0, ((List<AwardInterpretation>)awRepo.findAll()).size());

		ShiftDto[] shifts = mapper.readValue(new File(jsonFile.getURI()),ShiftDto[].class);
		Batch batch = new Batch();
		workShiftService.saveShift(shifts[0],batch);

		assertEquals(1, ((List<Shift>)shiftRepo.findAll()).size());
		assertEquals(1, ((List<Break>)breakRepo.findAll()).size());
		assertEquals(1, ((List<Allowance>)allowanceRepo.findAll()).size());
		assertEquals(4, ((List<AwardInterpretation>)awRepo.findAll()).size());
	}

	@Test
	public void testShiftServiceSaveBatch () throws IOException {
		ShiftDto[] shifts = mapper.readValue(new File(jsonFile.getURI()),ShiftDto[].class);
		workShiftService.executeBatch(Arrays.asList(shifts));

		assertEquals(1,((List<Batch>)batchRepo.findAll()).size());
		//test shift failed
		assertEquals(0, ((List<BatchShiftFailed>)shiftFailedRepo.findAll()).size());
		assertEquals(1, ((List<Shift>)shiftRepo.findAll()).size());
		assertEquals(1, ((List<Break>)breakRepo.findAll()).size());
		assertEquals(1, ((List<Allowance>)allowanceRepo.findAll()).size());
		assertEquals(4, ((List<AwardInterpretation>)awRepo.findAll()).size());
	}

	@Test
	public void testConvertTimestamp() throws Exception {
		ShiftDto[] shifts = mapper.readValue(new File(jsonFile.getURI()),ShiftDto[].class);
		ShiftDto shiftDto = shifts[0];
		Shift shift = workShiftService.saveShift(shiftDto,new Batch());
		assertEquals(1595526660 * 1000L,shift.getStart().getTime());
		assertEquals("2020-07-23 13:51:00",shift.getStart().toString());
	}

	@Test
	public void testDuplicateAW() throws Exception {
		ShiftDto[] shifts = mapper.readValue(new File(jsonFileDuplicateAW.getURI()),ShiftDto[].class);
		ShiftDto shiftDto = shifts[0];
		Shift shift = workShiftService.saveShift(shiftDto,new Batch());
		assertEquals(5, shiftDto.getAwardInterpretation().size());
		//we expect 4 records since there's one duplicate
		assertEquals(4, ((List<AwardInterpretation>)awRepo.findAll()).size());
	}

	@Test
	public void testDuplicateAllowance() throws Exception {
		ShiftDto[] shifts = mapper.readValue(new File(jsonFileDuplicateAW.getURI()),ShiftDto[].class);
		ShiftDto shiftDto = shifts[0];
		Shift shift = workShiftService.saveShift(shiftDto,new Batch());
		assertEquals(2, shiftDto.getAllowances().size());
		//we expect 1 records since there's one duplicate
		assertEquals(1, ((List<Allowance>)allowanceRepo.findAll()).size());
	}

	@Test
	public void testMaxIntFail() throws Exception {
		mockServer.expect(ExpectedCount.once(),
				requestTo(new URI("http://localhost:8080/api/v1/shifts/1")))
				.andExpect(method(HttpMethod.GET))
				.andRespond(withStatus(HttpStatus.OK)
						.contentType(MediaType.APPLICATION_JSON)
						.body(jsonTester.from(jsonFileGtMaxInt).getJson())
				);

		Assertions.assertThrows(RestClientException.class, () -> {
			ResponseEntity<ShiftDto[]> resp = restTemplate.getForEntity("http://localhost:8080/api/v1/shifts/1", ShiftDto[].class);
		});
		mockServer.verify();
	}

	@Test
	public void testDiffTypes() throws Exception {
		Assertions.assertThrows(InvalidFormatException.class, () -> {
			ShiftDto[] shifts = mapper.readValue(new File(jsonFileBreakIt.getURI()),ShiftDto[].class);
		});
	}

	@Test
	public void testBatchShiftFailedGetsSavedAfterException() throws IOException {
		assertEquals(0, ((List<BatchShiftFailed>)shiftFailedRepo.findAll()).size());

		ShiftDto[] shifts = mapper.readValue(new File(jsonFileDuplicateAW.getURI()),ShiftDto[].class);

		WorkShiftService service = new WorkShiftService(
				shiftRepo, shiftFailedRepo,batchRepo,breakRepo,awRepo,mockAllowanceRepo);

		Allowance allowance = new Allowance();
		List<Allowance> allowanceList = new ArrayList<Allowance>();
		allowanceList.add(allowance);
		Mockito.lenient()
				.when(mockAllowanceRepo.saveAll(Mockito.anyCollection()))
				.thenThrow(new RuntimeException("something horrible happened"));

		service.executeBatch(Arrays.asList( shifts ));
		assertEquals(1, ((List<BatchShiftFailed>)shiftFailedRepo.findAll()).size());
	}

}
