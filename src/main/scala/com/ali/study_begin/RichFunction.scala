package com.ali.study_begin
//对于温度异常的人发往kafka集群提取出来单独进行处理
//对正常的人直接处理ssh::
import java.util.Properties

import com.ali.Constant.constant
import com.ali.KafkaUtil.KafkaUtils
import org.apache.flink.api.common.functions.RichFlatMapFunction
import org.apache.flink.streaming.api.scala.{SplitStream, StreamExecutionEnvironment}
import org.apache.flink.api.scala._
import org.apache.flink.configuration.Configuration
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaProducer011
import org.apache.flink.util.Collector
import org.apache.kafka.clients.producer.{KafkaProducer, ProducerRecord}
object RichFunction {
  def main(args: Array[String]): Unit = {
    val env=StreamExecutionEnvironment.getExecutionEnvironment

    val properties=new Properties
    properties.load(this.getClass.getClassLoader.getResourceAsStream("producer.properties"))
    val splitstream= env.socketTextStream("hadoop01", 6000)
      .map(perline => {
        val arr: Array[String] = perline
          .split(",")
        if(arr.length==4){
        val name = arr(0).trim
        val age = arr(1).trim.toInt
        val temptrue = arr(2).trim.toDouble
        val city = arr(3).trim
        Tem(name, age, temptrue,city)}
        else{
          Tem("",0,0.0,"")
        }
      }).flatMap(new MyRichFlatMapFuntion(constant.Rich_topic,properties))
      .print("体温正常的旅客信息是")
  env.execute()
  }
}
class MyRichFlatMapFuntion(topic:String,properties:Properties) extends RichFlatMapFunction[Tem,Tem]{
  private var producer:KafkaProducer[String,String]=null
  override def open(parameters: Configuration): Unit = {
//        val KafkaSink: FlinkKafkaProducer011[String] = KafkaUtils.getKafkaSink(topic)
producer=new KafkaProducer[String,String](properties)
  }
  override def flatMap(in: Tem, collector: Collector[Tem]): Unit = {

    val normal:Boolean=in.temptrue>=36.3&&in.temptrue<=37.2
    if(normal){
      collector.collect(in)
    }
    else{
      val msg:ProducerRecord[String,String]=new ProducerRecord(topic,in.toString)
      producer.send(msg)
    }
  }

  override def close(): Unit = {
producer.close()
  }
}
