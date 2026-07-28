package in.joblelo.JobAgentBackend.service;

import in.joblelo.JobAgentBackend.entity.AuthUser;
import in.joblelo.JobAgentBackend.entity.GmailAccount;
import in.joblelo.JobAgentBackend.entity.OauthAccount;
import in.joblelo.JobAgentBackend.entity.UserJob;
import in.joblelo.JobAgentBackend.model.Provider;
import in.joblelo.JobAgentBackend.planner.model.gmail.GmailAccessTokenResponse;
import in.joblelo.JobAgentBackend.repository.AuthUserRepo;
import in.joblelo.JobAgentBackend.repository.GmailAccountRepo;
import in.joblelo.JobAgentBackend.repository.OauthAccountRepo;
import in.joblelo.JobAgentBackend.repository.UserJobRepo;
import in.joblelo.JobAgentBackend.requestdto.CreateUserGmailAccountRequest;
import in.joblelo.JobAgentBackend.responsedto.GitHubUserResponse;
import in.joblelo.JobAgentBackend.responsedto.TokenResponse;
import in.joblelo.JobAgentBackend.responsedto.UserInfoResponse;
import in.joblelo.JobAgentBackend.responsedto.UserJobResponse;
import in.joblelo.JobAgentBackend.utils.CookieUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.core.ParameterizedTypeReference;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.security.core.context.SecurityContextHolder;
import in.joblelo.JobAgentBackend.exceptionhandling.ApiException;
import org.springframework.http.HttpStatus;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final JwtDecoder jwtDecoder;
    private final AuthUserRepo authUserRepo;
    private final OauthAccountRepo oauthAccountRepo;
    private final JwtService jwtService;
    private final EncryptionService encryptionService;
    private final GmailAccountRepo gmailAccountRepo;
    private final CookieUtil cookieUtil;
    private final UserJobRepo userJobRepo;
    @Value("${google.client.id}")
    private String clientId;
    private final String TOKEN_TYPE = "Bearer";

    private final RestTemplate restTemplate = new RestTemplate();
    private final Map<String, CacheEntry> avatarCache = new ConcurrentHashMap<>();
    private static final long AVATAR_CACHE_TTL = 3600_000; // 1 hour

    private record CacheEntry(byte[] data, String contentType, long expiry) {
        boolean isValid() { return System.currentTimeMillis() < expiry; }
    }


    public TokenResponse createGoogleUser(String idToken, HttpServletResponse httpServletResponse) {
        try {

            if (idToken == null || idToken.isEmpty()) {
                throw new ApiException("Empty token!!", HttpStatus.BAD_REQUEST);
            }

            Jwt jwt = jwtDecoder.decode(idToken);

            Instant expiresAt = jwt.getExpiresAt();

            if (expiresAt != null && expiresAt.isBefore(Instant.now())) {
                throw new ApiException("Token expired", HttpStatus.UNAUTHORIZED);
            }

            if (!jwt.getAudience().contains(clientId)) {
                throw new ApiException("Invalid token audience", HttpStatus.BAD_REQUEST);
            }

            Boolean emailVerified = jwt.getClaim("email_verified");
            if (emailVerified == null || !emailVerified) {
                throw new ApiException("Email not verified by Google", HttpStatus.BAD_REQUEST);
            }

            String email = jwt.getClaim("email");
            Optional<AuthUser> existingUser = authUserRepo.findByEmail(email);

            AuthUser user;
            if (existingUser.isPresent()) {
                user = existingUser.get();
                user.setUsername(jwt.getClaim("name"));
                user.setProfileUrl(jwt.getClaim("picture"));
                user = authUserRepo.save(user);
                log.info("User already exists with email: {}, updated profile", email);
            } else {
                String userId = UUID.randomUUID().toString();
                String username = jwt.getClaim("name");
                String profileUrl = jwt.getClaim("picture");

                user = new AuthUser();
                user.setUserId(userId);
                user.setUsername(username);
                user.setEmail(email);
                user.setProfileUrl(profileUrl);

                user = authUserRepo.save(user);
            }

            Optional<OauthAccount> existingOauthAccount = oauthAccountRepo.findByUserAndProvider(user, Provider.GOOGLE);

            if (existingOauthAccount.isEmpty()) {
                String providerUserId = jwt.getClaim("sub");

                OauthAccount oauthAccount = new OauthAccount();
                oauthAccount.setProvider(Provider.GOOGLE);
                oauthAccount.setProviderUserId(providerUserId);
                oauthAccount.setUser(user);

                oauthAccountRepo.save(oauthAccount);
            }

            String accessToken = jwtService.generateAccess(user.getUserId(), user.getEmail());
            String refreshToken = jwtService.generateRefresh(user.getUserId(), user.getEmail());

            cookieUtil.setCookie(httpServletResponse, refreshToken);

            return TokenResponse.builder()
                    .accessToken(accessToken)
                    .tokenType(TOKEN_TYPE)
                    .build();

        } catch (Exception e) {
            throw new ApiException("Failed saving user", e, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    public TokenResponse createGithubUser(
            String githubAccessToken,
            HttpServletResponse response) {

        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(githubAccessToken);

        HttpEntity<Void> entity = new HttpEntity<>(headers);

        ResponseEntity<GitHubUserResponse> userResponse =
                restTemplate.exchange(
                        "https://api.github.com/user",
                        HttpMethod.GET,
                        entity,
                        GitHubUserResponse.class
                );

        GitHubUserResponse githubUser = userResponse.getBody();

        if (githubUser == null) {
            throw new ApiException("Unable to fetch GitHub user", HttpStatus.BAD_REQUEST);
        }

        // GitHub may not return email in /user (if the user keeps email private).
        // Fetch emails endpoint if necessary.
        String email = githubUser.getEmail();
        if (email == null || email.isEmpty()) {
            ResponseEntity<List<Map<String, Object>>> emailsResp = restTemplate.exchange(
                    "https://api.github.com/user/emails",
                    HttpMethod.GET,
                    entity,
                    new ParameterizedTypeReference<List<Map<String, Object>>>() {
                    }
            );

            List<Map<String, Object>> emails = emailsResp.getBody();
            if (emails != null) {
                for (Map<String, Object> e : emails) {
                    Boolean primary = (Boolean) e.get("primary");
                    Boolean verified = (Boolean) e.get("verified");
                    String em = (String) e.get("email");
                    if (Boolean.TRUE.equals(primary) && Boolean.TRUE.equals(verified) && em != null) {
                        email = em;
                        break;
                    }
                }
                if ((email == null || email.isEmpty()) && !emails.isEmpty()) {
                    // fallback to first verified
                    for (Map<String, Object> e : emails) {
                        Boolean verified = (Boolean) e.get("verified");
                        String em = (String) e.get("email");
                        if (Boolean.TRUE.equals(verified) && em != null) {
                            email = em;
                            break;
                        }
                    }
                }
            }

            if (email == null || email.isEmpty()) {
                throw new ApiException("Email not available from GitHub. Request user to make email public or grant user:email scope", HttpStatus.BAD_REQUEST);
            }
        }

        final String resolvedEmail = email;

        AuthUser user = authUserRepo.findByEmail(resolvedEmail)
                .map(existing -> {
                    existing.setUsername(githubUser.getName());
                    existing.setProfileUrl(githubUser.getAvatar_url());
                    return authUserRepo.save(existing);
                })
                .orElseGet(() -> {
                    AuthUser newUser = new AuthUser();
                    newUser.setUserId(UUID.randomUUID().toString());
                    newUser.setUsername(githubUser.getName());
                    newUser.setEmail(resolvedEmail);
                    newUser.setProfileUrl(githubUser.getAvatar_url());
                    return authUserRepo.save(newUser);
                });

        oauthAccountRepo.findByUserAndProvider(user, Provider.GITHUB)
                .orElseGet(() -> {
                    OauthAccount oauthAccount = new OauthAccount();
                    oauthAccount.setUser(user);
                    oauthAccount.setProvider(Provider.GITHUB);
                    oauthAccount.setProviderUserId(
                            String.valueOf(githubUser.getId())
                    );
                    return oauthAccountRepo.save(oauthAccount);
                });

        String accessToken = jwtService.generateAccess(user.getUserId(), user.getEmail());

        String refreshToken = jwtService.generateRefresh(user.getUserId(), user.getEmail());

        cookieUtil.setCookie(response, refreshToken);

        return TokenResponse.builder()
                .accessToken(accessToken)
                .tokenType("Bearer")
                .build();
    }

    public TokenResponse newAccessToken(HttpServletRequest request) {
        String refreshToken = cookieUtil.getRefreshTokenFromCookie(request);

        if (refreshToken == null || refreshToken.isEmpty()) {
            throw new ApiException("RefreshToken missing", HttpStatus.UNAUTHORIZED);
        }
        if (!jwtService.validateRefreshToken(refreshToken)) {
            throw new ApiException("Invalid refresh token", HttpStatus.UNAUTHORIZED);
        }

        String userId = jwtService.extractSubject(refreshToken);
        String email = jwtService.extractClaim(refreshToken);

        String newAccessToken = jwtService.generateAccess(userId, email);

        return TokenResponse
                .builder()
                .tokenType(TOKEN_TYPE)
                .accessToken(newAccessToken)
                .build();
    }

    public UserInfoResponse getUserInfo() {

        String userId = (String) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();

        AuthUser user = authUserRepo
                .findById(userId)
                .orElseThrow(() -> new ApiException("User not found", HttpStatus.NOT_FOUND));


        return UserInfoResponse
                .builder()
                .username(user.getUsername())
                .email(user.getEmail())
                .profileUrl(user.getProfileUrl())
                .createdAt(user.getCreatedAt())
                .build();
    }

    @Transactional
    public void createUserGmailAccount(
            CreateUserGmailAccountRequest request) {

        String userId = (String) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();
// edd9ff3c-e3eb-4c68-8aa5-6dcb8f79da9c
        AuthUser user = authUserRepo
                .findById(userId)
                .orElseThrow(() ->
                        new ApiException("User not found", HttpStatus.NOT_FOUND));

        GmailAccount gmailAccount = gmailAccountRepo
                .findByUser(user)
                .orElse(new GmailAccount());

        gmailAccount.setUser(user);

        gmailAccount.setEncryptedAccessToken(
                encryptionService.encrypt(
                        request.getAccessToken()
                )
        );

        if (request.getRefreshToken() != null) {
            gmailAccount.setEncryptedRefreshToken(
                    encryptionService.encrypt(
                            request.getRefreshToken()
                    )
            );
        }

        gmailAccount.setAccessTokenExpiry(
                Instant.now().plusSeconds(
                        request.getExpiresIn()
                )
        );

        gmailAccountRepo.save(gmailAccount);
    }

    public GmailAccessTokenResponse googleAccessToken(String userId) {

        AuthUser user = authUserRepo
                .findById(userId)
                .orElseThrow(() -> new ApiException("User not found", HttpStatus.NOT_FOUND));

        GmailAccount account = gmailAccountRepo
                .findByUser(user)
                .orElseThrow(() -> new ApiException("Gmail account not found", HttpStatus.NOT_FOUND));

        String accessToken = encryptionService
                .decrypt(account.getEncryptedAccessToken());

        Instant accessTokenExpiry = account.getAccessTokenExpiry();

        return GmailAccessTokenResponse
                .builder()
                .accessToken(accessToken)
                .expireIn(accessTokenExpiry)
                .build();

    }

    @Transactional
    public void updateGmailAccount(String newAccessToken, Instant expireIn) {
        String userId = (String) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();

        AuthUser user = authUserRepo
                .findById(userId)
                .orElseThrow(() -> new ApiException("User not found", HttpStatus.NOT_FOUND));

        GmailAccount account = gmailAccountRepo
                .findByUser(user)
                .orElseThrow(() -> new ApiException("Gmail account not found", HttpStatus.NOT_FOUND));

        account.setEncryptedAccessToken(
                encryptionService.encrypt(newAccessToken)
        );
        account.setAccessTokenExpiry(expireIn);

        gmailAccountRepo.save(account);
    }

    @Transactional
    public List<UserJobResponse> getAllUserJobs(String sort) {
        String userId = (String) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();

        AuthUser user = authUserRepo
                .findById(userId)
                .orElseThrow(() -> new ApiException("User not found", HttpStatus.NOT_FOUND));

        List<UserJob> jobs = "relevance".equals(sort)
                ? userJobRepo.findByUserIdOrderByRelevanceScoreDesc(user.getUserId())
                : userJobRepo.findByUserIdOrderByCreatedAtDesc(user.getUserId());

        return jobs.stream().map(job -> UserJobResponse.builder()
                        .id(job.getId())
                        .provider(job.getProvider())
                        .providerJobId(job.getProviderJobId())
                        .role(job.getRole())
                        .company(job.getCompany())
                        .location(job.getLocation())
                        .employmentType(job.getEmploymentType())
                        .experience(job.getExperience())
                        .salary(job.getSalary())
                        .applyUrl(job.getApplyUrl())
                        .description(job.getDescription())
                        .relevanceScore(job.getRelevanceScore())
                        .rankingReason(job.getRankingReason())
                        .createdAt(job.getCreatedAt())
                        .build())
                .toList();

    }

    public void removeUserJob(Long id){
        String userId = (String) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();
        UserJob job = userJobRepo.findById(id).orElse(null);
        if (job != null && job.getUserId().equals(userId)) {
            userJobRepo.deleteById(id);
            log.info("Job {} deleted for user {}", id, userId);
        } else {
            log.warn("Job {} not found or not owned by user {}", id, userId);
        }
    }

    public void serveAvatar(HttpServletResponse response) {
        String userId = (String) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();

        AuthUser user = authUserRepo
                .findById(userId)
                .orElseThrow(() -> new ApiException("User not found", HttpStatus.NOT_FOUND));

        String profileUrl = user.getProfileUrl();
        if (profileUrl == null || profileUrl.isBlank()) {
            response.setStatus(204);
            return;
        }

        CacheEntry cached = avatarCache.get(profileUrl);
        if (cached != null && cached.isValid()) {
            response.setContentType(cached.contentType());
            response.setHeader("Cache-Control", "public, max-age=3600");
            try {
                response.getOutputStream().write(cached.data());
            } catch (Exception e) {
                log.warn("Failed to write cached avatar", e);
            }
            return;
        }

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.set("User-Agent", "JobLelo/1.0");
            HttpEntity<Void> entity = new HttpEntity<>(headers);
            ResponseEntity<byte[]> res = restTemplate.exchange(
                    profileUrl, HttpMethod.GET, entity, byte[].class);

            byte[] data = res.getBody();
            String contentType = res.getHeaders().getContentType() != null
                    ? res.getHeaders().getContentType().toString()
                    : "image/jpeg";

            if (data != null && data.length > 0) {
                avatarCache.put(profileUrl, new CacheEntry(data, contentType,
                        System.currentTimeMillis() + AVATAR_CACHE_TTL));
                response.setContentType(contentType);
                response.setHeader("Cache-Control", "public, max-age=3600");
                response.getOutputStream().write(data);
            } else {
                response.setStatus(204);
            }
        } catch (Exception e) {
            log.warn("Failed to fetch avatar from {}", profileUrl, e);
            response.setStatus(204);
        }
    }
}
