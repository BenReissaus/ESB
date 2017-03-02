package org.hpi.esb.datasender

import java.util.Properties
import java.util.concurrent.{ScheduledFuture, ScheduledThreadPoolExecutor, TimeUnit}
import org.apache.kafka.clients.producer.{KafkaProducer, ProducerConfig}
import org.hpi.esb.config.DataSenderConfig
import org.hpi.esb.util.Logging

class DataProducer(producerConfig: DataSenderConfig) extends Logging {

  val props = new Properties()
  props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, producerConfig.kafkaProducer.bootstrapServers.get)
  props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, producerConfig.kafkaProducer.keySerializerClass.get)
  props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, producerConfig.kafkaProducer.valueSerializerClass.get)
  props.put(ProducerConfig.ACKS_CONFIG, producerConfig.kafkaProducer.acks.get)
  props.put(ProducerConfig.BATCH_SIZE_CONFIG, producerConfig.kafkaProducer.batchSize.get.toString)

  val producer = new KafkaProducer[String, String](props)
  val executor: ScheduledThreadPoolExecutor = new ScheduledThreadPoolExecutor(producerConfig.numberOfThreads.get) //passing number of threads in pool
  val dataReader = new DataReader(producerConfig.dataInputPath.get)

  var t: ScheduledFuture[_] = _

  def shutDown(): Unit = {
    t.cancel(false)
    dataReader.close()
    producer.close()
    executor.shutdown()
    logger.info("Shut data producer down.")
  }

  def execute(): Unit = {
    val initialDelay = 0

    val producerThread = new DataProducerThread(
      dataProducer = this,
      kafkaProducer = producer,
      dataReader = dataReader,
      topicList = producerConfig.dataModel.columns.get,
      columnDelimiter = producerConfig.columnDelimiter.get,
      columnStartOption = producerConfig.dataModel.columnStart,
      columnEndOption = producerConfig.dataModel.columnEnd)

    t = executor.scheduleAtFixedRate(producerThread, initialDelay, producerConfig.sendingInterval.get, TimeUnit.MICROSECONDS)
    logger.info("Start sending messages to Apache Kafka.")
  }
}