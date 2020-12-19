package me.dusanov.etl.workshift.workshiftendpoint.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import me.dusanov.etl.workshift.workshiftendpoint.model.Shift;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ShiftService implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(ShiftService.class);
    @Value("classpath:/shift_data_326872_example.json")
    private Resource jsonFile;
    private final ObjectMapper mapper = new ObjectMapper();

    private Map<Integer, Shift> shifts = new HashMap<>();

    public List<Shift> getAll() {
        return new ArrayList<Shift>(shifts.values());
    }

    public List<Shift> get(Long shiftId) {
        return null;
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

        //generate 5 shift objects
        for (int x= 0; x< 5; x++){
            Integer min = 1;
            Integer max = 100000;
            Integer randId = (int)(Math.random() * (max - min + 1) + min);
            log.info("generated random id: " + randId);

            Shift shift = mapper.readValue(new File(jsonFile.getURI()),Shift[].class)[0];
            shift.setId(randId);
            shifts.put(randId,shift);

        }

    }
}
