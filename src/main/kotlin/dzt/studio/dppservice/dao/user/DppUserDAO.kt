package dzt.studio.dppservice.dao.user

import dzt.studio.dppservice.dao.MyBatisBaseDao
import dzt.studio.dppservice.domain.user.DppUser
import org.springframework.stereotype.Repository

/**
 * @ClassName DppUserDAO
 * @Description
 * @Author dzt
 * @Date 2021-05-13 11:21
 */
@Repository
interface DppUserDAO : MyBatisBaseDao<DppUser, String> {
    fun selectByParameters(record: DppUser):DppUser?
}