package com.ali.flink

import org.apache.flink.api.scala.{DataSet, ExecutionEnvironment}
import org.apache.flink.api.scala._

object DateSetWcApp {
  def main(args: Array[String]): Unit = {
    //1 env
    val env = ExecutionEnvironment.getExecutionEnvironment
    //2:source
    val source: DataSet[String] = env.readTextFile("D:\\out\\a.txt")
    //3:transform
    val aggSet = source.flatMap(_.split(" "))
      .map((_, 1))
      .groupBy(0)
      .sum(1)

    //4:sink
    aggSet.print()

  }
}
