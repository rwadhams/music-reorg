package com.wadhams.music.reorg.dto

import java.util.regex.Pattern

import com.wadhams.music.reorg.type.Music

import groovy.transform.ToString

@ToString(includeNames=true)
class AppMusic {
	static Pattern extensionPattern = ~/.*\.(\w{3,4})$/
	static int STARTING_SEQUENCE = 0
	
	File file
	MusicMetadata musicMetadata
	Music music
	String extension
	int sequenceNumber
	String newFilename

	def AppMusic(File f) {
		this.file = f
		this.musicMetadata = null
		this.music = Music.findByFileExtension(f)
		if (this.music == Music.Unknown) {
			def m = f.name =~ extensionPattern
			if (m) {
				extension = m[0][1]
			}
		}
		this.sequenceNumber = ++STARTING_SEQUENCE
		this.newFilename = null
	}
}
