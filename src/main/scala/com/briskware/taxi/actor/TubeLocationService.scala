package com.briskware.taxi.actor

import akka.actor.{ActorRef, Actor, ActorLogging}
import com.briskware.taxi.model.Location

case class CloseToTubeStation(loc: Location)
case class CloseToTubeStationResponse(isClose: Boolean)

/**
 * Tube Location Service
 *
 * This service implementation assumes that Tube stations are located within
 * the configured distance from the centre located at long/late 50.0/0.0
 */
class TubeLocationService(val owner: ActorRef) extends Actor with ActorLogging {

  /**
   * distance is 1 minute lat/long
   */
  val distance = 1D/60D

  var count = 0L
  var countNearTubeStation = 0L

  override def receive: Receive = {
    case CloseToTubeStation(loc) =>
      count += 1
      sender ! CloseToTubeStationResponse(isCloseToTubeStation(loc))
  }

  private def isCloseToTubeStation(loc: Location) = loc match {
    case Location(long,lat) if Math.abs(Math.abs(lat) - 50.0D) < distance && Math.abs(long) < distance =>
      countNearTubeStation += 1
      true
    case _ =>
      false
  }

  override def postStop(): Unit = {
    super.postStop()
    log.info(s"stopping, processed $count locations ($countNearTubeStation were near a Tube Station).")
  }
}
