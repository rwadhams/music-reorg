package com.wadhams.music.reorg.dto

import java.util.regex.Pattern

import com.wadhams.music.reorg.type.Extension

import groovy.transform.ToString

@ToString(includeNames=true)
class AppMusic {
	static int STARTING_SEQUENCE = 0
	
	File file
	MusicMetadata musicMetadata		//Artist and Title
	Extension extension
	int sequenceNumber
	String newFilename

	def AppMusic(File f) {
		this.file = f
		this.musicMetadata = null	//augmented later
		this.extension = Extension.findByFileExtension(f)
		this.sequenceNumber = ++STARTING_SEQUENCE
		this.newFilename = null		//augmented later
	}
}
