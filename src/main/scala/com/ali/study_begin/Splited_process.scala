package com.ali.study_begin

import org.apache.flink.streaming.api.functions.ProcessFunction
import org.apache.flink.streaming.api.scala.{DataStream, OutputTag, SplitStream, StreamExecutionEnvironment}
import org.apache.flink.util.Collector
//import org.apache.flink.util.Collector

object Splited_process {
  def main(args: Array[String]): Unit = {
    import org.apache.flink.api.scala._
    val outputTag=new OutputTag[Tem]("体温异常")
    val env=StreamExecutionEnvironment.getExecutionEnvironment
    val mainStream: DataStream[Tem] = env.socketTextStream("hadoop01", 6000)
      .map(perline => {
        val arr: Array[String] = perline
          .split(",")
        val name = arr(0).trim
        val age = arr(1).trim.toInt
        val temptrue = arr(2).trim.toDouble
        val city = arr(3).trim
        Tem(name, age, temptrue, city)
      }).process(new ProcessFunction[Tem, Tem] {
      override def processElement(i: Tem, context: ProcessFunction[Tem, Tem]#Context, collector: Collector[Tem]): Unit = {
        if (i.temptrue < 37.6 && i.temptrue > 36.3) {
          collector.collect(i)
        } else {
          context.output(outputTag, i)
        }
      }


    })
    mainStream.getSideOutput(outputTag)
        .print("体温偏高")

    mainStream.print("体温正常")
    env.execute
  }
}
//case class Tem(name:String, age:Int,temptrue:Double,city:String)
