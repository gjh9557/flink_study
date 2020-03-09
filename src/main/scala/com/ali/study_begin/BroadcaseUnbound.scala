package com.ali.study_begin

import org.apache.flink.api.common.state.MapStateDescriptor
import org.apache.flink.api.common.typeinfo.BasicTypeInfo
import org.apache.flink.api.common.typeutils.base.{CharSerializer, IntSerializer}
import org.apache.flink.streaming.api.datastream.BroadcastStream
import org.apache.flink.streaming.api.functions.co.BroadcastProcessFunction
import org.apache.flink.streaming.api.scala.{BroadcastConnectedStream, StreamExecutionEnvironment}
import org.apache.flink.util.Collector

object BroadcaseUnbound {
  def main(args: Array[String]): Unit = {
import org.apache.flink.api.scala._
    val env=StreamExecutionEnvironment.getExecutionEnvironment

    val dstream=env.fromElements((1,"nan"),(2,"nv"))

    val dstream2=env.socketTextStream("hadoop01",6000)
      .filter(_.trim.nonEmpty)
      .map(line=>{
        val arr = line.split(",")
        val name=arr(0)
        val age=arr(1)
        val gender=arr(2).toInt
        val score=arr(3)
        (name,age,gender,score)
      })

    val broadcastate: MapStateDescriptor[Integer, String] = new MapStateDescriptor[Integer, String]("genderinfo",BasicTypeInfo.INT_TYPE_INFO,BasicTypeInfo.STRING_TYPE_INFO)

    val BStream: BroadcastStream[(Int, String)] = dstream.broadcast(broadcastate)

    val bcConnectStream: BroadcastConnectedStream[(String, String, Int, String), (Int, String)] = dstream2.connect(BStream)
//输出元素，非广播流，广播流，输出元素
    bcConnectStream.process[(String, String, String, String)](new BroadcastProcessFunction[(String, String, Int, String), (Int, String),(String, String, String, String)] {
//      下述方法会执行多次，每次分析的是广播流中的一个元素
//      一个是输入的第非广播流参数
//      第二个是一个只读的变量第一个是输入的非广播流元素，第二是是广播流元素，第三个是输出元素
      override def processElement(in1: (String, String, Int, String),
                                  readOnlyContext: BroadcastProcessFunction[(String, String, Int, String), (Int, String), (String, String, String, String)]#ReadOnlyContext,
                                  collector: Collector[(String, String, String, String)]): Unit = {

        val genderflg=in1._3
        val str = readOnlyContext.getBroadcastState(broadcastate).get(genderflg)
        collector.collect((in1._1,in1._2,str,in1._4))
      }

      override def processBroadcastElement(in2: (Int, String), context: BroadcastProcessFunction[(String, String, Int, String), (Int, String), (String, String, String, String)]#Context, collector: Collector[(String, String, String, String)]): Unit = {
        val flag = in2._1
        val genderinfo=in2._2
        context.getBroadcastState(broadcastate).put(flag,genderinfo)
      }
    }).print()
    env.execute()
  }
}
