/*
 * Copyright 2023 HM Revenue & Customs
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

package controllers

import base.SpecBase
import mocks.services.MockUserAnswersService
import models.DelayReason.Strikes
import models.DelayType.ReportOfReceipt
import models.{ConfirmationDetails, UserAnswers}
import navigation.{FakeNavigator, Navigator}
import pages.{CheckYourAnswersPage, DelayDetailsPage, DelayReasonPage, DelayTypePage}
import play.api.inject.bind
import play.api.mvc.Call
import play.api.test.FakeRequest
import play.api.test.Helpers._
import services.UserAnswersService
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.SummaryList
import viewmodels.checkAnswers.{DelayReasonSummary, DelayTypeSummary}
import views.html.CheckYourAnswersView

import scala.concurrent.Future

class CheckYourAnswersControllerSpec extends SpecBase with MockUserAnswersService {

  class Fixture(val userAnswers: Option[UserAnswers] = Some(emptyUserAnswers
    .set(DelayTypePage, ReportOfReceipt)
    .set(DelayReasonPage, Strikes)
  )) {

    val application = applicationBuilder(userAnswers)
      .overrides(
        bind[Navigator].toInstance(new FakeNavigator(onwardRoute)),
        bind[UserAnswersService].toInstance(mockUserAnswersService)
      )
      .build()

    lazy val view = application.injector.instanceOf[CheckYourAnswersView]
    lazy val delayDetailsSummary = application.injector.instanceOf[DelayDetailsSummary]
    implicit lazy val msgs = messages(application)
  }

  def onwardRoute = Call("GET", "/foo")

  lazy val checkAnswersSubmitAction = routes.CheckYourAnswersController.onSubmit(testErn, testArc)

  "CheckYourAnswers Controller" - {

    ".onPageLoad" - {

      lazy val getRequest = FakeRequest(GET, routes.CheckYourAnswersController.onPageLoad(testErn, testArc).url)

      "when UserAnswers exist" - {

        "must return OK and the correct view" in new Fixture(Some(
          emptyUserAnswers
            .set(DelayTypePage, ReportOfReceipt)
            .set(DelayReasonPage, Strikes)
            .set(DelayDetailsPage, Some("details"))
        )) {
          running(application) {

            val result = route(application, getRequest).value
            implicit val _dataRequest = dataRequest(getRequest, userAnswers.get)

            val summaryListRows = Seq(
              DelayTypeSummary.row(userAnswers.get),
              DelayReasonSummary.row(userAnswers.get),
              Some(delayDetailsSummary.row(userAnswers.get))
            ).flatten

            status(result) mustEqual OK
            contentAsString(result) mustEqual view(SummaryList(summaryListRows), checkAnswersSubmitAction).toString
          }
        }
      }

      "when UserAnswers DO NOT exist" - {

        "must return SEE_OTHER and redirect to Journey Recovery" in new Fixture(None) {
          running(application) {

            val result = route(application, getRequest).value

            status(result) mustEqual SEE_OTHER
            redirectLocation(result) mustBe Some(routes.JourneyRecoveryController.onPageLoad(testErn, testArc).url)
          }
        }
      }
    }

    ".onSubmit" - {

      lazy val postRequest = FakeRequest(POST, checkAnswersSubmitAction.url)

      "when UserAnswers exist" - {

        "must save the ConfirmationDetails and redirect to the onward route" in new Fixture(Some(
          emptyUserAnswers
            .set(DelayTypePage, ReportOfReceipt)
            .set(DelayReasonPage, Strikes)
        )) {
          running(application) {

            val updatedAnswers = userAnswers.get.set(CheckYourAnswersPage, ConfirmationDetails(testConfirmationReference))

            MockUserAnswersService.set(updatedAnswers).returns(Future.successful(updatedAnswers))

            val result = route(application, postRequest).value

            status(result) mustEqual SEE_OTHER
            redirectLocation(result) mustBe Some(onwardRoute.url)
          }
        }
      }

      "when UserAnswers DO NOT exist" - {

        "must return SEE_OTHER and redirect to Journey Recovery" in new Fixture(None) {
          running(application) {

            val result = route(application, postRequest).value

            status(result) mustEqual SEE_OTHER
            redirectLocation(result) mustBe Some(routes.JourneyRecoveryController.onPageLoad(testErn, testArc).url)
          }
        }
      }
    }
  }
}
