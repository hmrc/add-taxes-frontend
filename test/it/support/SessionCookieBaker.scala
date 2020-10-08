/*
 * Copyright 2020 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package support

import java.net.URLEncoder

import org.joda.time.DateTime
import play.api.Application
import play.api.libs.crypto.CookieSigner
import uk.gov.hmrc.crypto.{CompositeSymmetricCrypto, PlainText}

object SessionCookieBaker {
  private val cookieKey = "gvBoGdgzqG1AarzF1LY0zQ=="

  def cookieValue(mdtpSessionData: Map[String, String],
                  optConfiguredTime: Option[DateTime])(implicit application: Application): String = {
    val cookieSigner             = application.injector.instanceOf[CookieSigner]

    def encode(data: Map[String, String]): PlainText = {
      val encoded = data
        .map {
          case (k, v) => URLEncoder.encode(k, "UTF-8") + "=" + URLEncoder.encode(v, "UTF-8")
        }
        .mkString("&")
      val key = "yNhI04vHs9<_HWbC`]20u`37=NGLGYY5:0Tg5?y`W<NoJnXWqmjcgZBec@rOxb^G".getBytes
      PlainText(cookieSigner.sign(encoded, key) + "-" + encoded)
    }

    val encodedCookie = encode(mdtpSessionData)
    val encrypted     = CompositeSymmetricCrypto.aesGCM(cookieKey, Seq()).encrypt(encodedCookie).value

    val cookieString: String = s"""mdtp="$encrypted"; Path=/; HTTPOnly"; Path=/; HTTPOnly"""

        cookieString
    }
}
