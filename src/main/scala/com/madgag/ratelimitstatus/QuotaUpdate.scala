package com.madgag.ratelimitstatus

import java.time.Clock.systemUTC
import java.time.ZoneOffset.UTC
import java.time.format.DateTimeFormatter.ISO_TIME
import java.time.{Clock, Instant, ZonedDateTime}

object QuotaUpdate {
  def apply(remaining: Int, limit: Int, reset: Instant)(implicit clock: Clock = systemUTC): QuotaUpdate =
    QuotaUpdate(remaining, limit, reset, capturedAt = clock.instant())
}

case class QuotaUpdate(remaining: Int, limit: Int, reset: Instant, capturedAt: Instant) {
  val consumed = limit - remaining

  val significantQuotaConsumed = consumed > limit * 0.3

  lazy val resetTimeString: String = ISO_TIME.format(ZonedDateTime.ofInstant(reset, UTC))
}
