package in.joblelo.JobAgentBackend.controller;

import in.joblelo.JobAgentBackend.responsedto.UserInfoResponse;
import in.joblelo.JobAgentBackend.responsedto.UserJobResponse;
import in.joblelo.JobAgentBackend.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class UserController{

    private final UserService userService;

    @GetMapping("/user")
    public UserInfoResponse userInfo(){
        return userService.getUserInfo();
    }

    @GetMapping("/user/job")
    public List<UserJobResponse> userJobs(
            @RequestParam(defaultValue = "recent") String sort){
        return userService.getAllUserJobs(sort);
    }

    @DeleteMapping("/user/job/{id}")
    public void userJobDelete(@PathVariable Long id){
        userService.removeUserJob(id);
    }


}
