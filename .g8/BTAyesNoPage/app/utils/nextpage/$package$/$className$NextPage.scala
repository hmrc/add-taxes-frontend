package utils.nextpage.$package$

import config.FrontendAppConfig
import identifiers.$className$Id
import play.api.mvc.{Call, Request}
import models.$package$.$className$
import utils.NextPage

trait $className$NextPage {

  implicit val $className;format="decap"$: NextPage[$className$Id.type, $className$] = {
    new NextPage[$className$Id.type, $className$] {
      override def get(b: $className$)(implicit appConfig: FrontendAppConfig, request: Request[_]): Call =
        b match {
          case $className$.Yes => ???
          case $className$.No => ???
        }
    }
  }
}