package me.dusanov.etl.workshift.workshiftendpoint.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import me.dusanov.etl.workshift.workshiftendpoint.kafka.Sender;
import me.dusanov.etl.workshift.workshiftendpoint.model.AllowanceDto;
import me.dusanov.etl.workshift.workshiftendpoint.model.BreakDto;
import me.dusanov.etl.workshift.workshiftendpoint.model.Shift;
import me.dusanov.etl.workshift.workshiftendpoint.model.ShiftCreatedMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.*;

@ConfigurationProperties(prefix = "workshift.service")
@Service
@RequiredArgsConstructor
public class ShiftService implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(ShiftService.class);
    @Value("classpath:/shift_data_326872_example.json")
    private Resource jsonFile;
    private final ObjectMapper mapper = new ObjectMapper();

    @Getter @Setter private int numofshiftstocreate;
    @Getter @Setter private int minrandom;
    @Getter @Setter private int maxrandom;
    @Getter @Setter private int createshiftinterval;

    private final Map<Integer, Shift> shifts = new HashMap<>();

    private final Sender sender;

    public List<Shift> getAll() {
        return new ArrayList<>(shifts.values());
    }

    public Shift get(Integer shiftId) {
        return shifts.get(shiftId);
    }

    public List<Shift> getSome(String commaSepartedIds) {
        List<Shift> result =  new ArrayList<>();
        try {
            for (String id : commaSepartedIds.split(","))
                result.add(shifts.get(Integer.valueOf(id)));
        } catch (Exception e) {
            log.error("could not parse comma separated ids", e);
        }
        return result;
    }

    @Override
    public void run(String... args) throws Exception {

        final Shift shiftTemplate = mapper.readValue(new File(jsonFile.getURI()),Shift[].class)[0];

        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                for (int x= 0; x< numofshiftstocreate; x++){
                    //do a deep copy with the jackson
                    Shift shift;
                    try {
                        shift = mapper.readValue(mapper.writeValueAsString(shiftTemplate), Shift.class);
                        Integer randId = (int)(Math.random() * (maxrandom - minrandom + 1) + minrandom);
                        shift.setId(randId);

                        for (AllowanceDto allwnc: shift.getAllowances()){
                            Integer randId2 = (int)(Math.random() * (maxrandom - minrandom + 1) + minrandom);
                            allwnc.setId(randId2);
                        }
                        for (BreakDto brejk: shift.getBreaks()){
                            Integer randId2 = (int)(Math.random() * (maxrandom - minrandom + 1) + minrandom);
                            brejk.setId(randId2);
                        }
                        shifts.put(randId,shift);
                        sender.send(new ShiftCreatedMessage(randId));
                        log.info("generated new shift object with id: " + randId);
                    } catch (JsonProcessingException e) {
                        log.error("failed to create a deep copy of shift object",e);
                    } catch (RuntimeException e) {
                        log.error("something bad happened while trying to generate new shift",e);
                    }
                }
            }
        };
        final Timer timer = new Timer("Timer");
        timer.schedule(task,0,createshiftinterval);
    }
}
