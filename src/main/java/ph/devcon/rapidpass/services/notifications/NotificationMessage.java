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

import javax.activation.DataSource;
import javax.mail.util.ByteArrayDataSource;
import java.util.HashMap;
import java.util.Map;


public class NotificationMessage {

    private final String from;
    private final String to;
    private final String title;
    private final String message;
    private final Map<String, DataSource> attachments;

    public static MessageBuilder New() {
        return new MessageBuilder();
    }

    private NotificationMessage(final String from, final String to, final String title, final String message,
                                final Map<String, DataSource> attachments) {
        this.from = from;
        this.to = to;
        this.title = title;
        this.message = message;
        this.attachments = attachments;
    }

    public String getFrom() {
        return this.from;
    }

    public String getTo() {
        return this.to;
    }

    public String getTitle() {
        return this.title;
    }

    public String getMessage() {
        return this.message;
    }

    public Map<String, DataSource> getAttachments() {
        return this.attachments;
    }

    public static class MessageBuilder {
        private String tempFrom;
        private String tempTo;
        private String tempTitle;
        private String tempMessage;
        private Map<String, DataSource> tempAttachments;

        public MessageBuilder from(final String from) {
            this.tempFrom = from;
            return this;
        }

        public MessageBuilder to(final String to) {
            this.tempTo = to;
            return this;
        }

        public MessageBuilder title(final String title) {
            this.tempTitle = title;
            return this;
        }

        public MessageBuilder message(final String message) {
            this.tempMessage = message;
            return this;
        }

        public MessageBuilder addAttachment(final String name, final String mimeType, final byte[] data) {
            if (this.tempAttachments == null) {
                this.tempAttachments = new HashMap<String, DataSource>();
            }
            DataSource attachment = new ByteArrayDataSource(data, mimeType);
            this.tempAttachments.put(name, attachment);
            return this;
        }

        public NotificationMessage create() {
            return new NotificationMessage(
                this.tempFrom, 
                this.tempTo, 
                this.tempTitle, 
                this.tempMessage, 
                this.tempAttachments);
        }
        
    }
}

