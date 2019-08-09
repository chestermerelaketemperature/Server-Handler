package com.chestermere.lake.temperature.controllers;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.chestermere.lake.temperature.Application;
import com.chestermere.lake.temperature.objects.Snapshot;

@RestController
@CrossOrigin
public class LatestController {

	/**
	 * @api {get} /latest Latest Snapshot
	 * 
	 * @apiVersion 0.0.1
	 * @apiName getLatest
	 * @apiGroup Snapshots
	 *
	 * @apiSuccess {Boolean} manual If this snapshot was taken manually or false if our controller made it.
	 * @apiSuccess {Long} id ID of this snapshot.
	 * @apiSuccess {Instant} creation The Instant this snapshot was created.
	 * @apiSuccess {Instant} creation The Instant this snapshot was created.
	 * @apiSuccess {Double} waterTemperature The temperature of the water.
	 * @apiSuccess {Double} airTemperature The temperature of the air.
	 *
	 * @apiSuccessExample Success-Response:
	 *     {
	 *       "manual": false,
	 *       "id": 1
	 *       "creation": "2019-07-26T05:56:05.518Z"
	 *       "waterTemperature": 67.5
	 *       "airTemperature": 21.1
	 *     }
	 *
	 * @apiExample {js} Example usage:
	 * 		$(document).ready(function() {
	 * 			$.ajax({
	 * 				url: "http://api.chestermerelaketemperature.com/latest"
	 * 			}).then(function(data) {
	 * 				$('.snapshot-creation').append(data.creation);
	 * 				$('.snapshot-air').append(data.airTemperature);
	 * 				$('.snapshot-water').append(data.waterTemperature);
	 * 			});
	 * 		});
	 */
	@RequestMapping(method = RequestMethod.GET, path = "/latest")
	public Snapshot getLatest() {
		return Application.getServer().getSnapshots().getLatest();
	}

}
