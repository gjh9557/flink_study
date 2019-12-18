package com.ali.flink

import com.ali.KafkaUtil.{EsUtils, KafkaUtils, MysqlUtils, RedisUtils}
import com.alibaba.fastjson.JSON
import com.alibaba.fastjson.serializer.SerializerFeature
import org.apache.flink.api.java.tuple.Tuple
import org.apache.flink.streaming.api.scala.{ConnectedStreams, DataStream, KeyedStream, SplitStream, StreamExecutionEnvironment}
import org.apache.flink.api.scala._
import org.apache.flink.runtime.state.filesystem.FsStateBackend
import org.apache.flink.streaming.api.datastream.DataStreamSink
import org.apache.flink.streaming.connectors.elasticsearch6.ElasticsearchSink
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaProducer011
import org.apache.flink.streaming.connectors.redis.RedisSink

object StreamApiApp {
  def main(args: Array[String]): Unit = {
    val env = StreamExecutionEnvironment.getExecutionEnvironment

//    env.setStateBackend(new FsStateBackend())
//    env.enableCheckpointing(60000) 间隔多久去做一次checkpoint将数据保存秋来
    //    val Kafkasource = KafkaUtils.getKafKaSource("a")
    //      val Dstream: DataStream[String] = env.addSource(Kafkasource)
    //
    //      val dataframe: DataStream[A] = Dstream.map(line => {
    //        val json = JSON.parseObject(line, classOf[A])
    //        json
    //    })
        val dataframe1 = env.readTextFile("C:\\Users\\Administrator\\Desktop\\test.txt")
    val dataframe: DataStream[A] = dataframe1
      .map(line => {
        val json = JSON.parseObject(line, classOf[A])
        json
      })

    //计算累加求和
    val total = totalnum(dataframe)
    total.print()
    //对select和filter的联合使用可以比拟flume的过滤器和选择器
    val tmp: (DataStream[A], DataStream[A]) = filter_select(dataframe)

    //connect和comap的操作:对于两个流的合并来同时进行操作

    connect_comap(tmp)

    //union 对两个一样的流进行连接,然后进行相应操作

    val unionDstream: DataStream[A] = tmp._1.union(tmp._2)

    //uoion与connect的区别：union操作俩个流类型必须一样，connect可以不一样，输出必须一样。union可以多个，connect只可以连个


    // sink2kafka 成功

//    val KafkaSink: FlinkKafkaProducer011[String] = KafkaUtils.getKafkaSink("test_17")
//    dataframe.map(x=>{x.name+":"+x.classes}).addSink(KafkaSink)
//    tmp._1.map(x=>x.name+":"+x.classes).addSink(KafkaSink)//?
//    env.execute()

    //sink2redis //成功
    val redis: RedisSink[(String, Int)] = RedisUtils.getredis()
    total.map(line=>(line._1.toString,line._2)).addSink(redis)

    //成功
//    val EsSink: ElasticsearchSink[String] = EsUtils.getEsSink("test")
    //sink2Es
//
//    dataframe1.addSink(EsSink)

    //sink2mysql 成功
//    val jdbcSink=new MysqlUtils("insert into test_17 values(?,?,?)")
//    dataframe.map(line=>{
// Array(line.name,line.age,line.classes)
//    }).addSink(jdbcSink)

    env.execute()
  }

  //connect和comap的操作:对于两个流的合并来同时进行操作
  private def connect_comap(tmp: (DataStream[A], DataStream[A])) = {
    val ConnStream: ConnectedStreams[A, A] = tmp._1.connect(tmp._2)

    val ConStrea: DataStream[Int] = ConnStream.map(
      (line: A) => {
        line.age
      },
      (line2: A) => {
        line2.classes
      }
    )
    ConStrea
  }

  /**
    * 这里类似将每条数据要筛选的方向挑选出来，然后按照分类给他一个标签，最后在下面可以直接求出来，然后对其jin
    * 行操作
    */
  private def filter_select(dataframe: DataStream[A]) = {

    val SplitStream: SplitStream[A] = dataframe.split(line => {
      var flag: List[String] = null
      if (line.classes == "2") {
        flag = List("2班级", "高一")
      } else if (line.classes == "3") {
        flag = List("3班", "高二")
      } else {
        flag = List(line.classes + "", "高一")
      }
      flag
    })

    //这里可以选择出打上标签后的数据
    val two: DataStream[A] = SplitStream.select("2班", "高二")
    val other: DataStream[A] = SplitStream.select("5班", "高一")
    (two, other)
  }

  private def totalnum(dataframe: DataStream[A]) = {
    //        统计个数

    val nameKeyedStream: KeyedStream[(Int, Int), Tuple] = dataframe.map(line => {
      (line.age, 1)
    }).keyBy(0)

    val reduce: DataStream[(Int, Int)] = nameKeyedStream.reduce((x, y) => {
      (x._1, x._2 + y._2)
    })
    //最后可以设置并行度，包括前面输出数字是一个终端设备号，可以更改与给的cpu核数有段
    reduce
  }
}

case class A(
              name: String,
              age: Int,
              classes: Int
            )
