package com.chestermere.lake.temperature.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.chestermere.lake.temperature.Application;
import com.chestermere.lake.temperature.objects.Snapshot;

@RestController
public class LatestController {

	@RequestMapping(method = RequestMethod.GET, path = "/latest")
	public Snapshot getLatest() {
		return Application.getServer().getSnapshots().getLatest();
	}

}
