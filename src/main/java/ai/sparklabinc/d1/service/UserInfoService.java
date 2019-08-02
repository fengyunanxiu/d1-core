package ai.sparklabinc.d1.service;

import org.springframework.stereotype.Service;

@Service
public class UserInfoService {

//    @Autowired
//    private UserInfoMapper userInfoMapper;

//    @Autowired
//    private JdbcTemplate jdbcTemplate;
//
//    public List<UserInfoDO> findUserInfoList() {
//        return userInfoMapper.selectList(null);
//    }
//
//    public Page<UserInfoDO> findByUserName(Integer curpage,Integer size,String name) {
//        Page<UserInfoDO> page =new Page<>(curpage, size);
//        return page.setRecords(userInfoMapper.findByUserName(page,name));
//    }

//    public List<Map<String, Object>> selectAll() {
//        String sql = "select * from user_info";
//        return jdbcTemplate.queryForList(sql);
//    }

//
//    /**
//     * 新增用户信息
//     *
//     * @param userInfoDO 角色参数
//     * @return 是否成功
//     */
//    public Boolean insertInfo(UserInfoDO userInfoDO) {
//        return insert(userInfoDO);
//    }
//
//    /**
//     * 批量新增用户信息
//     *
//     * @param userInfoDOList 角色参数
//     * @return 是否成功
//     */
//    public Boolean insertInfo(List<UserInfoDO> userInfoDOList) {
//        return insertOrUpdateBatch(userInfoDOList);
//    }
//
//    /**
//     * 更新用户信息
//     *
//     * @param userInfoDO 角色参数
//     * @return 是否成功
//     */
//    public Boolean updateInfo(UserInfoDO userInfoDO) {
//        return updateById(userInfoDO);
//    }
//
//    /**
//     * 删除用户信息
//     *
//     * @param userInfoDO 角色参数
//     * @return 是否成功
//     */
//    public Boolean deleteInfo(UserInfoDO userInfoDO) {
//        return deleteById(userInfoDO.getId());
//    }
//
//
//    /**
//     * 查询用户列表(分页)
//     *
//     * @param curpage
//     * @param size
//     * @return 查询角色分页列表
//     */
//    public Page<UserInfoDO> selectListPage(Integer curpage, Integer size) {
//        Page<UserInfoDO> page = new Page<UserInfoDO>(curpage, size);
//        Wrapper<UserInfoDO> wrapper = new EntityWrapper<UserInfoDO>().orderBy(false, "id", false);
//        Page<UserInfoDO> roleDOList = selectPage(page, wrapper);
//        return roleDOList;
//    }

}