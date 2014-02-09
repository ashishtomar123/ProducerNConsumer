package com.ntp

import akka.actor.ActorSystem
import akka.testkit.{ TestActorRef, ImplicitSender, TestKit }
import org.scalatest.{ BeforeAndAfterAll, FlatSpec }
import Message._
import scala.concurrent.duration._
import org.joda.time.DateTime

class ProducerConsumerNTPTest extends TestKit(ActorSystem("NTPTest")) with ImplicitSender with FlatSpec with BeforeAndAfterAll {

  val producerRef = TestActorRef(new Producer("P1"))
  val producerActor = producerRef.underlyingActor

  val startTime = Duration(0, SECONDS)
  val interval = Duration(5, SECONDS)
  val msgInterval = Duration(1, SECONDS)

  "Producer's consumer map " should "initially be empty" in {
    expectResult(producerActor.getConsumersSet.size)(0)
  }

  "Producer" should "receive the KeepAlive message sent by Consumer" in {
    val consumerRef3 = TestActorRef(new Consumer("C3", producerActor.self))
    consumerRef3.underlyingActor.receive(StartConsumer(startTime, interval))
    val time = new DateTime()
    consumerRef3.underlyingActor.receive(SendKeepAlive)
    assert(producerActor.getConsumersSet(consumerRef3.underlyingActor.self).isAfter(time) || consumerRef3.underlyingActor.k == 0)

  }

  "Producer" should "receive the Register message sent by Consumer" in {
    val consumerRef4 = TestActorRef(new Consumer("C4", producerActor.self))
    consumerRef4.underlyingActor.receive(StartConsumer(startTime, interval))
    expectResult(producerActor.getConsumersSet.contains(consumerRef4.underlyingActor.self))(true)
  }

  "Consumer " should "receive the Time message sent by Producer " in {
    var messages = List[Time]()
    producerActor.self ! Register
    producerActor.self ! StartProducing(startTime, msgInterval)
    receiveWhile(2000 millis) {
      case msg: Time => messages = msg :: messages
    }
    assert(messages.length > 0)
  }

  override protected def afterAll() { system.shutdown() }
}
