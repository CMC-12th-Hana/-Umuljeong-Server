package cmc.hana.umuljeong.web.controller;

import cmc.hana.umuljeong.auth.annotation.AuthUser;
import cmc.hana.umuljeong.converter.TaskCategoryConverter;
import cmc.hana.umuljeong.domain.Member;
import cmc.hana.umuljeong.domain.TaskCategory;
import cmc.hana.umuljeong.service.TaskCategoryService;
import cmc.hana.umuljeong.util.MemberUtil;
import cmc.hana.umuljeong.validation.annotation.ExistCompany;
import cmc.hana.umuljeong.validation.annotation.ExistTaskCategory;
import cmc.hana.umuljeong.web.dto.TaskCategoryRequestDto;
import cmc.hana.umuljeong.web.dto.TaskCategoryResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "TaskCategory API", description = "업무 카테고리 조회, 추가, 수정, 삭제")
@Validated
@RestController
@RequiredArgsConstructor
public class TaskCategoryRestController {

    private final TaskCategoryService taskCategoryService;

    @Operation(summary = "[006_02 & 006_02.1]", description = "업무 카테고리 목록 조회")
    @GetMapping("/company/{companyId}/client/business/task/categories")
    public ResponseEntity<TaskCategoryResponseDto.TaskCategoryListDto> getTaskCategoryList(@PathVariable(name = "companyId") @ExistCompany Long companyId, @AuthUser Member member) {
        List<TaskCategory> taskCategoryList = taskCategoryService.findByCompany(companyId);
        return ResponseEntity.ok(TaskCategoryConverter.toTaskCategoryListDto(taskCategoryList));
    }

    @Operation(summary = "[006_02.2]", description = "업무 카테고리 추가")
    @PostMapping("/company/{companyId}/client/business/task/category")
    public ResponseEntity<TaskCategoryResponseDto.CreateTaskCategoryDto> createTaskCategory(@PathVariable(name = "companyId") @ExistCompany Long companyId, @RequestBody TaskCategoryRequestDto.CreateTaskCategoryDto request, @AuthUser Member member) {
        // todo : 리더 권한 체크
        TaskCategory taskCategory = taskCategoryService.create(companyId, request);
        return ResponseEntity.ok(TaskCategoryConverter.toCreateTaskCategoryDto(taskCategory));
    }

    @Operation(summary = "[006_02.3]", description = "업무 카테고리 수정")
    @PatchMapping("/company/client/business/task/category/{categoryId}")
    public ResponseEntity<TaskCategoryResponseDto.UpdateTaskCategoryDto> updateTaskCategory(@PathVariable(name = "categoryId") @ExistTaskCategory Long taskCategoryId, @RequestBody TaskCategoryRequestDto.UpdateTaskCategoryDto request, @AuthUser Member member) {
        TaskCategory taskCategory = taskCategoryService.update(taskCategoryId, request);
        return ResponseEntity.ok(TaskCategoryConverter.toUpdateTaskCategory(taskCategory));
    }

    // todo : 하나만 수정하는 걸로 변경? YES
//    @PatchMapping("/company/{companyId}/client/business/task/categories")
//    public ResponseEntity<TaskCategoryResponseDto.UpdateTaskCategoryListDto> updateTaskCategoryList(@PathVariable(name = "companyId") @ExistCompany Long companyId, @RequestBody TaskCategoryRequestDto.UpdateTaskCategoryListDto request, @AuthUser Member member) {
//        /*
//            TODO : 리더 권한 체크 & 해당 회사의 업무 카테고리인지 검증 필요 : 아닌 경우 에러 응답
//        */
//        List<TaskCategory> taskCategoryList = taskCategoryService.updateList(companyId, request);
//        return ResponseEntity.ok(TaskCategoryConverter.toUpdateTaskCategoryListDto(taskCategoryList));
//    }


    @Operation(summary = "[006_02.4]", description = "업무 카테고리 삭제")
    @DeleteMapping("/company/client/business/task/categories")
    public ResponseEntity<TaskCategoryResponseDto.DeleteTaskCategoryListDto> deleteTaskCategory(@RequestBody TaskCategoryRequestDto.DeleteTaskCategoryListDto request) {
        // todo : delete할 때 넘겨줘야할 정보들이 어떤 게 있을까?..
        taskCategoryService.deleteList(request);
        return ResponseEntity.ok(TaskCategoryConverter.toDeleteTaskCategoryListDto());
    }
}
