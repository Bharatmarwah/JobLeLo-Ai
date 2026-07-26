package in.joblelo.JobAgentBackend.planner.tools;

import in.joblelo.JobAgentBackend.planner.model.ToolResult;
import in.joblelo.JobAgentBackend.planner.model.ToolSchema;

public interface JobSearchTool {
    ToolResult search(ToolSchema input);
}
