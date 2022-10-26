package com.wadhams.music.reorg.context

import com.wadhams.music.reorg.dto.AppMusic
import com.wadhams.music.reorg.type.Action

class AppContext {
	Action action
	String folderPath
	
	List<AppMusic> appMusicList
}
