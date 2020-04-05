package ph.devcon.rapidpass.utilities;


import org.bouncycastle.util.encoders.Hex;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ph.devcon.dctx.rapidpass.commons.HmacSha256;
import ph.devcon.dctx.rapidpass.commons.QrCodeSerializer;
import ph.devcon.dctx.rapidpass.commons.Signer;

@Configuration
public class QrCodeEncoder {

    @Value("${qrmaster.encryptionKey}")
    private String key;

    @Value("${qrmaster.skey}")
    private String signingKey;

    @Bean
    public QrCodeSerializer qrCodeSerializer() {
        final byte[] keyBytes = Hex.decode(key);
        return new QrCodeSerializer(keyBytes);
    }

    @Bean
    public Signer qrSigner() {
        final byte[] signingKeyBytes = Hex.decode(signingKey);
        return HmacSha256.signer(signingKeyBytes);
    }
}
