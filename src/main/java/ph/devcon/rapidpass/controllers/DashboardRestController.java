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
@RequestMapping("/registry")
@Slf4j
public class DashboardRestController {

    private DashboardService dashboardService;

    @Autowired
    public DashboardRestController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @PutMapping("/accessPasses/{referenceId}")
    public HttpEntity<RapidPass> approveOrDecline(@PathVariable String referenceId, @RequestBody RapidPass rapidPass) {
        String status = rapidPass.getStatus();

        RapidPass result = null;

        try {

            if (RequestStatus.APPROVED.toString().equals(status)) {
                result = dashboardService.grant(referenceId);
            } else if (RequestStatus.DENIED.toString().equals(status)) {
                result = dashboardService.decline(referenceId);
            }

        } catch (RegistryService.UpdateAccessPassException e) {
            e.printStackTrace();
        }

        return (result != null) ? ResponseEntity.ok().body(result) : ResponseEntity.notFound().build();
    }

}
