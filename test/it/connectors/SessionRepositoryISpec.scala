package connectors

import org.scalatest.concurrent.ScalaFutures.convertScalaFuture
import org.scalatest.{BeforeAndAfterAll, BeforeAndAfterEach, MustMatchers, WordSpec}
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.libs.json.{JsString, JsValue, Json}
import play.api.test.Helpers.{await, defaultAwaitTimeout}
import play.api.{Application, Configuration}
import repositories.SessionRepository
import uk.gov.hmrc.http.cache.client.CacheMap
import uk.gov.hmrc.mongo.MongoComponent

class SessionRepositoryISpec extends WordSpec with MustMatchers with BeforeAndAfterEach with GuiceOneAppPerSuite with BeforeAndAfterAll {

  override lazy val app: Application =
    GuiceApplicationBuilder()
      .configure(Map("mongodb.uri" -> s"mongodb://localhost:27017/add-taxes-frontend"))
      .build()

  val testValue: JsString = JsString("b")

  def cacheMap: CacheMap = CacheMap("id", Map())

  def testCacheMap: CacheMap = CacheMap("testId", Map("a" -> testValue))

  val mongoComponent = app.injector.instanceOf[MongoComponent]
  val config = app.injector.instanceOf[Configuration]

  lazy val testDataRepo = new SessionRepository(config, mongoComponent)

  "Upsert" when {
    "there is a result" should {
      "return result is acknowledged (true)" in {
        val result = testDataRepo.apply().upsert(cacheMap)

        await(result) mustBe true
      }
    }
    "there is data" should {
      "upsert the data" in {
        val result = testDataRepo.apply().upsert(testCacheMap)

        await(result) mustBe true
      }
    }
  }

  "Get" when {
    "id is found" should {
      "return the empty CacheMap" in {
        val result = testDataRepo.apply().get("id")

        await(result) mustBe Some(cacheMap)
      }
    }
    "id is found and there is data" should {
      "return the CacheMap with data" in {
        val result = testDataRepo.apply().get("testId")

        await(result) mustBe Some(testCacheMap)

        await(result).get.data mustBe Map("a" -> testValue)

        testDataRepo.apply().collection.drop().toFuture().futureValue

      }
    }
  }
}
