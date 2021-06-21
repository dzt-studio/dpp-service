package dzt.studio.dppservice.util

/**
 * @ClassName JSONResult
 * @Description
 * @Author dzt
 * @Date 2021-05-12 10:54
 */
class JSONResult<T> {
    var code: Int? = HttpCode.OK.code
    var message: String? = "操作成功"
    var content: T? = null

    constructor(code: Int?, message: String?, obj: T? = null) {
        this.code = code
        this.message = message
        content = obj
    }

    constructor(code: Int?, message: String?) {
        this.code = code
        this.message = message
    }

    constructor(code: Int?) {
        this.code = code
    }

    constructor(message: String?) {
        this.message = message
    }

}