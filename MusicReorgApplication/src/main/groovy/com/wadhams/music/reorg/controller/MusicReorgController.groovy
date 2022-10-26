package com.wadhams.music.reorg.controller

import com.wadhams.music.reorg.context.AppContext
import com.wadhams.music.reorg.service.MusicReorgService
import com.wadhams.music.reorg.type.Action

class MusicReorgController {
	AppContext context
	
	MusicReorgService mrService = new MusicReorgService()
	
	def MusicReorgController(AppContext context) {
		this.context = context
	}
	
	def execute() {
		context.appMusicList = mrService.findAllFiles(context.folderPath)
		println "Number of AppMusic found: ${context.appMusicList.size()}"
		println ''
		
		//augment AppMusic with title and artist
		context.appMusicList.each {am ->
			am.musicMetadata = mrService.findMusicMetadata(am.file, am.music)
		}

		//buildNewFilename
		context.appMusicList.each {am ->
			am.newFilename = mrService.buildNewFilename(am, context)
		}
		
		//Action.Report
		if (context.action == Action.Report) {
			mrService.report(context)
			println ''
		}
		else {	//Action.Rename
			context.appMusicList.each {am ->
				if (am.newFilename) {
					mrService.renameFile(am.file, am.newFilename)
				}
			}
		}

		println ''
	}
}
