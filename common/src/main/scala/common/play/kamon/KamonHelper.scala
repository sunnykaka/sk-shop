package common.play.kamon

import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

import kamon.trace._


/**
 * Created by liubin on 15-7-15.
 */
object KamonHelper {

  val map = new ConcurrentHashMap[String, TraceContext]()

  def setContext(context: TraceContext): String = {
    val key = UUID.randomUUID().toString
    map.put(key, context)
    key
  }

  def getContext(key: String): Option[TraceContext] = {
    Option(map.get(key))
  }

  def clearContext(key: String): Unit = {
    map.remove(key)
  }
}
