package ai.sparklabinc.controller;
import ai.sparklabinc.entity.UserInfoDO;
import ai.sparklabinc.service.UserInfoService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/user")
@Api(value = "用户类" ,tags = "用户操作接口")
public class UserInfoController {

    @Autowired
    private UserInfoService userInfoService;

    @GetMapping(value = "/findUserInfoList")
    @ApiOperation(value="获取用户列表", notes="获取用户列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "用户ID", required = false, paramType = "query")
    })
    public Object findUserInfoList(){
        return userInfoService.findUserInfoList();
    }

    @GetMapping(value = "/findByUserName")
    @ApiOperation(value="根据用户名获取用户列表", notes="根据用户名获取用户列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "name", value = "用户名", required = false, paramType = "query")
    })
    public Object findByUserName(String name){
        return userInfoService.findByUserName(name);
    }

    @GetMapping(value = "/selectAll")
    @ApiOperation(value="获取用户列表(jdbcTemplata)", notes="获取用户列表")
    public Object selectAll(){
        return userInfoService.selectAll();
    }

    @GetMapping(value = "/selectListPage")
    @ApiOperation(value="获取用户列表(分页)")
    public Object selectListPage(Integer page,Integer size){
        return userInfoService.selectListPage(page,size);
    }

    @PostMapping (value = "/insertInfo")
    @ApiOperation(value="新增用户信息")
    public Object insertInfo(UserInfoDO userInfoDO){
        return userInfoService.insertInfo(userInfoDO);
    }

    @PutMapping(value = "/updateInfo")
    @ApiOperation(value="更新用户信息")
    public Object updateInfo(UserInfoDO userInfoDO){
        return userInfoService.updateInfo(userInfoDO);
    }

    @DeleteMapping(value = "/deleteInfo")
    @ApiOperation(value="删除用户信息")
    public Object deleteInfo(UserInfoDO userInfoDO){
        return userInfoService.deleteInfo(userInfoDO);
    }


    @PostMapping(value = "/batchCaozuo")
    @ApiOperation(value="批量操作")
    public Object batchCaozuo(@RequestBody List<UserInfoDO> userInfoDOList){
        return userInfoService.insertInfo(userInfoDOList);
    }






}