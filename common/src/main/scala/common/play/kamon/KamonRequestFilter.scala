package common.play.kamon

import kamon.Kamon.tracer
import kamon.util.SameThreadExecutionContext
import play.api.mvc.{RequestHeader, EssentialAction, EssentialFilter}

/**
 * Created by liubin on 15-7-14.
 */
class KamonRequestFilter extends EssentialFilter {

  override def apply(next: EssentialAction): EssentialAction = {
    val action = (requestHeader: RequestHeader) ⇒ {
      val playExtension = kamon.Kamon(Play)

      val token = if (playExtension.includeTraceToken) {
        requestHeader.headers.get(playExtension.traceTokenHeaderName)
      } else None

      val key = KamonHelper.setContext(tracer.newContext(playExtension.generateTraceName(requestHeader), token))
      //      play.api.Logger.warn(s"before KamonRequestFilter... key: $key, uri: ${requestHeader.uri}, thread: ${Thread.currentThread()}, current context: ${Tracer.currentContext}")

      next(requestHeader).map { result =>
        KamonHelper.getContext(key).fold(result) { context =>
          KamonHelper.clearContext(key)
          context.collect { ctx =>
            //            play.api.Logger.debug(s"after KamonRequestFilter... random: $key, uri: ${requestHeader.uri}, thread: ${Thread.currentThread()}, current context: $ctx")
            ctx.finish()

            playExtension.httpServerMetrics.recordResponse(ctx.name, result.header.status.toString)

            if (playExtension.includeTraceToken) result.withHeaders(playExtension.traceTokenHeaderName -> ctx.token)
            else result

          } getOrElse result
        }
      }(SameThreadExecutionContext)
    }

    EssentialAction(action)
  }
}
