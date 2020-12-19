package me.dusanov.etl.workshift.workshiftendpoint.controller;

import lombok.RequiredArgsConstructor;
import me.dusanov.etl.workshift.workshiftendpoint.model.Shift;
import me.dusanov.etl.workshift.workshiftendpoint.service.ShiftService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/shifts")
public class ShiftController {

    private final ShiftService service;

    @GetMapping
    public List<Shift> getAll(@RequestParam(defaultValue = "", required = false) String ids){
        if (!ids.equals(""))
            return this.service.getSome(ids);
        else
            return service.getAll();
    }

    @GetMapping("/{shiftId}")
    public List<Shift> get(@PathVariable Long shiftId){
        return service.get(shiftId);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleException(Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(e.getLocalizedMessage());
    }

}
