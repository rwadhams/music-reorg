package com.wadhams.music.reorg.type

import java.util.regex.Pattern

enum Extension {
	MP3(['MP3'], 'mp3'),
	WMA(['WMA'], 'wma'),
	Unknown(['Unknown'], '');
	
	private static Pattern extensionPattern = ~/.*\.(\w{3,4})$/
	
	private static EnumSet<Extension> allEnums = EnumSet.allOf(Extension.class)
	
	private final List<String> matchingExtensions
	private final String newFileExtension
	
	Extension(List<String> matchingExtensions, String newFileExtension) {
		this.matchingExtensions = matchingExtensions
		this.newFileExtension = newFileExtension
	}
	
	public static Extension findByFileExtension(File f) {
		String fileExtension = ''
		def m = f.name =~ extensionPattern
		if (m) {
			//println m[0]
			//println m[0][1]
			fileExtension = m[0][1]
		}
		
		if (fileExtension) {
			fileExtension = fileExtension.toUpperCase()
			for (Extension e : allEnums) {
				if (e.matchingExtensions.contains(fileExtension)) {
					return e
				}
			}
		}
		else {
			println 'findByFileExtension() was passed a file without an extension'
			println ''
			return Extension.Unknown
		}
		
		//println "Unknown extension: $fileExtension"
		//println ''
		return Extension.Unknown
	}

}
