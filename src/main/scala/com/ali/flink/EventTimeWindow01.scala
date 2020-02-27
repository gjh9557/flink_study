package com.ali.flink

import org.apache.flink.api.common.restartstrategy.RestartStrategies
import org.apache.flink.api.java.tuple.Tuple
import org.apache.flink.streaming.api.{CheckpointingMode, TimeCharacteristic}
import org.apache.flink.streaming.api.functions.timestamps.BoundedOutOfOrdernessTimestampExtractor
import org.apache.flink.streaming.api.scala.{DataStream, StreamExecutionEnvironment, WindowedStream}
import org.apache.flink.streaming.api.windowing.time.Time
import org.apache.flink.api.scala._
import org.apache.flink.streaming.api.windowing.assigners.{SlidingEventTimeWindows, TumblingEventTimeWindows}
import org.apache.flink.streaming.api.windowing.windows.TimeWindow
import org.elasticsearch.index.seqno.ReplicationTracker.CheckpointState
object EventTimeWindow01 {
  def main(args: Array[String]): Unit = {
    val env=StreamExecutionEnvironment.getExecutionEnvironment
//    env.enableCheckpointing(60000)
//    env.getCheckpointConfig.setCheckpointingMode(CheckpointingMode.EXACTLY_ONCE)
//    env.getCheckpointConfig.setCheckpointTimeout(1000000)
//    env.getCheckpointConfig.setFailOnCheckpointingErrors(false)
//    env.getCheckpointConfig.setMaxConcurrentCheckpoints(5)
    env.setRestartStrategy(RestartStrategies.fixedDelayRestart(3,500))
    env.setStreamTimeCharacteristic(TimeCharacteristic.EventTime)
    val stream=env.socketTextStream("hadoop01",7777)
      .assignTimestampsAndWatermarks(
        new BoundedOutOfOrdernessTimestampExtractor[String](Time.milliseconds(0)) {
          override def extractTimestamp(t: String): Long = {
//eventTime word 告诉系统eventTime的获取，分配给watermark
            val eventTime=t.split(" ")(0).toLong
            println(eventTime)
            eventTime
          }
        }
      ).map(line=>{
      (line.split(" ")(1),1L)
    }).keyBy(0)
    //滚动窗口 5秒一个window
//val streamWindow: WindowedStream[(String, Long), Tuple, TimeWindow] = stream.window(TumblingEventTimeWindows.of(Time.seconds(5)))
    //滑动窗口 5秒执行一次，每次执行的是10秒内的数据
    val streamWindow: WindowedStream[(String, Long), Tuple, TimeWindow] = stream.window(SlidingEventTimeWindows.of(Time.seconds(10),Time.seconds(5)))

    val streamReduce: DataStream[(String, Long)] = streamWindow.reduce((x, y) => {
      (x._1, x._2 + y._2)
    })
    streamReduce.print()
    env.execute()

  }
}
