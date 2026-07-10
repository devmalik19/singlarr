package devmalik19.singlarr.controller;

import devmalik19.singlarr.constants.FolderType;
import devmalik19.singlarr.data.dto.MetadataResult;
import devmalik19.singlarr.helper.PaginationHelper;
import devmalik19.singlarr.service.LibraryService;
import devmalik19.singlarr.service.metadata.MetaDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class HomeController
{
	@Autowired
	private MetaDataService metaDataService;

	@Autowired
	private LibraryService libraryService;


	@GetMapping("/")
    public String home()
    {
        return "home";
    }

	@GetMapping("/home/search")
	public String search(
		@RequestParam(value = "search") String search,
		@RequestParam(value = "artist", required = false) String artist,
		@RequestParam(value = "album", required = false) String album,
		@RequestParam(value = "year", required = false) String year,
		Pageable pageable,
		Model model
	) throws Exception
	{
		Page<MetadataResult> searchResultsPage = metaDataService.search(search, artist, album, year, pageable);
		model.addAttribute("library", libraryService.getByType(FolderType.ARTIST));
		return PaginationHelper.prepareResponse(searchResultsPage, pageable, model);
	}
}
