package com.ali.study_begin

import org.apache.flink.api.common.functions.RichFlatMapFunction
import org.apache.flink.api.common.state.{ValueState, ValueStateDescriptor}
import org.apache.flink.configuration.Configuration
import org.apache.flink.streaming.api.scala.StreamExecutionEnvironment
import org.apache.flink.util.Collector

object KeyedState {
  def main(args: Array[String]): Unit = {
    import org.apache.flink.api.scala._
    val env=StreamExecutionEnvironment.getExecutionEnvironment
    env.socketTextStream("hadoop01",6000)
//      fromElements(Tem("gjh",1,34.2,"北京"),Tem(wl,2,36.2,tianjinwl,2,36.2,tianjin,Tem("wl",2,37.2,"tianjin"),Tem("gjh",2,34.5,"tianjin"),Tem("wt",2,37.1,"tianjin"))
      .map(perline=>{
      val arr = perline.split(",")
      Tem(arr(0),arr(1).toInt,arr(2).toDouble,arr(3))
    })
      .keyBy("name")
      .flatMap(new RichFlatMapFunction[Tem,String] {

        var tempvaluestate:ValueState[Double]=_
        override def open(parameters: Configuration): Unit = {

          val desc:ValueStateDescriptor[Double]=new ValueStateDescriptor("temptrue",classOf[Double])

          tempvaluestate= getRuntimeContext.getState[Double](desc)


        }
        override def flatMap(value: Tem, out: Collector[String]): Unit = {

          val t1 = tempvaluestate.value()
          val now: Double = value.temptrue
          if(now>36.1&&now<37.3){

            if(t1>0) {
              if ((now - t1).abs > 0.8)
                {out.collect(s"${value.name}这次的体温是${value.temptrue},上次的体温是${t1},相差过大${(now - t1)}有虚假情况，重测")}
              else {
                out.collect(s"${value.name}的上次体温为${t1}这次体温为${value.temptrue}")
              }
            }
            else
              {out.collect(s"这次体温为${value.temptrue}")}
          }
          else {
            out.collect(s"${value.name}体温异常温度是${value.temptrue}")
          }
          tempvaluestate.update(now)
        }
      }).print()
    env.execute()
  }
}
