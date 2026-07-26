package in.joblelo.JobAgentBackend.entity;

import in.joblelo.JobAgentBackend.model.Provider;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name="oauth_account", uniqueConstraints = {
        @UniqueConstraint(
                name = "uk_provider_user",
                columnNames = {"provider", "provider_user_id"}
        )
})
public class OauthAccount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Provider provider;

    @CreationTimestamp
    private Instant createdAt;

    @UpdateTimestamp
    private Instant updatedAt;

    @Column(name = "provider_user_id", nullable = false)
    private String providerUserId;

    @ManyToOne
    @JoinColumn(name = "user_id",nullable = false)
    private AuthUser user;

}
