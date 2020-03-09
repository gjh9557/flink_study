package com.ali.study_begin

import org.apache.commons.io.FileUtils
import org.apache.flink.api.common.functions.RichMapFunction
import org.apache.flink.configuration.Configuration
import org.apache.flink.streaming.api.scala.StreamExecutionEnvironment

import scala.collection.{immutable, mutable}
//import scala.collection.parallel.immutable
import scala.io.{BufferedSource, Source}

object DistributeFile {
  def main(args: Array[String]): Unit = {
    import org.apache.flink.api.scala._
    import scala.collection.JavaConversions._
    val env=StreamExecutionEnvironment.getExecutionEnvironment
    env.registerCachedFile("file:///C:/Users/Administrator/Desktop/test/b.txt","hdfsGenderInfo")

    env.fromElements(Tem("gjh",1,34.2,"北京"),Tem("wl",2,34.2,"tianjin"))
//      复函数里面的俩个参数一个是输入参数一个是输出参数
      .map(new RichMapFunction[Tem,(String,String,Double,String)] {
//        转呗一个可辨识map结合，用于存储学生信息
      var bs:BufferedSource=null
        var map:mutable.Map[Int,String]=_
        override def open(parameters: Configuration): Unit = {

          map=mutable.Map()
          val ctx=getRuntimeContext.getDistributedCache.getFile("hdfsGenderInfo")

           bs= Source.fromFile(ctx)
          val lst: List[String] =bs.getLines().toList
          for(perLine <- lst){
            val arr=perLine.split(",")
            val flag=arr(0).toInt
            val gender=arr(1)
            map.put(flag,gender)
          }
        }
        override def map(value: Tem): (String,String,Double,String) = {
          val genderinfo=map.getOrDefault(value.age,"未知")
          (value.name,genderinfo,value.temptrue,value.city)
        }

      override def close(): Unit = {
        if(bs!=null){
          bs.close()
        }
      }
      }).print("学生完整的信息是")

    env.execute()

  }
}
