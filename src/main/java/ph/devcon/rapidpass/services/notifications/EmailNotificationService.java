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

package ph.devcon.rapidpass.services.notifications;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.activation.DataSource;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.util.Map.Entry;

@Service
@Qualifier("email")
@Slf4j
public class
EmailNotificationService implements NotificationService {

    @Autowired
    public JavaMailSender emailSender;

    @Override
    public void send(NotificationMessage message) throws NotificationException {
        log.debug("sending EMAIL msg to {}", message.getTo());
        MimeMessage msg = emailSender.createMimeMessage();

        try {
            MimeMessageHelper helper = new MimeMessageHelper(msg, true);
            helper.setFrom(message.getFrom());
            helper.setTo(message.getTo());
            helper.setSubject(message.getTitle());
            helper.setText(message.getMessage());

            if (message.getAttachments() != null) {
                for (Entry<String, DataSource> attachment : message.getAttachments().entrySet()) {
                    helper.addAttachment(attachment.getKey(), attachment.getValue());
                }
            }

            emailSender.send(msg);
            log.debug("  EMAIL sent! {}", message.getTo());
        } catch (MessagingException | MailException e) {
            throw new NotificationException(e);
        }
    }

}
