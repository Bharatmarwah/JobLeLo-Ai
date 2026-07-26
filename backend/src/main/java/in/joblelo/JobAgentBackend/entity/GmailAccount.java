package in.joblelo.JobAgentBackend.entity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "gmail_account")
public class GmailAccount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String encryptedRefreshToken;

    @Column(columnDefinition = "TEXT")
    private String encryptedAccessToken;

    private Instant accessTokenExpiry;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", unique = true, nullable = false)
    private AuthUser user;

    @Column(nullable = false)
    private boolean connected;
}