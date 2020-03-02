package com.ali.study_begin

import com.ali.KafkaUtil.KafkaUtils
import org.apache.flink.streaming.api.scala.StreamExecutionEnvironment

object kafka_source {
  def main(args: Array[String]): Unit = {
    import org.apache.flink.api.scala._
    val env=StreamExecutionEnvironment.getExecutionEnvironment
    val value = env.addSource(KafkaUtils.getKafKaSource("test_flink"))
      .filter(_.nonEmpty)
      .map(perline => {
        val arr = perline.split(",")
        val name = arr(0).trim
        val age = arr(1).trim.toInt
        val temptrue = arr(2).trim.toDouble
        val city = arr(3).trim
        Tem(name, age, temptrue, city)
      })
    value.map(line=>{
      line.name+"_"+line.age+"_"+line.temptrue+"_"+line.city
    }).addSink(KafkaUtils.getKafkaSink("test_sink"))

    env.execute()
  }
}
