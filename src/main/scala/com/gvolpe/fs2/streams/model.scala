package com.gvolpe.fs2.streams

object model {

  case class Event(id: Long, `type`: String, origin: String, destination: String)
  case class FlowMessage(headers: Map[String, Any], event: Event)
  case class MessageHeader(key: String, value: Any)

}
