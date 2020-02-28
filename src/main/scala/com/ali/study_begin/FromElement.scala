package com.ali.study_begin

import java.security.Policy.Parameters

import com.ali.enity.Student
import org.apache.flink.api.common.functions.FilterFunction
import org.apache.flink.api.java.utils.ParameterTool
import org.apache.flink.streaming.api.scala.{DataStream, StreamExecutionEnvironment}

object FromElement {
  def main(args: Array[String]): Unit = {
    if(args==null||args.length!=2){
      println("传入参数")
//      格式为 --str gjh --x y
      sys.exit(-1)
    }
    val arr=ParameterTool.fromArgs(args)
    val str=arr.get("str")
    import org.apache.flink.api.scala._
    val env =StreamExecutionEnvironment.getExecutionEnvironment
    val a=new Student("gjh", 63.2)
    val b=new Student("wl", 45.0)
    val FromEle: DataStream[Student] = env.fromElements(
     a,b
    )
    FromEle.filter(per=>{
      per.getScore<60
    }).print("不及格学生")
    FromEle.filter(per=>{
      per.getScore>60
    }).print("及格的学生")
//    自定义filter继承一个FilterFunction
    FromEle.filter(new FilterFunction[Student] {
      override def filter(t: Student): Boolean = {
        t.getName.equals(str)
      }
    }).print("自定义")
    env.execute()
  }
}
