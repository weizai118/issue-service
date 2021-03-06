package io.choerodon.issue.api.controller;

import io.choerodon.core.domain.Page;
import io.choerodon.core.exception.CommonException;
import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.issue.api.service.ProjectConfigService;
import io.choerodon.issue.api.service.StateMachineService;
import io.choerodon.issue.infra.feign.dto.StateMachineDTO;
import io.choerodon.swagger.annotation.CustomPageRequest;
import io.choerodon.swagger.annotation.Permission;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping(value = "/v1/organizations/{organization_id}/state_machine")
public class StateMachineController {

    @Autowired
    private StateMachineService stateMachineService;
    @Autowired
    private ProjectConfigService projectConfigService;

    @Permission(level = ResourceLevel.ORGANIZATION)
    @ApiOperation(value = "分页查询状态机列表")
    @CustomPageRequest
    @GetMapping
    public ResponseEntity<Page<StateMachineDTO>> pagingQuery(@PathVariable("organization_id") Long organizationId,
                                                             @RequestParam(required = false) Integer page,
                                                             @RequestParam(required = false) Integer size,
                                                             @RequestParam(required = false) String[] sort,
                                                             @RequestParam(required = false) String name,
                                                             @RequestParam(required = false) String description,
                                                             @RequestParam(required = false) String[] param) {
        return stateMachineService.pageQuery(organizationId, page, size, sort, name, description, param);
    }

    @Permission(level = ResourceLevel.ORGANIZATION)
    @ApiOperation(value = "删除状态机")
    @DeleteMapping(value = "/{state_machine_id}")
    public ResponseEntity<Boolean> delete(@PathVariable("organization_id") Long organizationId, @PathVariable("state_machine_id") Long stateMachineId) {
        return stateMachineService.delete(organizationId, stateMachineId);
    }

    @Permission(level = ResourceLevel.PROJECT)
    @ApiOperation(value = "【内部调用】查询状态机关联的项目id列表的Map")
    @GetMapping(value = "/query_project_ids_map")
    public ResponseEntity<Map<String, List<Long>>> queryProjectIdsMap(@PathVariable("organization_id") Long organizationId,
                                                                      @RequestParam("state_machine_id") Long stateMachineId) {
        return Optional.ofNullable(projectConfigService.queryProjectIdsMap(organizationId, stateMachineId))
                .map(result -> new ResponseEntity<>(result, HttpStatus.OK))
                .orElseThrow(() -> new CommonException("error.queryProjectIdsMap.get"));
    }

    @Permission(level = ResourceLevel.PROJECT)
    @ApiOperation(value = "【内部调用】状态机删除节点的校验，是否可以直接删除")
    @GetMapping(value = "/check_delete_node")
    public ResponseEntity<Map<String, Object>> checkDeleteNode(@PathVariable("organization_id") Long organizationId,
                                                               @RequestParam("state_machine_id") Long stateMachineId,
                                                               @RequestParam("status_id") Long statusId) {
        return Optional.ofNullable(stateMachineService.checkDeleteNode(organizationId, stateMachineId, statusId))
                .map(result -> new ResponseEntity<>(result, HttpStatus.OK))
                .orElseThrow(() -> new CommonException("error.checkDeleteNode.get"));
    }
}
