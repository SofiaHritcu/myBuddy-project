package ro.mybuddy.server.report.controller;

import io.swagger.annotations.*;
import org.hibernate.cfg.NotYetImplementedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ro.mybuddy.server.report.exceptions.InvalidUserException;
import ro.mybuddy.server.report.exceptions.ReportException;
import ro.mybuddy.server.report.model.Report;
import ro.mybuddy.server.report.model.ReportDto;
import ro.mybuddy.server.report.service.ReportService;
import ro.mybuddy.server.user.model.*;

import java.util.List;
import java.util.stream.Collectors;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
public class ReportController {

    @Autowired
    private ReportService service;

    /**
     * Returns a ResponseEntity containing the execution status of saving a post report.
     * The post ID is checked for existence, so a valid one must be provided.
     * The report argument must specify the reporting username and the reason for reporting formatted as a message.
     * <p>
     *     The given parameters are validated by the ReportService.saveReport method, then saved. In case of failure,
     *      a ReportException is thrown, which leads to returning an instance with HttpStatus.NOT_ACCEPTABLE (406).
     * </p>
     * @param postId  the ID of reported post
     * @param report  the report data
     * @param bindingResult
     * @return instance containing HttpStatus.OK (200) if the provided parameters are valid
     *         otherwise an instance containing HttpStatus.NOT_ACCEPTABLE (406)
     */
    @ApiOperation(value = "Saves a report into the system.")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Report saved successfully"),
            @ApiResponse(code = 406, message = "Invalid username or post")
    })
    @ApiImplicitParams(value = {
            @ApiImplicitParam(name = "message"),
            @ApiImplicitParam(name = "username")
    })
    @PostMapping(value="/post/newsfeed/report/{postId}")
    ResponseEntity<?> saveReport(
            @PathVariable String postId,
            @RequestBody ReportDto report,
            BindingResult bindingResult) {
        if(bindingResult.hasErrors()){
            List<String> errors = bindingResult.getAllErrors().stream()
                    .map(x -> x.getDefaultMessage())
                    .collect(Collectors.toList());
            return new ResponseEntity<>(errors.toString(), HttpStatus.NOT_ACCEPTABLE);
        }
        try {
            service.saveReport(postId, report);
            return new ResponseEntity<Void>(HttpStatus.OK);
        } catch (ReportException e) {
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.NOT_ACCEPTABLE);
        }
    }

    /**
     * Returns a ResponseEntity containing the execution status of fetching all the reports.
     * @return an instance which wraps the list of reports and the HttpStatus.OK code
     *         in case of fetching errors, an instance with the HttpStatus.INTERNAL_SERVER_ERROR code
     */
    @ApiOperation(value = "Fetches all the reports from the system.")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Reports fetched successfully"),
            @ApiResponse(code = 500, message = "Internal error")
    })
    @GetMapping(value="/post/newsfeed/report")
    ResponseEntity<?> findAll() {
        try {
            var reports = service.findAll();
            return new ResponseEntity<>(reports, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.toString(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @ApiOperation(value = "Deletes a report from the system only if it exists.")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Report deleted successfully (if existent)"),
            @ApiResponse(code = 500, message = "Internal error")
    })
    @DeleteMapping(value = "/post/newsfeed/report/{id}")
    ResponseEntity<?> deleteReport(@PathVariable long id) {
        try {
            service.deleteReport(id);
            return new ResponseEntity<Void>(HttpStatus.OK);
        } catch (ReportException re) {
            return new ResponseEntity<>(re.toString(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
