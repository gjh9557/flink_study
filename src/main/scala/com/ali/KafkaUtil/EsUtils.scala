package com.ali.KafkaUtil

import java.util

import com.ali.Constant.constant
import com.ali.flink.A
import org.apache.flink.api.common.functions.RuntimeContext
import org.apache.flink.streaming.connectors.elasticsearch.{ElasticsearchSinkFunction, RequestIndexer}
import org.apache.flink.streaming.connectors.elasticsearch6.ElasticsearchSink
import org.apache.http.HttpHost
import org.elasticsearch.client.Requests

object EsUtils {
val httpHosts=new util.ArrayList[HttpHost]

  httpHosts.add(new HttpHost("hadoop01",constant.Esport,constant.schme))
  httpHosts.add(new HttpHost("hadoop02",constant.Esport,constant.schme))
  httpHosts.add(new HttpHost("hadoop03",constant.Esport,constant.schme))
def getEsSink() ={
  new ElasticsearchSink[A]()
}

//  class MyElasticSearchFunction extends ElasticsearchSinkFunction[A]{
////    override def process(t: A, runtimeContext: RuntimeContext, requestIndexer: RequestIndexer) = {
////      Requests.indexRequest().index("xx").`type`("_doc").source()
////
////      requestIndexer.add()
////    }
//  }

}
