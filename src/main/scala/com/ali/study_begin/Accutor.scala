package com.ali.study_begin

import org.apache.flink.api.common.JobExecutionResult
import org.apache.flink.api.common.accumulators.LongCounter
import org.apache.flink.api.common.functions.RichMapFunction
import org.apache.flink.configuration.Configuration
import org.apache.flink.streaming.api.scala.StreamExecutionEnvironment
//import sun.management.counter.LongCounter

object Accutor {
  def main(args: Array[String]): Unit = {
    import org.apache.flink.api.scala._
    val env=StreamExecutionEnvironment.getExecutionEnvironment
    val Source = env.socketTextStream("hadoop01",6000)
        .map(perline=>{
          val arr=perline.split(",")
          Tem(arr(0),arr(1).toInt,arr(2).toDouble,arr(3))
        })
//      fromElements(Tem("gjh",12,37.2,"北京"),Tem("gjh",12,34.2,"北京"),Tem("gjh",12,38.2,"北京"))

    Source.map(new RichMapFunction[Tem,String] {
      val total =new LongCounter(0)
      val normal =new LongCounter(0)
      val execution=new LongCounter(0)


      override def open(parameters: Configuration): Unit = {
        getRuntimeContext.addAccumulator("total",total)
        getRuntimeContext.addAccumulator("normal",normal)
        getRuntimeContext.addAccumulator("execution",execution)

      }
      override def map(value: Tem): String = {
        total.add(1)
        var msg="体温过高需要排查"
        if(value.temptrue>36.3&&value.temptrue<37.3){
          normal.add(1)
          (s"名字为${value.name},温度是${value.temptrue},体温正常放行")
        }
        else{
          execution.add(1)
          (s"名字为${value.name},温度是${value.temptrue},体温不正常，隔离"+msg)

        }
      }

      override def close(): Unit = super.close()
    }).print()


    val result: JobExecutionResult = env.execute.getJobExecutionResult
    val total=result.getAccumulatorResult[Long]("total")
    val normal=result.getAccumulatorResult[Long]("normal")
    val execution=result.getAccumulatorResult[Long]("execution")
println(s"旅客总数是${total},正常人数为${normal},异常人数为${execution}")
  }
}
