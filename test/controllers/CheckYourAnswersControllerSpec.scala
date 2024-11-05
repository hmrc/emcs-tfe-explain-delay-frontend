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
import fixtures.SubmitExplainDelayFixtures
import handlers.ErrorHandler
import mocks.services.{MockSubmitExplainDelayService, MockUserAnswersService}
import models.DelayReason.Strikes
import models.DelayType.ReportOfReceipt
import models.{ConfirmationDetails, MissingMandatoryPage, SubmitExplainDelayException, UserAnswers}
import navigation.{FakeNavigator, Navigator}
import pages.{ConfirmationPage, DelayDetailsPage, DelayReasonPage, DelayTypePage}
import play.api.i18n.Messages
import play.api.inject.bind
import play.api.mvc.Call
import play.api.test.FakeRequest
import play.api.test.Helpers._
import services.{SubmitExplainDelayService, UserAnswersService}
import views.html.CheckYourAnswersView

import scala.concurrent.Future

class CheckYourAnswersControllerSpec extends SpecBase with MockUserAnswersService with MockSubmitExplainDelayService with SubmitExplainDelayFixtures {

  class Fixture(val userAnswers: Option[UserAnswers] = Some(emptyUserAnswers
    .set(DelayTypePage, ReportOfReceipt)
    .set(DelayReasonPage, Strikes)
    .set(DelayDetailsPage, None)
  )) {
    val application = applicationBuilder(userAnswers)
      .overrides(
        bind[Navigator].toInstance(new FakeNavigator(onwardRoute)),
        bind[UserAnswersService].toInstance(mockUserAnswersService),
        bind[SubmitExplainDelayService].toInstance(mockSubmitExplainDelayService)
      )
      .build()

    lazy val view = application.injector.instanceOf[CheckYourAnswersView]
    lazy val errorHandler = application.injector.instanceOf[ErrorHandler]

    implicit lazy val msgs: Messages = messages(application)
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

            status(result) mustEqual OK
            contentAsString(result) mustEqual view(checkAnswersSubmitAction).toString
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

        "when the submission is successful" - {

          "must save the ConfirmationDetails and redirect to the onward route" in new Fixture(Some(
            emptyUserAnswers
              .set(DelayTypePage, ReportOfReceipt)
              .set(DelayReasonPage, Strikes)
              .set(DelayDetailsPage, None)
          )) {
            running(application) {

              MockSubmitExplainDelayService.submit(testErn, testArc, getMovementResponseModel, userAnswers.get)
                .returns(Future.successful(successResponseChRIS))

              val updatedAnswers = userAnswers.get.set(ConfirmationPage,
                ConfirmationDetails(
                  receipt = testConfirmationReference,
                  delayType = ReportOfReceipt,
                  delayReason = Strikes,
                  delayDetails = None
                )
              )

              MockUserAnswersService.set().returns(Future.successful(updatedAnswers))

              val result = route(application, postRequest).value

              status(result) mustEqual SEE_OTHER
              redirectLocation(result) mustBe Some(onwardRoute.url)
            }
          }

        }

        "when the submission fails" - {

          "must render an internal server error" in new Fixture(Some(
            emptyUserAnswers
              .set(DelayTypePage, ReportOfReceipt)
              .set(DelayReasonPage, Strikes)
              .set(DelayDetailsPage, None)
          )) {

            running(application) {

              MockSubmitExplainDelayService.submit(testErn, testArc, getMovementResponseModel, userAnswers.get)
                .returns(Future.failed(SubmitExplainDelayException("some exception occurred")))

              val result = route(application, postRequest).value

              status(result) mustBe INTERNAL_SERVER_ERROR
              contentAsString(result) mustBe await(errorHandler.internalServerErrorTemplate(postRequest)).toString()
            }
          }

          "when invalid data exists so the submission can NOT be generated" - {

            "must return BadRequest" in new Fixture(Some(emptyUserAnswers)) {

              running(application) {

                MockSubmitExplainDelayService.submit(testErn, testArc, getMovementResponseModel, emptyUserAnswers)
                  .returns(Future.failed(MissingMandatoryPage("bang")))

                val result = route(application, postRequest).value

                status(result) mustBe BAD_REQUEST
                contentAsString(result) mustBe await(errorHandler.badRequestTemplate(postRequest)).toString()
              }
            }
          }

        }
      }
    }
  }
}
