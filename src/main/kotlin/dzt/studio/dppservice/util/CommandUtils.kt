package dzt.studio.dppservice.util

/**
 * @ClassName CommandUtils
 * @Description
 * @Author dzt
 * @Date 2021-05-27 15:48
 */
class CommandUtils {
    companion object {
        fun runCommand(fv:String,containerId: String): String {
            return "/data/$fv/bin/flink run -d -c cn.z023.plugs.FlinkSqlPlugKt -yid $containerId /data/flink/jar/dap-plug-1.0-SNAPSHOT.jar"
        }
        fun runCommand(fv:String,containerId: String,checkpointpath:String): String {
            return "/data/$fv/bin/flink run -d -c cn.z023.plugs.FlinkSqlPlugKt --allowNonRestoredState -s $checkpointpath -yid $containerId /data/flink/jar/dap-plug-1.0-SNAPSHOT.jar"
        }
        fun cancelCommand(fv:String,jobId:String,containerId: String):String{
            return "/data/$fv/bin/flink cancel $jobId -yid  $containerId"
        }
        fun runWithAppCommand(mainClass:String,containerId: String,jarName:String,params:String?,fv:String):String{
            return "/data/$fv/bin/flink run  -d -c  $mainClass -yid  $containerId /data/flink/jar/jobs/$jarName $params 2>&1"
        }
        fun runWithAppCommand(mainClass:String,containerId: String,jarName:String,params:String?,savepointpath:String,fv:String):String{
            return "/data/$fv/bin/flink run -d -c  $mainClass -yid  $containerId -s $savepointpath --allowNonRestoredState /data/flink/jar/jobs/$jarName $params  2>&1"
        }
    }
}