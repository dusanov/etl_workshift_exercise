package me.dusanov.etl.workshift.workshiftendpoint.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.Setter;
import me.dusanov.etl.workshift.workshiftendpoint.model.AllowanceDto;
import me.dusanov.etl.workshift.workshiftendpoint.model.BreakDto;
import me.dusanov.etl.workshift.workshiftendpoint.model.Shift;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ConfigurationProperties(prefix = "workshift.service")
@Service
public class ShiftService implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(ShiftService.class);
    @Value("classpath:/shift_data_326872_example.json")
    private Resource jsonFile;
    private final ObjectMapper mapper = new ObjectMapper();
    @Getter @Setter private int numofshiftstocreate;
    @Getter @Setter private int minrandom;
    @Getter @Setter private int maxrandom;

    private Map<Integer, Shift> shifts = new HashMap<>();

    public List<Shift> getAll() {
        return new ArrayList<Shift>(shifts.values());
    }

    public Shift get(Integer shiftId) {
        return shifts.get(shiftId);
    }

    public List<Shift> getSome(String commaSepartedIds) {
        List<Shift> result =  new ArrayList<>();
        try {
            for (String id : commaSepartedIds.split(","))
                result.add(shifts.get(Integer.valueOf(id)));
        }catch (Exception e)
        {
            log.error("could not parse comma sepparted ids", e);
        }
        return result;
    }

    @Override
    public void run(String... args) throws Exception {

        Integer min = minrandom;
        Integer max = maxrandom;
        //generate numofshiftstocreate shift objects
        for (int x= 0; x< numofshiftstocreate; x++){

            Integer randId = (int)(Math.random() * (max - min + 1) + min);
            log.info("generated random shift id: " + randId);

            Shift shift = mapper.readValue(new File(jsonFile.getURI()),Shift[].class)[0];
            shift.setId(randId);

            for (AllowanceDto allwnc: shift.getAllowances()){
                Integer randId2 = (int)(Math.random() * (max - min + 1) + min);
                allwnc.setId(randId2);
            }
            for (BreakDto brejk: shift.getBreaks()){
                Integer randId2 = (int)(Math.random() * (max - min + 1) + min);
                brejk.setId(randId2);
            }
            shifts.put(randId,shift);

        }

    }
}
