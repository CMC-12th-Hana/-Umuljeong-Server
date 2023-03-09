package cmc.hana.umuljeong.web.controller;

import cmc.hana.umuljeong.auth.annotation.AuthUser;
import cmc.hana.umuljeong.converter.TaskConverter;
import cmc.hana.umuljeong.domain.Business;
import cmc.hana.umuljeong.domain.ClientCompany;
import cmc.hana.umuljeong.domain.Member;
import cmc.hana.umuljeong.domain.Task;
import cmc.hana.umuljeong.domain.enums.MemberRole;
import cmc.hana.umuljeong.exception.BusinessException;
import cmc.hana.umuljeong.exception.CompanyException;
import cmc.hana.umuljeong.exception.TaskException;
import cmc.hana.umuljeong.exception.common.ErrorCode;
import cmc.hana.umuljeong.service.TaskService;
import cmc.hana.umuljeong.util.MemberUtil;
import cmc.hana.umuljeong.validation.annotation.ExistBusiness;
import cmc.hana.umuljeong.validation.annotation.ExistCompany;
import cmc.hana.umuljeong.validation.annotation.ExistTask;
import cmc.hana.umuljeong.web.dto.TaskRequestDto;
import cmc.hana.umuljeong.web.dto.TaskResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Tag(name = "Task API", description = "업무 조회, 추가")
@Validated
@RestController
@RequiredArgsConstructor
public class TaskRestController {

    private final TaskService taskService;

    @Operation(summary = "[002_05_5]", description = "업무 조회")
    @Parameters({
            @Parameter(name = "member", hidden = true)
    })
    @GetMapping("/company/client/business/task/{taskId}")
    public ResponseEntity<TaskResponseDto.TaskDto> getTask(@PathVariable(name = "taskId") @ExistTask Long taskId, @AuthUser Member member) {
        boolean isValid = false;
        for(ClientCompany clientCompany : member.getCompany().getClientCompanyList()) {
            for(Business business : clientCompany.getBusinessList()) {
                isValid = business.getTaskList().stream().anyMatch(task -> task.getId() == taskId);
                if(isValid) break;
            }
            if(isValid) break;
        }
        if(!isValid) throw new TaskException(ErrorCode.TASK_ACCESS_DENIED);

        Task task = taskService.findById(taskId);
        return ResponseEntity.ok(TaskConverter.toTaskDto(task));
    }

    @Operation(summary = "[002_02, 002_03]", description = "업무 목록 조회")
    @Parameters({
            @Parameter(name = "member", hidden = true)
    })
    @GetMapping("/company/{companyId}/client/business/tasks")
    public ResponseEntity<TaskResponseDto.TaskListDto> getTaskList(@PathVariable(name = "companyId") @ExistCompany Long companyId, @RequestParam(name = "date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date, @AuthUser Member member) {
        if(companyId != member.getCompany().getId()) throw new CompanyException(ErrorCode.COMPANY_ACCESS_DENIED);

        List<Task> taskList;
        if(member.getMemberRole() == MemberRole.LEADER) {
            taskList = taskService.findByCompanyAndDate(companyId, date);
            return ResponseEntity.ok(TaskConverter.toLeaderTaskListDto(taskList));
        }

        taskList = taskService.findByMemberAndDate(member, date);
        return ResponseEntity.ok(TaskConverter.toStaffTaskListDto(taskList));
    }

//    @Deprecated
//    @Parameters({
//            @Parameter(name = "member", hidden = true)
//    })
//    @GetMapping("/company/client/business/{businessId}/tasks")
//    public ResponseEntity<TaskResponseDto.TaskListDto> getTaskListByBusiness(@PathVariable(name = "businessId") @ExistBusiness Long businessId,  @RequestParam(name = "date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date, @AuthUser Member member) {
//        List<Task> taskList = taskService.findByBusinessAndMemberAndDate(businessId, member, date);
//        return ResponseEntity.ok(TaskConverter.toLeaderTaskListDto(taskList)); // todo : 요구사항에 따라 변경
//    }

    @Operation(summary = "[002_05]", description = "업무 추가")
    @Parameters({
            @Parameter(name = "member", hidden = true)
    })
    @PostMapping(value = "/company/client/business/task", consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
    public ResponseEntity<TaskResponseDto.CreateTaskDto> createTask(@ModelAttribute @Valid TaskRequestDto.CreateTaskDto request, @AuthUser Member member) {
        boolean isValid = false;
        for(ClientCompany clientCompany : member.getCompany().getClientCompanyList()) {
            isValid = clientCompany.getBusinessList().stream().anyMatch(business -> business.getId() == request.getBusinessId());
            if(isValid) break;
        }
        if(!isValid) throw new BusinessException(ErrorCode.BUSINESS_ACCESS_DENIED);

        Task task = taskService.create(request, member);
        return ResponseEntity.ok(TaskConverter.toCreateTaskDto(task));
    }

    @Deprecated
    @Operation(summary = "[002_05_5.1]", description = "업무 수정")
    @Parameters({
            @Parameter(name = "member", hidden = true)
    })
    @PatchMapping("/company/client/business/task/{taskId}")
    public ResponseEntity<TaskResponseDto.UpdateTaskDto> updateTask(@PathVariable(name = "taskId") @ExistTask Long taskId, @RequestPart @Valid TaskRequestDto.UpdateTaskDto request, @AuthUser Member member) {
        boolean isValid = false;
        for(ClientCompany clientCompany : member.getCompany().getClientCompanyList()) {
            for(Business business : clientCompany.getBusinessList()) {
                isValid = business.getTaskList().stream().anyMatch(task -> task.getId() == taskId);
                if(isValid) break;
            }
            if(isValid) break;
        }
        if(!isValid) throw new TaskException(ErrorCode.TASK_ACCESS_DENIED);

        Task task = taskService.update(request);
        return ResponseEntity.ok(TaskConverter.toUpdateTaskDto(task));
    }
}
