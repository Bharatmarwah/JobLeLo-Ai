package in.joblelo.JobAgentBackend.planner.model.gmail;


import org.jsoup.Jsoup;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Base64;

@Component
public class GmailMessageParser {

    public ParsedEmail parse(GmailMessageDetail detail) {

        String sender = getHeader(detail, "From");
        String recipient = getHeader(detail, "To");
        String subject = getHeader(detail, "Subject");

        String body = extractBody(detail.getPayload());

        return ParsedEmail.builder()
                .messageId(detail.getId())
                .threadId(detail.getThreadId())
                .sender(sender)
                .recipient(recipient)
                .subject(subject)
                .snippet(detail.getSnippet())
                .body(body)
                .receivedAt(
                        Instant.ofEpochMilli(
                                Long.parseLong(detail.getInternalDate())
                        )
                )
                .build();
    }

    private String getHeader(GmailMessageDetail detail, String headerName) {

        if (detail.getPayload() == null ||
                detail.getPayload().getHeaders() == null) {
            return null;
        }

        return detail.getPayload()
                .getHeaders()
                .stream()
                .filter(h -> headerName.equalsIgnoreCase(h.getName()))
                .map(GmailHeader::getValue)
                .findFirst()
                .orElse(null);
    }

    private String extractBody(GmailPayload payload) {

        if (payload == null) {
            return "";
        }

        // Prefer text/plain
        if (payload.getParts() != null) {

            for (GmailPart part : payload.getParts()) {

                if ("text/plain".equalsIgnoreCase(part.getMimeType())) {
                    return decode(part.getBody().getData());
                }
            }

            // Fallback to HTML
            for (GmailPart part : payload.getParts()) {

                if ("text/html".equalsIgnoreCase(part.getMimeType())) {
                    return Jsoup.parse(
                            decode(part.getBody().getData())
                    ).text();
                }
            }
        }

        if (payload.getBody() != null &&
                payload.getBody().getData() != null) {
            return decode(payload.getBody().getData());
        }

        return "";
    }

    private String decode(String encoded) {

        if (encoded == null) {
            return "";
        }

        return new String(
                Base64.getUrlDecoder().decode(encoded),
                StandardCharsets.UTF_8
        );
    }
}