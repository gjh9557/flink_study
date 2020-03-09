package com.ali.study_begin

import org.apache.flink.api.common.functions.{RichFunction, RichMapFunction}
import org.apache.flink.api.scala.ExecutionEnvironment
import org.apache.flink.configuration.Configuration

//import scala.collection.parallel.mutable

import scala.collection.mutable

object Broadcastbound {
  def main(args: Array[String]): Unit = {
    import org.apache.flink.api.scala._
    val env=ExecutionEnvironment.getExecutionEnvironment
    val dset: DataSet[(Int, String)] = env.fromElements((1,"nan"),(2,"nv"))

    val dset2=env.fromElements(("gjh",12,1,12.3),("wl",13,2,13.2),("wt",14,2,13.5))

    dset2.map(new RichMapFunction[(String,Int,Int,Double),(String,Int,String,Double)] {
//      上面的俩个参数一个是输入参数样例，一个是输出模式样例

      var bcTmp:mutable.Map[Int,String]=_
      override def open(parameters: Configuration): Unit = {

        import scala.collection.JavaConversions._
        bcTmp=mutable.Map()
        var bc:java.util.List[(Int,String)]=getRuntimeContext.getBroadcastVariable("genderinfo")
        for(perEle<-bc){
          bcTmp.put(perEle._1,perEle._2)
        }
      }
      override def map(value: (String, Int, Int, Double)): (String, Int, String, Double) = {

        val gender=bcTmp.getOrElse(value._3,"无")
        (value._1,value._2,gender,value._4)
      }

    }).withBroadcastSet(dset,"genderinfo")
        .print()
//    env.execute()
  }
}
