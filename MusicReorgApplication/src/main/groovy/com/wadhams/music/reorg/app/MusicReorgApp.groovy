package com.wadhams.music.reorg.app

import com.wadhams.music.reorg.context.AppContext
import com.wadhams.music.reorg.controller.MusicReorgController
import com.wadhams.music.reorg.type.Action

class MusicReorgApp {
    static void main(String[] args) {
		println 'MusicReorgApp started...'
		println ''
		println 'Usage: MusicReorgApp <action> <folderPath>'
		println '<action> = REPORT | rep | RENAME | ren'
		println ''
		
		if (args.size() == 2) {
			AppContext context = new AppContext()
			context.action = Action.findByName(args[0])
			println "Action..........: ${context.action}"
			context.folderPath = args[1]
			println "Folder path.....: ${context.folderPath}"
			println ''

			if (context.action == Action.Unknown) {
				println "Unknown \'action\' parameter: ${args[0]}"
				println ''
				println 'See \'Usage\' above. Application did not run.'
				println ''
			}
			else {
				MusicReorgApp app = new MusicReorgApp()
				app.execute(context)
			}
		}
		else {
			println "Invalid number of arguments. args.size(): ${args.size()}"
			println 'See \'Usage\' above. Application did not run.'
			println ''
		}

		println 'MusicReorgApp ended.'
    }
	
	def execute(AppContext context) {
		MusicReorgController controller = new MusicReorgController(context)
		controller.execute()
	}

}
