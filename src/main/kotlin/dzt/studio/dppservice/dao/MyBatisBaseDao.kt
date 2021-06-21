package dzt.studio.dppservice.dao

import java.io.Serializable

interface MyBatisBaseDao<Model, PK : Serializable> {
    fun deleteByPrimaryKey(id: PK): Int

    fun insert(record: Model): Int

    fun insertSelective(record: Model): Int

    fun selectByPrimaryKey(id: PK): Model

    fun updateByPrimaryKeySelective(record: Model): Int

    fun updateByPrimaryKey(record: Model): Int

    fun selectAll():List<Model>

    fun upsert(record: Model): Int

}