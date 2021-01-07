package me.dusanov.etl.workshift.etljobapp;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import me.dusanov.etl.workshift.etljobapp.config.EST_TZ_Date;
import me.dusanov.etl.workshift.etljobapp.dto.ShiftDto;
import me.dusanov.etl.workshift.etljobapp.etl.WorkShiftJob;
import me.dusanov.etl.workshift.etljobapp.model.*;
import me.dusanov.etl.workshift.etljobapp.repo.*;
import me.dusanov.etl.workshift.etljobapp.service.WorkShiftClient;
import me.dusanov.etl.workshift.etljobapp.service.WorkShiftService;
import org.junit.jupiter.api.*;
import org.mockito.InjectMocks;
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
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.WebApplicationContext;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.validateMockitoUsage;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
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
	private final BatchRepo batchRepo;
	private final ShiftFailedRepo shiftFailedRepo;

	@Mock ExtractFailedRepo extractFailedRepo;
	@Mock AllowanceRepo mockAllowanceRepo;
	@Mock BreakRepo mockBreakRepo;
	@Mock RestTemplate mockRestTemplate;
	@InjectMocks
	WorkShiftClient client;// = new WorkShiftClient(extractFailedRepo);

	@Mock WorkShiftClient mockWorkClient;
	@Mock WorkShiftService mockWorkService;

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
		//make sure that mocked up calls really happened
		validateMockitoUsage();
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
	public void testShiftServiceExecuteBatch () throws IOException {
		ShiftDto[] shifts = mapper.readValue(new File(jsonFile.getURI()),ShiftDto[].class);
		workShiftService.executeBatch(new Batch(),Arrays.asList(shifts));

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
		//assertEquals(4, ((List<AwardInterpretation>)awRepo.findAll()).size());
		// actually business reqs changed, we expect 5
		assertEquals(5, ((List<AwardInterpretation>)awRepo.findAll()).size());
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
	public void testExtractFail() throws Exception {

		assertEquals(0, ((List<BatchExtractFailed>) extractFailedRepo.findAll()).size());

		Mockito.when(mockRestTemplate.getForObject("null/1",ShiftDto.class))
				.thenThrow(new RuntimeException("something horrible happened"));

		Mockito.when(extractFailedRepo.save(Mockito.mock(BatchExtractFailed.class)))
				.thenReturn(Mockito.mock(BatchExtractFailed.class));

		Batch batch = workShiftService.createNewBatch(new Batch());
		client.get(batch,1);

		//null/1 as url is because config is not injected into test
		Mockito.verify(mockRestTemplate).getForObject("null/1",ShiftDto.class);
		Mockito.verify(extractFailedRepo).save(Mockito.any(BatchExtractFailed.class));
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
				shiftRepo,shiftFailedRepo,batchRepo,breakRepo,awRepo,mockAllowanceRepo);

		Mockito.lenient()
				.when(mockAllowanceRepo.saveAll(Mockito.anyCollection()))
				.thenThrow(new RuntimeException("something horrible happened"));

		Batch batch = new Batch();
		service.executeBatch(batch,Arrays.asList( shifts ));

		Mockito.verify(mockAllowanceRepo).saveAll(Mockito.anyCollection());

		List<BatchShiftFailed> failedOnes = (List<BatchShiftFailed>) shiftFailedRepo.findAll();
		assertEquals(1, failedOnes.size());

		BatchShiftFailed fail = failedOnes.get(0);
		assertEquals("something horrible happened", fail.getErrorMessage());
		assertEquals(batch.getId(), fail.getBatchId());
		assertEquals(shifts[0].getId(), fail.getShiftId());
		assertEquals(shifts[0], mapper.readValue(fail.getDto(),ShiftDto.class));

	}

	//Transactional can not work with the redis key type conversions for some reason
	//https://stackoverflow.com/questions/41264091/spring-data-redis-transactional-support-on-repository
	@Disabled
	@Test
	public void testRollback() throws IOException {
		// configure break repo mock to throw exception on saveAll
		//call workShiftService.executeBatch with the breakRepo mocked up
		//validate that the break repo mock was called
		// validate that the shift,allowances and award interpretations were rolled back
		// validate that the BatchShiftFailed has been saved
		// validate that the Batch has been saved

		assertEquals(0, ((List<Shift>)shiftRepo.findAll()).size());
		assertEquals(0, ((List<Allowance>)allowanceRepo.findAll()).size());
		assertEquals(0, ((List<AwardInterpretation>)awRepo.findAll()).size());
		assertEquals(0, ((List<Break>)breakRepo.findAll()).size());
		assertEquals(0, ((List<BatchShiftFailed>)shiftFailedRepo.findAll()).size());
		assertEquals(0, ((List<Batch>)batchRepo.findAll()).size());

		ShiftDto[] shifts = mapper.readValue(new File(jsonFileDuplicateAW.getURI()),ShiftDto[].class);

		WorkShiftService service = new WorkShiftService(
				shiftRepo,shiftFailedRepo,batchRepo,mockBreakRepo,awRepo,allowanceRepo);

		Mockito.lenient()
				.when(mockBreakRepo.saveAll(Mockito.anyCollection()))
				.thenThrow(new RuntimeException("something horrible happened"));

		Batch batch = service.createNewBatch(new Batch());
		service.executeBatch(batch,Arrays.asList( shifts ));

		Mockito.verify(mockBreakRepo).saveAll(Mockito.anyCollection());

		assertEquals(0, ((List<Shift>)shiftRepo.findAll()).size());
		assertEquals(0, ((List<Allowance>)allowanceRepo.findAll()).size());
		assertEquals(0, ((List<AwardInterpretation>)awRepo.findAll()).size());
		assertEquals(0, ((List<Break>)breakRepo.findAll()).size());
		assertEquals(1, ((List<BatchShiftFailed>)shiftFailedRepo.findAll()).size());
		assertEquals(1, ((List<Batch>)batchRepo.findAll()).size());
	}

	@Test
	public void testWorkShiftJobExecuteIdSelection() throws ParseException {

		List<Shift> mockedAllShifts = Arrays.asList(new Shift[]{new Shift(1,1,1,"",null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null),
																new Shift(2,1,1,"",null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null),
																new Shift(3,1,1,"",null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null)});

		List<BatchShiftFailed> mockedFailedShifts = Arrays.asList(new BatchShiftFailed[]{new BatchShiftFailed(4,"1","1","")});

		List<ShiftDto> mockedShiftDtos = Arrays.asList(new ShiftDto[]{new ShiftDto(1,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null),
																		new ShiftDto(2,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null),
																		new ShiftDto(3,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null),
																		new ShiftDto(4,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null),
																		new ShiftDto(5,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null),
																		new ShiftDto(6,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null),
																		new ShiftDto(7,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null)});

		Batch batch = new Batch();
		Mockito.when(mockWorkService.createNewBatch(Mockito.any()))
				.thenReturn(batch);

		Mockito.when(mockWorkService.getAll())
				.thenReturn(mockedAllShifts);

		Mockito.when(mockWorkService.getAllFailed())
				.thenReturn(mockedFailedShifts);

		Mockito.when(mockWorkClient.getAll(Mockito.any()))
				.thenReturn(mockedShiftDtos);

		final WorkShiftJob job = new WorkShiftJob(mockWorkService,mockWorkClient);

		job.execute();

		Mockito.verify(mockWorkService).getAll();
		Mockito.verify(mockWorkService).getAllFailed();
		Mockito.verify(mockWorkClient).getAll(Mockito.any());
		Mockito.verify(mockWorkClient).getSome(batch,"5,6,7");

	}

}
