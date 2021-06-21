package dzt.studio.dppservice.domain.job

/**
 * @ClassName JobCheckPoint
 * @Description
 * @Author Tandz
 * @Date 2020-09-25 10:01
 */
class JobCheckPoint {
    var latest: JobLatest? = null
}

class JobLatest {
    var completed: JobCompleted? = null
    var savepoint: String? = null
}

class JobCompleted {
    var external_path: String? = null
}