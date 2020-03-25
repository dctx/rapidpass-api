package ph.devcon.rapidpass.services.email;

public interface EmailService<Payload extends EmailPayload, Response> {
    Response send() throws Exception;
}
