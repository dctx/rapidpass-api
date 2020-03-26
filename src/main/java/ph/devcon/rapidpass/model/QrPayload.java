package ph.devcon.rapidpass.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Value;

/**
 * The {@link QrPayload} class models the payload we will generate into a RapidPass QR.
 * See QR code <a href="https://docs.google.com/document/d/13J-9MStDRL7thMm9eBgcSFU3X4b0_oeb3aikbhUZZAs/edit?disco=AAAAJPQWy0E">design docs</a>
 */
@Value
public class QrPayload {

    /**
     * Pass Type. 0 for individual pass, 1 for vehicle pass
     */
    private byte pt;
    /**
     * Control Code.
     */
    private int cc;
    /**
     * Valid From. Unix timestamp, 32-bit seconds from epoch
     */
    private long vf;
    /**
     * Valid Until. Unix timestamp, 32-bit seconds from epoch
     */
    private long vu;
    /**
     * ID type. first character to denote the id type (e.g. ‘D’ for driver’s license 'P' for plate)
     */
    private String idt;

    /**
     * Constructor Jackson JSON Mappers use to for deserialization.
     *
     * @param pt  pass type
     * @param cc  control code
     * @param vf  valid from. seconds from epoch
     * @param vu  valid until. seconds from epoch
     * @param idt id type
     */
    @JsonCreator
    public QrPayload(@JsonProperty("pt") byte pt,
                     @JsonProperty("cc") int cc,
                     @JsonProperty("vf") long vf,
                     @JsonProperty("vu") long vu,
                     @JsonProperty("idt") String idt) {
        this.pt = pt;
        this.cc = cc;
        this.vf = vf;
        this.vu = vu;
        this.idt = idt;
    }

}
