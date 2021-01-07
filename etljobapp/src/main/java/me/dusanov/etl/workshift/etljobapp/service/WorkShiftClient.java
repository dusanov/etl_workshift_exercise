package me.dusanov.etl.workshift.etljobapp.service;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import me.dusanov.etl.workshift.etljobapp.dto.ShiftDto;
import me.dusanov.etl.workshift.etljobapp.model.Batch;
import me.dusanov.etl.workshift.etljobapp.model.BatchExtractFailed;
import me.dusanov.etl.workshift.etljobapp.repo.ExtractFailedRepo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
@ConfigurationProperties(prefix = "workshift.endpoint")
@RequiredArgsConstructor
@Configuration
public class WorkShiftClient {

    private static final Logger log = LoggerFactory.getLogger(WorkShiftClient.class);

    @Autowired private ExtractFailedRepo extractFailedRepo;
    @Autowired private RestTemplate restTemplate;
    @Bean
    public RestTemplate restTemplate(){
        return new RestTemplate();
    }

    @Getter @Setter private String url;

    public ShiftDto get(Batch batch, Integer id){
        ShiftDto result = new ShiftDto();
        final String url = this.url + "/" + id;
        try { result = restTemplate.getForObject(url, ShiftDto.class); }
        catch (RuntimeException e){
            logError(batch, url, e);
        }
        return result;
    }

    public List<ShiftDto> getSome(Batch batch, String ids){
        List<ShiftDto> result = new ArrayList<>();
        final String url = this.url + "?ids=" + ids;
        try { result = Arrays.asList(restTemplate.getForObject(url, ShiftDto[].class)); }
        catch (RuntimeException e){
            logError(batch, url, e);
        }
        return result;
    }

    public List<ShiftDto> getAll(Batch batch){
        List<ShiftDto> result = new ArrayList<>();
        final String url = this.url;
        try { result = Arrays.asList(restTemplate.getForObject(url, ShiftDto[].class)); }
        catch (RuntimeException e){
            logError(batch, url, e);
        }
        return result;
    }

    private void logError(Batch batch, String url, RuntimeException e) {
        log.error("caught error in WorkShiftClient: " + e.getMessage());
        try {
            extractFailedRepo.save(
                    new BatchExtractFailed(null, e.getMessage(), url, batch.getId()));
            log.error("BatchShiftFailed has been saved");
        } catch (Exception fatal){
            log.error(String.format("fatal error happened while trying to extract for batchId %s: %s\nroot cause: %s",
                    batch.getId(), fatal.getMessage(), e.getMessage()), e);
        }
    }
}
