package com.ali.KafkaUtil

import com.ali.Constant.constant
import org.apache.flink.streaming.connectors.redis.RedisSink
import org.apache.flink.streaming.connectors.redis.common.config.FlinkJedisPoolConfig
import org.apache.flink.streaming.connectors.redis.common.mapper.{RedisCommand, RedisCommandDescription, RedisMapper}

object RedisUtils {
  val conf=new FlinkJedisPoolConfig.Builder().setHost(constant.redishost).setPort(6379).build()
def getredis() ={
  val redis: RedisSink[(String, Int)] = new RedisSink[(String,Int)](conf,new MyRedisMapper)
  redis
}
  class MyRedisMapper extends RedisMapper[(String,Int)]{
    override def getCommandDescription: RedisCommandDescription = {
      new RedisCommandDescription(RedisCommand.HSET,"gjh_test")
    }

    override def getKeyFromData(t: (String, Int)): String = t._1

    override def getValueFromData(t: (String, Int)): String = t._2.toString
  }
}
