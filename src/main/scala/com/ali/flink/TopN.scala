package com.ali.flink

import org.apache.flink.streaming.api.scala.function.ProcessAllWindowFunction
import org.apache.flink.streaming.api.scala.{DataStream, StreamExecutionEnvironment}
import org.apache.flink.streaming.api.windowing.assigners.{SlidingProcessingTimeWindows, TumblingProcessingTimeWindows}
import org.apache.flink.streaming.api.windowing.time.Time
import org.apache.flink.streaming.api.windowing.windows.TimeWindow
import org.apache.flink.util.Collector

import scala.collection.immutable.TreeMap

object TopN {
  def main(args: Array[String]): Unit = {
    val env = StreamExecutionEnvironment.getExecutionEnvironment
    import org.apache.flink.api.scala._

    val lines=env.socketTextStream("hadoop01",8888)

    val reduce: DataStream[(String, Int)] = lines.flatMap(x => {
      val arr = x.split(" ")
      arr
    }).map(x => {
      (x, 1)
    })
    val result: DataStream[(String, Int)] = reduce
      .keyBy(0)
      .window(SlidingProcessingTimeWindows.of(Time.seconds(6), Time.seconds(2)))
        .sum(1)
   result
    .windowAll(TumblingProcessingTimeWindows.of(Time.seconds(2)))
      .process(new TopNFunction(3)).print().setParallelism(1)

    env.execute("flinkwc")

  }
}
private class TopNFunction(Size:Int) extends ProcessAllWindowFunction[(String,Int),(String,Int),TimeWindow] {
  override def process(context: Context, elements: Iterable[(String, Int)], out: Collector[(String, Int)]): Unit = {
    var treemap=new TreeMap[Int,(String,Int)]()
    (new Ordering[Int](){
      override def compare(x: Int, y: Int): Int = if(x<y) -1
      else 1
    })
    import scala.collection.JavaConversions._
for(element<-elements){
  treemap=treemap++List((element._2,element))
  if(treemap.size>Size){
    treemap.dropRight(1)
  }
  for(entry<-treemap.entrySet()){
    out.collect(entry.getValue)
  }
}
  }
}
