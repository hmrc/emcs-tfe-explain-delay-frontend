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
import forms.DelayDetailsFormProvider
import mocks.services.MockUserAnswersService
import models.DelayType.ReportOfReceipt
import models.{DelayReason, NormalMode, UserAnswers}
import navigation.{FakeNavigator, Navigator}
import pages.{DelayDetailsPage, DelayReasonPage, DelayTypePage}
import play.api.Application
import play.api.data.Form
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.mvc.Call
import play.api.test.FakeRequest
import play.api.test.Helpers._
import services.UserAnswersService
import views.html.DelayDetailsView

class DelayDetailsControllerSpec extends SpecBase with MockUserAnswersService {

  class Fixture(val userAnswers: Option[UserAnswers] = Some(emptyUserAnswers.set(DelayTypePage, ReportOfReceipt))) {
    val application: Application = applicationBuilder(userAnswers)
      .overrides(
        bind[Navigator].toInstance(new FakeNavigator(onwardRoute)),
        bind[UserAnswersService].toInstance(mockUserAnswersService)
      )
      .build()

    lazy val view: DelayDetailsView = application.injector.instanceOf[DelayDetailsView]
  }

  def onwardRoute: Call = Call("GET", "/foo")

  val formProvider = new DelayDetailsFormProvider()
  val form: Form[Option[String]] = formProvider(false)

  lazy val delayDetailsRoute: String = routes.DelayDetailsController.onPageLoad(testErn, testArc, NormalMode).url

  "DelayDetails Controller" - {

    val userAnswers = Some(
      emptyUserAnswers
        .set(DelayTypePage, ReportOfReceipt)
        .set(DelayReasonPage, DelayReason.Other)
        .set(DelayDetailsPage, Some("answer"))
    )

    val userAnswersAllWhiteSpaces = Some(
      emptyUserAnswers
        .set(DelayTypePage, ReportOfReceipt)
        .set(DelayReasonPage, DelayReason.BadWeather)
        .set(DelayDetailsPage, None)
    )

    val userAnswersUptoThisController = Some(
      emptyUserAnswers
        .set(DelayTypePage, ReportOfReceipt)
        .set(DelayReasonPage, DelayReason.Other)
    )

    "must return OK and the correct view for a GET when the question has not been previously answered" in new Fixture(userAnswersUptoThisController) {
      running(application) {
        val request = FakeRequest(GET, delayDetailsRoute)

        val result = route(application, request).value

        val view = application.injector.instanceOf[DelayDetailsView]

        status(result) mustEqual OK
        contentAsString(result) mustEqual view(form, fieldIsMandatory = true, NormalMode)(dataRequest(request), messages(application)).toString
      }
    }


    "must populate the view correctly on a GET when the question has previously been answered" in new Fixture(userAnswers) {
      running(application) {
        val request = FakeRequest(GET, delayDetailsRoute)

        val view = application.injector.instanceOf[DelayDetailsView]

        val result = route(application, request).value

        status(result) mustEqual OK
        contentAsString(result) mustEqual view(form.fill(Some("answer")), fieldIsMandatory = true, NormalMode)(dataRequest(request), messages(application)).toString
      }
    }

    "must redirect to the next page when valid data is submitted" in new Fixture(userAnswers) {
      running(application) {
        val request =
          FakeRequest(POST, delayDetailsRoute)
            .withFormUrlEncodedBody(("value", "answer"))

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual onwardRoute.url
      }
    }

    "must return a Bad Request and errors when invalid data is submitted" in new Fixture(userAnswers) {
      running(application) {
        val request =
          FakeRequest(POST, delayDetailsRoute)
            .withFormUrlEncodedBody(("value", "<>:;"))

        val boundForm = form.bind(Map("value" -> "<>:;"))

        val view = application.injector.instanceOf[DelayDetailsView]

        val result = route(application, request).value

        status(result) mustEqual BAD_REQUEST
        contentAsString(result) mustEqual view(boundForm, fieldIsMandatory = true, NormalMode)(dataRequest(request), messages(application)).toString
      }
    }

    "must redirect to the next page if Delay Details was all whitespace" in new Fixture(userAnswersAllWhiteSpaces) {
      running(application) {
        val request =
          FakeRequest(POST, delayDetailsRoute)
            .withFormUrlEncodedBody(("value", "  "))

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual onwardRoute.url
      }
    }

    "must redirect to the next page if Delay Details contains some whitespace" in new Fixture(userAnswers) {
      running(application) {
        val request =
          FakeRequest(POST, delayDetailsRoute)
            .withFormUrlEncodedBody(("value", "   answer  "))

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual onwardRoute.url
      }
    }

    "must redirect to Journey Recovery for a GET if no existing data is found" in new Fixture(None) {
      running(application) {
        val request = FakeRequest(GET, delayDetailsRoute)

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual routes.JourneyRecoveryController.onPageLoad(testErn, testArc).url
      }
    }

    "must redirect to Journey Recovery for a POST if no existing data is found" in new Fixture(None) {
      running(application) {
        val request =
          FakeRequest(POST, delayDetailsRoute)
            .withFormUrlEncodedBody(("value", "answer"))

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual routes.JourneyRecoveryController.onPageLoad(testErn, testArc).url
      }
    }

    ".trimValue" - {

      val application: Application = new GuiceApplicationBuilder().build()
      val controller: DelayDetailsController = application.injector.instanceOf[DelayDetailsController]

      "must return None when only whitespace present" in {
        controller.trim(Some("   ")) mustBe None
      }

      "must return None when passed a None" in {
        controller.trim(None) mustBe None
      }

      "must return Some value when text is present" in {
        controller.trim(Some("more information")) mustBe Some("more information")
      }

      "must return Some value with whitespace removed" in {
        controller.trim(Some("     more information       ")) mustBe Some("more information")
      }
    }
  }
}
