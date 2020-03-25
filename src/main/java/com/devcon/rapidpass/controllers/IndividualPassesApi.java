/**
 * NOTE: This class is auto generated by the swagger code generator program (3.0.14).
 * https://github.com/swagger-api/swagger-codegen
 * Do not edit the class manually.
 */
package com.devcon.rapidpass.controllers;

import com.devcon.rapidpass.models.ErrorResponse;
import com.devcon.rapidpass.models.IndividualRapidPass;
import com.devcon.rapidpass.models.LinkCollection;
import com.devcon.rapidpass.models.RapidPassStatusUpdate;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.annotations.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.bind.annotation.CookieValue;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.constraints.*;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
@Api(value = "individual-passes", description = "the individual-passes API")
public interface IndividualPassesApi {

    Logger log = LoggerFactory.getLogger(IndividualPassesApi.class);

    default Optional<ObjectMapper> getObjectMapper(){
        return Optional.empty();
    }

    default Optional<HttpServletRequest> getRequest(){
        return Optional.empty();
    }

    default Optional<String> getAcceptHeader() {
        return getRequest().map(r -> r.getHeader("Accept"));
    }

    @ApiOperation(value = "Approve Individual Pass", nickname = "approveIndividualPass", notes = "", response = IndividualRapidPass.class, tags={ "individual-pass", })
    @ApiResponses(value = { 
        @ApiResponse(code = 202, message = "Individual Pass is Approved. Returns Approved Individual Pass", response = IndividualRapidPass.class),
        @ApiResponse(code = 401, message = "Authorizing person is not allowed to approve"),
        @ApiResponse(code = 404, message = "Indivdual Pass Request not found"),
        @ApiResponse(code = 500, message = "Something went wrong with the request", response = ErrorResponse.class) })
    @RequestMapping(value = "/individual-passes/{individualPassId}/updatePassRequestStatus",
        produces = { "application/xml", "application/json" }, 
        consumes = { "application/json", "application/xml" },
        method = RequestMethod.POST)
    default ResponseEntity<IndividualRapidPass> approveIndividualPass(@ApiParam(value = "Rapid Pass Status Update containing the status and the approving authority" ,required=true )  @Valid @RequestBody RapidPassStatusUpdate body,@ApiParam(value = "",required=true) @PathVariable("individualPassId") Integer individualPassId) {
        if(getObjectMapper().isPresent() && getAcceptHeader().isPresent()) {
            if (getAcceptHeader().get().contains("application/json")) {
                try {
                    return new ResponseEntity<>(getObjectMapper().get().readValue("{\n  \"accessType\" : \"accessType\",\n  \"origin\" : {\n    \"zip\" : \"12343D\",\n    \"country\" : {\n      \"code\" : \"PH\",\n      \"name\" : \"Philippines\"\n    },\n    \"city\" : \"sin city\",\n    \"street\" : \"1 out of nowhere\",\n    \"state\" : \"state of the nation\"\n  },\n  \"destinations\" : [ null, null ],\n  \"checkpoints\" : [ {\n    \"name\" : \"Alabang SLEX Checkpoint 01\",\n    \"location\" : {\n      \"latitude\" : 14.51978,\n      \"longitude\" : 121.003615\n    },\n    \"id\" : 223234234\n  }, {\n    \"name\" : \"Alabang SLEX Checkpoint 01\",\n    \"location\" : {\n      \"latitude\" : 14.51978,\n      \"longitude\" : 121.003615\n    },\n    \"id\" : 223234234\n  } ],\n  \"passRequestResult\" : {\n    \"duration\" : 0,\n    \"qrCode\" : \"qrCode\",\n    \"effectivityDate\" : \"2020-04-01T00:00:00.000+0000\",\n    \"approvingEntity\" : {\n      \"role\" : \"Head of Infectious Disease Outbreak Control\",\n      \"organization\" : \"AFP\"\n    },\n    \"status\" : \"status\"\n  }\n}", IndividualRapidPass.class), HttpStatus.NOT_IMPLEMENTED);
                } catch (IOException e) {
                    log.error("Couldn't serialize response for content type application/json", e);
                    return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
                }
            }
        } else {
            log.warn("ObjectMapper or HttpServletRequest not configured in default IndividualPassesApi interface so no example is generated");
        }
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }


    @ApiOperation(value = "Delete an Individual Pass.", nickname = "deleteIndividualPass", notes = "", tags={ "individual-pass", })
    @ApiResponses(value = { 
        @ApiResponse(code = 202, message = "Individual Pass is deleted."),
        @ApiResponse(code = 404, message = "Individual Pass Owner not found"),
        @ApiResponse(code = 500, message = "Something went wrong with the request", response = ErrorResponse.class) })
    @RequestMapping(value = "/individual-passes/{individualPassId}",
        produces = { "application/xml", "application/json" }, 
        method = RequestMethod.DELETE)
    default ResponseEntity<Void> deleteIndividualPass(@ApiParam(value = "",required=true) @PathVariable("individualPassId") Integer individualPassId) {
        if(getObjectMapper().isPresent() && getAcceptHeader().isPresent()) {
        } else {
            log.warn("ObjectMapper or HttpServletRequest not configured in default IndividualPassesApi interface so no example is generated");
        }
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }


    @ApiOperation(value = "Register Request Passes of one or more individuals", nickname = "registerIndividualPassRequests", notes = "", response = LinkCollection.class, tags={ "individual-pass", })
    @ApiResponses(value = { 
        @ApiResponse(code = 201, message = "Individual Pass is registered. Returns links to registered individual pass data.", response = LinkCollection.class),
        @ApiResponse(code = 500, message = "Something went wrong with the request", response = ErrorResponse.class) })
    @RequestMapping(value = "/individual-passes/requestPass",
        produces = { "application/xml", "application/json" }, 
        consumes = { "application/json", "application/xml" },
        method = RequestMethod.POST)
    default ResponseEntity<LinkCollection> registerIndividualPassRequests(@ApiParam(value = "An array of containing individuals passes to be registered" ,required=true )  @Valid @RequestBody List<IndividualRapidPass> body) {
        if(getObjectMapper().isPresent() && getAcceptHeader().isPresent()) {
            if (getAcceptHeader().get().contains("application/json")) {
                try {
                    return new ResponseEntity<>(getObjectMapper().get().readValue("[ {\n  \"templated\" : true,\n  \"name\" : \"Supporting document\",\n  \"href\" : \"/{resource} . . .\"\n}, {\n  \"templated\" : true,\n  \"name\" : \"Supporting document\",\n  \"href\" : \"/{resource} . . .\"\n} ]", LinkCollection.class), HttpStatus.NOT_IMPLEMENTED);
                } catch (IOException e) {
                    log.error("Couldn't serialize response for content type application/json", e);
                    return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
                }
            }
        } else {
            log.warn("ObjectMapper or HttpServletRequest not configured in default IndividualPassesApi interface so no example is generated");
        }
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }


    @ApiOperation(value = "Retrieve an individual Pass", nickname = "retrieveIndividualPass", notes = "", response = IndividualRapidPass.class, tags={ "individual-pass", })
    @ApiResponses(value = { 
        @ApiResponse(code = 200, message = "Individual Pass found", response = IndividualRapidPass.class),
        @ApiResponse(code = 404, message = "Individual Pass Owner not found"),
        @ApiResponse(code = 500, message = "Something went wrong with the request", response = ErrorResponse.class) })
    @RequestMapping(value = "/individual-passes/{individualPassId}",
        produces = { "application/json", "application/xml" }, 
        method = RequestMethod.GET)
    default ResponseEntity<IndividualRapidPass> retrieveIndividualPass(@ApiParam(value = "",required=true) @PathVariable("individualPassId") Integer individualPassId) {
        if(getObjectMapper().isPresent() && getAcceptHeader().isPresent()) {
            if (getAcceptHeader().get().contains("application/json")) {
                try {
                    return new ResponseEntity<>(getObjectMapper().get().readValue("{\n  \"accessType\" : \"accessType\",\n  \"origin\" : {\n    \"zip\" : \"12343D\",\n    \"country\" : {\n      \"code\" : \"PH\",\n      \"name\" : \"Philippines\"\n    },\n    \"city\" : \"sin city\",\n    \"street\" : \"1 out of nowhere\",\n    \"state\" : \"state of the nation\"\n  },\n  \"destinations\" : [ null, null ],\n  \"checkpoints\" : [ {\n    \"name\" : \"Alabang SLEX Checkpoint 01\",\n    \"location\" : {\n      \"latitude\" : 14.51978,\n      \"longitude\" : 121.003615\n    },\n    \"id\" : 223234234\n  }, {\n    \"name\" : \"Alabang SLEX Checkpoint 01\",\n    \"location\" : {\n      \"latitude\" : 14.51978,\n      \"longitude\" : 121.003615\n    },\n    \"id\" : 223234234\n  } ],\n  \"passRequestResult\" : {\n    \"duration\" : 0,\n    \"qrCode\" : \"qrCode\",\n    \"effectivityDate\" : \"2020-04-01T00:00:00.000+0000\",\n    \"approvingEntity\" : {\n      \"role\" : \"Head of Infectious Disease Outbreak Control\",\n      \"organization\" : \"AFP\"\n    },\n    \"status\" : \"status\"\n  }\n}", IndividualRapidPass.class), HttpStatus.NOT_IMPLEMENTED);
                } catch (IOException e) {
                    log.error("Couldn't serialize response for content type application/json", e);
                    return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
                }
            }
        } else {
            log.warn("ObjectMapper or HttpServletRequest not configured in default IndividualPassesApi interface so no example is generated");
        }
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }

}
