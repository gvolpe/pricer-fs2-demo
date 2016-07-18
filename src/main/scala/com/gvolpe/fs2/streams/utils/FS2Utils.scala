package com.gvolpe.fs2.streams.utils

import com.gvolpe.fs2.streams.{PipeT, SinkT}
import fs2.Task

object FS2Utils {

  def liftSink[A](f: A => Task[Unit]): SinkT[A] = liftPipe[A, Unit](f)

  def liftPipe[A, B](f: A => Task[B]): PipeT[A, B] = _.evalMap (f(_))

}
