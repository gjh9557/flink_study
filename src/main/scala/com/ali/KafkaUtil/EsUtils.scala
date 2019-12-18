package com.ali.KafkaUtil

import java.util

import com.ali.Constant.constant
import com.ali.flink.A
import com.alibaba.fastjson.JSON
import org.apache.flink.api.common.functions.RuntimeContext
import org.apache.flink.streaming.connectors.elasticsearch.{ElasticsearchSinkFunction, RequestIndexer}
import org.apache.flink.streaming.connectors.elasticsearch6.ElasticsearchSink
import org.apache.http.HttpHost
import org.elasticsearch.action.index.IndexRequest
import org.elasticsearch.client.Requests
import org.elasticsearch.common.xcontent.XContentType

object EsUtils {
val httpHosts=new util.ArrayList[HttpHost]

  httpHosts.add(new HttpHost("hadoop01",constant.Esport,constant.schme))
//  httpHosts.add(new HttpHost("hadoop02",constant.Esport,constant.schme))
//  httpHosts.add(new HttpHost("hadoop03",constant.Esport,constant.schme))
def getEsSink(indexname:String) ={
  val EsSinkBuilder = new ElasticsearchSink.Builder[String](httpHosts,new MyElasticSearchFunction(indexname))
EsSinkBuilder.setBulkFlushMaxActions(20)
  val EsSink = EsSinkBuilder.build()
EsSink
}

  class MyElasticSearchFunction(indexname:String) extends ElasticsearchSinkFunction[String]{
    override def process(t: String, runtimeContext: RuntimeContext, requestIndexer: RequestIndexer) = {
//implicit val formats=org.json4s.DefaultFormats

//     val map=new util.HashMap[String,String]()
//      map.put("name",t.name)
//      map.put("age",t.age.toString)
//      map.put("classes",t.classes.toString)

val json = JSON.parseObject(t)

      val indexRequest: IndexRequest = Requests.indexRequest().index(indexname).`type`("test_doc").source(json)

      requestIndexer.add(indexRequest)
    }
  }

}
