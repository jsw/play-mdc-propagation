package util

import java.util.concurrent.{Callable, ExecutorService}

import brave.internal.WrappingExecutorService
import com.typesafe.scalalogging.StrictLogging
import org.slf4j.MDC

import scala.concurrent.ExecutionContext

object MdcUtil extends StrictLogging {

  private def setContextMap(context: java.util.Map[String, String]): Unit = {
    if (context == null) {
      MDC.clear()
    } else {
      MDC.setContextMap(context)
    }
  }

  class WrappedRunnable(r: Runnable) extends Runnable {
    val mdcContext = MDC.getCopyOfContextMap
    logger.debug(s"mdcContext=${mdcContext}")
    override def run(): Unit = {
      val oldMDCContext = MDC.getCopyOfContextMap
      logger.debug(s"oldMDCCOntext=${oldMDCContext}")
      setContextMap(mdcContext)
      logger.debug(s"set MDC to ${mdcContext}")
      try {
        r.run()
      } finally {
        setContextMap(oldMDCContext)
        logger.debug(s"reset MDC to $oldMDCContext")
      }
    }
  }

  class WrappedCallable[T](c: Callable[T]) extends Callable[T] {
    val mdcContext = MDC.getCopyOfContextMap
    logger.debug(s"mdcContext=${mdcContext}")
    override def call(): T = {
      val oldMDCContext = MDC.getCopyOfContextMap
      logger.debug(s"oldMDCCOntext=${oldMDCContext}")
      setContextMap(mdcContext)
      logger.debug(s"set MDC to ${mdcContext}")
      try {
        c.call()
      } finally {
        setContextMap(oldMDCContext)
        logger.debug(s"reset MDC to $oldMDCContext")
      }
    }
  }

  class MdcExecutionContext(delegate: ExecutionContext) extends ExecutionContext with StrictLogging {
    override def execute(r: Runnable) = delegate.execute(new WrappedRunnable(r))
    override def reportFailure(t: Throwable) = delegate.reportFailure(t)
  }

  class MdcExecutorService(d: ExecutorService) extends WrappingExecutorService {
    override protected def delegate: ExecutorService = d
    override protected def wrap[C](task: Callable[C]): Callable[C] = new WrappedCallable(task)
    override protected def wrap(task: Runnable): Runnable = new WrappedRunnable(task)
  }
}
