package com.ali.study_begin

import java.net.InetSocketAddress
import java.util

import com.ali.KafkaUtil.MysqlUtils
import com.alibaba.fastjson.JSON
import org.apache.flink.streaming.api.scala.{SplitStream, StreamExecutionEnvironment}
import org.apache.flink.streaming.connectors.redis.RedisSink
import org.apache.flink.streaming.connectors.redis.common.config.FlinkJedisClusterConfig
import org.apache.flink.streaming.connectors.redis.common.mapper.{RedisCommand, RedisCommandDescription, RedisMapper}

object redis_sink {
  def main(args: Array[String]): Unit = {


    import org.apache.flink.api.scala._
    import scala.collection.JavaConversions._
    //  val nodes: Set[InetSocketAddress]=
    val nodes: java.util.Set[InetSocketAddress] = new util.HashSet[InetSocketAddress]()
    nodes.add(new InetSocketAddress("hadoop01", 7001))
    nodes.add(new InetSocketAddress("hadoop01", 7002))
    nodes.add(new InetSocketAddress("hadoop02", 7001))
    nodes.add(new InetSocketAddress("hadoop02", 7002))
    nodes.add(new InetSocketAddress("hadoop03", 7001))
    nodes.add(new InetSocketAddress("hadoop03", 7002))


    val env = StreamExecutionEnvironment.getExecutionEnvironment
    val splitstream = env.socketTextStream("hadoop01", 6000)
      .map(perline => {
        val arr: Array[String] = perline
          .split(",")
        val name = arr(0).trim
        val age = arr(1).trim.toInt
        val temptrue = arr(2).trim.toDouble
        val city = arr(3).trim
        Tem(name, age, temptrue, city)
      })
    splitstream.print()
//    sink2redis成功
//    splitstream.addSink(new RedisSink[Tem](new FlinkJedisClusterConfig.Builder().setNodes(nodes).build(), new MyredisMappe))
//sink2mysql
val mysqlsink=new MysqlUtils("insert into test_flink values(?,?,?,?)")
    splitstream.map(line=>{
      Array(line.name,line.age,line.temptrue,line.city)
    }).
      addSink(mysqlsink)
    println("shuchul")
    env.execute()
  }
}
class MyredisMappe extends RedisMapper[Tem]{
  override def getCommandDescription: RedisCommandDescription = {
    new RedisCommandDescription(RedisCommand.LPUSH,"redis_test")
  }

  override def getKeyFromData(data: Tem): String = {
    data.name+"_"+data.city
  }

  override def getValueFromData(data: Tem): String = {
    data.name+"_"+data.city+"_"+data.temptrue
  }
}
