package filters

import javax.inject.{Inject, Singleton}

import com.typesafe.scalalogging.StrictLogging
import org.slf4j.MDC
import play.api.inject.Module
import play.api.mvc.{EssentialAction, EssentialFilter}
import play.api.{Configuration, Environment}
import util.RequestIdUtil

object HttpContextFilter {
  def apply() = new HttpContextFilter()
}


/**
  * Play Filter that generates a requestId via base64-encocded UUID, appending to an optional incoming HTTP header requestId.
  * The requestId is stored in the MDC.
  */
@Singleton
class HttpContextFilter @Inject() extends EssentialFilter with StrictLogging {

  val RequestId = "RequestId"

  override def apply(next: EssentialAction) = EssentialAction { requestHeader =>
    val incomingRequestId = requestHeader.headers.get(RequestId).map(_.trim).filter(_.nonEmpty)
    val requestId = RequestIdUtil.newRequestId(incomingRequestId)
    MDC.put(RequestId, requestId)
    logger.info("MDC set")
    next(requestHeader)
  }
}

class HttpContextModule extends Module {
  def bindings(environment: Environment, configuration: Configuration) = Seq(
    bind[HttpContextFilter].toSelf
  )
}

trait HttpContextComponents {
  lazy val httpContextFilter: HttpContextFilter = HttpContextFilter()
}
