package com.ali.KafkaUtil

import java.util.Properties

import com.ali.Constant.constant
import org.apache.flink.api.common.serialization.SimpleStringSchema
import org.apache.flink.streaming.connectors.kafka.{FlinkKafkaConsumer011, FlinkKafkaProducer011}

object KafkaUtils {
  val prop = new Properties()

  prop.setProperty("bootstrap.servers", constant.bootstrap_servers)
  prop.setProperty("group.id", constant.groupid)

  def getKafKaSource(topic: String): FlinkKafkaConsumer011[String] = {
    val KafkaConsumer: FlinkKafkaConsumer011[String] = new FlinkKafkaConsumer011[String](topic, new SimpleStringSchema(), prop)
    KafkaConsumer
  }

  def getKafkaSink(topic: String): FlinkKafkaProducer011[String] = {
    val Kafkaproducer: FlinkKafkaProducer011[String] = new FlinkKafkaProducer011[String](constant.brokerlist, topic, new SimpleStringSchema())
    Kafkaproducer
  }
}
