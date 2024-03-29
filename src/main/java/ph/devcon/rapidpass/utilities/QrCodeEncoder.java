/*
 * Copyright (c) 2020.  DevConnect Philippines, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed
 * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *
 * See the License for the specific language governing permissions and limitations under the License.
 */

package ph.devcon.rapidpass.utilities;


import lombok.AllArgsConstructor;
import lombok.Setter;
import org.bouncycastle.util.encoders.Hex;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ph.devcon.dctx.rapidpass.commons.HmacSha256;
import ph.devcon.dctx.rapidpass.commons.QrCodeSerializer;
import ph.devcon.dctx.rapidpass.commons.Signer;
import ph.devcon.rapidpass.services.ICheckpointService;

@Configuration
@Setter
@AllArgsConstructor
public class QrCodeEncoder {

    private final ICheckpointService checkpointService;

    @Bean
    public QrCodeSerializer qrCodeSerializer() {
        String encryptionKey = checkpointService.getLatestKeys().getEncryptionKey();
        final byte[] keyBytes = Hex.decode(encryptionKey);
        return new QrCodeSerializer(keyBytes);
    }

    @Bean
    public Signer qrSigner() {
        String signingKey = checkpointService.getLatestKeys().getSigningKey();
        final byte[] signingKeyBytes = Hex.decode(signingKey);
        return HmacSha256.signer(signingKeyBytes);
    }
}
