package com.ali.KafkaUtil

import java.sql
import java.sql.{Connection, PreparedStatement}
import java.util.Properties

import com.alibaba.druid.pool.DruidDataSourceFactory
import javax.sql.DataSource
import org.apache.flink.configuration.Configuration
import org.apache.flink.streaming.api.functions.sink.{RichSinkFunction, SinkFunction}

class MysqlUtils(sql:String) extends RichSinkFunction[Array[Any]]{
  val driver="com.mysql.jdbc.Driver"

  val url="jdbc:mysql://hadoop01:3306/test?useSSL=false"

  val username="root"

  val password="123456"

  val maxActive="20"

  var connection:Connection=null;

  override def open(parameters: Configuration): Unit = {
    val properties=new Properties()
    properties.put("diverClassName",driver)
    properties.put("url",url)
    properties.put("username",username)
    properties.put("password",password)
    properties.put("maxActive",maxActive)

    val dataSource: DataSource = DruidDataSourceFactory.createDataSource(properties)
   connection=dataSource.getConnection()
  }

  override def invoke(value: Array[Any], context: SinkFunction.Context[_]): Unit = {
    val ps: PreparedStatement = connection.prepareStatement(sql)
    println(value.mkString(","))
    for (i<-0 until value.length){
      ps.setObject(i+1,value(i))
    }
    ps.executeUpdate()
  }
  override def close(): Unit = {

    if(connection!=null){
      connection.close()
    }

  }

}
