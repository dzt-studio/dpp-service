package dzt.studio.dppservice.controller

import dzt.studio.dppservice.domain.job.DppContainerInfo
import dzt.studio.dppservice.service.ContainerService
import dzt.studio.dppservice.util.HttpCode
import dzt.studio.dppservice.util.JSONResult
import dzt.studio.dppservice.util.PageRequest
import org.springframework.stereotype.Component
import org.springframework.web.bind.annotation.*
import javax.annotation.Resource

/**
 * @ClassName ContainerController
 * @Description
 * @Author dzt
 * @Date 2021-06-11 15:23
 */
@RestController
@Component
@RequestMapping("/container")
class ContainerController {
    @Resource
    var containerService: ContainerService? = null

    @GetMapping("/list-all")
    fun jobList(): JSONResult<Any> {
        return JSONResult(HttpCode.OK.code, "操作成功", containerService!!.getContainerList())
    }

    @PostMapping("/list")
    fun jobList(@RequestBody pageRequest: PageRequest): JSONResult<Any> {
        return JSONResult(HttpCode.OK.code, "操作成功", containerService!!.getList(pageRequest))
    }

    @PostMapping("/create")
    fun createContainer(@RequestBody dppContainerInfo: DppContainerInfo): JSONResult<Any> {
        return if (containerService!!.createContainer(dppContainerInfo)) {
            JSONResult(HttpCode.OK.code, "操作成功")
        } else {
            JSONResult(HttpCode.OK.code, "操作失败")
        }
    }

    @GetMapping("/delete")
    fun deleteContainer(@RequestParam(value = "id") id: String): JSONResult<Any> {
        return if (containerService!!.deleteContainer(id)) {
            JSONResult(HttpCode.OK.code, "操作成功")
        } else {
            JSONResult(HttpCode.OK.code, "操作失败")
        }
    }

    @PostMapping("/update")
    fun updateContainer(@RequestBody dppContainerInfo: DppContainerInfo): JSONResult<Any> {
        return if (containerService!!.updateContainer(dppContainerInfo)) {
            JSONResult(HttpCode.OK.code, "操作成功")
        } else {
            JSONResult(HttpCode.OK.code, "操作失败")
        }
    }

}