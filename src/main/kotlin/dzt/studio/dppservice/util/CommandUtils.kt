package dzt.studio.dppservice.util

/**
 * @ClassName CommandUtils
 * @Description
 * @Author dzt
 * @Date 2021-05-27 15:48
 */
object CommandUtils {

    fun runCommand(fv: String, containerId: String): String {
        return "/data/flink/$fv/bin/flink run -d -c dzt.studio.dppplugin.FlinkSqlPlugin -yid $containerId /data/flink/$fv/jar/dpp-plugin.jar"
    }

    fun runCommand(fv: String, containerId: String, checkpointpath: String): String {
        return "/data/flink/$fv/bin/flink run -d -c dzt.studio.dppplugin.FlinkSqlPlugin --allowNonRestoredState -s $checkpointpath -yid $containerId /data/flink/$fv/jar/dpp-plugin.jar"
    }

    fun cancelCommand(fv: String, jobId: String, containerId: String): String {
        return "/data/flink/$fv/bin/flink cancel $jobId -yid  $containerId"
    }

    fun runWithAppCommand(
        mainClass: String,
        containerId: String,
        jarName: String,
        params: String?,
        fv: String
    ): String {
        return "/data/flink/$fv/bin/flink run  -d -c  $mainClass -yid  $containerId /data/flink/jar/jobs/$jarName $params 2>&1"
    }

    fun runWithAppCommand(
        mainClass: String,
        containerId: String,
        jarName: String,
        params: String?,
        savepointpath: String,
        fv: String
    ): String {
        return "/data/flink/$fv/bin/flink run -d -c  $mainClass -yid  $containerId -s $savepointpath --allowNonRestoredState /data/flink/jar/jobs/$jarName $params  2>&1"
    }

    fun runPreJobOnYarn(fv: String, jm: Int, tm: Int, ys: Int): String {
        return "/data/flink/$fv/bin/flink run -m yarn-cluster -yjm $jm -ytm $tm -ys $ys -d -c dzt.studio.dppplugin.FlinkSqlPlugin /data/flink/$fv/jar/dpp-plugin.jar"
    }

    fun runPreJobOnYarn(fv: String, checkpointpath: String, jm: Int, tm: Int, ys: Int): String {
        return "/data/flink/$fv/bin/flink run -m yarn-cluster -yjm $jm -ytm $tm -ys $ys -d -c dzt.studio.dppplugin.FlinkSqlPlugin --allowNonRestoredState -s $checkpointpath /data/flink/$fv/jar/dpp-plugin.jar"
    }

    fun runPreJobWithApp(
        mainClass: String,
        jarName: String,
        params: String?,
        savepointpath: String,
        fv: String,
        jm: Int,
        tm: Int,
        ys: Int
    ): String {
        return "/data/flink/$fv/bin/flink run -m yarn-cluster -yjm $jm -ytm $tm -ys $ys  -d -c  $mainClass  -s $savepointpath --allowNonRestoredState /data/flink/jar/jobs/$jarName $params  2>&1"
    }

    fun runPreJobWithApp(
        mainClass: String,
        jarName: String,
        params: String?,
        fv: String,
        jm: Int,
        tm: Int,
        ys: Int
    ): String {
        return "/data/flink/$fv/bin/flink run -m yarn-cluster -yjm $jm -ytm $tm -ys $ys  -d -c  $mainClass  /data/flink/jar/jobs/$jarName $params  2>&1"
    }


}