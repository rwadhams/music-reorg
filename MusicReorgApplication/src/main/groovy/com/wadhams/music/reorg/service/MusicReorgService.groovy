package com.wadhams.music.reorg.service

import static groovy.io.FileType.FILES

import java.text.NumberFormat
import java.util.regex.Pattern
import com.mpatric.mp3agic.ID3v1
import com.mpatric.mp3agic.ID3v2
import com.mpatric.mp3agic.Mp3File
import com.wadhams.music.reorg.context.AppContext
import com.wadhams.music.reorg.dto.AppMusic
import com.wadhams.music.reorg.dto.MusicMetadata
import com.wadhams.music.reorg.type.Extension
import org.jaudiotagger.audio.AudioFile
import org.jaudiotagger.audio.AudioFileIO
import org.jaudiotagger.tag.Tag
import org.jaudiotagger.tag.FieldKey

class MusicReorgService {
	Pattern ampersandPattern = ~/&/
	Pattern doubleQuotePattern = ~/"/
	Pattern dotSlashesPattern = ~/[.\/\\]/				//dot, forward and back slash
	Pattern illegalsPattern = ~/[#%{}<>*?$!:@+`|=\[\]]/	//my illegal filename characters

	NumberFormat nf3 = NumberFormat.getNumberInstance()
	String unknownAlbumText = 'Unknown'
	
	def MusicReorgService() {
		nf3 = NumberFormat.getNumberInstance()
		nf3.setGroupingUsed(false)
		nf3.setMinimumIntegerDigits(3)
	}
	
	List<AppMusic> findAllFiles(String folderPath) {
		List<AppMusic> appMusicList = []
		
		File dir = new File(folderPath)
		dir.eachFile(FILES) {f ->
			//println f.name
			appMusicList << new AppMusic(f)
		}

		return appMusicList
	}
	
	MusicMetadata findMusicMetadata(File f, Extension music) {
		MusicMetadata mmd = new MusicMetadata()
		
		if (music == Extension.MP3) {
			Mp3File mp3file = new Mp3File(f)
			if (mp3file.hasId3v2Tag()) {
				ID3v2 id3v2Tag = mp3file.getId3v2Tag()
				String artist = id3v2Tag.getArtist()
				if (artist) {
					mmd.artist = artist.trim()
				}
				
				String album = id3v2Tag.getAlbum()
				if (album && album.trim()) {
					mmd.album = album.trim()
				}
				else {
					mmd.album = unknownAlbumText
				}
				
				String title = id3v2Tag.getTitle()
				if (title) {
					mmd.title = title.trim()
				}
			}
		}
		else if (music == Extension.WMA) {
			AudioFile audioFile = AudioFileIO.read(f)
			Tag tag = audioFile.getTag()
			if (tag != null) {
				String artist = tag.getFirst(FieldKey.ARTIST)
				if (artist) {
					mmd.artist = artist.trim()
				}

				String album = tag.getFirst(FieldKey.ALBUM)
				if (album) {
					mmd.album = album.trim()
				}
				else {
					mmd.album = unknownAlbumText
				}
				
				String title = tag.getFirst(FieldKey.TITLE)
				if (title) {
					mmd.title = title.trim()
				}
			}
		}
		
		return mmd
	}
	
	String buildNewFilename(AppMusic am) {
		StringBuilder sb = new StringBuilder()
		
		//short-circuit
		if (am.extension == Extension.Unknown || !am.musicMetadata.artist || !am.musicMetadata.title) {
			return null
		}
		
		sb.append(normaliseFilenameText(am.musicMetadata.artist))
		sb.append('_(')
		sb.append(normaliseFilenameText(am.musicMetadata.album))
		sb.append(')_')
		sb.append(nf3.format(am.sequenceNumber))
		sb.append('_')
		sb.append(normaliseFilenameText(am.musicMetadata.title))
		sb.append('.')
		sb.append(am.extension.newFileExtension)
		
		return sb.toString()
	}

	String normaliseFilenameText(String text) {
		String normalisedText
		
		normalisedText = text.replaceAll(ampersandPattern, 'and')
		normalisedText = normalisedText.replaceAll(doubleQuotePattern, '\'')
		normalisedText = normalisedText.replaceAll(dotSlashesPattern, '-')
		normalisedText = normalisedText.replaceAll(illegalsPattern, '')
		
		return normalisedText
	}
	
	def report(AppContext context) {
		int mp3Count = 0
		int wmaCount = 0
		int unknownMusicCount = 0
		int noArtistCount = 0
		int noAlbumCount = 0
		int noTitleCount = 0
		int noNewFilename = 0
		
		context.appMusicList.each {am ->
			println "Filename...: ${am.file.name}"

			if (!am.musicMetadata.artist) {
				println "\t*** No Artist"
				noArtistCount++
			}
			
			if (am.musicMetadata.album == unknownAlbumText) {
				println "\t*** No Album"
				noAlbumCount++
			}
			
			if (!am.musicMetadata.title) {
				println "\t*** No Title"
				noTitleCount++
			}
			
			//Music counts
			if (am.extension == Extension.MP3) {
				mp3Count++
			}
			else if (am.extension == Extension.WMA) {
				wmaCount++
			}
			else {
				println "\t*** *** Unknown music"
				unknownMusicCount++
			}
			
			if (am.newFilename) {
				println "\tProposed new filename: ${am.newFilename}"
			}
			else {
				println "\t*** No New Filename"
				noNewFilename++
			}
			
		}
		println ''
		println 'File Count Totals'
		println '-----------------'
		println "mp3 files..........: $mp3Count"
		println "wma files..........: $wmaCount"
		println "unknown music......: $unknownMusicCount"
		println ''
		println "No Artist..........: $noArtistCount"
		println "No Album...........: $noAlbumCount"
		println "No Title...........: $noTitleCount"
		println "No New Filename....: $noNewFilename"
		println ''
	}
	
	def renameFile(File f, String newFilename) {
		String rename = "${f.parent}\\$newFilename"
		println "Renaming...${f.path} to: $rename"
		f.renameTo(rename)
	}

}
