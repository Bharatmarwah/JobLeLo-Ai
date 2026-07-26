package in.joblelo.JobAgentBackend.Orchestration;

import in.joblelo.JobAgentBackend.conversation.ConversationAnalyzer;
import in.joblelo.JobAgentBackend.conversation.model.ConversationAnalyzerResponse;
import in.joblelo.JobAgentBackend.conversation.model.IntentType;
import in.joblelo.JobAgentBackend.general.GeneralQueryProcessor;
import in.joblelo.JobAgentBackend.memory.MemoryMerge;
import in.joblelo.JobAgentBackend.memory.ltm.entity.UserProfile;
import in.joblelo.JobAgentBackend.memory.ltm.service.UserProfileService;
import in.joblelo.JobAgentBackend.memory.stm.formatter.MemoryFormatter;
import in.joblelo.JobAgentBackend.memory.stm.manager.MemoryManager;
import in.joblelo.JobAgentBackend.memory.stm.model.ChatMessage;
import in.joblelo.JobAgentBackend.model.ChatRequest;
import in.joblelo.JobAgentBackend.model.ChatResponse;
import in.joblelo.JobAgentBackend.planner.JobSearchOrchestrator;
import in.joblelo.JobAgentBackend.planner.builder.SearchContextBuilder;
import in.joblelo.JobAgentBackend.planner.model.ExecutionContext;
import in.joblelo.JobAgentBackend.planner.model.PlannerContext;
import in.joblelo.JobAgentBackend.planner.model.SearchContext;
import in.joblelo.JobAgentBackend.planner.model.SearchResultContext;
import in.joblelo.JobAgentBackend.unknown.UnknownQueryProcessor;
import in.joblelo.JobAgentBackend.utils.SecurityUtils;
import in.joblelo.JobAgentBackend.validation.ConversationValidator;
import in.joblelo.JobAgentBackend.validation.handler.ValidationClassificationHandler;
import in.joblelo.JobAgentBackend.validation.handler.ValidationFallbackHandler;
import in.joblelo.JobAgentBackend.validation.model.ValidationStatus;
import in.joblelo.JobAgentBackend.validation.model.ValidatorResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class ChatOrchestrator {

    private final MemoryManager memoryManager;
    private final MemoryFormatter memoryFormatter;
    private final ConversationAnalyzer conversationAnalyzer;
    private final UserProfileService userProfileService;
    private final MemoryMerge memoryMerge;
    private final SecurityUtils securityUtils;
    private final ConversationValidator validator;
    private final GeneralQueryProcessor generalQueryProcessor;
    private final UnknownQueryProcessor unknownQueryProcessor;
    private final ValidationFallbackHandler validationFallbackHandler;
    private final ValidationClassificationHandler validationClassificationHandler;
    private final SearchContextBuilder searchContextBuilder;
    private final JobSearchOrchestrator jobSearchOrchestrator;

    public ChatResponse processChat(ChatRequest request) {

        // Retrieve recent STM
        List<ChatMessage> messages =
                memoryManager.getRecentMessages(request.getSessionId());

        String previousMemory =
                memoryFormatter.formatConversation(messages);

        // Analyze conversation
        ConversationAnalyzerResponse analyzerResponse =
                conversationAnalyzer.classifyWithContext(
                        request,
                        previousMemory
                );

        // UNKNOWN
        if (analyzerResponse.getIntent() == IntentType.UNKNOWN) {
            return unknownQueryProcessor.process(
                    request,
                    analyzerResponse.getContextSummary()
            );
        }

        // Store user message in STM
        memoryManager.addUserMessage(request);

        if (analyzerResponse.getProfileUpdateOperations().hasUpdates()) {

            userProfileService.updateProfile(
                    securityUtils.getCurrentUserId(),
                    analyzerResponse.getEntities(),
                    analyzerResponse.getProfileUpdateOperations()
            );
        }

        // Fetch latest LTM
        UserProfile userProfile =
                userProfileService.getUserProfileDetails(
                        securityUtils.getCurrentUserId()
                );

        // Merge STM + LTM
        String memory =
                memoryMerge.mergeMemory(
                        analyzerResponse.getContextSummary(),
                        userProfile
                );

        //testing
        log.info("Merged Memory: {}", memory);

        // SEARCH JOB
        if (analyzerResponse.getIntent() == IntentType.SEARCH_JOB) {

            //validation
            ValidatorResponse validation =
                    validator.validate(request, memory);

            log.info("Validation Result: {}", validation);

            if(validation.getValidationStatus() == ValidationStatus.READY
                    && validation.isContinueExecution()){

                SearchContext searchContext =
                        searchContextBuilder
                                .build(validation.getValidatedSearchContext());

                PlannerContext context = PlannerContext.builder()
                        .searchContext(searchContext)
                        .executionContext(
                                ExecutionContext.builder()
                                        .goalReached(false)
                                        .iteration(0)
                                        .build()
                        )
                        .searchResultContext(
                                SearchResultContext.builder()
                                        .totalJobsFound(0)
                                        .jobs(new ArrayList<>())
                                        .build()
                        )
                        .build();

               return jobSearchOrchestrator.plan(request,context);
            }

            if (validation.getValidationStatus() == ValidationStatus.NEED_INFORMATION
                    && !validation.isContinueExecution()) {

                return validationClassificationHandler
                        .classificationResponse(request,validation);
            }

            if ((validation.getValidationStatus() == ValidationStatus.REJECTED
                    || validation.getValidationStatus() == ValidationStatus.ERROR)
                    && !validation.isContinueExecution()) {

                return validationFallbackHandler.fallbackResponse(validation);
            }
        }
        // GENERAL CHAT
        return generalQueryProcessor.process(
                request,
                memory
        );
    }
}