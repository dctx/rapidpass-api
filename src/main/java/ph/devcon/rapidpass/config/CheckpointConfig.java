package ph.devcon.rapidpass.config;


import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import ph.devcon.rapidpass.api.models.KeyEntry;

import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@Configuration
@ConfigurationProperties("qrmaster")
@Getter
@Setter
public class CheckpointConfig {

    public String[] responseKeys;

    public KeyEntry getKeyEntry(String masterKey) {
        HashMap<String, KeyEntry> configurations = getConfigurations(responseKeys);
        return configurations.get(masterKey);
    }

    public HashMap<String, KeyEntry> getConfigurations(String[] strings) {

        final HashMap<String, KeyEntry> keys = new HashMap<>();

        for (String entry : strings) {

            String[] cell = entry.split(",");
            KeyEntry keyEntry = new KeyEntry();
            keyEntry.setEncryptionKey(cell[2]);
            keyEntry.setSigningKey(cell[1]);
            keyEntry.setValidTo(cell[3]);

            keys.put(cell[0], keyEntry);
        }

        return keys;
    }

    public List<KeyEntry> getAllKeyEntries() {
        return this.getConfigurations(this.responseKeys)
                .keySet()
                .stream()
                .map(this::getKeyEntry).collect(Collectors.toList());
    }
}