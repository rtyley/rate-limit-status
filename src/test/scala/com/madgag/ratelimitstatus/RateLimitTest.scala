package com.madgag.ratelimitstatus

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

import java.time.{Duration, Instant}

class RateLimitTest extends AnyFlatSpec with Matchers {

  val rateLimit = RateLimit(window = Duration.ofMinutes(10))

  "RateLimitStatus" should "know when consumption is too high" in {
    val reset = Instant.now().plus(rateLimit.window.dividedBy(2))

    rateLimit.statusFor(QuotaUpdate(remaining = 10, limit = 100, reset)).consumptionIsDangerous shouldBe true
    rateLimit.statusFor(QuotaUpdate(remaining = 45, limit = 100, reset)).consumptionIsDangerous shouldBe true
    rateLimit.statusFor(QuotaUpdate(remaining = 60, limit = 100, reset)).consumptionIsDangerous shouldBe false
  }

  it should "be real descriptive" in {
    val reset = Instant.now().plus(rateLimit.window.dividedBy(2))

    println(rateLimit.statusFor(QuotaUpdate(remaining = 10, limit = 100, reset)).summary)

  }
}
