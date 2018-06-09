package com.madgag.ratelimitstatus

import java.time.Duration.ZERO
import java.time.{Duration, Instant}

import scala.language.implicitConversions
import scala.math.Ordering.Implicits._
import scala.math.round

case class Status(rateLimit: RateLimit, quotaUpdate: QuotaUpdate) {
  import quotaUpdate._
  import rateLimit._

  val previousReset: Instant = reset.minus(window)

  val elapsedWindowDuration: Duration = Duration.between(previousReset, capturedAt)

  val reasonableSampleTimeElapsed: Boolean = elapsedWindowDuration > reasonableSampleTime

  val proportionOfWindowElapsed: Float = elapsedWindowDuration.toNanos.toFloat / window.toNanos

  case class StarvationProjection(nonZeroConsumed: Int) {
    assert(nonZeroConsumed > 0)

    val averageTimeToConsumeOneUnitOfQuota = elapsedWindowDuration dividedBy nonZeroConsumed

    val projectedTimeToExceedingLimit = averageTimeToConsumeOneUnitOfQuota multipliedBy remaining

    val projectedInstantLimitWouldBeExceededWithoutReset = capturedAt plus projectedTimeToExceedingLimit

    // the smaller this number, the worse things are - negative numbers indicate starvation
    val bufferDurationBetweenResetAndLimitBeingExceeded =
      Duration.between(reset, projectedInstantLimitWouldBeExceededWithoutReset)

    val bufferAsProportionOfWindow =
      bufferDurationBetweenResetAndLimitBeingExceeded.toMillis.toFloat / window.toMillis

    lazy val summary = {
      val mins = s"${bufferDurationBetweenResetAndLimitBeingExceeded.abs.toMinutes} mins"
      if (bufferDurationBetweenResetAndLimitBeingExceeded.isNegative) {
        s"will exceed quota $mins before reset occurs at $resetTimeString"
      } else s"would exceed quota $mins after reset"
    }
  }

  val projectedConsumptionOverEntireQuotaWindow: Option[Int] = if (elapsedWindowDuration <= ZERO) None else {
    Some(round(consumed / proportionOfWindowElapsed))
  }

  lazy val starvationProjection: Option[StarvationProjection] =
    if (consumed > 0) Some(StarvationProjection(consumed)) else None

  val consumptionIsDangerous = (reasonableSampleTimeElapsed || significantQuotaConsumed) &&
    starvationProjection.exists(_.bufferAsProportionOfWindow < 0.2)

  val summary = (
    Some(s"Consumed $consumed/$limit over ${elapsedWindowDuration.toMinutes} mins") ++
      projectedConsumptionOverEntireQuotaWindow.map(p => s"projected consumption over window: $p") ++
      starvationProjection.filter(_ => consumptionIsDangerous).map(_.summary)
    ).mkString(", ")
}