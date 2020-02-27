package com.ali.study_begin

import org.apache.flink.streaming.api.scala.{SplitStream, StreamExecutionEnvironment}

object Splited {
  def main(args: Array[String]): Unit = {
    import org.apache.flink.api.scala._
    val env=StreamExecutionEnvironment.getExecutionEnvironment
    val splitstream: SplitStream[Tem] = env.socketTextStream("hadoop01", 6000)
      .map(perline => {
        val arr: Array[String] = perline
          .split(",")
        val name = arr(0).trim
        val age = arr(1).trim.toInt
        val temptrue = arr(2).trim.toDouble
        val city = arr(3).trim
        Tem(name, age, temptrue,city)
      }).split(tem => {
      if (tem.temptrue < 37.3 && tem.temptrue > 36.5)
        Seq("正常")
      else
        Seq("异常")
    })
    splitstream.select("正常").print("正常人")
    splitstream.select("异常").print("异常人")

    env.execute
  }
}
case class Tem(name:String, age:Int,temptrue:Double,city:String)
