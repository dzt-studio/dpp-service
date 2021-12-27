package dzt.studio.dppservice.util

import com.alibaba.fastjson.JSON
import org.apache.ibatis.type.BaseTypeHandler
import org.apache.ibatis.type.JdbcType
import org.apache.ibatis.type.MappedTypes
import org.postgresql.util.PGobject
import java.sql.CallableStatement
import java.sql.PreparedStatement
import java.sql.ResultSet


/**
 *@ClassName JsonbTypeHandler
 *@Description TODO
 *@Author Dzt
 *@Date 2021/12/24 10:38 上午
 **/
@MappedTypes(Any::class)
class JsonbTypeHandler : BaseTypeHandler<Any>() {
    private val jsonObject = PGobject()
    override fun setNonNullParameter(ps: PreparedStatement?, i: Int, parameter: Any?, jdbcType: JdbcType?) {
        if (ps != null) {
            jsonObject.type = "jsonb"
            jsonObject.value = JSON.toJSONString(parameter)
            ps.setObject(i, jsonObject)
        }
    }

    override fun getNullableResult(rs: ResultSet?, columnName: String?): Any {
        return rs!!.getObject(columnName)
    }

    override fun getNullableResult(rs: ResultSet?, columnIndex: Int): Any {
        return rs!!.getObject(columnIndex)
    }

    override fun getNullableResult(cs: CallableStatement?, columnIndex: Int): Any {
        return cs!!.getObject(columnIndex);
    }
}