package utils.nextpage.$package$

import config.FrontendAppConfig
import identifiers.$className$Id
import models.$package$.$className$
import play.api.mvc.{Call, Request}
import utils.NextPage

trait $className$NextPage {

  implicit val $className;format="decap"$: NextPage[$className$Id.type, $className$] = {
    new NextPage[$className$Id.type, $className$] {
      override def get(b: $className$)(implicit appConfig: FrontendAppConfig, request: Request[_]): Call =
        b match {
          case $className$.Option1 => ???
          case $className$.Option2 => ???
        }
    }
  }
}