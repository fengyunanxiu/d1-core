package ai.sparklabinc.mapper;

import ai.sparklabinc.entity.UserInfoDO;
import com.baomidou.mybatisplus.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface UserInfoMapper extends BaseMapper<UserInfoDO> {
    List<UserInfoDO> findByUserName(String name);
}