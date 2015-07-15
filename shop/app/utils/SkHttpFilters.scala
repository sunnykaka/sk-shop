package utils

import javax.inject.Inject
import common.play.kamon.KamonRequestFilter
import play.api.Configuration
import play.api.http.HttpFilters
import play.api.mvc.EssentialFilter


/**
 * Created by liubin on 15-7-14.
 */
class SkHttpFilters @Inject() (
  kamonRequestFilter: KamonRequestFilter,
  configuration: Configuration
) extends HttpFilters {


  def filters = {
    var seq = Seq[EssentialFilter]()
    if(configuration.getBoolean("shop.statsEnable").getOrElse(false)) {
      seq = Seq(kamonRequestFilter) ++ seq
    }
    seq
  }

}
