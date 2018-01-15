package filters

import javax.inject.{Inject, Singleton}

import com.typesafe.scalalogging.StrictLogging
import org.slf4j.MDC
import play.api.http.HeaderNames
import play.api.inject.Module
import play.api.mvc._
import play.api.{Configuration, Environment}

import scala.concurrent.ExecutionContext

// https://www.playframework.com/documentation/2.5.x/ScalaHttpFilters
// https://groups.google.com/forum/#!topic/play-framework/3hvCr4E_6Q0
// https://github.com/playframework/playframework/tree/master/framework/src/play-filters-helpers/src/main/scala/play/filters

object AccessLogFilter {
  def apply()(implicit ec: ExecutionContext): AccessLogFilter = new AccessLogFilter()
}

@Singleton
class AccessLogFilter @Inject() ()(implicit ec: ExecutionContext)
  extends EssentialFilter with HeaderNames with StrictLogging {

  override def apply(next: EssentialAction) = EssentialAction { requestHeader =>
    val startTime = System.currentTimeMillis
    next(requestHeader).map { result =>
      val endTime = System.currentTimeMillis
      val requestTime = endTime - startTime
      val headerLog = requestHeader.headers.headers
        .map(kv => s"${kv._1}=${kv._2}")
        .mkString(", ")
      val message = List(
        requestHeader.remoteAddress,
        headerLog,
        requestHeader.method,
        requestHeader.uri,
        s"requestTime=${requestTime}",
        s"status=${result.header.status}").mkString(" ")
      logger.info(message)
      if (MDC.getCopyOfContextMap == null) {
        logger.error("No MDC")
      }
      result.withHeaders("X-Request-Time" -> requestTime.toString)
    }
  }
}

class AccessLogModule extends Module {
  def bindings(environment: Environment, configuration: Configuration) = Seq(
    bind[AccessLogFilter].toSelf
  )
}

trait AccessLogComponents {
  def configuration: Configuration
  implicit def executionContext: ExecutionContext
  lazy val accessLogFilter: AccessLogFilter = AccessLogFilter()(executionContext)
}
