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

package ph.devcon.rapidpass.services;

import com.google.zxing.WriterException;
import ph.devcon.dctx.rapidpass.commons.QrCodeSerializer;
import ph.devcon.dctx.rapidpass.model.QrCodeData;

import java.io.IOException;

/**
 * Service for generating QR code images using {@link QrCodeData} objects.
 */
public interface QrGeneratorService {

    /**
     * Generates a QR from a {@link QrCodeData} object. The object is serialized by {@link QrCodeSerializer} then Base64 encoded.
     * see <a href="https://docs.google.com/document/d/13J-9MStDRL7thMm9eBgcSFU3X4b0_oeb3aikbhUZZAs/edit#">design docs</a>
     *
     * @param payload payload to transform into QR
     * @return QR code file image
     * @throws IOException              on errors in json processing or saving QR to file
     * @throws WriterException          on errors in generating QR code
     * @throws IllegalArgumentException if there are missing data from the {@link QrCodeData} payload.
     */
    byte[] generateQr(QrCodeData payload) throws IOException, WriterException, IllegalArgumentException;
}
