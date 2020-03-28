package ph.devcon.rapidpass.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ph.devcon.rapidpass.enums.RequestStatus;
import ph.devcon.rapidpass.models.RapidPass;
import ph.devcon.rapidpass.models.RapidPassRequest;
import ph.devcon.rapidpass.services.DashboardService;
import ph.devcon.rapidpass.services.RegistryService;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * Dashboard API Rest Controller
 */
@CrossOrigin
@RestController
@RequestMapping("/dashboard")
@Slf4j
public class DashboardRestController {

    private DashboardService dashboardService;

    @Autowired
    public DashboardRestController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @PutMapping("/access-passes/{referenceId}")
    public HttpEntity<?> getAccessPasses(@PathVariable String referenceId, @RequestBody RapidPassRequest rapidPassRequest) {
        RequestStatus requestStatus = rapidPassRequest.getRequestStatus();

        RapidPass result = null;

        try {

            if (requestStatus == RequestStatus.APPROVED) {
                result = dashboardService.grant(referenceId);
            } else if (requestStatus == RequestStatus.DENIED) {
                result = dashboardService.decline(referenceId);
            }

        } catch (RegistryService.UpdateAccessPassException e) {
            e.printStackTrace();
        }

        return (result != null) ? ResponseEntity.ok().body(result) : ResponseEntity.notFound().build();
    }

}
