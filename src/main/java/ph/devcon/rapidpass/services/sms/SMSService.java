package ph.devcon.rapidpass.services.sms;

public interface SMSService<Payload extends SMSPayload, Response> {
    Response send() throws Exception;
}
