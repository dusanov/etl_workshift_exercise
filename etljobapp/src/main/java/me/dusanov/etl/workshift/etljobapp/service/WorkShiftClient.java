package me.dusanov.etl.workshift.etljobapp.service;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import me.dusanov.etl.workshift.etljobapp.dto.ShiftDto;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;

@Service
@ConfigurationProperties(prefix = "workshift.endpoint")
@RequiredArgsConstructor
public class WorkShiftClient {

    @Getter @Setter private String url;

    private final RestTemplate restTemplate = new RestTemplate();

    public ShiftDto get(Integer id){
        ShiftDto dto = restTemplate.getForObject(url+"/" + id, ShiftDto.class);
        return dto;
    }

    public List<ShiftDto> getSome(String ids){
        ShiftDto[] dtos = restTemplate.getForObject(url+"?ids="+ids, ShiftDto[].class);
        return Arrays.asList(dtos);
    }

    public List<ShiftDto> getAll(){
        ShiftDto[] dtos = restTemplate.getForObject(url, ShiftDto[].class);
        return Arrays.asList(dtos);
    }
}
