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
import models.DelayReason.Strikes
import models.DelayType.ReportOfReceipt
import models.{ConfirmationDetails, UserAnswers}
import pages.{ConfirmationPage, DelayReasonPage, DelayTypePage}
import play.api.i18n.Messages
import play.api.test.FakeRequest
import play.api.test.Helpers._
import views.html.ConfirmationView

class ConfirmationControllerSpec extends SpecBase {

  class Fixture(val userAnswers: Option[UserAnswers] = Some(emptyUserAnswers
    .set(DelayTypePage, ReportOfReceipt)
    .set(DelayReasonPage, Strikes)
    .set(ConfirmationPage, ConfirmationDetails(
      receipt = testConfirmationReference,
      delayType = ReportOfReceipt,
      delayReason = Strikes,
      delayDetails = None
    ))
  )) {
    val application = applicationBuilder(userAnswers).build()
    val view = application.injector.instanceOf[ConfirmationView]
    implicit val msgs: Messages = messages(application)
  }

  lazy val getRequest = FakeRequest(GET, routes.ConfirmationController.onPageLoad(testErn, testArc).url)

  "Confirmation Controller" - {

    "when UserAnswers contains Confirmation Details" - {

      "must return OK and the correct view for a GET" in new Fixture {

        running(application) {

          implicit val dr = dataRequest(getRequest, userAnswers.get)
          val result = route(application, getRequest).value

          status(result) mustEqual OK
          contentAsString(result) mustEqual view(
            ConfirmationDetails(
              receipt = testConfirmationReference,
              delayType = ReportOfReceipt,
              delayReason = Strikes,
              delayDetails = None
            )
          ).toString
        }
      }
    }

    "when UserAnswers is missing Confirmation Details" - {

      "must return SEE_OTHER and redirect to Journey Recovery" in new Fixture(Some(emptyUserAnswers)) {

        running(application) {

          val result = route(application, getRequest).value

          status(result) mustEqual SEE_OTHER
          redirectLocation(result) mustBe Some(routes.JourneyRecoveryController.onPageLoad(testErn, testArc).url)
        }
      }
    }

    "when UserAnswers is None" - {

      "must return SEE_OTHER and redirect to Journey Recovery" in new Fixture(None) {

        running(application) {

          val result = route(application, getRequest).value

          status(result) mustEqual SEE_OTHER
          redirectLocation(result) mustBe Some(routes.JourneyRecoveryController.onPageLoad(testErn, testArc).url)
        }
      }
    }
  }
}
