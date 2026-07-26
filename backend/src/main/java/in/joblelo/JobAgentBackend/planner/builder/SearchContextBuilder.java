package in.joblelo.JobAgentBackend.planner.builder;

import in.joblelo.JobAgentBackend.planner.model.SearchContext;
import in.joblelo.JobAgentBackend.validation.model.ValidatedSearchContext;
import org.springframework.stereotype.Component;

@Component
public class SearchContextBuilder {

    public SearchContext build(ValidatedSearchContext searchContext) {

        SearchContext context = new SearchContext();

        context.setQueryRole(resolveValue(searchContext.getQueryRole(), searchContext.getProfileRole()));
        context.setProfileRole(searchContext.getProfileRole());
        context.setEmployeeType(resolveValue(searchContext.getQueryEmploymentType(),searchContext.getProfileEmploymentType()));
        context.setExperience(resolveValue(searchContext.getQueryExperience(),searchContext.getProfileExperience()));
        context.setLocation(resolveValue(searchContext.getQueryLocation(),searchContext.getProfileLocation()));
        context.setSkills(resolveValue(searchContext.getQuerySkills(),searchContext.getProfileSkills()));

        return context;
    }

    private static <T> T resolveValue(T queryValue, T profileValue) {
        return queryValue != null ? queryValue : profileValue;
    }

}