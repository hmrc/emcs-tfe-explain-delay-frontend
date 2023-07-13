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

package forms

import forms.behaviours.StringFieldBehaviours
import play.api.data.{Form, FormError}

class DelayDetailsFormProviderSpec extends StringFieldBehaviours {

  val requiredKey = "delayDetails.error.required"
  val lengthKey = "delayDetails.error.length"
  val maxLength = 350

  val formProvider = new DelayDetailsFormProvider()


  ".value" - {

    "field is optional" - {

      val form: Form[Option[String]] = formProvider(false)

      "form returns no errors" - {

        "input is valid" in {
          val data = Map("value" -> "Test 123.")
          val result = form.bind(data)

          result.errors mustBe Seq()
          result.value.flatten mustBe Some("Test 123.")
        }

        "input includes a Carriage Return" in {
          val data = Map("value" -> "Test\n123.")
          val result = form.bind(data)

          result.errors mustBe Seq()
          result.value.flatten mustBe Some("Test 123.")
        }

        "input is a value of 350 alpha characters that are valid" in {
          val data = Map("value" -> "a" * maxLength)
          val result = form.bind(data)

          result.errors mustBe Seq()
          result.value.flatten mustBe Some("a" * maxLength)
        }

        "input begins with a valid special character" in {
          val data = Map("value" -> ".A")
          val result = form.bind(data)

          result.errors mustBe Seq()
          result.value.flatten mustBe Some(".A")
        }

        "input is just numbers" in {
          val data = Map("value" -> "123")
          val result = form.bind(data)

          result.errors mustBe Seq()
          result.value.flatten mustBe Some("123")
        }

        "input is only whitespace" in {
          val data = Map("value" ->
            """
              |
              |
              |
              |
              |
              |
              |""".stripMargin)
          val result = form.bind(data)

          result.errors mustBe Seq()
          result.value.flatten mustBe Some("")
        }

        "input is empty" in {
          val data = Map("value" -> "")
          val result = form.bind(data)

          result.errors mustBe Seq()
          result.value.flatten mustBe None
        }

        "input field is missing" in {
          val data = Map[String, String]()
          val result = form.bind(data)

          result.errors mustBe Seq()
          result.value.flatten mustBe None
        }
      }
    }

    "field is mandatory" - {

      val form: Form[Option[String]] = formProvider(true)

      "form returns errors" - {

        "input field is empty" in {
          val data = Map("value" -> "")
          val result = form.bind(data)

          result.errors must contain only FormError("value", "delayDetails.error.required")
        }

        "input field is missing" in {
          val data = Map[String, String]()
          val result = form.bind(data)

          result.errors must contain only FormError("value", "delayDetails.error.required")
        }

        "input is only whitespace" in {
          val data = Map("value" ->
            """
              |
              |
              |
              |
              |
              |
              |""".stripMargin)
          val result = form.bind(data)

          result.errors must contain only FormError("value", "delayDetails.error.required")
        }
      }
    }

    "form returns an error when" - {

      val form: Form[Option[String]] = formProvider(true)

      "alpha numeric data isn't used" in {
        val data = Map("value" -> "..")
        val result = form.bind(data)

        result.errors must contain only FormError("value", "delayDetails.error.character", Seq(ALPHANUMERIC_REGEX))
      }

      "more than 350 characters are used" in {
        val data = Map("value" -> "a" * (maxLength + 1) )
        val result = form.bind(data)

        result.errors must contain only FormError("value", "delayDetails.error.length", Seq(maxLength))
      }

      "invalid characters are used" in {
        val data = Map("value" -> "<>")
        val result = form.bind(data)

        result.errors mustBe Seq(
          FormError("value", "delayDetails.error.character", Seq(ALPHANUMERIC_REGEX)),
          FormError("value", "delayDetails.error.xss", Seq(XSS_REGEX))
        )
      }
    }



  }
}
