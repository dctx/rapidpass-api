package ph.devcon.rapidpass.services;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.core.JdbcTemplate;
import ph.devcon.rapidpass.config.CheckpointConfig;
import ph.devcon.rapidpass.repositories.AccessPassRepository;
import ph.devcon.rapidpass.repositories.ScannerDeviceRepository;
import ph.devcon.rapidpass.services.controlcode.ControlCodeService;

import javax.sql.DataSource;
import java.util.List;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;

/**
 * Integration tests for {@link CheckpointServiceImpl}. Connects to a live db!
 *
 * @author jonasespelita@gmail.com
 */
@ExtendWith(MockitoExtension.class)
class CheckpointServiceImplIT {
    ICheckpointService instance;

    @Mock
    AccessPassRepository mockAccessPassRepository;
    @Mock
    ScannerDeviceRepository mockScannerDeviceRepository;
    @Mock
    ControlCodeService mockControlCodeService;
    @Mock
    CheckpointConfig mockCheckpointConfig;

    @BeforeEach
    void setUp() {

        // setup data source
        final HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setUsername("postgres");
        hikariConfig.setPassword("Dctx@2020");
        hikariConfig.setJdbcUrl("jdbc:postgresql://rapidpassdb01.koreacentral.cloudapp.azure.com:5432/rapidpassdb_dev");
        DataSource ds = new HikariDataSource(hikariConfig);

        instance = new CheckpointServiceImpl(mockAccessPassRepository,
                mockScannerDeviceRepository,
                mockControlCodeService,
                mockCheckpointConfig,
                new JdbcTemplate(ds));

    }

    @Test
    void retrieveRevokedAccessPassesJdbc() {
        when(mockControlCodeService.encode(anyInt())).thenReturn("FAKECONTROL");

        final List<Map<String, Object>> revoked = instance.retrieveRevokedAccessPassesJdbc(null);
        System.out.println("revoked = " + revoked);
        assertThat(revoked, is(not(empty())));
    }
}