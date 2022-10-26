package com.wadhams.music.reorg.service

import static groovy.io.FileType.FILES

import java.text.NumberFormat
import java.text.SimpleDateFormat

import com.mpatric.mp3agic.ID3v1
import com.mpatric.mp3agic.Mp3File
import com.wadhams.music.reorg.context.AppContext
import com.wadhams.music.reorg.dto.AppMusic
import com.wadhams.music.reorg.dto.MusicMetadata
import com.wadhams.music.reorg.type.Music

class MusicReorgService {
	SimpleDateFormat sdf1 = new SimpleDateFormat('yyyyMMdd_HHmmss')
	SimpleDateFormat sdf2 = new SimpleDateFormat('EEE MMM dd hh:mm:ss yyyy')	//Sat Jun  8 17:33:06 2013
	NumberFormat nf6 = NumberFormat.getNumberInstance() 
	
	def MusicReorgService() {
		nf6 = NumberFormat.getNumberInstance()
		nf6.setGroupingUsed(false)
		nf6.setMinimumIntegerDigits(6)

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
	
	MusicMetadata findMusicMetadata(File f, Music music) {
		MusicMetadata mmd = new MusicMetadata()
		
		if (music == Music.MP3) {
			Mp3File mp3file = new Mp3File(f)
			if (mp3file.hasId3v1Tag()) {
				ID3v1 id3v1Tag = mp3file.getId3v1Tag()
				mmd.artist = id3v1Tag.getArtist()
				mmd.title = id3v1Tag.getTitle()
			}
		}
		
		return mmd
	}
	
	String buildNewFilename(AppMusic am, AppContext context) {
		StringBuilder sb = new StringBuilder()
		
		//short-circuit
		if (am.musicMetadata.artist == null || am.musicMetadata.title == null) {
			return null
		}
		
		sb.append(am.musicMetadata.artist)
		sb.append('_(')
		sb.append(am.musicMetadata.title)
		sb.append(')_')
		sb.append(nf6.format(am.sequenceNumber))
		sb.append('.')
		if (am.music != Music.Unknown) {
			sb.append(am.music.extension)
		}
		else if (am.extension) {
			sb.append(am.extension)
		}
		else {
			return null		//catastrophic problem with file extension
		}
		
		return sb.toString()
	}
	
	def report(AppContext context) {
		int mp3Count = 0
		int wmaCount = 0
		int unknownMusicCount = 0
		int noArtistCount = 0
		int noTitleCount = 0
		
		context.appMusicList.each {am ->
			println "Filename...: ${am.file.name}"

			if (am.musicMetadata.artist == null) {
				println "\t*** No Artist"
				noArtistCount++
			}
			
			if (am.musicMetadata.title == null) {
				println "\t*** No Title"
				noTitleCount++
			}
			
			//Music counts
			if (am.music == Music.MP3) {
				mp3Count++
			}
			else if (am.music == Music.WMA) {
				wmaCount++
			}
			else {
				println "\t*** *** Unknown music"
				unknownMusicCount++
			}
			
			println "\tProposed new filename: ${am.newFilename}"
		}
		println ''
		println 'File Count Totals'
		println '-----------------'
		println "mp3 files..........: $mp3Count"
		println "wma files..........: $wmaCount"
		println "unknown music......: $unknownMusicCount"
		println ''
		println "No Artist..........: $noArtistCount"
		println "No Title...........: $noTitleCount"
		println ''
	}
	
	def renameFile(File f, String newFilename) {
		String rename = "${f.parent}\\$newFilename"
		println "Renaming...${f.path} to: $rename"
		f.renameTo(rename)
	}

}
