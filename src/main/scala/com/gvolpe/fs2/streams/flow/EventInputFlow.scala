package com.gvolpe.fs2.streams.flow

import java.time.Instant

import com.gvolpe.fs2.streams.model.FlowMessage
import fs2.{Stream, Task}

trait EventInputFlow {

  def process(flowMessage: FlowMessage): Stream[Task, Unit] = for {
    currentTimeHeader <- Stream.eval(Task.now(addStartingHeader(flowMessage)))
  } yield ()

  private def addStartingHeader(flowMessage: FlowMessage) = {
    val startingHeaders = flowMessage.headers.updated("starting", Instant.now())
    flowMessage.copy(headers = startingHeaders)
  }

}
