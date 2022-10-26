package com.wadhams.music.reorg.type

import java.util.regex.Pattern

enum Music {
	MP3(['MP3'], 'MP3'),
	WMA(['WMA'], 'WMA'),
	Unknown(['Unknown'], '');
	
	private static Pattern extensionPattern = ~/.*\.(\w{3,4})$/
	
	private static EnumSet<Music> allEnums = EnumSet.allOf(Music.class)
	
	private final List<String> matchingExtensions
	private final String extension
	
	Music(List<String> matchingExtensions, String extension) {
		this.matchingExtensions = matchingExtensions
		this.extension = extension
	}
	
	public static Music findByFileExtension(File f) {
		String fileExtension = ''
		def m = f.name =~ extensionPattern
		if (m) {
			//println m[0]
			//println m[0][1]
			fileExtension = m[0][1]
		}
		
		if (fileExtension) {
			fileExtension = fileExtension.toUpperCase()
			for (Music e : allEnums) {
				if (e.matchingExtensions.contains(fileExtension)) {
					return e
				}
			}
		}
		else {
			println 'findByFileExtension() was passed a file without an extension'
			println ''
			return Music.Unknown
		}
		
		//println "Unknown extension: $fileExtension"
		//println ''
		return Music.Unknown
	}

}
