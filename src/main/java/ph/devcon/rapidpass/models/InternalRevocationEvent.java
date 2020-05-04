package ph.devcon.rapidpass.models;

import ph.devcon.rapidpass.api.models.RevocationEvent;
import ph.devcon.rapidpass.entities.AccessPass;

public class InternalRevocationEvent extends RevocationEvent {
    public static InternalRevocationEvent buildFrom(AccessPass accessPass) {
        InternalRevocationEvent event = new InternalRevocationEvent();
        event.setControlCode(accessPass.getControlCode());

        switch (accessPass.getStatus()) {
            case "SUSPENDED":
                event.setEventType("RapidPassRevoked");
        }

        event.setTimestamp(Math.toIntExact(accessPass.getDateTimeUpdated().toEpochSecond()));
        return event;
    }
}
