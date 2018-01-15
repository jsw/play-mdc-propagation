package util

import com.typesafe.scalalogging.StrictLogging
import org.slf4j.MDC

import scala.concurrent.ExecutionContext

class MdcExecutionContext(delegate: ExecutionContext) extends ExecutionContext with StrictLogging {
  override def execute(r: Runnable) = {
    val mdcContext = MDC.getCopyOfContextMap
    logger.info(s"Current mdc: ${mdcContext}")
    delegate.execute(new Runnable {
      def run() = {
        val oldMDCContext = MDC.getCopyOfContextMap
        logger.info(s"oldMDCContext: ${oldMDCContext}")
        setContextMap(mdcContext)
        logger.info(s"Running runnable with mdc: ${mdcContext}")
        try {
          r.run() // what happens when MDC is modified by the runnable?
        } finally {
          logger.info(s"Reverting to oldMDCContext: ${oldMDCContext}")
          setContextMap(oldMDCContext)
        }
      }
    })
  }

  private[this] def setContextMap(context: java.util.Map[String, String]): Unit = {
    if (context == null) {
      MDC.clear()
    } else {
      MDC.setContextMap(context)
    }
  }

  override def reportFailure(t: Throwable) = delegate.reportFailure(t)
}
