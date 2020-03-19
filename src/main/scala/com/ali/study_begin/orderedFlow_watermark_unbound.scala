//package com.ali.study_begin
//
////import org.apache.flink.api.common.state.StateTtlConfig.TimeCharacteristic
//import java.text.SimpleDateFormat
//
//import org.apache.flink.streaming.api.TimeCharacteristic
//import org.apache.flink.streaming.api.functions.AssignerWithPeriodicWatermarks
//import org.apache.flink.streaming.api.scala.StreamExecutionEnvironment
//import org.apache.flink.streaming.api.watermark.Watermark
////import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment
//import org.apache.flink.api.scala._
//object orderedFlow_watermark_unbound {
//  def main(args: Array[String]): Unit = {
////    通过enenttime，统计北京西站每个红外测温仪最近三秒中，最先抵达的旅客和最迟的旅客
////    环境
//    val env=StreamExecutionEnvironment.getExecutionEnvironment
//    env.setParallelism(1)
//
////    指定eventtime的字段
//    env.setStreamTimeCharacteristic(TimeCharacteristic.EventTime)
////    获得source,过滤控制，变为对偶元祖，给每个元素分配timestamp和watermark
//    env.socketTextStream("hadoop01",6000)
//      .filter(_.trim.nonEmpty)
//      .map(perline=>{
//        val arr = perline.split("//s+")
//        (arr(0),arr(1))
//
//      }).assignTimestampsAndWatermarks(new Mywater)
////    根据id进行分组，使用滚动窗口进行分析，显示结果
//  }
//}
//class Mywater extends  AssignerWithPeriodicWatermarks[(String,Long)]{
//  private var maxTimestamp=0L
//
//  private var maxOrderedTime=10000L
//
//  private val sdf:SimpleDateFormat=new SimpleDateFormat("yyyy年MM月dd日 HH:ss")
//
//  override def getCurrentWatermark: Watermark = {
//
//  }
//
//  override def extractTimestamp(t: (String, Long), l: Long): Long = {
//    val currentTimestamp=t._2
//
//    maxTimestamp=maxTimestamp.max(currentTimestamp)
//
//
//    currentTimestamp
//  }
//}
