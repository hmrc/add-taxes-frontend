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

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock
import com.github.tomakehurst.wiremock.core.WireMockConfiguration
import com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig
import org.scalatest.{BeforeAndAfterAll, BeforeAndAfterEach, TestSuite}
import org.scalatest.concurrent.{Eventually, IntegrationPatience}
import org.scalatestplus.play.guice.GuiceOneServerPerSuite
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.test.Injecting
import play.api.{Application, Environment, Mode}
import playconfig.featuretoggle.{Feature, FeatureToggleSupport}
import support.AddTaxesIntegrationTest.{stubHost, stubPort}

trait AddTaxesIntegrationTest
    extends Eventually
    with IntegrationPatience
    with Injecting
    with GuiceOneServerPerSuite
    with BeforeAndAfterAll
    with BeforeAndAfterEach
    with FeatureToggleSupport {
  this: TestSuite =>

  override implicit final lazy val app: Application = GuiceApplicationBuilder()
    .in(Environment.simple(mode = Mode.Dev))
    .configure(baseConfig ++ config)
    .build()

  protected def config: Map[String, Any] = Map.empty

  private final def baseConfig: Map[String, Any] =
    Map(
      "auditing.enabled" -> "false",
      "play.filters.csrf.header.bypassHeaders.Csrf-Token" -> "nocheck",
      "messages-feature-switch" -> "false",
      "enrolment-store-proxy.host"-> "http://localhost:6002",
      "identity-verification-frontend.host" -> "http://localhost:6002"
    ) ++ microservices.flatMap { microserviceName =>
      val key: String = s"microservice.services.$microserviceName"
      Map(s"$key.host" -> stubHost, s"$key.port" -> stubPort)
    }

  private final def microservices: Seq[String] = Seq(
    "business-tax-account",
    "iv",
    "sa",
    "enrolment-store-proxy",
    "vat-subscription",
    "auth",
    "identity-verification-frontend",
    "self-service-time-to-pay-frontend",
    "tax-enrolments"
  )

  protected def resetAllFeatures(): Unit =
    Feature.allTogglableFeatures.foreach(removeOverride)

  lazy val wmConfig: WireMockConfiguration = wireMockConfig() port stubPort

  lazy val wireMockServer: WireMockServer = new WireMockServer(wmConfig)

  override def beforeAll(): Unit = {
    super.beforeAll()
    wireMockServer.start()
    WireMock.configureFor(stubHost, stubPort)
    resetAllFeatures()
  }

  override def afterAll(): Unit = {
    super.beforeAll()
    wireMockServer.stop()
    resetAllFeatures()
  }

  override def beforeEach(): Unit = {
    super.beforeEach()
    resetAllFeatures()
    WireMock.reset()
  }

}

object AddTaxesIntegrationTest {
  val stubHost = "localhost"
  val stubPort = 6002
}
