package devmalik19.singlarr.service.metadata;

import devmalik19.singlarr.data.dto.MetadataResult;
import devmalik19.singlarr.service.BaseIT;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class MusicBrainzServiceIT extends BaseIT
{
	@Autowired
	private MusicBrainzService musicBrainzService;

	@Test
	void searchSongByTitle_shouldReturnResults()
	{
		List<MetadataResult> results = musicBrainzService.searchSongByTitle("Bohemian Rhapsody", "", 0, 10);
		assertNotNull(results);
		assertFalse(results.isEmpty(), "Should find results for 'Bohemian Rhapsody'");

		MetadataResult first = results.get(0);
		assertNotNull(first.getTitle());
		assertTrue(first.getTitle().toLowerCase().contains("bohemian"),
			"First result title should contain 'bohemian'. Got: " + first.getTitle());
	}

	@Test
	void searchSongByTitleArtist_shouldReturnResults()
	{
		List<MetadataResult> results = musicBrainzService.searchSongByTitleArtist(
			"Believer", "Imagine Dragons", "", 0, 10);
		assertNotNull(results);
		assertFalse(results.isEmpty(), "Should find results for 'Believer' by 'Imagine Dragons'");

		MetadataResult first = results.get(0);
		assertNotNull(first.getArtists());
		assertTrue(first.getArtists().toLowerCase().contains("imagine"),
			"Artist should contain 'imagine'. Got: " + first.getArtists());
	}

	@Test
	void searchSongByTitleAlbum_shouldReturnResults()
	{
		List<MetadataResult> results = musicBrainzService.searchSongByTitleAlbum(
			"Yesterday", "Help!", "", 0, 10);
		assertNotNull(results);
		assertFalse(results.isEmpty(), "Should find results for 'Yesterday' on album 'Help!'");
	}

	@Test
	void searchSongByTitleArtistAlbum_shouldReturnResults()
	{
		List<MetadataResult> results = musicBrainzService.searchSongByTitleArtistAlbum(
			"Smells Like Teen Spirit", "Nirvana", "Nevermind", "", 0, 10);
		assertNotNull(results);
		assertFalse(results.isEmpty(), "Should find 'Smells Like Teen Spirit' by Nirvana on Nevermind");

		MetadataResult first = results.get(0);
		assertNotNull(first.getAlbums());
	}

	@Test
	void searchSongByTitle_withYear_shouldFilterResults()
	{
		List<MetadataResult> results = musicBrainzService.searchSongByTitle(
			"Shape of You", "2017", 0, 10);
		assertNotNull(results);
		assertFalse(results.isEmpty(), "Should find 'Shape of You' from 2017");

		MetadataResult first = results.get(0);
		assertEquals("2017", first.getYear(), "Year should be 2017. Got: " + first.getYear());
	}

	@Test
	void searchSongByTitle_withGibberish_shouldReturnEmptyList()
	{
		List<MetadataResult> results = musicBrainzService.searchSongByTitle(
			"xyznonexistentsong98765zzz", "", 0, 10);
		assertNotNull(results);
		assertTrue(results.isEmpty(), "Gibberish query should return empty results");
	}
}
