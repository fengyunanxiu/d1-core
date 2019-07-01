package ai.sparklabinc.mapper;

import ai.sparklabinc.entity.UserInfoDO;
import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.baomidou.mybatisplus.plugins.Page;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Mapper
@Repository
public interface UserInfoMapper extends BaseMapper<UserInfoDO> {
    List<UserInfoDO> findByUserName(Page page,@RequestParam("name") String name);


}
