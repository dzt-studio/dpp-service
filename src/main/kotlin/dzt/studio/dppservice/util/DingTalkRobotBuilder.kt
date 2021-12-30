package dzt.studio.dppservice.util

import com.dingtalk.api.DefaultDingTalkClient
import com.dingtalk.api.request.OapiRobotSendRequest


object DingTalkRobotBuilder {
    fun send(tokenId: String, msg: String) {
        val client = DefaultDingTalkClient("https://oapi.dingtalk.com/robot/send?access_token=$tokenId")
        val request = OapiRobotSendRequest()
        request.msgtype = "text"
        val text = OapiRobotSendRequest.Text()
        text.content = "【预警】：$msg"
        request.setText(text)
        val at = OapiRobotSendRequest.At()
        at.isAtAll = true
        request.setAt(at)
        client.execute(request)
    }
}